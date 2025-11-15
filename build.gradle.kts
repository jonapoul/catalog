import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import java.util.Properties

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.publish)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
  `java-gradle-plugin`
}

// TODO: https://github.com/gradle/gradle/issues/22600
tasks.validatePlugins { enableStricterValidation = true }

val javaVersionStr = providers.gradleProperty("catalog.javaVersion").get()
val javaVersion = JavaVersion.toVersion(javaVersionStr)

java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

kotlin {
  jvmToolchain(javaVersionStr.toInt())
  explicitApi()

  compilerOptions {
    allWarningsAsErrors = true
    jvmTarget = JvmTarget.fromTarget(javaVersionStr)
  }

  @OptIn(ExperimentalAbiValidation::class)
  abiValidation { enabled = true }
}

ktlint {
  version = libs.versions.ktlint
  reporters { reporter(HTML) }
}

detekt {
  config.from(file("detekt.yml"))
  buildUponDefaultConfig = true
}

val detektTasks = tasks.withType(Detekt::class)
detektTasks.configureEach { reports.html.required = true }
val detektCheck by tasks.registering { dependsOn(detektTasks) }
tasks.check { dependsOn(detektCheck) }

gradlePlugin.plugins.create("catalog") {
  id = "dev.jonpoulton.catalog"
  implementationClass = "dev.jonpoulton.catalog.gradle.CatalogPlugin"
}

// Adapted from https://github.com/GradleUp/shadow/blob/1d7b0863fed3126bf376f11d563e9176de176cd3/build.gradle.kts#L63-L65
// Allows gradle test cases to use the same classpath as the parent build - meaning we don't need to specify versions
// when loading plugins into test projects.
val testPluginClasspath by configurations.registering { isCanBeResolved = true }

// Plugins used in tests could be resolved in classpath.
tasks.pluginUnderTestMetadata { pluginClasspath.from(testPluginClasspath) }

dependencies {
  fun compileOnly(plugin: Provider<PluginDependency>) =
    with(plugin.get()) { compileOnly("$pluginId:$pluginId.gradle.plugin:$version") }

  fun testPluginClasspath(plugin: Provider<PluginDependency>) =
    with(plugin.get()) { testPluginClasspath("$pluginId:$pluginId.gradle.plugin:$version") }

  compileOnly(libs.plugins.agp)
  compileOnly(libs.plugins.jetbrainsCompose)
  compileOnly(libs.plugins.kotlinAndroid)
  compileOnly(libs.plugins.kotlinJvm)
  compileOnly(libs.plugins.kotlinMultiplatform)

  implementation(libs.kotlinpoet)

  testImplementation(kotlin("test"))
  testImplementation(libs.test.junit)
  testImplementation(libs.test.truth)

  testPluginClasspath(libs.plugins.agp)
  testPluginClasspath(libs.plugins.jetbrainsCompose)
  testPluginClasspath(libs.plugins.kotlinAndroid)
  testPluginClasspath(libs.plugins.kotlinJvm)
  testPluginClasspath(libs.plugins.kotlinCompose)
}

fun androidHome(): String? {
  val androidHome = System.getenv("ANDROID_HOME")
  if (!androidHome.isNullOrBlank() && File(androidHome).exists()) {
    logger.info("Using system environment variable $androidHome as ANDROID_HOME")
    return androidHome
  }

  val localProps = rootProject
    .file("local.properties")
    .takeIf { it.exists() }
    ?: rootDir.resolve("../local.properties")

  if (localProps.exists()) {
    val properties = Properties()
    localProps.inputStream().use { properties.load(it) }
    val sdkHome = properties.getProperty("sdk.dir")
    if (File(sdkHome).exists()) {
      logger.info("Using local.properties sdk.dir $sdkHome as ANDROID_HOME")
      return sdkHome
    }
  }

  logger.warn("No Android SDK found - Android unit tests will be skipped")
  return null
}

tasks.test {
  systemProperty("test.version.gradle", GradleVersion.current().version)
  androidHome()?.let { systemProperty("test.androidHome", it) }
}

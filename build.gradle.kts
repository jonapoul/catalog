import dev.detekt.gradle.Detekt
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.publish)
  alias(libs.plugins.blueprint.test)
  alias(libs.plugins.detekt)
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

  @OptIn(ExperimentalAbiValidation::class) abiValidation()
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

fun Provider<PluginDependency>.artifact() =
  map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}") }

dependencies {
  fun compileOnly(plugin: Provider<PluginDependency>) = compileOnly(plugin.artifact())
  fun testPluginClasspath(plugin: Provider<PluginDependency>) = testPluginClasspath(plugin.artifact())

  compileOnly(libs.plugins.agp.kmp)
  compileOnly(libs.plugins.agp.lib)
  compileOnly(libs.plugins.jetbrainsCompose)
  compileOnly(libs.plugins.kotlinAndroid)
  compileOnly(libs.plugins.kotlinJvm)
  compileOnly(libs.plugins.kotlinMultiplatform)

  implementation(libs.kotlinpoet)

  testCompileOnly(libs.test.junit6.api)
  testImplementation(kotlin("test"))
  testImplementation(libs.test.assertk)
  testImplementation(libs.test.blueprintAssertk)
  testImplementation(libs.test.junit4)
  testImplementation(libs.test.truth)
  testRuntimeOnly(libs.test.junit6.launcher)
  testRuntimeOnly(libs.test.junit6.vintage)

  testPluginClasspath(libs.plugins.agp.kmp)
  testPluginClasspath(libs.plugins.agp.lib)
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

  val localProps =
    rootProject.file("local.properties").takeIf { it.exists() }
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
  useJUnitPlatform()
  systemProperty("test.version.gradle", GradleVersion.current().version)
  androidHome()?.let { systemProperty("test.androidHome", it) }
}

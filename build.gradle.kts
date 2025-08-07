import blueprint.recipes.DetektAll
import blueprint.recipes.detektBlueprint
import blueprint.recipes.kotlinJvmBlueprint
import blueprint.recipes.ktlintBlueprint
import blueprint.recipes.spotlessBlueprint
import java.util.Properties

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath(libs.plugin.blueprint.core)
    classpath(libs.plugin.blueprint.recipes)
  }
}

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.publish)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.spotless)
  `kotlin-dsl`
  `java-gradle-plugin`
}

tasks.validatePlugins {
  // TODO: https://github.com/gradle/gradle/issues/22600
  enableStricterValidation = true
}

kotlinJvmBlueprint(libs.versions.kotlin)
ktlintBlueprint(libs.versions.ktlint.cli)
spotlessBlueprint()
detektBlueprint(
  detektAllConfig = DetektAll.Apply(ignoreRelease = true),
  configFile = projectDir.resolve("detekt.yml"),
)

gradlePlugin {
  plugins {
    create("catalog-deprecated") {
      id = "dev.jonpoulton.catalog"
      implementationClass = "dev.jonpoulton.catalog.gradle.CatalogPlugin"
    }
    create("catalog-android") {
      id = "dev.jonpoulton.catalog.android"
      implementationClass = "dev.jonpoulton.catalog.gradle.CatalogAndroidPlugin"
    }
  }
}

// Adapted from https://github.com/GradleUp/shadow/blob/1d7b0863fed3126bf376f11d563e9176de176cd3/build.gradle.kts#L63-L65
// Allows gradle test cases to use the same classpath as the parent build - meaning we don't need to specify versions
// when loading plugins into test projects.
val testPluginClasspath by configurations.registering {
  isCanBeResolved = true
}

tasks.pluginUnderTestMetadata {
  // Plugins used in tests could be resolved in classpath.
  pluginClasspath.from(testPluginClasspath)
}

dependencies {
  compileOnly(libs.plugin.agp)
  compileOnly(libs.plugin.compose)
  compileOnly(libs.plugin.kotlin)
  compileOnly(libs.plugin.kotlinCompose)

  implementation(libs.kotlinpoet)

  testImplementation(kotlin("test"))
  testImplementation(libs.test.junit)
  testImplementation(libs.test.truth)

  testPluginClasspath(libs.plugin.agp)
  testPluginClasspath(libs.plugin.compose)
  testPluginClasspath(libs.plugin.kotlin)
  testPluginClasspath(libs.plugin.kotlinCompose)
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

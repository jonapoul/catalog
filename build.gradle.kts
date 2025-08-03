import blueprint.recipes.DetektAll
import blueprint.recipes.detektBlueprint
import blueprint.recipes.kotlinJvmBlueprint
import blueprint.recipes.ktlintBlueprint
import blueprint.recipes.spotlessBlueprint

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
  `java-gradle-plugin`
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
    create("catalog") {
      id = "dev.jonpoulton.catalog"
      implementationClass = "dev.jonpoulton.catalog.gradle.CatalogPlugin"
    }
  }
}

dependencies {
  compileOnly(libs.plugin.agp)
  compileOnly(libs.plugin.kotlin)
  implementation(libs.kotlinpoet)
  testImplementation(libs.test.junit)
  testImplementation(libs.test.truth)
}

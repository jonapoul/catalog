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
  alias(libs.plugins.agp.app) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.publish) apply false
  alias(libs.plugins.spotless) apply false
}

package dev.jonpoulton.catalog.gradle.test

import blueprint.test.ScenarioTest

abstract class CatalogScenarioTest : ScenarioTest() {
  override val gradleVersion: String = System.getProperty("test.version.gradle")
}

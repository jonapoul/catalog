package dev.jonpoulton.catalog.gradle

import blueprint.test.buildGradleKts
import blueprint.test.failsBuild
import blueprint.test.outputContains
import blueprint.test.settingsGradleKts
import blueprint.test.taskFailed
import dev.jonpoulton.catalog.gradle.test.ANDROID_TASK_NAME
import dev.jonpoulton.catalog.gradle.test.COMPILE_SDK
import dev.jonpoulton.catalog.gradle.test.CatalogScenarioTest
import dev.jonpoulton.catalog.gradle.test.RequiresAndroidSdk
import dev.jonpoulton.catalog.gradle.test.androidGradleProperties
import dev.jonpoulton.catalog.gradle.test.androidLocalProperties
import dev.jonpoulton.catalog.gradle.test.androidStringsXml
import dev.jonpoulton.catalog.gradle.test.assertThatCatalogTask
import kotlin.test.Test

@RequiresAndroidSdk
class InvalidResourcesScenario : CatalogScenarioTest() {
  override val fileTree = fileTree {
    settingsGradleKts()
    androidLocalProperties()
    androidGradleProperties()

    buildGradleKts(
      """
      plugins {
        id("com.android.library")
        id("dev.jonpoulton.catalog")
      }

      android {
        namespace = "a.b.c"
        compileSdk = $COMPILE_SDK
      }
      """
        .trimIndent()
    )

    androidStringsXml(
      $$"""
      <resources>
        <string name="valid_string">Hello %1$s</string>
        <string name="clashing_string">%1$s and %1$d</string>
        <string name="mixed_positions_string">%1$s and %s</string>
        <plurals name="clashing_plural">
          <item quantity="one">%d thing</item>
          <item quantity="other">%s things</item>
        </plurals>
      </resources>
      """
        .trimIndent()
    )
  }

  @Test
  fun `reports all invalid resources in one run`() = runScenario {
    assertThatCatalogTask(ANDROID_TASK_NAME)
      .failsBuild()
      .taskFailed(ANDROID_TASK_NAME)
      .outputContains("Found 3 invalid resources:")
      .outputContains("clashing_string")
      .outputContains("mixed_positions_string")
      .outputContains("clashing_plural")
  }
}

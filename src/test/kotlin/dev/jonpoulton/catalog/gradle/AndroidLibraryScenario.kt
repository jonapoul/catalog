package dev.jonpoulton.catalog.gradle

import assertk.assertThat
import blueprint.test.buildGradleKts
import blueprint.test.buildsSuccessfully
import blueprint.test.contentContains
import blueprint.test.exists
import blueprint.test.settingsGradleKts
import blueprint.test.taskSucceeded
import dev.jonpoulton.catalog.gradle.test.ANDROID_TASK_NAME
import dev.jonpoulton.catalog.gradle.test.COMPILE_SDK
import dev.jonpoulton.catalog.gradle.test.CatalogScenarioTest
import dev.jonpoulton.catalog.gradle.test.RequiresAndroidSdk
import dev.jonpoulton.catalog.gradle.test.androidGradleProperties
import dev.jonpoulton.catalog.gradle.test.androidLocalProperties
import dev.jonpoulton.catalog.gradle.test.androidStringsXml
import dev.jonpoulton.catalog.gradle.test.assertThatCatalogTask
import dev.jonpoulton.catalog.gradle.test.generatedFile
import kotlin.test.Test

@RequiresAndroidSdk
class AndroidLibraryScenario : CatalogScenarioTest() {
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
      """
      <resources>
        <!-- Here's a comment -->
        <string name="app_name">Hello World</string>

        <!-- Here's another comment -->
        <string-array name="my_string_array">
          <item>A</item>
          <item>B</item>
          <item>C</item>
        </string-array>
      </resources>
      """
        .trimIndent()
    )
  }

  @Test
  fun `generates strings`() = runScenario {
    assertThatCatalogTask(ANDROID_TASK_NAME).buildsSuccessfully().taskSucceeded(ANDROID_TASK_NAME)

    assertThat(generatedFile("kotlin/catalogMain/a/b/c/Strings.kt"))
      .exists()
      .contentContains(
        """
        public object Strings {
          /**
           * Here's a comment
           */
          public val appName: String
            @Composable
            @ReadOnlyComposable
            get() = stringResource(R.string.app_name)
        }
        """
          .trimIndent()
      )
  }

  @Test
  fun `generates string arrays`() = runScenario {
    assertThatCatalogTask(ANDROID_TASK_NAME).buildsSuccessfully().taskSucceeded(ANDROID_TASK_NAME)

    assertThat(generatedFile("kotlin/catalogMain/a/b/c/StringArrays.kt"))
      .exists()
      .contentContains(
        """
        public object StringArrays {
          /**
           * Here's another comment
           */
          public val myStringArray: Array<String>
            @Composable
            @ReadOnlyComposable
            get() = stringArrayResource(R.array.my_string_array)
        }
        """
          .trimIndent()
      )
  }
}

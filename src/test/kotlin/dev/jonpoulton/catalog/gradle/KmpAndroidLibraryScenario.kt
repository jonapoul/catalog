package dev.jonpoulton.catalog.gradle

import assertk.assertThat
import blueprint.test.buildGradleKts
import blueprint.test.buildsSuccessfully
import blueprint.test.contentContains
import blueprint.test.exists
import blueprint.test.settingsGradleKts
import blueprint.test.taskSucceeded
import dev.jonpoulton.catalog.gradle.test.COMPILE_SDK
import dev.jonpoulton.catalog.gradle.test.CatalogScenarioTest
import dev.jonpoulton.catalog.gradle.test.KMP_TASK_NAME
import dev.jonpoulton.catalog.gradle.test.RequiresAndroidSdk
import dev.jonpoulton.catalog.gradle.test.androidGradleProperties
import dev.jonpoulton.catalog.gradle.test.androidLocalProperties
import dev.jonpoulton.catalog.gradle.test.assertThatCatalogTask
import dev.jonpoulton.catalog.gradle.test.generatedFile
import dev.jonpoulton.catalog.gradle.test.kmpStringsXml
import kotlin.test.Test

@RequiresAndroidSdk
class KmpAndroidLibraryScenario : CatalogScenarioTest() {
  override val fileTree = fileTree {
    settingsGradleKts()
    androidLocalProperties()
    androidGradleProperties()

    buildGradleKts(
      """
      plugins {
        kotlin("multiplatform")
        kotlin("plugin.compose")
        id("com.android.kotlin.multiplatform.library")
        id("dev.jonpoulton.catalog")
        id("org.jetbrains.compose")
      }

      compose.resources {
        packageOfResClass = "x.y.z"
        nameOfResClass = "SomeOtherName"
      }

      kotlin {
        jvm()

        android {
          namespace = "a.b.c"
          compileSdk = $COMPILE_SDK
        }
      }
      """
        .trimIndent()
    )

    kmpStringsXml(
      """
      <resources>
        <!-- Here's a comment -->
        <string name="app_name">Hello World</string>
      </resources>
      """
        .trimIndent()
    )
  }

  @Test
  fun `generates strings`() = runScenario {
    assertThatCatalogTask(KMP_TASK_NAME).buildsSuccessfully().taskSucceeded(KMP_TASK_NAME)

    assertThat(generatedFile("kotlin/catalogCommonMain/a/b/c/Strings.kt"))
      .exists()
      .contentContains("package a.b.c")
      .contentContains("import x.y.z.SomeOtherName")
      .contentContains("import org.jetbrains.compose.resources.stringResource")
      .contentContains(
        """
        public object Strings {
          /**
           * Here's a comment
           */
          public val appName: String
            @Composable
            get() = stringResource(SomeOtherName.string.app_name)
        }
        """
          .trimIndent()
      )
  }
}

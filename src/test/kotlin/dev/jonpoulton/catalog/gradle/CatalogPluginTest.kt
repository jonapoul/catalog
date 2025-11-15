package dev.jonpoulton.catalog.gradle

import dev.jonpoulton.catalog.gradle.test.ANDROID_TASK_NAME
import dev.jonpoulton.catalog.gradle.test.KMP_TASK_NAME
import dev.jonpoulton.catalog.gradle.test.assertContains
import dev.jonpoulton.catalog.gradle.test.assertSuccess
import dev.jonpoulton.catalog.gradle.test.generatedFile
import dev.jonpoulton.catalog.gradle.test.runTask
import dev.jonpoulton.catalog.gradle.test.writeAndroidStringsFile
import dev.jonpoulton.catalog.gradle.test.writeBuildFile
import dev.jonpoulton.catalog.gradle.test.writeKmpStringsFile
import dev.jonpoulton.catalog.gradle.test.writeSettingsFile
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertTrue

class CatalogPluginTest {
  @get:Rule val temporaryFolder = TemporaryFolder()

  private lateinit var root: File

  @Before
  fun before() {
    root = temporaryFolder.root
    root.writeSettingsFile()
  }

  @Test
  fun `Generate strings in android library project`() = with(root) {
    writeBuildFile(
      """
        plugins {
          kotlin("android")
          id("com.android.library")
          id("dev.jonpoulton.catalog")
        }

        android {
          namespace = "a.b.c"
          compileSdk = 36
        }
      """.trimIndent(),
    )

    writeAndroidStringsFile(
      sourceSet = "main",
      code = """
        <resources>
          <!-- Here's a comment -->
          <string name="app_name">Hello World</string>
        </resources>
      """.trimIndent(),
    )

    runTask(root, ANDROID_TASK_NAME)
      .build()
      .assertSuccess(":$ANDROID_TASK_NAME")

    val outputFile = generatedFile("kotlin/catalogMain/a/b/c/Strings.kt")
    assertTrue(outputFile.exists())
    outputFile.assertContains(
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
      """.trimIndent(),
    )
  }

  @Test
  fun `Generate string arrays in android library project`() = with(root) {
    writeBuildFile(
      """
        plugins {
          kotlin("android")
          id("com.android.library")
          id("dev.jonpoulton.catalog")
        }

        android {
          namespace = "a.b.c"
          compileSdk = 36
        }
      """.trimIndent(),
    )

    writeAndroidStringsFile(
      sourceSet = "main",
      code = """
        <resources>
          <!-- Here's a comment -->
          <string-array name="my_string_array">
            <item>A</item>
            <item>B</item>
            <item>C</item>
          </string-array>
        </resources>
      """.trimIndent(),
    )

    runTask(root, ANDROID_TASK_NAME)
      .build()
      .assertSuccess(":$ANDROID_TASK_NAME")

    val outputFile = generatedFile("kotlin/catalogMain/a/b/c/StringArrays.kt")
    assertTrue(outputFile.exists())
    outputFile.assertContains(
      """
        public object StringArrays {
          /**
           * Here's a comment
           */
          public val myStringArray: Array<String>
            @Composable
            @ReadOnlyComposable
            get() = stringArrayResource(R.array.my_string_array)
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `Generate strings in KMP project`() = with(root) {
    writeBuildFile(
      """
        plugins {
          kotlin("multiplatform")
          kotlin("plugin.compose")
          id("com.android.library")
          id("dev.jonpoulton.catalog")
          id("org.jetbrains.compose")
        }

        android {
          namespace = "a.b.c"
          compileSdk = 36
        }

        compose.resources {
          packageOfResClass = "x.y.z"
          nameOfResClass = "SomeOtherName"
        }

        kotlin {
          jvm()
          androidTarget()
        }
      """.trimIndent(),
    )

    writeKmpStringsFile(
      code = """
        <resources>
          <!-- Here's a comment -->
          <string name="app_name">Hello World</string>
        </resources>
      """.trimIndent(),
    )

    runTask(root, KMP_TASK_NAME)
      .build()
      .assertSuccess(":$KMP_TASK_NAME")

    val outputFile = generatedFile("kotlin/catalogCommonMain/a/b/c/Strings.kt")
    assertTrue(outputFile.exists())
    outputFile.assertContains("package a.b.c")
    outputFile.assertContains("import x.y.z.SomeOtherName")
    outputFile.assertContains("import org.jetbrains.compose.resources.stringResource")
    outputFile.assertContains(
      """
        public object Strings {
          /**
           * Here's a comment
           */
          public val appName: String
            @Composable
            get() = stringResource(SomeOtherName.string.app_name)
        }
      """.trimIndent(),
    )
  }
}

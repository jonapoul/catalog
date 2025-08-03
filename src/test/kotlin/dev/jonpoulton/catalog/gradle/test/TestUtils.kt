@file:Suppress("UnusedReceiverParameter")

package dev.jonpoulton.catalog.gradle.test

import com.google.common.truth.StringSubject
import org.gradle.api.Project
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.Assume.assumeFalse
import org.junit.Assume.assumeTrue
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal const val ANDROID_TASK_NAME = "generateMainResourceCatalog"

internal fun StringSubject.isEqualToKotlin(
  @Language("kotlin") code: String,
) = isEqualTo(code.trimIndent())

internal fun File.writeBuildFile(
  @Language("kotlin") code: String,
) = resolve("build.gradle.kts").writeText(code)

internal fun File.writeSettingsFile() = resolve("settings.gradle.kts").writeText(
  """
    pluginManagement {
      repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
      }
    }

    dependencyResolutionManagement {
      repositories {
        mavenCentral()
        google()
      }
    }
  """.trimIndent(),
)

internal fun File.writeAndroidStringsFile(
  @Language("xml") code: String,
  name: String = "strings.xml",
  sourceSet: String = "main",
) = resolve("src/$sourceSet/res/values/$name")
  .also { it.parentFile.mkdirs() }
  .writeText(code)

@Deprecated(message = "Needs at least one param", level = DeprecationLevel.ERROR)
internal fun Project.runGradleTask(): Unit = error("Not supported")

internal fun buildRunner(root: File): GradleRunner = GradleRunner
  .create()
  .withPluginClasspath()
  .withProjectDir(root)
  .withGradleVersion(System.getProperty("test.version.gradle"))
  .withEnvironment(mapOf("ANDROID_HOME" to androidHomeOrSkip().absolutePath))

internal fun runTask(root: File, task: String): GradleRunner = buildRunner(root).runTask(task)

internal fun GradleRunner.runTask(task: String): GradleRunner = withArguments(
  task,
  "--configuration-cache",
  "--info",
  "--stacktrace",
  "-Pandroid.useAndroidX=true", // needed for android builds to work, unused otherwise
)

internal fun File.generatedFile(path: String): File = resolve("build/generated/$path")

internal fun File.assertContains(
  @Language("kotlin") code: String,
) = readText().let { contents ->
  assertTrue(actual = contents.contains(code), message = contents)
}

internal fun BuildResult.assertSuccess(task: String) =
  assertEquals(
    actual = task(task)?.outcome,
    expected = TaskOutcome.SUCCESS,
    message = output,
  )

internal fun androidHomeOrSkip(): File {
  val androidHome = System.getProperty("test.androidHome")
  assumeFalse(androidHome.isNullOrBlank())

  val androidHomePath = File(androidHome)
  assumeTrue(androidHomePath.exists())

  return androidHomePath
}

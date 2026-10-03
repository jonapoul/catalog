package dev.jonpoulton.catalog.gradle.test

import assertk.Assert
import blueprint.test.FileTree
import blueprint.test.Scenario
import blueprint.test.assertThatTask
import blueprint.test.gradleProperties
import blueprint.test.localProperties
import blueprint.test.withArgument
import blueprint.test.withConfigurationCache
import com.google.common.truth.StringSubject
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language

internal const val ANDROID_TASK_NAME = ":catalogMain"
internal const val KMP_TASK_NAME = ":catalogCommonMain"
internal const val COMPILE_SDK = 36

internal fun StringSubject.isEqualToKotlin(@Language("kotlin") code: String) =
  isEqualTo(code.trimIndent())

internal fun FileTree.Builder.androidLocalProperties() =
  localProperties("sdk.dir=${ANDROID_SDK?.invariantSeparatorsPath}")

internal fun FileTree.Builder.androidGradleProperties() =
  gradleProperties("android.useAndroidX=true")

internal fun FileTree.Builder.androidStringsXml(@Language("xml") contents: String) =
  ("src" / "main" / "res" / "values" / "strings.xml")(contents)

internal fun FileTree.Builder.kmpStringsXml(@Language("xml") contents: String) =
  ("src" / "commonMain" / "composeResources" / "values" / "strings.xml")(contents)

internal fun Scenario.assertThatCatalogTask(task: String): Assert<GradleRunner> =
  assertThatTask(task).withConfigurationCache().withArgument("--stacktrace")

internal fun Scenario.generatedFile(path: String): File = rootDir.resolve("build/generated/$path")

package dev.jonpoulton.catalog.gradle.test

import java.io.File
import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

internal val ANDROID_SDK: File? = System.getProperty("test.androidHome")?.let(::File)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(AndroidSdkCondition::class)
internal annotation class RequiresAndroidSdk

private class AndroidSdkCondition : ExecutionCondition {
  override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult =
    if (ANDROID_SDK?.isDirectory == true) {
      enabled("Android SDK at $ANDROID_SDK")
    } else {
      error("No Android SDK found, set ANDROID_HOME")
    }
}

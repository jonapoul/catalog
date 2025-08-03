package dev.jonpoulton.catalog.gradle

import dev.jonpoulton.catalog.gradle.internal.toCamelCase

fun interface NameTransform {
  operator fun invoke(resourceName: String): String

  companion object {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(
      message = "Zero-arg not supported, add at least one NameTransform",
      level = DeprecationLevel.ERROR,
    )
    fun chained(): NameTransform = error("Not supported")

    /**
     * Can be used to chain together any combination of other [NameTransform]s, in order of declaration. E.g.
     * ```
     * val transform = NameTransform.chained(
     *   NameTransform.removePrefix("my"),
     *   NameTransform.CamelCase,
     *   NameTransform.removeSuffix("ing"),
     * )
     * val output = transform("my_test_string") // -> "testStr"
     * ```
     */
    fun chained(vararg transforms: NameTransform) = NameTransform { name ->
      var mutableName = name
      transforms.forEach { transform -> mutableName = transform(mutableName) }
      mutableName
    }

    /**
     * Resource name of `"my_test_string"` transformed to property with no change, like:
     * ```kotlin
     * val my_test_string: String
     * ```
     */
    val NoChange = NameTransform { name -> name }

    /**
     * Resource name of `"my_test_string"` transformed to property like:
     * ```kotlin
     * val myTestString: String
     * ```
     */
    val CamelCase = NameTransform { name -> name.toCamelCase() }

    /**
     * Resource name of `"my_test_string"` with prefix of `"my_"` transformed to property like:
     * ```kotlin
     * val test_string: String
     * ```
     */
    fun removePrefix(prefix: String) = NameTransform { name -> name.removePrefix(prefix) }

    /**
     * Resource name of `"my_test_string"` with suffix of `"_string"` transformed to property like:
     * ```kotlin
     * val my_test: String
     * ```
     */
    fun removeSuffix(suffix: String) = NameTransform { name -> name.removeSuffix(suffix) }
  }
}

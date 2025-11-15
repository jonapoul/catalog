package dev.jonpoulton.catalog.gradle

import dev.jonpoulton.catalog.gradle.internal.toCamelCase
import kotlin.DeprecationLevel.ERROR

public fun interface NameTransform {
  public operator fun invoke(resourceName: String): String

  public companion object {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Zero-arg not supported, add at least one NameTransform", level = ERROR)
    public fun chained(): NameTransform = error("Not supported")

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
    public fun chained(vararg transforms: NameTransform): NameTransform = NameTransform { name ->
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
    public val NoChange: NameTransform = NameTransform { name -> name }

    /**
     * Resource name of `"my_test_string"` transformed to property like:
     * ```kotlin
     * val myTestString: String
     * ```
     */
    public val CamelCase: NameTransform = NameTransform { name -> name.toCamelCase() }

    /**
     * Resource name of `"my_test_string"` with prefix of `"my_"` transformed to property like:
     * ```kotlin
     * val test_string: String
     * ```
     */
    public fun removePrefix(prefix: String): NameTransform = NameTransform { name -> name.removePrefix(prefix) }

    /**
     * Resource name of `"my_test_string"` with suffix of `"_string"` transformed to property like:
     * ```kotlin
     * val my_test: String
     * ```
     */
    public fun removeSuffix(suffix: String): NameTransform = NameTransform { name -> name.removeSuffix(suffix) }
  }
}

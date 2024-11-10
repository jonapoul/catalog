/*
 * Copyright (C) 2022 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flaviofaria.catalog.gradle

open class CatalogExtension {
  /**
   * When true, Catalog will generate extension property getters to access resource IDs, like:
   *
   * ```kotlin
   * @get:StringRes
   * public inline val Strings.myTestString: Int
   *   get() = R.string.my_test_string
   * ```
   */
  var generateResourceProperties: Boolean = true

  /**
   * When true, Catalog will generate extension methods to access resource values. Example
   * resource in `strings.xml`:
   *
   * ```xml
   * <string name="my_test_string">Good morning, %1$s! It’s %2$d°C outside.</string>
   * ```
   *
   * and the generated Kotlin code:
   *
   * ```kotlin
   * context(Context)
   * public inline val Strings.myTestString(arg1: String, arg2: Int): Int {
   *   return getString(R.string.my_test_string, arg1, arg2)
   * }
   * ```
   */
  var generateResourcesExtensions: Boolean = true

  /**
   * When true, Catalog will generate Composable extension methods to access resource values.
   * If set to null, this property will be automatically enabled if the "androidx.compose.ui:ui"
   * Maven artifact is found in this Gradle module's dependencies.
   *
   * Example resource in `strings.xml`:
   *
   * ```xml
   * <string name="my_test_string">Good morning, %1$s! It’s %2$d°C outside.</string>
   * ```
   *
   * and the generated Kotlin code:
   *
   * ```kotlin
   * @Composable
   * @ReadOnlyComposable
   * public inline fun Strings.myTestResource(arg1: String, arg2: Int): CharSequence =
   *     stringResource(R.string.my_test_string, arg1, arg2)
   * ```
   */
  var generateComposeExtensions: Boolean? = null

  /**
   * If not explicitly set, this will be enabled if the "androidx.compose.animation:animation-graphics"
   * maven artifact is found in this Gradle module's dependencies.
   */
  var generateComposeAnimatedVectorExtensions: Boolean? = null
}

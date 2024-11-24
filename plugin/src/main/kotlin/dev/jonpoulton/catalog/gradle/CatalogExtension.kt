package dev.jonpoulton.catalog.gradle

open class CatalogExtension {
  /**
   * Package name of the generated resource accessor classes. Defaults to your project's namespace.
   */
  var packageName: String? = null

  /**
   * When true, all generated accessors will have internal visibility.
   */
  var generateInternal: Boolean = false

  /**
   * String to prepend before all generated types. E.g. a typePrefix of "Test" will generate a string accessor type of
   * `TestStrings`.
   */
  var typePrefix: String? = null

  /**
   * How to transform resource names into code. Defaults to camelcase, so "my_test_resource" is transformed to
   * "myTestResource".
   */
  var nameTransform: NameTransform = NameTransform.CamelCase

  /**
   * Defines the naming convention for string/plural resource parameters
   */
  var parameterNaming: CatalogParameterNaming = CatalogParameterNaming.ByType
}

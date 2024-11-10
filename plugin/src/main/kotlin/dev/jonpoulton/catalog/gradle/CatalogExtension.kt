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
   * Defines the naming convention for string/plural resource parameters
   */
  var parameterNaming: CatalogParameterNaming = CatalogParameterNaming.ByType
}

enum class CatalogParameterNaming {
  // Parameters named like "arg1", "arg2", "arg3"
  Arg,

  // Parameters named like "int1", "string2", "float3"
  ByType,
}

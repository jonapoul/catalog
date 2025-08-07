package dev.jonpoulton.catalog.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class CatalogExtension @Inject constructor(objects: ObjectFactory) {
  /**
   * When true, code generation will run during IDE sync as well as during regular build.
   */
  val generateAtSync: Property<Boolean> = objects
    .property<Boolean>()
    .convention(false)

  /**
   * Package name of the generated resource accessor classes. Defaults to your project's namespace.
   */
  val packageName: Property<String> = objects
    .property<String>()
    .unsetConvention()

  /**
   * When true, all generated accessors will have internal visibility. Defaults to public.
   */
  val generateInternal: Property<Boolean> = objects
    .property<Boolean>()
    .convention(false)

  /**
   * String to prepend before all generated types. E.g. a typePrefix of "Test" will generate a string accessor type of
   * `TestStrings`.
   */
  val typePrefix: Property<String> = objects
    .property<String>()
    .unsetConvention()

  /**
   * How to transform resource names into code. Defaults to camelcase, so "my_test_resource" is transformed to
   * "myTestResource".
   */
  val nameTransform: Property<NameTransform> = objects
    .property<NameTransform>()
    .convention(NameTransform.CamelCase)

  /**
   * Defines the naming convention for string/plural resource parameters
   */
  val parameterNaming: Property<CatalogParameterNaming> = objects
    .property<CatalogParameterNaming>()
    .convention(CatalogParameterNaming.ByType)
}

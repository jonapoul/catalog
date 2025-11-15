package dev.jonpoulton.catalog.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

public open class CatalogExtension @Inject constructor(objects: ObjectFactory) {
  /**
   * When true, code generation will run during IDE sync as well as during regular build.
   */
  public val generateAtSync: Property<Boolean> = objects
    .property(Boolean::class.java)
    .convention(false)

  /**
   * Package name of the generated resource accessor classes. Defaults to your project's namespace.
   */
  public val packageName: Property<String> = objects
    .property(String::class.java)
    .unsetConvention()

  /**
   * When true, all generated accessors will have internal visibility. Defaults to public.
   */
  public val generateInternal: Property<Boolean> = objects
    .property(Boolean::class.java)
    .convention(false)

  /**
   * String to prepend before all generated types. E.g. a typePrefix of "Test" will generate a string accessor type of
   * `TestStrings`.
   */
  public val typePrefix: Property<String> = objects
    .property(String::class.java)
    .unsetConvention()

  /**
   * How to transform resource names into code. Defaults to camelcase, so "my_test_resource" is transformed to
   * "myTestResource".
   */
  public val nameTransform: Property<NameTransform> = objects
    .property(NameTransform::class.java)
    .convention(NameTransform.CamelCase)

  /**
   * Defines the naming convention for string/plural resource parameters
   */
  public val parameterNaming: Property<CatalogParameterNaming> = objects
    .property(CatalogParameterNaming::class.java)
    .convention(CatalogParameterNaming.ByType)
}

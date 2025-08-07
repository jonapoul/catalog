@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class StringArrayCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.StringArray,
) : CatalogWriter<ResourceEntry.XmlItem.StringArray>() {
  private val stringArrayResourceMember by lazy { resourceAccessor("stringArrayResource") }

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.XmlItem.StringArray,
  ): TypeSpec.Builder {
    val statementArgs = arrayOf(stringArrayResourceMember, rClass, resource.name)
    val statementFormat = "return %M(%T.array.%L)"

    val stringArrayType = Array::class
      .asClassName()
      .parameterizedBy(String::class.asClassName())

    val getter = FunSpec
      .getterBuilder()
      .addAnnotation(composableClass)
      .addReadOnlyComposable(config)
      .addStatement(statementFormat, *statementArgs)
      .build()

    return addProperty(
      PropertySpec
        .builder(config.nameTransform(resource.name), stringArrayType)
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(getter)
        .build(),
    )
  }
}

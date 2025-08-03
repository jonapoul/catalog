@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class IntegerCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.Integer,
) : CatalogWriter<ResourceEntry.XmlItem.Integer>() {
  private val integerResourceMember = MemberName("androidx.compose.ui.res", "integerResource")

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.XmlItem.Integer,
  ): TypeSpec.Builder {
    val statementFormat = "return %M(%T.integer.%L)"
    val statementArgs = arrayOf(integerResourceMember, rClass, resource.name)

    val getter = FunSpec
      .getterBuilder()
      .addAnnotation(composableClass)
      .addAnnotation(readOnlyComposableClass)
      .addStatement(statementFormat, *statementArgs)
      .build()

    return addProperty(
      PropertySpec
        .builder(config.nameTransform(resource.name), Int::class.asClassName())
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(getter)
        .build(),
    )
  }
}

@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class ColorCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = Color,
) : CatalogWriter<ResourceEntry.XmlItem.Color>() {
  private val colorResourceMember by lazy { resourceAccessor("colorResource") }
  private val composeColorClass = ClassName("androidx.compose.ui.graphics", "Color")

  override fun buildMember(resource: ResourceEntry.XmlItem.Color, receiver: ClassName?): Member =
    Member.Property(
      PropertySpec.builder(config.nameTransform(resource.name), composeColorClass)
        .receiverIfSet(receiver)
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(buildGetter(resource))
        .build(),
    )

  private fun buildGetter(resource: ResourceEntry.XmlItem.Color): FunSpec {
    val statementFormat = "return %M(%T.color.%L)"
    val statementArgs = arrayOf(colorResourceMember, rClass, resource.name)
    return FunSpec.getterBuilder()
      .addAnnotation(composableClass)
      .addReadOnlyComposable(config)
      .addStatement(statementFormat, *statementArgs)
      .build()
  }
}

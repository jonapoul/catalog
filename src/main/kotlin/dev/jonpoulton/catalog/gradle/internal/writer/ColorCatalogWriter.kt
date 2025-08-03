@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class ColorCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.Color,
) : CatalogWriter<ResourceEntry.XmlItem.Color>() {
  private val colorResourceMember = MemberName("androidx.compose.ui.res", "colorResource")
  private val composeColorClass = ClassName("androidx.compose.ui.graphics", "Color")

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.XmlItem.Color,
  ): TypeSpec.Builder = addProperty(
    PropertySpec
      .builder(config.nameTransform(resource.name), composeColorClass)
      .addKdoc(resource)
      .addInternalIfConfigured()
      .mutable(false)
      .getter(buildGetter(resource))
      .build(),
  )

  private fun buildGetter(resource: ResourceEntry.XmlItem.Color): FunSpec {
    val statementFormat = "return %M(%T.color.%L)"
    val statementArgs = arrayOf(colorResourceMember, rClass, resource.name)
    return FunSpec
      .getterBuilder()
      .addAnnotation(composableClass)
      .addAnnotation(readOnlyComposableClass)
      .addStatement(statementFormat, *statementArgs)
      .build()
  }
}

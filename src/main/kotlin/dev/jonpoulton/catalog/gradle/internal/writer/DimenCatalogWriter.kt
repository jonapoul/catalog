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

internal class DimenCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.Dimen,
) : CatalogWriter<ResourceEntry.XmlItem.Dimen>() {
  private val dimensionResourceMember = MemberName("androidx.compose.ui.res", "dimensionResource")
  private val composeDpClass = ClassName(packageName = "androidx.compose.ui.unit", "Dp")

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.XmlItem.Dimen,
  ): TypeSpec.Builder {
    val statementFormat = "return %M(%T.dimen.%L)"
    val statementArgs = arrayOf(dimensionResourceMember, rClass, resource.name)

    val getter = FunSpec
      .getterBuilder()
      .addAnnotation(composableClass)
      .addAnnotation(readOnlyComposableClass)
      .addStatement(statementFormat, *statementArgs)
      .build()

    return addProperty(
      PropertySpec
        .builder(config.nameTransform(resource.name), composeDpClass)
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(getter)
        .build(),
    )
  }
}

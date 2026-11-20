@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class DimenCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = Dimen,
) : CatalogWriter<ResourceEntry.XmlItem.Dimen>() {
  private val dimensionResourceMember by lazy { resourceAccessor("dimensionResource") }
  private val composeDpClass = ClassName(packageName = "androidx.compose.ui.unit", "Dp")

  override fun buildMember(
    resource: ResourceEntry.XmlItem.Dimen,
    receiver: ClassName?,
  ): Member {
    val statementFormat = "return %M(%T.dimen.%L)"
    val statementArgs = arrayOf(dimensionResourceMember, rClass, resource.name)

    val getter =
      FunSpec.getterBuilder()
        .addAnnotation(composableClass)
        .addReadOnlyComposable(config)
        .addStatement(statementFormat, *statementArgs)
        .build()

    return Member.Property(
      PropertySpec.builder(config.nameTransform(resource.name), composeDpClass)
        .receiverIfSet(receiver)
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(getter)
        .build(),
    )
  }
}

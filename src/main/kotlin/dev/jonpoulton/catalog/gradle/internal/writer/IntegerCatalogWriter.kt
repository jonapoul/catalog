@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class IntegerCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.Integer,
) : CatalogWriter<ResourceEntry.XmlItem.Integer>() {
  private val integerResourceMember by lazy { resourceAccessor("integerResource") }

  override fun buildMember(
    resource: ResourceEntry.XmlItem.Integer,
    receiver: ClassName?,
  ): Member {
    val statementFormat = "return %M(%T.integer.%L)"
    val statementArgs = arrayOf(integerResourceMember, rClass, resource.name)

    val getter =
      FunSpec.getterBuilder()
        .addAnnotation(composableClass)
        .addReadOnlyComposable(config)
        .addStatement(statementFormat, *statementArgs)
        .build()

    return Member.Property(
      PropertySpec.builder(config.nameTransform(resource.name), Int::class.asClassName())
        .receiverIfSet(receiver)
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(getter)
        .build(),
    )
  }
}

@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class StringCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.String,
) : CatalogWriter<ResourceEntry.XmlItem.WithArgs.String>() {
  private val stringResourceMember by lazy { resourceAccessor("stringResource") }

  override fun buildMember(
    resource: ResourceEntry.XmlItem.WithArgs.String,
    receiver: ClassName?,
  ): Member {
    val sortedArgs = resource.args.sortedBy { it.position }
    val formattedParameters = getFormattedParameters(sortedArgs, config.parameterNaming)
    val statementArgs = mutableListOf(stringResourceMember, rClass, resource.name)
    statementArgs.addAll(formattedParameters.map { it.name })
    val functionCallParams = mutableListOf("%T.${resourceType.resourceGroup}.%L")
    functionCallParams.addAll(resource.args.map { "%L" })
    val statementFormat = "return %M(${functionCallParams.joinToString()})"
    val name = config.nameTransform(resource.name)

    return if (sortedArgs.isEmpty()) {
      // No args, so make a property with a composable getter
      val getter =
        FunSpec.getterBuilder()
          .addAnnotation(composableClass)
          .addReadOnlyComposable(config)
          .addStatement(statementFormat, *statementArgs.toTypedArray())
          .build()
      Member.Property(
        PropertySpec.builder(name, String::class)
          .receiverIfSet(receiver)
          .addKdoc(resource)
          .addInternalIfConfigured()
          .mutable(false)
          .getter(getter)
          .build(),
      )
    } else {
      // At least one arg, so make a composable function
      Member.Function(
        FunSpec.builder(name)
          .receiverIfSet(receiver)
          .addKdoc(resource)
          .addAnnotation(composableClass)
          .addReadOnlyComposable(config)
          .addInternalIfConfigured()
          .addFormattedParameters(formattedParameters)
          .returns(String::class)
          .addStatement(statementFormat, *statementArgs.toTypedArray())
          .build(),
      )
    }
  }
}

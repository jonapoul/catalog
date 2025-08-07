@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.NameTransform
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class StringCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.String,
) : CatalogWriter<ResourceEntry.XmlItem.WithArgs.String>() {
  private val stringResourceMember by lazy { resourceAccessor("stringResource") }

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.XmlItem.WithArgs.String,
  ): TypeSpec.Builder {
    val sortedArgs = resource.args.sortedBy { it.position }
    val formattedParameters = getFormattedParameters(sortedArgs, config.parameterNaming)
    val statementArgs = mutableListOf(stringResourceMember, rClass, resource.name)
    statementArgs.addAll(formattedParameters.map { it.name })
    val functionCallParams = mutableListOf("%T.${resourceType.resourceGroup}.%L")
    functionCallParams.addAll(resource.args.map { "%L" })
    val statementFormat = "return %M(${functionCallParams.joinToString()})"

    return if (sortedArgs.isEmpty()) {
      // No args, so make a property with a composable getter
      addProperty(resource, config.nameTransform, statementFormat, statementArgs)
    } else {
      // At least one arg, so make a composable function
      addFunction(resource, config.nameTransform, statementFormat, statementArgs, formattedParameters)
    }
  }

  private fun TypeSpec.Builder.addProperty(
    resource: ResourceEntry.XmlItem.WithArgs.String,
    nameTransform: NameTransform,
    statementFormat: String,
    statementArgs: MutableList<Any>,
  ): TypeSpec.Builder {
    val getter = FunSpec
      .getterBuilder()
      .addAnnotation(composableClass)
      .addReadOnlyComposable(config)
      .addStatement(statementFormat, *statementArgs.toTypedArray())
      .build()
    val property = PropertySpec
      .builder(nameTransform(resource.name), String::class)
      .addKdoc(resource)
      .addInternalIfConfigured()
      .mutable(false)
      .getter(getter)
      .build()
    return addProperty(property)
  }

  private fun TypeSpec.Builder.addFunction(
    resource: ResourceEntry.XmlItem.WithArgs.String,
    nameTransform: NameTransform,
    statementFormat: String,
    statementArgs: MutableList<Any>,
    formattedParameters: List<FormattedParameter>,
  ): TypeSpec.Builder {
    val function = FunSpec
      .builder(nameTransform(resource.name))
      .addKdoc(resource)
      .addAnnotation(composableClass)
      .addReadOnlyComposable(config)
      .addInternalIfConfigured()
      .addFormattedParameters(formattedParameters)
      .returns(String::class)
      .addStatement(statementFormat, *statementArgs.toTypedArray())
      .build()
    return addFunction(function)
  }
}

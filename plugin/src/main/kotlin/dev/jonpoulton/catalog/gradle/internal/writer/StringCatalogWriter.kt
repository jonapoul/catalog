@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType
import dev.jonpoulton.catalog.gradle.internal.toCamelCase

internal class StringCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.String,
) : CatalogWriter<ResourceEntry.XmlItem.WithArgs.String>() {
  private val stringResourceMember = MemberName("androidx.compose.ui.res", "stringResource")

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
      val getter = FunSpec
        .getterBuilder()
        .addAnnotation(composableClass)
        .addAnnotation(readOnlyComposableClass)
        .addStatement(statementFormat, *statementArgs.toTypedArray())
        .build()
      val property = PropertySpec
        .builder(resource.name.toCamelCase(), String::class)
        .addKdoc(resource)
        .addInternalIfConfigured()
        .mutable(false)
        .getter(getter)
        .build()
      addProperty(property)
    } else {
      // At least one arg, so make a composable function
      val function = FunSpec
        .builder(resource.name.toCamelCase())
        .addKdoc(resource)
        .addAnnotation(composableClass)
        .addAnnotation(readOnlyComposableClass)
        .addInternalIfConfigured()
        .addFormattedParameters(formattedParameters)
        .returns(String::class)
        .addStatement(statementFormat, *statementArgs.toTypedArray())
        .build()
      addFunction(function)
    }
  }
}

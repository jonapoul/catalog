@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceType

internal class PluralCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.Plural,
) : CatalogWriter<ResourceEntry.XmlItem.WithArgs.Plural>() {
  private val experimentalComposeUiApiClass = ClassName("androidx.compose.ui", "ExperimentalComposeUiApi")

  private val pluralResourceMember by lazy { resourceAccessor("pluralStringResource") }

  private val optInClass = ClassName("kotlin", "OptIn")

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.XmlItem.WithArgs.Plural,
  ): TypeSpec.Builder {
    val sortedArgs = resource.args.sortedBy { it.position }
    val formattedParameters = getFormattedParameters(sortedArgs, config.parameterNaming)
    val methodName = "%M"
    val quantityParamName = "quantity"

    val statementArgs = mutableListOf(pluralResourceMember, rClass, resource.name, quantityParamName)
    statementArgs.addAll(formattedParameters.map { it.name })

    val functionCallParams = mutableListOf("%T.${resourceType.resourceGroup}.%L", "%L")
    functionCallParams.addAll(resource.args.map { "%L" })
    val statementFormat = "return $methodName(${functionCallParams.joinToString()})"

    val annotation = AnnotationSpec
      .builder(optInClass)
      .addMember("%T::class", experimentalComposeUiApiClass)
      .build()

    val function = FunSpec
      .builder(config.nameTransform(resource.name))
      .addKdoc(resource)
      .apply { if (config.pluralAccessorIsExperimental) addAnnotation(annotation) }
      .addAnnotation(composableClass)
      .addReadOnlyComposable(config)
      .addInternalIfConfigured()
      .addParameter(name = quantityParamName, type = Int::class)
      .addFormattedParameters(formattedParameters)
      .returns(String::class)
      .addStatement(statementFormat, *statementArgs.toTypedArray())
      .build()

    return addFunction(function)
  }
}

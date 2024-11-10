package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.FunSpec
import dev.jonpoulton.catalog.gradle.CatalogParameterNaming
import dev.jonpoulton.catalog.gradle.internal.StringArg
import kotlin.reflect.KClass

@Suppress("CyclomaticComplexMethod")
internal fun getFormattedParameters(
  sortedArgs: List<StringArg>,
  parameterNaming: CatalogParameterNaming,
): List<FormattedParameter> = sortedArgs.map { (position, typeChar) ->
  val type = when (typeChar) {
    'd', 'i' -> Int::class
    'u', 'x', 'o' -> UInt::class
    'f', 'e', 'g', 'a' -> Double::class
    's' -> String::class
    'c' -> Char::class
    else -> error("Unexpected argument type $typeChar")
  }

  val name = when (parameterNaming) {
    CatalogParameterNaming.Arg -> "arg$position"
    CatalogParameterNaming.ByType -> when (type) {
      Int::class -> "int"
      UInt::class -> "uint"
      Double::class -> "double"
      String::class -> "string"
      Char::class -> "char"
      else -> error("Unexpected type $type")
    } + position.toString()
  }

  FormattedParameter(type, name)
}

internal fun FunSpec.Builder.addFormattedParameters(parameters: List<FormattedParameter>): FunSpec.Builder {
  parameters.forEach { parameter ->
    addParameter(parameter.name, parameter.type)
  }
  return this
}

internal data class FormattedParameter(
  val type: KClass<*>,
  val name: String,
)

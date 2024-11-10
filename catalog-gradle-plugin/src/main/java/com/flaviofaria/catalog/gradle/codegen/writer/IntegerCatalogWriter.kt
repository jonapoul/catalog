/*
 * Copyright (C) 2023 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
import com.flaviofaria.catalog.gradle.codegen.ResourceType
import com.flaviofaria.catalog.gradle.codegen.toCamelCase
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

class IntegerCatalogWriter(
  packageName: String,
) : CatalogWriter<ResourceEntry.XmlItem.Integer>(
  packageName, ResourceType.Integer,
) {
  private val integerResourceMember = MemberName(
    packageName = "androidx.compose.ui.res",
    simpleName = "integerResource",
  )

  override fun buildExtensionMethod(
    builder: FileSpec.Builder,
    resource: ResourceEntry.XmlItem.Integer,
    contextReceiver: TypeName?,
    asComposeExtensions: Boolean,
  ): FileSpec.Builder {
    val statementArgs: Array<Any>
    val statementFormat: String
    if (asComposeExtensions) {
      statementFormat = "return %M(%T.integer.%L)"
      statementArgs = arrayOf(integerResourceMember, rClass, resource.name)
    } else {
      statementFormat = "return resources.getInteger(%T.integer.%L)"
      statementArgs = arrayOf(rClass, resource.name)
    }

    val function = FunSpec.builder(resource.name.toCamelCase())
      .optionallyAddKdoc(resource)
      .optionallyAddComposeAnnotations(asComposeExtensions)
      .addModifiers(KModifier.INLINE)
      .optionallyAddContextReceiver(contextReceiver)
      .addReceiver(asComposeExtensions)
      .returns(Int::class.asClassName())
      .addStatement(statementFormat, *statementArgs)
      .build()

    return builder.addFunction(function)
  }
}

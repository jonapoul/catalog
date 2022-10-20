package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.charset.Charset

class StringArrayCatalogWriterTest {

    @get:Rule
    var folder = TemporaryFolder()

    private val resources = listOf(
        ResourceEntry.StringArray(
            file = File("."),
            name = "string_array_1",
            docs = "String array 1 docs",
        ),
        ResourceEntry.StringArray(
            file = File("."),
            name = "string_array_2",
            docs = null,
        ),
    )

    @Test
    fun `GIVEN composeExtensions disabled THEN generate standard extensions`() {
        val writer = StringArrayCatalogWriter(
            packageName = "com.example",
            composeExtensions = false,
        )
        val codegenDestination = folder.newFolder()
        val codegenFile = File(codegenDestination, "StringArrays.kt")
        writer.write(resources, "main", codegenDestination = codegenDestination)
        assertThat(
            codegenFile.readBytes().toString(Charset.defaultCharset()),
        ).isEqualTo(
            """
            |@file:JvmName("StringArraysMain")
            |@file:Suppress("NOTHING_TO_INLINE")
            |package com.example
            |
            |import android.content.Context
            |import android.view.View
            |import androidx.fragment.app.Fragment
            |import com.flaviofaria.catalog.runtime.StringArrays
            |
            |import com.example.R
            |
            |/**
            | * String array 1 docs
            | */
            |inline val StringArrays.stringArray1: Int
            |  get() = R.array.string_array_1
            |
            |inline val StringArrays.stringArray2: Int
            |  get() = R.array.string_array_2
            |
            |/**
            | * String array 1 docs
            | */
            |context(Context)
            |inline fun StringArrays.stringArray1(): Array<String> {
            |  return resources.getStringArray(R.array.string_array_1)
            |}
            |
            |context(Context)
            |inline fun StringArrays.stringArray2(): Array<String> {
            |  return resources.getStringArray(R.array.string_array_2)
            |}
            |
            |/**
            | * String array 1 docs
            | */
            |context(Fragment)
            |inline fun StringArrays.stringArray1(): Array<String> {
            |  return resources.getStringArray(R.array.string_array_1)
            |}
            |
            |context(Fragment)
            |inline fun StringArrays.stringArray2(): Array<String> {
            |  return resources.getStringArray(R.array.string_array_2)
            |}
            |""".trimMargin(),
        )
    }

    @Test
    fun `GIVEN composeExtensions enabled THEN generate standard extensions`() {
        val writer = StringArrayCatalogWriter(
            packageName = "com.example",
            composeExtensions = true,
        )
        val codegenDestination = folder.newFolder()
        val codegenFile = File(codegenDestination, "StringArrays.kt")
        writer.write(resources, "main", codegenDestination = codegenDestination)
        assertThat(
            codegenFile.readBytes().toString(Charset.defaultCharset()),
        ).isEqualTo(
            """
            |@file:JvmName("StringArraysMain")
            |@file:Suppress("NOTHING_TO_INLINE")
            |package com.example
            |
            |import androidx.compose.runtime.Composable
            |import androidx.compose.runtime.ReadOnlyComposable
            |import androidx.compose.ui.res.stringArrayResource
            |import android.content.Context
            |import android.view.View
            |import androidx.fragment.app.Fragment
            |import com.flaviofaria.catalog.runtime.StringArrays
            |
            |import com.example.R
            |
            |/**
            | * String array 1 docs
            | */
            |inline val StringArrays.stringArray1: Int
            |  get() = R.array.string_array_1
            |
            |inline val StringArrays.stringArray2: Int
            |  get() = R.array.string_array_2
            |
            |/**
            | * String array 1 docs
            | */
            |context(Context)
            |@Composable
            |@ReadOnlyComposable
            |fun StringArrays.stringArray1(): Array<String> {
            |  return stringArrayResource(R.array.string_array_1)
            |}
            |
            |context(Context)
            |@Composable
            |@ReadOnlyComposable
            |fun StringArrays.stringArray2(): Array<String> {
            |  return stringArrayResource(R.array.string_array_2)
            |}
            |
            |/**
            | * String array 1 docs
            | */
            |context(Fragment)
            |@Composable
            |@ReadOnlyComposable
            |fun StringArrays.stringArray1(): Array<String> {
            |  return stringArrayResource(R.array.string_array_1)
            |}
            |
            |context(Fragment)
            |@Composable
            |@ReadOnlyComposable
            |fun StringArrays.stringArray2(): Array<String> {
            |  return stringArrayResource(R.array.string_array_2)
            |}
            |""".trimMargin(),
        )
    }
}
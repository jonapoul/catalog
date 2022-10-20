package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.StringArg
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.charset.Charset

class StringCatalogWriterTest {

    @get:Rule
    var folder = TemporaryFolder()

    private val resources = listOf(
        ResourceEntry.String(
            file = File("."),
            name = "string_1",
            docs = "String 1 docs",
            args = emptyList(),
        ),
        ResourceEntry.String(
            file = File("."),
            name = "string_2",
            docs = null,
            args = listOf(
                StringArg(
                    position = 1,
                    type = 'd',
                    isOptional = false,
                ),
                StringArg(
                    position = 2,
                    type = 'i',
                    isOptional = false,
                ),
                StringArg(
                    position = 3,
                    type = 'u',
                    isOptional = true,
                ),
                StringArg(
                    position = 4,
                    type = 'x',
                    isOptional = false,
                ),
                StringArg(
                    position = 5,
                    type = 'o',
                    isOptional = false,
                ),
            ),
        ),
        ResourceEntry.String(
            file = File("."),
            name = "string_3",
            docs = null,
            args = listOf(
                StringArg(
                    position = 1,
                    type = 'f',
                    isOptional = true,
                ),
                StringArg(
                    position = 2,
                    type = 'e',
                    isOptional = false,
                ),
                StringArg(
                    position = 3,
                    type = 'g',
                    isOptional = false,
                ),
                StringArg(
                    position = 4,
                    type = 'a',
                    isOptional = true,
                ),
                StringArg(
                    position = 5,
                    type = 's',
                    isOptional = true,
                ),
            ),
        ),
        ResourceEntry.String(
            file = File("."),
            name = "string_4",
            docs = null,
            args = listOf(
                StringArg(
                    position = 1,
                    type = 'c',
                    isOptional = true,
                ),
                StringArg(
                    position = 2,
                    type = 'n',
                    isOptional = false,
                ),
            ),
        ),
    )

    @Test
    fun `GIVEN composeExtensions disabled THEN generate standard extensions`() {
        val writer = StringCatalogWriter(
            packageName = "com.example",
            composeExtensions = false,
        )
        val codegenDestination = folder.newFolder()
        val codegenFile = File(codegenDestination, "Strings.kt")
        writer.write(resources, "main", codegenDestination = codegenDestination)
        assertThat(
            codegenFile.readBytes().toString(Charset.defaultCharset()),
        ).isEqualTo(
            """
            |@file:JvmName("StringsMain")
            |@file:Suppress("NOTHING_TO_INLINE")
            |package com.example
            |
            |import android.content.Context
            |import android.view.View
            |import androidx.fragment.app.Fragment
            |import com.flaviofaria.catalog.runtime.Strings
            |
            |import com.example.R
            |
            |/**
            | * String 1 docs
            | */
            |inline val Strings.string1: Int
            |  get() = R.string.string_1
            |
            |inline val Strings.string2: Int
            |  get() = R.string.string_2
            |
            |inline val Strings.string3: Int
            |  get() = R.string.string_3
            |
            |inline val Strings.string4: Int
            |  get() = R.string.string_4
            |
            |/**
            | * String 1 docs
            | */
            |context(Context)
            |inline fun Strings.string1(): CharSequence {
            |  return getText(R.string.string_1)
            |}
            |
            |context(Context)
            |inline fun Strings.string2(arg1: Int, arg2: Int, arg3: UInt?, arg4: UInt, arg5: UInt): String {
            |  return getString(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |inline fun Strings.string3(arg1: Double?, arg2: Double, arg3: Double, arg4: Double?, arg5: String?): String {
            |  return getString(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |inline fun Strings.string4(arg1: Char?): String {
            |  return getString(R.string.string_4, arg1, arg2)
            |}
            |
            |/**
            | * String 1 docs
            | */
            |context(Fragment)
            |inline fun Strings.string1(): CharSequence {
            |  return getText(R.string.string_1)
            |}
            |
            |context(Fragment)
            |inline fun Strings.string2(arg1: Int, arg2: Int, arg3: UInt?, arg4: UInt, arg5: UInt): String {
            |  return getString(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |inline fun Strings.string3(arg1: Double?, arg2: Double, arg3: Double, arg4: Double?, arg5: String?): String {
            |  return getString(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |inline fun Strings.string4(arg1: Char?): String {
            |  return getString(R.string.string_4, arg1, arg2)
            |}
            |""".trimMargin(),
        )
    }

    @Test
    fun `GIVEN composeExtensions enabled THEN generate standard extensions`() {
        val writer = StringCatalogWriter(
            packageName = "com.example",
            composeExtensions = true,
        )
        val codegenDestination = folder.newFolder()
        val codegenFile = File(codegenDestination, "Strings.kt")
        writer.write(resources, "main", codegenDestination = codegenDestination)
        assertThat(
            codegenFile.readBytes().toString(Charset.defaultCharset()),
        ).isEqualTo(
            """
            |@file:JvmName("StringsMain")
            |@file:Suppress("NOTHING_TO_INLINE")
            |package com.example
            |
            |import androidx.compose.runtime.Composable
            |import androidx.compose.runtime.ReadOnlyComposable
            |import androidx.compose.ui.res.stringResource
            |import android.content.Context
            |import android.view.View
            |import androidx.fragment.app.Fragment
            |import com.flaviofaria.catalog.runtime.Strings
            |
            |import com.example.R
            |
            |/**
            | * String 1 docs
            | */
            |inline val Strings.string1: Int
            |  get() = R.string.string_1
            |
            |inline val Strings.string2: Int
            |  get() = R.string.string_2
            |
            |inline val Strings.string3: Int
            |  get() = R.string.string_3
            |
            |inline val Strings.string4: Int
            |  get() = R.string.string_4
            |
            |/**
            | * String 1 docs
            | */
            |context(Context)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string1(): CharSequence {
            |  return stringResource(R.string.string_1)
            |}
            |
            |context(Context)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string2(arg1: Int, arg2: Int, arg3: UInt, arg4: UInt, arg5: UInt): String {
            |  return stringResource(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string3(arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: String): String {
            |  return stringResource(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string4(arg1: Char): String {
            |  return stringResource(R.string.string_4, arg1, arg2)
            |}
            |
            |/**
            | * String 1 docs
            | */
            |context(Fragment)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string1(): CharSequence {
            |  return stringResource(R.string.string_1)
            |}
            |
            |context(Fragment)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string2(arg1: Int, arg2: Int, arg3: UInt, arg4: UInt, arg5: UInt): String {
            |  return stringResource(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string3(arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: String): String {
            |  return stringResource(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |@Composable
            |@ReadOnlyComposable
            |fun Strings.string4(arg1: Char): String {
            |  return stringResource(R.string.string_4, arg1, arg2)
            |}
            |""".trimMargin(),
        )
    }
}
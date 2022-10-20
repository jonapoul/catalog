package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.StringArg
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.charset.Charset

class PluralCatalogWriterTest {

    @get:Rule
    var folder = TemporaryFolder()

    private val resources = listOf(
        ResourceEntry.Plural(
            file = File("."),
            name = "plural_1",
            docs = "Plural 1 docs",
            args = emptyList(),
        ),
        ResourceEntry.Plural(
            file = File("."),
            name = "plural_2",
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
        ResourceEntry.Plural(
            file = File("."),
            name = "plural_3",
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
        ResourceEntry.Plural(
            file = File("."),
            name = "plural_4",
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
        val writer = PluralCatalogWriter(
            packageName = "com.example",
            composeExtensions = false,
        )
        val codegenDestination = folder.newFolder()
        val codegenFile = File(codegenDestination, "Plurals.kt")
        writer.write(resources, "main", codegenDestination = codegenDestination)
        assertThat(
            codegenFile.readBytes().toString(Charset.defaultCharset()),
        ).isEqualTo(
            """
            |@file:JvmName("PluralsMain")
            |@file:Suppress("NOTHING_TO_INLINE")
            |package com.example
            |
            |import android.content.Context
            |import android.view.View
            |import androidx.fragment.app.Fragment
            |import com.flaviofaria.catalog.runtime.Plurals
            |
            |import com.example.R
            |
            |/**
            | * Plural 1 docs
            | */
            |inline val Plurals.plural1: Int
            |  get() = R.plurals.plural_1
            |
            |inline val Plurals.plural2: Int
            |  get() = R.plurals.plural_2
            |
            |inline val Plurals.plural3: Int
            |  get() = R.plurals.plural_3
            |
            |inline val Plurals.plural4: Int
            |  get() = R.plurals.plural_4
            |
            |/**
            | * Plural 1 docs
            | */
            |context(Context)
            |inline fun Plurals.plural1(quantity: Int, ): String {
            |  return resources.getQuantityString(R.plurals.plural_1, quantity)
            |}
            |
            |context(Context)
            |inline fun Plurals.plural2(quantity: Int, arg1: Int, arg2: Int, arg3: UInt?, arg4: UInt, arg5: UInt): String {
            |  return resources.getQuantityString(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |inline fun Plurals.plural3(quantity: Int, arg1: Double?, arg2: Double, arg3: Double, arg4: Double?, arg5: String?): String {
            |  return resources.getQuantityString(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |inline fun Plurals.plural4(quantity: Int, arg1: Char?): String {
            |  return resources.getQuantityString(R.plurals.plural_4, quantity, arg1, arg2)
            |}
            |
            |/**
            | * Plural 1 docs
            | */
            |context(Fragment)
            |inline fun Plurals.plural1(quantity: Int, ): String {
            |  return resources.getQuantityString(R.plurals.plural_1, quantity)
            |}
            |
            |context(Fragment)
            |inline fun Plurals.plural2(quantity: Int, arg1: Int, arg2: Int, arg3: UInt?, arg4: UInt, arg5: UInt): String {
            |  return resources.getQuantityString(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |inline fun Plurals.plural3(quantity: Int, arg1: Double?, arg2: Double, arg3: Double, arg4: Double?, arg5: String?): String {
            |  return resources.getQuantityString(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |inline fun Plurals.plural4(quantity: Int, arg1: Char?): String {
            |  return resources.getQuantityString(R.plurals.plural_4, quantity, arg1, arg2)
            |}
            |""".trimMargin(),
        )
    }

    @Test
    fun `GIVEN composeExtensions enabled THEN generate standard extensions`() {
        val writer = PluralCatalogWriter(
            packageName = "com.example",
            composeExtensions = true,
        )
        val codegenDestination = folder.newFolder()
        val codegenFile = File(codegenDestination, "Plurals.kt")
        writer.write(resources, "main", codegenDestination = codegenDestination)
        assertThat(
            codegenFile.readBytes().toString(Charset.defaultCharset()),
        ).isEqualTo(
            """
            |@file:JvmName("PluralsMain")
            |@file:Suppress("NOTHING_TO_INLINE")
            |package com.example
            |
            |import androidx.compose.ui.ExperimentalComposeUiApi
            |import androidx.compose.ui.res.pluralStringResource
            |import androidx.compose.runtime.Composable
            |import androidx.compose.runtime.ReadOnlyComposable
            |import android.content.Context
            |import android.view.View
            |import androidx.fragment.app.Fragment
            |import com.flaviofaria.catalog.runtime.Plurals
            |
            |import com.example.R
            |
            |/**
            | * Plural 1 docs
            | */
            |inline val Plurals.plural1: Int
            |  get() = R.plurals.plural_1
            |
            |inline val Plurals.plural2: Int
            |  get() = R.plurals.plural_2
            |
            |inline val Plurals.plural3: Int
            |  get() = R.plurals.plural_3
            |
            |inline val Plurals.plural4: Int
            |  get() = R.plurals.plural_4
            |
            |/**
            | * Plural 1 docs
            | */
            |context(Context)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural1(quantity: Int, ): String {
            |  return pluralStringResource(R.plurals.plural_1, quantity)
            |}
            |
            |context(Context)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural2(quantity: Int, arg1: Int, arg2: Int, arg3: UInt, arg4: UInt, arg5: UInt): String {
            |  return pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural3(quantity: Int, arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: String): String {
            |  return pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Context)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural4(quantity: Int, arg1: Char): String {
            |  return pluralStringResource(R.plurals.plural_4, quantity, arg1, arg2)
            |}
            |
            |/**
            | * Plural 1 docs
            | */
            |context(Fragment)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural1(quantity: Int, ): String {
            |  return pluralStringResource(R.plurals.plural_1, quantity)
            |}
            |
            |context(Fragment)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural2(quantity: Int, arg1: Int, arg2: Int, arg3: UInt, arg4: UInt, arg5: UInt): String {
            |  return pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural3(quantity: Int, arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: String): String {
            |  return pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
            |}
            |
            |context(Fragment)
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable
            |fun Plurals.plural4(quantity: Int, arg1: Char): String {
            |  return pluralStringResource(R.plurals.plural_4, quantity, arg1, arg2)
            |}
            |""".trimMargin(),
        )
    }
}
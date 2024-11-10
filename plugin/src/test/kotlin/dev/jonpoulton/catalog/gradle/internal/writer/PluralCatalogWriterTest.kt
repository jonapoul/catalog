@file:Suppress("RedundantVisibilityModifier")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.google.common.truth.Truth.assertThat
import dev.jonpoulton.catalog.gradle.CatalogParameterNaming
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry.XmlItem.WithArgs
import dev.jonpoulton.catalog.gradle.internal.StringArg
import dev.jonpoulton.catalog.gradle.test.isEqualToKotlin
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.charset.Charset

class PluralCatalogWriterTest {
  @get:Rule
  var folder = TemporaryFolder()

  private lateinit var codegenDestination: File
  private lateinit var codegenFile: File

  private fun pluralCatalogWriter(
    packageName: String = "com.example",
    internal: Boolean = false,
    prefix: String = "",
    parameterNaming: CatalogParameterNaming = CatalogParameterNaming.Arg,
  ) = PluralCatalogWriter(config = GenerateResourcesTask.TaskConfig(packageName, internal, prefix, parameterNaming))

  private val resources = listOf(
    WithArgs.Plural(
      file = File("."),
      name = "plural_1",
      docs = "Plural 1 docs",
      args = emptyList(),
    ),
    WithArgs.Plural(
      file = File("."),
      name = "plural_2",
      docs = null,
      args = listOf(
        StringArg(position = 1, type = 'd'),
        StringArg(position = 2, type = 'i'),
        StringArg(position = 3, type = 'u'),
        StringArg(position = 4, type = 'x'),
        StringArg(position = 5, type = 'o'),
      ),
    ),
    WithArgs.Plural(
      file = File("."),
      name = "plural_3",
      docs = null,
      args = listOf(
        StringArg(position = 1, type = 'f'),
        StringArg(position = 2, type = 'e'),
        StringArg(position = 3, type = 'g'),
        StringArg(position = 4, type = 'a'),
        StringArg(position = 5, type = 's'),
      ),
    ),
    WithArgs.Plural(
      file = File("."),
      name = "plural_4",
      docs = null,
      args = listOf(
        StringArg(position = 1, type = 'c'),
      ),
    ),
  )

  @Before
  fun setUp() {
    codegenDestination = folder.newFolder()
    codegenFile = File("${codegenDestination.absolutePath}/com/example/Plurals.kt")
  }

  @Test
  fun `GIVEN defaults THEN generate public properties with no prefix`() {
    pluralCatalogWriter().write(resources, codegenDestination)
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualToKotlin(
      """
        // DO NOT EDIT: Auto-generated by Catalog. https://github.com/jonapoul/Catalog
        @file:Suppress("NOTHING_TO_INLINE")

        package com.example

        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.ReadOnlyComposable
        import androidx.compose.ui.ExperimentalComposeUiApi
        import androidx.compose.ui.res.pluralStringResource
        import kotlin.Char
        import kotlin.Double
        import kotlin.Int
        import kotlin.OptIn
        import kotlin.String
        import kotlin.Suppress
        import kotlin.UInt

        public object Plurals {
          /**
           * Plural 1 docs
           */
          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural1(quantity: Int): String = pluralStringResource(R.plurals.plural_1, quantity)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural2(
            quantity: Int,
            arg1: Int,
            arg2: Int,
            arg3: UInt,
            arg4: UInt,
            arg5: UInt,
          ): String = pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural3(
            quantity: Int,
            arg1: Double,
            arg2: Double,
            arg3: Double,
            arg4: Double,
            arg5: String,
          ): String = pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural4(quantity: Int, arg1: Char): String = pluralStringResource(R.plurals.plural_4, quantity, arg1)
        }

      """,
    )
  }

  @Test
  fun `GIVEN internal THEN generate internal properties with no prefix`() {
    pluralCatalogWriter(internal = true).write(resources, codegenDestination)
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualToKotlin(
      """
        // DO NOT EDIT: Auto-generated by Catalog. https://github.com/jonapoul/Catalog
        @file:Suppress("NOTHING_TO_INLINE")

        package com.example

        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.ReadOnlyComposable
        import androidx.compose.ui.ExperimentalComposeUiApi
        import androidx.compose.ui.res.pluralStringResource
        import kotlin.Char
        import kotlin.Double
        import kotlin.Int
        import kotlin.OptIn
        import kotlin.String
        import kotlin.Suppress
        import kotlin.UInt

        internal object Plurals {
          /**
           * Plural 1 docs
           */
          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          internal inline fun plural1(quantity: Int): String = pluralStringResource(R.plurals.plural_1, quantity)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          internal inline fun plural2(
            quantity: Int,
            arg1: Int,
            arg2: Int,
            arg3: UInt,
            arg4: UInt,
            arg5: UInt,
          ): String = pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          internal inline fun plural3(
            quantity: Int,
            arg1: Double,
            arg2: Double,
            arg3: Double,
            arg4: Double,
            arg5: String,
          ): String = pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          internal inline fun plural4(quantity: Int, arg1: Char): String = pluralStringResource(R.plurals.plural_4, quantity, arg1)
        }

      """,
    )
  }

  @Test
  fun `GIVEN prefix THEN generate public properties with prefix`() {
    pluralCatalogWriter(prefix = "Sample").write(resources, codegenDestination)
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualToKotlin(
      """
        // DO NOT EDIT: Auto-generated by Catalog. https://github.com/jonapoul/Catalog
        @file:Suppress("NOTHING_TO_INLINE")

        package com.example

        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.ReadOnlyComposable
        import androidx.compose.ui.ExperimentalComposeUiApi
        import androidx.compose.ui.res.pluralStringResource
        import kotlin.Char
        import kotlin.Double
        import kotlin.Int
        import kotlin.OptIn
        import kotlin.String
        import kotlin.Suppress
        import kotlin.UInt

        public object SamplePlurals {
          /**
           * Plural 1 docs
           */
          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural1(quantity: Int): String = pluralStringResource(R.plurals.plural_1, quantity)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural2(
            quantity: Int,
            arg1: Int,
            arg2: Int,
            arg3: UInt,
            arg4: UInt,
            arg5: UInt,
          ): String = pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural3(
            quantity: Int,
            arg1: Double,
            arg2: Double,
            arg3: Double,
            arg4: Double,
            arg5: String,
          ): String = pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural4(quantity: Int, arg1: Char): String = pluralStringResource(R.plurals.plural_4, quantity, arg1)
        }

      """,
    )
  }

  @Test
  fun `GIVEN type-named args THEN generate public properties with no prefix`() {
    pluralCatalogWriter(parameterNaming = CatalogParameterNaming.ByType)
      .write(resources, codegenDestination)
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualToKotlin(
      """
        // DO NOT EDIT: Auto-generated by Catalog. https://github.com/jonapoul/Catalog
        @file:Suppress("NOTHING_TO_INLINE")

        package com.example

        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.ReadOnlyComposable
        import androidx.compose.ui.ExperimentalComposeUiApi
        import androidx.compose.ui.res.pluralStringResource
        import kotlin.Char
        import kotlin.Double
        import kotlin.Int
        import kotlin.OptIn
        import kotlin.String
        import kotlin.Suppress
        import kotlin.UInt

        public object Plurals {
          /**
           * Plural 1 docs
           */
          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural1(quantity: Int): String = pluralStringResource(R.plurals.plural_1, quantity)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural2(
            quantity: Int,
            int1: Int,
            int2: Int,
            uint3: UInt,
            uint4: UInt,
            uint5: UInt,
          ): String = pluralStringResource(R.plurals.plural_2, quantity, int1, int2, uint3, uint4, uint5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural3(
            quantity: Int,
            double1: Double,
            double2: Double,
            double3: Double,
            double4: Double,
            string5: String,
          ): String = pluralStringResource(R.plurals.plural_3, quantity, double1, double2, double3, double4, string5)

          @OptIn(ExperimentalComposeUiApi::class)
          @Composable
          @ReadOnlyComposable
          public inline fun plural4(quantity: Int, char1: Char): String = pluralStringResource(R.plurals.plural_4, quantity, char1)
        }

      """,
    )
  }
}

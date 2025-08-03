package dev.jonpoulton.catalog.gradle.internal

import com.google.common.truth.Truth.assertThat
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry.XmlItem.WithArgs
import org.intellij.lang.annotations.Language
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import javax.xml.parsers.DocumentBuilderFactory

class ValueResourceParserTest {
  @get:Rule
  var folder = TemporaryFolder()

  private val parser = ValueResourceParser(DocumentBuilderFactory.newInstance().newDocumentBuilder())

  @Test
  fun `parseFile()`() {
    @Language("XML")
    val xmlContent = """
      |<resources>
      |   <string name="string_no_args_wo_docs">String no args w/o docs</string>
      |   <!-- Some test doc -->
      |   <string name="string_no_args_with_docs">String no args with docs</string>
      |   <!--
      |   Some test documentation:
      |       - Bullet 1
      |       - Bullet 2
      |   @since 20221231
      |   -->
      |   <string name="string_no_args_with_ktdocs">String no args w/o docs</string>
      |   <string name="string_with_non_positioned_args">String with %d non-positioned %d args</string>
      |   <string name="string_with_positioned_args">Args %3${'$'}d are %1${'$'}f out %4${'$'}s of %2${'$'}c order</string>
      |   <string name="unformatted_string" formatted="false">Some %1${'$'}f unformatted %2${'$'}s args %3${'$'}d</string>
      |   <string name="double_percent_symbol">Double %% symbol</string>
      |   <string name="escaped_percent_symbol">Escaped \% symbol</string>
      |   <integer name="integer">12345</integer>
      |   <integer name="negative_integer">-1</integer>
      |   <!-- Here's a comment -->
      |   <integer name="zero_with_comment">0</integer>
      |   <!-- there's no arg count validation, the only risk is going out of bounds -->
      |   <plurals name="some_plural">
      |       <item quantity="one">Single %1${'$'}d argument</item>
      |       <item quantity="other">Double %2${'$'}d arguments %1${'$'}d</item>
      |   </plurals>
      |   <string-array name="some_string_array">
      |       <item>Item 1</item>
      |       <item>Item 2</item>
      |       <item>Item 3</item>
      |   </string-array>
      |</resources>
    """.trimMargin()

    val file = folder.newFile()
    file.writeText(xmlContent)

    val resourceEntries = parser.parseFile(file)

    assertThat(resourceEntries).containsExactly(
      WithArgs.String(
        file = file,
        name = "string_no_args_wo_docs",
        docs = null,
        args = emptyList(),
      ),
      WithArgs.String(
        file = file,
        name = "string_no_args_with_docs",
        docs = "Some test doc",
        args = emptyList(),
      ),
      WithArgs.String(
        file = file,
        name = "string_no_args_with_ktdocs",
        docs = "Some test documentation:\n    - Bullet 1\n    - Bullet 2\n@since 20221231",
        args = emptyList(),
      ),
      WithArgs.String(
        file = file,
        name = "string_with_non_positioned_args",
        docs = null,
        args = listOf(
          StringArg(position = 1, type = 'd'),
          StringArg(position = 2, type = 'd'),
        ),
      ),
      WithArgs.String(
        file = file,
        name = "string_with_positioned_args",
        docs = null,
        args = listOf(
          StringArg(position = 3, type = 'd'),
          StringArg(position = 1, type = 'f'),
          StringArg(position = 4, type = 's'),
          StringArg(position = 2, type = 'c'),
        ),
      ),
      WithArgs.String(file = file, name = "unformatted_string", docs = null, args = emptyList()),
      WithArgs.String(file = file, name = "double_percent_symbol", docs = null, args = emptyList()),
      WithArgs.String(file = file, name = "escaped_percent_symbol", docs = null, args = emptyList()),
      ResourceEntry.XmlItem.Integer(file = file, name = "integer", docs = null),
      ResourceEntry.XmlItem.Integer(file = file, name = "negative_integer", docs = null),
      ResourceEntry.XmlItem.Integer(file = file, name = "zero_with_comment", docs = "Here's a comment"),
      WithArgs.Plural(
        file = file,
        name = "some_plural",
        docs = "there's no arg count validation, the only risk is going out of bounds",
        args = listOf(
          StringArg(position = 1, type = 'd'),
          StringArg(position = 2, type = 'd'),
        ),
      ),
      ResourceEntry.XmlItem.StringArray(file = file, name = "some_string_array", docs = null),
    )
  }
}

@file:Suppress("ComplexCondition", "NestedBlockDepth")

package dev.jonpoulton.catalog.gradle.internal

import org.w3c.dom.Comment
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.lang.Integer.max
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilder

internal class ValueResourceParser(private val docBuilder: DocumentBuilder) {
  // Original pattern from String.format() is %(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])
  // https://en.wikipedia.org/wiki/Printf_format_string#Type_field
  private val fsPattern =
    Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([diufFeEgGxXoscpaAn%])")

  fun parseFile(file: File): Set<ResourceEntry> {
    val doc = docBuilder.parse(file)
    val resourcesElements = doc.getElementsByTagName("resources").item(0)
    val resources = mutableSetOf<ResourceEntry>()
    var precedingComment: String? = null

    for (i in 0 until resourcesElements.childNodes.length) {
      val node = resourcesElements.childNodes.item(i)
      when (node.nodeType) {
        Node.ELEMENT_NODE -> {
          val element = node as Element
          val name = node.attributes.getNamedItem("name").nodeValue
          when (element.tagName) {
            "item" -> null
            "string" -> readString(node, name, file, precedingComment)
            "plurals" -> ResourceEntry.XmlItem.WithArgs.Plural(file, name, precedingComment, node.parsePlurals(name))
            "string-array" -> ResourceEntry.XmlItem.StringArray(file, name, precedingComment)
            "color" -> ResourceEntry.XmlItem.Color(file, name, precedingComment)
            "dimen" -> ResourceEntry.XmlItem.Dimen(file, name, precedingComment)
            "integer" -> ResourceEntry.XmlItem.Integer(file, name, precedingComment)
            "id" -> null
            else -> null
          }?.let {
            resources += it
          }
          precedingComment = null
        }

        Node.COMMENT_NODE -> {
          precedingComment = (node as Comment).data.trimIndent().trim()
        }
      }
    }
    return resources
  }

  private fun readString(
    node: Node,
    name: String,
    file: File,
    precedingComment: String?,
  ): ResourceEntry.XmlItem.WithArgs.String {
    val formatted = node.attributes.getNamedItem("formatted")?.nodeValue != "false"
    val args = if (formatted) node.textContent.extractArgs(name) else emptyList()
    return ResourceEntry.XmlItem.WithArgs.String(file, name, precedingComment, args)
  }

  private fun Element.parsePlurals(pluralName: String): List<StringArg> {
    val allArgs = mutableListOf<Map<Int, StringArg>>()
    var highestArgPosition = 0
    for (i in 0 until childNodes.length) {
      val child = childNodes.item(i)
      if (child.nodeType == Node.ELEMENT_NODE) {
        val childElement = child as Element
        if (childElement.tagName == "item") {
          val quantityArgs = childElement
            .textContent
            .extractArgs(pluralName)
            .associateBy { it.position }
          if (quantityArgs.isNotEmpty()) { // plurals with no argument
            highestArgPosition = max(
              highestArgPosition,
              quantityArgs.values.maxOf { it.position },
            )
          }
          allArgs += quantityArgs
        }
      }
    }
    val sharedArgs = mutableListOf<StringArg>()
    for (argPosition in 1..highestArgPosition) {
      var sharedArg: StringArg? = null
      allArgs.forEach { quantityArgs ->
        val arg = quantityArgs[argPosition]
        when {
          arg == null -> return@forEach
          sharedArg == null -> sharedArg = arg
          arg.type != requireNotNull(sharedArg).type ->
            error(
              "Inconsistent argument types in plural resource $pluralName. Make sure args with the" +
                " same index across all quantity entries have the same type.",
            )
        }
      }
      sharedArg?.let { sharedArgs += it }
    }
    return sharedArgs
  }

  @Suppress("LoopWithTooManyJumpStatements", "MagicNumber")
  private fun String.extractArgs(resourceName: String): List<StringArg> {
    val args = mutableMapOf<Int, StringArg>()
    val matcher = fsPattern.matcher(this)
    var implicitPosition = 0
    var hasPositionalArgs = false
    while (matcher.find()) {
      if (matcher.groupCount() == 1) {
        // literal %
        val content = matcher.group(0)
        require(content == "%") {
          // TODO improve error message for debugging
          "Unexpected string resource argument format: $content"
        }
        continue
      }
      val start = matcher.start()
      val end = matcher.end()
      if (start > 0 && this[start - 1] == '\\' || start < end && this[start + 1] == '%') {
        // ignores \% and %%
        continue
      }
      val type = matcher
        .group(6)
        .first()
        .lowercase()
        .first()
      val positionGroup = matcher.group(1)
      val arg = if (positionGroup != null) {
        require(positionGroup.endsWith("$")) {
          // TODO improve error message for debugging
          "Unexpected position placeholder: $positionGroup"
        }
        hasPositionalArgs = true
        val position = positionGroup.substring(0, positionGroup.lastIndex).toInt()
        position to StringArg(position, type)
      } else {
        val position = ++implicitPosition
        position to StringArg(position, type)
      }
      val existingArg = args[arg.first]
      require(existingArg == null || existingArg.type == arg.second.type) {
        "Argument #${arg.first} appears multiple times in $resourceName with different " +
          "types (%${existingArg?.type}, %${arg.second.type})"
      }
      args += arg
    }
    require(!hasPositionalArgs || implicitPosition == 0) {
      // TODO improve error message for debugging
      "Argument positions should be either all explicit or all implicit"
    }
    return args.values.toList()
  }
}

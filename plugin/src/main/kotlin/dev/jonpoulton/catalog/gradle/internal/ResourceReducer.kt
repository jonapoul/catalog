package dev.jonpoulton.catalog.gradle.internal

import java.io.File

internal class ResourceReducer {
  fun <T : ResourceEntry> reduce(resources: List<T>): T {
    val typedArgs = mutableMapOf<Int, Pair<StringArg, File>>()
    val resource = resources
      .asSequence()
      .onEach { it.validateArgumentTypes(typedArgs) }
      .maxByOrNull { resource ->
        when (resource) {
          is ResourceEntry.XmlItem.WithArgs -> resource.args.size
          else -> return resource // non-reducible resource type
        }
      }

    // they all have the same name, it doesn't matter
    resources.first().verifyIfNoArgHasBeenSkipped(typedArgs)
    return requireNotNull(resource)
  }

  private fun ResourceEntry.validateArgumentTypes(typedArgs: MutableMap<Int, Pair<StringArg, File>>) {
    val args = when (this) {
      is ResourceEntry.XmlItem.WithArgs.String -> args
      is ResourceEntry.XmlItem.WithArgs.Plural -> args
      else -> return // no need to validate other resource types
    }
    args.forEach { arg ->
      val knownArg = typedArgs[arg.position]
      if (knownArg == null) {
        typedArgs[arg.position] = arg to file
      } else {
        require(knownArg.first.type == arg.type) {
          """
            Inconsistent string argument type for [$name] at position [${arg.position}] in files
              ${knownArg.second}
              $file
          """.trimIndent()
        }
      }
    }
  }

  private fun ResourceEntry.verifyIfNoArgHasBeenSkipped(
    typedArgs: MutableMap<Int, Pair<StringArg, File>>,
  ) {
    val keys = typedArgs.keys
    if (keys.isNotEmpty()) {
      val maxKey = requireNotNull(keys.maxOrNull())
      val missingArgs = (1..maxKey)
        .mapNotNull { pos -> pos.takeIf { it !in typedArgs } }
        .toList()
      require(missingArgs.isEmpty()) {
        val formattedArgs = missingArgs.joinToString { "#$it" }
        val plural = if (missingArgs.size > 1) "s" else ""
        "Missing arg$plural $formattedArgs for resource $name"
      }
    }
  }
}

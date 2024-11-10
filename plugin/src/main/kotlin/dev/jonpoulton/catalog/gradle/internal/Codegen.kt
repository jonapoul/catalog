package dev.jonpoulton.catalog.gradle.internal

import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.writer.ColorCatalogWriter
import dev.jonpoulton.catalog.gradle.internal.writer.DimenCatalogWriter
import dev.jonpoulton.catalog.gradle.internal.writer.DrawableCatalogWriter
import dev.jonpoulton.catalog.gradle.internal.writer.IntegerCatalogWriter
import dev.jonpoulton.catalog.gradle.internal.writer.PluralCatalogWriter
import dev.jonpoulton.catalog.gradle.internal.writer.StringArrayCatalogWriter
import dev.jonpoulton.catalog.gradle.internal.writer.StringCatalogWriter
import java.io.File

internal class Codegen(
  private val valueResourceParser: ValueResourceParser,
  private val drawableResourceParser: DrawableResourceParser,
  config: GenerateResourcesTask.TaskConfig,
) {
  private val resourceReducer = ResourceReducer()

  private val resourceWriterRegistry = mapOf(
    ResourceEntry.XmlItem.WithArgs.String::class to StringCatalogWriter(config),
    ResourceEntry.XmlItem.WithArgs.Plural::class to PluralCatalogWriter(config),
    ResourceEntry.XmlItem.StringArray::class to StringArrayCatalogWriter(config),
    ResourceEntry.XmlItem.Color::class to ColorCatalogWriter(config),
    ResourceEntry.XmlItem.Dimen::class to DimenCatalogWriter(config),
    ResourceEntry.XmlItem.Integer::class to IntegerCatalogWriter(config),
    ResourceEntry.Drawable::class to DrawableCatalogWriter(config),
  )

  fun start(sourceSetDirs: Set<File>, outputDir: File) {
    val resourceEntries = sourceSetDirs
      .asSequence()
      .flatMap { it.walk() }
      .filterNot { it.isDirectory }
      .flatMap { it.toResourceEntries() }
      .distinctBy { it.name } // group by name to eliminate alternative resources
      .toList()

    outputDir.mkdirs()

    resourceEntries
      .groupBy { it::class } // groups by type
      .map { it.value }
      .flatMap { groupedByType -> groupedByType.groupBy { it.name }.map { it.value } } // groups by resource name
      .map(resourceReducer::reduce)
      .groupBy { it::class } // groups them back by type to write Kotlin files
      .forEach { (type, resources) ->
        @Suppress("UNCHECKED_CAST")
        resourceWriterRegistry[type]?.write(
          resources as List<Nothing>,
          outputDir,
        ) ?: error("Could not find resource writer for type $type")
      }
  }

  private fun File.toResourceEntries(): Iterable<ResourceEntry> = when {
    parentFile.name.startsWith("values") && lowercaseExtension == "xml" -> valueResourceParser.parseFile(this)
    parentFile.name.startsWith("drawable") -> toDrawableResourceEntry()?.let { listOf(it) }
    else -> null
  } ?: emptyList()

  private val File.lowercaseExtension: String
    get() = extension.lowercase()

  private val File.isValidBitmap: Boolean
    get() = parentFile.name.startsWith("drawable") && lowercaseExtension in VALID_BITMAP_EXTENSIONS

  private val File.is9PatchDrawable: Boolean
    get() = nameWithoutExtension.endsWith(".9") && lowercaseExtension == "png"

  private fun File.toDrawableResourceEntry(): ResourceEntry? = when {
    is9PatchDrawable -> ResourceEntry.Drawable(
      file = this,
      name = nameWithoutExtension.substringBeforeLast(".9"),
      type = ResourceEntry.Drawable.Type.NINE_PATCH,
    )

    isValidBitmap -> ResourceEntry.Drawable(
      file = this,
      name = nameWithoutExtension,
      type = ResourceEntry.Drawable.Type.BITMAP,
    )

    lowercaseExtension == "xml" -> drawableResourceParser.parseFile(this)
    else -> null
  }

  private companion object {
    val VALID_BITMAP_EXTENSIONS = setOf(
      "gif",
      "jpeg",
      "jpg",
      "png",
      "webp",
    )
  }
}

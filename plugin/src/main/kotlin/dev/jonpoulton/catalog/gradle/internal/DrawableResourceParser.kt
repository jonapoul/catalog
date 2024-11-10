package dev.jonpoulton.catalog.gradle.internal

import java.io.File
import javax.xml.parsers.DocumentBuilder

internal class DrawableResourceParser(private val docBuilder: DocumentBuilder) {
  @Suppress("CyclomaticComplexMethod")
  fun parseFile(file: File): ResourceEntry.Drawable {
    val doc = docBuilder.parse(file)
    return ResourceEntry.Drawable(
      file = file,
      name = file.nameWithoutExtension,
      type = when (doc.documentElement.tagName) {
        "animated-vector" -> ResourceEntry.Drawable.Type.ANIMATED_VECTOR
        "animation-list" -> ResourceEntry.Drawable.Type.ANIMATION_LIST
        "bitmap" -> ResourceEntry.Drawable.Type.BITMAP_REFERENCE
        "clip" -> ResourceEntry.Drawable.Type.CLIP
        "inset" -> ResourceEntry.Drawable.Type.INSET
        "layer-list" -> ResourceEntry.Drawable.Type.LAYER_LIST
        "level-list" -> ResourceEntry.Drawable.Type.LEVEL_LIST
        "nine-patch" -> ResourceEntry.Drawable.Type.NINE_PATCH_REFERENCE
        "scale" -> ResourceEntry.Drawable.Type.SCALE
        "selector" -> ResourceEntry.Drawable.Type.STATE_LIST
        "shape" -> ResourceEntry.Drawable.Type.SHAPE
        "transition" -> ResourceEntry.Drawable.Type.TRANSITION
        "vector" -> ResourceEntry.Drawable.Type.VECTOR
        else -> ResourceEntry.Drawable.Type.OTHER
      },
    )
  }
}

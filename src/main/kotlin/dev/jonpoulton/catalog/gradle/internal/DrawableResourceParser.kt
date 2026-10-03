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
      type =
        when (doc.documentElement.tagName) {
          "animated-vector" -> ANIMATED_VECTOR
          "animation-list" -> ANIMATION_LIST
          "bitmap" -> BITMAP_REFERENCE
          "clip" -> CLIP
          "inset" -> INSET
          "layer-list" -> LAYER_LIST
          "level-list" -> LEVEL_LIST
          "nine-patch" -> NINE_PATCH_REFERENCE
          "scale" -> SCALE
          "selector" -> STATE_LIST
          "shape" -> SHAPE
          "transition" -> TRANSITION
          "vector" -> VECTOR
          else -> OTHER
        },
    )
  }
}

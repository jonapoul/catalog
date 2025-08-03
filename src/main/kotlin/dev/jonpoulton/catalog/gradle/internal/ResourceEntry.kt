package dev.jonpoulton.catalog.gradle.internal

import java.io.File

internal sealed interface ResourceEntry {
  val file: File
  val name: String

  sealed interface XmlItem : ResourceEntry {
    val docs: String?

    sealed interface WithArgs : XmlItem {
      val args: List<StringArg>

      data class String(
        override val file: File,
        override val name: kotlin.String,
        override val docs: kotlin.String?,
        override val args: List<StringArg>,
      ) : WithArgs

      data class Plural(
        override val file: File,
        override val name: kotlin.String,
        override val docs: kotlin.String?,
        override val args: List<StringArg>,
      ) : WithArgs
    }

    data class StringArray(
      override val file: File,
      override val name: String,
      override val docs: String?,
    ) : XmlItem

    data class Color(
      override val file: File,
      override val name: String,
      override val docs: String?,
    ) : XmlItem

    data class Dimen(
      override val file: File,
      override val name: String,
      override val docs: String?,
    ) : XmlItem

    data class Integer(
      override val file: File,
      override val name: String,
      override val docs: String?,
    ) : XmlItem
  }

  data class Drawable(
    override val file: File,
    override val name: String,
    val type: Type,
  ) : ResourceEntry {
    enum class Type {
      ANIMATED_VECTOR,
      ANIMATION_LIST,
      CLIP,
      BITMAP,
      BITMAP_REFERENCE,
      INSET,
      LAYER_LIST,
      LEVEL_LIST,
      NINE_PATCH,
      NINE_PATCH_REFERENCE,
      SCALE,
      SHAPE,
      STATE_LIST,
      TRANSITION,
      VECTOR,
      OTHER,
    }
  }
}

data class StringArg(
  val position: Int,
  val type: Char,
)

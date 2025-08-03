package dev.jonpoulton.catalog.gradle.internal

internal enum class ResourceType(
  val receiverType: kotlin.String,
  val resourceGroup: kotlin.String,
) {
  String(
    receiverType = "Strings",
    resourceGroup = "string",
  ),
  Plural(
    receiverType = "Plurals",
    resourceGroup = "plurals",
  ),
  StringArray(
    receiverType = "StringArrays",
    resourceGroup = "array",
  ),
  Color(
    receiverType = "Colors",
    resourceGroup = "color",
  ),
  Dimen(
    receiverType = "Dimens",
    resourceGroup = "dimen",
  ),
  Drawable(
    receiverType = "Drawables",
    resourceGroup = "drawable",
  ),
  Integer(
    receiverType = "Integers",
    resourceGroup = "integer",
  ),
}

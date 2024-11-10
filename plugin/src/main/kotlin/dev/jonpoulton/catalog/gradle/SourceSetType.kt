package dev.jonpoulton.catalog.gradle

// Order based on https://developer.android.com/studio/write/add-resources#resource_merging
enum class SourceSetType {
  VARIANT,
  BUILD_TYPE,
  FLAVOR,
  MAIN,
}

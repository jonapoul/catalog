package dev.jonpoulton.catalog.gradle.internal

internal fun taskName(qualifier: String): String = "generate${qualifier.capitalize()}ResourceCatalog"

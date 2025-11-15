package dev.jonpoulton.catalog.gradle.internal

internal fun taskName(qualifier: String): String = "catalog${qualifier.capitalize()}"

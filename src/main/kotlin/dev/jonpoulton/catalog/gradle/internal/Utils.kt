package dev.jonpoulton.catalog.gradle.internal

import com.squareup.kotlinpoet.ClassName

internal fun rClass(packageName: String) = ClassName(packageName, "R")

package dev.jonpoulton.catalog.gradle.test

import com.google.common.truth.StringSubject
import org.intellij.lang.annotations.Language

internal fun StringSubject.isEqualToKotlin(
  @Language("kotlin") code: String,
) = isEqualTo(code.trimIndent())

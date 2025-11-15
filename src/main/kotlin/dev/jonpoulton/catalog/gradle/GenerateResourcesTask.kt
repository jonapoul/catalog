package dev.jonpoulton.catalog.gradle

import com.squareup.kotlinpoet.ClassName
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CacheableTask
public abstract class GenerateResourcesTask : SourceTask() {
  @get:OutputDirectory public abstract val outputDirectory: DirectoryProperty

  init {
    group = LifecycleBasePlugin.BUILD_GROUP
  }

  internal data class TaskConfig(
    val packageName: String,
    val generateInternal: Boolean,
    val typePrefix: String,
    val parameterNaming: CatalogParameterNaming,
    val nameTransform: NameTransform,
    val resClass: ClassName,
    val composableResourceAccessorPackage: String,
    val pluralAccessorIsExperimental: Boolean,
    val useReadOnlyComposable: Boolean,
  )
}

package dev.jonpoulton.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import dev.jonpoulton.catalog.gradle.internal.Codegen
import dev.jonpoulton.catalog.gradle.internal.DrawableResourceParser
import dev.jonpoulton.catalog.gradle.internal.ValueResourceParser
import dev.jonpoulton.catalog.gradle.internal.capitalize
import dev.jonpoulton.catalog.gradle.internal.readManifestPackageName
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@CacheableTask
abstract class GenerateResourcesTask : SourceTask() {
  @get:[Input Optional] abstract val packageName: Property<String>
  @get:[Input Optional] abstract val typePrefix: Property<String>
  @get:Input abstract val generateInternal: Property<Boolean>
  @get:Input abstract val parameterNaming: Property<CatalogParameterNaming>
  @get:Internal abstract val nameTransform: Property<NameTransform>
  @get:Internal abstract val sourceSetDirs: SetProperty<File>
  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun action() {
    val outputDirectory = outputDirectory.asFile.get()
    if (outputDirectory.exists()) outputDirectory.deleteRecursively()

    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    Codegen(
      valueResourceParser = ValueResourceParser(docBuilder),
      drawableResourceParser = DrawableResourceParser(docBuilder),
      config = TaskConfig(
        packageName = packageName.get(),
        generateInternal = generateInternal.get(),
        typePrefix = typePrefix.get(),
        parameterNaming = parameterNaming.get(),
        nameTransform = nameTransform.get(),
      ),
    ).start(sourceSetDirs.get(), outputDirectory)
  }

  data class TaskConfig(
    val packageName: String,
    val generateInternal: Boolean,
    val typePrefix: String,
    val parameterNaming: CatalogParameterNaming,
    val nameTransform: NameTransform,
  )

  companion object {
    fun taskName(qualifier: String): String = "generate${qualifier.capitalize()}ResourceCatalog"

    fun register(
      target: Project,
      taskName: String,
      catalogExtension: CatalogExtension,
      commonExtension: CommonExtension<*, *, *, *, *, *>,
      sourceSetDirs: Set<File>,
      sourceSetQualifier: SourceSetQualifier,
    ): TaskProvider<GenerateResourcesTask> {
      val name = sourceSetQualifier.name
      val packageName = catalogExtension.packageName.orNull
        ?: commonExtension.namespace
        ?: commonExtension.sourceSets.findByName(name)?.readManifestPackageName()
        ?: error("Missing package name in manifest file for source set $name")

      return target.tasks.register<GenerateResourcesTask>(taskName) {
        this.packageName.set(packageName)
        this.generateInternal.set(catalogExtension.generateInternal)
        this.typePrefix.set(catalogExtension.typePrefix.orNull.orEmpty())
        this.nameTransform.set(catalogExtension.nameTransform)
        this.parameterNaming.set(catalogExtension.parameterNaming)
        this.sourceSetDirs.set(sourceSetDirs)
        this.outputDirectory.set(
          target.projectDir.resolve("build/generated/kotlin/generate${name.capitalize()}Resources"),
        )
        for (dir in sourceSetDirs) source(dir)
      }
    }
  }
}

package dev.jonpoulton.catalog.gradle

import dev.jonpoulton.catalog.gradle.internal.Codegen
import dev.jonpoulton.catalog.gradle.internal.DrawableResourceParser
import dev.jonpoulton.catalog.gradle.internal.ValueResourceParser
import dev.jonpoulton.catalog.gradle.internal.capitalize
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@CacheableTask
abstract class GenerateResourcesTask : SourceTask() {
  @Nested
  lateinit var input: TaskInput

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  fun initialize(input: TaskInput) {
    this.input = input
    val outputDir = File(
      project.projectDir,
      "build/generated/kotlin/generate${input.sourceSetQualifier.name.capitalize()}Resources",
    )
    for (dir in input.sourceSetDirs) source(dir)
    outputDirectory.set(outputDir)
  }

  @TaskAction
  fun action() {
    val outputDirectory = outputDirectory.asFile.get()
    if (outputDirectory.exists()) outputDirectory.deleteRecursively()

    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    Codegen(
      valueResourceParser = ValueResourceParser(docBuilder),
      drawableResourceParser = DrawableResourceParser(docBuilder),
      config = TaskConfig(
        packageName = input.packageName,
        generateInternal = input.generateInternal,
        typePrefix = input.typePrefix,
        parameterNaming = input.parameterNaming,
        nameTransform = input.nameTransform,
      ),
    ).start(input.sourceSetDirs, outputDirectory)
  }

  data class TaskInput(
    @Input val packageName: String,
    @Input val generateInternal: Boolean,
    @Input val typePrefix: String,
    @Input val parameterNaming: CatalogParameterNaming,
    @Internal val nameTransform: NameTransform,
    @Internal val sourceSetDirs: Set<File>,
    @Internal val sourceSetQualifier: SourceSetQualifier,
  )

  data class TaskConfig(
    val packageName: String,
    val generateInternal: Boolean,
    val typePrefix: String,
    val parameterNaming: CatalogParameterNaming,
    val nameTransform: NameTransform,
  )
}

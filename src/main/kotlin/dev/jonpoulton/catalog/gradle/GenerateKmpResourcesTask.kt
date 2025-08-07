package dev.jonpoulton.catalog.gradle

import com.squareup.kotlinpoet.ClassName
import dev.jonpoulton.catalog.gradle.internal.Codegen
import dev.jonpoulton.catalog.gradle.internal.DrawableResourceParser
import dev.jonpoulton.catalog.gradle.internal.ValueResourceParser
import dev.jonpoulton.catalog.gradle.internal.capitalize
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.jetbrains.compose.resources.ResourcesExtension
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@CacheableTask
abstract class GenerateKmpResourcesTask : GenerateResourcesTask() {
  @get:Input abstract val resClassPackageName: Property<String>
  @get:Input abstract val resClassName: Property<String>

  @get:[Input Optional] abstract val packageName: Property<String>
  @get:[Input Optional] abstract val typePrefix: Property<String>
  @get:Input abstract val generateInternal: Property<Boolean>
  @get:Input abstract val parameterNaming: Property<CatalogParameterNaming>
  @get:Internal abstract val nameTransform: Property<NameTransform>
  @get:Internal abstract val sourceSetDirs: SetProperty<File>
  @get:OutputDirectory abstract override val outputDirectory: DirectoryProperty

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
        resClass = ClassName(resClassPackageName.get(), resClassName.get()),
        composableResourceAccessorPackage = "org.jetbrains.compose.resources",
        pluralAccessorIsExperimental = false,
        useReadOnlyComposable = false,
      ),
    ).start(sourceSetDirs.get(), outputDirectory)
  }

  companion object {
    fun register(
      target: Project,
      taskName: String,
      catalogExtension: CatalogExtension,
      resourcesExtension: ResourcesExtension,
      sourceSetDirs: Set<File>,
      sourceSetName: String,
      androidPackageName: Provider<String>,
    ): TaskProvider<GenerateKmpResourcesTask> = with(target) {
      tasks.register<GenerateKmpResourcesTask>(taskName) {
        this.packageName.set(catalogExtension.packageName.orElse(androidPackageName))
        this.resClassPackageName.set(resourcesExtension.packageOfResClass)
        this.resClassName.set(resourcesExtension.nameOfResClass)
        this.generateInternal.set(catalogExtension.generateInternal)
        this.typePrefix.set(catalogExtension.typePrefix.orNull.orEmpty())
        this.nameTransform.set(catalogExtension.nameTransform)
        this.parameterNaming.set(catalogExtension.parameterNaming)
        this.sourceSetDirs.set(sourceSetDirs)
        this.outputDirectory.set(
          layout.buildDirectory.dir("generated/kotlin/generate${sourceSetName.capitalize()}Resources"),
        )
        for (dir in sourceSetDirs) source(dir)
      }
    }
  }
}

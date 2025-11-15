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
import org.jetbrains.compose.resources.ResourcesExtension
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@CacheableTask
public abstract class GenerateKmpResourcesTask : GenerateResourcesTask() {
  @get:Input public abstract val resClassPackageName: Property<String>
  @get:Input public abstract val resClassName: Property<String>

  @get:[Input Optional] public abstract val packageName: Property<String>
  @get:[Input Optional] public abstract val typePrefix: Property<String>
  @get:Input public abstract val generateInternal: Property<Boolean>
  @get:Input public abstract val parameterNaming: Property<CatalogParameterNaming>
  @get:Internal public abstract val nameTransform: Property<NameTransform>
  @get:Internal public abstract val sourceSetDirs: SetProperty<File>
  @get:OutputDirectory public abstract override val outputDirectory: DirectoryProperty

  @TaskAction
  public fun action() {
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

  internal companion object {
    internal fun register(
      target: Project,
      taskName: String,
      catalogExtension: CatalogExtension,
      resourcesExtension: ResourcesExtension,
      sourceSetDirs: Set<File>,
      sourceSetName: String,
      androidPackageName: Provider<String>,
    ): TaskProvider<GenerateKmpResourcesTask> = with(target) {
      tasks.register(taskName, GenerateKmpResourcesTask::class.java) { task ->
        task.packageName.set(catalogExtension.packageName.orElse(androidPackageName))
        task.resClassPackageName.set(resourcesExtension.packageOfResClass)
        task.resClassName.set(resourcesExtension.nameOfResClass)
        task.generateInternal.set(catalogExtension.generateInternal)
        task.typePrefix.set(catalogExtension.typePrefix.orNull.orEmpty())
        task.nameTransform.set(catalogExtension.nameTransform)
        task.parameterNaming.set(catalogExtension.parameterNaming)
        task.sourceSetDirs.set(sourceSetDirs)
        task.outputDirectory.set(layout.buildDirectory.dir("generated/kotlin/catalog${sourceSetName.capitalize()}"))
        for (dir in sourceSetDirs) task.source(dir)
      }
    }
  }
}

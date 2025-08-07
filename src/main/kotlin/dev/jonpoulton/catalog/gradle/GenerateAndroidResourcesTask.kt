package dev.jonpoulton.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import dev.jonpoulton.catalog.gradle.internal.Codegen
import dev.jonpoulton.catalog.gradle.internal.DrawableResourceParser
import dev.jonpoulton.catalog.gradle.internal.ValueResourceParser
import dev.jonpoulton.catalog.gradle.internal.capitalize
import dev.jonpoulton.catalog.gradle.internal.rClass
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
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@CacheableTask
abstract class GenerateAndroidResourcesTask : GenerateResourcesTask() {
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
    val packageName = packageName.orNull.orEmpty()

    Codegen(
      valueResourceParser = ValueResourceParser(docBuilder),
      drawableResourceParser = DrawableResourceParser(docBuilder),
      config = TaskConfig(
        packageName = packageName,
        generateInternal = generateInternal.get(),
        typePrefix = typePrefix.get(),
        parameterNaming = parameterNaming.get(),
        nameTransform = nameTransform.get(),
        resClass = rClass(packageName),
        composableResourceAccessorPackage = "androidx.compose.ui.res",
        pluralAccessorIsExperimental = true,
        useReadOnlyComposable = true,
      ),
    ).start(sourceSetDirs.get(), outputDirectory)
  }

  companion object {
    fun register(
      target: Project,
      taskName: String,
      catalogExtension: CatalogExtension,
      commonExtension: CommonExtension<*, *, *, *, *, *>,
      sourceSetDirs: Set<File>,
      sourceSetName: String,
    ): TaskProvider<GenerateAndroidResourcesTask> = with(target) {
      tasks.register<GenerateAndroidResourcesTask>(taskName) {
        val packageName = catalogExtension.packageName.orNull
          ?: commonExtension.namespace
          ?: commonExtension.sourceSets.findByName(sourceSetName)?.readManifestPackageName()
          ?: error("Missing package name in manifest file for source set $sourceSetName")

        this.packageName.set(packageName)
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

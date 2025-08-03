@file:Suppress("UnstableApiUsage", "unused")

package dev.jonpoulton.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import java.io.File

class CatalogPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    val catalogExtension = extensions.create<CatalogExtension>("catalog")

    val androidComponents = extensions.getByType(AndroidComponentsExtension::class)
    androidComponents.finalizeDsl { commonExtension ->
      var sourceSetQualifier = SourceSetQualifier("main", SourceSetType.MAIN)
      val mainTaskProvider = getTaskProviderForSourceSet(
        catalogExtension = catalogExtension,
        commonExtension = commonExtension,
        sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(sourceSetName = "main"),
        sourceSetQualifier = sourceSetQualifier,
      )

      androidComponents.onVariants { variant ->
        sourceSetQualifier = SourceSetQualifier(variant.name, SourceSetType.VARIANT)
        val variantTaskProvider = getTaskProviderForSourceSet(
          catalogExtension = catalogExtension,
          commonExtension = commonExtension,
          sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(variant.name),
          sourceSetQualifier = sourceSetQualifier,
        )
        val buildTypeTaskProvider = variant.buildType?.let { buildType ->
          sourceSetQualifier = SourceSetQualifier(buildType, SourceSetType.BUILD_TYPE)
          getTaskProviderForSourceSet(
            catalogExtension = catalogExtension,
            commonExtension = commonExtension,
            sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(buildType),
            sourceSetQualifier = sourceSetQualifier,
          )
        }
        val flavorTaskProvider = variant
          .flavorName
          .takeUnless { it?.isEmpty() == true } // build variants come with a "" flavor
          ?.let { flavorName ->
            sourceSetQualifier = SourceSetQualifier(flavorName, SourceSetType.FLAVOR)
            getTaskProviderForSourceSet(
              catalogExtension = catalogExtension,
              commonExtension = commonExtension,
              sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(flavorName),
              sourceSetQualifier = sourceSetQualifier,
            )
          }
        variant.sources.java?.apply {
          addGeneratedSourceDirectory(mainTaskProvider, GenerateResourcesTask::outputDirectory)
          addGeneratedSourceDirectory(variantTaskProvider, GenerateResourcesTask::outputDirectory)
          buildTypeTaskProvider?.let { addGeneratedSourceDirectory(it, GenerateResourcesTask::outputDirectory) }
          flavorTaskProvider?.let { addGeneratedSourceDirectory(it, GenerateResourcesTask::outputDirectory) }
        }
      } // onVariants
    } // finalizeDsl

    afterEvaluate {
      // Add a wrapper task
      val taskName = GenerateResourcesTask.taskName(qualifier = "")
      tasks.register(taskName) {
        dependsOn(tasks.withType<GenerateResourcesTask>())
      }
    }
  }

  private fun Project.getTaskProviderForSourceSet(
    catalogExtension: CatalogExtension,
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    sourceSetDirs: Set<File>,
    sourceSetQualifier: SourceSetQualifier,
  ): TaskProvider<GenerateResourcesTask> {
    val taskName = GenerateResourcesTask.taskName(sourceSetQualifier.name)
    val provider = runCatching { tasks.named<GenerateResourcesTask>(taskName) }.getOrNull()
      ?: GenerateResourcesTask.register(
        target = this,
        taskName = taskName,
        catalogExtension = catalogExtension,
        commonExtension = commonExtension,
        sourceSetDirs = sourceSetDirs,
        sourceSetQualifier = sourceSetQualifier,
      )

    afterEvaluate {
      if (catalogExtension.generateAtSync.get() && isGradleSync) {
        tasks.maybeCreate("prepareKotlinIdeaImport").dependsOn(provider)
      }
    }

    return provider
  }

  /**
   * Gets all the /res folders for a given source set name. This should only get one item, unless another source set
   * has been added by another plugin or Gradle script.
   */
  private fun CommonExtension<*, *, *, *, *, *>.getQualifiedSourceSetsByName(sourceSetName: String): Set<File> =
    sourceSets.getByName(sourceSetName).res.let { res ->
      (res as DefaultAndroidSourceDirectorySet).srcDirs
    }

  private val isGradleSync: Boolean
    get() = System.getProperty("idea.sync.active") == "true"
}

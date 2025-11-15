@file:Suppress("UnstableApiUsage", "unused")

package dev.jonpoulton.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import dev.jonpoulton.catalog.gradle.internal.taskName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import java.io.File

public class CatalogPlugin : Plugin<Project> {
  override fun apply(project: Project): Unit = with(project) {
    val catalogExtension = extensions.create("catalog", CatalogExtension::class.java)

    with(pluginManager) {
      withPlugin("org.jetbrains.kotlin.android") {
        applyAndroid(catalogExtension)
      }

      withPlugin("org.jetbrains.kotlin.jvm") {
        error("JVM builds aren't supported yet!")
      }

      withPlugin("org.jetbrains.kotlin.multiplatform") {
        withPlugin("org.jetbrains.compose") {
          applyKmp(catalogExtension)
        }
      }
    }

    val catalogTasks = tasks.withType(GenerateResourcesTask::class.java)

    // Run before any kotlin compilation
    tasks.withType(AbstractKotlinCompile::class.java).configureEach { it.dependsOn(catalogTasks) }

    // Add a wrapper task
    val wrapper = tasks.register("catalog") { task ->
      task.group = LifecycleBasePlugin.BUILD_GROUP
      task.dependsOn(catalogTasks)
    }

    afterEvaluate {
      if (catalogExtension.generateAtSync.get() && isGradleSync) {
        tasks.maybeCreate("prepareKotlinIdeaImport").dependsOn(wrapper)
      }
    }
  }

  private fun Project.applyKmp(catalogExtension: CatalogExtension) {
    val resourcesExtension = extensions
      .getByType(ComposeExtension::class.java)
      .extensions
      .getByType(ResourcesExtension::class.java)

    extensions
      .getByType(KotlinMultiplatformExtension::class.java)
      .sourceSets
      .getByName("commonMain") { kotlinSourceSet ->
        val name = kotlinSourceSet.name
        val taskName = taskName(qualifier = name)

        val task = GenerateKmpResourcesTask.register(
          target = this,
          taskName = taskName,
          catalogExtension = catalogExtension,
          resourcesExtension = resourcesExtension,
          sourceSetDirs = setOf(file("src/$name/composeResources")),
          sourceSetName = name,
          androidPackageName = androidPackageNameOrNull(),
        )

        kotlinSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })
      }
  }

  private fun Project.applyAndroid(catalogExtension: CatalogExtension) {
    val androidComponents = extensions.getByType(AndroidComponentsExtension::class.java)
    androidComponents.finalizeDsl { ext ->
      @Suppress("UNCHECKED_CAST")
      val commonExtension = ext as CommonExtension<*, *, *, *, *, *>
      val mainTaskProvider = getTaskProviderForSourceSet(
        catalogExtension = catalogExtension,
        commonExtension = commonExtension,
        sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(sourceSetName = "main"),
        sourceSetName = "main",
      )

      androidComponents.onVariants { variant ->
        val variantTaskProvider = getTaskProviderForSourceSet(
          catalogExtension = catalogExtension,
          commonExtension = commonExtension,
          sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(variant.name),
          sourceSetName = variant.name,
        )
        val buildTypeTaskProvider = variant.buildType?.let { buildType ->
          getTaskProviderForSourceSet(
            catalogExtension = catalogExtension,
            commonExtension = commonExtension,
            sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(buildType),
            sourceSetName = buildType,
          )
        }
        val flavorTaskProvider = variant
          .flavorName
          .takeUnless { it?.isEmpty() == true } // build variants come with a "" flavor
          ?.let { flavorName ->
            getTaskProviderForSourceSet(
              catalogExtension = catalogExtension,
              commonExtension = commonExtension,
              sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(flavorName),
              sourceSetName = flavorName,
            )
          }
        variant.sources.kotlin?.apply {
          addGeneratedSourceDirectory(mainTaskProvider, GenerateResourcesTask::outputDirectory)
          addGeneratedSourceDirectory(variantTaskProvider, GenerateResourcesTask::outputDirectory)
          buildTypeTaskProvider?.let { addGeneratedSourceDirectory(it, GenerateResourcesTask::outputDirectory) }
          flavorTaskProvider?.let { addGeneratedSourceDirectory(it, GenerateResourcesTask::outputDirectory) }
        }
      } // onVariants
    } // finalizeDsl
  }

  private fun Project.getTaskProviderForSourceSet(
    catalogExtension: CatalogExtension,
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    sourceSetDirs: Set<File>,
    sourceSetName: String,
  ): TaskProvider<GenerateAndroidResourcesTask> {
    val taskName = taskName(sourceSetName)
    return runCatching { tasks.named(taskName, GenerateAndroidResourcesTask::class.java) }.getOrNull()
      ?: GenerateAndroidResourcesTask.register(
        target = this,
        taskName = taskName,
        catalogExtension = catalogExtension,
        commonExtension = commonExtension,
        sourceSetDirs = sourceSetDirs,
        sourceSetName = sourceSetName,
      )
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

  @Suppress("TooGenericExceptionCaught", "SwallowedException")
  private fun Project.androidPackageNameOrNull(): Provider<String> = try {
    val ext = extensions.getByType(CommonExtension::class.java)
    provider { ext.namespace }
  } catch (e: Exception) {
    provider { error("No android plugin was applied to $path - can't find package name!") }
  }
}

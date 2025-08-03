@file:Suppress("UnstableApiUsage")

package dev.jonpoulton.catalog.gradle

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import dev.jonpoulton.catalog.gradle.internal.capitalize
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class CatalogPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    val catalogExtension = extensions.create("catalog", CatalogExtension::class.java)

    val androidComponents = extensions.getByType(AndroidComponentsExtension::class.java)
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
  }

  private fun AndroidSourceSet.readManifestPackageName(): String? {
    val manifestFile = (manifest as DefaultAndroidSourceFile).srcFile
    return if (manifestFile.exists()) {
      val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
      val doc = docBuilder.parse(manifestFile)
      val manifestRoot = doc.getElementsByTagName("manifest").item(0)
      manifestRoot.attributes.getNamedItem("package")?.nodeValue
    } else {
      null
    }
  }

  private fun Project.getTaskProviderForSourceSet(
    catalogExtension: CatalogExtension,
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    sourceSetDirs: Set<File>,
    sourceSetQualifier: SourceSetQualifier,
  ): TaskProvider<GenerateResourcesTask> {
    val packageName = catalogExtension.packageName
      ?: commonExtension.namespace
      ?: commonExtension.sourceSets.findByName(sourceSetQualifier.name)?.readManifestPackageName()
      ?: error("Missing package name in manifest file for source set ${sourceSetQualifier.name}")

    val taskName = "generate${sourceSetQualifier.name.capitalize()}ResourceCatalog"
    val provider = runCatching { tasks.named(taskName, GenerateResourcesTask::class.java) }.getOrNull()
      ?: tasks.register(taskName, GenerateResourcesTask::class.java) { task ->
        task.initialize(
          GenerateResourcesTask.TaskInput(
            packageName = packageName,
            generateInternal = catalogExtension.generateInternal,
            typePrefix = catalogExtension.typePrefix.orEmpty(),
            nameTransform = catalogExtension.nameTransform,
            parameterNaming = catalogExtension.parameterNaming,
            sourceSetDirs = sourceSetDirs,
            sourceSetQualifier = sourceSetQualifier,
          ),
        )
      }

    if (catalogExtension.generateAtSync && isGradleSync) {
      afterEvaluate {
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

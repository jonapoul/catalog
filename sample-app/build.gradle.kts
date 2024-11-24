import blueprint.recipes.DetektAll
import blueprint.recipes.androidBaseBlueprint
import blueprint.recipes.androidResourcesBlueprint
import blueprint.recipes.detektBlueprint
import blueprint.recipes.kotlinJvmBlueprint
import blueprint.recipes.ktlintBlueprint
import blueprint.recipes.spotlessBlueprint
import dev.jonpoulton.catalog.gradle.NameTransform

plugins {
  alias(libs.plugins.agp.app)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("dev.jonpoulton.catalog")
}

catalog {
  packageName = null
  generateInternal = true
  typePrefix = "Sample"
  nameTransform = NameTransform.chained(
    NameTransform.removePrefix(prefix = "prefixed_"),
    NameTransform.removeSuffix(suffix = "_with_suffix"),
    NameTransform.CamelCase,
  )
}

kotlinJvmBlueprint(libs.versions.kotlin)
androidBaseBlueprint()
androidResourcesBlueprint(viewBinding = false)

detektBlueprint(detektAllConfig = DetektAll.Apply(ignoreRelease = false))
ktlintBlueprint(libs.versions.ktlint.cli)
spotlessBlueprint()

android {
  namespace = "dev.jonpoulton.catalog.sample"

  defaultConfig {
    applicationId = "dev.jonpoulton.catalog.sample"
    versionCode = 1
    versionName = "1.0"
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.animation.graphics)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.ui.core)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.activity.compose)
  implementation(libs.material)
}

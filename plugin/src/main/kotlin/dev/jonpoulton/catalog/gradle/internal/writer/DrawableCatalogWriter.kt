@file:Suppress("SpreadOperator")

package dev.jonpoulton.catalog.gradle.internal.writer

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dev.jonpoulton.catalog.gradle.GenerateResourcesTask
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry
import dev.jonpoulton.catalog.gradle.internal.ResourceEntry.Drawable.Type
import dev.jonpoulton.catalog.gradle.internal.ResourceType
import dev.jonpoulton.catalog.gradle.internal.toCamelCase

internal class DrawableCatalogWriter(
  override val config: GenerateResourcesTask.TaskConfig,
  override val resourceType: ResourceType = ResourceType.Drawable,
) : CatalogWriter<ResourceEntry.Drawable>() {
  private val optInClass = ClassName("kotlin", "OptIn")
  private val experimentalAnimationGraphicsApiClass =
    ClassName("androidx.compose.animation.graphics", "ExperimentalAnimationGraphicsApi")

  private val painterResourceMember = MemberName("androidx.compose.ui.res", "painterResource")

  private val animatedVectorResourceMember =
    MemberName("androidx.compose.animation.graphics.res", "animatedVectorResource")

  private val composePainterClass = ClassName("androidx.compose.ui.graphics.painter", "Painter")

  private val composeAnimatorVectorClass =
    ClassName("androidx.compose.animation.graphics.vector", "AnimatedImageVector")

  private val optInAnnotation = AnnotationSpec
    .builder(optInClass)
    .addMember("%T::class", experimentalAnimationGraphicsApiClass)
    .build()

  override fun TypeSpec.Builder.addResource(
    resource: ResourceEntry.Drawable,
  ): TypeSpec.Builder {
    val statementArgs: Array<Any>
    val statementFormat: String
    val returnType: TypeName
    when {
      resource.type == Type.ANIMATED_VECTOR -> {
        statementFormat = "return %T.%M(%T.drawable.%L)"
        statementArgs = arrayOf(composeAnimatorVectorClass, animatedVectorResourceMember, rClass, resource.name)
        returnType = composeAnimatorVectorClass
      }

      else -> {
        statementFormat = "return %M(%T.drawable.%L)"
        statementArgs = arrayOf(painterResourceMember, rClass, resource.name)
        returnType = composePainterClass
      }
    }

    val getter = FunSpec
      .getterBuilder()
      .addAnnotation(composableClass)
      .addModifiers(KModifier.INLINE)
      .addStatement(statementFormat, *statementArgs)
      .build()

    return addProperty(
      PropertySpec
        .builder(resource.name.toCamelCase(), returnType)
        .mutable(false)
        .addInternalIfConfigured()
        .apply { if (resource.type == Type.ANIMATED_VECTOR) addAnnotation(optInAnnotation) }
        .getter(getter)
        .build(),
    )
  }
}

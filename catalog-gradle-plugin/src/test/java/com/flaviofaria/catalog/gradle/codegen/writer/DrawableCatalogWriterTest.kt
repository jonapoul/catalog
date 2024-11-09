/*
 * Copyright (C) 2023 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.charset.Charset
import org.gradle.api.internal.changedetection.state.ResourceEntryFilter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DrawableCatalogWriterTest {

  @get:Rule
  var folder = TemporaryFolder()

  private lateinit var codegenDestination: File

  private val resources = listOf(
    ResourceEntry.Drawable(
      file = File("."),
      name = "drawable_1",
      type = ResourceEntry.Drawable.Type.BITMAP,
    ),
    ResourceEntry.Drawable(
      file = File("."),
      name = "drawable_2",
      type = ResourceEntry.Drawable.Type.ANIMATED_VECTOR,
    ),
  )

  private lateinit var codegenFile: File
  private val writer = DrawableCatalogWriter(
    packageName = "com.example",
    generateComposeAnimatedVectorExtensions = true,
  )

  @Before
  fun setUp() {
    codegenDestination = folder.newFolder()
    codegenFile = File("${codegenDestination.absolutePath}/com/example/Drawables.kt")
  }

  @Test
  fun `GIVEN generateResourcesExtensions and generateComposeExtensions disabled THEN generate property extensions only`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourceProperties = true,
      generateResourcesExtensions = false,
      generateComposeExtensions = false,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("DrawablesMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import androidx.`annotation`.DrawableRes
      |import com.flaviofaria.catalog.runtime.resources.Drawables
      |import kotlin.Int
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable1: Int
      |  get() = R.drawable.drawable_1
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable2: Int
      |  get() = R.drawable.drawable_2
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateResourcesExtensions enabled and generateComposeExtensions disabled THEN generate property and resources extensions only`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourceProperties = true,
      generateResourcesExtensions = true,
      generateComposeExtensions = false,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("DrawablesMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import android.graphics.drawable.Drawable
      |import androidx.`annotation`.DrawableRes
      |import androidx.core.content.ContextCompat
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.resources.Drawables
      |import kotlin.Int
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable1: Int
      |  get() = R.drawable.drawable_1
      |
      |context(Context)
      |public inline fun Drawables.drawable1(): Drawable = ContextCompat.getDrawable(this@Context,
      |    R.drawable.drawable_1)!!
      |
      |context(Fragment)
      |public inline fun Drawables.drawable1(): Drawable = ContextCompat.getDrawable(requireContext(),
      |    R.drawable.drawable_1)!!
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable2: Int
      |  get() = R.drawable.drawable_2
      |
      |context(Context)
      |public inline fun Drawables.drawable2(): Drawable = ContextCompat.getDrawable(this@Context,
      |    R.drawable.drawable_2)!!
      |
      |context(Fragment)
      |public inline fun Drawables.drawable2(): Drawable = ContextCompat.getDrawable(requireContext(),
      |    R.drawable.drawable_2)!!
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateResourcesExtensions disabled and generateComposeExtensions enabled THEN generate property and compose extensions only`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourceProperties = true,
      generateResourcesExtensions = false,
      generateComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("DrawablesMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import androidx.`annotation`.DrawableRes
      |import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
      |import androidx.compose.animation.graphics.res.animatedVectorResource
      |import androidx.compose.animation.graphics.vector.AnimatedImageVector
      |import androidx.compose.runtime.Composable
      |import androidx.compose.ui.graphics.painter.Painter
      |import androidx.compose.ui.res.painterResource
      |import com.flaviofaria.catalog.runtime.compose.Drawables
      |import kotlin.Int
      |import kotlin.OptIn
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable1: Int
      |  get() = R.drawable.drawable_1
      |
      |@Composable
      |public inline fun Drawables.drawable1(): Painter = painterResource(R.drawable.drawable_1)
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable2: Int
      |  get() = R.drawable.drawable_2
      |
      |@OptIn(ExperimentalAnimationGraphicsApi::class)
      |@Composable
      |public inline fun Drawables.drawable2(): AnimatedImageVector =
      |    AnimatedImageVector.animatedVectorResource(R.drawable.drawable_2)
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateComposeExtensions enabled and generateComposeAnimatedVectorExtensions disabled THEN skip animated vector extension methods`() {
    val writer = DrawableCatalogWriter(
      packageName = "com.example",
      generateComposeAnimatedVectorExtensions = false,
    )
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourceProperties = true,
      generateResourcesExtensions = false,
      generateComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("DrawablesMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import androidx.`annotation`.DrawableRes
      |import androidx.compose.runtime.Composable
      |import androidx.compose.ui.graphics.painter.Painter
      |import androidx.compose.ui.res.painterResource
      |import com.flaviofaria.catalog.runtime.compose.Drawables
      |import kotlin.Int
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable1: Int
      |  get() = R.drawable.drawable_1
      |
      |@Composable
      |public inline fun Drawables.drawable1(): Painter = painterResource(R.drawable.drawable_1)
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable2: Int
      |  get() = R.drawable.drawable_2
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateComposeExtensions enabled THEN skip non-supported resource types`() {
    val resources = listOf(
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_1",
        type = ResourceEntry.Drawable.Type.ANIMATED_VECTOR,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_2",
        type = ResourceEntry.Drawable.Type.ANIMATION_LIST,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_3",
        type = ResourceEntry.Drawable.Type.CLIP,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_4",
        type = ResourceEntry.Drawable.Type.BITMAP,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_5",
        type = ResourceEntry.Drawable.Type.BITMAP_REFERENCE,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_6",
        type = ResourceEntry.Drawable.Type.INSET,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_7",
        type = ResourceEntry.Drawable.Type.LAYER_LIST,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_8",
        type = ResourceEntry.Drawable.Type.LEVEL_LIST,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_9",
        type = ResourceEntry.Drawable.Type.NINE_PATCH,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_10",
        type = ResourceEntry.Drawable.Type.NINE_PATCH_REFERENCE,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_11",
        type = ResourceEntry.Drawable.Type.SCALE,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_12",
        type = ResourceEntry.Drawable.Type.SHAPE,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_13",
        type = ResourceEntry.Drawable.Type.STATE_LIST,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_14",
        type = ResourceEntry.Drawable.Type.TRANSITION,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_15",
        type = ResourceEntry.Drawable.Type.VECTOR,
      ),
      ResourceEntry.Drawable(
        file = File("."),
        name = "drawable_16",
        type = ResourceEntry.Drawable.Type.OTHER,
      ),
    )
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourceProperties = true,
      generateResourcesExtensions = false,
      generateComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("DrawablesMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import androidx.`annotation`.DrawableRes
      |import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
      |import androidx.compose.animation.graphics.res.animatedVectorResource
      |import androidx.compose.animation.graphics.vector.AnimatedImageVector
      |import androidx.compose.runtime.Composable
      |import androidx.compose.ui.graphics.painter.Painter
      |import androidx.compose.ui.res.painterResource
      |import com.flaviofaria.catalog.runtime.compose.Drawables
      |import kotlin.Int
      |import kotlin.OptIn
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable1: Int
      |  get() = R.drawable.drawable_1
      |
      |@OptIn(ExperimentalAnimationGraphicsApi::class)
      |@Composable
      |public inline fun Drawables.drawable1(): AnimatedImageVector =
      |    AnimatedImageVector.animatedVectorResource(R.drawable.drawable_1)
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable2: Int
      |  get() = R.drawable.drawable_2
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable3: Int
      |  get() = R.drawable.drawable_3
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable4: Int
      |  get() = R.drawable.drawable_4
      |
      |@Composable
      |public inline fun Drawables.drawable4(): Painter = painterResource(R.drawable.drawable_4)
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable5: Int
      |  get() = R.drawable.drawable_5
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable6: Int
      |  get() = R.drawable.drawable_6
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable7: Int
      |  get() = R.drawable.drawable_7
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable8: Int
      |  get() = R.drawable.drawable_8
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable9: Int
      |  get() = R.drawable.drawable_9
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable10: Int
      |  get() = R.drawable.drawable_10
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable11: Int
      |  get() = R.drawable.drawable_11
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable12: Int
      |  get() = R.drawable.drawable_12
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable13: Int
      |  get() = R.drawable.drawable_13
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable14: Int
      |  get() = R.drawable.drawable_14
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable15: Int
      |  get() = R.drawable.drawable_15
      |
      |@Composable
      |public inline fun Drawables.drawable15(): Painter = painterResource(R.drawable.drawable_15)
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable16: Int
      |  get() = R.drawable.drawable_16
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateResources and generateComposeExtensions enabled THEN generate property, resources and compose extensions`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourceProperties = true,
      generateResourcesExtensions = true,
      generateComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("DrawablesMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import android.graphics.drawable.Drawable
      |import androidx.`annotation`.DrawableRes
      |import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
      |import androidx.compose.animation.graphics.res.animatedVectorResource
      |import androidx.compose.animation.graphics.vector.AnimatedImageVector
      |import androidx.compose.runtime.Composable
      |import androidx.compose.ui.graphics.painter.Painter
      |import androidx.compose.ui.res.painterResource
      |import androidx.core.content.ContextCompat
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.compose.Drawables
      |import kotlin.Int
      |import kotlin.OptIn
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable1: Int
      |  get() = R.drawable.drawable_1
      |
      |context(Context)
      |public inline fun com.flaviofaria.catalog.runtime.resources.Drawables.drawable1(): Drawable =
      |    ContextCompat.getDrawable(this@Context, R.drawable.drawable_1)!!
      |
      |context(Fragment)
      |public inline fun com.flaviofaria.catalog.runtime.resources.Drawables.drawable1(): Drawable =
      |    ContextCompat.getDrawable(requireContext(), R.drawable.drawable_1)!!
      |
      |@Composable
      |public inline fun Drawables.drawable1(): Painter = painterResource(R.drawable.drawable_1)
      |
      |@get:DrawableRes
      |public inline val Drawables.drawable2: Int
      |  get() = R.drawable.drawable_2
      |
      |context(Context)
      |public inline fun com.flaviofaria.catalog.runtime.resources.Drawables.drawable2(): Drawable =
      |    ContextCompat.getDrawable(this@Context, R.drawable.drawable_2)!!
      |
      |context(Fragment)
      |public inline fun com.flaviofaria.catalog.runtime.resources.Drawables.drawable2(): Drawable =
      |    ContextCompat.getDrawable(requireContext(), R.drawable.drawable_2)!!
      |
      |@OptIn(ExperimentalAnimationGraphicsApi::class)
      |@Composable
      |public inline fun Drawables.drawable2(): AnimatedImageVector =
      |    AnimatedImageVector.animatedVectorResource(R.drawable.drawable_2)
      |""".trimMargin(),
    )
  }
}

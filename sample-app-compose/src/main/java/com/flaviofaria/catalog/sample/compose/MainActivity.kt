/*
 * Copyright (C) 2022 Flavio Faria
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
package com.flaviofaria.catalog.sample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.flaviofaria.catalog.library.libraryName
import com.flaviofaria.catalog.library.libraryStringArray
import com.flaviofaria.catalog.runtime.compose.Colors
import com.flaviofaria.catalog.runtime.compose.Dimens
import com.flaviofaria.catalog.runtime.compose.Drawables
import com.flaviofaria.catalog.runtime.compose.Integers
import com.flaviofaria.catalog.runtime.compose.Plurals
import com.flaviofaria.catalog.runtime.compose.StringArrays
import com.flaviofaria.catalog.runtime.compose.Strings

class MainActivity : ComponentActivity() {

  @OptIn(ExperimentalAnimationGraphicsApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Strings.appName()
      StringArrays.someStringArray()
      Strings.libraryName()
      StringArrays.libraryStringArray()
      Plurals.somePlural(3, 1, 2)
      Colors.black()
      Dimens.margin()
      Integers.numberOfWidgets()
      Column {
        Text(text = Strings.composable().toString())
        Icon(painter = Drawables.icLauncherForeground(), contentDescription = "")
        var atEnd by remember { mutableStateOf(false) }
        IconButton(onClick = { atEnd = !atEnd }) {
          Icon(
            painter = rememberAnimatedVectorPainter(Drawables.animatedVector(), atEnd),
            contentDescription = null,
          )
        }
      }
    }
  }
}

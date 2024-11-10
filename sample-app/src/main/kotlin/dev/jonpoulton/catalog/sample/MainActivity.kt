package dev.jonpoulton.catalog.sample

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

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalAnimationGraphicsApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleStrings.appName
      SampleStringArrays.someStringArray
      SamplePlurals.somePlural(quantity = 3, int1 = 1, int2 = 2)
      SampleColors.black
      SampleDimens.margin
      SampleIntegers.numberOfWidgets
      Column {
        Text(text = SampleStrings.composable)
        Icon(painter = SampleDrawables.icLauncherForeground, contentDescription = "")
        var atEnd by remember { mutableStateOf(false) }
        IconButton(onClick = { atEnd = !atEnd }) {
          Icon(
            painter = rememberAnimatedVectorPainter(SampleDrawables.animatedVector, atEnd),
            contentDescription = null,
          )
        }
      }
    }
  }
}

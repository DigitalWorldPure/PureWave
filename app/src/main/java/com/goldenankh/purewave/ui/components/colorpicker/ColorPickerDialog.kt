/*
The MIT License

Copyright (c) 2026 DigitalWorldPure

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.goldenankh.purewave.ui.components.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import android.graphics.Color as AndroidColor
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.goldenankh.purewave.R

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val hsv = rememberSaveable(initialColor) {
        FloatArray(3).also {
            AndroidColor.colorToHSV(initialColor.toArgb(), it)
        }
    }

    var hue by rememberSaveable { mutableFloatStateOf(hsv[0]) }
    var saturation by rememberSaveable { mutableFloatStateOf(hsv[1]) }
    var value by rememberSaveable { mutableFloatStateOf(hsv[2]) }

    val selectedColor = Color.hsv(
        hue = hue,
        saturation = saturation,
        value = value
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_color)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SaturationValuePicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            if(windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
                                60.dp
                            }
                            else {
                                220.dp
                            }
                        ),
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onChanged = { s, v ->
                        saturation = s
                        value = v
                    }
                )

                HuePicker(
                    hue = hue,
                    onChanged = { hue = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(selectedColor)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_action))
            }
        }
    )
}

@Composable
private fun SaturationValuePicker(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    value: Float,
    onChanged: (Float, Float) -> Unit
) {
    Canvas(
        modifier = modifier
            .pointerInput(hue) {
                detectDragGestures(
                    onDragStart = { position ->
                        updateSV(position, size.width.toFloat(), size.height.toFloat(), onChanged)
                    },
                    onDrag = { change, _ ->
                        updateSV(change.position, size.width.toFloat(), size.height.toFloat(), onChanged)
                    }
                )
            }
    ) {
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(Color.White, Color.hsv(hue, 1f, 1f))
            )
        )
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black)
            )
        )

        val x = saturation * size.width
        val y = (1f - value) * size.height
        drawCircle(
            color = Color.White,
            radius = 10.dp.toPx(),
            center = Offset(x, y),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun updateSV(
    position: Offset,
    width: Float,
    height: Float,
    onChanged: (Float, Float) -> Unit
) {
    val s = (position.x / width).coerceIn(0f, 1f)
    val v = (1f - position.y / height).coerceIn(0f, 1f)
    onChanged(s, v)
}

@Composable
private fun HuePicker(
    hue: Float,
    onChanged: (Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { position ->
                        onChanged((position.x / size.width * 360f).coerceIn(0f, 360f))
                    },
                    onDrag = { change, _ ->
                        onChanged((change.position.x / size.width * 360f).coerceIn(0f, 360f))
                    }
                )
            }
    ) {
        val colors = (0..6).map {
            Color.hsv(it * 60f, 1f, 1f)
        }

        drawRect(
            brush = Brush.horizontalGradient(colors)
        )

        val x = hue / 360f * size.width
        drawLine(
            color = Color.Black,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 3.dp.toPx()
        )
    }
}

private fun Color.toArgb(): Int =
    AndroidColor.argb(
        (alpha * 255f).toInt().coerceIn(0, 255),
        (red * 255f).toInt().coerceIn(0, 255),
        (green * 255f).toInt().coerceIn(0, 255),
        (blue * 255f).toInt().coerceIn(0, 255)
    )

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun ColorPickerDialogPreview() {
    ColorPickerDialog(
        initialColor = Color.Red,
        onDismiss = {},
        onColorSelected = { _ -> },
        WindowSizeClass.calculateFromSize(DpSize(320.dp, 500.dp))
    )
}
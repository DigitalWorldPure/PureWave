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

package com.goldenankh.purewave.ui.components.curves.curveviewer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CurveViewer(
    modifier: Modifier = Modifier,
    onEditCurve: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable {
                onEditCurve()
            }
            .border(
                width = 1.dp,
                color = Color.Gray
            )
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val path = Path()

            path.moveTo(
                16.dp.toPx(),
                size.height * 0.75f
            )

            path.cubicTo(
                size.width * .30f,
                size.height * .65f,
                size.width * .55f,
                size.height * .55f,
                size.width * .70f,
                size.height * .30f
            )

            path.cubicTo(
                size.width * .80f,
                size.height * .12f,
                size.width * .90f,
                size.height * .25f,
                size.width - 16.dp.toPx(),
                size.height * .40f
            )

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = 2.dp.toPx()
                )
            )
        }
    }
}

@Preview
@Composable
private fun CurveViewerPreview() {
    CurveViewer(onEditCurve = {})
}
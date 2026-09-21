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

package com.goldenankh.purewave.ui.components.tracklist.playbackindicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackIndicator(
    contentHeight: Int,
    playbackPositionPx: Float,
    scrollX: Float,
    scrollY: Float,
    timeScaleHeight: Dp,
    indicatorColor: Color,
    dotColor: Color,
    modifier: Modifier = Modifier,
) {
    val density =
        LocalDensity.current

    val lineWidth =
        with(density) {
            2.dp.toPx()
        }

    val dotSize =
        with(density) {
            6.dp.toPx()
        }

    val scaleHeight =
        with(density) {
            timeScaleHeight.toPx()
        }

    val x =
        playbackPositionPx -
                scrollX

    val dotCenterY =
        scaleHeight -
                dotSize / 2f

    val lineTop =
        dotCenterY +
                dotSize / 2f

    Canvas(
        modifier.fillMaxSize()
    ) {

        drawLine(
            indicatorColor,
            Offset(
                x,
                lineTop
            ),
            Offset(
                x,
                (
                        scaleHeight +
                                contentHeight -
                                scrollY
                        )
                    .coerceAtLeast(
                        scaleHeight
                    )
            ),
            lineWidth
        )

        drawCircle(
            dotColor,
            dotSize / 2f,
            Offset(
                x,
                dotCenterY
            )
        )
    }
}

@Preview
@Composable
private fun PlaybackIndicatorPreview() {
    PlaybackIndicator(
        720,
        100f,
        0f,
        0f,
        56.dp,
        Color(0xFF1976D2),
        Color(0xFFE53935),
    )
}
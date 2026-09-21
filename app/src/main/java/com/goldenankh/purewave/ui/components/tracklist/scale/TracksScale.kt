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

package com.goldenankh.purewave.ui.components.tracklist.scale

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Scale in seconds
 *
 * @param contentWidth Content width for displaying divisions
 * @param contentHeight Content height for displaying divisions
 * @param scrollX How far the list is scrolled along the X-axis
 * @param dpPerSecond How much DP per second?
 * @param timeDivisionSeconds How many seconds are in one scale division?
 * @param trackWidth Header element width
 *
 */
fun DrawScope.drawTracksScale(
    contentWidth: Int,
    contentHeight: Int,
    scrollX: Float,
    dpPerSecond: Dp,
    timeDivisionSeconds: Int,
    trackWidth: Float
) {
    if (contentWidth <= 0) {
        return
    }

    clipRect(
        left = trackWidth,
        top = 0f,
        right = size.width,
        bottom = size.height
    ) {

        val dpPerSecondPx = (dpPerSecond.value * density)
                .coerceAtLeast(1f)

        val majorStep = timeDivisionSeconds
                .coerceAtLeast(1)
        val minorStep = (majorStep / 5)
                .coerceAtLeast(1)

        val labelBaselinePx = 18.dp.value * density

        val majorStrokeWidth = 1.5.dp.value * density
        val minorStrokeWidth = 1.dp.value * density
        val minorTopPadding = 8.dp.value * density

        val textSize = 12.dp.value * density

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color(0xFF555555).toArgb()
                this.textSize = textSize
                textAlign = Paint.Align.CENTER
            }

        /*
         * The scale is located in content coordinates
         *
         * So scrollX is simply subtracted from X
         */
        val lastSecond = (contentWidth / dpPerSecondPx)
                .coerceAtLeast(0f)
                .toInt()

        for (second in 0..lastSecond) {

            val contentX = trackWidth + second * dpPerSecondPx

            val x = contentX - scrollX

            /*
             * Completely outside the viewport
             */
            if (x > size.width) {
                break
            }

            if (x < 0f) {
                continue
            }

            when {
                second % majorStep == 0 -> {

                    drawLine(
                        color = Color(0xFF777777),
                        start = Offset(
                            x,
                            30.dp.value * density
                        ),
                        end = Offset(
                            x,
                            contentHeight.toFloat()
                        ),
                        strokeWidth = majorStrokeWidth
                    )

                    drawContext
                        .canvas
                        .nativeCanvas
                        .drawText(
                            second.toString(),
                            x,
                            labelBaselinePx,
                            paint
                        )
                }

                second % minorStep == 0 -> {

                    drawLine(
                        color = Color(0xFFAAAAAA),
                        start = Offset(
                            x,
                            30.dp.value * density + minorTopPadding
                        ),
                        end = Offset(x, contentHeight.toFloat()),
                        strokeWidth = minorStrokeWidth
                    )
                }
            }
        }
    }
}
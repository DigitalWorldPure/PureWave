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

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The area above for moving the playback indicator to a specific position
 * by clicking
 *
 * @param timeScaleHeight Height of the scale
 * @param scrollX How far the list is scrolled along the X-axis
 * @param onTap Called when tapping the hit area
 *
 */
@Composable
fun PlaybackIndicatorScaleHitArea(
    timeScaleHeight: Dp,
    scrollX: Float,
    modifier: Modifier = Modifier,
    onTap: (offset: Offset) -> Unit
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .height(timeScaleHeight)
                .pointerInput(
                    scrollX
                ) {

                    detectTapGestures(
                        onTap = { offset ->
                            onTap.invoke(offset)
                        }
                    )
                }
    )
}

@Preview
@Composable
private fun PlaybackIndicatorScaleHitAreaPreview() {
    PlaybackIndicatorScaleHitArea(
        timeScaleHeight = 56.dp,
        scrollX = 200f,
        onTap = {}
    )
}
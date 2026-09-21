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

package com.goldenankh.purewave.ui.components.tracklist.dividers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.TracksData
import com.goldenankh.purewave.ui.components.tracklist.lazy.LazyTracksState

fun DrawScope.drawTrackDividers(
    tracksData: TracksData,
    separatorColor: Color,
    strokeWidth: Dp,
    state: LazyTracksState
) {
    if (tracksData.tracks.isEmpty()) {
        return
    }

    val stroke = strokeWidth.toPx()
    val firstTrack = tracksData.tracks.first()

    // Top divider
    tracksData.positions[firstTrack.id]?.let {
        val firstY = it.top.dp.toPx() - state.scrollY
        drawLine(
            color = separatorColor,
            start = Offset(0f, firstY),
            end = Offset(size.width, firstY),
            strokeWidth = stroke
        )
    }

    // Intermediate dividers
    tracksData.tracks.forEach { track ->

        tracksData.positions[track.id]?.let {
            val y = it.bottom.dp.toPx() - state.scrollY
            drawLine(
                color = separatorColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = stroke
            )
        }
    }

    // Bottom divider
    val lastTrack = tracksData.tracks.last()
    tracksData.positions[lastTrack.id]?.let {
        val lastY =  it.bottom.dp.toPx() - state.scrollY
        drawLine(
            color = separatorColor,
            start = Offset(0f, lastY),
            end = Offset(size.width, lastY),
            strokeWidth = stroke
        )
    }
}
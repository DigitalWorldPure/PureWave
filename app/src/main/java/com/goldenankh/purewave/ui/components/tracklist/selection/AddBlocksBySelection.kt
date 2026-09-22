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

package com.goldenankh.purewave.ui.components.tracklist.selection

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.AddSelection
import com.goldenankh.domain.model.TracksData
import com.goldenankh.purewave.ui.components.tracklist.lazy.scrollstate.LazyTracksState

fun DrawScope.drawSelectionArea(
    addSelection: AddSelection?,
    state: LazyTracksState,
    tracksData: TracksData,
    rowHeight: Dp
) {
    addSelection?.let { selection ->

        val left =
            minOf(
                selection.startX.dp.toPx(),
                selection.currentX.dp.toPx()
            ) - state.scrollX

        val right =
            maxOf(
                selection.startX.dp.toPx(),
                selection.currentX.dp.toPx()
            ) - state.scrollX

        val track = tracksData.tracks[selection.rowIndex]

        val trackPosition = tracksData.positions[track.id]

        val top = trackPosition?.top?.dp?.toPx()?.minus(state.scrollY)
            ?: return@drawSelectionArea

        drawRect(
            color = Color(0x334CAF50),
            topLeft = Offset(
                left,
                top
            ),
            size = Size(
                width = right - left,
                height = rowHeight.toPx()
            )
        )
    }
}


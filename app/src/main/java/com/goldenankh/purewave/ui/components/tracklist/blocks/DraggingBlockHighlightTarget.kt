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

package com.goldenankh.purewave.ui.components.tracklist.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.DropTarget
import com.goldenankh.domain.model.TrackPosition
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.purewave.ui.components.tracklist.lazy.scrollstate.LazyTracksState
import com.goldenankh.purewave.ui.components.tracklist.lazy.scrollstate.rememberLazyTracksState
import com.goldenankh.purewave.ui.components.tracklist.preview.TrackParametersPreviewProvider
import kotlin.math.roundToInt

@Composable
fun DraggingBlock(
    rowHeight: Dp,
    dragSession: DragSession,
    moveBlockDropTarget: DropTarget,
    trackPosition: TrackPosition,
    dropIndicatorColor: Color,
    dropIndicatorBorderColor: Color,
    state: LazyTracksState
) {
    Box(
        Modifier
            .offset {
                IntOffset(
                    (moveBlockDropTarget.gapStartX.dp.toPx() + moveBlockDropTarget.offset.dp.toPx() - state.scrollX).roundToInt(),
                    (trackPosition.top.dp.toPx() - state.scrollY).roundToInt()
                )
            }
            .width(dragSession.block.width.dp)
            .height(rowHeight)
            .background(
                dropIndicatorColor, RoundedCornerShape(
                    8.dp
                )
            )
            .border(
                2.dp, dropIndicatorBorderColor, RoundedCornerShape(
                    8.dp
                )
            ))
}

@Preview
@Composable
fun DraggingBlockPreview(
    @PreviewParameter(TrackParametersPreviewProvider::class)
    tracksData: TracksData
) {
    val state = rememberLazyTracksState()
    val block = tracksData.tracks[0].items[0] as? TracksItem.Block
    val rowPosition = tracksData.positions["row-0"]
    val gapPosition = rowPosition?.itemPositions?.get("gap-0")

    DraggingBlock(
        rowHeight = 72.dp,
        dragSession = DragSession(block ?: TracksItem.Block("", "", 0f, RGBColor(0, 0, 0)), 0, 0, 0f, 0f, 0f, 0f),
        moveBlockDropTarget = DropTarget(0, 1, 10f, gapPosition?.startX ?: 0f, (gapPosition?.endX ?: 0f) - (gapPosition?.startX ?: 0f), false),
        trackPosition = rowPosition ?: TrackPosition(0, 0, mapOf()),
        dropIndicatorColor = Color(0x556750A4),
        dropIndicatorBorderColor = Color(0xFF6750A4),
        state
    )
}
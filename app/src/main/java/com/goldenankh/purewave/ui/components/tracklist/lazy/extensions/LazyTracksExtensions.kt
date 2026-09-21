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

package com.goldenankh.purewave.ui.components.tracklist.lazy.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.ItemPosition
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.purewave.ui.components.tracklist.lazy.LazyTracksState
import com.goldenankh.purewave.ui.components.tracklist.lazy.model.PlacedTrackItem
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
fun LazyLayoutMeasureScope.measureTracks(
    state: LazyTracksState,
    tracksData: TracksData,
    flatTracks: List<TracksItem>,
    rowHeightPx: Int,
    constraints: Constraints
): MeasureResult {
    val viewportWidth = constraints.maxWidth
    val viewportHeight = constraints.maxHeight

    state.update(
        viewportWidth,
        viewportHeight,
        tracksData.contentWidth.dp.toPx().toInt(),
        tracksData.contentHeight.dp.toPx().toInt()
    )

    if (flatTracks.isEmpty()) {
        return layout(
            viewportWidth,
            viewportHeight
        ) {}
    }

    val overscanX = viewportWidth.toFloat()
    val overscanY = viewportHeight.toFloat()

    val visibleLeft = state.scrollX - overscanX
    val visibleRight = state.scrollX + viewportWidth + overscanX

    val visibleTop = state.scrollY - overscanY
    val visibleBottom = state.scrollY + viewportHeight + overscanY

    val firstRow = findFirstRow(tracksData, visibleTop.toDp().value)
    val lastRow = findLastRow(tracksData, visibleBottom.toDp().value)

    if (firstRow !in 0..lastRow) {
        return layout(viewportWidth, viewportHeight) {}
    }

    val placed = arrayListOf<PlacedTrackItem>()

    for (rowIndex in firstRow..lastRow) {
        val track = tracksData.tracks[rowIndex]
        val itemPositions = tracksData.positions[track.id]?.itemPositions

        val firstItem = findFirstItem(
            track.items,
            itemPositions,
            visibleLeft.toDp().value
        )

        val lastItem = findLastItem(
            track.items,
            itemPositions,
            visibleRight.toDp().value
        )

        if (firstItem !in 0..lastItem) {
            continue
        }

        for (itemIndex in firstItem..lastItem) {
            val trackItem = track.items[itemIndex]
            val flatIndex = flatTracks.indexOf(trackItem)

            val trackPosition = tracksData.positions[track.id]
            val itemPosition = trackPosition?.itemPositions?.get(trackItem.id)

            if (flatIndex < 0 || trackPosition == null || itemPosition == null) {
                continue
            }

            val placeable =
                compose(flatIndex)
                    .firstOrNull()
                    ?.measure(
                        Constraints.fixed(
                            trackItem.width.dp.toPx().toInt(),
                            rowHeightPx
                        )
                    )
                    ?: continue

            placed += PlacedTrackItem(
                placeable,
                itemPosition.startX.dp.toPx().toInt() - state.scrollX.roundToInt(),
                trackPosition.top.dp.toPx().toInt() - state.scrollY.roundToInt()
            )
        }
    }

    return layout(
        viewportWidth,
        viewportHeight
    ) {

        placed.forEach {
            it.placeable.place(
                it.x,
                it.y
            )
        }
    }
}

private fun findFirstRow(
    tracksData: TracksData,
    y: Float
): Int = tracksData.tracks.indexOfFirst { track ->
    val trackPosition = tracksData.positions[track.id]
    trackPosition != null && trackPosition.bottom >= y
}

private fun findLastRow(
    tracksData: TracksData,
    y: Float
): Int = tracksData.tracks.indexOfLast { track ->
    val trackPosition = tracksData.positions[track.id]
    trackPosition != null &&  trackPosition.top < y
}


private fun findFirstItem(
    items: List<TracksItem>,
    itemPositions: Map<String, ItemPosition>?,
    x: Float
): Int = items.indexOfFirst {
    val itemPosition = itemPositions?.get(it.id)
    itemPosition != null && itemPosition.endX >= x
}

private fun findLastItem(
    items: List<TracksItem>,
    itemPositions: Map<String, ItemPosition>?,
    x: Float
): Int = items.indexOfLast {
    val itemPosition = itemPositions?.get(it.id)
    itemPosition != null && itemPosition.startX < x
}
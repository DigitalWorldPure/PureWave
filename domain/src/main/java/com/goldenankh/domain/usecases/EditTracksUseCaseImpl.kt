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

package com.goldenankh.domain.usecases

import com.goldenankh.domain.config.TracksSettings
import com.goldenankh.domain.extensions.totalWidth
import com.goldenankh.domain.helpers.TrackItemIdGenerator
import com.goldenankh.domain.model.AddSelection
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.DropTarget
import com.goldenankh.domain.model.ItemInfo
import com.goldenankh.domain.model.ItemPosition
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TrackPosition
import com.goldenankh.domain.model.TrackRow
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.common.RGBColor
import javax.inject.Inject

class EditTracksUseCaseImpl @Inject constructor(
    private val itemIdGenerator: TrackItemIdGenerator
) : EditTracksUseCase {

    override fun calculateTracksData(tracks: List<TrackRow>): TracksData {
        return normalizeTracksWidth(tracks).let { normalizedTracks ->
            TracksData(
                normalizedTracks,
                calculateTrackPositions(normalizedTracks),
                calculateMaxTracksWidth(normalizedTracks),
                calculateMaxTracksHeight(normalizedTracks)
            )
        }
    }

    override fun getDragAndDropTarget(
        tracks: TracksData,
        block: TracksItem.Block,
        positionX: Float,
        positionY: Float,
        currentTouchOffsetX: Float,
        scrollX: Float,
        scrollY: Float,
    ): DropTarget? {
        val contentX = positionX + scrollX
        val contentY = positionY + scrollY

        val track = trackAt(tracks, contentY) ?: return null
        val trackIndex = tracks.tracks.indexOf(track)

        val blockWidth = block.width

        val startDragItem = positionX - currentTouchOffsetX + scrollX
        val endDragItem = startDragItem + block.width

        val itemPositions = tracks.positions[track.id]?.itemPositions ?: return null

        val sourceIntersectionItem = itemWithIdInRange(
            track = track,
            id = block.id,
            itemPositions = itemPositions,
            rangeX = startDragItem..endDragItem
        )

        val targetItem = sourceIntersectionItem ?: itemAt(
            track,
            itemPositions,
            contentX
        ) ?: return null

        val targetItemPosition = itemPositions[targetItem.id] ?: return null

        val targetItemIndex = track.items.indexOf(targetItem)
        var gapItemIndex: Int?
        var gapStartX: Float?
        var gapWidth: Float?

        if (sourceIntersectionItem != null) {
            val left = track.items.getOrNull(targetItemIndex - 1)
            val right = track.items.getOrNull(targetItemIndex + 1)
            val leftPos = left?.let { itemPositions[left.id] }
            val rightPos = right?.let { itemPositions[right.id] }

            val start = if (left is TracksItem.Gap && leftPos != null) {
                leftPos.startX
            } else {
                targetItemPosition.startX
            }

            val end = if (right is TracksItem.Gap && rightPos != null) {
                rightPos.endX
            } else {
                targetItemPosition.endX
            }

            gapItemIndex = if (left is TracksItem.Gap) {
                targetItemIndex - 1
            } else {
                targetItemIndex
            }
            gapStartX = start
            gapWidth = end - start
        } else if (targetItem is TracksItem.Gap) {
            gapItemIndex = targetItemIndex
            gapStartX = targetItemPosition.startX
            gapWidth = targetItem.width
        } else {
            return null
        }

        if (blockWidth > gapWidth) {
            return null
        }

        val blockLeft = contentX - currentTouchOffsetX
        val rawOffset = blockLeft - gapStartX

        val maxOffset = (gapWidth - blockWidth)
            .coerceAtLeast(0f)

        return DropTarget(
            rowIndex = trackIndex,
            gapIndex = gapItemIndex,
            offset = rawOffset.coerceIn(0f, maxOffset),
            gapStartX = gapStartX,
            gapWidth = gapWidth,
            targetIsSourceBlockGap = sourceIntersectionItem != null
        )
    }

    override fun addBlockAt(
        tracksData: TracksData,
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float,
        blockWidth: Float
    ): List<TrackRow>? {
        val contentX = positionX + scrollX
        val contentY = positionY + scrollY

        val targetTrack = trackAt(tracksData, contentY) ?: return null
        val trackIndex = tracksData.tracks.indexOf(targetTrack)

        val itemPositions = tracksData.positions[targetTrack.id]?.itemPositions ?: return null

        val item = itemAt(targetTrack, itemPositions, contentX)

        if (item == null ||
            trackIndex !in tracksData.tracks.indices ||
            item !is TracksItem.Gap
        ) {
            return null
        }

        val sourceTrack = tracksData.tracks[trackIndex]

        var currentWidth = 0f

        sourceTrack.items.forEachIndexed { index, item ->

            val width = item.width

            if (
                item is TracksItem.Gap &&
                contentX >= currentWidth &&
                contentX < currentWidth + width
            ) {

                if (width < blockWidth) {
                    return null
                }

                val offset = (contentX - currentWidth)
                    .coerceIn(0f, width - blockWidth)

                val block =
                    TracksItem.Block(
                        id =
                            "block-${System.nanoTime()}",
                        text = "New block",
                        width = blockWidth,
                        color = RGBColor(103, 80, 164)
                    )

                val newItems = sourceTrack.items.toMutableList()

                newItems.removeAt(index)
                newItems.addAll(
                    index,
                    splitGap(
                        item,
                        block,
                        offset
                    )
                )
                mergeAdjacentGaps(newItems)
                val result = tracksData.tracks.toMutableList()

                result[trackIndex] = sourceTrack.copy(
                    items = newItems
                )
                return result
            }
            currentWidth += width
        }
        return null
    }


    override fun addBlocksInSelection(
        tracks: List<TrackRow>,
        rowIndex: Int,
        startX: Float,
        endX: Float,
        blockWidth: Float
    ): List<TrackRow> {

        if (
            rowIndex !in tracks.indices ||
            blockWidth <= 0f
        ) {
            return tracks
        }

        val row = tracks[rowIndex]

        if (startX == endX) {
            return tracks
        }

        val forward = endX > startX

        /*
        * The start of the selection must be within the gap.
        * This prevents starting an addition on top of an existing block.
        */
        val startItemInfo = findItemAt(row, startX)

        if (startItemInfo == null || startItemInfo.item !is TracksItem.Gap) {
            return tracks
        }

        val selectionMin = minOf(startX, endX)
        val selectionMax = maxOf(startX, endX)
        /*
        * Add blocks until we find the first intersection
        */
        val intersectingBlocks = row.items.mapIndexed { index, item ->
            val itemStart = itemStart(row, index)

            val itemEnd = itemStart + item.width

            Triple(
                index,
                itemStart,
                itemEnd
            )
        }.filter { (index, itemStart, itemEnd) ->
            row.items[index] is TracksItem.Block &&
                    itemEnd > selectionMin &&
                    itemStart < selectionMax
        }

        val effectiveEnd = if (forward) {
            val firstBlock =
                intersectingBlocks
                    .minByOrNull {
                        it.second
                    }

            firstBlock?.second ?: selectionMax
        } else {
            val lastBlock =
                intersectingBlocks
                    .maxByOrNull {
                        it.third
                    }

            lastBlock?.third ?: selectionMin
        }


        /*
        * Length from the start point to the boundary
        */
        val availableLength = if (forward) {
            effectiveEnd - startX
        } else {
            startX - effectiveEnd
        }

        if (availableLength < blockWidth) {
            return tracks
        }

        val count = (availableLength / blockWidth).toInt()

        if (count <= 0) {
            return tracks
        }

        /*
        * Actual fill endpoint.
        */
        val usedLength = count * blockWidth

        val fillStart = if (forward) {
            startX
        } else {
            startX - usedLength
        }

        val fillEnd = if (forward) {
            startX + usedLength
        } else {
            startX
        }


        val gapIndex = row.items.indexOfFirst { item ->
            val index =
                row.items.indexOf(item)

            val itemStart =
                itemStart(row, index)

            val itemEnd =
                itemStart +
                        item.width

            item is TracksItem.Gap &&
                    fillStart >= itemStart &&
                    fillEnd <= itemEnd
        }

        if (gapIndex < 0) {
            return tracks
        }

        val gap = row.items[gapIndex] as? TracksItem.Gap ?: return tracks

        val gapStart = itemStart(
            row,
            gapIndex
        )

        val gapEnd = gapStart + gap.width

        if (fillStart < gapStart || fillEnd > gapEnd) {
            return tracks
        }

        /*
        * Fill the selected area with blocks
        */
        val replacement = ArrayList<TracksItem>()

        if (fillStart > gapStart) {
            replacement += TracksItem.Gap(
                id = itemIdGenerator.generateItemId(),
                width = fillStart - gapStart
            )
        }

        repeat(count) { index ->
            replacement +=
                TracksItem.Block(
                    id =
                        "block-${System.nanoTime()}-$index",
                    text = "New block",
                    width = blockWidth,
                    color = RGBColor(103, 80, 164)
                )


        }

        if (fillEnd < gapEnd) {
            replacement +=
                TracksItem.Gap(
                    id = itemIdGenerator.generateItemId(),
                    width = gapEnd - fillEnd
                )
        }

        val newItems = row.items.toMutableList()

        newItems.removeAt(gapIndex)
        newItems.addAll(gapIndex, replacement)

        mergeAdjacentGaps(newItems)

        val result = tracks.toMutableList()

        result[rowIndex] = row.copy(
            items = newItems
        )

        return result
    }

    override fun removeBlockAt(
        tracksData: TracksData,
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float
    ): List<TrackRow>? {
        val contentY = positionY + scrollY
        val contentX = positionX + scrollX

        val track = trackAt(
            tracksData,
            contentY
        ) ?: return null

        val itemPositions = tracksData.positions[track.id]?.itemPositions ?: return null

        val item = itemAt(
            track,
            itemPositions,
            contentX
        ) ?: return null

        val trackIndex = tracksData.tracks.indexOf(track)

        if (item is TracksItem.Block) {
            val info = findItemAt(
                track,
                contentX
            ) ?: return null

            if (info.item !is TracksItem.Block) {
                return null
            }

            val newItems = track.items.toMutableList()
            newItems.removeAt(info.index)

            newItems.add(
                info.index,
                TracksItem.Gap(
                    id = itemIdGenerator.generateItemId(),
                    width = info.item.width
                )
            )

            mergeAdjacentGaps(newItems)

            val result = tracksData.tracks.toMutableList()
            result[trackIndex] = track.copy(items = newItems)

            return result
        }
        return null
    }


    override fun moveBlock(
        tracks: List<TrackRow>,
        blockId: String,
        sourceRowIndex: Int,
        targetRowIndex: Int,
        targetGapIndex: Int,
        targetIsSourceBlockGap: Boolean,
        dropOffset: Float,
    ): List<TrackRow>? {
        if (sourceRowIndex !in tracks.indices ||
            targetRowIndex !in tracks.indices
        ) {
            return null
        }

        val sourceRow = tracks[sourceRowIndex]

        val sourceBlockIndex = sourceRow.items.indexOfFirst {
            it is TracksItem.Block && it.id == blockId
        }

        if (sourceBlockIndex < 0) {
            return null
        }

        val block = sourceRow.items[sourceBlockIndex] as? TracksItem.Block ?: return null

        return if (sourceRowIndex == targetRowIndex) {
            moveBlockSameRow(
                tracks,
                sourceRowIndex,
                sourceBlockIndex,
                block,
                targetGapIndex,
                targetIsSourceBlockGap,
                dropOffset
            )
        } else {
            moveBlockDifferentRows(
                tracks,
                sourceRowIndex,
                sourceBlockIndex,
                targetRowIndex,
                targetGapIndex,
                block,
                dropOffset
            )
        }
    }

    override fun calculateInitialDragSession(
        tracksData: TracksData,
        startPositionX: Float,
        startPositionY: Float,
        scrollX: Float,
        scrollY: Float
    ): DragSession? {
        val contentX = startPositionX + scrollX
        val contentY = startPositionY + scrollY

        val track = trackAt(tracksData, contentY) ?: return null
        val trackPositions = tracksData.positions[track.id]?.itemPositions ?: return null

        val targetItem = itemAt(track, trackPositions, contentX) ?: return null
        val block = targetItem as? TracksItem.Block ?: return null

        val sourceRowIndex = tracksData.tracks.indexOf(track)
        val sourceItemIndex = track.items.indexOf(targetItem)

        val trackPosition = tracksData.positions[track.id] ?: return null
        val itemPosition = trackPosition.itemPositions[targetItem.id] ?: return null

        val blockLeft = itemPosition.startX - scrollX
        val blockTop = trackPosition.top - scrollY

        return DragSession(
            block = block,
            sourceRowIndex = sourceRowIndex,
            sourceItemIndex = sourceItemIndex,
            touchOffsetX = startPositionX - blockLeft,
            touchOffsetY = startPositionY - blockTop,
            pointerPositionX = startPositionX,
            pointerPositionY = startPositionY
        )
    }

    override fun startAddItemsBySelection(
        tracksData: TracksData,
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float
    ): AddSelection? {
        val contentX = positionX + scrollX
        val contentY = positionY + scrollY

        val track = trackAt(
            tracksData,
            contentY
        ) ?: return null

        val itemPositions = tracksData.positions[track.id]?.itemPositions
            ?: return null

        val item = itemAt(track, itemPositions, contentX)

        if (item is TracksItem.Gap) {

            val rowMinX = itemPositions.values.firstOrNull()?.startX
                ?: return null

            val rowMaxX = itemPositions.values.lastOrNull()?.endX
                ?: return null

            val start = contentX.coerceIn(
                rowMinX, rowMaxX
            )

            return AddSelection(
                rowIndex = tracksData.tracks.indexOf(track),
                startX = start,
                currentX = start,
                minX = rowMinX,
                maxX = rowMaxX
            )
        }
        return null
    }

    override fun updateAddSelectionArea(
        selection: AddSelection,
        positionX: Float,
        scrollX: Float,
    ): AddSelection {
        val current = positionX.coerceIn(
            selection.minX - scrollX,
            selection.maxX - scrollX
        ) + scrollX

        return selection.copy(
            currentX = current.coerceIn(
                selection.minX, selection.maxX
            )
        )
    }


    private fun moveBlockDifferentRows(
        tracks: List<TrackRow>,
        sourceRowIndex: Int,
        sourceBlockIndex: Int,
        targetRowIndex: Int,
        targetGapIndex: Int,
        block: TracksItem.Block,
        dropOffset: Float
    ): List<TrackRow>? {


        val sourceRow = tracks[sourceRowIndex]

        val targetRow = tracks[targetRowIndex]
        val targetGap = targetRow.items.getOrNull(targetGapIndex) as? TracksItem.Gap ?: return null

        if (block.width > targetGap.width) {
            return tracks
        }

        val sourceItems = sourceRow.items.toMutableList()

        sourceItems.removeAt(sourceBlockIndex)
        sourceItems.add(
            sourceBlockIndex,
            TracksItem.Gap(
                itemIdGenerator.generateItemId(),
                block.width
            )
        )

        mergeAdjacentGaps(sourceItems)

        val targetItems = targetRow.items.toMutableList()
        targetItems.removeAt(targetGapIndex)

        targetItems.addAll(
            targetGapIndex,
            splitGap(
                targetGap,
                block,
                dropOffset
            )
        )

        mergeAdjacentGaps(
            targetItems
        )

        if (
            sourceItems.totalWidth() !=
            sourceRow.totalWidth() ||
            targetItems.totalWidth() !=
            targetRow.totalWidth()
        ) {
            return null
        }

        return tracks.toMutableList().apply {
            this[sourceRowIndex] =
                sourceRow.copy(
                    items = sourceItems
                )
            this[targetRowIndex] =
                targetRow.copy(
                    items = targetItems
                )
        }
    }

    private fun moveBlockSameRow(
        tracks: List<TrackRow>,
        rowIndex: Int,
        sourceBlockIndex: Int,
        block: TracksItem.Block,
        targetGapIndex: Int,
        targetIsSourceBlockGap: Boolean,
        dropOffset: Float
    ): List<TrackRow>? {

        val row = tracks[rowIndex]
        val originalWidth = row.totalWidth()

        val items = row.items.toMutableList()
        items.removeAt(sourceBlockIndex)

        items.add(
            sourceBlockIndex,
            TracksItem.Gap(
                itemIdGenerator.generateItemId(),
                block.width
            )
        )

        if (targetIsSourceBlockGap) {
            mergeAdjacentGaps(
                items
            )
        }

        val targetGap = items.getOrNull(
            targetGapIndex
        ) as? TracksItem.Gap ?: return null

        if (block.width > targetGap.width) {
            return null
        }

        items.removeAt(targetGapIndex)

        items.addAll(
            targetGapIndex,
            splitGap(
                targetGap,
                block,
                dropOffset
            )
        )

        mergeAdjacentGaps(
            items
        )

        if (items.totalWidth() != originalWidth) {
            return null
        }

        return tracks.toMutableList().apply {
            this[rowIndex] = row.copy(
                items = items
            )
        }
    }

    private fun trackAt(
        tracksData: TracksData,
        contentY: Float
    ): TrackRow? = tracksData.tracks.firstOrNull {
        val trackPosition = tracksData.positions[it.id]
        trackPosition?.let { contentY >= trackPosition.top && contentY < trackPosition.bottom }
            ?: false
    }

    private fun itemAt(
        track: TrackRow,
        itemPositions: Map<String, ItemPosition>,
        contentX: Float
    ): TracksItem? = track.items.firstOrNull {
        val itemPosition = itemPositions[it.id]
        itemPosition?.let { contentX >= it.startX && contentX < it.endX } ?: false
    }

    private fun mergeAdjacentGaps(
        items: MutableList<TracksItem>
    ) {
        var index = 0

        while (index < items.lastIndex) {
            val first = items[index]

            val second = items[index + 1]

            if (first is TracksItem.Gap
                && second is TracksItem.Gap
            ) {
                items[index] =
                    TracksItem.Gap(
                        first.id,
                        first.width + second.width
                    )
                items.removeAt(index + 1)
            } else {
                index++
            }
        }
    }

    private fun findItemAt(
        track: TrackRow,
        x: Float
    ): ItemInfo? {
        var position = 0f

        track.items.forEachIndexed { index, item ->
            val start = position
            val end = start + item.width

            if (x in start..<end) {
                return ItemInfo(
                    index = index,
                    item = item,
                    start = start,
                    end = end
                )
            }

            position = end
        }

        val last = track.items.lastOrNull()

        if (last != null) {
            val total = track.totalWidth()
            if (x == total) {
                val index = track.items.lastIndex
                return ItemInfo(
                    index = index,
                    item = last,
                    start = total - last.width,
                    end = total
                )
            }
        }
        return null
    }

    private fun itemStart(
        row: TrackRow,
        index: Int
    ): Float {
        var x = 0f
        for (i in 0 until index) {
            x += row.items[i].width
        }
        return x
    }

    private fun splitGap(
        gap: TracksItem.Gap,
        block: TracksItem.Block,
        offset: Float
    ): List<TracksItem> {
        val available = (gap.width - block.width)
            .coerceAtLeast(0f)

        val before = offset.coerceIn(0f, available)

        val after = (gap.width - before - block.width)
            .coerceAtLeast(0f)

        return buildList {
            if (before > 0f) {
                add(
                    TracksItem.Gap(
                        itemIdGenerator.generateItemId(),
                        before
                    )
                )
            }
            add(block)

            if (after > 0f) {
                add(
                    TracksItem.Gap(
                        itemIdGenerator.generateItemId(),
                        after
                    )
                )
            }
        }
    }

    private fun itemWithIdInRange(
        track: TrackRow,
        itemPositions: Map<String, ItemPosition>,
        id: String,
        rangeX: ClosedFloatingPointRange<Float>
    ): TracksItem? {
        val itemWithId = track.items.firstOrNull { rowItem ->
            rowItem.id == id
        } ?: return null

        val itemPosition = itemPositions[itemWithId.id]
        itemPosition?.let {
            if (it.startX in rangeX || it.endX in rangeX) {
                return itemWithId
            }
        }
        return null
    }

    private fun calculateMaxTracksWidth(
        tracks: List<TrackRow>
    ) = tracks.maxOfOrNull { track ->
        track.items.sumOf { it.width.toInt() }
    } ?: 0

    private fun calculateMaxTracksHeight(
        tracks: List<TrackRow>
    ): Int = tracks.size * TracksSettings.TRACK_HEIGHT

    private fun normalizeTracksWidth(
        tracks: List<TrackRow>
    ): List<TrackRow> {

        if (tracks.isEmpty()) {
            return tracks
        }

        val endTrackMinSpace = TracksSettings.END_TRACK_MINIMUM_SPACE
            .coerceAtLeast(0f)

        data class TrackInfo(
            val row: TrackRow,
            val prefixWidth: Float,
            val currentEnd: Float
        )

        val infos =
            tracks.map { track ->
                val lastGapIndex =
                    track.items.indexOfLast {
                        it is TracksItem.Gap
                    }

                if (lastGapIndex >= 0) {

                    TrackInfo(
                        row = track,
                        prefixWidth = track.items
                            .take(lastGapIndex)
                            .totalWidth(),
                        currentEnd = track.totalWidth()
                    )

                } else {
                    val end = track.totalWidth()

                    TrackInfo(
                        row = track,
                        prefixWidth = end,
                        currentEnd = end
                    )
                }
            }

        var targetEnd =
            infos.maxOfOrNull {
                it.currentEnd
            } ?: 0f

        infos.forEach {
            targetEnd =
                maxOf(
                    targetEnd,
                    it.prefixWidth +
                            endTrackMinSpace
                )
        }

        return infos.map { info ->
            val items = info.row.items.toMutableList()
            val lastIndex = items.lastIndex

            val requiredWidth = (targetEnd - info.prefixWidth)
                .coerceAtLeast(
                    endTrackMinSpace
                )

            if (
                lastIndex >= 0 &&
                items[lastIndex] is TracksItem.Gap
            ) {
                val gap = items[lastIndex] as TracksItem.Gap

                items[lastIndex] = gap.copy(
                    width = requiredWidth
                )

            } else {
                items += TracksItem.Gap(
                    id = itemIdGenerator.generateItemId(),
                    width = requiredWidth
                )
            }

            info.row.copy(
                items = items
            )
        }
    }

    private fun calculateTrackPositions(
        tracks: List<TrackRow>
    ): Map<String, TrackPosition> {
        val trackPositions = hashMapOf<String, TrackPosition>()
        var currentTop = 0
        tracks.forEach { track ->
            val itemPositions = hashMapOf<String, ItemPosition>()
            var currentX = 0f
            track.items.forEach { item ->
                val width = item.width
                val start = currentX
                val end = start + width

                itemPositions[item.id] = ItemPosition(
                    startX = start,
                    endX = end
                )

                currentX = end
            }
            trackPositions[track.id] = TrackPosition(
                top = currentTop,
                bottom = currentTop + TracksSettings.TRACK_HEIGHT,
                itemPositions = itemPositions
            )
            currentTop += TracksSettings.TRACK_HEIGHT
        }
        return trackPositions
    }
}


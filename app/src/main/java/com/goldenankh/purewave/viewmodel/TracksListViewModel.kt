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

package com.goldenankh.purewave.viewmodel

import androidx.lifecycle.ViewModel
import com.goldenankh.domain.model.AddSelection
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.DropTarget
import com.goldenankh.domain.model.TrackRow
import com.goldenankh.domain.usecases.EditTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * ViewModel for working with tracks
 *
 * Tracks are designed to hold audio or synthesized sound samples.
 * They consist of rows containing blocks.
 *
 */
@HiltViewModel
class TracksListViewModel @Inject constructor(
    private val editTracksUseCase: EditTracksUseCase
) : ViewModel() {
    val tracksDataState = MutableStateFlow(initialTracksData.let (::updateTrackData))
    val moveBlockDragSessionState = MutableStateFlow<DragSession?>(null)
    val moveBlockDropTargetState = MutableStateFlow<DropTarget?>(null)
    val addSelectionState = MutableStateFlow<AddSelection?>(null)

    /**
     * Updates tracks after editing (add, move or remove blocks)
     *
     * @param newTracks New tracks for the update
     *
     */
    fun updateTracks(newTracks: List<TrackRow>) {
        tracksDataState.value = newTracks.let (::updateTrackData)
    }

    /**
     * Adds a block at the specified coordinates to the
     * track found based on the Y-coordinate
     *
     * @param positionX X-axis position for adding the block
     * @param positionY Y-axis position for adding the block
     * @param scrollX Current scroll position along the X-axis
     * @param scrollY Current scroll position along the Y-axis
     * @param blockWidth Width of the new block being added
     *
     */
    fun addBlockAt(
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float,
        blockWidth: Float
    ) {
        val result = editTracksUseCase.addBlockAt(tracksDataState.value, positionX, positionY,
            scrollX, scrollY, blockWidth)
        result?.let(::updateTracks)
    }

    /**
     * Fills the selected area with blocks;
     * if a block is encountered within the selected area,
     * filling proceeds only up to the left edge of that block
     *
     * @param blockWidth Width of the new block being added
     *
     */
    fun addBlocksInSelection(
        blockWidth: Float
    ) {
        val selection = addSelectionState.value
        addSelectionState.value = null

        if (selection != null) {
            val startX = selection.startX
            val endX = selection.currentX
            val rowIndex = selection.rowIndex
            val result = editTracksUseCase.addBlocksInSelection(
                tracksDataState.value.tracks, rowIndex, startX,
                endX, blockWidth
            )
            result.let(::updateTracks)
        }
    }

    /**
     * Removes a block at the specified coordinates
     *
     * @param positionX X-axis position for block removal
     * @param positionY Y-axis position for block removal
     * @param scrollX Current scroll position along the X-axis
     * @param scrollY Current scroll position along the Y-axis
     *
     */
    fun removeBlockAt(
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float
    ) {
        val result = editTracksUseCase.removeBlockAt(tracksDataState.value, positionX, positionY, scrollX, scrollY)
        result?.let(::updateTracks)
    }

    /**
     * Moves a block within a track and to other
     * positions of other tracks
     *
     * @param blockId ID of the block being moved
     * @param sourceRowIndex Index of the track containing the block being moved
     * @param targetRowIndex Index of the track to which the block is being moved
     * @param targetGapIndex Gap index of the position where the block is being moved
     * @param targetIsSourceBlockGap Evaluates to true if the block partially or fully
     * intersects with the block at the current position
     * @param dropOffset Offset for moving the block relative to the gap
     *
     */
    fun moveBlock(
        blockId: String,
        sourceRowIndex: Int,
        targetRowIndex: Int,
        targetGapIndex: Int,
        targetIsSourceBlockGap: Boolean,
        dropOffset: Float,
    ) {
        val result = editTracksUseCase.moveBlock(tracksDataState.value.tracks, blockId, sourceRowIndex, targetRowIndex, targetGapIndex,
                targetIsSourceBlockGap, dropOffset)
        result?.let(::updateTracks)
    }

    /**
     * Calculating parameters for initiating block movement
     *
     * @param startPositionX Position X starts drag and drop
     * @param startPositionY Position Y starts drag and drop
     * @param scrollX Current scroll position along the X-axis
     * @param scrollY Current scroll position along the Y-axis
     *
     */
    fun onStartDragBlock(
        startPositionX: Float,
        startPositionY: Float,
        scrollX: Float,
        scrollY: Float
    ) {
        moveBlockDragSessionState.value = editTracksUseCase.calculateInitialDragSession(
            tracksDataState.value, startPositionX, startPositionY, scrollX, scrollY
        )
        moveBlockDropTargetState.value = null
    }

    /**
     * Processing drag-and-drop position while the block is moving
     *
     * @param dragAmountX X-coordinate of the current drag-and-drop operation
     * @param dragAmountY Y-coordinate of the current drag-and-drop operation
     *
     */
    fun updateDragBlockSessionPosition(
        dragAmountX: Float,
        dragAmountY: Float
    ) {
        moveBlockDragSessionState.value = moveBlockDragSessionState.value?.let {
            it.copy(
                pointerPositionX = it.pointerPositionX + dragAmountX,
                pointerPositionY = it.pointerPositionY + dragAmountY
            )
        }
    }

    /**
     * Processing drag-and-drop parameters [moveBlockDropTargetState] while the block is moving
     *
     * @param scrollX Current scroll position along the X-axis
     * @param scrollY Current scroll position along the Y-axis
     *
     */
    fun updateDragAndDropBlock(
        scrollX: Float,
        scrollY: Float,
    ) {
        val tracksData = tracksDataState.value
        val session = moveBlockDragSessionState.value ?: return
        val block = session.block
        val positionX = session.pointerPositionX
        val positionY = session.pointerPositionY
        val currentTouchOffsetX = session.touchOffsetX
        moveBlockDropTargetState.value = editTracksUseCase.getDragAndDropTarget(tracksData, block,
            positionX, positionY, currentTouchOffsetX, scrollX, scrollY)
    }

    /**
     * Once the block has been moved, the drag parameters
     * need to be reset
     *
     */
    fun onEndDragBlock() {
        moveBlockDragSessionState.value = null
        moveBlockDropTargetState.value = null
    }

    /**
     * Before selecting an area via drag-and-drop to fill it with blocks,
     * we calculate the initial parameters
     *
     * @param positionX Position X starts drag and drop
     * @param positionY Position X starts drag and drop
     * @param scrollX Current scroll position along the X-axis
     * @param scrollY Current scroll position along the Y-axis
     *
     */
    fun startAddItemsBySelection(
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float
    ) {
        addSelectionState.value = editTracksUseCase.startAddItemsBySelection(
            tracksDataState.value, positionX, positionY, scrollX, scrollY
        )
    }

    /**
     * Processing drag-and-drop selection area parameters
     *
     * @param positionX X-position of the end of the selection area
     * @param scrollX Current scroll position along the X-axis
     *
     */
    fun updateAddSelectionArea(
        positionX: Float,
        scrollX: Float
    ) {
        addSelectionState.value?.let {
            addSelectionState.value = editTracksUseCase.updateAddSelectionArea(it, positionX, scrollX)
        }
    }

    /**
     * Handling drag-and-drop directly to a specific X-coordinate
     *
     * @param currentX X-axis position with the X-coordinate plus the scroll value
     *
     */
    fun updateAddSelectionCurrent(
        currentX: Float
    ) {
        addSelectionState.value = addSelectionState.value?.copy(currentX = currentX)
    }

    /**
     * After completing the selection for filling the area with blocks,
     * we reset the parameters
     *
     */
    fun clearAddSelection() {
        addSelectionState.value = null
    }

    /**
     * Parameter processing and calculations for tracks
     *
     * @param newTracks New tracks for the update
     *
     */
    private fun updateTrackData(newTracks: List<TrackRow>) =
        editTracksUseCase.calculateTracksData(newTracks)
}
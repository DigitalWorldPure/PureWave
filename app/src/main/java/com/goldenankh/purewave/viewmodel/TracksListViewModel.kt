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
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.usecases.EditTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TracksListViewModel @Inject constructor(
    private val editTracksUseCase: EditTracksUseCase
) : ViewModel() {
    val tracksDataState = MutableStateFlow(initialTracksData.let (::updateTrackData))
    val moveBlockDragSessionState = MutableStateFlow<DragSession?>(null)
    val moveBlockDropTargetState = MutableStateFlow<DropTarget?>(null)
    val addSelectionState = MutableStateFlow<AddSelection?>(null)

    fun updateTracks(newTracks: List<TrackRow>) {
        tracksDataState.value = newTracks.let (::updateTrackData)
    }

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

    fun removeBlockAt(
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float
    ) {
        val result = editTracksUseCase.removeBlockAt(tracksDataState.value, positionX, positionY, scrollX, scrollY)
        result?.let(::updateTracks)
    }

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

    fun onEndDragBlock() {
        moveBlockDragSessionState.value = null
        moveBlockDropTargetState.value = null
    }

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

    fun updateAddSelectionArea(
        positionX: Float,
        scrollX: Float
    ) {
        addSelectionState.value?.let {
            addSelectionState.value = editTracksUseCase.updateAddSelectionArea(it, positionX, scrollX)
        }
    }

    fun updateAddSelectionCurrent(
        currentX: Float
    ) {
        addSelectionState.value = addSelectionState.value?.copy(currentX = currentX)
    }

    fun clearAddSelection() {
        addSelectionState.value = null
    }

    private fun updateTrackData(newTracks: List<TrackRow>) =
        editTracksUseCase.calculateTracksData(newTracks)
}
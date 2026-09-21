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

import com.goldenankh.domain.model.AddSelection
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.DropTarget
import com.goldenankh.domain.model.TrackRow
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TracksItem

interface EditTracksUseCase {
    fun calculateTracksData(tracks: List<TrackRow>): TracksData
    fun addBlockAt(tracksData: TracksData, positionX: Float, positionY: Float,
                   scrollX: Float, scrollY: Float, blockWidth: Float): List<TrackRow>?
    fun addBlocksInSelection(tracks: List<TrackRow>, rowIndex: Int, startX: Float,
                             endX: Float, blockWidth: Float): List<TrackRow>
    fun removeBlockAt(tracksData: TracksData, positionX: Float, positionY: Float,
                      scrollX: Float, scrollY: Float): List<TrackRow>?
    fun moveBlock(tracks: List<TrackRow>, blockId: String, sourceRowIndex: Int, targetRowIndex: Int,
                  targetGapIndex: Int, targetIsSourceBlockGap: Boolean, dropOffset: Float): List<TrackRow>?
    fun calculateInitialDragSession(tracksData: TracksData, startPositionX: Float, startPositionY: Float,
                                    scrollX: Float, scrollY: Float): DragSession?
    fun getDragAndDropTarget(tracks: TracksData, block: TracksItem.Block, positionX: Float, positionY: Float,
                             currentTouchOffsetX: Float, scrollX: Float, scrollY: Float): DropTarget?
    fun startAddItemsBySelection(tracksData: TracksData, positionX: Float, positionY: Float,
                                 scrollX: Float, scrollY: Float): AddSelection?
    fun updateAddSelectionArea(selection: AddSelection, positionX: Float, scrollX: Float): AddSelection
}
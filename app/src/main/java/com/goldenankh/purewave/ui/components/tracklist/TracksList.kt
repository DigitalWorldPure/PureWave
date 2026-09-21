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

@file:Suppress("NAME_SHADOWING")

package com.goldenankh.purewave.ui.components.tracklist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.layout.LazyLayoutPrefetchState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.goldenankh.domain.model.AddSelection
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.DropTarget
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.TrackRow
import com.goldenankh.purewave.viewmodel.TracksListViewModel
import com.goldenankh.purewave.ui.components.tracklist.blocks.Block
import com.goldenankh.purewave.ui.components.tracklist.blocks.Gap
import com.goldenankh.purewave.ui.components.tracklist.controls.ControlPanel
import com.goldenankh.purewave.ui.components.tracklist.dividers.drawTrackDividers
import com.goldenankh.purewave.ui.components.tracklist.headers.TrackHeaders
import com.goldenankh.purewave.ui.components.tracklist.lazy.LazyTracksState
import com.goldenankh.purewave.ui.components.tracklist.lazy.TracksLazyLayout
import com.goldenankh.purewave.ui.components.tracklist.playbackindicator.PlaybackIndicator
import com.goldenankh.purewave.ui.components.tracklist.scale.PlaybackIndicatorScaleHitArea
import com.goldenankh.purewave.ui.components.tracklist.scale.drawTracksScale
import com.goldenankh.purewave.ui.components.tracklist.selection.drawSelectionArea
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

// ============================================================================
// SCROLL STATE
// ============================================================================

@Composable
fun rememberLazyTracksState(): LazyTracksState = remember { LazyTracksState() }

// ============================================================================
// ADDITION
// ============================================================================

private fun contentToTracksDp(
    contentPx: Float, density: Float
): Float {
    if (density <= 0f) {
        return 0f
    }
    return contentPx.coerceAtLeast(0f) / density
}


// ============================================================================
// REMOVE TAP GESTURE
// ============================================================================

private suspend fun PointerInputScope.detectRemoveTap(
    onTap: (Offset) -> Unit
) {
    awaitEachGesture {

        val down = awaitFirstDown(
            requireUnconsumed = false, pass = PointerEventPass.Initial
        )

        val startPosition = down.position
        var moved = false

        while (true) {

            val event = awaitPointerEvent(
                PointerEventPass.Final
            )

            val change = event.changes.firstOrNull {
                it.id == down.id
            }

            if (change == null) {
                break
            }

            val dx = change.position.x - startPosition.x

            val dy = change.position.y - startPosition.y

            val distance = sqrt(dx * dx + dy * dy)

            if (distance > viewConfiguration.touchSlop) {
                moved = true
            }

            if (change.changedToUpIgnoreConsumed()) {
                if (!moved) {
                    onTap(
                        change.position
                    )
                }

                break
            }
        }
    }
}

// ============================================================================
// LAZY TRACKS
// ============================================================================

@Composable
fun LazyTracks(
    modifier: Modifier = Modifier,
    tracksData: TracksData,
    moveBlockDragSession: State<DragSession?>,
    moveBlockDropTarget: State<DropTarget?>,
    addSelectionState: State<AddSelection?>,
    addBlockAt: (Float, Float, Float, Float, Float) -> Unit,
    addBlocksInSelection: (Float) -> Unit,
    removeBlockAt: (Float, Float, Float, Float) -> Unit,
    moveBlock: (String, Int, Int, Int, Boolean, Float) -> Unit,
    onStartDrag: (Float, Float, Float, Float) -> Unit,
    updateDragAndDropBlock: (Float, Float) -> Unit,
    updateDragSessionPosition: (Float, Float) -> Unit,
    onEndDragBlock: () -> Unit,
    startAddItemsBySelection: (
        positionX: Float,
        positionY: Float,
        scrollX: Float,
        scrollY: Float
    ) -> Unit,
    updateAddSelectionArea: (positionX: Float, scrollX: Float) -> Unit,
    updateAddSelectionCurrent: (currentX: Float) -> Unit,
    clearAddSelection: () -> Unit,
    state: LazyTracksState,
    rowHeight: Dp,
    dpPerSecond: Dp,
    timeDivisionSeconds: Int,
    timeScaleHeight: Dp,
    trackWidth: Dp,
    trackColor: Color,
    trackBorderColor: Color,
    trackTextColor: Color,
    backgroundColor: Color,
    dropIndicatorColor: Color,
    dropIndicatorBorderColor: Color,
    separatorColor: Color,
    separatorWidth: Dp,
    playbackIndicatorColor: Color,
    playbackDotColor: Color,
    playbackSpeed: Dp,
    autoScrollEdge: Dp,
    autoScrollSpeed: Dp,
    newBlockWidth: Dp,
    content: @Composable (TracksItem) -> Unit,
) {
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()

    var editMode by remember {
        mutableStateOf(
            TracksActionMode.NONE
        )
    }

    val isDragging = moveBlockDragSession.value != null

    val scrollState = rememberScrollable2DState { delta ->
        state.consumeScroll(
            -delta
        )
        delta
    }

    val prefetchState = remember { LazyLayoutPrefetchState() }

    var playbackPositionPx by remember { mutableFloatStateOf(0f) }

    var isPlaying by remember { mutableStateOf(false) }

    var playbackJob by remember { mutableStateOf<Job?>(null) }

    var autoScrollJob by remember { mutableStateOf<Job?>(null) }

    var addSelectionAutoScrollJob by remember { mutableStateOf<Job?>(null) }

// ========================================================================
// PLAYBACK
// ========================================================================

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null

        isPlaying = false

        if (editMode == TracksActionMode.PLAY) {
            editMode = TracksActionMode.NONE
        }
    }

    fun startPlayback() {
        if (isPlaying) {
            return
        }

        editMode = TracksActionMode.PLAY

        clearAddSelection()
        isPlaying = true

        playbackJob = scope.launch {
            val speed = with(density) {
                playbackSpeed.toPx()
            }.coerceAtLeast(1f)

            val playbackEdge = with(density) {
                96.dp.toPx()
            }

            var last = System.nanoTime()

            while (isActive) {
                val now = System.nanoTime()
                val dt = ((now - last).coerceIn(0L, 50_000_000L).toFloat() / 1_000_000_000f)
                last = now

                val start = 0f
                val end = tracksData.contentWidth.coerceAtLeast(start.toInt()).toFloat()

                val next = playbackPositionPx + speed * dt
                if (next >= end) {
                    playbackPositionPx = start

                    state.consumeScroll(
                        Offset(
                            -state.scrollX, 0f
                        )
                    )
                } else {
                    playbackPositionPx = next

                    val viewportLeft = state.scrollX
                    val viewportRight = state.scrollX + state.viewportWidth

                    val leftSafe = viewportLeft + playbackEdge
                    val rightSafe = viewportRight - playbackEdge

                    if (next > rightSafe && state.scrollX < state.maxScrollX) {

                        val desiredScroll = next - rightSafe

                        state.consumeScroll(
                            Offset(
                                desiredScroll.coerceAtLeast(0f), 0f
                            )
                        )

                    } else if (next < leftSafe && state.scrollX > 0f) {

                        val desiredScroll = next - leftSafe

                        state.consumeScroll(
                            Offset(
                                desiredScroll.coerceAtMost(0f), 0f
                            )
                        )
                    }
                }

                delay(16.milliseconds)
            }
        }
    }

    fun togglePlayback() {
        if (isPlaying) {
            stopPlayback()
        } else {
            startPlayback()
        }
    }


    fun setPlaybackPositionFromScale(localX: Float) {
        val startX = 0f

        val endX = tracksData.contentWidth.coerceAtLeast(startX.toInt()).toFloat()

        playbackPositionPx = (localX + state.scrollX).coerceIn(
            startX, endX
        )
    }

// ========================================================================
// AUTO SCROLL
// ========================================================================

    fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    fun startAutoScroll() {
        stopAutoScroll()
        autoScrollJob = scope.launch {
            val edge = with(density) {
                autoScrollEdge.toPx()
            }

            val speed = with(density) {
                autoScrollSpeed.toPx()
            }

            var last = System.nanoTime()


            while (isActive) {
                val moveBlockDragSession = moveBlockDragSession.value ?: break
                val pointerX = with(density) {
                    moveBlockDragSession.pointerPositionX.dp.toPx()
                }
                val pointerY = with(density) {
                    moveBlockDragSession.pointerPositionY.dp.toPx()
                }

                val now = System.nanoTime()

                val dt = ((now - last).coerceIn(0L, 50_000_000L).toFloat() / 1_000_000_000f)

                last = now

                var dx = 0f
                var dy = 0f

                if (pointerX > state.viewportWidth - edge && state.scrollX < state.maxScrollX) {

                    val factor = (1f - (state.viewportWidth - pointerX) / edge).coerceIn(0f, 1f)

                    dx = speed * factor * dt

                } else if (pointerX < edge && state.scrollX > 0f) {
                    val factor = (1f - pointerX / edge).coerceIn(0f, 1f)

                    dx = -speed * factor * dt
                }

                if (pointerY > state.viewportHeight - edge && state.scrollY < state.maxScrollY) {

                    val factor = (1f - (state.viewportHeight - pointerY) / edge).coerceIn(0f, 1f)

                    dy = speed * factor * dt

                } else if (pointerY < edge && state.scrollY > 0f) {
                    val factor = (1f - pointerY / edge).coerceIn(0f, 1f)

                    dy = -speed * factor * dt
                }

                if (dx != 0f || dy != 0f) {

                    state.consumeScroll(Offset(dx, dy))

                    val scrollX = contentToTracksDp(
                        state.scrollX, density.density
                    )
                    val scrollY = contentToTracksDp(
                        state.scrollY, density.density
                    )
                    updateDragAndDropBlock(scrollX, scrollY)
                }

                delay(16.milliseconds)
            }
        }
    }

// ========================================================================
// ADD SELECTION AUTO SCROLL
// ========================================================================

    fun stopAddSelectionAutoScroll() {
        addSelectionAutoScrollJob?.cancel()
        addSelectionAutoScrollJob = null
    }

    fun startAddSelectionAutoScroll() {
        stopAddSelectionAutoScroll()
        addSelectionAutoScrollJob = scope.launch {

            val edge = with(density) {
                autoScrollEdge.toPx()
            }

            val speed = with(density) {
                autoScrollSpeed.toPx()
            }

            var last = System.nanoTime()

            while (isActive) {
                val selection = addSelectionState.value ?: break

                /*
                 * During auto-scrolling, the finger physically remains
                 * near the edge of the viewport
                 *
                 * save its on-screen X-position,
                 * scroll the content,
                 * and recalculate `currentX` in terms of content coordinates
                 */
                val pointerX = with(density) { selection.currentX.dp.toPx() } - state.scrollX
                val now = System.nanoTime()

                val dt = ((now - last).coerceIn(0L, 50_000_000L).toFloat() / 1_000_000_000f)
                last = now
                var dx = 0f

                if (pointerX > state.viewportWidth - edge && state.scrollX < state.maxScrollX) {
                    val factor = (1f - (state.viewportWidth - pointerX) / edge).coerceIn(0f, 1f)
                    dx = speed * factor * dt
                } else if (pointerX < edge && state.scrollX > 0f) {

                    val factor = (1f - pointerX / edge).coerceIn(0f, 1f)

                    dx = -speed * factor * dt
                }

                if (dx != 0f) {
                    state.consumeScroll(
                        Offset(
                            dx, 0f
                        )
                    )

                    val currentX = (pointerX + state.scrollX).coerceIn(
                        with(density) { selection.minX.dp.toPx() }, with(density) { selection.maxX.dp.toPx() }
                    )

                    val currentXDp = currentX / density.density

                    updateAddSelectionCurrent(currentXDp)
                }

                delay(16.milliseconds)
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .clipToBounds()
            .background(
                backgroundColor
            )
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {

            // =================================================================
            // TRACKS
            // =================================================================

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
            ) {
                TrackHeaders(
                    tracksData = tracksData,
                    trackWidth = trackWidth,
                    timeScaleHeight = timeScaleHeight,
                    rowHeight = rowHeight,
                    trackColor = trackColor,
                    trackBorderColor = trackBorderColor,
                    trackTextColor = trackTextColor,
                    state = state
                )

                // =============================================================
                // TRACKS AREA
                // =============================================================

                Box(
                    Modifier
                        .drawBehind {
                            drawTracksScale(
                                contentWidth = tracksData.contentWidth.dp.toPx().toInt(),
                                contentHeight = tracksData.contentHeight.dp.toPx().toInt(),
                                scrollX = state.scrollX,
                                dpPerSecond = dpPerSecond,
                                trackWidth = trackWidth.toPx(),
                                timeDivisionSeconds = timeDivisionSeconds,
                            )
                        }
                        .fillMaxSize()
                        .padding(
                            start = trackWidth
                        )
                        .clipToBounds()) {
                    PlaybackIndicatorScaleHitArea(
                        timeScaleHeight, state.scrollX
                    ) { offset ->
                        setPlaybackPositionFromScale(
                            localX = offset.x
                        )
                    }

                    val scrollableModifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = timeScaleHeight
                        )
                        .clipToBounds()
                        .scrollable2D(
                            state = scrollState,
                            enabled = !isDragging && editMode != TracksActionMode.ADD
                        )

                    Box(
                        scrollableModifier
                            .padding(top = 0.5.dp, bottom = 0.5.dp)
                            .drawBehind {

                                // Add selection

                                drawSelectionArea(
                                    addSelectionState.value, state, tracksData, rowHeight
                                )

                                // Dividers

                                drawTrackDividers(
                                    tracksData, separatorColor, separatorWidth, state
                                )
                            }
                        // =================================================
                        // REMOVE TAP
                        // =================================================

                        .pointerInput(
                            tracksData,
                            editMode
                        ) {

                            if (editMode != TracksActionMode.REMOVE) {
                                return@pointerInput
                            }

                            detectRemoveTap { position ->
                                val positionX = contentToTracksDp(
                                    position.x, density.density
                                )
                                val positionY = contentToTracksDp(
                                    position.y, density.density
                                )
                                val scrollX = contentToTracksDp(
                                    state.scrollX, density.density
                                )
                                val scrollY = contentToTracksDp(
                                    state.scrollY, density.density
                                )

                                removeBlockAt(positionX, positionY, scrollX, scrollY)
                            }
                        }

                        // =================================================
                        // ADD TAP
                        // =================================================

                        .pointerInput(
                            tracksData,
                            editMode
                        ) {

                            if (editMode != TracksActionMode.ADD) {
                                return@pointerInput
                            }

                            detectTapGestures { position ->

                                val positionX = contentToTracksDp(
                                    position.x, density.density
                                )
                                val positionY = contentToTracksDp(
                                    position.y, density.density
                                )

                                val scrollX = contentToTracksDp(
                                    state.scrollX, density.density
                                )
                                val scrollY = contentToTracksDp(
                                    state.scrollY, density.density
                                )

                                addBlockAt(
                                    positionX,
                                    positionY,
                                    scrollX,
                                    scrollY,
                                    newBlockWidth.value,
                                )
                            }
                        }

                        // =================================================
                        // ADD LONG-PRESS SELECTION
                        // =================================================

                        .pointerInput(
                            tracksData,
                            editMode
                        ) {

                            if (editMode != TracksActionMode.ADD) {
                                return@pointerInput
                            }

                            detectDragGesturesAfterLongPress(

                                onDragStart = { position ->
                                    val positionX = contentToTracksDp(
                                        position.x, density.density
                                    )
                                    val positionY = contentToTracksDp(
                                        position.y, density.density
                                    )

                                    val scrollX = contentToTracksDp(
                                        state.scrollX, density.density
                                    )
                                    val scrollY = contentToTracksDp(
                                        state.scrollY, density.density
                                    )

                                    startAddItemsBySelection(
                                        positionX,
                                        positionY,
                                        scrollX,
                                        scrollY
                                    )

                                    startAddSelectionAutoScroll()
                                },

                                onDrag = { change: PointerInputChange, _: Offset ->
                                    addSelectionState.value
                                        ?: return@detectDragGesturesAfterLongPress

                                    change.consume()

                                    val scrollX = contentToTracksDp(
                                        state.scrollX, density.density
                                    )
                                    val changePositionX = contentToTracksDp(
                                        change.position.x, density.density
                                    )
                                    updateAddSelectionArea(scrollX, changePositionX)
                                },

                                onDragEnd = {
                                    stopAddSelectionAutoScroll()

                                    addBlocksInSelection(
                                        newBlockWidth.value
                                    )
                                },

                                onDragCancel = {
                                    stopAddSelectionAutoScroll()
                                    clearAddSelection()
                                })
                        }

                        // =================================================
                        // EXISTING BLOCK DRAG
                        // =================================================

                        .pointerInput(
                            tracksData,
                            editMode,
                            state
                        ) {

                            if (editMode != TracksActionMode.NONE) {
                                return@pointerInput
                            }

                            detectDragGesturesAfterLongPress(

                                onDragStart = { startPosition ->
                                    val startPositionX = contentToTracksDp(
                                        startPosition.x, density.density
                                    )
                                    val startPositionY = contentToTracksDp(
                                        startPosition.y, density.density
                                    )
                                    val scrollX = contentToTracksDp(
                                        state.scrollX, density.density
                                    )
                                    val scrollY = contentToTracksDp(
                                        state.scrollY, density.density
                                    )

                                    onStartDrag(
                                        startPositionX,
                                        startPositionY,
                                        scrollX,
                                        scrollY
                                    )

                                    updateDragAndDropBlock(scrollX, scrollY)

                                    startAutoScroll()
                                }, onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                    if (moveBlockDragSession.value == null) {
                                        return@detectDragGesturesAfterLongPress
                                    }

                                    change.consume()

                                    val dragAmountX = dragAmount.x / density.density
                                    val dragAmountY = dragAmount.y / density.density

                                    val scrollX = contentToTracksDp(
                                        state.scrollX, density.density
                                    )
                                    val scrollY = contentToTracksDp(
                                        state.scrollY, density.density
                                    )

                                    updateDragSessionPosition(dragAmountX, dragAmountY)

                                    updateDragAndDropBlock(scrollX, scrollY)
                                }, onDragEnd = {
                                    stopAutoScroll()

                                    val moveBlockDragSession = moveBlockDragSession.value
                                    val moveBlockDropTarget = moveBlockDropTarget.value
                                    if (moveBlockDragSession != null && moveBlockDropTarget != null) {
                                        moveBlock(
                                            moveBlockDragSession.block.id,
                                            moveBlockDragSession.sourceRowIndex,
                                            moveBlockDropTarget.rowIndex,
                                            moveBlockDropTarget.gapIndex,
                                            moveBlockDropTarget.targetIsSourceBlockGap,
                                            moveBlockDropTarget.offset
                                        )
                                    }
                                    onEndDragBlock()
                                }, onDragCancel = {
                                    stopAutoScroll()
                                    onEndDragBlock()
                                })
                        }) {
                        TracksLazyLayout(
                            state,
                            tracksData,
                            moveBlockDragSession.value,
                            with(density) { rowHeight.toPx() }.toInt(),
                            prefetchState,
                            Modifier.fillMaxSize(),
                            content
                        )

                        val moveBlockDragSession = moveBlockDragSession.value
                        val moveBlockDropTarget = moveBlockDropTarget.value
                        if (moveBlockDragSession != null && moveBlockDropTarget != null) {
                            val targetRow =
                                tracksData.tracks.getOrNull(moveBlockDropTarget.rowIndex)

                            if (targetRow != null) {
                                val trackPosition = tracksData.positions[targetRow.id]

                                if (trackPosition != null) {
                                    Box(
                                        Modifier
                                            .offset {
                                                IntOffset(
                                                    (moveBlockDropTarget.gapStartX.dp.toPx() + moveBlockDropTarget.offset.dp.toPx() - state.scrollX).roundToInt(),
                                                    (trackPosition.top.dp.toPx() - state.scrollY).roundToInt()
                                                )
                                            }
                                            .width(moveBlockDragSession.block.width.dp)
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
                            }
                        }

                        if (moveBlockDragSession != null) {

                            Box(
                                Modifier
                                    .offset {

                                        IntOffset(
                                            (moveBlockDragSession.pointerPositionX.dp.toPx() - moveBlockDragSession.touchOffsetX.dp.toPx()).roundToInt(),
                                            (moveBlockDragSession.pointerPositionY.dp.toPx() - moveBlockDragSession.touchOffsetY.dp.toPx()).roundToInt()
                                        )
                                    }
                                    .width(moveBlockDragSession.block.width.dp)
                                    .height(rowHeight)
                                    .background(
                                        with(moveBlockDragSession.block.color) {
                                            Color(r, g, b)
                                        }, RoundedCornerShape(
                                            8.dp
                                        )
                                    )
                                    .border(
                                        1.dp, Color.White.copy(.45f), RoundedCornerShape(
                                            8.dp
                                        )
                                    ), Alignment.CenterStart
                            ) {
                                Text(
                                    text = moveBlockDragSession.block.text,
                                    maxLines = 1,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }

                    PlaybackIndicator(
                        contentHeight = with(density) { tracksData.contentHeight.dp.toPx() }.toInt(),
                        playbackPositionPx = playbackPositionPx,
                        scrollX = state.scrollX,
                        scrollY = state.scrollY,
                        timeScaleHeight = timeScaleHeight,
                        indicatorColor = playbackIndicatorColor,
                        dotColor = playbackDotColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // =================================================================
            // PANEL
            // =================================================================

            ControlPanel(mode = editMode, onModeChange = { newMode ->
                if (newMode == TracksActionMode.ADD || newMode == TracksActionMode.REMOVE) {
                    stopPlayback()
                }

                stopAddSelectionAutoScroll()
                clearAddSelection()
                editMode = newMode
            }, onPlay = {
                togglePlayback()
            })
        }
    }


}

// ============================================================================
// DEMO
// =========================
// ===================================================

@Composable
fun LazyTracksDemo() {
    val tracksViewModel = hiltViewModel<TracksListViewModel>()

    val tracksData = tracksViewModel.tracksDataState.collectAsState()
    val moveBlockDragSessionState = tracksViewModel.moveBlockDragSessionState.collectAsState()
    val moveBlockDropTargetState = tracksViewModel.moveBlockDropTargetState.collectAsState()
    val addSelectionState = tracksViewModel.addSelectionState.collectAsState()
    val state = rememberLazyTracksState()

    LazyTracks(
        tracksData = tracksData.value,
        moveBlockDragSession = moveBlockDragSessionState,
        moveBlockDropTarget = moveBlockDropTargetState,
        addSelectionState = addSelectionState,
        addBlockAt = tracksViewModel::addBlockAt,
        removeBlockAt = tracksViewModel::removeBlockAt,
        moveBlock = tracksViewModel::moveBlock,
        onStartDrag = tracksViewModel::onStartDragBlock,
        updateDragAndDropBlock = tracksViewModel::updateDragAndDropBlock,
        updateDragSessionPosition = tracksViewModel::updateDragBlockSessionPosition,
        onEndDragBlock = tracksViewModel::onEndDragBlock,
        startAddItemsBySelection = tracksViewModel::startAddItemsBySelection,
        updateAddSelectionArea = tracksViewModel::updateAddSelectionArea,
        updateAddSelectionCurrent = tracksViewModel::updateAddSelectionCurrent,
        addBlocksInSelection = tracksViewModel::addBlocksInSelection,
        clearAddSelection = tracksViewModel::clearAddSelection,
        state = state,
        modifier = Modifier.fillMaxSize(),
        dpPerSecond = 20.dp,
        timeDivisionSeconds = 5,
        trackWidth = 100.dp,
        playbackSpeed = 300.dp,
        newBlockWidth = 150.dp,
        rowHeight = 72.dp,
        timeScaleHeight = 56.dp,
        trackColor = Color(0xFFE0E0E0),
        trackBorderColor = Color(0xFFC8C8C8),
        trackTextColor = Color(0xFF444444),
        separatorColor = Color(0xFF000000),
        separatorWidth = 1.dp,
        playbackIndicatorColor = Color(0xFF1976D2),
        playbackDotColor = Color(0xFFE53935),
        backgroundColor = Color(0xFFF5F5F5),
        dropIndicatorColor = Color(0x556750A4),
        dropIndicatorBorderColor = Color(0xFF6750A4),
        autoScrollEdge = 72.dp,
        autoScrollSpeed = 1000.dp,
    ) { item ->
        when (item) {
            is TracksItem.Block -> {
                Block(item)
            }

            is TracksItem.Gap -> {
                Gap()
            }
        }
    }

}

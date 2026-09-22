package com.goldenankh.purewave.ui.components.tracklist.lazy.scrollstate

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

@Stable
class LazyTracksState internal constructor() {

    private var scrollXState by mutableFloatStateOf(0f)
    private var scrollYState by mutableFloatStateOf(0f)
    private var maxScrollXState by mutableFloatStateOf(0f)
    private var maxScrollYState by mutableFloatStateOf(0f)
    private var viewportWidthState by mutableFloatStateOf(0f)
    private var viewportHeightState by mutableFloatStateOf(0f)

    val scrollX: Float
        get() = scrollXState

    val scrollY: Float
        get() = scrollYState

    val maxScrollX: Float
        get() = maxScrollXState

    val maxScrollY: Float
        get() = maxScrollYState

    val viewportWidth: Float
        get() = viewportWidthState

    val viewportHeight: Float
        get() = viewportHeightState

    internal fun update(
        viewportWidth: Int,
        viewportHeight: Int,
        contentWidth: Int,
        contentHeight: Int
    ) {
        viewportWidthState = viewportWidth.toFloat()
        viewportHeightState = viewportHeight.toFloat()

        maxScrollXState = (contentWidth - viewportWidth)
                .coerceAtLeast(0)
                .toFloat()

        maxScrollYState = (contentHeight - viewportHeight)
                .coerceAtLeast(0)
                .toFloat()

        scrollXState = scrollXState.coerceIn(0f, maxScrollXState)
        scrollYState = scrollYState.coerceIn(0f, maxScrollYState)
    }

    internal fun consumeScroll(
        delta: Offset
    ): Offset {
        val oldX = scrollXState
        val oldY = scrollYState

        val newX = (oldX + delta.x)
                .coerceIn(0f, maxScrollXState)

        val newY = (oldY + delta.y)
                .coerceIn(0f, maxScrollYState)

        scrollXState = newX

        scrollYState = newY

        return Offset(
            newX - oldX,
            newY - oldY
        )
    }
}
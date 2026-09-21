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

package com.goldenankh.purewave.ui.components.tracklist.lazy

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
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.TracksItem

class TracksItemProvider(
    private val session: DragSession?,
    private val flatTracks: List<TracksItem>,
    private val content: @Composable (TracksItem) -> Unit
) : LazyLayoutItemProvider {

    override val itemCount: Int
        get() = flatTracks.size

    @Composable
    override fun Item(index: Int, key: Any) {

        val item = flatTracks.getOrNull(index) ?: return
        val draggedId = session?.block?.id

        Box(
            modifier = Modifier.alpha(
                if (item.id == draggedId) {
                    0.5f
                } else {
                    1f
                }
            )
        ) {
            content(item)
        }
    }

    override fun getKey(index: Int): Any =
        flatTracks.getOrNull(index)?.id ?: "tracks-item-$index"

    override fun getIndex(key: Any): Int {

        if (key !is String) return -1

        for (index in flatTracks.indices) {
            if (flatTracks.getOrNull(index)?.id == key) {
                return index
            }
        }

        return -1
    }

    override fun getContentType(index: Int): Any? =
        when (flatTracks.getOrNull(index)) {
            is TracksItem.Block -> "block"
            is TracksItem.Gap -> "gap"
            null -> null
        }


}
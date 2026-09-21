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

import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutPrefetchState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.purewave.ui.components.tracklist.lazy.extensions.measureTracks

@Composable
fun TracksLazyLayout(
    state: LazyTracksState,
    tracksData: TracksData,
    session: DragSession?,
    rowHeightPx: Int,
    prefetchState: LazyLayoutPrefetchState? = null,
    modifier: Modifier,
    content: @Composable (TracksItem) -> Unit
) {
    val flatTracks = tracksData.tracks.flatMap { it.items }

    val itemProvider =
        remember(
            session,
            flatTracks,
            content
        ) {
            TracksItemProvider(
                session,
                flatTracks,
                content
            )
        }

    LazyLayout(
        itemProvider = {
            itemProvider
        },
        modifier = modifier,
        prefetchState = prefetchState
    ) {

        measureTracks(
            state,
            tracksData,
            flatTracks,
            rowHeightPx,
            it
        )
    }
}
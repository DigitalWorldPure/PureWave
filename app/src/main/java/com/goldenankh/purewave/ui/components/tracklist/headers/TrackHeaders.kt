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

package com.goldenankh.purewave.ui.components.tracklist.headers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.TracksData
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.tracklist.lazy.scrollstate.LazyTracksState
import com.goldenankh.purewave.ui.components.tracklist.preview.TrackParametersPreviewProvider
import kotlin.math.roundToInt

@Composable
fun TrackHeaders(
    tracksData: TracksData,
    trackWidth: Dp,
    timeScaleHeight: Dp,
    rowHeight: Dp,
    trackColor: Color,
    trackBorderColor: Color,
    trackTextColor: Color,
    state: LazyTracksState
) {
    Box(
        Modifier
            .width(trackWidth)
            .fillMaxHeight()
            .padding(
                top = timeScaleHeight
            )
            .clipToBounds()
    ) {
        tracksData.tracks.forEachIndexed { index, track ->
            val trackPosition = tracksData.positions[track.id]

            trackPosition?.let {
                Box(
                    Modifier
                        .offset {
                            IntOffset(
                                0,
                                it.top.dp.toPx().toInt() - state.scrollY.roundToInt()
                            )
                        }
                        .width(trackWidth)
                        .height(rowHeight)
                        .background(
                            trackColor
                        )
                        .border(
                            1.dp,
                            trackBorderColor
                        ),
                    Alignment.Center
                ) {

                    Text(
                        text = stringResource(R.string.default_track_name, (index + 1).toString()),
                        color = trackTextColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TrackHeadersPreview(
    @PreviewParameter(TrackParametersPreviewProvider::class)
    tracksData: TracksData
) {
    TrackHeaders(
        tracksData = tracksData,
        trackWidth = 100.dp,
        timeScaleHeight = 56.dp,
        rowHeight = 72.dp,
        trackColor = Color(0xFFE0E0E0),
        trackBorderColor = Color(0xFFC8C8C8),
        trackTextColor = Color(0xFF444444),
        state = LazyTracksState()
    )
}


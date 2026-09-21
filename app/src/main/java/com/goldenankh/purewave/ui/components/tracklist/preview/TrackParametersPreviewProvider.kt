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

package com.goldenankh.purewave.ui.components.tracklist.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.goldenankh.domain.config.TracksSettings
import com.goldenankh.domain.model.ItemPosition
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TrackPosition
import com.goldenankh.domain.model.TrackRow
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.common.RGBColor

class TrackParametersPreviewProvider : PreviewParameterProvider<TracksData> {
    override val values: Sequence<TracksData> = sequenceOf(
        // Three row example
        calculateTracksData(
            tracks = listOf(
                TrackRow(
                    id = "row-0", items = listOf(

                        TracksItem.Block(
                            "block-0", "Bass", 120f, RGBColor(103, 80, 164)
                        ),

                        TracksItem.Gap(
                            "gap-0", 220f
                        ),

                        TracksItem.Block(
                            "block-1", "Vocal", 160f, RGBColor(0, 106, 106)
                        ),

                        TracksItem.Gap(
                            "gap-1", 700f
                        )
                    )
                ),

                TrackRow(
                    id = "row-1", items = listOf(

                        TracksItem.Gap(
                            "gap-2", 180f
                        ),

                        TracksItem.Block(
                            "block-2", "Melody", 140f, RGBColor(0, 106, 106)
                        ),

                        TracksItem.Gap(
                            "gap-3", 500f
                        ),

                        TracksItem.Block(
                            "block-3", "Drum", 180f, RGBColor(64, 94, 145)
                        ),

                        TracksItem.Gap(
                            "gap-4", 800f
                        )
                    )
                ),
            ),
        ),

        calculateTracksData(
            tracks = listOf(
                TrackRow(
                    id = "row-2", items = listOf(

                        TracksItem.Gap(
                            "gap-5", 400f
                        ),

                        TracksItem.Block(
                            "block-4", "Vocal interlude", 220f, RGBColor(122, 78, 171)
                        ),

                        TracksItem.Gap(
                            "gap-6", 1000f
                        )
                    )
                )
            )
        ),

        // One row with one example
        calculateTracksData(
            tracks = listOf(
                TrackRow(
                    id = "row-2", items = listOf(
                        TracksItem.Block(
                            "block-4", "Vocal interlude", 320f, RGBColor(122, 78, 171)
                        )
                    )
                ),
            )
        )
    )



    private fun calculateTracksData(tracks: List<TrackRow>): TracksData {
        return TracksData(
            tracks,
            calculateTrackPositions(tracks),
            calculateMaxTracksWidth(tracks),
            calculateMaxTracksHeight(tracks)
        )
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

    private fun calculateMaxTracksWidth(
        tracks: List<TrackRow>
    ) = tracks.maxOfOrNull { track ->
        track.items.sumOf { it.width.toInt() }
    } ?: 0

    private fun calculateMaxTracksHeight(
        tracks: List<TrackRow>
    ): Int = tracks.size * TracksSettings.TRACK_HEIGHT
}
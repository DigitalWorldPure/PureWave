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

import com.goldenankh.domain.model.TrackRow
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.common.RGBColor

// TODO: Need to replace the demo data with production data
val TracksListViewModel.initialTracksData
    get() = listOf(
        TrackRow(
            id = "row-0",
            items = listOf(

                TracksItem.Block(
                    "block-0",
                    "Bass",
                    120f,
                    RGBColor(103, 80, 164)
                ),

                TracksItem.Gap(
                    "gap-0",
                    220f
                ),

                TracksItem.Block(
                    "block-1",
                    "Vocal",
                    160f,
                    RGBColor(0, 106, 106)
                ),

                TracksItem.Gap(
                    "gap-1",
                    700f
                )
            )
        ),

        TrackRow(
            id = "row-1",
            items = listOf(

                TracksItem.Gap(
                    "gap-2",
                    180f
                ),

                TracksItem.Block(
                    "block-2",
                    "Melody",
                    140f,
                    RGBColor(0, 106, 106)
                ),

                TracksItem.Gap(
                    "gap-3",
                    500f
                ),

                TracksItem.Block(
                    "block-3",
                    "Drum",
                    180f,
                    RGBColor(64, 94, 145)
                ),

                TracksItem.Gap(
                    "gap-4",
                    800f
                )
            )
        ),

        TrackRow(
            id = "row-2",
            items = listOf(

                TracksItem.Gap(
                    "gap-5",
                    400f
                ),

                TracksItem.Block(
                    "block-4",
                    "Vocal interlude",
                    220f,
                    RGBColor(122, 78, 171)
                ),

                TracksItem.Gap(
                    "gap-6",
                    1000f
                )
            )
        ),

        TrackRow(
            id = "row-3",
            items = listOf(

                TracksItem.Block(
                    "block-5",
                    "Hat",
                    190f,
                    RGBColor(0, 133, 119)
                ),

                TracksItem.Gap(
                    "gap-7",
                    1200f
                )
            )
        ),
        TrackRow(
            id = "row-4",
            items = listOf()
        ),
        TrackRow(
            id = "row-5",
            items = listOf()
        ),
        TrackRow(
            id = "row-6",
            items = listOf()
        ),
        TrackRow(
            id = "row-7",
            items = listOf()
        ),
        TrackRow(
            id = "row-8",
            items = listOf()
        ),
        TrackRow(
            id = "row-9",
            items = listOf()
        ),
        TrackRow(
            id = "row-10",
            items = listOf()
        ),
        TrackRow(
            id = "row-11",
            items = listOf()
        )
    )
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

package com.goldenankh.purewave.ui.components.tracklist.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.common.RGBColor

/**
 * Block containing audio information (e.g., a sample)
 *
 * @param item display settings
 *
 */
@Composable
fun Block(
    item: TracksItem.Block,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(
                with(item.color) {
                    Color(r, g, b)
                },
                RoundedCornerShape(
                    8.dp
                )
            )
            .border(
                1.dp,
                Color.White.copy(
                    .25f
                ),
                RoundedCornerShape(
                    8.dp
                )
            ),
        Alignment.CenterStart
    ) {

        Text(
            item.text,
            maxLines = 1,
            color = Color.White,
            modifier =
                Modifier.padding(
                    horizontal = 12.dp
                )
        )
    }
}

@Preview
@Composable
private fun BlockPreview() {
    Block(
        item = TracksItem.Block("block-1", "Bass", 200f, RGBColor(0, 205, 0)),
        modifier = Modifier.height(72.dp)
    )
}
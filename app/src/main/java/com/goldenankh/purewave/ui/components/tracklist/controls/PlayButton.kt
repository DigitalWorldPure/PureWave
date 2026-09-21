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

package com.goldenankh.purewave.ui.components.tracklist.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PlayButton(
    isPlaying: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .width(44.dp)
            .height(36.dp)
            .background(
                if (selected) {
                    Color(0xFF3367D6)
                        .copy(alpha = .14f)
                } else {
                    Color.White.copy(.95f)
                },
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (selected) {
                    Color(0xFF3367D6)
                } else {
                    Color(0xFFCCCCCC)
                },
                RoundedCornerShape(8.dp)
            )
            .pointerInput(
                isPlaying,
                selected
            ) {
                detectTapGestures {
                    onClick()
                }
            },
        Alignment.Center
    ) {

        Text(
            if (isPlaying) "■" else "▶",
            color =
                if (isPlaying) {
                    Color(0xFFCC3333)
                } else {
                    Color(0xFF3367D6)
                }
        )
    }
}

@Preview
@Composable
fun PlayButtonPreview() {
    PlayButton(
        isPlaying = false,
        selected = false,
        onClick = {}
    )
}
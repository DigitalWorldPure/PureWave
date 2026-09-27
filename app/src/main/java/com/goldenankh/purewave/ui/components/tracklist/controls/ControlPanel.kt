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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.tracklist.TracksActionMode

/**
 * Editing and playback panel
 *
 * @param mode Active mode
 * @param selectedButtonColor Color for the active button
 * @param onModeChange Called when the mode changes
 * @param onPlay Called when playback starts or ends
 *
 */
@Composable
fun ControlPanel(
    mode: TracksActionMode,
    selectedButtonColor: Color,
    onModeChange: (TracksActionMode) -> Unit,
    onPlay: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .border(
                    1.dp,
                    Color(0xFFCCCCCC)
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        ToggleModeButton(
            selected = mode == TracksActionMode.PLAY,
            selectedColor = selectedButtonColor,
            imageResId = R.drawable.ic_stop,
            selectedImageResId = R.drawable.ic_play,
            onClick = onPlay
        )

        Spacer(Modifier.width(8.dp))

        ToggleModeButton(
            selected = mode == TracksActionMode.ADD,
            selectedColor = selectedButtonColor,
            imageResId = R.drawable.ic_plus
        ) {
            onModeChange(
                if (mode == TracksActionMode.ADD) {
                    TracksActionMode.NONE
                } else {
                    TracksActionMode.ADD
                }
            )
        }

        Spacer(
            Modifier.width(8.dp)
        )

        ToggleModeButton(
            selected = mode == TracksActionMode.REMOVE,
            selectedColor = selectedButtonColor,
            imageResId = R.drawable.ic_minus
        ) {
            onModeChange(
                if (mode == TracksActionMode.REMOVE) {
                    TracksActionMode.NONE
                } else {
                    TracksActionMode.REMOVE
                }
            )
        }
    }
}

@Preview
@Composable
private fun ControlPanelPreview() {
    ControlPanel(
        mode = TracksActionMode.ADD,
        onModeChange = {},
        selectedButtonColor = Color(0xFFCECECE),
        onPlay = {})
}
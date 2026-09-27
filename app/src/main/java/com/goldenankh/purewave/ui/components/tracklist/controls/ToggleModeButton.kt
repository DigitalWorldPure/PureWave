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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goldenankh.purewave.R

/**
 * Button for switching states
 *
 * @param selected switched to this button
 * @param selectedColor background button color if switched to this button
 * @param imageResId Image if button is not selected
 * @param selectedImageResId Image if button is selected
 * @param onClick called when this button is clicked
 *
 */
@Composable
fun ToggleModeButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    selectedColor: Color,
    @DrawableRes imageResId: Int,
    @DrawableRes selectedImageResId: Int? = null,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(
                if (selected) {
                    selectedColor
                } else {
                    Color.Transparent
                },
                RoundedCornerShape(14.dp)
            )
    ) {
        Image(
            painter = painterResource(
                if (selected)
                    imageResId
                else
                    selectedImageResId ?: imageResId
            ),
            contentDescription = null,
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
        )
    }
}

@Preview
@Composable
fun ToggleModeButtonPreview() {
    ToggleModeButton(
        selected = false,
        selectedColor = Color.Gray,
        imageResId = R.drawable.ic_plus,
        onClick = {}
    )
}

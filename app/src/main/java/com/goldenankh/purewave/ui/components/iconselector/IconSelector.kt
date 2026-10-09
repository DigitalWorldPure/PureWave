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

package com.goldenankh.purewave.ui.components.iconselector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goldenankh.domain.model.sample.SampleIcon

@Composable
fun IconSelector(
    selectedIcon: SampleIcon?,
    onIconSelected: (SampleIcon?) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        OutlinedButton(
            onClick = {
                expanded = true
            },
            shape = RectangleShape,
            modifier = Modifier.size(
                width = 320.dp,
                height = 245.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (selectedIcon != null) {
                    Icon(
                        imageVector = selectedIcon.toImageVector(),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp)
                    )
                }

                Spacer(
                    Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("Без иконки")
                },
                onClick = {
                    onIconSelected(null)
                    expanded = false
                }
            )

            SampleIcon.entries.forEach { icon ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector =
                                    icon.toImageVector(),
                                contentDescription = null
                            )

                            Spacer(
                                Modifier.width(12.dp)
                            )

                            Text(icon.id)
                        }
                    },
                    onClick = {
                        onIconSelected(icon)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun IconSelectorPreview() {
    IconSelector(
        selectedIcon = SampleIcon.PIANO,
        onIconSelected = { _ -> }
    )
}
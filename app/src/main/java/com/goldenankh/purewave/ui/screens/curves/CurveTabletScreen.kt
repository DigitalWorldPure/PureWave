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

package com.goldenankh.purewave.ui.screens.curves

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.curves.curveviewer.CurveViewer
import com.goldenankh.purewave.viewmodel.curves.CurveViewModel

@Composable
fun CurveTabletScreen(
    curveId: Long?,
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit,
    onEditCurve: (Long?) -> Unit,
    viewModel: CurveViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isSmallHeight by remember { derivedStateOf { windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::setName,
            placeholder = {
                Text(stringResource(R.string.name))
            },
            isError = state.nameError,
            supportingText = {
                if (state.nameError) {
                    Text(stringResource(R.string.error_name_not_specified))
                }
            },
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.curve_parameter),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(140.dp)
            )

            CurveViewer(
                modifier = Modifier
                    .weight(1f)
                    .height(
                        if (isSmallHeight) {
                            80.dp
                        } else {
                            245.dp
                        }
                    ),
                onEditCurve = {
                    onEditCurve(curveId)
                }
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBD0010)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.cancel_action))
            }

            Button(
                onClick = viewModel::save,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1111EE)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.save_action))
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    if (state.saveCompleted) {
        onBack()
    }
}
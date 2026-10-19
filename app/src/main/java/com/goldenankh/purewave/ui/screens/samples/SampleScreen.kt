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

package com.goldenankh.purewave.ui.screens.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.colorpicker.ColorPickerDialog
import com.goldenankh.purewave.ui.components.iconselector.IconSelector
import com.goldenankh.purewave.ui.routing.Routes.EMPTY
import com.goldenankh.purewave.ui.theme.PureWaveTheme
import com.goldenankh.purewave.viewmodel.samples.SampleViewModel

@Composable
fun SampleScreen(
    sampleId: Long?,
    onBack: () -> Unit,
    onEditProgram: (Long?) -> Unit,
    windowSizeClass: WindowSizeClass,
    viewModel: SampleViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
        ) {
            val (title, nameField, iconLabel, iconSelector, colorLabel, colorBox,
                programLabel, programButton, durationLabel, durationText, cancelButton,
                saveButton) = createRefs()

            // All left-side labels share one end barrier. Every control to their right
            // starts 8 dp after the widest label, regardless of localized text length.
            val labelsEnd = createEndBarrier(
                iconLabel,
                colorLabel,
                programLabel,
                durationLabel
            )

            Text(
                text = stringResource(R.string.sample_header),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                placeholder = { Text(stringResource(R.string.name)) },
                isError = state.nameError,
                supportingText = {
                    if (state.nameError)
                        Text(stringResource(R.string.error_name_not_specified))
                },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.constrainAs(nameField) {
                    top.linkTo(title.bottom, margin = 28.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = stringResource(R.string.icon_parameter),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(iconLabel) {
                    start.linkTo(parent.start)
                    top.linkTo(nameField.bottom, margin = 28.dp)
                    bottom.linkTo(iconSelector.bottom)
                }
            )

            Box(
                modifier = Modifier.constrainAs(iconSelector) {
                    start.linkTo(labelsEnd, margin = 8.dp)
                    end.linkTo(parent.end)
                    top.linkTo(nameField.bottom, margin = 28.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                IconSelector(
                    modifier = Modifier.size(
                        width = 320.dp,
                        height = 245.dp
                    ),
                    selectedIcon = state.icon,
                    onIconSelected = viewModel::setIcon
                )
            }

            Text(
                text = stringResource(R.string.color_parameter),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(colorLabel) {
                    start.linkTo(parent.start)
                    top.linkTo(iconSelector.bottom, margin = 24.dp)
                    bottom.linkTo(colorBox.bottom)
                }
            )

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .background(with(state.color) { Color(r, g, b) })
                    .clickable {
                        navController.navigate(state.color)
                    }
                    .constrainAs(colorBox) {
                        start.linkTo(labelsEnd, margin = 8.dp)
                        end.linkTo(parent.end)
                        top.linkTo(iconSelector.bottom, margin = 24.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            Text(
                text = stringResource(R.string.program_parameter),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(programLabel) {
                    start.linkTo(parent.start)
                    top.linkTo(colorBox.bottom, margin = 24.dp)
                    bottom.linkTo(programButton.bottom)
                }
            )

            Button(
                onClick = { onEditProgram(sampleId) },
                modifier = Modifier.constrainAs(programButton) {
                    start.linkTo(labelsEnd, margin = 8.dp)
                    top.linkTo(colorBox.bottom, margin = 24.dp)
                }
            ) {
                Text(stringResource(R.string.edit_action))
            }

            Text(
                text = stringResource(R.string.duration_parameter),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(durationLabel) {
                    start.linkTo(parent.start)
                    top.linkTo(programButton.bottom, margin = 24.dp)
                    bottom.linkTo(durationText.bottom)
                }
            )

            Text(
                text = stringResource(R.string.duration_in_seconds, state.durationSeconds),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(durationText) {
                    start.linkTo(labelsEnd, margin = 8.dp)
                    top.linkTo(programButton.bottom, margin = 24.dp)
                }
            )

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBD0010)),
                modifier = Modifier.constrainAs(cancelButton) {
                    start.linkTo(parent.start)
                    end.linkTo(saveButton.start, margin = 12.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(stringResource(R.string.cancel_action))
            }

            Button(
                onClick = viewModel::save,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWaveTheme.colorScheme.primary
                ),
                modifier = Modifier.constrainAs(saveButton) {
                    start.linkTo(cancelButton.end, margin = 12.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(stringResource(R.string.save_action))
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = EMPTY,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(
            route = EMPTY
        ) {}
        dialog<RGBColor> { backStackEntry ->
            val initialColor = backStackEntry.toRoute<RGBColor>()
            ColorPickerDialog(
                initialColor = with(initialColor) { Color(r, g, b) },
                windowSizeClass = windowSizeClass,
                onDismiss = { navController.popBackStack() },
                onColorSelected = {
                    viewModel.setColor(RGBColor((255 * it.red).toInt(), (255 * it.green).toInt(), (255 * it.blue).toInt()))
                }
            )
        }
    }

    if(state.saveCompleted) {
        onBack()
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun SampleScreenPreview() {
    SampleScreen(
        null,
        {},
        {},
        WindowSizeClass.calculateFromSize(DpSize(320.dp, 500.dp))
    )
}
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.dialog.ConfirmDeleteDialog
import com.goldenankh.purewave.ui.components.list.CommonList
import com.goldenankh.purewave.ui.components.list.CommonListItem
import com.goldenankh.purewave.ui.components.list.MasterDetailScreen
import com.goldenankh.purewave.ui.routing.Routes.CONFIRM_REMOVE
import com.goldenankh.purewave.ui.routing.Routes.EMPTY
import com.goldenankh.purewave.ui.routing.Routes.SAMPLE
import com.goldenankh.purewave.ui.routing.Routes.SAMPLE_EDIT_PROGRAM
import com.goldenankh.purewave.viewmodel.samples.SamplesViewModel

@Composable
fun SamplesTabletScreen(
    initialSelectedId: Long? = null,
    initiallyEditing: Boolean = false,
    onInitialDetailBack: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    windowSizeClass: WindowSizeClass,
    viewModel: SamplesViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable(initialSelectedId) { mutableStateOf(initialSelectedId) }
    var editMode by rememberSaveable(initialSelectedId, initiallyEditing) { mutableStateOf(initiallyEditing) }
    var programMode by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableLongStateOf(0L) }

    val selectedSample = state.samples.firstOrNull { it.id == selectedId }

    MasterDetailScreen(
        listContent = {
            CommonList(
                title = stringResource(R.string.samples_header),
                items = state.samples,
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                onMenuClick = onMenuClick,
                onAddClick = {
                    selectedId = null
                    editMode = true
                    programMode = false
                },
                modifier = Modifier.fillMaxSize()
            ) { sample ->
                CommonListItem(
                    title = sample.name,
                    onClick = {
                        selectedId = sample.id
                        editMode = true
                        programMode = false
                    },
                    onEditClick = {
                        selectedId = sample.id
                        editMode = true
                        programMode = false
                    },
                    onDeleteClick = { deleteId = sample.id }
                )
            }
        },
        detailContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                NavHost(
                    navController = navController,
                    startDestination = EMPTY,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(
                        route = EMPTY
                    ) {}
                    dialog(
                        route = "${CONFIRM_REMOVE}?id={id}&name={name}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.LongType
                                defaultValue = -1L
                            },
                            navArgument("name") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) { entry ->
                        val id = entry.arguments?.getLong("id") ?: -1L
                        val name = entry.arguments?.getString("name") ?: stringResource(R.string.sample_header)
                        ConfirmDeleteDialog(
                            title = name ,
                            onConfirm = {
                                viewModel.delete(id)
                                if (selectedId == id) {
                                    selectedId = null
                                    editMode = false
                                    programMode = false
                                }
                                deleteId = 0L
                            },
                            onDismiss = { deleteId = 0L }
                        )
                    }
                    composable(
                        route = "${SAMPLE_EDIT_PROGRAM}?id={id}&haveSample={haveSample}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.LongType
                                defaultValue = -1L
                            },
                            navArgument("haveSample") {
                                type = NavType.BoolType
                                defaultValue = false
                            }
                        )
                    ) { entry ->
                        val id = entry.arguments?.getLong("id") ?: -1L
                        val haveSample = entry.arguments?.getBoolean("haveSample") ?: false
                        SampleEditProgramScreen(
                            sampleId = id,
                            onBack = {
                                if (haveSample) {
                                    selectedId = null
                                }
                                else if (onInitialDetailBack != null && initiallyEditing) {
                                    onInitialDetailBack()
                                } else {
                                    programMode = false
                                    editMode = true
                                }
                            }
                        )
                    }

                    composable(
                        route = "${SAMPLE}?id={id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { entry ->
                        val id = entry.arguments?.getLong("id") ?: -1L
                        SampleScreen(
                            sampleId = id,
                            onBack = {
                                editMode = false
                                selectedId = null
                            },
                            onEditProgram = {
                                programMode = true
                            },
                            windowSizeClass = windowSizeClass
                        )
                    }
                }
            }
        }
    )

    if (programMode) {
        navController.navigate(
            "${SAMPLE_EDIT_PROGRAM}?id=${selectedId ?: -1L}"
        )
    } else if (editMode) {
        navController.navigate(
            "${SAMPLE}?id=$selectedId"
        )
    } else if (selectedSample != null) {
        navController.navigate(
            "${SAMPLE_EDIT_PROGRAM}?id=${selectedSample.id}&haveSample=true"
        )
    }
    else {
        navController.navigate(EMPTY)
    }

    if (deleteId != 0L) {
        val sample = state.samples.firstOrNull { it.id == deleteId }
        navController.navigate(
            "${CONFIRM_REMOVE}?id=${deleteId}&name=${sample?.name}"
        )
    }
}

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.dialog.ConfirmDeleteDialog
import com.goldenankh.purewave.ui.components.list.CommonList
import com.goldenankh.purewave.ui.components.list.CommonListItem
import com.goldenankh.purewave.ui.components.list.MasterDetailScreen
import com.goldenankh.purewave.viewmodel.samples.SamplesViewModel

@Composable
fun SamplesTabletScreen(
    initialSelectedId: Long? = null,
    initiallyEditing: Boolean = false,
    onInitialDetailBack: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onDialogClick: (RGBColor) -> Unit,
    viewModel: SamplesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedId by remember(initialSelectedId) { mutableStateOf(initialSelectedId) }
    var editMode by remember(initialSelectedId, initiallyEditing) { mutableStateOf(initiallyEditing) }
    var programMode by remember { mutableStateOf(false) }
    var deleteId by remember { mutableLongStateOf(0L) }

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
                if (programMode) {
                    SampleEditProgramScreen(
                        sampleId = selectedId,
                        onBack = {
                            if (onInitialDetailBack != null && initiallyEditing) {
                                onInitialDetailBack()
                            } else {
                                programMode = false
                                editMode = true
                            }
                        }
                    )
                } else if (editMode) {
                    SampleScreen(
                        sampleId = selectedId,
                        onBack = {
                            editMode = false
                            selectedId = null
                        },
                        onEditProgram = {
                            programMode = true
                        },
                        onDialogClick = onDialogClick
                    )
                } else if (selectedSample != null) {
                    SampleEditProgramScreen(
                        sampleId = selectedSample.id,
                        onBack = { selectedId = null }
                    )
                }
            }
        }
    )

    if (deleteId != 0L) {
        val sample = state.samples.firstOrNull { it.id == deleteId }
        ConfirmDeleteDialog(
            title = sample?.name ?: "Сэмпл",
            onConfirm = {
                viewModel.delete(deleteId)
                if (selectedId == deleteId) {
                    selectedId = null
                    editMode = false
                    programMode = false
                }
                deleteId = 0L
            },
            onDismiss = { deleteId = 0L }
        )
    }
}

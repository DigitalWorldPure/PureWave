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
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.dialog.ConfirmDeleteDialog
import com.goldenankh.purewave.ui.components.list.CommonList
import com.goldenankh.purewave.ui.components.list.CommonListItem
import com.goldenankh.purewave.ui.components.list.MasterDetailScreen
import com.goldenankh.purewave.viewmodel.curves.CurvesViewModel

@Composable
fun CurvesTabletScreen(
    initialSelectedId: Long? = null,
    initiallyEditing: Boolean = false,
    onInitialDetailBack: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onEditCurve: (Long?) -> Unit,
    viewModel: CurvesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedId by remember(initialSelectedId) { mutableStateOf(initialSelectedId) }
    var editMode by remember(initialSelectedId, initiallyEditing) { mutableStateOf(initiallyEditing) }
    var deleteId by remember { mutableLongStateOf(0L) }

    val selectedCurve = state.curves.firstOrNull { it.id == selectedId }

    MasterDetailScreen(
        listContent = {
            CommonList(
                title = stringResource(R.string.curves_header),
                items = state.curves,
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                onMenuClick = onMenuClick,
                onAddClick = {
                    selectedId = null
                    editMode = true
                },
                modifier = Modifier.fillMaxSize()
            ) { curve ->
                CommonListItem(
                    title = curve.name,
                    onClick = {
                        selectedId = curve.id
                        editMode = false
                    },
                    onEditClick = {
                        selectedId = curve.id
                        editMode = true
                    },
                    onDeleteClick = {
                        deleteId = curve.id
                    }
                )
            }
        },
        detailContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (editMode) {
                    CurveScreen(
                        curveId = selectedId,
                        onBack = {
                            if (onInitialDetailBack != null && initiallyEditing) {
                                onInitialDetailBack()
                            } else {
                                editMode = false
                                selectedId = null
                            }
                        },
                        onEditCurve = onEditCurve
                    )
                } else if (selectedCurve != null) {
                    CurveEditorScreen(
                        curveId = selectedCurve.id,
                        onBack = {
                            selectedId = null
                        }
                    )
                }
            }
        }
    )

    if (deleteId != 0L) {
        val curve = state.curves.firstOrNull { it.id == deleteId }
        ConfirmDeleteDialog(
            title = curve?.name ?: stringResource(R.string.curve),
            onConfirm = {
                viewModel.delete(deleteId)
                if (selectedId == deleteId) {
                    selectedId = null
                }
                deleteId = 0L
            },
            onDismiss = { deleteId = 0L }
        )
    }
}
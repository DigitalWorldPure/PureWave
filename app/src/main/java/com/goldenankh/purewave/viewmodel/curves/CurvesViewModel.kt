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

package com.goldenankh.purewave.viewmodel.curves

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldenankh.domain.usecases.curves.DeleteCurveUseCase
import com.goldenankh.domain.usecases.curves.ObserveCurvesUseCase
import com.goldenankh.purewave.ui.components.uistate.curves.CurvesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurvesViewModel @Inject constructor(
    observeCurves: ObserveCurvesUseCase,
    private val deleteCurve: DeleteCurveUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val searchQuery =
        savedStateHandle.getStateFlow(
            "search",
            ""
        )

    val uiState: StateFlow<CurvesUiState> =
        combine(
            observeCurves(),
            searchQuery
        ) { curves, query ->

            val filtered = if (query.isBlank()) {
                curves
            } else {
                curves.filter {
                    it.name.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }

            CurvesUiState(
                curves = filtered,
                searchQuery = query
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                CurvesUiState()
            )

    fun setSearchQuery(value: String) {
        savedStateHandle["search"] = value
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            deleteCurve(id)
        }
    }
}
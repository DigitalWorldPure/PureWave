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
import com.goldenankh.domain.model.curve.Curve
import com.goldenankh.domain.usecases.curves.GetCurveUseCase
import com.goldenankh.domain.usecases.curves.SaveCurveUseCase
import com.goldenankh.purewave.ui.components.uistate.curves.CurveUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurve: GetCurveUseCase,
    private val saveCurve: SaveCurveUseCase
) : ViewModel() {

    private val curveId: Long? =
        savedStateHandle
            .get<Long>("id")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(CurveUiState())

    val uiState =
        _uiState.asStateFlow()

    init {
        if (curveId != null) {
            loadCurve(curveId)
        }
    }

    private fun loadCurve(id: Long) {
        viewModelScope.launch {
            getCurve(id).collect { curve ->
                curve ?: return@collect

                _uiState.update {
                    it.copy(
                        name = curve.name,
                        curveData = curve.data
                    )
                }
            }
        }
    }

    fun setName(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                nameError = false
            )
        }
    }

    fun save() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update {
                it.copy(nameError = true)
            }
            return
        }

        viewModelScope.launch {
            saveCurve(
                Curve(
                    id = curveId ?: 0L,
                    name = state.name.trim(),
                    data = state.curveData
                )
            )
            _uiState.update {
                it.copy(saveCompleted = true)
            }
        }
    }
}
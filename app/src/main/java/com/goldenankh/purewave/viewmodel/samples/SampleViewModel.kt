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

package com.goldenankh.purewave.viewmodel.samples

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.domain.model.sample.Sample
import com.goldenankh.domain.model.sample.SampleIcon
import com.goldenankh.domain.usecases.samples.ObserveSampleUseCase
import com.goldenankh.domain.usecases.samples.SaveSampleUseCase
import com.goldenankh.purewave.ui.components.uistate.samples.SampleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeSample: ObserveSampleUseCase,
    private val saveSample: SaveSampleUseCase
) : ViewModel() {

    private val sampleId =
        savedStateHandle
            .get<Long>("id")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(SampleUiState())

    val uiState = _uiState.asStateFlow()

    init {
        if (sampleId != null) {
            load(sampleId)
        }
    }

    private fun load(id: Long) {
        viewModelScope.launch {
            observeSample(id).collect { sample ->
                sample ?: return@collect

                _uiState.update {
                    it.copy(
                        name = sample.name,
                        icon = sample.icon,
                        color = with(sample.color) { RGBColor(r, g, b) },
                        durationSeconds =
                            sample.durationSeconds
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

    fun setIcon(icon: SampleIcon?) {
        _uiState.update {
            it.copy(icon = icon)
        }
    }

    fun setColor(color: RGBColor) {
        _uiState.update {
            it.copy(color = color)
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
            saveSample(
                Sample(
                    id = sampleId ?: 0L,
                    name = state.name.trim(),
                    icon = state.icon,
                    color = state.color,
                    program = state.program,
                    durationSeconds =
                        state.durationSeconds
                )
            )
            _uiState.update {
                it.copy(saveCompleted = true)
            }
        }
    }
}
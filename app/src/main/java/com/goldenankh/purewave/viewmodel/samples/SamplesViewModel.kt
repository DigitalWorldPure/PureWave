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
import com.goldenankh.domain.usecases.samples.DeleteSampleUseCase
import com.goldenankh.domain.usecases.samples.ObserveSamplesUseCase
import com.goldenankh.purewave.ui.components.uistate.samples.SamplesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SamplesViewModel @Inject constructor(
    observeSamples: ObserveSamplesUseCase,
    private val deleteSample: DeleteSampleUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val searchQuery: StateFlow<String> =
        savedStateHandle.getStateFlow("searchQuery", "")

    val uiState: StateFlow<SamplesUiState> =
        combine(observeSamples(), searchQuery) { samples, query ->
            val normalized = query.trim()
            SamplesUiState(
                samples = if (normalized.isEmpty()) samples else {
                    samples.filter {
                        it.name.contains(normalized, ignoreCase = true)
                    }
                },
                searchQuery = query
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SamplesUiState()
        )

    fun setSearchQuery(value: String) {
        savedStateHandle["searchQuery"] = value
    }

    fun delete(id: Long) {
        viewModelScope.launch { deleteSample(id) }
    }
}
package com.example.galleryapp.ui.slideshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SlideshowViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SlideshowUiState>(SlideshowUiState.Loading)
    val uiState: StateFlow<SlideshowUiState> = _uiState.asStateFlow()

    private var advanceJob: Job? = null

    private val photoColors = listOf(
        0xFF1A3A5CL, 0xFF3A1A5CL, 0xFF1A5C3AL,
        0xFF5C3A1AL, 0xFF5C1A3AL, 0xFF1A5C5CL,
        0xFF5C5C1AL, 0xFF2A2A5CL,
    )

    init {
        _uiState.value = SlideshowUiState.Playing(photoColors = photoColors)
        startAutoAdvance()
    }

    private fun startAutoAdvance() {
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            while (true) {
                delay(3500)
                _uiState.update { state ->
                    if (state is SlideshowUiState.Playing && !state.isPaused) {
                        state.copy(currentIndex = (state.currentIndex + 1) % state.photoColors.size)
                    } else state
                }
            }
        }
    }

    fun togglePause() {
        _uiState.update { state ->
            if (state is SlideshowUiState.Playing) state.copy(isPaused = !state.isPaused) else state
        }
    }

    fun goToPrevious() {
        _uiState.update { state ->
            if (state is SlideshowUiState.Playing) {
                state.copy(currentIndex = (state.currentIndex - 1 + state.photoColors.size) % state.photoColors.size)
            } else state
        }
    }

    fun goToNext() {
        _uiState.update { state ->
            if (state is SlideshowUiState.Playing) {
                state.copy(currentIndex = (state.currentIndex + 1) % state.photoColors.size)
            } else state
        }
    }

    override fun onCleared() {
        super.onCleared()
        advanceJob?.cancel()
    }
}

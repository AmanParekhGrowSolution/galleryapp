package com.example.galleryapp.ui.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemoriesStoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MemoriesStoryUiState>(MemoriesStoryUiState.Loading)
    val uiState: StateFlow<MemoriesStoryUiState> = _uiState.asStateFlow()

    private var progressJob: Job? = null

    private val slides = listOf(
        StorySlide(0xFF1A3A5C, "On this day", "Apr 9, 2024"),
        StorySlide(0xFF3A1A5C, "1 year ago", "April 9, 2023"),
        StorySlide(0xFF1A5C3A, "Best of April", "12 highlights"),
        StorySlide(0xFF5C3A1A, "With friends", "Sara, Arjun & 2 more"),
        StorySlide(0xFF5C1A3A, "Sunday brunch", "5 photos"),
        StorySlide(0xFF1A5C5C, "Goa trip", "32 photos"),
        StorySlide(0xFF5C5C1A, "Through your lens", "Curated by GalleryApp"),
    )

    init {
        _uiState.value = MemoriesStoryUiState.Playing(slides = slides)
        startProgress()
    }

    private fun startProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                delay(80)
                _uiState.update { state ->
                    if (state is MemoriesStoryUiState.Playing) {
                        val newProgress = state.progress + 0.012f
                        if (newProgress >= 1f) {
                            val nextIndex = state.currentIndex + 1
                            if (nextIndex < state.slides.size) {
                                state.copy(currentIndex = nextIndex, progress = 0f)
                            } else {
                                state
                            }
                        } else {
                            state.copy(progress = newProgress)
                        }
                    } else state
                }
            }
        }
    }

    fun goToPrevious() {
        _uiState.update { state ->
            if (state is MemoriesStoryUiState.Playing) {
                state.copy(
                    currentIndex = maxOf(0, state.currentIndex - 1),
                    progress = 0f,
                )
            } else state
        }
        startProgress()
    }

    fun goToNext() {
        _uiState.update { state ->
            if (state is MemoriesStoryUiState.Playing) {
                state.copy(
                    currentIndex = minOf(state.slides.size - 1, state.currentIndex + 1),
                    progress = 0f,
                )
            } else state
        }
        startProgress()
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
    }
}

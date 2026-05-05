package com.example.galleryapp.ui.slideshow

sealed interface SlideshowUiState {
    data object Loading : SlideshowUiState
    data class Playing(
        val photoColors: List<Long>,
        val currentIndex: Int = 0,
        val isPaused: Boolean = false,
    ) : SlideshowUiState
}

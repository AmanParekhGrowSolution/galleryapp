package com.example.galleryapp.ui.memories

data class StorySlide(
    val colorHex: Long,
    val title: String,
    val subtitle: String,
)

sealed interface MemoriesStoryUiState {
    data object Loading : MemoriesStoryUiState
    data class Playing(
        val slides: List<StorySlide>,
        val currentIndex: Int = 0,
        val progress: Float = 0f,
    ) : MemoriesStoryUiState
}

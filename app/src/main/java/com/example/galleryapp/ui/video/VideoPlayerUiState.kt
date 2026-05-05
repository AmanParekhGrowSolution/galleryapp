package com.example.galleryapp.ui.video

sealed interface VideoPlayerUiState {
    data object Loading : VideoPlayerUiState
    data class Ready(
        val photoId: Long,
        val isPlaying: Boolean = true,
        val progress: Float = 0.32f,
        val speed: Float = 1f,
        val isMuted: Boolean = false,
        val showControls: Boolean = true,
        val durationSeconds: Int = 187,
    ) : VideoPlayerUiState
}

package com.example.galleryapp.ui.video

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VideoPlayerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<VideoPlayerUiState>(VideoPlayerUiState.Loading)
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    fun loadVideo(photoId: Long) {
        _uiState.value = VideoPlayerUiState.Ready(photoId = photoId)
    }

    fun togglePlay() {
        _uiState.update { state ->
            if (state is VideoPlayerUiState.Ready) state.copy(isPlaying = !state.isPlaying) else state
        }
    }

    fun toggleControls() {
        _uiState.update { state ->
            if (state is VideoPlayerUiState.Ready) state.copy(showControls = !state.showControls) else state
        }
    }

    fun toggleMute() {
        _uiState.update { state ->
            if (state is VideoPlayerUiState.Ready) state.copy(isMuted = !state.isMuted) else state
        }
    }

    fun cycleSpeed() {
        _uiState.update { state ->
            if (state is VideoPlayerUiState.Ready) {
                val next = when (state.speed) {
                    0.5f -> 1f
                    1f -> 1.5f
                    1.5f -> 2f
                    else -> 0.5f
                }
                state.copy(speed = next)
            } else state
        }
    }

    fun seekBy(deltaSeconds: Int) {
        _uiState.update { state ->
            if (state is VideoPlayerUiState.Ready) {
                val newProgress = (state.progress + deltaSeconds.toFloat() / state.durationSeconds).coerceIn(0f, 1f)
                state.copy(progress = newProgress)
            } else state
        }
    }
}

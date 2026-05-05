package com.example.galleryapp.ui.video

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VideoTrimmerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<VideoTrimmerUiState>(VideoTrimmerUiState.Loading)
    val uiState: StateFlow<VideoTrimmerUiState> = _uiState.asStateFlow()

    fun loadVideo(photoId: Long) {
        _uiState.value = VideoTrimmerUiState.Editing(photoId = photoId)
    }

    fun selectTool(tool: TrimTool) {
        _uiState.update { state ->
            if (state is VideoTrimmerUiState.Editing) state.copy(activeTool = tool) else state
        }
    }

    fun setTrimStart(fraction: Float) {
        _uiState.update { state ->
            if (state is VideoTrimmerUiState.Editing) {
                state.copy(trimStart = fraction.coerceIn(0f, state.trimEnd - 0.05f))
            } else state
        }
    }

    fun setTrimEnd(fraction: Float) {
        _uiState.update { state ->
            if (state is VideoTrimmerUiState.Editing) {
                state.copy(trimEnd = fraction.coerceIn(state.trimStart + 0.05f, 1f))
            } else state
        }
    }
}

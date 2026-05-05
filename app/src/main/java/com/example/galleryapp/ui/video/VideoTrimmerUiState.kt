package com.example.galleryapp.ui.video

enum class TrimTool { TRIM, FRAME, SPEED, MUTE, SPLIT }

sealed interface VideoTrimmerUiState {
    data object Loading : VideoTrimmerUiState
    data class Editing(
        val photoId: Long,
        val trimStart: Float = 0.18f,
        val trimEnd: Float = 0.72f,
        val playhead: Float = 0.32f,
        val durationSeconds: Int = 187,
        val activeTool: TrimTool = TrimTool.TRIM,
    ) : VideoTrimmerUiState
}

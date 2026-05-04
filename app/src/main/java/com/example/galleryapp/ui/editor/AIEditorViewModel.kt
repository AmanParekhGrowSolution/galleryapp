package com.example.galleryapp.ui.editor

import androidx.lifecycle.ViewModel
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class EditorTool { AI, Crop, Adjust, Filter, Brush, Text, Sticker }
enum class FilterPreset { Original, Vivid, Mono, Fade, Warm, Cool, Noir, Bloom }

data class AIEditorUiState(
    val photoColor: Long = 0xFF1E3A5FL,
    val selectedTool: EditorTool = EditorTool.Adjust,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val saturation: Float = 12f,
    val warmth: Float = -4f,
    val sharpness: Float = 0f,
    val selectedFilter: FilterPreset = FilterPreset.Original
)

class AIEditorViewModel : ViewModel() {
    private val repository = GalleryRepositoryImpl()
    private val _uiState = MutableStateFlow(AIEditorUiState())
    val uiState: StateFlow<AIEditorUiState> = _uiState.asStateFlow()

    fun loadPhoto(photoId: Long) {
        val photo = repository.getPhotoById(photoId)
        _uiState.update { it.copy(photoColor = photo?.placeholderColor ?: 0xFF1E3A5FL) }
    }

    fun selectTool(tool: EditorTool) = _uiState.update { it.copy(selectedTool = tool) }
    fun selectFilter(filter: FilterPreset) = _uiState.update { it.copy(selectedFilter = filter) }
    fun setBrightness(v: Float) = _uiState.update { it.copy(brightness = v) }
    fun setContrast(v: Float) = _uiState.update { it.copy(contrast = v) }
    fun setSaturation(v: Float) = _uiState.update { it.copy(saturation = v) }
    fun setWarmth(v: Float) = _uiState.update { it.copy(warmth = v) }
    fun setSharpness(v: Float) = _uiState.update { it.copy(sharpness = v) }
    fun revert() = _uiState.update {
        it.copy(brightness = 0f, contrast = 0f, saturation = 12f, warmth = -4f, sharpness = 0f,
            selectedFilter = FilterPreset.Original)
    }
}

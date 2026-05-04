package com.example.galleryapp.ui.cleaner

import androidx.lifecycle.ViewModel
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.CleanerCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CleanerUiState(
    val usedGb: Float = 87.4f,
    val totalGb: Float = 128f,
    val freeableGb: Float = 9.2f,
    val categories: List<CleanerCategory> = emptyList(),
    val isCleaning: Boolean = false,
    val cleaned: Boolean = false
)

class CleanerViewModel : ViewModel() {
    private val repository = GalleryRepositoryImpl()
    private val _uiState = MutableStateFlow(
        CleanerUiState(categories = repository.getCleanerCategories())
    )
    val uiState: StateFlow<CleanerUiState> = _uiState.asStateFlow()

    fun startCleaning() {
        _uiState.update { it.copy(isCleaning = true) }
    }

    fun finishCleaning() {
        _uiState.update { state ->
            state.copy(
                isCleaning = false,
                cleaned = true,
                usedGb = state.usedGb - state.freeableGb,
                freeableGb = 0f
            )
        }
    }
}

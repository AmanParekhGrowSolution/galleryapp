package com.example.galleryapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PhotoFilter { All, Videos, Screenshots, GIFs }

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val sections: List<PhotoSection>,
        val selectedFilter: PhotoFilter,
        val selectionMode: Boolean,
        val selectedIds: Set<Long>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

data class PhotoSection(val dateLabel: String, val photos: List<Photo>)

class HomeViewModel : ViewModel() {
    private val repository = GalleryRepositoryImpl()
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            repository.getPhotos().collect { photos ->
                val sections = groupByDate(photos)
                _uiState.update {
                    HomeUiState.Success(
                        sections = sections,
                        selectedFilter = PhotoFilter.All,
                        selectionMode = false,
                        selectedIds = emptySet()
                    )
                }
            }
        }
    }

    fun selectFilter(filter: PhotoFilter) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) state.copy(selectedFilter = filter) else state
        }
    }

    fun toggleSelectionMode() {
        _uiState.update { state ->
            if (state is HomeUiState.Success) {
                state.copy(
                    selectionMode = !state.selectionMode,
                    selectedIds = emptySet()
                )
            } else state
        }
    }

    fun togglePhotoSelection(photoId: Long) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) {
                val newIds = if (photoId in state.selectedIds) {
                    state.selectedIds - photoId
                } else {
                    state.selectedIds + photoId
                }
                state.copy(selectedIds = newIds)
            } else state
        }
    }

    private fun groupByDate(photos: List<Photo>): List<PhotoSection> {
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L
        val today = mutableListOf<Photo>()
        val yesterday = mutableListOf<Photo>()
        val older = mutableListOf<Photo>()

        photos.forEach { photo ->
            val age = now - photo.dateTaken
            when {
                age < dayMs -> today.add(photo)
                age < 2 * dayMs -> yesterday.add(photo)
                else -> older.add(photo)
            }
        }

        return buildList {
            if (today.isNotEmpty()) add(PhotoSection("Today", today))
            if (yesterday.isNotEmpty()) add(PhotoSection("Yesterday", yesterday))
            if (older.isNotEmpty()) add(PhotoSection("Earlier", older))
        }
    }
}

package com.example.galleryapp.ui.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PhotoViewerUiState {
    data object Loading : PhotoViewerUiState
    data class Success(
        val photo: Photo,
        val isFavorite: Boolean,
        val showInfo: Boolean
    ) : PhotoViewerUiState
    data class Error(val message: String) : PhotoViewerUiState
}

class PhotoViewerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GalleryRepositoryImpl(application)
    private val _uiState = MutableStateFlow<PhotoViewerUiState>(PhotoViewerUiState.Loading)
    val uiState: StateFlow<PhotoViewerUiState> = _uiState.asStateFlow()

    fun loadPhoto(photoId: Long) {
        _uiState.update { PhotoViewerUiState.Loading }
        viewModelScope.launch {
            val photo = repository.getPhotoById(photoId)
            _uiState.update {
                if (photo != null) {
                    PhotoViewerUiState.Success(
                        photo = photo,
                        isFavorite = photo.isFavorite,
                        showInfo = false
                    )
                } else {
                    PhotoViewerUiState.Error("Photo not found")
                }
            }
        }
    }

    fun toggleFavorite() {
        _uiState.update { state ->
            if (state is PhotoViewerUiState.Success) state.copy(isFavorite = !state.isFavorite) else state
        }
    }

    fun toggleInfo() {
        _uiState.update { state ->
            if (state is PhotoViewerUiState.Success) state.copy(showInfo = !state.showInfo) else state
        }
    }
}

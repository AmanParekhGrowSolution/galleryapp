package com.example.galleryapp.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.Album
import com.example.galleryapp.domain.model.AlbumType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AlbumsUiState {
    data object Loading : AlbumsUiState
    data class Success(
        val featured: List<Album>,
        val myAlbums: List<Album>,
        val social: List<Album>
    ) : AlbumsUiState
    data class Error(val message: String) : AlbumsUiState
}

class AlbumsViewModel : ViewModel() {
    private val repository = GalleryRepositoryImpl()
    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            repository.getAlbums().collect { albums ->
                _uiState.update {
                    AlbumsUiState.Success(
                        featured = albums.filter { it.type == AlbumType.Featured },
                        myAlbums = albums.filter { it.type == AlbumType.User },
                        social = albums.filter { it.type == AlbumType.Social }
                    )
                }
            }
        }
    }
}

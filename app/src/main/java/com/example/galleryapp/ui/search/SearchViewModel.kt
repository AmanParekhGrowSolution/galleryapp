package com.example.galleryapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Empty : SearchUiState
    data class Results(val photos: List<Photo>, val query: String) : SearchUiState
}

class SearchViewModel : ViewModel() {
    private val repository = GalleryRepositoryImpl()
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun onQueryChange(q: String) {
        _query.update { q }
        if (q.isBlank()) {
            _uiState.update { SearchUiState.Empty }
        } else {
            search(q)
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            repository.getPhotos().collect { photos ->
                val results = photos.filter {
                    it.displayName.contains(query, ignoreCase = true)
                }
                _uiState.update { SearchUiState.Results(results, query) }
            }
        }
    }
}

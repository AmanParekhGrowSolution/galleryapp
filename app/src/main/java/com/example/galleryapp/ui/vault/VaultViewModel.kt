package com.example.galleryapp.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface VaultUiState {
    data class Locked(
        val enteredPin: String = "",
        val showError: Boolean = false
    ) : VaultUiState

    data class Unlocked(
        val photos: List<Photo> = emptyList(),
        val selectedFilter: String = "All"
    ) : VaultUiState
}

private const val CORRECT_PIN = "1234"

class VaultViewModel : ViewModel() {
    private val repository = GalleryRepositoryImpl()
    private val _uiState = MutableStateFlow<VaultUiState>(VaultUiState.Locked())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    fun appendDigit(digit: String) {
        _uiState.update { state ->
            if (state is VaultUiState.Locked && state.enteredPin.length < 4) {
                state.copy(enteredPin = state.enteredPin + digit, showError = false)
            } else state
        }
        val current = (_uiState.value as? VaultUiState.Locked) ?: return
        if (current.enteredPin.length == 4) validatePin(current.enteredPin)
    }

    fun deleteDigit() {
        _uiState.update { state ->
            if (state is VaultUiState.Locked && state.enteredPin.isNotEmpty()) {
                state.copy(enteredPin = state.enteredPin.dropLast(1), showError = false)
            } else state
        }
    }

    private fun validatePin(pin: String) {
        if (pin == CORRECT_PIN) {
            viewModelScope.launch {
                repository.getVaultPhotos().collect { photos ->
                    _uiState.update { VaultUiState.Unlocked(photos = photos) }
                }
            }
        } else {
            _uiState.update { VaultUiState.Locked(enteredPin = "", showError = true) }
        }
    }

    fun lock() {
        _uiState.update { VaultUiState.Locked() }
    }

    fun selectFilter(filter: String) {
        _uiState.update { state ->
            if (state is VaultUiState.Unlocked) state.copy(selectedFilter = filter) else state
        }
    }
}

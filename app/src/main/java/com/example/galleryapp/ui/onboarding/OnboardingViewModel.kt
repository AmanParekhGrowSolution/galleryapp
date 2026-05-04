package com.example.galleryapp.ui.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,
    val selectedLanguage: String = "English"
)

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextPage() {
        _uiState.update { state ->
            state.copy(currentPage = (state.currentPage + 1).coerceAtMost(state.totalPages - 1))
        }
    }

    fun selectLanguage(language: String) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun isLastPage(): Boolean = _uiState.value.currentPage == _uiState.value.totalPages - 1
}

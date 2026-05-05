package com.example.galleryapp.ui.collage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CollageMakerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CollageMakerUiState>(CollageMakerUiState.Editing())
    val uiState: StateFlow<CollageMakerUiState> = _uiState.asStateFlow()

    fun selectLayout(index: Int) {
        _uiState.update { state ->
            if (state is CollageMakerUiState.Editing) state.copy(selectedLayoutIndex = index) else state
        }
    }

    fun selectTab(tab: CollageTab) {
        _uiState.update { state ->
            if (state is CollageMakerUiState.Editing) state.copy(activeTab = tab) else state
        }
    }

    fun selectBackground(colorHex: Long) {
        _uiState.update { state ->
            if (state is CollageMakerUiState.Editing) state.copy(backgroundColorHex = colorHex) else state
        }
    }

    fun setBorder(px: Int) {
        _uiState.update { state ->
            if (state is CollageMakerUiState.Editing) state.copy(borderPx = px) else state
        }
    }

    fun setCornerRadius(dp: Int) {
        _uiState.update { state ->
            if (state is CollageMakerUiState.Editing) state.copy(cornerRadius = dp) else state
        }
    }
}

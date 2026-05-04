package com.example.galleryapp.ui.premium

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PremiumPlan { Monthly, Yearly, Lifetime }

data class PremiumUiState(
    val selectedPlan: PremiumPlan = PremiumPlan.Yearly
)

class PremiumViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState: StateFlow<PremiumUiState> = _uiState.asStateFlow()

    fun selectPlan(plan: PremiumPlan) = _uiState.update { it.copy(selectedPlan = plan) }
}

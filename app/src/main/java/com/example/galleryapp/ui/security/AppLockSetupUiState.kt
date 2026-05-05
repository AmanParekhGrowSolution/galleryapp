package com.example.galleryapp.ui.security

sealed interface AppLockSetupUiState {
    data class PinEntry(
        val pin: String = "",
        val isConfirming: Boolean = false,
        val enableBiometric: Boolean = true,
        val errorMessage: String? = null,
    ) : AppLockSetupUiState
    data object Saved : AppLockSetupUiState
}

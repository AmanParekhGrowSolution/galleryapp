package com.example.galleryapp.ui.security

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.galleryapp.data.local.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppLockSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager.create(application)
    private val _uiState = MutableStateFlow<AppLockSetupUiState>(AppLockSetupUiState.PinEntry())
    val uiState: StateFlow<AppLockSetupUiState> = _uiState.asStateFlow()

    private var firstPin: String = ""

    fun enterDigit(digit: Int) {
        _uiState.update { state ->
            if (state is AppLockSetupUiState.PinEntry && state.pin.length < 4) {
                state.copy(pin = state.pin + digit, errorMessage = null)
            } else state
        }
    }

    fun deleteDigit() {
        _uiState.update { state ->
            if (state is AppLockSetupUiState.PinEntry && state.pin.isNotEmpty()) {
                state.copy(pin = state.pin.dropLast(1), errorMessage = null)
            } else state
        }
    }

    fun confirmPin() {
        val state = _uiState.value as? AppLockSetupUiState.PinEntry ?: return
        if (state.pin.length < 4) return

        if (!state.isConfirming) {
            firstPin = state.pin
            _uiState.value = AppLockSetupUiState.PinEntry(
                isConfirming = true,
                enableBiometric = state.enableBiometric
            )
        } else {
            if (state.pin == firstPin) {
                prefs.setVaultPin(state.pin)
                _uiState.value = AppLockSetupUiState.Saved
            } else {
                firstPin = ""
                _uiState.value = AppLockSetupUiState.PinEntry(
                    isConfirming = true,
                    enableBiometric = state.enableBiometric,
                    errorMessage = "PINs don't match — try again",
                )
            }
        }
    }

    fun toggleBiometric() {
        _uiState.update { state ->
            if (state is AppLockSetupUiState.PinEntry) state.copy(enableBiometric = !state.enableBiometric)
            else state
        }
    }
}

package com.example.galleryapp.ui.vault

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.local.PrefsManager
import com.example.galleryapp.data.repository.GalleryRepositoryImpl
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface VaultUiState {
    data class Locked(
        val enteredPin: String = "",
        val showError: Boolean = false,
        val lockoutRemainingSeconds: Int = 0
    ) : VaultUiState

    data class Unlocked(
        val photos: List<Photo> = emptyList(),
        val selectedFilter: String = "All"
    ) : VaultUiState
}

private val LOCKOUT_SECONDS_BY_FAIL = mapOf(5 to 30, 6 to 60, 7 to 120, 8 to 300)
private const val MAX_FAIL_LOCKOUT_SECONDS = 300

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GalleryRepositoryImpl()
    private val prefs = PrefsManager(application)
    private val _uiState = MutableStateFlow<VaultUiState>(VaultUiState.Locked())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        checkExistingLockout()
    }

    private fun checkExistingLockout() {
        val lockoutUntil = prefs.getVaultLockoutUntil()
        val remaining = ((lockoutUntil - System.currentTimeMillis()) / 1000).toInt()
        if (remaining > 0) {
            _uiState.value = VaultUiState.Locked(lockoutRemainingSeconds = remaining)
            startCountdown(remaining)
        }
    }

    fun appendDigit(digit: String) {
        val current = _uiState.value as? VaultUiState.Locked ?: return
        if (current.lockoutRemainingSeconds > 0) return
        if (current.enteredPin.length >= 4) return

        val newPin = current.enteredPin + digit
        _uiState.update { VaultUiState.Locked(enteredPin = newPin, showError = false) }

        if (newPin.length == 4) {
            validatePin(newPin)
        }
    }

    fun deleteDigit() {
        _uiState.update { state ->
            if (state is VaultUiState.Locked && state.enteredPin.isNotEmpty()) {
                state.copy(enteredPin = state.enteredPin.dropLast(1), showError = false)
            } else state
        }
    }

    private fun validatePin(pin: String) {
        val isCorrect = if (prefs.hasVaultPin()) {
            prefs.verifyVaultPin(pin)
        } else {
            // No PIN set yet — fall back to default for demo purposes
            pin == "1234"
        }

        if (isCorrect) {
            prefs.resetVaultFailCount()
            viewModelScope.launch {
                repository.getVaultPhotos().collect { photos ->
                    _uiState.update { VaultUiState.Unlocked(photos = photos) }
                }
            }
        } else {
            val failCount = prefs.incrementVaultFailCount()
            val lockoutSeconds = LOCKOUT_SECONDS_BY_FAIL[failCount]
                ?: if (failCount > 8) MAX_FAIL_LOCKOUT_SECONDS else 0

            if (lockoutSeconds > 0) {
                val lockoutUntil = System.currentTimeMillis() + lockoutSeconds * 1000L
                prefs.setVaultLockoutUntil(lockoutUntil)
                _uiState.value = VaultUiState.Locked(lockoutRemainingSeconds = lockoutSeconds)
                startCountdown(lockoutSeconds)
            } else {
                _uiState.value = VaultUiState.Locked(enteredPin = "", showError = true)
            }
        }
    }

    private fun startCountdown(seconds: Int) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.update { state ->
                    if (state is VaultUiState.Locked) state.copy(lockoutRemainingSeconds = remaining)
                    else state
                }
            }
            _uiState.update { state ->
                if (state is VaultUiState.Locked) state.copy(lockoutRemainingSeconds = 0, showError = false)
                else state
            }
        }
    }

    fun lock() {
        countdownJob?.cancel()
        _uiState.update { VaultUiState.Locked() }
    }

    fun selectFilter(filter: String) {
        _uiState.update { state ->
            if (state is VaultUiState.Unlocked) state.copy(selectedFilter = filter) else state
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

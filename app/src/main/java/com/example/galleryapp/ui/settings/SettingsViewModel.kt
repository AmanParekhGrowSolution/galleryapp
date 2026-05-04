package com.example.galleryapp.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val appLockEnabled: Boolean = true,
    val hideAppIcon: Boolean = false,
    val stripMetadata: Boolean = true,
    val cloudBackup: Boolean = false,
    val showVideoDuration: Boolean = true,
    val roundedThumbnails: Boolean = false,
    val darkMode: String = "System",
    val defaultGrid: String = "4 columns",
    val storageLabel: String = "9.2 GB",
    val trashLabel: String = "12 items"
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleAppLock() = _uiState.update { it.copy(appLockEnabled = !it.appLockEnabled) }
    fun toggleHideAppIcon() = _uiState.update { it.copy(hideAppIcon = !it.hideAppIcon) }
    fun toggleStripMetadata() = _uiState.update { it.copy(stripMetadata = !it.stripMetadata) }
    fun toggleCloudBackup() = _uiState.update { it.copy(cloudBackup = !it.cloudBackup) }
    fun toggleShowVideoDuration() = _uiState.update { it.copy(showVideoDuration = !it.showVideoDuration) }
    fun toggleRoundedThumbnails() = _uiState.update { it.copy(roundedThumbnails = !it.roundedThumbnails) }
}

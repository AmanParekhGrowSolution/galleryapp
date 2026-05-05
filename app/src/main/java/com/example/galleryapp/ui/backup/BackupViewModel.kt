package com.example.galleryapp.ui.backup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BackupProvider(
    val id: String,
    val name: String,
    val emoji: String,
    val quota: String
)

data class BackupUiState(
    val enabled: Boolean = false,
    val provider: String = "none",
    val wifiOnly: Boolean = true,
    val chargingOnly: Boolean = false,
    val includeVault: Boolean = false,
    val backedUpCount: Int = 8421,
    val pendingCount: Int = 23,
    val usedGb: Int = 32,
    val providers: List<BackupProvider> = listOf(
        BackupProvider("gdrive", "Google Drive", "🔵", "15 GB free"),
        BackupProvider("dropbox", "Dropbox", "🔷", "2 GB free"),
        BackupProvider("onedrive", "OneDrive", "🌀", "5 GB free"),
        BackupProvider("webdav", "Custom (WebDAV)", "☁️", "Self-hosted")
    )
)

class BackupViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun toggleEnabled() = _uiState.update { it.copy(enabled = !it.enabled) }
    fun selectProvider(id: String) = _uiState.update { it.copy(provider = id) }
    fun toggleWifiOnly() = _uiState.update { it.copy(wifiOnly = !it.wifiOnly) }
    fun toggleChargingOnly() = _uiState.update { it.copy(chargingOnly = !it.chargingOnly) }
    fun toggleIncludeVault() = _uiState.update { it.copy(includeVault = !it.includeVault) }
}

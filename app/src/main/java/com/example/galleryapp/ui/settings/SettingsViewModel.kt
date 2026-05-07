package com.example.galleryapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.local.DisplayPreferences
import com.example.galleryapp.data.local.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val appLockEnabled: Boolean = true,
    val hideAppIcon: Boolean = false,
    val stripMetadata: Boolean = true,
    val cloudBackup: Boolean = false,
    val showVideoDuration: Boolean = true,
    val roundedThumbnails: Boolean = false,
    val darkMode: String = "System",
    val defaultGrid: String = "3 columns",
    val storageLabel: String = "9.2 GB",
    val trashLabel: String = "12 items"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager.create(application)
    private val displayPrefs = DisplayPreferences.getInstance(application)

    private val _baseState = MutableStateFlow(loadBaseFromPrefs())

    val uiState: StateFlow<SettingsUiState> = combine(
        _baseState,
        displayPrefs.darkMode,
        displayPrefs.defaultGrid,
        displayPrefs.showVideoDuration,
        displayPrefs.roundedThumbnails
    ) { base, darkMode, defaultGrid, showVideoDuration, roundedThumbnails ->
        base.copy(
            darkMode = darkMode,
            defaultGrid = defaultGrid,
            showVideoDuration = showVideoDuration,
            roundedThumbnails = roundedThumbnails
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = loadInitialState()
    )

    private fun loadBaseFromPrefs(): SettingsUiState = SettingsUiState(
        appLockEnabled = prefs.getBoolean(PrefsManager.KEY_APP_LOCK, true),
        hideAppIcon = prefs.getBoolean(PrefsManager.KEY_HIDE_APP_ICON, false),
        stripMetadata = prefs.getBoolean(PrefsManager.KEY_STRIP_METADATA, true),
        cloudBackup = prefs.getBoolean(PrefsManager.KEY_CLOUD_BACKUP, false)
    )

    private fun loadInitialState(): SettingsUiState = loadBaseFromPrefs().copy(
        darkMode = displayPrefs.darkMode.value,
        defaultGrid = displayPrefs.defaultGrid.value,
        showVideoDuration = displayPrefs.showVideoDuration.value,
        roundedThumbnails = displayPrefs.roundedThumbnails.value
    )

    fun toggleAppLock() = toggle(PrefsManager.KEY_APP_LOCK) { it.copy(appLockEnabled = !it.appLockEnabled) }

    fun toggleHideAppIcon() = toggle(PrefsManager.KEY_HIDE_APP_ICON) { it.copy(hideAppIcon = !it.hideAppIcon) }

    fun toggleStripMetadata() = toggle(PrefsManager.KEY_STRIP_METADATA) { it.copy(stripMetadata = !it.stripMetadata) }

    fun toggleCloudBackup() = toggle(PrefsManager.KEY_CLOUD_BACKUP) { it.copy(cloudBackup = !it.cloudBackup) }

    fun toggleShowVideoDuration() {
        displayPrefs.setShowVideoDuration(!displayPrefs.showVideoDuration.value)
    }

    fun toggleRoundedThumbnails() {
        displayPrefs.setRoundedThumbnails(!displayPrefs.roundedThumbnails.value)
    }

    fun setDarkMode(value: String) {
        displayPrefs.setDarkMode(value)
    }

    fun setDefaultGrid(value: String) {
        displayPrefs.setDefaultGrid(value)
    }

    private fun toggle(key: String, transform: (SettingsUiState) -> SettingsUiState) {
        _baseState.update { current ->
            val updated = transform(current)
            when (key) {
                PrefsManager.KEY_APP_LOCK -> prefs.setBoolean(key, updated.appLockEnabled)
                PrefsManager.KEY_HIDE_APP_ICON -> prefs.setBoolean(key, updated.hideAppIcon)
                PrefsManager.KEY_STRIP_METADATA -> prefs.setBoolean(key, updated.stripMetadata)
                PrefsManager.KEY_CLOUD_BACKUP -> prefs.setBoolean(key, updated.cloudBackup)
            }
            updated
        }
    }
}

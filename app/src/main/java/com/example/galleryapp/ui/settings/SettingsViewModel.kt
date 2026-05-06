package com.example.galleryapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.galleryapp.data.local.PrefsManager
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

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager(application)

    private val _uiState = MutableStateFlow(loadFromPrefs())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private fun loadFromPrefs(): SettingsUiState = SettingsUiState(
        appLockEnabled = prefs.getBoolean(PrefsManager.KEY_APP_LOCK, true),
        hideAppIcon = prefs.getBoolean(PrefsManager.KEY_HIDE_APP_ICON, false),
        stripMetadata = prefs.getBoolean(PrefsManager.KEY_STRIP_METADATA, true),
        cloudBackup = prefs.getBoolean(PrefsManager.KEY_CLOUD_BACKUP, false),
        showVideoDuration = prefs.getBoolean(PrefsManager.KEY_SHOW_VIDEO_DURATION, true),
        roundedThumbnails = prefs.getBoolean(PrefsManager.KEY_ROUNDED_THUMBNAILS, false),
        darkMode = prefs.getString(PrefsManager.KEY_DARK_MODE, "System"),
        defaultGrid = prefs.getString(PrefsManager.KEY_DEFAULT_GRID, "4 columns")
    )

    fun toggleAppLock() = toggle(PrefsManager.KEY_APP_LOCK) { it.copy(appLockEnabled = !it.appLockEnabled) }

    fun toggleHideAppIcon() = toggle(PrefsManager.KEY_HIDE_APP_ICON) { it.copy(hideAppIcon = !it.hideAppIcon) }

    fun toggleStripMetadata() = toggle(PrefsManager.KEY_STRIP_METADATA) { it.copy(stripMetadata = !it.stripMetadata) }

    fun toggleCloudBackup() = toggle(PrefsManager.KEY_CLOUD_BACKUP) { it.copy(cloudBackup = !it.cloudBackup) }

    fun toggleShowVideoDuration() = toggle(PrefsManager.KEY_SHOW_VIDEO_DURATION) { it.copy(showVideoDuration = !it.showVideoDuration) }

    fun toggleRoundedThumbnails() = toggle(PrefsManager.KEY_ROUNDED_THUMBNAILS) { it.copy(roundedThumbnails = !it.roundedThumbnails) }

    private fun toggle(key: String, transform: (SettingsUiState) -> SettingsUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            when (key) {
                PrefsManager.KEY_APP_LOCK -> prefs.setBoolean(key, updated.appLockEnabled)
                PrefsManager.KEY_HIDE_APP_ICON -> prefs.setBoolean(key, updated.hideAppIcon)
                PrefsManager.KEY_STRIP_METADATA -> prefs.setBoolean(key, updated.stripMetadata)
                PrefsManager.KEY_CLOUD_BACKUP -> prefs.setBoolean(key, updated.cloudBackup)
                PrefsManager.KEY_SHOW_VIDEO_DURATION -> prefs.setBoolean(key, updated.showVideoDuration)
                PrefsManager.KEY_ROUNDED_THUMBNAILS -> prefs.setBoolean(key, updated.roundedThumbnails)
            }
            updated
        }
    }
}

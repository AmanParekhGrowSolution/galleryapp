package com.example.galleryapp.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DisplayPreferences private constructor(private val prefs: PrefsManager) {

    private val _darkMode = MutableStateFlow(prefs.getString(PrefsManager.KEY_DARK_MODE, "System"))
    val darkMode: StateFlow<String> = _darkMode.asStateFlow()

    private val _defaultGrid = MutableStateFlow(prefs.getString(PrefsManager.KEY_DEFAULT_GRID, "3 columns"))
    val defaultGrid: StateFlow<String> = _defaultGrid.asStateFlow()

    private val _showVideoDuration = MutableStateFlow(prefs.getBoolean(PrefsManager.KEY_SHOW_VIDEO_DURATION, true))
    val showVideoDuration: StateFlow<Boolean> = _showVideoDuration.asStateFlow()

    private val _roundedThumbnails = MutableStateFlow(prefs.getBoolean(PrefsManager.KEY_ROUNDED_THUMBNAILS, false))
    val roundedThumbnails: StateFlow<Boolean> = _roundedThumbnails.asStateFlow()

    fun setDarkMode(value: String) {
        _darkMode.value = value
        prefs.setString(PrefsManager.KEY_DARK_MODE, value)
    }

    fun setDefaultGrid(value: String) {
        _defaultGrid.value = value
        prefs.setString(PrefsManager.KEY_DEFAULT_GRID, value)
    }

    fun setShowVideoDuration(value: Boolean) {
        _showVideoDuration.value = value
        prefs.setBoolean(PrefsManager.KEY_SHOW_VIDEO_DURATION, value)
    }

    fun setRoundedThumbnails(value: Boolean) {
        _roundedThumbnails.value = value
        prefs.setBoolean(PrefsManager.KEY_ROUNDED_THUMBNAILS, value)
    }

    companion object {
        @Volatile
        private var instance: DisplayPreferences? = null

        fun getInstance(context: Context): DisplayPreferences =
            instance ?: synchronized(this) {
                instance ?: DisplayPreferences(PrefsManager.create(context.applicationContext))
                    .also { instance = it }
            }
    }
}

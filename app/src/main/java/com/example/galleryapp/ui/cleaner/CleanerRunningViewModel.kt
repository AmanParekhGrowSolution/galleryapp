package com.example.galleryapp.ui.cleaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CleanerRunningViewModel : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private var progressJob: Job? = null

    fun resumeProgress() {
        if (progressJob?.isActive == true) return
        progressJob = viewModelScope.launch {
            while (true) {
                if (_progress.value >= 1f) _progress.value = 0f
                delay(60)
                _progress.value = (_progress.value + 0.01f).coerceAtMost(1f)
            }
        }
    }

    fun pauseProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
    }
}

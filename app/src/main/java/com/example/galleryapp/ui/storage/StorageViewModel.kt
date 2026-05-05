package com.example.galleryapp.ui.storage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StorageBreakdown(
    val label: String,
    val valueGb: Float,
    val colorArgb: Long
)

data class StorageCategory(
    val label: String,
    val count: String,
    val sizeLabel: String,
    val colorArgb: Long,
    val iconName: String
)

data class StorageUiState(
    val usedGb: Float = 87.4f,
    val totalGb: Float = 128f,
    val freeableGb: Float = 9.2f,
    val breakdown: List<StorageBreakdown> = listOf(
        StorageBreakdown("Photos", 32.4f, 0xFF0066FF),
        StorageBreakdown("Videos", 28.1f, 0xFFA78BFA),
        StorageBreakdown("Screenshots", 4.2f, 0xFF34D399),
        StorageBreakdown("WhatsApp", 12.7f, 0xFF25D366),
        StorageBreakdown("Downloads", 6.4f, 0xFFF97316),
        StorageBreakdown("Other", 3.6f, 0xFFFBBF24)
    ),
    val categories: List<StorageCategory> = listOf(
        StorageCategory("Photos", "8,421 photos", "32.4 GB", 0xFF6366F1, "image"),
        StorageCategory("Videos", "412 videos", "28.1 GB", 0xFFA78BFA, "video"),
        StorageCategory("Screenshots", "1,205 items", "4.2 GB", 0xFF34D399, "image"),
        StorageCategory("Duplicates", "124 items", "1.8 GB", 0xFFEC4899, "copy"),
        StorageCategory("RAW & HEIC", "86 items", "5.4 GB", 0xFF7DD3FC, "star"),
        StorageCategory("Trash", "24 items", "320 MB", 0xFF9CA3AF, "delete")
    )
)

class StorageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()
}

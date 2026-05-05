package com.example.galleryapp.ui.collage

enum class CollageTab { LAYOUT, RATIO, BACKGROUND, SPACING }

data class CollageLayout(val tiles: List<CollageTile>)
data class CollageTile(val xPct: Float, val yPct: Float, val wPct: Float, val hPct: Float)

sealed interface CollageMakerUiState {
    data class Editing(
        val selectedLayoutIndex: Int = 1,
        val backgroundColorHex: Long = 0xFFFFFFFF,
        val activeTab: CollageTab = CollageTab.LAYOUT,
        val borderPx: Int = 6,
        val cornerRadius: Int = 8,
    ) : CollageMakerUiState
}

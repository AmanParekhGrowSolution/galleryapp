package com.example.galleryapp.domain.model

data class CleanerCategory(
    val name: String,
    val count: Int,
    val sizeBytes: Long,
    val colorHex: Long
) {
    val sizeLabel: String get() {
        val mb = sizeBytes / (1024f * 1024f)
        return if (mb >= 1000) "%.1f GB".format(mb / 1024f) else "%.0f MB".format(mb)
    }
}

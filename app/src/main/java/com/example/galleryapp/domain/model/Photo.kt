package com.example.galleryapp.domain.model

data class Photo(
    val id: Long,
    val displayName: String,
    val dateTaken: Long,
    val size: Long,
    val mimeType: String,
    val placeholderColor: Long,
    val width: Int = 0,
    val height: Int = 0,
    val isFavorite: Boolean = false,
    val uri: String? = null,
    val duration: Long = 0L
)

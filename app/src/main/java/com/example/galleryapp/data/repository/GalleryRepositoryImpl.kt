package com.example.galleryapp.data.repository

import com.example.galleryapp.domain.model.Album
import com.example.galleryapp.domain.model.AlbumType
import com.example.galleryapp.domain.model.CleanerCategory
import com.example.galleryapp.domain.model.Photo
import com.example.galleryapp.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GalleryRepositoryImpl : GalleryRepository {

    private val sampleColors = listOf(
        0xFF1E3A5FL, 0xFF2D5A27L, 0xFF5C1F1FL, 0xFF3D1F5CL,
        0xFF1F4D5CL, 0xFF5C3D1FL, 0xFF1F5C3DL, 0xFF5C1F4DL,
        0xFF2A5C1FL, 0xFF5C2A1FL, 0xFF1F2A5CL, 0xFF5C1F2AL,
        0xFF3B3B1FL, 0xFF1F3B3BL, 0xFF3B1F3BL, 0xFF1F3B1FL,
        0xFF4A2020L, 0xFF204A20L, 0xFF20204AL, 0xFF4A4A20L,
        0xFF204A4AL, 0xFF4A204AL, 0xFF602030L, 0xFF306020L
    )

    private val samplePhotos = (1L..48L).map { id ->
        val colorIndex = ((id - 1) % sampleColors.size).toInt()
        Photo(
            id = id,
            displayName = "IMG_${2024000 + id}.jpg",
            dateTaken = System.currentTimeMillis() - (id * 3_600_000L),
            size = 2_500_000L + (id * 100_000L),
            mimeType = "image/jpeg",
            placeholderColor = sampleColors[colorIndex],
            width = 4032,
            height = 3024,
            isFavorite = id % 7L == 0L
        )
    }

    private val sampleAlbums = listOf(
        Album(1L, "Camera", 1284, 0xFF1E3A5FL, AlbumType.Featured),
        Album(2L, "Screenshots", 326, 0xFF2D5A27L, AlbumType.Featured),
        Album(3L, "WhatsApp", 2104, 0xFF25D366L, AlbumType.Featured),
        Album(4L, "Downloads", 87, 0xFF5C3D1FL, AlbumType.Featured),
        Album(5L, "Goa Trip", 142, 0xFF1F4D5CL, AlbumType.User),
        Album(6L, "Family", 89, 0xFF5C1F1FL, AlbumType.User),
        Album(7L, "Receipts", 64, 0xFF3D1F5CL, AlbumType.User),
        Album(8L, "Selfies", 218, 0xFF5C2A1FL, AlbumType.User),
        Album(9L, "Pets", 47, 0xFF2D5A27L, AlbumType.User),
        Album(10L, "Documents", 31, 0xFF1F3B3BL, AlbumType.User),
        Album(11L, "Instagram", 412, 0xFFE1306CL, AlbumType.Social),
        Album(12L, "Telegram", 178, 0xFF0088CCL, AlbumType.Social),
    )

    private val vaultPhotos = (101L..118L).map { id ->
        val colorIndex = ((id - 101) % sampleColors.size).toInt()
        Photo(
            id = id,
            displayName = "VAULT_${id}.jpg",
            dateTaken = System.currentTimeMillis() - (id * 7_200_000L),
            size = 1_800_000L,
            mimeType = "image/jpeg",
            placeholderColor = sampleColors[colorIndex]
        )
    }

    private val cleanerCategories = listOf(
        CleanerCategory("Duplicate photos", 124, 1_887_436_800L, 0xFFEF4444L),
        CleanerCategory("Similar photos", 86, 964_689_920L, 0xFF8B5CF6L),
        CleanerCategory("Blurry photos", 23, 220_200_960L, 0xFF6366F1L),
        CleanerCategory("Large videos", 7, 3_435_973_837L, 0xFFF59E0BL),
        CleanerCategory("Old screenshots", 412, 713_031_680L, 0xFF10B981L),
        CleanerCategory("WhatsApp media", 1241, 2_516_582_400L, 0xFF25D366L),
    )

    override fun getPhotos(): Flow<List<Photo>> = flowOf(samplePhotos)

    override fun getAlbums(): Flow<List<Album>> = flowOf(sampleAlbums)

    override fun getVaultPhotos(): Flow<List<Photo>> = flowOf(vaultPhotos)

    override fun getCleanerCategories(): List<CleanerCategory> = cleanerCategories

    override fun getPhotoById(id: Long): Photo? = samplePhotos.find { it.id == id }
}

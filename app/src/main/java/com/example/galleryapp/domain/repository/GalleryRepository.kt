package com.example.galleryapp.domain.repository

import com.example.galleryapp.domain.model.Album
import com.example.galleryapp.domain.model.CleanerCategory
import com.example.galleryapp.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPhotos(): Flow<List<Photo>>
    fun getAlbums(): Flow<List<Album>>
    fun getVaultPhotos(): Flow<List<Photo>>
    fun getCleanerCategories(): List<CleanerCategory>
    fun getPhotoById(id: Long): Photo?
}

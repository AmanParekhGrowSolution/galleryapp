package com.example.galleryapp.domain.model

data class Album(
    val id: Long,
    val name: String,
    val count: Int,
    val coverColor: Long,
    val type: AlbumType = AlbumType.User
)

enum class AlbumType { Featured, User, Social, Utility }

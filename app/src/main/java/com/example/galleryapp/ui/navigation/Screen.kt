package com.example.galleryapp.ui.navigation

object Screen {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Main = "main"
    const val Home = "home"
    const val Albums = "albums"
    const val Search = "search"
    const val Moments = "moments"
    const val Vault = "vault"
    const val Cleaner = "cleaner"
    const val Settings = "settings"
    const val Premium = "premium"
    const val Map = "map"
    const val Trash = "trash"
    const val Storage = "storage"
    const val Backup = "backup"
    const val PhotoViewer = "photo_viewer/{photoId}"
    const val AIEditor = "ai_editor/{photoId}"

    fun photoViewer(photoId: Long) = "photo_viewer/$photoId"
    fun aiEditor(photoId: Long) = "ai_editor/$photoId"
}

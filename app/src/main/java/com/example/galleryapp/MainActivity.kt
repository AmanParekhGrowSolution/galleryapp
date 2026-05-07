package com.example.galleryapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.galleryapp.ui.navigation.AppNavigation
import com.example.galleryapp.ui.theme.GalleryappTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* ViewModels re-check permissions when composing; no explicit callback needed */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) requestMediaPermissionsIfNeeded()
        setContent {
            GalleryappTheme {
                AppNavigation()
            }
        }
    }

    private fun requestMediaPermissionsIfNeeded() {
        val needed = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!isGranted(Manifest.permission.READ_MEDIA_IMAGES)) add(Manifest.permission.READ_MEDIA_IMAGES)
                if (!isGranted(Manifest.permission.READ_MEDIA_VIDEO)) add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (!isGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                    add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (!isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
        if (needed.isNotEmpty()) {
            requestPermissionsLauncher.launch(needed.toTypedArray())
        }
    }

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

package com.example.galleryapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE8FF),
    onPrimaryContainer = Color(0xFF001645),
    secondary = AccentPurple,
    onSecondary = Color.White,
    tertiary = AccentTeal,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceDark,
    surface = SurfaceCard,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFFEEF0F5),
    onSurfaceVariant = SubtextGray,
    outline = DividerLight,
    error = AccentRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    background = DarkBg,
    onBackground = Color.White,
    surface = Color(0xFF1A1A1A),
    onSurface = Color.White,
    error = AccentRed,
    onError = Color.White,
)

@Composable
fun GalleryappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.galleryapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RadiusXs = RoundedCornerShape(4.dp)
val RadiusSm = RoundedCornerShape(8.dp)    // --radius-sm
val RadiusMd = RoundedCornerShape(12.dp)   // --radius-md
val RadiusLg = RoundedCornerShape(15.dp)   // --radius-lg
val RadiusXl = RoundedCornerShape(20.dp)
val RadiusPill = RoundedCornerShape(999.dp)

val AppShapes = Shapes(
    extraSmall = RadiusXs,
    small = RadiusSm,
    medium = RadiusMd,
    large = RadiusLg,
    extraLarge = RadiusXl,
)

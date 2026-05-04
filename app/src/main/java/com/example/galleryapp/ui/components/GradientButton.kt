package com.example.galleryapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val warningGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
private val errorGradient = listOf(Color(0xFFEF4444), Color(0xFFDC2626))

@Composable
fun PrimaryGradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GradientButton(label = label, onClick = onClick, gradient = primaryGradient, modifier = modifier)
}

@Composable
fun WarningGradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GradientButton(label = label, onClick = onClick, gradient = warningGradient, modifier = modifier)
}

@Composable
fun ErrorGradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GradientButton(label = label, onClick = onClick, gradient = errorGradient, modifier = modifier)
}

@Composable
private fun GradientButton(
    label: String,
    onClick: () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                Brush.linearGradient(gradient),
                RoundedCornerShape(25.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

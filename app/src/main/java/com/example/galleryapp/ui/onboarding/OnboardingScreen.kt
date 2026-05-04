package com.example.galleryapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.components.PrimaryGradientButton

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val accentGradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
private val successGradient = listOf(Color(0xFF10B981), Color(0xFF059669))

private data class OnboardPage(
    val icon: ImageVector,
    val gradient: List<Color>,
    val titleRes: Int,
    val subtitleRes: Int
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pages = listOf(
        OnboardPage(Icons.Default.Translate, accentGradient, R.string.choose_language, R.string.choose_language_subtitle),
        OnboardPage(Icons.Default.Lock, primaryGradient, R.string.offline_title, R.string.offline_subtitle),
        OnboardPage(Icons.Default.Storage, successGradient, R.string.storage_title, R.string.storage_subtitle),
        OnboardPage(Icons.Default.Image, primaryGradient, R.string.permission_title, R.string.permission_subtitle),
    )

    val page = pages.getOrNull(uiState.currentPage) ?: pages.last()
    val isLast = viewModel.isLastPage()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        if (uiState.currentPage < pages.size - 1) {
            Text(
                text = stringResource(R.string.skip),
                color = Color.White.copy(alpha = 0.87f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .clickable { onComplete() },
                fontSize = 15.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Brush.linearGradient(page.gradient))
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(page.titleRes),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(page.subtitleRes),
                color = Color.White.copy(alpha = 0.87f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            if (uiState.currentPage == 0) {
                Spacer(modifier = Modifier.height(24.dp))
                LanguageItem("English", uiState.selectedLanguage == "English") {
                    viewModel.selectLanguage("English")
                }
                LanguageItem("हिंदी", uiState.selectedLanguage == "हिंदी") {
                    viewModel.selectLanguage("हिंदी")
                }
                LanguageItem("Español", uiState.selectedLanguage == "Español") {
                    viewModel.selectLanguage("Español")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            PageIndicator(currentPage = uiState.currentPage, totalPages = uiState.totalPages)

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryGradientButton(
                label = when {
                    isLast -> stringResource(R.string.allow_continue)
                    uiState.currentPage == 0 -> stringResource(R.string.next)
                    else -> stringResource(R.string.next)
                },
                onClick = {
                    if (isLast) onComplete() else viewModel.nextPage()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LanguageItem(name: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PageIndicator(currentPage: Int, totalPages: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (index == currentPage) 22.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage)
                            Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)))
                        else
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.3f)
                                )
                            )
                    )
            )
        }
    }
}

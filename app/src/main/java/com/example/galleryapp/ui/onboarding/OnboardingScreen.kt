package com.example.galleryapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import androidx.compose.material3.Icon
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.DividerLight
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

private data class LanguageOption(
    val flag: String,
    val nameRes: Int,
    val subNameRes: Int,
    val code: String
)

private val languages = listOf(
    LanguageOption("🇬🇧", R.string.lang_english, R.string.lang_english_sub, "English"),
    LanguageOption("🇮🇳", R.string.lang_hindi, R.string.lang_hindi_sub, "Hindi"),
    LanguageOption("🇪🇸", R.string.lang_spanish, R.string.lang_spanish_sub, "Spanish"),
    LanguageOption("🇧🇷", R.string.lang_portuguese, R.string.lang_portuguese_sub, "Portuguese"),
    LanguageOption("🇫🇷", R.string.lang_french, R.string.lang_french_sub, "French"),
    LanguageOption("🇩🇪", R.string.lang_german, R.string.lang_german_sub, "German"),
    LanguageOption("🇸🇦", R.string.lang_arabic, R.string.lang_arabic_sub, "Arabic"),
    LanguageOption("🇯🇵", R.string.lang_japanese, R.string.lang_japanese_sub, "Japanese"),
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (uiState.currentPage < uiState.totalPages - 1) {
            Text(
                text = stringResource(R.string.skip),
                color = SubtextGray,
                fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(20.dp)
                    .clickable { onComplete() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 96.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Text(
                text = stringResource(R.string.choose_language),
                color = OnSurfaceDark,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.choose_language_subtitle_new),
                color = SubtextGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                languages.forEach { lang ->
                    val selected = uiState.selectedLanguage == lang.code
                    LanguageRow(
                        flag = lang.flag,
                        name = stringResource(lang.nameRes),
                        subName = stringResource(lang.subNameRes),
                        selected = selected,
                        onClick = { viewModel.selectLanguage(lang.code) }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PageDots(currentPage = uiState.currentPage, total = uiState.totalPages)
            Spacer(modifier = Modifier.height(12.dp))
            NextButton(
                label = stringResource(R.string.next),
                onClick = { if (viewModel.isLastPage()) onComplete() else viewModel.nextPage() }
            )
        }
    }
}

@Composable
private fun LanguageRow(
    flag: String,
    name: String,
    subName: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFEEF3FF) else Color(0xFFF5F6FA))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) BrandBlue else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = flag, fontSize = 28.sp)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(name, color = OnSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subName, color = SubtextGray, fontSize = 13.sp)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (selected) BrandBlue else Color.White)
                .border(1.5.dp, if (selected) BrandBlue else DividerLight, CircleShape)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp).semantics { role = Role.Image }
                )
            }
        }
    }
}

@Composable
private fun PageDots(currentPage: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { index ->
            val isActive = index == currentPage
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .then(if (isActive) Modifier.size(width = 24.dp, height = 4.dp) else Modifier.size(width = 8.dp, height = 4.dp))
                    .clip(CircleShape)
                    .background(if (isActive) BrandBlue else Color(0xFFCCCCCC))
            )
        }
    }
}

@Composable
private fun NextButton(label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(BrandBlue)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

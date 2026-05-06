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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.AccentGreen
import com.example.galleryapp.ui.theme.AccentPurple
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

private data class FeaturePage(
    val icon: ImageVector,
    val iconBg: Color,
    val titleRes: Int,
    val subtitleRes: Int
)

private val featurePages = listOf(
    FeaturePage(Icons.Default.CloudOff, BrandBlue, R.string.offline_title, R.string.offline_subtitle),
    FeaturePage(Icons.Default.DeleteSweep, AccentGreen, R.string.storage_title, R.string.storage_subtitle),
    FeaturePage(Icons.Default.Lock, AccentPurple, R.string.vault_onboard_title, R.string.vault_onboard_subtitle),
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

        when (uiState.currentPage) {
            0 -> LanguagePageContent(
                uiState = uiState,
                onSelectLanguage = viewModel::selectLanguage,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = 120.dp)
            )
            else -> {
                val pageIndex = uiState.currentPage - 1
                if (pageIndex < featurePages.size) {
                    FeaturePageContent(
                        page = featurePages[pageIndex],
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(bottom = 120.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PageDots(currentPage = uiState.currentPage, total = uiState.totalPages)
            Spacer(modifier = Modifier.height(12.dp))
            val buttonLabel = if (viewModel.isLastPage()) {
                stringResource(R.string.get_started)
            } else {
                stringResource(R.string.next)
            }
            NextButton(
                label = buttonLabel,
                onClick = { if (viewModel.isLastPage()) onComplete() else viewModel.nextPage() }
            )
        }
    }
}

@Composable
private fun LanguagePageContent(
    uiState: OnboardingUiState,
    onSelectLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
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
                    onClick = { onSelectLanguage(lang.code) }
                )
            }
        }
    }
}

@Composable
private fun FeaturePageContent(
    page: FeaturePage,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(page.iconBg.copy(alpha = 0.12f))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(page.iconBg.copy(alpha = 0.18f))
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.iconBg,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { role = Role.Image }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(page.titleRes),
            color = OnSurfaceDark,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(page.subtitleRes),
            color = SubtextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
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
                    modifier = Modifier
                        .size(14.dp)
                        .semantics { role = Role.Image }
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
                    .then(
                        if (isActive) Modifier.size(width = 24.dp, height = 4.dp)
                        else Modifier.size(width = 8.dp, height = 4.dp)
                    )
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
            .background(
                Brush.linearGradient(listOf(BrandBlue, Color(0xFF4F46E5)))
            )
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

package com.example.galleryapp.ui.cleaner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CropOriginal
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.domain.model.CleanerCategory

private val ScreenBg = Color(0xFFF4F8FB)
private val StorageCardGradientStart = Color(0xFF1B52C5)
private val StorageCardGradientEnd = Color(0xFF3B7BFF)
private val StorageBarYellow = Color(0xFFF5A623)
private val TextDark = Color(0xFF1A1A2E)
private val TextSub = Color(0xFF6B7280)

@Composable
fun CleanerScreen(
    onBack: () -> Unit,
    viewModel: CleanerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.statusBarsPadding()
        ) {
            item {
                CleanerTopBar(onBack = onBack)
            }
            item {
                Spacer(Modifier.height(4.dp))
                StorageHeroCard(
                    usedGb = uiState.usedGb,
                    totalGb = uiState.totalGb,
                    freeableGb = uiState.freeableGb,
                    onCleanNow = viewModel::startCleaning
                )
            }
            item {
                Text(
                    text = stringResource(R.string.categories),
                    color = TextDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                )
            }
            item {
                CategoriesCard(categories = uiState.categories)
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                CompressSection()
            }
        }
    }
}

@Composable
private fun CleanerTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = TextDark
            )
        }
        Text(
            text = stringResource(R.string.cleaner_title),
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Info,
                contentDescription = stringResource(R.string.info),
                tint = TextDark
            )
        }
    }
}

@Composable
private fun StorageHeroCard(
    usedGb: Float,
    totalGb: Float,
    freeableGb: Float,
    onCleanNow: () -> Unit
) {
    val photosGb = (usedGb * 0.48f)
    val otherGb = usedGb - photosGb

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(StorageCardGradientStart, StorageCardGradientEnd)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.storage).uppercase(),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "%.1f".format(usedGb),
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "/ %.0f GB used".format(totalGb),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            StorageBar(usedGb = usedGb, totalGb = totalGb, photosGb = photosGb)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StorageLegendItem(
                    color = StorageBarYellow,
                    label = "${stringResource(R.string.storage_photos_video)} %.1f GB".format(photosGb)
                )
                StorageLegendItem(
                    color = Color.White,
                    label = "${stringResource(R.string.storage_other)} %.1f GB".format(otherGb)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.you_can_free_up),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "%.1f GB".format(freeableGb),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                        .clickable(onClick = onCleanNow)
                        .padding(horizontal = 22.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.clean_now),
                        color = StorageCardGradientStart,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageBar(usedGb: Float, totalGb: Float, photosGb: Float) {
    val usedFraction = (usedGb / totalGb).coerceIn(0f, 1f)
    val photosFraction = (photosGb / totalGb).coerceIn(0f, usedFraction)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.25f))
    ) {
        // Photos & Video segment (yellow)
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = photosFraction)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(StorageBarYellow)
        )
        // Other segment (white), starts after photos fraction
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = usedFraction)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.5f))
        )
        // Photos re-drawn on top to cover the white overlap
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = photosFraction)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(StorageBarYellow)
        )
    }
}

@Composable
private fun StorageLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun CategoriesCard(categories: List<CleanerCategory>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        categories.forEachIndexed { index, category ->
            CleanerCategoryRow(category = category)
            if (index < categories.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFE5E7EB)
                )
            }
        }
    }
}

@Composable
private fun CleanerCategoryRow(category: CleanerCategory) {
    val accentColor = Color(category.colorHex)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon(category.name),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = category.name,
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${category.count} items${stringResource(R.string.separator_dot)}${category.sizeLabel}",
                color = TextSub,
                fontSize = 12.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(accentColor)
                .clickable {}
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.review),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun categoryIcon(name: String): ImageVector = when {
    name.contains("Duplicate", ignoreCase = true) -> Icons.Outlined.ContentCopy
    name.contains("Similar", ignoreCase = true) -> Icons.Outlined.GridView
    name.contains("Blurry", ignoreCase = true) -> Icons.Outlined.BlurOn
    name.contains("video", ignoreCase = true) -> Icons.Outlined.Videocam
    name.contains("screenshot", ignoreCase = true) -> Icons.Outlined.CropOriginal
    name.contains("WhatsApp", ignoreCase = true) -> Icons.Outlined.Forum
    else -> Icons.Outlined.CropOriginal
}

@Composable
private fun CompressSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.compress_convert),
            color = TextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CompressTile(
                label = stringResource(R.string.compress_photos),
                subtitle = stringResource(R.string.save_pct_photos),
                icon = Icons.Outlined.PhotoSizeSelectSmall,
                gradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)),
                modifier = Modifier.weight(1f)
            )
            CompressTile(
                label = stringResource(R.string.compress_videos),
                subtitle = stringResource(R.string.save_pct_videos),
                icon = Icons.Outlined.VideoFile,
                gradient = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CompressTile(
    label: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(gradient))
            .clickable {}
            .padding(16.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .padding(bottom = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.80f), fontSize = 12.sp)
        }
    }
}

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.domain.model.CleanerCategory

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))

@Composable
fun CleanerScreen(
    onBack: () -> Unit,
    viewModel: CleanerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.statusBarsPadding()
        ) {
            item {
                CleanerTopBar(onBack = onBack)
            }
            item {
                StorageHeroCard(
                    usedGb = uiState.usedGb,
                    totalGb = uiState.totalGb,
                    freeableGb = uiState.freeableGb,
                    onCleanNow = viewModel::startCleaning
                )
            }
            item {
                Text(
                    text = "Categories",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
                )
            }
            items(uiState.categories) { category ->
                CleanerCategoryRow(category = category)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
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
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
        }
        Text(
            text = stringResource(R.string.cleaner_title),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(Icons.Default.Info, contentDescription = stringResource(R.string.info), tint = Color.White)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(primaryGradient))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.storage_used, "%.1f GB".format(usedGb), "%.0f GB".format(totalGb)),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.1f GB free".format(totalGb - usedGb),
                color = Color.White.copy(alpha = 0.87f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            StorageBar(usedGb = usedGb, totalGb = totalGb)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.free_up, "%.1f GB".format(freeableGb)),
                    color = Color.White,
                    fontSize = 14.sp
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Color.White, Color(0xFFF8F8F8))))
                        .clickable(onClick = onCleanNow)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.clean_now),
                        color = Color(0xFF6366F1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageBar(usedGb: Float, totalGb: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f))))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = (usedGb / totalGb).coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.linearGradient(listOf(Color.White, Color(0xFFF0F0F0))))
        )
    }
}

@Composable
private fun CleanerCategoryRow(category: CleanerCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(Color(category.colorHex).copy(alpha = 0.2f), Color(category.colorHex).copy(alpha = 0.2f)))),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Brush.linearGradient(listOf(Color(category.colorHex), Color(category.colorHex).copy(alpha = 0.85f))))
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(category.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(
                text = "${category.count} items · ${category.sizeLabel}",
                color = Color.White.copy(alpha = 0.87f),
                fontSize = 12.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(Color(category.colorHex).copy(alpha = 0.2f), Color(category.colorHex).copy(alpha = 0.2f))))
                .clickable {}
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.review),
                color = Color(category.colorHex),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CompressSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Compress & Convert",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CompressTile(
                label = "Compress photos",
                subtitle = "Save ~30%",
                gradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)),
                modifier = Modifier.weight(1f)
            )
            CompressTile(
                label = "Compress videos",
                subtitle = "Save ~50%",
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
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
        }
    }
}

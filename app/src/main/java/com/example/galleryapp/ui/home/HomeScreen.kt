package com.example.galleryapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.galleryapp.domain.model.Photo

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val accentGradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
private val successGradient = listOf(Color(0xFF10B981), Color(0xFF059669))
private val errorGradient = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
private val warningGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706))

@Composable
fun HomeScreen(
    onPhotoClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onVaultClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is HomeUiState.Success -> {
                HomeContent(
                    state = state,
                    onPhotoClick = onPhotoClick,
                    onSettingsClick = onSettingsClick,
                    onPremiumClick = onPremiumClick,
                    onVaultClick = onVaultClick,
                    onFilterSelect = viewModel::selectFilter,
                    onToggleSelection = viewModel::togglePhotoSelection
                )
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onPhotoClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onVaultClick: () -> Unit,
    onFilterSelect: (PhotoFilter) -> Unit,
    onToggleSelection: (Long) -> Unit
) {
    val filters = PhotoFilter.entries

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeTopBar(
                onSettingsClick = onSettingsClick,
                onPremiumClick = onPremiumClick
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FilterChipsRow(
                filters = filters,
                selectedFilter = state.selectedFilter,
                onFilterSelect = onFilterSelect
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            QuickAccessRow(onVaultClick = onVaultClick)
        }

        state.sections.forEach { section ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(label = section.dateLabel)
            }
            items(section.photos) { photo ->
                PhotoThumbnailItem(
                    photo = photo,
                    isSelected = photo.id in state.selectedIds,
                    selectionMode = state.selectionMode,
                    onClick = {
                        if (state.selectionMode) onToggleSelection(photo.id)
                        else onPhotoClick(photo.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit,
    onPremiumClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.content_desc_menu),
                tint = Color.White
            )
        }
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            color = Color(0xFF8B5CF6),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.weight(1f)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(warningGradient))
                .clickable(onClick = onPremiumClick)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.try_free),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.content_desc_search),
                tint = Color.White
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.content_desc_more),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun FilterChipsRow(
    filters: List<PhotoFilter>,
    selectedFilter: PhotoFilter,
    onFilterSelect: (PhotoFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected)
                            Brush.linearGradient(primaryGradient)
                        else
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                    )
                    .clickable { onFilterSelect(filter) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = filter.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun QuickAccessRow(onVaultClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickTile(
            icon = Icons.Default.Favorite,
            label = stringResource(R.string.favorites),
            gradient = errorGradient,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        QuickTile(
            icon = Icons.Default.CameraAlt,
            label = stringResource(R.string.camera),
            gradient = primaryGradient,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        QuickTile(
            icon = Icons.Default.Schedule,
            label = stringResource(R.string.recent),
            gradient = successGradient,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        QuickTile(
            icon = Icons.Default.Lock,
            label = stringResource(R.string.vault_album),
            gradient = accentGradient,
            onClick = onVaultClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickTile(
    icon: ImageVector,
    label: String,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradient))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.87f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionHeader(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.select),
            color = Color(0xFF8B5CF6),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun PhotoThumbnailItem(
    photo: Photo,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Brush.linearGradient(listOf(Color(photo.placeholderColor), Color(photo.placeholderColor).copy(alpha = 0.85f))))
            .clickable(onClick = onClick)
    ) {
        if (selectionMode) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected)
                            Brush.linearGradient(primaryGradient)
                        else
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f))
                            )
                    )
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

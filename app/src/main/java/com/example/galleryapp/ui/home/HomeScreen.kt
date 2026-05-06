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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.domain.model.Photo
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray
import com.example.galleryapp.ui.theme.SurfaceLight
import com.example.galleryapp.ui.theme.TryFreeGold
import com.example.galleryapp.ui.theme.TryFreeGoldLight

private val tryFreeGradient = listOf(TryFreeGold, TryFreeGoldLight)

private data class QuickItem(
    val icon: ImageVector,
    val labelRes: Int,
    val tintColor: Color
)

private val quickItems = listOf(
    QuickItem(Icons.Default.Favorite, R.string.quick_access_favorites, Color(0xFFEF4444)),
    QuickItem(Icons.Default.CameraAlt, R.string.quick_access_camera, Color(0xFF14B8A6)),
    QuickItem(Icons.Default.Update, R.string.quick_access_recent, Color(0xFFF97316)),
    QuickItem(Icons.Default.Lock, R.string.quick_access_vault, Color(0xFF1A6BFF)),
)

@Composable
fun HomeScreen(
    onPhotoClick: (Long) -> Unit,
    onVaultClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> CircularProgressIndicator(
                color = BrandBlue,
                modifier = Modifier.align(Alignment.Center)
            )
            is HomeUiState.Success -> HomeContent(
                state = state,
                onPhotoClick = onPhotoClick,
                onVaultClick = onVaultClick,
                onFilterSelect = viewModel::selectFilter,
                onToggleSelection = viewModel::togglePhotoSelection
            )
            is HomeUiState.Error -> Text(
                text = state.message,
                color = OnSurfaceDark,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onPhotoClick: (Long) -> Unit,
    onVaultClick: () -> Unit,
    onFilterSelect: (PhotoFilter) -> Unit,
    onToggleSelection: (Long) -> Unit
) {
    val filters = PhotoFilter.entries

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeTopBar()
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
            items(section.photos, key = { it.id }) { photo ->
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
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.content_desc_menu),
                tint = OnSurfaceDark
            )
        }
        Text(
            text = stringResource(R.string.gallery_title),
            color = BrandBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1f)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(tryFreeGradient))
                .clickable {}
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.try_free),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.content_desc_search),
                tint = OnSurfaceDark
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
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) BrandBlue else Color(0xFFF0F1F5))
                    .clickable { onFilterSelect(filter) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = filter.name,
                    color = if (isSelected) Color.White else SubtextGray,
                    fontSize = 13.sp,
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
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        quickItems.forEachIndexed { index, item ->
            val onClick = if (index == 3) onVaultClick else ({})
            QuickAccessItem(item = item, onClick = onClick)
        }
    }
}

@Composable
private fun QuickAccessItem(item: QuickItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(item.tintColor.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.labelRes),
                tint = item.tintColor,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(item.labelRes),
            color = SubtextGray,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SectionHeader(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = OnSurfaceDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.select),
            color = BrandBlue,
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
            .background(Color(photo.placeholderColor))
            .clickable(onClick = onClick)
    ) {
        if (selectionMode) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) BrandBlue else Color.White.copy(alpha = 0.6f))
            ) {
                if (isSelected) {
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
}

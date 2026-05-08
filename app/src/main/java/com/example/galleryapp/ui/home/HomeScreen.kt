package com.example.galleryapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
    onSettingsClick: () -> Unit = {},
    onPremiumClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val cameraUnavailableMsg = stringResource(R.string.camera_app_unavailable)
    val cameraPermissionDeniedMsg = stringResource(R.string.camera_permission_denied)

    val launchCameraIntent: () -> Unit = remember(context) {
        {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, cameraUnavailableMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCameraIntent()
        else Toast.makeText(context, cameraPermissionDeniedMsg, Toast.LENGTH_SHORT).show()
    }

    val onCameraClick: () -> Unit = remember(context, cameraPermissionLauncher) {
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCameraIntent()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

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
                onCameraClick = onCameraClick,
                onSettingsClick = onSettingsClick,
                onPremiumClick = onPremiumClick,
                onSearchClick = onSearchClick,
                onFilterSelect = viewModel::selectFilter,
                onToggleSelection = viewModel::togglePhotoSelection,
                onToggleSelectionMode = viewModel::toggleSelectionMode
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
    onCameraClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFilterSelect: (PhotoFilter) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onToggleSelectionMode: () -> Unit
) {
    val filters = PhotoFilter.entries

    LazyVerticalGrid(
        columns = GridCells.Fixed(state.gridColumns),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeTopBar(
                selectionMode = state.selectionMode,
                selectedCount = state.selectedIds.size,
                onCancelSelection = onToggleSelectionMode,
                onShareSelected = {},
                onDeleteSelected = {},
                onSettingsClick = onSettingsClick,
                onPremiumClick = onPremiumClick,
                onSearchClick = onSearchClick
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
            QuickAccessRow(onCameraClick = onCameraClick, onVaultClick = onVaultClick)
        }

        state.sections.forEach { section ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(label = section.dateLabel, onToggleSelectionMode = onToggleSelectionMode)
            }
            items(section.photos, key = { it.id }) { photo ->
                PhotoThumbnailItem(
                    photo = photo,
                    isSelected = photo.id in state.selectedIds,
                    selectionMode = state.selectionMode,
                    roundedThumbs = state.roundedThumbs,
                    showVideoDuration = state.showVideoDuration,
                    onClick = {
                        if (state.selectionMode) onToggleSelection(photo.id)
                        else onPhotoClick(photo.id)
                    },
                    onLongClick = {
                        if (!state.selectionMode) {
                            onToggleSelectionMode()
                            onToggleSelection(photo.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    selectionMode: Boolean,
    selectedCount: Int,
    onCancelSelection: () -> Unit,
    onShareSelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    onSettingsClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    if (selectionMode) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancelSelection) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cancel),
                    tint = OnSurfaceDark
                )
            }
            Text(
                text = stringResource(R.string.home_selected_count, selectedCount),
                color = OnSurfaceDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onShareSelected) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.content_desc_share),
                    tint = OnSurfaceDark
                )
            }
            IconButton(onClick = onDeleteSelected) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.content_desc_delete),
                    tint = OnSurfaceDark
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSettingsClick) {
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
                    .clickable(onClick = onPremiumClick)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.try_free),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.content_desc_search),
                    tint = OnSurfaceDark
                )
            }
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
private fun QuickAccessRow(onCameraClick: () -> Unit = {}, onVaultClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        quickItems.forEachIndexed { index, item ->
            val onClick = when (index) {
                1 -> onCameraClick
                3 -> onVaultClick
                else -> ({})
            }
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
private fun SectionHeader(label: String, onToggleSelectionMode: () -> Unit) {
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
            fontSize = 13.sp,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clickable(onClick = onToggleSelectionMode)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoThumbnailItem(
    photo: Photo,
    isSelected: Boolean,
    selectionMode: Boolean,
    roundedThumbs: Boolean,
    showVideoDuration: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (roundedThumbs) 12.dp else 2.dp,
        label = "thumbnailCornerRadius"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(photo.placeholderColor))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        if (photo.uri != null) {
            AsyncImage(
                model = photo.uri.toUri(),
                contentDescription = photo.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showVideoDuration && photo.mimeType.startsWith("video/") && photo.duration > 0) {
            val totalSeconds = photo.duration / 1000
            val mm = totalSeconds / 60
            val ss = totalSeconds % 60
            val durationText = "%d:%02d".format(mm, ss)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = durationText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

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
                        contentDescription = stringResource(R.string.content_desc_selected),
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

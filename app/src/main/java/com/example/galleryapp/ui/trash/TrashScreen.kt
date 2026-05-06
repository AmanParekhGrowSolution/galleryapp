package com.example.galleryapp.ui.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val dangerGradient = listOf(Color(0xFFEF4444), Color(0xFFDC2626))

@Composable
fun TrashScreen(
    onBack: () -> Unit,
    viewModel: TrashViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        when (val state = uiState) {
            is TrashUiState.Loading -> Unit
            is TrashUiState.Success -> {
                TrashContent(
                    state = state,
                    onBack = {
                        if (state.selectMode) viewModel.exitSelectMode() else onBack()
                    },
                    onSelectAll = viewModel::selectAll,
                    onEnterSelect = viewModel::enterSelectMode,
                    onToggleItem = viewModel::toggleSelection,
                    onRestore = viewModel::restoreSelected,
                    onDelete = viewModel::deleteSelected
                )
            }
        }
    }
}

@Composable
private fun TrashContent(
    state: TrashUiState.Success,
    onBack: () -> Unit,
    onSelectAll: () -> Unit,
    onEnterSelect: () -> Unit,
    onToggleItem: (Long) -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(bottom = if (state.selectMode) 96.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                TrashTopBar(
                    selectMode = state.selectMode,
                    selectedCount = state.selectedIds.size,
                    itemCount = state.items.size,
                    onBack = onBack,
                    onSelectToggle = if (state.selectMode) onSelectAll else onEnterSelect
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                TrashInfoBanner()
            }

            items(state.items, key = { it.id }) { item ->
                TrashPhotoCell(
                    item = item,
                    selectMode = state.selectMode,
                    selected = item.id in state.selectedIds,
                    onToggle = { onToggleItem(item.id) },
                    onLongPress = { if (!state.selectMode) onEnterSelect() }
                )
            }
        }

        if (state.selectMode && state.selectedIds.isNotEmpty()) {
            TrashActionBar(
                selectedCount = state.selectedIds.size,
                onRestore = onRestore,
                onDelete = onDelete,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun TrashTopBar(
    selectMode: Boolean,
    selectedCount: Int,
    itemCount: Int,
    onBack: () -> Unit,
    onSelectToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = Color.White
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (selectMode)
                    stringResource(R.string.trash_selected_count, selectedCount)
                else
                    stringResource(R.string.recently_deleted),
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            if (!selectMode) {
                Text(
                    text = stringResource(R.string.trash_items_auto_delete, itemCount),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        TextButton(onClick = onSelectToggle) {
            Text(
                text = if (selectMode)
                    stringResource(R.string.select_all)
                else
                    stringResource(R.string.select),
                color = Color(0xFF818CF8),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun TrashInfoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF59E0B).copy(alpha = 0.12f))
            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.trash_auto_delete_info),
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun TrashPhotoCell(
    item: TrashItem,
    selectMode: Boolean,
    selected: Boolean,
    onToggle: () -> Unit,
    onLongPress: () -> Unit
) {
    val urgent = item.daysLeft <= 5
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(onClick = onToggle)
    ) {
        // Placeholder thumbnail gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E1B4B).copy(alpha = 0.8f),
                            Color(0xFF312E81).copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // Days left badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(
                    if (urgent) Color(0xFFEC4899).copy(alpha = 0.95f)
                    else Color.Black.copy(alpha = 0.65f)
                )
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${item.daysLeft}d",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.2.sp
            )
        }

        // Selection overlay
        if (selectMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (selected) Color(0xFF6366F1).copy(alpha = 0.35f)
                        else Color.Transparent
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected)
                            Brush.linearGradient(primaryGradient)
                        else
                            Brush.linearGradient(listOf(Color.Black.copy(0.4f), Color.Black.copy(0.4f)))
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrashActionBar(
    selectedCount: Int,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1B4B).copy(alpha = 0.97f))
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Restore button
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(25.dp))
                .background(Brush.linearGradient(primaryGradient))
                .clickable(onClick = onRestore)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RestoreFromTrash,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = stringResource(R.string.restore_selected, selectedCount),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Delete button
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(25.dp))
                .background(Brush.linearGradient(dangerGradient))
                .clickable(onClick = onDelete)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = stringResource(R.string.delete_selected, selectedCount),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

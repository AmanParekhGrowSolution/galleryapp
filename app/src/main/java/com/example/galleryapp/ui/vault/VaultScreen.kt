package com.example.galleryapp.ui.vault

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))

@Composable
fun VaultScreen(
    onBack: () -> Unit,
    onNeedsSetup: () -> Unit,
    viewModel: VaultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        when (val state = uiState) {
            is VaultUiState.NeedsSetup -> {
                LaunchedEffect(Unit) { onNeedsSetup() }
            }
            is VaultUiState.Locked -> LockedVault(
                state = state,
                onBack = onBack,
                onDigit = viewModel::appendDigit,
                onDelete = viewModel::deleteDigit
            )
            is VaultUiState.Unlocked -> UnlockedVault(
                state = state,
                onBack = onBack,
                onLock = viewModel::lock,
                onFilterSelect = viewModel::selectFilter
            )
        }
    }
}

@Composable
private fun LockedVault(
    state: VaultUiState.Locked,
    onBack: () -> Unit,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(primaryGradient))
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.content_desc_vault_lock),
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.vault_title),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.vault_subtitle),
            color = Color.White.copy(alpha = 0.87f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        PinDotsRow(pinLength = state.enteredPin.length, hasError = state.showError)

        if (state.showError) {
            Text(
                text = stringResource(R.string.incorrect_pin),
                color = Color(0xFFEF4444),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        NumericKeypad(onDigit = onDigit, onDelete = onDelete)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PinDotsRow(pinLength: Int, hasError: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        repeat(4) { index ->
            val filled = index < pinLength
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            hasError -> Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
                            filled -> Brush.linearGradient(primaryGradient)
                            else -> Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f)))
                        }
                    )
            )
        }
    }
}

@Composable
private fun NumericKeypad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "biometric", "0", "delete")
    val rows = keys.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { key ->
                    when (key) {
                        "biometric" -> Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        "delete" -> Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clickable { onDelete() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        else -> Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.06f))))
                                .clickable { onDigit(key) }
                        ) {
                            Text(key, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UnlockedVault(
    state: VaultUiState.Unlocked,
    onBack: () -> Unit,
    onLock: () -> Unit,
    onFilterSelect: (String) -> Unit
) {
    val filters = listOf("All", "Personal", "Documents", "Receipts")

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            UnlockedTopBar(onBack = onBack, onLock = onLock)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            PrivacyNoticeCard()
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            FilterChips(
                filters = filters,
                selectedFilter = state.selectedFilter,
                onFilterSelect = onFilterSelect
            )
        }
        items(state.photos) { photo ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Brush.linearGradient(listOf(Color(photo.placeholderColor), Color(photo.placeholderColor).copy(alpha = 0.85f))))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(primaryGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}

@Composable
private fun UnlockedTopBar(onBack: () -> Unit, onLock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.vault_title), color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.auto_locks), color = Color.White.copy(alpha = 0.87f), fontSize = 11.sp)
        }
        IconButton(onClick = {}) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add), tint = Color.White) }
        IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.content_desc_more), tint = Color.White) }
    }
}

@Composable
private fun PrivacyNoticeCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6366F1).copy(alpha = 0.15f), Color(0xFF8B5CF6).copy(alpha = 0.15f))
                )
            )
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(stringResource(R.string.end_to_end_private), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.hidden_from_gallery), color = Color.White.copy(alpha = 0.87f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
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
                            Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.1f)))
                    )
                    .clickable { onFilterSelect(filter) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(filter, color = Color.White, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}

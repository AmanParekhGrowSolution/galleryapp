package com.example.galleryapp.ui.security

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.BrandBlueDark

@Composable
fun AppLockSetupScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AppLockSetupViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AppLockSetupUiState.Saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_back),
                    tint = Color(0xFF0F1115),
                )
            }
            Text(
                text = stringResource(R.string.app_lock_setup_title),
                color = Color(0xFF0F1115),
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        when (val state = uiState) {
            is AppLockSetupUiState.PinEntry -> {
                PinEntryContent(
                    state = state,
                    onEnterDigit = viewModel::enterDigit,
                    onDelete = viewModel::deleteDigit,
                    onConfirm = viewModel::confirmPin,
                    onToggleBiometric = viewModel::toggleBiometric,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 28.dp),
                )
            }
            is AppLockSetupUiState.Saved -> Unit
        }
    }
}

@Composable
private fun PinEntryContent(
    state: AppLockSetupUiState.PinEntry,
    onEnterDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    onToggleBiometric: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(BrandBlue, BrandBlueDark))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp).semantics { role = Role.Image },
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = if (state.isConfirming) stringResource(R.string.app_lock_confirm_title) else stringResource(R.string.app_lock_create_title),
                color = Color(0xFF0F1115),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (state.isConfirming) stringResource(R.string.app_lock_confirm_subtitle) else stringResource(R.string.app_lock_create_subtitle),
                color = Color(0xFF597393),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )

            if (state.errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.errorMessage,
                    color = Color(0xFFFF3D7F),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(32.dp))

            PinDots(pin = state.pin)

            if (state.isConfirming) {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (state.enableBiometric) Color(0xFFE8F0FF) else Color(0xFFF4F8FB)
                        )
                        .border(
                            width = 1.dp,
                            color = if (state.enableBiometric) BrandBlue else Color(0xFFE8EFF6),
                            shape = RoundedCornerShape(18.dp),
                        )
                        .clickable(onClick = onToggleBiometric)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = stringResource(R.string.app_lock_biometric),
                            tint = BrandBlue,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.app_lock_biometric),
                            color = BrandBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        PinKeypad(
            pin = state.pin,
            isConfirming = state.isConfirming,
            onEnterDigit = onEnterDigit,
            onDelete = onDelete,
            onConfirm = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun PinDots(pin: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(4) { i ->
            val filled = i < pin.length
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (filled) BrandBlue else Color.Transparent)
                    .then(
                        if (!filled) Modifier.border(2.dp, Color(0xFFCBD5E1), CircleShape)
                        else Modifier
                    ),
            )
        }
    }
}

@Composable
private fun PinKeypad(
    pin: String,
    isConfirming: Boolean,
    onEnterDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keys = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, -1, 0, -2)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(keys, key = { i, _ -> i }) { _, key ->
            when {
                key == -1 -> {
                    if (!isConfirming) {
                        KeypadButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = stringResource(R.string.app_lock_biometric),
                                tint = Color(0xFF597393),
                                modifier = Modifier.size(26.dp),
                            )
                        }
                    } else {
                        Box(modifier = Modifier.size(64.dp))
                    }
                }
                key == -2 -> {
                    if (pin.length == 4) {
                        KeypadButton(
                            onClick = onConfirm,
                            background = BrandBlue,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.app_lock_confirm_pin),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    } else {
                        KeypadButton(
                            onClick = onDelete,
                            background = Color.Transparent,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.delete),
                                tint = Color(0xFF597393),
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
                else -> {
                    KeypadButton(onClick = { onEnterDigit(key) }) {
                        Text(
                            text = "$key",
                            color = Color(0xFF0F1115),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    onClick: () -> Unit,
    background: Color = Color(0xFFF4F8FB),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

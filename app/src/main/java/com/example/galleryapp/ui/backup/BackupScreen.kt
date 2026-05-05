package com.example.galleryapp.ui.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val successGradient = listOf(Color(0xFF10B981), Color(0xFF059669))

@Composable
fun BackupScreen(
    onBack: () -> Unit,
    viewModel: BackupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item {
                BackupTopBar(onBack = onBack)
            }

            item {
                BackupStatusHero(
                    uiState = uiState,
                    onToggle = viewModel::toggleEnabled
                )
            }

            item {
                SectionLabel(stringResource(R.string.backup_cloud_destination))
            }

            item {
                ProviderList(
                    providers = uiState.providers,
                    selectedProvider = uiState.provider,
                    onSelectProvider = viewModel::selectProvider
                )
            }

            item {
                SectionLabel(stringResource(R.string.backup_settings_label))
            }

            item {
                BackupSettingsCard(
                    uiState = uiState,
                    onToggleWifi = viewModel::toggleWifiOnly,
                    onToggleCharging = viewModel::toggleChargingOnly,
                    onToggleVault = viewModel::toggleIncludeVault
                )
            }
        }
    }
}

@Composable
private fun BackupTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = Color.White
            )
        }
        Text(
            text = stringResource(R.string.backup_title),
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun BackupStatusHero(uiState: BackupUiState, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (uiState.enabled)
                    Brush.linearGradient(successGradient)
                else
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.06f)))
            )
            .then(
                if (!uiState.enabled)
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                else
                    Modifier
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (uiState.enabled) Color.White.copy(alpha = 0.22f)
                            else Color.White.copy(alpha = 0.06f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.enabled) Icons.Default.Security else Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = if (uiState.enabled) Color.White else Color(0xFF818CF8),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.enabled)
                            stringResource(R.string.backup_on)
                        else
                            stringResource(R.string.backup_off),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (uiState.enabled)
                            stringResource(R.string.backup_last_synced, uiState.backedUpCount)
                        else
                            stringResource(R.string.backup_device_only),
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.5.sp
                    )
                }

                Switch(
                    checked = uiState.enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.35f),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }

            if (uiState.enabled) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BackupStatChip(
                        label = stringResource(R.string.backup_stat_backed_up),
                        value = uiState.backedUpCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    BackupStatChip(
                        label = stringResource(R.string.backup_stat_pending),
                        value = uiState.pendingCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    BackupStatChip(
                        label = stringResource(R.string.backup_stat_used),
                        value = "${uiState.usedGb} GB",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BackupStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.5f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 18.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun ProviderList(
    providers: List<BackupProvider>,
    selectedProvider: String,
    onSelectProvider: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
    ) {
        Column {
            providers.forEachIndexed { index, provider ->
                val active = selectedProvider == provider.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectProvider(provider.id) }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = provider.emoji, fontSize = 18.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = provider.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = provider.quota,
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) Brush.linearGradient(primaryGradient)
                                else Brush.linearGradient(
                                    listOf(Color.Transparent, Color.Transparent)
                                )
                            )
                            .border(
                                1.5.dp,
                                if (active) Color.Transparent else Color.White.copy(alpha = 0.35f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (active) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                if (index < providers.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 62.dp)
                            .background(Color.White.copy(alpha = 0.06f))
                    )
                }
            }
        }
    }
}

@Composable
private fun BackupSettingsCard(
    uiState: BackupUiState,
    onToggleWifi: () -> Unit,
    onToggleCharging: () -> Unit,
    onToggleVault: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
    ) {
        Column {
            BackupToggleRow(
                label = stringResource(R.string.backup_wifi_only),
                subtitle = stringResource(R.string.backup_wifi_only_sub),
                checked = uiState.wifiOnly,
                onToggle = onToggleWifi
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 16.dp)
                    .background(Color.White.copy(alpha = 0.06f))
            )
            BackupToggleRow(
                label = stringResource(R.string.backup_charging_only),
                subtitle = stringResource(R.string.backup_charging_only_sub),
                checked = uiState.chargingOnly,
                onToggle = onToggleCharging
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 16.dp)
                    .background(Color.White.copy(alpha = 0.06f))
            )
            BackupToggleRow(
                label = stringResource(R.string.backup_include_vault),
                subtitle = stringResource(R.string.backup_include_vault_sub),
                checked = uiState.includeVault,
                onToggle = onToggleVault
            )
        }
    }
}

@Composable
private fun BackupToggleRow(
    label: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 11.5.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

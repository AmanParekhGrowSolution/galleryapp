package com.example.galleryapp.ui.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
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

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val warningGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706))

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToCleaner: () -> Unit,
    onNavigateToTrash: () -> Unit = {},
    onNavigateToStorage: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {},
    onNavigateToAppLock: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.statusBarsPadding()
        ) {
            item {
                SettingsTopBar(onBack = onBack)
            }
            item {
                PremiumBanner(onClick = onNavigateToPremium)
            }
            item {
                SettingsGroupLabel(stringResource(R.string.privacy_security))
                SettingsToggleRow(
                    icon = Icons.Default.Lock,
                    iconGradient = primaryGradient,
                    label = stringResource(R.string.vault_title),
                    subtitle = "Active",
                    checked = true,
                    onToggle = { onNavigateToVault() }
                )
                SettingsToggleRow(
                    icon = Icons.Default.Security,
                    iconGradient = primaryGradient,
                    label = stringResource(R.string.app_lock),
                    subtitle = null,
                    checked = uiState.appLockEnabled,
                    onToggle = {
                        if (!uiState.appLockEnabled) onNavigateToAppLock() else viewModel.toggleAppLock()
                    }
                )
                SettingsToggleRow(
                    icon = Icons.Default.Security,
                    iconGradient = listOf(Color(0xFF6B7280), Color(0xFF4B5563)),
                    label = stringResource(R.string.strip_metadata),
                    subtitle = null,
                    checked = uiState.stripMetadata,
                    onToggle = viewModel::toggleStripMetadata
                )
            }
            item {
                SettingsGroupLabel(stringResource(R.string.storage))
                SettingsNavRow(
                    icon = Icons.Default.Storage,
                    iconGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                    label = stringResource(R.string.storage_manager_title),
                    value = uiState.storageLabel,
                    onClick = onNavigateToStorage
                )
                SettingsNavRow(
                    icon = Icons.Default.Delete,
                    iconGradient = listOf(Color(0xFF6B7280), Color(0xFF4B5563)),
                    label = stringResource(R.string.recently_deleted),
                    value = uiState.trashLabel,
                    onClick = onNavigateToTrash
                )
                SettingsNavRow(
                    icon = Icons.Default.CloudUpload,
                    iconGradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)),
                    label = stringResource(R.string.backup_title),
                    value = if (uiState.cloudBackup) stringResource(R.string.backup_on) else stringResource(R.string.backup_off),
                    onClick = onNavigateToBackup
                )
            }
            item {
                SettingsGroupLabel(stringResource(R.string.display))
                SettingsNavRow(
                    icon = Icons.Default.ModeNight,
                    iconGradient = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
                    label = stringResource(R.string.dark_mode),
                    value = uiState.darkMode,
                    onClick = {}
                )
                SettingsNavRow(
                    icon = Icons.Default.GridView,
                    iconGradient = primaryGradient,
                    label = stringResource(R.string.default_grid),
                    value = uiState.defaultGrid,
                    onClick = {}
                )
                SettingsToggleRow(
                    icon = Icons.Default.GridView,
                    iconGradient = listOf(Color(0xFF6B7280), Color(0xFF4B5563)),
                    label = stringResource(R.string.show_video_duration),
                    subtitle = null,
                    checked = uiState.showVideoDuration,
                    onToggle = viewModel::toggleShowVideoDuration
                )
                SettingsToggleRow(
                    icon = Icons.Default.GridView,
                    iconGradient = listOf(Color(0xFF6B7280), Color(0xFF4B5563)),
                    label = stringResource(R.string.rounded_thumbnails),
                    subtitle = null,
                    checked = uiState.roundedThumbnails,
                    onToggle = viewModel::toggleRoundedThumbnails
                )
            }
            item {
                SettingsGroupLabel(stringResource(R.string.about))
                SettingsNavRow(
                    icon = Icons.Default.Star,
                    iconGradient = warningGradient,
                    label = stringResource(R.string.version),
                    value = "2.5.14",
                    onClick = {}
                )
                SettingsNavRow(
                    icon = Icons.Default.Star,
                    iconGradient = primaryGradient,
                    label = stringResource(R.string.whats_new),
                    value = "",
                    onClick = {}
                )
                SettingsNavRow(
                    icon = Icons.Default.Star,
                    iconGradient = warningGradient,
                    label = stringResource(R.string.rate_app),
                    value = "",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
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
            text = stringResource(R.string.settings_title),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PremiumBanner(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(warningGradient))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = stringResource(R.string.content_desc_premium),
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(stringResource(R.string.get_premium), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.unlock_ai), color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp).semantics { role = Role.Image }
            )
        }
    }
}

@Composable
private fun SettingsGroupLabel(label: String) {
    Text(
        text = label,
        color = Color(0xFF8B5CF6),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconGradient: List<Color>,
    label: String,
    subtitle: String?,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon = icon, gradient = iconGradient)
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
            }
        }
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

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    iconGradient: List<Color>,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon = icon, gradient = iconGradient)
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f).padding(start = 12.dp)
        )
        if (value.isNotEmpty()) {
            Text(value, color = Color.White.copy(alpha = 0.87f), fontSize = 13.sp)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp).semantics { role = Role.Image }
        )
    }
}

@Composable
private fun SettingIcon(icon: ImageVector, gradient: List<Color>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Brush.linearGradient(gradient))
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp).semantics { role = Role.Image })
    }
}

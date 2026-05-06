package com.example.galleryapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SectionLabelGray
import com.example.galleryapp.ui.theme.SubtextGray
import com.example.galleryapp.ui.theme.SurfaceLight

private val goldGradient = listOf(Color(0xFFF5B800), Color(0xFFFFD84D))

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { SettingsTopBar(onBack = onBack) }
        item { PremiumBanner(onClick = onNavigateToPremium) }

        item {
            SettingsGroupLabel(stringResource(R.string.privacy_security).uppercase())
        }
        item {
            SettingsCard {
                SettingsNavRow(
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFF4B7BF5),
                    label = stringResource(R.string.private_vault),
                    subtitle = stringResource(R.string.private_vault_sub),
                    value = stringResource(R.string.active),
                    showChevron = false,
                    onClick = onNavigateToVault
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsToggleRow(
                    icon = Icons.Default.Security,
                    iconColor = Color(0xFF6E7BF5),
                    label = stringResource(R.string.app_lock),
                    subtitle = stringResource(R.string.fingerprint_face_pin),
                    checked = uiState.appLockEnabled,
                    onToggle = { if (!uiState.appLockEnabled) onNavigateToAppLock() else viewModel.toggleAppLock() }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsToggleRow(
                    icon = Icons.Default.VisibilityOff,
                    iconColor = Color(0xFF4B9E5F),
                    label = stringResource(R.string.hide_app_icon),
                    subtitle = stringResource(R.string.hide_app_icon_sub),
                    checked = false,
                    onToggle = {}
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsToggleRow(
                    icon = Icons.Default.Image,
                    iconColor = Color(0xFFE0534A),
                    label = stringResource(R.string.strip_metadata),
                    subtitle = null,
                    checked = uiState.stripMetadata,
                    onToggle = viewModel::toggleStripMetadata
                )
            }
        }

        item { SettingsGroupLabel(stringResource(R.string.storage).uppercase()) }
        item {
            SettingsCard {
                SettingsNavRow(
                    icon = Icons.Default.Storage,
                    iconColor = Color(0xFFF59E0B),
                    label = stringResource(R.string.smart_cleaner),
                    subtitle = stringResource(R.string.smart_cleaner_sub, "9.2"),
                    value = uiState.storageLabel,
                    showChevron = false,
                    onClick = onNavigateToCleaner
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsNavRow(
                    icon = Icons.Default.Delete,
                    iconColor = Color(0xFF8A8FA0),
                    label = stringResource(R.string.recently_deleted),
                    subtitle = stringResource(R.string.recently_deleted_sub, 12, 30),
                    value = "",
                    showChevron = true,
                    onClick = onNavigateToTrash
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsToggleRow(
                    icon = Icons.Default.CloudUpload,
                    iconColor = Color(0xFF4B7BF5),
                    label = stringResource(R.string.cloud_backup),
                    subtitle = stringResource(R.string.cloud_backup_sub),
                    checked = uiState.cloudBackup,
                    onToggle = onNavigateToBackup
                )
            }
        }

        item { SettingsGroupLabel(stringResource(R.string.display).uppercase()) }
        item {
            SettingsCard {
                SettingsNavRow(
                    icon = Icons.Default.ModeNight,
                    iconColor = Color(0xFF5B4B9E),
                    label = stringResource(R.string.dark_mode),
                    subtitle = null,
                    value = uiState.darkMode,
                    showChevron = false,
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsNavRow(
                    icon = Icons.Default.GridView,
                    iconColor = BrandBlue,
                    label = stringResource(R.string.default_grid),
                    subtitle = null,
                    value = uiState.defaultGrid,
                    showChevron = false,
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsToggleRow(
                    icon = Icons.Default.GridView,
                    iconColor = Color(0xFFE0534A),
                    label = stringResource(R.string.show_video_duration),
                    subtitle = null,
                    checked = uiState.showVideoDuration,
                    onToggle = viewModel::toggleShowVideoDuration
                )
                HorizontalDivider(modifier = Modifier.padding(start = 58.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                SettingsToggleRow(
                    icon = Icons.Default.Image,
                    iconColor = Color(0xFF4B9E5F),
                    label = stringResource(R.string.rounded_thumbnails),
                    subtitle = null,
                    checked = uiState.roundedThumbnails,
                    onToggle = viewModel::toggleRoundedThumbnails
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
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = OnSurfaceDark
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            color = OnSurfaceDark,
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
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(goldGradient))
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.get_premium),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.unlock_ai),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .semantics { role = Role.Image }
            )
        }
    }
}

@Composable
private fun SettingsGroupLabel(label: String) {
    Text(
        text = label,
        color = SectionLabelGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
    ) {
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    subtitle: String?,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon = icon, color = iconColor)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(label, color = OnSurfaceDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, color = SubtextGray, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCCCCCC)
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    subtitle: String?,
    value: String,
    showChevron: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon = icon, color = iconColor)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(label, color = OnSurfaceDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, color = SubtextGray, fontSize = 12.sp)
            }
        }
        if (value.isNotEmpty()) {
            Text(value, color = SubtextGray, fontSize = 13.sp)
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SubtextGray,
                modifier = Modifier
                    .size(18.dp)
                    .semantics { role = Role.Image }
            )
        }
    }
}

@Composable
private fun SettingIcon(icon: ImageVector, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(color)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(18.dp)
                .semantics { role = Role.Image }
        )
    }
}

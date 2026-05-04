package com.example.galleryapp.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))

@Composable
fun AIEditorScreen(
    photoId: Long,
    onClose: () -> Unit,
    viewModel: AIEditorViewModel = viewModel()
) {
    LaunchedEffect(photoId) { viewModel.loadPhoto(photoId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.Center)
                .background(Brush.linearGradient(listOf(Color(uiState.photoColor), Color(uiState.photoColor).copy(alpha = 0.85f))))
        )

        EditorTopBar(
            onClose = onClose,
            onSave = { onClose() },
            onRevert = viewModel::revert,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))))
        ) {
            when (uiState.selectedTool) {
                EditorTool.Adjust -> AdjustPanel(
                    brightness = uiState.brightness,
                    contrast = uiState.contrast,
                    saturation = uiState.saturation,
                    warmth = uiState.warmth,
                    sharpness = uiState.sharpness,
                    onBrightness = viewModel::setBrightness,
                    onContrast = viewModel::setContrast,
                    onSaturation = viewModel::setSaturation,
                    onWarmth = viewModel::setWarmth,
                    onSharpness = viewModel::setSharpness
                )
                EditorTool.Filter -> FilterPanel(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelect = viewModel::selectFilter
                )
                EditorTool.AI -> AIToolsPanel()
                else -> Unit
            }

            ToolTabBar(
                selectedTool = uiState.selectedTool,
                onToolSelect = viewModel::selectTool
            )
        }
    }
}

@Composable
private fun EditorTopBar(
    onClose: () -> Unit,
    onSave: () -> Unit,
    onRevert: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.close),
            tint = Color.White,
            modifier = Modifier.clickable(onClick = onClose).size(24.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.12f))))
                .clickable(onClick = onRevert)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.revert), color = Color.White, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(primaryGradient))
                .clickable(onClick = onSave)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.save), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AdjustPanel(
    brightness: Float,
    contrast: Float,
    saturation: Float,
    warmth: Float,
    sharpness: Float,
    onBrightness: (Float) -> Unit,
    onContrast: (Float) -> Unit,
    onSaturation: (Float) -> Unit,
    onWarmth: (Float) -> Unit,
    onSharpness: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        AdjustSlider(stringResource(R.string.brightness), brightness, -100f..100f, onBrightness)
        AdjustSlider(stringResource(R.string.contrast), contrast, -100f..100f, onContrast)
        AdjustSlider(stringResource(R.string.saturation), saturation, -100f..100f, onSaturation)
        AdjustSlider(stringResource(R.string.warmth), warmth, -100f..100f, onWarmth)
        AdjustSlider(stringResource(R.string.sharpness), sharpness, 0f..100f, onSharpness)
    }
}

@Composable
private fun AdjustSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(
            text = value.toInt().toString(),
            color = Color(0xFF8B5CF6),
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.weight(2f).padding(start = 12.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF8B5CF6),
                activeTrackColor = Color(0xFF6366F1)
            )
        )
    }
}

@Composable
private fun FilterPanel(
    selectedFilter: FilterPreset,
    onFilterSelect: (FilterPreset) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(FilterPreset.entries) { filter ->
            val isSelected = filter == selectedFilter
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onFilterSelect(filter) }
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected)
                                Brush.linearGradient(primaryGradient)
                            else
                                Brush.linearGradient(
                                    listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.1f))
                                )
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = filter.name,
                    color = if (isSelected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.87f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AIToolsPanel() {
    val tools = listOf(
        stringResource(R.string.remove_bg),
        stringResource(R.string.object_eraser),
        stringResource(R.string.ai_enhance),
        stringResource(R.string.unblur),
        stringResource(R.string.old_photo),
        stringResource(R.string.sky_replace)
    )
    val rows = tools.chunked(3)
    Column(modifier = Modifier.padding(16.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { tool ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF6366F1).copy(alpha = 0.2f), Color(0xFF8B5CF6).copy(alpha = 0.2f))
                                )
                            )
                            .clickable {}
                            .padding(vertical = 12.dp)
                    ) {
                        Text(tool, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (row.size < 3) repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private data class ToolTab(val tool: EditorTool, val icon: ImageVector, val label: String)

@Composable
private fun ToolTabBar(selectedTool: EditorTool, onToolSelect: (EditorTool) -> Unit) {
    val tabs = listOf(
        ToolTab(EditorTool.AI, Icons.Default.AutoAwesome, "AI"),
        ToolTab(EditorTool.Crop, Icons.Default.Crop, "Crop"),
        ToolTab(EditorTool.Adjust, Icons.Default.Tune, "Adjust"),
        ToolTab(EditorTool.Filter, Icons.Default.FilterAlt, "Filter"),
        ToolTab(EditorTool.Brush, Icons.Default.Brush, "Brush"),
        ToolTab(EditorTool.Text, Icons.Default.TextFields, "Text"),
        ToolTab(EditorTool.Sticker, Icons.Default.Lightbulb, "Sticker"),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val isSelected = tab.tool == selectedTool
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onToolSelect(tab.tool) }
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = if (isSelected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tab.label,
                    color = if (isSelected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.5f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

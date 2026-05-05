package com.example.galleryapp.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.DarkBgDeep
import com.example.galleryapp.ui.theme.TrimYellow

@Composable
fun VideoTrimmerScreen(
    photoId: Long,
    onBack: () -> Unit,
    viewModel: VideoTrimmerViewModel = viewModel(),
) {
    LaunchedEffect(photoId) { viewModel.loadVideo(photoId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgDeep),
    ) {
        TrimmerTopBar(
            onBack = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp),
        )

        when (val state = uiState) {
            is VideoTrimmerUiState.Editing -> {
                VideoPreviewArea(modifier = Modifier.weight(1f).fillMaxWidth())

                TimeReadout(
                    trimStart = state.trimStart,
                    trimEnd = state.trimEnd,
                    durationSeconds = state.durationSeconds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                )

                FilmstripWithHandles(
                    trimStart = state.trimStart,
                    trimEnd = state.trimEnd,
                    playhead = state.playhead,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                )

                Spacer(Modifier.height(18.dp))

                TrimToolBar(
                    activeTool = state.activeTool,
                    onSelectTool = viewModel::selectTool,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
            is VideoTrimmerUiState.Loading -> Unit
        }
    }
}

@Composable
private fun TrimmerTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.White,
            )
        }
        Text(
            text = stringResource(R.string.trim_video_title),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandBlue)
                .padding(horizontal = 18.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.save),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun VideoPreviewArea(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.content_desc_play),
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(56.dp),
            )
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.content_desc_play),
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun TimeReadout(
    trimStart: Float,
    trimEnd: Float,
    durationSeconds: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = formatTrimTime(trimStart, durationSeconds),
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${formatTrimTime(trimEnd - trimStart, 1)} ${stringResource(R.string.trim_selected)}",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = formatTrimTime(trimEnd, durationSeconds),
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun FilmstripWithHandles(
    trimStart: Float,
    trimEnd: Float,
    playhead: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.04f)),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            repeat(12) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color(0xFF1A1A2E)),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(trimStart)
                .background(Color.Black.copy(alpha = 0.6f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .fillMaxWidth(1f - trimEnd)
                .background(Color.Black.copy(alpha = 0.6f)),
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(start = (trimStart * 100).dp.coerceAtLeast(0.dp))
                .width((trimEnd - trimStart).let { 0.dp }),
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = (trimStart * 200).coerceAtMost(180f).dp)
                .fillMaxHeight()
                .width(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(TrimYellow),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.Black),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = ((1f - trimEnd) * 200).coerceAtMost(180f).dp)
                .fillMaxHeight()
                .width(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(TrimYellow),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.Black),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = (playhead * 200).coerceAtMost(198f).dp)
                .fillMaxHeight()
                .width(2.dp)
                .background(Color.White),
        )
    }
}

@Composable
private fun TrimToolBar(
    activeTool: TrimTool,
    onSelectTool: (TrimTool) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tools = listOf(
        Triple(TrimTool.TRIM, Icons.Default.Crop, R.string.trim_tool_trim),
        Triple(TrimTool.FRAME, Icons.Default.Image, R.string.trim_tool_frame),
        Triple(TrimTool.SPEED, Icons.Default.Speed, R.string.trim_tool_speed),
        Triple(TrimTool.MUTE, Icons.Default.VolumeOff, R.string.trim_tool_mute),
        Triple(TrimTool.SPLIT, Icons.Default.ContentCut, R.string.trim_tool_split),
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        tools.forEach { (tool, icon, labelRes) ->
            TrimToolButton(
                icon = icon,
                label = stringResource(labelRes),
                isActive = activeTool == tool,
                onClick = { onSelectTool(tool) },
            )
        }
    }
}

@Composable
private fun TrimToolButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val iconColor = if (isActive) BrandBlue else Color.White.copy(alpha = 0.7f)
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) BrandBlue.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f)
                )
                .then(
                    if (isActive) Modifier.clip(CircleShape) else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = label,
            color = iconColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun formatTrimTime(fraction: Float, durationSeconds: Int): String {
    val seconds = (fraction * durationSeconds).toInt()
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

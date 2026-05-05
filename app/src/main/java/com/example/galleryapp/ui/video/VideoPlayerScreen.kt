package com.example.galleryapp.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.galleryapp.ui.theme.BrandBlue

private val topGradient = Brush.verticalGradient(
    listOf(Color(0x99000000), Color.Transparent)
)
private val bottomGradient = Brush.verticalGradient(
    listOf(Color.Transparent, Color(0xB3000000))
)

@Composable
fun VideoPlayerScreen(
    photoId: Long,
    onBack: () -> Unit,
    onTrim: (Long) -> Unit,
    viewModel: VideoPlayerViewModel = viewModel(),
) {
    LaunchedEffect(photoId) { viewModel.loadVideo(photoId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.toggleControls() },
    ) {
        VideoPlaceholder(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
        )

        when (val state = uiState) {
            is VideoPlayerUiState.Ready -> {
                if (state.showControls) {
                    VideoTopBar(
                        speed = state.speed,
                        onBack = onBack,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .background(topGradient)
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    )

                    CenterPlayControls(
                        isPlaying = state.isPlaying,
                        onSeekBack = { viewModel.seekBy(-10) },
                        onTogglePlay = viewModel::togglePlay,
                        onSeekForward = { viewModel.seekBy(10) },
                        modifier = Modifier.align(Alignment.Center),
                    )

                    VideoBottomBar(
                        progress = state.progress,
                        durationSeconds = state.durationSeconds,
                        isMuted = state.isMuted,
                        speed = state.speed,
                        onToggleMute = viewModel::toggleMute,
                        onCycleSpeed = viewModel::cycleSpeed,
                        onTrim = { onTrim(photoId) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(bottomGradient)
                            .navigationBarsPadding()
                            .padding(horizontal = 14.dp)
                            .padding(bottom = 20.dp, top = 16.dp),
                    )
                }
            }
            is VideoPlayerUiState.Loading -> Unit
        }
    }
}

@Composable
private fun VideoPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFF111111)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.size(96.dp),
        )
    }
}

@Composable
private fun VideoTopBar(
    speed: Float,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = Color.White,
            )
        }
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.video_filename),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.video_metadata),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = stringResource(R.string.video_extract_frame),
                tint = Color.White,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.content_desc_more),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun CenterPlayControls(
    isPlaying: Boolean,
    onSeekBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleButton(size = 54, onClick = onSeekBack) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = stringResource(R.string.video_seek_back),
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
        CircleButton(
            size = 72,
            onClick = onTogglePlay,
            backgroundAlpha = 0.2f,
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) stringResource(R.string.video_pause) else stringResource(R.string.video_play),
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
        CircleButton(size = 54, onClick = onSeekForward) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = stringResource(R.string.video_seek_forward),
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun CircleButton(
    size: Int,
    onClick: () -> Unit,
    backgroundAlpha: Float = 0.45f,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = backgroundAlpha))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun VideoBottomBar(
    progress: Float,
    durationSeconds: Int,
    isMuted: Boolean,
    speed: Float,
    onToggleMute: () -> Unit,
    onCycleSpeed: () -> Unit,
    onTrim: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SeekBar(
            progress = progress,
            durationSeconds = durationSeconds,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            VideoAction(
                icon = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                label = if (isMuted) stringResource(R.string.video_muted) else stringResource(R.string.video_sound),
                onClick = onToggleMute,
            )
            VideoAction(
                icon = Icons.Default.Speed,
                label = "${speed}x",
                onClick = onCycleSpeed,
            )
            VideoAction(
                icon = Icons.Default.PictureInPicture,
                label = stringResource(R.string.video_pip),
            )
            VideoAction(
                icon = Icons.Default.Lock,
                label = stringResource(R.string.video_lock),
            )
            VideoAction(
                icon = Icons.Default.Cast,
                label = stringResource(R.string.video_cast),
            )
            VideoAction(
                icon = Icons.Default.Share,
                label = stringResource(R.string.content_desc_share),
            )
        }
    }
}

@Composable
private fun SeekBar(
    progress: Float,
    durationSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val currentSeconds = (progress * durationSeconds).toInt()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formatTime(currentSeconds),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandBlue)
                    .align(Alignment.CenterStart),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = (progress * 100).coerceIn(0f, 100f).let { 0.dp }),
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(BrandBlue)
                        .align(Alignment.Center),
                )
            }
        }
        Text(
            text = formatTime(durationSeconds),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp),
        )
    }
}

@Composable
private fun VideoAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

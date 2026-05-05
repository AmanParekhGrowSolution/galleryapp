package com.example.galleryapp.ui.slideshow

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

private val topGradient = Brush.verticalGradient(
    listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
)
private val bottomGradient = Brush.verticalGradient(
    listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
)

@Composable
fun SlideshowScreen(
    onBack: () -> Unit,
    viewModel: SlideshowViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when (val state = uiState) {
            is SlideshowUiState.Playing -> {
                val bgColor by animateColorAsState(
                    targetValue = Color(state.photoColors[state.currentIndex]),
                    animationSpec = tween(800),
                    label = "slideshowBgColor",
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bgColor),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.TopCenter)
                        .background(topGradient),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.BottomCenter)
                        .background(bottomGradient),
                )

                SlideshowTopBar(
                    isPaused = state.isPaused,
                    onBack = onBack,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                )

                SlideshowCaption(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, end = 24.dp, bottom = 130.dp),
                )

                SlideshowControls(
                    isPaused = state.isPaused,
                    photoCount = state.photoColors.size,
                    currentIndex = state.currentIndex,
                    onPrevious = viewModel::goToPrevious,
                    onTogglePause = viewModel::togglePause,
                    onNext = viewModel::goToNext,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 28.dp),
                )
            }
            is SlideshowUiState.Loading -> Unit
        }
    }
}

@Composable
private fun SlideshowTopBar(
    isPaused: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.White,
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isPaused) Color(0xFFFF3D7F) else Color(0xFF34D399)),
            )
            Text(
                text = if (isPaused) stringResource(R.string.slideshow_paused) else stringResource(R.string.slideshow_playing),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.slideshow_settings),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun SlideshowCaption(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "APRIL 2024",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.slideshow_caption),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 26.sp,
            letterSpacing = (-0.3).sp,
        )
    }
}

@Composable
private fun SlideshowControls(
    isPaused: Boolean,
    photoCount: Int,
    currentIndex: Int,
    onPrevious: () -> Unit,
    onTogglePause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProgressDots(
            count = photoCount,
            currentIndex = currentIndex,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SlideControlButton(size = 48.dp, onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = stringResource(R.string.video_seek_back),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
            SlideControlButton(size = 64.dp, backgroundAlpha = 0.16f, onClick = onTogglePause) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) stringResource(R.string.video_play) else stringResource(R.string.video_pause),
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
            SlideControlButton(size = 48.dp, onClick = onNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = stringResource(R.string.video_seek_forward),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun SlideControlButton(
    size: androidx.compose.ui.unit.Dp,
    backgroundAlpha: Float = 0.45f,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = backgroundAlpha))
            .then(Modifier.padding(0.dp)),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun ProgressDots(
    count: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val isActive = i == currentIndex
            Box(
                modifier = Modifier
                    .width(if (isActive) 14.dp else 5.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(if (isActive) Color.White else Color.White.copy(alpha = 0.4f)),
            )
        }
    }
}

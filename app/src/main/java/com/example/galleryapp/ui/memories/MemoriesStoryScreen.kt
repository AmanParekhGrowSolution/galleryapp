package com.example.galleryapp.ui.memories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.AccentPurple

private val topScrim = Brush.verticalGradient(
    listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent),
)
private val bottomScrim = Brush.verticalGradient(
    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
)

@Composable
fun MemoriesStoryScreen(
    onBack: () -> Unit,
    viewModel: MemoriesStoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.resumeProgress()
        onPauseOrDispose { viewModel.pauseProgress() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when (val state = uiState) {
            is MemoriesStoryUiState.Playing -> {
                val slide = state.slides[state.currentIndex]

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(slide.colorHex)),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter)
                        .background(topScrim),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .align(Alignment.BottomCenter)
                        .background(bottomScrim),
                )

                ProgressBars(
                    slideCount = state.slides.size,
                    currentIndex = state.currentIndex,
                    progress = state.progress,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )

                StoryTopBar(
                    slideIndex = state.currentIndex,
                    slideCount = state.slides.size,
                    onClose = onBack,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 18.dp, start = 14.dp, end = 14.dp),
                )

                StoryContent(
                    slide = slide,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 110.dp),
                )

                StoryActionBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                )

                TapZone(
                    onTapLeft = viewModel::goToPrevious,
                    onTapRight = viewModel::goToNext,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is MemoriesStoryUiState.Loading -> Unit
        }
    }
}

@Composable
private fun ProgressBars(
    slideCount: Int,
    currentIndex: Int,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(slideCount) { i ->
            val fillFraction = when {
                i < currentIndex -> 1f
                i == currentIndex -> progress
                else -> 0f
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color.White.copy(alpha = 0.3f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fillFraction)
                        .background(Color.White),
                )
            }
        }
    }
}

@Composable
private fun StoryTopBar(
    slideIndex: Int,
    slideCount: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(BrandBlue, AccentPurple))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp).semantics { role = Role.Image },
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.memories_title),
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "${slideIndex + 1} ${stringResource(R.string.story_of)} $slideCount",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(R.string.story_mute_notifications),
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun StoryContent(slide: StorySlide, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = slide.subtitle.uppercase(),
            color = Color(0xFFFFDC00),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = slide.title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 32.sp,
            letterSpacing = (-0.4).sp,
        )
    }
}

@Composable
private fun StoryActionBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(Color.White.copy(alpha = 0.14f)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = stringResource(R.string.story_send_message),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.content_desc_favorite),
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.content_desc_share),
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun TapZone(
    onTapLeft: () -> Unit,
    onTapRight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTapLeft,
                ),
        )
        Spacer(Modifier.weight(0.4f))
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTapRight,
                ),
        )
    }
}

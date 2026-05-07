package com.example.galleryapp.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.SubtextGray
import kotlinx.coroutines.delay

private val iconOuterGradient = listOf(Color(0xFF1A6BFF), Color(0xFF1A6BFF))
private val iconInnerGradient = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    isFirstRun: Boolean = false
) {
    var navigated by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        delay(2000)
        if (!navigated && lifecycleOwner.lifecycle.currentState >= Lifecycle.State.STARTED) {
            navigated = true
            if (isFirstRun) onNavigateToOnboarding() else onNavigateToMain()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            AppIconBadge()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.gallery_title),
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.splash_tagline_new),
                color = SubtextGray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            SplashDots(activeIndex = 1, total = 3)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.splash_footer),
                color = SubtextGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppIconBadge() {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF1A6BFF))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(iconInnerGradient))
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = stringResource(R.string.app_name),
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(44.dp)
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F1F5))
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color(0xFF555555),
                    modifier = Modifier.size(14.dp).semantics { role = Role.Image }
                )
            }
        }
    }
}

@Composable
private fun SplashDots(activeIndex: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (index == activeIndex) BrandBlue else Color(0xFFCCCCCC))
            )
        }
    }
}

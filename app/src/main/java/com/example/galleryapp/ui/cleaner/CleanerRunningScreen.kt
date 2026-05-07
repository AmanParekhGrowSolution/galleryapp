package com.example.galleryapp.ui.cleaner

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.ui.theme.AccentGreen
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.DarkBg
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun CleanerRunningScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
    viewModel: CleanerRunningViewModel = viewModel(),
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val bg = if (dark) DarkBg else Color(0xFFF4F8FB)
    val cardBg = if (dark) Color(0x0AFFFFFF) else Color.White
    val cardBorder = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)
    val dotDivider = if (dark) Color(0x0AFFFFFF) else Color(0xFFEDF2F7)

    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val pct = (progress * 100).toInt()

    LifecycleResumeEffect(viewModel) {
        viewModel.resumeProgress()
        onPauseOrDispose { viewModel.pauseProgress() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "pulseAlpha",
    )

    val tasks = listOf(
        Triple("Scanning duplicates", true, false),
        Triple("Finding blurry photos", pct > 30, false),
        Triple("Detecting screenshots", pct > 55, false),
        Triple("Analysing large videos", pct > 80, pct > 55 && pct <= 80),
        Triple("Computing space saved", pct >= 100, pct >= 80 && pct < 100),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding(),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Text("✕", fontSize = 22.sp, color = fg)
            }
            Text("Smart Cleaner", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Radial progress ring
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    val radius = size.minDimension / 2f - stroke.width / 2
                    drawArc(
                        color = Color(if (dark) 0x0FFFFFFF else 0xFFE5EAF2.toInt()),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = stroke,
                    )
                    drawArc(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(BrandBlue, Color(0xFFA78BFA)),
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = stroke,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "SCANNING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = subFg,
                        letterSpacing = 1.sp,
                        fontFamily = InterFontFamily,
                    )
                    Text(
                        "$pct",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = fg,
                        fontFamily = InterFontFamily,
                    )
                    Text(
                        "~ ${maxOf(0, 12 - pct / 8)}s remaining",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        fontFamily = InterFontFamily,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Task checklist card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                tasks.forEachIndexed { i, (label, done, active) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        done -> AccentGreen
                                        active -> Color.Transparent
                                        else -> if (dark) Color(0x14FFFFFF) else Color(0xFFE5EAF2)
                                    }
                                )
                                .then(
                                    if (active) Modifier.border(2.dp, BrandBlue, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            when {
                                done -> Text("✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                                active -> Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandBlue.copy(alpha = pulseAlpha))
                                )
                            }
                        }
                        Text(
                            label,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = fg.copy(alpha = if (done || active) 1f else 0.4f),
                            fontFamily = InterFontFamily,
                        )
                        if (done) {
                            Text("Done", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = subFg, fontFamily = InterFontFamily)
                        }
                    }
                    if (i < tasks.lastIndex) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(dotDivider))
                    }
                }
            }
        }
    }
}

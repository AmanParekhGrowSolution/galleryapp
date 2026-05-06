package com.example.galleryapp.ui.cleaner

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.AccentGreenDark
import com.example.galleryapp.ui.theme.AccentPink
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.BrandPurple
import com.example.galleryapp.ui.theme.DarkBg
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun CleanerResultScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val bg = if (dark) DarkBg else Color.White
    val cardBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val cardBorder = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)

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
            Text("All clean!", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
        ) {
            // Hero
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF34D399), AccentGreenDark),
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✓", fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    buildAnnotatedString {
                        append("Freed up ")
                        withStyle(SpanStyle(color = AccentGreenDark)) { append("2.4 GB") }
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp,
                    color = fg,
                    fontFamily = InterFontFamily,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "By removing 187 items in 14 seconds",
                    fontSize = 13.sp,
                    color = subFg,
                    fontFamily = InterFontFamily,
                )
            }

            // Breakdown cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple(AccentPink, "Duplicates removed" to "124 items", "1.2 GB"),
                    Triple(BrandPurple, "Blurry photos removed" to "38 items", "320 MB"),
                    Triple(Color(0xFFFBBF24), "Old screenshots" to "21 items", "180 MB"),
                    Triple(BrandBlue, "Compressed videos" to "4 items", "720 MB"),
                ).forEach { (color, labelPair, saved) ->
                    val (label, sub) = labelPair
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("●", fontSize = 18.sp, color = color)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                            Text(sub, fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                        }
                        Text(saved, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AccentGreenDark, fontFamily = InterFontFamily)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // CTA row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .border(1.dp, if (dark) Color(0x1FFFFFFF) else Color(0xFFE5EAF2), RoundedCornerShape(23.dp))
                        .clickable {},
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Share", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                }
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(BrandBlue)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Done", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = InterFontFamily)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

package com.example.galleryapp.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.AccentGreenDark
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.BrandPurple
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun PremiumNudgeScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        CenterDialogFrame(dark = dark, onClose = onClose) {
            Column {
                // Gradient header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(BrandBlue, BrandPurple),
                            )
                        )
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                ) {
                    // Close button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(30.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✕", fontSize = 14.sp, color = Color.White)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("✨", fontSize = 32.sp)
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "PRO FEATURE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 1.5.sp,
                            fontFamily = InterFontFamily,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Unlimited AI editing",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp,
                            fontFamily = InterFontFamily,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "You've used your 3 free AI edits this month. Go Pro for unlimited.",
                            fontSize = 12.5.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            fontFamily = InterFontFamily,
                        )
                    }
                }

                // Feature list
                Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp)) {
                    listOf(
                        Triple("🪄", "Unlimited AI tools", "Magic Erase, Sky Swap, Sharpen"),
                        Triple("🧹", "Smart Cleaner Pro", "Auto-runs weekly"),
                        Triple("🛡", "Cloud backup encrypted", "Your photos, your keys"),
                    ).forEach { (emoji, label, sub) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(emoji, fontSize = 16.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                                Text(sub, fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                            }
                            Text("✓", fontSize = 16.sp, color = AccentGreenDark, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                // CTA
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(BrandBlue)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Try Pro free for 7 days",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontFamily = InterFontFamily,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onClose)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Maybe later",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = subFg,
                            fontFamily = InterFontFamily,
                        )
                    }
                }
            }
        }
    }
}

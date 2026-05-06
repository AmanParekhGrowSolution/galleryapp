package com.example.galleryapp.ui.dialogs

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.AccentPink
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun PhotoInfoScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val cardBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val divider = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)

    Box(modifier = Modifier.fillMaxSize()) {
        // Dark photo bg
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
        ThumbPlaceholder(seed = 410, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))

        BottomSheetFrame(dark = true, onClose = onClose, title = "Details") {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 4.dp),
            ) {
                // Filename & date
                Text("IMG_20240409_142733.jpg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
                Text("Tuesday, April 9 · 2:27 PM", fontSize = 12.sp, color = subFg, fontFamily = InterFontFamily)

                Spacer(Modifier.height(14.dp))

                // Fake map
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (dark) Color(0xFF0F172A) else Color(0xFFCFE5D6)),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📍", fontSize = 13.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Anjuna Beach, Goa", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = InterFontFamily)
                                Text("15.5760°N, 73.7440°E", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.7f), fontFamily = InterFontFamily)
                            }
                            Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Camera info card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("📷", fontSize = 18.sp)
                        }
                        Column {
                            Text("Pixel 8 Pro · Main camera", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
                            Text("Ultra HDR · 12 MP", fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ƒ/1.7" to "Aperture", "1/250" to "Shutter", "ISO 64" to "ISO", "24mm" to "Focal").forEach { (value, label) ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (dark) Color(0x0AFFFFFF) else Color.White)
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
                                Text(label, fontSize = 9.5.sp, color = subFg, fontFamily = InterFontFamily)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // File facts
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .padding(horizontal = 14.dp),
                ) {
                    listOf("Size" to "4.2 MB", "Resolution" to "4032 × 3024", "Format" to "JPEG · sRGB", "Path" to "/DCIM/Camera")
                        .forEachIndexed { i, (label, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(label, fontSize = 12.sp, color = subFg, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                            }
                            if (i < 3) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(divider))
                        }
                }

                Spacer(Modifier.height(12.dp))

                // People
                Text(
                    "PEOPLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(Triple("S", Color(0xFFFFB4D2), "Sara"), Triple("A", Color(0xFFA78BFA), "Arjun"), Triple("M", Color(0xFFFBBF24), "Mom"))
                        .forEach { (initial, color, name) ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(cardBg)
                                    .padding(start = 5.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(color),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(initial, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                                Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                            }
                        }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, subFg.copy(alpha = 0.4f), CircleShape)
                            .clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+", fontSize = 16.sp, color = subFg)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

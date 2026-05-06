package com.example.galleryapp.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun MapPlaceSheetScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val chipBg = if (dark) Color(0x0FFFFFFF) else Color(0xFFEBF2F8)
    val mapBg = if (dark) Color(0xFF0F172A) else Color(0xFFCFE5D6)

    Box(modifier = Modifier.fillMaxSize()) {
        // Map background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(mapBg),
        )

        // Map photo clusters
        listOf(
            Triple(0.3f, 150.dp, 58),
            Triple(0.6f, 230.dp, 124),
            Triple(0.2f, 320.dp, 12),
        ).forEachIndexed { i, (xFraction, yOffset, count) ->
            val isActive = i == 1
            Box(
                modifier = Modifier
                    .padding(start = (xFraction * 360).dp)
                    .padding(top = yOffset)
                    .size(if (isActive) 56.dp else 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .then(
                        if (isActive) Modifier.clickable {}
                        else Modifier
                    ),
            ) {
                ThumbPlaceholder(seed = listOf(210, 250, 310)[i], modifier = Modifier.fillMaxSize())
                // Count badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = (-4).dp, end = (-4).dp)
                        .size(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(BrandBlue),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$count", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }

        // Bottom sheet
        BottomSheetFrame(dark = dark, onClose = onClose) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 14.dp),
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("📍", fontSize = 20.sp, color = AccentPink)
                    Text("Anjuna Beach, Goa", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "124 photos · 14 trips · 2018 – 2024",
                    fontSize = 12.sp,
                    color = subFg,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(start = 30.dp),
                )

                Spacer(Modifier.height(14.dp))

                // Cover photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                ) {
                    ThumbPlaceholder(seed = 250, modifier = Modifier.fillMaxSize())
                }

                Spacer(Modifier.height(12.dp))

                // Action chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("View all", "Slideshow", "Share", "Favourite").forEach { label ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(chipBg)
                                .clickable {}
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Photo preview grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false,
                ) {
                    items(8) { i ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp)),
                        ) {
                            ThumbPlaceholder(seed = 250 + i * 2, modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                Text(
                    "Show all 124 photos →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(vertical = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontFamily = InterFontFamily,
                )
            }
        }
    }
}

package com.example.galleryapp.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.AccentPink
import com.example.galleryapp.ui.theme.BgLight3
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.DarkBg
import com.example.galleryapp.ui.theme.DarkBorder
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark

@Composable
fun MultiSelectScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val bg = if (dark) DarkBg else Color.White
    val chipBg = if (dark) Color(0x0FFFFFFF) else BgLight3
    val borderColor = if (dark) DarkBorder else Color(0xFFE8EFF6)
    val bottomBarBg = if (dark) Color(0xF2131111) else Color(0xF2FFFFFF)

    val allItems = remember { (0 until 18).map { it } }
    var selected by remember { mutableStateOf(setOf(2, 3, 6, 9, 11)) }

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar – blue background, status bar inset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✕", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${selected.size} selected",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = InterFontFamily,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { selected = allItems.toSet() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✓", fontSize = 20.sp, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("⋮", fontSize = 22.sp, color = Color.White)
                    }
                }
            }

            // Action chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Share", "Add to album", "Move to vault", "Tag people").forEach { label ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(chipBg)
                            .clickable {}
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = fg,
                            fontFamily = InterFontFamily,
                        )
                    }
                }
            }

            // Photo grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                items(allItems, key = { it }) { id ->
                    val isSel = id in selected
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selected = if (isSel) selected - id else selected + id },
                    ) {
                        ThumbPlaceholder(
                            seed = 200 + id,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(if (isSel) 0.88f else 1f),
                        )
                        if (isSel) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x260066FF))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isSel) BrandBlue else Color(0x66000000))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSel) {
                                Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(72.dp))
        }

        // Bottom action bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(bottomBarBg)
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            listOf(
                "Share" to fg,
                "Album" to fg,
                "Delete" to AccentPink,
                "More" to fg,
            ).forEach { (label, color) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {}
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Spacer(Modifier.size(20.dp))
                    Text(
                        text = label,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontFamily = InterFontFamily,
                    )
                }
            }
        }
    }
}

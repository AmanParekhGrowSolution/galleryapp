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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

private data class AlbumItem(val id: String, val name: String, val count: Int, val seed: Int)

@Composable
fun MoveToAlbumScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray

    val albums = remember {
        listOf(
            AlbumItem("favs", "Favourites", 184, 100),
            AlbumItem("trip", "Goa Trip", 124, 110),
            AlbumItem("fam", "Family", 318, 120),
            AlbumItem("work", "Work", 92, 130),
            AlbumItem("screens", "Screenshots", 421, 140),
            AlbumItem("food", "Food", 56, 150),
            AlbumItem("pets", "Bruno", 213, 160),
        )
    }
    var picked by remember { mutableStateOf("trip") }

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        BottomSheetFrame(
            dark = dark,
            onClose = onClose,
            title = "Move 5 items",
            footer = {
                val albumName = albums.find { it.id == picked }?.name ?: ""
                PrimaryButton(text = "Move to $albumName", onClick = onClose)
            },
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Create new album row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (dark) Color(0x1F0066FF) else Color(0xFFE8F0FF))
                            .border(1.dp, Color(0x660066FF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+", fontSize = 18.sp, color = BrandBlue, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Create new album", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandBlue, fontFamily = InterFontFamily)
                        Text("Name, cover and add items", fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .height(1.dp)
                        .background(if (dark) Color(0x0FFFFFFF) else Color(0xFFEDF2F7))
                )

                albums.forEach { album ->
                    val isSel = picked == album.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { picked = album.id }
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        ) {
                            ThumbPlaceholder(seed = album.seed, modifier = Modifier.fillMaxSize())
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(album.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                            Text("${album.count} items", fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                        }
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isSel) BrandBlue else Color.Transparent)
                                .border(
                                    width = if (isSel) 0.dp else 2.dp,
                                    color = if (isSel) Color.Transparent else if (dark) Color(0x40FFFFFF) else Color(0xFFCBD5E1),
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSel) {
                                Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

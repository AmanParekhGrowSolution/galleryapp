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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun CreateAlbumScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val inputBg = if (dark) Color(0x0FFFFFFF) else Color(0xFFF4F8FB)
    val inputBorder = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)
    val sectionBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val divider = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)

    val covers = remember { listOf(110, 250, 318, 425, 580, 720) }
    var selectedCover by remember { mutableIntStateOf(0) }
    var albumName by remember { mutableStateOf("Goa 2024") }

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        BottomSheetFrame(
            dark = dark,
            onClose = onClose,
            title = "New album",
            footer = {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .border(1.dp, if (dark) Color(0x1FFFFFFF) else Color(0xFFE5EAF2), RoundedCornerShape(23.dp))
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                    }
                    PrimaryButton(text = "Create", onClick = onClose, modifier = Modifier.weight(1f))
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 4.dp),
            ) {
                // Cover preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp)),
                ) {
                    ThumbPlaceholder(seed = covers[selectedCover], modifier = Modifier.fillMaxSize())
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xB3000000)),
                                    startY = 65f,
                                )
                            ),
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                    ) {
                        Text(
                            "ALBUM COVER",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp,
                            fontFamily = InterFontFamily,
                        )
                        Text(
                            albumName.ifEmpty { "Album name" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontFamily = InterFontFamily,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    "CHOOSE COVER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    covers.forEachIndexed { i, seed ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (selectedCover == i) 2.5.dp else 1.dp,
                                    color = if (selectedCover == i) BrandBlue else inputBorder,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .clickable { selectedCover = i },
                        ) {
                            ThumbPlaceholder(seed = seed, modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "NAME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(inputBg)
                        .border(1.dp, inputBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = albumName,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = fg,
                        fontFamily = InterFontFamily,
                    )
                    if (albumName.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(subFg.copy(alpha = 0.15f))
                                .clickable { albumName = "" },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("✕", fontSize = 10.sp, color = subFg)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(sectionBg)
                        .padding(horizontal = 14.dp),
                ) {
                    listOf(
                        Triple("Shared album", "Add collaborators later", false),
                        Triple("Sync to cloud", "Backup automatically", true),
                    ).forEachIndexed { i, (label, sub, on) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                                Text(sub, fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                            }
                            MiniToggle(on = on, dark = dark)
                        }
                        if (i == 0) {
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(divider))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

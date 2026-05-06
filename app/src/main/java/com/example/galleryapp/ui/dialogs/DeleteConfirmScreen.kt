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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.AccentPink
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun DeleteConfirmScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val optionBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val divider = if (dark) Color(0x0FFFFFFF) else Color(0xFFEDF2F7)

    var removeBackup by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        CenterDialogFrame(dark = dark, onClose = onClose) {
            Column {
                // Content
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp).padding(top = 26.dp, bottom = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(AccentPink.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🗑", fontSize = 28.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Delete 5 items?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = fg,
                        fontFamily = InterFontFamily,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "They'll move to Recently deleted for 30 days, then be permanently removed.",
                        fontSize = 13.sp,
                        color = subFg,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp,
                        fontFamily = InterFontFamily,
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(optionBg)
                            .clickable { removeBackup = !removeBackup }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (removeBackup) BrandBlue else Color.Transparent)
                                .border(
                                    width = if (removeBackup) 0.dp else 1.5.dp,
                                    color = if (removeBackup) Color.Transparent else subFg,
                                    shape = RoundedCornerShape(5.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (removeBackup) {
                                Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Text(
                            "Also remove backup copies",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = fg,
                            modifier = Modifier.weight(1f),
                            fontFamily = InterFontFamily,
                        )
                    }
                }

                // Footer buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(divider)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, if (dark) Color(0x1FFFFFFF) else Color(0xFFE5EAF2), RoundedCornerShape(22.dp))
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Cancel", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(AccentPink)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Move to bin", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = InterFontFamily)
                    }
                }
            }
        }
    }
}

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun RenameScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val inputBg = if (dark) Color(0x0FFFFFFF) else Color(0xFFF4F8FB)
    val divider = if (dark) Color(0x0FFFFFFF) else Color(0xFFEDF2F7)

    var name by remember { mutableStateOf("Anjuna sunset") }

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        CenterDialogFrame(dark = dark, onClose = onClose) {
            Column {
                Column(modifier = Modifier.padding(horizontal = 22.dp).padding(top = 22.dp, bottom = 6.dp)) {
                    Text(
                        "Rename file",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = fg,
                        fontFamily = InterFontFamily,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "IMG_20240409_142733.jpg",
                        fontSize = 12.sp,
                        color = subFg,
                        fontFamily = InterFontFamily,
                    )
                    Spacer(Modifier.height(16.dp))

                    // Input field (simulated — no actual text input needed for the design prototype)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(inputBg)
                            .border(1.5.dp, BrandBlue, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = fg,
                                fontFamily = InterFontFamily,
                            )
                            if (name.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (dark) Color(0x1FFFFFFF) else Color(0xFFD5DCE5))
                                        .clickable { name = "" },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✕", fontSize = 10.sp, color = fg, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${name.length}/64 characters",
                        fontSize = 11.sp,
                        color = subFg,
                        fontFamily = InterFontFamily,
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(divider))

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
                            .background(BrandBlue)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Save", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = InterFontFamily)
                    }
                }
            }
        }
    }
}

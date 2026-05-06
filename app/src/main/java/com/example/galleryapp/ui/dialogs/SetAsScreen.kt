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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun SetAsScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val divider = if (dark) Color(0x0AFFFFFF) else Color(0xFFEDF2F7)

    val targets = listOf(
        Triple("🏠", "Home wallpaper" to "Behind your home apps", Color(0xFF0066FF)),
        Triple("🔒", "Lock screen wallpaper" to "Replace current lockscreen", Color(0xFFA78BFA)),
        Triple("👤", "Contact photo" to "Pick a contact to assign", Color(0xFFFF3D7F)),
        Triple("💬", "Profile picture" to "Apps that allow it", Color(0xFF34D399)),
        Triple("★", "Album cover" to "Goa Trip", Color(0xFFFBBF24)),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        BottomSheetFrame(dark = dark, onClose = onClose, title = "Use photo as") {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                // Preview
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .width(96.dp)
                            .height(144.dp)
                            .shadow(12.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        ThumbPlaceholder(seed = 425, modifier = Modifier.fillMaxSize())
                    }
                }
                Spacer(Modifier.height(14.dp))

                targets.forEachIndexed { i, (emoji, labelPair, accentColor) ->
                    val (label, sub) = labelPair
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onClose)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(accentColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(emoji, fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                            Text(sub, fontSize = 11.5.sp, color = subFg, fontFamily = InterFontFamily)
                        }
                        Text("›", fontSize = 18.sp, color = subFg)
                    }
                    if (i < targets.lastIndex) {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(1.dp).background(divider))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

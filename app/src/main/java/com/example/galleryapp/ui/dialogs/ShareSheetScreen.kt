package com.example.galleryapp.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SubtextGray

@Composable
fun ShareSheetScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val cardBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val divider = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)

    val contacts = listOf(
        Pair("Sara", Color(0xFFFFB4D2)),
        Pair("Arjun", Color(0xFFA78BFA)),
        Pair("Mom", Color(0xFFFBBF24)),
        Pair("Riya", Color(0xFF34D399)),
        Pair("Karan", Color(0xFF0066FF)),
        Pair("Dev", Color(0xFFF97316)),
    )
    val apps = listOf(
        Triple("WhatsApp", Color(0xFF25D366), "💬"),
        Triple("Messages", Color(0xFF3B82F6), "✉"),
        Triple("Mail", Color(0xFFF97316), "✉"),
        Triple("Drive", Color(0xFFFFC107), "▲"),
        Triple("Telegram", Color(0xFF229ED9), "✈"),
        Triple("Instagram", Color(0xFFE1306C), "◉"),
        Triple("Bluetooth", Color(0xFF0066FF), "✦"),
        Triple("Nearby", Color(0xFF34D399), "≋"),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        BottomSheetFrame(dark = dark, onClose = onClose) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Preview strip
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                    Text("Share 3 photos", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = InterFontFamily)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(210, 215, 222).forEach { seed ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                ThumbPlaceholder(seed = seed, modifier = Modifier.fillMaxSize())
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (dark) Color(0x0FFFFFFF) else Color(0xFFF4F8FB)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🖼", fontSize = 14.sp)
                                Text("Original", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = subFg, fontFamily = InterFontFamily)
                            }
                        }
                    }
                }

                // Contacts
                Text(
                    "SEND TO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    contacts.forEach { (name, color) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(56.dp)
                                .clickable {},
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(color),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(name.first().toString(), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                            Text(name, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = fg, fontFamily = InterFontFamily)
                        }
                    }
                }

                // Apps grid
                Text(
                    "APPS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    apps.chunked(4).forEach { rowApps ->
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            rowApps.forEach { (appName, color, emoji) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth().clickable {},
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(color),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(emoji, fontSize = 20.sp)
                                    }
                                    Text(appName, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = fg, fontFamily = InterFontFamily)
                                }
                            }
                        }
                    }
                }

                // Options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 18.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .padding(horizontal = 14.dp),
                ) {
                    listOf(
                        Triple("Share original quality", "12.4 MB total", false),
                        Triple("Edit before sending", null, false),
                        Triple("Hide location", "Strip GPS metadata", true),
                    ).forEachIndexed { i, (label, sub, hasToggle) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = fg, fontFamily = InterFontFamily)
                                if (sub != null) {
                                    Text(sub, fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                                }
                            }
                            if (hasToggle) {
                                MiniToggle(on = true, dark = dark)
                            } else {
                                Text("›", fontSize = 18.sp, color = subFg)
                            }
                        }
                        if (i < 2) {
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(divider))
                        }
                    }
                }
            }
        }
    }
}

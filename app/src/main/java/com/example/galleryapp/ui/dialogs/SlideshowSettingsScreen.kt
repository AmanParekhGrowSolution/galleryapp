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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
fun SlideshowSettingsScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val inactiveBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val inactiveBorder = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)
    val sectionBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val divider = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)

    var duration by remember { mutableFloatStateOf(3f) }
    var transition by remember { mutableStateOf("fade") }
    var loop by remember { mutableStateOf(true) }
    var shuffle by remember { mutableStateOf(false) }
    var music by remember { mutableStateOf(true) }

    val transitions = listOf("fade" to "Fade", "slide" to "Slide", "ken" to "Ken Burns", "dissolve" to "Dissolve")

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        BottomSheetFrame(
            dark = dark,
            onClose = onClose,
            title = "Slideshow",
            footer = {
                PrimaryButton(text = "▶  Start slideshow", onClick = onClose)
            },
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
            ) {
                // Duration slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Time per photo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                    Text("${duration.toInt()}s", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue, fontFamily = InterFontFamily)
                }
                Slider(
                    value = duration,
                    onValueChange = { duration = it },
                    valueRange = 1f..10f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandBlue,
                        activeTrackColor = BrandBlue,
                        inactiveTrackColor = if (dark) Color(0x1FFFFFFF) else Color(0xFFE5EAF2),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 0.dp, bottom = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    listOf("1s", "5s", "10s").forEach { label ->
                        Text(label, fontSize = 10.sp, color = subFg, fontFamily = InterFontFamily)
                    }
                }

                // Transition
                Text(
                    "TRANSITION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 18.dp),
                ) {
                    transitions.forEach { (id, label) ->
                        val isActive = transition == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isActive) BrandBlue else inactiveBg)
                                .border(width = if (isActive) 0.dp else 1.dp, color = inactiveBorder, shape = RoundedCornerShape(10.dp))
                                .clickable { transition = id },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isActive) Color.White else fg, fontFamily = InterFontFamily)
                        }
                    }
                }

                // Toggles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(sectionBg)
                        .padding(horizontal = 14.dp)
                        .padding(bottom = 18.dp),
                ) {
                    listOf(
                        Triple("Loop", "Restart from first photo", loop),
                        Triple("Shuffle", "Random order", shuffle),
                        Triple("Background music", "Ambient · 0:42", music),
                    ).forEachIndexed { i, (label, sub, isOn) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(if (i == 0) "↺" else if (i == 1) "⬦" else "♪", fontSize = 18.sp, color = subFg)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                                Text(sub, fontSize = 11.sp, color = subFg, fontFamily = InterFontFamily)
                            }
                            Box(
                                modifier = Modifier.clickable {
                                    when (i) {
                                        0 -> loop = !loop
                                        1 -> shuffle = !shuffle
                                        2 -> music = !music
                                    }
                                }
                            ) {
                                MiniToggle(on = isOn, dark = dark)
                            }
                        }
                        if (i < 2) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(divider))
                    }
                }
            }
        }
    }
}

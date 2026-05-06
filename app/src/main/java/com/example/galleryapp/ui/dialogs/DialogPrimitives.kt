package com.example.galleryapp.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.DarkBg
import com.example.galleryapp.ui.theme.InterFontFamily
import com.example.galleryapp.ui.theme.OnSurfaceDark

@Composable
fun PhantomGridBg(dark: Boolean, modifier: Modifier = Modifier) {
    val bg = if (dark) DarkBg else Color.White
    val fg = if (dark) Color.White else OnSurfaceDark

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .padding(top = 32.dp),
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (dark) Color(0x0FFFFFFF) else Color(0xFFF4F8FB)),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Photos",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = fg,
                fontFamily = InterFontFamily,
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .blur(2.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            userScrollEnabled = false,
        ) {
            items(12) { i ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    ThumbPlaceholder(seed = 400 + i, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.15f)))
                }
            }
        }
    }
}

/** Full-screen overlay with scrim + bottom-aligned sheet. Place inside a Box(fillMaxSize). */
@Composable
fun BoxScope.BottomSheetFrame(
    dark: Boolean,
    onClose: () -> Unit,
    title: String? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val bg = if (dark) Color(0xFF1A1818) else Color.White
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else Color(0xFF597393)
    val handleColor = if (dark) Color(0x2EFFFFFF) else Color(0xFFD5DCE5)

    // Scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8C000000))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClose,
            ),
    )

    // Sheet anchored to bottom
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(bg)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {},
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = if (title != null) 6.dp else 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(handleColor),
            )
        }

        if (title != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = fg,
                    fontFamily = InterFontFamily,
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", fontSize = 16.sp, color = subFg)
                }
            }
        }

        content()

        if (footer != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
            ) {
                footer()
            }
        } else {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/** Full-screen overlay with scrim + centered dialog. Place inside a Box(fillMaxSize). */
@Composable
fun BoxScope.CenterDialogFrame(
    dark: Boolean,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val bg = if (dark) Color(0xFF1A1818) else Color.White

    // Scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8C000000))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClose,
            ),
    )

    // Centered dialog card
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {},
    ) {
        content()
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(23.dp))
            .background(BrandBlue)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontFamily = InterFontFamily,
        )
    }
}

@Composable
fun ThumbPlaceholder(seed: Int, modifier: Modifier = Modifier) {
    val hue = (seed * 67) % 360
    val color = Color.hsl(hue.toFloat(), 0.55f, 0.55f)
    Box(modifier = modifier.background(color))
}

@Composable
fun MiniToggle(on: Boolean, dark: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 38.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (on) BrandBlue else if (dark) Color(0x1AFFFFFF) else Color(0xFFE4EAF1)),
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .size(18.dp)
                .align(if (on) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White),
        )
    }
}

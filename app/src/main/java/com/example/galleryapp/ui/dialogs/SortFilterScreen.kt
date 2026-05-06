package com.example.galleryapp.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.mutableIntStateOf
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SortFilterScreen(
    onClose: () -> Unit,
    dark: Boolean = false,
) {
    val fg = if (dark) Color.White else OnSurfaceDark
    val subFg = if (dark) Color(0x99FFFFFF) else SubtextGray
    val inactiveBg = if (dark) Color(0x0AFFFFFF) else Color(0xFFF4F8FB)
    val inactiveBorder = if (dark) Color(0x0FFFFFFF) else Color(0xFFE8EFF6)

    var sort by remember { mutableStateOf("newest") }
    var selectedTypes by remember { mutableStateOf(setOf("photo", "video")) }
    var gridSize by remember { mutableIntStateOf(2) }

    val sorts = listOf(
        "newest" to "Newest first",
        "oldest" to "Oldest first",
        "name" to "Name A → Z",
        "size" to "Largest first",
    )
    val typeOpts = listOf(
        "photo" to "Photos",
        "video" to "Videos",
        "raw" to "RAW",
        "screen" to "Screenshots",
        "live" to "Live photos",
        "fav" to "Favourites",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        PhantomGridBg(dark = dark)

        BottomSheetFrame(
            dark = dark,
            onClose = onClose,
            title = "Sort & filter",
            footer = {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, if (dark) Color(0x1FFFFFFF) else Color(0xFFE5EAF2), RoundedCornerShape(22.dp))
                            .clickable {
                                sort = "newest"
                                selectedTypes = setOf("photo", "video")
                                gridSize = 2
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg, fontFamily = InterFontFamily)
                    }
                    PrimaryButton(text = "Apply", onClick = onClose, modifier = Modifier.weight(1.5f))
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
            ) {
                // Sort section
                Text(
                    "SORT BY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                sorts.forEach { (id, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sort = id }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            label,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = fg,
                            fontFamily = InterFontFamily,
                        )
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (sort == id) 6.dp else 2.dp,
                                    color = if (sort == id) BrandBlue else if (dark) Color(0x40FFFFFF) else Color(0xFFCBD5E1),
                                    shape = CircleShape,
                                ),
                        )
                    }
                }

                // Types section
                Text(
                    "SHOW TYPES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    typeOpts.forEach { (id, label) ->
                        val isOn = id in selectedTypes
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isOn) BrandBlue else inactiveBg)
                                .clickable {
                                    selectedTypes = if (isOn) selectedTypes - id else selectedTypes + id
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(
                                label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOn) Color.White else fg,
                                fontFamily = InterFontFamily,
                            )
                        }
                    }
                }

                // Grid size
                Text(
                    "GRID SIZE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subFg,
                    letterSpacing = 0.4.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5).forEach { (id, cols) ->
                        val isActive = gridSize == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isActive) (if (dark) Color(0x240066FF) else Color(0xFFE8F0FF)) else inactiveBg)
                                .border(
                                    width = if (isActive) 1.5.dp else 1.dp,
                                    color = if (isActive) BrandBlue else inactiveBorder,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .clickable { gridSize = id }
                                .padding(6.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                repeat(cols) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        repeat(cols) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(1.5.dp))
                                                    .background(
                                                        if (isActive) BrandBlue
                                                        else if (dark) Color(0x2EFFFFFF)
                                                        else Color(0xFFCBD5E1)
                                                    ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

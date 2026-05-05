package com.example.galleryapp.ui.collage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.DarkBgDeep

private val photoColors = listOf(
    Color(0xFF2A4A7C), Color(0xFF3A1A5C), Color(0xFF1A5C3A),
    Color(0xFF5C3A1A), Color(0xFF5C1A3A), Color(0xFF1A4C5C),
)

private val layouts = listOf(
    listOf(listOf(0f, 0f, 1f, 1f)),
    listOf(listOf(0f, 0f, 0.5f, 1f), listOf(0.5f, 0f, 0.5f, 1f)),
    listOf(listOf(0f, 0f, 1f, 0.5f), listOf(0f, 0.5f, 0.5f, 0.5f), listOf(0.5f, 0.5f, 0.5f, 0.5f)),
    listOf(
        listOf(0f, 0f, 0.5f, 0.5f), listOf(0.5f, 0f, 0.5f, 0.5f),
        listOf(0f, 0.5f, 0.5f, 0.5f), listOf(0.5f, 0.5f, 0.5f, 0.5f),
    ),
    listOf(listOf(0f, 0f, 0.66f, 1f), listOf(0.66f, 0f, 0.34f, 0.5f), listOf(0.66f, 0.5f, 0.34f, 0.5f)),
    listOf(
        listOf(0f, 0f, 1f, 0.4f),
        listOf(0f, 0.4f, 0.33f, 0.6f), listOf(0.33f, 0.4f, 0.33f, 0.6f), listOf(0.66f, 0.4f, 0.34f, 0.6f),
    ),
)

private val backgroundColors = listOf(
    0xFFFFFFFFu.toLong(), 0xFF000000u.toLong(), 0xFF0066FFu.toLong(),
    0xFFFF3D7Fu.toLong(), 0xFF34D399u.toLong(), 0xFFFFDC00u.toLong(),
    0xFFA78BFAu.toLong(), 0xFFF97316u.toLong(),
)

@Composable
fun CollageMakerScreen(
    onBack: () -> Unit,
    viewModel: CollageMakerViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState as? CollageMakerUiState.Editing ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgDeep),
    ) {
        CollageTopBar(
            onBack = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp),
        )

        CollageCanvas(
            selectedLayoutIndex = state.selectedLayoutIndex,
            backgroundColorHex = state.backgroundColorHex,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(18.dp),
        )

        CollageTabContent(
            state = state,
            onSelectLayout = viewModel::selectLayout,
            onSelectBackground = viewModel::selectBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
        )

        CollageTabBar(
            activeTab = state.activeTab,
            onSelectTab = viewModel::selectTab,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF070707))
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun CollageTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.White,
            )
        }
        Text(
            text = stringResource(R.string.collage_title),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandBlue)
                .clickable { }
                .padding(horizontal = 18.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.save),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun CollageCanvas(
    selectedLayoutIndex: Int,
    backgroundColorHex: Long,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(backgroundColorHex))
                .padding(6.dp),
        ) {
            val tiles = layouts[selectedLayoutIndex]
            tiles.forEachIndexed { index, tile ->
                val colorIndex = index % photoColors.size
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(tile[2])
                            .fillMaxSize()
                            .padding(start = (tile[0] * 100).dp.coerceAtMost(0.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(photoColors[colorIndex]),
                    )
                }
            }
        }
    }
}

@Composable
private fun CollageTabContent(
    state: CollageMakerUiState.Editing,
    onSelectLayout: (Int) -> Unit,
    onSelectBackground: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.height(90.dp)) {
        when (state.activeTab) {
            CollageTab.LAYOUT -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(layouts, key = { i, _ -> i }) { index, layout ->
                        LayoutThumbnail(
                            tiles = layout,
                            isSelected = state.selectedLayoutIndex == index,
                            onClick = { onSelectLayout(index) },
                        )
                    }
                }
            }
            CollageTab.RATIO -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val ratios = listOf("1:1", "4:5", "9:16", "16:9", "3:4", "2:3", "Free")
                    itemsIndexed(ratios, key = { i, _ -> i }) { index, ratio ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (index == 0) BrandBlue else Color.White.copy(alpha = 0.06f))
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        ) {
                            Text(ratio, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            CollageTab.BACKGROUND -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(backgroundColors, key = { i, _ -> i }) { _, colorHex ->
                        val isSelected = state.backgroundColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .then(
                                    if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                                    else Modifier.border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                )
                                .clickable { onSelectBackground(colorHex) },
                        )
                    }
                }
            }
            CollageTab.SPACING -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(12.dp, 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "${stringResource(R.string.collage_border)} ${state.borderPx} px",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${stringResource(R.string.collage_corner_radius)} ${state.cornerRadius} dp",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun LayoutThumbnail(
    tiles: List<List<Float>>,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) BrandBlue.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BrandBlue else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .padding(4.dp),
    ) {
        tiles.forEach { tile ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(tile[2])
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isSelected) BrandBlue else Color.White.copy(alpha = 0.3f)),
            )
        }
    }
}

@Composable
private fun CollageTabBar(
    activeTab: CollageTab,
    onSelectTab: (CollageTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf(
        Triple(CollageTab.LAYOUT, Icons.Default.GridView, R.string.collage_tab_layout),
        Triple(CollageTab.RATIO, Icons.Default.SpaceBar, R.string.collage_tab_ratio),
        Triple(CollageTab.BACKGROUND, Icons.Default.Image, R.string.collage_tab_background),
        Triple(CollageTab.SPACING, Icons.Default.Settings, R.string.collage_tab_spacing),
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        tabs.forEach { (tab, icon, labelRes) ->
            val isActive = activeTab == tab
            val tint = if (isActive) BrandBlue else Color.White.copy(alpha = 0.7f)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectTab(tab) }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Icon(imageVector = icon, contentDescription = stringResource(labelRes), tint = tint, modifier = Modifier.size(18.dp))
                Text(text = stringResource(labelRes), color = tint, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

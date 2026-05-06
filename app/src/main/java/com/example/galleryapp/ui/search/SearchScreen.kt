package com.example.galleryapp.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SectionLabelGray
import com.example.galleryapp.ui.theme.SubtextGray

private val peopleSamples = listOf("You", "Sara", "Arjun", "Mom", "+ Tag")
private val thingsSamples = listOf("Beach", "Food", "Selfies", "Documents", "Sunset", "Pets")
private val placesSamples = listOf("Mumbai" to "247 photos", "Goa" to "64 photos", "Bengaluru" to "38 photos")
private val thingColors = listOf(
    Color(0xFF2D6A4F), Color(0xFFD4875E), Color(0xFF5E72A8),
    Color(0xFF7B4F9E), Color(0xFFB85C55), Color(0xFF3D7A6E)
)

@Composable
fun SearchScreen(
    onPhotoClick: (Long) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBarRow(query = query, onQueryChange = viewModel::onQueryChange)
            }

            when (val state = uiState) {
                is SearchUiState.Empty -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        AIHintRow()
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PeopleSectionHeader()
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PeopleRow()
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SectionHeader(label = stringResource(R.string.things))
                    }
                    items(thingsSamples.zip(thingColors), key = { it.first }) { (name, color) ->
                        ThingTile(name = name, color = color)
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PlacesSectionHeader()
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PlacesList()
                    }
                }
                is SearchUiState.Results -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "${state.photos.size} results",
                            color = SubtextGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(state.photos, key = { it.id }) { photo ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(photo.placeholderColor))
                                .clickable { onPhotoClick(photo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBarRow(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF0F1F5))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.content_desc_search),
                tint = SubtextGray,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_hint_full),
                        color = SubtextGray,
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(color = OnSurfaceDark, fontSize = 15.sp),
                    cursorBrush = SolidColor(BrandBlue),
                    singleLine = true
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = SubtextGray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onQueryChange("") }
                )
            }
        }
    }
}

@Composable
private fun AIHintRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F1F5))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BrandBlue.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(18.dp).semantics { role = Role.Image }
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = stringResource(R.string.search_ai_label),
                color = OnSurfaceDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.search_ai_subtitle),
                color = SubtextGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label,
        color = OnSurfaceDark,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun PeopleSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.people),
            color = OnSurfaceDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.see_all),
            color = BrandBlue,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun PeopleRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        peopleSamples.forEach { name ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EBF5))
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = name,
                        tint = SubtextGray,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(name, color = SubtextGray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ThingTile(name: String, color: Color) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x99000000))))
        )
        Text(
            text = name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun PlacesSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.places),
            color = OnSurfaceDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.map_link),
            color = BrandBlue,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun PlacesList() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        placesSamples.forEach { (city, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE8EBF5))
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = stringResource(R.string.content_desc_place_pin),
                        tint = BrandBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(city, color = OnSurfaceDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(count, color = SubtextGray, fontSize = 12.sp)
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SubtextGray,
                    modifier = Modifier.size(20.dp).semantics { role = Role.Image }
                )
            }
        }
    }
}

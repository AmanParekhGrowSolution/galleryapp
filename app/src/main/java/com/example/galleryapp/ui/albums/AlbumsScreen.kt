package com.example.galleryapp.ui.albums

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.domain.model.Album
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.OnSurfaceDark
import com.example.galleryapp.ui.theme.SectionLabelGray
import com.example.galleryapp.ui.theme.SubtextGray
import com.example.galleryapp.ui.theme.SurfaceLight

private val darkOverlay = listOf(Color.Transparent, Color(0xCC000000))

@Composable
fun AlbumsScreen(
    onVaultClick: () -> Unit,
    onCleanerClick: () -> Unit,
    onPhotoClick: (Long) -> Unit,
    viewModel: AlbumsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        when (val state = uiState) {
            is AlbumsUiState.Loading -> CircularProgressIndicator(
                color = BrandBlue,
                modifier = Modifier.align(Alignment.Center)
            )
            is AlbumsUiState.Success -> AlbumsContent(
                state = state,
                onVaultClick = onVaultClick,
                onCleanerClick = onCleanerClick
            )
            is AlbumsUiState.Error -> Text(
                text = state.message,
                color = OnSurfaceDark,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun AlbumsContent(
    state: AlbumsUiState.Success,
    onVaultClick: () -> Unit,
    onCleanerClick: () -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        item { AlbumsTopBar() }
        item { FeaturedGrid(albums = state.featured) }
        item { SectionHeader(label = stringResource(R.string.my_albums), count = state.myAlbums.size) }
        item { MyAlbumsGrid(albums = state.myAlbums) }
    }
}

@Composable
private fun AlbumsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.content_desc_menu),
                tint = OnSurfaceDark
            )
        }
        Text(
            text = stringResource(R.string.albums_title),
            color = OnSurfaceDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add), tint = OnSurfaceDark)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Sort, contentDescription = stringResource(R.string.sort), tint = OnSurfaceDark)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.content_desc_more), tint = OnSurfaceDark)
        }
    }
}

@Composable
private fun SectionHeader(label: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = OnSurfaceDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$count",
            color = BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FeaturedGrid(albums: List<Album>) {
    val rows = albums.chunked(2)
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowAlbums ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowAlbums.forEach { album ->
                    FeaturedAlbumTile(album = album, modifier = Modifier.weight(1f))
                }
                if (rowAlbums.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FeaturedAlbumTile(album: Album, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(album.coverColor).copy(alpha = 0.9f),
                        Color(album.coverColor)
                    )
                )
            )
            .clickable {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(darkOverlay))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                album.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${album.count} items",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MyAlbumsGrid(albums: List<Album>) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        albums.chunked(3).forEach { rowAlbums ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowAlbums.forEach { album ->
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(album.coverColor).copy(alpha = 0.8f),
                                            Color(album.coverColor)
                                        )
                                    )
                                )
                                .clickable {}
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            album.name,
                            color = OnSurfaceDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Text(
                            "${album.count}",
                            color = SubtextGray,
                            fontSize = 11.sp
                        )
                    }
                }
                repeat(3 - rowAlbums.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

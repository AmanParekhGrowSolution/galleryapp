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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.domain.model.Album

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
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
            .background(Brush.verticalGradient(bgGradient))
    ) {
        when (val state = uiState) {
            is AlbumsUiState.Loading -> CircularProgressIndicator(
                color = Color(0xFF8B5CF6),
                modifier = Modifier.align(Alignment.Center)
            )
            is AlbumsUiState.Success -> AlbumsContent(
                state = state,
                onVaultClick = onVaultClick,
                onCleanerClick = onCleanerClick
            )
            is AlbumsUiState.Error -> Text(
                text = state.message,
                color = Color.White,
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
        item { SectionLabel(stringResource(R.string.featured)) }
        item { FeaturedGrid(albums = state.featured) }
        item { SectionLabel(stringResource(R.string.my_albums)) }
        item { MyAlbumsRow(albums = state.myAlbums) }
        if (state.social.isNotEmpty()) {
            item { SectionLabel(stringResource(R.string.social_apps)) }
            items(state.social) { album -> SocialAlbumRow(album = album) }
        }
        item { SectionLabel(stringResource(R.string.utility)) }
        item { UtilitySection(onVaultClick = onVaultClick) }
    }
}

@Composable
private fun AlbumsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.albums_title),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        IconButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add), tint = Color.White)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Sort, contentDescription = null, tint = Color.White)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.content_desc_more), tint = Color.White)
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun FeaturedGrid(albums: List<Album>) {
    val rows = albums.chunked(2)
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowAlbums ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color(album.coverColor))
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
            Text(album.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("${album.count}", color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun MyAlbumsRow(albums: List<Album>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums) { album ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(album.coverColor).copy(alpha = 0.8f), Color(album.coverColor))
                            )
                        )
                        .clickable {}
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(album.name, color = Color.White, fontSize = 12.sp, maxLines = 1)
                Text("${album.count}", color = Color.White.copy(alpha = 0.87f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SocialAlbumRow(album: Album) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(album.coverColor))
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(album.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("${album.count}", color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun UtilitySection(onVaultClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        UtilityRow(
            icon = Icons.Default.Favorite,
            iconGradient = listOf(Color(0xFFEF4444), Color(0xFFDC2626)),
            label = stringResource(R.string.favorites),
            subtitle = null,
            onClick = {}
        )
        UtilityRow(
            icon = Icons.Default.Lock,
            iconGradient = primaryGradient,
            label = stringResource(R.string.vault_album),
            subtitle = stringResource(R.string.vault_subtitle_short),
            onClick = onVaultClick
        )
        UtilityRow(
            icon = Icons.Default.Delete,
            iconGradient = listOf(Color(0xFF6B7280), Color(0xFF4B5563)),
            label = stringResource(R.string.recently_deleted),
            subtitle = null,
            onClick = {}
        )
    }
}

@Composable
private fun UtilityRow(
    icon: ImageVector,
    iconGradient: List<Color>,
    label: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(iconGradient))
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
            }
        }
    }
}

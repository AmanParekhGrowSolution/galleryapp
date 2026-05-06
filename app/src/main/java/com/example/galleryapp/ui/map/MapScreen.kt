package com.example.galleryapp.ui.map

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.R

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val mapBgGradient = listOf(Color(0xFF0D2137), Color(0xFF1A3852), Color(0xFF1E4B6E))

private data class MapPin(val name: String, val count: Int, val x: Float, val y: Float, val color: Long)

private val mapPins = listOf(
    MapPin("Mumbai", 247, 0.35f, 0.55f, 0xFF1E3A5FL),
    MapPin("Goa", 64, 0.3f, 0.65f, 0xFF2D5A27L),
    MapPin("Pune", 38, 0.38f, 0.58f, 0xFF5C1F1FL),
    MapPin("Bengaluru", 28, 0.37f, 0.72f, 0xFF3D1F5CL),
)

@Composable
fun MapScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(mapBgGradient))
        ) {
            mapPins.forEach { pin ->
                MapPinItem(
                    pin = pin,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = (pin.x * 350).dp,
                            top = (pin.y * 500).dp
                        )
                )
            }
        }

        MapTopBar(
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        MapControls(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp))

        LocationInfoCard(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun MapTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
        }
        Text(
            text = stringResource(R.string.map_title),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(Icons.Default.FilterAlt, contentDescription = stringResource(R.string.filter), tint = Color.White)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.content_desc_more), tint = Color.White)
        }
    }
}

@Composable
private fun MapPinItem(pin: MapPin, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(x = (-40).dp, y = (-40).dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Color.White, Color(0xFFF5F5F5))))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.linearGradient(listOf(Color(pin.color), Color(pin.color).copy(alpha = 0.85f))))
                    )
                    Column(modifier = Modifier.padding(start = 6.dp)) {
                        Text(pin.name, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.photos_label, pin.count), color = Color.Black.copy(alpha = 0.6f), fontSize = 9.sp)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color.White, Color(0xFFF0F0F0))))
            )
        }
    }
}

@Composable
private fun MapControls(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MapControlButton(icon = Icons.Default.Add, contentDescription = stringResource(R.string.zoom_in), onClick = {})
        MapControlButton(icon = Icons.Default.Remove, contentDescription = stringResource(R.string.zoom_out), onClick = {})
        MapControlButton(icon = Icons.Default.Layers, contentDescription = stringResource(R.string.change_map_layers), onClick = {})
        MapControlButton(icon = Icons.Default.GpsFixed, contentDescription = stringResource(R.string.my_location), onClick = {})
    }
}

@Composable
private fun MapControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.15f))))
            .clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun LocationInfoCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF0F0C29).copy(alpha = 0.95f), Color(0xFF0F0C29))))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF1E3A5F), Color(0xFF0D2D4A))))
                )
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Mumbai", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        stringResource(R.string.photos_label, 247),
                        color = Color.White.copy(alpha = 0.87f),
                        fontSize = 12.sp
                    )
                    Text("Mar 2024 – Apr 2026", color = Color.White.copy(alpha = 0.87f), fontSize = 11.sp)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(primaryGradient))
                        .clickable {}
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.view), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF1E3A5F).copy(alpha = (0.4f + it * 0.1f)), Color(0xFF1E3A5F).copy(alpha = (0.4f + it * 0.1f)))))
                    )
                }
            }
        }
    }
}

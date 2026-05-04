package com.example.galleryapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.galleryapp.R
import com.example.galleryapp.ui.albums.AlbumsScreen
import com.example.galleryapp.ui.home.HomeScreen
import com.example.galleryapp.ui.moments.MomentsScreen
import com.example.galleryapp.ui.search.SearchScreen

private val bgGradient = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))

private data class TabItem(
    val route: String,
    val icon: ImageVector,
    val labelRes: Int
)

@Composable
fun MainScreen(
    onNavigateToPhotoViewer: (Long) -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToCleaner: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val innerNav = rememberNavController()
    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        TabItem(Screen.Home, Icons.Default.PhotoLibrary, R.string.tab_photos),
        TabItem(Screen.Albums, Icons.Default.PhotoLibrary, R.string.tab_albums),
        TabItem(Screen.Search, Icons.Default.Search, R.string.tab_search),
        TabItem(Screen.Moments, Icons.Default.AutoAwesome, R.string.tab_moments),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        NavHost(
            navController = innerNav,
            startDestination = Screen.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            composable(Screen.Home) {
                HomeScreen(
                    onPhotoClick = onNavigateToPhotoViewer,
                    onSettingsClick = onNavigateToSettings,
                    onPremiumClick = onNavigateToPremium,
                    onVaultClick = onNavigateToVault
                )
            }
            composable(Screen.Albums) {
                AlbumsScreen(
                    onVaultClick = onNavigateToVault,
                    onCleanerClick = onNavigateToCleaner,
                    onPhotoClick = onNavigateToPhotoViewer
                )
            }
            composable(Screen.Search) {
                SearchScreen(onPhotoClick = onNavigateToPhotoViewer)
            }
            composable(Screen.Moments) {
                MomentsScreen(
                    onMapClick = onNavigateToMap,
                    onPhotoClick = onNavigateToPhotoViewer
                )
            }
        }

        BottomNavBar(
            tabs = tabs,
            currentRoute = currentRoute,
            onTabSelected = { route ->
                innerNav.navigate(route) {
                    popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun BottomNavBar(
    tabs: List<TabItem>,
    currentRoute: String?,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F0C29).copy(alpha = 0.95f), Color(0xFF0F0C29))))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab.route) }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.labelRes),
                        tint = if (selected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(tab.labelRes),
                        color = if (selected) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

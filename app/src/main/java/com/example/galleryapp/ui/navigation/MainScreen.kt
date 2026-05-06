package com.example.galleryapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.galleryapp.ui.theme.BrandBlue
import com.example.galleryapp.ui.theme.SubtextGray

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
    onNavigateToMap: () -> Unit,
    onNavigateToMemoriesStory: () -> Unit = {},
    onNavigateToSlideshow: () -> Unit = {},
    onNavigateToCollageMaker: () -> Unit = {},
) {
    val innerNav = rememberNavController()
    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        TabItem(Screen.Home, Icons.Default.PhotoLibrary, R.string.tab_photos),
        TabItem(Screen.Albums, Icons.Default.Collections, R.string.tab_albums),
        TabItem(Screen.Search, Icons.Default.Search, R.string.tab_search),
        TabItem(Screen.Moments, Icons.Default.AutoAwesome, R.string.tab_moments),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NavHost(
            navController = innerNav,
            startDestination = Screen.Home,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 64.dp)
        ) {
            composable(Screen.Home) {
                HomeScreen(
                    onPhotoClick = onNavigateToPhotoViewer,
                    onVaultClick = onNavigateToVault,
                    onSettingsClick = onNavigateToSettings,
                    onPremiumClick = onNavigateToPremium,
                    onSearchClick = {
                        innerNav.navigate(Screen.Search) {
                            popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Albums) {
                AlbumsScreen(
                    onVaultClick = onNavigateToVault,
                    onCleanerClick = onNavigateToCleaner,
                    onPhotoClick = onNavigateToPhotoViewer,
                    onSettingsClick = onNavigateToSettings
                )
            }
            composable(Screen.Search) {
                SearchScreen(onPhotoClick = onNavigateToPhotoViewer)
            }
            composable(Screen.Moments) {
                MomentsScreen(
                    onMapClick = onNavigateToMap,
                    onPhotoClick = onNavigateToPhotoViewer,
                    onStoryClick = onNavigateToMemoriesStory,
                    onSettingsClick = onNavigateToSettings
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                NavTabItem(
                    icon = tab.icon,
                    label = stringResource(tab.labelRes),
                    selected = selected,
                    onClick = { onTabSelected(tab.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) BrandBlue else SubtextGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) BrandBlue else SubtextGray,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

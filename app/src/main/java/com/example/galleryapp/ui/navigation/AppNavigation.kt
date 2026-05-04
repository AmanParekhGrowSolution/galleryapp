package com.example.galleryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.galleryapp.ui.cleaner.CleanerScreen
import com.example.galleryapp.ui.editor.AIEditorScreen
import com.example.galleryapp.ui.map.MapScreen
import com.example.galleryapp.ui.onboarding.OnboardingScreen
import com.example.galleryapp.ui.premium.PremiumScreen
import com.example.galleryapp.ui.settings.SettingsScreen
import com.example.galleryapp.ui.splash.SplashScreen
import com.example.galleryapp.ui.vault.VaultScreen
import com.example.galleryapp.ui.viewer.PhotoViewerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash) {

        composable(Screen.Splash) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main) {
            MainScreen(
                onNavigateToPhotoViewer = { photoId ->
                    navController.navigate(Screen.photoViewer(photoId))
                },
                onNavigateToVault = { navController.navigate(Screen.Vault) },
                onNavigateToCleaner = { navController.navigate(Screen.Cleaner) },
                onNavigateToSettings = { navController.navigate(Screen.Settings) },
                onNavigateToPremium = { navController.navigate(Screen.Premium) },
                onNavigateToMap = { navController.navigate(Screen.Map) }
            )
        }

        composable(
            route = Screen.PhotoViewer,
            arguments = listOf(navArgument("photoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            PhotoViewerScreen(
                photoId = photoId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.aiEditor(photoId)) }
            )
        }

        composable(
            route = Screen.AIEditor,
            arguments = listOf(navArgument("photoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            AIEditorScreen(
                photoId = photoId,
                onClose = { navController.popBackStack() }
            )
        }

        composable(Screen.Vault) {
            VaultScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Cleaner) {
            CleanerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPremium = { navController.navigate(Screen.Premium) },
                onNavigateToVault = { navController.navigate(Screen.Vault) },
                onNavigateToCleaner = { navController.navigate(Screen.Cleaner) }
            )
        }

        composable(Screen.Premium) {
            PremiumScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.Map) {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}

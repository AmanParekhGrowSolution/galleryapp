package com.example.galleryapp.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.galleryapp.ui.backup.BackupScreen
import com.example.galleryapp.ui.cleaner.CleanerResultScreen
import com.example.galleryapp.ui.cleaner.CleanerRunningScreen
import com.example.galleryapp.ui.cleaner.CleanerScreen
import com.example.galleryapp.ui.collage.CollageMakerScreen
import com.example.galleryapp.ui.dialogs.CreateAlbumScreen
import com.example.galleryapp.ui.dialogs.DeleteConfirmScreen
import com.example.galleryapp.ui.dialogs.MapPlaceSheetScreen
import com.example.galleryapp.ui.dialogs.MoveToAlbumScreen
import com.example.galleryapp.ui.dialogs.MultiSelectScreen
import com.example.galleryapp.ui.dialogs.PhotoInfoScreen
import com.example.galleryapp.ui.dialogs.PremiumNudgeScreen
import com.example.galleryapp.ui.dialogs.RenameScreen
import com.example.galleryapp.ui.dialogs.SetAsScreen
import com.example.galleryapp.ui.dialogs.ShareSheetScreen
import com.example.galleryapp.ui.dialogs.SlideshowSettingsScreen
import com.example.galleryapp.ui.dialogs.SortFilterScreen
import com.example.galleryapp.ui.editor.AIEditorScreen
import com.example.galleryapp.ui.map.MapScreen
import com.example.galleryapp.ui.memories.MemoriesStoryScreen
import com.example.galleryapp.ui.onboarding.OnboardingScreen
import com.example.galleryapp.ui.premium.PremiumScreen
import com.example.galleryapp.ui.security.AppLockSetupScreen
import com.example.galleryapp.ui.settings.SettingsScreen
import com.example.galleryapp.ui.slideshow.SlideshowScreen
import com.example.galleryapp.ui.splash.SplashScreen
import com.example.galleryapp.ui.storage.StorageScreen
import com.example.galleryapp.ui.trash.TrashScreen
import com.example.galleryapp.ui.vault.VaultScreen
import com.example.galleryapp.ui.video.VideoPlayerScreen
import com.example.galleryapp.ui.video.VideoTrimmerScreen
import com.example.galleryapp.ui.viewer.PhotoViewerScreen

private const val PREFS_NAME = "gallery_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val isFirstRun = remember { !prefs.getBoolean(KEY_ONBOARDING_DONE, false) }
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
                },
                isFirstRun = isFirstRun
            )
        }

        composable(Screen.Onboarding) {
            OnboardingScreen(
                onComplete = {
                    prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
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
                onNavigateToMap = { navController.navigate(Screen.Map) },
                onNavigateToMemoriesStory = { navController.navigate(Screen.MemoriesStory) },
                onNavigateToSlideshow = { navController.navigate(Screen.Slideshow) },
                onNavigateToCollageMaker = { navController.navigate(Screen.CollageMaker) },
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

        composable(
            route = Screen.VideoPlayer,
            arguments = listOf(navArgument("photoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            VideoPlayerScreen(
                photoId = photoId,
                onBack = { navController.popBackStack() },
                onTrim = { id -> navController.navigate(Screen.videoTrimmer(id)) }
            )
        }

        composable(
            route = Screen.VideoTrimmer,
            arguments = listOf(navArgument("photoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            VideoTrimmerScreen(
                photoId = photoId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MemoriesStory) {
            MemoriesStoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.CollageMaker) {
            CollageMakerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Slideshow) {
            SlideshowScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AppLockSetup) {
            AppLockSetupScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.Vault) {
            VaultScreen(
                onBack = { navController.popBackStack() },
                onNeedsSetup = {
                    navController.navigate(Screen.AppLockSetup) {
                        popUpTo(Screen.Vault) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Cleaner) {
            CleanerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPremium = { navController.navigate(Screen.Premium) },
                onNavigateToVault = { navController.navigate(Screen.Vault) },
                onNavigateToCleaner = { navController.navigate(Screen.Cleaner) },
                onNavigateToTrash = { navController.navigate(Screen.Trash) },
                onNavigateToStorage = { navController.navigate(Screen.Storage) },
                onNavigateToBackup = { navController.navigate(Screen.Backup) },
                onNavigateToAppLock = { navController.navigate(Screen.AppLockSetup) }
            )
        }

        composable(Screen.Trash) {
            TrashScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Storage) {
            StorageScreen(
                onBack = { navController.popBackStack() },
                onOpenCleaner = { navController.navigate(Screen.Cleaner) }
            )
        }

        composable(Screen.Backup) {
            BackupScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Premium) {
            PremiumScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.Map) {
            MapScreen(onBack = { navController.popBackStack() })
        }

        // ── Wave 2 — overlays and dialogs ──────────────────────────────────────

        composable(Screen.MultiSelect) {
            MultiSelectScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.MoveToAlbum) {
            MoveToAlbumScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.CreateAlbum) {
            CreateAlbumScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.DeleteConfirm) {
            DeleteConfirmScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.ShareSheet) {
            ShareSheetScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.SortFilter) {
            SortFilterScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.PhotoInfo) {
            PhotoInfoScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.Rename) {
            RenameScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.SetAs) {
            SetAsScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.SlideshowSettings) {
            SlideshowSettingsScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.CleanerRunning) {
            CleanerRunningScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.CleanerResult) {
            CleanerResultScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.MapPlaceSheet) {
            MapPlaceSheetScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.PremiumNudge) {
            PremiumNudgeScreen(onClose = { navController.popBackStack() })
        }
    }
}

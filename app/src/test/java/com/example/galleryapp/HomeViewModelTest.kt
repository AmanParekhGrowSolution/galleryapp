package com.example.galleryapp

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.galleryapp.ui.home.HomeUiState
import com.example.galleryapp.ui.home.HomeViewModel
import com.example.galleryapp.ui.home.PhotoFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = HomeViewModel(application)
        val initial = viewModel.uiState.value
        assertTrue(initial is HomeUiState.Loading)
    }

    @Test
    fun `after init state transitions to Success`() = runTest {
        val viewModel = HomeViewModel(application)
        viewModel.uiState.test {
            val first = awaitItem()
            assertTrue(first is HomeUiState.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            val second = awaitItem()
            assertTrue(second is HomeUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectFilter updates selectedFilter in Success state`() = runTest {
        val viewModel = HomeViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(PhotoFilter.Videos)
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(PhotoFilter.Videos, (state as HomeUiState.Success).selectedFilter)
    }

    @Test
    fun `Videos filter only shows video mime types`() = runTest {
        val viewModel = HomeViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(PhotoFilter.Videos)
        val state = viewModel.uiState.value as? HomeUiState.Success ?: return@runTest
        state.sections.flatMap { it.photos }.forEach { photo ->
            assertTrue("Expected video mime type, got ${photo.mimeType}", photo.mimeType.startsWith("video/"))
        }
    }

    @Test
    fun `GIFs filter only shows gif mime types`() = runTest {
        val viewModel = HomeViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(PhotoFilter.GIFs)
        val state = viewModel.uiState.value as? HomeUiState.Success ?: return@runTest
        state.sections.flatMap { it.photos }.forEach { photo ->
            assertEquals("image/gif", photo.mimeType)
        }
    }

    @Test
    fun `All filter shows all photos including videos and gifs`() = runTest {
        val viewModel = HomeViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(PhotoFilter.Videos)
        val videosCount = (viewModel.uiState.value as? HomeUiState.Success)
            ?.sections?.sumOf { it.photos.size } ?: 0
        viewModel.selectFilter(PhotoFilter.All)
        val allCount = (viewModel.uiState.value as? HomeUiState.Success)
            ?.sections?.sumOf { it.photos.size } ?: 0
        assertTrue("All filter should show more or equal photos than Videos filter", allCount >= videosCount)
    }

    @Test
    fun `toggleSelectionMode enables and disables selection`() = runTest {
        val viewModel = HomeViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleSelectionMode()
        val afterToggle = viewModel.uiState.value as? HomeUiState.Success
        assertTrue(afterToggle?.selectionMode == true)
        viewModel.toggleSelectionMode()
        val afterToggleOff = viewModel.uiState.value as? HomeUiState.Success
        assertFalse(afterToggleOff?.selectionMode == true)
    }

    @Test
    fun `togglePhotoSelection adds and removes photoId from selectedIds`() = runTest {
        val viewModel = HomeViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleSelectionMode()
        viewModel.togglePhotoSelection(1L)
        val withSelection = viewModel.uiState.value as? HomeUiState.Success
        assertTrue(withSelection?.selectedIds?.contains(1L) == true)
        viewModel.togglePhotoSelection(1L)
        val withoutSelection = viewModel.uiState.value as? HomeUiState.Success
        assertFalse(withoutSelection?.selectedIds?.contains(1L) == true)
    }
}

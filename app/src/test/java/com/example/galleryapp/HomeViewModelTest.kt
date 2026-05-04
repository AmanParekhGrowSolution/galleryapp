package com.example.galleryapp

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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = HomeViewModel()
        val initial = viewModel.uiState.value
        assertTrue(initial is HomeUiState.Loading)
    }

    @Test
    fun `after init state transitions to Success`() = runTest {
        val viewModel = HomeViewModel()
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
        val viewModel = HomeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(PhotoFilter.Videos)
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(PhotoFilter.Videos, (state as HomeUiState.Success).selectedFilter)
    }

    @Test
    fun `toggleSelectionMode enables and disables selection`() = runTest {
        val viewModel = HomeViewModel()
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
        val viewModel = HomeViewModel()
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

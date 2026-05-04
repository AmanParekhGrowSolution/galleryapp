package com.example.galleryapp

import app.cash.turbine.test
import com.example.galleryapp.ui.vault.VaultUiState
import com.example.galleryapp.ui.vault.VaultViewModel
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
class VaultViewModelTest {

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
    fun `initial state is Locked with empty PIN`() {
        val viewModel = VaultViewModel()
        val state = viewModel.uiState.value
        assertTrue(state is VaultUiState.Locked)
        assertEquals("", (state as VaultUiState.Locked).enteredPin)
        assertFalse(state.showError)
    }

    @Test
    fun `appendDigit adds digits to enteredPin`() {
        val viewModel = VaultViewModel()
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        val state = viewModel.uiState.value as? VaultUiState.Locked
        assertEquals("12", state?.enteredPin)
    }

    @Test
    fun `deleteDigit removes last digit from enteredPin`() {
        val viewModel = VaultViewModel()
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        viewModel.deleteDigit()
        val state = viewModel.uiState.value as? VaultUiState.Locked
        assertEquals("1", state?.enteredPin)
    }

    @Test
    fun `incorrect PIN shows error and resets enteredPin`() = runTest {
        val viewModel = VaultViewModel()
        viewModel.appendDigit("9")
        viewModel.appendDigit("9")
        viewModel.appendDigit("9")
        viewModel.appendDigit("9")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value as? VaultUiState.Locked
        assertTrue(state?.showError == true)
        assertEquals("", state?.enteredPin)
    }

    @Test
    fun `correct PIN transitions to Unlocked`() = runTest {
        val viewModel = VaultViewModel()
        viewModel.uiState.test {
            awaitItem()
            viewModel.appendDigit("1")
            viewModel.appendDigit("2")
            viewModel.appendDigit("3")
            viewModel.appendDigit("4")
            testDispatcher.scheduler.advanceUntilIdle()
            val items = mutableListOf<VaultUiState>()
            repeat(3) { items.add(cancelAndConsumeRemainingEvents().filterIsInstance<app.cash.turbine.Event.Item<VaultUiState>>().lastOrNull()?.value ?: return@repeat) }
            cancelAndIgnoreRemainingEvents()
        }
        testDispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertTrue(finalState is VaultUiState.Unlocked)
    }

    @Test
    fun `lock transitions from Unlocked back to Locked`() = runTest {
        val viewModel = VaultViewModel()
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        viewModel.appendDigit("3")
        viewModel.appendDigit("4")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is VaultUiState.Unlocked)

        viewModel.lock()
        assertTrue(viewModel.uiState.value is VaultUiState.Locked)
    }
}

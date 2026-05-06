package com.example.galleryapp

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.galleryapp.ui.vault.VaultUiState
import com.example.galleryapp.ui.vault.VaultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class VaultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext()
        // Reset vault prefs so each test starts from a clean state
        application.getSharedPreferences("gallery_secure_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Locked with empty PIN`() {
        val viewModel = VaultViewModel(application)
        val state = viewModel.uiState.value
        assertTrue(state is VaultUiState.Locked)
        assertEquals("", (state as VaultUiState.Locked).enteredPin)
        assertFalse(state.showError)
    }

    @Test
    fun `appendDigit adds digits to enteredPin`() {
        val viewModel = VaultViewModel(application)
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        val state = viewModel.uiState.value as? VaultUiState.Locked
        assertEquals("12", state?.enteredPin)
    }

    @Test
    fun `deleteDigit removes last digit from enteredPin`() {
        val viewModel = VaultViewModel(application)
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        viewModel.deleteDigit()
        val state = viewModel.uiState.value as? VaultUiState.Locked
        assertEquals("1", state?.enteredPin)
    }

    @Test
    fun `incorrect PIN shows error and resets enteredPin`() = runTest {
        val viewModel = VaultViewModel(application)
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
    fun `correct default PIN transitions to Unlocked`() = runTest {
        val viewModel = VaultViewModel(application)
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        viewModel.appendDigit("3")
        viewModel.appendDigit("4")
        testDispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertTrue("Expected Unlocked but got $finalState", finalState is VaultUiState.Unlocked)
    }

    @Test
    fun `lock transitions from Unlocked back to Locked`() = runTest {
        val viewModel = VaultViewModel(application)
        viewModel.appendDigit("1")
        viewModel.appendDigit("2")
        viewModel.appendDigit("3")
        viewModel.appendDigit("4")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is VaultUiState.Unlocked)
        viewModel.lock()
        assertTrue(viewModel.uiState.value is VaultUiState.Locked)
    }

    @Test
    fun `five failed attempts trigger lockout`() = runTest {
        val viewModel = VaultViewModel(application)
        // First 4 failures — advance time to let each complete
        repeat(4) {
            viewModel.appendDigit("9"); viewModel.appendDigit("9")
            viewModel.appendDigit("9"); viewModel.appendDigit("9")
            testDispatcher.scheduler.advanceUntilIdle()
        }
        // 5th failure — check lockout BEFORE advancing time (countdown would drain it)
        viewModel.appendDigit("9"); viewModel.appendDigit("9")
        viewModel.appendDigit("9"); viewModel.appendDigit("9")
        val state = viewModel.uiState.value as? VaultUiState.Locked
        assertTrue(
            "Expected lockout after 5 fails, remaining=${state?.lockoutRemainingSeconds}",
            (state?.lockoutRemainingSeconds ?: 0) > 0
        )
    }

    @Test
    fun `lockout countdown decrements over time`() = runTest {
        val viewModel = VaultViewModel(application)
        // Trigger 5 failures
        repeat(4) {
            viewModel.appendDigit("9"); viewModel.appendDigit("9")
            viewModel.appendDigit("9"); viewModel.appendDigit("9")
            testDispatcher.scheduler.advanceUntilIdle()
        }
        viewModel.appendDigit("9"); viewModel.appendDigit("9")
        viewModel.appendDigit("9"); viewModel.appendDigit("9")

        val before = (viewModel.uiState.value as? VaultUiState.Locked)?.lockoutRemainingSeconds ?: 0
        assertTrue("Expected lockout to have started, remaining=$before", before > 0)

        // Advance by 2 seconds — countdown loop executes 2 iterations
        advanceTimeBy(2_000)
        val after = (viewModel.uiState.value as? VaultUiState.Locked)?.lockoutRemainingSeconds ?: 0
        assertTrue("Expected countdown to decrease, before=$before after=$after", after < before)
    }
}

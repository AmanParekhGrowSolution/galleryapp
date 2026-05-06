---
name: mobile-testing
description: Android MVVM testing patterns with JUnit4, hand-written fakes, Turbine, coroutines-test, and Compose UI testing. Includes MainDispatcherRule, StandardTestDispatcher, and fake repository patterns.
trigger:
  keywords: [test, JUnit, Turbine, "ViewModel test", "unit test", Espresso, ComposeRule, "coroutine test", runTest, TestDispatcher, "MainDispatcherRule", fake, fakeRepo]
  when: Any unit test, ViewModel test, repository test, or Compose UI test is being written or modified
---

# Mobile Testing Patterns (MVVM)

## Test Dependencies

```kotlin
// app/build.gradle.kts
dependencies {
    // Unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    // Compose UI tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

> **JUnit4, not JUnit5.** Android instrumentation tests require JUnit4. JUnit5 needs a third-party engine plugin (`android-junit5`) and does not work in instrumented tests without extra setup. Stick to JUnit4 (`@Test`, `@Before`, `@After`, `@get:Rule`) for all Android test modules.

---

## MainDispatcherRule — required for every ViewModel test

Copy this once into `test/util/MainDispatcherRule.kt` and reuse everywhere.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
```

**`StandardTestDispatcher` vs `UnconfinedTestDispatcher`**

| | `StandardTestDispatcher` | `UnconfinedTestDispatcher` |
|---|---|---|
| Coroutine start | Lazy — needs `advanceUntilIdle()` or `runCurrent()` | Eager — runs immediately on current thread |
| Default in `runTest {}` | Yes (since coroutines-test 1.7) | No |
| Use for | Step-by-step state verification with Turbine | Simple suspend-function tests with no state sequence |

Use `StandardTestDispatcher` (the default) for all ViewModel tests. `UnconfinedTestDispatcher` inside `runTest` with `setMain` causes coroutines to run outside the test scheduler — flaky tests.

---

## Hand-Written Fakes (preferred over Mockk for repositories)

Mockk stubs verify calls but don't enforce the real contract. A fake that implements the same interface catches mismatches mocks can't.

```kotlin
// test/fake/FakeItemRepository.kt
class FakeItemRepository : ItemRepository {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    private var shouldThrow = false

    fun emit(items: List<Item>) { _items.value = items }
    fun emitError(message: String = "Network error") { shouldThrow = true }

    override suspend fun getItems(): Result<List<Item>> {
        if (shouldThrow) return Result.failure(RuntimeException("Network error"))
        return Result.success(_items.value)
    }

    override fun observeItems(): Flow<List<Item>> = _items
}
```

Use **Mockk** for leaf dependencies that are hard to fake (system APIs, third-party SDKs). Use **fakes** for your own Repository interfaces.

---

## ViewModel Testing

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepo = FakeItemRepository()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(GetItemsUseCase(fakeRepo))
    }

    @Test
    fun `initial load — success updates items in state`() = runTest {
        val items = listOf(Item("1", "Test"))
        fakeRepo.emit(items)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.items).isEmpty()

            viewModel.onRefresh()

            assertThat(awaitItem().isRefreshing).isTrue()

            val success = awaitItem()
            assertThat(success.items).containsExactlyElementsIn(items)
            assertThat(success.isRefreshing).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load failure — errorMessage is set`() = runTest {
        fakeRepo.emitError()

        viewModel.uiState.test {
            skipItems(1)        // initial empty state
            viewModel.onRefresh()
            skipItems(1)        // isRefreshing = true

            val error = awaitItem()
            assertThat(error.errorMessage).isNotNull()
            assertThat(error.isRefreshing).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onItemClicked — NavigateToDetail event is emitted`() = runTest {
        viewModel.events.test {
            viewModel.onItemClicked("42")
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToDetail("42"))
        }
    }

    @Test
    fun `onDismissError — clears errorMessage`() = runTest {
        fakeRepo.emitError()
        viewModel.onRefresh()
        advanceUntilIdle()

        viewModel.uiState.test {
            val errorState = awaitItem()
            assertThat(errorState.errorMessage).isNotNull()

            viewModel.onDismissError()
            assertThat(awaitItem().errorMessage).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

### Turbine rules

- Always end with `cancelAndIgnoreRemainingEvents()` unless testing a terminal event — prevents flakiness from buffered unconsumed items.
- Use `skipItems(n)` to skip past known states you don't want to assert on.
- Use `awaitItem()` for StateFlow — do not use `.first()` (misses initial value handling in tests).
- `advanceUntilIdle()` drains all pending coroutines when using `StandardTestDispatcher`.

---

## Repository Testing

```kotlin
class ItemRepositoryImplTest {

    private val mockApi: ItemApi = mockk()
    private val mockDao: ItemDao = mockk()
    private lateinit var repository: ItemRepositoryImpl

    @Before
    fun setUp() {
        repository = ItemRepositoryImpl(mockApi, mockDao)
    }

    @Test
    fun `getItems — maps DTO to domain model`() = runTest {
        coEvery { mockApi.fetchItems() } returns listOf(ItemDto("1", "Test"))
        coEvery { mockDao.save(any()) } just Runs

        val result = repository.getItems()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.first()?.title).isEqualTo("Test")
    }

    @Test
    fun `getItems — network failure returns failure Result`() = runTest {
        coEvery { mockApi.fetchItems() } throws IOException("timeout")

        val result = repository.getItems()

        assertThat(result.isFailure).isTrue()
    }
}
```

---

## Compose UI Testing

Test the **Screen composable** directly — not the Route. Screen is stateless and takes plain parameters.

```kotlin
class HomeScreenTest {

    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `shows items when state is success`() {
        val items = persistentListOf(Item("1", "Test Item"))

        composeRule.setContent {
            HomeScreen(
                uiState           = HomeUiState(items = items),
                snackbarHostState = remember { SnackbarHostState() },
                onRefresh         = {},
                onSearchChanged   = {},
                onItemClick       = {},
                onDismissError    = {},
            )
        }

        composeRule.onNodeWithText("Test Item").assertIsDisplayed()
    }

    @Test
    fun `shows loading indicator when refreshing with empty list`() {
        composeRule.setContent {
            HomeScreen(
                uiState           = HomeUiState(isRefreshing = true, items = persistentListOf()),
                snackbarHostState = remember { SnackbarHostState() },
                onRefresh = {}, onSearchChanged = {}, onItemClick = {}, onDismissError = {},
            )
        }

        composeRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun `clicking item invokes onItemClick callback`() {
        var clickedId: String? = null
        val items = persistentListOf(Item("1", "Test Item"))

        composeRule.setContent {
            HomeScreen(
                uiState           = HomeUiState(items = items),
                snackbarHostState = remember { SnackbarHostState() },
                onItemClick       = { clickedId = it },
                onRefresh = {}, onSearchChanged = {}, onDismissError = {},
            )
        }

        composeRule.onNodeWithText("Test Item").performClick()
        assertThat(clickedId).isEqualTo("1")
    }
}
```

---

## What to Test — Decision Guide

| Layer | Write a test when… | Skip when… |
|---|---|---|
| ViewModel | Non-trivial state transitions, error flows, event emission, filter/sort logic | Single-field passthrough, no conditional logic |
| Repository | Caching, retry, DTO→domain mapping, error wrapping | 1:1 delegation to API with no transform |
| UseCase | Combines 2+ repos, has branching logic | Single repo call with no transform |
| Compose Screen | Critical user journeys (login, purchase, vault unlock) | Pure display with no interaction logic |

**Never test:** Composable layout aesthetics, hardcoded string values, simple `copy()` calls with no side effects.

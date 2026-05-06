---
name: mvvm-architecture
description: MVVM + UDF (Unidirectional Data Flow) architecture patterns for Android - ViewModel, StateFlow<UiState>, sealed UiState, stateless Composables, one-time events, stateIn, and layered UI → Domain → Data structure.
trigger:
  keywords: [UiState, StateFlow, ViewModel, "sealed interface", "state management", "unidirectional", "data class state", "collectAsStateWithLifecycle", MVVM, UDF, stateIn, UseCase, Repository]
  when: New UiState type is being defined, a ViewModel is being created or refactored, or the state/event/repository layer of a screen needs to be designed
---

# MVVM + UDF Architecture

Unidirectional data flow: UI dispatches events → ViewModel processes them → StateFlow updates → UI recomposes.

```
UI (stateless Composable)
  │  named callback functions
  ▼
ViewModel  ──StateFlow<UiState>──▶  UI
  │
  ▼
UseCase (optional — when logic is shared or complex)
  │
  ▼
Repository interface (domain/) → impl (data/)
  │
  ▼
Remote / Local data source
```

---

## 1. UiState Patterns

### Sealed interface — mutually exclusive states

Use when the entire screen swaps between fundamentally different layouts.

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val items: ImmutableList<Item>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```

### Data class — fixed layout, changing content

Use when the layout is always the same and only values change (most common).

```kotlin
@Immutable
data class HomeUiState(
    val items: ImmutableList<Item> = persistentListOf(),
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
)
```

### Hybrid — outer data class + inner sealed interface (Now In Android pattern)

Best when a screen has both a persistent chrome (toolbar, filters) and a content area that switches states.

```kotlin
data class ForYouUiState(
    val feedState: NewsFeedUiState = NewsFeedUiState.Loading,
    val isOffline: Boolean = false,
)

sealed interface NewsFeedUiState {
    data object Loading : NewsFeedUiState
    data class Success(val feed: ImmutableList<NewsItem>) : NewsFeedUiState
}
```

### Stable lists — use `ImmutableList` instead of `@Immutable` hacks

Compose infers `List<T>` as **unstable** — it recomposes even when nothing changed.
Fix: use `kotlinx.collections.immutable`. No annotation needed.

```kotlin
// build.gradle.kts
implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

// Usage
data class HomeUiState(
    val items: ImmutableList<Item> = persistentListOf(),  // ✅ Compose sees as stable
)

// In ViewModel — convert before emitting
_uiState.update { it.copy(items = newItems.toImmutableList()) }
```

Only use `@Immutable` / `@Stable` annotations after confirming recomposition issues in Layout Inspector — premature annotation causes silent missed-recomposition bugs.

---

## 2. ViewModel

Rules:
- Expose `StateFlow<UiState>` — never `MutableStateFlow`, `MutableState`, or `LiveData`
- Use direct named functions per action — **no `onIntent()` dispatcher**
- One-time events via `Channel.BUFFERED` exposed as `Flow`
- `viewModelScope` for all coroutines — never `GlobalScope`

```kotlin
class HomeViewModel(
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        loadItems()
    }

    fun onRefresh() = loadItems()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onItemClicked(itemId: String) {
        viewModelScope.launch { _events.send(HomeEvent.NavigateToDetail(itemId)) }
    }

    fun onDismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            getItemsUseCase()
                .onSuccess { items ->
                    _uiState.update { it.copy(items = items.toImmutableList(), isRefreshing = false, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isRefreshing = false, errorMessage = error.message) }
                }
        }
    }
}
```

### Derived state from repository Flow — use `stateIn`

When building UiState directly from a repository `Flow`, use `stateIn` — never collect and re-emit manually.

```kotlin
class NewsViewModel(private val repo: NewsRepository) : ViewModel() {

    val uiState: StateFlow<NewsUiState> = repo.getNews()
        .map { news -> NewsUiState.Success(news.toImmutableList()) }
        .catch { emit(NewsUiState.Error(it.message ?: "Unknown")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),  // ← canonical value
            initialValue = NewsUiState.Loading,
        )
}
```

`WhileSubscribed(5_000)`: upstream flow stops 5 s after the last collector disappears — survives rotation (config change < 5 s) but cancels if the app is truly backgrounded.

### AndroidViewModel — only when genuinely needed

Use only when the ViewModel needs `Application` context (e.g., `ContentResolver`, file paths, system services). Never for convenience.

```kotlin
class GalleryViewModel(
    application: Application,
    private val repository: GalleryRepository,
) : AndroidViewModel(application) {

    private val contentResolver = application.contentResolver
}
```

### Persisting state through process death

Use `SavedStateHandle.getStateFlow()` — zero-boilerplate state that survives process death AND configuration changes.

```kotlin
class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repo: SearchRepository,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow("query", "")

    fun onQueryChanged(query: String) {
        savedStateHandle["query"] = query  // auto-persisted
    }
}
```

---

## 3. One-Time Events

### Option A — State flag (Google's official recommendation)

Best for navigation triggers. No race conditions, no lost events on process death.

```kotlin
data class LoginUiState(
    val navigateToHome: Boolean = false,
    val isLoading: Boolean = false,
)

// ViewModel
fun onLoginSuccess() = _uiState.update { it.copy(navigateToHome = true) }
fun onNavigatedToHome() = _uiState.update { it.copy(navigateToHome = false) }

// UI — Route composable
LaunchedEffect(uiState.navigateToHome) {
    if (uiState.navigateToHome) {
        navController.navigate(Destination.Home)
        viewModel.onNavigatedToHome()
    }
}
```

### Option B — `Channel(BUFFERED)` (community standard for transient UI messages)

Best for toasts, snackbars, error popups — messages that don't affect nav state.

```kotlin
sealed interface HomeEvent {
    data class NavigateToDetail(val itemId: String) : HomeEvent
    data class ShowSnackbar(val message: String) : HomeEvent
}

// ViewModel
private val _events = Channel<HomeEvent>(Channel.BUFFERED)
val events: Flow<HomeEvent> = _events.receiveAsFlow()

// UI — Route composable only
LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
        when (event) {
            is HomeEvent.NavigateToDetail -> navController.navigate(...)
            is HomeEvent.ShowSnackbar    -> snackbarHostState.showSnackbar(event.message)
        }
    }
}
```

**Never use `SharedFlow(replay=1)` for events** — it re-delivers on resubscription, causing duplicate navigation/snackbars.

---

## 4. Stateless Composable Screen (Route / Screen Split)

```kotlin
// Route composable — owns ViewModel, handles events, NOT previewed
@Composable
fun HomeRoute(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToDetail -> onNavigateToDetail(event.itemId)
                is HomeEvent.ShowSnackbar    -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    HomeScreen(
        uiState           = uiState,
        snackbarHostState = snackbarHostState,
        onRefresh         = viewModel::onRefresh,
        onSearchChanged   = viewModel::onSearchQueryChanged,
        onItemClick       = viewModel::onItemClicked,
        onDismissError    = viewModel::onDismissError,
    )
}

// Screen composable — purely stateless, previewable, testable in isolation
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onDismissError: () -> Unit,
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        when {
            uiState.isRefreshing && uiState.items.isEmpty() -> LoadingIndicator()
            uiState.errorMessage != null -> ErrorContent(
                message   = uiState.errorMessage,
                onRetry   = onRefresh,
                onDismiss = onDismissError,
            )
            else -> ItemList(
                items        = uiState.items,
                isRefreshing = uiState.isRefreshing,
                onItemClick  = onItemClick,
                modifier     = Modifier.padding(padding),
            )
        }
    }
}
```

---

## 5. Domain Layer

```kotlin
// domain/model/Item.kt — pure Kotlin, zero Android imports
data class Item(val id: String, val title: String)

// domain/repository/ItemRepository.kt — interface only
interface ItemRepository {
    suspend fun getItems(): Result<List<Item>>
    fun observeItems(): Flow<List<Item>>
}

// domain/usecase/GetItemsUseCase.kt
// Add a UseCase when: (1) logic is shared across 2+ ViewModels,
// (2) it combines multiple repositories, or (3) ViewModel exceeds ~200 lines.
// Skip it for simple single-repository CRUD.
class GetUserNewsUseCase(
    private val newsRepo: NewsRepository,
    private val userDataRepo: UserDataRepository,
) {
    operator fun invoke(userId: String): Flow<List<UserNewsResource>> =
        newsRepo.getNewsResources()
            .combine(userDataRepo.getUserData(userId)) { news, user ->
                news.mapToUserNewsResources(user)
            }
}
```

UseCase naming: `Verb (present tense) + Noun + UseCase`
— `GetLatestNewsUseCase`, `LogOutUserUseCase`, `ObserveAlbumsUseCase`

---

## 6. Data Layer

```kotlin
class ItemRepositoryImpl(
    private val remoteSource: ItemRemoteDataSource,
    private val localSource: ItemLocalDataSource,
) : ItemRepository {

    override suspend fun getItems(): Result<List<Item>> = runCatching {
        val remote = remoteSource.fetchItems()
        localSource.save(remote)
        remote.map { it.toDomain() }
    }

    override fun observeItems(): Flow<List<Item>> =
        localSource.observeAll().map { it.map(ItemEntity::toDomain) }
}
```

---

## 7. Testing

**Use hand-written fakes, not Mockito/Mockk mocks for repositories** — fakes catch real contract violations that mocks miss.

```kotlin
// test/fake/FakeItemRepository.kt
class FakeItemRepository : ItemRepository {
    private val flow = MutableStateFlow<List<Item>>(emptyList())

    fun emit(items: List<Item>) { flow.value = items }
    fun emitError() { /* override to throw in getItems */ }

    override suspend fun getItems(): Result<List<Item>> =
        Result.success(flow.value)

    override fun observeItems(): Flow<List<Item>> = flow
}
```

```kotlin
class HomeViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepo = FakeItemRepository()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(GetItemsUseCase(fakeRepo))
    }

    @Test
    fun `initial load — success updates items`() = runTest {
        val items = listOf(Item("1", "Test"))
        fakeRepo.emit(items)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.items).isEmpty()

            viewModel.onRefresh()
            assertThat(awaitItem().isRefreshing).isTrue()
            assertThat(awaitItem().items).isEqualTo(items.toImmutableList())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onItemClicked — NavigateToDetail event emitted`() = runTest {
        viewModel.events.test {
            viewModel.onItemClicked("42")
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToDetail("42"))
        }
    }
}

// Reuse across all ViewModel tests
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
```

---

## Quick Reference

| Layer | Rule |
|---|---|
| UiState | Sealed interface for exclusive layouts; `@Immutable data class` + `ImmutableList` for combined state |
| ViewModel | `StateFlow<UiState>` + named functions; `Channel(BUFFERED)` for transient events; state flag for navigation |
| `stateIn` | Always `WhileSubscribed(5_000)` for screen-bound flows from repositories |
| Screen | Stateless; `collectAsStateWithLifecycle()`; events only in Route composable |
| UseCase | Add when shared across 2+ ViewModels or combining 2+ repositories; skip for simple CRUD |
| Repository | Interface in `domain/`; implementation in `data/`; only layer that touches network/DB |
| Testing | Hand-written fakes > Mockk mocks; `StandardTestDispatcher` + `MainDispatcherRule` + Turbine |
| Scope | `viewModelScope` always; never `GlobalScope` or `runBlocking` |
| Process death | `savedStateHandle.getStateFlow(key, default)` for auto-persisted state |

**Remember**: State flows down via `StateFlow`, events flow up via named callbacks, one-time messages via `Channel`.

---
name: coroutines-patterns
description: Kotlin Coroutines and Flow patterns for structured concurrency, error handling, and async operations.
trigger:
  keywords: [coroutine, Flow, suspend, launch, async, Channel, StateFlow, SharedFlow, timer, delay, withContext, callbackFlow, conflate]
  when: Any coroutine scope, Flow pipeline, countdown timer, async operation, or structured concurrency is being written
---

# Coroutines Patterns

Structured concurrency for Kotlin.

## Coroutine Scopes

```kotlin
// ✅ ViewModel scope (auto-cancelled)
class HomeViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // Cancelled when ViewModel cleared
        }
    }
}

// ✅ Lifecycle scope
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            // Cancelled when lifecycle destroyed
        }
    }
}

// ❌ AVOID: GlobalScope
GlobalScope.launch { }  // Never cancelled, memory leaks
```

## Dispatchers

```kotlin
// Main - UI operations
withContext(Dispatchers.Main) {
    textView.text = "Updated"
}

// IO - Network, disk
withContext(Dispatchers.IO) {
    api.fetchData()
    database.query()
}

// Default - CPU intensive
withContext(Dispatchers.Default) {
    list.sortedBy { it.score }
}
```

## Flow Patterns

```kotlin
// StateFlow for UI state — always use asStateFlow() to hide the mutable backing
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

// One-time events — use Channel(BUFFERED), NOT MutableSharedFlow
// SharedFlow(replay=1) re-delivers on resubscription → duplicate navigation/snackbars
private val _events = Channel<HomeEvent>(Channel.BUFFERED)
val events: Flow<HomeEvent> = _events.receiveAsFlow()

// Collect with lifecycle awareness — NEVER collectAsState()
@Composable
fun HomeRoute(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // collectAsStateWithLifecycle() stops collecting when UI is not STARTED — saves CPU/battery
}
```

## `stateIn` — Canonical Pattern for Repository Flows

When building state from a repository `Flow`, use `stateIn` instead of collecting and re-emitting manually.

```kotlin
// ✅ Correct — stateIn with WhileSubscribed(5_000)
val uiState: StateFlow<NewsUiState> = repo.getNews()
    .map { NewsUiState.Success(it.toImmutableList()) }
    .catch { emit(NewsUiState.Error(it.message ?: "Unknown")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),  // survives rotation, cancels on background
        initialValue = NewsUiState.Loading,
    )

// ❌ Wrong — manual collect + re-emit
init {
    viewModelScope.launch {
        repo.getNews().collect { _uiState.value = Success(it) }  // leaks scope, no upstream cancellation
    }
}
```

`WhileSubscribed(5_000)`: stops the upstream 5 s after the last collector disappears — survives config changes (rotation < 5 s) but cancels if the app is truly backgrounded.

## Combining Multiple Flows

```kotlin
// combine — emits when ANY upstream emits
val uiState: StateFlow<HomeUiState> = combine(
    newsRepo.getNews(),
    userDataRepo.getUserData(),
) { news, user ->
    HomeUiState.Success(news.mapToUserNews(user).toImmutableList())
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

// flatMapLatest — cancels previous inner flow when outer emits
val userPosts: Flow<List<Post>> = userIdFlow
    .flatMapLatest { userId -> postRepo.getPostsForUser(userId) }
```

## `callbackFlow` — Wrapping Callback APIs into Flow

```kotlin
fun LocationManager.locationUpdates(): Flow<Location> = callbackFlow {
    val callback = object : LocationListener {
        override fun onLocationChanged(loc: Location) { trySend(loc) }
    }
    requestLocationUpdates(GPS_PROVIDER, 1000L, 1f, callback)

    awaitClose { removeUpdates(callback) }  // clean up when Flow is cancelled
}

## Flow Operators

```kotlin
flow
    .filter { it.isActive }
    .map { transform(it) }
    .distinctUntilChanged()
    .debounce(300)
    .catch { emit(fallback) }
    .collect { process(it) }
```

## Error Handling

```kotlin
// Try-catch in coroutine
viewModelScope.launch {
    try {
        val result = repository.fetchData()
        _state.value = Success(result)
    } catch (e: Exception) {
        _state.value = Error(e.message)
    }
}

// supervisorScope - siblings don't cancel
supervisorScope {
    launch { task1() }  // Failure doesn't cancel task2
    launch { task2() }
}
```

## Cancellation

```kotlin
// Cooperative cancellation
suspend fun processItems(items: List<Item>) {
    items.forEach { item ->
        ensureActive()  // Check cancellation
        process(item)
    }
}

// CancellationException handling
try {
    coroutineWork()
} catch (e: CancellationException) {
    throw e  // Don't swallow!
} catch (e: Exception) {
    handleError(e)
}
```

## Anti-Patterns

| Anti-pattern | Fix |
|---|---|
| `GlobalScope.launch` | `viewModelScope.launch` or `lifecycleScope.launch` |
| `runBlocking` in production | `suspend` functions all the way up |
| `MutableSharedFlow<Event>()` for one-time events | `Channel<Event>(Channel.BUFFERED)` |
| `SharedFlow(replay=1)` for navigation | Re-delivers on resubscription → use state flag or `Channel` |
| `collectAsState()` in Compose | `collectAsStateWithLifecycle()` |
| Manual collect+re-emit from repo Flow | `stateIn(WhileSubscribed(5_000))` |
| Swallowing `CancellationException` | Always rethrow it |

---

**Remember**: Structured concurrency = lifecycle-bound, cancellable, debuggable.

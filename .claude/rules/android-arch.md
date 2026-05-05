---
description: UDF architecture rules — auto-loaded when editing ViewModel, domain, or data layer files
paths:
  - "app/src/main/java/**/*ViewModel.kt"
  - "app/src/main/java/**/domain/**/*.kt"
  - "app/src/main/java/**/data/**/*.kt"
  - "app/src/main/java/**/repository/**/*.kt"
  - "app/src/main/java/**/usecase/**/*.kt"
---

## Layered UDF Architecture (CLAUDE.md §3)

```
ui/<feature>/
  <Feature>Screen.kt       ← stateless composable, receives UiState + callbacks
  <Feature>ViewModel.kt    ← exposes StateFlow<UiState>, processes events
  <Feature>UiState.kt      ← sealed interface preferred

domain/model/              ← pure Kotlin, zero android.* imports
domain/repository/         ← interfaces ONLY
domain/usecase/            ← optional, single-responsibility

data/repository/           ← implementations
data/remote/               ← Retrofit/Ktor
data/local/                ← Room DAOs
```

### Hard rules
- ViewModels expose **`StateFlow<UiState>`** — never expose `MutableState` to the UI layer
- UI collects with **`collectAsStateWithLifecycle()`** — never `collectAsState()`
- ViewModels must NOT import `android.view.*` or hold Activity/Fragment `Context`
- Domain models: zero `android.*` imports
- Repositories are the only layer that touches network or database
- Use `viewModelScope` for ViewModel coroutines; `lifecycleScope` only in Activity/Fragment

### UiState pattern
```kotlin
sealed interface FeatureUiState {
    data object Loading : FeatureUiState
    data class Success(val items: List<Item>) : FeatureUiState
    data class Error(val message: String) : FeatureUiState
}
```

### Null safety
- Avoid `!!` — prefer `?.let {}`, `?: return`, `requireNotNull(x) { "reason" }`
- `!!` only acceptable when null is structurally impossible, with a comment explaining why

### Coroutine scope — advisory (LC1–LC3)
| Rule | Pattern to avoid | Use instead |
|------|-----------------|-------------|
| **LC1** | `GlobalScope.launch { }` | `viewModelScope.launch { }` in VM, `lifecycleScope.launch { }` in Activity/Fragment |
| **LC2** | `runBlocking { }` in production code | Suspend function or `launch { }` |
| **LC3** | `viewModelScope.launch { }` outside a `*ViewModel.kt` | Match scope to the component's lifecycle |

```kotlin
// LC1 violation
GlobalScope.launch { fetchData() }  // leaks — not tied to any lifecycle

// correct
viewModelScope.launch { fetchData() }  // cancelled when ViewModel cleared

// LC2 violation
fun loadSync() = runBlocking { repository.fetch() }  // blocks calling thread

// correct
suspend fun load() = repository.fetch()
```

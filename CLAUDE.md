# CLAUDE.md
> Automatically loaded by Claude Code as its system context.
> Architectural, security, and accessibility rules are enforced by the CI review workflow.

---

## 1. Project Overview

**Kotlin + Jetpack Compose** Android application.
- Min SDK: follows `app/build.gradle.kts`
- DI: plain constructor injection / manual DI today. Add Hilt only when a feature explicitly requires it.
- Architecture: UDF (Unidirectional Data Flow) layered architecture — UI → Domain → Data

---

## 2. Visual design

When the user provides a screenshot, HTML mockup, Figma export, or other design reference, treat that reference as the authoritative source for colours, typography, spacing, and layout. Extract values directly from the reference.

When no reference is provided, match the look established by sibling screens in `app/src/main/java/com/example/galleryapp/ui/` — read 1–2 nearby screens before composing a new one.

Code-level conventions (apply regardless of palette):
- Use `animateColorAsState` / `animateFloatAsState` for animated colour/alpha
- Loading/shimmer states → use a shimmer effect, not a solid placeholder

---

## 3. Architecture (Layered UDF)

```
ui/<feature>/
  <Feature>Screen.kt       ← stateless composable, receives UiState + event callbacks
  <Feature>ViewModel.kt    ← ViewModel, exposes StateFlow<UiState>, processes events
  <Feature>UiState.kt      ← sealed interface (preferred) or data class

domain/model/              ← pure Kotlin data models, no Android imports
domain/repository/         ← interfaces only
domain/usecase/            ← single-responsibility use cases (optional layer)

data/repository/           ← implementations, annotated @Singleton if using Hilt
data/remote/               ← Retrofit/Ktor interfaces
data/local/                ← Room DAOs
```

### Rules

- ViewModels expose **StateFlow<UiState>** — never expose MutableState to the UI layer
- Screens collect state with `collectAsStateWithLifecycle()` (never `collectAsState()`)
- ViewModels must NOT import `android.view.*` or hold Activity/Fragment `Context`
  - Application `Context` is acceptable when genuinely needed (e.g., file paths, resources)
- Repositories are the only layer that touches network or database
- Domain models must be pure Kotlin — no `android.*` imports
- Use `viewModelScope` for ViewModel-owned coroutines; use `lifecycleScope` only in Activity/Fragment

### UiState pattern

Prefer sealed interfaces with `data object` for parameterless states:

```kotlin
sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(val items: List<Item>) : NewsUiState
    data class Error(val message: String) : NewsUiState
}
```

---

## 4. Kotlin & Compose Coding Rules

### Null safety

- **Avoid `!!`** — prefer `?.let {}`, `?: return`, or `requireNotNull(x) { "reason" }`
- `!!` is acceptable only when the null is impossible by construction and a comment explains why
- Never use `!!` on values that come from external input, network, or user interaction

### Compose performance

- Wrap expensive calculations in `remember { }` or `remember(key) { }`
- Use `derivedStateOf { }` to prevent over-triggering recomposition from fast-changing state
- Annotate pure data holders passed into composables with `@Immutable` or `@Stable`
- Profile recompositions in Layout Inspector before optimising; don't annotate prematurely
- Defer state reads inside lambdas when possible: `Modifier.offset { IntOffset(x.value, 0) }`
- **[P1]** Always supply `key =` to `items()` in `LazyColumn`/`LazyRow` — prevents full re-render when list order changes
- **[P2]** Never sort/filter a collection inside `items(...)` — wrap with `remember(list) { list.sortedBy { ... } }` above the lazy layout
- **[P3]** Wrap scroll-position reads (e.g. `listState.firstVisibleItemIndex`) in `derivedStateOf { }` — avoids recomposition on every pixel scroll

### Coroutines & lifecycle

- `viewModelScope` — use for data loading, business logic, API calls (survives rotation)
- `lifecycleScope` — use only for UI-scoped work in Activity/Fragment
- Prefer `flow {}` + `collect` over callbacks; expose cold flows from repositories
- **[LC1]** Never use `GlobalScope.launch` / `GlobalScope.async` — it bypasses structured concurrency and leaks
- **[LC2]** Never use `runBlocking` in production code — it blocks the calling thread; use `suspend` functions or `launch`
- **[LC3]** Never call `viewModelScope.launch` outside a `ViewModel` class — use the scope that matches the lifecycle

---

## 5. String Resources

- **User-visible UI strings** (labels, buttons, error messages, titles) → `strings.xml`
- **Dynamic / computed strings** built in code (e.g. formatted dates, concatenated values) → inline in code is fine
- **Internal constants, log tags, URLs, keys** → hardcode in code, not strings.xml
- Use `%1$s` placeholders in strings.xml for parameterised user-visible text

---

## 6. Testing

Write tests for **critical business logic**. Not every PR requires tests — focus effort where bugs are costly.

| Layer      | Framework              | Write a test when…                                    |
|------------|------------------------|-------------------------------------------------------|
| ViewModel  | JUnit 4 + Turbine      | Non-trivial state transitions, error handling flows   |
| Repository | JUnit 4 + Mockito      | Caching logic, retry behaviour, data mapping          |
| UI         | Espresso / ComposeRule | Critical user journeys (login, checkout, etc.)        |

**Skipping tests is acceptable for:** simple CRUD ViewModels, UI-only composables, config changes, and one-liners.

---

## 7. Accessibility

- Every `Image` and `Icon` must have a non-empty `contentDescription` (or `contentDescription = null` with explicit `Role` if decorative)
- Minimum touch target size: 48×48 dp (use `Modifier.minimumInteractiveComponentSize()` from Material3)
- Use `Modifier.semantics {}` to add meaningful accessibility labels to custom interactive components

---

## 8. Dependency Injection

Currently: plain constructor injection.

If a feature grows to need DI:
- Add **Hilt** (Google's recommended solution for Android)
- Scope bindings: `@Singleton` for app-wide services, `@ViewModelScoped` for ViewModel-specific deps
- Do NOT use Koin in new code (Hilt is the project standard)

---

## 10. Security (OWASP MASVS)

Rules enforced by the linter as **BLOCKING**. Security violations must be fixed before any PR is opened.

### Network (MASVS-NETWORK)
- **[SEC1]** Never use `http://` URLs — HTTPS only for all network calls
- Implement `NetworkSecurityConfig` if you need to add certificate pinning

### Credentials & Cryptography (MASVS-CRYPTO)
- **[SEC2]** Never hardcode API keys, tokens, secrets, or passwords in source — use environment variables, `BuildConfig` fields injected at build time, or Android Keystore
- **[SEC5]** Never use `java.util.Random` for anything security-related — use `java.security.SecureRandom`
- **[SEC6]** Never use deprecated algorithms: `MD5`, `SHA-1`, `DES`, `ECB` cipher mode
  - ❌ `MessageDigest.getInstance("MD5")`
  - ✅ `MessageDigest.getInstance("SHA-256")`
  - ❌ `Cipher.getInstance("AES/ECB/PKCS5Padding")`
  - ✅ `Cipher.getInstance("AES/GCM/NoPadding")`

### TLS Validation (MASVS-NETWORK)
- **[SEC9]** Never use `ALLOW_ALL_HOSTNAME_VERIFIER` or override `checkServerTrusted` with a no-op body — always validate certificates properly

### Build configuration (MASVS-RESILIENCE)
- **[SEC10]** Never set `android:debuggable="true"` in `app/src/main/AndroidManifest.xml` — the debug build type sets this automatically; the main manifest must not override it

---

## 11. Privacy & Logging

Rules enforced by the linter as **ADVISORY** — surfaced as review comments but do not block merge.

- **[PRIV1]** Never log sensitive fields (`token`, `password`, `email`, `phone`, `userId`) with `Log.d/v/i/w/e` — guard with `if (BuildConfig.DEBUG)` and strip before release
- **[PRIV2]** Never leave `println()` in production source — replace with `Log.*` or remove entirely
- This app handles user photos — treat file paths, EXIF metadata, and gallery contents as PII: do not log or send them to analytics without explicit consent

---

## 9. Git & PR Conventions

- Branch: `claude/fix-issue-{N}-description` or `claude/feat-issue-{N}-description`
- Commit: `fix: description (#N)` or `feat: description (#N)`
- PR title must match commit format
- Keep PRs focused — one feature or fix per PR
- No commented-out code in merged PRs

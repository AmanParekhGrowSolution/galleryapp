# CLAUDE.md
> Automatically loaded by Claude Code as its system context.
> Rules below are the single source of truth for this repo — the CI review workflow enforces them.

---

## 1. Project Overview

**Kotlin + Jetpack Compose** Android application.
- Min SDK: follows `app/build.gradle.kts`
- DI: plain constructor injection / manual DI today. Add Hilt only when a feature explicitly requires it.
- Architecture: UDF (Unidirectional Data Flow) layered architecture — UI → Domain → Data

---

## 2. Gradient Design System ← MANDATORY

Every composable you create or modify MUST use `Brush` gradients for backgrounds and buttons.
Flat solid-colour backgrounds and `containerColor` overrides on Material3 components are **forbidden**.

### Colour Tokens

| Token      | Start                 | End                   |
|------------|-----------------------|-----------------------|
| Primary    | `Color(0xFF6366F1)`   | `Color(0xFF8B5CF6)`   |
| Accent     | `Color(0xFF06B6D4)`   | `Color(0xFF3B82F6)`   |
| Success    | `Color(0xFF10B981)`   | `Color(0xFF059669)`   |
| Warning    | `Color(0xFFF59E0B)`   | `Color(0xFFD97706)`   |
| Error      | `Color(0xFFEF4444)`   | `Color(0xFFDC2626)`   |
| Background | `Color(0xFF0F0C29)`   | `Color(0xFF24243E)`   |

### Code Patterns

```kotlin
// Screen background
Box(
    modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(
            listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
        )
    )
)

// Primary button — wrap in Box, never override Button's containerColor
Box(
    modifier = Modifier
        .background(
            Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))),
            RoundedCornerShape(12.dp)
        )
        .clickable(onClick = onClick)
        .padding(horizontal = 24.dp, vertical = 14.dp)
) {
    Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
}

// Card with subtle gradient tint
Card(
    modifier = Modifier
        .fillMaxWidth()
        .background(
            Brush.linearGradient(
                listOf(Color(0xFF6366F1).copy(alpha = 0.10f), Color(0xFF8B5CF6).copy(alpha = 0.10f))
            ),
            RoundedCornerShape(16.dp)
        )
)
```

**Rules:**
- Text on gradient background → `Color.White` or `Color.White.copy(alpha = 0.87f)`
- Icons on gradient → `tint = Color.White`
- Animated colour/alpha → use `animateColorAsState` / `animateFloatAsState`
- Loading/shimmer states → use gradient shimmer, not solid placeholders

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

### Coroutines

- `viewModelScope` — use for data loading, business logic, API calls (survives rotation)
- `lifecycleScope` — use only for UI-scoped work in Activity/Fragment
- Prefer `flow {}` + `collect` over callbacks; expose cold flows from repositories

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

## 9. Git & PR Conventions

- Branch: `claude/fix-issue-{N}-description` or `claude/feat-issue-{N}-description`
- Commit: `fix: description (#N)` or `feat: description (#N)`
- PR title must match commit format
- Keep PRs focused — one feature or fix per PR
- No commented-out code in merged PRs

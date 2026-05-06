---
name: jetpack-compose
description: Jetpack Compose patterns for declarative UI, state management, theming, animations, and performance optimization.
trigger:
  keywords: [Composable, Compose, UI, animation, theming, recomposition, LazyColumn, LazyRow, Modifier, remember, derivedStateOf, "@Stable", "@Immutable"]
  when: Any Composable screen, component, animation, theming change, or Compose performance optimization is being written
---

# Jetpack Compose Patterns

Modern declarative UI patterns for Android.

## State Management

### State Hoisting

```kotlin
// ✅ CORRECT: Stateless composable
@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text("Count: $count")
        Button(onClick = onIncrement) {
            Text("+")
        }
    }
}

// Parent owns state
@Composable
fun CounterScreen() {
    var count by rememberSaveable { mutableStateOf(0) }
    
    Counter(
        count = count,
        onIncrement = { count++ }
    )
}
```

### Remember Variants

```kotlin
// remember - Survives recomposition
val alpha by remember { mutableStateOf(1f) }

// rememberSaveable - Survives config change
var count by rememberSaveable { mutableStateOf(0) }

// remember with key - Resets on key change
val animation = remember(itemId) { Animatable(0f) }

// derivedStateOf - Computed, updates only when result changes
val isValid by remember {
    derivedStateOf { email.isNotBlank() && password.length >= 8 }
}
```

## Composition Patterns

### Slot API

```kotlin
@Composable
fun AppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { title() },
        navigationIcon = { navigationIcon() },
        actions = actions
    )
}

// Usage
AppBar(
    title = { Text("Home") },
    navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
    actions = {
        IconButton(onClick = {}) { Icon(Icons.Default.Search, null) }
    }
)
```

### Modifier Pattern

```kotlin
@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,  // First optional parameter
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,  // Apply modifier first
        enabled = enabled,
        content = content
    )
}
```

## Side Effects

### LaunchedEffect

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    // Runs once
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    // Runs when key changes
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }
}
```

### DisposableEffect

```kotlin
@Composable
fun LifecycleObserver(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
```

## Theming

### Material 3

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

// Usage
val backgroundColor = MaterialTheme.colorScheme.surface
val textStyle = MaterialTheme.typography.bodyLarge
```

## Lists

### LazyColumn

```kotlin
LazyColumn {
    items(
        items = users,
        key = { it.id }  // Critical for performance
    ) { user ->
        UserItem(user = user)
    }
}
```

## Animations

### Animate Values

```kotlin
val alpha by animateFloatAsState(
    targetValue = if (visible) 1f else 0f,
    animationSpec = tween(durationMillis = 300)
)

val size by animateDpAsState(
    targetValue = if (expanded) 200.dp else 100.dp
)
```

### AnimatedContent

```kotlin
AnimatedContent(
    targetState = state,
    transitionSpec = {
        fadeIn() togetherWith fadeOut()
    }
) { targetState ->
    when (targetState) {
        is Loading -> LoadingContent()
        is Success -> SuccessContent(targetState.data)
        is Error -> ErrorContent()
    }
}
```

## Collecting State — `collectAsStateWithLifecycle()` is mandatory

```kotlin
// ✅ Correct — stops collecting when UI is not in STARTED state (saves CPU/battery)
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ❌ Wrong — keeps collecting even when UI is in background
val uiState by viewModel.uiState.collectAsState()
```

Add the dependency if missing: `implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.x")`

## Stable Lists — Use `ImmutableList` instead of `@Immutable` hacks

`List<T>` is inferred as **unstable** by the Compose compiler — every parent recomposition forces a child recomposition even when content hasn't changed.

```kotlin
// build.gradle.kts
implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

// UiState
data class HomeUiState(
    val items: ImmutableList<Item> = persistentListOf(),  // ✅ Compose infers as stable
)

// ViewModel — convert before emitting
_uiState.update { it.copy(items = newItems.toImmutableList()) }
```

Only apply `@Immutable` / `@Stable` manually after confirming recomposition issues in Layout Inspector — premature annotation causes silent missed-recomposition bugs.

### Compose Compiler Stability Config (multi-module projects)

Classes from non-Compose modules (e.g., `:domain` or `:data`) are inferred as unstable even if all fields are `val`. Fix without adding annotations to domain models:

```
// compose_compiler_config.stability_config.conf  (create at project root)
com.example.domain.model.*
kotlinx.collections.immutable.ImmutableList
```

```kotlin
// app/build.gradle.kts
composeCompiler {
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.stability_config.conf")
}
```

## Previews

Always provide a preview for every Screen composable. Use a dedicated preview data builder.

```kotlin
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, name = "Light")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            uiState = HomeUiState(
                items = persistentListOf(Item("1", "Sample"), Item("2", "Another")),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {}, onSearchChanged = {}, onItemClick = {}, onDismissError = {},
        )
    }
}
```

## Anti-Patterns

| Anti-pattern | Fix |
|---|---|
| `collectAsState()` | `collectAsStateWithLifecycle()` |
| `List<T>` in UiState | `ImmutableList<T>` from kotlinx-collections-immutable |
| Sorting/filtering inside `items { }` | `remember(list) { list.sortedBy { ... } }` above LazyColumn |
| Missing `key =` on `items()` | Always supply `key = { it.id }` |
| Calling ViewModel in Screen composable | Only in Route composable; Screen is stateless |
| `@Immutable` without Layout Inspector check | Profile first; use compiler stability config instead |
| Business logic in composable | Logic goes in ViewModel; composable only reads state + calls callbacks |

---

**Remember**: Compose is declarative. Describe the UI, don't command it.

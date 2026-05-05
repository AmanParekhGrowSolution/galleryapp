---
description: UI checklist — auto-loaded when editing Android UI screen files
paths:
  - "app/src/main/java/**/*Screen.kt"
  - "app/src/main/java/**/ui/**/*.kt"
  - "app/src/main/res/**/*.xml"
---

## UI checklist — auto-loaded for Compose UI files

### Checklist before finishing any composable
- [ ] Every `Image`/`Icon` has a non-null `contentDescription` (or explicit decorative `Role`)
- [ ] Touch targets ≥ 48×48 dp — use `Modifier.minimumInteractiveComponentSize()`
- [ ] Single-char separators like `Text(":")` → `strings.xml`
- [ ] State collected with `collectAsStateWithLifecycle()`, never `collectAsState()`

### Performance — before finishing any LazyColumn / LazyRow
- [ ] **[P1]** Every `items(...)` call has a `key = { item -> item.id }` parameter
- [ ] **[P2]** No inline `.sortedBy`/`.filter`/`.groupBy` inside `items(...)` — wrap with `remember(list) { ... }` above the layout
- [ ] **[P3]** Any `listState.firstVisibleItemIndex` or `firstVisibleItemScrollOffset` read is inside `derivedStateOf { }`

```kotlin
// P1 + P2 correct pattern
val sorted = remember(photos) { photos.sortedByDescending { it.dateTaken } }
LazyColumn {
    items(sorted, key = { it.id }) { photo ->
        PhotoCard(photo)
    }
}

// P3 correct pattern
val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
```

### Window insets — required for every new screen

`MainActivity` calls `enableEdgeToEdge()`, so the app draws under the status bar and navigation bar. **Never use `Modifier.padding(top = N.dp)` as a substitute** — it breaks on notch/cutout devices. Use the rule below that matches where the screen lives:

**Inner-nav screen** (registered inside `MainScreen.kt`'s inner `NavHost` — the four bottom-tab destinations, or any new tab you add):
- Do **not** add `.statusBarsPadding()` — the parent `NavHost` modifier already applies it.
- Do **not** add bottom padding for the nav pill — the parent reserves it.
- Just write the screen content and let the parent handle all insets.

**Top-level screen** (registered directly in `AppNavigation.kt` — e.g. PhotoViewer, Vault, Settings, or any new full-screen route):
- The top bar / first content `LazyColumn` must apply `.statusBarsPadding()` on its `Modifier`.
- Any bottom-aligned bar, FAB, or sheet must apply `.navigationBarsPadding()`.
- Reference: `PhotoViewerScreen.kt:79-94` — floating top bar with `.statusBarsPadding()`, floating bottom bar with `.navigationBarsPadding()`.

**Anti-patterns:**
```kotlin
// ❌ double padding — inner-nav screen re-applies status bar inset
Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) { ... }

// ❌ fixed top padding — breaks on tall-cutout devices
Modifier.padding(top = 28.dp)

// ❌ Scaffold + manual statusBarsPadding — double padding
Scaffold(...) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding).statusBarsPadding()) { ... }
}
```

```kotlin
// ✅ top-level screen — floating bars pattern
Box(modifier = Modifier.fillMaxSize()) {
    FullScreenContent(modifier = Modifier.fillMaxSize())
    TopBar(modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding())
    BottomBar(modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding())
}

// ✅ top-level screen — list pattern
LazyColumn(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
    item { ScreenHeader() }
    items(list, key = { it.id }) { ScreenRow(it) }
    item { Spacer(Modifier.navigationBarsPadding()) }
}
```

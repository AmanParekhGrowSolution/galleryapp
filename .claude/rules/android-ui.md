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

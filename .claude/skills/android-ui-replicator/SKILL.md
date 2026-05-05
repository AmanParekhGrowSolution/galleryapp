---
name: android-ui-replicator
description: >
  Replicates Android UI screens from screenshots or HTML mockups into production-ready
  Kotlin code. Use this skill whenever a user uploads a screenshot, shares an HTML file,
  or provides a design reference and wants it converted to Android / Kotlin code.
  Triggers on: "replicate", "clone", "rebuild", "copy", "generate", "convert this design",
  "turn this into code", "make this Android layout", "make this Compose screen",
  "convert this HTML", "convert this mockup", "bundler HTML", "design HTML",
  "Figma export HTML", or any .html / .htm file path mentioned alongside Android / Kotlin /
  Compose context. Applies for single screens or multi-screen apps. Always use this skill
  when (a) a screenshot, image, or HTML/mockup file is present AND (b) Android / Kotlin /
  Compose is mentioned or implied.
---

# Android UI Replicator — Kotlin (Compose + XML)

Converts screenshots or HTML design mockups into accurate, production-ready Android Kotlin
code — Jetpack Compose by default, or XML when the project uses View-based layouts —
then performs a self-review pass to catch and fix mismatches before delivering the final output.

---

## Workflow Overview

```
0. DETECT INPUT   → Identify input type (image vs HTML) and route to the right branch
1. DETECT OUTPUT  → Determine target format: Compose or XML
2. DECODE         → [HTML only] Unpack bundler HTML to plain files
3. ANALYZE        → Study the design in depth and document all tokens + components
4. GENERATE       → Write code that replicates the UI
5. REVIEW         → Compare generated code against the design source
6. FIX            → Correct every identified mismatch
7. DELIVER        → Present clean final output
```

Always follow all steps in order. Never skip REVIEW and FIX.

---

## Step 0 — DETECT INPUT

Inspect what the user has provided:

| Input | Detected type | Next action |
|---|---|---|
| `.png` / `.jpg` / `.webp` / `.heic` / image attached | Screenshot | Skip to Step 1 — DETECT OUTPUT |
| `.html` / `.htm` path or file contents | HTML mockup | Check for bundler format (below), then Step 1 |
| No clear design provided | Ask the user | Request a screenshot or HTML file before continuing |

### Bundler HTML detection

If the input is HTML, grep the file for:
```
<script type="__bundler/manifest">
```
- **Found** → bundler HTML. Run Step 2 — DECODE before analyzing.
- **Not found** → plain HTML. Read the file directly with the Read tool; extract CSS tokens and HTML structure. Skip Step 2.

---

## Step 1 — DETECT OUTPUT MODE

Determine whether to generate Compose or XML. Check in order:

1. **Explicit user instruction** — if the user says "use Compose" or "use XML", that wins.
2. **Grep `app/build.gradle*` and `*/build.gradle*`** for `androidx.compose` → **Compose mode**.
3. **Check for `app/src/main/res/layout/`** — if XML layouts exist → **XML mode**.
4. **Default to Compose** and note the assumption.

Report the detected mode at the top of the ANALYZE output:
```
Output mode: Compose   (detected androidx.compose in app/build.gradle.kts)
```

---

## Step 2 — DECODE (Bundler HTML only)

Run the bundler decoder:

```
node "D:\galleryapp\.claude\skills\android-ui-replicator\scripts\decode-bundler.mjs" "<input.html>" "<output-dir>"
```

Replace `<input.html>` with the user's file path and `<output-dir>` with a temp directory (e.g. `C:\temp\ui-decoded`).

The decoder writes:
- `template.html` — the design document with plaintext CSS variables (exact design tokens)
- `assets/<uuid>.jsx` — JSX source files (MIME `text/jsx`)
- `assets/<uuid>.js` — JS files (two MIME types — see below)
- `assets/<uuid>.woff2` — fonts (note family/weight only)
- `manifest.json` — pruned asset index (uuid → `{ filename, mime }`)

**After decoding, the design is in plain text.** Proceed to ANALYZE using:
- `template.html` `<style>` block → colors, typography, spacing, radii
- `assets/*.jsx` AND `assets/*.js` with `"mime": "application/javascript"` in `manifest.json` → screen component source (readable JSX stored under a different MIME by the bundler)
- Skip `assets/*.js` with `"mime": "text/javascript"` — those are vendor bundles (React, Babel runtime), not design-relevant
- Font names from `@font-face` rules → typography family

See `references/bundler-format.md` for the full format specification.

---

## Step 3 — ANALYZE (Deep Design Inspection)

### Source of truth by input type

| Input type | Colors / tokens | Layout / components |
|---|---|---|
| Screenshot | Estimate from pixels — note any uncertainty | Infer from visual inspection |
| Bundler HTML | **Exact** CSS variables from `template.html` | JSX source in `assets/*.jsx` |
| Plain HTML | CSS properties / class values | HTML element hierarchy |

Before writing a single line of code, document:

### Layout Structure
- Root layout / scroll container type
- Nesting hierarchy — identify parent/child containers
- Scroll behavior — `LazyColumn`, `ScrollView`, `RecyclerView`, pager, etc.

### Components Inventory
List every visible UI element:
- Buttons (text, icon, FAB, outlined, filled, text-only)
- Text elements (heading, body, caption, label) — note semantic level
- Input fields
- Images / icons (describe shape, color, position)
- Top bar / bottom bar / navigation rail / drawer
- Chips, Cards, Dialogs, Bottom sheets, Snack bars
- List / grid items — describe the item layout
- Progress bars, switches, checkboxes, radio buttons

### Visual Properties
- **Colors** — hex codes (e.g. `#0066FF`) or CSS var names (e.g. `--brand-blue`)
  - *Screenshot:* estimate from pixels, flag uncertain values with `// TODO: confirm color`
  - *HTML:* cite exact CSS variable values — no estimation needed
- **Typography** — font family, size (sp), weight, letter spacing
- **Spacing** — margins and padding (dp); for HTML, cite `--space-*` var values
- **Elevation / Shadows** — note which cards/bars have shadows
- **Corner radius** — rounded cards, buttons, fields (dp or `--radius-*`)
- **Icons** — describe clearly so the correct Material icon name can be used
- **Images** — placeholder, aspect ratio, `contentScale`

### Theme / Design System
- Material 3 (default) vs Material 2?
- Light or dark theme?
- Custom font family (cite from `@font-face` or design)?

---

## Step 4 — GENERATE

### Compose mode — file structure

```
ui/<feature>/
├── <Feature>Screen.kt       ← stateless composable, receives UiState + event callbacks
├── <Feature>ViewModel.kt    ← ViewModel, exposes StateFlow<UiState>
└── <Feature>UiState.kt      ← sealed interface
ui/theme/
├── Color.kt                 ← ALL extracted color tokens go here
├── Type.kt                  ← typography scale
└── Theme.kt                 ← MaterialTheme wrapper
```

### Compose — code rules (aligned with CLAUDE.md §3, §4, §7)

```kotlin
// UiState — sealed interface with data object for parameterless states
sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(val items: List<Photo>) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

// ViewModel — exposes StateFlow, never MutableState
class GalleryViewModel(...) : ViewModel() {
    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()
    // use viewModelScope for all coroutines
}

// Screen — stateless composable
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle() // never collectAsState()
    GalleryContent(uiState = uiState)
}
```

**Mandatory Compose rules:**
- `collectAsStateWithLifecycle()` — never `collectAsState()`
- `LazyColumn` / `LazyRow`: always supply `key =` to `items()` (CLAUDE.md P1)
- Never sort/filter inside `items(...)` — use `remember(list) { list.sortedBy {...} }` above
- Expensive calculations → `remember { }` or `remember(key) { }`
- `contentDescription` on **every** `Image` and `Icon` — or `null` + explicit `Role` if decorative (CLAUDE.md §7)
- Use `MaterialTheme.colorScheme.*` or theme color vals — never hardcode hex inline
- User-visible strings → `strings.xml` (CLAUDE.md §5)
- No `!!` on external/network/user values; no `GlobalScope`; no `runBlocking` (CLAUDE.md §4)

**Color tokens:**
```kotlin
// ui/theme/Color.kt — all extracted colors declared here
val BrandBlue = Color(0xFF0066FF)
// etc. — use CSS var values verbatim if source was HTML
```

See `references/compose-patterns.md` for complete Login, Dashboard, List, Profile, and Settings starters.

---

### XML mode — file structure

For each screen, generate:
```
📁 Screen Name/
├── activity_<name>.xml       (or fragment_<name>.xml)
├── <Name>Activity.kt         (or <Name>Fragment.kt)
├── item_<name>.xml           (if RecyclerView present)
└── <Name>Adapter.kt          (if RecyclerView present)
```

### XML — layout rules

```xml
<!-- Always include in root layout -->
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"

<!-- Use ConstraintLayout as root unless the design clearly uses another -->
<!-- Always use Material components, not plain widgets -->
<!-- com.google.android.material.button.MaterialButton -->
<!-- com.google.android.material.textfield.TextInputLayout -->
<!-- com.google.android.material.card.MaterialCardView -->
```

**Mandatory XML attributes:**
- `android:id` on every interactive/referenced view
- `android:layout_width` and `android:layout_height`
- `android:text` via `@string/...` resource
- `android:contentDescription` on all ImageViews
- `android:inputType` on all EditTexts

### XML — Kotlin Activity/Fragment rules

```kotlin
class NameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupListeners()
    }
}
```

- ViewBinding always (never `findViewById`)
- Material components from `com.google.android.material.*`
- Data classes for list models; `ListAdapter` with `DiffUtil.ItemCallback` for RecyclerView

**Color constants:**
```xml
<!-- res/values/colors.xml — all extracted colors declared here -->
<color name="brand_blue">#0066FF</color>
```

Declare all extracted colors in `colors.xml`. Never hardcode hex inline in layouts.

See `references/common-patterns.md` for XML Login, Dashboard, List, Profile, Settings starters.

---

## Step 5 — REVIEW (Self-QA Pass)

### Visual Accuracy Checklist

```
[ ] Layout hierarchy matches the design structure
[ ] All UI components from the design are present in the code
[ ] No extra components added that are not in the design
[ ] Colors match — for HTML: cite exact CSS var value; for screenshot: best-match hex
[ ] Typography — font family, sizes, and weights are correct
[ ] Spacing — margins and padding match (for HTML: cite --space-* values)
[ ] Corner radius on cards/buttons matches (for HTML: cite --radius-* values)
[ ] Icon names resolve to visually correct Material icons
[ ] Image placeholders have correct aspect ratio and contentScale
[ ] Top bar / bottom bar titles, nav icons, action icons match
[ ] Navigation tab count, labels, and icons match
[ ] List/grid item layout matches the card/row design
```

### Compose Quality Checklist

```
[ ] key = is provided to every items() in LazyColumn/LazyRow
[ ] collectAsStateWithLifecycle() used (not collectAsState())
[ ] UiState is a sealed interface (not a flat data class)
[ ] No MutableState exposed past the ViewModel boundary
[ ] contentDescription set on every Image and Icon
[ ] All colors come from theme / Color.kt (no hardcoded hex inline)
[ ] All user-visible strings are in strings.xml
[ ] No !! on external/network/user values
[ ] No GlobalScope or runBlocking
```

### XML Quality Checklist

```
[ ] All IDs are unique and descriptive
[ ] ViewBinding used everywhere (no findViewById)
[ ] All strings are in strings.xml (no hardcoded text)
[ ] All colors are in colors.xml (no hardcoded hex inline)
[ ] All dimensions use sp (text) or dp (layout)
[ ] Material components used (not plain Android widgets)
[ ] Kotlin code compiles (no syntax errors, correct imports)
[ ] Adapter has DiffUtil (if RecyclerView used)
[ ] ContentDescription set on all ImageViews
```

### Report Format

```
## Review Report

### Correct
- [list what matches well]

### Mismatches Found
1. [Component] — [what's wrong] → Fix: [what to change]

### Fixes Required: [N]
```

---

## Step 6 — FIX (Apply All Corrections)

- Fix **every** mismatch identified in the review report
- Show only the changed sections with a clear comment: `// FIXED: reason`
- If no mismatches found: "No fixes needed — all elements match the design."

---

## Step 7 — DELIVER (Final Output)

### Compose mode — delivery order

1. `<Feature>UiState.kt`
2. `<Feature>ViewModel.kt`
3. `<Feature>Screen.kt`
4. `ui/theme/Color.kt` additions
5. `ui/theme/Type.kt` additions (if typography tokens changed)
6. `strings.xml` additions
7. Supporting composables (item cards, shared components)

End with a Compose Setup Checklist:
```
## Setup Checklist
- [ ] Add to app/build.gradle.kts: implementation("androidx.lifecycle:lifecycle-viewmodel-compose:<version>")
- [ ] Add to app/build.gradle.kts: implementation("androidx.lifecycle:lifecycle-runtime-compose:<version>")
- [ ] Add color vals to ui/theme/Color.kt
- [ ] Add strings to res/values/strings.xml
- [ ] Wire ViewModel in DI or pass as viewModel() parameter
```

### XML mode — delivery order

1. `activity_name.xml` (or `fragment_name.xml`)
2. `NameActivity.kt` (or `NameFragment.kt`)
3. `colors.xml` additions
4. `strings.xml` additions
5. `themes.xml` additions (if needed)
6. Adapter files (if RecyclerView)
7. Item layout XMLs (if RecyclerView)

End with an XML Setup Checklist:
```
## Setup Checklist
- [ ] Add dependency: implementation("com.google.android.material:material:1.12.0")
- [ ] Enable ViewBinding in build.gradle: buildFeatures { viewBinding = true }
- [ ] Add strings from strings.xml additions
- [ ] Add colors from colors.xml additions
- [ ] Register Activity in AndroidManifest.xml
```

---

## Multi-Screen Projects

If the user provides multiple screens (screenshots or HTML with multiple JSX files):
1. Analyze **all** screens before generating any code
2. Identify shared tokens / components (common toolbar, nav, colors) → extract to theme / shared files
3. Generate screens in the order provided
4. In Review, also check **cross-screen consistency** (same color values, same spacing scale)

---

## Handling Ambiguity

| Situation | Action |
|---|---|
| Icon not clearly visible | Use closest Material icon, note it: `// TODO: verify icon` |
| Exact color unclear (screenshot) | Best-guess hex, add: `// TODO: confirm color` |
| Text content not readable | Use placeholder: `@string/placeholder_title` |
| Custom font detected (HTML) | Use exact `font-family` name from `@font-face`; add `// TODO: add font file to assets` |
| Custom font suspected (screenshot) | Use default Roboto, note: `// TODO: add custom font if needed` |
| Animation suspected | Implement static version, note: `// TODO: add transition animation` |
| Bundler decode fails | Report the error, ask user to open HTML in browser and share a screenshot instead |

---

## Reference Files

- `references/material-icons.md` — Common Material icon names mapped to visual descriptions
- `references/common-patterns.md` — XML code snippets for Login, Dashboard, List, Profile, Settings screens
- `references/compose-patterns.md` — Compose code snippets for Login, Dashboard, List, Profile, Settings screens
- `references/bundler-format.md` — Bundler HTML format specification (manifest/template structure, MIME types)
- `scripts/decode-bundler.mjs` — Node script that decodes bundler HTML to plain files; run before analyzing

Read reference files only when you need them — don't load all of them upfront.

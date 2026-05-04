---
name: android-ui-replicator
description: >
  Replicates Android UI screens from screenshots into production-ready Kotlin + XML code.
  Use this skill whenever a user uploads a screenshot of an Android screen and wants it
  converted to Kotlin code, or asks to "replicate", "clone", "rebuild", "copy", or
  "generate" an Android UI from an image. Also triggers when the user says things like
  "turn this design into code", "convert this screen to Kotlin", "make this Android layout",
  or "write the XML for this UI". Applies for single screens or multi-screen apps. Always
  use this skill when both (a) a screenshot or design image is present AND (b) Android /
  Kotlin / XML is mentioned or implied.
---

# Android UI Replicator — Kotlin + XML

Converts screenshot(s) of Android UIs into accurate, production-ready Kotlin + XML code,
then performs a self-review pass to catch and fix any mismatches before delivering the final output.

---

## Workflow Overview

```
1. ANALYZE   → Study the screenshot in depth
2. GENERATE  → Write Kotlin + XML that replicates the UI
3. REVIEW    → Compare generated code against the screenshot
4. FIX       → Correct every identified mismatch
5. DELIVER   → Present clean final output
```

Always follow all five steps in order. Never skip the REVIEW and FIX phases.

---

## Step 1 — ANALYZE (Deep Screenshot Inspection)

Before writing a single line of code, study the screenshot carefully. Document:

### Layout Structure
- Root layout type: `ConstraintLayout`, `LinearLayout`, `RelativeLayout`, `CoordinatorLayout`, etc.
- Nesting hierarchy — identify parent/child containers
- Scroll behavior — is there a `ScrollView`, `RecyclerView`, `NestedScrollView`?

### Components Inventory
List every visible UI element:
- Buttons (text, icon, FAB, outlined, filled, text-only)
- TextViews (heading, body, caption, label)
- EditTexts / TextInputLayouts
- ImageViews / icons (describe shape, color, position)
- Toolbars / AppBars / BottomNavigationView / NavigationDrawer
- Chips, Cards, Dialogs, BottomSheets, Snackbars
- RecyclerView items — describe the item layout
- Progress bars, switches, checkboxes, radio buttons

### Visual Properties (extract as precisely as possible)
- **Colors**: Background color, text color, icon tint, button fill, divider color
  → Express as hex codes (e.g. `#1A73E8`) or Material color tokens
- **Typography**: Font size (sp), font weight (bold/medium/regular), letter spacing
- **Spacing**: Margins and padding (dp) — estimate from proportions if not exact
- **Elevation / Shadows**: Cards, FABs, AppBars
- **Corner radius**: Rounded cards, buttons, input fields (dp)
- **Icons**: Describe the icon clearly so the correct Material icon name can be used
- **Images**: Placeholder, aspect ratio, scaleType

### Theme / Design System
- Material Design 2 vs Material Design 3 (Material You)?
- Light or dark theme?
- Any custom brand colors?

---

## Step 2 — GENERATE (Write Kotlin + XML Code)

### File Structure to Produce

For each screen, generate:

```
📁 Screen Name/
├── activity_<name>.xml       (or fragment_<name>.xml)
├── <Name>Activity.kt         (or <Name>Fragment.kt)
├── item_<name>.xml           (if RecyclerView is present)
└── <Name>Adapter.kt          (if RecyclerView is present)
```

### XML Layout Rules

```xml
<!-- Always include these in root layout -->
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"

<!-- Use ConstraintLayout as root unless the screenshot clearly uses another -->
<!-- Use Material components — NOT plain Android widgets -->
<!-- com.google.android.material.button.MaterialButton, NOT android.widget.Button -->
<!-- com.google.android.material.textfield.TextInputLayout -->
<!-- com.google.android.material.card.MaterialCardView -->
```

### Mandatory XML Attributes to Always Set
- `android:id` on every interactive or referenced view (use descriptive names)
- `android:layout_width` and `android:layout_height`
- `android:text` with string resource reference: `@string/hint_email`
- `android:contentDescription` on all ImageViews
- `android:inputType` on all EditTexts

### Kotlin Activity/Fragment Rules

```kotlin
// Minimum structure for Activity
class NameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupListeners()
    }

    private fun setupUI() { /* populate views, set initial state */ }
    private fun setupListeners() { /* click listeners, input watchers */ }
}
```

- Use **ViewBinding** always (not `findViewById`)
- Use **Material components** imports from `com.google.android.material.*`
- Kotlin **data classes** for any list/RecyclerView models
- RecyclerView: write `ListAdapter` with `DiffUtil.ItemCallback`

### Color & Style Constants

```xml
<!-- res/values/colors.xml -->
<color name="primary">#1A73E8</color>

<!-- res/values/themes.xml -->
<!-- Base theme: Theme.Material3.Light or Theme.MaterialComponents.Light.NoActionBar -->
```

Declare all extracted colors in `colors.xml`. Do NOT hardcode colors inline in layouts.

---

## Step 3 — REVIEW (Self-QA Pass)

After generating all code, perform a mandatory review. Go through this checklist:

### Visual Accuracy Checklist
```
[ ] Layout hierarchy matches the screenshot structure
[ ] All UI components from the screenshot are present in the code
[ ] No extra components added that are not in the screenshot
[ ] Colors match (background, text, buttons, icons, dividers)
[ ] Typography — font sizes and weights are correct
[ ] Spacing — margins and padding are proportionally correct
[ ] Corner radius on cards/buttons matches
[ ] Icon names resolve to visually correct Material icons
[ ] Image placeholders have correct aspect ratio and scaleType
[ ] AppBar/Toolbar title, subtitle, nav icon, action icons match
[ ] BottomNav tabs — correct count, labels, icons
[ ] RecyclerView item layout matches the card/row design
```

### Code Quality Checklist
```
[ ] All IDs are unique and descriptive
[ ] ViewBinding is used everywhere (no findViewById)
[ ] All strings are in strings.xml (no hardcoded text)
[ ] All colors are in colors.xml (no hardcoded hex inline)
[ ] All dimensions use sp (text) or dp (layout)
[ ] Material components used (not plain Android widgets)
[ ] Kotlin code compiles (no syntax errors, correct imports)
[ ] Adapter has DiffUtil (if RecyclerView used)
[ ] ContentDescription set on all ImageViews
```

### Report Format

Output the review as:

```
## 🔍 Review Report

### ✅ Correct
- [list what matches well]

### ⚠️ Mismatches Found
1. [Component] — [what's wrong] → Fix: [what to change]
2. ...

### 🔧 Fixes Required: [N]
```

---

## Step 4 — FIX (Apply All Corrections)

- Fix **every** mismatch identified in the review report
- Show only the changed sections with a clear comment: `// FIXED: reason`
- If no mismatches were found, state: "✅ No fixes needed — all elements match the screenshot."

---

## Step 5 — DELIVER (Final Output)

Present the final output in this order:
1. `activity_name.xml` (or `fragment_name.xml`)
2. `NameActivity.kt` (or `NameFragment.kt`)
3. `colors.xml` additions
4. `strings.xml` additions
5. `themes.xml` additions (if needed)
6. Adapter files (if RecyclerView)
7. Item layout XMLs (if RecyclerView)

End with a **Setup Checklist** for the user:
```
## 📦 Setup Checklist
- [ ] Add dependency: implementation 'com.google.android.material:material:1.12.0'
- [ ] Enable ViewBinding in build.gradle: buildFeatures { viewBinding true }
- [ ] Add strings from strings.xml additions
- [ ] Add colors from colors.xml additions
- [ ] Register Activity in AndroidManifest.xml
```

---

## Multi-Screen Projects

If the user provides multiple screenshots:
1. Analyze ALL screens before generating any code
2. Identify shared components (common toolbar, nav, colors) → extract to base/shared files
3. Generate screens in the order the user provided them
4. In the Review phase, also check cross-screen consistency (same colors, same spacing system)

---

## Handling Ambiguity

| Situation | Action |
|-----------|--------|
| Icon not clearly visible | Use closest Material icon, note it: `// TODO: verify icon` |
| Exact color unclear | Best-guess hex, add comment: `// TODO: confirm color` |
| Text content not readable | Use placeholder: `@string/placeholder_title` |
| Custom font suspected | Use default Roboto, note: `// TODO: add custom font if needed` |
| Animation suspected | Implement static version, note: `// TODO: add transition animation` |

---

## Reference Files

- `references/material-icons.md` — Common Material icon names mapped to visual descriptions
- `references/common-patterns.md` — Code snippets for Login, Dashboard, List, Profile, Settings screens

Read these only if you need help identifying icon names or want a head start on a common pattern.

# claude-design-to-android

A skill that converts Claude Code Design handoff bundles (zip files of JSX/HTML/CSS prototypes) into Android Kotlin + Jetpack Compose code.

## What's in this skill

```
claude-design-to-android/
├── SKILL.md                            ← Main instructions (Phase 0 → Phase 6 workflow)
├── README.md                           ← This file
├── references/
│   ├── design-token-mapping.md         ← Color/typography/shadow/gradient conversion tables
│   │                                     + dark mode color scheme patterns
│   ├── component-mapping.md            ← Universal Compose patterns + illustrative examples
│   └── icon-mapping.md                 ← Lucide → Material Icons mapping (with AutoMirrored)
├── scripts/
│   ├── convert_colors.py               ← Auto-extract colors from a bundle → draft Color.kt
│   └── extract_assets.py               ← Find referenced assets, copy to res/drawable*/, generate manifest
└── evals/
    ├── evals.json                      ← Test cases + assertions
    └── sample_bundle.zip               ← Tiny 3-screen synthetic design for testing
```

## Quick start

When a user uploads a design bundle:

1. The skill discovers the zip, extracts it, classifies every file, and reads tokens before screens.
2. It runs `scripts/convert_colors.py` on the bundle to produce a draft `Color.kt`.
3. **It runs `scripts/extract_assets.py` on the bundle to copy referenced assets into `res/drawable*/` with Android-safe names, while skipping state-screenshot folders.**
4. It generates Composables for every screen + reusable components for shared UI.
5. It wires navigation and produces a complete project tree under `/home/claude/android-output/`.
6. It verifies its own output (color count, screen count, asset coverage, no deprecated APIs) before presenting.

## What about Claude Design assets?

Claude Design's handoff bundle ships *referenced assets* — logos, photos, illustrations, custom icons — alongside the JSX/CSS. These need to be copied into the Android project's `res/drawable*/` folders with Android-legal names (lowercase, no hyphens, can't start with a digit).

The skill handles this in **Phase 1.5**, using `scripts/extract_assets.py`:

- It scans every JSX/HTML/CSS file for image references via grep
- It compares the references against the actual files in the bundle
- Files in `screenshots/`, `previews/`, `states/` folders are skipped (those are preview captures, not real assets)
- Files referenced from JSX are copied to `res/drawable-nodpi/` (bitmaps) or `res/drawable/` (SVGs that need Vector Asset conversion)
- A `assets-manifest.md` is generated showing the origin → destination mapping, plus the exact Compose snippet to wire each one up via `painterResource(R.drawable.xxx)`

SVGs are flagged for manual Vector Asset conversion in Android Studio — the script doesn't attempt automated SVG → vector drawable XML conversion because gradients, masks, and filters often don't survive the trip cleanly.

## Testing the skill

The `evals/` folder has three test cases. To run them manually in Claude.ai:

1. Upload `evals/sample_bundle.zip` to a fresh chat.
2. Try each prompt from `evals.json`.
3. Compare against the expected output and assertions.

To run the color script standalone against the sample:

```bash
cd /path/to/skill-folder
unzip evals/sample_bundle.zip -d /tmp/sample
python3 scripts/convert_colors.py /tmp/sample --output /tmp/Color.kt
```

You should see 8 colors extracted (7 design tokens + 1 box-shadow color).

## Key design choices

- **Universal first, illustrative second.** Universal patterns (Frame, StatusBar, Buttons, Bottom Sheets) apply to every bundle; the calendar/task examples are clearly labeled as one-bundle-specific.
- **Reasoning over rules.** "Don't substitute Material defaults" is paired with "designers chose those values deliberately" — explaining the why so the model can generalize.
- **Honest scope.** The skill produces faithful translations, not pixel-perfect parity, because web→Android has genuine rendering differences (fonts, shadows, system insets).
- **Bundled deterministic helper.** Color conversion is mechanical and was being redone every invocation — the script handles it once, correctly, in milliseconds.
- **Verification built in.** Phase 6 separates automated checks (color count, deprecated-API grep) from user-review checks (shadow weight, gradient feel).

## Versioning notes

- Compose APIs as of Compose Material 1.6 / Activity 1.8+.
- Uses `Icons.AutoMirrored.Filled.*` for directional icons (RTL-safe).
- Uses `enableEdgeToEdge` instead of deprecated Accompanist `rememberSystemUiController`.
- Uses Jetpack Navigation Compose (not the old Fragment-based nav).

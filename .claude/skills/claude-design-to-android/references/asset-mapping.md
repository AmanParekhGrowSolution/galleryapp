# Asset Mapping Reference

How Claude Design assets become Android resources. The skill's Phase 1.5 references this file for the detailed rules.

---

## Table of Contents

1. [Referenced assets vs. state screenshots](#referenced-assets-vs-state-screenshots)
2. [Android resource naming rules](#android-resource-naming-rules)
3. [Choosing the right `res/` folder](#choosing-the-right-res-folder)
4. [SVG → vector drawable conversion](#svg--vector-drawable-conversion)
5. [Generating the asset manifest](#generating-the-asset-manifest)
6. [Referencing assets from Composables](#referencing-assets-from-composables)
7. [Placeholder images and remote content](#placeholder-images-and-remote-content)
8. [App icons and launcher assets](#app-icons-and-launcher-assets)

---

## Referenced assets vs. state screenshots

Claude Design bundles often contain two kinds of image files that look superficially similar:

- **Referenced assets** — logos, photos, illustrations, and custom icons that the design *displays*. These are referenced from JSX (`<img src="…"/>`), CSS (`background-image: url(…)`), or imports (`import logo from './logo.png'`). They need to ship in the Android app.

- **State screenshots** — PNG/JPG captures of each screen in different states, used by Claude Design itself for previews. These usually live in folders named `screenshots/`, `previews/`, or `states/`. They're not referenced from any JSX file. **Don't copy them to Android.**

The reliable signal is whether the JSX references the file. If yes, ship it. If no, skip it. Folders named `screenshots/`, `previews/`, `states/`, `captures/` are presumed to be in the second category even if a basename happens to match.

The bundled `extract_assets.py` script automates this: it walks the bundle, greps every `.jsx`/`.tsx`/`.html`/`.css` file for image references, cross-references against the file list, and only copies images that pass the test.

```bash
python3 scripts/extract_assets.py /home/claude/design-bundle \
    --output-dir /home/claude/android-output/res
```

---

## Android resource naming rules

Android enforces strict naming rules on files in `res/`. Violations cause the build to fail with cryptic errors. The rules:

- Lowercase letters, digits, and underscores only — `[a-z0-9_]`
- Must start with a letter — can't begin with a digit
- No hyphens, no spaces, no periods (other than the extension), no `@` symbols, no camelCase

Examples of the rename:

| Source filename | Android resource name | Why |
|---|---|---|
| `Logo.png` | `logo.png` | Lowercased |
| `hero-illustration.jpg` | `hero_illustration.jpg` | Hyphen → underscore |
| `01-splash.png` | `img_01_splash.png` | Digit prefix not allowed → prepend `img_` |
| `user@2x.png` | `user.png` | `@2x` density marker dropped (Android uses `drawable-xhdpi/` for that) |
| `user@3x.png` | `user.png` | Same as above (you'd put this in `drawable-xxhdpi/` instead) |
| `IMG_4815.JPG` | `img_4815.jpg` | Lowercased, extension lowercased |
| `icon.svg` | `icon.xml` | After Vector Asset conversion |
| `hero bg.png` | `hero_bg.png` | Space → underscore |
| `card.v2.png` | `card_v2.png` | Period in middle → underscore |

The `extract_assets.py` script applies all of these transformations automatically.

If two source files would map to the same resource name (e.g. `user@2x.png` and `user@3x.png` both → `user.png`), the script disambiguates by appending `_2`, `_3`, etc. — but this usually means you should be using density folders instead, so it's worth surfacing.

---

## Choosing the right `res/` folder

Different image types go in different `res/` subfolders:

| Asset type | Folder | Reason |
|---|---|---|
| Vector drawable (SVG → XML) | `drawable/` | Vectors scale freely, no density variants needed |
| Single-resolution PNG/JPG/WebP | `drawable-nodpi/` | Don't let Android scale it — keep original pixels |
| Density-specific PNG (rare in design bundles) | `drawable-mdpi/`, `drawable-hdpi/`, `drawable-xhdpi/`, `drawable-xxhdpi/`, `drawable-xxxhdpi/` | If the bundle ships `@2x`, `@3x`, etc. variants |
| App icon (launcher) | `mipmap-mdpi/` … `mipmap-xxxhdpi/` | Mipmaps survive density-stripping; standard launcher pattern |
| Localized images (rare) | `drawable-en/`, `drawable-ar/` | Only if the design has language-specific images |

**Default rule of thumb the script applies:**
- `.svg` → `drawable/` (after conversion to vector drawable XML)
- everything else (`.png`, `.jpg`, `.webp`, `.gif`) → `drawable-nodpi/`

`drawable-nodpi/` is the right choice when the bundle has no density variants. It tells Android *"this is the actual size — don't rescale it based on screen density"*. If a design provides density variants like `@2x` and `@3x`, override to put the @1x in `drawable-mdpi/`, the @2x in `drawable-xhdpi/`, the @3x in `drawable-xxhdpi/`.

---

## SVG → vector drawable conversion

Android doesn't render SVG natively at the resource level. SVGs need to be converted to vector drawable XML.

### Conversion options, in order of robustness

1. **Android Studio's Vector Asset tool** (most reliable): *File → New → Vector Asset → Local file*. Handles complex SVGs, gradients, and groups well. The downside is that it's manual.

2. **Programmatic conversion via `vd-tool`** (Android SDK utility): runs the same conversion logic in CLI form. Found at `<sdk>/build-tools/<version>/lib/vd-tool.jar`. Useful if many SVGs need converting.

3. **Manual conversion for simple SVGs**: a single-path SVG with basic colors translates almost mechanically:

```xml
<!-- SVG source -->
<svg viewBox="0 0 24 24"><path d="M12 2L2 22h20L12 2z" fill="#2564CF"/></svg>

<!-- Vector drawable -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#2564CF"
        android:pathData="M12 2L2 22h20L12 2z"/>
</vector>
```

For strokes:
```xml
<path
    android:pathData="…"
    android:strokeColor="#000000"
    android:strokeWidth="2"
    android:strokeLineCap="round"
    android:strokeLineJoin="round"/>
```

### What doesn't survive the conversion

- CSS animations and `<animate>` elements — drop them or use `AnimatedVectorDrawable` (separate file format)
- Filters (`<filter>`, blur, drop-shadow) — fake with Compose `Modifier.shadow` instead
- `<mask>` and `<clipPath>` with complex shapes — sometimes work, sometimes don't; test
- Embedded raster images (`<image href="…"/>`) — extract those as separate PNGs

When in doubt, the script copies the SVG as-is and flags it for manual conversion. Better than silent corruption.

---

## Generating the asset manifest

The script emits `assets-manifest.md` showing every imported asset's origin, destination, and Compose usage:

```markdown
# Asset Manifest

Generated by `extract_assets.py` from `/home/claude/design-bundle`.

- Files copied: **6**
- Skipped (in screenshots/previews folders): 3
- Skipped (no JSX reference): 1

## Imported assets

| Original | Android resource | Compose usage |
|---|---|---|
| `assets/Logo.png` | `res/drawable-nodpi/logo.png` | `Image(painterResource(R.drawable.logo), contentDescription = null, contentScale = ContentScale.Crop)` |
| `assets/check.svg` | `res/drawable/check.xml` ⚠️ **convert to vector drawable** | `Icon(painterResource(R.drawable.check), contentDescription = null)` |
| `public/img/hero-bg.jpg` | `res/drawable-nodpi/hero_bg.jpg` | `Image(painterResource(R.drawable.hero_bg), contentDescription = null, contentScale = ContentScale.Crop)` |
```

This makes the asset import auditable in one place. The user can scan the manifest, verify nothing was dropped, and copy the Compose snippets directly into screen files.

---

## Referencing assets from Composables

All drawables are accessed via `painterResource(R.drawable.<name>)`.

### Bitmap as decorative content

```kotlin
Image(
    painter = painterResource(R.drawable.hero_bg),
    contentDescription = null,         // null = decorative; describe it for non-decorative
    contentScale = ContentScale.Crop,  // Crop, Fit, FillBounds, FillHeight, FillWidth, Inside, None
    modifier = Modifier.fillMaxWidth().height(240.dp)
)
```

### Vector as a tinted icon

```kotlin
Icon(
    painter = painterResource(R.drawable.ic_logo),
    contentDescription = "App logo",
    tint = Primary,                    // omit or pass Color.Unspecified to keep original colors
    modifier = Modifier.size(32.dp)
)
```

### As a background

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .paint(painterResource(R.drawable.splash_bg), contentScale = ContentScale.Crop)
)
```

### Loading a drawable that might not exist (defensive)

```kotlin
val painter = runCatching { painterResource(R.drawable.maybe_missing) }
    .getOrElse { painterResource(R.drawable.placeholder) }
```

---

## Placeholder images and remote content

Sometimes the design uses Lorem-Picsum-style placeholders (`https://picsum.photos/...`), gradient fills standing in for "user photo here", or named placeholders that aren't shipped as real assets. These represent content that will come from a backend, not assets to import.

**Don't** copy them into `res/drawable/`. **Do** generate a Compose stub:

```kotlin
// Photo placeholder — replace with AsyncImage from Coil when wiring real data
Box(
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .background(Hair, RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
) {
    Icon(Icons.Default.Image, null, tint = Muted2, modifier = Modifier.size(32.dp))
}
```

In the README, tell the user how to wire real images:

```kotlin
// build.gradle.kts:
// implementation("io.coil-kt:coil-compose:2.5.0")

AsyncImage(
    model = imageUrl,
    contentDescription = null,
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.placeholder_error),
    contentScale = ContentScale.Crop,
    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
)
```

---

## App icons and launcher assets

If the bundle includes a designated app icon (named `app-icon.png`, `launcher-icon.png`, or in a folder called `app-icons/`), don't put it in `drawable/`. App icons need:

1. **Adaptive icon XML** in `res/mipmap-anydpi-v26/ic_launcher.xml`:
   ```xml
   <adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
       <background android:drawable="@color/ic_launcher_background"/>
       <foreground android:drawable="@drawable/ic_launcher_foreground"/>
   </adaptive-icon>
   ```

2. **Foreground vector** in `res/drawable/ic_launcher_foreground.xml` (must have padding around the visual).

3. **Background color** in `res/values/ic_launcher_background.xml`.

4. **Density-specific PNGs** in `res/mipmap-mdpi/ic_launcher.png` … `res/mipmap-xxxhdpi/ic_launcher.png` for older Android versions.

The cleanest path is to point the user at Android Studio's *File → New → Image Asset → Launcher Icons*, which generates all of the above from a single source image. Mention this in the README rather than trying to scaffold it from a generic asset.

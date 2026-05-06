# Design Token Mapping Reference

Universal conversion tables for ALL color, typography, shadow, border, spacing, and gradient formats found in Claude Design bundles.

---

## Table of Contents
1. [Color Conversion (all formats)](#color-conversion)
2. [Typography Conversion](#typography-conversion)
3. [Shadow Conversion](#shadow-conversion)
4. [Border & Shape Conversion](#border--shape-conversion)
5. [Spacing Patterns](#spacing-patterns)
6. [Gradient Patterns](#gradient-patterns)
7. [Dark Mode Color Schemes](#dark-mode-color-schemes)

---

## Color Conversion

### Every format → Compose Color

| Source Format | Example | Kotlin Output |
|---|---|---|
| `rgb(R, G, B)` | `rgb(37,100,207)` | `Color(0xFF2564CF)` |
| `rgba(R, G, B, A)` | `rgba(15,15,26,0.06)` | `Color(0xFF0F0F1A).copy(alpha = 0.06f)` |
| `#RRGGBB` | `#0066FF` | `Color(0xFF0066FF)` |
| `#RGB` (shorthand) | `#fff` | `Color(0xFFFFFFFF)` |
| CSS `rgba()` | `rgba(255,255,255,0.10)` | `Color.White.copy(alpha = 0.10f)` |
| Named CSS | `transparent` | `Color.Transparent` |
| Named CSS | `'#fff'` or `'white'` | `Color.White` |

### Hex conversion mental model
```
rgb(37, 100, 207):
  37  → 0x25
  100 → 0x64
  207 → 0xCF
  Result: Color(0xFF2564CF)

#0066FF → Color(0xFF0066FF)   (just prefix 0xFF)
```

### Token source patterns
Tokens can appear as:

**JS object:**
```jsx
const T = { primary: 'rgb(37,100,207)', ink: 'rgb(26,26,46)' }
```

**CSS custom properties:**
```css
:root { --brand-blue: #0066FF; --fg-1: #000000; }
```

**Inline in components:**
```jsx
const fg = dark ? "#fff" : "#0F1115";
```

**Mixed in screen functions:**
```jsx
background: 'linear-gradient(180deg, rgb(232,240,250), #fff)',
```

Convert ALL of them. Every unique color value → one Kotlin constant.

### Naming conventions
- JS object keys: camelCase the key → `T.primaryBg` → `val PrimaryBg`
- CSS variables: camelCase the variable name → `--brand-blue` → `val BrandBlue`
- Inline colors without names: name by usage → `val ScrimBackground`, `val DividerColor`

---

## Typography Conversion

### Font Weight
| CSS/JS Value | Compose |
|---|---|
| `400` / `normal` | `FontWeight.Normal` |
| `500` / `medium` | `FontWeight.Medium` |
| `600` / `semibold` | `FontWeight.SemiBold` |
| `700` / `bold` | `FontWeight.Bold` |
| `800` / `extrabold` | `FontWeight.ExtraBold` |

### Font Size → always `sp`
```
fontSize: 13  → fontSize = 13.sp
fontSize: 28  → fontSize = 28.sp
--fs-14: 14px → fontSize = 14.sp
```

### Letter Spacing
```
letterSpacing: -0.5  → letterSpacing = (-0.5).sp
letterSpacing: 0.3   → letterSpacing = 0.3.sp
letter-spacing: -0.28px → letterSpacing = (-0.28).sp
```

### Line Height
```
lineHeight: '20px' → lineHeight = 20.sp
line-height: 1.4   → lineHeight = (fontSize * 1.4).sp
```

### Text Overflow
```
overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap'
→ maxLines = 1, overflow = TextOverflow.Ellipsis

WebkitLineClamp: 3, WebkitBoxOrient: 'vertical'
→ maxLines = 3, overflow = TextOverflow.Ellipsis
```

### Text Decoration
```
textDecoration: 'line-through' → textDecoration = TextDecoration.LineThrough
textDecoration: 'underline'    → textDecoration = TextDecoration.Underline
```

### Text Transform
```
textTransform: 'uppercase' → text.uppercase()
textTransform: 'capitalize' → text.replaceFirstChar { it.uppercase() }
label.toUpperCase()         → label.uppercase()
```

### Named Typography (from CSS classes or JS objects)
If the design defines named styles (e.g. `.gx-h1`, `AppTypography.heading1`), create matching Kotlin objects:
```kotlin
object AppTypography {
    val h1 = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 27.5.sp)
    val h2 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 23.sp)
    val body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 19.6.sp)
    val caption = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
}
```

---

## Shadow Conversion

| Shadow Style | Compose Approach |
|---|---|
| Very subtle (`opacity < 0.08`) | `elevation = 1-2.dp` |
| Light (`opacity ~ 0.08-0.12`) | `elevation = 4.dp` |
| Medium (`opacity ~ 0.12-0.18`) | `elevation = 8.dp` |
| Strong (`opacity > 0.18`) | `elevation = 12-16.dp` |
| Colored shadow (brand color) | `Modifier.shadow(elevation, shape, ambientColor, spotColor)` |
| Upward shadow (sheets) | `ModalBottomSheet` handles internally |
| `box-shadow: none` | `elevation = 0.dp` |

### CSS variable shadows
```css
--shadow-1: 0 1px 2px rgba(0,0,0,0.08);
→ elevation = 2.dp

--shadow-overlay: 0 0 0 1px rgba(0,0,0,0.05), 0 8px 32px rgba(0,0,0,0.25);
→ elevation = 16.dp
```

---

## Border & Shape Conversion

### Border
```
border: '1px solid color'     → Modifier.border(1.dp, color, shape)
border: '1.5px solid color'   → Modifier.border(1.5.dp, color, shape)
border: 'none'                → (no border modifier)
borderBottom: '0.5px solid c' → HorizontalDivider(thickness = 0.5.dp, color = c)
borderLeft: '4px solid c'     → Modifier.drawBehind { drawRect(c, size = Size(4.dp, size.height)) }
```

### Border Radius → Shape
```
borderRadius: 14           → RoundedCornerShape(14.dp)
borderRadius: '50%'        → CircleShape
borderRadius: 999 / 999px  → RoundedCornerShape(50) (pill shape)
borderRadius: '24px 24px 0 0' → RoundedCornerShape(topStart=24.dp, topEnd=24.dp)
borderRadius: '18px 18px 0 0' → RoundedCornerShape(topStart=18.dp, topEnd=18.dp)
--radius-pill: 999px       → CircleShape or RoundedCornerShape(50)
```

---

## Spacing Patterns

### Padding
```
padding: 16                 → Modifier.padding(16.dp)
padding: '0 16px'           → Modifier.padding(horizontal = 16.dp)
padding: '16px 20px'        → Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
padding: '32px 24px 24px'   → Modifier.padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
padding: '10px 12px 8px'    → Modifier.padding(top = 10.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
paddingTop: 32              → Modifier.padding(top = 32.dp)
```

### Gap → Arrangement / Spacer
```
gap: 12 (column) → Column(verticalArrangement = Arrangement.spacedBy(12.dp))
gap: 8 (row)     → Row(horizontalArrangement = Arrangement.spacedBy(8.dp))
```

### CSS spacing scale
```css
--space-1: 2px; --space-2: 4px; --space-3: 8px;
```
→
```kotlin
object Spacing {
    val xs = 2.dp; val sm = 4.dp; val md = 8.dp
    // ... map all values
}
```

### Negative margins
Compose doesn't have margins. Translate:
```
margin: '20px -24px' → Use Modifier.fillMaxWidth() on the child, or offset
marginLeft: -8       → Modifier.offset(x = (-8).dp)
```

---

## Gradient Patterns

### Conversion rules
```kotlin
// linear-gradient(180deg, A, B)  →  vertical (top to bottom)
Brush.verticalGradient(listOf(ColorA, ColorB))

// linear-gradient(0deg, A, B)  →  bottom to top
Brush.verticalGradient(listOf(ColorA, ColorB), startY = Float.POSITIVE_INFINITY, endY = 0f)

// linear-gradient(90deg, A, B)  →  horizontal (left to right)
Brush.horizontalGradient(listOf(ColorA, ColorB))

// linear-gradient(135deg, A, B)  →  diagonal
Brush.linearGradient(
    colors = listOf(ColorA, ColorB),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

// radial-gradient(circle, A 0%, B 100%)
Brush.radialGradient(listOf(ColorA, ColorB))

// Gradient with stops
// linear-gradient(180deg, #FF9700 0%, #FFDC00 100%)
Brush.verticalGradient(
    0f to Color(0xFFFF9700),
    1f to Color(0xFFFFDC00)
)
```

### Complex gradients (stacked)
```css
background:
  linear-gradient(rgba(255,255,255,0.42), rgba(255,255,255,0.42)),
  linear-gradient(180deg, rgba(255,2,2,0.20) 0%, rgba(255,255,255,0.20) 46%),
  radial-gradient(circle, rgba(255,255,255,0.48) 0%, rgba(255,255,255,0) 100%),
  linear-gradient(180deg, #C78E00 0%, #E4A324 92%);
```
→ Approximate with the dominant gradient. Stack with `drawBehind` if needed.

### Usage
```kotlin
// Background
Modifier.background(brush = GradPremium)

// Shape + gradient
Modifier.background(brush = GradPremium, shape = RoundedCornerShape(15.dp))

// Text with gradient (rare)
Text(style = TextStyle(brush = gradientBrush))
```

---

## Dark Mode Color Schemes

If the bundle has a `dark` prop on screens, a `darkMode` toggle, or inline conditionals like `dark ? "#fff" : "#000"`, the Android side needs **two parallel color sets** managed through `MaterialTheme`. Don't scatter ternaries through Composables — lift the dark/light decision into the theme.

### Pattern 1: Two color schemes + AppTheme wrapper

```kotlin
// theme/Color.kt — define raw colors used by both schemes
val Primary = Color(0xFF2564CF)
val Ink = Color(0xFF131111)
val White = Color(0xFFFFFFFF)
val DarkBg = Color(0xFF0F0F1A)
// ... rest of design tokens

// theme/Theme.kt
private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    background = White,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    // ... map every semantic role
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    onPrimary = White,
    background = DarkBg,
    onBackground = White,
    surface = Color(0xFF1A1A2E),
    onSurface = White,
    // ...
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
```

### Pattern 2: Inside Composables, read from the theme

Instead of:
```kotlin
// ❌ Don't do this — ternaries scatter the dark logic everywhere
val bg = if (dark) DarkBg else White
val text = if (dark) White else Ink
```

Do this:
```kotlin
// ✅ Read the resolved color from the active scheme
val bg = MaterialTheme.colorScheme.background
val text = MaterialTheme.colorScheme.onBackground
```

For brand colors that aren't part of `colorScheme` (e.g. a specific accent that doesn't fit `primary`/`secondary`/`tertiary`), define a custom `CompositionLocal`:

```kotlin
data class AppColors(
    val brandAccent: Color,
    val ghostBorder: Color,
    val scrim: Color,
)

val LocalAppColors = staticCompositionLocalOf {
    error("AppColors not provided")
}

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors) {
            content()
        }
    }
}

// Usage in Composables:
val accent = LocalAppColors.current.brandAccent
```

### When NOT to bother with dark mode

If the bundle has no `dark` prop, no `darkMode` state, and no dark-themed screens, skip dark mode entirely. Forcing it on a single-mode design produces worse results than not having it.

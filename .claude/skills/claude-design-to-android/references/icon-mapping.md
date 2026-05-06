# Icon Mapping Reference

Maps inline SVG icons from Claude Design (lucide-style) to Android/Compose equivalents.

---

## Strategy

1. **Prefer Material Icons** — if a standard Material icon matches, use it
2. **Custom ImageVector** — for icons without Material equivalents, convert SVG paths
3. **Compose Painter** — as a last resort, use `painterResource` with vector drawables

---

## Lucide → Material Icons Mapping

> **Use `Icons.AutoMirrored.*` for directional icons.** Icons that imply a direction (back, forward, send, list bullets) need to flip in right-to-left locales like Arabic and Hebrew. The `Icons.Default.ArrowBack` family is deprecated for this reason — use `Icons.AutoMirrored.Filled.ArrowBack` and friends. Non-directional icons (Menu, Search, Add, Star, etc.) stay on `Icons.Default.*`.

| Design Icon Name | Lucide Reference | Material Icon (Compose) |
|---|---|---|
| `menu` | 3 horizontal lines | `Icons.Default.Menu` |
| `search` | Circle + diagonal line | `Icons.Default.Search` |
| `plus` | + cross | `Icons.Default.Add` |
| `x` (close) | X cross | `Icons.Default.Close` |
| `left` (chevron) | < chevron | `Icons.Default.ChevronLeft` |
| `right` (chevron) | > chevron | `Icons.Default.ChevronRight` |
| `down` (chevron) | v chevron | `Icons.Default.KeyboardArrowDown` |
| `arrowLeft` | ← arrow | `Icons.AutoMirrored.Filled.ArrowBack` (mirrors in RTL) |
| `arrowRight` | → arrow | `Icons.AutoMirrored.Filled.ArrowForward` |
| `send` | paper plane | `Icons.AutoMirrored.Filled.Send` |
| `list` | bulleted list | `Icons.AutoMirrored.Filled.List` |
| `calendar` | Calendar with lines | `Icons.Default.CalendarToday` or `Icons.Outlined.CalendarMonth` |
| `check` | ✓ checkmark | `Icons.Default.Check` |
| `checkSquare` | ☑ square check | `Icons.Default.CheckBox` |
| `clipboard` | Clipboard | `Icons.Default.ContentPaste` or `Icons.Default.Assignment` |
| `user` | Person silhouette | `Icons.Default.Person` |
| `star` | ★ star | `Icons.Default.Star` / `Icons.Outlined.Star` |
| `bell` | 🔔 bell | `Icons.Default.Notifications` |
| `edit` | Pencil | `Icons.Default.Edit` |
| `settings` | ⚙ gear | `Icons.Default.Settings` |
| `mapPin` | Map pin | `Icons.Default.LocationOn` |
| `clock` | Clock | `Icons.Default.Schedule` or `Icons.Default.AccessTime` |
| `circle` | Empty circle | `Icons.Outlined.Circle` |
| `circleCheck` | Filled check circle | `Icons.Default.CheckCircle` |
| `users` | Multiple people | `Icons.Default.Group` |
| `moon` | Crescent moon | `Icons.Default.DarkMode` |
| `refresh` | Circular arrows | `Icons.Default.Refresh` |
| `grid` | 4 squares | `Icons.Default.GridView` or `Icons.Default.Apps` |
| `lock` | Padlock | `Icons.Default.Lock` |
| `help` | ? in circle | `Icons.Default.Help` |
| `info` | i in circle | `Icons.Default.Info` |
| `trash` | Trash can | `Icons.Default.Delete` |
| `moreV` | 3 vertical dots | `Icons.Default.MoreVert` |
| `image` | Image/photo | `Icons.Default.Image` |

---

## Icons Requiring Custom ImageVector

These icons don't have good Material equivalents:

### Crown Icon (used in PRO badge, paywall, upgrade)
```kotlin
val CrownIcon: ImageVector = ImageVector.Builder(
    name = "Crown",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    addPath(
        pathData = PathData {
            moveTo(2f, 19f)
            horizontalLineTo(22f)
            lineTo(20.5f, 9f)
            lineTo(15.5f, 13f)
            lineTo(12f, 5f)
            lineTo(8.5f, 13f)
            lineTo(3.5f, 9f)
            close()
        },
        fill = SolidColor(Color.Unspecified) // Set at usage site
    )
}.build()
```

### Chevron Down (smaller variant, 20×20)
Use `Icons.Default.KeyboardArrowDown` with `Modifier.size(20.dp)`.

---

## Icon Sizing Rules

The design uses these icon sizes consistently:

| Context | SVG Size | Compose Size |
|---|---|---|
| Top bar actions | 24×24 | `Modifier.size(24.dp)` |
| Tab bar icons | 24×24 | `Modifier.size(24.dp)` |
| Detail row icons | 18×18 | `Modifier.size(18.dp)` |
| Small indicators | 12-16 | `Modifier.size(N.dp)` |
| Large hero (splash) | 44×44 | `Modifier.size(44.dp)` |
| Large hero (paywall) | 44×44 | `Modifier.size(44.dp)` |

---

## Icon Color Rules

Icons in the design always receive a color parameter. Map these:

| Context | Design Color | Compose Tint |
|---|---|---|
| Active tab | `T.primary` | `tint = Primary` |
| Inactive tab | `T.muted` | `tint = Muted` |
| Top bar action | `T.ink` | `tint = Ink` |
| Detail row icon | `T.muted` | `tint = Muted` |
| On primary bg (FAB, button) | `'#fff'` | `tint = White` |
| Danger action (trash) | `T.danger` | `tint = Danger` |
| Success check | `T.success` | `tint = Success` |

---

## Stroke Properties

The design icons use these stroke properties that should be preserved in custom vectors:

```
strokeWidth: 2       → strokeLineWidth = 2f
strokeLinecap: round  → strokeLineCap = StrokeCap.Round
strokeLinejoin: round → strokeLineJoin = StrokeJoin.Round
fill: none            → fill = null (stroke only)
```

For custom `ImageVector` paths with stroke:
```kotlin
addPath(
    pathData = PathData { /* ... */ },
    fill = null,
    stroke = SolidColor(Color.Unspecified),
    strokeLineWidth = 2f,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round
)
```

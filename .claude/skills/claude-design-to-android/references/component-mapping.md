# Component Mapping Reference

Maps Claude Design JSX components to Kotlin/Jetpack Compose equivalents.

> **About the examples in this file.** The patterns below come from real Claude Design bundles. Some sections show **universal patterns** (Frame, StatusBar, Buttons, Bottom Sheets) that apply to any bundle. Others show **illustrative examples** drawn from a calendar/task app — the function signatures, route names (`calendar`, `tasks`, `notes`, `mine`), and specific values (`"April 2026"`, `TaskState.OVERDUE`) are from that one bundle. Treat those as templates to adapt, not as a fixed schema. When you see app-specific values in this file, replace them with the values from the bundle you're converting.

---

## Table of Contents

**Universal patterns** (apply to every bundle):
1. [Frame → Scaffold/Box](#frame)
2. [Status Bar → Edge-to-edge](#status-bar)
3. [FAB → FloatingActionButton](#fab)
4. [Buttons](#buttons)
5. [Bottom Sheets](#bottom-sheets)
6. [Input Fields](#input-fields)
7. [Toggle/Switch](#toggleswitch)
8. [Overlays & Scrims](#overlays)
9. [Absolute Positioning](#absolute-positioning)

**Illustrative examples** (adapt to your bundle):
10. [TabBar → NavigationBar](#tabbar)
11. [TopBar → TopAppBar/Row](#topbar)
12. [List Items](#list-items)
13. [Calendar Grid](#calendar-grid)

---

## Frame

The design uses a `Frame` component as a phone-sized container (typically 390×844). On Android:

```kotlin
// DON'T hard-code 390×844 dimensions — Android phones vary widely.
// DO use Scaffold for screens with top bar + bottom nav + FAB:
@Composable
fun ScreenName(/* params */) {
    Scaffold(
        topBar = { /* TopBar composable */ },
        bottomBar = { /* TabBar composable */ },
        floatingActionButton = { /* FAB composable */ },
        containerColor = White // match the Frame's bg prop
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            // Screen content
        }
    }
}
```

For screens without Scaffold (Splash, Paywall, full-bleed):

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(White)
) {
    // Content
}
```

---

## Status Bar

Designs render a fake status bar inside the frame. On Android, the system handles the status bar — don't render it as a Composable. Instead, configure edge-to-edge in `MainActivity`:

```kotlin
// MainActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(
            scrim = android.graphics.Color.TRANSPARENT,
            darkScrim = android.graphics.Color.TRANSPARENT
        )
    )
    setContent { AppTheme { /* ... */ } }
}
```

Imports:
```kotlin
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
```

Requires `androidx.activity:activity-compose:1.8.0+`.

> **Don't use `rememberSystemUiController` from Accompanist.** That library is deprecated; the Compose team's `enableEdgeToEdge` API is the current replacement.

Also skip `HomeIndicator` from the design — that's an iOS concept Android doesn't render.

For status bar text color (light icons on dark bg, or dark icons on light bg), use `SystemBarStyle.dark(...)` or `SystemBarStyle.light(...)` accordingly.

---

## FAB

```kotlin
@Composable
fun AppFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = RoundedCornerShape(18.dp),
        containerColor = Primary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp
        )
    ) {
        Icon(Icons.Default.Add, "Add", tint = White, modifier = Modifier.size(24.dp))
    }
}
```

For colored shadows (when the design has a tinted shadow under the FAB):
```kotlin
modifier = modifier
    .size(56.dp)
    .shadow(
        elevation = 12.dp,
        shape = RoundedCornerShape(18.dp),
        ambientColor = Primary.copy(alpha = 0.3f),
        spotColor = Primary.copy(alpha = 0.3f)
    )
```

---

## Buttons

### Primary Button

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContainerColor = PrimaryDisabled // from your tokens
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 3.dp
        )
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White)
    }
}
```

Replace `52.dp`, `14.dp`, `15.sp` with the values from your bundle's button styling.

### Ghost Button

```kotlin
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    color: Color = Primary,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
```

---

## Bottom Sheets

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(Hair, RoundedCornerShape(2.dp))
            )
        },
        tonalElevation = 0.dp,
        scrimColor = ScrimColor // your design's scrim color, e.g. Color(0xFF0F0F1A).copy(alpha = 0.45f)
    ) {
        content()
    }
}
```

Adjust `topStart`/`topEnd` radii and the drag handle styling to match your design.

---

## Input Fields

The design typically uses a custom-styled box rather than Material's `OutlinedTextField`. Match the design's visual style:

```kotlin
@Composable
fun AppInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPlaceholder: Boolean = false,
    isBig: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Muted,
            letterSpacing = 0.3.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isBig) 52.dp else 44.dp)
                .border(1.dp, Hair, RoundedCornerShape(12.dp))
                .background(White, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                value,
                fontSize = if (isBig) 16.sp else 14.sp,
                fontWeight = if (isBig) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isPlaceholder) Muted2 else Ink
            )
        }
    }
}
```

> The example above renders the value as static text. For a real editable field, swap the inner `Text(value, ...)` for a `BasicTextField(value, onValueChange, ...)` styled to match.

---

## Toggle/Switch

```kotlin
@Composable
fun AppToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedTrackColor = Primary,
            checkedThumbColor = White,
            uncheckedTrackColor = Hair,
            uncheckedThumbColor = White
        ),
        modifier = Modifier.scale(0.85f) // adjust if your design is smaller than Material's default
    )
}
```

---

## Overlays

The design's scrim is typically a translucent dark overlay:

```kotlin
// Example scrim color: rgba(15,15,26,0.45) → Color(0xFF0F0F1A).copy(alpha = 0.45f)
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(ScrimColor)
        .clickable(onClick = onDismiss)
)
```

When using `ModalBottomSheet`, pass this color via `scrimColor =`.

---

## Absolute Positioning

JSX uses `position: absolute` extensively. In Compose, three patterns cover most cases:

### Pattern 1: Overlay within a Box

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    // Background content
    Content(modifier = Modifier.align(Alignment.TopStart))
    // Overlay at specific position
    FloatingElement(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 20.dp, bottom = 155.dp)
    )
}
```

### Pattern 2: Offset from anchor

```kotlin
Box(
    modifier = Modifier
        .offset(x = 0.dp, y = 140.dp)
        .fillMaxWidth()
)
```

### Pattern 3: Notification badge

```kotlin
Box {
    Icon(Icons.Default.Notifications, null)
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-8).dp, y = 8.dp)
            .size(8.dp)
            .background(DangerRed, CircleShape)
            .border(2.dp, White, CircleShape)
    )
}
```

---

# Illustrative Examples

The sections below show patterns from a calendar/task app bundle. They are **not** universal — the route names, screen names, dimensions, and component shapes are specific to that one bundle. Use them as a template: replace the names and values with what's in the bundle you're converting.

---

## TabBar

**Example only — adapt routes and icons to your bundle.**

In one bundle, the design pattern was:

```jsx
<TabBar active="calendar" onNav={onNav}/>
// Tabs: calendar, tasks, notes, mine
```

A Compose translation looked like:

```kotlin
@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = White,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 0.5.dp,
            color = Hair,
            shape = RectangleShape
        )
    ) {
        // Replace these with the tabs from YOUR bundle
        val tabs = listOf(
            TabItem("calendar", "Calendar", Icons.Outlined.CalendarToday, Icons.Filled.CalendarToday),
            TabItem("tasks",    "Tasks",    Icons.Outlined.CheckCircle,   Icons.Filled.CheckCircle),
            TabItem("notes",    "Notes",    Icons.Outlined.Description,   Icons.Filled.Description),
            TabItem("mine",     "Mine",     Icons.Outlined.Person,        Icons.Filled.Person),
        )

        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(tab.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.activeIcon else tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) Primary else Muted
                    )
                },
                label = {
                    Text(
                        tab.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) Primary else Muted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector
)
```

The design's tab bar height was 83px (iOS includes the home indicator area in this number — Android doesn't, so the actual `NavigationBar` will be shorter; that's fine).

---

## TopBar

**Example only — your bundle will have its own variants.**

The calendar/task design had multiple top bar variants. Implement each as a separate Composable.

### Month Top Bar (Calendar)

```kotlin
@Composable
fun MonthTopBar(
    month: String,                    // e.g. "April 2026" — pass in, don't hardcode
    viewMode: String = "Month",
    onToggleView: () -> Unit = {},
    onSearch: () -> Unit = {},
    onNotifications: () -> Unit = {},
    notificationBadge: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(month, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                 letterSpacing = (-0.3).sp, color = Ink)
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Muted,
                 modifier = Modifier.size(18.dp))
        }
        // View mode pill, search, bell — match design spacing exactly
    }
    HorizontalDivider(thickness = 0.5.dp, color = Hair)
}
```

### Simple Top Bar

```kotlin
@Composable
fun SimpleTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.offset(x = (-8).dp)) {
                // Use AutoMirrored.Filled.ArrowBack for proper RTL support.
                // Icons.Default.ArrowBack is deprecated.
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink)
            }
        }
        Text(title, fontSize = if (onBack != null) 18.sp else 20.sp,
             fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp,
             color = Ink, modifier = Modifier.weight(1f))
        trailing()
    }
    HorizontalDivider(thickness = 0.5.dp, color = Hair)
}
```

---

## List Items

**Example only — your bundle's list items will have different shapes.**

The calendar/task design had task rows with circle checkbox, title, due date, and priority pill:

```kotlin
@Composable
fun TaskRow(
    title: String,
    due: String? = null,
    priority: String? = null,
    state: TaskState = TaskState.DEFAULT,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Checkbox circle — three states from this design
        when (state) {
            TaskState.DONE -> { /* Green filled check circle (20dp) */ }
            TaskState.OVERDUE -> { /* Empty circle with dangerRed border (22dp, 1.8dp border) */ }
            else -> { /* Empty circle with muted2 border (22dp, 1.8dp border) */ }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (state == TaskState.DONE) Muted2 else Ink,
                textDecoration = if (state == TaskState.DONE) TextDecoration.LineThrough else null
            )
            // Due date and priority row...
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = Hair)
}

enum class TaskState { DEFAULT, DONE, OVERDUE }
```

---

## Calendar Grid

**Example only — only relevant if your bundle has a calendar.**

```kotlin
@Composable
fun MonthGrid(
    rows: List<List<DayCell>>,
    events: Map<Int, List<CalendarEvent>>,
    onDayClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // 7 cells per row
                            .height(88.dp)
                            .border(width = 0.5.dp, color = Hair, shape = RectangleShape)
                            .clickable(enabled = !cell.isOutsideMonth) { onDayClick(cell.day) }
                            .padding(start = 6.dp, top = 6.dp, end = 4.dp, bottom = 4.dp)
                    ) {
                        Column {
                            if (cell.isToday) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${cell.day}", fontSize = 12.sp,
                                         fontWeight = FontWeight.Bold, color = White)
                                }
                            } else {
                                Text(
                                    "${cell.day}",
                                    fontSize = 12.sp,
                                    fontWeight = if (cell.isOutsideMonth) FontWeight.Normal
                                                else FontWeight.Medium,
                                    color = if (cell.isOutsideMonth) Muted2 else Ink
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            val dayEvents = events[cell.day] ?: emptyList()
                            dayEvents.take(2).forEach { event ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .background(event.color.copy(alpha = 0.9f),
                                                    RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(event.title, fontSize = 9.sp,
                                         fontWeight = FontWeight.SemiBold, color = White,
                                         maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            if (dayEvents.size > 2) {
                                Text("+${dayEvents.size - 2} more", fontSize = 9.sp, color = Muted)
                            }
                        }
                    }
                }
            }
        }
    }
}
```

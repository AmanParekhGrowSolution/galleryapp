# Compose Pattern Snippets

Production-ready Jetpack Compose starting points for common screen types.
Aligned with `CLAUDE.md` §3 (UDF architecture), §4 (Kotlin/Compose rules), §7 (accessibility).

---

## UiState pattern (use this for every screen)

```kotlin
// ui/<feature>/<Feature>UiState.kt
sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(val items: List<PhotoItem>) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}
```

---

## ViewModel pattern

```kotlin
// ui/<feature>/<Feature>ViewModel.kt
class GalleryViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init { loadPhotos() }

    private fun loadPhotos() {
        viewModelScope.launch {
            _uiState.value = try {
                GalleryUiState.Success(repository.getPhotos())
            } catch (e: Exception) {
                GalleryUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
```

---

## Screen pattern (stateless composable)

```kotlin
// ui/<feature>/<Feature>Screen.kt
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GalleryContent(uiState = uiState)
}

@Composable
private fun GalleryContent(uiState: GalleryUiState) {
    when (uiState) {
        is GalleryUiState.Loading -> LoadingView()
        is GalleryUiState.Success -> PhotoGrid(items = uiState.items)
        is GalleryUiState.Error   -> ErrorView(message = uiState.message)
    }
}
```

---

## Theme files

```kotlin
// ui/theme/Color.kt  — put ALL extracted tokens here, never inline
val BrandBlue      = Color(0xFF0066FF)
val BrandBlueSoft  = Color(0xFFAAD5FF)
val BrandPurple    = Color(0xFF3300FF)
val FgPrimary      = Color(0xFF000000)
val FgSecondary    = Color(0xFF1E3354)
val FgMuted        = Color(0xFF7A8389)
val BgLight        = Color(0xFFFFFFFF)
val BgLight2       = Color(0xFFF9F9FA)
val Success        = Color(0xFF00A023)
val Danger         = Color(0xFFFF0202)
val Warning        = Color(0xFFFF9700)
```

```kotlin
// ui/theme/Type.kt
val AppTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 24.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)
```

---

## Login screen

```kotlin
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = BrandBlue
        )
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.label_email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.label_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
        ) {
            Text(stringResource(R.string.action_sign_in), style = MaterialTheme.typography.titleLarge)
        }
    }
}
```

---

## Dashboard / Home screen

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = BrandBlue) }

            is HomeUiState.Success -> HomeContent(
                sections = state.sections,
                modifier = Modifier.padding(paddingValues)
            )

            is HomeUiState.Error -> ErrorView(
                message = state.message,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun HomeContent(sections: List<Section>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        sections.forEach { section ->
            item(key = "header_${section.id}") {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(section.items, key = { it.id }) { item ->
                SectionItemCard(item = item, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
```

---

## List / Feed screen

```kotlin
@Composable
fun PhotoListScreen(viewModel: PhotoListViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val photos = remember(uiState) {
        (uiState as? PhotoListUiState.Success)?.photos ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoCard(photo = photo)
        }
    }
}

@Composable
private fun PhotoCard(photo: Photo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = photo.url,
                contentDescription = photo.altText,   // never null — CLAUDE.md §7
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(12.dp)) {
                Text(photo.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(photo.subtitle, style = MaterialTheme.typography.bodyMedium, color = FgMuted)
            }
        }
    }
}
```

---

## Grid screen (3-column photo grid)

```kotlin
@Composable
fun PhotoGridScreen(photos: List<Photo>) {
    val columns = 3
    val sorted = remember(photos) { photos.sortedByDescending { it.timestamp } }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(sorted, key = { it.id }) { photo ->
            AsyncImage(
                model = photo.thumbnailUrl,
                contentDescription = photo.altText,
                modifier = Modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
    }
}
```

---

## Profile screen

```kotlin
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight2)
            .verticalScroll(rememberScrollState())
    ) {
        // Avatar + name header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgLight)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = (uiState as? ProfileUiState.Success)?.avatarUrl,
                    contentDescription = stringResource(R.string.cd_user_avatar),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = (uiState as? ProfileUiState.Success)?.displayName.orEmpty(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        // Settings rows
        ProfileMenuItem(
            icon = Icons.Default.PhotoLibrary,
            label = stringResource(R.string.menu_my_photos),
            onClick = {}
        )
        ProfileMenuItem(
            icon = Icons.Default.Settings,
            label = stringResource(R.string.menu_settings),
            onClick = {}
        )
        ProfileMenuItem(
            icon = Icons.Default.Logout,
            label = stringResource(R.string.menu_sign_out),
            onClick = {}
        )
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(BgLight)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,   // decorative — label provides context
            tint = FgSecondary
        )
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FgMuted)
    }
}
```

---

## Settings screen

```kotlin
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item(key = "section_account") {
                SettingsSectionHeader(title = stringResource(R.string.settings_section_account))
            }
            item(key = "notifications") {
                SettingsToggleRow(
                    label = stringResource(R.string.settings_notifications),
                    checked = true,
                    onCheckedChange = {}
                )
            }
            item(key = "section_privacy") {
                SettingsSectionHeader(title = stringResource(R.string.settings_section_privacy))
            }
            item(key = "analytics") {
                SettingsToggleRow(
                    label = stringResource(R.string.settings_analytics),
                    checked = false,
                    onCheckedChange = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = FgMuted,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
```

---

## Loading shimmer

```kotlin
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    Box(modifier = modifier.background(FgMuted.copy(alpha = alpha), RoundedCornerShape(8.dp)))
}
```

---

## Error view

```kotlin
@Composable
fun ErrorView(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Danger, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, color = FgSecondary)
        if (onRetry != null) {
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry), color = BrandBlue)
            }
        }
    }
}
```

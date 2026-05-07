# REVIEW.md — Android Code Review Configuration
> Loaded by Claude as a companion to CLAUDE.md during every PR review.
> CLAUDE.md defines the rules. REVIEW.md defines how to report them.

---

## Severity Calibration

### 🔴 Important — Block merge
Report these as **BLOCKING** violations. PR cannot merge until resolved.

| Category | Rule |
|---|---|
| **Dead handlers** | `onClick`, `onDismiss`, `onConfirm`, or similar handler that is empty `{}` or calls an empty function |
| **Broken state machine** | `viewModelScope.launch` missing `Loading` state before async call, or missing `Success`+`Error` (no try/catch with error state) |
| **Unregistered routes** | `navigate(route)` where the destination is not registered in the NavHost graph |
| **Architecture** | ViewModel holding Activity/Fragment `Context`; UI importing data/domain layer directly; `collectAsState()` instead of `collectAsStateWithLifecycle()` |
| **Security** | HTTP URLs; hardcoded secrets/keys; deprecated crypto (MD5/SHA-1/DES/ECB); `ALLOW_ALL_HOSTNAME_VERIFIER`; `android:debuggable="true"` in main manifest |
| **Security — judgment calls** | `SharedPreferences` storing credentials (use `EncryptedSharedPreferences`); `setJavaScriptEnabled(true)` without justification; `@JavascriptInterface` leaking sensitive data; exported components without access control |
| **Accessibility** | `Image` or `Icon` missing `contentDescription` (or explicit decorative `Role`) |
| **Coroutines** | `GlobalScope.launch`; `runBlocking` in production code |
| **Null safety** | `!!` on values from external input, network, or user interaction |

### 🟡 Nit — Worth fixing, does not block
Report these as **ADVISORY**. Flag them; never fail the PR.

| Category | Rule |
|---|---|
| **Kotlin idioms** | `!!` on internal/construction-safe values; `enum class` where `sealed interface` fits better for `UiState` |
| **Compose performance** | Missing `key =` in `items()`; state reads not in `derivedStateOf`; expensive calc not in `remember {}` |
| **String resources** | Multi-word user-visible strings hardcoded instead of in `strings.xml` |
| **Privacy** | File paths, EXIF, or gallery content logged or sent to analytics without consent handling |
| **Testing** | Non-trivial ViewModel/Repository with complex state transitions and no unit test |
| **Compose style** | Loading state using solid placeholder instead of shimmer |

### 🟣 Pre-existing — Note but never block
If a violation exists in unchanged lines (not in the diff), prefix with `Pre-existing:` and list it once in advisory notes. Do not dedicate inline comments to pre-existing code.

---

## Do Not Report

Never raise issues for:

- **Generated code**: Room DAOs, Apollo/GraphQL generated files, Hilt generated components, `BuildConfig`, `R.kt`
- **Build files**: `*.gradle.kts`, `gradle.properties`, `settings.gradle*`, `local.properties`
- **Lock files**: `gradle/wrapper/gradle-wrapper.properties`, dependency lockfiles
- **Test-only code**: Test ViewModels, fake repositories, and test helpers are allowed to use `mutableStateOf`, `runBlocking`, and other patterns forbidden in production
- **Migration files**: Room migration classes
- **Third-party vendored code**: Anything under `app/src/main/java/com/thirdparty/` or similar vendor paths
- **Commented-out code that is being deleted**: If a diff shows lines being removed, do not flag what was there before

---

## Android-Specific Check Matrix

### Must check on EVERY PR

| Check | How to verify | Severity |
|---|---|---|
| Dead click handlers | Read ViewModel handler function body via `Read` tool | 🔴 BLOCKING |
| State machine completeness | Trace `viewModelScope.launch` for `Loading` + `Success` + `Error` | 🔴 BLOCKING |
| Navigation route registration | Read NavHost file, verify destination exists | 🔴 BLOCKING |
| `Image`/`Icon` contentDescription | Scan every `Image(` and `Icon(` in diff | 🔴 BLOCKING |
| `collectAsStateWithLifecycle()` | Verify not using `collectAsState()` in UI | 🔴 BLOCKING |
| `enableEdgeToEdge()` before `setContent` | Check new `Activity` classes | 🔴 BLOCKING |
| Inset padding correct level | NavHost applies once; inner screens inherit | 🔴 BLOCKING |
| `LazyColumn`/`LazyRow` `key =` | All `items(...)` calls have `key = { it.id }` | 🟡 Nit |
| `remember {}` around expensive ops | Flag costly ops inside composable body | 🟡 Nit |

### Check only if relevant to the diff

| Check | Trigger |
|---|---|
| `EncryptedSharedPreferences` | Diff adds `SharedPreferences` |
| `WebView` JS security | Diff adds `WebView` |
| `@JavascriptInterface` isolation | Diff adds `@JavascriptInterface` |
| Runtime permissions | Diff adds `DANGEROUS` permission usage |
| Crypto algorithm | Diff adds `Cipher`, `MessageDigest`, `KeyGenerator` |

---

## Nit Cap

Report **at most 5 nits** per review. If you find more, pick the 5 most impactful and add:

> "Plus N similar style issues not listed here. Address in a follow-up."

---

## Inline Comments

- Post inline comments **only for BLOCKING violations** that are line-specific.
- Advisory/nit issues go in the summary comment only — do not clutter the diff view with style suggestions.
- Use the `mcp__github_inline_comment__create_inline_comment` tool for inline comments.

---

## Summary Comment Format

```
## Claude Review — PR #N

**Verdict: ✅ PASSED** | **Verdict: ❌ BLOCKED**

### ❌ Blocking Violations  (or "None")
- `file.kt:42` — [RULE CODE] Description

### ⚠️ Advisory Notes  (or "None")
- `file.kt:88` — Description (pre-existing: yes/no)

### ✅ Checks Passed
- Dead click handlers: all N handlers traced and verified functional
- Navigation routes: all navigate() calls registered in NavGraph
- State machines: all viewModelScope.launch blocks have Loading + Success + Error
- Architecture: correct layer separation confirmed
- Security: no MASVS violations found
- Accessibility: all Image/Icon have contentDescription

### 📁 Files Reviewed
- `path/to/File.kt`
- ...
```

Lead with: `"No blocking issues. N architecture, M style."` if only advisory notes.
Verdict is **PASS** only if zero blocking violations AND zero linter `[BLOCKING]` lines.

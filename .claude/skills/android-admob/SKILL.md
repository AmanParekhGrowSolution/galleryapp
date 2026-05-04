---
name: android-admob
description: "Implements Google AdMob monetization (App Open ads, Adaptive Banner, Interstitial, Rewarded) in any Android Kotlin + Jetpack Compose app using Koin DI. Use this skill whenever the user wants to add ads, monetize their Android app, integrate AdMob, set up Google ads, add banner ads, add interstitial ads, add app open ads, or earn revenue from their app. Also trigger when the user mentions an AdMob App ID (ca-app-pub-...), asks about ad placement strategy, wants aggressive monetization, or asks how to put ads in their Android app. Trigger even if the user only says 'add ads' or 'monetize this' — always use this skill for any ad integration task on an Android project."
---

# Android AdMob Skill

You are implementing Google AdMob monetization in an Android Kotlin + Jetpack Compose app. Your job is to wire up ads correctly, aggressively, and in a way that won't get the app banned from the Play Store.

## Step 1 — Gather context

Before writing any code, read these files to understand the project:

1. `CLAUDE.md` — architecture, DI setup, package name, screen list
2. `gradle/libs.versions.toml` — existing dependency versions
3. `app/build.gradle.kts` — current dependencies block
4. `app/src/main/AndroidManifest.xml` — application class name, existing activities
5. The Application class file (name from manifest `android:name`)
6. The main DI module that registers app-wide singletons (usually `di/AppModule.kt`)
7. The main list/home screen composable

**Then ask the user for the following (accept defaults if they don't answer):**

| Question | Default |
|---|---|
| AdMob App ID? | Use test ID `ca-app-pub-3940256099942544~3347511713` |
| Which screens should have a persistent banner? | Main/home screen, above bottom nav |
| Which user actions should trigger an interstitial? | After saving a record (alarm, note, item, etc.) |
| Which Activity class name should be excluded from App Open ads? | Any activity whose name contains `Ring`, `Alarm`, `Lock`, or `Dismiss` |

## Step 2 — Add dependencies

In `gradle/libs.versions.toml`, add to `[versions]`:
```toml
playServicesAds = "25.1.0"
lifecycleProcess = "2.8.7"
```

Add to `[libraries]`:
```toml
play-services-ads = { group = "com.google.android.gms", name = "play-services-ads", version.ref = "playServicesAds" }
androidx-lifecycle-process = { group = "androidx.lifecycle", name = "lifecycle-process", version.ref = "lifecycleProcess" }
```

In `app/build.gradle.kts` dependencies block:
```kotlin
implementation(libs.play.services.ads)
implementation(libs.androidx.lifecycle.process)
```

## Step 3 — AndroidManifest.xml

Add inside `<application>` tag, before any `<activity>` tags:
```xml
<!-- AdMob App ID — replace with production ID before shipping -->
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="USER_APP_ID_HERE" />
```

Add permissions alongside existing `<uses-permission>` lines:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Step 4 — Create the `ads/` package

Create all four files under `app/src/main/java/<package>/ads/`:

### AdIds.kt
```kotlin
package <package>.ads

/**
 * Centralized ad unit IDs.
 * TEST IDs are Google's official test units — safe during development.
 * Replace every constant with a real ad unit ID from AdMob console before releasing.
 */
object AdIds {
    // TODO: Replace with production App ID
    const val APP_ID = "USER_APP_ID_HERE"

    // TODO: Replace with real ad unit IDs from AdMob console (one per placement)
    const val APP_OPEN     = "ca-app-pub-3940256099942544/9257395921"
    const val BANNER_HOME  = "ca-app-pub-3940256099942544/9214589741"
    const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED     = "ca-app-pub-3940256099942544/5224354917"
}
```

### AppOpenAdManager.kt
```kotlin
package <package>.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

/**
 * App Open ads fire on every cold start and foreground resume.
 * Google designed this format explicitly for utility apps — fully policy-compliant.
 * Caller is responsible for NOT showing this on any alarm/ring/dismiss activity.
 */
class AppOpenAdManager(private val context: Context) {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0

    fun loadAd() {
        if (isLoadingAd || isAdAvailable()) return
        isLoadingAd = true
        AppOpenAd.load(
            context,
            AdIds.APP_OPEN,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingAd = false
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity, onShowComplete: () -> Unit = {}) {
        if (isShowingAd) return
        if (!isAdAvailable()) {
            loadAd()
            onShowComplete()
            return
        }
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onShowComplete()
                loadAd()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                appOpenAd = null
                isShowingAd = false
                onShowComplete()
            }
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        appOpenAd?.show(activity)
    }

    private fun isAdAvailable(): Boolean {
        val ageMs = Date().time - loadTime
        return appOpenAd != null && ageMs < 4 * 3_600_000L
    }
}
```

### InterstitialAdManager.kt
```kotlin
package <package>.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Manages interstitial ads. Registered as a Koin single so any screen can inject it.
 *
 * Pattern: pre-load on screen entry (LaunchedEffect), show in onDismissed callback
 * so navigation always fires — even if the ad wasn't ready.
 */
class InterstitialAdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd() {
        if (interstitialAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            context,
            AdIds.INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    /** Show if ready; always calls [onDismissed] so navigation is never blocked. */
    fun showAd(activity: Activity, onDismissed: () -> Unit = {}) {
        val ad = interstitialAd ?: run { onDismissed(); return }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onDismissed()
                loadAd()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                onDismissed()
            }
        }
        ad.show(activity)
    }

    fun isReady() = interstitialAd != null
}
```

### AdComposables.kt
```kotlin
package <package>.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Adaptive banner — sizes itself to screen width for maximum fill rate.
 * Place at the bottom of any screen scaffold, above the bottom nav bar if present.
 */
@Composable
fun AdaptiveBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                val displayMetrics = context.resources.displayMetrics
                val adWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
                )
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
```

## Step 5 — Update the Application class

Read the existing Application class carefully. You need to add:
- `MobileAds.initialize()` on a background thread
- `AppOpenAdManager` wired to `ProcessLifecycleOwner` + `ActivityLifecycleCallbacks`
- A guard so App Open ads never show on excluded activities

**Important Kotlin quirk:** When an Application class implements both `Application.ActivityLifecycleCallbacks` AND `DefaultLifecycleObserver`, their `onCreate` and `onStart` methods are ambiguous to the Kotlin compiler. Always qualify: `super<Application>.onCreate()` and `super<DefaultLifecycleObserver>.onStart(owner)`.

```kotlin
class YourApplication : Application(),
    Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super<Application>.onCreate()
        // ... existing init (Koin, etc.) ...

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@YourApplication) {}
        }
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.loadAd()
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onStart(owner)
        val activity = currentActivity ?: return
        // Exclude any activity that is an alarm ring / lock screen / dismiss screen
        if (!isExcludedActivity(activity)) {
            appOpenAdManager.showAdIfAvailable(activity)
        }
    }

    private fun isExcludedActivity(activity: Activity): Boolean {
        val name = activity.javaClass.simpleName
        return name.contains("Ring", ignoreCase = true) ||
               name.contains("Alarm", ignoreCase = true) && name != this.javaClass.simpleName ||
               name.contains("Lock", ignoreCase = true) ||
               name.contains("Dismiss", ignoreCase = true)
        // Add the user's specific excluded activity class here if needed
    }

    override fun onActivityResumed(activity: Activity) { currentActivity = activity }
    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) currentActivity = null
    }
    override fun onActivityCreated(a: Activity, b: android.os.Bundle?) {}
    override fun onActivityStarted(a: Activity) {}
    override fun onActivityStopped(a: Activity) {}
    override fun onActivitySaveInstanceState(a: Activity, b: android.os.Bundle) {}
    override fun onActivityDestroyed(a: Activity) {}
}
```

**Note:** Tailor the `isExcludedActivity` check based on the actual excluded activity class names the user provided.

## Step 6 — Register InterstitialAdManager in Koin

In the app-wide DI module (usually `di/AppModule.kt`), add:

```kotlin
import <package>.ads.InterstitialAdManager
import org.koin.android.ext.koin.androidContext

// inside the module { } block:
single { InterstitialAdManager(androidContext()) }
```

## Step 7 — Add banner to the main screen

In the main/home screen composable, find the `Column` or `Scaffold` layout. Place the banner **above** the bottom nav bar (or at the very bottom if there is no bottom nav):

```kotlin
import <package>.ads.AdaptiveBannerAd
import <package>.ads.AdIds

// Inside the Column/Scaffold, just before AlarmBottomNav or at end of Column:
AdaptiveBannerAd(adUnitId = AdIds.BANNER_HOME)
```

## Step 8 — Add interstitial to save screens

In every screen that has a save/confirm action that navigates back:

1. Inject `InterstitialAdManager` via `koinInject()`
2. Pre-load in `LaunchedEffect(Unit)`
3. Intercept the save event — show the interstitial, then call the navigation callback in `onDismissed`

```kotlin
import <package>.ads.InterstitialAdManager
import org.koin.compose.koinInject

@Composable
fun YourSaveScreen(
    onSave: (result) -> Unit = {}
) {
    val activity = LocalContext.current as Activity
    val interstitialAdManager: InterstitialAdManager = koinInject()

    LaunchedEffect(Unit) {
        interstitialAdManager.loadAd()
    }

    // In the event collector or save callback:
    // interstitialAdManager.showAd(activity) { onSave(result) }
    //
    // The key: navigation fires inside onDismissed, NOT before showAd().
    // This way the user always gets navigated back — whether the ad showed or not.
}
```

## Step 9 — Build and verify

```bash
export JAVA_HOME="/e/Android Studio/jbr" && ./gradlew assembleDebug
```

Fix all errors before moving on. Common issues:
- Ambiguous `super` call → use `super<Application>.onCreate()` / `super<DefaultLifecycleObserver>.onStart(owner)`
- Missing import for `koinInject` → `import org.koin.compose.koinInject`
- Missing import for `InterstitialAdManager` → add full package path

Then install:
```bash
./gradlew installDebug
```

## Step 10 — Update CLAUDE.md

Append a `## Monetization (Google AdMob)` section to the project's `CLAUDE.md` documenting:
- SDK version
- App ID (note it's test vs prod)
- The `ads/` package structure
- Ad placement table (format, screen, policy note)
- DI wiring (InterstitialAdManager = Koin single, AppOpenAdManager = created in Application)
- Hard policy rules (never show from background, never on ring/alarm screen)
- Production checklist (replace test IDs, use BuildConfig fields, create one unit per placement)

## Policy rules — never violate

These will get the AdMob account permanently banned if broken:

- Never show any ad while an alarm/ring/dismiss screen is active
- Never trigger ads from `AlarmReceiver`, `AlarmService`, `BroadcastReceiver`, or any background context
- Never show an interstitial on cold app launch (before content is shown)
- Never show an interstitial on back-press or app exit
- Never show an interstitial after every button tap — only at natural content transitions
- Ads must never obscure or delay the user's ability to dismiss an alarm

## Ad format quick reference

| Format | Test Unit ID | Best placement |
|---|---|---|
| App Open | `ca-app-pub-3940256099942544/9257395921` | Every foreground resume via `ProcessLifecycleOwner` |
| Adaptive Banner | `ca-app-pub-3940256099942544/9214589741` | Bottom of main screen, always-on |
| Interstitial | `ca-app-pub-3940256099942544/1033173712` | After save actions, natural screen transitions |
| Rewarded | `ca-app-pub-3940256099942544/5224354917` | Optional feature unlock ("watch ad to snooze again") |
| Rewarded Interstitial | `ca-app-pub-3940256099942544/5354046379` | Avoid — gray area for utility apps |
| Native | `ca-app-pub-3940256099942544/2247696110` | Complex to implement in Compose — skip for now |

## Production checklist (tell the user at the end)

1. Create real ad units in AdMob console — **one unit per placement** (enables per-placement analytics)
2. Replace all `TODO` constants in `AdIds.kt` with real unit IDs
3. Store IDs in `BuildConfig` fields via `buildConfigField` in `build.gradle.kts` (keeps them out of source)
4. Register your physical test device's advertising ID in AdMob console before testing
5. Enable frequency caps in AdMob console (recommended: max 3 interstitials/user/hour)
6. Consider AdMob Mediation (AppLovin MAX) after baseline is stable — typically 20–40% eCPM lift

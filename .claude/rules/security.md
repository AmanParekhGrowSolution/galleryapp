---
description: Security and privacy rules (OWASP MASVS) — auto-loaded when editing Kotlin, manifest, or network/data files
paths:
  - "app/src/main/java/**/*.kt"
  - "app/src/main/AndroidManifest.xml"
  - "app/src/main/java/**/network/**/*.kt"
  - "app/src/main/java/**/data/**/*.kt"
  - "app/src/main/res/xml/network_security_config.xml"
---

## Security Rules (CLAUDE.md §10) — BLOCKING

All SEC rules are enforced by `scripts/check-claude-md.sh`. Violations are **BLOCKING** — the PR
cannot merge until every SEC finding is resolved.

### SEC1 — HTTPS only (MASVS-NETWORK)
```kotlin
// ❌ cleartext
val url = "http://api.example.com/photos"

// ✅ secure
val url = "https://api.example.com/photos"
```

### SEC2 — No hardcoded credentials (MASVS-CRYPTO)
```kotlin
// ❌ hardcoded
val apiKey = "sk-abc123xyz789longvalue"

// ✅ injected at build time
val apiKey = BuildConfig.API_KEY  // set via local.properties + buildConfigField
// OR retrieved at runtime from Android Keystore
```

### SEC5 — SecureRandom, not Random (MASWE-0027)
```kotlin
// ❌
val nonce = java.util.Random().nextBytes(16)

// ✅
val nonce = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) }
```

### SEC6 — Modern crypto algorithms only (MASTG-DEMO-0022/0023)
```kotlin
// ❌ broken algorithms
MessageDigest.getInstance("MD5")
MessageDigest.getInstance("SHA-1")
Cipher.getInstance("AES/ECB/PKCS5Padding")
Cipher.getInstance("DESede/CBC/PKCS5Padding")

// ✅ strong algorithms
MessageDigest.getInstance("SHA-256")
Cipher.getInstance("AES/GCM/NoPadding")
```

### SEC9 — Always validate TLS certificates (MASWE-0052)
```kotlin
// ❌ bypasses certificate checks
HttpsURLConnection.setDefaultHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER)

// ❌ no-op TrustManager
override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) { /* empty */ }

// ✅ use the system default TrustManager; only override if doing cert pinning with proper validation
```

### SEC10 — Remove debuggable from main manifest (MASWE-0067)
```xml
<!-- ❌ never in app/src/main/AndroidManifest.xml -->
<application android:debuggable="true" ...>

<!-- ✅ the debug build type sets debuggable=true automatically -->
<!-- main manifest should have no debuggable attribute -->
```

---

## Privacy Rules (CLAUDE.md §11) — ADVISORY

These appear as review comments but do not block merge.

### PRIV1 — Never log sensitive data (MASWE-0001)
```kotlin
// ❌ exposes token in logcat
Log.d(TAG, "Auth token: $token")
Log.i(TAG, "User email: ${user.email}")

// ✅ guard with build flag or omit entirely
if (BuildConfig.DEBUG) Log.d(TAG, "Auth state: ${state::class.simpleName}")
```

### PRIV2 — No println in production
```kotlin
// ❌
println("photo path: $path")
System.out.println(response.body())

// ✅
Log.d(TAG, "photo path: $path")  // + BuildConfig.DEBUG guard for sensitive info
```

### Photo / gallery data is PII
This app processes user photos. Treat the following as sensitive personal data:
- File paths, filenames, album names
- EXIF metadata (GPS coordinates, device model, timestamps)
- Face detection / AI analysis results

Do not log, cache unencrypted, or send to analytics without explicit user consent.

#!/usr/bin/env bash
# check-claude-md.sh — deterministic CLAUDE.md compliance linter
#
# Usage:
#   bash scripts/check-claude-md.sh                  # scan all .kt files under app/src/main/
#   bash scripts/check-claude-md.sh --diff <BASE>    # restrict to lines changed vs BASE (e.g. origin/main)
#   bash scripts/check-claude-md.sh <file> [<file>…] # scan specific files
#
# Exit codes: 0 = no BLOCKING violations (advisory may be present), 1 = BLOCKING violations found
# Output format: <file>:<line> [<ID>] [BLOCKING|ADVISORY] <description> — `<snippet>`
#
# BLOCKING rules (fail the PR):
#   A1  IconButton touch target < 48 dp
#   S2  single-char hardcoded UI separator — move to strings.xml
#   ARCH1  ViewModel imports android.view.*
#   ARCH2  .collectAsState() instead of .collectAsStateWithLifecycle()
#   SEC1   cleartext HTTP URL literal (http://) — use HTTPS (MASVS-NETWORK)
#   SEC2   hardcoded credential/API key in source — use environment var or Keystore (MASVS-CRYPTO)
#   SEC5   java.util.Random import — not cryptographically secure; use SecureRandom (MASWE-0027)
#   SEC6   deprecated crypto: MD5, SHA-1, ECB mode, or DES (MASTG-DEMO-0022/0023)
#   SEC9   permissive hostname verifier or no-op TrustManager (MASWE-0052)
#   SEC10  android:debuggable="true" in main AndroidManifest.xml (MASWE-0067)
#
# ADVISORY rules (comment but do not block):
#   P1    LazyColumn/LazyRow items() missing key= — unstable recomposition
#   P2    inline collection op (.sortedBy/.filter etc.) inside items() — wrap with remember(key)
#   P3    scroll state read (.firstVisibleItemIndex) without derivedStateOf {}
#   LC1   GlobalScope.launch/async — ignores structured concurrency
#   LC2   runBlocking in main source — blocks calling thread
#   LC3   viewModelScope used outside a ViewModel file
#   PRIV1 Log.* statement mentioning sensitive fields (token/password/email/phone)
#   PRIV2 println() in production source — use Log.* with a build-flag guard

set -euo pipefail

BLOCKING_VIOLATIONS=0
ADVISORY_VIOLATIONS=0
DIFF_BASE=""
TARGET_FILES=()

# ── Argument parsing ─────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --diff)
      DIFF_BASE="$2"; shift 2 ;;
    *)
      TARGET_FILES+=("$1"); shift ;;
  esac
done

# Default: all .kt files under app/src/main/
if [[ ${#TARGET_FILES[@]} -eq 0 ]]; then
  mapfile -t TARGET_FILES < <(find app/src/main -name "*.kt" 2>/dev/null)
fi

if [[ ${#TARGET_FILES[@]} -eq 0 ]]; then
  echo "check-claude-md: no .kt files found" >&2
  exit 0
fi

# ── Diff-filter helper ───────────────────────────────────────────────────────
# If --diff was given, build a set of "file:line" pairs that are NEW in this
# diff so we only flag lines that were actually changed.
declare -A DIFF_LINES  # key = "file:linenum", value = 1

if [[ -n "$DIFF_BASE" ]]; then
  while IFS= read -r raw; do
    # git diff --unified=0 output: +++ b/<path>  /  @@ -a,b +c,d @@
    if [[ "$raw" =~ ^\+\+\+\ b/(.+)$ ]]; then
      current_file="${BASH_REMATCH[1]}"
    elif [[ "$raw" =~ ^@@\ -[0-9]+[,0-9]*\ \+([0-9]+)[,]?([0-9]*)\ @@ ]]; then
      start=${BASH_REMATCH[1]}
      count=${BASH_REMATCH[2]}
      [[ -z "$count" ]] && count=1
      for (( i=0; i<count; i++ )); do
        DIFF_LINES["${current_file}:$((start + i))"]=1
      done
    fi
  done < <(git diff --unified=0 "$DIFF_BASE" -- "${TARGET_FILES[@]}" 2>/dev/null || true)
fi

is_in_diff() {
  local file="$1" line="$2"
  [[ -z "$DIFF_BASE" ]] && return 0
  # Normalise: strip leading ./ and repo-root prefix
  local key
  key=$(realpath --relative-to="$(git rev-parse --show-toplevel 2>/dev/null || pwd)" "$file" 2>/dev/null || echo "$file")
  key="${key#./}"
  [[ "${DIFF_LINES[${key}:${line}]+_}" ]] && return 0 || return 1
}

# ── Report helper ────────────────────────────────────────────────────────────
report() {
  local file="$1" line="$2" id="$3" severity="$4" desc="$5" snippet="$6"
  printf '%s:%s [%s] [%s] %s — `%s`\n' "$file" "$line" "$id" "$severity" "$desc" "$snippet"
  if [[ "$severity" == "BLOCKING" ]]; then
    BLOCKING_VIOLATIONS=$((BLOCKING_VIOLATIONS + 1))
  else
    ADVISORY_VIOLATIONS=$((ADVISORY_VIOLATIONS + 1))
  fi
}

# ── Rule scanners ────────────────────────────────────────────────────────────

scan_file() {
  local file="$1"
  local lineno=0

  while IFS= read -r line_text; do
    lineno=$((lineno + 1))
    is_in_diff "$file" "$lineno" || continue

    # ── Accessibility (A1) ───────────────────────────────────────────────────

    # A1 — Touch target < 48 dp on IconButton
    if echo "$line_text" | grep -qP 'IconButton\('; then
      if echo "$line_text" | grep -qP 'Modifier\.size\(([0-3]?[0-9]|4[0-7])\.dp\)'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "A1" "BLOCKING" "touch target < 48 dp — minimum is Modifier.size(48.dp)" "$snippet"
      fi
    fi

    # ── String resources (S2) ────────────────────────────────────────────────

    # S2 — Single-char hardcoded UI separators in Text(...)
    if echo "$line_text" | grep -qP 'Text\(\s*"[:/·–→]"'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "S2" "BLOCKING" "hardcoded single-char separator — move to strings.xml" "$snippet"
    fi

    # ── Architecture (ARCH1–ARCH2) ───────────────────────────────────────────

    # ARCH1 — ViewModel imports android.view.*
    if [[ "$file" == *ViewModel.kt ]]; then
      if echo "$line_text" | grep -qP '^import android\.view\.'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "ARCH1" "BLOCKING" "ViewModel must not import android.view.*" "$snippet"
      fi
    fi

    # ARCH2 — plain collectAsState() instead of collectAsStateWithLifecycle()
    if echo "$line_text" | grep -qP '\.collectAsState\(\)'; then
      if ! echo "$line_text" | grep -qP 'collectAsStateWithLifecycle'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "ARCH2" "BLOCKING" "use collectAsStateWithLifecycle() — not plain collectAsState()" "$snippet"
      fi
    fi

    # ── Security — OWASP MASVS (SEC1–SEC9) ──────────────────────────────────

    # SEC1 — cleartext HTTP URL (not localhost / loopback / .local)
    if echo "$line_text" | grep -qP '"http://'; then
      if ! echo "$line_text" | grep -qP '"http://(localhost|127\.0\.0\.1|10\.\d+\.\d+\.\d+|[^"]*\.local)'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "SEC1" "BLOCKING" "cleartext HTTP URL — use HTTPS (MASVS-NETWORK / MASWE-0050)" "$snippet"
      fi
    fi

    # SEC2 — hardcoded credential: api_key/token/secret/password = "long-value"
    if echo "$line_text" | grep -qiP '(api_?key|api_?secret|access_?token|auth_?token|secret|password|passwd)\s*=\s*"[A-Za-z0-9_+/=\-]{16,}"'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "SEC2" "BLOCKING" "hardcoded credential — use environment variable or Android Keystore (MASVS-CRYPTO / MASWE-0013)" "$snippet"
    fi

    # SEC5 — java.util.Random import (not cryptographically secure)
    if echo "$line_text" | grep -qP '^import\s+java\.util\.Random\b'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "SEC5" "BLOCKING" "java.util.Random is not cryptographically secure — use java.security.SecureRandom (MASWE-0027)" "$snippet"
    fi

    # SEC6 — deprecated hash algorithms: MD5 / SHA-1
    if echo "$line_text" | grep -qP 'MessageDigest\.getInstance\(\s*"(MD5|SHA-1|SHA1)"\s*\)'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "SEC6" "BLOCKING" "deprecated hash (MD5/SHA-1) — use SHA-256 or stronger (MASTG-DEMO-0022)" "$snippet"
    fi
    # SEC6 — deprecated cipher: ECB mode or DES
    if echo "$line_text" | grep -qP 'Cipher\.getInstance\(\s*"([^"]*[/]ECB[/]|DES[^"]*)"\s*\)'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "SEC6" "BLOCKING" "deprecated cipher (ECB mode or DES) — use AES/GCM or AES/CBC+HMAC (MASTG-DEMO-0023)" "$snippet"
    fi

    # SEC9 — permissive hostname verifier (ALLOW_ALL or custom lambda)
    if echo "$line_text" | grep -qP 'ALLOW_ALL_HOSTNAME_VERIFIER'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "SEC9" "BLOCKING" "ALLOW_ALL_HOSTNAME_VERIFIER bypasses TLS cert validation — validate hostname properly (MASWE-0052)" "$snippet"
    fi
    # SEC9 — custom TrustManager: flag checkServerTrusted override for manual review
    if echo "$line_text" | grep -qP 'override\s+fun\s+checkServerTrusted'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "SEC9" "BLOCKING" "custom checkServerTrusted — verify it actually validates certs and does not no-op (MASWE-0052)" "$snippet"
    fi

    # ── Compose performance — ADVISORY (P1–P3) ───────────────────────────────

    # P1 — items() inside LazyColumn/LazyRow without key= on same line
    if echo "$line_text" | grep -qP '\bitems\('; then
      if ! echo "$line_text" | grep -qP '\bkey\s*='; then
        if grep -qP 'Lazy(Column|Row|Grid)\b' "$file" 2>/dev/null; then
          snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
          report "$file" "$lineno" "P1" "ADVISORY" "LazyColumn/Row items() missing key= — supply a stable key to prevent unnecessary recomposition" "$snippet"
        fi
      fi
    fi

    # P2 — inline collection op as argument to items() (should be in remember)
    if echo "$line_text" | grep -qP '\bitems\([^)]*\.(sortedBy|sortedWith|sortedByDescending|filter|groupBy)\s*[{\(]'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "P2" "ADVISORY" "inline collection op in items() — wrap with remember(key) { ... } to avoid recomputation on every recomposition" "$snippet"
    fi

    # P3 — scroll state read without derivedStateOf
    if echo "$line_text" | grep -qP '\.(firstVisibleItemIndex|firstVisibleItemScrollOffset)\b'; then
      if ! echo "$line_text" | grep -qP 'derivedStateOf\s*\{'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "P3" "ADVISORY" "scroll state read without derivedStateOf {} — wrap to avoid recomposition on every scroll pixel" "$snippet"
      fi
    fi

    # ── Lifecycle / coroutines — ADVISORY (LC1–LC3) ──────────────────────────

    # LC1 — GlobalScope.launch / GlobalScope.async
    if echo "$line_text" | grep -qP '\bGlobalScope\.(launch|async)\b'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "LC1" "ADVISORY" "GlobalScope.launch/async ignores structured concurrency — use viewModelScope or lifecycleScope" "$snippet"
    fi

    # LC2 — runBlocking in main source (not in test files)
    if [[ "$file" != *Test* && "$file" != */test/* && "$file" != */androidTest/* ]]; then
      if echo "$line_text" | grep -qP '\brunBlocking\s*[{\(]'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "LC2" "ADVISORY" "runBlocking in production code blocks the calling thread — use suspend functions or launch instead" "$snippet"
      fi
    fi

    # LC3 — viewModelScope used outside a ViewModel file
    if [[ "$file" != *ViewModel* && "$file" != *viewmodel* ]]; then
      if echo "$line_text" | grep -qP '\bviewModelScope\.(launch|async)\b'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "LC3" "ADVISORY" "viewModelScope used outside a ViewModel — use the appropriate coroutine scope for this context" "$snippet"
      fi
    fi

    # ── Privacy / logging — ADVISORY (PRIV1–PRIV2) ──────────────────────────

    # PRIV1 — Log.* containing sensitive field names
    if echo "$line_text" | grep -qiP 'Log\.[dviwе]\(.*?(token|password|passwd|secret|passphrase|email|phone|userId|user_id|creditCard|ssn)\b'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "PRIV1" "ADVISORY" "Log.* may expose sensitive user data — strip before release or guard with BuildConfig.DEBUG (MASWE-0001)" "$snippet"
    fi

    # PRIV2 — println in production source
    if echo "$line_text" | grep -qP '\bprintln\s*\(|\bSystem\.out\.println\s*\('; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "PRIV2" "ADVISORY" "println in production code — use Log.* with BuildConfig.DEBUG guard and remove before release" "$snippet"
    fi

  done < "$file"
}

# ── Manifest scanner — SEC10 ─────────────────────────────────────────────────
# Scans app/src/main/AndroidManifest.xml for android:debuggable="true".
# Not diff-filtered — debuggable=true should never be in the main manifest
# regardless of which PR introduced it.
scan_manifest() {
  local manifest_file="app/src/main/AndroidManifest.xml"
  [[ -f "$manifest_file" ]] || return 0

  local lineno=0
  while IFS= read -r line_text; do
    lineno=$((lineno + 1))
    if echo "$line_text" | grep -qP 'android:debuggable\s*=\s*"true"'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$manifest_file" "$lineno" "SEC10" "BLOCKING" "android:debuggable=true in main manifest — remove from production build (MASWE-0067)" "$snippet"
    fi
  done < "$manifest_file"
}

# ── Run across all target files ──────────────────────────────────────────────
for f in "${TARGET_FILES[@]}"; do
  [[ -f "$f" ]] || continue
  scan_file "$f"
done

# Manifest scan always runs (not file-list driven)
scan_manifest

# ── Summary ──────────────────────────────────────────────────────────────────
TOTAL_VIOLATIONS=$((BLOCKING_VIOLATIONS + ADVISORY_VIOLATIONS))

if [[ $TOTAL_VIOLATIONS -gt 0 ]]; then
  echo ""
  echo "check-claude-md: $BLOCKING_VIOLATIONS blocking + $ADVISORY_VIOLATIONS advisory violation(s) found."
  [[ $ADVISORY_VIOLATIONS -gt 0 ]] && echo "  ADVISORY violations are informational — they do not fail CI but must be addressed before merge."
  [[ $BLOCKING_VIOLATIONS -gt 0 ]] && echo "  BLOCKING violations must be fixed before opening a PR." && exit 1
  exit 0
else
  echo "check-claude-md: all checks passed."
  exit 0
fi

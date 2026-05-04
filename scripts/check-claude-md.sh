#!/usr/bin/env bash
# check-claude-md.sh — deterministic CLAUDE.md compliance linter
#
# Usage:
#   bash scripts/check-claude-md.sh                  # scan all .kt files under app/src/main/
#   bash scripts/check-claude-md.sh --diff <BASE>    # restrict to lines changed vs BASE (e.g. origin/main)
#   bash scripts/check-claude-md.sh <file> [<file>…] # scan specific files
#
# Exit codes: 0 = clean, 1 = violations found
# Output format: <file>:<line> [<ID>] <description> — `<snippet>`
#
# Rules: G1 (flat bg, single + multi-line), G2 (containerColor), G3 (text alpha),
#        G4 (icon tint), G5 (color/tint param threaded without Color.White),
#        A1 (touch target < 48dp), S2 (separator), ARCH1 (ViewModel view.*), ARCH2 (collectAsState)

set -euo pipefail

VIOLATIONS=0
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
    elif [[ "$raw" =~ ^@@\ -[0-9]+[,0-9]*\ \+([0-9]+)[,]?([0-9]*)\ @@  ]]; then
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
  local file="$1" line="$2" id="$3" desc="$4" snippet="$5"
  printf '%s:%s [%s] %s — `%s`\n' "$file" "$line" "$id" "$desc" "$snippet"
  VIOLATIONS=$((VIOLATIONS + 1))
}

# ── Rule scanners ────────────────────────────────────────────────────────────

scan_file() {
  local file="$1"
  local lineno=0

  while IFS= read -r line_text; do
    lineno=$((lineno + 1))
    is_in_diff "$file" "$lineno" || continue

    # G1 — flat solid background: .background(Color...)  without Brush. on the same line
    if echo "$line_text" | grep -qP '\.background\(\s*Color[\.\(]'; then
      if ! echo "$line_text" | grep -qP 'Brush\.'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "G1" "flat solid background — use Brush.linearGradient/verticalGradient" "$snippet"
      fi
    fi

    # G2 — containerColor override on Material3 component
    if echo "$line_text" | grep -qP 'containerColor\s*='; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "G2" "containerColor override — flat solid colour, forbidden" "$snippet"
    fi

    # G3 — Text color alpha ≠ 0.87f or 1.0f (white)
    # Matches: color = Color.White.copy(alpha = X) where X is not 0.87 and not 1.0 and not 1f
    if echo "$line_text" | grep -qP 'color\s*=\s*Color\.White\.copy\(alpha\s*=\s*'; then
      if echo "$line_text" | grep -qP 'color\s*=\s*Color\.White\.copy\(alpha\s*=\s*0\.(?!87f?\b)'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "G3" "text alpha ≠ 0.87f — must be Color.White or Color.White.copy(alpha = 0.87f)" "$snippet"
      fi
    fi

    # G4 — Icon tint not Color.White (tint with alpha or solid non-white)
    if echo "$line_text" | grep -qP 'tint\s*=\s*Color\.White\.copy\('; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "G4" "icon tint has alpha — must be tint = Color.White (no copy)" "$snippet"
    fi
    if echo "$line_text" | grep -qP 'tint\s*=\s*Color\(0x'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "G4" "icon tint is a non-white solid colour — must be tint = Color.White" "$snippet"
    fi

    # G5 — color/tint parameter threaded without a Color.White literal at call site
    # Catches `Text(... color = someVariable)` / `Icon(... tint = someVariable)` patterns
    # where a non-literal Color variable is passed in (e.g. `color = color`, `tint = iconTint`).
    # Variables start with lowercase; Color literals start with uppercase Color.
    # Informational — output says "verify caller value" rather than hard-blocking.
    if echo "$line_text" | grep -qP '\b(Text|Icon)\b'; then
      if echo "$line_text" | grep -qP '\b(color|tint)\s*=\s*[a-z][a-zA-Z0-9_.]*\b'; then
        if ! echo "$line_text" | grep -qP '\b(color|tint)\s*=\s*Color\.'; then
          snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
          report "$file" "$lineno" "G5" "color/tint parameter threaded without Color.White literal — verify caller value or hardcode Color.White" "$snippet"
        fi
      fi
    fi

    # A1 — Touch target < 48 dp on IconButton
    if echo "$line_text" | grep -qP 'IconButton\('; then
      if echo "$line_text" | grep -qP 'Modifier\.size\(([0-3]?[0-9]|4[0-7])\.dp\)'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "A1" "touch target < 48 dp — minimum is Modifier.size(48.dp)" "$snippet"
      fi
    fi

    # S2 — Single-char hardcoded UI separators in Text(...)
    if echo "$line_text" | grep -qP 'Text\(\s*"[:/·–→]"'; then
      snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
      report "$file" "$lineno" "S2" "hardcoded single-char separator — move to strings.xml" "$snippet"
    fi

    # ARCH1 — ViewModel imports android.view.*
    if [[ "$file" == *ViewModel.kt ]]; then
      if echo "$line_text" | grep -qP '^import android\.view\.'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "ARCH1" "ViewModel must not import android.view.*" "$snippet"
      fi
    fi

    # ARCH2 — UI layer uses plain collectAsState() instead of collectAsStateWithLifecycle()
    if echo "$line_text" | grep -qP '\.collectAsState\(\)(?!WithLifecycle)'; then
      # Make sure it's not collectAsStateWithLifecycle on same line
      if ! echo "$line_text" | grep -qP 'collectAsStateWithLifecycle'; then
        snippet=$(echo "$line_text" | sed 's/^[[:space:]]*//')
        report "$file" "$lineno" "ARCH2" "use collectAsStateWithLifecycle() — not plain collectAsState()" "$snippet"
      fi
    fi

  done < "$file"
}

# ── G1 multi-line scanner ────────────────────────────────────────────────────
# The line-by-line scan_file only catches .background(Color...) on one line.
# This function catches the split form:
#   .background(
#       Color(0xFF...)     <- Color on a continuation line
#   )
# It scans up to 6 continuation lines after each .background( opener.
scan_file_multiline() {
  local file="$1"
  local start_lines
  start_lines=$(awk '
    /\.background\(/ && !/Brush\./ {
      start = NR
      buf = $0
      for (i = 1; i <= 6; i++) {
        if ((getline nextline) <= 0) break
        if (nextline ~ /Brush\./) { buf = ""; break }
        buf = buf "\n" nextline
        if (nextline ~ /^[[:space:]]*\)/) {
          if (buf ~ /Color[\.\(]/ && buf !~ /Brush\./) print start
          buf = ""
          break
        }
      }
    }
  ' "$file")

  while IFS= read -r start_line; do
    [[ -z "$start_line" ]] && continue
    is_in_diff "$file" "$start_line" || continue
    local snippet
    snippet=$(sed -n "${start_line}p" "$file" | sed 's/^[[:space:]]*//')
    report "$file" "$start_line" "G1" "multi-line flat .background(...) — use Brush.linearGradient/verticalGradient" "$snippet"
  done <<< "$start_lines"
}

# ── Run across all target files ───────────────────────────────────────────────
for f in "${TARGET_FILES[@]}"; do
  [[ -f "$f" ]] || continue
  scan_file "$f"
  scan_file_multiline "$f"
done

# ── Summary ───────────────────────────────────────────────────────────────────
if [[ $VIOLATIONS -gt 0 ]]; then
  echo ""
  echo "check-claude-md: $VIOLATIONS violation(s) found — fix all before committing."
  exit 1
else
  echo "check-claude-md: all checks passed."
  exit 0
fi

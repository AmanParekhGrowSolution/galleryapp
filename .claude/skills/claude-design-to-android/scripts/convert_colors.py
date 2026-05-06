#!/usr/bin/env python3
"""
convert_colors.py — Scan a Claude Design bundle for color literals and emit a
draft `Color.kt` for Jetpack Compose.

Usage:
    python3 convert_colors.py <path-to-design-bundle>
    python3 convert_colors.py <path-to-bundle> --output Color.kt

What it finds:
- rgb(R, G, B)
- rgba(R, G, B, A)
- #RGB shorthand
- #RRGGBB
- #RRGGBBAA
- Color literals inside named token objects (const T = { primary: '...' })
- CSS custom properties (--brand-blue: #0066FF;)

What it doesn't do:
- Resolve var(--xxx) references (it lists them so you can map them manually).
- Pretty names for inline literals — those get auto-named like "Color1", "Color2".
  Rename them in the output.

The output is a draft. Review the names before checking it in.
"""

import argparse
import re
import sys
from pathlib import Path
from collections import OrderedDict


COLOR_PATTERNS = [
    # rgb(37, 100, 207) or rgb(37,100,207)
    (re.compile(r"rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)"), "rgb"),
    # rgba(37, 100, 207, 0.5)
    (re.compile(r"rgba\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*([\d.]+)\s*\)"), "rgba"),
    # #RRGGBBAA (8-digit must come before 6-digit)
    (re.compile(r"#([0-9A-Fa-f]{8})\b"), "hex8"),
    # #RRGGBB
    (re.compile(r"#([0-9A-Fa-f]{6})\b"), "hex6"),
    # #RGB shorthand
    (re.compile(r"#([0-9A-Fa-f]{3})\b"), "hex3"),
]

# Token-object pattern: capture key + color value
# matches:  primary: 'rgb(37,100,207)',  or  primary: "#0066FF",
TOKEN_LINE = re.compile(
    r"""(?P<key>[A-Za-z_][A-Za-z0-9_]*)\s*:\s*['"](?P<value>[^'"]+)['"]"""
)

# CSS custom property:  --brand-blue: #0066FF;
CSS_VAR = re.compile(
    r"""--(?P<key>[A-Za-z_-][A-Za-z0-9_-]*)\s*:\s*(?P<value>[^;]+);"""
)

# var(--xxx) references — we list these for manual mapping
VAR_REF = re.compile(r"var\(\s*--([A-Za-z_-][A-Za-z0-9_-]*)\s*\)")

VALID_FILE_EXTS = {".jsx", ".tsx", ".js", ".ts", ".html", ".css", ".scss"}


def to_hex(color_str):
    """
    Convert any color string to a normalized 8-digit hex (RRGGBBAA) for
    deduplication, plus the Compose Color(...) literal.
    Returns (hex_key, kotlin_literal) or (None, None) if unparseable.
    """
    s = color_str.strip()

    for pattern, kind in COLOR_PATTERNS:
        m = pattern.fullmatch(s) or pattern.search(s)
        if not m:
            continue

        if kind == "rgb":
            r, g, b = int(m.group(1)), int(m.group(2)), int(m.group(3))
            if any(v > 255 for v in (r, g, b)):
                continue
            return _format_color(r, g, b, 1.0)

        if kind == "rgba":
            r, g, b, a = int(m.group(1)), int(m.group(2)), int(m.group(3)), float(m.group(4))
            if any(v > 255 for v in (r, g, b)) or not (0 <= a <= 1):
                continue
            return _format_color(r, g, b, a)

        if kind == "hex8":
            hex_val = m.group(1).upper()
            r = int(hex_val[0:2], 16)
            g = int(hex_val[2:4], 16)
            b = int(hex_val[4:6], 16)
            a = int(hex_val[6:8], 16) / 255.0
            return _format_color(r, g, b, a)

        if kind == "hex6":
            hex_val = m.group(1).upper()
            r = int(hex_val[0:2], 16)
            g = int(hex_val[2:4], 16)
            b = int(hex_val[4:6], 16)
            return _format_color(r, g, b, 1.0)

        if kind == "hex3":
            hex_val = m.group(1).upper()
            r = int(hex_val[0] * 2, 16)
            g = int(hex_val[1] * 2, 16)
            b = int(hex_val[2] * 2, 16)
            return _format_color(r, g, b, 1.0)

    return None, None


def _format_color(r, g, b, a):
    """Return (hex_key, kotlin_literal). hex_key is RRGGBBAA for dedup."""
    a_byte = round(a * 255)
    hex_key = f"{r:02X}{g:02X}{b:02X}{a_byte:02X}"
    if abs(a - 1.0) < 0.005:
        # Fully opaque: simple Color(0xFFRRGGBB)
        kotlin = f"Color(0xFF{r:02X}{g:02X}{b:02X})"
    else:
        # With alpha: Color(0xFFRRGGBB).copy(alpha = X.Xf)
        kotlin = f"Color(0xFF{r:02X}{g:02X}{b:02X}).copy(alpha = {round(a, 2)}f)"
    return hex_key, kotlin


def camel_case(s):
    """brand-blue → brandBlue, my_color → myColor"""
    parts = re.split(r"[-_]", s)
    if not parts:
        return s
    return parts[0].lower() + "".join(p.capitalize() for p in parts[1:])


def pascal_case(s):
    """brand-blue → BrandBlue"""
    parts = re.split(r"[-_]", s)
    return "".join(p.capitalize() for p in parts if p)


def scan_file(path):
    """Scan a single file for color literals. Returns list of (name, kotlin, hex_key)."""
    try:
        text = path.read_text(encoding="utf-8", errors="ignore")
    except Exception as e:
        print(f"  warning: couldn't read {path}: {e}", file=sys.stderr)
        return [], []

    named = []  # (name, kotlin_literal, hex_key, source_value)
    inline = []  # (kotlin_literal, hex_key, source_value)
    var_refs = []  # raw var() references, to surface for manual mapping

    # Named tokens in JS objects: primary: 'rgb(...)'
    for m in TOKEN_LINE.finditer(text):
        key = m.group("key")
        value = m.group("value")
        hex_key, kotlin = to_hex(value)
        if hex_key:
            named.append((pascal_case(key), kotlin, hex_key, value))

    # CSS custom properties: --brand-blue: #0066FF;
    for m in CSS_VAR.finditer(text):
        key = m.group("key")
        value = m.group("value").strip()
        hex_key, kotlin = to_hex(value)
        if hex_key:
            named.append((pascal_case(key), kotlin, hex_key, value))

    # Bare colors that aren't already captured by the named patterns.
    # Strip out things we already matched to avoid double-counting.
    cleaned = TOKEN_LINE.sub(" ", text)
    cleaned = CSS_VAR.sub(" ", cleaned)
    for pattern, _kind in COLOR_PATTERNS:
        for m in pattern.finditer(cleaned):
            value = m.group(0)
            hex_key, kotlin = to_hex(value)
            if hex_key:
                inline.append((kotlin, hex_key, value))

    # var() references
    for m in VAR_REF.finditer(text):
        var_refs.append(m.group(1))

    return named, inline, var_refs


def main():
    parser = argparse.ArgumentParser(description="Extract design colors → Color.kt")
    parser.add_argument("bundle_path", help="Path to extracted design bundle directory")
    parser.add_argument("--output", default=None,
                        help="Output path for Color.kt (default: print to stdout)")
    parser.add_argument("--package", default="com.example.designapp.theme",
                        help="Kotlin package for the output file")
    args = parser.parse_args()

    bundle = Path(args.bundle_path)
    if not bundle.exists():
        print(f"error: {bundle} not found", file=sys.stderr)
        sys.exit(1)

    files = [p for p in bundle.rglob("*") if p.is_file() and p.suffix.lower() in VALID_FILE_EXTS]
    if not files:
        print(f"error: no .jsx/.tsx/.js/.ts/.html/.css files found under {bundle}", file=sys.stderr)
        sys.exit(1)

    print(f"Scanning {len(files)} files…", file=sys.stderr)

    # Dedupe: prefer named occurrences over inline
    by_hex = OrderedDict()  # hex_key → (name, kotlin, source_value)
    inline_anon_counter = 1
    all_var_refs = set()

    for f in files:
        named, inline, var_refs = scan_file(f)
        all_var_refs.update(var_refs)
        for name, kotlin, hex_key, source in named:
            if hex_key not in by_hex:
                by_hex[hex_key] = (name, kotlin, source)
            else:
                # Already named — keep the existing name unless current is better
                existing_name = by_hex[hex_key][0]
                if existing_name.startswith("Color") and existing_name[5:].isdigit():
                    by_hex[hex_key] = (name, kotlin, source)

        for kotlin, hex_key, source in inline:
            if hex_key not in by_hex:
                anon_name = f"Color{inline_anon_counter}"
                inline_anon_counter += 1
                by_hex[hex_key] = (anon_name, kotlin, source)

    # Generate Kotlin
    lines = []
    lines.append(f"package {args.package}")
    lines.append("")
    lines.append("import androidx.compose.ui.graphics.Color")
    lines.append("")
    lines.append("// Auto-generated from design bundle by convert_colors.py.")
    lines.append("// Review the names — anonymous inline colors are named ColorN; rename them.")
    lines.append("")

    seen_names = set()
    # Reserve names that would shadow Compose imports
    RESERVED = {"Color", "TextStyle", "Modifier", "Brush"}
    for hex_key, (name, kotlin, source) in by_hex.items():
        # Avoid collisions with Compose type names
        if name in RESERVED:
            name = name + "Token"
        # Disambiguate duplicate names
        unique_name = name
        suffix = 2
        while unique_name in seen_names:
            unique_name = f"{name}{suffix}"
            suffix += 1
        seen_names.add(unique_name)

        lines.append(f"val {unique_name} = {kotlin} // from: {source}")

    if all_var_refs:
        lines.append("")
        lines.append("// CSS var() references found in the bundle — these reference")
        lines.append("// custom properties. If any aren't already defined above, map them manually:")
        for v in sorted(all_var_refs):
            kotlin_name = pascal_case(v)
            if kotlin_name not in seen_names:
                lines.append(f"// val {kotlin_name} = ??? // from: var(--{v})")

    output = "\n".join(lines) + "\n"

    if args.output:
        Path(args.output).write_text(output, encoding="utf-8")
        print(f"Wrote {len(by_hex)} colors → {args.output}", file=sys.stderr)
    else:
        print(output)
        print(f"# Found {len(by_hex)} unique colors", file=sys.stderr)


if __name__ == "__main__":
    main()

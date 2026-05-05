# Bundler HTML Format Reference

Self-unpacking HTML files produced by design-to-code tools (e.g. Claude design export). Understanding this format is required by the BUNDLER DECODE step in the skill.

## Structure

A bundler HTML file is ~180 lines of shell HTML plus two very large single-line `<script>` payloads:

```
HTML shell (lines 1–~175)
  └─ thumbnail SVG             ← decorative, NOT the real UI
  └─ DOMContentLoaded unpacker script  ← runtime decoder, irrelevant to us

<script type="__bundler/manifest">...</script>   ← ~1.5 MB JSON
<script type="__bundler/template">...</script>   ← ~20 KB JSON-encoded string
```

## Manifest (`__bundler/manifest`)

A flat JSON object. Keys are UUIDv4 strings; values are asset entries:

```json
{
  "63401937-2b8b-4d54-93ea-44b3e0eb58ad": {
    "mime": "text/jsx",
    "compressed": true,
    "data": "H4sIAAAAA..."
  }
}
```

- `mime` — one of: `text/jsx`, `text/javascript`, `application/javascript`, `font/woff2`, `image/svg+xml`, `image/png`
- `compressed` — `true` means the decoded base64 is gzip-compressed
- `data` — base64-encoded payload (decode → optionally gunzip → raw asset bytes)

Typical GalleryApp manifest contents:
- 2× `text/jsx` — JSX helper/utility files (tweaks panel, design canvas wrapper)
- 7× `application/javascript` — **screen component source** (labeled `*.jsx` in their file headers, just stored under a different MIME type by the bundler — these ARE the UI to replicate)
- 3× `text/javascript` — vendor bundles (React, Babel runtime, etc.) — skip for design extraction
- 7× `font/woff2` — Inter font weights (400/600/700/800) — note family/weight, skip bytes

**Critical distinction:** `application/javascript` entries contain readable JSX component source (check the file header comment — e.g. `// components.jsx`). `text/javascript` entries are minified vendor bundles. Use `manifest.json` to distinguish before reading any `.js` file.

## Template (`__bundler/template`)

A JSON-encoded **string** (not an object). JSON.parse yields a complete HTML document string:

```html
<!DOCTYPE html>
<html>
<head>
  <style>
    :root {
      --brand-blue: #0066FF;
      --brand-blue-soft: #AAD5FF;
      /* ... full design token set ... */
    }
    @font-face { font-family: 'Inter'; src: url("<uuid>"); ... }
  </style>
</head>
<body>
  <div id="root"></div>
  <script type="text/babel" src="<uuid>"></script>  ← references manifest UUIDs
  <!-- 9 such script tags, one per JSX screen -->
</body>
</html>
```

The `<style>` block contains **all design tokens** as CSS custom properties — these are exact, machine-readable values. The runtime would rewrite `url("<uuid>")` references to blob URLs; the decoder doesn't need to.

## Design extraction strategy

| What you need | Where to find it | How |
|---|---|---|
| Color palette | `template.html` `<style>` block | Grep for `--` CSS vars |
| Type scale | `template.html` `<style>` block | Grep for `--fs-` |
| Spacing system | `template.html` `<style>` block | Grep for `--space-` |
| Border radii | `template.html` `<style>` block | Grep for `--radius-` |
| Font family/weight | `template.html` `@font-face` rules | Read `font-family` / `font-weight` |
| Screen structure + components | `assets/<uuid>.jsx` files | Read as JSX source |
| Screen names | JSX `export default function <Name>` / component names | Read JSX |

## Detection heuristic

A file is a bundler HTML if it contains:
```
<script type="__bundler/manifest">
```
If this string is absent, treat the file as plain HTML and read it directly.

## Decoder script

`scripts/decode-bundler.mjs` in this skill handles all of the above automatically.
Run it once; then read `template.html` and `assets/*.jsx` with normal Read/Grep tools.

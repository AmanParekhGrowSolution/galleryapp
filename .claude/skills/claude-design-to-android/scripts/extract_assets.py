#!/usr/bin/env python3
"""
extract_assets.py — Find image assets referenced in a Claude Design bundle and
copy them into the Android resource folder structure with valid resource names.

Why this exists:
    Claude Design handoff bundles ship logos, photos, illustrations, and custom
    icons alongside the JSX. They're useless to an Android app sitting in the
    bundle's `assets/` folder — they need to live in `app/src/main/res/drawable*/`
    with Android-legal names (lowercase letters, digits, underscores; can't
    start with a digit).

What it does:
    1. Walks the bundle for image files (.png, .jpg, .jpeg, .svg, .webp, .gif)
    2. Greps the bundle's JSX/HTML/CSS for filenames to find which images are
       *actually referenced* (vs. which are state screenshots in screenshots/)
    3. Copies referenced images to the right res/ folder (drawable-nodpi for
       bitmaps; drawable for SVGs converted to vector drawables)
    4. Renames to Android resource conventions
    5. Writes assets-manifest.md so the user can audit the import
    6. Prints a Compose snippet for each asset so it's easy to wire up

Usage:
    python3 extract_assets.py <bundle-path> --output-dir <android-output>/res
    python3 extract_assets.py <bundle-path> --output-dir ./res --manifest manifest.md
    python3 extract_assets.py <bundle-path> --check  # report only, don't copy
"""

import argparse
import re
import shutil
import sys
from pathlib import Path

IMAGE_EXTS = {".png", ".jpg", ".jpeg", ".webp", ".gif", ".svg"}
SOURCE_EXTS = {".jsx", ".tsx", ".js", ".ts", ".html", ".htm", ".css", ".scss"}

# Folders whose contents are presumed to be *only* preview screenshots, never
# referenced as real assets — even if grep happens to find a name match.
SCREENSHOT_FOLDERS = {"screenshots", "previews", "states", "captures"}

# Match any string that looks like an image filename inside source files.
# Examples that match:
#   src="logo.png"
#   "/assets/hero.jpg"
#   url('img/bg.webp')
#   import logo from './logo.svg'
#   src="avatar@2x.png"   (density marker)
IMAGE_REF_PATTERN = re.compile(
    r"""[\"'`(/=]([A-Za-z0-9_@./-]+\.(?:png|jpg|jpeg|webp|gif|svg))(?=[\"')\s>])""",
    re.IGNORECASE,
)


def is_in_screenshot_folder(path: Path, bundle_root: Path) -> bool:
    """Check if this file lives under a screenshots/previews/states/ folder."""
    try:
        rel = path.relative_to(bundle_root)
    except ValueError:
        return False
    return any(part.lower() in SCREENSHOT_FOLDERS for part in rel.parts)


def collect_referenced_filenames(bundle_root: Path) -> set:
    """Grep all JSX/HTML/CSS for image filenames. Returns a set of basenames."""
    referenced = set()
    for path in bundle_root.rglob("*"):
        if not path.is_file() or path.suffix.lower() not in SOURCE_EXTS:
            continue
        try:
            text = path.read_text(encoding="utf-8", errors="ignore")
        except Exception:
            continue
        for m in IMAGE_REF_PATTERN.finditer(text):
            full_ref = m.group(1)
            # Just the basename — the path part is brittle (relative to where
            # the JSX lives) and we'll re-resolve via the actual filesystem.
            basename = Path(full_ref).name.lower()
            referenced.add(basename)
    return referenced


def collect_image_files(bundle_root: Path) -> list:
    """Walk for every image file in the bundle."""
    images = []
    for path in bundle_root.rglob("*"):
        if path.is_file() and path.suffix.lower() in IMAGE_EXTS:
            images.append(path)
    return images


def to_resource_name(filename: str) -> str:
    """
    Convert a filename to an Android resource name:
      - lowercase
      - alphanumerics and underscore only
      - hyphens, spaces, periods (other than ext) → underscore
      - drop @2x / @3x density suffixes (Android uses density folders)
      - if name starts with digit, prefix with 'img_'
    """
    p = Path(filename)
    stem = p.stem.lower()
    ext = p.suffix.lower()

    # Drop @2x / @3x / @1.5x density markers — Android uses drawable-xhdpi/ etc.
    stem = re.sub(r"@\d+(?:\.\d+)?x", "", stem)

    # Replace hyphens, spaces, dots, anything not [a-z0-9_] with underscore
    stem = re.sub(r"[^a-z0-9_]", "_", stem)

    # Collapse multiple underscores
    stem = re.sub(r"_+", "_", stem).strip("_")

    # Can't start with a digit
    if stem and stem[0].isdigit():
        stem = "img_" + stem

    # Empty after sanitizing? Fall back.
    if not stem:
        stem = "asset"

    return stem + ext


def choose_target_folder(image_path: Path) -> str:
    """
    Decide which res/ subfolder this image goes into.
      - .svg → drawable/  (converts to vector drawable; note in manifest)
      - .png/.jpg/etc → drawable-nodpi/  (don't let Android rescale)
    """
    if image_path.suffix.lower() == ".svg":
        return "drawable"
    return "drawable-nodpi"


def compose_usage_snippet(resource_name_no_ext: str, ext: str) -> str:
    """Return a Compose snippet showing how to reference this asset."""
    if ext == ".svg":
        # Vector drawable — usually used as Icon
        return (
            f"Icon(painterResource(R.drawable.{resource_name_no_ext}), "
            f"contentDescription = null)"
        )
    return (
        f"Image(painterResource(R.drawable.{resource_name_no_ext}), "
        f"contentDescription = null, contentScale = ContentScale.Crop)"
    )


def main():
    parser = argparse.ArgumentParser(
        description="Extract referenced image assets from a Claude Design bundle "
                    "into Android res/ folders."
    )
    parser.add_argument("bundle_path", help="Path to extracted design bundle")
    parser.add_argument("--output-dir", default=None,
                        help="Path to Android res/ folder (e.g. android-output/res). "
                             "Required unless --check is passed.")
    parser.add_argument("--manifest", default=None,
                        help="Where to write assets-manifest.md "
                             "(default: <output-dir>/../assets-manifest.md)")
    parser.add_argument("--check", action="store_true",
                        help="Report only — don't copy anything. Useful for "
                             "verifying an existing import.")
    parser.add_argument("--include-unreferenced", action="store_true",
                        help="Copy ALL images, including ones that don't appear "
                             "to be referenced. Use if grep is missing dynamic refs.")
    args = parser.parse_args()

    bundle = Path(args.bundle_path)
    if not bundle.exists():
        print(f"error: bundle path {bundle} not found", file=sys.stderr)
        sys.exit(1)

    if not args.check and not args.output_dir:
        print("error: --output-dir is required (unless --check is passed)", file=sys.stderr)
        sys.exit(1)

    print(f"Scanning {bundle} for assets…", file=sys.stderr)

    all_images = collect_image_files(bundle)
    referenced_names = collect_referenced_filenames(bundle)

    print(f"  Found {len(all_images)} image files", file=sys.stderr)
    print(f"  Found {len(referenced_names)} unique filenames referenced in source", file=sys.stderr)

    # Classify each image
    to_copy = []           # (src_path, target_folder, target_name, reason)
    skipped_screenshot = []  # path
    skipped_unreferenced = []  # path

    for img in all_images:
        in_screenshots = is_in_screenshot_folder(img, bundle)
        is_referenced = img.name.lower() in referenced_names

        if in_screenshots and not is_referenced:
            skipped_screenshot.append(img)
            continue

        if not is_referenced and not args.include_unreferenced:
            skipped_unreferenced.append(img)
            continue

        target_folder = choose_target_folder(img)
        target_name = to_resource_name(img.name)
        to_copy.append((img, target_folder, target_name,
                        "referenced" if is_referenced else "fallback"))

    # Disambiguate target names if two source files map to the same one
    seen_names = {}
    final_copy_list = []
    for src, folder, name, reason in to_copy:
        key = (folder, name)
        if key in seen_names:
            stem = Path(name).stem
            ext = Path(name).suffix
            counter = 2
            while (folder, f"{stem}_{counter}{ext}") in seen_names:
                counter += 1
            name = f"{stem}_{counter}{ext}"
            key = (folder, name)
        seen_names[key] = src
        final_copy_list.append((src, folder, name, reason))

    # Print the plan
    print("\n=== Asset extraction plan ===", file=sys.stderr)
    print(f"  Will copy:        {len(final_copy_list)} file(s)", file=sys.stderr)
    print(f"  Skipped (screenshot folder): {len(skipped_screenshot)}", file=sys.stderr)
    print(f"  Skipped (not referenced):    {len(skipped_unreferenced)}", file=sys.stderr)

    if args.check:
        print("\nCheck mode — not copying. Plan:", file=sys.stderr)
        for src, folder, name, reason in final_copy_list:
            print(f"  {src.relative_to(bundle)}  →  res/{folder}/{name}  ({reason})")
        if skipped_unreferenced and not args.include_unreferenced:
            print("\nFiles that LOOK like assets but weren't referenced "
                  "(re-run with --include-unreferenced if these should ship):", file=sys.stderr)
            for s in skipped_unreferenced:
                print(f"  {s.relative_to(bundle)}", file=sys.stderr)
        sys.exit(0)

    # Do the copy
    out_root = Path(args.output_dir)
    manifest_path = Path(args.manifest) if args.manifest else out_root.parent / "assets-manifest.md"

    manifest_lines = [
        "# Asset Manifest",
        "",
        f"Generated by `extract_assets.py` from `{bundle}`.",
        "",
        f"- Files copied: **{len(final_copy_list)}**",
        f"- Skipped (in screenshots/previews folders): {len(skipped_screenshot)}",
        f"- Skipped (no JSX reference): {len(skipped_unreferenced)}",
        "",
        "## Imported assets",
        "",
        "| Original | Android resource | Compose usage |",
        "|---|---|---|",
    ]

    copied_count = 0
    for src, folder, name, _reason in final_copy_list:
        target_dir = out_root / folder
        target_dir.mkdir(parents=True, exist_ok=True)
        target_path = target_dir / name

        # SVGs need conversion to be used as resources — flag, don't blindly copy
        if src.suffix.lower() == ".svg":
            # Copy the SVG to a sidecar so the user can convert it via Android
            # Studio's Vector Asset tool. The Kotlin code references the eventual
            # XML name, but the user has to do the conversion step.
            target_path = target_dir / (Path(name).stem + ".svg")
            note = " ⚠️ **convert to vector drawable** (Android Studio → File → New → Vector Asset)"
            xml_name = Path(name).stem + ".xml"
            usage = compose_usage_snippet(Path(xml_name).stem, ".svg")
        else:
            note = ""
            usage = compose_usage_snippet(Path(name).stem, src.suffix.lower())

        shutil.copy2(src, target_path)
        copied_count += 1

        try:
            origin = src.relative_to(bundle)
        except ValueError:
            origin = src

        # In the manifest, show the eventual resource path (the .xml name for SVGs)
        if src.suffix.lower() == ".svg":
            manifest_resource = f"res/{folder}/{Path(name).stem}.xml{note}"
        else:
            manifest_resource = f"res/{folder}/{name}"

        manifest_lines.append(
            f"| `{origin}` | `{manifest_resource}` | `{usage}` |"
        )

    if skipped_unreferenced:
        manifest_lines.extend([
            "",
            "## Skipped (no JSX reference found)",
            "",
            "These images exist in the bundle but no JSX/HTML/CSS file references them. "
            "If any of these are actually used (e.g. dynamic imports the regex missed), "
            "re-run with `--include-unreferenced`.",
            "",
        ])
        for s in skipped_unreferenced:
            try:
                manifest_lines.append(f"- `{s.relative_to(bundle)}`")
            except ValueError:
                manifest_lines.append(f"- `{s}`")

    manifest_path.parent.mkdir(parents=True, exist_ok=True)
    manifest_path.write_text("\n".join(manifest_lines) + "\n", encoding="utf-8")

    print(f"\n✓ Copied {copied_count} asset(s) to {out_root}", file=sys.stderr)
    print(f"✓ Manifest: {manifest_path}", file=sys.stderr)

    # Surface the SVG warning if any
    svg_count = sum(1 for s, *_ in final_copy_list if s.suffix.lower() == ".svg")
    if svg_count:
        print(
            f"\n⚠ {svg_count} SVG(s) were copied as-is. They need conversion to vector "
            f"drawable XML — open Android Studio → File → New → Vector Asset, "
            f"or convert programmatically with `s2v` / `vd-tool`.",
            file=sys.stderr,
        )


if __name__ == "__main__":
    main()

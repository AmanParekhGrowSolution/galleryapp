#!/usr/bin/env node
// Decodes a self-unpacking "bundler" HTML file into a directory of plain files.
// The bundler format embeds two <script type="__bundler/*"> tags:
//   - manifest: JSON map of UUID → { mime, compressed, data (base64+optional-gzip) }
//   - template: JSON-encoded string containing the full design HTML document
//
// Usage: node decode-bundler.mjs <input.html> <output-dir>
// Output:
//   <output-dir>/template.html      — unwrapped design document (CSS vars, JSX refs)
//   <output-dir>/assets/<uuid>.<ext> — every asset, gunzipped if needed
//   <output-dir>/manifest.json       — pruned map (uuid → { filename, mime })

import { readFileSync, writeFileSync, mkdirSync } from 'fs';
import { gunzipSync } from 'zlib';
import { join } from 'path';

const [, , inputHtml, outputDir] = process.argv;

if (!inputHtml || !outputDir) {
  console.error('Usage: node decode-bundler.mjs <input.html> <output-dir>');
  process.exit(1);
}

// --- Read ---
let html;
try {
  html = readFileSync(inputHtml, 'utf8');
} catch (e) {
  console.error(`Error: cannot read "${inputHtml}": ${e.message}`);
  process.exit(1);
}

// --- Extract script tag payloads ---
const manifestMatch = html.match(/<script\s+type="__bundler\/manifest">([\s\S]*?)<\/script>/);
const templateMatch  = html.match(/<script\s+type="__bundler\/template">([\s\S]*?)<\/script>/);

if (!manifestMatch) {
  console.error('Error: <script type="__bundler/manifest"> not found — not a bundler HTML file.');
  process.exit(1);
}
if (!templateMatch) {
  console.error('Error: <script type="__bundler/template"> not found.');
  process.exit(1);
}

// --- Parse ---
let manifest, template;
try {
  manifest = JSON.parse(manifestMatch[1]);
} catch (e) {
  console.error('Error: manifest JSON parse failed:', e.message);
  process.exit(1);
}
try {
  template = JSON.parse(templateMatch[1]); // string → full HTML document
} catch (e) {
  console.error('Error: template JSON parse failed:', e.message);
  process.exit(1);
}

// --- Create output dirs ---
try {
  mkdirSync(join(outputDir, 'assets'), { recursive: true });
} catch (e) {
  console.error(`Error: cannot create output directory "${outputDir}": ${e.message}`);
  process.exit(1);
}

// --- Write template ---
writeFileSync(join(outputDir, 'template.html'), template, 'utf8');

// --- MIME → extension table ---
const MIME_TO_EXT = {
  'text/javascript':       '.js',
  'application/javascript':'.js',
  'text/jsx':              '.jsx',
  'text/css':              '.css',
  'font/woff2':            '.woff2',
  'font/woff':             '.woff',
  'font/ttf':              '.ttf',
  'image/svg+xml':         '.svg',
  'image/png':             '.png',
  'image/jpeg':            '.jpg',
  'image/gif':             '.gif',
  'image/webp':            '.webp',
  'text/html':             '.html',
  'application/json':      '.json',
};

// --- Decode assets ---
const prunedManifest = {};
const counts = {};
let totalBytes = 0;
const errors = [];

for (const [uuid, entry] of Object.entries(manifest)) {
  const ext = MIME_TO_EXT[entry.mime] ?? '.bin';
  const filename = `${uuid}${ext}`;

  let decoded = Buffer.from(entry.data, 'base64');
  if (entry.compressed) {
    try {
      decoded = gunzipSync(decoded);
    } catch (e) {
      errors.push(`Warning: failed to gunzip ${uuid} (${entry.mime}): ${e.message}`);
    }
  }

  writeFileSync(join(outputDir, 'assets', filename), decoded);
  totalBytes += decoded.length;

  prunedManifest[uuid] = { filename, mime: entry.mime };
  counts[entry.mime] = (counts[entry.mime] ?? 0) + 1;
}

// --- Write pruned manifest ---
writeFileSync(join(outputDir, 'manifest.json'), JSON.stringify(prunedManifest, null, 2), 'utf8');

// --- Print summary ---
if (errors.length) errors.forEach(w => console.warn(w));

const countStr = Object.entries(counts)
  .map(([mime, n]) => `${n}× ${mime}`)
  .join(', ');
const kb = (totalBytes / 1024).toFixed(1);
const total = Object.keys(manifest).length;

console.log(`OK — decoded ${total} assets (${countStr})`);
console.log(`     ${kb} KB written to: ${outputDir}`);
console.log(`     template.html + assets/ + manifest.json`);

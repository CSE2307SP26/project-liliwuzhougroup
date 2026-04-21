#!/bin/bash
set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUT_DIR="$ROOT_DIR/out/app"

mkdir -p "$OUT_DIR"
javac -d "$OUT_DIR" "$ROOT_DIR"/src/main/*.java
java -cp "$OUT_DIR" main.MainMenu

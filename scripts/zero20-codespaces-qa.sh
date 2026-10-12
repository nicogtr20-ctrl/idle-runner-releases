#!/usr/bin/env bash
# ZERO 2.0 local QA inside GitHub Codespaces, without GitHub Actions minutes.
# This replays the tested workflow's Java compilation, regression tests and
# read-only shadow invariants. It does not publish, sign or install an APK.
set -euo pipefail

cd "$(dirname "$0")/.."
for tool in java javac python3; do
  if ! command -v "$tool" >/dev/null 2>&1; then
    printf 'ERROR: Missing %s. Configure JDK 17 and Python 3 first.\n' "$tool" >&2
    exit 2
  fi
done

echo "ZERO 2.0 offline-from-Actions QA — active branch: $(git branch --show-current)"
if [[ "$(git branch --show-current)" != "feature/zero-20-hierarchical-intelligence" ]]; then
  echo "ERROR: wrong branch. Do not run ZERO 2.0 tests on a release branch." >&2
  exit 3
fi

python3 - "$@" <<'PY'
from pathlib import Path
import os
import subprocess
import sys

workflow = Path(".github/workflows/zero-20-shadow-qa.yml")
text = workflow.read_text(encoding="utf-8")

steps = (
    "Compile real prior learning systems and ZERO 2.0 model",
    "Verify shadow model, old physics, cognition and exact scoped resets",
    "Ensure new module cannot override score or physics",
)

def extract_step(name):
    marker = "      - name: " + name + "\n        run: |\n"
    if text.count(marker) != 1:
        raise SystemExit("ERROR: expected exactly one workflow step: " + name)
    start = text.index(marker) + len(marker)
    next_step = text.find("\n      - name: ", start)
    raw = text[start:next_step if next_step >= 0 else len(text)]
    lines = raw.splitlines()
    if any(line.strip() and not line.startswith("          ") for line in lines):
        raise SystemExit("ERROR: unexpected YAML indentation in " + name)
    return "\n".join(line[10:] for line in lines) + "\n"

for name in steps:
    print("\n==> " + name, flush=True)
    subprocess.run(
        ["bash", "-c", "set -euo pipefail\n" + extract_step(name)],
        check=True,
        cwd=os.getcwd(),
    )

print("\nPASS: Java regression + shadow invariants finished (no Android build yet).", flush=True)

if len(sys.argv) > 1:
    if sys.argv[1:] != ["--android"]:
        raise SystemExit("Usage: bash scripts/zero20-codespaces-qa.sh [--android]")
    if not os.environ.get("ANDROID_HOME") and not os.environ.get("ANDROID_SDK_ROOT"):
        raise SystemExit("Android SDK is not configured. Java QA passed; Android build skipped.")
    if not subprocess.run(["bash", "-c", "command -v gradle >/dev/null"], check=False).returncode == 0:
        raise SystemExit("Gradle not installed. Java QA passed; Android build skipped.")
    print("\n==> Assemble unsigned/debug APK ONLY (no release or OTA)", flush=True)
    subprocess.run(["gradle", "--no-daemon", "assembleDebug"], check=True)

print("\nNo OTA, signing, profile or save-slot writes were performed.")
PY

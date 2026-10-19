#!/usr/bin/env bash
set -euo pipefail
mkdir -p /tmp/zero362-qa
# Preserve ALL previously certified real original-app V361 tests.
bash tools/zero361_android_ui.sh
# The upstream script reinstalls, runs and force-stops the original Android app.
# Keep the newly born profile in its real, isolated instrumented test context.
adb shell pm clear com.overmind.zero.ota
adb shell am instrument -w -r -e class com.overmind.zero.Zero362VirginLifeTest \
  com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner \
  2>&1 | tee /tmp/zero362-qa/virgin-android.txt
grep -q 'OK (2 tests)' /tmp/zero362-qa/virgin-android.txt
echo "ZERO362_REAL_ORIGINAL_NEW_GAME_ZERO_EXPERIENCE_FULL_FUEL_RESET_SAVES=PASS"

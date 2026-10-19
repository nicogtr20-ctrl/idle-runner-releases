#!/usr/bin/env bash
set -euo pipefail
bash tools/zero362_android_ui.sh
adb shell pm clear com.overmind.zero.ota
adb shell am instrument -w -r -e class com.overmind.zero.Zero363TrueSpeedAndGripTest \
  com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner \
  2>&1 | tee /tmp/zero363-speed-and-real-grip.txt
grep -q 'OK (3 tests)' /tmp/zero363-speed-and-real-grip.txt
echo 'V363_REAL_ORIGINAL_ANDROID_TRACTION_V3_ONLY_GRIP_CAUSALITY_BRAIN_RESUME=PASS'

#!/usr/bin/env bash
set -euo pipefail
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
# The emulator can fail independently of ZERO's cognition. Record real
# every-200 decision physical evidence on HOST immediately, including
# loss of connection / GPU/ADB crashes, before Android can be torn down.
adb logcat -c || true
adb logcat -v threadtime 'ZERO366_REAL:I' '*:S' > /tmp/zero366-real-progress-logcat.txt 2>&1 &
capture_pid=$!
trap 'kill "$capture_pid" 2>/dev/null || true' EXIT
set +e
timeout --signal=TERM --kill-after=10s 1000s adb shell am instrument -w -r \
  -e class com.overmind.zero.Zero366OriginalAutonomousSurvival2000Test \
  com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner \
  2>&1 | tee /tmp/zero366-blind-2000-original.txt
status=${PIPESTATUS[0]}
set -e
echo "V366 ORIGINAL AUTONOMOUS PHYSICAL EVIDENCE (may be partial):"
cat /tmp/zero366-real-progress-logcat.txt || true
if [ "$status" -ne 0 ]; then
  echo "ZERO366 ORIGINAL AUTONOMOUS 2000 instrumentation exit=$status" >&2
  exit "$status"
fi
grep -F 'ZERO366_BLIND_2000_UNSCRIPTED_SURVIVAL_G3_G5_PRELIMINARY=PASS_NOT_PHONE' \
  /tmp/zero366-blind-2000-original.txt
grep -q 'OK (1 test)' /tmp/zero366-blind-2000-original.txt
echo "ZERO366_BLIND_2000_REAL_GAME_SURVIVAL_GATE=PASS"

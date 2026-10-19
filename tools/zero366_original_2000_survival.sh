#!/usr/bin/env bash
set -euo pipefail
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
set +e
timeout --signal=TERM --kill-after=10s 1000s adb shell am instrument -w -r \
  -e class com.overmind.zero.Zero366OriginalAutonomousSurvival2000Test \
  com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner \
  2>&1 | tee /tmp/zero366-blind-2000-original.txt
status=${PIPESTATUS[0]}
set -e
if [ "$status" -ne 0 ]; then
  echo "ZERO366 ORIGINAL AUTONOMOUS 2000 instrumentation exit=$status" >&2
  exit "$status"
fi
grep -F 'ZERO366_BLIND_2000_UNSCRIPTED_SURVIVAL_G3_G5_PRELIMINARY=PASS_NOT_PHONE' \
  /tmp/zero366-blind-2000-original.txt
grep -q 'OK (1 test)' /tmp/zero366-blind-2000-original.txt
echo "ZERO366_BLIND_2000_REAL_GAME_SURVIVAL_GATE=PASS"

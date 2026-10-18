#!/usr/bin/env bash
set -euo pipefail
app_package=com.overmind.zero.ota
runner=com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner
mkdir -p /tmp/zero356-ui
gradle --no-daemon assembleDebug assembleDebugAndroidTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell pm clear "$app_package"
adb logcat -c
set +e
timeout --signal=TERM --kill-after=10s 420s adb shell am instrument -w -r \
    -e class com.overmind.zero.Zero3OriginalGameV356Test \
    "$runner" 2>&1 | tee /tmp/zero356-ui/original-game-android-instrumentation.txt
status="${PIPESTATUS[0]}"
set -e
adb logcat -d -v threadtime > /tmp/zero356-ui/android-logcat.txt || true
if [ "$status" -ne 0 ] || ! grep -q 'OK (3 tests)' /tmp/zero356-ui/original-game-android-instrumentation.txt; then
  echo "V360_ORIGINAL_GAME_INTEROCEPTION_AND_GPT_SHARE_ANDROID=FAIL exit=$status"
  exit 1
fi
adb exec-out run-as "$app_package" tar -C files -cf - qa > /tmp/zero356-ui.tar
tar -xf /tmp/zero356-ui.tar -C /tmp/zero356-ui
test -s /tmp/zero356-ui/qa/zero3-v356-original-game.png
echo "V360_ORIGINAL_GAME_REAL_RED_DAMAGE_AUTO_REBIRTH_LEGACY_CHECKPOINT_GPT_SHARE=PASS"

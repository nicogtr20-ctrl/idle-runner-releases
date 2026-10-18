#!/usr/bin/env bash
set -euo pipefail
mkdir -p /tmp/zero354-ui
app_package=com.overmind.zero.ota
test_runner=com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner

if [[ "${OVERMIND_CANDIDATE_NAME:-}" == "3.0-candidate.355" ]]; then
  # Avoid a single blocking connectedAndroidTest hanging the whole emulator job
  # with 0/2 tests complete for 35 minutes; collect actionable instrumentation logs.
  gradle --no-daemon assembleDebug assembleDebugAndroidTest
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
  adb shell pm clear "$app_package"
  adb logcat -c
  status=0
  original_log=/tmp/zero354-ui/original-v355-instrumentation.log
  printf "START original fullscreen GameActivity / GRU3 QA\n" | tee "$original_log"
  timeout --signal=TERM --kill-after=10s 240s adb shell am instrument -w -r \
    -e class com.overmind.zero.Zero3OriginalGameV355Test \
    "$test_runner" 2>&1 | tee -a "$original_log" || status=$?
  adb logcat -d -v threadtime > /tmp/zero354-ui/android-logcat-original.txt || true
  if ! grep -Eq 'OK \(1 test\)' "$original_log"; then
    echo "ORIGINAL_GAME_ANDROID_QA=FAIL exit=$status" | tee -a "$original_log"
    exit 1
  fi

  adb logcat -c
  brain_log=/tmp/zero354-ui/cognition-v354-instrumentation.log
  printf "START true GRU3 cortex/brain/settings QA\n" | tee "$brain_log"
  status=0
  timeout --signal=TERM --kill-after=10s 780s adb shell am instrument -w -r \
    -e class com.overmind.zero.Zero3UiIntegrationTest \
    "$test_runner" 2>&1 | tee -a "$brain_log" || status=$?
  adb logcat -d -v threadtime > /tmp/zero354-ui/android-logcat-brain.txt || true
  if ! grep -Eq 'OK \(1 test\)' "$brain_log"; then
    echo "BRAIN_SETTINGS_ANDROID_QA=FAIL exit=$status" | tee -a "$brain_log"
    exit 1
  fi
else
  gradle --no-daemon -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true connectedDebugAndroidTest
fi

# Both runs must produce real screenshots from a live Android system, not mocks.
adb exec-out run-as "$app_package" tar -C files -cf - qa > /tmp/zero354-ui.tar
tar -tf /tmp/zero354-ui.tar > /tmp/zero354-ui/tar-files.txt
tar -xf /tmp/zero354-ui.tar -C /tmp/zero354-ui
test -s /tmp/zero354-ui/qa/zero3-brain.png
test -s /tmp/zero354-ui/qa/zero3-body.png
if [[ "${OVERMIND_CANDIDATE_NAME:-}" == "3.0-candidate.355" ]]; then
  test -s /tmp/zero354-ui/qa/zero3-original-game.png
fi
echo "ANDROID_UI_AND_ORIGINAL_GAME=PASS"

#!/usr/bin/env bash
set -euo pipefail
qa_status=0
gradle --no-daemon -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true connectedDebugAndroidTest || qa_status=$?
apk_path=app/build/outputs/apk/debug/app-debug.apk
app_package=$("$ANDROID_HOME/cmdline-tools/latest/bin/apkanalyzer" manifest application-id "$apk_path")
mkdir -p /tmp/zero354-ui
adb shell pm path "$app_package"
adb exec-out run-as "$app_package" tar -C files -cf - qa > /tmp/zero354-ui.tar
if ! tar -tf /tmp/zero354-ui.tar >/dev/null 2>&1; then cat /tmp/zero354-ui.tar; exit 1; fi
tar -xf /tmp/zero354-ui.tar -C /tmp/zero354-ui
test -s /tmp/zero354-ui/qa/zero3-brain.png
test -s /tmp/zero354-ui/qa/zero3-body.png
exit "$qa_status"

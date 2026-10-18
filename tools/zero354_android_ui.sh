#!/usr/bin/env bash
set -euo pipefail
gradle --no-daemon connectedDebugAndroidTest
apk_path=app/build/outputs/apk/debug/app-debug.apk
app_package=$("$ANDROID_HOME/cmdline-tools/latest/bin/apkanalyzer" manifest application-id "$apk_path")
mkdir -p /tmp/zero354-ui
adb exec-out run-as "$app_package" tar -C files -cf - qa > /tmp/zero354-ui.tar
tar -xf /tmp/zero354-ui.tar -C /tmp/zero354-ui
test -s /tmp/zero354-ui/qa/zero3-brain.png
test -s /tmp/zero354-ui/qa/zero3-body.png

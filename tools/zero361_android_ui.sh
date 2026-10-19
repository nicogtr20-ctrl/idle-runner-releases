#!/usr/bin/env bash
set -euo pipefail
# V361 releases must satisfy BOTH inherited real-life V356 Android QA and
# the new physical motor-owner / V354 vision-memory-migration regressions.
bash tools/zero356_android_ui.sh
adb shell am instrument -w -r -e class com.overmind.zero.Zero361MotorOwnershipTest   com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner   2>&1 | tee /tmp/zero356-ui/v361-neural-vision-motor.txt
grep -q 'OK (4 tests)' /tmp/zero356-ui/v361-neural-vision-motor.txt
echo "ZERO361_ORIGINAL_APP_CHECKPOINT_LEGACY_VISION_MOTOR_OWNER_ANDROID=PASS"

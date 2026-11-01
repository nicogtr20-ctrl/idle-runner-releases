#!/usr/bin/env bash
set -euo pipefail
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
for test in Zero390IsolatedMotorCreditAndroidTest Zero387TactileContactAndroidTest Zero387DirectMotorCalibrationAndroidTest Zero386EffortAuthorityAndroidTest Zero384EmbodiedProprioAndroidTest Zero382ColdResumeAndroidTest Zero401LocalMotorCreditSafetyAndroidTest Zero406LongRunMetricIntegrityAndroidTest Zero409PhysicalContactLedgerAndroidTest Zero410OriginalNeuralDecisionBoundaryCheckpointAndroidTest Zero414RealRearWallMotorCreditSafetyAndroidTest Zero420MotorMapEligibilityAndroidTest Zero421OriginalDeliveredLocalProbeAndroidTest Zero422LearnedHarmCandidateCoverageAndroidTest Zero425TangentActorCreditAndroidTest Zero426JointSampleActorScoreAndroidTest Zero427JointOffPolicyEligibilityAndroidTest Zero428RandomUniformActorCreditAndroidTest Zero431RearWallMuscleEligibilityAndroidTest Zero432RearWallDeliveredProbeAndroidTest Zero433InertialOpticalEvidenceAndroidTest Zero434ObservedDelayedContactAndroidTest Zero435AcceleratedDelayedContactAndroidTest Zero442DelayedBankDriftEligibilityAndroidTest Zero446HeldoutForwardAuthorityAndroidTest Zero454PairedPhysicalMotorContrastAndroidTest Zero458V371MeasuredForwardEvidenceAndroidTest
do adb shell am instrument -w -r -e class "com.overmind.zero.$test" com.overmind.zero.ota.test/androidx.test.runner.AndroidJUnitRunner 2>&1 | tee "$RUNNER_TEMP/$test.txt"
grep -q "OK (" "$RUNNER_TEMP/$test.txt"
! grep -qE "FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed" "$RUNNER_TEMP/$test.txt"
done
grep -q "OK (4 tests)" "$RUNNER_TEMP/Zero390IsolatedMotorCreditAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero401LocalMotorCreditSafetyAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero406LongRunMetricIntegrityAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero409PhysicalContactLedgerAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero410OriginalNeuralDecisionBoundaryCheckpointAndroidTest.txt"
grep -q "OK (2 tests)" "$RUNNER_TEMP/Zero414RealRearWallMotorCreditSafetyAndroidTest.txt"
grep -q "OK (9 tests)" "$RUNNER_TEMP/Zero420MotorMapEligibilityAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero421OriginalDeliveredLocalProbeAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero422LearnedHarmCandidateCoverageAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero425TangentActorCreditAndroidTest.txt"
grep -q "OK (8 tests)" "$RUNNER_TEMP/Zero426JointSampleActorScoreAndroidTest.txt"
grep -q "OK (4 tests)" "$RUNNER_TEMP/Zero427JointOffPolicyEligibilityAndroidTest.txt"
grep -q "OK (5 tests)" "$RUNNER_TEMP/Zero428RandomUniformActorCreditAndroidTest.txt"
grep -q "OK (6 tests)" "$RUNNER_TEMP/Zero431RearWallMuscleEligibilityAndroidTest.txt"
grep -q "OK (5 tests)" "$RUNNER_TEMP/Zero432RearWallDeliveredProbeAndroidTest.txt"
grep -q "OK (5 tests)" "$RUNNER_TEMP/Zero433InertialOpticalEvidenceAndroidTest.txt"
grep -q "OK (5 tests)" "$RUNNER_TEMP/Zero434ObservedDelayedContactAndroidTest.txt"
grep -q "OK (5 tests)" "$RUNNER_TEMP/Zero435AcceleratedDelayedContactAndroidTest.txt"
grep -q "OK (5 tests)" "$RUNNER_TEMP/Zero442DelayedBankDriftEligibilityAndroidTest.txt"
grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero446HeldoutForwardAuthorityAndroidTest.txt"
grep -q "OK (4 tests)" "$RUNNER_TEMP/Zero458V371MeasuredForwardEvidenceAndroidTest.txt"

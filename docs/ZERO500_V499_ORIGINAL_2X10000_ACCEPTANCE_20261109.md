# ZERO500 — V499 original GRU3 real 2×10,000-decision acceptance

**Status:** laboratory-only; no APK, no OTA, no merge, no learned neural-reset.

## Verified before ZERO500

V499 code at `lab/zero499-posture-motor-transducer-velocity-servo` commit `cec810d449794aa68e27e7448ae811a550c973a4` passed [Android four-axis original 4,747-decision body test](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/42375185911). From Android logs: EAST forward final 5.850, NORTH 6.287, WEST 0.413, SOUTH 4.967; WEST is still substantially slower. The original pre-V499 WEST response was -0.438. Four scripted-heading physical trials cannot prove unscripted autonomous navigation.

## Isolated acceptance test

This branch makes NO changes to the 170-particle solver, GRU3 actor/recurrent head, 194 sensory inputs, V319 CH2 grip, 512 original motor synapses, training-room scene, physical energy rules, thresholds, or saved V356/12 user slots.

Two existing true original GameActivity Android 10,000-decision tests accept instrumentation argument `zero499_posture_motor=true`. At their already-existing original owner boundary, they invoke the pre-existing `originalV499EnablePostureMotorTransducerForQA()` method and assert that the owner actually enabled V499. Without the argument, previous tests behave identically.

CI uses unchanged V417 strict 10×1000-window gates, real signed/gross center-of-mass travel, real blue/red material touch and learned motor updates. Two independent scenarios are mandatory: restored real 33,312-trained S21 saved brain/room, and fresh wild original brain. Each must finish >=10,000 consecutive decisions; failures are scientific evidence, not exceptions to bypass.

Other two same-SHA required gates are unchanged original Android motor regressions and actual V356 saved brain checksum/512 weights/12 named slots. The initial V499 four-axis CI success is **not** sufficient for release.

## Workflow names

- `zero500-v499-trained-s21-10000.yml`
- `zero500-v499-wild-red-blue-10000.yml`
- `zero500-v499-all-motor-regressions.yml`
- `zero500-v499-33312-v356-crc.yml`

**Promotion gate:** ALL four same-commit workflows pass unmodified assertions; only then is any real-device APK/OTA decision eligible. If either 10k behavior fails, retain the traces and correct the actual locomotion controller rather than tuning acceptance thresholds.

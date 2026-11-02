# ZERO465 — correct the original S21 blue-ceiling interpretation and isolate life/window effects

Date: 2026-11-02. Research-only branch `lab/zero465-original-life-window-causality`. Baseline V464 SHA `7f04e597cd5a3cbda604d57109380812e233469a`. V465 read-only Android test instrumentation SHA `12cf872bb4ee6499b9be42edf903d3fbd4638c9a`. No APK/OTA/merge; no changes to actor, weights, GRU3, 194 sensors, CH2 V319, physical solver, V356 CRC, 12 saves, or resource/reward rules.

## Actual completed V464 Android jobs, original user data

- Original saved S21: [run 41364545218](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41364545218), job 125021810219 **behavior FAIL** after **10002** new real decisions (33312 -> 43314). Signed **418.02942**, gross **3254.62238**, signed/gross **12.8442%**, **7/10** thousand-decision windows >=10%, **2 inverse** windows (4 and 9), **4 new blue**, **2834** original cortex weight updates, V371 verified directions **0**, V377 reliable banks **3**. Ten sampled window signed/gross ratios: 0.139795, 0.177755, 0.203037, -0.143676, 0.253762, 0.338533, 0.016663, 0.305327, -0.128537, 0.365060. Note samples may include up to a few decisions beyond exact 1000 boundaries.
- Original Android regressions: [run 41364545146](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41364545146), job 125021809972 **PASS**.
- Original 33312-decision S21 512-weight/V356 CRC: [run 41364545188](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41364545188), job 125021810159 **PASS**.
- Wild original: [run 41364545160](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41364545160), first job 125021809948 **FAIL before first decision** at `Original sole GRU3 must really finish boot`. This is not a valid 10k safety score. Same-job retry requested, new job 125032813422 in progress at last inspection. Do not infer red/blue rates until a completed valid wild run.

## Important correction to ZERO464 interpretation

V464 read-only count proves the saved S21 **initial scene** has five blue entities, one already consumed, hence four immediately ready. However, the real V464 S21 Android log reports:

`ZERO464_ORIGINAL_LIFE_RESOURCE_AUDIT initialLife=0 finalLife=4 rebirths=4 savedReadyBlue=4 collectedNewBlue=4`

Source verification: `GameActivity.Lab.resetEpisode()` increments `brain3LifeSerial` on actual physical reset, retains the learned GRU3 weights, and, when `trainingActive`, calls `trainingRoom.newEntities()`. That method constructs new `UnknownWorld.Entity` instances from the original saved **layout**, which includes all five original blue placements (the already-consumed state is only in the initial saved **scene**). Thus **after a genuine death, five blues can be ready again**. The claim that the >=5-blue hard gate is impossible over this full 10k experiment was too broad: it is only impossible for an uninterrupted continuation with no resource regeneration. The actual V464 test had FOUR physical rebirths and nevertheless collected only FOUR blues. The >=5 criterion remains intact and unpassed. No reset is forced or treated as a free pickup.

The ten V464 S21 blue increments were: 0,1,0,1,0,0,1,0,1,0. Negative movement occurred in windows 4 and 9; their temporal relationship to the four real deaths is **not yet measured**. We cannot attribute the reversals to death without the within-window life counter.

## V465 single controlled diagnostic change

Only `app/src/androidTest/java/com/overmind/zero/Zero417TrainedS21VelocityCredit10000AndroidTest.java` gains a **read-only** life-serial snapshot in the same original Android UI-thread transaction as each 1000-decision motor/material snapshot. New `ZERO465_WINDOW_LIFE_RESOURCE` records bucket, physical life serial, cumulative genuine rebirths, new actual blue pickups in that bucket, and cumulative new blue. It cannot modify the body, perception, motor output, learned values, saved state or benchmark thresholds.

The four original Android workflows were allowed on this isolated branch and the source test change was pushed at `12cf872bb4ee6499b9be42edf903d3fbd4638c9a`. Verify their actual GitHub run IDs, completed status and log outcomes before any claim. A behavior FAIL on V465 is expected unless an independent correction is later shown; this experiment is to avoid mistaking reset/resource effects for motor learning. Do not publish or combine untested behavioral changes.

## Historical V421–V423, checked against completed jobs and logs

- V421: S21 [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL, 17.1723%, 8/10, 2 inverse, 4 blue. Wild [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL, 87 red/3 blue, red 35 -> 52. Regressions 41149404217 and CRC 41149399737 PASS.
- V422: S21 41149758090 FAIL, 11.2881%, 7/10, 2 inverse, 4 blue. Wild 41149762948 FAIL, 52 red/5 blue, red 19 -> 33. Regressions 41149771615 and CRC 41149767313 PASS.
- V423: S21 41149983685 FAIL, 10.0486%, 7/10, 2 inverse, 4 blue. Wild 41149987734 FAIL, 26 red/4 blue, red 14 -> 12. Regressions 41149995431 and CRC 41149992279 PASS.

## Decision and next causal step

**NOT APPROVED.** The recurring S21 negative windows, zero V371 certified steering directions, and wild red/blue safety failures remain open. First correlate original death boundaries and physical wall/motor telemetry with the new V465 life windows, and finish the V464 wild same-code retry. Only then select **one** causally supported behavioral correction for a separate original 2x10k Android experiment. Do not relax 25%, 9/10, zero inverse, five blue, or wild red<=2x blue / decreasing late red; no V2.2, scripted headings or invented rewards.

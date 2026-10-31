# ZERO435 — verified original S21 10,001-decision FAIL; wild emulator boot retry

Date: 2026-10-31. Documentation-only audit branch `audit/zero435-s21-10001-fail-wild-boot-retry-20261031` from exact ZERO435 lab commit `7907f92ca9c507fb853c8fb37b82ddb38e2df2cb`. **NO gameplay code changed in this audit; no release, OTA, APK, merge, checkpoint edit or save-slot edit.**

## Inputs reviewed and historical independent original Android verification

Read `AGENTS.md`, `docs/ZERO419_WHOLE_ORIGINAL_GRU3_SENSORS_BRAIN_CAUSAL_AUDIT_20261031.md`, ZERO420, ZERO421, ZERO422, ZERO423 source dossiers, ZERO435 protocol, and actual original GitHub Actions job logs. All three historical regressions and CRC integrity tests **PASS**, but both original behavioral 10k tests per version **FAIL**.

| Lab | Original S21 10k | Original wild 10k | Android regressions | Original saved 33,312/V356 CRC |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000): 10,001 decisions, signed/gross 574.05910/3342.93503 = **17.1723%**, 4 new blue, 8/10 >=10%, **2 inverse**: FAIL | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560): 10,002, signed/gross 2748.31782/4459.65664 = 61.6262%, 3 blue, 87 red; red first/last 5k **35/52**: FAIL | [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS | [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS |
| V422 | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090): 10,003, 336.47877/2980.82377 = **11.2881%**, 4 blue, 7/10 >=10%, **2 inverse**: FAIL | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948): 10,003, 1873.24584/4386.45121 = 42.7053%, 5 blue, 52 red; first/last **19/33**: FAIL | [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS | [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS |
| V423 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685): 10,002, 281.30871/2799.49447 = **10.0486%**, 4 blue, 7/10 >=10%, **2 inverse**: FAIL | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734): 10,003, 1209.86984/3893.18490 = 31.0766%, 4 blue, 26 red; first/last **14/12** (improved, still 26 > 2×4): FAIL | [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS | [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS |

The wild first/last values above come from cumulative authentic `ZERO417_WILD_ACTUAL_1000` buckets 5 and 10, not estimated from last-life HUD.

## ZERO435 current run and strict original S21 outcome

Same exact lab SHA `7907f92ca9c507fb853c8fb37b82ddb38e2df2cb` for all four workflows.

- [Original Android regression 41219146180](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41219146180): **PASS**, including five V435 causal invariant tests and the established original Android test suite.
- [Saved original 33,312 S21/512 weights/V356 CRC 41219146216](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41219146216): **PASS**, Android instrumented integrity `OK (1 test)`.
- [Original S21 41219146069](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41219146069): **FAIL** despite 10,001 actual autonomous new decisions, cursor 33,312→43,313, original 170-material body and sole GRU3 owner. **signed=544.89597; gross=3702.70629; signed/gross=14.7162%** (required >=25%). **6 actual new blue**, no new red in blue-only room (historical red=90 unchanged). **7/10** windows >=10% (required >=9), **1 inverse** (required zero). Cortex localWeightUpdates=3590, censored=6411, rearWallAccepted=1693, frontWallExcluded=2057, passiveRearRetreatRejectedV430=889, undeliveredRearMuscleCreditsRejectedV432=293, V420 safeMotorMapAccepted=3703 / wall excluded=4302 / other excluded=1996. No claim that 3590 updates improved causal control.
- [Original wild 41219146173](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41219146173): attempt 1 **FAIL before any real decisions**, `AssertionError: Original sole GRU3 must really finish boot` at Android test line 114, after emulator launch timeout. This is **not** a measured hazard-avoidance failure or success. Reran **only this failed job**, same SHA and no code change, via GitHub Actions rerun. At first check, run attempt **2 queued**, not yet evaluated. Await 10k genuine wild results before deciding any behavioral correction.

### Original S21 exact ten 1,000-decision windows (V435)

| Window | Signed | Gross | Signed/gross | Blue cumulative | Opposed movements | Static movements | Wall contact frames |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 1 | 45.97067 | 247.07273 | 18.6061% | 0 | 66 | 497 | 24624 |
| 2 | 30.09643 | 505.08523 | 5.9587% | 1 | 241 | 200 | 7680 |
| 3 | 71.03245 | 427.85989 | 16.6018% | 2 | 74 | 216 | 0 |
| 4 | 45.68268 | 605.43642 | 7.5454% | 4 | 356 | 79 | 2963 |
| 5 | 49.06005 | 225.73487 | 21.7335% | 4 | 50 | 526 | 24384 |
| 6 | 62.63836 | 533.44067 | 11.7423% | 5 | 261 | 211 | 10402 |
| 7 | 75.32361 | 221.01924 | 34.0801% | 5 | 29 | 591 | 23409 |
| 8 | -10.73112 | 267.92964 | **-4.0052%** | 6 | 181 | 492 | 20783 |
| 9 | 128.60083 | 468.91061 | 27.4254% | 6 | 120 | 258 | 15033 |
| 10 | 47.12708 | 200.06257 | 23.5562% | 6 | 58 | 652 | 24304 |

This is a measured improvement from V434's S21 11.19%, 4 blue and 2 inverse windows, but **not approval**. Three windows (2, 4 and 8) fall below the >=10% criterion; window 8 is inverse. V435's 6 blue do not by themselves establish controlled steering.

### Important physical-vs-intent observation (not an inferred neural fix)

All six `ZERO404_HAZARD REAL_MATERIAL_CAUSALITY sign=BENEFIT` blue contacts had **positive PRE body momentum toward the gem** (PRE_HARM_momentumToward 1.157–2.116). **Five of six** had issued intentions strongly **away** from the touched gem (`intentionToward < -0.8`); the sixth was approximately perpendicular (`-0.050`). All six reported `offered=3`. Thus the increased blue count can largely reflect existing inertial motion rather than successful intended approach. This is evidence that actual velocity, delayed learned optical associations and selected GRU3 direction remain poorly coupled. OpticalAtTouch=0 in two contacts is not proof of retinal blindness: ZERO419 established center-versus-protruding-eye azimuth telemetry mismatch.

### V435 source and remaining causal uncertainty

V435 only filters insertion into V371 six-clean-decision delayed-contact history. It computes normalized 0.30s displacement `mx,my`, PRE velocity from 194D inputs 191/192, then requires positive PRE-relative travel component >0.35 before a delayed motor-bank link. Original physics, 194 senses, GRU3, 512 cortex weights, V319 CH2, contact consequences and reward are not changed. The 5 synthetic Android invariants pass. But **no per-decision accepted/rejected V435 delayed-link counts are emitted in the long-run S21 log**, so the exact frequency of the new gate in vivo remains unknown. The result cannot be causally attributed to this gate alone without matched trace and control, even though the patch is isolated relative to V434.

Source-risk: `V371.complete` still trains `learnedX/Y[bank]` and increments `trained[bank]` on every clean observed motion, including pure coasting; V435 filters only the delayed-contact trace. This is a separate potential misattribution to investigate read-only after wild completes, **not yet a verified cause or authorization for a second patch**. No changes to GRU3, sensors, body or motor policy while original wild retry is pending.

## Next gate / preservation

Check attempt 2 of [41219146173](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41219146173) and its *latest* job logs. If Android boot fails again, investigate emulator/boot reproducibility without changing the behavioral code; if it completes 10k, calculate genuine wild first/last 5000 red, blue, signed/gross, red/blue, all 1k windows and red PRE momentum/issued intention. Only then consider **one** causally justified isolated V436 behavior correction on a distinct lab branch with the same four original Android tests. **No release, no OTA, no APK, no stable merge.** Original 33,312 S21, 512 weights, V356 CRC, CH2 V319, 194 sensors, original 170-material body and 12 user slots remain untouched.

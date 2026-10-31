# ZERO433 — actual original S21 10k failure; wild emulator boot retry; no code change

Date: 2026-10-31. Audit-only branch `audit/zero433-s21-physical-fail-wild-retry-20261031`, based on `lab/zero433-inertial-optical-physical-evidence` at `c6706b65b431685211b09765d9c6b621d23f0349`. **No production code, checkpoint, APK, OTA, save slot or physics modification. No approval.**

## Mandatory source review and reproducibility

Read root `AGENTS.md`, `docs/AGENTS.md`, `ZERO419`–`ZERO423`, `ZERO433` and inspected the original Java call chain: `Zero3OriginalBrainV356.sense/actuate/observed`, `Zero3RecurrentBrain.choose/completeConsequence`, `Zero3CognitionV353.select`, `Zero3InstrumentalCreditV371.opticalPreferenceV374/score`, `Zero3InertialResponseV377`, `Zero3ContactValenceV369.utility`, `Zero3LearningFoundation.AdaptiveCortex.learnV332/command`, `Zero3SensoryFrameV315` and `SensorimotorMind.Senses.spectral8`.

Independently fetched all six V421–V423 original Android behavioral job logs and checked the other six regression/CRC Action conclusions:
- V421 [S21 41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) 10,001 new, 17.1723%, 8/10 >=10%, 2 inverse, 4 new blue; [wild 41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) 10,002, 3 blue, 87 red, red 35 -> 52. [Reg 41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS, [CRC 41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS.
- V422 [S21 41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) 10,003, 11.2881%, 7/10, 2 inverse, 4 blue; [wild 41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) 10,003, 5 blue, 52 red, red 19 -> 33. [Reg 41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS, [CRC 41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS.
- V423 [S21 41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) 10,002, 10.0486%, 7/10, 2 inverse, 4 blue; [wild 41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) 10,003, 4 blue, 26 red, red 14 -> 12. [Reg 41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS, [CRC 41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS. All behavior FAIL; all regression/CRC PASS.

## V433 original S21 actual Android result — FAIL, not an emulator timeout

[Action 41201035384](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035384), job `124536716939`: actual saved original S21 at 33,312 completed decisions, 512 cortical weights, 194 input sensors, V319 CH2, original body and original blue-only training room. Completed to cursor 43,316 = **10,004 genuine new decisions**. Final `ZERO417_ORIGINAL_USER_S21_10000_RESULT`:

- **signed 209.72467, gross 3321.19637, efficiency 0.063147 = 6.3147%**, vs hard minimum 25%.
- **6/10** distinct 1k windows >=10%, **3 opposite** windows (4, 6, 10), vs 9/10 and zero inverse.
- **6 new actual blue pickups**, passing blue-only pickup threshold 5 but not overall acceptance.
- **3314 actual local cortical weight updates**, `censored=6690`, `rearWallAccepted=1812`, `frontWallExcluded=1709`, `safeMotorMapAccepted=3432`, `safeMotorMapExcludedWall=4638`, `safeMotorMapExcludedOther=1934`, `passiveRearRetreatRejectedV430=885`, `undeliveredRearMuscleCreditsRejectedV432=365`; `randomUniformActorHeadsSkippedV428=6`.
- S21's inherited `red=90` is HISTORICAL, **not 90 newly encountered red gems**. No red-safety claim is possible from this blue-only room.

| Window | End cursor | signed/gross % | new blue | solid-contact frames | opposed movement decisions | static decisions |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | 34314 | 20.0132 | 0 | 24400 | 53 | 588 |
| 2 | 35313 | 16.8623 | 1 | 9306 | 241 | 231 |
| 3 | 36313 | 27.1929 | 0 | 24448 | 33 | 580 |
| 4 | 37314 | -14.1630 | 1 | 18013 | 261 | 407 |
| 5 | 38314 | 13.2868 | 2 | 0 | 116 | 209 |
| 6 | 39314 | -9.8379 | 0 | 10228 | 336 | 211 |
| 7 | 40312 | 21.5430 | 0 | 24432 | 1 | 830 |
| 8 | 41314 | 4.3324 | 1 | 12890 | 250 | 313 |
| 9 | 42313 | 22.1776 | 0 | 24352 | 4 | 758 |
| 10 | 43315 | -17.1958 | 1 | 18520 | 204 | 624 |

Compared with same-chain V432 S21 **19.2907%, 7/10, 2 inverse, 4 blue**, the V433 optical-evidence censor is associated with a large locomotion regression while pickups increased. This matched original Android result falsifies V433 as an adequate locomotion fix; it does not establish the single root cause.

## V433 regressions and CRC

[Regressions 41201035438](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035438) **PASS**, including `Zero433InertialOpticalEvidenceAndroidTest` and earlier Android tests. [Original trained S21 CRC 41201035418](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035418) **PASS**. Technical correctness alone is not scientific/behavioral approval.

## V433 wild: infrastructure failure, one same-SHA retry in progress

[Wild Action 41201035346](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035346) initial job `124536716982` **FAILED before original brain startup**: `cursor=-1, owner=false, destroyed=true`, Android Activity died/launch timeout, **zero valid original decisions** and no meaningful red/blue rates. This cannot be counted as a behavioral failure or success. Re-ran only this job without changing source; retry job `124545802672` was **in_progress** on last audit. Do not invent wild 10k numbers. Preserve source state and recheck after completion; if infrastructure fails again, retry same SHA rather than tuning the brain on absent data.

## Causal interpretation and next gate

The V433 single change returns zero directional optical preference when PRE COM speed >=2 and V377 motor travel is unverified. This is physically conservative, but `Zero3CognitionV353.select` then has less learned contact-valence discrimination between offered GRU actions in exactly the drifting state that dominates red collisions. The original body can still have the wrong velocity after issuing an away-heading. **This is a plausible failure mechanism, not proof of a fix.** The original GRU candidate set remains often three options; no evidence permits hardcoded avoidance or restored V2.2 steering.

No V434 behavioral patch while the V433 wild original 10k retry remains in progress. After final wild log, inspect red 1k windows/first-last 5k, blue, signed/gross, PRE inertia and actor optical scores, measured motor response, and only then justify ONE isolated code change on a new lab branch with full original Android 2x10k, regressions and CRC. Preserve S21 33,312, 512 weights, V356 checkpoint CRC, CH2 V319, 194 inputs, original 170-body physics and all 12 slots. **NO APK, NO OTA, NO MERGE.**

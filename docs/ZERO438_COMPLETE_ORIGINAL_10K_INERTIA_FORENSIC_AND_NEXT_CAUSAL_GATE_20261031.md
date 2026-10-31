# ZERO438 — completed original Android 2×10k failure, motor-inertia forensics
Date: 2026-10-31. Documentation-only audit branch `audit/zero438-complete-original-10k-physical-failure-20261031` based on exact V438 commit `c5d1ef11f028a6dde2c60a5e253e77b90b59a6d7`. No gameplay/brain/physics change, APK, OTA, merge, reset or saved-profile mutation.

## Mandatory source and controls
Read root AGENTS.md and docs/AGENTS.md; ZERO419, ZERO420, ZERO421, ZERO422, ZERO423; ZERO438 V437 complete protocol; MASTER, implementation contract and UI specification. Confirmed 12 original V421–V423 Actions with GitHub job metadata on 31 October:
- V421: [S21 FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000), [wild FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560), [regressions PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217), [CRC PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737).
- V422: [S21 FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090), [wild FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948), [regressions PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615), [CRC PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313).
- V423: [S21 FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685), [wild FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734), [regressions PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431), [CRC PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279).

## V438 actual complete original Android jobs — final
- [Original trained S21 41243395973](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243395973), job 124661747352: **FAIL behavioral gate**, **10,004** additional self-generated decisions from exactly 33,312, final cursor 43,316. Signed **544.84796**, gross **3703.02721**, signed/gross **14.7136%**, below required 25%. **7/10** windows >=10%, **one inverse** (need 9/10 and zero inverse), **6 new real blue** (passes pickup subgate). 3,590 original local motor weight updates, 6,414 censored, 1,693 verified rear-wall accepts, 2,059 front-wall exclusions, 293 undelivered rear-probe rejections. Historical S21 red=90 is restored prior count, NOT 90 new red collisions. Real material pickup logs confirm six blue in this run.
- S21 1k windows: bucket | signed | gross | signed/gross | blue pickups | wall-contact frames:
  1 | 45.97067 | 247.07273 | 18.6061% | 0 | 24640
  2 | 30.09643 | 505.08523 | 5.9587% | 1 | 7664
  3 | 70.44135 | 428.54170 | 16.4375% | 1 | 0
  4 | 46.27378 | 604.75461 | 7.6517% | 2 | 2963
  5 | 48.86057 | 226.55573 | 21.5667% | 0 | 24416
  6 | 64.95908 | 534.77977 | 12.1469% | 1 | 10370
  7 | 73.44011 | 219.13242 | 33.5140% | 0 | 23441
  8 | -10.96886 | 267.65650 | **-4.0981% inverse** | 1 | 20751
  9 | 128.60925 | 469.04898 | 27.4192% | 0 | 15065
  10 | 47.24537 | 200.19638 | 23.5995% | 0 | 24320.
  1k snapshots are independent approximate decision buckets; final metrics include the trailing four decisions.
- [Original newborn wild 41243395992](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243395992), job 124661747346: **FAIL behavioral gate**, **10,002** genuine self-generated decisions. Signed **-215.55336**, gross **3220.44385**, signed/gross **-6.6933%** (need >=12%). **3 blue**, **17 red**, red/blue **5.667** (need <=2). First 5k red **13**, last 5k red **4** (the *decrease* subgate alone passes). 4,869 original local motor updates, 5,133 censored, 1,824 harm-rejected, 781 rear-wall accepts, 969 front-wall exclusions, 1,611 motor-map wall exclusions, 2,295 motor-map harm exclusions, 5,751 learned-harm candidate expansions.
- Wild 1k windows: bucket | signed | gross | signed/gross | red | blue:
  1 | -49.19889 | 364.06461 | -13.5138% | 0 | 3
  2 | 7.86273 | 300.87153 | 2.6133% | 0 | 0
  3 | -4.70595 | 338.90129 | -1.3886% | 0 | 0
  4 | -64.82275 | 409.26100 | -15.8390% | 9 | 0
  5 | -116.25098 | 292.61836 | -39.7278% | 4 | 0
  6 | 0.37833 | 280.79855 | 0.1347% | 0 | 0
  7 | -29.00458 | 249.63760 | -11.6187% | 1 | 0
  8 | -11.54781 | 291.79395 | -3.9575% | 0 | 0
  9 | 51.78590 | 307.55052 | 16.8382% | 1 | 0
  10 | 0.10699 | 384.52996 | 0.0278% | 1 | 0.
  Final 17th red touch occurs after the last bucket snapshot.
- [Original Android regressions 41243396007](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243396007) **PASS**, job 124661747331. [Original S21 V356 checkpoint/512-weight CRC 41243395989](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243395989) **PASS**, job 124661747299. These technical PASS results do NOT override the two failed behavioral gates.

## Red contact physical causal trace (all 17, original wild)
Real material red-contact decisions: **3346, 3366, 3386, 3406, 3426, 3859, 3878, 3898, 3919, 4327, 4348, 4368, 4388, 6294, 8227, 9982, 10002**.
**17/17** have measured PRE red-ward COM momentum strictly positive (range about +0.24 to +2.84); **13/17** have selected command heading away from the red (intentionToward negative). The remaining 4 include small/positive red-ward intentions. After learning, many contacts have seven offered GRU3 alternatives and chosen optical score equal to the best offered optical score, yet physical momentum remains toward the hazard. Three blue pickups happen before decision 1000, with no further blue in the remaining 9k. The clusters at 3346–3426, 3859–3919 and 4327–4388 recur at about 20-decision cooldown intervals. This is strong evidence that neural choice and actual body motion diverge, not proof of any single missing actuator force.

## Source-level review and causal limitations
- `Zero3RecurrentBrain.choose` freshly samples candidate zero; V438 restores the mathematically legitimate V426 score from all freshly drawn candidates. V438's S21 14.7136%, 7/10, 1 inverse, 6 blue and wild -6.6933%, 17 red, 3 blue are effectively the same published behavioral metrics as failed V436. **Correcting candidate-score bookkeeping does not fix physical steering.** Do not repeat V437's false premise.
- `Zero3CognitionV353.select` ranks real-contact and learned V371/V377 motor scores; `Zero3InstrumentalCreditV371.opticalPreferenceV374` correctly refuses to equate unverified intended heading with actual movement under high inertia in V433 mode. V434/V435 learned delayed contact associations and V436 seven-option coverage are active, yet 13/17 real red impacts still occur while commanding away. Simply increasing candidates, visual valence weight, or suppressing gradients has not shown causal motor control.
- `Zero3InertialResponseV377` models only 8 heading banks × 2 speed-magnitude regimes, not the *direction* of current velocity; its residual `actualTravel - 0.30*preVelocity` includes ordinary drag. This is a **plausible conditioning/identifiability gap**, NOT yet a proved singular root cause. Its heldout trust cannot be assumed to imply reversal/braking authority. A defensible next code intervention needs paired real physical PRE/post measurements stratified by velocity angle and motor effort, with independent heldout validation and an isolated Android A/B. Do not insert a scripted counter-velocity command or a new reward.
- `Zero3OriginalBrainV356.sense` supplies 194 existing sensors, actual COM velocity in 191/192, 24 spectral rays, and original V319 CH2 grip. The V315 coarse obstacle rays ignore chamber geometry while the 16 fine rays use it, but the wild test has no chamber; changing this alone cannot explain wild inertia failure. `ZERO404_HAZARD.opticalAtTouch` uses center-based azimuth whereas actual retina uses a protruding material eye, so a zero telemetry sample is NOT proof of retinal blindness. Do not change vision based only on this telemetry.

## Decision and next work
**NOT APPROVED.** The run has a valid negative result and no unambiguously causal, isolated production-code correction proven by the available telemetry. Rather than make another speculative version, preserve V438 and instrument the **same genuine Android body** for per-decision PRE/POST COM velocity vector, actual 64 delivered muscle commands, per-sector delivered perturbation, solver drag, contact red damping, V377 bank/trust/heldout and candidate optical versus physical displacement; then use matched physical A/B to justify ONE code change before another 2×10k. This document changes no code, physics, training data, weights, CRC, CH2 V319, 194 inputs or 12 slots. No APK, OTA or stable merge.

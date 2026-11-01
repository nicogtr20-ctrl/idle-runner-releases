# ZERO443 — COMPLETE original Android 10k / motor-evidence forensic (2026-11-01)

**Branch:** `audit/zero443-complete-original-motor-evidence-20261101`, documentation only, branched from exact V443 behavioral/telemetry SHA `4e243b4b2e91d744f58a21185d87a59c97872bc0`. **No behavior change, no APK/OTA, no merge.** The earlier protocol is `docs/ZERO443_V442_ORIGINAL_MOTOR_EVIDENCE_TELEMETRY_PROTOCOL_20261101.md`. This report is based on the **actual full decoded GitHub job logs**, not on workflow labels or expected outcomes. Both behavioral workflows exited FAILURE because the strict scientific assertions failed after the real 10k decisions, not because of an emulator launch failure.

## Exact completed GitHub Actions, same source SHA

| Action | Run | Job | Actual conclusion |
|---|---:|---:|---|
| Saved original S21 33,312 + 10,003 decisions | [41269049693](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41269049693) | 124737973364 | FAILURE, scientific gate |
| Original newborn wild 10,005 decisions | [41269049677](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41269049677) | 124737975212 | FAILURE, scientific gate |
| Original Android physical regressions + five V442/V443 tests | [41269049713](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41269049713) | 124738001808 | SUCCESS, including `OK (5 tests)` |
| Actual user original 33,312 brain, 512 weights, V356 CRC | [41269049662](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41269049662) | 124737832625 | SUCCESS, `OK (1 test)` |

### S21 original: every ~1,000-decision window

The saved user brain starts at decision 33,312; the result is decision 43,315, **10,003 new original decisions**. The displayed historical red cumulative counter of 90 is **inherited**; there were **zero new red contacts** in this blue-only S21 room. Each window uses signed material progress divided by gross material movement, not simulated travel.

| Window | End decision | New blue | Signed | Gross | Signed/gross | Cumulative local cortex updates |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | 34,314 | 0 | 54.01813 | 235.08394 | 22.9782% | 167 |
| 2 | 35,313 | 1 | 5.21238 | 461.28328 | 1.1300% | 585 |
| 3 | 36,312 | 0 | 27.13212 | 110.61336 | 24.5288% | 693 |
| 4 | 37,313 | 1 | -50.53356 | 260.11480 | -19.4274% | 899 |
| 5 | 38,313 | 0 | 95.15941 | 313.67136 | 30.3373% | 1,256 |
| 6 | 39,314 | 0 | 44.84163 | 154.69461 | 28.9872% | 1,421 |
| 7 | 40,314 | 1 | 43.20566 | 486.31329 | 8.8843% | 1,851 |
| 8 | 41,315 | 0 | 47.32618 | 156.24489 | 30.2897% | 2,085 |
| 9 | 42,316 | 1 | -8.90568 | 241.05759 | -3.6944% | 2,375 |
| 10 | 43,314 | 0 | 49.93905 | 339.60852 | 14.7049% | 2,754 |

**Final actual S21:** signed 307.42393 / gross 2758.91777 = **11.1429%**; **4 new blue**; **6/10** windows at or above 10%; **2 inverse** (4,9). Fails >=25%, >=5 blue, >=9/10 and zero inverse. The final partial decisions are included in final cumulative values. Original motor owner remained active. Wall-contact telemetry is very large in several windows (e.g. 24,288 in window 1 and 23,984 in window 8); these are implementation counters, **not** independently measured seconds stuck at a wall.

### Wild original: every ~1,000-decision window

Original newborn wild starts at 0, completes **10,005 genuine decisions** and includes physical energy -7 RED and +22 BLUE contacts. No reward injection or scheduled heading.

| Window | End decision | New blue | New red | Signed | Gross | Signed/gross | Cumulative local cortex updates |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 1 | 1,003 | 3 | 0 | 53.59572 | 383.30409 | 13.9826% | 622 |
| 2 | 2,004 | 0 | 4 | 117.84221 | 410.56779 | 28.7023% | 1,265 |
| 3 | 3,004 | 1 | 15 | 102.33215 | 434.61079 | 23.5457% | 1,927 |
| 4 | 4,002 | 0 | 10 | 44.78626 | 444.61780 | 10.0730% | 2,542 |
| 5 | 5,003 | 0 | 0 | 28.41450 | 326.18693 | 8.7111% | 2,828 |
| 6 | 6,004 | 0 | 0 | -27.11488 | 401.47275 | -6.7539% | 3,368 |
| 7 | 7,004 | 0 | 11 | -117.20753 | 399.69800 | -29.3240% | 4,034 |
| 8 | 8,002 | 0 | 9 | -93.65990 | 311.95570 | -30.0235% | 4,592 |
| 9 | 9,001 | 0 | 0 | -50.35739 | 289.89184 | -17.3711% | 5,133 |
| 10 | 10,004 | 0 | 0 | -6.42973 | 264.78581 | -2.4283% | 5,450 |

**Final actual wild:** signed 52.12098 / gross 3667.33597 = **1.4212%**; **4 blue** (all collected by decision 3,004), **49 red**; red first 5k **29**, red last 5k **20** (improves only the *direction* of red rate); **49 > 2×4 = 8**, so red avoidance **FAIL**. The last five windows have negative signed progress; this is not successful locomotion. Harm-rejected local credit 1,675, local cortex updates 5,450. The red contacts are real `REAL_MATERIAL_CAUSALITY` events, not counter artifacts.

## V443's decisive new causal observation

V443 added **read-only** instrumentation to `Zero3InstrumentalCreditV371.score` and `opticalPreferenceV374`, with a fifth physical Android regression proving that the historical score can be observed without changing the returned censored zero. Counts below are **candidate-score evaluations, not decisions, not unique contacts**.

| Diagnostic | S21 | Wild |
|---|---:|---:|
| `scoreEvaluations` | 278,262 | 413,987 |
| `predicted` (V377 expected available) | 6,573 | 861 |
| `unsupportedDrift` | 190,393 | 323,649 |
| `censoredNonzeroHistorical` | **74,271** | **0** |
| `censoredZeroHistorical` | 116,122 | **323,649** |
| `uncensoredNonzeroHistorical` | 7,715 | **0** |
| `opticalDriftSuppressed` | 190,393 | 323,649 |
| `verifiedV377Banks` | 5 | 1 |
| `verifiedV371Directions` | **0** | **0** |
| `delayedRealLinks` | 395 | **0** |

**New substantial result:** In original wild V443, the V442 delayed-history censor suppresses **zero nonzero historical scores**. Every one of its 323,649 suppressed historical-bank evaluations was already exactly zero, and there were **zero delayed real links**. Consequently the earlier V442 claim of improving hazard safety by censoring a learned delayed historical bank is **not supported in wild**: that mechanism had no useful bank score to censor. The V441/V442 near-identical wild results now have a direct mechanistic explanation. This does not mean that all cognitive learning is absent: the V369 contact-valence learning and local motor updates continue.

In S21, by contrast, the same gate censors 74,271 nonzero historical scores, but this is a blue-only room with inherited prior experience and still fails locomotion. `delayedRealLinks=395` is a current diagnostic count that may include pre-existing checkpoint learning; do **not** call these 395 new blue pickups. There are only 5/16 heldout-reliable V377 bank/regime entries in S21 and 1/16 in wild; the separate V371 eight-direction verifier reports 0/8 for both. V377 predicts in just 861/413,987 wild candidate evaluations (~0.21%); 323,649/413,987 (~78.18%) are in the unverified PRE drift state. Under strict V433/V441/V442 these evaluations return zero rather than treating a requested heading as physical travel. **This is a verified evidence-availability bottleneck, not yet proof of which motor algorithm should replace it.**

The 49 wild real red event records show **45/49** had positive PRE momentum toward the contacted hazard, **33/49** issued a command pointing away (`intentionToward<0`), **48/49** had PRE COM speed below 2, and **32/49** had `chosenOptical<bestOptical` among the *offered* actor options. An offered-option optical score is not a proven physically achievable escape. Red contact at 33/49 opposite commands is direct evidence that issuing an opposite direction often fails to reverse the body in time; it is **not** proof that simply forcing another heading would help. The raw `opticalAtTouch` azimuth is known to use center-to-object rather than the retina eye azimuth (ZERO419); zero in that field is not proof the actual eye was blind.

## Historical 12 GitHub Actions V421–V423 rechecked directly

All three historical original Android **regression** jobs and all three original **S21 CRC** jobs completed SUCCESS; all six long behavioral jobs completed FAILURE on scientific criteria:

| Version | S21 run | S21 signed/gross; blue; windows >=10%; inverse | Wild run | Wild signed/gross; red/blue; first→last red |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) | 17.1723%; 4; 8/10; 2 | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) | 61.6262%; 87/3; 35→52 |
| V422 | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) | 11.2881%; 4; 7/10; 2 | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) | 42.7053%; 52/5; 19→33 |
| V423 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) | 10.0486%; 4; 7/10; 2 | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) | 31.0766%; 26/4; 14→12 |

Exact regressions/CRC pairs: V421 [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) / [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737); V422 [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) / [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313); V423 [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) / [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279). Source background and constraints read: AGENTS.md, ZERO419 whole original sensory audit, ZERO420 real V371/V377 motor eligibility, ZERO421 delivered muscle gradient, ZERO422 learned harm candidate coverage, ZERO423 immediate own decision motor credit.

## What the code actually does, and what a single next repair must prove

- `Zero3InstrumentalCreditV371.score`: when `inertia.expected` is unavailable and PRE speed is at least 0.35, the strict V442 branch returns 0; V443's shadow read proves this is **censoring real historical signal in S21** but **not in wild**. `opticalPreferenceV374` also returns 0 in the same unverified-drift state. The current actor can therefore learn that a wavelength is harmful without having a verified, *action-specific* physical displacement to rank candidates.
- `Zero3InertialResponseV377`: uses 16 heading × PRE speed-regime bins, independent heldout error against passive-coast residual. `reliableBanks` is 1 in wild, 5 in S21. The V371 older eight-heading verifier is 0 in both. This is an **empirically unsupported action→COM response map** in the tested states, not a lack of a 194D input or a missing GRU network.
- `Zero3InstrumentalCreditV371.linkTrueContactToPriorMotor`: only links a real contact to up to six previously completed *clean, physically accelerated* approaches with appropriate seen rays. Wild produced **zero** such delayed links despite 49 red contacts; changing historical score weights cannot repair an empty evidence bank. Simply crediting a desired heading or a passive collision would invent motor causality.
- V421 corrected delivered local muscle perturbation, V423 corrected immediate own-decision motor credit, and V439/V440 changed measured velocity observation gains. None passed all real 10k gates. Do not recycle those as a certified fix, nor interpret more cortex weight updates as improved gait.

**Causal next experiment (not yet an approved behavioral patch):** instrument a *single genuine original decision trace* at the junction `V371.begin → original cortex 64 delivered muscle outputs → 170-material physics → V371.complete/V377.finish`, including PRE speed regime, train/heldout assignment, accepted/rejected clean samples, actual action-conditioned acceleration, V377 predicted-vs-coast errors, V369 real valence, and whether a later contact had a physically linked recent ray. This is necessary to distinguish (1) missing eligible independent motor evidence, (2) unreliable body response despite eligible evidence, and (3) a candidate-selection override after good physical evidence. Only after one of those is established should a **single** causally justified correction be made on another isolated branch and tested with the same original S21/wild 10k Android actions + regressions + CRC. Do not change the scientific thresholds, install V2.2, impose a safe direction, invent a pickup, or modify the saved S21 model.

## Decision and preserved invariants

**V443 is NOT approved.** 10k original S21 and wild are scientifically FAIL; technical regression and CRC PASS are necessary but insufficient. The original saved S21 33,312 decisions/512 cortex weights, V356 CRC, V319 CH2 independent grip, 194 input senses, GRU64, 170-material physical body and all 12 user saves remain untouched. This branch contains **documentation only**. No APK, OTA, production merge or "locomotion solved" claim.

# ZERO470 — historical 12-job log audit and live original Android run triage
Date: 2026-11-02. **AUDIT ONLY / NO RELEASE.** Source reference `db79ea1f14238aff28789e7b011e91556ace7d72` on `lab/zero470-coarse-fine-chamber-ray-consistency`. This separate audit branch contains **documentation only**; the running V470 source branch and all original Android runs are left unchanged.

## Source contract
Read `AGENTS.md` and `docs/ZERO419_WHOLE_ORIGINAL_GRU3_SENSORS_BRAIN_CAUSAL_AUDIT_20261031.md`, `ZERO420_REAL_V371_V377_RED_WALL_MOTOR_EVIDENCE_CAUSAL_REPAIR_20261031.md`, `ZERO421_REAL_DELIVERED_V332_MUSCLE_CAUSAL_GRADIENT_20261031.md`, `ZERO422_LEARNED_HARM_7_CANDIDATE_ACTOR_SAFETY_AB_20261031.md`, `ZERO423_IMMEDIATE_OWN_DECISION_MOTOR_CREDIT_10000_20261031.md` and `ZERO470_V469_REAL_FAILURE_AND_ORIGINAL_194D_COARSE_FINE_RAY_PROVENANCE_20261102.md`. Directly fetched all 12 historical GitHub job logs and their job conclusions (not merely the earlier summaries).

## Historical V421–V423 original Android job outcomes
| Version | S21 run/job | Wild run/job | Regression run/job | V356 CRC run/job |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) / 124382019440 FAIL | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) / 124382035940 FAIL | 41149404217 / 124382062953 PASS | 41149399737 / 124382048134 PASS |
| V422 | 41149758090 / 124383218205 FAIL | 41149762948 / 124383232426 FAIL | 41149771615 / 124383260950 PASS | 41149767313 / 124383247784 PASS |
| V423 | 41149983685 / 124383952413 FAIL | 41149987734 / 124396959851 FAIL | 41149995431 / 124383992920 PASS | 41149992279 / 124383982271 PASS |

All three S21 runs restored the actual 33,312-decision user brain; their 10 windows are genuine logged decision intervals. The original 512-weight V356 CRC and Android regressions passed separately, but **none** of the six behavioral runs passed.

### S21 actual 1000-decision window signed/gross percentages
| Window | V421 | V422 | V423 |
|---:|---:|---:|---:|
| 1 | 16.52 | 16.61 | 19.84 |
| 2 | 19.29 | 16.15 | 6.49 |
| 3 | 19.88 | 24.27 | 16.05 |
| 4 | **-15.21** | **-11.79** | **-41.03** |
| 5 | 43.93 | 30.05 | 26.50 |
| 6 | 26.74 | 14.88 | 15.65 |
| 7 | 12.69 | 8.79 | 10.13 |
| 8 | 29.37 | 25.33 | 24.81 |
| 9 | **-19.79** | **-19.60** | **-10.74** |
| 10 | 38.31 | 26.32 | 29.91 |

S21 full outcomes from raw logs:
- V421: 10,001 new decisions, signed 574.05910 / gross 3342.93503 = **17.1723%**, 8/10 windows >=10%, 2 inverse, 4 new blue, 1,918 local motor weight updates; 2,506 rear-wall accepted and 1,599 front-wall excluded, 7,027 local samples censored. Delivered-probe V421 enabled, V420 safe upper map disabled.
- V422: 10,003 new decisions, signed 336.47877 / gross 2980.82377 = **11.2881%**, 7/10 windows >=10%, 2 inverse, 4 new blue, 1,851 local updates, 2,397 rear-wall accepted, 2,278 front-wall excluded, 7,234 censored. V421 delivered-probe disabled.
- V423: 10,002 new decisions, signed 281.30871 / gross 2799.49447 = **10.0486%**, 7/10 windows >=10%, 2 inverse, 4 new blue, 2,962 immediate-own-decision updates, 1,968 rear-wall accepted, 1,993 front-wall excluded, 7,040 censored. More updates did not yield stable locomotion.

### Wild actual red/blue windows and physical movement
| Window | V421 red / blue | V422 red / blue | V423 red / blue |
|---:|---|---|---|
| 1 | 0 / 3 | 0 / 3 | 0 / 3 |
| 2 | 10 / 0 | 10 / 0 | 0 / 0 |
| 3 | 6 / 0 | 9 / 0 | 2 / 0 |
| 4 | 9 / 0 | 0 / 0 | 12 / 1 |
| 5 | 10 / 0 | 0 / 0 | 0 / 0 |
| 6 | 10 / 0 | 6 / 0 | 0 / 0 |
| 7 | 10 / 0 | 4 / 1 | 6 / 0 |
| 8 | 13 / 0 | 12 / 0 | 0 / 0 |
| 9 | 9 / 0 | 10 / 0 | 0 / 0 |
| 10 | 10 / 0 | 1 / 1 | 6 / 0 |

- V421: 10,002 decisions, signed 2748.31782 / gross 4459.65664 = 61.6262%, **87 red / 3 blue**, red **35 first 5k -> 52 last 5k**, 5,843 motor updates, 1,535 harm-gradient rejects. Red safety FAIL.
- V422: 10,003 decisions, signed 1873.24584 / gross 4386.45121 = 42.7053%, **52 red / 5 blue**, red **19 -> 33**, 4,587 updates, 1,655 harm rejects. Red safety FAIL.
- V423: 10,003 decisions, signed 1209.86984 / gross 3893.18490 = 31.0766%, **26 red / 4 blue**, red **14 -> 12** (improves rate but red/blue = 6.5), 4,712 updates, 1,808 harm rejects. Red safety FAIL.

The persistent S21 inverse windows 4 and 9 across all three independent interventions are stronger evidence for a shared body/episode/control failure than for any single raw-probe, candidate-count or delayed-credit explanation. This is a *hypothesis*, not a proven single root cause. The later V465–V468 work narrowed both negative windows to post-rebirth motion and fixed stale cross-life 64 motor efferences, but the actual behavioral gate still failed. The V469 optical drift preference worsened wild locomotion to 2.783% and was rejected.

## V470 current exact-SHA run triage
Four original workflow runs created 2026-11-02 05:58:50 UTC on **identical** SHA `db79ea1f14238aff28789e7b011e91556ace7d72`:
- [S21 41397116177](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116177), job 125120843131: **IN PROGRESS** at last check; no 10k metrics can be claimed yet.
- [Wild 41397116200](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116200), job 125120908073: first attempt **FAILED BEFORE FIRST DECISION** at `Zero417WildOriginalVelocityCredit10000AndroidTest.java:114` with `AssertionError: Original sole GRU3 must really finish boot`. Android Activity displayed but the 30-second owner-ready poll never succeeded. This is **boot/instrumentation failure, not measured red avoidance**. **Re-ran the SAME job/SHA** using GitHub Actions; run attempt 2 is in progress at last check (started 06:18 UTC). Do not change physics or claim failure/success of behavior based on the first attempt.
- [Regressions 41397116373](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116373), job 125120899118: **PASS**, including both `Zero470CoarseFineRayConsistencyAndroidTest` tests and all established original Android suites.
- [CRC 41397116230](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116230), job 125120725403: **PASS**, actual 33,312 trained user brain, 512 weights, V356 checkpoint byte identity.

V470 changes only `Zero3OriginalBrainV356.sense`: 8 coarse rays `inputs[8..15]` become max-pools of 16 already measured chamber-aware fine rays `inputs[44..59]`. The underlying `Zero3SensoryFrameV315.capture` still computes its original chamber-null rays for other consumers; 194D shape and existing 512 motor weights/checkpoint remain unchanged. Source difference from V468 consists of this 12-line change, test, workflows and documentation. **Only full original S21 and wild 10k results can decide its behavioral value**.

## Next actions / no release
1. Poll both in-progress V470 jobs; read all 10 genuine windows and complete signed/gross, blue, red first/last 5000, local gradients, V371/V377 motor reliability, GRU/optical traces and original body contacts when they finish.
2. If wild retry again fails *before* GRU owner boot, inspect actual owner error/Activity startup and isolate a **test-only** readiness fix, without changing motor learning or treating boot as behavior.
3. If a full 10k fails the preregistered behavior gates, investigate the measured causal source and make at most **one isolated, justified** behavioral change on a new branch, then rerun all four original Android gates.
4. Hard gates remain S21 >=10k, >=25% signed/gross, 9/10 windows >=10%, zero inverse, >=5 new blue; wild >=10k, >=12% locomotion, late red < early red, total red <=2x blue; regressions+CRC PASS. No APK, OTA, stable merge, new 2.2 blend, hardcoded headings, synthetic reward or modification of original S21/12 saves.

# ZERO447 — V446 original Android outcome and fail-closed neural-choice root-cause capture
2026-11-01. Experimental branch `lab/zero447-original-neural-choice-failure-forensics`, based on exact V446 behavioral commit `64f8c551d829b25f0f49ebf6ebfb8c48876d927c`. NO release, APK, OTA, stable merge, weight or save edits.

## Contract
Read AGENTS.md, docs/ZERO419 through ZERO423 and V446 dossier. The original S21 trained checkpoint contains 33,312 prior decisions and 512 cortex weights; original 194D/64-GRU3, V356 CRC, V319 CH2, 170-material body and 12 slots remain unchanged. V2.2 is not restored. No scripted movement or reward. Original strict thresholds remain: S21 >=10,000 new decisions, >=25% signed/gross, 9/10 windows >=10%, zero inverse, >=5 new blue; wild >=10,000 real decisions, >=12%, late red < early red, red <=2x blue; Android regressions and CRC PASS.

## Actual completed V446 GitHub Actions, commit `64f8c551d829b25f0f49ebf6ebfb8c48876d927c`
- [S21 original 41279152306](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41279152306): **FAIL**, actual final **10,002** new decisions (43,314 total), **18.0209%** signed/gross (570.09987 / 3163.54081), **8/10** independent windows >=10%, **one inverse** (window 4), **4 new blue**. 3,084 real cortex updates. Original S21 90 reds are historical, not newly touched. Nine of ten windows positive but still below mandatory gate. Physical telemetry: 4,879 static and 1,133 opposed movement decisions across 10 windows; 279,632 evaluated motor proposals, only 638 V377 predictions, 209,970 unsupported drift, 80,260 censored nonzero historical scores, 395 real delayed motor links; the V444 per-bank telemetry field still reports the **old** squared-error threshold and must NOT be confused with V446 active trust.
- [Wild original 41279152222](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41279152222): **FAIL due to a genuine original neural-choice runtime error**, after nine complete windows and at least 9,328 observed original decisions, BEFORE 10,000. The original test asserts `real world must not stop due to neural or physics error`; reported `Scelta neurale 3.0 non valida`. The original wrapper swallowed the precise RuntimeException class/message/site, so no scientifically justified actor/motor correction can be made yet. At the ninth completed window (9,001 decisions), signed 1375.54464, gross 4179.57675, signed/gross **32.9111%**, **4 blue**, **50 red**, **37 red** in first five windows and **13 red** in windows 6–9; 4,979 real local weight updates, 1,550 harmful local samples rejected. These are **partial**, NOT a passed 10,000-decision red rate; total red/blue already 12.5, far above 2. 45/50 red contacts had precontact momentum toward danger, 18/50 current intention away (<-0.3), 46/50 speed <2. This experiment substantially improves observed wild locomotion versus V445 1.4195% at full 10k, but safety remains unacceptable and completion failed.
- [Android regressions 41279152213](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41279152213): **PASS** including V446 three causal tests.
- [Original S21 33,312-decision/512-weight CRC 41279152258](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41279152258): **PASS**.

### Original S21 1000-decision windows
| Window | Signed/gross % | New blue | Static decisions | Opposed decisions |
|---|---:|---:|---:|---:|
| 1 | 26.3334 | 0 | 539 | 54 |
| 2 | 14.0010 | 1 | 207 | 244 |
| 3 | 30.3145 | 0 | 491 | 45 |
| 4 | -3.2666 | 1 | 385 | 228 |
| 5 | 28.6076 | 0 | 615 | 22 |
| 6 | 23.7534 | 0 | 914 | 0 |
| 7 | 16.1861 | 1 | 244 | 257 |
| 8 | 31.7120 | 0 | 563 | 31 |
| 9 | 3.8048 | 1 | 470 | 176 |
| 10 | 25.0907 | 0 | 451 | 76 |

### Wild 1000-decision windows (9 complete; no tenth because neural failure)
| Window | Signed/gross % | New blue | New red |
|---|---:|---:|---:|
| 1 | 13.9981 | 3 | 0 |
| 2 | 28.7665 | 0 | 4 |
| 3 | 21.2665 | 0 | 13 |
| 4 | 36.2101 | 0 | 9 |
| 5 | 30.4627 | 0 | 11 |
| 6 | 36.3591 | 0 | 7 |
| 7 | 41.3747 | 0 | 5 |
| 8 | 41.6319 | 0 | 0 |
| 9 | 40.7177 | 1 | 1 |

## Source-confirmed diagnostic blind spot
`Zero3OriginalBrainV356.actuate` catches every `RuntimeException` from the original `brain.choose` and motor boundary, then rethrows the same generic `IOException("Scelta neurale 3.0 non valida", failure)`. `GameActivity` saves only `failure.getMessage()` into `brain3Error`, and the original wild Android test reads only this field. Consequently V446 has a **real reproducible failure** but the current log does not identify the exception origin. Do NOT guess that V377, actor weights, physics or sensory input is definitely responsible. The 10k failure is not a test-harness false positive.

## Single V447 change and precommitted test
Change **only** the error message in the original `actuate` catch to include the original RuntimeException class, message and first stack-frame site, retaining its cause and identical `interrupt()`/fail-closed behavior. No control or learning parameter is changed. All four original Android workflows are configured for this new branch on one commit, including 2x10k, regressions and CRC. A future wild failure should reveal the exact source call for a subsequent **single causal correction**; if it unexpectedly passes, still enforce all red/blue and motor thresholds. Do not change the original actor or relax any acceptance threshold until a cause is measured. Historic V421–V423 12 jobs were rechecked on 2026-11-01: six behavior FAIL, six regression/CRC PASS, as detailed in V446 dossier.

**Status: NOT APPROVED.** No APK, OTA, stable merge, new saved brain or modification of the 12 slots.

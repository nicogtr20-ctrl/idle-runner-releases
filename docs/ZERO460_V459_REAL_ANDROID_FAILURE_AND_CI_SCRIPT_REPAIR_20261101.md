# ZERO460 — V459 original Android results and isolated regression-runner shell repair
Date: 2026-11-01. Experimental QA branch `lab/zero460-v459-original-regression-shell-repair`, parent exact V459 SHA `e27efdad1f40f5096114cf412c872c68e54a1a11`. No behavioral/physics/brain change in ZERO460. No APK/OTA/merge.

## V459 actual four GitHub Actions (same source SHA)
- [S21 original 41341861311](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41341861311): **FAIL behavioral**, 10,002 real decisions from original 33,312; signed/gross 10.3282%, 6/10 windows >=10%, 4 inverse windows, 4 new blue, 3,276 local updates. ZERO417_ORIGINAL_USER_S21_10000_RESULT decisions=43314 baseline=33312 blue=4 red=90 violet=0 signed=314.67862 gross=3046.78548 efficiency=0.103282 localWeightUpdates=3276 localHarmRejected=0 localTurnsFiltered=0 motorOwner=true.
- [Wild original 41341861101](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41341861101): **FAIL red/blue safety**, 10,002 real decisions; signed/gross 33.7226%, red 50 / blue 5, first 5k red 37, last 5k 13. ZERO417_WILD_REAL_10000_RESULT decisions=10002 baseline=0 blue=5 red=50 violet=0 signed=1609.57953 gross=4772.99305 efficiency=0.337226 localWeightUpdates=5441 localHarmRejected=1692 localTurnsFiltered=0 motorOwner=true.
- [Original Android regressions 41341861144](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41341861144): **FAIL before instrumentation**. Runner executed multiline `for test in ...` as a standalone `/usr/bin/sh -c` command and returned `Syntax error: end of file unexpected`; no individual Android test ran. Not a physics regression result.
- [Original saved S21 V356 CRC 41341861040](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41341861040): **PASS**, exact saved original 33,312 decision brain and 512 weights integrity Android test (`OK (1 test)`).

## Original contiguous 1000-decision windows (from full job logs)
| Window | S21 signed/gross | S21 blue | S21 red | Wild signed/gross | Wild blue | Wild red |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | 12.900% | 0 | 0 | 13.966% | 3 | 0 |
| 2 | 13.176% | 1 | 0 | 28.804% | 0 | 4 |
| 3 | 21.944% | 0 | 0 | 21.267% | 0 | 13 |
| 4 | -15.786% | 1 | 0 | 36.210% | 0 | 9 |
| 5 | 33.426% | 0 | 0 | 30.520% | 0 | 11 |
| 6 | 19.795% | 0 | 0 | 36.294% | 0 | 7 |
| 7 | -6.961% | 1 | 0 | 41.340% | 0 | 5 |
| 8 | -1.446% | 0 | 0 | 41.677% | 0 | 0 |
| 9 | -22.381% | 1 | 0 | 40.718% | 1 | 1 |
| 10 | 41.459% | 0 | 0 | 39.439% | 1 | 0 |

## Physical evidence and interpretation
- S21 V371 verified directions 1, V377 verified banks 2, V353 score evaluations 296661, unsupported PRE drift 203946.
- Wild V371 verified directions 0, V377 verified banks 1, V353 score evaluations 408404, unsupported PRE drift 341424.
- S21 motor local weight updates 3276, wild updates 5441, wild harmful motor credits rejected 1692. Wild contacts are genuine physical red/blue; S21 historical red=90 is inherited and not 90 new red contacts.
- V459 S21 10.33% versus V457 18.02%; four inverse windows versus V457 one. V459 wild 33.72% and 50 red/5 blue, identical headline behavior to V457; no evidence that PRE-coast subtraction solved either task.
- V459 V371 stores PRE-coast residual as bank response while V377 already stores speed-regime-conditioned residual. A V371 motor bank can be verified without a corresponding V377 reliable prediction; residual is not the same as absolute physical travel. This is a source-level risk to investigate separately, **not** evidence that an untested new direction should be forced.
- Original 194-dimensional sensor projection, GRU3, 170 material physical solver, V319 CH2, 512 motor weights and twelve save slots are unchanged by this QA branch. CRC PASS is direct evidence for the saved brain; broader preservation follows unchanged source files.

## Historical required V421–V423 job statuses independently rechecked
| Version | S21 | Wild | Android regressions | CRC |
|---|---|---|---|---|
| V421 | 41149391000 FAIL | 41149393560 FAIL | 41149404217 PASS | 41149399737 PASS |
| V422 | 41149758090 FAIL | 41149762948 FAIL | 41149771615 PASS | 41149767313 PASS |
| V423 | 41149983685 FAIL | 41149987734 FAIL | 41149995431 PASS | 41149992279 PASS |

## ONE isolated corrective change: fix the CI runner command boundary
- Before: multiline `script: |` contained `for`, `do`, `done` on separate lines. `reactivecircus/android-emulator-runner@v2` invoked each as separate `/usr/bin/sh -c` and the loop could not parse.
- After: single `script: bash .github/scripts/zero417-original-regressions.sh` invokes a checked-in full Bash script containing the identical Android test class list, all existing exact pass assertions, and `set -euo pipefail`.
- The four original workflows are enabled on this isolated branch to run on the **same final commit**. S21/wild remain unchanged V459 behavior and are expected to fail; this is a QA-only control, not a locomotion solution.
- If Android regressions fail after the shell fix, inspect actual per-test instrumentation and make no claims of PASS without logs. If tests are still running, retain branch and check later.

## Gate
NO approval: S21 <25%, fewer than 9/10 positive windows, four inverse windows, only 4 blue; wild 50 red/5 blue exceeds 2x even though 37→13 improves; V459 regression not executed. No APK/OTA/stable merge.
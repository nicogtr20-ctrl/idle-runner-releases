# ZERO456 — V455 matched V454 ABBA-OFF original Android forensics (research only)
Date: 2026-11-01. Documentation-only branch `audit/zero456-v455-matched-abba-control-results-20261101`, parent source `e103225daf0320f88f3849a1de6c61c241402c6b`. No behavioral source edits, no release, APK, OTA, merge or user checkpoint/slot writes.

## Prerequisites reviewed
Read `AGENTS.md`, ZERO419 whole-original-GRU3/194-sensor audit, ZERO420 V371/V377 eligibility, ZERO421 delivered V332 probe, ZERO422 learned-harm actor coverage, ZERO423 own-decision credit, ZERO454 ABBA protocol and ZERO455 matched-control protocol. Inspected actual completed original Android job logs for V421, V422 and V423: all six 10k behavioral runs FAIL; all six regression/CRC jobs SUCCESS. Historical results are from original job logs, not inferred from GitHub Actions green compilation.

| Version | S21 decisions | S21 signed/gross | S21 windows >=10% | S21 reverse | New blue | Wild decisions | Wild signed/gross | Wild red early→late | Wild blue | Regressions / CRC |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|
| V421 | 10001+ | 17.1723% | 8/10 | 2 | 4 | 10002 | 61.6262% | 35→52 | 3 | PASS / PASS |
| V422 | 10003 | 11.2881% | 7/10 | 2 | 4 | 10003 | 42.7053% | 19→33 | 5 | PASS / PASS |
| V423 | 10002 | 10.0486% | 7/10 | 2 | 4 | 10003 | 31.0766% | 14→12 | 4 | PASS / PASS |

V421 source runs: S21 41149391000, wild 41149393560, regression 41149404217, CRC 41149399737.
V422: 41149758090, 41149762948, 41149771615, 41149767313.
V423: 41149983685, 41149987734, 41149995431, 41149992279.
S21 historical redCum=90 is inherited from the saved user state, NOT new red contacts in the blue-only S21 room. Wild red counts are true physical harmful contacts. The 10-window boundaries are sampled at ~1000 genuine original decisions, not synthetic repeats.

## Matched comparison: V454 ON vs V455 OFF
`compare_commits(lab/zero454-motor-response-audit, lab/zero455-v454-matched-off-control)` shows only workflow names, two original Android test opt-in calls, two GameActivity logging lines and a new protocol document changed (no motor/GRU/physics algorithm replacement). The test source retains V421, V423, V420, V425/426, V432/433/434/435/442 and V422 QA flags, but deliberately does not invoke `originalV454EnablePairedMotorContrastForQA()`. This isolates the **combined** ABBA duty-cycle/credit-target intervention, not its two mechanisms individually. Both source variants keep the original V356 single 64-GRU actor, 194 sensors, 512 local weights, 170-material solver, V319 CH2 and checkpoint.

| S21 physical measure | V454 ABBA ON, run 41316640363 | V455 ABBA OFF, run 41320023593 |
|---|---:|---:|
| Original self-issued decisions | 10001 | 10002 |
| Signed / gross | 233.90274 / 2754.14043 | 570.09987 / 3163.54081 |
| Net efficiency | 8.4928% | **18.0209%** |
| Windows >=10% | 6/10 | **8/10** |
| Reverse windows | 3 | **1** |
| Actual new blue | 4 | 4 |
| Original local weight updates | 1079 | **3084** |
| V454 accepted paired-credit decisions | 1079 | 0 (mode disabled) |
| V454 rejected paired-credit decisions | 8640 | 0 (mode disabled) |
| Verified V377 banks | 2 | 2 |
| Verified V371 steering directions | 0 | 0 |
| V443 predicted motor scores | 8656 | 638 |
| V443 unsupported-drift scores | 164778 | 209970 |
| Android behavioral conclusion | FAIL | FAIL |

The matched OFF control improves S21 net efficiency by **+9.5281 percentage points** and restores 2005 additional actual local weight updates. This is direct empirical evidence that the **combined ABBA ON intervention is harmful on this original S21 fixture**. It does NOT identify whether the harm is caused by the altered physical ON/OFF muscle duty cycle, the credit estimator, or their interaction; it does not prove a generally optimal alternative. V455 reproduces the previous ~18.02% S21 plateau, so disabling ABBA alone does NOT solve locomotion.

### V455 actual S21 1000-decision windows
| Window | Original decision cursor | New blue in window | Signed | Gross | Signed/gross | Opposed movement decisions | Static movement decisions | Wall-contact frames |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| 1 | 34313 | 0 | 63.71582 | 241.95859 | 26.3334% | 54 | 539 | 24304 |
| 2 | 35313 | 1 | 78.05115 | 557.46994 | 14.0010% | 244 | 207 | 9132 |
| 3 | 36315 | 0 | 72.69370 | 239.79859 | 30.3145% | 45 | 491 | 24208 |
| 4 | 37314 | 1 | -11.31577 | 338.23087 | **-3.3456%** | 229 | 385 | 18460 |
| 5 | 38315 | 0 | 85.37612 | 296.69233 | 28.7760% | 21 | 615 | 19110 |
| 6 | 39313 | 0 | 28.62023 | 120.48873 | 23.7534% | 0 | 914 | 23856 |
| 7 | 40314 | 1 | 82.86147 | 511.93116 | 16.1861% | 257 | 244 | 7403 |
| 8 | 41313 | 0 | 67.51607 | 212.90387 | 31.7120% | 31 | 563 | 23984 |
| 9 | 42313 | 1 | 10.91298 | 275.74469 | **3.9576%** | 175 | 470 | 19455 |
| 10 | 43313 | 0 | 91.68940 | 368.20915 | 24.9014% | 77 | 451 | 16192 |

V455 final: original S21 saved baseline=33312, decisions=43314, signed=570.09987, gross=3163.54081, blue=4, inherited red=90, localWeightUpdates=3084, `motorOwner=true`. `ZERO417_WALL_TRACTION_STABILITY_10000 alignedWindows=8 oppositeWindows=1`. Physical trace: `creditCalls=3084`, `censored=6918`, `rearWallAccepted=2234`, `frontWallExcluded=2294`, `safeMotorMapAccepted=2911`, `safeMotorMapExcludedWall=5167`, `undeliveredRearMuscleCreditsRejectedV432=345`. The real body still frequently stalls and opposes its commanded movement. V443 has 279632 scoring evaluations, only 638 predicted and 209970 unsupported-drift; there are 0 verified V371 directions. This remains a physical control/learnability bottleneck, not evidence that optical preference should be imposed as a forced heading.

### V454 wild (ON) completed, but V455 wild OFF is not yet comparable
V454 wild run 41316640365: 10003 original decisions, signed=59.95076, gross=3724.88821, **1.6095%**, 58 real red / 4 blue, first five 1k windows 39 red and last five 19 red, 5904 weight updates, 72 motor predictions, 375212 unsupported drift, **0** verified V377 banks and V371 directions. FAIL.
V455 wild first attempt 41320023629 (job 124888304742) failed BEFORE any 1000-decision window at `AssertionError: Original sole GRU3 must really finish boot`. No valid red/blue comparison is possible. A retry of exactly the same failed job was requested via GitHub Actions; retry job 124910751553 was in progress at the time of this dossier. Do not classify its outcome in advance, and do not use the failed startup as evidence of an avoidance or movement regression. Read retry logs and all 10 windows when completed.

## Integrity and interpretation
V455 Android original regression run 41320023591 SUCCESS; original 33312 S21 V356 checkpoint / 512 weights CRC run 41320023578 SUCCESS. These technical PASS results do not substitute for behavior. V455 S21 behavioral run 41320023593 FAILURE. Wild run 41320023629 FAILURE on initial attempt; retry pending. No APK, OTA, stable merge, 2.2 restoration, pre-programmed directions, invented rewards, sensor topology change or 12 user-slot writes.

## Next causal decision
Keep V454 ABBA ON rejected. Wait for V455 wild rerun; compare real 10k red rates and V377 motor prediction coverage to V454 ON and V453 before making a **single** further behavioral edit. If startup fails again, investigate exact Activity boot lifecycle separately from learning (never silently skip GRU3 boot assertion). If wild runs but red/blue gate fails, the actual physical hazard prediction/candidate coupling and unverified V371 steering remain open. Any new edit requires an isolated experimental branch and full original S21+wild 10k, regression and CRC Actions on the same SHA. **ZERO remains NOT APPROVED.**

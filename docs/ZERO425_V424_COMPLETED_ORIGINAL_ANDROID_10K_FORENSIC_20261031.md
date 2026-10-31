# ZERO425 audit — V424 four original Android Actions COMPLETE, behavioral FAIL
Date 2026-10-31 UTC. Read AGENTS.md, docs/AGENTS.md, ZERO419..ZERO424 before analysis. **Forensic only**; original 33,312-decision S21/512 cortex weights, V356 CRC, CH2 V319, 194 senses, 170-material body, 12 saves, checkpoint and stable release untouched. No APK/OTA/merge.

## Exact CI and original state
- [V424 original trained S21 run 41154281984](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154281984): completed FAILURE; job 124397535291, Android instrumentation completed **10,003** genuinely additional decisions from original 33,312 to 43,315; user read-only brain reconstruction PASS, original sole GRU3 owner true. Failure is hard behavioral window criterion, NOT emulator startup.
- [V424 original newborn wild run 41154286099](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154286099): completed FAILURE; job 124397548211, **10,002** real decisions, actual red/blue physical contacts, NOT preboot censored.
- [V424 full original Android regressions 41154289430](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154289430): completed SUCCESS, job 124397558867, includes existing V420/V421/V422 cases. This is technical only, not behavioral.
- [V424 original 33,312 S21 512-weight V356 CRC 41154293160](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154293160): completed SUCCESS, job 124397570494. No original saved state modified.
- Both actual long-run logs verify flags `deliveredLocalProbeLabV421=true`, `immediateOwnDecisionCreditLabV423=true`, `safeMotorMapLabV420=false`, `independentDecisionCreditLab=false`. Four runs checked independently; different workflow-only trigger commit SHAs, no intervening production Java change.

## S21 exact physical windows (1,000 decisions each)
| Window | Signed | Gross | Signed/gross | Additional blue | Cortex updates cumulative | Opposed decisions | Static decisions | Solid contact frames |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 1 | 40.88290 | 228.47760 | 17.8936% | 0 | 191 | 62 | 549 | 24416 |
| 2 | 84.30475 | 554.51443 | 15.2033% | 1 | 640 | 247 | 224 | 8647 |
| 3 | 51.04146 | 222.70571 | 22.9188% | 0 | 806 | 45 | 576 | 24192 |
| 4 | -49.55078 | 318.59275 | -15.5530% | 1 | 1084 | 245 | 444 | 18334 |
| 5 | 56.89776 | 274.84269 | 20.7019% | 0 | 1395 | 25 | 648 | 19708 |
| 6 | 13.04101 | 104.57299 | 12.4707% | 0 | 1476 | 1 | 921 | 24096 |
| 7 | -15.41348 | 448.09715 | -3.4398% | 1 | 1970 | 292 | 265 | 6915 |
| 8 | -105.78849 | 221.03029 | -47.8615% | 0 | 2492 | 240 | 645 | 4517 |
| 9 | -58.77284 | 194.69623 | -30.1869% | 1 | 2687 | 198 | 702 | 18872 |
| 10 | 137.29083 | 441.07495 | 31.1264% | 0 | 3149 | 110 | 300 | 10954 |

Totals: **signed 153.93312, gross 3008.60479, efficiency 5.1164%**, only **6/10 windows >=10%**, **4 inverse windows** (4,7,8,9), **4 new real blue** (historical redCum=90, new red=0 because S21 room blue-only). `l400 creditCalls=3149 weightUpdates=3149 censored=6854 rearWallAccepted=1767 frontWallExcluded=1673 velocityCreditSamples=10003`. Physical solver remains extremely wall-bound: 921 static decisions in window 6 despite true active learner; changing the gradient source does not establish useful steering. Gate FAIL: <25%, <9 windows, inverses>0, blue<5.

## Wild exact physical windows
| Window | Blue new | Red new | Signed/gross |
|---:|---:|---:|---:|
|1|3|0|16.3253%|
|2|0|0|27.9915%|
|3|1|0|31.8260%|
|4|0|0|33.6796%|
|5|0|4|41.5176%|
|6|0|0|58.6242%|
|7|0|4|27.9484%|
|8|0|17|56.5487%|
|9|0|0|9.1730%|
|10|0|0|48.6537%|

Totals **10,002 decisions, 4 blue, 25 red, signed 1352.63893, gross 3795.55237, efficiency 35.6375%, 4798 local weight updates, 1781 harmful credit rejections**. First 5,000 RED **4**, last 5,000 RED **21**; red:blue **6.25:1**. The wild locomotion efficiency passes 12% but BOTH harm gates FAIL (late rate worsens; red>2×blue). Actual red physical logs at decision 7,101 show issued intention toward red 0.978 and learnedVisible=true with three options. Wild had blue only in first 3,000 and 21/25 red after 5,000; this is not learned sustained avoidance.

### V423 wild rerun: prior preboot censor resolved (important new comparator)
The original V423 [wild run 41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) **attempt 2** is now actually complete with 10,003 real original decisions, unlike its first preboot failure. Job 124396959851: **4 blue, 26 red**, signed 1209.86984, gross 3893.18490, efficiency 31.0766%, 4,712 cortex updates. Red windows by 1k: **[0,0,2,12,0,0,6,0,0,6]**, first 5k **14**, last 5k **12**. This means V423 DID pass the *direction of red-rate improvement*, but **FAILED red <=2×blue** (26 >8). V423 opt-ins verified: immediateOwnDecisionCreditLabV423=true, deliveredLocalProbeLabV421=false. V424's 25 vs 26 red overall is only one contact different while its late red pattern deteriorates dramatically (4→21 versus V423 14→12); avoid claiming V424 improves hazard learning. All 12 originally specified V421/V422/V423 runs rechecked: all six behavioral 10k are completed/failure, all six original regression/CRC jobs completed/success. The V423 wild attempt-2 log resolves the earlier censor, not the failure.

## Causal interpretation and next isolated experiment
V424 combined *previously separately implemented* V421 delivered muscle perturbation and V423 same-decision credit. More physically valid synaptic attribution produced **3,149 S21 updates**, but S21 efficiency was 5.12%, versus 10.05% for V423 and 17.17% V421 (different policies, no inference of monotonic improvement). Wild red total 25 is lower than V421 87/V422 52 but late red 21>4, so no safety claim.

Further independent **source-confirmed gradient inconsistency** (not yet behavioral proof): `Zero3RecurrentBrain.choose` clips and unit-normalizes the 2D GRU actor proposal plus Gaussian perturbation before sending the chosen physical heading. `completeConsequence` nevertheless credits each unnormalized raw `lastNoise[0..1]` as if the solver received its radial and saturated components. Radial perturbation changes vector magnitude but not the normalized direction; clipped axes also have zero local derivative. In addition, cognition selection among candidates biases the sampled distribution; do NOT claim this second issue solved by a simple tangent projection. Proposed V425 one-variable **research-only** tangent/clipping-aware high-level actor credit, with Android radial/tangent/saturation regression plus full original 2×10k and CRC, on new isolated lab branch. This is a causal mathematical eligibility correction, NOT a forced heading or reward, and may still fail because of candidate-selection bias, body shear, vision, or physical steering. Pre-register hard gates unchanged and reject if failed.

## Decision
**V424 REJECTED for deployment.** No release or migration. Preserve exact S21 checkpoint, all original material, GRU3, V319 and 12 saves. Recheck only new genuine 10k original runs; no tuning thresholds after results.

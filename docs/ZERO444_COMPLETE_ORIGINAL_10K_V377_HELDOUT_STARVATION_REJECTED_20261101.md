# ZERO444 — completed original Android 2×10k forensic: clean evidence is NOT missing

2026-11-01, documentation-only audit branch `audit/zero444-complete-10k-heldout-evidence-20261101`, source experiment `lab/zero444-original-v377-heldout-evidence-diagnostics`, exact CI commit `8911fadc0fcbd3d9cded075f674379ab74969759`. Read AGENTS.md and ZERO419–ZERO423 first. **NO locomotion fix demonstrated. NO APK, OTA, merge, brain reset, old 2.2 controller, scripted steering or reward changes.**

## Verified completed GitHub Actions (actual jobs/logs)
- [S21 41275012694](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41275012694), job 124755319136: **FAIL**, real trained original S21 baseline 33312, end 43315, 10003 additional decisions, 4 new blue, 0 new red (90 pre-existing), signed 307.42393, gross 2758.91777, signed/gross **11.1429%**, 2754 cortex updates, 6/10 windows >=10%, 2 negative windows (4,9).
- [Wild 41275012471](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41275012471), job 124755318242: **FAIL**, 10003 real decisions, 4 blue, 49 red, signed 52.38459, gross 3666.77884, **1.4286%**, 5450 cortex updates, 1675 rejected harmful local credits. Red 29 in first 5000, 20 in last 5000 (only red-rate trend passes); red/blue 49/4=12.25 >2.
- [Original Android regressions 41275012428](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41275012428), job 124755318377: **PASS**.
- [Actual original S21 33312/512 weights/V356 CRC 41275012497](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41275012497), job 124755318277: **PASS**.
All four checked from completed GitHub jobs and actual logs; no in-flight result remains.

## All 10 S21 real 1000-decision windows
| Bucket | signed | gross | efficiency % | blue new | weight updates cumulative |
|---:|---:|---:|---:|---:|---:|
|1|54.01813|235.08394|22.9782|0|167|
|2|5.48243|461.66566|1.1875|1|585|
|3|27.04048|110.61708|24.4451|0|693|
|4|-51.56521|261.81334|-19.6954|1|899|
|5|96.01265|311.58672|30.8141|0|1256|
|6|44.74309|154.56309|28.9481|0|1421|
|7|43.30420|486.44481|8.9022|1|1851|
|8|47.26900|156.12302|30.2768|0|2085|
|9|-8.26970|240.18794|-3.4430|1|2375|
|10|49.36025|340.60004|14.4921|0|2754|

## All 10 wild real 1000-decision windows
| Bucket | signed | gross | efficiency % | blue window | red window | red cumulative |
|---:|---:|---:|---:|---:|---:|---:|
|1|53.48659|382.96564|13.9664|3|0|0|
|2|117.76229|408.83830|28.8041|0|4|4|
|3|102.50429|436.29995|23.4940|1|15|19|
|4|44.80317|444.99658|10.0682|0|10|29|
|5|28.50201|325.67140|8.7518|0|0|29|
|6|-26.95005|401.33531|-6.7151|0|0|29|
|7|-117.13314|399.93367|-29.2881|0|11|40|
|8|-94.06033|312.24200|-30.1242|0|9|49|
|9|-50.89148|290.73593|-17.5044|0|0|49|
|10|-5.22551|263.34119|-1.9843|0|0|49|

All four blue wild pickups occurred by the end of bucket 3; no further pickups during the remaining ~7000 decisions. Note the 1000-window samples end at 10002; the final result includes the last decision and is authoritative for totals.

## V444 actual V377 TRAIN / independent HELDOUT diagnosis
| Metric | S21 original | wild original |
|---|---:|---:|
| Accepted clean TRAIN | 2173 | 5260 |
| Accepted independent HELDOUT | 559 | 1279 |
| TRAIN low speed (<2) | 1927 | 5012 |
| HELDOUT low speed (<2) | 488 | 1220 |
| TRAIN high speed (>=2) | 246 | 248 |
| HELDOUT high speed (>=2) | 71 | 59 |
| Stored TRAIN / HELDOUT | 21625 / 5416 | 5260 / 1279 |
| Banks with >=4 TRAIN | 16/16 | 16/16 |
| Banks with >=3 HELDOUT | 16/16 | 16/16 |
| Banks with coastLoss > .10 | 8/16 | **1/16** |
| Banks with heldout loss < .90 × coastLoss | 7/16 | **12/16** |
| Banks passing BOTH reliability conditions | 5/16 | **1/16** |
| Reliable low / high speed banks | 1 / 4 | **0 / 1** |
| V371 verified directional banks | 0 | 0 |
| V443 candidate score evaluations | 278262 | 413901 |
| V377 predicted evaluations | 6573 | 861 |
| Unsupported PRE-drift evaluations | 190393 | 323563 |
| Nonzero delayed real contact links | 395 | **0** |

**Substantial finding:** V377's wild scarcity of trusted motor predictions is NOT due to a lack of clean samples: all 16 direction×speed banks independently exceed sample minima. Eleven banks pass relative heldout loss reduction but fail the existing absolute `coastLoss>.10` test; only one bank has enough response magnitude. This is compatible with weak physically controllable movement / inadequate action-conditioned state representation, NOT proof that lowering the absolute gate would make steering safe. Source `Zero3InertialResponseV377.reliable()` uses >=4 TRAIN, >=3 HELDOUT, `coastLoss>.10` and `loss<.90*coastLoss`. `reliableSteeringDirectionsV383` additionally requires positive measured forward response >.10 in at least two headings. No fake certification or arbitrary threshold relaxation.

## Actual RED-contact body / retina / actor forensic
From the 49 `ZERO404_HAZARD: REAL_MATERIAL_CAUSALITY sign=HARM` records in the wild job: **45/49** had measured PRE velocity toward the harmful object; **33/49** had current neural intention away; **48/49** PRE speed <2; **47/49** offered 7 actor alternatives after learned harm; **47/49** `learnedVisible=true`; **33/49** selected optical score lower than the best of offered alternatives; **34/49** center-azimuth `opticalAtTouch=0` (NOT evidence that the protruding-eye retina did not see it, per ZERO419). The actor has learned negative consequences but still cannot reliably arrest real inertial motion. No assertion that 7 candidates alone solve it.

## Historic V421–V423 original 12 Actions rechecked directly
| Version | S21 10k | wild 10k | first→last red | blue/red wild | regressions | CRC |
|---|---|---|---|---|---|---|
|V421|[41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL, 10001, 17.1723%, 8/10, 2 inverse, 4 blue|[41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL, 10002, 61.6262%|35→52|3/87|[41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS|[41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS|
|V422|[41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL, 10003, 11.2881%, 7/10, 2 inverse, 4 blue|[41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL, 10003, 42.7053%|19→33|5/52|[41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS|[41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS|
|V423|[41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL, 10002, 10.0486%, 7/10, 2 inverse, 4 blue|[41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL, 10003, 31.0766%|14→12|4/26|[41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS|[41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS|

## Next strictly causal investigation
Measure the actual 16 bank-specific `meanX/meanY`, forward projection, heldout `loss/coastLoss`, and whether predicted low-speed direction is physically large enough to brake/steer. Do this **read-only** before changing any confidence gate, reward, action policy, muscle force, sensor or saved weight. Only then consider one isolated behavior change, tested against both full original 10k gates and CRC/regressions.

**NO RELEASE.** Exact original S21 33312 decision V356 checkpoint, 512 weights, CH2 V319, 194 sensors, 170-material body, original 12 save slots and installed stable phone remain untouched.

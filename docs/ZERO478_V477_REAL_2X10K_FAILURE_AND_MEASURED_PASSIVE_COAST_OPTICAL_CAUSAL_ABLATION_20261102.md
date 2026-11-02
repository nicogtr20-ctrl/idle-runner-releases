# ZERO478 — V477 actual Android 2×10k FAIL; measured passive coast vs unsupported optical zero
Date: 2026-11-02. Research-only branch `lab/zero478-measured-passive-coast-learned-optical-risk`, based exactly on V477 `e5df844c531ca9c85daa0f3cf367bc35505a2341`. No APK, OTA, merge or save mutation.

## Required historical verification
Read AGENTS.md, ZERO419..ZERO423 and actual GitHub Actions job logs. V421 S21 [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL 17.1723%, 4 blue, inverse 4/9; wild [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL 87 red/3 blue, red 35→52, signed/gross 61.6262%. Regressions [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS, CRC [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS.
V422 S21 [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL 11.2881%, 4 blue, inverse 4/9; wild [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL 52 red/5 blue, red 19→33, 42.7053% progress. Regression [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS; CRC [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS.
V423 S21 [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL 10.0486%, 4 blue, inverse 4/9; wild [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL 26 red/4 blue, red 14→12, 31.0766% progress. Regression [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS; CRC [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS. Historical workflows may have different SHAs across gates; do not infer same-SHA approval.

## V477 actual same-commit results
All four workflows ran `e5df844c531ca9c85daa0f3cf367bc35505a2341`. S21 [41463978053](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41463978053) FAIL: 10,002 real new decisions (33,312→43,314), 290.814 signed / 2758.996 gross = **10.5406%** (1000-window snapshot 10.5335%), 6/10 windows >=10%, two reverse windows 4/9, 4 new blue, 2,689 cortex updates. Wild [41463977841](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41463977841) FAIL: 10,005 genuine decisions, 9.46836 signed / 3602.24726 gross = **0.2628%**, 56 red/3 blue, red 18 first 5000 →38 last, 5,566 cortex updates. Android regressions [41463977936](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41463977936) PASS, original S21 CRC [41463978166](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41463978166) PASS. Note S21's 90 cumulative red is old fixture history, NOT new red contacts in blue-only room.

### V477 S21 consecutive ~1000 decision windows
| bucket | signed | gross | signed/gross | new blue |
|---|---|---|---|---|
| 1 | 46.77242 | 237.17289 | 0.197208 | 0 |
| 2 | 6.24989 | 476.91367 | 0.013105 | 1 |
| 3 | 19.00645 | 106.98953 | 0.177648 | 0 |
| 4 | -59.46806 | 301.11412 | -0.197493 | 1 |
| 5 | 113.91148 | 286.28207 | 0.397899 | 0 |
| 6 | 29.37093 | 157.82753 | 0.186095 | 0 |
| 7 | 43.53761 | 452.23765 | 0.096272 | 1 |
| 8 | 43.04351 | 166.72444 | 0.258172 | 0 |
| 9 | -52.40245 | 316.0367 | -0.165811 | 1 |
| 10 | 100.56014 | 257.35094 | 0.390751 | 0 |

### V477 wild consecutive ~1000 decision windows
| bucket | signed | gross | signed/gross | new blue | new red |
|---|---|---|---|---|---|
| 1 | 12.58351 | 379.91408 | 0.033122 | 3 | 0 |
| 2 | -12.93747 | 283.3227 | -0.045663 | 0 | 0 |
| 3 | 20.29902 | 373.58985 | 0.054335 | 0 | 0 |
| 4 | 19.37799 | 390.81995 | 0.049583 | 0 | 10 |
| 5 | -26.09817 | 353.0005 | -0.073932 | 0 | 8 |
| 6 | 59.8732 | 267.48247 | 0.22384 | 0 | 0 |
| 7 | 20.02905 | 391.89558 | 0.051108 | 0 | 6 |
| 8 | 14.6936 | 421.70768 | 0.034843 | 0 | 14 |
| 9 | -59.36279 | 365.08146 | -0.162601 | 0 | 9 |
| 10 | -38.95994 | 375.1329 | -0.103856 | 0 | 9 |

Wild exact red rate first 5000 = 18/5000 = 0.0036 per decision; last 5000 = 38/5000 = 0.0076, 2.11× WORSE. Red/blue 56/3 = 18.67×, threshold <=2×. Blue 3/3 occurred in first 1000, none afterward.

### Motor/GRU3/sensor/physical evidence
V477 S21 `scoreEvaluations=291372, predicted=6055, unsupportedDrift=201003, opticalDriftSuppressed=191323, v476MidTrain=838, v476MidHeldout=183, v476MidReliable=0, v477MidOpposingPreRejected=776, verifiedV377Banks=4, verifiedV371Directions=2`.
V477 wild `scoreEvaluations=409436, predicted=24417, unsupportedDrift=289454, opticalDriftSuppressed=288542, v476MidTrain=1466, v476MidHeldout=444, v476MidReliable=2, v477MidOpposingPreRejected=2863, verifiedV377Banks=6, verifiedV371Directions=0`. The wild V444 stricter original 16 banks have zero heldout-reliable banks, despite six under V446. 52/56 true harm events had positive PRE momentum toward source; 42/56 PRE speeds in [0.35,2), 54/56 had `learnedVisible=true`, but only 11/56 commanded intention toward harm >0.3. Offered alternatives: 3 (2 events), 5 (27), 7 (27). `chosenMotor=0` on 52/56. These are measurements of actual event logs, NOT proof of a single root cause. V477 rejects many passive braking confounds but does not make ZERO brake or learn to avoid red.

## Single V478 source-level causal ablation
The original V433/V442 strict gate correctly refuses to pretend an unvalidated commanded heading is real travel. However, when PRE COM velocity is already measured and nontrivial, it returns **exactly zero** optical risk and instrumental score for most unsupported commands, despite actual inertial travel continuing toward a previously experienced harmful wavelength. This makes unsupported actions appear neutral when their *shared, action-independent* passive momentum is physically risky. The 52/56 observed harm contacts with momentum toward hazard and 289,454 unsupported-drift evaluations motivate testing this precise missing physical baseline.

V478 lab-only flag, enabled AFTER original V433/V434/V435/V442 on genuine original GameActivity decision boundary: for unverified command under measured drift, use the real 194D PRE COM velocity (191/192 ×130), a 0.30s passive displacement, and existing V369 genuinely learned spectral value to calculate risk. **The passive value is identical for every unverified candidate heading.** No fictitious motor authority, no assumed braking, no known red/blue label, no invented reward, no preset escape direction. Previously verified V377 motor predictions and stationary behavior unchanged. The passive-only score excludes delayed action-bank bonuses and stall penalties, so it cannot manufacture command-specific motor evidence. Adds transient `v478PassiveScore` and `v478PassiveOptical` counts. The actual passive coast can be altered by viscosity/collisions: this is a short-horizon physical baseline, not a validated future trajectory. A/B must decide efficacy.

Added `Zero478MeasuredPassiveCoastOpticalAndroidTest`: known real harm with east PRE drift and east/west unverified commands same negative risk; reversed PRE drift positive sign; learned benefit positive and no learned contact neutral; stationary behavior and V433/V442 prerequisites unchanged. Existing 6-case V476/V477 passive brake suite still required. No schema or physics changes.

## Four unchanged strict acceptance gates
1. Actual saved 33,312 S21 512-weight GRU3, original 170-material/CH2 V319/194D/12 slots, >=10000 real decisions, >=25% signed/gross, 9/10 windows >=10%, 0 reverse, >=5 newly collected blue.
2. Actual wild >=10000 real decisions, >=12% signed/gross, real red last 5000 < first 5000 and total red <=2×blue.
3. Full original Android regressions and new V478 cases PASS.
4. Exact original V356 CRC/512 weights and 12 slots PASS. Same SHA for all four, otherwise NOT approved.

**STATUS:** V478 experimental commit prepared, Android Actions not yet proven. No APK, OTA, merge, phone change or saved-brain replacement. If V478 fails, do not claim locomotion repaired; inspect real score counter reachability and per-contact PRE inertia, then isolate next hypothesis.

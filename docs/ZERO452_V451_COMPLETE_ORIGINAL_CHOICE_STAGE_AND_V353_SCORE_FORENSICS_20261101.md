# ZERO452 — V451 complete original Android 10k choice-stage forensics and V353 score-component experiment
Date: 2026-11-01. Branch: `lab/zero452-cognition-score-component-red-forensics`. **Research only. No APK, OTA, merge, brain reset, or saved-slot rewrite.**

## Mandatory baseline and historical verification
Read root AGENTS.md, docs/AGENTS.md, ZERO419–ZERO423, master blueprint, implementation contract and UI contract. Re-fetched all twelve historical V421/V422/V423 GitHub Actions statuses and the six complete behavioral job logs. All six historical behavioral tests FAIL; all six corresponding regression/CRC runs PASS. Confirmed values from genuine 1000-decision log windows:
| Version | S21 signed/gross | S21 good windows | inverse | new blue | wild signed/gross | red first/last 5000 | wild red/blue | cortex updates S21/wild |
|---|---:|---:|---:|---:|---:|---|---|---|
| V421 | 17.1723% | 8/10 | 2 | 4 | 61.6262% | 35/52 | 87/3 | 1918/5843 |
| V422 | 11.2881% | 7/10 | 2 | 4 | 42.7053% | 19/33 | 52/5 | 1851/4587 |
| V423 | 10.0486% | 7/10 | 2 | 4 | 31.0766% | 14/12 | 26/4 | 2962/4712 |

Historical run IDs in order S21/wild/regression/CRC: V421 41149391000/41149393560/41149404217/41149399737; V422 41149758090/41149762948/41149771615/41149767313; V423 41149983685/41149987734/41149995431/41149992279.

## V451 exact-commit Android results (bdeda7a4c5691f56953520f6823250f18364d674)
- Original S21 [41300516860](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41300516860): **FAIL**, 10,002 new original GRU3 decisions, original 33,312 saved prior; signed=570.09987, gross=3163.54081, efficiency=18.0209%; 8/10 1000-decision windows >=10%, one inverse, 4 new blue, 3084 real local weight updates. Original S21 redCum=90 is inherited cumulative state, **not** 90 new red collisions.
- Original wild [41300516811](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41300516811): **FAIL**, 10,003 true original GRU3 decisions; signed=1609.91210, gross=4773.54255, efficiency=33.7257%; 50 actual red vs 5 blue; first/last 5000 red=37/13; 5441 real local cortex updates, 1692 harmful-credit rejections. Red decreasing PASS, red<=2*blue FAIL.
- Original Android regressions [41300516796](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41300516796): PASS. Original saved S21 V356 512-weight CRC [41300516830](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41300516830): PASS.
- Both complete behavioral logs were read, including all 10 authentic 1000-decision buckets each, real hazard touch traces, cortex update counts and motor evidence.

| Window | S21 signed/gross % | S21 new blue | S21 static/opposed | wild signed/gross % | wild new blue | wild red |
|---|---:|---:|---:|---:|---:|---:|
| 1 | 26.2683 | 0 | 540/54 | 13.9308 | 3 | 0 |
| 2 | 14.0369 | 1 | 208/244 | 28.7785 | 0 | 4 |
| 3 | 30.2511 | 0 | 489/45 | 21.2903 | 0 | 13 |
| 4 | -3.3291 | 1 | 385/229 | 36.2105 | 0 | 9 |
| 5 | 28.7550 | 0 | 616/21 | 30.4642 | 0 | 11 |
| 6 | 23.6475 | 0 | 917/0 | 36.3271 | 0 | 7 |
| 7 | 16.2163 | 1 | 240/257 | 41.3643 | 0 | 5 |
| 8 | 31.7120 | 0 | 563/31 | 41.7034 | 0 | 0 |
| 9 | 3.5907 | 1 | 470/177 | 40.6766 | 1 | 1 |
| 10 | 25.3163 | 0 | 452/75 | 39.4576 | 1 | 0 |

## Causal localization of actual wild red contacts
Parsed **all 50** real `ZERO404_HAZARD: REAL_MATERIAL_CAUSALITY sign=HARM` lines. Every event has V451 stage trace. **0/50** changed candidate between cognition V353 and random V428; **0/50** changed between random V428 and planner V354. **48/50** did not use executive option recall. In **16/50**, final selected optical utility was >0.10 below the best actually offered actor option. In **14/16** of those, the optical disadvantage was already present at the V353 cognition stage. In **2/16**, V353's selected candidate had a substantially better optical score but V354 executive recalled a historically learned option, reducing optical utility (decision 3357, best 0.19222239, cognition 0.16587937, final -0.18187374; decision 3808, best 0.059824143, cognition 0.043117322, final -0.053082366).

This **does not prove** V353 selected the wrong option: its real score combines physical motor V371/V377, V374 optical preference (which is travel/inertia dependent), embodied cost, and optionally heldout energy/damage predictions. The V405 raw V369 optical score alone is not the full policy objective. Similarly, the executive's V374 physical preference can differ from raw optical preference. Never force a best optical heading or invent a red penalty.

## Single isolated V452 intervention: read-only decomposition of existing V353 selection
Source audit: `Zero3CognitionV353.select()` computes `movementScore + opticalPreferenceV374 + embodied.learnedCost + (trusted energy-damage-cost-uncertainty)`, then applies its original empirical margin against actor candidate zero. Existing V451 telemetry did not expose those score components. To test whether a physical model, a trusted prediction, an embodied cost or the margin explains the 14/16 optical gaps, V452 captures the **already computed** actual per-option values in transient nonpersistent QA fields. The new `choiceScoreComponentsV452()` reports V353's chosen index and best raw-optical index, their individual motor, optical, embodied, predictive and total scores and best-total index. The existing `choiceStageTraceForQAV451()` appends these values at actual real material contacts. No score weighting, random sampling, GRU state, executive policy, motor control, learning gradient, energy outcome, physics, sensory dimension, serialized schema or checkpoint is changed.

The algebraic score and all original decision branches are preserved; extra `contactValence.utility()` is a pure read of real existing learned evidence. If original S21/wild behavior diverges materially from V451, investigate instrumentation side effects before drawing any inference. This is **not** a locomotion fix.

### Required V452 Android gates
Push to isolated V452 branch triggers the exact original 10k S21, original 10k wild, regression and CRC workflows. Verify exact commit SHA and actual jobs, not just dispatch. Read 10 1k windows per behavioral run, compare first/last 5000 real red and blue, signed/gross, cortex gradients, motor evidence and physical inertia, 194D/GRU3 state, and decompose every HARM contact. If V353 chose an optically worse option, distinguish: V371/V377 motor estimate, V374 optical transform, embodied cost, trusted energy/damage, or margin. Only then consider one causal behavioral change on a **new** branch with original Android A/B tests. If any gate fails, no release.

### Hard acceptance
S21 saved original >=10,000 real decisions, >=25% signed/gross, >=9/10 windows >=10%, zero inverse windows, >=5 new blue. Wild original >=10,000 real decisions, red last 5000 strictly less than first 5000, total red <=2*blue. Original Android regressions and 33312/512 V356 CRC both PASS. Preserve CH2 V319, 194 sensors, 170-material original body, all 12 user save slots; never enable V2.2 or authored headings.

## Exact-commit V452 Android runs (pending; not PASS)
The four *same-commit* original workflows were actually dispatched by push on `e302fd71d92ff8329f7d294d1c7759462405a595` (an additional comment-only V356 edit to trigger the original CRC workflow, with no code behavior change):
- [S21 original 10000 — 41307947006](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307947006)
- [Wild original 10000 — 41307947019](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307947019)
- [Android regressions — 41307946994](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307946994)
- [Original S21 V356 CRC — 41307946992](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307946992)

Earlier branch-edit pushes produced superseded runs at different commit SHAs; **do not use those** to approve V452. At the last status check these four exact-commit runs were pending/in progress. Their actual 10k outcomes remain unknown. No behavioral repair is claimed.

# ZERO473 — V472 real 2×10k FAIL; V371 heldout motor attribution must subtract PRE COM coast
Date: 2026-11-02. Isolated branch `lab/zero473-v371-pre-inertia-residual-credit` based on V472 `6c78b9a5897de0fb122d3d0bbbee5b7dc9ae6a03`. **EXPERIMENTAL, NOT RELEASE APPROVED.** No APK, OTA, stable merge, user saved slots or checkpoint changes.

## Mandatory source and historical verification
Read `AGENTS.md`, ZERO419–ZERO423 dossiers and checked actual GitHub Actions job logs for all 12 historical runs. All six original 10k behavioral runs FAIL; all six regression/CRC runs PASS.

| Version | S21 10k original (efficiency; qualifying windows; inverse; new blue) | Wild original 10k (red/blue; first/last 5000) | Android regression / CRC |
|---|---|---|---|
| V421 | 10,001; 17.1723%; 8/10; 4,9; 4 | 10,002; 87/3; 35→52 | PASS/PASS |
| V422 | 10,003; 11.2881%; 7/10; 4,9; 4 | 10,003; 52/5; 19→33 | PASS/PASS |
| V423 | 10,002; 10.0486%; 7/10; 4,9; 4 | 10,003; 26/4; 14→12 | PASS/PASS |

Original run IDs V421: 41149391000, 41149393560, 41149404217, 41149399737. V422: 41149758090, 41149762948, 41149771615, 41149767313. V423: 41149983685, 41149987734, 41149995431, 41149992279. These historical runs are not evidence of approval.

## NEW V472 actual Android result — all four completed on `6c78b9a5`
- [S21 original](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41423095523): **FAIL**, 10,001 self-generated new decisions from saved original 33,312. Final signed **230.50352**, gross **2660.95998**, efficiency **8.6624%**, **6/10** windows >=10%, **2 inverse** (4,9), **4** newly collected blue, **4** genuine rebirths. 2,627 physically eligible local weight updates. The 10 independent thousand-decision signed/gross windows are:
  1. +46.54650 /236.57784 = +19.6749%;
  2. +6.79545 /476.00841 = +1.4276%;
  3. +20.37855 /102.91442 = +19.8015%;
  4. -70.55368 /292.77530 = **-24.0982%**;
  5. +104.56970 /265.39261 = +39.4019%;
  6. +41.58868 /154.20158 = +26.9703%;
  7. +8.87119 /428.00100 = +2.0727%;
  8. +46.40911 /159.09941 = +29.1699%;
  9. -61.02149 /299.62516 = **-20.3659%**;
  10. +86.89036 /246.21464 = +35.2905%.
- [Wild original](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41423095529): **FAIL**, 10,001 real decisions, signed **-167.91136**, gross **3400.68622**, signed/gross **-4.9376%**, **3 blue / 20 red**, red first 5000 **9** → last 5000 **11** (worse); 5,152 local updates, 1,820 harm-rejected local credits. 10 independent thousand-decision red counts: `0,0,0,9,0,1,4,0,0,6`; blue counts: `3,0,0,0,0,0,0,0,0,0`; signed/gross per window: `+.036087,+.026620,+.010059,+.040325,-.060338,-.159655,-.088347,-.110337,-.092865,-.092187`. Wild net motion becomes increasingly reverse after the fifth window, despite lower absolute red count than V471.
- [Original Android regressions + V472 mathematical gate](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41423095527): **PASS**.
- [Exact saved original S21 33,312 / 512 weights / V356 CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41423095639): **PASS**.

## Source-level causal diagnosis and bounded V473 hypothesis
V472 removed V371's squared-displacement floor and certified **3 S21** and **2 wild** V371 headings, whereas V471 certified zero. However V472 wild had **0 reliable V377 banks**, 283,407 unsupported-drift candidate evaluations and 266,024 optical-drift suppressions. S21 had 5 V377 reliable banks, 190,671 unsupported-drift evaluations and 178,855 optical suppressions. V472's independently heldout V371 training/validation is still fit to **total** decision displacement `dx,dy`, which includes **already present PRE-decision center-of-mass velocity**. Repeatable passive coasting can therefore beat a zero-motion baseline and certify a heading without showing that the motor actually caused the travel. The severe V472 behavioral regression is consistent with this misattribution, but is not proof it is the sole cause.

**Exactly one new V473 behavioral change**: inside `Zero3InstrumentalCreditV371.complete`, use measured residual `actual displacement normalized to .30s - .30s × actual PRE COM velocity` for V371's TRAIN/HELDOUT `learnedX/Y`, `error` and `baseline`. The PRE COM velocity is already captured from original real 194D senses `191..192` by `begin` and scaled to physical units by 130. This isolates active change from passive coast. `Zero3InertialResponseV377.finish` still receives original full `dx,dy`; stall detection, genuine resource consequences, six-decision historical contact traces, real physics, CH2 V319, GRU3 actor, body solver and all rewards are unchanged. External forces/friction can still affect the residual; it is not guaranteed causal muscle proof, and no automatic acceptance is inferred.

New `Zero473V371PreInertiaResidualAndroidTest` checks: (1) repeatable passive drift cannot certify steering; (2) a real forward displacement ABOVE measured PRE drift can; (3) negative active residual cannot. These are mathematical source regressions, NOT synthetic survival evidence. All four unchanged original Android workflows (S21 >=10k, wild >=10k, regression, CRC) are enabled on isolated V473 branch; all must run against the same final commit. Record results here after completion, including 10 windows, red/blue, gradients, V371/V377 and exact CRC.

## Unchanged acceptance and preservation
S21 >=10k, >=25% signed/gross, >=9/10 windows >=10%, ZERO inverse windows, >=5 true new blue. Wild >=10k, >=12% signed/gross, real blue, red latter 5000 < first 5000, red total <=2× blue. Regression and exact saved CRC must both PASS. Preserve original trained S21 33,312 decisions, 512 weights, V356 CRC, V319 CH2, 194 sensors, original 170-material physical body and all 12 user save slots. No V2.2, invented reward, prescribed heading, APK, OTA or merge.

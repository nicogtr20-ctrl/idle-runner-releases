# ZERO474 — V473 real 2×10k FAIL; repair V371 residual-as-travel scoring (LAB ONLY)

Date: 2026-11-02. Branch `lab/zero474-v371-residual-physical-travel-reconstruction`, parent V473 `a597a7b1808dcc827ef7ce1217e7e2a7d5bf6eaf`. NO RELEASE, NO APK/OTA, NO MERGE.

## Preflight / historical verification
Read AGENTS.md, ZERO419–ZERO423 and ZERO473. Rechecked all 12 historical GitHub Actions jobs. V421 S21 41149391000 and wild 41149393560 FAIL; regressions 41149404217 and CRC 41149399737 PASS. V422 S21 41149758090 and wild 41149762948 FAIL; regressions 41149771615 and CRC 41149767313 PASS. V423 S21 41149983685 and wild 41149987734 FAIL; regressions 41149995431 and CRC 41149992279 PASS. Their detailed window, gradient and red/blue results remain in ZERO473 dossier.

## V473 same-SHA real Android runs, all completed on a597a7b1
- [S21 41431207589](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41431207589) **FAIL**, restored actual original trained S21 33,312, final cursor 43,315 = **10,003 new autonomous decisions**, signed **265.38575**, gross **2751.97541**, efficiency **9.6435%**, **6/10** qualifying 1k windows, **two inverse windows 4 and 9**, **4 new blue**, four real rebirths. **2,677** physically eligible local cortex updates. Original historical S21 redCum=90 is NOT new red evidence. Ten independent S21 windows (signed/gross; efficiency; new blue):
  1. +46.54650 /236.57784; +19.6749%; 0
  2. +6.39172 /477.33849; +1.3390%; 1
  3. +19.02604 /106.87140; +17.8027%; 0
  4. -59.73775 /298.80963; **-19.9919%**; 1
  5. +114.16320 /288.75735; +39.5360%; 0
  6. +29.29700 /157.65911; +18.5825%; 0
  7. +32.59368 /451.81399; +7.2140%; 1
  8. +43.83314 /166.98106; +26.2504%; 0
  9. -65.47595 /305.47735; **-21.4340%**; 1
  10. +98.74817 /261.68919; +37.7349%; 0
- [Wild 41431207390](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41431207390) **FAIL**, **10,002** genuine self-issued decisions, signed **39.45853**, gross **3515.11809**, efficiency **1.1225%**, **3 blue / 30 red**; red **13 in first 5k → 17 in last 5k**, increasing, and 30 > 2×3. **5,107** local updates; **1,799** rejected harmful motor credits. Ten 1k wild windows: red `0,0,0,9,4,0,7,3,4,3`; blue `3,0,0,0,0,0,0,0,0,0`; signed/gross efficiencies `+.032992,-.045269,+.053487,+.037501,-.051516,+.125016,-.084097,-.037955,+.027157,+.029318`.
- [Regressions 41431207654](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41431207654) **PASS**, including three V473 physical residual tests.
- [Exact original S21 512-weight / V356 CRC 41431207484](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41431207484) **PASS**.
- S21 V443: 291,210 candidate score evaluations; 18,504 V377 predicted; 187,810 unsupported drift; 181,017 optical suppressions; V371 verified 2; V377 verified 4 (V444 diagnostic separately reports 5 bank reliability checks). Wild: 403,239 scores; 61,092 predicted; 258,621 unsupported drift; 258,151 optical suppressions; V371 verified 1; V377 verified 4 (V444 separately reports only 1 bank passing its stricter coast-loss diagnostic). These are evaluations, NOT 1:1 decisions.
- Source-level real body remains 170 material, original GRU3, V319 CH2; four death PRE/POST transitions reset velocity and do not teleport signed/gross. No conclusive proof that V473 residual fix solved any motor issue.

## Single causal consistency bug and V474 experiment
V473 changed V371 `learnedX/Y` and heldout errors to **PRE-coast-subtracted residual**. But the original V371 `score()` and `opticalPreferenceV374()` still interpreted `learnedX/Y` as **TOTAL** body displacement whenever V377 had no validated prediction and the body was below the V433 unsupported-drift threshold. V377 `expected()` correctly reconstructs **0.30s × measured PRE COM velocity + learned residual**. This is a source-proven unit/meaning inconsistency: the same motor evidence can predict two different physical travel vectors depending on which predictor is available. It is not proof of the sole cause of bad gait.

Exactly ONE conceptual V474 change: use `reconstructRealTravelFromResidualV474(pre194, learnedX, learnedY)` for V371 fallback optical ranking in both `score` and `opticalPreferenceV374`. It adds the **measured** original PRE velocity (sensors 191/192 scaled /130) times the original 0.30s normalization to the **already learned physical residual**. No authored heading, invented reward, new motor force, physics change, body reset, saved schema or GRU topology. V377 remains unchanged; all strict V433 drift-censorship gates remain. New Android pure mathematical regression tests check stationary, positive coast, reverse coast and invalid PRE; these do not establish autonomous success.

## Mandatory acceptance (unchanged)
Four same-commit original Android workflow runs: S21 >=10,000 actual decisions, >=25% signed/gross, >=9/10 1k windows >=10%, no inverse, >=5 true new blue; wild >=10,000 actual decisions, >=12% signed/gross, blue collected, red last 5000 < first 5000 and total red <=2× blue; complete original regressions + new V474 test PASS; exact trained S21 33,312 / 512 weights / V356 CRC PASS. If any fails, V474 remains lab-only and no APK, OTA or merge.

Preserve original user S21 checkpoint, CH2 V319, 194 sensors, original 170-body, all 12 saved slots. No V2.2 restoration.

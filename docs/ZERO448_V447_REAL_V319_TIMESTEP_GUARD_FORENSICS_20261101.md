# ZERO448 — V447 real Android neural stop: original V319 timestep guard forensic
Date: 2026-11-01. Research-only branch `lab/zero448-v319-original-timestep-guard-forensics` from the exact V447 behavior commit `249b75e71cf67bc1423e25ab28f05d726f8be4ed`. No release, APK, OTA, stable merge, saved user brain or slot modifications.

## Instructions and historical controls
Reviewed AGENTS.md and original docs ZERO419, ZERO420, ZERO421, ZERO422, ZERO423. Verified all 12 original GitHub Actions: V421 S21 41149391000 FAIL, wild 41149393560 FAIL, regressions 41149404217 PASS, CRC 41149399737 PASS; V422 S21 41149758090 FAIL, wild 41149762948 FAIL, regressions 41149771615 PASS, CRC 41149767313 PASS; V423 S21 41149983685 FAIL, wild 41149987734 FAIL, regressions 41149995431 PASS, CRC 41149992279 PASS. Previous ZERO447 dossier documents original V446 full S21 and partial wild windows.

## New V447 measured finding
- Original V447 wild run [41283577658](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41283577658) **FAIL**. Real instrumentation reported `java.lang.AssertionError: real world must not stop due to neural or physics error: Scelta neurale 3.0 non valida [IllegalStateException: V356 timestep outside real physics @ com.overmind.zero.Zero3ContextAdhesionV319.originalTimestepV356(Zero3ContextAdhesionV319.java:152)]`. This is a real physical-action lifecycle exception, not evidence that the GRU3 produced a NaN or invalid choice. The original wrapper had masked the location; V447 exposed it.
- The ninth complete wild 1000-decision window ended at decision **9004**: signed total 1375.68227, gross 4181.03582, signed/gross **32.9029%**, 4 blue and 50 red; red per window = 0,4,13,9,11,7,5,0,1; blue per window = 3,0,0,0,0,0,0,0,1. First 5000 red = 37; next four 1000-windows red = 13. The test failed before the required tenth full window; **do not claim >=10k** or a last-5000 comparison. At the latest recorded cursor, 9488 real decisions had been reached. At least 50 red / 4 blue already violates the safety ratio.
- Original V447 regression [41283577618](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41283577618) **PASS**; CRC [41283577622](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41283577622) **PASS**.
- Original V447 S21 [41283577617](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41283577617) was **still in progress** when checked. Do not infer a result or interrupt it.

## Source trace and unresolved guard ambiguity
`GameActivity.Lab.onDraw` supplies `slice=TEST_MAX_FIXED_DT=1f/60f` and invokes `brain3.actuate` before real material solver. `Zero3OriginalBrainV356.actuate` invokes `motor.actuate`, then `grip.originalTimestepV356(slice)`, then `grip.actuate`; `brain3.observed` later calls `grip.observeWorldV354` after the real solver. V319's original guard is `!active || hasStep || !finite(dt) || dt<=0 || dt>.04`, and V447's exception does NOT distinguish these clauses. With the fixed 1/60 input, the likely alternatives are an inactive V319 action or an unconsumed previous physical step; **which one actually happened is not yet proven**. No arbitrary reset, silent skip or invented physical outcome is permitted.

## Single V448 change, no altered motor or reward
V448 changes ONLY the existing V319 exception message to include `active`, `hasStep`, `dt`, `steps`, `scale` at the exact failed guard, keeping the same predicate, exception type, fail-closed behavior and training. Four original Android workflows are enabled for this new branch on the same final commit. Do not claim V448 fixes locomotion. After wild reproduces the error, use the actual guard state and adjacent source path to justify **one** behavioral repair on a new branch, with full Android regression, CRC and two independent original 10k tests. If wild completes, apply every strict red/blue gate anyway.


## Historical original Android log reconstruction (V421–V423; 12 runs actually verified)
All 12 runs were checked by job ID and all six behavioral **full job logs** parsed, not inferred from workflow names. S21 redCum=90 is **historical in saved checkpoint**; all six S21 runs had zero newly touched red. Values below are the last complete 1000-window counters (the final test may include 1–3 additional decisions).

| Run | Original decision windows | Signed/gross total | New blue | New red | First 5k red → last 5k red | Local cortex weight updates | Outcome |
|---|---:|---:|---:|---:|---:|---:|---|
| V421 S21 41149391000 | 10 | 574.11644 / 3342.83681 = 17.17% | 4 | 0 | n/a | 1918 | FAIL: 8/10 >=10%, two inverse |
| V421 wild 41149393560 | 10 | 2747.91379 / 4459.19043 = 61.62% | 3 | 87 | 35 → 52 | 5842 | FAIL: reds worsen, 29× blue |
| V422 S21 41149758090 | 10 | 336.44837 / 2980.70822 = 11.29% | 4 | 0 | n/a | 1851 | FAIL: 7/10 >=10%, two inverse |
| V422 wild 41149762948 | 10 | 1872.49766 / 4385.50670 = 42.70% | 5 | 52 | 19 → 33 | 4587 | FAIL: reds worsen, 10.4× blue |
| V423 S21 41149983685 | 10 | 281.40051 / 2799.39399 = 10.05% | 4 | 0 | n/a | 2961 | FAIL: 7/10 >=10%, two inverse |
| V423 wild 41149987734 | 10 | 1209.85250 / 3892.88175 = 31.08% | 4 | 26 | 14 → 12 | 4712 | FAIL: 6.5× blue despite decreasing late red |

Original S21 signed/gross % per successive 1000-decision window:
- V421: 16.52, 19.29, 19.88, -15.21, 43.93, 26.74, 12.69, 29.37, -19.79, 38.31; 1160 opposed and 4711 static decisions across windows.
- V422: 16.61, 16.15, 24.27, -11.79, 30.05, 14.88, 8.79, 25.33, -19.60, 26.32; 1230 opposed and 5404 static.
- V423: 19.84, 6.49, 16.05, -41.03, 26.50, 15.65, 10.13, 24.81, -10.74, 29.91; 1205 opposed and 5897 static.

Original wild successive 1000-decision red counts / blue counts:
- V421 red: 0,10,6,9,10,10,10,13,9,10; blue: 3,0,0,0,0,0,0,0,0,0.
- V422 red: 0,10,9,0,0,6,4,12,10,1; blue: 3,0,0,0,0,0,1,0,0,1.
- V423 red: 0,0,2,12,0,0,6,0,0,6; blue: 3,0,0,1,0,0,0,0,0,0.

Original wild signed/gross % per successive 1000-decision window:
- V421: 12.12, 54.15, 60.10, 55.93, 70.17, 72.35, 70.79, 66.01, 73.61, 76.18.
- V422: 10.85, 45.10, 32.18, 22.77, 38.23, 27.82, 33.08, 68.88, 70.55, 49.03.
- V423: 11.88, 23.38, 44.35, 57.44, 4.94, 39.51, 31.98, 15.61, 43.21, 32.58.

These logs establish that more cortex updates or better gross travel alone are NOT proof of avoidance or competent steering. They do not expose every per-weight signed gradient; no gradient distribution has been invented. Physical S21 static/opposed counters and actual wild harm/blue contact counters are separately reported rather than conflated. All historical regression/CRC jobs PASS; behavioral gates fail.

## Non-negotiable gates
Original S21 33312 prior decisions, 512 weights, V356 CRC, V319 CH2, 194 sensory inputs, original 170-material body and 12 slots remain untouched. No V2.2, fixed steering, fabricated reward, saved-weight edits. Release requires original S21 >=10000 real new decisions, >=25% signed/gross, 9/10 windows >=10%, zero inverse, >=5 new blue; original wild >=10000, >=12%, last 5000 red < first 5000, red <=2x blue, plus regressions and CRC PASS. **NOT APPROVED.**

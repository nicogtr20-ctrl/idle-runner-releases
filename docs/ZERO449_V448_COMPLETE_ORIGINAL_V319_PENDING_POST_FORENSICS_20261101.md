# ZERO449 — V448 complete Android forensic and physical POST trace
2026-11-01. Experimental branch: lab/zero449-v319-physical-post-lifecycle-trace. Parent V448 commit 1d95540604fa66f1af199cccafdf3ba6fa44f1e7. NO APK, OTA, stable merge or saved-brain edits.

## Reviewed controls
AGENTS.md and ZERO419–ZERO423 read. All twelve historical Actions rechecked: V421, V422, V423 each S21 FAIL, wild FAIL, regression PASS, CRC PASS. All six historical full behavioral logs examined. Exact per-1000 windows, signed/gross, red/blue, muscle counters and limitations are in docs/ZERO448_V447_REAL_V319_TIMESTEP_GUARD_FORENSICS_20261101.md.

## V448 original Android final
S21 https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41289724425 FAIL: 43,314 total decisions from original 33,312, 10,002 new; signed/gross 570.09987 / 3163.54081 = 18.0209%; 8/10 windows >=10%, one inverse, four new blue, 3084 cortex updates. Window efficiencies (%): 26.33, 14.01, 30.31, -3.27, 28.61, 23.75, 16.19, 31.71, 3.96, 24.90. Original S21 redCum=90 is HISTORICAL, not new red.
Wild https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41289724424 FAIL before 10k: 9002 decisions at ninth full window, signed/gross 1375.51399 / 4180.36086 = 32.9042% partial, 50 red and 4 blue, 4979 cortex updates. Red per 1000 = 0,4,13,9,11,7,5,0,1; blue = 3,0,0,0,0,0,0,0,1. First 5000 red=37, next four 1000 red=13; cannot claim last 5000. Already 50 red / 4 blue fails <=2x.
Regression https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41289724555 PASS; original CRC https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41289724448 PASS. V447 S21 41283577617 CANCELLED, no approval.

## Verified V319 failure
Wild Java error: IllegalStateException: V356 timestep outside real physics [active=true,hasStep=true,dt=0.016666668,steps=13,scale=0.6666667] at Zero3ContextAdhesionV319.originalTimestepV356:152. Therefore inactive V319 and invalid dt are ruled out. A prior physical PRE did not complete its corresponding V319 POST observation before the next PRE. The source of the missing POST remains UNPROVEN; do not silently reset the step, synthesize a physical observation, or change rewards.

## Single read-only V449 experiment
GameActivity now tracks original successful PRE, ordinary POST, terminal POST, world-generation skips, reset counts, and prior stage/episode/life serial. On the same fail-closed V319 exception, it includes the preceding lifecycle state. This changes no actor decision, physics, muscles, V319, GRU3, 194 senses, 512 weights, original V356 checkpoint, CH2 owner or twelve saves. Four original Android workflows added to the isolated branch allowlists for real 2x10000 and regressions/CRC. If failure reproduces, inspect exact missing callback before ONE justified repair.

## Non-negotiable approval
S21 original >=10000 new decisions, >=25% signed/gross, 9/10 windows >=10%, zero inverse, >=5 new blue; wild original >=10000 decisions, >=12% signed/gross, last 5000 red < first 5000, total red <=2x blue; regressions and original CRC PASS. NOT APPROVED.

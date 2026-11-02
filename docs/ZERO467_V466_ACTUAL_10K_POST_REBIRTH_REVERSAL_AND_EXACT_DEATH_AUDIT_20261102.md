# ZERO467 — V466 completed physical 10k and exact-death read-only experiment
Date 2026-11-02; research branch `lab/zero467-exact-death-boundary-physical-forensics`, parent `f62f810bd89afbfd953b80e3f698440692a39999`. No APK, OTA, merge or behavioral claim.

## Completed original V466 Actions, all same SHA
- S21 [41372071458](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41372071458), job 125044467439: FAIL after **10,001** original autonomous decisions (33,312→43,313), signed 417.99631, gross 3254.54515, efficiency **12.8435%**, **7/10** windows >=10%, **2 inverse** (4 and 9), **4 new blue**, 2,834 original motor updates. No new red in saved blue-only training room. Zero V371 certified directions; 3 V377 reliable banks.
- Wild [41372071426](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41372071426), job 125044467396: FAIL after **10,002** genuine original decisions, signed 722.10625, gross 3997.52336, efficiency **18.0638%**, **44 real red / 4 real blue**, red **19** in first 5000 and **25** in last 5000, ratio 11.0. 5,079 motor updates, 1,716 harm rejects; 6,473 genuinely learned-harm 7-choice expansions. Zero V371 directions, 3 V377 reliable banks.
- Original Android regressions [41372071445](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41372071445): PASS.
- Exact original S21 33,312 decisions, 512 weights, V356 CRC [41372071333](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41372071333): PASS.
These four runs are confirmed on identical commit `f62f810bd89afbfd953b80e3f698440692a39999`. Behavior still FAIL.

## S21 actual 1000-decision windows
|Window|Signed|Gross|Signed/gross|Blue|Life|
|--:|--:|--:|--:|--:|--:|
|1|58.55305|417.43293|14.0269%|0|0|
|2|109.74040|618.71122|17.7369%|1|1|
|3|46.47947|228.59652|20.3325%|0|1|
|4|-48.18263|334.43630|-14.4071%|1|2|
|5|71.62506|280.87922|25.5003%|0|2|
|6|60.48315|178.66269|33.8533%|0|2|
|7|7.34911|441.03282|1.6663%|1|3|
|8|50.63037|165.82342|30.5327%|0|3|
|9|-39.98077|311.98315|-12.8150%|1|4|
|10|101.29910|276.98688|36.5718%|0|4|

## Sub-window discovery: reversal after detected rebirth, not before
The V466 test polls the actual life serial and motor odometer on the game thread. Four genuine transitions were observed. The numbers below split each 1000-decision window at the sampled transition (not necessarily the exact physical death; the poll may lag).

|Window|Cursor observed|Signed before|Signed after|Gross before|Gross after|
|--:|--:|--:|--:|--:|--:|
|2|34652|+37.21959|+72.52081|124.14811|494.56311|
|4|37078|+41.69977|**-89.88240**|174.45027|159.98603|
|7|39513|+12.48717|-5.13806|33.51569|407.51713|
|9|42004|+39.76806|**-79.74883**|123.18073|188.80242|

The two severe reverse windows have positive signed progress BEFORE observed rebirth and strongly negative AFTER. This narrows investigation to new-life control, but does NOT prove a reset bug, bad motor force, or specific cause. New life resets original recurrent activations in `Zero3OriginalBrainV356.newLife()` and physical body in `GameActivity.resetEpisode()`, while preserving learned weights. The benchmark metric is real cumulative signed/gross, not a fake death displacement; the exact death boundary must be observed before any behavioral change.

## Wild 1000-decision windows
|Window|New blue|New red|Signed/gross|
|--:|--:|--:|--:|
|1|3|0|6.7740%|
|2|0|0|21.1982%|
|3|0|0|17.9320%|
|4|0|10|12.8260%|
|5|1|9|4.8568%|
|6|0|0|20.1523%|
|7|0|5|21.8285%|
|8|0|5|20.4543%|
|9|0|5|21.6320%|
|10|0|10|29.6964%|

S21 V443 motor evidence: 281,677 candidate score evaluations, 27,103 predicted, 191,425 unsupported drift. Wild: 383,805 evaluated, 26,871 predicted, 293,079 unsupported drift. Both zero verified V371 steering directions; GRU3, 194 sensor inputs, original physical 170-body, CH2 V319 remain unchanged.

## V421–V423 original historical Actions rechecked
V421 S21 [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL 17.1723%, 8/10, 2 inverse, 4 blue; wild [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL 87 red/3 blue, red 35→52. Regression 41149404217 PASS; CRC 41149399737 PASS.
V422 S21 [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL 11.2881%, 7/10, 2 inverse, 4 blue; wild [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL 52 red/5 blue, red 19→33. Regression 41149771615 PASS; CRC 41149767313 PASS.
V423 S21 [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL 10.0486%, 7/10, 2 inverse, 4 blue; wild [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL 26 red/4 blue, red 14→12. Regression 41149995431 PASS; CRC 41149992279 PASS.
Historical runs use differing head SHAs, so no same-commit approval inference.

## Single V467 measurement correction
V466 life snapshots are delayed by the test's 260ms poll. V467 adds an opt-in, nonpersisted QA-only `GameActivity` log `ZERO467_EXACT_DEATH` at the genuine terminal branch, PRE before `resetEpisode()` and POST immediately afterward, recording life serial, original decision cursor, reserve, actual material center/velocity, and original cumulative signed/gross. Only the original S21 Android test enables it; CI logcat captures it. No changed actor, sensor, physics, learning, reward, training-room materials, user save schema, V2.2 bridge, directions or checkpoints.

Run four strict original Android gates on one V467 commit. S21 >=10k, >=25% progress, >=9/10 windows >=10%, no inverse, >=5 blue; wild >=10k, >=12%, late red < early red and red <=2× blue; regressions and original V356 CRC PASS. If any fail, do not publish, merge or claim success. The exact PRE/POST telemetry must be used to justify any later single behavioral intervention.

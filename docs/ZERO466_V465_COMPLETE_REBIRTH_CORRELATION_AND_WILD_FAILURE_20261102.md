# ZERO466 — original V465 real-life boundary motor attribution and wild 10k completed failure
Date: 2026-11-02. Isolated research branch `lab/zero466-original-rebirth-boundary-motor-attribution`, based on V465 `64a9635c9807946e57f44c0ff07ccc89682e528c`. No APK, OTA, merge, saved-slot write, V2.2 motor, forced direction or invented reward. This is a single **read-only measurement correction**, NOT a claimed locomotion correction.

## V465 genuine original Android outcomes (read full jobs)
- S21 [run 41368341089](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41368341089), job 125033561345, **behavior FAIL**, actual 10,002 additional decisions (33,312 → 43,314). Signed 418.02942, gross 3254.62238, efficiency **12.8442%**; 7/10 windows >=10%, 2 negative (4 and 9), 4 new blue, 2,834 local motor weight updates, zero verified V371 directions, 3 reliable V377 banks. Source original 512 weights/V356 untouched.
- Wild [run 41368341143](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41368341143), job 125033580396, **behavior FAIL** after 10,002 real autonomous decisions: signed 722.10625, gross 3997.52336, **18.0638%**; 4 blue, **44 real red**, red **19 first 5k →25 last 5k** (worsens), red/blue=11.0, 5,079 motor updates, zero verified V371 directions, 3 V377 reliable banks. V422 learned-harm candidate expansion happened 6,473 times with 7 offered, but still did not establish red avoidance. No invented hazard labels.
- Original regressions [run 41368340984](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41368340984), job 125033520620: **PASS** on SHA 12cf872bb4ee6499b9be42edf903d3fbd4638c9a (27 Android test classes).
- Original S21 CRC [run 41368328701](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41368328701), job 125033185707: **PASS** on *earlier* SHA 51902bdc767f7ac8c1d26f4a4324011301593791. This is **not** a same-commit four-gate proof; V466 reruns all four on one commit.
- V464 wild original [run 41364545160](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41364545160), retry job 125032813422, now also completed 10,002: **FAIL**, same 44 red/4 blue, 19→25, 18.0638%. The initial V464 job failed before boot, so only retry supplies behavioral evidence.

## Exact V465 S21 1000-decision windows and real life
| Bucket | Life at end | Rebirth in bucket | New blue | Signed | Gross | Signed/gross |
|---:|---:|:---:|---:|---:|---:|---:|
|1|0|no|0|58.72656|418.75973|14.0239%|
|2|1|yes|1|109.56689|617.38442|17.7469%|
|3|1|no|0|46.39118|228.48620|20.3037%|
|4|2|yes|1|-48.09434|334.54662|-14.3760%|
|5|2|no|0|71.62506|280.87922|25.5003%|
|6|2|no|0|60.48527|178.75410|33.8371%|
|7|3|yes|1|7.40479|440.73812|1.6801%|
|8|3|no|0|50.57257|166.02671|30.4605%|
|9|4|yes|1|-40.02255|311.36950|-12.8537%|
|10|4|no|0|101.34088|277.60053|36.5060%|

All four blue pickups occur in windows with a genuine rebirth (2,4,7,9). Both negative windows (4,9) coincide with a rebirth, but the other two rebirth windows (2,7) are not negative. **Association is not proof of causation**: 1000-decision snapshots cannot tell whether the reverse movement was before death, during reset, or after rebirth. Original room has 4 initially ready blue, but death recreates all five; keep >=5 real pickup gate.

## Wild 1000-decision windows (V465, rounded)
| Bucket | New blue | New red | Signed/gross |
|---:|---:|---:|---:|
|1|3|0|6.7740%|
|2|0|0|21.1685%|
|3|0|0|17.9806%|
|4|0|10|12.8612%|
|5|1|9|4.8275%|
|6|0|0|20.2061%|
|7|0|5|21.8521%|
|8|0|5|20.3362%|
|9|0|5|21.6382%|
|10|0|10|29.6620%|

## Causal next measurement: V466
V465 only samples life serial at each 1000-decision boundary. V466 adds **one** read-only S21 Android instrumentation change: in the already existing original UI-thread decision poll, detect genuine `originalV356LifeForQA()` transitions and log decision cursor, old/new life, actual cumulative signed/gross movement and genuine blue contacts. This can partition each long window into approximate within-life segments and identify whether reverse motion precedes or follows a real rebirth. The poll may lag the actual death by several decisions, so do **not** claim exact physical pre/post collision timing.

Only Android test instrumentation and four CI workflow allowlist/name entries are changed; no original production code, brain, body, sensors, GRU3, V319 CH2, reward, checkpoint or user slot is modified. All four original Android tests must run on the exact same V466 SHA: S21 >=10k and hard 25%/9-of-10/no inverse/5-blue, wild >=10k with fewer late reds and red<=2x blue, full regressions and saved V356 CRC. V466 is diagnostic; failure to pass behavior is not a successful locomotion fix.

## Historical V421–V423 cross-check
V421 S21 17.1723%, 8/10, 2 inverse, 4 blue; wild 87 red/3 blue, 35→52. V422 S21 11.2881%, 7/10, 2 inverse, 4 blue; wild 52 red/5 blue, 19→33. V423 S21 10.0486%, 7/10, 2 inverse, 4 blue; wild 26 red/4 blue, 14→12. All six behavioral tests FAIL; all six corresponding regression/CRC jobs PASS. Full source: V419–V423 dossiers and their linked GitHub runs.

**Decision: NOT APPROVED.** No APK, OTA, stable merge, threshold lowering or claim of successful red avoidance. If V466 cannot separate pre/post-life physical motion, instrument exact original material death boundary next; do not guess a behavioral patch.

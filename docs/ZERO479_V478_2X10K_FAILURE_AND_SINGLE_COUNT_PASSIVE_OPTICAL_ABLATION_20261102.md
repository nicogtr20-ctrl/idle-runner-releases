# ZERO479 — V478 full Android 2×10k FAIL; isolate double-counting of measured passive coast
Date: 2026-11-02. Research-only branch `lab/zero479-single-count-measured-passive-optical-evidence` from exact V478 source `044030207cbd741c6caa5bb8b5776cc81702700b`. No APK, OTA, stable merge, V2.2, checkpoint or save mutation.

## Sources and historical 12-run check
Read `AGENTS.md`, ZERO419..ZERO423. Independently fetched complete GitHub job logs for all twelve historical runs. V421 S21 10,001, 17.1723%, 4 blue, reverse 4/9; wild 10,002, 87 red/3 blue, red 35→52; regressions PASS, CRC PASS. V422 S21 10,003, 11.2881%, 4 blue, reverse 4/9; wild 10,003, 52 red/5 blue, red 19→33; regressions PASS, CRC PASS. V423 S21 10,002, 10.0486%, 4 blue, reverse 4/9; wild 10,003, 26 red/4 blue, red 14→12; regressions PASS, CRC PASS. Historical runs do not necessarily share a single SHA. Source Actions: 41149391000/41149393560/41149404217/41149399737; 41149758090/41149762948/41149771615/41149767313; 41149983685/41149987734/41149995431/41149992279.

## V478 four actual same-SHA Actions — finished
- [S21](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41475655258): FAIL; 10,002 real original decisions (33,312→43,314), signed 230.08847 / gross 2687.85647 = **8.5603%**, 6/10 >=10%, inverse **2,4,9** (three), 4 new blue, 2,807 muscle updates, four genuine rebirths. Historical 90 S21 red are saved fixture history, not 90 new touches.
- [Wild](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41475655203): FAIL; 10,005 original self-issued decisions, signed 269.48603 / gross 3530.16376 = **7.6338%**, 25 red / 4 blue, red **5 first 5,000 → 20 last 5,000** (4× worsening), 5,151 muscle updates, 1,742 local harm-rejected.
- [Android regressions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41475655156): PASS, including 4 V478 tests and 6 V476/V477 tests.
- [Original V356/512 weights CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41475655140): PASS. All four checkout `044030207cbd741c6caa5bb8b5776cc81702700b`.

### S21 exact successive 1,000-decision windows
|Bucket|Signed/gross|Blue newly|
|---|---:|---:|
|1|0.155419|0|
|2|-0.006629|1|
|3|0.229937|0|
|4|-0.244659|1|
|5|0.288163|0|
|6|0.237100|0|
|7|0.067344|1|
|8|0.298710|0|
|9|-0.181971|1|
|10|0.287013|0|

### Wild exact successive 1,000-decision windows
|Bucket|New blue|New red|Signed/gross|
|---|---:|---:|---:|
|1|3|0|0.026163|
|2|0|0|-0.004466|
|3|0|0|0.058171|
|4|0|5|-0.084192|
|5|0|0|0.016584|
|6|0|10|0.052980|
|7|0|5|0.199379|
|8|0|0|0.203612|
|9|0|5|0.100319|
|10|1|0|0.174985|

## Actual motor, eye, cortex and hazard evidence
V478 S21 294,730 candidate evaluations, 196,581 unsupported PRE-drift, 196,581 passive-score evaluations and 190,154 passive-optical evaluations, 1,326 V377 predictions, 4 verified V377 banks, 1 V371 direction, 927 mid-speed train/179 heldout but 0 mid-speed reliable. Wild 421,953 candidate evaluations, 318,651 unsupported drift, 318,651 passive-score evaluations and 313,407 passive-optical evaluations, 29,870 predicted, 2 verified V377 banks, 1 V371 direction, 1 reliable strict V444 bank, 1,706 mid train/424 heldout and 0 mid reliable. Original 194D/GRU3 remains sole actor and original 170-material solver intact.

From 25 actual wild red contact log records: 25/25 PRE momentum toward the touched hazard, 23/25 had `learnedVisible=true`, 22/25 offered seven actor candidates, 17/25 cognition chose candidate zero; in 19/25 a sampled alternative had raw optical preference at least 0.01 higher than the issued candidate. These do NOT prove the alternatives would physically steer: motor mapping is largely unverified. 5 red in first half vs 20 in second confirms failure to learn reliable avoidance.

## One source-level causal inconsistency and V479 experiment
V478 uses exactly the same measured PRE COM velocity for all unsupported options in `Zero3InstrumentalCreditV371.score`, scoring learned spectral consequences of physical coast. It then **adds a second opticalPreferenceV374 computed from the normalized direction of that same PRE velocity**, also for all unsupported options. This is two correlated terms from one measured physical displacement, not independent evidence; normalization additionally discards measured speed in the second term. Both are physically shared, hence neither by itself proves candidate-specific steering.

**ONE behavioral source correction**: retain the physical passive score once; for the same unsupported drift branch `opticalPreferenceV374` contributes zero (and still counts reachability). Verified V377 predictions, V371 heldout reconstructions, stationary optical hypotheses, contact valence, actor proposals, body physics, CH2 V319, local cortex and all rewards unchanged. This may worsen or improve selection between verified and unverified options; Android 2×10k must decide. No hand-coded safe direction or invented motor control. The old V478 unit cases now assert no duplicate; new `Zero479SingleCountPassiveOpticalAndroidTest` verifies same coast across opposite unsupported actions, actual learned harm/benefit sign, and unchanged stationary optical behavior.

## Immutable acceptance
S21 original 33,312 decision / 512 weights / V356 CRC, >=10,000 decisions, >=25% signed/gross, >=9/10 >=10% windows, zero inverse windows, >=5 new blue. Wild original >=10,000 decisions, >=12% signed/gross, red latter 5k strictly fewer than former 5k and total red <=2× actual blue. Full Android regressions and exact CRC PASS on same SHA. Preserve CH2 V319, 194 sensors, original body and 12 save slots. NO release/OTA/merge until ALL PASS. Pending new four same-SHA GitHub Actions; do not report green before observing them.

# ZERO470 — V469 failed original 2×10k; isolated 194D chamber-ray consistency repair
Date: 2026-11-02. **LAB ONLY, no APK/OTA/merge, no checkpoint or user slot edits.**
Branch: `lab/zero470-coarse-fine-chamber-ray-consistency` based on **V468**, not V469. V469's unsupported actor-heading optical preference is deliberately NOT carried forward.

## Source contract and historical evidence
Read `AGENTS.md` and full ZERO419, ZERO420, ZERO421, ZERO422, ZERO423 dossiers. Rechecked all 12 historical original Actions and all 12 job logs:
- V421 S21 41149391000 FAIL: 10,001+ new decisions, 17.1723%, 8/10 windows >=10%, inverse 4/9, 4 blue. Wild 41149393560 FAIL: 87 red/3 blue, first 5k 35 -> last 5k 52. Regressions 41149404217 PASS; V356 CRC 41149399737 PASS.
- V422 S21 41149758090 FAIL: 11.2881%, 7/10, inverse 4/9, 4 blue. Wild 41149762948 FAIL: 52 red/5 blue, 19 -> 33. Regressions 41149771615 PASS; CRC 41149767313 PASS.
- V423 S21 41149983685 FAIL: 10.0486%, 7/10, inverse 4/9, 4 blue. Wild 41149987734 FAIL: 26 red/4 blue, 14 -> 12 but 6.5× red/blue. Regressions 41149995431 PASS; CRC 41149992279 PASS.

## V469 original same-SHA Android results (REJECT)
Four completed GitHub Actions, all V469:
- S21 [41389629449](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41389629449): 43,316-33,312=**10,004 new real decisions**; signed **532.34749**, gross **3507.49657**, **15.1774%**; 7/10 windows >=10%, inverse 4 and 9, **4 new blue**, 4 real rebirths, 3,411 local cortex updates. FAIL.
- Wild [41389629407](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41389629407): **10,003 real decisions**, signed **107.41923**, gross **3859.86946**, only **2.7830%**, 23 red / 3 blue, **11 red first 5k -> 12 last 5k**; 5,000 local updates, 1,811 real harm gradient rejects. FAIL.
- Regressions [41389629518](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41389629518): PASS including V469 four-case unit.
- Original S21 33,312/512 V356 CRC [41389629399](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41389629399): PASS.

### V469 original S21 1000-decision windows
|Window|Signed|Gross|Signed/gross|New blue|
|--:|--:|--:|--:|--:|
|1|58.72656|418.75973|14.0239%|0|
|2|58.46311|571.06826|10.2375%|1|
|3|124.57215|301.76018|41.2818%|0|
|4|-11.02735|378.61485|-2.9126%|1|
|5|112.31865|333.93500|33.6349%|0|
|6|54.99293|161.49581|34.0522%|0|
|7|90.93222|516.93223|17.5907%|1|
|8|47.14606|185.86546|25.3657%|0|
|9|-36.45155|280.74619|-12.9838%|1|
|10|32.98373|357.96572|9.2142%|0|

### V469 original wild 1000-decision windows
|Window|New red|New blue|Signed|Gross|Signed/gross|
|--:|--:|--:|--:|--:|--:|
|1|0|3|26.50128|391.22293|6.7740%|
|2|0|0|47.16334|375.29835|12.5669%|
|3|0|0|59.53031|398.50226|14.9385%|
|4|11|0|-38.21541|477.93316|-7.9960%|
|5|0|0|84.58139|341.00003|24.8039%|
|6|0|0|16.12073|414.71356|3.8872%|
|7|7|0|-63.27101|338.76107|-18.6772%|
|8|0|0|54.90326|401.78483|13.6648%|
|9|4|0|-106.08575|336.23757|-31.5508%|
|10|1|0|26.68951|383.90986|6.9520%|

### Causal assessment
V469 restored a weak intended-heading optical preference during **unverified body drift**. Compared to V468 wild 20.1625%/41 red/3 blue, V469 shows 2.7830%/23 red/3 blue. This is a strong locomotion regression; fewer total red touches do not mean avoidance competence because actual movement collapses and late red increases. V469 wild reports 297,599 unsupported-drift candidate scores, 95,490 still optical-suppressed, **zero verified V371 directions**, 6,422 seven-option expansions, 5,000 local updates. V469 S21 205,539 unsupported-drift scores, 0 verified directions. A mere change in candidate preference cannot certify physically realized steering. Reject V469; do not merge.

## V470 single isolated source-causal repair
ZERO419 identified a **measurable inconsistency in the 194 original GRU3 inputs**:
- `Zero3SensoryFrameV315.capture` builds 16 membrane obstacle rays using `firstSolidDistance(null, objects,...)`: chamber geometry is missing.
- `Zero3AutonomousLoopV317.encode` maps those rays to the coarse inputs 8..15 by max-pooling adjacent pairs.
- **The same** `Zero3OriginalBrainV356.sense` then builds fine inputs 44..59 using `firstSolidDistance(chamber, objects,...)`. In training rooms with physical chamber walls, coarse and fine views of the same membrane/heading may contradict each other.
- One isolated V470 change: **after the 16 original chamber-aware fine rays have been physically sampled**, set each coarse input 8+i to max of fine inputs 44+2i and 45+2i. This removes inconsistent geometry provenance, does not add or delete any of the 194 neurons, does not introduce a color label, heading, goal, reward, force or hidden object location. In open world with null chamber this should be equivalent to existing 16-ray pooling.
- New Android `Zero470CoarseFineRayConsistencyAndroidTest` checks 8 exact maxima and all other 186 sensory values untouched, plus invalid 194-shape rejection.
- No changes to GRU3 learned weights, 512 motor cortex weights, 64 efferences, V319 CH2, 170-material body, 12 user saves, V356 checkpoint serialization, real red/blue rewards or previous V468 reset.

## Precommitted original Android acceptance
Four separate original Android workflows on SAME FINAL SHA: S21 original >=10,000 new real decisions, >=25% signed/gross, >=9/10 windows >=10%, no inverse, >=5 actual new blue; wild original >=10,000, >=12%, red latter 5000 fewer than first 5000, total red <=2× actual blue; original regression including V470 test PASS; exact 33,312 S21 512-weight V356 CRC PASS. If any fail, V470 remains rejected lab experiment. No APK/OTA/merge.

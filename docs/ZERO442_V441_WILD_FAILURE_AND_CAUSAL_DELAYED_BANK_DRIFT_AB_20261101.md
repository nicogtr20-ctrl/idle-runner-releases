# ZERO442 — original V441 wild failure and causal delayed-bank under drift A/B
2026-11-01. Isolated experimental branch `lab/zero442-no-unverified-delayed-bank-under-drift` from V441 audit `611fcacb1ce860ee26dbbb53cc27c46b1edf9b0d`, original V441 behavioral commit `ca3f7f83051fb8b1dfc1840373aa7742ca63ca80`. **NOT APPROVED. NO APK, OTA, stable merge or original slot writes.**

## Read-first and actual Android evidence
Read root `AGENTS.md`, `docs/AGENTS.md`, ZERO419–ZERO423, ZERO440–ZERO441, MASTER, IMPLEMENTATION CONTRACT, UI spec and complete original V441 logs. V441 wild [41262684124](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684124): **10,004 real decisions**, signed 52.20141 / gross 3667.09150 = **1.4235%**, **49 red / 4 blue**, first 5,000 **29 red**, last 5,000 **20 red**; 5,450 real cortex updates, 1,675 harm rejections, 8,186 learned-harm seven-candidate expansions. In 49 real harmful contacts, 45 had PRE COM velocity toward hazard, 30 commanded materially away, 35 had PRE speed >=0.35U/s, 47 offered 7 sampled alternatives. Four blue occurred within first 3,001 decisions; none after. V441 wild FAIL despite declining red rate.

S21 [41262684117](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684117) first attempt failed at Activity/GRU boot before any valid 10k result; unchanged attempt 2 requested, must check actual result. CRC [41262684187](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684187) PASS. Original V441 regression [41262684185](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684185) had a **test-harness false failure**: actual V433 suite 5 tests PASS but YAML expected 3; corrected harness on unchanged V441 behavior [41266183060](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266183060) PASS.

## Source-confirmed inconsistent motor evidence and ONE intervention
In `Zero3InstrumentalCreditV371.opticalPreferenceV374`, strict V441 rejects a new commanded heading as material travel when real PRE COM speed >=0.35U/s and `V377.expected` lacks independently heldout-validated response. In the same state, `score` still returns `observedDelayedContactScoreV434` indexed by the newly commanded bank. V434/V435 historic bank/contact association follows past measured forward acceleration and actual contact, but is not an independently validated actuator response for the **new** command under current drift.

V442 adds only a transient opt-in: if strict V433/V434/V435 are enabled, real PRE speed >=0.35 and `V377.expected` is unavailable, censor ONLY this V434 observational bank score for the current candidate. Preserve real positive/negative material-contact valence, historic bank records, low-speed V434 ranking, validated V377 prediction, GRU3 actor, V319 grip, cortex learning, original physics and 12 saves. No preset direction, invented reward, artificial brake, extra neuron or altered sensory scale. Risk: censoring a weak useful bank signal may worsen exploration or blue collection; only original 10k tests can decide.

194D sensory map remains unchanged: slots 16/17 body velocity /10 from V440, slots 191/192 /130 for V371/V377. No 13x velocity decoder mismatch. Original trained S21 33,312 decisions, 512 cortex weights, V356 CRC, CH2 V319, 64 GRU, original 170-material body and 12 slots preserved.

## Precommitted Android gates
Four new unit cases: requires V433/V434/V435 and no pending decision; real negative delayed contact preserved but bank score censored at 1.2U/s; positive and negative true-contact ranking unchanged at 0.2U/s; real positive contact preserved while bank score censored at 1.2U/s. Full original 23-suite regression including V433 five tests must PASS. On ONE final source SHA, original saved S21 33,312→>=43,312 true decisions with >=25% signed/gross, >=9/10 1000 windows >=10%, zero inverse, >=5 new blue; original newborn wild >=10k, >=12% signed/gross, last 5k red < first 5k, total red <=2×blue; regressions and exact original CRC PASS. Report 10 windows, all red/blue contacts, motor signed/gross, gradients and wall exclusions. Retry emulator boot without code change if no valid decisions. **No APK/OTA/merge unless all gates pass.**

Historical V421–V423: S21 17.1723%/11.2881%/10.0486% FAIL, wild 87/52/26 red FAIL, original regressions/CRC PASS in all three. These are separate experimental commits, not certification.

## V442 final tested source and original Android workflow dispatch
All four workflows were triggered on exactly source commit `14c3a7ffbdf508c7e4f60ea8b4fba389fd8573e1` (separate experimental branch):
- [Original S21 10,000 decisions 41266630484](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630484)
- [Original newborn wild 10,000 red/blue decisions 41266630508](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630508)
- [Full Android regressions, corrected V433 count and new V442 tests 41266630497](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630497)
- [Exact original S21 V356/512 CRC 41266630512](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630512)

**At dispatch these were QUEUED, not PASS.** The older V441 S21 boot retry remains active on its own original V441 SHA; no comparison or approval can be claimed until its genuine original 10k finishes. The document-only update does not change the tested source.

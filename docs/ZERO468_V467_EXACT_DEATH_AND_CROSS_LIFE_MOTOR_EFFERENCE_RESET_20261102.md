# ZERO468 — V467 completed original Android 10k; one cross-life motor echo repair
Date: 2026-11-02. Branch: `lab/zero468-clear-cross-life-motor-efference`. Parent: `3c878909fb2bfe770f6d0a5e14fc521a527095bb`.
**LAB ONLY — no APK, OTA, merge, changed reward, scripted direction, V2.2 path, saved profile or V356 schema.**

## V467 completed same-SHA actual Android actions
- S21 41379672845, job 125067485685, **FAIL**: 10,005 original trained decisions (33,312 to 43,317), signed/gross ~418.02822/3254.96393 = **12.8428%**; 7/10 windows >=10%, 2 inverse windows (4 and 9), 4 real new blue, 2,834 motor updates. Exact S21 V356 CRC and regression are separate PASS on same SHA.
- Wild 41379672756, job 125067485504, **FAIL**: 10,002 original autonomous decisions, signed/gross **722.10625/3997.52336 = 18.0638%**, **44 real red / 4 blue**, first 5k **19 red**, last 5k **25 red**, ratio 11.0. 5,079 motor updates, 1,716 harmful local-gradient rejects, 6,473 prior learned-harm actor expansions (from preceding V466 comparator), zero V371 verified directions, three V377 banks.
- Regressions 41379673166 **PASS**, original 33,312/512/V356 CRC 41379672884 **PASS**; all four SHA `3c878909fb2bfe770f6d0a5e14fc521a527095bb`.

### V467 S21 genuine 1000-decision windows (logcat)
|window|signed|gross|efficiency|new blue|life serial|
|--:|--:|--:|--:|--:|--:|
|1|58.56540|419.57005|13.9584%|0|0|
|2|110.16754|617.05038|17.8539%|1|1|
|3|46.03998|228.12024|20.1823%|0|1|
|4|-48.18263|334.43630|-14.4071%|1|2|
|5|71.62506|280.87922|25.5003%|0|2|
|6|60.49405|178.88324|33.8176%|0|2|
|7|7.41653|440.66614|1.6830%|1|3|
|8|50.55205|165.96955|30.4586%|0|3|
|9|-40.02255|311.36950|-12.8537%|1|4|
|10|101.37279|278.01931|36.4625%|0|4|

### V467 wild genuine 1000-decision windows (logcat)
|window|new blue|new red|signed/gross|
|--:|--:|--:|--:|
|1|3|0|6.7247%|
|2|0|0|21.3084%|
|3|0|0|17.9120%|
|4|0|10|12.8120%|
|5|1|9|4.8743%|
|6|0|0|20.0877%|
|7|0|5|21.9614%|
|8|0|5|20.3432%|
|9|0|5|21.5210%|
|10|0|10|29.7618%|

## Exact physical death PRE/POST (V467 logcat, not poll)
- Death 1 at cursor **34652**, life 0→1, reserve 0→100, COM velocity (-0.05215,-0.32516)→(0,0), signed **95.77264→95.77264**, gross **541.58104→541.58104**.
- Death 2 at cursor **37075**, life 1→2, velocity (-0.64074,+0.14131)→(0,0), signed **255.04955→255.04955**, gross **1437.71656→1437.71656**.
- Death 3 at cursor **39511**, life 2→3, velocity (-0.36393,-0.81695)→(0,0), signed **310.65087→310.65087**, gross **2091.66687→2091.66687**.
- Death 4 at cursor **42002**, life 3→4, velocity (+0.04767,-0.55156)→(0,0), signed **396.01182→396.01182**, gross **2788.29001→2788.29001**.
- PRE/POST identical signed/gross disproves a death-teleport increment contaminating these counters. It does **not** prove that GRU reset alone causes later reverse movement. Inverse blocks remain after reset, not a direct odometer jump.

## Source-confirmed single defect and causal intervention
`GameActivity.resetEpisode()` resets the 170-material body, COM velocity, original GRU recurrent activations via `Zero3OriginalBrainV356.newLife()` and the room. But `newLife()` did **not** clear `executed64`: the prior body's last 64 physical cortex commands persisted. `Zero3OriginalBrainV356.sense()` copies exactly those 64 stale values into the newborn GRU's **194D sensory input slots 124..187**, falsely reporting a motor efference the newborn's reset solver never executed. This is a concrete cross-life proprioceptive provenance error; whether it causes the 200+ decision post-rebirth reversals is unproven.

**ONE behavioral change in V468:** `Arrays.fill(executed64,0f)` inside original `newLife()` after aborting old action/resetting recurrent activations. No extra force, heading, valence, exploration width, brain weights, GRU topology, CH2 V319, 194 sensor channels, original material physics or checkpoint format changes. The 64 sensory channels remain, but correctly start as no previous motor action. Added newborn Android regression and exact trained S21 CRC test to assert no ghost commands and preserved decision cursor/512 motor weights/V356 decode. All four original Android workflows run on this separate branch.

## Precommitted original gates (UNCHANGED)
- S21 real saved 33,312-decision brain, >=10k new real decisions; signed/gross >=25%, >=9/10 adjacent 1000-decision windows >=10%, zero inverse, >=5 real blue.
- Wild real newborn >=10k autonomous decisions; signed/gross >=12%, red last 5000 fewer than first 5000, total red <=2× true blue.
- Full original Android regressions and exact original 33,312/512 V356 CRC **PASS on same SHA**. If any gate fails, V468 rejected, no APK/OTA/merge.

## Historical V421–V423 outcomes rechecked via Actions API
V421 S21 41149391000 FAIL 17.1723%, 8/10, 2 inverse, 4 blue; wild 41149393560 FAIL 87 red/3 blue (35→52); regression 41149404217 PASS, CRC 41149399737 PASS. V422 S21 41149758090 FAIL 11.2881%, 7/10, 2 inverse, 4 blue; wild 41149762948 FAIL 52 red/5 blue (19→33); regression 41149771615 PASS, CRC 41149767313 PASS. V423 S21 41149983685 FAIL 10.0486%, 7/10, 2 inverse, 4 blue; wild 41149987734 FAIL 26 red/4 blue (14→12); regression 41149995431 PASS, CRC 41149992279 PASS. The four jobs of each historical experiment were at differing SHA, so no same-commit approval inference.

## Status
V468 is a causal **hypothesis**, not a verified locomotion fix. Preserve user's exact 12 save slots, S21 brain, 512 weights, V356 CRC, CH2 V319 and 170-material body. Record real CI outcomes after Actions complete; do not infer from a green compile.

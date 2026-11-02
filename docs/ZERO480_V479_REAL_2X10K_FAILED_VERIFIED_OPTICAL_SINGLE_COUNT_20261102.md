# ZERO480 — V479 original Android 2×10k FAIL; verified optical evidence single-count ablation
Date 2026-11-02. Branch `lab/zero480-verified-optical-physical-single-count`. Based exactly on V479 `33ecc857c30306acb8b873ed1a58c5f60983f325`. Research ONLY; NO merge, APK, OTA, V2.2 restore, direction script, artificial reward, or checkpoint mutation.

## Source and history
Read AGENTS.md and ZERO419–ZERO423. Rechecked twelve historical GitHub Actions: V421 S21 41149391000 FAIL / wild 41149393560 FAIL / regressions 41149404217 PASS / CRC 41149399737 PASS; V422 41149758090 FAIL / 41149762948 FAIL / 41149771615 PASS / 41149767313 PASS; V423 41149983685 FAIL / 41149987734 FAIL / 41149995431 PASS / 41149992279 PASS. Historical S21 results V421 17.1723%, V422 11.2881%, V423 10.0486%, all 4 blue and inverse windows 4/9. Wild V421 87 red/3 blue (35→52); V422 52/5 (19→33); V423 26/4 (14→12). These are behavioral failures despite technical PASS.

## V479 four original Android Actions — all complete on identical SHA
- [S21 41481695145](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41481695145) FAIL: 10,003 self-generated original decisions, saved baseline 33,312 → 43,315, 512 original weights, 4 additional true blue, 0 new red (90 red in historical saved record, not new room touches), signed 290.70910 / gross 2758.79439 = **10.5375%**, 6/10 windows >=10%, inverse windows 4 and 9, 2690 local motor updates.
- [Wild 41481695318](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41481695318) FAIL: 10,002 original decisions, 3 blue, **33 red** (18 first 5k → 15 last 5k, improvement in trend but total red 11× blue), signed 340.87661 / gross 3507.83236 = **9.7176%** (<12%), 5083 local updates, 1760 harm-rejected motor credits.
- [Android regressions 41481695053](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41481695053) PASS.
- [Saved original V356/512 CRC 41481695119](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41481695119) PASS.
All checkout `33ecc857c30306acb8b873ed1a58c5f60983f325`. The four tests do not establish behavioral approval.

### S21 successive 1000-decision windows
|Bucket|Signed/gross|New blue|Wall contact frames|Static decisions|
|---|---:|---:|---:|---:|
|1|0.196749|0|24576|561|
|2|0.013562|1|14843|285|
|3|0.177523|0|24320|920|
|4|-0.199069|1|17706|625|
|5|0.396234|0|21066|540|
|6|0.185825|0|24528|789|
|7|0.096448|1|12582|302|
|8|0.258331|0|24480|753|
|9|-0.167522|1|16427|528|
|10|0.390039|0|21513|605|

### Wild successive 1000-decision windows
|Bucket|Signed/gross|New blue|New red|
|---|---:|---:|---:|
|1|0.033122|3|0|
|2|-0.044326|0|0|
|3|0.053335|0|0|
|4|0.049833|0|10|
|5|-0.073961|0|8|
|6|0.217320|0|0|
|7|0.093670|0|5|
|8|0.248274|0|4|
|9|0.189531|0|3|
|10|0.209865|0|3|

## Motor/GRU3 and sensory evidence
Wild V479 411,009 alternative evaluations, 31,730 V377 predictions, 280,715 unsupported drift evaluations, 280,715 passive-score evaluations, 279,446 passive-optical branch reaches; 6 V377 banks verified, 0 V371 directions, 2 transient V476 mid banks, 0 strict V444 reliable banks. 6,633 learned-harm expansion events; GRU3 remains sole original chooser. S21 291,341 alternatives, 6,042 V377 predictions, 201,003 unsupported drift, 4 V377 banks, 2 V371 directions, 0 mid reliable, 5 strict V444 banks. Actual S21 194D sensor topology and CH2 V319 remain unchanged. In wild hazard records, most touches have already-measured momentum toward danger; source motor predictions are sparse, so risk recognition alone does not prove ability to brake.

## One source-confirmed remaining duplicate and V480 single ablation
V479 removed duplicate `opticalPreferenceV374` for unsupported PRE drift, but **left the same duplication for V377 predicted travel and V371 heldout-verified reconstructed travel**: `score()` already computes learned retinal/contact consequence of that exact predicted physical displacement; `opticalPreferenceV374()` scores the same retina and physical bearing a second time, normalized to unit direction. This double weights a single physical observation relative to unverified alternatives. V480 makes opticalPreference zero ONLY when the SAME verified physical displacement has already been scored; unverified stationary optical hypothesis, V479 unsupported drift handling, real red/blue contact valence, candidate generation, GRU, body solver, 512 synapses, CH2 and checkpoints unchanged. This is a *causal accounting correction*, not proof of safer behavior. The regression V480 covers heldout-verified positive and negative actual-contact consequences and stationary unverified fallback; it does not replace real Android 2×10k.

## Unchanged approval gates
S21 original >=10000 self-generated decisions, >=25% signed/gross, >=9/10 1k windows >=10%, zero inverse, >=5 true new blue. Wild original >=10000, >=12% signed/gross, latter 5000 red < first 5000, total red <=2× true blue. Same SHA Android regressions and saved original V356 CRC PASS. Until ALL pass, **no APK/OTA/merge or solved claim**. V480 Actions must be inspected after push; no pending run is pre-certified.

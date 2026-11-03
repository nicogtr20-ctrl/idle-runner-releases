# ZERO486 — V485 completed Android audit and prospective observed-only motor baseline diagnostic

Date: 2026-11-03. Isolated branch `lab/zero486-independent-observed-baseline-motor-credit-audit`. Parent ZERO485 exact SHA `8ea8093a1ebd9706ddd3c77d7a90d65bdff54ff7`. **Research only: no behavioral patch, no APK/OTA/merge.**

## Read-first constraints and provenance
Read repository `AGENTS.md`, `docs/AGENTS.md`, ZERO419–423, MASTER, CONTRACT, UI SPEC, and issue #123. Original user S21 brain is 33,312 decisions / 19,452 prior motor updates, 512 original cortical weights, V356 CRC, original 170-material body, 194 GRU3 senses, V319 CH2, and all twelve save slots. No V2.2 control, invented rewards, predefined motor directions or maps.

Historical V421–423: all six S21/wild >=10k behavioral runs FAIL; six Android regression/CRC runs PASS, but historical gate SHAs must not be treated as a common four-run approval. V482 original S21 10,003 FAIL 10.1855%, 6/10, inverse 4 and 9, 4 blue; wild 10,002 FAIL 51 red/4 blue, red 19->32; technical gates PASS. V483 and V484 were diagnostic-only and also behavior FAIL.

## V485 exact completed four GitHub Actions (same SHA `8ea8093a`)
- [S21 41544858265](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41544858265) **FAIL**, 10,002 real decisions after original 33,312; signed 280.79178 / gross 2757.25055 = **10.1838%**, 6/10 windows >=10%, inverse windows 4 and 9, **4** new blue, 2,669 local motor updates. True 170-material original physics and autonomous GRU3.
- [Wild 41544857983](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41544857983) **FAIL**, 10,003 genuine decisions, signed 580.11568 / gross 3862.56816 = **15.0189%**, **51 red / 4 blue**; red first 5,000 = 19, last 5,000 = 32; 5,396 local updates, 1,643 harm-rejected local credits. Meets movement >=12% but fails BOTH red criteria.
- [Regressions 41544858162](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41544858162) **PASS**.
- [Original S21 V356 CRC and causal Android diagnostics 41544858092](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41544858092) **PASS**, original 512 synapses and 33,312 saved decisions unchanged, original V483/V484/V485 physical tests pass. Workflow conclusion SUCCESS is technical, not scientific success.

### All 10 original S21 1000-decision windows
| Window | Signed | Gross | Signed/gross | New blue |
|---|---:|---:|---:|---:|
| 1 | 46.72037 | 236.90651 | 19.7210% | 0 |
| 2 | 6.30194 | 477.18005 | 1.3207% | 1 |
| 3 | 18.94195 | 106.70117 | 17.7523% | 0 |
| 4 | -59.65307 | 299.65974 | **-19.9069%** | 1 |
| 5 | 114.07852 | 287.90724 | 39.6234% | 0 |
| 6 | 29.29700 | 157.65911 | 18.5825% | 0 |
| 7 | 21.40898 | 437.77525 | 4.8904% | 1 |
| 8 | 44.79858 | 169.55624 | 26.4211% | 0 |
| 9 | -54.26149 | 321.11856 | **-16.8976%** | 1 |
| 10 | 113.13371 | 262.67675 | 43.0696% | 0 |

### All 10 original wild 1000-decision windows
| Window | Signed | Gross | Signed/gross | Red in window | Blue in window |
|---|---:|---:|---:|---:|---:|
| 1 | 12.58351 | 379.91408 | 3.3122% | 0 | 3 |
| 2 | -12.57685 | 283.73775 | -4.4326% | 0 | 0 |
| 3 | 19.64188 | 373.55372 | 5.2581% | 0 | 0 |
| 4 | 32.74616 | 392.54144 | 8.3421% | 9 | 0 |
| 5 | 44.06426 | 398.58536 | 11.0552% | 10 | 0 |
| 6 | 83.68238 | 413.52980 | 20.2361% | 10 | 0 |
| 7 | 107.15052 | 357.09641 | 30.0060% | 10 | 0 |
| 8 | 84.96447 | 381.81001 | 22.2531% | 0 | 1 |
| 9 | 134.23505 | 479.85449 | 27.9741% | 12 | 0 |
| 10 | 73.68471 | 401.44217 | 18.3550% | 0 | 0 |

## V485 real causal-gradient result (CRC+diagnostic run 41544858092)
Original S21 cortex and 170-material physical solver, same-state ON/OFF V390 genuine small CH0/1/3 perturbations; 8 repeats x 48 sites x 3 initial westward speeds = **1,152** comparisons. All 1,152 physically measurable. The unpaired estimator uses `score(ON)*delivered`, oracle reference uses `[score(ON)-score(OFF)]*delivered` at identical PRE state, with physical work included.

| PRE west speed | Single-sample opposite sign | 48-site mean opposite sign | Mean estimator/causal cosine | Mean abs unpaired | Mean abs paired |
|---|---:|---:|---:|---:|---:|
| 0.35 | 186/384 | 16/48 | 0.335036 | 0.009555677 | 0.004172545 |
| 1.00 | 195/384 | 19/48 | 0.332185 | 0.011607232 | 0.003890764 |
| 2.00 | 195/384 | 18/48 | 0.245212 | 0.014504917 | 0.003023370 |

47/48 sites at each speed sampled both positive and negative genuine V390 raw perturbations. Aggregate single-sample discordance = **576/1152 (50%)**. This is strong evidence of noisy, poorly aligned *finite-sample* local credit in these states, not proof of asymptotic bias or that 576 real synapse updates are incorrect. The original V417 whole-body POST-minus-PRE includes passive drift, unprobed muscle motion and real work. `AdaptiveCortex.learnV332` multiplies the full measured response by the delivered probe without subtracting a learned baseline; its separate `learn()` path does have an 8-heading context baseline, but the V423 own-decision local learning uses `learnV332`, not `learn()`. Replacing live credit with sequential V454 ABBA is NOT justified: historical V454 ON and OFF failed behavior.

## ONE new isolated ZERO486 experiment — observed-only baseline control variate
Add exactly one read-only Android test to `Zero484ActualV390ProbeMatchedPhysicalAndroidTest`. For each speed 0.35, 1, 2 U/s, perform 16 full cycles x 48 actual V390 sites, **2,304 genuine physical ON/OFF comparisons** total. Rotate four laboratory headings across cycles, so every site sees all four, unlike V485's fixed site-heading pairing. First cycle warms up a baseline from prior **ON physical outcomes only** per heading. For remaining 15 cycles, compare per-site:
1. existing unpaired `score(ON)*delivered`;
2. independent historical baseline `[score(ON)-mean(prior ON scores at heading)]*delivered`;
3. physically matched oracle `[score(ON)-score(OFF)]*delivered` **only as heldout truth**, never to compute the candidate baseline.

Log both cosine similarities to oracle, wrong-sign site counts, and mean absolute signal at each speed; no threshold chosen after observing results, no claim that an offline oracle can be used in live gameplay. The prospective baseline is a variance-reduction hypothesis, not a new reward, steering policy, physics model or online motor patch. If it fails to improve alignment consistently, reject it. If it improves, next assess contact, wall, mixed headings and genuinely online adaptation before changing the 512-weight learner. Same-sha four original Android gates required; expected behavioral FAIL because live code is untouched.

## No release
Hard gate unchanged: S21 >=25%, >=9/10 1k windows >=10%, zero inverse, >=5 true new blue; wild >=12%, red late < early, red <=2x blue; regressions and CRC PASS. Preserve V319 CH2, 194 sensors, saved S21/512/V356 CRC, original physics/game/UI and 12 slots. No APK, OTA, merge.

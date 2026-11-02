# ZERO475 — completed original 2×10k Android results and motor-speed regime audit (NO RELEASE)

Date: 2026-11-02. Independent read of GitHub Actions job logs, source and AGENTS.md; historical ZERO419–ZERO423 dossiers reviewed first. This is a **read-only audit branch** off V475 `0c164323a59d7c69770351a0b1e9c331bdba2f2f`. No production source, brain, weights, sensors, physics, reward, original user data, 12 slots, APK, OTA or stable ref modified.

## Same-SHA V475 Android actions — FINAL

All four runs use **exact** SHA `0c164323a59d7c69770351a0b1e9c331bdba2f2f`:

| Original Android gate | Run | Conclusion |
|---|---|---|
| S21 saved 33,312 original decisions + >=10,000 real self-generated | [41447731784](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41447731784) | **FAIL behavior** |
| Wild original GRU3 >=10,000, real red/blue | [41447732049](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41447732049) | **FAIL behavior** |
| Complete original Android regressions + V475 diagnostics | [41447732001](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41447732001) | **PASS** |
| Exact S21 original V356 CRC / 512 weights | [41447731877](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41447731877) | **PASS** |

**S21 original:** final cursor **43,315** from 33,312 = **10,003 new decisions**; signed **265.38575**, gross **2751.97541**, signed/gross **9.6435%**; **6/10** >=10% windows, **two inverse (4 and 9)**, **four actual new blue** (four real rebirths), **2677** local cortex updates. Red cumulative 90 is inherited from original S21, NOT new S21 harm. The ten actual 1k windows:

| Bucket | Signed | Gross | Ratio | New blue |
|---|---:|---:|---:|---:|
| 1 | +46.72037 | 236.90651 | +19.7210% | 0 |
| 2 | +6.30194 | 477.18005 | +1.3207% | 1 |
| 3 | +18.94195 | 106.70117 | +17.7523% | 0 |
| 4 | -59.65307 | 299.65974 | **-19.9069%** | 1 |
| 5 | +114.07852 | 287.90724 | +39.6234% | 0 |
| 6 | +29.29700 | 157.65911 | +18.5825% | 0 |
| 7 | +32.59368 | 451.81399 | +7.2140% | 1 |
| 8 | +43.83314 | 166.98106 | +26.2504% | 0 |
| 9 | -65.45522 | 304.68422 | **-21.4830%** | 1 |
| 10 | +98.72744 | 262.48232 | +37.6130% | 0 |

**Wild original:** **10,005** real decisions, signed **39.29498**, gross **3515.63351**, efficiency **1.1177%**; **3 blue / 30 red** (red/blue **10.0×**, required <=2×); **13 red in first 5,000** vs **17 in last 5,000** (worsening, not decreasing); **5107** local cortex updates and **1799** harmful-credit exclusions. Wild 1k buckets (red / blue / signed-gross %):

1. 0 / 3 / +3.3122%
2. 0 / 0 / -4.5663%
3. 0 / 0 / +5.3940%
4. 9 / 0 / +3.6620%
5. 4 / 0 / -5.2042%
6. 0 / 0 / +12.5916%
7. 7 / 0 / -8.3373%
8. 3 / 0 / -3.8635%
9. 4 / 0 / +2.7175%
10. 3 / 0 / +2.9864%

## The actual V475 reachability result (candidate evaluations, NOT decisions)

| Physical/visual motor telemetry | S21 | Wild |
|---|---:|---:|
| Candidate score evaluations | 291,210 | 403,399 |
| V377 predicted motor travel | 18,504 | 61,109 |
| V474 score reconstructions actually reached | **4,894** (**1.68%** of evaluations) | **567** (**0.14%**) |
| V474 optical reconstructions reached | 1,683 | 492 |
| Unsupported PRE drift score blocks | 187,810 (**64.49%**) | 258,716 (**64.13%**) |
| Optical drift suppressions | 181,017 | 258,246 |
| V377 reliable banks by production verification | 4 | 4 |
| V371 verified directions | 2 | 1 |
| V444 stricter reliable V377 banks | 5 | **1** |
| Delayed real contact links | 395 | **0** |

The V474 mathematically corrected fallback is **almost never reached in wild**. Its use cannot reasonably explain a large improvement in avoidance; V475 is observational and behaves nearly identically to V474. The 64% drift block is intentionally safety-preserving; removing it without verified action→physical response would repeat the unsafe V469 optical-under-drift experiment, which failed. The S21 four rebirths correlate with both reverse windows but not all rebirths; do not assert a proved death-reset root cause.

## Real harmful contacts and motor causality

All **30** true wild HARM contact lines in `ZERO404_HAZARD` were individually parsed (not conflated with candidate evaluation counts): **24/30** had PRE measured momentum toward the hazard (>0.3), **16/30** had the commanded intention toward it (>0.3), **28/30** reported already visible learned harm, **12/30** had zero nonzero optical-preference candidates, **13/30** had zero nonzero motor-scoring candidates, and **15/30** had best-minus-chosen raw optical gap >0.1. The optical sampling at the exact touch was zero in 18/30 logs. These overlap; the data do not prove that any other requested heading could have physically braked in time. Candidate counts at red contact were 3 (2 events), 5 (20), 7 (8). The original GRU remains sole chooser.

## Source-level discrepancy to test before another behavioral patch

In `Zero3InertialResponseV377.slot`, PRE COM speed is split into just two motor-response regimes at **2.0** physical units/second. But `Zero3InstrumentalCreditV371.unsupportedPreDriftV441` treats speeds **>=0.35** as significant physical drift that cannot be replaced by intended heading. Thus measured moving states in the **0.35–2.0** interval are mixed into V377's low-speed model with nearly stationary states while V433 censors their optical fallback if V377 does not independently validate the corresponding action. This is a **source-confirmed regime-resolution mismatch**, NOT proof that moving the split improves prediction or motor control. The existing original S21 saved V377 banks already contain learned data under the old 2.0 boundary; **changing the boundary in place would reinterpret saved evidence and is not permitted as an unguarded fix**.

A legitimate next causal test must measure the actual distribution and independent heldout prediction loss in speed bands <0.35, 0.35–2.0 and >=2.0, plus signed actuator response vs PRE coasting, before attempting a single isolated motor-model intervention. Any new model must leave the original V356 serialized banks and 12 slots intact, be opt-in on a separate branch, and re-run same-SHA original Android S21/wild 10k + regressions + CRC. Do not bypass V433 or lower the user's gates.

## Historical V421–V423 rechecked in this audit

All twelve run statuses independently queried on 2026-11-02: V421 S21 41149391000 FAIL, wild 41149393560 FAIL, regression 41149404217 PASS, CRC 41149399737 PASS; V422 S21 41149758090 FAIL, wild 41149762948 FAIL, regression 41149771615 PASS, CRC 41149767313 PASS; V423 S21 41149983685 FAIL, wild 41149987734 FAIL, regression 41149995431 PASS, CRC 41149992279 PASS. These historical runs are NOT all one identical SHA within each group and must not be presented as same-SHA integrity proof.

## Decision

**ZERO 3.0 is NOT approved.** S21 <25%, only 6/10 qualifying windows, two inverse, four blue; wild red rate increases and 30 > 2×3. Technical regressions/CRC PASS do not override behavior. No V2.2, authored heading, reward, new motor force, checkpoint rewrite, APK, OTA or merge. This audit deliberately does **not** push an unjustified behavioral change: the V475 evidence points to missing independently validated physical steering, not to a safe one-line correction.

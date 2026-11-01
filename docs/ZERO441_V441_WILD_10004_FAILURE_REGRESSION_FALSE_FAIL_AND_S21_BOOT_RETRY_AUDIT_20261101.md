# ZERO441 — actual V441 Android failure audit, V441 S21 boot retry, and V442 causal candidate (NO behavioral change)
Date: 2026-11-01. Branch: `audit/zero441-real-wild-49-red-and-qa-false-fail-20261101`. Based on tested V441 source `ca3f7f83051fb8b1dfc1840373aa7742ca63ca80`. **Audit only. No APK, OTA, stable merge, trained-brain changes, saved-slot writes, new steering, rewards or actuator changes.**

## Read-first / immutable contract
Read root `AGENTS.md`, `docs/AGENTS.md`, ZERO419–ZERO423, ZERO440–ZERO441, MASTER, IMPLEMENTATION CONTRACT, UI SPEC and actual GitHub Actions job logs. Preserve real original S21 33,312 decisions, 512 cortex weights, V356 checkpoint CRC, V319 CH2 grip, 194 sensors, 64 GRU, original 170-material solver and twelve slots. No V2.2 blending, no preset headings, no manufactured energy or goal.

## Historical original Android runs reverified 2026-11-01
| Version | S21 10k | Wild 10k | Regression | Original CRC |
|---|---|---|---|---|
| V421 | 41149391000 FAIL (17.1723%, 4 blue, 2 inverse) | 41149393560 FAIL (87 red / 3 blue; 35→52) | 41149404217 PASS | 41149399737 PASS |
| V422 | 41149758090 FAIL (11.2881%, 4 blue, 2 inverse) | 41149762948 FAIL (52 red / 5 blue; 19→33) | 41149771615 PASS | 41149767313 PASS |
| V423 | 41149983685 FAIL (10.0486%, 4 blue, 2 inverse) | 41149987734 FAIL (26 red / 4 blue; 14→12) | 41149995431 PASS | 41149992279 PASS |

The twelve exact run IDs were checked via GitHub Actions API and their conclusions match the prior forensic dossiers; these are separate experimental SHAs, not a single candidate.

## V441 actual source commit and four Actions
- S21 `41262684117`, job `124718987452`, attempt 1 **FAIL before valid 10k data**: Android built, exact user brain reconstructed (`len=613516`), emulator launched GameActivity, but `Zero417TrainedS21VelocityCredit10000AndroidTest.java:184` asserted `Original sole GRU3 must really finish boot`. No valid 1000-decision S21 windows, signed/gross, or new blue can be inferred. Re-run same job without code change was requested successfully (attempt 2 pending/in progress at audit time).
- Wild `41262684124`, job `124718987378`, attempt 1 **completed genuine 10,004 decisions and FAIL scientific gates**: actual newborn cursor 0→10,004, original GRU owner true; 4 blue, **49 red**, first 5,000 **29 red**, last 5,000 **20 red** (red decreases, but 49 > 2×4), signed `52.20141`, gross `3667.09150`, **1.4235%** vs 12% required. No blue after decision ~3001. 5,450 actual local cortex updates, 4,554 censored, 1,675 harm-rejected, 8,186 real learned-harm 7-candidate expansions; 6.125 red per blue. V440 comparator: 39 red / 4 blue, 15→24, -3.2351%; V441 improves the red temporal trend and signed/gross but worsens red total (39→49) and fails absolute requirements.
- Original Android regression `41262684185`, job `124718987405`: **workflow FAIL from stale test-count assertion**, not a reported JUnit failure. All 23 instrumented suites produced `OK (... tests)`; new `Zero433InertialOpticalEvidenceAndroidTest` now has **5 tests** and logs `OK (5 tests)`, while YAML still executes `grep -q "OK (3 tests)" "$RUNNER_TEMP/Zero433InertialOpticalEvidenceAndroidTest.txt"`. With `set -e`, that mismatch alone terminates shell exit 1. Must fix workflow assertion and rerun to obtain a real regression PASS. Never label the current workflow PASS.
- Original S21 V356/512 checkpoint CRC `41262684187`, job `124718987351`: **PASS**; original real user brain reconstructed and bit-identical in the integrity fixture. This is not a behavioral pass.

### V441 wild 1,000-decision windows, original material contacts
| Window | Decision cursor | Signed | Gross | Signed/gross | New blue | New red |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | 1002 | 53.48659 | 382.96564 | 13.9664% | 3 | 0 |
| 2 | 2005 | 117.87545 | 411.23348 | 28.6639% | 0 | 4 |
| 3 | 3001 | 102.39113 | 433.90477 | 23.5976% | 1 | 15 |
| 4 | 4005 | 45.22715 | 446.16242 | 10.1369% | 0 | 10 |
| 5 | 5005 | 28.23548 | 325.64749 | 8.6706% | 0 | 0 |
| 6 | 6002 | -27.56650 | 399.34494 | -6.9029% | 0 | 0 |
| 7 | 7003 | -116.91615 | 401.07751 | -29.1505% | 0 | 11 |
| 8 | 8003 | -93.62011 | 312.23353 | -29.9840% | 0 | 9 |
| 9 | 9004 | -51.58837 | 290.94970 | -17.7310% | 0 | 0 |
| 10 | 10003 | -5.14008 | 263.25936 | -1.9525% | 0 | 0 |

Windows are approximate cursor-based 1,000 increments, not exact boundaries at the four final decisions. Final signed/gross comes from final `ZERO417_WILD_REAL_10000_RESULT`, not rounded bucket totals.

## Original physical contact forensic
The 49 `ZERO404_HAZARD REAL_MATERIAL_CAUSALITY` records all carry actual -7 energy deltas, original real contact, PRE COM momentum and current GRU action. **45/49** had PRE COM velocity toward the touched hazard; **30/49** issued intention materially away (`intentionToward < -0.3`), **12/49** materially toward (`>0.3`); **35/49** PRE speed >=0.35U/s; **47/49** offered seven genuinely sampled candidates; **29/49** selected optical scores >.01 below the best of *offered* options (not a 360° optimum). Counts are from complete job text filtered for actual material harm; none proves a specific muscle gradient caused the collision. Red touch optical center azimuth can disagree with true retina eye azimuth per ZERO419.

## Sensor, actor, motor credit and possible causal defect
Actual `Zero3OriginalBrainV356.sense`: coarse 16/17 use measured bodyVel /10 after V440; separate slots 191/192 remain /130, and V371/V377 decode 191/192 ×130. There is **no V440/V441 13× velocity unit mismatch** in those two slots. 194D input, 64-GRU actor and 512 local weights are unchanged. The original V319 grip owns CH2. Lower motor credit counters above prove updates but not an effective controllable brake; true 64-output PRE/POST delivered-gradient traces remain unavailable in the workflow log.

**Source-confirmed attribution gap for a future single-variable V442 experiment:** In `Zero3InstrumentalCreditV371.score`, if `inertia.expected` is unavailable and PRE speed >=.35, the strict V441 branch still returns `observedDelayedContactScoreV434(observation, bank, contact)`. The bank is computed from the *new commanded heading*, while the historical V434 score is only an association between a prior heading bank and an actual contact following measured approach; V435 requires actual forward acceleration when recording it, but that does not independently validate current motor controllability in this PRE speed regime. In the same state, `opticalPreferenceV374` correctly rejects pretending that the new heading is material travel. Thus two candidate scoring paths use conflicting standards of physical action evidence. A narrowly scoped V442 test could censor **only the V434 bank-score fallback at measured PRE drift >=.35 without a heldout-validated V377 response**, while keeping the contact valence, V435 historical memories, low-speed V434 behavior and reliable V377 predictions. This is a causal *hypothesis*, not proof of a locomotion cure; original S21/wild 10k, regression and CRC must decide. No such behavior change is made on this audit branch.

## Mandatory next actions
1. Wait for V441 S21 unchanged attempt 2 and read complete original Android log; retry only transient emulator boot failures, never invent S21 windows.
2. Correct stale regression workflow expectation `OK (3 tests)`→`OK (5 tests)` on a separate experimental branch and rerun real Android regression.
3. If proceeding with V442, change only the V434 observational-bank fallback eligibility under unsupported measured drift, add Android unit tests for PRE drift/low speed/heldout V377, and run **both original 10k Android lives** plus full regressions and original S21 CRC on one source SHA. Do not claim approval unless every user gate passes.

**Current status: ZERO 3.0 NOT APPROVABLE. No APK, OTA, stable merge, or modifications to original brain, body, checkpoints or 12 saved profiles.**

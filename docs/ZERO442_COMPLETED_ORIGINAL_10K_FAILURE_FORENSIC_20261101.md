# ZERO442 — definitive original Android 2×10,000 forensic results (NON-RELEASE)

Date: 2026-11-01. **Documentation-only audit branch** `audit/zero442-real-10k-failure-and-drift-root-cause-20261101` based on the **exact tested source** `14c3a7ffbdf508c7e4f60ea8b4fba389fd8573e1` on `lab/zero442-no-unverified-delayed-bank-under-drift`. No behavioral source edits, user brain/saves, release, OTA, APK, or stable merge. Original V388 phone remains unchanged.

## Source contract and historical verification

Read root `AGENTS.md`, `docs/AGENTS.md`, `docs/ZERO419...` through `ZERO423...`, `ZERO30_MASTER_BLUEPRINT.md`, `ZERO30_IMPLEMENTATION_CONTRACT.md`, and `ZERO30_UI_UX_SPEC.md`. Exact original 33,312-trained S21 512 weights/V356 CRC, 194 sensory inputs, 64 GRU, V319 CH2 grip, 170-material body, 12 saved slots, no 2.2 motor replacement and no pre-authored directions or rewards.

The twelve original historical V421–V423 Actions were rechecked **directly** on 2026-11-01. Each version's original regression and original CRC workflow succeeded, but **both 10k behavioral workflows failed**:
| Version | S21 run, decisions, signed/gross, new blue | Wild run, decisions, signed/gross, red/blue, first/last 5000 red | Regression | CRC |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000): 10,001, **17.1723%**, 4 | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560): 10,002, 61.6262%, **87/3**, 35→52 | [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS | [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS |
| V422 | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090): 10,003, **11.2881%**, 4 | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948): 10,003, 42.7053%, **52/5**, 19→33 | [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS | [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS |
| V423 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685): 10,002, **10.0486%**, 4 | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734): 10,003, 31.0766%, **26/4**, 14→12 | [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS | [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS |

Note: the displayed S21 final `decisions` counters are 43,313/43,315/43,314 **including** 33,312 saved original decisions. New session decisions are the difference. Wild first/last red counts come from individual real `ZERO404_HAZARD` event decisions and agree with 1k windows. All full job logs were inspected for terminal metrics and 1k windows; no historical PASS is inferred from a unit test.

## V441 S21 unchanged retry (previously boot-blocked) — now completed FAIL

[Run 41262684117, attempt 2](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684117), original V441 SHA `ca3f7f83051fb8b1dfc1840373aa7742ca63ca80`: 43,315 total decisions (10,003 new), signed **357.91440**, gross **2823.38834**, **12.6768%**, **4 new blue**, 2 inverse windows (4,9), 6/10 windows >=10%, **2766** local synapse updates. Actual test fails. Earlier V441 wild [41262684124](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684124) had 10,004 decisions, 49 red /4 blue, 29→20 red, 1.4235% signed/gross. The original V441 Android regression count was a YAML false negative; corrected unchanged behavior regression [41266183060](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266183060) PASS; CRC [41262684187](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41262684187) PASS.

## V442 — actual completed four Actions

| Gate | Action | Result |
|---|---|---|
| Original S21 10k | [41266630484](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630484), job 124730672217 | **FAIL**, valid 10,002 new decisions |
| Original wild 10k | [41266630508](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630508), job 124730672324 | **FAIL**, valid 10,005 decisions |
| Original regressions and V442 four-case causal test | [41266630497](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630497), job 124730672364 | **PASS** |
| Exact saved original S21 V356 CRC/512 weights | [41266630512](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41266630512), job 124730672230 | **PASS** |

S21 final: `decisions=43314 baseline=33312 blue=4 red=90 signed=307.39532 gross=2758.68564 efficiency=0.111428 localWeightUpdates=2754 motorOwner=true`. **The red=90 is inherited from the original saved user history; this S21 room contains no new red contacts.** 4 new blue <5. 6/10 windows >=10%; two inverse windows. These are genuine Android physical results, not emulator boot errors.

Wild final: `decisions=10005 baseline=0 blue=4 red=49 signed=52.12098 gross=3667.33597 efficiency=0.014212 localWeightUpdates=5450 localHarmRejected=1675 motorOwner=true`. 29 actual red events in decisions 1–5000 and 20 in decisions 5001–10005 (improves rate), but 49 red versus only 4 blue (**12.25×** blue, allowed <=2×). All 4 blue are obtained by ~3002 decisions; no later blue. The final efficiency is **1.4212%**, below required >=12% wild.

### Exact 1,000-decision windows from original Android log

| Window | S21 signed/gross % | S21 new blue | Wild signed/gross % | Wild new blue | Wild red |
|---|---:|---:|---:|---:|---:|
| 1 | 22.9782 | 0 | 13.9158 | 3 | 0 |
| 2 | 1.1300 | 1 | 28.7816 | 0 | 4 |
| 3 | 24.5288 | 0 | 23.5816 | 1 | 15 |
| 4 | **−19.4274** | 1 | 10.1058 | 0 | 10 |
| 5 | 30.3373 | 0 | 8.6655 | 0 | 0 |
| 6 | 28.9872 | 0 | **−6.7151** | 0 | 0 |
| 7 | 8.8843 | 1 | **−29.3392** | 0 | 11 |
| 8 | 30.2897 | 0 | **−30.0597** | 0 | 9 |
| 9 | **−3.3795** | 1 | **−17.5044** | 0 | 0 |
| 10 | 14.4162 | 0 | **−2.2047** | 0 | 0 |

The 1k buckets are instrumented near the exact thousand boundary (e.g. 1005, 2004); exact first/last 5000 red is calculated separately from per-event decision numbers, not rounded bucket boundaries.

### Physical collision, learning and optical forensic

From **all 49** `ZERO404_HAZARD REAL_MATERIAL_CAUSALITY sign=HARM` lines in the full wild job log:
- **45/49** had PRE material COM momentum already pointing toward the hazard; **30/49** had current GRU command pointing materially away (`intentionToward < −0.3`).
- **35/49** had PRE COM speed >=0.35U/s, the V441/V442 strict drift threshold; **47/49** had seven actor-sampled alternatives (`offered=7`).
- **32/49** had the chosen offered optical valence below the best offered by >0.001. This is *not* proof the best optical option would have changed real body travel, and V405 optical telemetry is actor-heading based, not an independently verified actuator response.
- 4 blue contacts, 49 real red contacts, 5,450 local weight updates and 1,675 rejected harmful local samples. Training count is not evidence of successful locomotion.

**Crucial controlled result:** V442 wild first ten 1k window metrics, 49 red/4 blue and signed/gross trajectory reproduce V441 wild essentially identically (minor final extra decision). Thus V442's isolated censoring of an unverified delayed-bank score did **not** materially alter wild behavior. The 4-case Android regression proves the branch can censor a *constructed* historical score; it does NOT prove such a score was present in the genuine 10k wild decisions. No behavioral improvement can be credited to V442.

### Source-level causal limitation (verified, not yet a demonstrated motor fix)

In `Zero3InstrumentalCreditV371.score`, if V377's `inertia.expected` is absent and PRE COM speed >=0.35, V442 returns 0 rather than `observedDelayedContactScoreV434`. In `opticalPreferenceV374`, the same strict drift/no-verified-map state already returns 0. In `Zero3CognitionV353.select`, candidate scoring then falls back to other learned/predictive components. This may leave **no discriminative material-contact directional score** for those candidate choices despite real V369 harm memory. The 49 red contacts demonstrate persistent motor/sensory coupling failure, **not** that a specific V442 score branch was invoked or that any guessed commanded direction would have been physically safe.

Another source fact: `Zero3InertialResponseV377.slot` separates PRE velocity at **2 U/s**, whereas V441/V442 reject unsupported drift from **0.35 U/s**. Most red events are in the 0.35–2 interval. This is a potential physical-state aggregation mismatch requiring measured evidence; do **not** silently reinterpret saved V377 bank statistics, alter the original body, or lower thresholds based on the 10k outcome.

### Decision / ADR: no unsupported behavioral patch

The tested V442 code is rejected for release. A next isolated LAB should **first instrument read-only counters** for how often the V442 censor path actually fires with a nonzero historical score, how often V377 returns a verified motor prediction, and the per-choice separation of learned-contact/optic/predictor/planner scores versus PRE and POST COM travel. This is necessary to distinguish a no-op from a ranking failure and a real actuation failure. Then use exactly one source-proven corrective hypothesis with original Android 2×10k + regression + CRC. A premature change to action headings, reward or body physics would violate the causal contract.

**All gates remain binding:** S21 >=10k, >=25% signed/gross, >=9/10 windows >=10%, zero inverse, >=5 new blue; wild >=10k, >=12% signed/gross, red last 5000 < first 5000, red <=2× blue; original Android regression and saved S21 CRC both PASS. No APK, OTA, stable merge, or claim of fixed locomotion.

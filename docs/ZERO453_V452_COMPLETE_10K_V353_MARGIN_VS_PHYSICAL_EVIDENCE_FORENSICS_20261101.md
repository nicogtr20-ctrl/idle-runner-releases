# ZERO453 — V452 complete original 2×10,000 Android results; V353 confidence/optical score audit
Date: 2026-11-01. Experimental branch `lab/zero453-v353-score-margin-physical-evidence-audit`. **NOT a behavioral fix, not release-approved. No APK/OTA/merge.**

## Sources and protected invariants
Read root AGENTS.md, docs/AGENTS.md, ZERO419–ZERO423 and master blueprint, implementation contract and UI contract. Re-fetched all twelve historical V421/V422/V423 Actions and read six actual 10k behavioral job logs. Their scientific results remain FAIL; their six Android regression/CRC jobs all PASS. Source at V452 exact commit `e302fd71d92ff8329f7d294d1c7759462405a595`; V452 diagnostics only, not a new brain. User S21 restored from actual 33,312-decision original; 512 cortex weights, V356 checkpoint CRC, 194 senses, V319 CH2, original 170-material solver, original twelve save slots and phone remain untouched.

| Prior version | S21 signed/gross | S21 >=10% windows | inverse windows | S21 new blue | Wild signed/gross | red first/last 5k | wild red/blue | local updates S21/wild |
|---|---:|---:|---:|---:|---:|---|---|---|
| V421 | 17.1723% | 8/10 | 2 | 4 | 61.6262% | 35/52 | 87/3 | 1918/5843 |
| V422 | 11.2881% | 7/10 | 2 | 4 | 42.7053% | 19/33 | 52/5 | 1851/4587 |
| V423 | 10.0486% | 7/10 | 2 | 4 | 31.0766% | 14/12 | 26/4 | 2962/4712 |

Historical run IDs S21/wild/regression/CRC: V421 `41149391000/41149393560/41149404217/41149399737`; V422 `41149758090/41149762948/41149771615/41149767313`; V423 `41149983685/41149987734/41149995431/41149992279`.

## V452 exact-commit Android results (four workflows)
- [S21 41307947006](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307947006): **FAIL**. 10,002 new decisions, 18.0209% signed/gross, 8/10 good 1k windows, 1 inverse window (4), 4 new actual blue, 3,084 cortex updates. 90 cumulative red are **inherited** from the user brain, not new red contacts.
- [Wild 41307947019](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307947019): **FAIL**. 10,006 original decisions, 33.7023% signed/gross at final assertion, 50 actual red and 5 actual blue, 37/13 red in first/last five 1k buckets, 5,441 cortex updates and 1,693 harmful local credit rejections. Red declining PASS, total red <=2× blue FAIL.
- [Android regressions 41307946994](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307946994): **PASS**.
- [Original S21 512-weight V356 CRC 41307946992](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41307946992): **PASS**.

| 1000-decision bucket | S21 signed/gross % | S21 new blue | S21 static/opposed | Wild signed/gross % | Wild new blue | Wild new red |
|---|---:|---:|---|---:|---:|---:|
| 1 | 26.3334 | 0 | 539/54 | 13.9308 | 3 | 0 |
| 2 | 14.0119 | 1 | 209/244 | 28.8700 | 0 | 4 |
| 3 | 30.3092 | 0 | 489/45 | 21.3400 | 0 | 13 |
| 4 | -3.3456 | 1 | 385/229 | 36.1290 | 0 | 9 |
| 5 | 28.7760 | 0 | 615/21 | 30.4093 | 0 | 11 |
| 6 | 23.7534 | 0 | 914/0 | 36.3985 | 0 | 7 |
| 7 | 16.1813 | 1 | 244/257 | 41.3985 | 0 | 5 |
| 8 | 31.6731 | 0 | 563/31 | 41.6319 | 0 | 0 |
| 9 | 3.9113 | 1 | 470/175 | 40.6350 | 1 | 1 |
| 10 | 24.9692 | 0 | 451/77 | 39.4298 | 1 | 0 |

The ten bucket ratios above are taken from actual Android `ZERO417_*_1000` lines. Bucket endpoints vary by a few decisions; the exact final totals are from the final Android assertion, not sums of rounded bucket ratios. No synthetic or scripted decisions.

## V452 red-contact score decomposition: causal interpretation
Parsed all **50** original wild `ZERO404_HAZARD: REAL_MATERIAL_CAUSALITY sign=HARM` traces, with per-candidate V452 fields. At real red contact:
- 14/50 chosen actor candidates were >0.10 lower in *raw* learned optical utility than the best offered raw-optical candidate; 12/14 of those were cognition candidate zero, with **zero** `chosenOptPref` and **zero** `chosenMotor` in V353's actual scoring. These are not necessarily safe alternatives physically: under measurable drift, unvalidated V377 action→travel cannot be treated as reliable.
- Across all 50 red contacts, **38/50** have zero selected `chosenOptPref`, **48/50** have zero selected `chosenMotor`, and **38/50** retain candidate zero. In **45/50** real PRE contact momentum was already toward the red object. A positive command-to-safety cannot by itself cancel physical inertia.
- 6/14 of the >0.10 raw-optical gaps had the *optically best candidate's* total V353 score above the chosen candidate's total, but this is **not** equivalent to the actual best-total candidate beating the V353 safety margin. There are two cases of executive recall, so the final candidate need not be V353's chosen one.
- V452's `bestTotalIndex` is the index of the **highest total-score candidate**, whereas its `bestTotal` field is the score of the **best raw-optical candidate**. This naming ambiguity is a genuine instrumentation defect: treating `bestTotal` as the global maximum would falsely diagnose the V353 safety margin. The actual margin and prediction-trust gate were not logged.
- V353 source: `chosen = bestScore > original + (canPredict ? max(margin,costSafety) : margin) ? best : 0`. `margin` is existing 0.005 if learned visible contact evidence is reliable, otherwise 0.025. V371/V377 `opticalPreferenceV374` returns zero for unsupported drift under the already enabled strict V433 evidence mode; changing it to assume commanded travel would fabricate a physical prediction. The V452 data therefore establish a **decision-evidence bottleneck**, not proof of an erroneous actor direction or permission to force raw optical maximum.

## Single isolated V453 change
**Telemetry correction only**, in `Zero3CognitionV353`:
- Keep the legacy V452 fields but append unambiguous `opticalCandidateTotal`, `bestScoredTotal`, `actorOriginalTotal`, `bestScoreGain`, `requiredGain`, `marginBlocked`, `canPredict`, `materialEvidence`, `nonzeroOptPrefCandidates`, `nonzeroMotorCandidates`.
- All numbers are read from values **already calculated by the original selector**; no additional model prediction or candidate sampling. The existing V353 score, margin, choice and reward, GRU3 learning, motor outputs, V319 physics, sensor schema and saved checkpoint remain exactly unchanged.
- Purpose: distinguish a score-ranked but physically untrusted alternative from a candidate actually blocked by the preexisting safety margin, and avoid promoting a risky optical-only steering tweak.
- This is not a behavioral correction: no causal intervention on the muscles or policy is justified until the next original Android run establishes which actual gate dominates.

## Required V453 validation
Run four original Android workflows on the **same exact commit**: S21 >=10k, wild >=10k, full regressions, exact saved S21 CRC. Read every 1k bucket and all real red-contact score fields. If V453 behavior diverges from V452 materially, reject instrumentation as non-neutral. Do not release unless S21 >=25%, >=9/10 good buckets, zero inverse, >=5 new blue; wild >=10k real decisions, red last 5k < first 5k and red <=2× blue; regressions and CRC PASS. No V2.2, hard-coded heading or synthetic reward.

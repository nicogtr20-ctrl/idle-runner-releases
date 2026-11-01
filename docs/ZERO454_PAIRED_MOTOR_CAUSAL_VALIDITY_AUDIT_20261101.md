# ZERO454 — independent scientific validity audit (no gameplay change)
Date: 2026-11-01. Documentation-only audit branch `audit/zero454-paired-motor-validity-20261101`, based on `lab/zero454-motor-response-audit` commit `5597869434adef52c9b9f85fb648c98da99e4b59`. **NO RELEASE, APK, OTA, MERGE, or approval.**

## Sources actually checked
Read root AGENTS.md, docs/AGENTS.md, ZERO419–ZERO423 and canonical ZERO30 master/implementation/UI contracts before this audit. Inspected `Zero454PairedMotorContrast`, `Zero3LearningFoundation.AdaptiveCortex/Coordinator`, `Zero3OriginalBrainV356`, V353 scoring, original S21/wild Android test sources and all four V454 workflow definitions. Independently fetched **all 12** historical V421/V422/V423 GitHub Actions job statuses **and the six behavioral and six regression/CRC full job logs**. The historical results are not approvals:

| Lab | S21 original signed/gross | S21 >=10% windows | S21 opposite windows | S21 new blue | Wild signed/gross | Wild red first/last 5k | Wild red/blue | Regressions / CRC |
|---|---:|---:|---:|---:|---:|---|---|---|
| V421 | 17.1723% | 8/10 | 2 | 4 | 61.6262% | 35/52 | 87/3 | PASS / PASS |
| V422 | 11.2881% | 7/10 | 2 | 4 | 42.7053% | 19/33 | 52/5 | PASS / PASS |
| V423 | 10.0486% | 7/10 | 2 | 4 | 31.0766% | 14/12 | 26/4 | PASS / PASS |

Run IDs S21/wild/regression/CRC: V421 `41149391000/41149393560/41149404217/41149399737`; V422 `41149758090/41149762948/41149771615/41149767313`; V423 `41149983685/41149987734/41149995431/41149992279`. Each behavioral job completed FAILURE; the six source regressions/checkpoint jobs completed SUCCESS. The logs confirm actual original autonomous decision windows, real contacts, V356 CRC Android `OK (1 test)` and full Android regressions. Do not confuse inherited S21 90 red contacts with new S21 harm.

Most recent **confirmed** exact-commit original V452 evidence is in `docs/ZERO453_V452_COMPLETE_10K_V353_MARGIN_VS_PHYSICAL_EVIDENCE_FORENSICS_20261101.md`: S21 10,002 / 18.0209%, 8/10 good, one inverse, four new blue; wild 10,006 / 33.7023%, 50 red, 5 blue, 37/13 red first/last five buckets. Regression and original CRC PASS. Both behavioral gates FAIL. V453 new Android outcome cannot be inferred from the V452 report.

## Source-level V454 hypothesis and genuine measurement
The original V417 local credit used whole-decision PRE→POST COM acceleration and attributed it to a single local motor perturbation; independent V415 motor experiments reported 92/177 wrong-sign examples. V454 adds a **real physical** ON/OFF/OFF/ON four-frame contrast on the same original V390 probe, measuring COM velocity increments after each solver step. `Zero454PairedMotorContrast` computes `0.5*(a-b-c+d)` for matched four-frame increments and rejects a quad if actual touch, external correction or teleport occurs. The instrument's mean is fed into V332 local credit only when at least two quads are valid, with original V401/V414 eligibility still required. The original body solver, V319 CH2, 194D GRU3, 512 cortex weights, V356 checkpoint format, 12 slots, physical harm/energy and all red/blue contacts are not replaced. No scripted heading or invented reward appears in this intervention.

## Important interpretation limits before any behavioral claim
1. **Not an exact counterfactual.** The ON pulse changes body state and the subsequent OFF frame inherits membrane inertia, slow local cortical state and contact history. The ABBA pattern cancels only an approximately linear drift. A measured positive ABBA value is evidence of physical probe association, **not** a mathematical proof that a different complete heading would avoid a red gem.
2. **Multiple active research flags.** The V454 test harness enables V421 delivered local probe and V423 immediate own-decision credit before enabling V454 paired contrast. Those are different experimental switches. Comparing V454 directly to V453/V452 cannot isolate the paired contrast's effect. A matched V421+V423 control with V454 OFF is required before attributing any gain to V454 alone.
3. **Duty cycle changes the intervention.** The V454 physical probe is actually applied on two of four substeps rather than continuously; this changes motor actuation. `deliveredProbeV397` is accumulated only when ON but divided by the count of all probing frames, so the effective V332 perturbation scale also differs. Any result must be described as a combined changed physical probe schedule and paired estimator, not a pure telemetry improvement.
4. **Physical validity gates remain necessary.** A valid quad excludes observed surface touch, correction and teleport, but the full decision's genuine harm, weather, front-wall and terminal flags still must exclude false learning. Check `validPairedCreditDecisionsV454`, `rejectedPairedCreditDecisionsV454`, original V401/V414 counters and real red contacts together.
5. **Workflow results not established by source.** Four V454 Android workflows are configured for the isolated branch and source has V454 opt-in in both original 10k tests, plus a physical-contrast regression. Source inspection alone cannot confirm run IDs, actual execution, same SHA or PASS. Do not report these as completed or approved without fetching the corresponding Actions jobs/logs.

## Required next validation (no threshold relaxation)
- First retrieve actual V453 S21/wild/regression/CRC run IDs and logs if available; verify the telemetry correction is behavior-neutral and examine `bestScoreGain|requiredGain|marginBlocked|canPredict|materialEvidence` for all 50 red contacts.
- Check the **actual V454** four Android Actions on identical SHA; do not alter its branch while tests may be running. Parse all ten 1000-decision windows per environment, S21 static/opposed/blue, wild red per window and first/last 5000, signed/gross, local V332 gradients, V377 heldout evidence, sensory GRU3/retina and V319 physical steps.
- If V454 fails or is inconclusive, do not add another policy tweak. Isolate exactly one cause with a matched original-body control; retain the protected user S21 original 33,312 decisions, 512 weights, V356 CRC, V319 CH2, 194 senses, original 170-material solver and all 12 saved slots.
- Hard acceptance: original S21 >=10,000 decisions, >=25% signed/gross, >=9/10 windows >=10%, **zero** inverse windows, >=5 new blues; wild >=10,000 original decisions, late 5000 red < early 5000, red <=2*blue; regressions AND CRC PASS. Until then **NO APK/OTA/stable merge**.

This audit modifies only this Markdown document on its own branch; it does not interrupt or modify the running V454 experimental branch.


## Verified actual V453 and V454 original Android evidence — update 2026-11-01
These are **real completed** GitHub Actions job logs, not merely workflow definitions. V453 exact source `e5f204713cc8d22039b7e3163709f11c026d15ef`: [S21 41312000716](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41312000716) FAIL, [wild 41312000652](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41312000652) FAIL, [regressions 41312000708](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41312000708) PASS, [CRC 41312000667](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41312000667) PASS. V454 exact source `e12b1160d71ae342555b3a659953c9cad152012d`: [S21 41316640363](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41316640363) FAIL, [wild 41316640365](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41316640365) FAIL, [regressions 41316640379](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41316640379) PASS, [CRC 41316640390](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41316640390) PASS.

| Original 10k metric | V453 | V454 ABBA ON |
|---|---:|---:|
| S21 additional decisions | 10,002 | 10,001 |
| S21 signed/gross | 570.09987 / 3163.54081 = **18.0209%** | 233.90274 / 2754.14043 = **8.4928%** |
| S21 windows >=10% | 8/10 | **6/10** |
| S21 inverse windows | 1 (4) | **3 (4,7,9)** |
| S21 actual new blues | 4 | 4 |
| S21 local synaptic updates | 3,084 | **1,079** |
| Wild original decisions | 10,005 | 10,003 |
| Wild signed/gross | 1609.67424 / 4774.47470 = **33.7142%** | 59.95076 / 3724.88821 = **1.6095%** |
| Wild red first 5k / last 5k | 37 / 13 | **39 / 19** |
| Wild total red / blue | 50 / 5 (10×) | **58 / 4 (14.5×)** |
| Wild local synaptic updates | 5,441 | 5,904 |
| Wild V377 verified motor banks | 1 | **0** |
| Wild V371 motor score predictions / evaluations | 16,004 / 408,448 | **72 / 416,795** |
| Regressions / exact S21 V356 CRC | PASS / PASS | PASS / PASS |
| Behavioral approval | FAIL / FAIL | **FAIL / FAIL** |

### All genuine 1000-decision windows (percent signed/gross, with actual wild red/blue)
| Window | V453 S21 % | V454 S21 % | V453 wild % | V454 wild % | V454 wild red | V454 wild blue |
|---|---:|---:|---:|---:|---:|---:|
| 1 | 26.3334 | 15.8067 | 13.9826 | 15.3822 | 0 | 3 |
| 2 | 14.0010 | 3.8265 | 28.7190 | 9.5359 | 7 | 1 |
| 3 | 30.3145 | 32.4445 | 21.4100 | -5.0501 | 11 | 0 |
| 4 | -3.3456 | -27.6357 | 36.2624 | -8.1023 | 11 | 0 |
| 5 | 28.7760 | 43.8569 | 30.3265 | -5.6621 | 10 | 0 |
| 6 | 23.7534 | 28.9630 | 36.4147 | -0.2888 | 9 | 0 |
| 7 | 16.1861 | -0.8135 | 41.3263 | -4.5292 | 10 | 0 |
| 8 | 31.7120 | 39.2471 | 41.7531 | 2.3493 | 0 | 0 |
| 9 | 3.6694 | -20.0688 | 40.6212 | 10.9750 | 0 | 0 |
| 10 | 25.2321 | 15.5249 | 39.4772 | 3.2078 | 0 | 0 |

V454 S21 real physical static movement per window: `545,312,927,678,618,868,419,754,624,632`; opposed: `54,239,2,213,19,1,210,1,216,15`. Its original V319/170-material solver continues, but three windows move in the inverse direction. Wild physical RED logs contain 58 genuine `sign=HARM` events: **56/58** already have momentum toward the object, **23/58** issue intention >0.3 toward it, **58/58** report `learnedVisible=true` (which only means some visible learned wavelength, not proven correct classification of the specific red). S21's cumulative 90 reds are inherited, not 90 new contacts.

V454 physical credit counters from genuine logs: S21 `pairedMotorContrastLabV454=true, validPairedCreditDecisionsV454=1079, rejectedPairedCreditDecisionsV454=8640`; wild `pairedMotorContrastLabV454=true, validPairedCreditDecisionsV454=5907, rejectedPairedCreditDecisionsV454=2511`. Wild `v444AcceptedTrain=5941, v444AcceptedHeldout=1498` but `v444ReliableBanks=0`, `verifiedV371Directions=0`, `unsupportedDrift=375212`. This supports a genuine evidence/control bottleneck. The ABBA arm is markedly worse than V453, but because multiple flags and the motor duty cycle differ, the comparison alone cannot prove the ABBA estimator is the sole cause.

### Isolated matched control launched, not yet approved
New independent experimental branch `lab/zero455-v454-matched-off-control`, exact CI source commit [e103225daf0320f88f3849a1de6c61c241402c6b](https://github.com/nicogtr20-ctrl/idle-runner-releases/commit/e103225daf0320f88f3849a1de6c61c241402c6b). It copies the same V454 source, tests, 194D GRU3, original physical solver, and all other research opt-ins, but removes **only the two Android original 10k test calls** that enable V454 ABBA. Four workflows were triggered on that same SHA: [S21 41320023593](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41320023593), [wild 41320023629](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41320023629), [regressions 41320023591](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41320023591), [CRC 41320023578](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41320023578). At first check S21/wild/regression PENDING and CRC IN_PROGRESS; **no V455 result is claimed**. This control isolates the combined V454 pulse-schedule+credit-target effect, not the estimator independent of duty cycle.

**Disposition:** V454 rejected for behavioral approval; V455 control pending. No APK, OTA, merge, hardcoded motor heading, fake reward or changes to the original 33,312 S21 brain, V356 CRC, 512 synapses, V319 CH2, 194 sensors or 12 slots.

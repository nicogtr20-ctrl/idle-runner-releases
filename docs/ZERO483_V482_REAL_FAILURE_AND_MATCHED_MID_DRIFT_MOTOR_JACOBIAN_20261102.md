# ZERO483 — V482 failure and original S21 mid-drift matched muscle-causality audit
Date: 2026-11-02. Branch `lab/zero483-original-s21-mid-drift-matched-motor-jacobian`, parent `80cf5ab955ef77a47b7fe368e3c9d816c2acb590`. **LAB DIAGNOSTIC ONLY; no locomotion fix or release claim.**

## Mandatory context read
Read repository `AGENTS.md`, `docs/AGENTS.md`, canonical ZERO30 blueprint/contract/UI, ZERO419–423, ZERO454/455/457 and [issue #123](https://github.com/nicogtr20-ctrl/idle-runner-releases/issues/123). Original GRU3 194 inputs, 64 units, 512 cortex weights, V356 CRC, V319 CH2, 170-material body, original S21 33,312 decisions, 12 saved profiles remain unchanged. Never enable V2.2, scripted directions, inferred red labels or fabricated rewards.

## V482 complete same-SHA result (confirmed from full GitHub job logs)
SHA `80cf5ab955ef77a47b7fe368e3c9d816c2acb590`. [S21 41507815325](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41507815325) FAIL: 10,003 new original decisions, signed 280.84656, gross 2757.31573, **10.1855%**, 6/10 windows >=10%, inverse 4 and 9, 4 actual new blue. S21 1000-decision signed/gross ratios: 19.679%, 1.320%, 17.805%, -19.789%, 39.640%, 18.634%, 4.833%, 26.486%, -16.765%, 43.066%. S21 red=90 inherited from the saved brain, NOT newly hit red.
[Wild 41507815306](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41507815306) FAIL: 10,002 genuine decisions, signed 580.17609, gross 3862.06523, **15.0224%**, 4 blue, **51 red**, first 5k **19**, last 5k **32**. Wild red per 1k: 0,0,0,9,10,10,10,0,12,0; blue: 3,0,0,0,0,0,0,1,0,0; signed/gross percentages: 3.299,-4.394,5.258,8.342,11.055,20.236,30.059,22.207,27.908,18.446.
[Regressions 41507815248](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41507815248) PASS; [saved S21 512/V356 CRC 41507815348](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41507815348) PASS. All four exact same SHA. V482 is **REJECTED**.

## Confirmed motor evidence starvation and limitations
Of 51 real wild red contacts, 47 had PRE momentum toward danger, 39 had zero nonzero physical `opticalPreferenceV374` among offered candidates, 33 had a better *raw* optical alternative, 20 still had margin block. These are correlated observations, not evidence an alternate heading was physically executable.
Wild motor telemetry: 415,446 V371 score evaluations, 320,040 unsupported PRE-drift, 14,631 V377 predicted; V444 **1 reliable heldout motor bank** (distinct from `verifiedV377Banks=4` in another counter), V371 **0 verified directions**; V476 intermediate banks 0 reliable. V390/V421/V423 local cortex still updated 5,396 times, but its V417 training target is total POST-minus-PRE COM acceleration, not the isolated sampled muscle effect. V454 ABBA was **OFF** in V482 (`pairedMotorContrastLabV454=false`); therefore `validPairedCreditDecisionsV454=0` in V482 is not evidence that no paired physical quads can occur. Earlier V454 ON vs matched V455 OFF was behaviorally unfavorable to ON; do not repeat ON/OFF duty-cycle intervention as an unproven fix.

## Causal question and one isolated experiment
Original `Coordinator.observeContactV390` rejects a V454 quad if `correction||teleport||any touchSense>.23`. The original free-floor friction in `SymbioteMatterV1.step` is implicit material drag; ordinary floor drag is NOT automatically `touchSense` or a world correction. This must be tested with actual material substeps, not assumed to be the reason for missing motor evidence.

New **diagnostic-only** `Zero483OriginalMidDriftCausalMotorJacobianAndroidTest` reuses the existing ZERO413 exact-user restored-body counterfactual apparatus. Three genuine original S21 *free* material starting states with whole-body PRE WEST velocities 0.35, 1.0, 2.0 U/s; for each, run 16 sectors × original CH0/CH1/CH3 = 48 matched +/−0.40 delivered-output interventions, 24 physical substeps per arm, identical saved posture, cortex, ground law and PRE momentum. Report 144 physical signed COM velocity differentials and their sign disagreement with V417's **unpaired** POST-minus-PRE acceleration and with matched full-minus-passive control. No live GRU command or learned weight is altered. A separate true 8-substep original-body check tests whether normal free-floor drag produces touchSense contamination or allows two physically complete V454 quads. These are *measurement* tests; no artificial goal, red map, reward or steering.

**Interpretation:** a nonzero Jacobian establishes physical actuator authority in that local context, not general locomotion. Discordance with unpaired V417 shows a credit-sign identification risk, not an already validated safe learning rule. No change to V371/V377 scores or motor bank thresholds until genuine heldout causal evidence exists. The 10k gates remain unchanged.

## Original historical Actions independently checked
V421 S21 10,001 17.1723%, 8/10, 2 inverse, 4 blue; wild 10,002 87 red/3 blue, 35→52; regression/CRC PASS. V422 S21 10,003 11.2881%, 7/10, 2 inverse, 4 blue; wild 10,003 52 red/5 blue, 19→33; regression/CRC PASS. V423 S21 10,002 10.0486%, 7/10, 2 inverse, 4 blue; wild 10,003 26 red/4 blue, 14→12; regression/CRC PASS. All six original behavioral runs FAIL. The four historical jobs in each version use **different commit SHAs**, so their green technical runs are not same-SHA certification.

## Precommitted Android checks on one final commit
1. Exact S21 original restored 33,312 GRU3 / 170 body **>=10,000** real autonomous decisions, >=25% signed/gross, 9/10 >=10%, 0 inverse, >=5 new real blue.
2. Exact wild original **>=10,000** real decisions, >=12% signed/gross, red last 5k < first 5k, total red <=2×blue.
3. Full original Android regression PASS.
4. Exact original S21 512-weight/V356 CRC PASS, with the new *separate* read-only physical diagnostic instrumented after the CRC check.
No APK, OTA, production merge, profile mutation or behavioral claim if any gate fails. Expect the unchanged V482 live behavior to remain FAIL: this audit is to isolate the next single justified repair rather than invent one.

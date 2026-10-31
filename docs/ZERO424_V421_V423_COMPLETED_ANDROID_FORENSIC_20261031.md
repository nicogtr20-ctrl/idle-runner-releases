# ZERO424 forensic handoff — actual completed V421/V422/V423 Android runs
Date: 2026-10-31. Branch: `audit/zero424-v421-v423-10k-forensic-20261031`, forked from V423; **read-only evidence and no behavior/code edits**. Source `AGENTS.md`, `ZERO419`–`ZERO423` reviewed before conclusions. All user save slots, original S21 33,312-decision brain, 512 motor weights, V356 CRC, CH2 V319, 194D GRU3 and 170-material physics remain unchanged. No APK, OTA, merge or approval.

## Definitive GitHub Actions outcomes (actual Android, not hypothetical)

| Arm | S21 real 10k | Wild real 10k | Original regressions | Original saved S21 V356 CRC |
| --- | --- | --- | --- | --- |
| V421 delivered V332 perturbation | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) **FAIL** | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) **FAIL** | [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) **PASS** | [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) **PASS** |
| V422 learned-harm 7 candidates | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) **FAIL** | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) **FAIL** | [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) **PASS** | [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) **PASS** |
| V423 own-decision immediate V332 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) **FAIL** | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) **FAIL / boot-instrumentation censor** | [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) **PASS** | [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) **PASS** |

All six behavior jobs were marked completed/failure by GitHub; none remained "still running" in the initial check. Full redirected job logs were retrieved for each; the six successful technical jobs were also inspected. V423 wild failed at `Zero417WildOriginalVelocityCredit10000AndroidTest.java:114` **before any 10k trial**: `AssertionError: Original sole GRU3 must really finish boot`. It is **censored**, not a scored failure of red avoidance or evidence of brain loss. Its run checkout was commit `85cb7f2`; V423 S21 was `6721cc3`, and their only one-commit difference is a wild workflow name/branch trigger (no Java change). Rerun requested for wild job `124383966645`, new attempt job `124396959851` was **in_progress** at last check. Do not replace a 10k result with an assumption.

## Exact original trained S21 10,000+ decision results

All start with the genuine S21 original `baseline=33312`, true physical training room, no new red (historical `red=90` is inherited and **not** a new 10k injury). The minimum is 25% signed/gross, >=9 of 10 windows >=10%, **zero negative windows**, >=5 new blue.

| Version | Final cursor | Signed | Gross | Signed/gross | Windows >=10% | Negative windows | New blue | Weight updates | Decision |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | --- |
| V421 | 43313 | 574.05910 | 3342.93503 | **17.1723%** | 8/10 | 2 (4,9) | 4 | 1918 | FAIL |
| V422 | 43315 | 336.47877 | 2980.82377 | **11.2881%** | 7/10 | 2 (4,9) | 4 | 1851 | FAIL |
| V423 | 43314 | 281.30871 | 2799.49447 | **10.0486%** | 7/10 | 2 (4,9) | 4 | 2962 | FAIL |

S21 independent 1k signed/gross windows, in order:
- V421: `[.165218,.192875,.198798,-.152053,.439291,.267355,.126894,.293715,-.197862,.383081]`
- V422: `[.166078,.161528,.242661,-.117919,.300526,.148848,.087885,.253308,-.196037,.263205]`
- V423: `[.198432,.064892,.160520,-.410265,.265003,.156450,.101294,.248111,-.107413,.299148]`

The signed/gross and true measured contact logs rule out claiming a locomotion cure. In particular V423 immediate credit produces **2962** updates (vs V421 1918) but substantially worse signed travel and the severe fourth-window reversal (-41.0%); more gradient steps != better movement. V421's delivered perturbation improves the S21 aggregate over V417/V422 (~11.29%) but still has 2 reverse windows and misses blue count and 25% gate. This is an observed comparison, not proof that its isolated perturbation estimate is the root cause.

S21 windows contain very high original physical wall-contact frame counts (V421 windows 1..10: 24432,9322,24112,18224,14124,24288,9428,24336,17457,14310; V422: 24352,9256,24288,18213,20013,24512,8601,24400,17011,20029). V423 fourth inverse window: 17968 wall-contact frames, 241 opposed-movement decisions and 719 near-static decisions; sixth: 24144 wall-contact frames and 925 near-static decisions. These are **measured wall/tissue outcomes**, not a fabricated directional prescription. Multiple inverse windows persist despite altered motor-credit eligibility. The physical traction/motor response problem is not explained by gradient-count alone.

## Exact original newborn wild V421/V422 10k red safety

Wild has 3/5 genuine blue pickups respectively and no purple; RED is the genuine physical injury counter, not a synthetic label supplied to actor. Minimum 10k, >=12% signed/gross, red last 5k **strictly less** than first 5k, total red <=2× blue.

| Version | Decisions | Blue | Red | Red first 5k | Red last 5k | Red / blue | Signed | Gross | Efficiency | Updates | Decision |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | --- |
| V421 | 10002 | 3 | 87 | **35** | **52** | 29.0x | 2748.31782 | 4459.65664 | 61.6262% | 5843 | FAIL |
| V422 | 10003 | 5 | 52 | **19** | **33** | 10.4x | 1873.24584 | 4386.45121 | 42.7053% | 4587 | FAIL |
| V423 | 0 scored | — | — | — | — | — | — | — | — | — | BOOT CENSORED |

Wild per-1000 RED V421: `[0,10,6,9,10,10,10,13,9,10]`; V422: `[0,10,9,0,0,6,4,12,10,1]`. First/last are derived from **10 actual adjacent thousand-decision windows**, not comparing historical totals or HUD's wrapped 50-life list. Both wild trials travel forward efficiently yet **become more injurious**, so gait efficiency alone is an invalid proxy for hazard avoidance.

## Candidate/risk forensic, source and actual red-contact logs

The V422 original 52 `ZERO404_HAZARD sign=HARM` records contain **50/52** with `offered=7` and `learnedVisible=true`; **42/52** issue a heading with `intentionToward > .3` even though the red wavelength has been learned. Therefore V422's actual wider candidate intervention DID engage and nevertheless failed; "the brain never sees harm" or "there are always only 3 candidates" is false for this run. On several contacts `bestOptical` exceeds `chosenOptical`, but that is a read-only V369 *commanded-heading* score and not an independently validated physical trajectory. The observed `PRE_HARM_momentumToward` is frequently positive even when `intentionToward` is negative, consistent with ongoing inertia/traction. It is **not** sufficient evidence to replace the actor with a scripted escape vector.

Source inspection confirms V369 uses 3 spectral bands x 8 azimuth rays and `Zero3CognitionV353.select` combines V371 instrumental physical response, V374 learned-contact preference and trusted V353 predictors, with margin and planner/executive overrides. The 194D GRU3 has compressed tactile/obstacle/shape/velocity and prior 64 muscle efferences; 96-skin/74-gel detailed state is not all directly exposed to the GRU. V421 V332 delivered-noise repair and V423 same-decision gradient repair are isolated opt-in interventions; the real 170-material solver, V319 CH2 and saved original V356 remain intact. No evidence currently justifies changing 194D input shape or assigning any new target direction.

## Next exact action and controls

1. Wait for rerun of V423 wild `41149987734` (attempt 2); distinguish transient cold emulator startup from reproducible real original GRU3 boot failure. Never count a preboot failure as red-safety result.
2. Investigate **paired actual issued candidate vs realized displacement** in wild learned-red 7-candidate states, with original `V369` retina azimuth, V371/V377 heldout response, V354 planner/executive final selected action, real COM momentum, skin shear, and actual contact. Measure selected-vs-best offered optical utility and physically achieved response. Keep this read-only until causal attribution is demonstrated.
3. For S21, isolate the recurring 4th/9th inverse 1k blocks: same original body and wall/tactile geometry, active V390 probe, physical actuator clipping, CH0/1/3 gradients, V319 grip and COM/gel drift. Require an independently validated physical response before any further motor learning rule.
4. Only then propose **one** new experimental causal code correction with original Android regression, 512/V356 CRC and both genuine 10k trials, preserving the existing thresholds. No merge/APK/OTA if any hard gate fails.

**Decision: V421/V422/V423 NOT APPROVED.** This report does not authorize new persistent brain migration, V2.2 restoration, 12-slot overwrite or a release.

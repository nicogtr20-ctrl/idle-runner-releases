# ZERO469 — V468 completed failure; isolated learned-harm optical drift ablation

Date: 2026-11-02. Branch: `lab/zero469-learned-harm-optical-drift-ablation`.
**Research only. No APK, OTA, merge, production activation, edited user save or claim of competent locomotion.**

## Inputs and historical checks

Read `AGENTS.md` and original `docs/ZERO419...`, `ZERO420...`, `ZERO421...`, `ZERO422...`, `ZERO423...` first. Rechecked twelve historical GitHub Actions via API and six full behavioral job logs:
- V421 [S21](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL 17.1723%, 8/10 windows >=10%, two inverse 4/9, four blue; [wild](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL 87 red/3 blue, red 35 first 5k to 52 last. [Regression](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS; [CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS.
- V422 [S21](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL 11.2881%, 7/10, two inverse 4/9, four blue; [wild](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL 52 red/5 blue, red 19 to 33. [Regression](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS; [CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS.
- V423 [S21](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL 10.0486%, 7/10, two inverse, four blue; [wild](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL 26 red/4 blue, red 14 to 12. [Regression](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS; [CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS. Historical four workflows per version used DIFFERENT commit SHAs; their green technical jobs do not establish same-SHA approval.

## V468 actual same-SHA Android result: REJECTED

All four runs on `58c1f4c73f9ab2d533f770bc7db8f479395b0ee0`:
- [S21 41384834232](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41384834232), job 125083252261: **FAIL**; 43,317 cursor minus 33,312 original = **10,005 new real decisions**, 532.28783 signed / 3507.61237 gross = **15.1752%**; 7/10 windows >=10%; 2 inverse (4,9); 4 actual new blue; 4 real rebirths; 3412 local weight updates. The reset of 64 cross-life motor efferences does not fix locomotion.
- [Wild 41384834278](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41384834278), job 125083252185: **FAIL**; 10,002 real decisions, 907.21493 signed / 4499.52305 gross = **20.1625%**, **41 actual red / 3 actual blue**, red first 5k **20**, red last 5k **21**, ratio 13.67, 5402 local updates and 1756 harmful gradient rejects.
- [Original Android regressions 41384834231](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41384834231) **PASS** including V468 cross-life efference test.
- [Original 33,312 S21/512 V356 CRC 41384834266](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41384834266) **PASS**.

### V468 S21 original ten independent 1000-decision windows

|window|signed|gross|signed/gross|blue new|
|--:|--:|--:|--:|--:|
|1|58.83582|419.00461|14.0418%|0|
|2|57.90358|570.17591|10.1554%|1|
|3|124.54804|301.74347|41.2761%|0|
|4|-10.55297|379.27903|-2.7824%|1|
|5|112.01043|333.54828|33.5815%|0|
|6|55.30115|161.88253|34.1613%|0|
|7|90.99162|517.41268|17.5859%|1|
|8|47.05571|185.26823|25.3987%|0|
|9|-35.91776|279.54872|-12.8485%|1|
|10|32.17187|359.63311|8.9457%|0|

### V468 wild original ten independent 1000-decision windows

|window|new red|new blue|signed|gross|signed/gross|
|--:|--:|--:|--:|--:|--:|
|1|0|3|26.32869|391.52178|6.7247%|
|2|0|0|47.30377|374.21472|12.6408%|
|3|0|0|59.57464|399.82610|14.9001%|
|4|10|0|88.53496|454.90309|19.4624%|
|5|10|0|110.80014|498.58939|22.2227%|
|6|5|0|106.80088|445.51315|23.9726%|
|7|4|0|85.30624|424.77699|20.0826%|
|8|6|0|100.17589|502.15238|19.9493%|
|9|6|0|133.84038|465.61128|28.7451%|
|10|0|0|148.54934|542.41417|27.3867%|

## New causal evidence: learned harm cannot rank most candidate choices during drift

The V468 wild original `ZERO443_MOTOR_EVIDENCE` log reports **379,920** V371 candidate score evaluations, **288,873** unsupported PRE-drift evaluations and **288,873** optical-drift suppressions. Only 37,779 V377 predicted evaluations; **0 V371 verified directions** and **0 reliable V377 banks** by V444 strict independent test (the separate V377 summary's 3 banks are not V444 independently reliable). The original source `Zero3InstrumentalCreditV371.opticalPreferenceV374` explicitly returns **zero** under strict V433 when the body is already moving >=0.35 physical speed and no trusted action-to-velocity prediction exists. V353 `select` calculates raw learned V369 optical utility for telemetry, but ranks with the zeroed optical preference, so it can choose the first GRU candidate with every score exactly zero. Of 41 genuine red contacts in V468 logcat, **30** have `nonzeroOptPrefCandidates=0`, **33** have `nonzeroMotorCandidates=0`, and **18** have `intentionToward>0.3`. These are correlated real-contact traces, NOT proof that the actor can physically steer away from danger.

The design reason for V433 is valid: the intended heading is NOT necessarily actual body travel. Thus V469 is a **falsification experiment**, not an asserted fix. It asks whether an overly absolute optical suppression creates a second learned-harm selection deadlock, despite genuine observed contact valence. The risk is that the weak actor-heading hypothesis may be wrong under drift.

## Exactly ONE isolated behavioral difference from V468

Add transient `enableLearnedHarmDriftOpticalAblationLabV469` in original V371/V353/GRU3 chain, explicitly enabled by the original S21/wild Android tests only **after** strict V433 and original brain restoration. During unsupported measured drift **only if V369 has a CURRENTLY VISIBLE wavelength with genuinely learned negative physical contact value**, return the EXISTING `0.35 * contact.utility(observation, sampledActorHeading)` instead of unconditional zero. Otherwise retain strict V433 zero. This is a weak candidate ranking hypothesis, not a physical motor prediction or authored escape vector. No fixed RED class, reward, target direction, motor force, physics change, CH2 grip change, 194 input topology, 512 cortex weights, GRU3 weights, checkpoint bytes, saved 12 slots or V2.2 fallback.

New `Zero469LearnedHarmOpticalDriftAndroidTest` checks four contracts: original strict mode stays zero by default; only two actual negative contact observations with visible photons can enable weak candidate utility; positive-only or invisible evidence cannot; V433 prerequisite mandatory. It does not substitute for genuine 10k behavior.

## Required real Android proof, not yet approved

All four original GitHub Actions must finish on the **same final commit SHA**: S21 10k >=25% signed/gross, >=9/10 1000-decision windows >=10%, zero inverse, >=5 new blue; wild >=10k, >=12%, real red last 5k < first 5k and total red <=2× real blue; original Android regressions including V469 4 tests PASS; original 33,312/512 V356 CRC PASS. If ANY fails, V469 is rejected. Do not release, merge, install OTA/APK, reset user checkpoint or change thresholds. Source branch is an experiment only.


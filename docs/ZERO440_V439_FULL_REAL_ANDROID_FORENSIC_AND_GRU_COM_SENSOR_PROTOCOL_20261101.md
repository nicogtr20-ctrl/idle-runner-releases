# ZERO440 — V439 full original Android 2×10k forensic + single GRU COM-speed observation experiment
Date: 2026-11-01. Source branch `lab/zero440-gru-inertia-observation-gain` starts from exact V439 behavioral commit `d187b1e05b5b14ceb0954b8469e1c9db1661164e`. **RESEARCH ONLY — no APK, OTA, stable merge, saved-brain overwrite or change to 12 slots.**

## Required sources / history
Read `AGENTS.md`, `docs/AGENTS.md`, `ZERO419_WHOLE_ORIGINAL_GRU3_SENSORS_BRAIN_CAUSAL_AUDIT_20261031.md`, `ZERO420_REAL_V371_V377_RED_WALL_MOTOR_EVIDENCE_CAUSAL_REPAIR_20261031.md`, `ZERO421_REAL_DELIVERED_V332_MUSCLE_CAUSAL_GRADIENT_20261031.md`, `ZERO422_LEARNED_HARM_7_CANDIDATE_ACTOR_SAFETY_AB_20261031.md`, `ZERO423_IMMEDIATE_OWN_DECISION_MOTOR_CREDIT_10000_20261031.md`, MASTER/contract/UI specification. GitHub latest-attempt job metadata rechecked 2026-11-01: V421 S21 41149391000 FAIL, wild 41149393560 FAIL, regressions 41149404217 PASS, CRC 41149399737 PASS; V422 S21 41149758090 FAIL, wild 41149762948 FAIL, regressions 41149771615 PASS, CRC 41149767313 PASS; V423 S21 41149983685 FAIL, wild 41149987734 FAIL, regressions 41149995431 PASS, CRC 41149992279 PASS. Their full Android logs were inspected, not just job conclusions. V421 S21 17.1723%, 4 new blue, 2 inverse; wild 61.6262%, 87 red / 3 blue (35→52 red by halves). V422 S21 11.2881%, 4 blue, 2 inverse; wild 42.7053%, 52 red / 5 blue (19→33). V423 S21 10.0486%, 4 blue, 2 inverse; wild 31.0766%, 26 red / 4 blue (14→12 but ratio 6.5).

## V439 actual final Android evidence, same SHA d187b1e0
- [Original saved S21, run 41248172521, job 124676092129](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172521): **FAIL behavioral**, genuine final cursor **43313** from original **33312** = **10001** new completed decisions. Signed **450.16089**, gross **3042.04698**, efficiency **14.7980%**, **7/10** windows >=10%, **two inverse** (4,9), **4** new true blue, original historical red count 90 (NOT 90 new reds). 2945 real local weight updates, 7056 censored, 2167 rear wall accepted, 2082 front wall excluded, 438 undelivered rear probes rejected. Original owner true. No invented heading.
- S21 1000-decision windows: bucket | signed | gross | ratio | blue newly in window | wall contact frames:
  1 | 47.69031 | 245.03802 | 19.4624% | 0 | 24640
  2 | 18.47413 | 479.85661 | 3.8499% | 1 | 14454
  3 | 25.92176 | 116.86272 | 22.1814% | 0 | 24368
  4 | -46.67932 | 324.83760 | **-14.3700%** | 1 | 17454
  5 | 133.41155 | 319.46264 | 41.7612% | 0 | 20377
  6 | 58.58187 | 189.01633 | 30.9930% | 0 | 24368
  7 | 114.97965 | 542.93574 | 21.1774% | 1 | 6799
  8 | 32.42119 | 172.40246 | 18.8055% | 0 | 24256
  9 | -60.75658 | 308.71589 | **-19.6804%** | 1 | 17286
  10 | 126.11633 | 342.91897 | 36.7773% | 0 | 18784.
  Snapshot boundaries are approximate and trailing decision is included in final totals.
- [Original wild, run 41248172551, job 124676090819](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172551): **FAIL locomotion**, **10001** real decisions, signed **-314.26115**, gross **3206.02148**, efficiency **-9.8022%** (existing wild locomotion gate >=12%). **3 blue / 3 red**, red first 5000 **3**, last 5000 **0**; **red-count subgates PASS**, not sufficient for approval. All 3 blues occurred by decision 718, no more over remaining >9000. 5066 real local weight updates, 4935 censored, 1845 harm rejected, 6117 learned-harm 7-option expansions. Red physical contact decisions **1676, 3501, 3521**; all 3 had positive measured PRE velocity toward red; two issued directions away (intentionToward -0.9961 and -0.9340), one toward (+0.8033). Last red contact was in fourth 1000-decision bucket.
- Wild 1000-decision windows: bucket | signed | gross | ratio | red | blue:
  1 | -39.96830 | 375.79056 | -10.6358% | 0 | 3
  2 | 8.57144 | 340.01310 | 2.5209% | 1 | 0
  3 | -14.04279 | 314.05481 | -4.4714% | 0 | 0
  4 | -106.24411 | 306.93685 | -34.6143% | 2 | 0
  5 | -36.87942 | 291.81232 | -12.6381% | 0 | 0
  6 | -10.49767 | 326.64195 | -3.2138% | 0 | 0
  7 | 1.24991 | 310.16721 | 0.4030% | 0 | 0
  8 | -92.79258 | 288.43609 | -32.1709% | 0 | 0
  9 | 44.17888 | 313.69751 | 14.0833% | 0 | 0
  10 | -67.93061 | 338.30203 | -20.0799% | 0 | 0.
- [Original regressions 41248172444](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172444) **PASS** and [original 33312/512/V356 CRC 41248172737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172737) **PASS**. No proof of S21 actual handset performance; these are original-game Android emulator instrumentation, not physical Galaxy S21 tests.

## Interpretation: V439 versus V438, not approval
V438 S21 14.7136%, 7/10 windows, 1 inverse, 6 new blue; wild -6.6933%, 17 red / 3 blue (13→4 red). V439 S21 14.7980%, 7/10, 2 inverse, 4 blue; wild -9.8022%, 3 red / 3 blue (3→0). Thus V439 strongly reduced red contacts in THIS one wild trace but also worsened signed locomotion and lost S21 blue pickups; 3 red could partly reflect failure to explore, NOT necessarily learned avoidance. Neither version is approved. The 512-weight cortex V439 feature7 was scaled /110→/10 without altering physics, CH2 or 194 GRU inputs.

## Single V440 causal hypothesis, precommitted before implementation
Source audit confirms **two independent copies of real measured COM velocity** enter the 194D GRU: coarse `inputs[16],inputs[17]` from `Zero3AutonomousLoopV317.encode` use `bodyVel/120`; high-level `inputs[191],inputs[192]` use `bodyVel/130`. At real hazard-contact COM speeds ~0.14–1.88 units/s, each raw GRU velocity feature is only ~0.0012–0.0157 while photon and recurrent features can be O(0.1–1). V439 boosted ONLY the lower 512-weight cortex's COM feedback, not the high-level GRU that must choose a braking direction. This is a measured dynamic-range mismatch, **not proof of unique cause**.

V440 changes ONLY the **coarse original-game GRU COM velocity observation gain** in `Zero3OriginalBrainV356.sense`: after the existing 20D encoder, overwrite ONLY `inputs[16]` and `inputs[17]` with clipped actual PRE `frame.bodyVelX/10` and `frame.bodyVelY/10`. Do not change `inputs[191..192]`: V371/V377 explicitly multiply those by 130 to reconstruct physical velocity, so changing them would corrupt the motor predictor. No extra sensors or weights, no preset heading, no force/reward change, no physics change, no new actor, no V2.2 restore, no CH2 grip change, no checkpoint format/CRC or 12 slots touched. Only original V356 game sensory path is affected. All 194 GRU input slots and original trained synapses remain intact.

**Risk:** Original 33,312 S21 GRU weights learned the /120 coarse feature distribution. A 12× larger measured feature may worsen movement, change exploration or destabilize world-model learning. A/B is necessary. A physically improved readout is not proof of learned avoidance. Do not tune further after seeing this test.

## V440 exact acceptance and test protocol
On a single final source SHA, run four original Android Actions: trained original S21 >=10000 genuine consecutive decisions from exactly 33312; >=25% signed/gross, >=9/10 windows >=10%, zero inverse, >=5 new blue. Original wild >=10000 real consecutive decisions, >=12% signed/gross as pre-existing test, real blue pickups, red last 5000 strictly < first 5000 and total red <=2× blue. Original Android regressions PASS and exact V356 33312 S21 512-weight CRC PASS. Compare against V439 same-world original runs. If boot failure, rerun unchanged; if behavior fails, no release. The workflow source must include V440 branch and all four Actions must run on the SAME final commit. Record run IDs and full 1000-decision windows after completion.

## Physical diagnostics still missing
The current original Android logs expose PRE red velocity/heading, contact/blue counts, 1k signed/gross, cortical update counters and learned 7-option coverage, but not a per-decision paired record of 64 delivered muscle outputs, actual PRE/POST COM vector, skin force/drag and V377 bank/heldout trust. Those measurements are needed to separate passive coasting from muscular braking, especially if V440 fails. Avoid speculative new rewards, fixed escape directions or claims of solved locomotion.

**Release status: BLOCKED.** Preserve original 33,312/512 V356 CRC, 194 sensors, original body/CH2 V319, all 12 saved profiles. No APK, OTA, stable merge.

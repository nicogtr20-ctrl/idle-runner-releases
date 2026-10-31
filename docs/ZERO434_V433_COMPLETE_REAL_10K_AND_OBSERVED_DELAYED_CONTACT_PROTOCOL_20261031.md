# ZERO434 — V433 completed original 10k forensics; delayed real-contact bank evidence (LAB ONLY)

Date: 2026-10-31. Branch: `lab/zero434-observed-delayed-contact-without-motor-map`. Base EXACT V433 source commit `c6706b65b431685211b09765d9c6b621d23f0349`. **Not approved; no APK, OTA, merge, saved-profile write, checkpoint migration or original body change.**

## Prerequisites and historical confirmation

Read root `AGENTS.md`, `docs/AGENTS.md`, `docs/ZERO419...` through `ZERO423...`, ZERO433 original protocol and the V433 S21 audit. Checked the 12 specified V421–V423 GitHub Actions conclusions directly:
- V421 S21 [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL (10,001, 17.1723%, 8/10, two inverse, four blue); wild [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL (87 red, 3 blue, red 35→52). Regression [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS, CRC [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS.
- V422 S21 [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL (10,003, 11.2881%, 7/10, two inverse, four blue); wild [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL (52 red, 5 blue, 19→33). Regression [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS, CRC [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS.
- V423 S21 [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL (10,002, 10.0486%, 7/10, two inverse, four blue); wild [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL on attempt 2 (26 red, 4 blue, 14→12). Regression [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS, CRC [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS.

## V433 definitive original Android forensics (both 10k now completed)

V433 same source SHA `c6706b65`. Original [S21 41201035384](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035384): **10,004 genuine new decisions**, saved 33,312→43,316, signed 209.72467, gross 3321.19637, **6.3147%** efficiency, **6/10** windows >=10%, **3 inverse** windows (4,6,10), **6 new blue**; FAIL. S21 1k signed/gross percent: [20.0132,16.8623,27.1929,-14.1630,13.2868,-9.8379,21.5430,4.3324,22.1776,-17.1958]. Real lower cortex 3,314 updates; 6,690 censored; 1,812 rear-wall accepted; 1,709 front-wall excluded. S21 red=90 is inherited historic state, not 90 new reds; S21 is blue-only. V432 S21 comparator 19.2907%, 7/10, two inverse, four blue.

V433 [wild 41201035346](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035346): attempt 1 failed BEFORE brain startup (no valid decisions); **attempt 2 job 124545802672 completed 2026-10-31 13:35:37Z with 10,001 real original decisions, and BEHAVIORAL FAIL**, not an emulator boot failure. Signed **-20.82683**, gross **3678.97690**, signed/gross **-0.5661%** (strict >=12% FAIL). **3 true blue, 38 true red**; red first 5000 **18**, last 5000 **20** (FAIL); red/blue **12.67×** (FAIL vs <=2×). Ten 1k windows:

| 1k window | blue new | red new | signed/gross % |
|---|---:|---:|---:|
| 1 | 3 | 0 | -13.5758 |
| 2 | 0 | 0 | 2.7187 |
| 3 | 0 | 0 | -1.4204 |
| 4 | 0 | 9 | -9.4836 |
| 5 | 0 | 9 | -29.2251 |
| 6 | 0 | 0 | 9.0845 |
| 7 | 0 | 1 | 3.6683 |
| 8 | 0 | 0 | 21.3633 |
| 9 | 0 | 10 | 2.2900 |
| 10 | 0 | 9 | 8.1470 |

The 38 actual harm log records: **25/38** PRE velocity toward hazard >0.3; **14/38** issued heading toward >0.3; **15/38** issued heading away <-0.3; **33/38** contacts with 3 candidate options, 5/38 with 7; **9/38** selected candidate optical score below best offered; 17/38 center-based optical-at-touch telemetry >0.1 (other zero readings are NOT proof of invisibility at protruding retina). Genuine physical harm -7 per event. Actual cortex 5,004 updates, 1,781 harm-censored, 390 rear-wall credits, 728 front-wall excluded, 69 undelivered rear probes rejected; V420 upper motor map 2,222 harmful samples excluded. This confirms BOTH missing physical braking and inadequate selection/credit, not one proven unique cause.

V433 [regression 41201035438](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035438) PASS and [original S21 CRC 41201035418](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41201035418) PASS. V433 is **NOT APPROVED**. The 0 optical preference in moving/unverified V433 was a source-correct censor of imaginary commanded travel, but lost another class of real historical evidence.

## Source-level discrepancy and ONE V434 experimental behavior change

`Zero3InstrumentalCreditV371.linkTrueContactToPriorMotor` already stores `delayedContactCount[b][bank]` and signed `delayedContactValue[b][bank]` only after a **physically clean, actually moving approach with PRE visible wavelength** is followed by an actual later measured material energy contact. V369 independently learns the contacted wavelength's signed value from real contacts. Yet `score()` returned zero unconditionally when V377 was unverified or the body was moving, throwing away those observed prior bank/contact associations. V433 also correctly censored the unverified commanded-heading optical travel in `opticalPreferenceV374`, so for many moving decisions there was no discriminating real-contact evidence left among actor-generated options.

**Single isolated V434 change:** while V433's no-phantom-heading-travel mode is active, allow `score()` to use **only the already-existing true delayed bank/contact association** (>=2 linked contacts, current photons visible >.08, V369 actually learned sign). It is a bounded, observational *score*, not a verified motion prediction, synthetic energy, new reward, action vector, brake force, forced avoidance or object identity. It does not modify GRU actor sampling, muscle dynamics, physics, red damping, V319 CH2, 194 sensors, 512 synapses, V356 CRC, 12 save slots, original S21 checkpoint or existing V433 optical censor. No V2.2 fallback.

**Causal limitation:** prior action-bank associations may be confounded by inertia; even genuine earlier clean motion does not prove a new command can now brake. V434 is an explicitly falsifiable laboratory test, NOT a claim of safe red avoidance.

## Android verification protocol, pending

- New `Zero434ObservedDelayedContactAndroidTest`: 5 Android invariants: no invented value without links; negative true measured contact only affects associated bank; positive true measured contact same generic rule; heldout/unlinked cannot fabricate association; V433 comparator required and no motor map forged.
- Existing original Android regressions + new test, exact user 33,312 S21/512-weight CRC, actual original S21 >=10,000 and actual original wild >=10,000. All four workflows on `lab/zero434-observed-delayed-contact-without-motor-map`, unchanged hard gates. S21 >=25%, >=9/10 1k >=10%, zero inverse, >=5 blue; wild >=12%, red last 5000 < first 5000, red <=2x blue. No APK/OTA/merge unless **ALL** pass. Record completed run IDs and full 1k/blue/red/signed/gross diagnostics in a follow-up section after Actions complete.

## ADR

Decision: test existing *observed* delayed-contact evidence in the same GRU3 scorer rather than restore V433's physically unsupported desired-heading-to-travel inference. Alternative: revert V433 (known V432 52 red/3 blue, S21 19.29%, still FAIL); rejected as no new causal test. Alternative: force opposite heading or increase scripted braking; prohibited and not causally grounded. Risk: false correlations with passive inertia, underexploration, negative S21 movement. Mitigation: ephemeral LAB switch, isolated branch, same exact 2x10k Android gates, full CRC/regressions, no publication or persistence changes.

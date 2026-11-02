# ZERO483 completed — original S21 matched mid-drift actuator causality, 2×10k behavior and integrity

Date 2026-11-02. Evidence-only audit branch `audit/zero483-complete-matched-motor-physical-sign-20261102` from tested experiment commit `71e7c0177658c4a2dad8a3d79283cb67fdd92ed3`. **NOT a locomotion fix, no release, no OTA/APK, no merge.** The tested `lab/zero483-original-s21-mid-drift-matched-motor-jacobian` code is diagnostic-only and leaves live selection/physics/learning unchanged.

## Four completed same-SHA GitHub Actions (read actual job logs, not merely job conclusions)

- [S21 original 41523669787](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41523669787), job 125545768776: **FAIL**, actual decision count 43,314 from 33,312 = **10,002 new**, signed **280.79178**, gross **2757.25055**, signed/gross **10.1838%**, 6/10 independent 1000-decision windows >=10%, inverse windows **4 and 9**, **4 new real blue**, local weight updates 2,669. Inherited `red=90` is **not** 90 new harmful contacts. Failed the nine-window assertion.
- [Wild original 41523669812](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41523669812), job 125545768624: **FAIL**, **10,005** actual decisions, signed **580.54926**, gross **3863.60473**, **15.0261%**, **51 red / 4 blue**, red **19** first 5k -> **32** last 5k, local weight updates **5,396**, harm-rejected **1,643**. Failed late-red-rate assertion and red<=2*blue.
- [Full Android regressions 41523669687](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41523669687), job 125545768397: **PASS**.
- [Saved S21 CRC + separate physical experiment 41523669644](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41523669644), job 125545768681: **PASS**; original saved 33,312/512/V356 CRC test **OK (1 test)** and `Zero483OriginalMidDriftCausalMotorJacobianAndroidTest` **OK (2 tests)**. All four workflows checked out exact `71e7c0177658c4a2dad8a3d79283cb67fdd92ed3`.

### S21 1000-decision windows (from the real log)
| Window | Signed | Gross | Efficiency % | New blue | Status |
|---|---:|---:|---:|---:|---|
| 1 | 46.54650 | 236.57784 | 19.6749 | 0 | >=10 |
| 2 | 6.39296 | 477.41056 | 1.3391 | 1 | FAIL |
| 3 | 19.02480 | 106.79933 | 17.8136 | 0 | >=10 |
| 4 | -59.65307 | 299.65974 | -19.9069 | 1 | INVERSE |
| 5 | 114.16552 | 288.25726 | 39.6054 | 0 | >=10 |
| 6 | 29.21000 | 157.30909 | 18.5685 | 0 | >=10 |
| 7 | 21.41039 | 437.97002 | 4.8886 | 1 | FAIL |
| 8 | 44.73198 | 169.15868 | 26.4438 | 0 | >=10 |
| 9 | -54.46653 | 320.54957 | -16.9916 | 1 | INVERSE |
| 10 | 113.42923 | 263.55846 | 43.0376 | 0 | >=10 |

### Wild 1000-decision windows
| Window | Signed | Gross | Efficiency % | New red | New blue |
|---|---:|---:|---:|---:|---:|
| 1 | 12.50780 | 379.11517 | 3.2992 | 0 | 3 |
| 2 | -12.50632 | 285.22772 | -4.3847 | 0 | 0 |
| 3 | 19.82904 | 373.09668 | 5.3147 | 0 | 0 |
| 4 | 32.48405 | 392.52746 | 8.2756 | 9 | 0 |
| 5 | 43.87679 | 398.68594 | 11.0054 | 10 | 0 |
| 6 | 83.57601 | 412.41991 | 20.2648 | 10 | 0 |
| 7 | 107.46731 | 358.08553 | 30.0116 | 10 | 0 |
| 8 | 85.20269 | 381.40133 | 22.3394 | 0 | 1 |
| 9 | 133.57737 | 479.32994 | 27.8675 | 12 | 0 |
| 10 | 74.17478 | 403.19998 | 18.3965 | 0 | 0 |

The wild red rates are 19/5000 = 0.0038 contacts/decision early and 32/5000 = 0.0064 late: late/early **1.684×**, opposite to required improvement. Total red/blue **12.75×**, versus <=2×. Compared with V482, both autonomous runs reproduce essentially the same failure; the small numerical changes do not demonstrate an improvement.

## Physical cause-of-credit diagnostic — 144 matched true-solver interventions

The `Zero483OriginalMidDriftCausalMotorJacobianAndroidTest` restores the user's original S21 33,312-trained cortex and the original 170-material free-body posture, sets whole-body initial westward velocities 0.35, 1.0 and 2.0 units/s, and performs 16 sectors × CH0/CH1/CH3 = 48 per speed. Each matched arm uses the same restored posture, ground law and 24 material substeps. The *test helper* `Zero413MatchedPhysicalMotorCausalityAndroidTest.run` applies +0.40 versus -0.40 **to the post-cortex physical command buffer**, with clipping. It is an experimentally delivered command perturbation, **not** the exact small live V390 sampled neural noise or a naturally GRU-selected action. No real checkpoint or live policy is updated.

| PRE west speed | Passive dVx | Full cortex dVx | Full minus passive | Nonzero physical +/- | +/- opposite sign to full unpaired dVx |
|---|---:|---:|---:|---:|---:|
| 0.35 | +0.352755 | +0.805139 | +0.452384 | 48/48 | 24/48 |
| 1.0 | +0.889348 | +1.344425 | +0.455077 | 48/48 | 24/48 |
| 2.0 | +1.696605 | +2.196533 | +0.499928 | 48/48 | 27/48 |
| **Total** | | | | **144/144** | **75/144 (52.08%)** |

Actual +/- physical Jacobian ranges: -1.514682..+1.039340 at 0.35; -1.558548..+1.081951 at 1.0; -1.567909..+1.037528 at 2.0. All 144 measured perturbations produce nonzero effects under the tested physical context, with opposite signs across different local muscles. Therefore a single **uniform global COM acceleration sign** cannot represent the true effect of every local muscle in this context. The observed unpaired full-action dVx is positive for all three speed settings, partly because passive slowing of westward drift itself produces positive dVx. The matched full-minus-passive control also stays positive, so subtracting only a *single global passive baseline* does not identify the sign of each local muscle.

**Critical limitation:** 75/144 is a disagreement between a global full-action velocity signal and +/-0.40 isolated actuator effects under a **fixed EAST** test intention; it is **not** a measured 52% wrong-sign rate for live V417 gradients, not a measured failure probability for learned GRU commands, and not sufficient by itself to replace V417's target. The actual live V417 signal is `body.cvx - decisionStartVxV417` projected on the chosen heading and copied into all 16 `localResponseV400` sectors; the low-level `learnV332` multiplies that by the selected sector's perturbation and features. This is a source-confirmed **causal attribution risk**, not a validated replacement learning rule. Previous V454 ON/OFF experiment did not pass original 2×10k behavior; never enable it blindly.

## Ordinary floor drag is NOT the V454 contamination culprit in the tested free-body state

The second Android test used original free-floor material traction and actual `Coordinator.observeContactV390` for eight substeps with V454 enabled. It logged **touchFrames=0** and **validPairedQuads=2**. Thus normal passive material floor drag does not automatically trigger `skin.touchSense > .23` or censor paired quads. V482's zero paired credits occurred with V454 **OFF**, not proof of impossible physical quads. Real wall, storm, damage, external corrections and true touches still require separate eligibility analysis; do not loosen contamination gates from this result.

## Source-level mechanism and next falsifiable experiment

`Zero3OriginalBrainV356.observed` lines ~715-722 uses the entire decision's POST-minus-PRE COM acceleration and fills all 16 local response slots with one scalar; `Zero3LearningFoundation.AdaptiveCortex.learnV332` updates only the probed CH0/1/3 output using that scalar, the delivered V421 perturbation when enabled, and local features. The actuator can produce sector-dependent opposite effects (144 matched contrasts), while V371/V377 has insufficient heldout-verified steering in wild (V482: 1 reliable bank, 0 V371 directions). This plausibly explains why learned optical harm cannot be translated into reliable differentiated physical action; it is **not yet a demonstrated full causal chain** from gradient to wild collisions.

Next experiment must match the **actual delivered V390/V421 probe amplitude, site, direction and physical PRE posture** to a no-probe or +/- counterpart with identical original body state, *without* substituting a generic full-action baseline. Evaluate sign and magnitude of true finite-difference motor effect on independent heldout material states (multiple postures, grip states, drift signs and speeds, free floor and safely classifiable real wall contacts), and compare with the actual V417 signed learning update at that exact decision. Only then test a **single** new lab-only motor-credit rule. The previous V454 ABBA on live decisions was behaviorally unfavorable and is not preapproved. Any subsequent change must pass original S21 10k >=25%, 9/10 >=10%, 0 inverse, >=5 blue; original wild 10k >=12%, late red < early red, red <=2×blue; Android regressions and original V356/512 CRC PASS on the same SHA.

## Historical cross-check and preservation

V421/V422/V423 six real behavioral 10k Actions are FAIL, their regressions and CRC Actions PASS, but each historical four-run group had differing commit SHAs and cannot be called same-SHA approval. All actual V483 runs use the same `71e7c017` SHA. Original S21 33,312-decision checkpoint, 512 motor weights, V356 CRC, V319 CH2, 194 GRU sensors, original 170-body, original game and twelve saved profiles preserved. No V2.2, fixed heading, invented red map/reward, APK, OTA or merge.

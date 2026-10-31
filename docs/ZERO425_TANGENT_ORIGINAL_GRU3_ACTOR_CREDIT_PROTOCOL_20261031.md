# ZERO425 — original GRU3 actor normalized-heading gradient, research only
2026-10-31. Experimental branch `lab/zero425-tangent-realized-actor-credit` from V424; source and protocol recorded **before** any original Android 10k outcome. NO merge, OTA, APK, stable release, original checkpoint edits or migration.

## Actual V424 completed result
See separate immutable [forensic audit](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/audit/zero425-v424-completed-original-10k-forensic-20261031/docs/ZERO425_V424_COMPLETED_ORIGINAL_ANDROID_10K_FORENSIC_20261031.md). V424 S21 original 10,003 additional decisions: 5.1164% signed/gross, only 6/10 windows >=10%, **4 inverse** windows, 4 new blue, 3,149 cortex updates. V424 wild original 10,002 decisions: 35.6375% efficiency, 4 blue, 25 red; first 5k **4 red**, last 5k **21 red**. Full Android regressions and exact original 33,312 S21 CRC PASS. Both behavior gates FAIL, V424 NOT approved.

## Single causal source discrepancy and experiment
In the original sole `Zero3RecurrentBrain.choose`, the two actor heading coordinates are each clipped to ±1 and then normalized to a unit direction `(sx,sy)/hypot(sx,sy)`. The actual original 170-material solver never receives raw heading magnitude. Yet `completeConsequence` uses full unprojected pre-normalization `lastNoise[0..1]` as the actor gradient, rewarding/penalizing **radial** noise that cannot change the issued unit direction, and noise on already clipped axes that cannot change the clipped output. This is a source-level false eligibility analogous to, but distinct from, V421 low-level muscle saturation. Does NOT prove it alone causes wild red collisions.

**Exactly one added learning variable vs V424:** Android opt-in `tangentActorCreditLabV425` projects the existing sampled actor perturbation through the local Jacobian of the *actual original clip and normalization*: `(I-uuT) diag(unsaturatedAxes) noise / |clip(proposal+noise)|`. Only heading actor gradients (2 of 5 outputs) change; real survival advantage/critic, GRU3 state, actor samples, original 3/5/7 candidate selection, real V369 valence, V319 CH2, 512 cortex weights and V424 delivered same-decision cortex credit stay unchanged. When unflagged, original behavior is identical. No authored direction, hazard rule, reward, physical force, topology, sensor or save format. At zero norm return zero derivative. This projection does **not** remove candidate-selection conditional-sampling bias, does not create a physical counterfactual, and may not improve either hard gate.

### New original Android physical-eligibility regression
`Zero425TangentActorCreditAndroidTest`: radial-only perturbation has zero normalized-heading gradient; tangential perturbation has correctly signed nonzero gradient; saturated-axis and zero vector cannot fabricate gradient. These are mathematical eligibility tests, not substitutes for 10k autonomous original Android runs. All existing original regression classes including V420/421/422 remain mandatory. A regression suite that omits V425 test is NOT PASS for this experiment.

## Fixed nonnegotiable acceptance
- Exact restored user S21 33,312 trained original brain, original room and 170-material body, 194D/64GRU3/512 cortex/V319, >=10,000 **new real** decisions, >=25% signed/gross, >=9/10 independent 1,000-decision windows >=10%, **zero inverse**, >=5 new real blue.
- Truly newborn original wild, >=10,000 self-generated decisions with true red and blue contacts, >=12% signed/gross, last 5k red < first 5k red, total red <=2× true blue.
- All original Android regression including new V425 3 cases PASS, exact original saved 33,312 S21/V356 CRC PASS. No synthetic world results substitute. The 12 user save slots, checkpoint and old OTA remain untouched. No APK/OTA/merge until **all four** real Actions PASS.
- If an Action is still running, preserve state and recheck; if a preboot failure, censor and retry; if genuine behavioral fail, record complete windows, red rates, physical metrics and do not deploy. No threshold retuning or repeated simultaneous changes.

## Risks
The actor perturbation is selected by cognition from several Gaussian candidates, so conditional policy-gradient bias remains. The physical material can shear independently of heading. The sensory retina is eye-originated while some forensic logs use center-origin azimuth. The 194D GRU omits much of full body geometry. Tangent correction may weaken an already poor actor. These are separate unresolved hypotheses, not grounds for changing more than one variable in V425.

## Required CI
Four existing original Android Actions, extended to this branch: S21 10k, wild 10k, full regressions including new V425, exact saved S21 CRC. Fill links and full results only after actual Actions are created and inspected.

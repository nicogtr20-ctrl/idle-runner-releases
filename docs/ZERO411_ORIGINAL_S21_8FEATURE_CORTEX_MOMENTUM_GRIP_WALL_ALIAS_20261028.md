# ZERO411 — The original V3 cortical 8-feature representation confounds critical physical states

**28 October 2026. LAB SOURCE ONLY.** True user V388 S21 fully trained original 33,312-decision GRU3 + 19,452 cortical weight updates, original 512 cortical weights/64 slow states, original 170-mass body.

## Source-level objective facts

`Zero3LearningFoundation.AdaptiveCortex.command` has exactly 8 local features × 64 muscle outputs, for 512 trainable weights, with CH2 grip owned by original V319:
- features[0]=bias
- features[1]=local radial tissue angle projected on current sole GRU3 intention
- features[2]=tangential tissue angle projected on intention
- features[3]=local radial distance
- features[4]=local *relative* radial velocity `skin.vx - body.cvx`
- features[5]=`(heldGroundGripPoints + tactileWallContactMagnitude)/6`
- features[6]=sole high-level intended effort
- features[7]=prior local motor output memory, with local *relative* tangential shear blended into CH1

**Problem A: global physical inertia alias.** Uniformly add +65, -65 or orthogonal 65 units/s to every original 170 physical mass (skin, body & inner liquid) and `body.cvx/cvy` while preserving their genuine relative geometry/velocities, high-level intention, effort and original cortical checkpoint. No one of the original eight local motor features changes, so the unchanged trained cortex must output the exact same 64 commands. Yet a living body drifting WEST versus EAST needs a different physical braking policy. This does **NOT** imply the higher GRU3 is blind: it does sense COM velocity in full 194-input channels and could select different intentions/effort. It DOES mean the low-level 512-weight motor circuit is not independently conditioned on whole-body PRE inertia, impairing generalization of learned stopping.

**Problem B: wall contact versus actually held substrate alias.** A sector with six fully adherent floor grip bits and zero touch is represented exactly like a six-point wall arrest with zero grip if each touchSense equals 1. Both yield feature[5]=1, although one is productive substrate support and the other is an obstacle. This can encourage repeating an unproductive motor pattern against the wall. Again, original GRU3 tactile/sensory pathways may be able to disambiguate at the executive layer; the problem is motor-local feature separability.

## Scientific proof

[Android original trained user's S21 2 independent invariance tests](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero411-s21-inertial-motor-input-observability-android.yml):
1. EXACT restored trained original neural weights, same genuine S21 96+74 material posture, four original GRU3 possible voluntary intentions, controlled ±65/orthogonal uniform physical momentum intervention. Require all 64 muscle outputs identical within 1e-6 across momentum shifts.
2. Same actual S21 membrane contour; replace ONLY one six-node sector's traction/tactile readings with equal feature[5] from true grip support versus contact. Require all 64 original trained muscle outputs identical within 1e-6 across this physically distinct local state.

This is **observability/representational diagnosis** using controlled motor-free interventions, not >=10,000-decision locomotion success. Do NOT confuse static one-command invariance with adaptive learning improvement. Such a finding would call for a backward-compatible velocity/touch-grip sensory expansion with zero-initialized additional synapses and verified original 12-save migration, not a magic forced motor direction. A live behavioral candidate remains unapproved until **>=10,000 natural original GRU3 decisions** in both original wild hazard environment and real user's S21 wall/blue room, genuine red hazard exposure, repeated heldout physical contexts, improved steering/braking, and no rollback in object manipulation.

## Actual completed Android results

[Run 40780226267](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40780226267), source `9afc960018eb1b5a5a1b0ce09ac79ce6389782dd`: **2/2 PASS**, true owner-uploaded V388 33312 original neural, exact same S21 material posture, complete 512 neural weights, same 64 recurrent motor states and real original muscle commands. For the uniform velocity test every original executed muscle power remained nonzero (sum ~4), while largest of all 64 command differences was:
- EAST intention: **7.45e−9**
- SOUTH intention: **1.49e−8**
- WEST intention: **1.49e−8**
- NORTH intention: **2.98e−8**

True material patch substrate hold vs collision touch also produced **identical all 64 cortical outputs on all 4 intentions**. Both are reproducible representational *aliasing*. The margin is effectively floating-point rounding, not a measurable corrective response.

**This is a meaningful design flaw independent of statistical sample size, but not a reason to publish a hurried fixed controller.** Existing V3 high-level GRU is not blind to body velocity; low-level motor lacks it as an independent feature. To fix robustly, consider a *new separately trainable extra sensor branch* initialized to zero so all original 512 trained weights and behavior are preserved initially, plus original V356 checkpoint migration and sustained 10,000-decision true S21/wild test. Reusing feature[5] or feature[7] by overriding semantics would immediately scramble user 33k trained motor weights and is forbidden without measured A/B.

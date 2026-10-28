# ZERO401 — original GRU3 3.0: physical motor credit serious failure audit (28 October 2026)

**Branch:** `fix/zero401-motor-credit-harm-turn-contamination`. User-installed V400 preview and original 12 named saves UNCHANGED. The original V400 source exists as `lab/zero400-original-game-local-delay-motor-credit`, and a separately signed manual V400 installer was built from `preview/zero400-signed-manual-phone-test`; its versionCode=1200 package=com.overmind.zero.ota and original production certificate was verified. GitHub Actions could not publish its independent release due to GitHub 403 integration permission; the signed ZIP is at artifact 12832398527 from run 40726139823. No V401 OTA or APK signed/published.

## Root cause A: physical red and weather injury did not invalidate motor-local plasticity

Original V400 `Zero3OriginalBrainV356.observed` checks true muscle impulse, wall touch, heldout, physical intervention and runtime correction before `AdaptiveCortex.learnV332`. But it **does not check** `actualDamageDuringDecisionV359`, although the same method accumulates real energy harm from `GameActivity.resolveObjects` and `ShelterEcology.Exposure`. A true red contact can slow the body masses by ×0.6 in `GameActivity.resolveObjects` (separate from any muscle force) and consequently produce a false motor gradient. This is actual deployed V400 source, not a suspected missing helper.

**Fix**: `Zero401LocalMotorCreditSafety.cleanPhysicalDecision(...,actualHarm)` requires a physically clean true trial with NO real harm. A contaminated trial discards its current and preceding delayed trace, but the sole original GRU3 cognition and executive still receive genuine hurt, photons and life energy — no scripted red avoidance, no penalty injection, no reset, no change to 194 sensors/512 weights/GRU64/slot encoding. Diagnostic counters track censored red/weather trials.

## Root cause B: delayed V332 credit across sharply changing GRU intentions

Original V400 also credits next-decision material response to a PREVIOUS muscle perturbation with fixed `0.82` temporal multiplier, without testing whether the next actor action is physically comparable. In real autonomous GameActivity the GRU3 reverses or turns while looking at the world. V399 512-decision S21 proof held heading constant for each 16-decision run, so it could **not** validate reversed-intent delayed credit. This is a generalization flaw, not a demonstrated deficit of the GRU3 itself.

**Fix**: `nextDecisionDelayValidity(previous,intention)` attenuates only the next-action delayed component using *already issued genuine neural intentions*: full original 0.82 weight at aligned headings, decays to zero at 60° change, zero for >=60° or opposite, no change to the previous true local evidence. This is an **eligibility filter** on learning, not an authored motor direction. A 2k autonomous Android control is necessary because the original V399 did not test this case.

## Root cause C: reintroduced PRE inertia attribution omission (isolated experiment)

The original global V378 motor credit subtracts `PRE_v * executedSeconds` from center movement before assigning an actuator-caused result. Yet V400 ported V332 local credit verbatim, whose formula uses **raw COM displacement**. A body already drifting WEST may have CH0 physically braking toward EAST but still move WEST; V400 can train on the net WEST drift rather than measured decrease of inertia. This is confirmed source omission. Unlike (A)/(B), subtracting inertia mechanically is an approximation whose sign and gain still require **independent physical confirmation**.

**No V402 production patch**. A separate branch `lab/zero402-s21-local-motor-pre-inertia-credit` runs 4 independent arms (old/global, raw V332, V378-style PRE-inertia corrected V332, frozen) starting from the authentic user V388 33,312-decision cortex and the two original recorded S21 postures. Training: original 512 real solver decisions per arm, holdout: 16 genuine physical contexts incl walls/rest. Test must preserve frozen exactly and only promote if realistic steering and all headings improve. Do not mark scientific success merely because Android reports green.

## Tests and live acceptance

- [ZERO401 original Android regressions 40728022680](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40728022680): SUCCESS; 3 targeted safety assertions plus existing original brain/tactile/cold-resume checks.
- [ZERO401 2,000+ true autonomous original game 40728016172](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40728016172): inspect actual result before claims. Must complete >2000 real GRU3 decisions, physical efficiency >=12%, valid original blues, red-contact improvement and zero physics/neural errors; 2000 failure is a science FAIL.
- [V402 original S21 separate lab 40728656362](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40728656362): inspect completed original 33k cortex measurements and negative evidence before any new change.

Original V400 2,004 truly autonomous decisions: 19.88% motor efficiency, blue1 red3 (all red in second 1000). This demonstrates **improved signed travel but safety/goal gate FAIL**; 8.80% original V397 run is a separate (not randomized matched-seed) baseline. Avoid claiming V401 eliminates red collision by itself; the brain might choose to go toward red even if motor credit is safe.

**No APK/OTA/public release for V401 or V402** until true harm, anti-censor, locomotion and 12-save compatibility pass. The 3.0 GRU retains 100% motor and executive ownership.

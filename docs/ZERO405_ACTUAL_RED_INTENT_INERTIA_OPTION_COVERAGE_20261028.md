# ZERO405 — Investigating actual red contacts in original 3.0 GRU3

**28 October 2026. Real original body, original executive, NO forced decisions, no APK/OTA.** Source branch `lab/zero405-real-hazard-actor-option-coverage`; forked from `lab/zero404-real-hazard-intent-inertia-forensics` (V401 actual code). This is a READ-ONLY forensic, not a motor fix.

## Confirmed experimental motivation

- V400 original GameActivity 2,004 autonomous actions: signed efficiency 19.88% but BLUE=1, RED=3, real science gate FAIL [run 40720216856](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40720216856).
- V401 original 2,000 autonomous actions: signed efficiency 36.25%, BLUE=4 but RED=11 (all red in last 1000); real science gate FAIL [run 40728016172](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40728016172). V401 excluded 315 real damage-contaminated motor learning decisions (weather+gem, NOT 315 red gems) and filtered 532 abrupt-turn temporal contributions. Better motor credit does not guarantee safe action selection.
- Original `Zero3CognitionV353.select` and `Zero3ContactValenceV369.utility` correctly prefer safer independently supplied actions in simple red/blue tests, e.g. `Zero380FailureReproductionAndroidTest`, `Zero374RealTouchOpticalIdentityAndroidTest`. However `Zero3RecurrentBrain.choose` normally only samples 3 near-actor random alternatives before its independent mobility uncertainty gate trusts wider sampling. An accurate hazard learner cannot choose an unavailable alternative. This is a plausible limited-proposal **hypothesis**, not yet proved a cause of V401 red collisions.
- Also physical COM inertia may make a red touch happen despite the GRU commanding away; the real original S21 cortex often commands EAST but material actually moves WEST. Do NOT label every red contact a cognition failure.

## Exact source observer

`GameActivity.resolveObjects` captures genuine `UnknownWorld.Entity.touch` signed physical energy, real original material distance and angle, `brain3.directionX/Y`, and material center velocity immediately BEFORE `object.touch` applies its physical red ×0.6 slowdown. It does not read object physics before contact for any chooser.

- `intentionToward` is the dot product between GRU selected direction and ACTUAL material's incoming point-of-contact, so positive = chose approximate *toward* and negative = away. Body shape still matters.
- `PRE_HARM_momentumToward` is observed current center velocity projected toward the actually encountered material: positive = body already drifting into contact.
- `opticalAtTouch` is actual previously available signature optical intensity, not proof high-level brain recognized its true outcome before the FIRST harm.
- `PRE_choiceCoverage=o405|offered=N|learnedVisible=...|bestOptical=...|chosenOptical=...|worstOptical=...` is snapshotted in the real `Zero3RecurrentBrain.choose` from the **then-visible optical array and already-learned V369 contact preferences** on all genuinely generated GRU alternatives, strictly **BEFORE** the executive. `chosenOptical` is calculated on the final actual executive-authorized direction. These metrics do NOT override selection or read the true hidden gem reward. They cannot themselves prove all benefits/risks of an action because V353 world model, V319 adhesion and actuation latency also matter.
- These are observational correlations; a red impact may be due to inertia, choice, a third unseen hazard or an unwillingly shifted deformable tip. A causal alternative motor replay is required before assigning one mechanism alone.

## Scientific action

[Zero404 original-game real material intent vs momentum CI 40729416350](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40729416350) (standalone read-only collision audit). [Zero405 original red-contact candidate coverage CI 40730624733](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40730624733). Both run Android >2000 original independent GRU3 decisions and upload true contact logs even if red safety gate fails.

**No production change** to GRU3 actor sampling, V353 cognition, V319 grip, 512 cortical weights, 194 input width, V356 CRC, 12 saved slots, original physical world, signed V400 phone APK, V388 stable or latest.json. Do not invent performance outcomes before finished CI. If no red contacts occur, the original test is inconclusive on red causality; if a crash prevents startup, also inconclusive. A green compiler alone is NOT a physical fix.

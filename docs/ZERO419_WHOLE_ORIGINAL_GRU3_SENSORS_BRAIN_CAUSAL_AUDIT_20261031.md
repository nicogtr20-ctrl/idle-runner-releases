# ZERO419 — Non-invasive whole live-GRU3, sensor, credit and safety audit
Date: 2026-10-31. Branch: `audit/zero419-original-gru3-full-sensory-causal-audit`. Based EXACTLY on ZERO418 `61e5ca852ea6a5f75cd66a8c39917c0bf166edb7`. **Audit, not a new brain/version, APK, OTA or a verified locomotion fix. No production code changed.**

## Scope of actual LIVE owner (do not confuse the old V2.2 with GRU3)

Reviewed source of `GameActivity`, `Zero3OriginalBrainV356`, `Zero3SensoryFrameV315`, `Zero3AutonomousLoopV317.encode`, `SensorimotorMind.Senses.spectral8/firstSolidDistance`, `Zero3GruSensoryV347.Unified`, `Zero3RecurrentBrain`, `Zero3CognitionV353`, `Zero3ContactValenceV369`, `Zero3InstrumentalCreditV371`, `Zero3InertialResponseV377`, `Zero3ExecutiveV354`, `Zero3PlannerV354`, `Zero3LearningFoundation.AdaptiveCortex/Coordinator`, `Zero3ContextAdhesionV319`, `SymbioteMatterV1.step` and V417/V418 10k physical Android logs. Inspected prior V397 motor-eligibility test, V412 sensor restoration report, prior V413 physical interventions and existing original-game Android workflows.

### LIVE 194D GRU sensor map (actual `Zero3OriginalBrainV356.sense`)
- `0..7`: 8 compressed max tactile solidity/pulses; `8..15`: 8 compressed two-ray obstacle proximity (from `Zero3SensoryFrameV315.capture`); `16..17`: COM speed; `18`: mean 74-particle fluid density; `19`: proportion held skin.
- `20..43`: 3 spectral wavelength bands × 8 retina azimuths (real eye on deforming skin).
- `44..59`: 16 fine solid obstacle rays with chamber-aware geometry.
- `60..123`: 16 sectors each of touch, impulse, hold and extension.
- `124..187`: 64 last issued motor command efferences.
- `188`: reserve; `189`: previously observed damage; `190`: gel area error; `191..192`: COM velocity again; `193`: constant 1.
- 96×12 raw skin features, 74×7 particle interoception and `bodyAngle` exist in V315 but are NOT fully transported to 194D GRU. The distributed cortex sees some local real radial/tangential tissue motion while GRU sees only compressed contact/shape/velocity. This is a topology tradeoff / diagnosis gap, NOT alone proof of faulty math.
- Sole original 64-GRU actor chooses heading, effort, grip, duration; original 16×4/512 local cortex outputs CH0/CH1/CH3 to the 170-material solver, V319 grip owns CH2; preserves all existing 12 profile save slots and V356 CRC.

## Confirmed implementation/integration inconsistencies (NOT yet proven sole root causes)

**A. CRITICAL: higher motor response learner rejects wall escape feedback that local motor cortex accepts.**
- `Zero3OriginalBrainV356.observed`: V414 `verifiedRearRetreat` may allow a physically positive rear-wall motor probe in `Zero401LocalMotorCreditSafety`, updating original 512 weights.
- The same decision's `brain.witnessedMaterialMotionV371(...,physicallyTouchedSolidDuringDecisionV380)` passes ANY solid contact as `physicalObstruction`. `Zero3InstrumentalCreditV371.complete` only advances `inertia.finish` and trained/heldout motion map when `clean = physical&&!disturbed`; `Zero3RecurrentBrain.witnessedMaterialMotionV371` and subsequent instrumental complete explicitly suppress data for `blocked`.
- A genuinely measured rear-wall escape can therefore improve the low-level muscles but cannot populate the same body's high-level `V377` verified steering table. High-level candidate exploration remains dependent on two validated steering headings, causing a possible learnability deadlock. This is a source-confirmed **misaligned eligibility contract**, not proof every rear contact should be accepted. Fix would require same strict verified rear-only and genuine forward, no front wall/teleport/harm, and independent heldout physical measurements in high-level learner.

**B. HIGH: harmful RED collision velocity damping can be learned as muscle response by upper motor model.**
- `GameActivity.resolveObjects`: after a true red touch `object.touch`, damping runs `creature.vx[i]*=.6f; creature.vy[i]*=.6f`, followed by material velocity observation; this is world-caused physical change.
- `Zero3OriginalBrainV356.observed` accumulates `actualDamageDuringDecisionV359`. V401 local cortex motor credit correctly excludes damage, but `brain.witnessedMaterialMotionV371` is called WITHOUT harm flag and gates only `disturbed||externalCorrection||censored` and skin solid touch.
- When red touch is non-terminal and is not independently marked as a solid/external correction, `Zero3InstrumentalCreditV371.complete` may train its V377 action→travel map from a red-damped body. **Causal eligibility gap** in red-heavy real 10k tests. Instrument PRE/POST red impulses and prove exact samples before modifying, then censor *motor response attribution only* from actual red/world damping while preserving cognitive negative contact evidence and terminal training.

**C. HIGH: V397 measured delivered probe exists but V400/V414/V417/V418 delayed local learner uses pre-actuator noise.**
- In `AdaptiveCortex.command` actual output is `clip(tanh(2.4*(z+noise))*2*effort,-1,1)`. It can saturate or be scaled almost to zero.
- In `AdaptiveCortex.captureProbeV332`, the `LocalProbeV332.noise` is the **raw pre-tanh perturbation**, not effective motor output. `learnV332` updates using `probe.noise[out]` even when the physical delivered command difference may vanish.
- `deliveredProbeV397` was implemented for `AdaptiveCortex.learn(score,...)` and has an existing Android test for output saturation; the V400 local/delayed learning path directly calls `learnV332`, not that measured fallback, and original `Coordinator` does not enable V397 for this method. Therefore V397's guarantee does NOT establish local V332's credit quality. Test zero-delivered/saturated true solver probes on user's saved S21; never blindly swap coefficients before A/B.

**D. MEDIUM: co-present low- and high-resolution obstacle rays use different physical world geometry in rooms.**
- `Zero3SensoryFrameV315.capture` builds 16 rays through `firstSolidDistance(null,objects,...)`; `Zero3AutonomousLoopV317.encode` compresses these into the GRU's eight `inputs[8..15]`.
- `Zero3OriginalBrainV356.sense` independently computes `inputs[44..59]` with `firstSolidDistance(chamber,objects,...)`. Implementation of `firstSolidDistance` checks `solid(lab,...)` only when chamber is non-null. At the same time the coarse and fine neural eyes can disagree about a chamber door/wall that is not represented by an object. In ordinary open world `chamber=null`, no such mismatch.
- This is a source-confirmed two-sensor provenance inconsistency, not yet quantified on saved S21 training-room layout. Compare identical physical rays, with and without chamber geometry, in an original Android true-room test; do not assume all S21 wall nodes are absent from the object list.

**E. MEDIUM: original red-touch visual telemetry uses a different azimuth from original retina.**
- `GameActivity.resolveObjects` logs `opticalAtTouch=contactPhotonsV369[signature*8+sector]` using `atan2(object.y-creature.cy,object.x-creature.cx)` (body CENTER), whereas `Senses.spectral8` bins `atan2(object.y-eye.y,object.x-eye.x)` from a protruding skin EYE. Zero in this log does not prove the same source is invisible on its actual retinal azimuth. Change read-only logging to record max of eight **same band** rays or true retina azimuth, never change the actual optical input on the strength of this forensic mismatch.

**F. HIGH BEHAVIORAL: generated candidate diversity remains 3 around real red contact.**
- `Zero3RecurrentBrain.choose`: 3 options until motor trust becomes sufficient; 5 or 7 only with `motorCanSteer`, `realStall`, `embodiedImpass` evidence. Red decision logs in V417: 69/69 `offered=3`; V418: 54/54 `offered=3`.
- Source-level V405 log optical best is *among the same three offered actions only*, NOT the possible safest direction in 360°, so `bestOptical==chosenOptical` is not proof choosing red was inevitable. A high-confidence motor-map deadlock could restrict both physical action exploration and high-level direction selection, especially at walls.
- Actual V417 10k: 69 red, 5 blue, 58.86% signed movement; 64/69 red contacts with issued intention toward source `>0.3`; 67/69 have `learnedVisible=true` (at least ONE seen spectral band previously has observed consequences, NOT guaranteed specifically red). V418: 54 red, 3 blue, 44.64% motion; 39/54 intention toward red `>0.3`, 51/54 `learnedVisible=true`. Thus observed red touches are not explainable as inertia ONLY. Candidate coverage/ranking/safety and poor motor response coupling must be tested jointly.
- `Zero3RecurrentBrain.completeConsequence` actor gradient assumes continuous Gaussian perturbations, but `cognition.select` and optional `planner` select among candidates by learned model; this conditions the selected noise distribution. Investigate estimator bias on heldout original action traces before concluding it is the primary fault. Existing `executive.optionReplayV354` already prevents naive update for recalled policies.

**G. ARCHITECTURAL: incomplete full-body shape knowledge at the high-level GRU.**
- 96×12 membrane local sensing, 74×7 fluid interoception, and the V315 body angle are captured, but full geometry and tissue-relative slip are not all propagated through the compressed 194D vector. The local cortex sees measured radial/tangent velocity, but the high-level selector cannot directly condition its heading on the full deforming body posture; only 16 extension/contact groups and prior commands/COM velocity. This may compromise steering in elongated or multipronged morphologies. Requires an ablation on real body states and unchanged trained saved weights; **do not** append input neurons or change 194D topology/checkpoint in place.

## Controls which DO exist / must not be regressed
- Original single GRU3 motor owner and live original material solver, confirmed by V418 source regression success. No V2.2 75%-actuator blend.
- 194 real bounded input projection, tactile/proprioceptive original frame, real post-solver contact and spectral retina.
- Physically measured red energy / warning and negative reinforcement of terminal real contacts. Do **not** erase real red harm training when censoring contaminated motor derivative.
- Original V412 distinction between COM inertia vs tissue shear and held grip vs wall; original V319 independent grip owner; original V401 harmful/front-wall/no-real-probe/teleport reject.
- Checkpoint 33,312 decision trained original S21 with 19,452 motor updates, original V356 CRC, 512 stored motor cortex weights and 12 slots untouched.

## Exact 10k baselines and hard gates (NO threshold changes)
- V417 S21 exact 10,004: 11.2869%, 4 additional blue, **2** inverse 1k blocks; real wild 10,002: 58.8634%, 5 blue, 69 red. Both FAIL. GitHub runs `40958869958`, `40958870014`.
- V418 S21 exact 10,003: **6.4039%**, 4 additional blue, **3** inverse 1k blocks, 2,293 cortex updates; wild exact 10,006: **44.6441%**, 3 blue, 54 red, 5,016 updates. Both FAIL; source regressions and S21 512-weights/checkpoint CRC PASS. Runs `41025018667`, `41025024655`, `41025030704`, `41025036669`.
- S21 hard target: >=25% signed/gross, >=9/10 independent 1k windows >=10%, ZERO opposite windows, >=5 new blue. Blue-only S21 room does not allow inference about red safety.
- Wild: >=12% signed/gross, actual blue/visible red evidence, red last 5k < first 5k, and total red <=2× total blue. Source/regressions and saved checkpoint must also PASS. One green isolated motor or Android build test NEVER equals production approval.

## Prioritized diagnostic experiment plan, no unapproved brain modifications
1. **Original source-level causal trace:** for each genuinely issued action, log PRE/post COM, all 64 original cortex outputs, **isolated probe actual delivered delta**, contact-wall front/rear/foothold, RED damage/damping, external solver corrections; pair the high-level V377 accepts/rejects with low-level V400/V414 accepts/rejects; measure bad stored derivatives on identical physical events. Keep 10k runs as final validation, not replace with short quick demos.
2. **Physical S21 A/B gates**: same user body, restored original 33,312 saved brain; compare strict rear-wall motion map vs original strict map, and separately raw vs *delivered* probe. Preserve all original GRU weights before each matched evaluation and keep interventions inside physics.
3. **Sensor coherence tests**: two resolutions of obstacles vs actual chamber scene at same time and eye, 24 spectral bands/8 azimuths, 16 tactile/extension, COM speed and orientation; examine motor echo, clipping and saturation.
4. **Reds and autonomous proposal proof**: choose actor-only / cognition / planner / executive under unchanged observed sensory inputs in forensic read-only mode; log offered candidates (all 3), selected indices, actual visual valence, predicted *physical* direction and red-world contact. Never insert scripted safe direction or force.
5. If and only if sources/tests establish an actual causal repair, make a separate experiment branch with one change and run same 2×10000 consecutive original GRU3 Android decisions with identical environmental constraints. Never change preset goals or lower threshold.

**Current release decision: BLOCKED**. This file records an audit of the latest code, not that the causes have been conclusively fixed or tested. No APK/OTA/merge, no saved brains changed.

# ZERO 2.1 — Unified Cognition: architecture and scientific contract
**Date:** 2026-10-13. **Status:** integrated research kernel, NOT an intelligence-complete release. **Source:** private nicogtr20-ctrl/idle-runner-releases. **Development branch:** feature/zero21-unified-cognition. **Stable Android branch:** feature/overmind-inapp-updater; production OTA 0.11.6-ota.112, code 1112 at baseline.

## 1. Objective and falsifiability
ZERO should build useful, revisable internal hypotheses about its own deformable body and an unfamiliar environment; connect what it observes, what it chooses to do, what physically happens, and what it subsequently learns. It must retain proven motor skills when reward expectations change, transfer skill to unseen relevant situations, and withdraw mistaken expectations when the world changes. It should not receive secret object labels, exact resource locations, predefined 'open box' actions, or handwritten answers to a puzzle. Physical laws and energy needs are simulator design; whether it learns to exploit them must be measured.

This does NOT assert consciousness, AGI, semantic language understanding, or solved causal inference. A model that predicts repeated frames well can still fail at exploration and reward. A model that wins a benchmark can still overfit one habitat. Every behavioral claim needs reproducible paired evidence on the same initial learning state and held-out worlds.

## 2. Read these canonical sources BEFORE working
- docs/ZERO21_HANDOFF_NEXT_CHAT.md: operational continuation checklist, baseline, branch, strict release rules.
- docs/ZERO21_IMPLEMENTATION_LOG.md: file-by-file source audit and test status.
- This document: architectural invariants, milestones, failure criteria.
- Existing docs/ZERO20_OBJECT_CAUSAL_MEMORY.md, docs/ZERO20_ACTION_PRIORITY_AND_CONTEXT_GRIP.md, docs/ZERO20_PERCEPTUAL_ASSOCIATIONS.md in stable branch.
- docs/ZERO_20_INTELLIGENCE_RESEARCH.md on feature/zero-20-hierarchical-intelligence (long research experiments and NEGATIVE heldout results).
- OVERMIND_START_HERE.md, AGENTS.md, docs/WHITEPAPER.md, docs/ARCHITECTURE.md, docs/ROADMAP.md, docs/CHANGELOG.md, docs/HANDOFF.md, docs/UPDATES.md and experiment papers (especially 0095, 0099, 0101-0103, 0109-0116, 0116_2..9, 0118-0127).

IMPORTANT: docs/HANDOFF and docs/UPDATES contain historical sections describing OTA97/105 as 'current'. Never trust an old lead paragraph over the exact current main/updater/public/latest.json, stable branch head, signed release and real device version.

## 3. Verified starting physical/algorithmic state
- Android native Java; Samsung Galaxy S21; 10 material masses, 96 independently deformable tissue points, massless energetic centroid. No fake force nucleus.
- 26 existing policy sensor channels: energy reserve (index 0), centroid motion (1-2), body extension (3), 96-site touch and grip fractions (4-5), eight observed obstacle-sector proximity values (6-13), 12 raw spectral feature channels (14-25).
- 16 freely selected actions: 8 orientations x 2 adhesion levels; physical body/membrane/hand weights (62/82/18), all preserved.
- SpatialPerception physically observed and occluded 16 rays, bounded cell memory; body proprioception, distributed touch, impedance, optical cues and partially observed object-category hypotheses.
- SensorimotorMind already integrates episodic memory, goal continuity, instrumental navigation, source attribution, learned short horizon planning, context-specific grip recovery and homeostatic energy weighting.
- Separate shadow research models PredictiveWorldV20, TransferSkillsV20, FuturePlannerV20, PhysicalConsequenceV20, GripAffordanceV20, PerceptualAssociationsV20. Normal Android explicitly calls enableZero20ExperimentalAdvice(false).
- Historical hard-won physics rules: energy cover requires truly opposing bilateral held tissue contacts; construction plate needs one truly confirmed local RED adhesion point; gate independent holds; no collision solver translations counted as autonomous manipulation; wind acts on body material; energy contact only on real membrane touch.
- Existing twelve named saves, SharedPreferences active working brain, profile codec, explicit GPT/GitHub multipart share, same Android package com.overmind.zero.ota and release-signing certificate.

## 4. Why a unified representation is necessary
Observed regressions: long-run 96-point local RED command/contact ratio fell from ~78.4% to ~4.3% in different on-phone sessions while gross adhesion-on remained frequent; rapid HAND/MEMBRANE online weight drift may erase a physically successful technique. A virgin brain chose one action 245/369 times in a final life despite visible learned energetic opportunities, because generic manipulation credit exceeded metabolic priority. Prior v20 real-phone data had 44,574 predictive transitions but world prediction below reliability thresholds; all 5,578 future-planner checks abstained. These are observations, not proof of a unified cause. DO NOT silently lower confidence thresholds or script automatic RED to make the UI look intelligent.

We need one shared, time-aligned event semantics, not another independently rewarded movement module. Perception, physiology, object-hypothesis, action, physical displacement and actual contact-energy are joined by provenance and uncertainty. A skill's ability to move an object is different from the currently estimated utility of doing it.

## 5. Unified state schema (implemented kernel)
New pure-Java UnifiedCognitionV21:
- key unified_cognition_v21, format uc21; additive to preexisting save and 12 named slots; no old key overwritten.
- One 26-float real sensory frame + optional 16 actual ray observations; separate tentative scene category from ObjectExperienceV20; world and object prediction confidence are *read-only*.
- Belief snapshot = normalized metabolic reserve, actual touch proportion, actual held-grip proportion, nearest actually sensed obstacle, world/object confidence and epistemic uncertainty; temporary scene hypothesis ID is not semantic and NEVER saved as authoritative identity.
- Four coarse empirically visible contexts (energy low/high x physically near/far), 16 real actions, bounded 64-cell statistics: action opportunities, observed physical displacement success, favorable/damaging energy events and accumulated REAL contact energy.
- Temporal order: after normal SensorimotorMind.choose picks action (including exploration), record ONE pending action; after real clamped energy contact record the sign/amount separately; after physics and the existing 0.65 simulated-second decision window record the observed effect, then clear pending; never treat a hypothetical rollout or a repeated callback as experience.
- Shared existing LearningDiagnostics.shouldHoldout gate. Heldout events measure movement Brier loss against a no-change baseline without updating the 64 learned action/context cells. No same-sample train+test.
- Conservative confidence=0 unless at least 80 actual holdouts, 12 observed external-motion intervals, sufficient nonzero baseline loss, and >=8% heldout predictive improvement; this is a measurement only, NOT an action bias.
- During death, reset or app resume, pending action and unverified object hypothesis are erased; consolidated experience stays. A corrupt persisted model is rejected atomically, leaving current learned data unchanged.
- No privileged object.type, box ID, energy-under-cover, morphology legality flag or ground truth target is supplied to this model. Passing a tentative category does NOT prove an object instance; scene effects are ASSOCIATIONS, not object-specific causal effect.
- Every API is pure Java, bounded in time and memory. No sensor processing every 35ms physics substep; physical controls already sample at their established rates.

Current integration order in SensorimotorMind and GameActivity:
1. Load optional unified cognition from active SharedPreferences; attach to real policy and preserve existing model loading.
2. Only after action selection: unified.chosen(action, senses, observed spatial ranges, provisional object hypothesis, predictor confidences).
3. On physically applied contact reward/loss: unified.energyContact(realDelta).
4. On the original observed transition, sharing its exact holdout flag: unified.consequence(afterSenses, actually observed generic physicalEffect, train&&!holdout, holdout).
5. Save unified.encode() with all existing profile keys; WORLD scoped reset erases this evidence along with world model; death/resume erases transient pending only.

NOTE: generic physicalEffect is not proof a particular sensed object's position changed or an action directly caused that change. That limitation is intentionally represented in the schema and future gates.

## 6. What this IS NOT
The unified ledger is the first integrated foundation and cannot yet produce reliable 3D recognition, track the same physical box from arbitrary views, infer long-delayed cause, evaluate body-actuation affordances for named objects, automatically consolidate a universally transferable grip or improve policy. Its prediction confidence is informational. No additional game reward, new movement, new button, simulated lifespan advantage or claim of object comprehension was introduced. Existing historical model scores remain active as-is. Do not market or release this draft as 'finished ZERO 2.1 brain'.

## 7. Planned unified architecture beyond the kernel
### G1 — canonical observation and provenance (implemented on branch; CI pending)
Replace loosely timed, duplicated sensor/action bookkeeping with an immutable, timestamped observation pair and exactly-once physical-outcome event boundaries. Current ledger is the first adapter. Extend with physical event IDs generated by the evaluator, but NEVER expose privileged IDs to the learning policy. Add read-only telemetry for null hypotheses, evidence counts, holdout residuals and CPU cost. Ensure training/validation streams stay separate.

### G2 — body/world belief + multi-view object persistence
Estimate occupancy, displacement, texture/ray contour hypotheses, viewpoint and odometry uncertainty across consecutive genuinely perceived views. Segment overlapping silhouettes conservatively; abstain if two objects could explain the same rays. Object instance IDs must be learner-internal anonymous tracks generated by sensory continuity alone. Evaluate with objectively known game object IDs ONLY on benchmark side (outside agent API). Prove invariance under rotations, range, occlusion, unusual geometry and changing perspective; detect identity switches.

### G3 — action-affordance and durable skill knowledge
Associate read-only 96-tissue physical touch/traction geometry and actually applied hand/membrane coefficients with independently verified movement of a consistent observed object hypothesis. Archive HAND+MEMBRANE *pairs* when repeated successful opposing holds and actual displacement occur; record failed attempts separately. Mechanical competence persists independently from expected contact energy. Permit rollback/reuse only following self-selected adhesion, comparable observed contact geometry and sufficient repeated failure; never force RED or transfer incorrect box technique to a thin panel. Preserve locomotion before accepting mutants in new tasks.

### G4 — causal hypotheses and revisable beliefs
Explicitly distinguish intervention, temporal association and causal identification. For candidate 'moving this class often exposes energy', require repeated, reasonably localized sensory tracks, actual movement, subsequent physically acquired energy and matched independent control cases. Learn base rates for 100% full, 1% full, mixed benefit/harm, 99% empty. A source that was unobservable until a later action must not be attributed with unjustified certainty. Null hypotheses and abstention are valid and often correct. When environmental law changes, track prediction error and retain motor skill while revising utility estimates.

### G5 — shared memory service
Organize three typed stores: individual episodes with source and time, slowly updated relational knowledge with confidence and last tested context, and durable motor policies with frozen proven checkpoints. Protect knowledge against catastrophic overwrite but allow evidence-driven revision. Separate transient life state from cross-life stable skills; isolate slots/persons. Explicit replay must be off-policy observational or sandbox-only; it must not count imagined physics as lived.

### G6 — one homeostatic goal/decision arbiter
Choose between exploration, opportunistic energy collection, physical manipulation, sheltering, or inertia *based on measured expected outcomes*, not named-script behaviors. Energy is a drive derived from physiological reserve and real contact history; novelty may be rewarded only for validated prediction improvement, never movement loops. Feed alternative actions and skill rollouts through calibrated world-model uncertainty. The planner may abstain; the proven original policy remains fallback. Cap CPU work to a fixed fraction of actual sim frame time and preserve x20 physics performance.

### G7 — evidence-gated policy and independent evaluation
A policy suggestion can be turned on in a laboratory only after G2–G6 heldout calibration. Exact matched seeds and same virgin/trained snapshot baseline vs proposed integrated policy; train worlds and unseen heldout worlds must be separate. Report BOTH failures and successes. Do not call 0 ties an improvement; '0 eligible' is a safety gate, not a functioning planner. Phone production release requires clear net benefit in energy collected and survival with no clinically meaningful regression of valid grip, membrane integrity or frame rate. Use candidate-shutdown/rollback after real evidence of decline, not manual thresholds tuned post hoc.

## 8. Test design, not merely compilation
### Isolation and invariants
- JVM old classes, real 96-tissue/grip/box/panel/wind physics; same seed and old policy action trace before vs after attaching observer.
- No privileged entity accessor inside new learning code; static scan for prohibited typed object access and changes to score/reward/movement; no new modules inside the choose candidate loop.
- No train on holdout, no hallucinated physical effect from a RED command alone, no energy event from metabolism/wind or imaginary future.
- Exact corrupt-model rejection; both old profiles missing key and newly saved profiles load without reset; 12 slots fully independent; WORLD reset only, no accidental shared-key duplication.
- 0 unexpected physical movement by simple collision separation; TWO real contacts for a cover, ONE verified RED for plate, gate collision, authentic injury and hunger.

### Behavioral A/B worlds
- W1: energy under every movable cover; W2: 1 positive cover out of 100; W3: mixed harmful/helpful sensory situations; W4: 99% empty, 0.5% helpful, 0.5% harmful. World distributions affect environment only; model is NOT informed about percentages.
- W5: layout and mechanics distribution shift during life, e.g. altered cover material/storm strength or reward distribution. Observe relearning without forgetting legitimate grasp physics.
- W6: novel geometry and viewpoints, including opposite initial approach, tandem boxes, plates, full/partial occlusion, repeated gate entry.
- W7: motor actuation regression replay on user's saved virgin and trained brains; preserve original ability even while policy experimentation is heldout.
For every world: same initial trained/virgin brain, 25+ paired seeds as first smoke study, pre-registered outcomes, longer studies for rare rewards, survival windows past normal hunger death, distinct train/validation layouts. Larger samples and uncertainty intervals required before claiming uplift; never assume a 25-pair sample proves rare-event differences.

### Primary metrics
Real acquired energy net of real harm and metabolic cost; completed-life simulated duration distribution and death cause; real square displacement+uncover+actual reward contact; verified opposed grip per comparable contact frame, contact-to-hold latency and preserve/recover rate; plates with single-red only; new-world generalization and skill retrieval/rollback; object identity-switch/confusion rate from EVALUATOR; causal effect delayed-link precision/coverage; heldout Brier vs honest no-change / action-only baselines; model abstentions; per-decision CPU and RAM, thermal/battery on S21. Avoid comparing unmatched game minutes with x1 vs x20 and different world streams.

## 9. Release protocol
ZERO21 branch is research only. NEVER merge the complete old experimental branch into stable wholesale: code histories diverged and active stable OTA112 contains newer fixes. Keep signed APK, updater manifest, Vercel production, old mobile profile and 12 user slots unchanged while tests or behavioral gates fail. CI green is mandatory but not sufficient. Once real improvement proven in controlled A/B AND full Android QA, create selective PR into feature/overmind-inapp-updater; official workflow .github/workflows/overmind-ota-publish.yml signs the same package com.overmind.zero.ota with the original private cert. Verify GitHub Release, main/updater/public/latest.json, SHA256, matching APK and Vercel HTTPS public readiness before telling user to press AGGIORNA. Android install is supervised, never silent, no uninstall/reset. Any new signed release must preserve older profiles via optional additive schema and safe default.

## 10. Research discipline
In every commit/paper append the exact source SHA, CI run IDs, model parameters modified, metrics and regression failures, whether an action policy can be affected, on-device validation status and a concrete falsifying observation. Distinguish [implemented], [compiled], [tested in matched lab], [tested on S21], [released]. Do not confuse journal logs with scientific proof, or historical 'DONE' statements with current reality. Mark UNKNOWN clearly.


## 2026-10-13 — Integrated research implementation G2–G7 (read before assumptions)
The new working source and complete scientific limitations are documented in **[ZERO21_G2_G7_INTEGRATION.md](ZERO21_G2_G7_INTEGRATION.md)**. Implemented in a single research branch: `ObjectContinuityV21` anonymous sightline tracking and delayed energy hypothesis, `CompetenceMemoryV21` real physical HAND/MEMBRANE archive distinct from metabolic utility, `UnifiedArbiterV21` single cautious energy/risk/uncertainty arbiter in SHADOW, and genuine bilateral-cover paired physics arena with new-only vs original unchanged action policy. `SensorimotorMind`, `GameActivity`, `ResetDomains`, `BrainToolsActivity` and manual GitHub telemetry are wired with additive optional schema keys. A full causal world-model, reliable multi-view identity through extreme deformity, and validated performance-raising actuating integration are NOT yet complete. **No signed OTA or on-device experiment.**

# OVERMIND ZERO 2.0 — Research program and implementation ledger

Status: **EXPERIMENTAL, separate Git branch; NOT signed OTA.** First opened 2026-10-12. This paper MUST be updated after each implementation, ablation, test, and decision before proposing integration into the stable app.

## Vision and falsifiable objective

Create a progressively capable embodied agent which acquires transferable skills by sensing, acting, comparing **actual** consequences, predicting uncertain future changes and testing alternative actions. Not: hard-coded approach/open/escape scripts, hidden food coordinates, rewards on button presses, or arbitrary survival boosts.

ZERO is a 96-site, distributed-material organism. Its existing sensors, learned motor and grip weights, episodic/temporal/spatial/causal/navigation mechanisms, saved profiles and spontaneous exploration must be preserved.

**Intelligence is measured in novel, held-out physical worlds**: actual energy acquired, genuine source-confirmed energy covers uncovered, survival time distribution (not just single record), causal intervention success, prediction accuracy and calibrated uncertainty, generalization to unseen scenes, non-oracle self-unsticking, and absence of exploit/reward hacking. Report medians, means, distributions and no-change controls with paired seeds.

## Ground truth about 2026-10-12

Main `SensorimotorMind`: 26 sensory features and 16 discrete heading/adhesion actions. `LearnedSequencePlanner`: depth 3, beam 2, confidenced advice. Additional learned modules for vision, spatial evidence, proprioception, local impedance, temporal context, navigation and causal estimates are already connected; simply adding models can impair decisions or waste memory. Historical regression: average survival ~939 -> 546 -> 333 sim seconds, object movement 3.92 -> 1.24 -> 0.92 per life across successive report windows. The experiment in `experiment/zero-0128-ota-regression-replay` found 160 successful short physics counterfactuals but **not** full autonomous recovery; old unsourced double-box displacement was suppressed by OTA99. Spatial learning counters 11101 -> 1 -> 6205 in snapshots show a restore issue, not total brain erasure.

## Architecture / delivery gates

### Gate A — Validated latent world model [IMPLEMENTED IN SHADOW, QA IN PROGRESS]
- Dedicated nonsemantic, action-conditioned, low-dimensional world-state prediction constructed **only from actually sensed vectors and executed actions**, no hidden object IDs or scene state.
- Separate train vs real heldout observations, compare prediction against unchanged/last-state baseline, no training on heldout.
- Estimate novelty/uncertainty from observed residuals; NO automatic curiosity reward, steering or physics modifications in initial rollout.
- Persist learned readouts across death/scene saves/named slot copies; transient pending actions reset or resume conservatively; reject malformed/truncated/nonfinite models atomically; bounded memory and compute.
- Shadow statistics only. The stable `SensorimotorMind` must make **exactly the same decisions** as baseline for a matched external run until real benchmarks justify explicit learned-model advice. Any future advisory needs an ablation switch, evidence gate, shadow-only calibration and automatic failure-safe rollback.

### Gate B — Discovered temporal skills [CODED AND CI VERIFIED IN SHADOW — generalization not yet established]
- Detect repeated *actually lived* multi-action subsequences with verified delayed energetic/environmental outcomes, never a fixed handcrafted "open the box" routine.
- Prefer evidence across different lives and physical contexts; suppress memorized false correlations. Learned skills should be discoverable, interruptible and context-specific, tested against historical `TemporalSkillBank` and `EpisodicMemory`.
- No actuator overwrite, no motor replay simply on position match; learned suggestion only after independent validation.

### Gate C — Hierarchical uncertain planning [CODED AND CI VERIFIED IN SHADOW — benefit not yet established]
- Decision horizon >3 through learned option structure and learned world transitions; model-rollout is hypothetical, never counted as an actual experience.
- Calibrated uncertainty penalizes unrealistic futures; planning can abstain and fall back to original policy. Measured compute budget for Galaxy S21, slower brain tick than 40Hz mechanics.

### Gate D — Intrinsic information gain + transfer [PLANNED]
- Novelty based on out-of-training prediction improvements, **not on raw sensory change** or revisiting stimulus loops.
- Learn to choose useful experiments that reduce uncertainty without rewarding motor spam or existentially costly risk.
- Train worlds vs heldout worlds, novel orientation/cover multiplicity/occlusion/storm variation, compare true energy outcomes and deaths. Retain selective resets and life-long skill consolidation.

### Gate E — Independent integration QA + release decision [PLANNED]
- Add read-only telemetry of model errors/confidence/actual influence/CPU/sample counts; export remains explicit user action only and preserves private secrets.
- Historical grip (2 real opposing faces for energy covers), one red grip for construction plates, barrier, no phantom collision translation, 96-point tissue, wind, memory restore, all 12 slots, reset domains, Android assembleDebug.
- Paired headless long-life simulation. Test NO new module vs shadow vs evidence-enabled policy; isolate one system at a time. No release or forced migration before statistically meaningful improvements and no regressions.
- Signed OTA source-hash integrity gates remain enforced; independent feature branch must NOT publish an OTA.

## Implementation ledger

| Date | Phase | Change | Tests | Outcome |
|---|---|---|---|---|
| 2026-10-12 | Planning | Created isolated ZERO 2.0 branch and this paper | Initial review of core source and current telemetry | No stable code, brain, saved profile or production OTA changed |
| 2026-10-12 | Gate A implementation | `PredictiveWorldV20`: 20 real sensory channels, 5-parameter predictor per action/channel, actual-only recurrent context, heldout forecast error vs unchanged baseline, atomic versioned serialization, confidence/uncertainty shadow output | Java `PredictiveWorldV20Regression` and 12 historical physics/cognition regressions; Android assembleDebug, GitHub Actions run [38291504234](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38291504234) **SUCCESS** | No command, learned policy score, motor or source reward changed |
| 2026-10-12 | Gate A integration | Attached after real `SensorimotorMind` chosen actions, observed consequences on existing heldout split; saved optional `predictive_world_v20` in working profile / 12 slots; selective WORLD reset only; dashboard real confidence and transitions | Extended 360 paired baseline-vs-shadow decision/serialized-brain invariance checks; rerun of full Android QA after dashboard integration | **Verification completed on code SHA 90edd7d; experimental branch only** |
| 2026-10-12 | Legacy QA integration | Five pre-existing PR `javac` workflows had explicit fixed source lists and initially failed `PredictiveWorldV20` dependency resolution; appended new class to their compile inputs **without deleting a single assertion** | New `zero-20-shadow-qa` push run [38292243013](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292243013) **SUCCESS**; historical PR rerun queued | No production OTA, no removal of legacy QA |
| 2026-10-12 | Phase A accountability | Draft PR [#31](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/31), new dashboard evidence counters and exact 360-decision same-model comparison | All 8 historical/new workflows passed on code SHA 90edd7d | Draft; **not mergeable as production-ready** |

## Verified Phase A build and controls — 2026-10-12

**Latest code-tested SHA: `90edd7d487d7db5ee56f625d94ab74ac68264fb5`.** Nine matched real-physics A/B pairs use `BehavioralLaboratory` with three scenarios (energy, danger and locked gate) × three seeds. Both agents use exactly the same virgin motor/hand/membrane baselines and identical sensory state; one additionally trains the new shadow-world model. Regression asserts exact equality of actual traveled distance (within 0.0001 units), real energy gained/lost, physical resource and hazard contacts, gate openings, gate passages, and decisions. **All 9 pairs PASS**. Another 360 deterministic paired model-decision transitions PASS; the complete old `SensorimotorMind.serialize()` strings remain identical across shadow-on/off.

All eight checks for experimental code commit `90edd7d` completed with SUCCESS:
- New integration PR QA: [38292626247](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292626247)
- New integration push QA: [38292621192](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292621192)
- Historic spatial/physical recovery QA: [38292628152](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292628152)
- Historic 0126 confirmed single-red plate QA: [38292626398](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292626398)
- Historic 0122 ecology/motor QA: [38292626445](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292626445)
- Historic 0125 telemetry Android QA: [38292626385](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292626385)
- Historic 0121 independence QA: [38292626431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292626431)
- Historic passive grip observer QA: [38292626313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38292626313)

Each Java workflow's existing assertions were kept. Old hardcoded javac dependencies were *extended* to add `PredictiveWorldV20.java` instead of removing checks. No signed stable OTA workflow ran on the branch.

### Exactly what this proves — and what it does NOT
- Proves trained action-conditioned sensory forecasts improve on a controlled synthetic heldout dataset, optional persistence survives death and corrupt input is rejected, production shadow integration has no measured effect on the old policy in our paired scenarios, existing Java physics QA and Android **build** are successful.
- Does **NOT** prove novel hierarchical reasoning, more real-world energy, better survival in an unseen habitat, a complete long-life replay of the user's saved agent, or no runtime problems on the Galaxy S21. These are still explicit gates B–E.
- Thus **ZERO 2.0 is an experimental research branch**, not an APK/release ready to replace OTA99 or a completed intelligence upgrade.

## Gate B/C implementation protocol — 2026-10-12

1. **Long-horizon behavior traces**: learn reusable action motifs from executed decisions and consequences, not from suggested actions. Distinguish positive and negative *real* energy contacts, no credit for idle motion, and require corroboration in more than one physical life before describing a motif as transferable. Keep the existing 2–8-action TemporalSkillBank intact and compare evidence instead of silently rewriting it. Start as shadow, zero policy bias.
2. **Uncertain counterfactual planning**: score up to 5 future decisions with the action-conditioned v2.0 sensory predictor, pre-existing learned action values, pessimistic uncertainty penalties and an empirical heldout gate. Hypothetical states never become observations or rewards. Start as shadow only, bounded compute, rollback/abstain on uncertain/out-of-distribution states.
3. **QA**: exact decision/persistence parity on original ZERO, genuine 96-point paired physical scenarios, negative contact without learned positive bias, repeated cross-life motifs, model corruption/restore, all legacy GitHub workflows and Android build. No user profile migration or OTA before real lifespan benchmarks and demonstrated benefits.
4. **Evidence**: append each GitHub run ID, failures, mitigations and actual results to this paper; do not call a completed compilation proof of more intelligence.

## Gate B/C delivery — 2026-10-12: implemented, tested, intentionally not promoted

**Latest measured production-neutral code SHA:** `3e7d5078942b858b7ed76c694d84acc2e73952fe`, on `feature/zero-20-hierarchical-intelligence`. The active stable OTA remains unchanged.

### B — Cross-life discovered action motifs (`TransferSkillsV20`)

- Observes ZERO's **actually selected** heading/adhesion actions and actual positive/negative **clamped contact energy**; never reads object labels, food positions, gate flags, or synthetic reward. Stores a bounded 32-motif library, with motifs spanning **3–12** real actions, coarse learned context and raw exemplar actions.
- Separate whole-life holdouts (`life % 4 == 0`), positive evidence must be corroborated across at least two training lives **plus a heldout life**; harmful real consequences decrease certification. New candidates are not created from holdout lives. Repeated contributions from one life cannot masquerade as independent corroboration.
- Unseen-context matches are rejected; no silent override of legacy 2–8-action `TemporalSkillBank`. Old and new memories are independently serialized and selectively reset. **This is an observational association, not proof that a sequence caused its reward.**
- New read-only suggestion is **not used as a motor choice** and never enters the old score or reward.

### C — Uncertainty-gated five-decision lookahead (`FuturePlannerV20`)

- Uses only the separately learned `PredictiveWorldV20` action-conditioned **sensory** predictions and existing *experienced* action values. Horizon **5** vs original depth **3**, beam **2**, up to **256** leaf paths. Hypothetical rollouts never train any model or count as lived episodes.
- Abstains until ≥60 heldout observations, predictive confidence ≥0.10 and absolute heldout error ≤0.18. Pessimistically subtracts error-proportional penalties increasing with depth. Evaluates only every **8 real decisions**, **after** the original action was already selected, to bound S21 load and preserve decision timing.
- Stores **statistics only**; imagined next action is discarded upon restore. New explicit independent save keys: `transfer_skills_v20`, `future_planner_v20`; existing sensorimotor, motor and membrane keys untouched.
- The new dashboard reports corroborated candidates and valid hypothetical forecasts, explicitly tagged as shadow-only.

### Real CI evidence and initial issue

The first B/C dedicated run [38300171336](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300171336) failed because a **corruption-test fixture attempted to substitute a literal numeric string that was absent from the actual serialized sample**. That test did not corrupt the input. Fixed by mutating the known existing serialized positive-energy field to NaN before asserting atomic rejection. **No protection was disabled**. No claim of successful regression is based on this earlier failed run.

All eight workflows **SUCCESS** on corrected code SHA `3e7d507`:

| Check | GitHub Actions execution |
|---|---|
| New shadow Gate A/B/C Java regressions + debug APK | [38300317519](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300317519) |
| New independent pull-request integration check | [38300322509](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300322509) |
| Historic 0127 persistence, physical invariants and survival QA | [38300322557](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300322557) |
| Historic 0126 single-red construction grip QA | [38300322515](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300322515) |
| Historic 0122 ecology, autonomy and Android QA | [38300322507](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300322507) |
| Historic 0125 telemetry and Android QA | [38300322576](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300322576) |
| Historic 0121 old/new independence and Android QA | [38300322561](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300322561) |
| Historic passive grip observer and Android QA | [38300323263](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38300323263) |

Notable test output:
- Gate A synthesized holdout model: **12,501** action transitions, 2,085 heldout, forecast error **0.00304** vs unchanged-state reference **0.10997** (synthetic fixture; not a real-life performance measurement).
- Gate B synthetic demonstration: a **12-action** repeatedly observed sequence is corroborated across separate train and holdout *synthetic* lives; real negative energy devalues motifs, novel contexts do not trigger replay, truncated/nonfinite save rejected transactionally.
- Gate C synthetic world-model fixture: five-step counterfactual scoring, evidence abstention, real learned model state remains **byte-identical** after hypothetical evaluation.
- **440** deterministic old-versus-new policy choices and old brain serialization remain identical, despite observed real energetic contact injection; **nine** matched genuine body/membrane physics simulations (energy, hazard, gate across three seeds) remain identical with and without shadow modules.
- All legacy regression assertions and original physical constraints remain active. Full Android debug APK **compiles**. GitHub artifact storage is quota-limited and this workflow does not publish APKs.

### Acceptance still outstanding

1. Neither the historical 105-minute life nor current autonomous lifespan has been recreated with the restored user checkpoint under matched seeds.
2. No demonstration yet of transfer in *unseen physical habitats*; recurring motifs are observable correlations, not yet proven causally useful strategies.
3. No proof that the new five-step forecast improves real energy/survival; there is **zero policy influence by design**.
4. Gate D information-gain experiments, long-life matched A/B evaluation, computation/memory profiling on Galaxy S21, and a signed release decision all remain pending.

**Do NOT merge or publish ZERO 2.0 merely because these tests are green.**

## Gate D — benchmark and experimental-policy protocol (2026-10-12; pre-registered, results pending)

**Warning about earlier 32-second lab:** `BehavioralLaboratory` tests a visible resource, red hazard, or a gate. It does NOT simulate real energy covers requiring two opposing red grips. Those 9 paired scenes are useful for no-interference regression, but NOT evidence of recovering old two-box capability. We must explicitly benchmark full cover/hidden-energy physics.

New independent headless **real-cover arena** uses `MotorBrain.Body`, all 96 `Morphology.Surface` points, `SquareGripPuzzle.constrain/uncover`, `ShelterEcology.separateBoxes`, hidden energy touching, cost from time, real damages, and exact original `SensorimotorMind`. The examiner chooses layouts and records energy and movement, but the agent receives only genuine sensed vectors/contact consequences (no access to examiner target positions). Train on one set of configurations, evaluate on separately withheld orientations/layouts, paired seeds, identical initial saved brain/physical body for both policies.

Two ablations: original brain + passive v2.0 modules, and same brain + **sandbox-only** capped experimental planning advice. The latter is disabled by default, never enabled or surfaced in `GameActivity`. No release/merge without actual gain across holdout layouts AND no degradation in damage/survival (not merely different action counts). Threshold and decision effect must be recorded, including abstentions. Both sides can fail or tie; report inconclusive honestly.

Benchmark is limited by its episode duration and approximations; it does NOT prove that a 105-minute Android life behaves the same. Preserve real captured profile privately, never include secret user memories in GitHub workflow logs.

## Non-negotiable invariants
1. No destroying/mutating real ZERO brain; no copying historical profiles into production automatically; optional legacy default safe.
2. No privileged semantic target coordinates or scripted correct actions in learning code.
3. No invented energy, speed, grasp force, physics override or fake goal success.
4. Holdout is truly excluded from weight updates; counters never masquerade as evaluation victories.
5. Old saved keys untouched; new optional keys versioned; failure atomic; explicit resets isolated.
6. Tests include negative cases (virgin model no influence, corruption, death/resume, nonfinite input, unexpected state, out-of-distribution uncertainty).

**Interpretation rule:** Passing Java unit tests or compiling Android does not prove emergent intelligence or phone runtime parity. Benchmark full-life generalization before claiming any capability improvement.

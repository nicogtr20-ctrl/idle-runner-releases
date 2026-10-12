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

### Gate B — Discovered temporal skills [PLANNED]
- Detect repeated *actually lived* multi-action subsequences with verified delayed energetic/environmental outcomes, never a fixed handcrafted "open the box" routine.
- Prefer evidence across different lives and physical contexts; suppress memorized false correlations. Learned skills should be discoverable, interruptible and context-specific, tested against historical `TemporalSkillBank` and `EpisodicMemory`.
- No actuator overwrite, no motor replay simply on position match; learned suggestion only after independent validation.

### Gate C — Hierarchical uncertain planning [PLANNED]
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

## Non-negotiable invariants
1. No destroying/mutating real ZERO brain; no copying historical profiles into production automatically; optional legacy default safe.
2. No privileged semantic target coordinates or scripted correct actions in learning code.
3. No invented energy, speed, grasp force, physics override or fake goal success.
4. Holdout is truly excluded from weight updates; counters never masquerade as evaluation victories.
5. Old saved keys untouched; new optional keys versioned; failure atomic; explicit resets isolated.
6. Tests include negative cases (virgin model no influence, corruption, death/resume, nonfinite input, unexpected state, out-of-distribution uncertainty).

**Interpretation rule:** Passing Java unit tests or compiling Android does not prove emergent intelligence or phone runtime parity. Benchmark full-life generalization before claiming any capability improvement.

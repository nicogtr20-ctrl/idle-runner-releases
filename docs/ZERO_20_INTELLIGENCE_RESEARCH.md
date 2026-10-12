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

## Gate D actual-headless bilateral cover A/B — 2026-10-12: NEGATIVE (safety block correctly maintained)

### What was implemented
- `Zero20CoveredEnergyBenchmark.java`: standalone deterministic Android-free physical simulator, **real** `MotorBrain.Body`, `Morphology.Surface` (96 sites), `SquareGripPuzzle.constrain/uncover` (strict two-sided energy-cover grip), `PanelPhysics`, `ShelterEcology.separateBoxes`, post-contact finite hidden resources, harmful contacts, real sensed visual/tactile features, original policy and optional ZERO2 shadow/experimental advice.
- 14 *training* scenarios with preserved cloned original profile and V2 model state; then twelve **previously unseen** paired A/B configurations (2 layout families × 6 distinct seed values) using a **single identical learned post-training snapshot**. Experimental bounded planner advice is guarded and **only enabled by the benchmark**, never by `GameActivity`. It is OFF by default in `SensorimotorMind`.
- GitHub Actions `zero-20-energy-arena.yml` fetches latest user-voluntary uploaded **private telemetry profile**, merges all multipart parts *without showing weights*, compiles real Java physics, writes only numeric outcomes to run logs and human-readable summary. No APK artifact, OTA or profile mutation.
- Lab caveats: ~128 simulated seconds per test, static resource layout; **not** full Android streaming/weather/mutation/chamber ecology, not proof of full-life 105-minute performance.

### Reproducible result
Verified GitHub CI [38301753150](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38301753150) **SUCCESS**, using all five private report parts. Earlier run [38301526258](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38301526258) successfully ran all 12 A/B paired scenarios but FAILED its final GitHub-summary parser because `ZERO20_AB ZERO20_ARENA` had two log prefixes. Fixed parser without modifying trial code or discarding any verification.

| Outcome (latest real trained original brain) | Experimental ZERO2 minus original |
|---|---:|
| Matched heldout paired cases | 12 |
| ZERO2 wins / losses / ties | 0 / 0 / **12** |
| Different executed paths | **0** |
| Eligible reliable speculative plans | **0** |
| Mean real collected energy delta | **0.0** |
| Mean physical survival delta | **0.0 seconds** |
| Mean harmful contact energy delta | **0.0** |
| Fully uncovered hidden-energy covers across the 12 candidate heldout scenes | **0** |
| New world-model actions trained in lab | 2,150 |
| Independent world-model heldout samples | 538 |
| World-model heldout predictive error | **0.052731** |
| Unchanged-state baseline predictive error | **0.047759** |
| Independently corroborated long action options | **0** |

**Interpretation:** New learned world model performs approximately **10.4% worse** than predicting unchanged physical sensory state in this *actual* sampled setting. The confidence gate appropriately returns zero; consequently the opt-in experimental policy abstains. Neither "more intelligent" nor "less intelligent" in these 12 cases—ZERO2 produces **identical actions**, so we cannot claim a benefit. Several test cases include actual opposing grip frames but **no completed cover displacement/uncover**; this echoes the original box-manipulation difficulty rather than proving a planning cure.

### Research decision: DO NOT PROMOTE

**The deployment threshold FAILED.** Retain production OTA99 unmodified; keep PR #31 draft and no automatic decision influence. The next research revision must validate a *selective* dynamics/no-change predictor on truly different real physical habitats and independently measured action-to-consequence calibration. Repeated real energy contact and real spatial exploration should be learned as delayed outcomes without assuming that any press, touch, or ordinary body travel earns food. Only after this predictor beats the unchanged-world baseline **on untouched heldout scenes**, and real ablated paired outcomes improve, may any planner policy be considered for stable deployment.

## Gate D revision — selective change prediction (pre-registered, 2026-10-12)

**Motivation:** original real-saved-brain benchmark gave `0.052731` holdout error vs `0.047759` trivial unchanged-sensation predictor, so 0 trustworthy plans and 0 A/B advantages. A new model is not promoted until true independent test improvement. This revision is committed *before new experiment runs*.

**Candidate:** replace indiscriminate residual updates with *per-action/per-sensory-channel, train-observation-only estimates of consequential change frequency*, including a conservative action sample count. Blend learned delta toward the unchanged-state prior when sensory consequences have not been repeatedly observed; zero-improvement/unknown channels return prior rather than fabricate motion. Add bounded weight decay and an explicit measured-no-change diagnostic. Serialized independently in `pw21` while safely migrating prior `pw20` optional knowledge without touching any original cognitive/motor weights or counted observations. Keep the exact same confidence threshold, original brain policy and intact baseline controls.

**Falsifiable evaluation:** use identical `Zero20CoveredEnergyBenchmark` seeds, original last uploaded private brain and 14 train/12 unseen layouts; compare model holdout RMSE with baseline. Include training-only calibration and *separate* scene-family evaluation; no selecting candidate based on private heldout outcome without documenting and rerunning a fresh untouched split. Gate remains off unless it beats the no-change benchmark. Also verify stationary environments do not hallucinate effects, changed sensory channels retain learning, data corruption atomicity, migration and mortality, eight historical regressions and Android assembleDebug.

**Success levels:** (1) lower real heldout prediction error (calibrated forecast); (2) enough out-of-sample gain to pass conservative planner eligibility; (3) **independently** better actual hidden-energy retrieval/survival on new unseen habitats with no costlier harm. Each level reported separately; a pass in (1) is *not* evidence for (2) or (3).

## Gate D selective change model — tested on pinned real saved brains, 2026-10-12

### Implementation
- Existing `PredictiveWorldV20` upgraded to **selective `pw21` state** while preserving the optional external save key `predictive_world_v20`: per-action/per-visual-feature count and *training-only* physical-change frequency (16 actions × 20 sensed channels), regularized residual weights, and a conservative unchanged-state prior for unsupported action-feature pairs.
- Spectral prediction is also limited by actual sensed spectral-band visibility, preventing a blank, unobserved patch from acquiring an imaginary wavelength. No hidden entity identity, goal position, free energy or physics rewrite. Original `SensorimotorMind` stays unchanged except for an explicitly sandbox-only pre-existing A/B advisory switch (always OFF in `GameActivity`).
- **Transactional backward migration** from `pw20` to `pw21`: retain historical model weights/counters, reset only uncalibrated new readiness to zero until real experience supports changes. Reject malformed, NaN and unsupported payloads without overwriting the last intact state. Restore, death/resume, spectral-zero/no-change behavior and heldout isolation have independent regressions.
- TWO distinct, **pinned** actual telemetry profiles: 18:38 Italy report `20261012-163858-48e97da70734`, and newly shared 22:27 Italy report `20261012-202752-55671d9a33fd`. Earlier "latest" references were mutable and risked comparing different brains; archived `zero-telemetry/history/` report IDs now fix each experimental version. Each profile uses 14 training scenarios and 24 heldout matched A/B trials (12 development layouts + **12 previously unseen layout/seed combinations**), total 48 A/B pairs. All recorded via physical bilateral covers, real finite source contact and the previous strict grip restrictions.

### Quantitative results, no false success claims

Verified independent experiment: [GitHub Actions 38304623281](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38304623281), **4 jobs SUCCESS** (two fixed archived brains × two layout splits). All differences in this table are real trained sensory model RMSE on the corresponding 14-scenario training experiment's heldout decision windows; the second split tests downstream behavior without retraining.

| Archived real ZERO checkpoint | Before selective change (where separately measured) | Selective `pw21` prediction error | Unchanged-state baseline | Confidence | Energy/survival A/B |
|---|---:|---:|---:|---:|---|
| 18:38 / `163858` | 0.052731 | **0.047851** | **0.047759** | **0** | 0 wins, 0 losses, 24 ties |
| 22:27 / `202752` | *not measured on old model with same archived weights* | **0.182019** | **0.182899** | **0** | 0 wins, 0 losses, 24 ties |

The 18:38 model's error dropped about **9.3%** relative to its earlier model version but remains *0.19% worse* than the trivial unchanged-state prediction. On the 22:27 saved brain the new predictor is **0.48% better** than unchanged-state, far less than the strict ~8% heldout margin required for safe planning. **0 actions were influenced, no matched scenario found an additional hidden energy cover, no actual survival advantage.** Zero eligible plans and zero sufficiently confirmed long-term skill options.

This was iterative development: the old historical split has already informed debugging; it is not independent proof of generalization. The fresh heldout layouts/seeds were never used for training, but share the same heldout RMSE estimate from the training experiment. Do not conflate test families or pretend two identical RMSE numbers are 2 independent sensory-model validations.

### Research decision
**Do NOT promote, do NOT merge PR #31 or publish a new OTA.** Runtime mobile performance and real 105-minute survival remain unverified. Further work needs (a) actually predictable delayed physical state features beyond the coarse 26 aggregated senses; (b) attribution of real bilateral grip, object movement and delayed source contact; (c) unseen life-length physical benchmarks that improve energy without extra death or hidden reward. A predictor that merely approaches or equals no-change is not a demonstrated increase in intelligence.

## Gate E — real-effect temporal attribution (pre-registered, 2026-10-12)

**Problem:** selective pw21 improved predictive RMSE but still does not reliably beat the unchanged-world reference and never caused energy or survival improvement in 48 authenticated frozen-brain paired trials. Additional speculative depth without real actuation-effect evidence is not useful.

**Experiment before implementation:** a bounded, optional `PhysicalConsequenceV20` that observes only (i) ZERO's chosen motor action and *own tactile/adhesive sensor readings*, (ii) a genuine nonnegative **observed external-body displacement effect** already emitted by real contact physics, and (iii) genuinely **received** energy changes after physical contact. It learns whether actual exterior movement tends to follow self-actuated contact, and whether energy tends to follow recent **real** exterior movement within a short window. It must never look up object IDs, concealed resources, true source coordinates, procedural tags or scripted "open a box" actions. A link across events is temporal evidence, NOT proof of causality (e.g. movement of an unrelated object must remain ambiguous). Wrong/quiet cases are as important as positive cases.

**Design controls:** compare action+own-touch empirical changes to the **same-contact contextual baseline** on independent heldout decision intervals. Minimum support/alternative trials, conservative abstention and confidence zero unless significant predictive gain. Persistent learning in an optional separately versioned key; transactionally parse with strict finite, count and range checks; death/save/reset isolation. Shadow-only integration: no motion policy bias, no reward shaping and no code disabling the two-sided red-grip law.

**Mandatory critical benchmark fix:** the prior 128-second real-cover laboratory fed a literal `physicalEffect=0` on every learning step even when the actual integrated cover physics had moved objects. This is a fidelity bug in **research fixture only**, and needs correcting with measured actual **world displacement after physics**. Do not manufacture energy or treat any impulse as a successful uncovering. Verify the new observer gets actual case-level physics feedback in the arena and exactly the same physical event stream reaches baseline/control without new decision influence.

**Success conditions:** (a) pass isolated falsifiable synthetic intervention/no-op/holdout/corruption tests; (b) 96-site real-cover sandbox benchmark with trained latest and archived checkpoint, actual exterior movement counted, repeatable stable outcomes, exact parity with shadow observer; (c) demonstrate out-of-training effect prediction improves the same-context no-change baseline. Only after (c), and independent actual positive energy/survival results on another untouched split, consider learned causal advice. No APK publication or stable merge on compilation alone.

## Non-negotiable invariants
1. No destroying/mutating real ZERO brain; no copying historical profiles into production automatically; optional legacy default safe.
2. No privileged semantic target coordinates or scripted correct actions in learning code.
3. No invented energy, speed, grasp force, physics override or fake goal success.
4. Holdout is truly excluded from weight updates; counters never masquerade as evaluation victories.
5. Old saved keys untouched; new optional keys versioned; failure atomic; explicit resets isolated.
6. Tests include negative cases (virgin model no influence, corruption, death/resume, nonfinite input, unexpected state, out-of-distribution uncertainty).

**Interpretation rule:** Passing Java unit tests or compiling Android does not prove emergent intelligence or phone runtime parity. Benchmark full-life generalization before claiming any capability improvement.

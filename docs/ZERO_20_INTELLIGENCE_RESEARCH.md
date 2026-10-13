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

## Gate E — real contact/physical effect attribution: implemented and measured, 2026-10-12

**Experimental source commit:** `028b73e45ea9172167ce35a3fe1f0872fac28967`; protected by independent draft PR #31. **No stable code, live save, main release, or OTA modified.**

### Changes and integrity

1. Added optional `PhysicalConsequenceV20.java` (`physical_consequence_v20`), a small bounded empirical model of **chosen actual action + own touched/held surface context → observed external movement**, and temporal proximity of physical movement followed by **actual** positive/negative resource contact. It never receives object identity, hidden resource locations, cover semantics or learned "correct" motor commands; it never changes action score, actuator, physics or reward.
2. A genuine measured external movement is **not** a tap/press command, a contact attempt, or an impulse with no displacement. Its independent action-conditional movement predictor is evaluated against the *same tactile-context* general reference on heldout decision intervals. Confidence is **zero** without enough independently supported actions and clear error improvement. Delayed energy with one recent movement is labelled **temporal association**; multiple possible movements remain **ambiguous**, not a proven cause.
3. Full optional model serialization with finite/count bounds and all-or-nothing recovery; pending unfinished movements discarded on death, scene/saved-profile resume. Reset lives in `CAUSAL` only; motor, world, self-body and other skill domains unchanged. Brain dashboard shows real observed displacement, energy proximity, ambiguity and calibrated confidence, explicitly **SHADOW**.
4. Repaired a consequential error in the **headless lab**: `Zero20CoveredEnergyBenchmark` previously always passed `physicalEffect=0` into the sensorimotor learner, even while its physics could move and uncover boxes. It now records **actual position updates caused by a prior correctly held cover's velocity** and mimics the live game by crediting externally verified actual uncover displacement. Nothing rewards merely issuing a grip command. Both control and experimental agents receive the same correct physical effect stream. The real game already recorded external effect, so this fix is to the research fixture, NOT a production physics change.
5. Preserved all prior fixed-source historical build lists by extending them to include the new class; no assertions removed. New regression checks action specificity, absence of movement on mere red/touch, real energy ambiguity, holdout exclusion, nonfinite/truncated profile rejection, death/resume and **440** exact original-policy parity actions.

### Actual CI evidence — successful and negative where applicable

- Entire true-cover replay: [GitHub Actions **38306317255**](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306317255), **4/4 jobs green**. 2 fixed, privately archived saved brains (18:38 and 22:27 local) × 2 layout/seed splits × 12 heldout paired trials = **48 comparisons**. Physical-cover solver includes 96 body contact points, strict opposite real grip, concealed finite resource. Neither alternative receives a scripted route.
- New learning/cognition/Android regression: [**38306317222**](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306317222), **PASS**, includes `PhysicalConsequenceV20Regression`, 440 original-model decisions, reset isolation, lost/corrupt saves, old contact/grip QA and Android `assembleDebug`.
- Full historic pull request QA on source commit `028b73e45`: [0121 independent](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321800), [0122 ecology](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321813), [0125 telemetry](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321851), [0126 single-red grip](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321775), [0127 recovery](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321824), [1169 observer](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321901), [new B/C/E](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38306321786): **all passed**.

| Frozen original ZERO checkpoint | External physical effects observed in each 14-scene *training* lab | Recorded actual movement → energy proximity in training | Heldout predictive sensory error vs unchanged state | 24 heldout A/B |
|---|---:|---:|---|---|
| 18:38 Italy (`163858`) | **95** actual external movement observations | 0 | 0.095126 vs 0.095045 | 0 wins / **24 ties** / 0 losses |
| 22:27 Italy (`202752`) | **327** actual external movement observations | **1** temporally associated energy contact, *not proven causal* | 0.206494 vs 0.208619 | 0 wins / **24 ties** / 0 losses |

Why are model error numbers different from the last paper section? Previously the lab fed **zero** to the old cognition for every physical change, causing different future actions and sensed states. The fixed experiment has materially different *correct* training/trajectory feedback. Comparing predictive RMSE before/after this lab fix as though it were a pure weight-ablation result would be **invalid**. The old v21 versus no-change reference on this fixed action stream is still narrowly worse for the 18:38 brain and only ~1.0% better for the 22:27 brain, below the existing conservative deployment gate.

These positive event counters are **not** evidence of more food or better manipulation; the same real physics ran in baseline and experimental groups. The new physical consequence estimator still has **zero empirically certified influence** in real saved-brain runs and the v2 planner remains abstaining. Real prolonged survival, heldout cause-to-effect advantages and on-phone runtime parity **not yet demonstrated**.

### Next experiment; no arbitrary release

Use *actual* relative-geometry, tactile force and short temporal sequences to estimate whether a **newly learned physical-change predictor** beats "no movement" and same-contact baselines on untouched heldout habitats; compare matched episodic energy/survival when (and only when) this predictive gate is satisfied. Preserve control, red holds and the user's current brain. **Do not publish ZERO 2.0** on the basis of this observational foundation alone.

## Gate F — learned bodily grip configuration → verified external effect (pre-registered 2026-10-12)

**User goal:** ZERO should discover which of its own self-generated material configurations are associated with genuinely moving an obstacle and later transfer that knowledge. Do not inject hidden box ID, physics law, opposite-face legality, favorable target coordinates, or canned manipulator strategy into its policy. The existing strict TWO true red opposing grips for energy covers and ONE true red grip for construction panels stay unchanged.

**Candidate shadow model:** use only ZERO's own 96 membrane sites: tactile contact, actual sustained hold, learned local grip command, radial extensions and *rotation-invariant circular dispersion of legitimately held sites*. Separate no-contact, contact-only, held concentrated versus distributed material configurations with conservative empirical counts and a same-contact baseline. A hold is NOT inferred from visual overlap, button presses or `gripDrive`; use actual physics-owned and composed `gripHold`. Learn binary real exterior displacement after executed action intervals, not grip commands or predicted velocities. Preserve evidence across death; pending partial intervals are discarded after app restore. Serialize a separate small schema and reject malformed/nonfinite/corrupt values atomically; scoped manipulation-only reset.

**Predeclared tests:** synthetic rotations of 96 sites, unilateral concentrated vs distributed grips vs touch-only, quiet/no movement, action-specific intervention, entirely disjoint heldout updates, denial of unsupported confidence, corruption/legacy-save/mortality, at least 440 paired old motor decisions unchanged. In the real deterministic 96-site headless covered-energy arena, compare two archived original brains on historical and previously unseen layouts; log train vs heldout real movement events and confidence against same-context baseline. **No scoring or motor effect.** Predictive improvement must not be confused with an autonomous energy/survival advantage: no release or planner activation until unseen-world matched A/B outcomes show benefit and zero regressions.

## Gate F — 96-site bodily grip affordance, implemented and tested 2026-10-12

**Experiment source version:** `e9bd97ce527985311a3a083b30a721a67aa6713d` on dedicated non-release branch `feature/zero-20-hierarchical-intelligence`. Previous phase Gate E and the currently installed OTA99 untouched.

### Actual implementation
- `GripAffordanceV20.java` (optional isolated save key `grip_affordance_v20`): records ZERO's OWN 96 soft membrane samples as tactile contact, its actual local red actuator commands, physically confirmed membrane holds, neural extension and circular dispersion of held tissue. Seven deliberately low-complexity, rotationally invariant bodily states: no contact, contact but no actuator, local command without sustained hold, concentrated held compact/extended, dispersed held compact/extended. Importantly **neither the evaluator nor the learner** uses concealed-resource coordinates, object labels, legal opposite-face results, or scripted correct actions to classify a self-grip.
- Observes one pre-selected own-body configuration at the decision boundary, the actually self-chosen action and a later actually measured nonnegative external-object displacement effect from existing physics. Learns an action-conditioned physical-movement probability and compares against an identical-body-context reference on truly heldout action intervals. **Any press/grip without measured movement is a NEGATIVE example**, never food or success.
- Sampling **currently occurs at the 0.65-second decision boundaries**; grips shorter than this may be missed. This is a REAL accuracy limitation, not evidence that ZERO doesn't perform transient opposite grips. A future passive interval trace of actual 96-site contacts with correct event timing should be evaluated before assuming the self/body policy cannot learn manipulation.
- Fully optional versioned atomic serialization, old/missing keys safe, malformed/nonfinite values rejected, pending action not fabricated after death/slot restore; manipulation-only reset. Brain dashboard now reports lived valid-hold configurations, dispersed actual tissue patterns, observed motions and heldout confidence. **No policy score, physics force, grip law or reward was changed**; original app has no active v2 grip steering. The existing GameActivity old brain/profile/12 named slots remain unchanged in stable branch.
- Regression on virgin, actual held vs attempted red grip, distributed/concentrated/extended 96-point shapes, equal shape rotated, negative/no-op, deliberate motor intervention, strict heldout exclusion, atomic corruption/death/resume, **440 byte-identical original brain and motor decisions** passed. New `GripAffordanceV20Regression.java` is now part of all future dedicated shadow QA. Fixed Java source lists were extended to include new classes; no historical physics assertion deleted.

### Reproducible runs

- Dedicated Gate A–F Java tests, 440 parity decisions, full original physics/grip regressions and Android `assembleDebug`: [GitHub Actions **38307503834**](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307503834) **SUCCESS**.
- True hidden-energy bilateral-cover headless replay: [GitHub Actions **38307494459**](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307494459) **4/4 jobs SUCCESS**; two frozen private brain profiles × two disjoint layout/seed splits, 12 matched pairs each, **48 A/B comparisons**.
- Historical PR physics/CI on same source: [0121](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307503987), [0122](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307503883), [0125](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307503922), [0126](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307503799), [0127](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307504026), [1169](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307504129), and separate [shadow push](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307494449): **all SUCCESS**.

| Frozen actual ZERO profile | Bodily states with a real held red grip across 14 training episodes | Of those, genuinely dispersed across 96-point body | True external-object movements in those episodes | Heldout Brier error for shape-conditioned prediction | Same-touch/body-context reference error | 24 matched hidden-cover A/B |
|---|---:|---:|---:|---:|---:|---|
| 18:38 report `163858` | 555 | **14** | 95 | **0.040419** | 0.042831 | 24 ties |
| 22:27 report `202752` | 1,169 | **20** | 327 | **0.130012** | 0.133779 | 24 ties |

Interpretation: learning self-grip configuration improves heldout predictive accuracy by approximately **5.6%** and **2.8%** versus the same-body-context reference, respectively; this is a genuine modest *offline* model gain. But it does **not** meet the conservative 6% evidence threshold, yields **zero certified confidence**, cannot be used to alter ZERO's policy, and does not establish genuine transferable causal control. Only **14** and **20** dispersed held-site patterns among hundreds of held-body observations were recorded by the coarse decision-time sampler. A positive association between surface disposition and later externally observed motion might still be confounded by other objects or self-motion.

ZERO2 performs **0 wins / 0 losses / 48 ties** versus its original brain in headless unseen/historical cases because both control and intervention remained the same physical decision policy (no eligible advisory). *Do not* represent this as survival improvement, a real-time phone test, or a learned way to open a cover.

### Go/no-go

**Gate F exploratory model integration PASSED, but deployment/autonomy improvement gate FAILED.** Keep PR #31 in draft; no OTA, no stable merge, no motor/adhesion intervention, no changes to user's installed learning. Next: collect physiologically true transient 96-site hold distributions with correct sub-decision event timing, then test whether learnable action-contingent configuration changes can be reproduced in new physical habitats without scripts or privileged environment state. Require independent 105-minute-compatible survival/energy A/B evidence before any new OTA.

## Gate G — capture sub-decision real grip transients (pre-registered 2026-10-12)

**Measured defect:** Gate F sampled the real membrane only once per ~0.65 simulated seconds (versus 0.035 s physics step). The 18:38 / 22:27 checkpoint experiments saw only **14 / 20** dispersed, legitimately held shape samples within **555 / 1,169** held snapshots. This is an observation-frequency limitation; it is not proof of a defective physical controller or lack of real transients.

**What to implement:** Extend the optional `GripAffordanceV20` with a bounded, alloc-free **per-physics-step observational microtrace**. Record the 96 simultaneous local red commands, touch values, per-mass genuinely composed held timers and local neural extension **after real collision/hold composition**; never union held sites across different instants to fabricate a distributed grip. Within each actually selected action interval, track microframes, any real hold, any *simultaneous* dispersed holds, max simultaneous held masses, and strongest learned self-state. Use those within-interval lived bodily features to inform the model's outcome readout at action completion. No physics changes or advice. Keep old `ga20` model payloads restorable, optional independent persisted aggregated counts, no pending partial fabricated after death/resume.

**Methodological caveat:** This establishes **co-occurrence during an actually executed action interval**, not a proven pre-intervention causal predictor; a microstate late in an interval could follow an early external effect. The model must continue to be shadow-only until true time-aligned cause/effect separation and an unseen-world heldout improvement are independently demonstrated. A single physical frame must never be interpreted as a completed long-lived grip or energy reward. App and headless lab must call the same sampling method at the same place in their physics step.

**Acceptance:** (1) independent one-frame 35ms ephemeral-distributed-grip example missed at 0.65s but detected at substep; false press-only/no-hold case remains negative; (2) 96-point simultaneous-set invariant and rotational invariance; (3) exact model + stats roundtrip/migration, deliberate corruption rejection, death/resume, no allocation-per-site in hot loop; (4) 440 unchanged original selected decisions and 48 true-cover A/B holdout repeats on two archived brains; (5) original physical/grip/ecology/Android QA all green. Report exposure vs confidence separately, no release or merge until actual energy/survival improves.

## Gate G implementation ledger — 2026-10-12 (committed, independent static checks passed; GitHub runner BLOCKED)

### Production-neutral changes implemented

- Optional `GripAffordanceV20` upgraded from `ga20` to atomic `ga21` **inside the same optional save key** `grip_affordance_v20`. Old `ga20` model outcomes and learning counts are migrated exactly, with no fabricated historical subframe exposure. The new five bounded counters track genuine physics microframes, microframes with valid simultaneous held material, microframes with distributed simultaneous holds, and actual intervals containing newly emerged held/distributed configurations. In-progress intervals do not survive app-restoration as invented successes.
- `SensorimotorMind.sampleGripPhysicsFrame(skin)` reads the physically composed, body-owned `Morphology.Surface` after the existing `GripSurfaceComposition.compose` at each **positive-time** Android physics slice (up to 35ms rather than approximately 650ms between cognitive decisions). The headless 96-site bilateral-cover arena calls exactly the same sampler after the same composing step. Uses only already-present sensed red motor output, actual hold time, touch and neural extension; no object identity, code for legal grip or hidden energy positions; no motor/adhesion changes.
- **Correct attribution:** subframes preserve simultaneous 96-point configurations. They are NEVER unioned across separate times to fabricate opposed/dispersed adhesion. Initial action-boundary tissue is separately stored but not mistakenly counted as a subframe of the following motor action. Zero-time Android draw ticks are not counted as lived frames. While learning is paused, genuine observation exposure may be accumulated, but unlabeled action intervals are not recorded as completed trained/heldout outcomes, preventing a corrupt restored profile.
- `BrainStatsActivity` displays actual microframes with valid grips, simultaneously dispersed grips and action intervals where brief dispersed grips emerged. `Zero20CoveredEnergyBenchmark` reports the same counters alongside old A/B fields; `zero-20-energy-arena.yml` publishes metrics, never the archived user's saved brain. All existing selective reset rules continue to apply.
- New regression assertions (in the existing `GripAffordanceV20Regression`, **not yet run on the current commit**) include: a real simulated 35ms dispersed microgrip that disappears before the action boundary; contact/red commands with no physical hold; two separated single-point holds that MUST NOT become a fictitious simultaneous distributed grasp; backward `ga20` migration; lifecycle/paused-learning atomicity; nonfinite/corrupt new counters; original 440 same-policy decisions; unchanged physics and Android tests. Existing GitHub QA `zero-20-shadow-qa.yml` now explicitly checks both real-game and benchmark frame sampler hooks.
- 13/13 independent **source/static integration assertions** manually executed in the connected GitHub session: correct microframe method, simultaneous 96-point dispersion, no oracle source reads, migration, interval alignment, real-game and lab hooks, gating, targeted tests, non-steering behavior and dashboard metrics. These are *not* compiled Java results.

### Validation blocker / exact status

The previously observed GitHub runner-start failure persists. For example [38309152646](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38309152646) and [38309152667](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38309152667) marked failed with **0 executed workflow steps, no assigned runner name**; prior retry [38307907608](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307907608) also failed at initialization. No evidence of Java/assertion execution in these runs. Thus, **the new ga21 microtrace code is not yet CI verified, no current Android APK build has been confirmed, and new 48-pair A/B numbers are not available**. No hidden success claim or weakened historical test was made.

**Last independently completed successful code suite remains Gate F** [38307503834](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307503834) and old 48-pair archive [38307494459](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38307494459), which do **not** test this new microtrace. Historical old-sample counts: 14 and 20 dispersed held poses, not microframes.

### Next strict go/no-go

Once GitHub can assign a runner: run Gate G unit tests and full original physics/Android QA. Then run pinned archived original ZERO 18:38 and 22:27 brains on both historical and fresh cover habitats; compare **actual simultaneously held microframe and fleeting-action exposure**, correctly time-aligned action outcomes, calibrated conditional prediction vs same-grip-context reference, and actual concealed energy plus mortality on 48 paired worlds. If a new shape observer is merely more complete, that is NOT proof ZERO makes smarter moves; policy remains original unless independent active-advice trials win on unseen conditions. Keep pull request draft and stable OTA99/real device memories untouched.

## Gate G follow-up: correct brief-grip semantics, runtime efficiency, and GitHub runner diagnosis — 2026-10-12

Latest experimental source changed as follows:
- 96-site circular grip dispersion now uses **precomputed** sine/cosine tables; the 35ms hot loop allocates no per-frame objects and no longer calls trigonometric functions for every valid held point. This improves performance predictability on the Galaxy S21 but is **not a measured phone benchmark**.
- Previously the `fleetingSpreadIntervals` name incorrectly counted any distributed grip *first seen after an action started*, even if it persisted. Now `GripAffordanceV20.physicsFrame()` recognizes a **genuinely transient** grip only when an actual held configuration is observed and subsequently observed **released within the same selected-action interval**. Two single-point holds at different times cannot be combined into a fake simultaneous distributed grip. New regression contrasts an actual `grip → release` within ~35ms physics steps against a grip persisting across frames, plus the existing tests for virtual red press and corrupted/old saves.
- No active ZIP/APK upload, user brain alteration, hidden reward, motor override, wall collision alteration or tweak to one-red-panel/two-opposite-red-box rules.
- **GitHub billing/action-block suspicion only**, not confirmed: dozens of consecutive workflows in this **private** repository end with `conclusion=failure`, `steps=[]` and `runner_name=""` in 2–5 seconds. This includes [38309355597](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38309355597). GitHub billing documentation explains included Actions-minute quotas in private repositories; similar account-level spending limits or failed-payment holds have caused zero-step failures. However the available GitHub connector **cannot read private account billing/runner entitlements**, so actual cause remains undetermined. Account owner should inspect GitHub **Settings → Billing and licensing → Usage / Budgets / Payment information**. Do **not** buy minutes or increase spending without explicit agreement. A local Google-hosted/preview builder cannot substitute for full Android regression parity.
- An additional 13/13 static source/invariant checks were performed on the previous version; the current source includes further transient-semantics changes, so this is **not** the same as Java compilation or trusted physical A/B outcomes.

**Gate remains blocked**: no Gate G new-run regression success, no 48-pair fresh microframe A/B results, no actual on-device benchmark and no demonstrated survival or energy advantage. Stable OTA99 and all user-created lives and named slots remain untouched.

## Non-negotiable invariants
1. No destroying/mutating real ZERO brain; no copying historical profiles into production automatically; optional legacy default safe.
2. No privileged semantic target coordinates or scripted correct actions in learning code.
3. No invented energy, speed, grasp force, physics override or fake goal success.
4. Holdout is truly excluded from weight updates; counters never masquerade as evaluation victories.
5. Old saved keys untouched; new optional keys versioned; failure atomic; explicit resets isolated.
6. Tests include negative cases (virgin model no influence, corruption, death/resume, nonfinite input, unexpected state, out-of-distribution uncertainty).

**Interpretation rule:** Passing Java unit tests or compiling Android does not prove emergent intelligence or phone runtime parity. Benchmark full-life generalization before claiming any capability improvement.

## 2026-10-12 — GitHub Actions cost/duplicate-run gate

**Cost-control implementation only; not a validation of Gate G.** The private owner's Actions quota reached the 2,000-minute included cap while repeated GitHub Actions runs failed before any runner allocated. No spending, billing upgrade, release, OTA or user-brain write was attempted by these changes.

- Keep the stable and original branches untouched. On the ZERO 2.0 experimental head, six historical pull-request QA workflows (passive grip, independent baseline, shelter, multipart telemetry, red grip and spatial recovery) **skip only when the PR head is `feature/zero-20-hierarchical-intelligence`**. They remain runnable normally on other PRs and via supported manual triggers.
- The single `.github/workflows/zero-20-shadow-qa.yml` workflow now runs on **pushes to this experimental branch**, not a duplicate pull_request trigger. It compiles the same Java model set and the full Android debug APK only once, and performs **58 Java class regression invocations**, preserving the previously separate legacy tests including `TelemetryChunksRegression`. It still verifies the original-policy/non-interference assertions.
- Nonfunctional documentation/script-only pushes are excluded from the automatic shadow build. A branch-scoped concurrency group with `cancel-in-progress` stops obsolete queued/running shadow builds after quick successive pushes. This saves repeated builds but does not substitute for a completed final-head test.
- The expensive four-job pinned-private-snapshot, 48-pair bilateral energy-cover A/B workflow no longer auto-runs whenever cognitive/physics Java changes. It can be requested **deliberately** by creating/updating `docs/ZERO20_ARENA_REQUEST.md` on the experimental branch (or by workflow_dispatch if GitHub permits dispatch for this workflow). The marker is a CI trigger only, not a report export or a change to ZERO's learning.
- This trade-off deliberately separates rapid regression (on each code push) from heavyweight statistical A/B (before any promotion). A/B trials remain **mandatory** before claiming actual energy/survival improvement or proposing integration.
- All edits were made to the experimental branch, still behind draft PR #31. No personal saved weights are baked into a public repository or APK. No change to installed OTA99 or to physical grip/energy rules.
- **Validation status:** Static workflow/source inspections and GitHub updates completed. Observed GitHub PR workflow conclusions `skipped` on the experimental head confirm elimination of duplicate runner allocations. Java runtime regressions, actual Android compile and new Gate G A/B remain **unverified until a runner is allocated**. A skipped check is not a passing test.
 
## 2026-10-12 — Gate G post-upgrade verified CI and 48-pair physical A/B

**Actual execution evidence, not just static code review.** First consolidated Java/Android run [38314214696](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38314214696), head `92dd9ef8a5e339fd9f0b8fe211b06682c18aca84`, completed **successfully** after account owner upgraded GitHub Actions entitlement. JDK17 + Gradle setup, compilation of the Java regression source set, 58 individual Java regression invocations (including all earlier legacy suites), source non-interference assertions and Android debug `assembleDebug` all reported **success**. Six separate duplicate PR workflows were correctly skipped. No APK was released, installed, OTA-signed or distributed.

Explicit heldout simulator gate run [38314359632](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38314359632), head `63f19848a31d618268559cfc042f33e39bd0ad38`, completed **successfully in all four jobs** on two pinned private-brain snapshots and two splits each (12 matched pairs per job; **48 total**). The later commit only added a CI marker doc and did not change executable learning code.

| Private snapshot | Split | Pairs | Wins / losses / ties | Changed paths | Candidate eligible plans | World confidence | Grip confidence | Mean energy / survival / damage delta |
|---|---|---:|---|---:|---:|---:|---:|---|
| original-1838 | historical | 12 | 0 / 0 / 12 | 0 | 0 | 0 | 0 | 0 / 0 / 0 |
| original-1838 | fresh | 12 | 0 / 0 / 12 | 0 | 0 | 0 | 0 | 0 / 0 / 0 |
| later-2227 | historical | 12 | 0 / 0 / 12 | 0 | 0 | 0 | 0 | 0 / 0 / 0 |
| later-2227 | fresh | 12 | 0 / 0 / 12 | 0 | 0 | 0 | 0 | 0 / 0 / 0 |

Additional diagnostic outcomes (same on each split of a given snapshot):

- **original-1838:** 2,150 observed training actions; 538 world/grip heldout samples; 51,184 35ms microframes, 10,275 held-grip frames, 272 simultaneously spread-grip frames, 56 fleeting spread actions, 95 observed physical movement events, **0 observed linked-energy events**. World heldout prediction error `0.095126` vs unchanged baseline `0.095045` (worse); grip error `0.041331` vs baseline `0.041714` (slightly lower, but still zero usable confidence).
- **later-2227:** 2,150 training actions; 538 heldout samples; 51,184 microframes, 21,925 held-grip frames, 318 simultaneously spread-grip frames, 86 fleeting spread actions, 327 physical movements, **1 linked-energy event**. World error `0.206494` vs unchanged baseline `0.208619` (slightly lower); grip error `0.120599` vs baseline `0.120159` (worse). World and grip confidence both stayed `0.00000`.
- Both snapshot groups: `corroboratedSkills=0`; `candidateEligiblePlans=0`; no model-driven behavior change. Thus this validates **physics-frame instrumentation, preservation of the original policy, and reproducibility** but shows **no empirical energetic or survival advantage**. Ties are expected for a shadow-only architecture; this is not a success of intelligence transfer.
- **Promotion decision: BLOCKED.** No claim of generalization, autonomous causal understanding, improved full-life survival, or better on-device performance. The A/B arena has genuine, finite, hidden bilateral-cover energy sources, but does not simulate the full continuous Android ecosystem/season/phone runtime.
- **Research priority next:** increase quality and attribution of *real source-confirmed energy and verified physical consequences*; inspect confidence-gate calibration and rarity of linked-energy training signals; distinguish insufficient evidence from code faults. Design a small, strictly gated advisory experiment **only after** heldout predictive confidence improves and independent ablations can prove better genuine energy/survival outcomes, with default shadow and immediate fallback. Do not compensate by inventing reward, forcing the red grip or bypassing obstacle physics.

All user device saves, learning profiles, stable OTA99 and original decision policy remain unchanged. No new OTA.

## 2026-10-13 — Long-survival diagnostic and recommended on-device passive study

Two read-only heldout stress jobs completed successfully in [GitHub run 38315010450](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38315010450) with a *diagnostic-only* 170s scenario. Initial reserve 40, continuous drain 0.31/s, theoretical no-contact depletion 129.032s. The standard 128s research baseline and Android code remain unchanged. Both stress-test jobs used pinned historical private brain profiles, 12 paired fresh heldout runs per profile.

| Pinned snapshot | Pairs | Deaths CONTROL / ACTIVE | Mean survival seconds CONTROL / ACTIVE | Covers CONTROL / ACTIVE | A/B outcomes |
|---|---:|---|---|---|---|
| later-2227 | 12 | 5 / 5 | 152.9325 / 152.9325 | 3 / 3 | 0 wins, 0 losses, 12 ties |
| original-1838 | 12 | 6 / 6 | 149.5200 / 149.5200 | 0 / 0 | 0 wins, 0 losses, 12 ties |

This confirms the original 128s benchmark was censored before normal no-contact death and did not measure discriminative survival outcomes. Extending the window reveals mortality without revealing benefits from ZERO 2.0, because candidate planners continue to abstain (eligible plans 0) and the ACTUAL motor policy is preserved. The later profile's learned grip error was 0.163783 vs baseline 0.170128 (only ~3.73% improvement), and predictive world error 0.157824 vs unchanged 0.156495 (worse). The original profile had grip baseline error 0.003041 (below the minimum 0.035 reliability floor), and world error 0.032850 vs baseline 0.032680 (worse). Neither can justify the required trust gates. Results prove neither incapability nor advantage, only insufficient, non-generalizing predictive evidence.

**User-proposed next research direction:** collect long, organic actual Android gameplay with ZERO 2.0 in strictly PASSIVE/SHADOW learning mode and explicit opt-in telemetry upload. Experimental GameActivity already constructs and persists all five v20 observers without changing normal `SensorimotorMind` action scoring; the normal production flag remains `zero20ExperimentalAdvice=false`. Existing `TelemetryActivity` and `TelemetryGitHub` already implement a user-initiated private GitHub `zero-telemetry` multipart report upload, which copies present working-profile values (including v20 keys) but is not a live direct ChatGPT message. Integrate only after Java/Android build, migration and performance QA with existing phone profile preservation; establish targeted aggregate event-attribution diagnostics instead of uploading huge raw per-35ms frame arrays, with resource evidence and confidence calibration, and verify no new private data is inadvertently sent. Leave old OTA stable and save slots intact until an explicit test build is approved.

## 2026-10-13 — Passive Android export implementation (pre-OTA)

The user requested running ZERO 2.0 on the real Galaxy S21 **while leaving the original organism completely in control**, then manually uploading a private diagnostics report to ChatGPT through the existing GitHub `zero-telemetry` route.

Implementation on experimental `feature/zero-20-hierarchical-intelligence` only:
- Five existing v20 models (`PredictiveWorldV20`, `TransferSkillsV20`, `FuturePlannerV20`, `PhysicalConsequenceV20`, `GripAffordanceV20`) remain attached and independently persisted in `GameActivity`. Device initialization now explicitly calls `sensorMind.enableZero20ExperimentalAdvice(false)`; A/B advisory remains a lab-only experiment. Original action scoring/forces/energy rules are not modified.
- `Zero20PassiveSnapshot.fromSaved(Map)` builds a compact, derived, read-only, **model-weight-free** diagnostics digest from a snapshot of the active working-profile preferences. It flags missing vs invalid optional v20 saves, totals and validation errors, specific heldout-gate confidence blockers, planner abstentions and gating, grip microframe observations, physical displacement evidence and actual energetic contacts with their ambiguous temporal associations. It makes **no causal or intelligence claims**.
- `TelemetryGitHub.report` attaches this digest under `zero20Passive` beside the existing complete `currentZero` payload **only on explicit user upload**. A consented private GitHub report is not a direct ChatGPT message; the user still asks ChatGPT to fetch the uploaded snapshot. Named slots, device identities and GitHub tokens remain excluded by the existing reporting policy.
- `TelemetryActivity` explains passivity, evidence and deliberate manual upload. No new background sync, raw 35ms frame log, simulator-driven phantom reward or special-case auto-grip.
- `Zero20PassiveSnapshotRegression` verifies virgin and corrupt optional saves, truthful status/reason reporting, raw weight exclusion from the compact digest and no mutation of the saved profile. The consolidated Java/Android workflow compiles and runs this regression and checks the hard device boundary before assembling an APK.
- **Production/OTA gate:** PR #31 remains draft; no merge, signed release, install, OTA publish or overwrite of the current phone brain. Even when the CI is green, actual Android runtime, preserved user slots, real-game FPS/CPU impact, upload consent and network behavior still require physical device testing before promotion.

### Verified private Android passive export build — 2026-10-13

- Consolidated GitHub Actions run [38315627020](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38315627020), commit `f49712cc8e9c815911d5e5d180f9391e36560780`, **completed success**.
- All 59 Java regression invocations, including `Zero20PassiveSnapshotRegression`, passed; source assertions confirmed the consented `zero20Passive` field and the explicit Android `sensorMind.enableZero20ExperimentalAdvice(false)` hard boundary.
- Full Android debug `assembleDebug` completed successfully with the experimental observer/report code. The subsequent commit only updated research documentation.
- This **does not** validate phone install/signature, 12 existing save-slot migration, real-device performance, or actual phone-to-private-GitHub upload. Those must be tested before a user-facing OTA. The branch remains draft and unpublished; no current ZERO brain, saves or OTA altered.

### 2026-10-13 — Verified officially signed private Android preview for supervised OTA99 upgrade test

- Requested and executed **NON-PUBLISHING** private GitHub Actions workflow `.github/workflows/zero-20-private-signed-preview.yml` on head `70018d7ace0b0cfdccfc2f3c62b3f139c7b48695`: [run 38316532904](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38316532904), **success**.
- Read stable production OTA99 manifest from repo's `main`: `com.overmind.zero.ota` versionCode **1099**, build name `0.11.6-ota.99`; immutable official stable APK SHA256 **`02c6670305764209eb6d3daff9805a33a131a264e2bc2a917925b1f8f19376f6`**. CI fetched *the actual saved stable APK*, verified hash and verified certificate directly.
- Reused production private signing bundle **in ephemeral private runner only**, built `assembleRelease`, and separately checked **matching signer cert SHA256 fingerprint** and identical Android package ID/versionCode against OTA99. Private key never sent to user, APK artifacts, source or logs.
- Built preview file `ZERO20-PASSIVE-SIGNED-PREVIEW-1099.apk`, SHA256 **`32e1a3aa4440f60737203548ad5db89da78b52da516c5b2e27a31dd068f63616`**. Artifact ID **11777555524**, private download [here](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38316532904/artifacts/11777555524), ~329 KB ZIP and ~signed APK, expires **2026-10-15**. Artifact is **not** OTA manifest, GitHub Release, and is not installed automatically.
- By using exact same versionCode 1099 the preview permits normal stable next higher code update path; Android package installer may allow replacement with same code and matching signature but the outcome must be checked on the actual Galaxy S21. Named slots, actual live model persistence, real-world frame rate and private upload are **unverified on the phone**. If a package manager refuses same-version replacement, **do not uninstall**; investigate safe update instead of losing data.
- Before voluntary trial, user should save a **named slot** and ideally explicitly upload a backup profile through the existing private GitHub report. Verify the named slot after upgrade and test normal motion, control isolation, new v20 metrics and explicit upload after ~20 min of autonomous play.
- Original PR remains draft, no merge, no stable OTA publish, no simulated extra reward, no altered motor policy and no changed user data as a result of CI operations.


### 2026-10-13 — First real Galaxy S21 ZERO 2.0 upload inspected (00:42 Italy)

Read-only review of private multipart report `20261012-224248-5cc40039bfbc` stored at `zero-telemetry/current.json`, timestamp 2026-10-12 22:42:48 UTC (00:42:48 Italy). **All five indexed JSON parts exist and parse; source=ANDROID_EXPLICIT_SHARE.** Real phone profile retains the older motor/hand/membrane and five new v20 saved models. No raw motor or private model weights copied into these notes.

- V20 `pw21` counters: 7,941 transitions; 6,352 training, 1,589 holdout, 1 invalid; heldout error 0.108519 vs unchanged baseline 0.107927 -> prediction not better, confidence 0.
- V20 `ga21`: 7,941 observed intervals, 10 physical motion events, 2,411 actual held-grip snapshots, 94 simultaneous spread snapshots, 320,138 microframes, 83,239 held microframes, 2,512 spread microframes, 349 fleeting spread intervals, 1,589 holdouts. Error 4.58e-5 vs no-change 5.43e-6; moving-event evidence insufficient and prediction not better -> confidence 0.
- V20 `pe20`: 7,941 intervals; 10 physical-motion events, 48 genuine energy contacts; **0** temporarily linked to one recent observed movement, 0 ambiguous, 48 without recent displacement; 1 invalid sample. Physical heldout error 2.71e-5 vs no-change 4.82e-6; causality not demonstrated.
- `fp20`: 994 shadow plan checks, 0 eligible, 994 abstained. This is correct fail-closed behavior and does **not** mean ZERO 2.0 is already improving control.
- `tv20`: 11 observed lives, 7,953 decisions, 46 good energy and 2 harmful events, 367 candidate occurrences, 32 retained motif candidates. Evidence is not proof of corroborated skills.
- Compare prior user report `20261012-202752-55671d9a33fd` (which lacks the v20 keys): base motor trials **+312** (4,838→5,150), base accepted **+109** (1,766→1,875), finite energy contacts **+92** (1,497→1,589), moved objects **+15** (1,261→1,276), hidden covers uncovered **+1** (175→176), explored cells **+513**, real damage credits **+10**. These increments provide strong profile-continuity evidence, not a device-level guarantee. Surviving app session and manual export also confirm basic launch/network function, not battery/FPS behavior.
- **Real defect detected in first upload:** `TelemetryGitHub.report()` produces compact `zero20Passive` correctly, but `partition()` used a hardcoded extras whitelist and silently excluded it. The entire raw saved `pw21`, `ga21`, `pe20`, `fp20`, `tv20` values were nevertheless present, so no experiment data lost. Fix committed to experimental branch: include `zero20Passive` in multipart `extras`, with source-guard assertion in the consolidated CI. No OTA currently published for this patch, and user phone stays on signed preview until a verified, explicitly approved stable OTA build.
- No brain reset, physiology change, motor advice activation, data upload from our side or alteration of user saves. Recommended next: continue passive in-vivo evidence; improve causal credit availability and confidence calibration scientifically, rather than manually making the actor smarter or unlocking trust gates by decree.

**Multipart diagnostics fix validation:** [consolidated run 38318470069](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38318470069) **success**, including pure Java regressions, explicit source assertion for `extras.zero20Passive` in the archive partition, original policy/physics non-interference, and full Android debug compilation. Not deployed; next user-facing report will include the compact digest only after a separately published OTA.


## 2026-10-13 — Official passive telemetry fix released via in-app AGGIORNA (OTA 102)

Owner requested the same normal in-app updater workflow used for OTA 1–99, not manual APK installation. We reviewed the repository's `AGENTS.md`, `OVERMIND_START_HERE.md`, `OTA_BOOTSTRAP.md`, `docs/WHITEPAPER.md`, `ARCHITECTURE.md`, `CHANGELOG.md`, `ROADMAP.md`, `HANDOFF.md`, `UPDATES.md`, and all available `EXPERIMENT_*.md` research logs.

- Selected subset of previously verified ZERO 2.0 PASSIVE observers and private multipart telemetry fix isolated in PR [#32](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/32) from the stable OTA99 base, distinct from draft research PR #31. All 59-test consolidated Java suites / full Android debug passed [38319633924](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38319633924). Legacy PR workflows with outdated direct javac class lists are branch-scoped skipped; they stay applicable on unrelated branches. The authoritative consolidated test covers v20 model regressions and a static check that `TelemetryGitHub.partition` forwards `zero20Passive` under `extras`.
- The merged stable publisher kept the preexisting 0.11.6 pinned SHA1 checks, updating five hashes to precisely the audited changed source files; never bypassed these immutability checks. Its old causal reset assertion had to be updated for the additive `CausalExperiments.KEY,PhysicalConsequenceV20.KEY`. The new v20 motor-lock checks were moved after Python `game` and `mind` source initialization. The first two attempted publisher runs (100, 101) halted before signed release because of these test harness assertions; neither modified the production manifest.
- **Official stable publisher [run 38320268844](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38320268844) completed SUCCESS** (publisher sequence 102), including all legacy signed gates, source pins, full signed `assembleRelease`, updater Android manifest verification, private-key signing and GitHub Release creation. New released identity: `com.overmind.zero.ota`, `0.11.6-ota.102`, **versionCode 1102**. GitHub Release [overmind-zero-v0.11.6-ota.102](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.102), asset `OVERMIND-ZERO.apk`, 322820 bytes.
- New GitHub main `updater/public/latest.json`: SHA256 `89c30159b3318e3f7cac2050a49103e1b3b327a885ff0a75145ed77c3a323254`, immutable app URL `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1102-89c30159b331.apk`. Vercel authenticated production inspection reports **READY** for main commit `878878ab1af298a492e08aff9d72495b3b320fb7` with production alias `overmind-zero-updates.vercel.app`. Direct external HTTP probe from analysis environment was unavailable; therefore do not assert that exact endpoint returned HTTP 200 here. The device's updater also independently reads the GitHub `main` manifest as fallback and verifies SHA256 before install.
- **User action required**: open existing Galaxy S21 ZERO app and press **AGGIORNA**, confirm Android install over previous OTA99/preview 1099. Do not uninstall or reset. Save named slot/private report before installing as precaution, verify all saved profiles and behavior still present, continue collecting genuine phone experiences and confirm `extras.zero20Passive` appears in newly uploaded multipart report. Phone runtime migration and uploaded report are **pending user test**, not CI assertions.
- The experimental RED advice branch remains disabled by `sensorMind.enableZero20ExperimentalAdvice(false)`. This is **an instrumentation/telemetry release, not evidence that ZERO gained intelligence** or that the observer can steer behavior.


## 2026-10-13 — 11:46 Europe/Rome real OTA post-fix telemetry audit

Read-only review of the newest explicit Galaxy S21 private multipart report `20261013-094611-80f78afb4123`, timestamp 2026-10-13 09:46:11.850 UTC, 5/5 JSON parts fetched from `zero-telemetry`, all part byte lengths, reported key sets and report identifiers consistent with index, 74 current-profile keys plus `extras.gripContactObserver`, `extras.lastBehaviorLab`, `extras.zero20Passive`. This is the first >10-hour follow-up on OTA 102 report `20261012-230854-5e30b1441b1a` (elapsed 10.622h wall clock), providing strong evidence the multipart digest fix is working. Uploaded SHA256 values are present in the index but no local cryptographic hash verification was done in this pass.

**Current vs prior actual phone observer digest:**

| Field | 01:08 Italy | 11:46 Italy | Interpretation |
|---|---:|---:|---|
| World transitions | 14,696 | 44,574 | +29,878 actual observed transitions |
| Holdout predictions | 2,940 | 8,915 | +5,975; still no trusted world model |
| World heldout error | 0.13914235 | 0.16143344 | Baseline also changed: 0.13969200 → 0.16384926; gain over no-change ~0.39% → ~1.47%, **well below required 8%** |
| Grip heldout Brier | 0.000022457358 | 0.00011419298 | Unchanged-action baseline 0.000003587779 → 0.00007363504; learned model **worse than baseline**; no grip confidence |
| Physical motion events | 16 | 333 | +317 micro-level displacement events, NOT necessarily +317 newly moved boxes |
| Physical energy contacts | 90 | 379 | +289 observed contacts |
| Single recent-motion energy associations | 0 | 15 | New temporally associated examples, **not proof of causality** |
| Ambiguous recent-motion energy | 0 | 2 | Cannot reliably attribute |
| Energy with no recent motion | 90 | 362 | Still 95.5% of 379 contacts, strongly limiting causal credit |
| LifeTransfer lives | 19 | 50 | +31 lives; 287 real positive and 92 negative energy events |
| Stored candidate motifs | 32 | 32 | Capacity cap reached; 4 independently corroborated (previously 0) |
| Future planner checks / eligible | 1,839 / 0 | 5,578 / 0 | **Motor advice remains blocked**, all 5,578 abstained; world/grip/physical confidence remains 0 |

**Important skill-quality caveat:** decoding the 4 confirmed motifs from `transfer_skills_v20` reveals **three nested repeated-action patterns for the same reduced action group** (length 3, 4 and 5, with stored action examples mostly 11), plus one repeated action-8 length-3 pattern. They are **not four independent, complex, multi-step navigation skills**. Confirmation criteria require at least two training lives and one heldout life, but no causal energy baseline or motor-policy uplift has been demonstrated.

**Original ZERO working-profile continuity** (compared with previous index): attempts 5,290→5,906 (+616), accepted 1,929→2,136 (+207), finite energies collected 1,629→1,830 (+201), old cumulative moved-objects 1,282→1,317 (+35), concealed square uncoverings 176→241 (+65), explored cells 18,356→19,074 (+718), room successes 4→6 (+2), collisions 4,561→4,689 (+128), real damage credits 1,325→1,413 (+88), grip events 608→749 (+141). Gate travel remains 76.00612 (no increment); the running original organism still exhibits measurable exploration and collection but none of this is attributable to v20 advice. No evidence of reset in the live working profile.

**Contact observer diagnostic (session-scoped, NOT lifetime-equivalent):** `gripContactObserver` changed session `life=1` prior to `life=12` now, so don't compare totals as matched durations. New BOX totals: 521,710 touch frames, 333,362 frames with adhesion on, 3,880,932 held patch-points and matching orange points, 64,918 adhesion-on frames with no orange patch. Observer records 147,261 interrupted held-point events, split into 144,068 **geometry/contact-face no longer matching** and 3,193 **same-face** drops. This is a promising diagnosis of why some grabs visibly disengage, but not evidence of erroneous physics by itself: a moving or rotating plate naturally changes faces. Determine spatial conditions and consequent outcome with controlled replays before modifying collision law. Positive motion events and uncovered squares support real activity.

**Internal consistency checks:** 35,659 training + 8,915 heldout = 44,574 world transitions; 15 single-linked + 2 ambiguous + 362 unlinked = 379 actual energy events; 287 positive + 92 negative = 379 energy events; planner 0 eligible + 5,578 abstained = 5,578 checks. Report claims `controlMode=SHADOW_ONLY` and `liveBehaviorChange=false`; source still explicitly disarms v20 motor advice. These are counters and config boundaries, not exhaustive runtime proof of non-interference. Continued shadow-only collection recommended; next priority is auditing geometry drop causes and energy-credit attribution while rejecting unearned confidence/skill claims.

NO user saves, OTA version, actuator policy or GitHub telemetry data were modified during this report analysis.


## 2026-10-13 — Root cause investigation: successive box grip success then decline

### Device evidence from two explicit 5-part private reports
Compared the October 13 11:46 Italy `20261013-094611-80f78afb4123` snapshot to October 13 01:08 `20261012-230854-5e30b1441b1a`.

- The stored **hand controller changed all 18/18 neural weights** (RMS difference ~2.09675), and the **membrane controller changed all 82/82** (RMS ~1.43843); unchanged save/slots were not deleted.
- During this interval the online learner accepted **233 of 611 new hand candidates (38.1%) and 271 of 612 membrane candidates (44.3%)**. Acceptance is real, but these counts alone do not prove the newly selected variants caused performance deterioration.
- Original ZERO still accomplished +35 total moved objects, +65 physically uncovered energy boxes, +201 actual finite food contacts; these aggregate lifetime counters do NOT show per-life grip success, nor isolate scene difficulty.
- In the newer session-scoped grip observer `BOX`: 521,710 frames with box touch, 333,362 adhesion-on frames, 64,918 adhesion-on frames with no red/orange held point (~19.5%), 147,261 interrupted individual point holds: 144,068 (~97.8%) attributed to changing face/geometry and 3,193 (~2.2%) same-face. These are individual sample-point events, **NOT** 147,261 complete grasp failures, and their frequency does not establish erroneous collision physics without replay/controls. `SquareCollisionSolver` revalidates penetration and 96-node membrane links, appropriately clearing illegal holds. That geometric effect may contribute to brief grips; no changes to collision, friction, red threshold or bilateral confirmation are justified yet.

### Verified missing protection in incumbent online learning
The shipped `OnlineBodyLearning` evaluates sequential baseline vs mutated HAND/MEMBRANE configurations using travelled path, net translation, exploration, generic contacts, actual energy, and generic external displacement. It has no requirement that a replacement preserve a previously successful bilateral-cover grip. An improvement on OTHER live activities can therefore replace proven effective adhesion/membrane coefficients. `GameActivity` archives/restores `GripSkillBank` exclusively from genuine *gate* holds and gate recovery failures, not from genuine square-cover successes. Thus the online evaluator cannot reliably protect successful manipulation at boxes; the user's report that it sometimes succeeds repeatedly and then performs worse is consistent with this real architectural gap, **not proof** of deliberate forgetting or of zero20 shadow intrusion. The phone's ZERO 2.0 remains non-actuating.

### Experimental mitigation (NOT released to phone)
On isolated, draft research branch `feature/zero-20-hierarchical-intelligence`, maintain 2 counters per decision interval: one genuinely contact-bearing square physics subframe, and one with a confirmed opposing-face hold; count **one per subframe** regardless of the number of boxes. These are exposed to `OnlineBodyLearning.observe(...,physicalEffect,realBoxTouchFrames,realOpposedGripFrames)`, with previous overloads intact.

Only when an incumbent HAND/MEMBRANE evaluation window has **≥8 true box-contact frames and ≥3 true opposing-grip frames** do we require the candidate window to demonstrate comparable opportunities (≥8 frames and ≥half the baseline) and held frames (≥3 and ≥60% of baseline). Otherwise reject that candidate **even if** it appears better at unrelated navigation, random energy collection or novel cells. MOTOR trials and naive initially unproven manipulation remain trainable; no reward for touching, no new preprogrammed grip, no unilateral movement of bilateral covers, no change to core physics or motor action policy. Existing saved controllers and 12 user slots stay structurally compatible; transient frame counters clear only at real new life/decision, never fabricate old evidence.

`BoxGripRetentionRegression` exercises a test-only scenario in which baseline HAND/MEMBRANE grip is physically evidenced while a more exploratory candidate lacks proof, plus checks that equal proven grip and initially unproven hands can still advance. **This is a regression of the evaluation rule, not a physically realistic full-life A/B of the phone.** The first CI exposed and fixed a premature baseline-counter reset in the experimental patch; the corrected test and pre-existing Java invariants passed during research QA [38380835217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38380835217). The final research workflow completed **success** on [38380835217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38380835217), including all consolidated Java regressions, non-interference checks, the new guard regression and complete Android debug \`assembleDebug\`. Build success is not yet evidence that genuine user gaming improves.

### Remaining causal gaps and next gate
A baseline window **without** a box may still lose a formerly effective manipulation configuration. The present mitigation protects short-term *observed* grip in two sequential windows; it is **not** durable archive/replay of box-specific technique. A future full fix should explicitly store successful physically witnessed HAND+MEMBRANE combinations with evidence of bilateral holds and real displacement (separate from gate records), rank by heldout repeated success in comparable contact opportunities, and allow evidence-triggered rollback without forcing movement or hallucinating goals. Before production OTA, design heldout, same-brain paired phone physics replay and gather in-app per-trial proof-rejection counters to confirm real reduction in lost grip with unchanged energy/survival, rather than selecting based on anecdotal sessions.

**Production OTA102, installed user profile, sign/manifest, saves, and user device have NOT been modified as part of this investigation.**

## 2026-10-13 — Box-grip retention correction officially published as OTA103

User authorized the normal in-app AGGIORNA workflow instead of leaving the verified physical-grip patch on the research branch. PR #33: https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/33 . Staged only the targeted OnlineBodyLearning.java, GameActivity.java, BoxGripRetentionRegression.java, updated source SHA pin and appropriate JVM/Android QA. The full research branch remains isolated.

Staging consolidated regression and Android debug build PASS: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38382838735 . Prior direct-javac PR jobs that lack the new classes were skipped for this particular staging branch; the complete suite tests real box bilateral physics, save profiles and strict v20 shadow-only motor disarming.

Official signed stable publisher PASS: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38383039538 . GitHub release: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.103 . Identity com.overmind.zero.ota, versionCode 1103, file OVERMIND-ZERO.apk (324076 bytes), SHA256 39c4f1107618447b806232c31dc67e21dc45e1a520fffdd2efa115844c0261f3. Public main/updater/public/latest.json now advertises OTA103. Vercel deployment dpl_AHB41G9u56npPTh2i5FmQcb7WfvG from main commit 57ae1a4e60cf04bb80a4c2343913e8b2a63df25b reports production READY for alias overmind-zero-updates.vercel.app.

Limit: patch protects a real proved bilateral grip only within a two-window hand/membrane incumbent/candidate comparison (>=8 true touch subframes and >=3 actual opposing held subframes, candidate must have comparable evidence). It is NOT durable cross-life archival and retrieval, does NOT invent physical grips, change bilateral cover laws, energy, move direction, model trust, previous brain serialization or named slots. It remains to validate the actual on-device installer and sustained box manipulation on real gameplay after upgrade. No A/B proof of improved long-term survival yet.


## 2026-10-13 — Gate H, first passive perceptual-association prototype (research-only)
User proposed autonomous associations of **seen shape → candidate kind of thing → lived motor/grip experiment → remembered physical effect**. Implemented the first strictly passive and falsifiable prerequisite in distinct branch `feature/zero20-perceptual-associations`, based on PR #31. See `docs/ZERO20_PERCEPTUAL_ASSOCIATIONS.md` for architecture, test coverage, explicit unsolved scientific gates and no-promotion conditions.

- Perception uses only the existing 16 physically occluded membrane-origin distance rays. Circularly aligned local depth contours yield up to 24 unnamed, uncertain prototypes; contradictory/disjoint close surfaces trigger abstention. This is not a full polygon detector, RGB camera, persistent object tracker or autonomous concept of a *box*. Environment object IDs, correct actions and hidden energy remain unavailable to the learner.
- The model observes the self-chosen real 16-way action and a later externally measured displacement; absent movement is a negative example. This is only scene-level action–effect correlation until perception, tactility and **the same moving object** can be aligned. Real energy contacts are counted separately without pretending they were caused by a nearby shape.
- Optional versioned `pa20` state follows active working save/named slots; death preserves learned hypotheses but drops pending action; heldout samples do not train. New passive digest and brain readout expose categories, ambiguity and genuine effects without raw weights in compact report.
- Strict original decision policy/physics preservation: no actuator advice, no new reward, no special hand code, no editor of phone profiles, no signed OTA. The **current stable OTA103** and draft research PR #31 remain separate.
- Next: prove multi-view shape grouping, true instance-specific physical attribution and durable technique retention, then heldout predictive improvements and real matched lifetime energy/survival. All claims of smarter behavior remain blocked until A/B and device evidence.


### Confirmed QA of first passive prototype — 2026-10-13
**Actual consolidated GitHub Actions run:** [38391024117](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38391024117), executable-source SHA `3621aedc7447d16286c903671593a72aba29288c`, **SUCCESS**. It compiled the full preexisting Java model set plus `PerceptualAssociationsV20` and its dedicated regression; passed the old body, grip, gate, plates, memory, save, privacy/telemetry, reset, physics and decision tests; verified 440 unchanged choice decisions and static absence of any new observer policy score/physics override; built the complete Android Debug APK with `assembleDebug`. Earlier staging run `38390903157` failed only the overly broad source-only assertion that also excluded passive `chosen()` bookkeeping; assertion was narrowed to forbid view/training/scoring within the action-choice policy and the final run passed. No bypass of any real physics test was used.

**What is not verified:** realistic rotation and 3D shape recognition on the Galaxy S21, same-instance tracking, independently proven causal manipulation, long-lived technique retention, battery/thermal overhead, installer signature, unchanged 12 slots after actual upgrade, and real behavioral benefit. **Promotion remains BLOCKED.** Release/OTA103 unchanged.

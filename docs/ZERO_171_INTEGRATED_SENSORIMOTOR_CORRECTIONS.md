# ZERO 171 — integrated correction, OTA publication authorized

2026-10-16. Branch `fix/zero171-integrated-sensorimotor-corrections`, based on unpublished ZERO170 `249563fd`. Installed/public baseline remains **OTA169 / 1169**. The user has now explicitly authorized source and signed APK OTA publication: “Si autorizzo. Pubblica anche apk tramite ota”. Release checks and stable-source promotion are in progress; do not claim publication until the served manifest and signed bytes are verified. The runtime revision 171 is independent of the next sequential OTA number.

## Scope and evidence

The complete active route is reviewed: live sensory geometry → sole 2.2 action selection → 724 motor weights → actual 96-shell + 74-interior material → physical collisions/contact → learned consequence → optional persistent archives. Historical brain/core keys, 12 named slots, app identity and signing config remain unchanged. No reset of the user's brain, authored gait, food vector, automatic object grip, forced confidence or synthetic success is introduced.

| Audit finding | Implemented correction | Verification |
| --- | --- | --- |
| Liquid sees rotated thin panels as squares | Shared `SymbioteWorldStepV171.interior` dispatches construction plates before bilateral covers; live game and new lab call it | Actual production dispatch test: real tip excluded, empty offside untouched, rotation |
| Vision/depth/occlusion sees wrong panel or skips it | Oriented panel containment; continuous rectangle/circle ray intersection shared by proximity, distributed vision and spatial rays. Room scans <=1U, below physical wall thickness; actual hit cells registered | A 14U plate between old 22U samples is detected; rotated tip/offside agree |
| Concave body projection points toward outside centroid | Use actual oriented inward edge normals, then polygon containment verification; nearest edge first, exhaustive fallback only for degenerate vertices | U-shaped membrane fixture previously leaves a parcel outside, now inside; old material invariants |
| Low reserve cancels action duration | Hunger alone no longer cancels the 2.2-selected 1/2/4-interval motor option; actual harm, exploration and stronger neural alternatives still interrupt | <16% reserve persistence plus real harm interruption; old duration tests |
| Exogenous movement trains direction muscles | Same per-frame measured active impulse + world-contact/disturbance flag gates slow and fast directional learning/holdout labels, not only skill archival | External-contaminated real gel trial cannot change any motor synapse |
| Motion model stale after posture or motor change | Optional `ContextualNavigationV171`: per-action real posture contexts, independent train/holdout displacement checks, exact motor RMS epoch guard. Old in1 spectral valence/history retained, not used as current symbiote motion proof | Incompatible posture/controller vetoes old trust; matched model learns; atomic malformed-save rejection |
| Body energy value learns on heldout intervals | Delay four body-model direct energetic value updates to the actual train boundary, including terminal contact; no heldout label reaches those updates | Actual `SensorimotorMind.contactOutcome/observe` heldout integration test |
| Stationary successful manipulation not archived | Separate optional manipulation bank under `symbiote_manipulation_skills_v171`. Only measured object displacement while really held by its own legal solver; not body distance or discovery bonus. Same contextual, distinct-life validation and reversible recovery rules | Production neural head actually moves a held plate; its actually executed weights archive across two later heldout lives. Same event cannot certify ground locomotion |
| FPS-dependent physics, exploration and spin | Normal x1/x5/x20 and TEST MAX all integrate 1/60s; fractional clock remainder retained; OU motor exploration and archive angular integration use actual dt | 30/120Hz give identical tick counts; zero-delta resume no normal tick; old muscle regressions |
| Old tests stand in for endogenous learning | Both Android lab entry points select current `SymbioteLaboratoryV171` for a symbiote; clones same 2.2/muscle/sensory checkpoints and same physical scenarios, frozen vs online learning | Deterministic real policy→head→gel run; frozen synapses unchanged; trained synapses genuinely update; no live preference mutation |
| Legacy predictor diagnostics mislabeled active | A separate active diagnostic journal evaluates actual pending recurrent 164D forecasts before training; old 26D journal is preserved separately. Temporal 26D model gets the matching 26D active-predictor baseline | Static dimensional integration checks plus active/core regressions |
| Missing provenance and confusing counts | Telemetry reports actual installed version/code and runtime revision in single and multipart reports; UI shows 724 total motor weights, distinct manipulation bank, honest confidence-zero meaning | Android compilation, JSON metadata forwarding/source checks |
| Stale documentation | Current status block prepended to entry docs and all canonical papers, linking this contract; older history remains explicitly superseded | Handoff and diff review |

## Persistent state and migration

- Existing ZERO170 `ms170` under original motor memory key is preserved, including legacy 169 quarantine and recovery rollback. New manipulation bank uses the same validated bounded archive implementation in a separate key, without touching locomotor weights at load.
- Contextual motion checkpoint `cn171` stores bounded per-action context cells and one actual 724-weight reference; changing motor RMS >0.004 starts a new validation epoch, preserving stored historical cells but preventing obsolete verification from influencing the policy.
- New optional keys, including `active_recurrent_diagnostics_v171`, are loaded AND saved by GameActivity, captured automatically by generic named slots, and covered by their proper selective reset domains. Old slots without them start with empty *additional* models. No old core, muscle, statistic or spectral association is deleted.
- Sensor confidence zero is an abstention state, NOT a broken wire. No confidence threshold is removed to manufacture activation. Scientific prediction trust still requires independent real observations.
- Continuous ray changes alter physical observations; all old predictor weights remain but future predictions/heldout results determine their influence.

## Tests are evidence with limits

`Zero171IntegratedRegression` tests physical geometry, production dispatch, actual active cortex force provenance, low-energy persistence, 30/120Hz clock, heldout energy, contextual persistence, a genuine neural-controller manipulation archive, and isolated endogenous neural simulation. Historical fixtures 167/168 now pass their **actually measured** material impulse; assertions remain unchanged. The older causal-map fixture now visits the **actual continuously observed former solid cell** rather than the hidden disk center.

Passing those tests proves the corrected mechanisms execute coherently. It does **not** prove autonomous learning is faster or that ZERO will reach energy on the Galaxy S21. An 8s virgin online trial can be worse than its frozen partner; report measurements, never suppress them. Laboratory comparison reports net/gross, signed chosen-direction progress, energy, actual pickups, actual object movement and invalid counts. It excludes world streaming/rendering/weather and says so. On-device same-save validation after an eventual single user-approved update remains mandatory for any behavioral improvement claim.

## Validation status

Local JVM historical suite: 94/94 passing after correcting fixture inputs, plus new integrated regression PASS (95 entrypoints in staging QA). Integrated manipulation benchmark: actually commanded neural membrane moved the held plate 16.284U over three independent lives, archived the same executing motor synapses, no locomotion skill fabricated. Endogenous virgin 8s comparison: frozen net27.130/gross32.339, online net23.023/gross29.410; **not evidence of uplift**.

Full local Android API35 Java compilation: **152 source files PASS**, including the complete application, activities and regressions. Seven staging Python integration gates PASS. This is source/API compilation, **not a Gradle-packaged or signed APK**.

The automatic approval reviewer blocked the branch push to the public GitHub repository, treating source publication as conflicting with the instruction not to publish, and stating the destination had not been verified as trusted. Subsequent read-only connector metadata confirmed this is the user's existing repository with admin/push permissions; no alternative write route was used. No GitHub CI run or remote branch exists for this correction. Commit and all verification remain local pending explicit authorization for source-branch upload. Never merge stable/main or publish an APK/OTA implicitly.

Read-only `origin/main/updater/public/latest.json` remains `0.11.6-ota.169`, code1169, SHA256 `fee775106adc55485d39ad8722a683a6ae345d467398d7f83757e24f0c55c599`; app/build.gradle, updater and publisher are unchanged from the base.

A second screening reconstructed the real explicitly shared report `20261016-135319-17a313abf3e2c`, verified all 14 shard hashes and all82 working keys, and cloned only its neural/controller data into identical fresh physical scenarios. A195s free-resource arena produced movement but **no pickups**; the online learner did not outperform frozen synapses. This finite controlled run does not establish a new code defect or long-term learning impossibility, but it explicitly prevents any claim that autonomous navigation has been solved.

No on-device installation, packaged APK test, or new phone efficacy is claimed. The frozen laboratory now also disables synaptic archive recovery, preventing a nominally frozen control from silently replacing its motor weights. Named-slot codec roundtrip tests include all three new optional persistence stores.

## Explicit release authorization — 16 October 2026

The latest user instruction supersedes the earlier publication hold and authorizes the source push, stable-source promotion and signed APK OTA. The earlier automatic rejection was respected; no alternate route bypassed it. Retain all 95 JVM regressions, source integration gates, exact reviewed source pins, stable signing identity and updater verification. Release remains in progress until public bytes and manifest match.

Release preparation: all 33 publisher Python source gates passed locally. The publisher now runs the full staging suite plus the previously publisher-only MaterialContactRegression (96 entrypoints), pins the reviewed 170/171 files, preserves all previous safety assertions with updated production expressions, and checks the real signed APK certificate against OTA169 SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583` before any release or hosting write. This fingerprint was independently extracted from the APK v2 signing block of the original committed OTA169 binary.

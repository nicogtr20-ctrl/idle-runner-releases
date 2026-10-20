# ZERO 3.0 — V377: physical inertia-aware causal motor credit
**20 October 2026. Status: EXPERIMENTAL source branch. NOT signed OTA.**

## Why
Actual S21 OTA375 `zero-telemetry/history/20261020-130507-2906bb269cdd6.json`: 14,066 GRU decisions, 3,590 opposite-moving decisions, 3,334 externally corrected decisions, 35.16% net signed progress relative to gross physical travel, old V371 "3/8 verified" directions that could be no more than coasting. User reports an actual balloon: body changes morphology/orientation but COM keeps drifting. V376 original Android material-world experiment [workflow 39429255810](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39429255810) independently confirmed that the first 0.4 sec after a WEST or NORTH command retains EAST movement even when body is able to reverse/turn over 11.2 sec. Fixed-heading-from-rest old QA did NOT test mid-motion turning.

## Actual code bug and bounded new model
V371 `Zero3InstrumentalCreditV371` bins direction into 8 unconditional average physical displacements; its heldout reference predicts **zero movement**, regardless of PRE velocity. Thus the model can mark a direction "verified" if body simply keeps drifting. The old V371 data are retained in checkpoints for history, but no longer allowed to decide optical motor heading. V377 is inserted *inside the existing V353 cognition of the only GRU64*, not a new actor/controller:
- `Zero3InertialMotorV377`: 8 commanded sectors × 4 physical pre-motion regimes (still, same, opposing, cross). Reads only real PRE velocity sensors 191/192 with original V356 ×130 normalization, original PRE actor candidate and actual physical POST COM displacement. The null is *straight-line continuation of PRE velocity* over the same 0.30s normalized time, not an imaginary stationary body.
- On clean training only, learns measured displacement-minus-inertia residual. Heldout trials independently measure if it predicts that residual better than constant-velocity continuation (minimum 5 train and 3 heldout; heldout MSE <80% of continuation error; baseline nontrivial). Wind, red-terminal interrupted motion, external solver contamination are censored.
- For a motor regime which HAS independent trustworthy evidence, rank a candidate's learned color consequence along its **predicted material displacement**, including inertia; otherwise retain only a weaker unverified optical-intention hypothesis. V353 one-step actor, V354 planner and V375 safe option gate use the same physical estimate. No hidden object classes or pre-programmed steering/velocity thrust/target.
- Explicitly do not claim PRE velocity × elapsed is a perfect passive dynamics comparator; material drag and collision can also change velocity. This is an improved *first-order inertial continuation baseline*, NOT a controlled zero-muscle A/B. Further matched coast controls and 10k-decision survival remain acceptance requirements.

## Preserved capabilities and compatibility
- GRU64 weights, V353 3-head model, V354 executive memory, V319 grip, V369 wavelength valence, V371 archived 6 PRE visual/POS material traces, V374 exact touched optical surface, V375 checkpoint up to goalAge24, V370 weather/solid split, V372 frozen motor probe, original SymbioteMatterV1, 96-skin/74 internal physical masses, 12 named brain slots ALL retained.
- Binary checkpoint: OLD V353+V369+V371 bytes stay an **unchanged prefix**. Append **OPTIONAL V377** 4-byte schema + 16 counter bytes + 32 fixed 32-byte cells. Old checkpoint loads with zero-prior new model. New checkpoint roundtrip verified on real Android QA. No deserialization reinterpretation and no forced neural reset.
- Phone observability adds `verifiedInertialContextsV377` (actual trusted speed-heading cells), `cleanPhysicalTurnsV377` and `disturbedPhysicalTurnsV377`. The old `verifiedPhysicalDirectionsV371` historical value remains visible but no longer determines motor choices.
- V377 QA includes passive coasting *must fail skill verification*, partial braking *must not be mistaken for moving toward beneficial WEST*, true reversal, environmental contamination, checkpoint migration. Original dynamic body turn V376 and all prior V375/V374/... regressions required.

## Acceptance gates before ever claiming solved
1. Actual original Android test and all previous regressions pass on newest head.
2. Heldout physically clean movement in at least the *currently moving opposite and transverse* regimes, both +/− signs and control over 0.4–2.0 seconds. Demonstrate moving-body turn, not just rest-start travel.
3. Matched passive coast over same initial COM velocity and body configuration, plus storm/gate test.
4. Independent 2k then 10k blind-world original Android runs show measurable improved physical survival, blue pickup conditional on visible blue, and lower red-contact rate **conditional on exposure**, against same version baseline.
5. Phone APK OTA only when user requests compatible experimental app. Preserve same package/cert, monotone version >1186, no data wipe; do not claim behavior improved without phone evidence.

**Do NOT merge, publish OTA, or erase neurons because source-level tests pass.**
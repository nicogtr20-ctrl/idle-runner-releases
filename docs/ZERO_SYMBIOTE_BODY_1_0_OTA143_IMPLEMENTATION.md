# ZERO Symbiote Body 1.0 — REAL implementation in signed OTA143
Documented 15 October 2026. **Physical/neuromotor alpha integrated into the Android game**, not an image or future-only plan.

## Distribution and provenance
- GitHub PR #47 merged into `feature/zero22-active-recurrent-ota117` at `292e859b05d106aa440e4d996e1dc0a4b225bc53`.
- The physically important equal/opposite active-cortex mass reaction was corrected at `ac48bd0ba1679e75f02595efd203c4422279f5c1`.
- QA JVM + Android debug passed workflow run `38647390672` (the original 16-direction ZERO 2.2 primary, all old physics regressions and new symbiote fixture).
- Official signed publisher workflow `38648335238` SUCCESS; original package `com.overmind.zero.ota`.
- Published Android `0.11.6-ota.143`, versionCode `1143`, APK `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1143-a5acce3b97f8.apk`, file size `415564` bytes, SHA-256 `a5acce3b97f895387990cb1ab05db190a01303a9288da12efcf1a8e779ad5c8f`.
- GitHub Releases creation endpoint returned HTTP403 on previous run due workflow integration permission. OTA143 used a verified signed Actions build artifact and the Vercel-linked `main` branch primary mechanism; a distinct live HTTP SHA-256 verification is launched by the production verification workflow. A GitHub release fallback for OTA143 must not be claimed unless separately established.

## Actual material physics and honest scope
- **`SymbioteMatterV1`**: physical 96-point perimeter, 64 additional internal viscous parcels, and the previous 10 `MotorBrain.Body` nodes repurposed as mobile **liquid interior** parcels. The centroid remains a read-only average, never a simulated central mass/attachment point. No 10-node elastic radial locomotion is executed for Symbiote mode. The existing 96-point membrane `Morphology.Surface` still owns true contact geometry.
- Real discrete 2D active-cortex forces on 16 sectors, local tangential shear, neighbor curvature/tension, damping, bounded area-preservation constraint, soft nearby-particle repulsion and pairwise momentum-conserving **XSPH-style viscosity**. Constraint-based local self-intersection guard reverts only contested material to the previously legal state if the proposed physical step is impossible. Internal cortex reaction uses both real shell and interior mass weights (corrected before OTA143).
- `SymbioteMatterV1Regression` exercises passive area stability, baseline finiteness, 75 independent commanded extension steps followed by 140 passive relaxing steps, no self-crossings, one sector must extend more than the opposite, reduced whole-body drift, trained head save/load and non-mutating failed restore. Laboratory commands ARE NOT a neural policy, reward script or hardcoded game behavior.
- This solver is an **XPBD-inspired quasi-incompressible fluid prototype**: it does NOT implement a full validated SPH fluid density and incompressibility scheme. “Can generate one controlled pseudopod in the laboratory” has been demonstrated; “ZERO learned reliable manipulation, squeezing through every passage, pressure-perfect real liquid or equivalent consciousness” has NOT been demonstrated. No real Android Galaxy S21 thermal/performance benchmark has been executed by the assistant.

## Cortex control and learning
- **`SymbioteMotorHeadV1`** is an autonomous trainable motor readout, **subordinate to RecurrentCoreV22 as sole high-level 16-action chooser**. 16 anonymous sectors × 4 controls (outward/inward activation, lateral drive, local voluntary grip intention, cortex stiffness). It reads real distributed body channels and the actual chosen action, no simulator hidden object identity, resource location, special solution pattern or robot arm labels.
- A separate real-outcome update uses actual positive/negative energy contacts, injury and bounded exploratory action perturbations only, with heldout intervals never updating the motor weights. It is NOT a second brain with competing action authority.
- Adhesion is intentional only if the head outputs a positive local command, and an actual legal physical box/gate contact is independently required by existing collision solver. Gravity/substrate drag is simulated separately and must not be confused with a magically held grip. All reward pickups remain gated through `MaterialGeometry.touches`.
- The learned motor readout is **fresh when a new Symbiote is born**, and so likely clumsy until real experiences accrue.

## Android safety and migration
- Existing `SaveSlotStore` veteran profiles and 12 slots are left on the original elastic physical model. Their active 2.2 brain is unchanged. On the **Partite di ZERO → CREA ZERO SIMBIONTE** action only, the system first creates a recovery copy, then clears the current working profile and sets `SymbioteMatterV1.ENABLED=true` for the NEW virgin body and brain. The user should explicitly save the experienced ZERO to a named slot before switching: the automatic recovery slot is overwritten on later switches.
- Symbiote physical checkpoints attach a versioned `symbiote` object to `GameSceneSnapshot`, including 64 parcel positions/velocities, material rest area and counters. Legacy v1 scenes without material persist exactly as before. Loading a symbiote profile into the original body mode (or vice versa) is rejected rather than silently converting or destroying physics. New motor readout stored in `SymbioteMotorHeadV1.KEY` as a separately validated schema; `rc22` and `cs23` formats preserved.
- `GameActivity` chooses **EITHER** legacy `creature.step/surface.step` for veteran or `symMatter.step` for a new Symbiote body, never both in one frame. x1/x5/x20/TEST MAX keep the existing fixed-step stepping infrastructure. `BrainToolsActivity` displays motor training and restored fluid snapshot diagnostics. `Zero20PassiveSnapshot` reports mode + anonymous aggregate counts without leaking physical state.

## Roadmap after first user experience
1. On Samsung Galaxy S21, verify visual softness, pseudopod independence, screen orientation, true body stability under grip, CPU heat and speed x5/x20/TEST MAX.
2. Export telemetry after 10–30+ wholly Symbiote-only lives, compare with virgin baseline. Record `symbioteBody` + `zero22`, inspect last-life decision trace. In particular distinguish *motor learning shortage* vs *solving collisions and density/topology defects*.
3. Improve the low-level motor learning and local proprioception with heldout comparative control tests; no resetting neural memory to hide bad results.
4. Only if indicated by observed physics, progress to fully validated density-corrected XPBD/PBF and realistic multi-pseudopod manipulation; no fake shortcut actions.

**This paper describes what is implemented in OTA143. The prior design contract remains `docs/ZERO_SYMBIOTE_BODY_1_0_ENGINEERING_PLAN.md`, but not all its aspirational gates have been experimentally proven on Android.**

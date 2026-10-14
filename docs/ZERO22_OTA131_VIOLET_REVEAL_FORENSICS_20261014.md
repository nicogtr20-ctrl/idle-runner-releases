# ZERO 2.2 / OTA131 — uncovered violet resource not reliably approached, verified forensics
Date: 14 October 2026, user-reported in-progress game.
No code/OTA modification done in this investigation: do not claim patched behavior.

## Exact private evidence
Latest committed branch `zero-telemetry`:
- Report `20261014-182328-ebc646c2ded4`, UTC timestamp 2026-10-14 18:23:28, 15 JSON chunks, `current.json` index; user reports sometimes moving a cover crate, revealing a VIOLET gem, then ignoring it.
- Prior report `20261014-164053-e62d161841c1`, 2026-10-14 16:40:53, OTA130 baseline before OTA131.
- Deltas between reports: 6676.785 simulated seconds (~111.28 minutes), finite pickups 681→757 (+76, **11.383/1000s** compared with previous interval's 13.367/1000s), uncovered crates 170→189 (+19, **2.846/1000s** vs previous interval 2.999/1000s), objects displaced 354→403 (+49).
- Real classified source pickups `energy_lifetime_types_v111`: BLUE 518→578 (**+60**), RED 177→194 (**+17**), VIOLET 163→179 (**+16**), legacy unclassified 0. Thus purple pickup ability EXISTS, not categorical blindness.
- Finished lives 75→84 (+9); the nine ended episodes mean 455s (7.58min), 36 positive and 15 negative resource contacts; current unfinished life 2704.65s (**45.08min**), 41 positive contacts, 2 harmful, 15 disk moves, 160 explored cells. Don't compare only completed post lives with previous completed group ignoring this long unfinished life.
- 300s latest decision flight `decision_flight_current_v1162` from sec 2404–2704, 457 actions: `recurrentDelta==0` for ALL 457; epsilon remains .09. Actual control reverted to incumbent, OTA131 safety guard works. Exactly five positive contacts visible in this window, each +22 and **zero +40/+80 rare** contacts; last +22 pickup at sec 2619.6, 84s before report despite movement.
- The 26D observation's signal-2 spectral group has intensity >.12 in ~95.6% of these 457 observations, signal-0 group >.12 in ~61.3%. Visual light does NOT prove that a violet resource is reachable: spectral-2 is also emitted by non-energy movable solids and rare sources mix spectral-0 and spectral-2.
- The historical last completed death flight also shows `recurrentDelta==0`, so persistent ignored resources cannot be blamed on recurrent influence on OTA131.

## Causal sensor-learning mismatch: confirmed source-code fact
- `GameActivity.java` renders `object.isRare()` with purple `0xffb58cff`; a normal hidden-box resource is `UnknownWorld.Entity(...,signature=0, law=4)`, gives +40 actual energy after the box is truly uncovered.
- `SquareGripPuzzle.uncover()` requires two-sided held-box physical displacement >=55 U and toggles `concealed=false`; pickup is NOT automatic. `resolveObjects()` requires REAL skin/body material contact `MaterialGeometry.touches`.
- `SensorimotorMind.Senses.spectral8` and `Senses.read` emit the main `signature=0` wavelength PLUS an additional synthetic-looking but physically visual wavelength `signature=2` for rare resources.
- However, `GameActivity.resolveObjects()` sends `sensorMind.contactOutcome(reserve-before, object.signature, sightAtTouch)`; `InstrumentalNavigation.energyOutcome` increments only `effects[object.signature]` and valence for that single channel.
- Decoded live `instrumental_navigation_v112` checkpoint: `effects=[757,194,0]`, `valence=[+0.95741975,-0.99024713,0]`. **Second wavelength never gets positive energy credit**, despite frequent visibility, because the rewarded rare object is physically signature 0. Blue resource uses signature 0 too. This is a definite missing cross-wavelength attribution in learned navigation, NOT proof that the purple object has no attractive signal at all; it ALSO has signature 0 positive.
- Blindly giving energy reward to spectral-2 would be WRONG because spectral-2 is emitted by ordinary movable boxes/panels/disks that have no energetic payoff.
- The legacy `UnknownWorld.Mind` knows positive signature0 and negative signature1, but cannot tell blue +22 from rare violet +40 solely from object signature, and no persistent causal/route relation from uncovered cover to exposed source is guaranteed.

## Additional independent learning blockers
- `zero20Passive.perceptualAssociations`: 24 tentative categories, 97035 partial surface views, 24305 unknown/occluded views, 33537 ambiguous multi-surface views; **951 real energy contacts NOT ATTRIBUTED to objects**, heldout predictive confidence 0, `objectIdentityVerified=false`, `causalTechniqueVerified=false`.
- `zero20Passive.zero21.objectContinuity`: 38 possible delayed energy links, 913 unlinked energy contacts, 59388 ambiguous ray views, `objectIdentityVerified=false`.
- `zero20Passive.physicalConsequences`: 97 possible single recent move→energy associations but 384 ambiguous multiple-move associations and 470 energy contacts without recent movement; heldout Brier .15237 vs baseline .15367, confidence 0.
- `zero20Passive.physicalGrip`: 7 alternative-action contexts, heldout .193867 vs .192654 baseline, confidence 0; does not prove knowledge of box→reward consequence.
- Therefore model has physical contacts and resource experiences, but lacks validated object identity, temporally delayed reward credit and counterfactual consequence to use the **two-step behavior** reliably.

## Diagnostics the current telemetry CANNOT give
- For each specific violet gem after uncover: exact observed position/distance to each membrane point, whether occluded by moved box or wall, actual near-rim light before uncover, line-of-sight evolution, accessibility/path, number of attempts, latency until collection, and whether the exposed source has since been consumed.
- Recent 300s decision flight sees wavelength sector intensities and gross physical motion, not the identity and location of individual gems. User's observation is credible but the data alone cannot label exactly which particular gem was ignored.

## Engineering direction (NOT YET IMPLEMENTED)
1. Make first READ-ONLY event-paired `boxUncovered → sourceNowVisible → bodyNear → contact/consumed` observer; no privileged source coords fed to the brain. Reuse physical eye/skin measured geometry for user-facing forensics only, bounded retained window, explicit delivery.
2. Learn *joint spectral patterns* and their effect from the organism's own pre/post contact photons, including delayed credit from 96-point object-contact physics; prevent pooling spectral2 from non-energy solids with rare spectral0+2 conjunction.
3. Require heldout validation, matched trials, and explicit abstention before enabling new choice bias. Preserve existing ZERO memory/12 save slots and no hardcoded target/colour.
4. Before shipping APK: JVM source fixture `rare signature0+2 vs harmless box signature2`, invisibility before actual uncover, reward only on true contact, delayed associations, shadow/noninterference parity, Android signer/OTA verification; compare with actual report baseline.
5. Report performance per simulated second and real physical approach after *reveal*, not raw box opens or absolute lifetime totals alone.

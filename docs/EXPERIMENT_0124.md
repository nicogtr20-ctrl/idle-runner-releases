# ZERO 0124 — Construction panels, unilateral contact, cinematic wind

## Design goal
Existing glowing blue square building blocks look like energy-cover crates and
require the same bilateral adhesion. The artistically crude wind is a repeated
rectilinear lattice and crude rectangular cyan wind shadows. This is a game.
It should make the physical wind, rigid structures and body feel legible,
alive and attractive without faking progress or scripted building behavior.

## 1) Real panels, not painted square crates
- Construction remains physical law 6 and inherits existing birth identity,
  saved location, physics memory, absence of food/hidden energy and learned
  sensor signatures. Existing law-5 energy covers remain exactly the old
  opposing-side adhesion puzzle.
- A law-6 material is a genuinely *narrow OBB plate*, thickness 14 world
  units, with one of THREE deterministic lengths (74, 92, 110). Length is
  derived from immutable original spawn coordinates, not randomized each frame
  or derived from the visible player goal. Color is brushed steel / teal-gray,
  with edge highlights, reinforcing ribs and corner rivets. No square X.
- A panel has world-space angle and angular velocity, which are also
  simulated (not merely rendered); friction decays both linear and angular
  speeds. CCD projects the actual 96-node membrane and 10 real tissue masses
  against its rotating thin geometry, and convex SAT prevents overlapping
  panel/old-crate or panel/panel solids.

## 2) One-sided physical pressure
- The original energy-cover square and door keep independent bilateral
  grip mechanics and verified thresholds.
- Plates do not pass through SquareGripPuzzle or receive sticky boxGripHold.
  A physical point pressing from ONE side applies momentum to the panel.
  A point closer to one extremity creates measurable torque, allowing rotation.
  Central impacts translate. No bilateral contacts or intentional grip command
  are prerequisites.
- Passive touching without material-relative impact or genuine motor demand
  causes no acceleration. There is no hardcoded policy named push panel,
  rotate panel or place shelter and no bonus for any special arrangement.
- True separation, contact pressure and altered object position can be
  sensed via ZERO's existing physical/sensory routes.

## 3) Wind graphics
- Preserve the same storm START/CYCLE/LENGTH/WARNING, wind direction,
  tissue impulse, exposure fractions, energy and integrity cost, reward
  attribution and skill-learning paths.
- Replace fixed thick grid lines and fake rectangular shadows with a
  restrained visual atmospheric field: curved traveling semitransparent
  streamlines, parallel luminous edge accents, atmospheric dust motes,
  gradients and physical wake curls behind real opaque panels/boxes.
  Continuous-time motion avoids per-frame random flashing, while wind
  warnings remain faint but readable.
- Wind shelter geometry is updated to match the ACTUAL rotated plate OBB
  by horizontal ray/slab intersection, NOT the obsolete square footprint.
- Paint operations are limited to 22 short paths, 26 fine visual motes and
  up to 3 wakes/visible solid per frame; no new textures, network assets
  or shader/Canvas state leaks. WindArt is view-only.

## Persistence and migration
- Existing JSON physical_scene_v100 keeps optional panelAngle and panelSpin
  only for law-6 materials. Old scene saves without those fields initialize
  their preserved physical material positions with angle 0, spin 0; restore
  validates finite bounded angles/spins and all prior fields.
- ShelterEcology upgrades its own embedded string from se1 to se2, appending
  angle and spin to every persisted placed material. Both se1 and se2
  decode atomically. Actual rotation without major displacement is preserved.
- The 12 named slots, old learned motor/grip/proprioceptive weights and
  procedural world remain unchanged; energy-cover puzzle stays isolated.
- No externally hosted binary source or remote AI calls are required.

## Regression
- Unilateral node impact actually moves a material without bilateral grip.
- Off-center impact rotates, centered impulse only translates, passive
  contact with no real force does not propel.
- The old covered energy box cannot be shoved open by plate physics.
- Rotating panels rotate real wind shadow coverage and cannot protect
  upwind tissue or arbitrarily distant tissue.
- SAT resolves old box and plate overlap for horizontal and rotated panels;
  new shelters preserve exact angle and spin across reload, and old se1 saves
  survive migration.
- New Android compile and every previously signed grip, ZERO 0123, 96-node
  body and shelter regression remain mandatory. The exact source files
  are hash-pinned to the signed publisher.

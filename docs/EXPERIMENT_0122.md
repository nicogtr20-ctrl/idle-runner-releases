# ZERO 0122 — Emergent shelters, wind damage and persistent constructed materials

## Objective and boundaries

ZERO has demonstrated real deformable motion, bilateral grips, manipulation of
several crates, survival under scarcity, and entry through a partly open door.
It has no innate concept of "house", "build", "safety", or object utility.
This release adds world consequences that can make an independently placed
obstacle genuinely useful. No predetermined building motor command, blueprints,
artificial construction reward or destination hint is supplied to the brain.

## World mechanics

- A blank construction block (physical law 6, 40 by 40 units, steel blue) uses
  exactly the existing opposing-side grip physics of old movable square crates.
  It has no hidden source, is not food and does not pay energy when moved.
  Every habitat expansion proposes one block at a physical non-overlapping site.
- Square-square overlap is resolved along its actual minimum penetration axis;
  overlapping blocks cannot be used as a phantom shelter wall.
- Visible directional storms start at second 60 of an individual life and
  last 34 seconds every 150 simulated seconds. Wind reverses direction in the
  next cycle, has 11 simulated seconds of warning, and is not chosen by ZERO.
- Every one of 96 actual living tissue sites receives external wind pressure
  and a small physical impulse when exposed. Environmental pressure changes
  existing tissue-pulse readout, not fake solid-contact touchSense.
- Existing opaque square solids and chamber wall slabs cast 165-unit bounded
  geometric shadows in the incoming direction. A point actually in the wind
  shadow experiences only 4% of the open-air pressure. A barrier placed on
  the wrong side of ZERO does not protect it.
- Full exposure costs 0.23 energy and up to 0.48 tissue-integrity units per
  simulated second. Shelter decreases this loss from actual local geometry.
  Injury has an additional bounded metabolic burden up to 0.11 energy/second
  during storms. Between storms tissue repairs at 0.085 integrity/second.
  No existing food or poisonous object changes its physical law.
- Physical wind and tissue consequences become ordinary observed learning
  experience. No extra +10 award is granted for arranging three blocks.

## Persistence and compatibility

- Only inert materials that are actually displaced at least 12 units become
  archive-worthy artifacts. Their immutable original anchors, actual positions,
  velocities and measured history are archived prior to streaming cleanup,
  death and physical saves. When ZERO returns within 320 units, materials can
  reappear at their last real locations without duplication.
- The archive is bounded to 96 constructions; the oldest can be retired beyond
  that bound, to prevent unbounded RAM and storage on Android.
- Additive optional state lives inside physical_scene_v100 as
  shelter_ecology_v122. Earlier saves work unchanged, as do all 12 named
  slots, existing trained motor/grip/membrane, long-term predictions, and
  scene reconciliation. Malformed new state rejects the whole scene rather
  than partially swapping brains or objects.
- Ordinary death resets physiological tissue integrity, not learned weights
  or successfully placed material. Explicit full new game still resets both.

## Controls and falsifiability

- A pure Java regression evaluates 96-point exposed area, correct blocker
  direction and reach, storm timing/reversal/warnings, energy/integrity benefit,
  no false touch, no placement reward, true moved-item save/revisit, malformed
  snapshot atomicity, collision separation and inert spawn.
- Keep existing body, 0121 distributed sensing, anti-penetration, dual-grip,
  16-ray spatial/optical, finite-energy, simulated mortality, diagnostics,
  and signed updater gates. Verify the Android app at speed x1/x20.
- Later use paired trained-vs-virgin scenes with identical material layout,
  storm and energy to distinguish emergent learned construction from chance.
  Physical tests alone do not demonstrate cognition, planning or consciousness.

## Limits

A square block provides a real 2D wind shadow, not structural roof beams or
crafting. The game currently simulates wind from a single horizontal axis,
not a complete fluid solver. Permanence is bounded to 96 moved materials;
a large number of historical constructions can be evicted.

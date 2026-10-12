# ZERO 0123 — Dynamic local tissue impedance and resistance

## Research question
Observed behavior: when a small appendage remains trapped between two crates,
larger material masses continue to move, pull the membrane taut, and may drag
that appendage free. This alone is not proof of a learned unjamming plan:
the old springs can produce that result passively.

Goal: distinguish real local motor intention, achieved tissue motion and
physical pressure in each of the 96 nodes, without injecting a scripted
escape/release motor action or modifying existing physical forces.

## Local physical readouts
- Expose the LAST motor-commanded target and tissue anchor for every node.
  These are read-only getters for existing Morphology private motor geometry.
- At actual decision boundaries, after physics and collision constraints,
  produce a 96 x 10 map: radial and tangential requested-vs-actual position
  error; positive neighbor-edge stretch and compression relative to actual
  material mass anchors; radial/tangential tissue velocity relative to body;
  hard solid contact; transient/environmental pressure; actual adhesion
  duration; and actuator demand.
- Stretch is computed from physical adjacent arc segments rather than
  center displacement. One small section can be restrained even when
  the centroid and remaining tissue move freely.
- Persistent local resistance is a PASSIVE DIAGNOSTIC only: contact and
  motor-position error, with reduced local motion across at least three
  distinct decision samples. These observations NEVER trigger scripted
  grip release, retreat, turning, pinching or predefined limb movements.
- Keep actual touchSense (solid contact) distinct from touchPulse (impacts
  and wind). A storm cannot manufacture a false hard collision.
- A just-restored physical scene has not executed a new real motor command,
  so the sensory model waits for Morphology.step before reading actuator
  endpoints, preventing fabricated post-restart experience.

## Learned responses
- All 960 local signals stay directly addressable. A compact action
  predictor sees 16 contiguous membrane sectors times 10 physical channels,
  max-pooling rare/local contact and tension, averaging other quantities.
- Independent, strictly action-conditioned transition and value models
  train only on genuine executed action results. Their action advice is
  zero until at least 32 holdout observations outperform the unchanged
  material state baseline by 5 percent with nontrivial baseline variance.
  Any later advice is bounded to +/- 0.08 and combined within the old
  learned body-bias clamp of +/- 0.35.
- No positive reward is generated from finding or escaping an obstruction.
  Current physically experienced energy and object movement stay the only
  material outcomes. Existing body/vision/locomotion/grip networks and
  their learned weights are unchanged.

## Saves, reset and performance
- New optional profile key local_impedance_v123. Old save slots without
  the key remain loadable with a virgin local model.
- All twelve named slots include new persistent learned weights
  automatically. Ordinary death resets transient sensory samples but not
  action/value weights or holdout evidence. Selective PROPRIOCEPTION reset
  clears all three body-related models together.
- No 960-feature map is persisted on every simulation frame or fed to a
  larger primary brain. The model updates only at decision intervals.
  Real motor forces, spring coefficients, gate, boxes and wind stay unchanged.

## Verification criteria
- A distant mobile point cannot mask a smaller, physically trapped region.
- Neighbor strain is sensed and old motor body coefficients remain identical.
- Wind raises transient force readings but never solid-touch sensor.
- No automatic correct action is produced from a virgin model.
- Learned bounded advice requires separate heldout future consequences.
- Encoded state survives saves and deaths; truncated snapshots are rejected.
- Run historical grip, body integrity, optical vision, ecology, signed source
  consistency checks and Android debug compile prior to signing/publishing.

## Limitations
Motor target error plus pressure is not a uniquely identifiable cause.
It might result from a heavy object, elastic lag, a narrow opening,
friction, or conflicting motors. This is mechanistic bodily awareness,
not proof of human-style reasoning or understanding of why the blockage
occurred. Demonstration of a learned unjamming strategy requires
trained-vs-virgin controlled scenario evaluations.

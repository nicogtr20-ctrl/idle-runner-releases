# OVERMIND ZERO 0120 — Distributed energetic symbiote (experimental)

## Rationale
OTA 85 corrected a real physical separation of the nucleus from the 96-point
membrane, but forced shape compatibility to an artificial central mass. User
chose a fluid black symbiote: the creature is a continuous deforming material,
not a nucleus with limbs.

## Physical and computational design
- There is **no collidable, inertial, or visible nucleus**. `Body.cx/cy`
  and `cvx/cvy` are derived read-only barycentric measurements of the ten
  real internally motorized material masses. Changing the cached center
  never moves the material. Those are used exclusively for cameras, maps,
  odometry, neural body-relative direction and navigation.
- Ten connected inner material points use exactly the SAME 62 motor-network
  parameters; mutual internal forces are equal/opposite across physical
  masses. External substrate adhesion/damping remains the locomotion
  mechanism. 96 independently driven membrane nodes keep their own
  82-weight actuator control and independent physically held grips.
- 96 membrane actuation forces create **opposite tissue reaction** on actual
  inner masses; the physical center no longer serves as the source of
  artificial motor impulse. Local material cohesion is elastic, not a
  fixed visual or rigid nucleus tether.
- Rigid square/gate collisions apply to actual physical tissue masses,
  membrane nodes and bonded edges only. Their collision solvers do NOT push
  the geometric barycenter. Gate and box velocity still require independently
  commanded, opposing and sustained adhesion as before.
- Energy and damaging stimulus contacts use true material membrane edges,
  actual tissue nodes, or filled polygon interior, not distance to a phantom
  nucleus. Room energy is likewise touched by the real organism.
- Damage viscosity applies to velocities of the ten real masses, never
  the logical center.
- Main brain stays **26 inputs, 16 actions, original serialized weights**.
  Its learned prediction, mortality memory and causal credit are not edited.
- Main obstacle sight emits 8 rays from physically observed membrane sites;
  visual signatures are checked for occlusion from a tissue-facing sensor.
  Secondary spatial map uses 16 independently situated membrane eyes
  and stores each physical ray's offset in its local mapped cells.
- Shape proprioception now observes 8 body sectors × 6 observable channels:
  pressure, actual hold, radial extension, radial velocity, NEAREST
  and FARTHEST perimeter distance from tissue centroid (48 signals).
  Its saved key changes to `body_proprioception_v120`; the old 32-dim
  predictor is not reinterpretated as a 48-dim predictor and starts virgin.
  Existing main 26-channel, procedural motor and morphology weights remain.
- Rendering stops drawing the physical black nucleus. Any shape centroid
  used for shading is only a draw reference, not a collision or learning rule.

## Tests / release gates
1. Zero phantom-food/phantom-wall collisions from a moved logical center.
2. True in/outside and edge contacts from the deformable 96-node body.
3. After actual physics integration and wall constraints, stored center equals
   average physical muscle location/velocity.
4. At least 10,200 actual distributed physics steps, including 5,400
   square+gate frames, no nonfinite position or unauthorized object movement.
5. Real copies of the actual saved 62/18/82 weight vectors on three physical
   movement contexts, PLUS completely virgin motors. Compare path and net
   displacement without writing anything into real player saves.
6. Fresh 62-weight locomotor learning improves in an independent trial,
   proprioception predicts 48 dimensions, and 16 spatial rays and occlusion
   tests remain valid.
7. Full legacy 3-box/gate independent grip, source-separated harm,
   terminal-survival learning, neural noninterference, and Android build.
8. Run a self-intersection diagnostic on repeated 96-node silhouette samples:
   this is necessary to rule out a web-like visual artifact.
9. Only after full QA, update exact GitHub integrity pins, sign Android OTA
   and verify Vercel production matches GitHub asset SHA/size.

## Compatibility / truthfulness
Older body map v109 is not transformed; it is reset only for that
new sensor network. Old named saves and old motor/decision/casual
memory remain untouched until the user explicitly chooses a reset.
The user may start a virgin creature if new physics renders old
learned trajectories ineffective. This is a physics architecture
change, not evidence of higher intelligence or consciousness.

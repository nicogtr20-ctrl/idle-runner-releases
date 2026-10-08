# ZERO 004 — World continuity & verifiable object displacement
## Reported by the Android tester
On v0.4.0-ota.15, ZERO's learned locomotion was obvious but object displacement was difficult to see. The simulation appeared to rewind. The physical contacts counter indicated 22 even though no visible, sustained object movement was apparent. The sample video was 8.2 seconds, and a visual review showed shifts in objects/world.

## Found source problems
- `GameActivity.onDraw()` reset the entire creature to origin after **26 seconds** or when it moved more than **520 units** from the world origin. This caused surprising cuts while being observed.
- The `physical_pushes_v04` count increments on a collision impulse/velocity spike, NOT on a measured distance. It cannot be trusted as a count of visibly moved objects.
- `UnknownWorld.Mind.choose()` used a strong preference for known positive energy signals even if energy was already at 99/100. Exploration of other unknowns was stifled.

## Changes
- ZERO persists in one continuous world as the simulated world expands around it; it is no longer reset by time/distance. An actual loss of all energy still restarts the organism, with saved learning retained.
- Habitats appear relative to ZERO's current coordinates. Far-away objects are unloaded to bound memory; fresh unknown objects spawn outside the immediate view.
- Object movement has visible evidence: a faint original-position ghost, a connecting trail, and a **world-units distance moved** label. Not a scripted animation.
- Distinct metrics: `URTI` counts physical contacts, `DISCHI SPOSTATI` counts actual initially-static disks moved at least **12 world units** by ZERO after physical contact, and `DISTANZA MASSIMA` is maximum measured shift.
- The old contact count is **not migrated as successes**. New confirmed movement metric starts at zero; motor weights, manipulation weights, energy, memory remain untouched.
- The learned preferences are moderated by the current energy need, allowing exploration when reserve is full, without reading hidden properties of objects.

## Validation
JVM tests of physical collisions, world-relative spawn coordinates and observed movement, as well as Android staging build. OTA publication on main updater branch **only after staging CI passes**. On device, verify no teleport after >26s, physical object displacement trail, counters and memory persistence.

## Limits
A micro-controller conditioned on a direction and narrow object signatures is not general reasoning, and this implementation does not yet create dynamically increasing appendage count. Physical contact effects remain approximations of spring/contact physics.

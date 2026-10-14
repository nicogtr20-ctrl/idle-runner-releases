# OVERMIND ZERO — World consistency fix (OTA123)
Date: 2026-10-14. Source branch: feature/zero22-active-recurrent-ota117.
QA: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38546977153 (JVM tests and Android debug build SUCCESS).

## Diagnosis backed by source
- Room: GameActivity.updateHabitat previously called activateChamber(false) when the room was complete and elapsedLife >= nextChamber, without checking the organism's physical location. Upon collecting room energy the timer becomes elapsedLife+40. Thus the prior room could disappear while ZERO was still physically inside.
- Energy boxes: each law-5 square that is generated is paired with one pre-existing law-4 +40 rare gem, sharing immutable spawnX/spawnY, and all 48 generations checked in WorldPlacement regression have a unique matching source (also after legal placement). Law-6 thin construction sheets deliberately contain no gem. An episode divisible by three may have NO energy square; no law-5 square is ever intentionally empty.
- The covered gem already has passive physical rim photons in OpticalCues/Senses.read; opaque boxes block the full spectrum and ZERO sees weak light from the NEAR external rim only within the 210-unit sensor radius. Before this fix the decoration was teal rather than purple.
- The real loss mechanism: HabitatContinuity.pruneDistant and enforceLimit could recycle an uncovered, non-consumed rare energy when ZERO travelled away or when world >24 objects. This removed a legitimate finite reward even though it had never been touched.

## Exactly what changed
1. HabitatContinuity.roomOccupied(room,body,skin) tests real centroid, all 10 tissue masses, and 96 deformable membrane patches against the complete room+gate with a small margin. GameActivity automatically replaces a finished room only after ZERO's **entire body** has left. If ZERO touches room with one membrane patch, room persists; the timer cannot erase walls beneath it. A real death starts a new life and a new environment, unchanged.
2. HabitatContinuity.protectedByInteraction protects any **uncovered/unconsumed** law-4 gem from both distance-based pruning and the ordinary world object soft cap. This is a soft cap; live rare resources can temporarily increase the object count. Once physically consumed, pruneDistant still removes the already-consumed object normally. Uncovered gems are not teleported, replenished, or created from history.
3. GameActivity.renderWorld makes the existing hidden-gem halo purple and gently pulsing, only when OpticalCues.coverOf confirms a real still-covered energy source. The brain's weak, occlusion-aware optical signals are unchanged, including the 210-unit limit. No premature pickup/extra energy and no new "purple" semantic label.
4. WorldPlacement main regression checks the one-to-one energy box/source pairing in 48 raw and legal worlds.
5. HabitatContinuity main regression asserts occupied room not dismissed (including membrane-only occupancy), uncollected exposed gem survives prune + cap, and collected gem is removed.

## Scope and limitations
- ZERO 2.2 core, learning, exploration, reward, grip physics, blue/red objects and panel laws untouched.
- Saves and Android application id unchanged. Install OTA over existing version, no reset.
- A covered unvisited source and cover may still be recycled together when very far, as before; guarantee applies **once the gem has been discovered/uncovered**.
- A normal simulated death is still a new physical world: scene objects and room restart while learned brain survives. This fix does not implement cross-life world persistence. This is intentional and separate from the mid-life disappearance bug.
- Tests establish invariants and build success; assess the actual new OTA on device with the user and telemetry.

# OVERMIND ZERO — Experiment 0116.3: small square obstacles and real hidden energy

**11 ottobre 2026.** Built on the 0.11.6-ota.69 observer-only stable branch (including its five-minute decision flight recorder). The causal-planning experiment 11.7 is NOT merged.

## Motivation and user-observed defects

The world was too sparse, with one large movable gate as its main challenge. ZERO should have opportunities to discover delayed instrumental relationships, without being told in its policy that something is a "block", a "reward", a door or a goal. Visible resources sometimes appeared visually ON TOP of the gate because the old room-overlap footprint started at the wall, whereas the gate stands farther toward the entry.

## New physical puzzle

- A modest **40x40** square cover is spawned occasionally alongside its **initially invisible/inaccessible finite +40 energy** (normal blue is +22; large chamber reward remains +80).
- The square and concealed resource are normal world objects persisted in the existing scene snapshot, with new laws 5 and 4 respectively. No new raw sensory dimensionality and no changed signature mapping of the original three wavelengths.
- The square is a rigid AABB collision obstacle in the existing eight obstacle rays and its usual optical signature remains unprivileged. The reward is omitted from ray/optical senses, rendering and collection while concealed.
- **Touch or one-sided pressure does not move the square.** Its vx and vy can change only after simultaneous sustained neural adhesion with at least two genuinely independent (separated) membrane masses on opposing **left/right OR top/bottom** faces, computed after physics. There is no direct "grab the box" instruction, no forced grip and no scripted displacement.
- The membrane's connecting edges are also checked against the square's interior. An edge tunneling through the obstacle is pushed outside and cannot count as a grip.
- After verified opposed-grip force has genuinely displaced the box by **55 units** from its original anchor, it uncovers the previously existing reward. Moving the box pays no energy. Only a subsequent actual physical contact with the revealed resource pays up to 40 energy, once; the delivered reward is clamped by remaining reserve.
- The original disk mechanics, gate laws, 16 action choices and learning objectives remain unchanged. Physical square displacement adds an ordinary external-object effect, but the energetic consequence is only an actually contacted resource, not an imagined future gain.

## Non-overlapping spawns

- `WorldPlacement.spawn` filters each candidate by physical radius plus 9 units, existing active objects, accepted new objects, clearance around ZERO's actual body and full active chamber geometry.
- `ScarcityLab.overlapsRoom` now includes the **freestanding gate to the left of the entrance wall**, which the previous exclusion rectangle missed. Existing objects that intrude are removed by the normal room exclusion loop; new spawns avoid the area.
- A concealed reward is an inseparable pair with its square; if the cover cannot spawn safely, the hidden resource is rejected as well.
- Movable squares are rolled back and stopped if their inertia would carry them through the chamber geometry. The square is not a teleporting obstacle.
- This explicitly addresses generation overlap. It does not yet solve every possible **subsequent dynamic collision between two independently moving objects**; that is separate physics work.

## Save/profile compatibility

The existing `GameSceneSnapshot` schema version remains 1, with a new optional `concealed` flag and extra law IDs 4/5. Old saves lacking the flag decode as false. Both cover and reward preserve position/velocity/state through checkpoint and named save slots. New optional counter `square_uncovered_v1163` records physically uncovered squares without pretending they are solved gate rooms. NO reset of the user's previous learned weights.

## Tests

The independent `SquareGripPuzzle.main` verifies that touch-only and one-sided grip cannot move/uncover the square, and that a legal non-tunneling bilateral grasp can displace it in 2D and release a resource exactly once. `WorldPlacement.main` checks no overlap of energy dots, blocks, existing geometry or gate, including new expansions, and no orphan hidden sources. Original dozens of JVM regressions, including perception, physical gate and 12-slot save codec, plus Android `assembleDebug`, run as GitHub QA.

This is an environmental challenge, not proof that ZERO has already learned to uncover blocks. The 0.11.6 decision blackbox should be used to measure actual captures, surface contact, releases and future reward collection before any changes to its autonomous policy.

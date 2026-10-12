# ZERO 0127 — Spatial checkpoint repair, red-grip isolation and survival recovery

**Status on 12 October 2026: staging only.** No signed APK has yet been built, published or tested on Galaxy S21. Source branch: `feature/zero-0127-spatial-persistence-box-recovery`. Stable base: `feature/overmind-inapp-updater`. Last confirmed user installation: OTA 97.

## Evidence and diagnosis
- User telemetry before/after 0124–0126 indicated moved objects/life 3.92 to 1.24 and mean survival 939 to 546 simulated seconds. These are observational, not matched-seed causal experiments.
- Old `SpatialPerception.putCell` persisted neutral occupancy value `0`; its `restore` rejected `0`, aborting the entire spatial model load including transition/prediction weights. Consequences can include a reset to 1 transition despite thousands reported previously.
- The construction-panel vs energy-cover SAT solver previously considered **every** old energy box movable even when its own opposing-grip condition was false. The two-cover AABB solver also displaced both boxes with no adhesion. Those geometric nudges could satisfy the old cover displacement/uncover threshold without genuine bilateral manipulation.
- A panel's serialized per-point timer is insufficient physical proof immediately after scene reload. A real contact must reconfirm a red hold in this physical session before any sheet inertia or separation is allowed.

## Staged changes
1. Spatial loader accepts legacy neutral occupancy cells, removes them from canonical storage and retains all learned predictor/value weights; `SpatialPerception` main regression covers migration. Map remains life-local by design; learned weights survive deaths.
2. Every old energy cover has a **transient** `bilateralHeldNow` set only by `SquareGripPuzzle.constrain` after opposing, physically separated, confirmed adhesive points. Collision solvers can move the cover only with this valid flag. This flag is deliberately NOT serialized, and cannot be inferred from `everCollided`.
3. Every construction panel has a transient `panelHeldNow` set by `PanelPhysics.constrain` from actual contact, local commanded grip and hold duration; a saved timer alone is never enough. Unheld materials receive zero collision-induced motion. A real single RED point remains sufficient for a construction plate, while energy covers and room gate still require opposite-side holds.
4. Periodic newly spawned panels are capped to every third area and three nearby, kept distant from old covered energy boxes and exposed finite-food contact areas. Previously placed materials are not erased.
5. Scarce finite +22 supplies are *proposed* every fourth rather than fifth area expansion and still must be physically reached/collected. Storm exposure metabolic cost is conservatively reduced from 0.23 to 0.18 energy/second and injury burden from 0.11 to 0.09/second. Wind geometry, tissue force, injury rate, storm frequency, shield benefit and all sensor/reward wiring stay unchanged.

## Physical implications and explicit limits
- Geometry-only separation of two already overlapping **unheld** rigid objects is deferred rather than granting movement of an inert energy cover. New spawns/rehydration must enforce valid non-overlap; investigate old saved scenes with overlaps rather than fabricating learned box movement.
- Old cover momentum from an earlier true bilateral movement still follows its existing drag law; this patch isolates **collision-induced** movement, not a redesign of classical inertia.
- No new direct goal input, scripted path, learning reward for shelter construction, free on-contact power, forced action or brain reset was added.
- More food opportunities and less wind damage may improve survival, but cannot establish recovery without unbiased post-release lifetime traces and matched environment comparisons.

## QA gate before any release
- `SpatialPerception.main`: legacy neutral-cell saved map loads while preserving learned weights.
- `PanelPhysicsRegression` and `ShelterEcologyRegression`: ungripped panel/box overlap must not move a cover; valid RED sheet movement still works; valid bilateral cover remains movable; old two-cover overlap cannot reveal energy.
- `MultiObjectGripRegression`, `GateBoxGripIsolationRegression`, `GripParityRegression`, and all 0121–0126 regressions must pass.
- `WorldPlacement` and `UnknownWorld` validate safe scarce-resource cadence and non-overlap.
- Android Gradle debug build, signed OTA publisher, SHA-256, Vercel latest.json, install over OTA 97 without data loss, plus in-game red-only motion video are **not yet verified**.
- Compare at least 25 completed lives in each release under matched energy/storm/obstacle conditions and monitor death reasons, free-energy contacts, spatial learned transitions after restart, cover unseals, plate presses, actual red holds, and protected fraction. Do not claim that 25 random live sessions prove causality.

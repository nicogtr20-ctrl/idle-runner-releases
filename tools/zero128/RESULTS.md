# ZERO 0128 — Historical physics counterfactuals (2026-10-12)

## Reproducibility
- Validated GitHub Actions run: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38289556493 (all five Java jobs SUCCESS)
- Original snapshots: `zero-telemetry` reports `20261012-134843-3f9f02712cf8` and `20261012-163858-48e97da70734` (kept private; run logs expose only aggregate measurements).
- Controller: same latest motor, hands and membrane weights, same starting positions and initial velocities per fixture, fixed four initial action headings and two grip drives, two environments. 32 cases per release, 160 total. 440 physics steps × 25 ms = 11 simulated seconds/case. These are controlled *open-loop* motor headings, NOT full autonomous `SensorimotorMind` decisions or full life/energy search.
- Exact checked-out release classes for MotorBrain, Morphology, SquareGripPuzzle, SquareCollisionSolver, ShelterEcology, PanelPhysics where present. Read-only test runner; no pushes to `main` or `feature/overmind-inapp-updater`, no APK, no user save modifications.

## Main findings
1. **OTA93/94 physical results match exactly** in all 32 matched scenarios. OTA94's impedance model can affect a *full-brain* policy, which was not executed here, but it did not change the isolated square physics.
2. **Old unsourced box collision translation reproduced**. With two old 40×40 energy covers 32 units apart (8-unit overlap), the old `ShelterEcology.separateBoxes` moves each cover 4 units away and removes the overlap even if **neither** has an adhesion hold, for **OTA93, 94, 95, 97**. This is NOT proof that ZERO independently learned two-cover manipulation.
3. **OTA99 closes the false-motion loophole**: same pair, zero verified holds => neither moves and overlap stays 8; only first held => first moves -8 and second stays; both held => each moves 4. The preserved strict bilateral-grip law on EACH old energy cover is enforced by OTA99.
4. On genuine frozen `latest` scene, fixed motor commands produce nonzero bilaterally held frames (only one heading/gain combination, 67 frames; all other combinations 0), but **none of the 160 short trials uncovered a fresh energy cover**. These short replay cases cannot establish relative real-life survival or energy yield.
5. `mid` scene average displacement of ZERO's centroid over the eight FULL-material trials (four directions × two gains): OTA93/94: approximately **98.25** world units, OTA95: **106.85**, OTA97/99: **108.50**. Thus this replay does NOT show that the new thin-panel physics is the standalone reason for reduced mobility. Modern saved panel poses are not historically compatible with original OTA93 squares: the covers-only controls are a cleaner comparison.
6. With only physical energy covers (no construction material), all five releases match per trial on cover motion and muscle displacement in the chosen scenes. This does NOT disprove a difficult long-run collision/scarcity interaction.

## Separate telemetry observations (not proven causal by the replay)
- Average completed life, last 25: **938.51** sim seconds (pre), **545.78** (mid), **333.11** (latest).
- Energy boxes/disks moved per life: **3.92** (pre), **1.24** (mid), **0.92** (latest). NOTE: source life metric is DISKS/objects moved, not a direct exact count of uncovered energy covers.
- Spatial learning persistence: old `spatial_perception_v110` serialized transition count **11,101**, mid count **1**, latest **6,205**; restore bug fixed later, the lost former weights were not reconstructed. Save checkpoint/restart and learning-policy implications require isolated replay.
- Latest saved energy cover had **9 actual red holds on one side**, with no verified opposing-side grip. This is a genuine failed bilateral physical manipulation at that instant, not necessarily a long-term decision deficit.

## What remains unknown
This bench isolates rigid-body physics but NOT the complete learned behavioral policy. Determining whether ZERO searches less for energy due to damaged spatial memory, new decision-model bias, or reduced ecological opportunities requires a **full-headless policy+ecology rollout with checkpoint restores and comparable seeds**, ideally verified with paired Android/CI instrumentation. The current evidence does not warrant artificially weakening the real bilateral-grip rule or resetting the user's brain.

No gameplay modification is authorized by this test alone.

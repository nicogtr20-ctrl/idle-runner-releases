# ZERO OTA165 — Evidence-gated directional motor duration

Date: 2026-10-16. Candidate `fix/zero165-directional-duration-credit` until JVM+Android QA, signed publication, and live APK HTTPS+SHA-256 succeed.

## Source evidence — user's explicitly shared real game data
Latest: `zero-telemetry/reports/20261016-074020-165d6c73acc66` (after signed OTA164). Previous report: `20261015-221636-14713a9b03001`.

* The living motor coordinate controller has 815,787 completed physics frames, 25,408 feedback intervals, 15,158 trained motor intervals and 5,083 untouched holdouts. Mean actuator intent realization is **0.0270** on [-1,+1], a near-null relationship between self-selected world heading and actual centroid shift.
* In the flight recorder, **50.27%** of measurable current decisions moved against the heading; **43.96%** in the completed life.
* The physically chosen action changes between successive decisions **76.6%-82.0%** of the time, preventing stable cortex activation.
* Read-only consecutive-action analysis: last life net normalized directional agreement for singleton runs -0.067 (259 usable), double +0.297 (26), >=3 consecutive +0.683 (13). Current life singleton -0.104 (114), double +0.037 (20), >=3 +0.526 (9). The longer-sample advantage is **correlational**, not an A/B proof.
* Saved `md160` means favor the *one-decision* option for **15 of 16** actions. Its score rewards energy and unconditioned distance/efficiency but never checked whether the resulting motion agreed with the self-chosen direction. Thus the old learned time policy can systematically entrench ineffective short bursts.

## Patch / boundaries
* Only `MotorDurationV22.java`: new per-action/per-duration **realized heading agreement**, computed from actual net displacement vs the 2.2-selected action angle, weighted by traversed distance and measured path. Trained/heldout samples remain independent.
* Prior `md160` reward means and counts remain completely intact and **are not mislabeled** as directional validation. Existing saved `motor_duration_v160` preference key remains; new format is `md165` with strict atomic `md160` migration and restart-safe `md165` restore.
* Until independently validated alignment data exists for a duration, sample medium/long 2/4 decisions by default, with 15% continued exploration across all three options. There is no prescribed gait, heading, limb, food vector or target. The recurrent brain remains the exclusive chooser.
* Immediate harm, high-confidence 2.2 challenger, deliberate exploration, or near-death reserve still interrupt any hold; a held action NEVER suppresses danger.
* The physical gel, friction, grip, 84 global cortex weights, 320 regional weights, 2.2 recurrent model and neurogenesis, 12 named save slots, application ID and updater signature remain unchanged.
* `Zero165MotorPersistenceRegression` asserts observed directional duration preference after separated real-like experiment/holdout samples, no second chooser, danger interruption, md160->md165 lossless migration, new-save exact roundtrip and malformed-snapshot rejection. Existing ZERO160-164 and all historical JVM/Android regressions remain mandatory.

## Interpretation / acceptance
A successful regression demonstrates the specific **motor-duration learning defect** is addressed and no saved brain is wiped. It DOES NOT prove that the learned regional muscle mapping is now coherent or that ZERO successfully seeks blue energy. On-device falsification requires new flight recorder data from same organism: reduced 1-tick bias, increased safe sustained actions, net direction agreement on >=3 sustained samples, energy/obstacle success, no increased harm, and continued save/restore correctness. Do not publish or claim a working APK if signed or independent live SHA-256 checks fail.

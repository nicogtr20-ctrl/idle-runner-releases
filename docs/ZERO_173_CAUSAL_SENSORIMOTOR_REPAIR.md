# ZERO173 — verified brain-to-body feedback repair (staging only)

Date: 2026-10-16. Base: released source `feature/zero22-active-recurrent-ota117` / OTA171, runtime172. No APK OTA number has been consumed and no installed neural, muscle, named-slot, firmware, package or signing identity is changed here.

## Actual phone evidence, not theory

Observed uploaded report: `zero-telemetry/reports/20261016-190008-18aef703b5d67`, 14 multipart shards. Actual OTA171 / code1171 / runtime172. Life statistics: 149 complete lives; latest life 242.683s, one actual +22 energy contact at 23.4s, maximum 226.297U distance from spawn. One successful contact does not prove a learned food-targeting skill.

Active 164D recurrent prediction: 45,436 transitions and 9,087 heldout transitions; predictor MSE .04280661 vs unchanged-state .041163225 (worse). The persisted `goal_continuity_v114` has 0 confirmed goals, 0 continuations and 0 observations. `instrumental_navigation_v112` has five positive and five negative object experiences, but zero historical nav advice decisions; this advice counter is legacy and not a definitive live 2.2 behavioral metric.

Most decisive physical and learning observations:
- 128 recent 0.65s actual-command records: 114 flagged exogenous and 14 eligible; every 64 heading-only command records in that window was classified exogenous. Exogenous classification was applied whenever **any** membrane surface reported touch >.25, including normal tactile contact and self-actuated traction. This prevents evidence from entering the body→navigation prediction and motor credit, despite actual muscle impulse.
- `contextual_navigation_v172` persisted 96 body-motion cells, of which 77 are current controller epoch 7. Zero met the independent train/holdout validation gate (6 train, 3 hold, error < .95 no-change baseline, nontrivial true displacement). Nearest stored within-action/current-epoch cell RMS distances: minimum .0683, median .1269. The old matching radius .065 is below **every** stored nearest neighbor, fragmentation sufficient to impede confidence accumulation.
- Previous last-life blackbox: 374 decisions, 255 executed actions differed from instantaneous greedy action; median greedy-vs-executed score margin ~.154, while the established strong-neural interrupt margin is .24. This is intentional temporal motor inertia, not evidence of a disconnected controller. Preserve real 1/2/4 duration options until matched-world tests establish a safer change.

## Changes (no movement scripts)

1. `GameActivity` uses a centralized pure external-motion provenance classifier in `MotorResponseJournalV172`: actual post-world displacement correction >.08U/frame, newly moved environment >.03, real storm, or actually held membrane contact still contaminates. A **touch reading alone does not**. Invalid measurements fail closed. Physical solver and 96+74 particles unchanged.
2. `ContextualNavigationV172`: near-posture RMS radius .065→.14, calibrated against the actual saved within-action contexts; prevent spurious transfers if significant opposing signed vx/vy or combined velocity difference >.40. Existing per-action/per-epoch bound and exact 724-weight RMS epoch guard remain. Independent real train/holdout confidence gates, no-change control comparison and corrupt-save rejection remain intact.
3. `SensorimotorMind`: do not call navigation `chosen()` before the living motor head possibly restores a verified 724-weight skill. Record **once** after the active actuator epoch is known. Legacy non-symbiote route unchanged.
4. `Zero172MotorResponseRegression`: physically derived neighboring postures converge across train/holdout, opposing velocity does not inherit trust, benign contact versus genuine external motion tests, exact persistence and all previous response/latency tests. Existing source and Android QA gates retained.

## What this does and does not prove

These are reproducible source causes that prevented motor learning, not a claim that ZERO now walks or follows blue light. The original 2.2 neural core, neurogenesis, motor learning weights, action choice ownership, search/exploration, named slots, physical world, physics clock, collision solver, UI, OTA channel, APK signing and profile formats are preserved. No attraction-to-resource coordinates, target headings, artificial impulses, locomotion rewards, scripted rhythm, object labels or synthetic holdout success.

All learned checkpoints remain installed-data compatible. Existing ZERO171/172 save keys remain the same, so learned skills are not reset; future contextual evidence must still validate independently. Initial results from the Galaxy S21 are unknown until the user runs the staged changes on-device. An updated report should assess eligible versus excluded motor observations, current-epoch validated predictions, intention continuations, command-vs-displacement and blue energy contacts. **No signed OTA is released by this branch.**

## CI and release gate

The first development branch `fix/zero173-brain-body-feedback` passed full ZERO22 corrective pre-release QA (GitHub Actions run `38955682972`): Java regressions including the new physical-provenance and nearby-context tests, all historical source/integration tests, and Android debug APK compilation. This branch is a clean source baseline copy for eventual PR to stable: perform a separate complete regression after all files are finalized, then build and verify the signed OTA only on explicit release decision, and finally test on the phone. Passing pre-release QA alone cannot establish efficacy.

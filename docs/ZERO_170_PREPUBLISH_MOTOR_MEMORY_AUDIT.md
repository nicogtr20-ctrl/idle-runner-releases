# ZERO 170 — UNRELEASED second audit of ZERO 169 symbiote motor memory

**Status: staging branch only. DO NOT MERGE INTO `feature/zero22-active-recurrent-ota117`, DO NOT RUN OTA PUBLISHER, DO NOT MODIFY `updater/public/latest.json`, and DO NOT BUMP VERSION before user explicitly authorizes release.** User has installed official OTA169, requests another verification and *one* later release.

Date: 2026-10-16, branch `fix/zero170-motor-memory-prepublish-audit`. All changes must be held in this branch.

## Symptoms and source-confirmed flaws in OTA169

1. `badStreak[action]` advanced on every failed result and did not reset when a different brain action was selected. Four separated failures could falsely be described as consecutive and automatically recall a skill.
2. `ms169` preserved only 724 weights plus action, not the actual physical membrane geometry and movement state. A recalled configuration could be inappropriate for a rotated or elongated fluid body.
3. Actual net displacement could arise from storm, object/gate collision corrections, passive coast or external forces. `physical()` had no motor-force/ambient intervention evidence and blindly labeled signed displacement as successful skill.
4. Automatic remembered-controller replacement had no post-recovery comparison, making it impossible to tell whether recall improved living motor outcomes or eroded them.
5. Cold-start partial recovery state and legacy confirmed records lacked the new verification criteria, risking unvalidated skill retention and false trust.
6. Near-circular cortex geometry has no well-defined principal orientation; naive principal-axis angle would amplify tiny numeric noise and veto valid skill contexts.

## Implemented corrections

- `SymbioteMotorSkillMemoryV169` remains the backing class name for compiled references, but its serialized data now uses **`ms170`** under unchanged key `symbiote_motor_skills_v169`. Existing original `ms169` saves are strictly parsed: ALL legacy champion and candidate weights are retained **as untrusted candidates**, never executed until *new* real active-force and posture evidence confirms them over independent lives. Maximum candidate count expanded 6 → 10 to retain all 4+6 historical entries without silent truncation. Historical brain/core 2.2, 84 shared cortex, 320 local cortex, 320 fast cortical synapses and past statistics remain unchanged.
- TRUE consecutive same-action failures only. Switch of 2.2 action, death/resume, actual success, storm or noncomparable external physics BREAK the run. A newly chosen action has no inherited failure count. Only 4 real physical attempts of the same intent permit recovery; cooldown prevents spamming. No new action chooser.
- Added **10-dimensional normalized, scale-aware living body context** at start of real motor decision: mean radius, maximum reach, anisotropy, signed shape orientation weighted by degree of elongation, membrane radial/tangential velocity, real touch/hold fractions, center speed. Similar weights in incompatible postures cannot validate or restore one another. Finite, bounded, corruption-safe checkpoint.
- `SymbioteMatterV1.lastMuscleReactionImpulse` measured ONLY from the 96 real active cortex force vectors is passed via the world integrator to the motor memory on **every real physics tick**. Store skills only if genuine muscle impulse ≥ minimum and initial passive speed limited. Each frame records whether storm/world contact/collision displacement corrections contaminated the result. External interaction vetoes skill certification and resets failure comparability. Real centre displacement is still measured after all world collisions. No fabricated distance or static gait.
- The EXACT original 724 motor weights are snapshotted on recall. A probation period checks 3 subsequent same-intent physically observed trials, requiring >=2 genuinely successful and a mean motor-control score at least +0.14 above ZERO's own actual last failed trials. If unsuccessful, original weights automatically return and further plastic training of the contaminated trial is skipped. If a technique twice fails post-recovery, demote it to unverified (still remembered as candidate) so it cannot loop harmful rollbacks.
- If app reload or real death interrupts the recovery experiment, `SymbioteMotorHeadV1.resumeLife/resetLife` restores original cortex before resuming normal policy. The pending probation and fallback synapses are serialized atomically inside the optional skill memory key, fail-closed on corruption.
- `SensorimotorMind.choose()` passes real living Body and Surface to motor memory; the same single RecurrentCoreV22 still chooses all 16 intents. `GameActivity` observes actual muscle-only reaction immediately after real `SymbioteMatterV1.step` and filters detected contact/weather/external displacement after world solver.
- Existing Body and Skill menus remain read-only, showing only real saved confirmed counts. No UI inflation or fake neuron count. Old 12 named save slots use same generic SaveProfileCodec with additional single string key; selective MEMBRANE reset still covers motor memory and fast network.
- ZERO169 original real 96+74 gel physical regression was updated to respect new same-action and physical-evidence rules. New `Zero170MotorReliabilityRegression` includes **actual** material runs plus adversarial passive/external movement, differently stretched membrane, nonconsecutive motor intention, legacy-save quarantine, real post-recovery success/rollback, crash-safe pending checkpoint, and actual production `SymbioteMotorHeadV1` recovery linkage. It does not replace the old brain with an outside policy.

## Important research distinction

A JVM laboratory's externally prescribed motor electrode confirms mechanical capability and the *verification/recovery logic*, **not** successful endogenous discovery. Even with stricter causal screening, active impulse + no observed external world correction is only a conservative credit signal; rigorous same-state counterfactual attribution requires a paired exogenous-force intervention test and actual phone telemetry. Do not tell user autonomous locomotion is fixed before enough post-release motor performance data (net displacement, angular shear, true energy pickup and recovery-heldout quality).

## No release

The branch is not production. A successful corrective QA on this branch may compile a **debug APK only for CI**, never sign/publish it as an OTA. Await explicit user approval for final audit and one combined OTA later. Verify public `latest.json` remains **0.11.6-ota.169 / 1169** and no new main manifest after these commits.

## Future final release gates

1. Full historical ZERO 161–169 JVM regressions, new 170 adversarial tests, Android Debug compilation. Never remove or lower tests to make it pass.
2. Source provenance pin update in stable publisher ONLY after user approves one-time release, then full signed APK, live Vercel SHA256 and independent bytes verification.
3. Same-save Android physical telemetry comparing OTA169 and new release, including effective installed neural weights, same-intent tries, candidate→heldout confirmations, recovery A/B retained/reverted, posture confidence, and actual net-to-gross travel.

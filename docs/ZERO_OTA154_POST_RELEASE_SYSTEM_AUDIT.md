# ZERO — OTA154 post-release system-wide static audit (2026-10-15)

## Scope and evidence
Reviewed source on `feature/zero22-active-recurrent-ota117` at `fc76715e2b41527260edc9ac694baa6a9b4f623f`, official signed version `0.11.6-ota.154` (1154). Inspected app tree (Java sources, QA entries) and critical paths of `GameActivity`, `SensorimotorMind`, `RecurrentCoreV22`, `SymbioteMatterV1`, `SymbioteMotorHeadV1`, `FluidInteroceptionV1`, `BodyProprioception`, `DistributedBodySense`, `LocalImpedanceSense`, `SpatialPerception`, `InstrumentalNavigation`, `WorldPlacement`, `OnlineBodyLearning`, `GameSceneSnapshot`, `SaveProfileCodec`, `GripContactObserver`, and persistence/update/release paths. This is a cross-system *static* audit, NOT exhaustive line-by-line formal verification or new on-device behavior validation.

All 6 originally reported OTA153 disconnects were addressed in OTA154; pre-release QA `38726087333` SUCCESS, signed publisher `38727071925` SUCCESS, independent HTTP/APK hash check `38727944242` SUCCESS. Existing tests do not cover all cross-system semantic contracts below.

## New confirmed code inconsistencies (not yet repaired)

### F1 — HIGH: inactive motor genes are still 'trained' in living Symbiote mode
**Source:** `GameActivity.java` roughly lines 1035–1084, 1350–1407 and `OnlineBodyLearning.java` lines 64–220.
`liveBody.weights(0/1/2,...)` chooses mutations of old `MotorBrain`, hand, membrane controllers; `liveBody.observe(...)` scores and accepts/rejects these genes from *actual* net motion/energy in the world. But the `useSymbiote` physics branch ignores all three controller arrays and steps only `symMatter.step(..., symMotor.commands(), ...)`. The evaluated candidate gene cannot be the cause of a changed physical trajectory. Counters `online_accepted_*`, recovered/archived old motor configurations, or an 'improved' score cannot validate improvement of the active 84-weight Symbiote head. They can still be relevant to legacy bodies, but must be isolated rather than reported as current living Symbiote competence.
**Acceptance test:** Under Symbiote mode, forcibly mutate an old-body gene and show *identical* executed cortex commands and physical evolution for equal initial state/RNG; assert no 'accepted active motor improvement' from an unexecuted controller; keep all legacy mode functionality.

### F2 — HIGH: commanded membrane extension can be mistaken for achieved shape in proprioceptive learning
**Source:** `SymbioteMatterV1.java` line ~351 (`skin.extension[i]=max(0,extend)*MAX_REACH`), `BodyProprioception.java` line ~60 (`v[2*SECTORS+k]+=skin.extension[i]/110f`).
The field contains motor intent even if solid collision/area preservation prevents actual extension. This is a legitimate *efference* but incorrectly treated as physically realized extension by a module described as actual-shape proprioception; current 26D `skin.peakExtension` and distributed `SHAPE` use achieved geometry. Expose demand and achieved geometry separately or use only achieved morphology for this proprioceptive channel; migration/learned-weight semantics must be reviewed.
**Acceptance test:** Same positive cortex command, two physical scenes with constrained vs free protrusion: achieved-shape channel differs; motor-request channel can remain equal.

### F3 — MEDIUM: suspended/resumed life has a blind 650ms actuator gap
**Source:** `SensorimotorMind.resumeEpisode` maintains `trialAction=currentAction`, `SymbioteMotorHeadV1.resumeLife()` clears `commands` and `lastAction`, `GameActivity.restoreScene()` only resumes senses; first `sensorMind.observe` makes its next choice after `elapsed>=.65s`.
Result: scene, 2.2 action and physical velocities survive, while living muscle commands are all zero until a fresh decision. Not a guaranteed crash, but introduces artificial discontinuity and loses attribution on that partially interrupted interval.
**Acceptance test:** save/reopen mid-action; compare earliest executed commands and no fabricated motor outcome after restart. Restore or deliberately reissue current action only with the proper pending-learning lifecycle.

### F4 — MEDIUM: world placement excludes only centroid, not full living membrane
**Source:** `WorldPlacement.free`: checks distance to `originX/Y` `< radius+68`; procedural spawns call `WorldPlacement.spawn(episode, creature.cx, creature.cy, ...)` without live `surface`. An extended pseudopod can reach new resources/solids near the centroid exclusion boundary at spawn time even when the center cannot. That can explain some instantaneous pickups without proving intentional approach; exact frequency requires telemetry.
**Acceptance test:** force 95–125-unit pseudopod and spawn around it; verify no actual solid/resource overlaps any real skin segment. Do not prohibit all near-food scenarios or change source identities.

### F5 — MEDIUM: passive grip flight-recorder is deleted on each app/load
**Source:** `GameActivity.load()`: `gripObserver=new GripContactObserver()` then `gripPrefs.edit().remove(GripContactObserver.KEY).apply()` even for ordinary scene restoration; `saveGripObserver()` writes only afterwards. Contradicts intent to keep the observer separate from named slots and may erase the contact forensic history precisely when relaunching to upload a report. This does NOT erase the organism's recurrent/motor weights.
**Acceptance test:** create a grip trace, close and reopen without reset, export; it retains the same previous samples/last-life state. Never cross-contaminate named profiles if persistence is intentionally separate.

### F6 — LOW/MEDIUM: measured 'work' is not physical work and is double-integrated in legacy total
**Source:** `SymbioteMatterV1.step`: `work += (|extend|+.25|lateral|+.1adhesive)*dt*.0002` (a cumulative effort-proxy), then `body.totalWork += work*dt`. The second term integrates an *already accumulated* work counter, so it grows roughly quadratically with long-term sustained effort and depends on life age. Metabolic cost in `GameActivity` is based on `freshWork=work-workBefore` and does **not** suffer this specific double integration. Rename distinct quantities and base displayed/legacy effort counters on incremental work.
**Acceptance test:** same single fixed motor interval evaluated early vs late in life must add the same legacy `totalWork`; no changes to genuine reserve debit.

## Risk/limitations requiring targeted tests, not yet proven bugs
- Non-atomic `SharedPreferences.apply()` cognition writes versus `saveScene().commit()` physical scene, and mid-death checkpoint ordering. Current named-slot path is checksummed, but crash at an arbitrary write boundary should be exercised. Do not claim corruption without reproduction.
- Eight-action sequential baseline/candidate windows in `OnlineBodyLearning` are **not** randomized matched-state A/B tests; world/spawn/weather and progressive scarcity can change across windows. Improvement estimates are confounded even for legacy bodies.
- Body/contact mechanical propagation through XPBD and floor traction can enable net translation in old physical benchmarks; do not tune drag/force/grip arbitrarily. Living Symbiote locomotion quality needs matched physical initial-condition sweeps.
- A nonzero learned model parameter count is not proof of valid world-model forecasts or independent causal competence. Heldout-vs-no-change prediction and actual net motion remain decisive.

## Action plan (NOT implemented by this audit)
1. Add deterministic executable regressions for F1–F6, ideally ensure tests fail on OTA154 for each confirmed defect.
2. Fix the causal contract of F1 (no false candidate attribution) and distinguish motor demand from achieved shape F2 without wiping saved weights.
3. Make resume safe F3, correct spawn/material overlap F4, stop wiping grip flight recorder F5, correct work accounting F6.
4. Run historical full JVM tests, Android build, source integrity hash gates and signed OTA pipeline. Keep 2.2 learned memory, same Android package/signature and 12 profiles. On-device A/B on same live ZERO afterwards.
5. Re-evaluate quality using net vs gross movement, pickups preceded by perceptual approach vs spawn adjacency, heldout model trust, cortex commands and actual metabolic cost.

**Audit state:** static findings reviewed; no game code modified, no OTA155 released by this report.

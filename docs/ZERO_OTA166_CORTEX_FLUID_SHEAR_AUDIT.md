# ZERO OTA166 — Cortex/liquid counter-rotation investigation and proposed physics correction

Date: 2026-10-16. Branch: fix/zero166-cortex-fluid-shear-coupling. Candidate only: do not install until independent full old+new JVM/Android QA and signed public APK byte verification both succeed.

## Direct evidence
Latest explicitly user-uploaded working symbiote report: zero-telemetry/reports/20261016-081817-167e8ec1eadc7 (post OTA165, md165); previous: 20261016-074020-165d6c73acc66 (OTA164, md160).
- Last complete life: 283 decision recorder rows; 279 materially measurable; mean cosine of chosen heading vs actual center displacement +0.03154; 132/279 move against chosen heading; transitions switch chosen action 64.9% (previous OTA164 last life 82.0%). Current 7 rows are insufficient to claim further trend.
- Cortex physical feedback saved: 905,569 physics frames, 28,616 closed feedback intervals, 17,672 trained intervals, mean signed realization ~+0.0381. More motor samples are not producing consistent chosen-direction locomotion.
- Signed saved shape area 2461.280U² (~0.0013% from exact rest). Radius maximum 40.1U, so frozen topology / mass leak is NOT the observed limitation.
- Actual instantaneous angular motion by least-squares centroid-relative rotation (sum r×(v-vcentroid)/sum |r|²) on latest snapshot: shell +0.123 rad/s and historic 10 interior MotorBrain parcels −1.339 rad/s, 64 fluid particles −0.755 rad/s. **Opposite signs and large interior shear** are observed at this instant; not an estimate of time-averaged angular behavior, and not proof of the sole cause of poor translation.
- User reports that a previously loaded organism with **the old body** still travels quickly. The stored controller may differ between saves, so that is an important qualitative A/B clue, not a controlled equal-brain parity experiment.

## Source-backed defect hypothesis
In SymbioteMatterV1.step(), cortex sites are friction-braked by real substrate damping, while equal and opposite local muscle forces accelerate 74 inner parcels. Its XSPH velocity mixing only couples pairs of INNER particles. It does not exchange viscous momentum with the 96 perimeter sites. This can sustain counter-rotation and internal slip while cortex friction absorbs external torque: the material is coded like an internal fluid and an almost disconnected shell.

## Candidate fix: real cross-layer viscous coupling
Introduce velocity exchange between each of 74 mobile parcels and its nearest *actual* shell patch within 20 world units. Pairwise momentum transfer is weighted by their masses (shell m=.5/96, parcel m=.5/74), preserving total translational momentum for unheld matter; real object-anchored shell points instead transfer shear to the object/substrate and are not moved by fluid. Uses only distance, relative velocity and physical grip; no brain direction, programmed gait, translation impulse, target or extra reward. The actual shell position is integrated for the exchange before area/volume and topology constraint projection. Unchanged old-body path, neural parameters, save schema, 12 slots, and signed source guards.

## Acceptance gates
- Direct pure pair tests: exchange actually damps near-surface internal shear; pair momentum balance within numerical tolerance; no attraction outside cutoff; non-moving motor produces no free drift; physically anchored patch never slips.
- All existing 161-165 world/matter/sensory/motor/holdout and unchanged old-body locomotion regressions MUST pass. Any regression failing means revisit candidate rather than weaken tests.
- After deployment, ask for actual 166 data, derive comparable observed inner vs shell omega, cosine chosen vs actual, sustained action rate, net/gross motion, prey/energy, geometry invalid and clipping.
- No claim that the issue is cured before the real same-brain phone telemetry and comparison.

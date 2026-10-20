# ZERO376 — real body inertia, course-change audit (20 October 2026)
**Research only / NO APK, NO OTA, NO stable merge.**

## User-observed symptom
In signed OTA375 the biological gel turns and deforms but its mass drifts onward like a balloon. The user's physical observation is about ACTUATION/CONTROL, not merely cognitive source discrimination or GRU energy prediction.

## Real smartphone telemetry, current
`zero-telemetry/history/20261020-130507-2906bb269cdd6.json` was explicitly shared by the user's Android phone at 13:05 UTC, installed `3.0-ota.375-test.1` versionCode1186, GRU checkpoint valid:
- 14,066 complete GRU decisions, 11,255 train, 2,811 independent heldout.
- Real motor diagnostic: 14,045 physical decisions; 8,080 aligned, **3,590 opposed**, 347 near static, 3,334 externally corrected.
- Net physical progress along commanded azimuth=5,680.193U, gross physical travel=16,155.594U: **35.16% signed/gross**. This is a relative motor-direction metric, not an estimated behavioral success probability.
- 4,949 measured/stored physical stalled decisions, **only 3/8 validated motor direction banks**, energy retina associations 9 positive and 21 negative. Old controller memories in the saved profile are read-only archival and are not evidence the GRU physically steers.
- 21 life history windows with mixed source events; the phone's inability to change trajectory makes further hue-only fixes inadequate.

## Inherited original-world 4-axis motor QA is SCIENTIFICALLY INCOMPLETE
`Zero364MotorBridgeAndroidTest.genuineGelCanMoveOnAllFourChosenAxesWithoutExternalThrust` was approved after testing a fresh body separately for EAST, NORTH, WEST and SOUTH, **holding the chosen direction constant for 120 decisions × 24 physics frames**. It proves longitudinal output from rest but says absolutely nothing about braking/reversal after the body already gained angular/linear momentum. The older pre-symbiont controller had different effectors; direct comparison is a baseline, not proof that the current 170-node gel can turn.

## Code-backed causal concern beyond being "still learning"
`SymbioteMatterV1.step`: each of 96 living sites gets radial/tangential actuator forces; equal-and-opposite material reaction drives 74 mobile interior nodes. Fluid shear, per-site traction, finite-difference post-projection velocities, and body COM are material/inertial, not teleported. A changing GRU intent is NOT itself a force on the centroid. The single owner currently mixes **75% frozen historical V2.2 motor peripheral** and **25% plastic GRU V3 cortex**. It may generate a change of shape while the COM keeps moving from prior velocity.

`Zero3InstrumentalCreditV371` estimates 8 direction banks based on `mx=actualDeltaX*0.30/seconds` and compares validation error against `baseline=mx²+my²` (zero-displacement prediction). This uses no PRE-decision COM velocity nor current inertia. A COM that continues coasting can validate a motor bank spuriously. The correct baseline for causal motor authority must be **passive trajectory at matching velocity**, not stationary zero. Therefore `verifiedPhysicalDirections=3` is NOT 3 proven voluntary course changes. Future V3 movement ranking must condition on PRE velocity, resulting POST velocity, actual contact/weather correction, and verified independent braking differential.

## Specific new original Android A/B
`Zero376DynamicTurnOriginalGelAndroidTest`: same zero-trained GRU V3 cortex and frozen 75% low-level peripheral in actual `SymbioteMatterV1`, no energy pickups, obstacles, storms or scripted propulsion. Three physically identical trials begin from rest and accelerate EAST for 45 real decisions (24 × 60 Hz physics steps each). Then EAST→EAST, EAST→WEST, EAST→NORTH for 28 more decisions. Record *actual material COM* X/Y displacement, pre-switch velocity, transverse and reversal consequences. QA fails rather than reclassifying 4-axis tests as steering proof if reversal remains forward or if turning cannot outperform continued east by 2 material units. This deterministic motor QUANTITATIVE A/B is a diagnostic, not proof of autonomous source pursuit.

First run failed solely on test's Java `String.format` placeholder mismatch **after physics step**, not from motor runtime; corrected without touching production code and rerun on exact research branch. Do not claim original A/B result until CI log prints `ZERO376_TRUE_PHYSICAL_TURN`.

## RESULT: definitive original Android A/B with measured velocities (CI 39429255810 SUCCESS)
**Physical turning CAN work, but immediate steering is delayed by inertia even in this clean motor test.** This fully instrumented run used three IDENTICAL warm-ups: EAST commanded for 45 decisions × 24 genuine physics frames at 60Hz; exact same initial measured X displacement **+61.74731U**, pre-switch COM velocity **(+3.96383, −2.03260)U/s**. The only independent variable after warm-up was EAST, WEST or NORTH for 28 genuine additional decisions (11.2 simulated seconds).

| Requested direction after east | FIRST 0.4 seconds, real X | FIRST 0.4 seconds, real Y | 11.2 seconds net X | 11.2 seconds net Y |
|---|---:|---:|---:|---:|
| Continue EAST | +1.59773 | −0.73211 | +42.22464 | −30.07537 |
| Reverse WEST | **+0.84956** | −0.79015 | **−33.56071** | +16.03445 |
| Turn NORTH | **+0.89500** | −0.68638 | +25.69326 | **+30.92417** |

**Interpretation:** For the FIRST 0.4s AFTER A TURN, physical body continues EAST regardless of the WEST or NORTH intention; even an EAST-to-NORTH command initially moves negative Y despite later positive Y. But after sufficient same-heading decisions the **unmodified material solver can** reverse East to net West, and turn to net North: no absolute impossibility to turn, no need for fake COM steering. The 2.2 frozen peripheral plus V3 cortex can actively change physical results compared with keeping east. This does **not** establish the user-trained phone checkpoint can choose the correct corrective actuation, or that a red touching radius affords 11s to brake.

**Scientific causal failure in active V371:** it stores 8 unconditional action→net displacement means; its heldout baseline is **ZERO motion**, not continuation of measured current COM velocity and local resistance. It never conditions its predicted motion or uncertainty on PRE-decision `body.cvx/cvy`, rotational/inertial state, or friction. Hence its apparent `motorVerified(3/8)` on phone can be wrong for turning and stopping. A controller needs a learned inertial transition (PRE velocity / tissue state / chosen action → POST velocity or velocity CHANGE), genuine matched passive-coast heldout baseline, and an action selection that accounts for braking TIME, not only instantaneous preferred photon direction. No scripted anti-red or energy-teleport force. 

**QA trace:** [GitHub Actions 39429255810](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39429255810) confirms the numbers above plus **all historical real Android regressions**. The first V376 run had ONLY a QA logger `String.format` placeholder error, fixed without altering production code. Signed OTA375 remains unchanged; no unverified OTA376.

## Next repair, ONLY after cause is established
1. Determine steering capacity with physical-body momentum already present, not from rest. Separate typical passive carry from weak or opposing motor response.
2. Preserve material force conservation, 96-site cortex, 64 interior particles and V319 grip. Never add a teleported COM target, goal force, scripted turning or second actor.
3. Repair only the proven limiting layer: low-level force/traction/shear if original body cannot turn even with fixed true intent; or action-conditioned `PRE velocity + chosen muscles → actual POST motion` learning and comparison to coast baseline if physics can turn but GRU chooses/values incorrectly.
4. Require sequential turn, moving-wind turn, autonomous resource approaches, mirrored target scenes, 10k+ decisions and checkpoint compatibility on current phone. Four static axes and conventional 27/37 test suites are insufficient.

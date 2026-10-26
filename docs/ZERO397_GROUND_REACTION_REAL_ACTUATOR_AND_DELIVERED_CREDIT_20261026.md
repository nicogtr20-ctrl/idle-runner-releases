# ZERO397 — Motor root cause corrections, genuine saved S21 A/B (26 October 2026)

**Experimental branch:** `fix/zero397-motor-evidence-safety`. User-installed signed V388 (build 1196) and all 12 brain saves remain untouched. Do not publish as fixed until the genuine unscripted original 2000-decision gate passes. Read together with `ZERO389_V388_REAL_PHONE_WALL_AND_LOCOMOTION_ROOT_CAUSES_20261025.md` and `ZERO392_393_394_CAUSAL_MOTOR_S21_FINDINGS_20261026.md`.

## Confirmed physical defects, exact source repairs

**Actuator inward inversion**: `SymbioteMatterV1.actuatorDirectionAt` previously blended outward body radius with actual concave polygon normal in a way that could become radially negative (one site ~107.6° from radial on the original S21 V388 posture). V397 enforces a positive radial contribution after the *true* local normal/radial blend, preserves the tangential component, and never uses a chosen world heading, food location, or scripted gait. This change **is active in V397**. Android genuine saved posture, all 96 normals + whole-body rotation equivariance: PASS.

**Core dragging floor-loaded skin AFTER friction pass**: `SymbioteMatterV1.step` computes per-site ground viscous drag, then `exchangeCortexFluidShear` adds fluid impulses to the just-damped surface without applying grounded mobility. V397 can pass its actual per-node drag mobility to the shear exchange: a loaded membrane accepts only the mobile fraction; the missing momentum goes to the physical substrate instead of mysteriously to a ghost core. Weighted shell/inner momentum accounting verified on Android. This is **OFF in production** and available ONLY via `configureGroundReactionShearLabV397(true)` until multi-posture acceptance.

## Measured real-user V388 33,312-decision brain, 170-body material

[Physical source + real Android run 40409603561](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40409603561) — 3/3 PASS for execution/invariants, NOT locomotion certification. Four neural commands × four independently physical conditions: original S21 posture free; original same posture with true training wall; second independent saved S21 posture free; second posture with all starting velocities stopped (heldout). Original full 512 cortical weights and 64 recurrent states restored for each trial. All interventions physically integrated for 120 frames each.

| Condition, intended EAST | Original response dx | V397 floor-reaction dx | Gain |
|---|---:|---:|---:|
| Original S21 training pose, no wall | -2.615158 | -2.634857 | -0.019699 |
| Same pose, real left wall | -0.427094 | -0.418137 | +0.008957 |
| Independent saved S21 free pose | -0.205566 | -0.250488 | -0.044922 |
| Free pose with arrested starting fluid/shell momentum | -0.118683 | -0.180969 | -0.062286 |

Other directions show small mixed changes, some slightly better, some slightly worse (full 16-cell log in linked Actions). Therefore **do NOT enable blanket floor-ground-shear rewrite in actual ZERO**; it does not solve EAST and marginally worsens both independent free poses. Test confirms root cause cannot be solved by generic extra friction.

## Neural root cause under laboratory test

`Zero3LearningFoundation.AdaptiveCortex.learn` historically credits a requested random `noise[out]` even when the physical motor command is **unchanged** due to tanh saturation, user effort gain or hard [-1,+1] clamp. V397 adds an opt-in (lab-only) per-output transient `deliveredProbeV397` that compares actual exported efference with the identical unperturbed same-substep cortex transfer function. Credit is zero if the real motor did not change. It changes neither original GRU64/194 inputs nor 512 trained weights nor V356 checkpoint structure/CRC. This is physical *effector eligibility*, not proof the true muscle caused COM progress: confounding baseline/obstacles still require further tests. Scientific Android 2/2 PASS [run 40410498474](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40410498474): synthetic true motor-saturation test confirms ZERO phantom motor eligibility; original restored 33,312-decision GRU3 + 128 *real* full gel episodes: nominal noise EAST -2.615158 -> -2.625046, ACTUALLY DELIVERED motor eligibility EAST -2.615158 -> -2.628037 (slightly WORSE); second saved posture EAST after learning -0.192963 vs -0.194733 (WORSE). Although the eligibility fix is mathematically legitimate, it does NOT repair directional locomotion by itself. **Keep lab-only; DO NOT turn this flag on in production as an alleged movement fix.**

[Multi-context motor-recovery physical selector run 40410125503](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40410125503) retained the original S21 neural GRU and tested recovery of learned cortex weights. EAST-only `0.25` fraction passed training conditions and an independent free-rest holdout, holdout gain +0.084442. SOUTH/WEST/NORTH approved zero recovery. **This is a checkpoint/posture-specific laboratory result, not permission to install a global 25% reset or a prefixed EAST motor steer.** Not in production.

## Validation and release gate

- Android original saved S21 material 3/3 PASS: run 40409173924, complete measured metrics run 40409603561.
- Original sensor/proprio/cortex motor/tactile/cold-resume regression suite PASS: run 40409718760, latest rerun `zero397-regression-original-android.yml`.
- True 128-action original trained cortex delivered-probe A/B: `zero397-delivered-neural-probe-ab.yml`; PASS technical; mixed/worse real motor gains, production activation rejected.
- **Original game natural unscripted >=2000 decisions**: `zero397-original-2000-locomotion-gate.yml`; requires >=12% signed/gross efficiency, actual blue pickup, red damage trend and no neural errors. Must check and NOT waive this failure.
- Never reset user GRU64 or 12 slots; no 2.2 motor 75% fallback, static east override, auto-grip, food-directed force, arbitrary velocity boost, unsigned OTA or live release from merely green unit tests.

## Next scientific action

If original 2000 fails (as V390 did at 9.69%), isolate true delivered-muscle perturbation vs body effect with matched physical state and pose-conditioned coast, keeping the sole GRU3 owner. The floor-shear perturbation is falsified as a general solution; do not retune it by eye. Incremental motor/credit corrections must pass the genuine phone full-state test in multiple poses, heldout and the original blind game before APK/OTA.

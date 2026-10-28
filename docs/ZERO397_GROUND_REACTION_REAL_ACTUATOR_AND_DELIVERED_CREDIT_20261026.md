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

## Original 2000-decision autonomous game benchmark: still gated

[First V397 genuine original-game run 40410760759](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40410760759) **FAILED the infrastructure/lifecycle test**, not a numeric motor-efficiency gate: after ~102s, the test's `originalV366MotorDiagnosticForQA()` was empty, causing `missing genuine V361 motor statistic grossDistance` before the 2,000-decision signed/blue/red result. Its first sampled motor summary was decisions=0 at startup. In source, that diagnostic can return empty **only if the original Lab or original GRU3 object is null**, whereas the original diagnostic itself always contains `grossDistance`. Do not label this as real locomotion success or failure; missing owner is independently unacceptable. V397 adds immediate per-burst `originalV356NeuralOwnerForQA()` assertions and periodic `ZERO397_AUTONOMY` logs with Activity destroyed/finishing and brain error; workflow now archives ActivityTaskManager/AndroidRuntime logs. [Reproduction rerun 40411679228](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40411679228) must be inspected for actual owner loss. Until authentic successful >=2000 metric run, **NO APK or OTA**.

## 28 October 2026 continuation: V398 real S21 material rheology NEGATIVE

Experimental branch `lab/zero398-s21-interior-shear-motor-dynamics`; [CI 40716278729](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40716278729), **Android 2/2 PASS technical measurements, material hypothesis FAIL**. Compared 10 parameter pairs across original training free, original training with west wall, independent S21 free, and independent at-rest free, each of 4 physical headings: 160 genuine 120-frame Android trajectories plus original baselines. ORIGINAL production defaults unchanged: interior damping 2.3/s, cortex-fluid shear .55. The experimental parameters are lab-only, not accessible in production.

- Fluid damping 6/s, 12/s, 24/s at original .55 shear gave no convincing improvements: training worst gain -0.486, -0.860, -1.097 respectively, heldout worst -0.526, -0.891, -1.121. Almost all 16 combinations degraded.
- Shear increased .55→1.20, unchanged fluid drag 2.3/s, gave EAST positive minimum gain +0.097 across all four physical contexts and average EAST +0.144, **but** WEST mean -0.271 and NORTH mean -0.362, 9/16 contexts deteriorated more than -.07. Increased shear 2.50 gave EAST mean +0.328 but WEST mean -0.981 and NORTH mean -0.807, 10/16 materially worse.
- **ZERO candidate achieved reliable EAST motion**: even worst posture EAST at new shear 2.50 was -2.460 instead of -2.615. A slightly less-negative result is NOT steering EAST.

Thus stronger friction, stronger inner damping, or stronger shear cannot be deployed as a direction-independent physical fix. The issue is context-dependent motor coordination/credit. No rollout, GRU64, named save, production on/off default, APK or OTA changed. Next isolated V399 tests **already implemented** V332 delayed sector credit on genuine restored S21 physics and 512 contiguous decisions per arm. Its 3-arm Android CI must be read before any live integration.

## 28 October 2026: original autonomous V397 definitive failure, not lost GRU owner

The rerun with explicit Android Lifecycle/owner forensic **completed full 2,002 actual original GRU3 decisions** with `owner=true`, `destroyed=false`, `finishing=false`, `brain3Error=""` throughout; [run 40411679228, failure is SCIENTIFIC motor gate](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40411679228). Exact original game: blue=2, red=1 (redFirst=0, redLater=1), signed progress **63.95298**, gross **726.92980**, intent-aligned efficiency **0.0880** (8.80%), below preregistered 12%; actualX=+459.517, actualY=+193.144. This excludes owner loss as the explanation of actual poor steering. The *first* 2000 attempt 40410760759 genuinely lost its available diagnostic and had NO usable final motor score; do not generalize that failure. The corrected test shows one 2,002-decision autonomous original life gate fails scientifically. The app must **not** be released as locomotion repaired. The 3/3 physical invariants and 2/2 delivered-credit lab successes cannot override this long-run fail.

Follow-up experiments:
- `lab/zero398-s21-interior-shear-motor-dynamics` [run 40716278729](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40716278729) confirms increasing generic core friction/shear worsens other true motor directions.
- `lab/zero399-s21-local-delayed-motor-credit` physically tests preexisting V332 delayed spatial credit on the real 33,312-decision S21 motor, original 170-material geometry and four heldout contexts, vs original V390 global and FROZEN. Report its results separately, and do not mistake scheduled lab intentions for autonomous choices.

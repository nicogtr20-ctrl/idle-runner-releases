# ZERO399 — Genuine Galaxy S21 33,312-decision cortex, original V332 delayed local motor credit

**28 October 2026.** Experimental only, source branch `lab/zero399-s21-local-delayed-motor-credit`. This protocol is documented after initial source implementation but **before reading its completed physical metrics**; do not falsely present it as a preregistered, statistically blinded independent clinical study.

## Rationale and prior real evidence

- The user's original V388 33,312-decision GRU3 body cannot reliably follow motor intention; V397 2,002 autonomous original decisions have signed/gross **8.80%** against required **12%**, blue2 red1 [run 40411679228](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40411679228). The Android actor and full original owner survived all 2,002 decisions.
- V392/V393 real-S21 motor Jacobian proved real local CH0/CH1/CH3 physical sensitivity; the signs and effective gains change with physical posture.
- Earlier V331 [576 independent single-site perturbations](ZERO30_V331_LOCAL_ACTUATOR_COUNTERFACTUAL.md) showed 481/576 late t12→t36 response dominant. Original V332 `AdaptiveCortex.learnV332` is **already implemented**, lab-only. Earlier V332/V333 BLIND improved locomotion in some synthetic scenarios, but failed BOX/bilateral and physical integrity gates; do not call those features missing or already eligible for production.
- V397 clipped/delivered-effector noise lab fixed a specific false-eligibility algebra but failed improvement on the real restored cortex [run 40410498474](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40410498474); remain lab-only.
- V398 160-sample S21 material rate sweep failed multicontext; **do not enable stronger blanket friction/shear** [run 40716278729](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40716278729).

## Exact V399 test

Source: `app/src/androidTest/java/com/overmind/zero/Zero399S21LocalDelayedLearningAndroidTest.java`.
Full saved original V388 cortex including its **512 weights + all 64 recurrent states**, 170 original body material, original 12-slot data read-only. Three independent copies of the same checkpoint (never writes to user): OLD_GLOBAL, LOCAL_DELAY_V332, FROZEN.

- Training: 32 independent training episodes ×16 **contiguous** 24-frame original solver decisions = 512 decisions/arm. True original user S21 and second S21 free pose alternate, heading is held constant for 16 successive decisions so delayed credit does not mix unrelated headings. All four headings balanced and **scheduled in laboratory only**; the actual app never receives scripted headings.
- Exactly one CH0/CH1/CH3 motor-sector noise probe in each train decision as already implemented V390; V319 grip unchanged. OLD uses existing global score and moment-aware V390 wall gate; LOCAL performs already approved V332 physical sector evidence and `previousNow + 0.82*next` after the next actually integrated POST; FROZEN has no updates. NO delayed credit across artificial scene resets. No feedback from a known future.
- Test scene uses actual `GameSceneSnapshot.workingPosture`, `SymbioteMatterV1.step`, `SymbioteWorldStepV171.interior`, genuine 96 membrane points and 74 inner material masses. Original GRU3 **cortex** is restored; headings are scheduled only to isolate low-level mechanics, so the 512-action test **is not** 512 independent decisions of the real autonomous GRU executive.
- Holdout: no exploration or learning; 4 physical situations ×4 directional intentions ×120 true solver frames: original free, original real training wall, second saved free, and second free with original momentum arrested. A distinct matching BEFORE evaluation tests existing original cortex. At heldout start, the **same fully restored 64 recurrent cortical states, RNG and baselines from original S21** are used in ALL arms and all baseline comparisons; only the trained 512 efferent weights are transplanted to the matched evaluation instance (no `forkForEvaluation()` cold-state confusion). This isolates changed synapses from ordinary motor-state progression. Heldout exploration is entirely OFF. Frozen arm is required to match baseline to numerical tolerance in all 16 contexts. It does NOT claim to measure the natural resumed changed-slow-state cortex independently; that is a separate task.

## Acceptance and limitations

Technical PASS requires complete real Android instrumentation, no topology crossing/gel invalid, finite response for all 16 heldout situations, genuine updates in OLD and LOCAL and zero in FROZEN. **Locomotion** only advances if a proposed method improves signed progress in independent real S21 postures with positive EAST net movement where EAST is commanded, does not regress WEST/NORTH/SOUTH or energy/grip, then survives **2,000+ truly autonomous** original GameActivity GRU3 decisions (not the scheduled lab trajectories). A lab-green run means only execution unless those physical measurements separately pass.

No source in the actual GameActivity, original saved brain, 194 sensors, 512×8 cortical model, CRC, 12 slots, OTA or signed APK is to change from this test. Do not enable the local-delay rule in production unless it is generalizable and preserves original manipulative abilities.

### Important first-attempt measurement artifact (caught before promotion)

Initial [run 40717210384](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40717210384) was technically GREEN but its heldout accidentally continued exploratory noise and copied 64 slow-states *after training*. As a result even FROZEN, with zero synaptic updates, displayed mean heldout change -0.0778 and apparent EAST regression -0.5952. **This cannot be attributed to learning**: it mixes state evolution, noise cursor and weights. A subsequent amendment disabled heldout exploration; the final correction at commit `422b301ae3949140be10e99749b46ecc35c52570` gives every arm the identical original recurrent state and tests frozen exact equality. Only run results at/after this commit may support the effect of motor synapse plasticity. Do not quote earlier numbers as correct control estimates.

## Verified outcome — final corrected S21 Android run (28 October 2026)

**[Run 40718235625](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40718235625): Android 1/1 PASS for true measurements.** This is commit `422b301ae3949140be10e99749b46ecc35c52570`, the *first* run that both disables heldout motor probing and restores identical original **64 recurrent motor states/RNG** in all three compared arms. Frozen arm matched original **exactly in all 16 cases (0.000000 gain)**, proving the previous confound was removed.

| Arm, original trained V388 33,312-decision cortex | Motor weight updates | True S21 conditions with gain >0.07 | Conditions with loss <−0.07 | Mean gain across all 16 contexts | Minimum gain |
|---|---:|---:|---:|---:|---:|
| V390 old global score | 512 | 0 | 0 | +0.005474 | −0.015259 |
| Already-implemented V332 local+0.82 delayed | 480 | **5** | **0** | **+0.046417** | −0.049517 |
| Frozen weights | 0 | 0 | 0 | exactly 0 | exactly 0 |

EAST directional signed material travel after training on original free pose: original −2.615158; global after −2.606674; V332 local after **−2.492630**. EAST on original real west wall: original −0.427094; V332 −0.378571. EAST in independent user free pose: original −0.205566; V332 −0.171112. EAST with free pose all velocities stopped: original −0.118683; V332 −0.091919. All EAST responses **remain NEGATIVE despite a favorable local gradient**. True S21 physical motor coordination has NOT been restored.

One other direction-context deteriorates by −0.049517, below the test's magnitude threshold but still an actual loss; never claim no regression at all. V332 improves some real original cortex responses at unchanged original body physics and neural memory; this is bounded 512-lab-decision evidence, not durable autonomous survival or complete locomotion.

**No runtime adoption, APK or OTA from this result.** The next independent branch `lab/zero400-original-game-local-delay-motor-credit` tests the actual autonomous original GRU3 cognitive decisions with this existing delayed local rule as laboratory-only opt-in, rather than scheduled user-free headings. It must pass original >=2,000 decision game gate, actual energy contacts and regression before any promotion.

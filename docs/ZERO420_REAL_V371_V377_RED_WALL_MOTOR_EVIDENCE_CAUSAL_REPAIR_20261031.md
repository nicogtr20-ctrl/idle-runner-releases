# ZERO420 — actual original GRU3 higher-body motor-response credit safety experiment
Date: 31 October 2026. **Research ONLY, NOT approved for any APK/OTA and NOT merged to stable**.

## Audit correction and source-proven defect

The ZERO419 broad audit correctly flagged inconsistent muscle/cortex/GRU3 information but understated the precise higher-order wall bug. The call chain `Zero3OriginalBrainV356.observed → Zero3RecurrentBrain.witnessedMaterialMotionV371 → Zero3CognitionV353.physicalMotorV371 → Zero3InstrumentalCreditV371.complete` passes `physicallyTouchedSolidDuringDecisionV380` as `physicallyBlocked`, but **`complete`'s actual clean/training condition does not check that flag**. It trains the `V377` action→velocity/inertia predictor on normal wall collisions as long as another disturbance is not flagged. A front wall can therefore be stored as a valid actuator response, even though original V401 excludes that same front-wall response from lower-level cortex gradient. It is worse than a simple low-level/high-level disagreement: high-level map can acquire bad motor trajectories.

Second source-confirmed gap: original RED gem contact executes `creature.vx[i]*=.6f; creature.vy[i]*=.6f`. Lower V401 excludes the damage while V371/V377 does **not** receive `actualDamageDuringDecisionV359` in its trust criteria. Thus RED/world-caused slowing may be incorporated as evidence of motor responses. The negative contact learning in `V369/V374` and the true survival/terminal reward MUST remain active.

## Single isolated intervention, preserves original learned brain

Branch `lab/zero420-original-high-level-motor-evidence-eligibility` based on documented ZERO419 audit, whose source is exactly ZERO418 code. For fair comparison with V417, the original autonomous tests explicitly activate V417 real COM velocity, but **do NOT activate the V418 ablation** (which failed 10k). V420 is the ONLY changed learning variable in the behavioral trial.

- Add **pure** `Zero420MotorMapEligibility.fromRealDecision`, independent of physics, with immutable real observations: physical action evidence; no storm, teleport, external correction, aborted/terminal action or *actual* material damage; no wall unless V414 strict verified real rear-only retreat with positive COM progress. A forged rear eligibility without a real wall is rejected.
- Apply it at `Zero3OriginalBrainV356.observed` to the **sole original GRU3's** `V371/V377` high-level empirical motor-response map ONLY when test explicitly enables transient `safeMotorMapEvidenceLabV420` at a genuine original decision boundary. Negative red contact valence, 12-head V353 predictor, original actual survival energy/reward, terminal injuries, high-level action choice, low-level original V417 muscular learning remain unchanged.
- Auditable counters `safeMotorMapAccepted`, `safeMotorMapExcludedWall`, `safeMotorMapExcludedHarm`, `safeMotorMapExcludedOther`. V356 saved 194D input, 64-unit GRU, 512 motor weights, 64 slow local states, exact saved S21 33,312 decisions and 12 profile layout, V319 independent physical grip and CRC remain untouched.
- New original V371/V377 Android `Zero420MotorMapEligibilityAndroidTest` makes distinct positive/negative rear/front/RED/storm/terminal claims and checks via genuine original V377 internal encoding that harmful/front-wall events CANNOT change trained inertial state while verified rear-only retreat can.

## Live real Android acceptance contract (unchanged)
- Original saved 33,312-decision S21 true training room body: minimum 10,000 **continuous self-generated** decisions, original game physics, 25% net signed/gross, 9/10 adjacent 1k windows >=10%, zero inverse windows, >=5 newly actually collected blue, original V319 physical grip, no reset.
- Original newborn wild real red/blue: >=10,000 continuous decisions, >=12% actual signed/gross, actual blue pickups, red contacts in latter 5k fewer than former 5k, total red <=2× actual blue, no synthetic red/blue or fixed direction.
- Original Android regression *and* V420 5-case causal gate + 33,312 saved user S21 512 synaptic weight/V356 checkpoint CRC integrity; all green.
- Compare directly V417 S21 11.2869%, 4 blue, 2 backwards 1k windows; V417 wild 58.86% but 69 red/5 blue. V418 had **worse** S21 6.4039%, 4 blue, 3 opposite; wild 44.64%, 54 red/3 blue. All four behavioral runs FAILED.

## Why this is only a laboratory hypothesis, not a promised cure
The above source bug is real. Whether its correction improves actual autonomous red avoidance and stuck-wall physics requires the full real 2×10k runs. If the red rate or S21 motor target fails, V420 stays unpublished. The broader sensor audit issues (actual delivered perturbation vs raw perturbation; 8 compressed rays vs 16 chamber rays; compressed body posture; limited three-candidate coverage; optical log center vs protruding eye) are tracked but intentionally **not altered** in this V420 ablation.

## Repeatability
Existing V417 real user S21 and wild Android test class names and workflow filenames were reused with the exact original thresholds, enabling `GameActivity.originalV420EnableSafeHighLevelMotorMapForQA()` instead of V418 experimental switch. The source-branch Action names are ZERO420. Mandatory workflows:
- `.github/workflows/zero417-original-real-braking-credit-regressions.yml` — now includes V420 5 tests
- `.github/workflows/zero417-s21-original-braking-credit-checkpoint-integrity.yml`
- `.github/workflows/zero417-original-user-s21-10000-real-braking-credit.yml`
- `.github/workflows/zero417-original-wild-10000-real-braking-credit.yml`

Do NOT cherry-pick or merge if any criterion fails; do not tune thresholds after observing results. 

## RESULTS: REAL Android 10,000/10,000 DONE — ZERO420 NOT APPROVED

Four GitHub Actions completed on October 30/31 2026:

1. [Original Android regressions + V420 five-case physical motor evidence](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41144716267): **PASS**. All 5 new V420 cases and established GRU3/motor/tactile/cold resume regressions pass.
2. [Original 33,312-decision S21 trained brain 512 weights/V356 CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41144711798): **PASS**.
3. [True original S21 user 10k](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41144702803): **FAIL**. Actual 10,004 independent decisions from 33,312, 4 additional blue pickups, 0 additional red (room contains blues only), efficiency **0.112869**, signed **336.45490**, gross **2980.92837**, 1851 cortex weight updates; **2 opposite windows** (4 and 9), only **7 of 10** windows >=10% (windows 1,2,3,5,6,8,10), expected >=9/10 and 0 opposite. Debug opt-in **VERIFIED active**: `safeMotorMapLabV420=true|safeMotorMapAccepted=3471|safeMotorMapExcludedWall=5421|safeMotorMapExcludedHarm=0|safeMotorMapExcludedOther=1112`. V417 comparator on same S21 **also** had 0.112869 efficiency, 4 blue, 1851 updates, same wall-window metrics. This proves the V420 causal exclusion, despite removing 5,421 wall-affected higher motor samples, **did not measurably change** this S21 behavior. It does NOT prove high-level motor learning works nor that local cortex can steer.
4. [True original newborn wild with physical red/blue 10k](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41144707024): **FAIL**. Actual 10,003 autonomous decisions, **5 blue** and **53 red** (RED still 10.6 times blues), signed/gross **0.486611** (48.6611%), signed 2156.48708, gross 4431.64365, 4,745 cortex weight updates. RED first 5000 = **19**, second 5000 = **34**: harm frequency **increases rather than decreases**, precisely the hard-gate assertion that failed. V417 comparator wild = 5 blue / **69 red**, 58.8634% locomotion, so V420 had fewer overall red contacts but worsened locomotion. Debug opt-in VERIFIED active: `safeMotorMapLabV420=true|safeMotorMapAccepted=6794|safeMotorMapExcludedWall=1052|safeMotorMapExcludedHarm=2143|safeMotorMapExcludedOther=14`; `independentDecisionCreditLab=false` means V418 ablation was OFF.

The original V417 global S21 baseline and V420 global S21 are **numerically identical**, though V420's motor eligibility counters show a real difference. This is important evidence that the blocked high-level samples are NOT the sole missing control loop; a separate source-level issue with local cortex action influence, candidate/risk planning or sensory encoding remains. The S21 room has no new red, so no safety claim from its inherited historical red=90.

**Decision:** NO APK, NO OTA, NO stable merge. Safe code fix is retained in the LAB branch as a tested negative behavioral result, without pretending it cured locomotion or red avoidance. Next research must inspect read-only candidate score distribution (3 actual offered actions per V417/V418 red event), delivered muscle perturbation vs raw V332 noise, wall contact and independent physically verified steering. Do NOT change original user's saved brain.

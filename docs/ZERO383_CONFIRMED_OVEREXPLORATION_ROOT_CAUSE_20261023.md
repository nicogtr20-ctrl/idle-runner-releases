# ZERO383 — ROOT-CAUSE MOTOR INTENT OSCILLATION, saved user V382 — 23 Oct 2026

Source: user-triggered `zero-telemetry/reports/20261023-171621-32434c07e66f0` on branch `zero-telemetry`, Android `3.0-ota.382-motor.1`, original GRU64 checkpoint V356 valid, 10,294 decisions. Never publish raw user brain, weights, flight or signing keys. Original V382 signed OTA stable unchanged.

## Behavioral evidence from actual 382 flight recorder
256 genuine completed decisions 10038..10293. 68 changes >90 degrees (vs 33 in earlier V381 256-decision window). 191/256 body advances along chosen intention (>+0.2), 39 opposite, 26 near zero; 63 of 68 large turn events not tagged external interference. For large turns, mean signed progress +0.162 U/decision vs +1.408 when direction changes <20 degrees. PRE-momentum-subtracted large-turn progress +0.221 U/decision, so the body does respond physically, but inertia plus brief ~0.44 second intentions prevent reliable turning. Brain physically saved successfully across V381→V382 (9997→10294 decisions), so not a reset.

## Actual motor matched A/B EXCLUDES a false physical diagnosis
Successful actual saved model A/B GitHub Actions `39995703029`: user-trained GRU V382 + same frozen muscle weights, exact sequence of 256 3-decimal-normalized original user headings and variable durations, 170-node actual gel, identical free no-weather arena. No author-made reward/target; no trained weight changes.
No V382 temporary drag release: 181/256 forward, signed progress 208.9335, gross 367.1173, hard-turn signed 5.1779.
Current V382 release -0.04: 195/256 forward, signed progress 232.4591, gross 384.4742, hard-turn signed 9.9600.
Therefore **do not simply delete the V382 physical floor turn relaxation**. It helps in this simplified but matched test. Test does not prove full-world gem acquisition with weather.

## Precise high-impact decision construction defect
Read actual `app/src/main/java/com/overmind/zero/Zero3RecurrentBrain.java` `choose`, around L240..L309:
- `learnedBearing=cognition.hasVisibleContactKnowledgeV369(x)` is a flag for **having experienced wavelength consequences**, not for **being able to move the real body to a chosen bearing**.
- Yet that flag immediately increases proposals from 3 to 7, scales angle noise `k>=3?4f:2.5f`. A repeated stall expands to 9 options up to 7x. Regenerates **every 0.25–0.65 seconds**, without motor transition time/cost/physically confirmed maneuverability condition for sharply different sampled headings.
- `cognition.select` and executive choose among alternatives based on predictions and limited contact valence; a new lateral/opposite heading is allowed even while the preceding acceleration/braking is still incomplete. Heading preference can churn much faster than the 170-mass body can respond. This is NOT a script forcibly dragging the balloon; it is an unregulated exploration/actuation-timescale mismatch. The sampler can routinely override the raw trained actor's heading.
- Controlled actual saved-user cognition instrumentation `Zero383UntrustedCostSavedBrainAndroidTest.measureReal382ActorAuthorityVsExplorationNoise`, 64 cloned saved-GRU *counterfactual* trials with reduced sensory reconstructions from the user's recorded bearings/velocity (not 64 actual on-phone decisions):
    raw actor directional logit magnitude avg **0.840321**
    selected direction noise magnitude avg **0.593508**
    noise magnitude > learned actor magnitude **20/64**
    selected amplified heading scale >=2.5 **39/64**
    selected 4x scale **29/64**
    final heading opposite actor's learned directional logits **8/64**.
  Strong direct mechanical support for over-exploration, not proof which concrete live decision was caused by which module: the flight recorder currently does not retain actor proposal vs V353 choice vs planner vs executive override. Instrument that next before applying a behavioral redesign.
- Physical velocity is present in sensory array `input[191/192]=clip(bodyVelX/Y/130f)`, very small relative to ordinary 2-5 U/s motion, while the 4x exploratory actor noise is substantial. This feature scaling is an additional plausible learning difficulty; sensor contribution should be measured against actual trained GRU weights, not guessed.

## Secondary **confirmed logic error**, magnitude LIMITED in tested samples
`Zero3CognitionV353.select` (and `Zero3PlannerV354.select`) subtract `cost = mean(model,2)` when `trusted(2)==false`. Its comments say unreliable motor-cost estimates should supply only a safety bound; the implementation still uses their raw predictions in ranking.
On exact actual trained checkpoint, `trusted(0)=trusted(1)=true`, `trusted(2)=false`; cost heldout MSE **5.78188576128e-7** versus baseline **2.62555172048e-10**, error ratio **2202.16×**. This is a genuine validation-gate bug.
However the realistic reduced-sensory 256-counterfactual score test shows cost prediction difference among headings **0.001148 average**, maximum 0.001149, and **0 ranking flips** when cost is excluded in those reconstructions. Therefore it CANNOT be asserted as the main cause of observed 68 live heading reversals. Correct for conceptual safety, but do not oversell it.
Actual user summary `predictivePlannerUses=216` is `Zero3CognitionV353.plannerUses`, incremented when `canPredict` is true even if GRU's original option remains; **it is NOT 216 actual overrides**. Separate multistep `Zero3PlannerV354.uses` is different.

## Verified test sources
- Full V382 phys traced A/B **SUCCESS**: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39995703029
- Exact V382 brain rejected cost-head and 256 reduced-sensory score comparisons **OK (1 test)**: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40025332445
- Two neural diagnostic instrumentation methods returned **OK (2 tests)** (workflow initially marked failed by stale `grep 'OK (1 test)'`, runner expectation fixed on branch). Controlled direction/noise trace in job logs of run `40025862895`.
- No modifications to production neural checkpoint, stable physical code or official OTA from audit.

## Required narrowly targeted fix design (NOT IMPLEMENTED)
1. **Do not hardcode blue attraction, red avoidance, turn limit, direction or gait.** Single GRU3 keeps full autonomy.
2. Separate exploratory *candidate generation* from *trusted physically reachable execution*. Learn and validate physically measured directional response conditional on relative current velocity (not just `speed>=2`), muscle impulse and previous heading; let candidate scoring price its real expected cost/time or uncertainty. Until verified, do not widely prefer large reversals from trained spectral knowledge alone. Do not remove exploratory capacity; regulate it using heldout physical ability and real outcome feedback.
3. Log per decision actor option0, V353 selected option, V354 planner proposed/selected, executive recalled option, final intent, predicted material velocity and post actual. That identifies which issuer causes each jump in same live run without modifying policies.
4. Correct prediction trust bug: never use raw untrusted cost prediction in score; retain a conservative verified error margin without imagining zero physical effort; regression-test both zero-override and trusted-cost cases.
5. Bench on authentic saved cortex with same 256 intended headings AND autonomous visual decisions in a real no-weather room and multiple hard turns; require strict gain in 0.4s, 1s and 3s material response, no regression in blue/red/gates, no deletion of learned checkpoint. Only then signed preview, NOT automatic OTA.

# ZERO 2.2 PRIMARY — first Android live telemetry after signed OTA135
Date: 2026-10-14 22:16 Europe/Rome (20:16 UTC).
User request: "Analizza i primi dati." This is a read-only forensic report; NO code modification, APK, learning reset, or reactivation of legacy TD.

## Raw reports
- Before: branch `zero-telemetry`, report `20261014-182328-ebc646c2ded4` UTC 18:23:28. In-app report says `zero22.mode = SHADOW_LEARNING`. At capture: completed lives 84, elapsed scarcity simulation 63600.277s, primary recurrent lived transitions 96939, 757 positive contacts, 194 negative, energy counters blue578/red194/violet179, total displaced403, boxes uncovered189.
- New: report `20261014-201619-f1eea9e32146` UTC 20:16:19. In-app report says `zero22.mode = PRIMARY_ACTIVE_LEARNING`. At capture: completed lives113, sim elapsed88514.4s, recurrent transitions136017, positive contacts1066, negative232, blue854/red232/violet212, displaced652, uncovered224. Current life ~536.714 simulated seconds, 9 positive contacts, 0 harmful contacts, 5 moved objects, current reserve 83.4/100.
- **Important sampling limitation**: the reporting interval straddles actual OTA135 installation. The first of 29 completed lives had already started at the preceding shadow snapshot (old current lifetime ~2704.65s). Thus total-delta rates are useful descriptive *mixed-exposure* rates and cannot prove causal improvement/decline entirely attributable to 2.2. The new 29 completed life stats include one carryover (91.6min).

## Main delta between reports
- ~24,914.1 additional simulated seconds (6.92 simulated h) according to persisted scarcity clock.
- 29 further deaths, +39,078 recurrent lived transitions, +62,522 replay updates (155098 -> 217620), preserved replay 96.
- +309 positive resources, +38 damaging resources, +276 blue, +33 violet real pickups (correct classified game counters), +249 externally displaced objects, +35 boxes physically uncovered.
- Per 1000 persisted simulated seconds: positives **12.40**, displaced objects **9.99**, boxes physically uncovered **1.40**, violet pickups **1.325**.
- Immediately preceding 16:40->18:23 shadow interval: positives **11.38**, objects **7.34**, boxes **2.85** per 1000 sim sec, violet +16 over6677s = 2.40/1000. Thus total resources and arbitrary displaced objects do not collapse, but exposed-box productivity approximately halves and violet pickup rate declines. This does NOT prove an engine bug or a causal effect of switching the controller; opportunity/difficulty of objects are not controlled. It is the strongest *behavioral regression candidate*.
- Ratio boxes uncovered / all externally moved objects changes from 19/49=38.8% in previous interval to 35/249=14.1% new. This ratio is descriptive only, as the denominator includes non-box construction objects and does not normalize exposure.

## Lives evolution
- Latest 29 completed lives: average16.01min, median11.62min, 8 over 1000s, longest91.60min **carryover** from pre-OTA report, two with zero beneficial contacts.
- First 10 in these 29: average24.33min / median14.84min; beneficial contact rate13.90/1000 simulated seconds.
- Middle 9: avg12.32min, median8.04min, 10.82 positives/1000s.
- Last 10: avg11.01min, median10.55min, 9.99 positives/1000s. Individual last10 deaths: ~19.2, 5.6, 8.7, 15.3, 21.5, 3.6, 12.4, 13.5, 5.9, 4.5min. Not demonstrably trending upward yet; first group includes carryover. No snapshot of multiple *entirely post-install* matched environments, so never claim reduced mental intelligence from this alone.

## Decision blackbox evidence — genuinely primary
- New current life `decision_flight_current_v1162`: 457 decisions in last300s, recurrent contribution field #16 nonzero **457/457** (OTA135 changes field 16 semantics from a DELTA to an absolute primary score contribution), no previous TD energetic/advisor term. Single recurrent brain chooses all 16 action codes. Average epsilon .1983 (range .12–.264) and 261/457 commands with adhesion (57.1%); max same-action consecutive streak just **5**, duration ~3.25 simulated seconds. No deadlock repeating one action in latest 300s, unlike previous OTA130.
- Learned navigation influences 405/457 decisions but only as subordinate evidence (field#12); causal policy 0/457; object-identification policy 0/457. This is still not a complete integrated learned multistep model.
- New current flight gains positive energy at t=265 (+22),318 (+40 rare),320 (+8.47),369 (+16.07),468 (+22),491 (+19.87) [these are per-decision observed positive contacts, partial values due capacity clamps, not necessarily six objects if events straddle]. Confirms active primary did in fact collect rare violet in this life, despite user-observed sometimes ignoring it.
- Last completed death flight had 411 decisions, 16 actions utilized, exploratory epsilon .15-.31, last event at~269.51 seconds, energy depleted to zero, only one beneficial +22 at t≈69s, no direct red harm. Last 64 decisions at reserve<15% featured **zero significant signature0 spectral intensity above .20** but frequent brighter signature2 optical cues: physics may have hidden/unreachable sources or ordinary objects. These photons are NOT proof that accessible violet food existed; don't claim clear food was knowingly ignored.
- One caveat in passive snapshot: `primaryDecisionsSession=0` is a diagnostic field not serialized, reconstructed in a freshly restored `RecurrentCoreV22` during digest, and is therefore **not actual evidence of inactivity**. Use decision blackbox absolute core score 457/457 and live app source authority instead.

## Learning models: real bottleneck persists
- ZERO22 physics `physicalReady=16/16`, physical held-out confidence=.347 vs old .289; provisional `energyReady=12/16` vs 3/16 before. This internal MSE readiness is NOT independent validation that chosen actions give survival reward, and prior OTA130 had transient false positives in energy readiness. No automatic promotion to "smart" or successful causal learning.
- CausalExperiments heldout error `0.21717069` vs reference `0.1967168` (model **10.4% worse**), confidence ZERO or negligible despite 135904 experiments and 27183 validations. Recent blackbox causal contribution zero.
- PhysicalConsequenceV20 heldout Brier `0.14930579` vs baseline `0.14766718` (worse), confidence0. Evidence updated with real motor results but cannot support object-specific causality.
- PerceptualAssociationsV20 error `0.12988372` vs action-only reference `0.12982474` (not outperforming). Confidence0, object identity unverified.
- ObjectExperienceV20 heldout squared cumulative error2033.72 vs baseline2106.71 over16108: improvement~3.46% vs required >=10%; policy choice bias zero.
- ObjectContinuityV21 from before->now: delayed possible energy links **38->43 (+5)**, unlinked real energy contacts **913->1255 (+342)**, 347 total new energy event reports (309 positive +38 harmful). **342/347 (98.6%)** cannot be credited to a previously tracked real object under existing continuity criteria, versus only 5 tentative delayed links. No full `cover moved -> new source exposed -> touched source -> positive reserve` object persistence/credit proof; model is not yet an autonomous multi-action causal learner.
- Grip mechanics were not changed by OTA135; new device data suggest ZERO still moves external objects, so there is no demonstrated broad physics shutdown. Need dedicated `post-uncover-to-rare-contact` event trace in next iteration; current telemetry cannot prove identity/visibility/reachability of a particular ignored violet.

## Immediate priority for next brain fix (not done in this analysis)
1. Improve **credit assignment for delayed energy contact after real prior environmental effect** entirely within 2.2 using learned temporal sequence and optical/tactile observations, not hardcoded violet attraction or source coordinates. Current rc22 reward head learns mainly immediate reserve difference; only a tiny generic external-motion signal, so can learn to move objects without learning eventual food.
2. Diagnose and improve **physical source-continuity** (hypotheses + heldout comparisons) on real observed sensor fields; ensure old V20/V21 modules offer meaningful validated sensory evidence, instead of zero contribution while the 2.2 matures. Do not re-enable competing legacy chooser.
3. Add read-only expose->pickup event sequence metrics with bounded stats and offset-specific intervals, no scoring changes until causal evidence is actually improved.
4. Compare consecutive post-OTA135 windows within the SAME brain, especially uncovered boxes and violet collection per 1000s and successful transition lag, death by starvation, state memories across restart. Preserve live saved rc22 and all12 slots.

## Final assessment
The architecture change **worked**; the intelligence claim is not yet earned. The core is exploring varied actions and collecting normal and rare energy. Its *multiple-action* representation and causal attribution remain uncalibrated; box uncover frequency dropped in the observed mixed-version interval and last10 lives show no monotone survival improvement. Keep the new core primary while correcting those deficiencies, per user's explicit mandate.

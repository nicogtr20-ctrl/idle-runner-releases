# ZERO OTA164 — Ordinary gel retraction and motor calibration

Date: 2026-10-16. Branch `fix/zero164-traction-motor-consistency`. **Candidate only until independent GitHub JVM/Android QA, signed publisher, and HTTPS APK verification pass.**

## Empirical issue
- Post-OTA162 report `zero-telemetry/reports/20261015-210624-1433efaa18fe5`: the saved 96-point contour had maximum membrane radius ~42.7U, with 10 locally inward centroid rays. OTA163 repaired normal geometry and missing far-tip reaction, but the real code only lifted radial contraction friction when radius exceeded `R+32` = 60U, so the observed ordinary ~43U tips remained strongly damped.
- A 25ms frame with traction .95 retains only `exp(-(1.8+66*.95)*.025)≈0.20` of radial velocity. The hard 60U release therefore did not help this ordinary geometry.
- Over two available before-OTA163 reports, local cortical 320-weight mean absolute magnitude grew from ~0.0000455 to ~0.00832 with the coordination learner while original shared 84-weight magnitude stayed ~0.02152. Growth alone is not proof of harmful learning. No OTA163 phone success is asserted.

## Physically grounded changes
1. `SymbioteMatterV1.radialRetractionRelease` computes a **continuous tissue-geometry and voluntary-contraction** factor; release starts when locally extended beyond R+9 and reaches full by R+17, only for strong inward motor commands beyond -0.83 (so an ordinary alternating crawl does not lose its footholds). No external heading, object, food identity, or gait.
2. Actual obstacle grip holds are protected: release is exactly 0 when `gripHold>=GRIP_HOLD`. Existing tangential substrate damping remains byte-identical. Unstretched/idle/expanding tissues retain original radial damping. Only inward radial damping is progressively reduced, using the same physical velocity decomposition.
3. `SymbioteCoordinationV162`: conservative local-parameter learning rate 0.006 (was 0.014); minimum accumulated real per-action path 0.8U (was 0.5U); per-channel update clip 0.0025 (was 0.005). Retained selected-action directions, observed physical centroid, on-device heldout isolation, no learned reward to 2.2, and separate grip learning.
4. **All schemas and persistent data unchanged**: `smh1`, `cl161`, `co162`, `rc22`, 12 slots, package, signature, optional neurological growth and UI. ZERO is not reset. Existing trained cortical weights remain intact; these limits apply to future updates only.

## Targeted JVM regression
`Zero164TractionRegression` proves moderate saved-scene-scale radius releases radial drag; occupied/unstretched/expanding/micro-noise surfaces cannot release; preserved original nonretracting physical coefficient; real 96-site + 74-inner-material body grows and retracts a living pseudopod; area/topology remain valid. Existing OTA161-163 stress tests, sensory wiring and prior regression suite remain mandatory.

**This does not prove autonomous locomotion.** Acceptance on the real Galaxy S21 requires at least 25 completed lives after installing signed OTA164 with the same saved brain, and comparison with prior 25 under similar conditions: NETTO/VIA, selected direction agreement, pathological long-tip persistence, center speed, energy, falls/harm and actual pickup. Do not interpret successful lab electrodes or a green APK build as goal-seeking competence.

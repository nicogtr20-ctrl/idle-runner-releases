# ZERO392 — Real S21 96-muscle causal Jacobian: first physically positive EAST responses

**Data analisi:** 26 ottobre 2026. **Stato: evidenza scientifica, non soluzione né APK.** Originale test [GitHub Actions #40236291112](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40236291112) **SUCCESS**; `Zero392TrueLocalActuatorJacobianAndroidTest` 1 test Android **PASS only measurement**, logcat `ZERO392_JACOBIAN` artifact `ZERO392-S21-CAUSAL-MOTOR-JACOBIAN`. Vero cervello V388 dell'utente ricostruito da `zero-telemetry/reports/20261025-104220-36e86668b9aff`: GRU3 33.312 decisioni, cortex 19.452 aggiornamenti, vera ricorrenza 64, vero corpo 170 punti, 96 membrana, parete training vera, stesse velocità e posture, no modification of actual brain/saves. No policy authored, each assay manually varies one local CH0/CH1/CH3 neuron in controlled LAB only. Simulate five decisions (120 physical frames at 60 Hz), intention EAST constant as an experimental stimulus ONLY, no pre-authored policy in app.

## Quantitative physically reproduced findings
Original cortex with **NO wall**: `dx=-2.6028442`, `dy=+0.7868347`, muscle impulse 7016.55, average CH0 negative contraction 0.0117. On actual wall `dx=-0.4154358`, `dy=+1.0296917`, muscle impulse 7827.03.

96 local intervention trials, one of 16 sectors × 3 motor channels (CH0,CH1,CH3) × positive/negative 0.30 perturbation. **40 of 96 increase east displacement relative to unchanged original**. Best free-space `dx=-1.4662933`; worst `dx=-4.3028717`. The top six variants were replayed with the same real Training wall and saved body:

| Intervention | No wall dx | With real wall dx |
|---|---:|---:|
| Original unmodified cortex | −2.60284 | −0.41544 |
| Sector 8, CH0 radial +0.30 | −1.46629 | **+0.11836** |
| Sector 2, CH0 radial −0.30 | −1.47987 | −1.14296 |
| Sector 9, CH0 radial +0.30 | −1.65179 | −0.30299 |
| Sector 14, CH0 radial −0.30 | −1.71040 | **+0.88661** |
| Sector 1, CH0 radial −0.30 | −1.78137 | −0.30479 |
| Sector 15, CH0 radial −0.30 | −1.79482 | **+0.59529** |

## What this actually proves
- The exact V388 S21 physical engine retains *some physical actuator authority* to move east even against a wall: a single learned motor output variation can move COM right by +0.88661 in this setting. **Not** a proof of sustainable autonomous walking or escaping the room over lives; no navigation.
- Learned 512 cortical weights/64 recurrent states contain *maladaptive motor activation combinations*: at baseline, 16-sector intact learned EAST intent moves WEST (−2.60 in free space; −0.415 at wall), whereas specific local radial changes improve EAST. No second V2.2 controller or force toward gem necessary to produce local positive movement.
- The *physical response to intervention varies dramatically with wall contact*. Top no-wall effectiveness **does not generalize automatically** to being against a wall: sector2 CH0−0.30 is good relative improvement in free space but significantly WORSE against wall than original. This refutes simply installing an inverse matrix from free-space calibration. Learning/credit requires separate real contact-conditioned context.
- V390 physically isolated perturbations and 18/18 Android regressions passed [40234592208](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40234592208), but **128 training decisions did NOT improve EAST** on already degraded saved cortex (baseline −2.6028, after isolated −2.6322), and original real autonomous 2,001 decision gate FAILED at signed progress/gross 9.69%, less than 12% criterion [40235005992](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40235005992). No APK V390/V392 or stable OTA.
- Lab ZERO391 relative-ground-friction modification made EAST **worse** (−2.6028 → −2.7202 free, −0.4154 → −0.4343 wall) and MUST NOT be integrated.

## Remaining causal engineering required
1. Build **contact-conditioned real motor response model**, learning an estimate of each local cortex muscle CH0/CH1/CH3 perturbation's marginal change in physical COM acceleration and location-specific traction, with train/heldout split. Gather actual no-wall and wall front/rear contexts separately. Avoid fixed EAST commands, never copy the lab's sector14 bias to live agent, no scripted navigation.
2. Causal training must compare actual perturbation outcome **against a matched passive or control pose** and its REAL nonlinear drag, not `dx - PRE_velocity × time` used in V378; stop falsely rewarding passive wall arrest.
3. Check actuator capabilities when user cortex has already undergone 19,452 updates, and whether a safety-gated incremental rehabilitation of existing 512 weights can improve all four headings **without overwriting any original V356 brain, 64-state recurrence, or 12 user slots**.
4. Require full original unscripted 2k+ decision gate, consistent longitudinal improvements and cold-start restore BEFORE preview or OTA. Code currently exists in **forensic lab branch** `lab/zero392-s21-local-actuator-causal-jacobian`, not main, not installed phone.

**Caution:** The 40/96 number is `dx intervention > baseline dx + 0.05`, not 40 motor directions validated. Even best NO wall is still negative dx; only some wall interventions produce positive east translation. The statement "ZERO knows how to walk" is not justified.

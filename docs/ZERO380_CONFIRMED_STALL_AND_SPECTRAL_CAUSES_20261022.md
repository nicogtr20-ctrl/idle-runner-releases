# ZERO 3.0 / OTA379 — Root-cause audit (22 ottobre 2026)

## Status
**DIAGNOSI CONFERMATA DAL CODICE + 3 TEST SU EMULATORE ANDROID API35**.
Source branch `audit/zero380-training-stall-spectral-causality`; automated QA:
https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39849819141
Executed: `Zero380FailureReproductionAndroidTest`, **OK (3 tests)**, unchanged original ZERO379 implementation. These tests intentionally assert the BAD result reproduced by the real original code. They are NOT tests of corrected behavior.
Training release signed APK build1189 remains untouched. No new OTA.
No real Galaxy S21 decision log from the user's latest session was accessed; distinguish source-level causality from observed on-phone probability.

## Proven failure #1 — one-tile red avoidance loses to an arbitrary margin
References: `Zero3ContactValenceV369.utility`: contribution for one wavelength/one azimuth = value * photons * alignment / **8**.
`Zero3InstrumentalCreditV371.opticalPreferenceV374`: when heading-to-displacement is not independently verified, multiplies this by **0.35**.
`Zero3CognitionV353.select`: refuses a better action if best-original score is <= **0.025**.
Two genuine negative red-band contact experiences (real +2 evidence) yield valence -1. A single red visible to the EAST at brightness 0.56 yields the utility difference between EAST (danger) and NORTH (escape) of **0.35 × 0.56 / 8 = 0.0245**, less than selection threshold 0.025. The system continues to select EAST toward the learned red. Same test with blue learned +1: NORTH remains selected even though EAST is the better available candidate.
The exact two failures both reproduced in real Android instrumentation.
These are calibration/dead-zone bugs; do NOT reinterpret as ZERO 'not knowing color'. These examples prove rejection of a better *available* proposed direction after learning, not that the actual user-supplied checkpoint contains that amount of evidence.

## Proven failure #2 — Training 1 walls only block part of organism
`TrainingRoomV379.constrain` projects membrane (96) + body nodes (10) but never calls project on the 64 `SymbioteMatterV1.INNER` liquid nodes. `SymbioteWorldStepV171.interior` only handles ScarcityLab and real crate/panels, ignoring the new `law=7` fixed-wall entities. Emulated Android test placed a liquid node inside a solid tile and called both live wall collision dispatch functions: it stayed inside. This causes inconsistent projected body, internal material, and rendered touch: a new physical solver MUST apply CCD and material continuity to all 170 actual nodes, not a scripted push toward the room center.
Also note: `TrainingRoomV379.project` lacks the saved prior positions and swept-edge correction already available in the prior square physics; multiple tile corners and high-speed crossing require tests.

## Proven failure #3 — corner contact can blind recovery learning
`GameActivity` compares POST-wall mass-center correction to PRE-solver mass-center and passes `externalMotorInterferenceV363` to `brain3.observed`. Any measured correction magnitude >0.08 is marked external. `Zero3OriginalBrainV356` uses `disturbed |=external`; `Zero3InstrumentalCreditV371.complete` updates `stalledStreak` *only for clean decisions*. Therefore repeated no-progress wall contact can be censored as 'external' and **fail to increment the motor stall signal that expands candidate exploration**. Avoid fabricating positive motor-credit: distinguish wall-contact obstruction (sensory/tactile/legitimate constraint) from unobserved external velocity and retain measured commanded-vs-actual limitation separately.
The QA third test also asserts the existing external detector does mark .09 physically corrected movement as external; the downstream feedback relationship is verified in code. A full Galaxy S21 corner-stall end-to-end reproduction remains to be instrumented.

## Other deterministic limitations
- `SensorimotorMind.Senses.spectral8` ignores lights beyond 210 physical units, while editor room is 480x640. A central gem at (0,0) is ~400 units from a corner and invisible there. A retina-range and room-size design decision, not an Android permission error.
- `GameActivity.resolveObjects` records contact valence only if **actual reserve changes**. A blue gem collected with energy ~100/100 may fill no missing reserve (0 delta) and not train V369; near-full small positive deltas produce values below learned-utility threshold 0.1. This is not a suggestion to manufacture reward. To fix, preserve causal energy conservation and teach the physically observed 'resource value/availability' separately from 'useful intake now', not a scripted GOOD_BLUE label.
- The actor usually samples only 3 nearby proposals (7 when contact knowledge is present; 9 on verified motor stall); it cannot select a direction that has never been proposed. This is especially serious when repeated motor stalls are censored by wall contact.

## Remediation gate (do not publish another speculative OTA)
1. Make fixed walls truly constrain skin, body nodes, AND all 64 fluid nodes; robust swept collisions, links/corners, correct touch and occlusion; preserve old open-world physics exactly. Require fresh Android tests covering single wall, closed corners, no tunneling, and no spontaneous movement.
2. Separate observed physical blocking from exogenous interference for diagnosis; do not contaminate existing motor credit, GRU weight training, red/blue rewards or V378 inertial credit. The motor policy must receive actual impasse signals without a scripted escape action.
3. Remove the proven optical decision dead zone only when **genuinely learned physical contact valence** supplies sufficient evidence; verify red and blue opposite-side and near/45° cases, no hardcoded colors and no target angle injection. Test old branch baseline and heldout improved behavior.
4. Instrument before and after: per decision photon sector/intensity, learned red/blue counts/values, candidate headings and action selection, motor intent vs actual COM displacement, physical obstacle contacts, disturbed flag, actual energy delta and terminal cause, next-life cold load.
5. At least 2k real original `GameActivity` decisions in reproducible wall / two-color layouts; no OTA until regressions and unchanged existing user-profile/saved slot identity are proven. On Galaxy S21 corroborate using real shared data after install.

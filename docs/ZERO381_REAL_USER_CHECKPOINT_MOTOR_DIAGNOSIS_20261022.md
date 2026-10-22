# ZERO381 — Physical actuator diagnosis from REAL phone checkpoint (2026-10-22)

## Evidence provenance (no artificial rewards or modifications)
Source: explicit user-initiated upload to private GitHub branch `zero-telemetry`, index `zero-telemetry/current.json`, immutable report `20261022-212619-2fdb10b7a9743`, installed Android `3.0-ota.380-training.1`, versionCode 1190, timestamp 2026-10-22 21:26:19 UTC (23:26 Europe/Rome).
Read `zero3Summary`, part-001, part-002 and the *exact 19 CRC-validated original V356 checkpoint fragments*. Full original brain was reconstructed only temporarily on GitHub Actions runner; neither weights nor tokens were committed, exported, printed, or modified. Test uses the 170-node physical `SymbioteMatterV1` with original V363 traction and saved motor peripheral weights.

## Original phone telemetry, actual game state
- Sole GRU V3 motor actor, valid checkpoint. **16,246 completed decisions** (12,997 training, 3,249 heldout), 9,975 adaptive cortex updates.
- Read-only physical motor diagnostic `m361`: decisions 16,246, physically active 16,223, externally corrected 3,736, aligned 7,539, opposed 5,419, almost static 562. These are lifetime aggregates, NOT instantaneous measured blue-gem intentions or a 1-to-1 steering success rate. Do NOT relabel the 7,610 historical stalled actions as 7,610 distinct failed decisions.
- Contact valence: evidence by optical band `[7,2,0]`, mean values `[+1,-1,0]`. Correctly recognizes *experienced* positive vs negative surface wavelengths.
- Verified motor heading banks: **0**. Heldout validated inertial-response banks: **5**. V353 predictive planner uses: **0** (energy and injury heads untrusted). V371 stalled motor actions: **7,610**.
- Frozen historical V2.2 motor head checkpoint `symbiote_motor_head_v1`: 16,317 selections, **0 training updates**. On live V380, `Zero3MotorPeripheralBridgeV364.PROVEN_CANDIDATE_SHARE=.75f`, i.e. three physical command channels use 75% frozen old peripheral, 25% adaptive V3 cortex, even after nearly 10k V3 cortex updates. CH2 is always original V319 adhesion. This low-level readout uses the SAME heading supplied by GRU3, so it is not a second chooser issuing a conflicting target.
- Saved Training layout has nine hand-placed blue gems. At upload `training_room_1_active_v379=false`, so snapshot was after leaving Training; current scene and Training scene are saved separately. The V2.2 `decision_flight_current_v1162` and `decision_flight_last_v1162` are empty/historical, and cannot prove a particular one-off brain intention toward a visible blue.

## TRUE A/B on the saved trained motor cortex and frozen muscle readout
Automated Android emulator run: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39858733615 — SUCCESS.
Fixture: `Zero381RealCheckpointMotorABAndroidTest.java` in audit-only branch `audit/zero381-real-checkpoint-motor-response-20261022`.
- Explicit original user-trained saved cortex, private checkpoint CRC and SHA verified, historical peripheral weights from that same report.
- Same physical 170-node organism initialized at rest with V363 traction. No world obstacles or energy/reward. Heldout-only commands, **zero gradient updates**.
- Warm 12 decisions EAST ×0.4 sec =4.8 sec; then 8 decisions (3.2 sec) EAST/EAST baseline, EAST/WEST reversal, EAST/NORTH turn.
- Within each controlled condition vary ONLY fraction of the frozen motor readout on CH0, CH1, CH3; preserve V319 grip and real muscle forces. This A/B uses explicit external test headings to IDENTIFY the motor only, never authored into the Android app.

| Old frozen motor % | Warm-up EAST material `dx` | First 0.4s after WEST `dx` | Full 3.2s after WEST `dx` | Continued EAST 3.2s baseline `dx` | 3.2s after NORTH `dy` |
|---:|---:|---:|---:|---:|---:|
| 0% | -1.0910 | +0.0345 | +2.8464 | +1.7669 | +0.4263 |
| 25% | +1.7355 | +0.4002 | +3.5063 | +3.9755 | +0.2024 |
| **75% (live signed build)** | **+14.2348** | **+0.8875** | **+0.9460** | **+12.2553** | **+5.0693** |
| 100% | +20.0442 | +1.2877 | +2.6974 | +14.6597 | +10.4878 |

All WEST trial first-0.4 and full-3.2s displacements are POSITIVE `dx`, i.e. still EAST in absolute space despite motor intention WEST. In live 75% mode, WEST is less EAST than continue-EAST (+0.9460 vs +12.2553), confirming *some* braking authority, but insufficient complete reversal in three seconds. North control produces positive Y over 3.2s (+5.0693) but initially Y is negative (-0.6006), revealing temporal response lag. Conversely all-pure V3 trained cortex (0% old) produces **negative travel during EAST warm-up**, so abruptly removing old motor would regress locomotion further.

### Defensible diagnosis
**Confirmed:** intended heading != promptly controlled body displacement, with the user's actual learned muscle states. **Confirmed:** old motor dominates current physical output while untrained/frozen; actor's trained cortex only affects 25% of selected channels. **Confirmed:** ZERO currently has no verified directional 8-bank response map despite extensive experience. **Not proven:** exact source of every on-phone gem miss, from per-decision V3 trace which is not currently exported. Do NOT claim camera/visual semantics are the key remaining failure. Do NOT simply change old motor percent 75->0 or bump muscle force without matched physical evidence; these alternatives are worse in the A/B.

### Next safe implementation direction (not done in this audit)
1. Add a **read-only V3-specific flight recorder** for current/lifetime sample: time, 194 physical perception relevant compact wavelength bearings, learned valence counts, GRU intent, final blend sector muscle commands, PRE COM velocity, POST COM acceleration/displacement at 0.4/1/3s, obstacle correction, and actual contact outcome. Bounded, no saved checkpoint format change, explicit user upload only, no new policy.
2. Preserve sole GRU3 intent and prior brain/12-slot checkpoints. Develop a MOTOR-ONLY, on-policy response estimator for actuation channels and **test learned acceleration/braking response independently**; gating heldout evidence by actual motor impulse and environment disturbance. No authored target heading, color, rewards, teleport, or motor reversal scripts.
3. Compare identical user-trained cortex against current blend 75% and candidates with physically learned inverse dynamics (real body), 2,000+ completed actual decisions and opposite-direction, lateral, wall cases. Require reversal within realistic consecutive decision horizons and high heldout directional reliability.
4. Do not publish an OTA solely on improved isolated A/B numbers; collect real in-game flight trace for gem approaches and verify user cognition check points are preserved.

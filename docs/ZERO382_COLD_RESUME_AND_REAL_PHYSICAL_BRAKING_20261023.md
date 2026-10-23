# ZERO382 — Cold-start recovery + physically verified turning response

Date: 23 October 2026. Original sole GRU64 V356 active brain, 194 senses, no additional chooser or scripted resource navigation. Source branch `fix/zero382-resume-and-physical-turn-braking`.

## The user's concrete failure
On signed OTA381 motor preview, after closing/reopening, the screenshot error was:
**"ZERO 3.0: Dati motori V381 corrotti: nessun salvataggio sovrascritto"**.
The user's observed consequence was creating a NEW virgin brain; their previous 16246-decision brain and the currently uploaded 9997-decision brain represent different histories. User clarified the repeated harm events were from **weather/storm**, not red gem collisions.

### Source-level cause
`Zero3OriginalBrainV356.restoreMotorObservationsV381` invoked after the **original valid V356 GRU checkpoint** was restored inside `GameActivity.load`; it threw an IOException if either of two independently persisted OPTIONAL V381 artifacts failed decoding (`zero3_physical_response_calibration_v381` and `zero3_motor_flight_recorder_v381`). Outer Activity catch then set `simulationRunning=false` and displayed that exact error, preventing use of the VALID main brain. The V381 saved calibration and last-256 flights can be read or used optionally but MUST NOT be part of the required brain identity/integrity gate.
**V382 fix:** each auxiliary attempts recovery in isolation; if one fails, that one starts new without interrupting the authenticated, unchanged V356 brain and living scene. Logs warning, never treats diagnostic damage as reason for new neural birth. Real Android test `Zero382ColdResumeAndroidTest` intentionally corrupts both auxiliary keys, launches GameActivity twice and confirms the exact original checkpoint and GRU neural owner survive. It also separately tests one-key corruption. Verified on Android emulator QA run `39965496618` (2 cold tests + earlier 28 motor/wall/color regressions).

## Real physical turn/braking experiment (not scripted locomotion)
All A/B experiments run the REAL 170-node `SymbioteMatterV1` fluid, 96 membrane, 10 nuclei, 64 interiors, original frozen V381 75% motor-readout and original V3 cortex. Prewarm 12 decisions (4.8s) on EAST, then 8 decisions (3.2s) on WEST or NORTH (0.4s per decision). Only parameter differs: short-lived **passive floor-contact traction** following the ORIGINAL GRU self-selected direction discontinuity. No COM force, no velocity multiplier, no target coordinate, no reward or hard-coded muscle output. This is a physical surface slip response decaying at e^-1.8t.
A) virgin original brain, 382 physical A/B on real gel, run `39966265177`:
- Baseline 0 added traction: EAST before turn=+13.366, straight EAST after=+11.740, WEST full after=**+0.7367** (fails reversal), NORTH full y=+6.8254.
- Extra traction +.04: WEST after=+1.1985 and NORTH after=+5.6062, worse: REJECT.
- **Traction release -.04:** EAST before=+13.366, straight EAST after=+11.740 (unchanged), WEST after=**−0.3981** (real reversal in 3.2s), NORTH after=+6.8255; short horizon WESTfirst +0.939 vs +1.022 original.
- More release -.06: WEST after=−0.1149, NORTH +7.2254. Better to choose moderate -.04 since achieves real virgin reversal with less reduced traction.
B) *user's actual signed V381 9,997-decision trained cortex, and the SAME frozen motor weights*, anonymously reconstructed from their explicit private upload `20261023-133146-3182a05bea513`, in emulator run `39966525470`:
- Baseline straight EAST=+12.41124; after WEST **+1.82877** coasting; NORTH +5.98380.
- **Traction release -.04:** same straight EAST=+12.41124; after WEST **+0.73212** (59.96% less wrong-direction coast); NORTH +6.14611. The trained brain does not yet fully reverse within 3.2s; **do not claim full locomotion solved**.
- Stronger -.06: WEST +0.64699 but higher release may harm grip, so choose -.04 conservatively.
C) An independent second lab varied the fast internal muscle state update speed (extra response +8/+16/+24). Outputs were identical to baseline in the virgin gel; **NOT enabled in production**. Experimental helper remains with zero defaults, but do not present it as proven.

### Actual production integration
- `SymbioteMatterV1` adds finite motor angular-change readout and decaying passive ground friction adjustment; **zero by default** preserves V2.2 physical baseline.
- `GameActivity.load` sets `configurePhysicalTurnBrakeV382(-.04f)` only after the original V3 brain is loaded. `GameActivity.resetEpisode` reconfigures the same for the same brain after birth/death.
- `Zero3OriginalBrainV356.actuate` passes only the newly chosen neural unit vector to `SymbioteMatterV1.observedOriginalMotorChangeV382` once per original decision. Cannot suggest a world direction or touch the GRU learning weights.
- New QA asserts resumed genuine Activity has V3 motor configuration of -.04 and unchanged valid original brain.
- Original `Zero3OriginalBrainV356.KEY`, brain checkpoint CRC, 12 named slots, 381 flight recorder and V381 calibration formats remain **unchanged**.
- Sign as **build1192, 3.0-ota.382-motor.1**, same original certificate `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`, package `com.overmind.zero.ota`. Experimental preview ONLY; public updater `latest.json` must remain OTA378 until phone verifies 382.

## Acceptance / remaining uncertainty
- Two real cold opens with deliberately invalid diagnostics must PASS.
- All original color, motor, gel, Training1, weather and wall regressions must PASS.
- Exact original V381 trained-brain and old peripheral identities must PASS. The QA stores private brain bytes only in runner temp and deletes after test; no checkpoint copied into docs/artifacts.
- S21 real use after install: verify app force-close + reopen **at least twice**, same experience count, Training and free-scene continue. Use explicit share; compare new flight recorder 256 decisions, especially sharp turns and motor corrections.
- **Do not claim** universal red avoidance, gem search or motor perfection. **No OTA stable publication** until proved on phone.

# ZERO412 — real trained S21 V3 cortex sensory alias repairs (29 Oct 2026)

**Status: code corrected on an experimental GitHub branch, no production APK/OTA, live 10,000-decision behavioral gates must PASS before any release.**

Source branch: `fix/zero412-observable-inertia-and-ground-contact` based on audited V410 V401/V406/V407/V409 integration. User explicitly requires **>=10,000 truly autonomous decisions** per behavioral acceptance test, not 2,000, with original live physics and no scripted directions.

## Source-proven root defects (V411 real original user S21, not a hypothetical neural training problem)

Original `Zero3LearningFoundation.AdaptiveCortex.command` used 8 input features for 64 muscle output channels (512 learned weights). Its feature4 only measured local tissue velocity relative to the body, and feature7 only its previous motor activity/tangential relative tissue shear. The cortex therefore produced *bit-identical 64 muscle commands* with the exact original 33,312-decision trained user's S21 body in still versus +65,-65u/s genuine whole-body inertial motion in all 4 instructed headings, even though the body physically continues against the desired direction. Confirmed original [V411 Android 2/2](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40780226267).

Feature5 was `(held+contact)/6` across each 6-node sector, so the cortex also produced bit-identical muscle commands if 6 physical nodes were anchored by real `gripHold` vs 6 nodes had real wall `touchSense` instead (same aggregate). This is precisely why the original muscle could keep pushing into a wall as though it had traction.

## V412 corrective implementation, preservation of original learned cortex

**No new weights, no second brain, no motor preset, no extra neuron, no artificial thrust, no changed V356 checkpoint wire, CRC or 12 named save slots.** Original 194 sensory input/64 GRU units, 16×4=64 cortex outputs, 512 trained synaptic coefficients, 64 persistent local recurrent motor states and CH2 independent V319 grip-head are preserved.

- For **CH0/CH1/CH3 only**, feature5 retains its original sum `(held+contact)/6` and adds a bounded `+.35*(held-contact)/6`. Thus physically measured grip and solid wall touch no longer mean the same thing in neural input. Original CH2 motor input feature5 is EXACT original; CH2 feature7 is EXACT original and V319 remains sole physical adhesion owner.
- For CH0, feature7 adds the locally projected true original body `(body.cvx*radialX + body.cvy*radialY)/110 * .30`. CH1 adds true tangential component, preserving the preexisting V385 tissue shear. CH3 adds original body COM speed projected on the **GRU's own issued intention** (real state, not a new target or author-selected heading). Values are bounded and a stationary body retains the precise legacy feedback in feature7.
- Important regression found *by the new Android proof*: first patch accidentally also fed new feature5 into CH2, which violated V319 independent grip. Fixed on current branch; real original S21 proof now **3/3 PASS**: [V412 alias repair](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40918582244).
- Important **saved-brain restoration bug**: `Zero3OriginalBrainV356` constructor configured the cortex, but `restore` replaced the entire cortex instance from V356 trained checkpoint and silently lost the new sensor opt-in. Fixed `restore()` to reattach only the physical-context flag AFTER preserving all loaded weights/state. An extra Android integration test loads the actual 33,312-decision original checkpoint, asserts the flag persists on the restored owner, saves new CRC and verifies the exact 33,312/19,452 readout. No saved bytes or neurons are reset.

## Scientific physical response (not yet enough to ship)

The original user's actual S21 saved 170-material physics tested **16 combinations of posture (wall/non-wall/independent/rest) and 4 heading probes**, identical skin, the original trained cortex with new context OFF vs ON. This is diagnostic only (scheduled LAB intent; not autonomous learning) [V412 16-context A/B 40918593058](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40918593058):
- Wall posture EAST progress improves from **−0.427094 to −0.351440** (+.075653) over 5 physically integrated intents.
- Original free EAST is still **−2.600342** vs baseline **−2.615158**; still moves WEST when instructed EAST. Improving input observability IS NOT a claim of correcting the learned coordination yet.
- 16 contexts: 1 gain >+.07, 0 losses below −.07, mean +.002444, worst −.046246. The physical cortex needs extended learning and possibly a separate, proven motor-credit improvement to steer reliably; don't claim 100%.
- [V412 3/3 original saved-S21 alias proof](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40918582244) verified old unchanged 64 outputs when context disabled and changed motor commands with real 65u/s body speed and held-vs-wall contact when enabled. 512 original learned weights unchanged and zero voluntary effort gives zero muscular thrust (CH0/1/3).

## 10k gates — ALL mandatory, no cherry-picking

1. [V412 10,000 original wild world GRU3 decisions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero412-original-wild-10000-red-contact-safety.yml). Includes genuine red/blue material pickups, true energy, 10 true 1k windows and signed/gross intent-efficiency; requires >=12% movement and **fewer harmful material contacts in last 5k than first 5k**. The prior V410 10,002 real original actions [run 40780644176](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40780644176) had 66.64% signed/gross but red rose from 15 to 49 in the second half, total **64 red**, so is a real FAILED hazard gate despite good travel. Only actual latest V412 run may approve.
2. [V412 10,000 ORIGINAL user's 33,312-trained GRU3 decisions in saved S21 Training Room 1](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero412-real-trained-user-s21-10000-autonomous.yml). Original 96+74 material, wall and blue objects; QA requires exact initial body coordinates and object count after restore, original full 33,312 neural history and local V332 motor updates. 10 direction/wall 1k windows; >=12% total, >=8/10 windows with positive ratio >=10%, <=2 opposite, actual new blue pickups. This is a BLUE-only room; CANNOT certify red avoidance by claiming no red. The first V412 attempt failed before beginning because old restore disabled the context flag; fixed and rerun with real full saved checkpoint.
3. [V412 Android original GRU3/tactile/motor/slot/physiology full regression](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero412-original-brain-memory-contact-regressions.yml) plus actual S21 source-level and 16-posture physics tests must all pass. A green unit test never substitutes for 10k red safety.

Note: Latest 10k workflows use GitHub Actions concurrency cancellation of superseded revisions, to ensure the only accepted long-run data is the current actual source. The user phone, original V400 signed experimental manual install, stable V388 OTA/latest.json, personal V388 uploaded telemetry and all 12 user named slots have not been touched.

**No APK/OTA or 'fixed 100%' statements until both real 10k gates genuinely pass.**

# ZERO439 — one-variable original-cortex measured COM inertia sensory-gain A/B
2026-10-31. Experimental branch `lab/zero439-measured-inertia-sensory-gain` based on completed V438 `c5d1ef11f028a6dde2c60a5e253e77b90b59a6d7`. This is a **hypothesis** to test, not an approved fix. No APK, OTA, merge, user-save writes or body changes.

## V438 full real Android evidence (pre-registered control)
- [S21 original 41243395973](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243395973) 10,004 genuine new decisions from exactly 33,312: signed 544.84796 / gross 3703.02721 = **14.7136%** (need 25%); **7/10** 1k windows >=10%, **1 inverse** (need 9/10 and 0); **6 new blue** (meets only blue gate).
- [Wild original 41243395992](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243395992) 10,002 genuine decisions: signed -215.55336 / gross 3220.44385 = **-6.6933%** (need >=12%); **17 red / 3 blue** (5.667, need <=2); red first 5000 **13** and last 5000 **4** (decreasing subgate only).
- [V438 original regressions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243396007) PASS; [original trained S21 512-weight/V356 CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41243395989) PASS.
- V438 complete 1k windows, physical red decisions, original gradients/counters and all 12 V421–V423 Action statuses are recorded on the **separate documentation-only** [audit branch](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/audit/zero438-complete-original-10k-physical-failure-20261031/docs/ZERO438_COMPLETE_ORIGINAL_10K_INERTIA_FORENSIC_AND_NEXT_CAUSAL_GATE_20261031.md). Read root AGENTS.md, docs/AGENTS.md and ZERO419–ZERO423 first.

## Physical causal rationale for this one experiment
All **17/17** genuine V438 wild red touches occurred with measured pre-contact body COM momentum toward the hazard (about 0.24–2.84 physical velocity units); **13/17** simultaneously had a GRU3 issued direction pointing away. GRU3 learned valence, up to seven offered options and correct sign of optical ranking therefore do not guarantee braking by the deformable physical body. V438's score correction reproduces failed V436 behavior. This experiment does NOT change action sampling or optical valence.

In `Zero3LearningFoundation.AdaptiveCortex.command`, the existing physically observed per-sector COM inertia signal is:
`localMomentum = 0.30f * clip(projectedMeasuredCOMVelocity / 110f, -1, 1)`.
At the actual wild red-contact speeds 0.24–2.84, this gives only about **0.00065–0.00775** in an existing bounded input feature, while its local recurrent motor state and command can range ±1. Such a tiny measured feedback signal may make the existing 512-weight cortex practically unable to distinguish the physically important drift from rest. This is a **quantified dynamic-range mismatch**, not a proved unique root cause; the cortex has other sensors and prior learning.

## Single changed behavioral variable
On this isolated branch ONLY, change the divisor for this already-measured, already-existing `localMomentum` from **110f to 10f**, leaving the 0.30 coefficient, clamp, channel conditions, CH2 V319 independent grip, 8-feature×64=512 topology and all saved synapses unchanged. At measured 0.24–2.84 velocity this increases physical feedback from ~0.00065–0.00775 to ~0.0072–0.0852, still bounded and not a prescribed direction or artificial force. The cortex's **own trained weights** decide whether and how to use the measured signal. No rewards, red labels, target vectors, 2.2 motor, physics solver, motor gains, learning rate, 194 GRU inputs or original 12 save slots are altered.

Risk: original S21 weights were trained with the prior feature distribution; an 11× input shift may worsen performance or violate existing Android tests. Do not claim success based on improved numerical signal alone. Preserve original checkpoint and measure exact real original 2×10k against V438; regressions and CRC must pass independently. This is a *single* causal-sensory intervention, not combined with other new adjustments.

## Acceptance / abort
- S21 33,312 original, >=10,000 new genuine decisions, >=25% signed/gross, >=9/10 1k windows >=10%, ZERO inverse, >=5 newly collected blue.
- Wild original newborn, >=10,000 real decisions, >=12% signed/gross, real blue pickups, red last 5000 strictly below first 5000, red total <=2× blue.
- Original Android regression PASS and original S21 V356 CRC/512 weights PASS, same commit.
- If fails: keep this lab-only, inspect actual speed-conditioned muscle response and gradient, no APK/OTA/merge, no second simultaneous behavioral correction.

## V439 Android jobs actually launched (same source commit `d187b1e05b5b14ceb0954b8469e1c9db1661164e`)
- [S21 original 10k](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172521)
- [Wild original 10k](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172551)
- [Original Android regressions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172444)
- [Original S21 CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41248172737)

These jobs were queued/running at the time of writing. Earlier runs triggered by the four workflow-metadata commits before the single behavioral source commit are **NOT** valid V439 evidence and MUST NOT be confused with these four exact SHA-matched runs. Do not claim any V439 PASS before the final logs are retrieved.

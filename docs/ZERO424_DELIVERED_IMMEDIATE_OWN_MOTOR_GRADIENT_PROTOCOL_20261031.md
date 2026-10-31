# ZERO424 — ONE controlled causal correction: delivered V390 probe in immediate own-decision V423 learning
Date: 2026-10-31. Experimental branch `lab/zero424-delivered-immediate-own-motor-credit` forked from `lab/zero423-immediate-own-decision-motor-credit`. **Research ONLY. No merge, APK, OTA, migration or original user-brain writes.**

## Precommitted causal hypothesis (before results)

The source `Zero3LearningFoundation.AdaptiveCortex.learnV332` computes a 512-weight gradient with the chosen original V390 motor probe. V423 correctly trains the completed action from its **own** measured COM acceleration at the action boundary, but still multiplies that response by `probe.noise[out]` measured **before** tanh, effort scaling and output clipping. On physically saturated or low-effort motor channels, this creates a nonzero gradient for an output that the 170-material solver **never received**. V421 separately proved in Android that zero delivered muscle perturbation must produce zero synaptic gradient, but its original 10k test still used the previous cross-decision credit rule. V423 separately measured 2,962 S21 weight updates yet had only 10.0486% net efficiency and a -41.0% inverse fourth window. More updates did not produce better locomotion.

**Single new experimental variable relative to V423:** use the already implemented V421 **post-tanh, post-effort, post-clipping physically delivered probe** for the **same completed decision's V423** motor gradient, instead of V423's raw pre-tanh noise. No changes to original actor/candidate selection, V369 learned valence, V319 grip CH2, 194D/64GRU3, 512 weight shape, 170-material physics, collision, reward, danger, goal, checkpoint, 12 save slots, S21 initial state, or original 10k hard gates. The existing V421 and V423 nonpersistent opt-in switches are enabled together **only** in both original Android 10k test classes. V418, V420 and V422 experimental switches remain OFF. This is not V2.2 restoration or an invented direction/reward.

Source diff intentionally limited to:
- `Zero417TrainedS21VelocityCredit10000AndroidTest.java`: call `originalV421EnableDeliveredLocalProbeForQA()` after V417 and before V423; assert both lab flags are actually true.
- `Zero417WildOriginalVelocityCredit10000AndroidTest.java`: same single change, same flags.
- Four original Android Actions workflows: add the new branch to their isolated CI triggers and label ZERO424. No production Java behavior changed.

## Fixed Android original acceptance — NEVER LOWER

1. True user's original S21 33,312-decision saved brain, 512 cortex weights, original 170-material room, 10,000 **self-generated completed** decisions, signed/gross >=25%, at least 9 of 10 independently sampled adjacent 1,000-decision windows >=10%, **zero negative** windows, >=5 additional real blue contacts.
2. True newborn original GRU3 wild, >=10,000 completed original decisions, actual material blue and red touches, signed/gross >=12%, last 5k red < first 5k red, total red <=2× blue.
3. Full original Android regression suite including V421 real delivered-probe saturation and V422 opt-in isolation tests, plus original exact saved S21 V356 CRC/512 weight integrity, must all PASS. All four Actions required, no synthetic pass substitutions.
4. If wild Android fails **before boot**, treat as censored, investigate/retry startup; do NOT infer hazard score from it. V423 original wild first attempt failed before boot and its rerun was in progress at protocol creation.
5. Original user S21 brain/checkpoint and all 12 saves untouched, no APK/OTA, no merge. Failure or no real 10k results => ZERO424 rejected/not approved.

## Previous measured controls, not success claims

V421 S21 10,001 additional decisions: 17.1723% signed/gross, 8/10 >=10%, two reverse windows, four blue; wild 10,002: 87 red/3 blue, first35 last52. V422 S21 10,003: 11.2881%, 7/10, two reverse, four blue; wild 10,003: 52 red/5 blue, first19 last33; at 50/52 true red touches the actor had seven alternatives and learned-visible harm, yet 42/52 issued intention toward red (>0.3). V423 S21 10,002: 10.0486%, 7/10, two reverse, four blue, 2,962 updates; wild first attempt preboot censored. Technical regression and S21 CRC jobs passed for V421/V422/V423, behavioral hard gates did NOT.

Detailed original job evidence and 10×1000 windows: `docs/ZERO424_V421_V423_COMPLETED_ANDROID_FORENSIC_20261031.md` on separate **audit** branch `audit/zero424-v421-v423-10k-forensic-20261031`. Never mix these experimental branches or infer a causal advantage from different test policies without complete matched Android outcomes.

## Pending Actions

The four V424 GitHub Android runs were launched and independently verified as **in_progress** on 2026-10-31; no PASS or behavior result is yet claimed:
- [S21 10k 41154281984](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154281984)
- [Wild 10k 41154286099](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154286099)
- [Original Android regressions 41154289430](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154289430)
- [Exact original S21 CRC 41154293160](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41154293160)

Workflow trigger commits differ only by workflow YAML edits, after the two original Android test files were already committed; no production Java source changed between the four runs. Preserve state and recheck. If V424 fails, the next experiment should first inspect real physical candidate/COM/skin shear, heldout action-to-motion estimates, saturation and true retinal azimuth without changing the learned original actor or inventing a destination.

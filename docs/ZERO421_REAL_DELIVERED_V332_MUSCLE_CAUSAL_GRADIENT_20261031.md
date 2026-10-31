# ZERO421 — original GRU3 physically delivered motor-probe learning
Experiment created 2026-10-31. Branch `lab/zero421-delivered-physical-probe-original-gru3`. Research only. **NO APK/OTA, NO user slot/brain changes, NO claim of solved locomotion.**

## Source-proven error and hypothesis
Original local cortical learner `Zero3LearningFoundation.AdaptiveCortex.learnV332()` computes per-muscle gradient from `probe.noise[out]` BEFORE `tanh(activationGain*(weights·features+noise))`, effort factor and actual final output saturation at ±1. In genuine S21, many channels may be saturated/effort-limited; an original V390 probe thus receives nonzero learning credit when the actual 170-material solver saw zero difference in commands.

`AdaptiveCortex.command()` already measured the real delivered difference for V397 global learner (as `deliveredProbeV397[out] += (actualClippedMuscle - unprobedClippedMuscle)/activationGain`). But V400/V414/V417 local `captureProbeV332/learnV332` discarded this measurement and used raw `noise` instead. This is a real *eligibility inconsistency*, not yet evidence that V421 fixes user's gait.

## Controlled correction (the ONLY V421 behavioral difference)
- Ephemeral `AdaptiveCortex.enableDeliveredLocalCreditLabV421()`, enabled only through `Zero3OriginalBrainV356.enableDeliveredLocalProbeLabV421()` and Android-only `GameActivity.originalV421EnableDeliveredLocalProbeForQA()` at a real completed decision boundary after V400 request.
- `command()` measures actual delivered muscle displacement after tanh, voluntary effort, final clip for V421 as for V397. `captureProbeV332()` adds an optional nonpersistent clone of the per-physical-substep mean measured delivered perturbation to its V332 local trace. `learnV332()` uses the actually delivered difference when present, the untouched raw noise otherwise.
- No changes to original 512 weights topology, 194 GRU senses, 64 units, original V319 CH2 grip owner, body solver, user V356 CRC, 12 save slots, sampling distribution, red/blue valence, actor policy, executive option recall or V417 measured signed COM gradient.
- V420 flag is **OFF** in both source-matched V421 original Android behavioral 10k tests. V418 ablation also **OFF**. V417 full measured local credit original comparator ON. Thus V421 isolates the physically delivered gradient, not several tweaks at once.

## Specific regression proof
`Zero421OriginalDeliveredLocalProbeAndroidTest` exercises a real original MotorBrain.Body, Morphology.Surface, authentic V390 single probe and V332 local gradient (no fake hazard coordinates): saturated actual muscle output must show an exactly ZERO delivered probe and update zero synapses; unsaturated tested with exact physical command must show same-sign nonzero response and synapse update; default behavior must retain pre-V421 raw-probe mode. S21 saved brain/512 weights/V356 CRC checked independently. Full original regression workflows remain in force (also V420 previous pure gate).

## 10k hard gates, unchanged
S21 exact original user's 33,312 train decisions, 10,000 more, >=25% signed/gross, >=9/10 >=10% local windows, zero negative windows, >=5 new blue; actual held/motor physics and 12 slots untouched. Real original virgin wild >=10,000 self-generated decisions with >=12% effective signed/gross, blue pickups, red last 5000 < first 5000 and red total <=2× blue. Regressions and saved trained original CRC must also PASS. Baseline V417 S21 11.2869%, 4 blue and 2 negative windows; wild 58.8634% 5 blue 69 red and red rate worsening. V418 and V420 both failed these behavior gates and have no APK/OTA.

## Real CI runs initiated
- [Full original Android regression and ZERO421 3-case motor physics](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217)
- [Exact original user S21 and 512 weight CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737)
- [Actual S21 10,000 self-generated original decisions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000)
- [Actual wild 10,000 self-generated red/blue decisions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560)

**Do not publish unless all four actual completed jobs pass the strict hard gates; do not accept synthetic 1k demos instead of original 10k.** Keep V420 source audited but inactive for this 1-variable V421 experiment. 

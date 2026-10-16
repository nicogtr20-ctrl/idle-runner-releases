# ZERO172 — physical command latency and honest motor experiments

16 October 2026. Branch `fix/zero172-proven-locomotor-learning`, based on the verified OTA170 source tree. Runtime revision172 is independent of the next sequential OTA number. User explicitly authorized corrections and source/APK OTA earlier, then asked for a check with concrete results and instructed continuation. No device reset, authored gait, target coordinates or manufactured confidence is introduced.

## Real report and reproduced failure

The explicitly shared report `20261016-163109-182ce35d1519e` is OTA170 / code1170 / runtime171. All14 parts passed exact byte counts and SHA256; all85 working keys assembled without named slots. Report:23503 decisions,77 completed lives,0 positive resources,15205.883 simulated seconds. Last completed flight recorder:293 decisions,512.621U sampled path,31.263U final displacement,135 opposite-heading intervals. This establishes poor control, not mere lack of visible animation.

### Concrete causes

1. **An unfinished physical option was interrupted by independent random proposals every0.65s.** A true neural-head/material step-response test reproduces movement in the previous direction during the first1.3–2.6s after reversal. The same body then moves in the newly commanded direction. The live duration journal reports6514 interruptions over11262 starts. A random proposal before the material can respond repeatedly cancels the experiment. Safe ongoing motor options now finish before independent exploration can replace them; real harm and a strong current recurrent alternative (>0.24 score margin) still interrupt immediately. The existing1/2/4-interval repertoire is retained; there is no imposed locomotion cycle or direction.
2. **Censoring corrupted duration labels.** `finish()` previously recorded an interrupted four-interval option after only one/two intervals as evidence about four intervals. Only an exactly completed option now trains its selected duration. UI counters retain historical records; md172 retains the full old energy/contact/counter history but requires fresh uncensored directional train/holdout evidence. Old md160/md165 checkpoints load atomically. Old directional labels do not silently authorize current control.
3. **Motion context omitted signed translational velocity.** The old context had posture, radial/tangential deformation and speed magnitude. Bodies moving+X and−X at equal speed could share the same context despite opposite next displacements. New optional `contextual_navigation_v172` / cn172 adds actual signed centroid vx/vy. Old cn171 evidence remains saved under its existing key and is never relabeled with fabricated missing velocity. New state is loaded/saved in live game, read by actual symbiote lab, captured by generic named slots and covered by navigation reset.
4. **Obsolete controller epochs occupied active capacity.**77 of96 real recorded cells belonged to obsolete motor configurations. The old weakest-count eviction retained high-count obsolete cells while repeatedly evicting new observations. Reproduction: after five distinct current contexts, old policy retains5 obsolete+1 current; corrected eviction retains1 obsolete+5 current. Same bounded capacity, no confidence waiver.
5. **Diagnostics needed the same active context.** Brain atlas/analysis now load cn172 and the actual saved physical posture through a read-only decoder, instead of showing legacy aggregate motion counts or null posture as active capability. Exploration counts/recorder flag now describe the actually executed proposal; a random replacement rejected by an ongoing option is not counted as executed exploration. Telemetry labels runtime172.

### What was tested and rejected

The earlier observation that the84 shared motor weights barely change is real (saved RMS difference from virgin~0.000002), but it was not sufficient to identify a safe fix. A direct connection of regional directional reward to shared weights was implemented experimentally, tested on actual material and **rejected**: after1600 controlled training intervals, summed directional displacement across8 heldout headings deteriorated from152.93U to13.29U (old algorithm156.42U). Increasing an update counter or changing more weights is not evidence of learning. That patch is absent from the final source. An eight-interval duration extension was also screened and not retained; the established1/2/4 repertoire and original synaptic formats remain.

## Actual policy → neural head → physical material: before and after

The report's real neural checkpoints were cloned into identical fresh arenas. Every command came from the actual2.2 policy; no electrode sequence, external target steering, centroid impulse or hand-written gait was used. Fixed1/60s,195 simulated seconds, online learning enabled in both old/new. Vercel/weather/streaming/rendering are excluded. These are deterministic scenario screens, not independent random statistical samples or a replay of the phone's exact world. Four no-object seed labels generate an identical scene; report that scene **once**, never claim four independent successes.

The FINAL correction includes md165→md172 directional-confidence migration. Earlier exploratory numbers before migration (including41→155U) are superseded by this final table, not cherry-picked as the delivered result.

| Scene | Final displacement OTA170→corrected (U) | Signed chosen-heading motion OTA170→corrected (U) | Opposite-heading intervals OTA170→corrected |
|---|---:|---:|---:|
| No objects |13.265→63.216 |44.738→53.908 |134→135 |
| One visible resource, direction0 |41.302→95.852 |30.222→55.813 |147→140 |
| Same resource, direction90 |28.858→114.705 |89.129→99.847 |114→113 |
| Same resource, direction180 |38.870→110.198 |123.218→162.065 |111→95 |
| Same resource, direction270 |51.055→25.323 |18.471→125.943 |144→115 |

Across the four rotated resource scenes, mean final displacement40.021→86.520U (+116%); total signed displacement261.040→443.668U (+70%); reverse intervals516→463. One final displacement is worse. Every screen still has0 pickups and0 invalid material states. Thus the correction yields concrete evidence of improved sustained displacement and directional response in this screen, **not a solved walking/navigation claim**.

## Required validation and preservation

-97 local JVM entrypoints PASS, including new `Zero172MotorResponseRegression`: real neural/material response delay; random proposals wait; danger/strong neural alternatives interrupt; incomplete options cannot train durations; historical48-cell duration load, exact roundtrip and corrupt-save atomicity; opposite residual velocity cannot inherit trust; obsolete epochs free capacity; new context/save/reset wiring.
- Existing historical tests retained. `Zero165MotorPersistenceRegression` checks the current md172 serialization while retaining md160 migration and all directional/safety assertions.
- Original rc22/smh1/cl161/sf167/co162/ms170 learned values are unchanged at migration. Original package `com.overmind.zero.ota`, installed signing identity, named slots and world generation are unchanged. No fake reward, automatic grip, resource direction, new neuron grant or scripted gait.
- CI must run Android debug/release builds, all97 JVM entrypoints, all existing source/signing/permissions gates and the new timing/context gate. Every changed/new Java source is pinned before signed publication.
- Device efficacy remains unverified. Zero pickups in screening and regressions in individual cases are material open results; do not suppress them.

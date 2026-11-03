# ZERO487 — original live V390 motor credit, prior-observation baseline A/B

Date: 2026-11-04. Branch `lab/zero487-onpolicy-observed-motor-baseline-credit`, parent ZERO486 SHA `24a588ab4ae7fb9f06d621813e24b4fb3a9440ff`. **LAB ONLY, RESEARCH, NOT AN APPROVED LOCOMOTION FIX.** No stable merge, user phone APK/OTA, persisted checkpoints or 12-slot mutation.

## Exact source problem and hard evidence
Read root AGENTS.md, docs/AGENTS.md, ZERO30 MASTER, IMPLEMENTATION CONTRACT, UI SPEC, previous ZERO419–423, ZERO454–455, ZERO483–486 and issue #123.

V486 original Android 4-gate same-SHA: S21 10,003 real autonomous decisions **FAIL**, 10.1855% signed/gross, 6/10 stable 1000 windows, inverse windows 4 and 9, 4 new blue; wild 10,002 **FAIL**, 51 RED/4 blue and 19 first-5000 RED vs 32 last-5000 RED. Full regressions and S21 original V356 CRC PASS. V486 is diagnostic only, not live behavioral correction.

V485 same-state original S21 170-material V390 site perturbation, 1,152 real paired ON/OFF trials: original unpaired score*delivered gradient has sign opposite to paired physical delta in 576/1152 measured pairs. This is finite-sample disagreement, NOT proof of 50% incorrect motor learning.

V486 independent physically observed baseline diagnostic (CRC run 41553244637): 2,304 genuine physical matched ON/OFF comparisons, 2,160 evaluated after prior-only warmup. A per-heading baseline trained only from earlier *actual ON* observations raised cosine to paired physical oracle from **0.088958→0.977209** (PRE speed .35), **0.109868→0.987707** (1), **0.151512→0.992428** (2). It reduced wrong-sign site means at each speed. **Not** proof that the same-state offline outcome transfers across unrelated real-world decisions; prospective original 10k required.

## One isolated on-policy live change
The original brain still chooses every action via ONE actual GRU3. Same original retina, contact valence, V353 chooser, original 170-body and 64-output / 512 synapse V390/421 local cortex. Only the isolated V417/V423 *lower local motor learning target* changes in explicitly flagged Android QA.

- `Zero487ObservedMotorBaseline`: 8 slots by **originally issued actual heading**, 3 actual PRE COM-speed regimes (`<.35`, `.35..2`, `>=2`), original real V417 motor outcome only. Baseline begins with zero samples. Warm up on at least 3 prior *physically eligible, contact-free, actual ON* V390 outcomes. Before warmup the corrected local gradient is **zero**, never guessed. Otherwise local V332 learner receives `actualScore - historicalMean`, bounded to [-1,1]. Current outcome enters historical baseline **AFTER** the selected decision's learning; no future leakage or simulated OFF comparator. True measured original harm/front wall/teleports remain excluded as before; independently validated rear-only wall events retain their old V417 credit rather than importing a contact-free baseline. New body on rebirth resets transient baseline; weights/checkpoint not reset. No innate direction, anti-red code, target reward or new body force.
- `Zero3OriginalBrainV356`: additional transient QA flag, old original V417 path unchanged when OFF, baseline centers only own V423 physically eligible credit, CH2 V319 external grip never touched. Read-only counters `v487Accepted` and `v487Centered`.
- `GameActivity`: QA-only V487 enable after required original V400/V417/V421/V423; never a real app setting.
- Two unchanged genuine Android 10k original S21/wild fixtures have ONLY added one V487 QA enable call. No QA food/resources, preset motion, shuffled starting scene or shortened decisions.
- `Zero487ObservedMotorBaselineAndroidTest`: four isolated mathematical guard tests: prior-only warmup, heading and PRE-speed disjointness, body-newLife reset, invalid/unbounded observed outcome protection. They do not count as 10k behavior.

This is a **variance-reduction hypothesis**, not a scientifically verified motor Jacobian: even an accepted baseline mixes real tissue morphology and passive friction, cannot prove a single counterfactual. Since live physics differs from the V486 same-state offline test, V487 can fail or worsen. Do NOT claim success without the original full behavior gates.

## Mandatory exact-SHA Android hard gates
Run 4 workflows all on the exact final SHA and read completed Android logs:
1. Saved user S21 33,312 / 512 weights / V356 CRC / 194 physical sensors / V319 grip, >=10000 **additional real original decisions**: signed/gross >=25%, >=9/10 adjacent 1000-decision windows >=10%, **zero** negative windows, >=5 new true blue.
2. Newborn original wild >=10000 real autonomous decisions with true red/blue material contacts: signed/gross >=12%, RED total <=2x BLUE total, RED last 5k < first 5k.
3. Full original Android regression including V487, and previously verified V483–V486 scientific diagnostics.
4. Byte-for-byte untouched saved original S21 33,312 / 512 weights / V356 CRC and 12 named slots.

The 4 original workflows are configured for V487 experimental branch, never production publishing. Their green build/CRC cannot override behavioral failure; if either 10k fails, retain lab research only and diagnose the failure. No APK, OTA or stable merge unless **all** criteria PASS on ONE SHA.

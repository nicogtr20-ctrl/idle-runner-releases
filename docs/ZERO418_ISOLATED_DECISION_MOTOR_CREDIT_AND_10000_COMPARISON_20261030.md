# ZERO418 — Remove cross-decision muscle credit contamination (research only)

**30 October 2026. NOT approved for APK/OTA, NOT deployed, NOT merged into stable.**

## Real original Android baseline (ZERO417, both >=10,000 autonomous GRU3 decisions)

- Saved 33,312-decision S21 checkpoint, physical wall/blue training room, 10,004 more decisions: **11.2869%** signed/gross; 4 additional blue; two 1,000-decision windows had negative signed motion; 1,851 original 512-synapse motor-cortex updates. The original training room has no red hazards; the 90 red material contacts in the cumulative ledger are historic and must NOT be treated as fresh red touches.
  - https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40958869958
- Newborn original wild GRU3, 10,002 consecutive decisions: **58.8634%** signed/gross, 5 blue and **69 real red** contacts, 5,556 original motor updates. Motion improved but severe hazard avoidance failure (69 > 2 × 5).
  - https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40958870014
- The original source regression and saved S21 33,312 trained-weights/V356 checkpoint-CRC integrity tests passed.
- Both behavioral gates FAILED. Never label ZERO417 "locomotion fixed" or use wild positive signed motion to excuse the S21 regression/hazards.

## Specific causal problem in source

V417 `Zero3OriginalBrainV356.observed()` estimates a PRE→POST velocity change on real S21 body. It writes `localResponseV400` for the current action, but also projects that *same* NEXT decision's velocity change onto the PREVIOUS action heading, fills `delayedResponseV400`, then `learnV332(priorProbe,priorResponse,actualDelayed)` credits the PREVIOUS single-site muscle perturbation. The NEXT decision has its own independent voluntary muscles and may accelerate the body without any causal contribution from the previous perturbation. The V401 60° heading gate removes sharp reversals but does NOT establish causal intervention equivalence. V417 can therefore reinforce a muscle for acceleration caused by another action. This is a **verified source attribution flaw**, NOT yet proof it explains all S21 failures.

## ZERO418 limited experiment

Branch `lab/zero418-isolated-decision-motor-credit`, derived from original V417 commit `7ac542ba5b039bdf44c0bed1b752eae437abcdb3`.

- Add unpersisted `independentDecisionCreditLabV418` switch, activated on the real original `GameActivity` and original V356 brain at a genuine decision boundary *after* V417 velocity credit has been enabled.
- For that research mode only, retain the previous completed decision's actually measured velocity response, but set the next independent action's delayed muscular-credit response to **0**. This avoids fabricating attribution to a different muscular intervention.
- Do not alter GRU3 194 sensory channels, 64 recurrent owner, original 512 synaptic weights or their saved state, V319 CH2 grip, actuation forces, physics, objects, material hazards, original 12 profiles or V356 binary CRC. V400/V417 production default remains unchanged.
- Retain V390 one physical probe, V401 external/world corrections/front wall/red/storm censorship and V414 physically true rear-only wall retreat exception, as well as all original game loops.
- No hardcoded goal-seeking, gait, new motor controller, rewards, teleportation or neural reset.

## PREDECLARED scientific gates, do NOT relax after results

1. Real trained S21 original 10,000 consecutive decision test: >=25% signed/gross, >=9/10 windows >=10%, zero inverse windows, >=5 newly reached blue, original physical owner and local neural updates; use identical exact user 33,312 brain and room.
2. Original newborn wild 10,000 consecutive decisions: >=12% signed/gross; actual real blue, red-exposed environment, red last 5,000 < first 5,000; total true red <=2× total true blue. A motion-only success with many red collisions IS a failure.
3. Original Android regressions, touched/red guards, original real physical sensory and motor owner, checkpoint and 12-slot invariants, 33,312-user trained checkpoint CRC before/after experiment switch all PASS.
4. Compare results against exact original V417 same-test scripts, not synthetic motor commands or a new candidate brain. A green build is never approval.

GitHub Actions V418 runs under original V417 workflow filenames but names tagged ZERO418. Original V417 log identifiers in these unchanged test scripts indicate comparable metrics; inspect `independentDecisionCreditLab=true` in motor diagnostics to verify V418 actually ran.

If any 10k test fails, keep research-only. No APK, OTA or stable merge. User phone existing GRU/checkpoints untouched.

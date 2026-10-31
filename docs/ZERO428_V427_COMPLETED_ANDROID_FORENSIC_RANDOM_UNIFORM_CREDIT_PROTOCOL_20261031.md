# ZERO428 — V427 complete original Android forensic and randomized-uniform actor-score eligibility

Date 2026-10-31. Branch `lab/zero428-uniform-random-impasse-actor-credit`, based on V427 commit `7e32181184dc3acfd4feb89c63eedcde602c14b3`. **Research only, not approved; no APK, OTA, merge, original brain/slot rewrite.** AGENTS.md, docs/AGENTS.md, canonical MASTER/CONTRACT and ZERO419–ZERO427 read.

## V427: actual finished 10,000-decision original Android evidence

- S21 trained 33,312 user decisions restored; [Action 41172705057](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41172705057), job 124453866975, completed **FAIL** after 10,003 additional real decisions to 43,315. 4 new blue, signed 429.96028, gross 3415.35195, efficiency **12.5890%** vs >=25% required; **5/10** 1k windows >=10%, **4 inverse**. Per-window ratios `[.199752,.068161,.414040,-.146299,.496293,.195760,-.005150,-.430228,-.219374,.469705]`. 3,686 genuine local cortex updates, 6,317 censored motor probes, 1,853 rear-wall eligible, 1,279 front-wall excluded; 512 saved weights and actual 170-material body not replaced. Wall contact frames per 1k `[24416,7951,17707,18236,15208,24352,4913,8318,17476,0]`. Significant opposed locomotion persists.
- Wild virgin original [Action 41172704905](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41172704905), job 124453866209, completed **FAIL** after 10,002 true decisions. 3 blue and **38 real red**, red per 1k `[0,0,0,9,9,0,1,0,9,10]`: **18 first 5k -> 20 last 5k** (worse), total red >2×blue. Signed -252.79009 / gross 3712.53391 = **-6.8091%** vs >=12% required. Per-window signed/gross `[-.160899,.051153,-.010831,-.119786,-.153123,.028367,-.111939,-.087420,-.101873,-.007637]`. 5,520 local updates, 1,743 real harmful motor credits rejected, 4,482 motor samples censored. No new blue after first 1k.
- All 38 true wild red contact traces checked: **20/38** issued intent away from hazard (dot < -0.3), **10/38** toward (>0.3), 8 neutral. **27/38** had PRE-contact momentum toward red (>0.3), and **13/38** combined intent away with momentum toward red. 34 contacts offered only 3 GRU candidates, 4 offered 7. This is direct evidence that braking/inertia as well as high-level choice remains unresolved. It is not proof the brain has learned avoidance.
- [V427 original Android regressions + 4 V427 tests 41172704908](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41172704908) **PASS**; [exact saved S21 33,312 / 512 weights / V356 CRC 41172704916](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41172704916) **PASS**. Both technical passes do not override behavior failures.

## Why V427 has a distinct source-level causal eligibility error

In `Zero3RecurrentBrain.choose`, the V384 embodied impasse adds independently uniform directions at candidate indices >=3, overriding their sampled actor headings. V427 correctly lets the *joint* actor-sampled set receive policy-gradient credit if **cognition selects** one of those uniform candidates, because the competing actor-dependent options can affect the selection.

But an independent branch `if(realStall&&!verifiedMotor) if(random()<exploration) selected=(int)(random()*options.length)` **overrides cognition's selection with an index uniformly random and independent of the actor's sampled candidate values**. If this selects k>=3, the executed heading is independent uniform too: the actor heading has no causal influence on that action or its physical outcome. V427 still applies the joint heading gradient of the three unexecuted competing actor candidates to the observed reward. Its expectation is zero in ideal stationary conditions but it injects unjustified high-variance actor updates during the very impasses whose motor learning is already failing.

## Single V428 experiment (no reward, direction or physics changes)

- Preserve V426 exact joint latent sampler and V427 cognition-selected uniform eligibility.
- Mark **only** independent random override of an actual k>=3 uniform candidate; if an accepted planner proposal replaces the override, clear that marker.
- For that exact case, skip the two actor-heading gradient rows, while preserving actual selected effort, adhesion, duration, GRU/world/critic training, cognitive negative-contact learning, V319 CH2, V423/V421 local physical learning and all real movement.
- No additional random draws, force, prescribed heading, new sensor or saved field. Expose a nonpersistent counter `randomUniformActorHeadsSkippedV428` in existing local credit diagnostics so the long original tests can show whether the correction actually fires.
- New `Zero428RandomUniformActorCreditAndroidTest` has five truth-table tests: random uniform skip, cognition-selected uniform retained, actor-dependent random choice retained, legacy V427 behavior retained when joint score OFF, ordinary on-policy retained. This unit test is **not** 10k behavioral proof.

**Causal scope limitation:** the V427 red/momentum evidence does NOT establish that random impasse credit caused the entire red safety failure. This repair removes a specific false learning attribution; locomotion and red avoidance remain scientific hypotheses. No mixing with V420/V422 alternative flags or V2.2, no synthetic rewards.

## Mandatory acceptance unchanged

Exact user original S21 33,312 + >=10,000 real decisions, signed/gross >=25%, >=9/10 windows >=10%, zero inverse, >=5 new real blue. Original newborn wild >=10,000 real decisions, signed/gross >=12%, real blue/red contacts, red last 5,000 < first 5,000 and red total <=2× blue. Full original Android regressions incl. 5 V428 tests and original S21 CRC/512-weight PASS. If any fail, do not approve/publish/merge. Preserve original 194 sensors, 64-GRU3, original 170-material body, V319 CH2, V356 CRC, 12 save slots.

## Original Android CI

The four existing original V417-compatibility workflow files are extended to the V428 isolated branch; a single commit triggers original S21 10k, wild 10k, Android regressions and CRC. Record **actual run IDs and full logs after completion**. A successful build is not an accepted organism.

## Triggered V428 actual original Android Actions (commit 8c2d9bc01387948e65d1c1cb46d7591ea5256fb7)
- [S21 saved 33,312 + genuine 10,000 — 41177945509](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41177945509): in_progress at initial recheck.
- [Wild genuine 10,000 red/blue — 41177945498](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41177945498): in_progress at initial recheck.
- [Original Android regression + 5 V428 tests — 41177945542](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41177945542): in_progress at initial recheck.
- [Exact original S21 512-weight V356 CRC — 41177945435](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41177945435): in_progress at initial recheck.
No behavioral result inferred until actual complete logs. Next check must inspect all four job logs, 1k signed/gross windows, 5k red halves, real blue contacts, and new randomized-uniform eligibility counter; only if all gates pass may a release be considered.

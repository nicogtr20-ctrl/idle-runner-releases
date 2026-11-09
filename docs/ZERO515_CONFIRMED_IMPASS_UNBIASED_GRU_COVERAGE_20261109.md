# ZERO515: verified impasse candidate coverage, original GRU3

## Real failing evidence

On original trained S21 V511, the full 10,003 new decisions returned signed/gross -19.4484%, 449 physically blocked preferred candidates, **zero** eligible physical relief candidates, and >32,000 executive candidate vetoes. See [original trained S21 fail](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/42535825920).

V512 actual first 1001 decisions observed 841 physically verified-impasse selections, 97 blocked preferences and again zero qualifying relief candidates. The previous V511 selector uses 7 candidate actions (including only 4 unbiased random headings) and requires >=0.05 learned cost improvement without increasing actual learned harm. Source trace included examples where 7 headings were all strongly west-oriented; a 0.02 cost improvement correctly failed the explicit threshold. Those observations do NOT prove that changing the threshold or deterministic rightward commands would work.

The full original wild V511 10,001 decisions PASS (17 blue/3 red, +13.6958% movement efficiency), so preserve existing learned-negative-risk gate and normal original choices.

## Exactly isolated experimental hypothesis

- Change only under explicit Android QA flag V515: **if and only if** the already-existing train+heldout V384 body memory confirms a physical impasse, expand the GRU3 original sampled candidate array from 7 to **16**, where indices 3..15 are independent uniformly sampled directions from the original RNG. All are still genuine sampled neural candidate alternatives; no authored wall-escape vector, target, force or choice.
- Keep V511 physical feasibility evidence, minimum relief threshold, all 194 senses, GRU/cortex 512 learned weights, V319 grip/CH2, 170-node material, contact harms, checkpoint schema, and twelve named save slots unchanged.
- If V384 impasse is not verified, candidate count and actual original actor/cognition policy are **bit-identical**.
- Exact unmodified V511 Android 10,000-decision true GameActivity acceptance assertions apply to independent S21 trained checkpoint and wild fresh GRU3; fail means no release, regardless of green compile. Additional same-SHA original motor regressions and actual V356/512-weights CRC required before even considering OTA.
- Randomness is stochastic; do not infer escape competence from one batch or lower the original decision thresholds.

## Required tests
`.github/workflows/zero515-trained-s21-10000.yml` and `.github/workflows/zero515-wild-red-blue-10000.yml` run on lab branch and preserve actual full 1,000-window signed/gross / RED-BLUE counters.

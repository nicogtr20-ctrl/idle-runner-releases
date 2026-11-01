# ZERO455 — V454 matched original-body ABBA OFF control (research only)
Date: 2026-11-01. Branch `lab/zero455-v454-matched-off-control`, branched from V454 source commit `e12b1160d71ae342555b3a659953c9cad152012d`. **No APK, OTA, stable merge, checkpoint edits, 2.2 restore, fixed direction, or fabricated reward.**

## Why this experiment is needed
The independent ZERO454 audit documented that the V454 ABBA ON/OFF/OFF/ON physical pulse is not a perfect counterfactual: ON changes gel state, OFF inherits inertia, and the intervention changes both motor duty cycle and V332 credit target. V454 original 10k tests also activate V421 delivered physical probe and V423 immediate own-decision credit, along with the existing V420/V425/V426/V432/V433/V434/V435/V442/V422 opt-ins. A direct comparison with an earlier V453 report is not enough to attribute a difference uniquely to ABBA.

## Exactly one matched variable
The two existing original Android 10k test classes are copied from the SAME V454 implementation and keep all original opt-ins and real S21/wild fixtures, but omit **only** the call to `GameActivity.originalV454EnablePairedMotorContrastForQA()`. The `Zero454PairedMotorContrast` source and original material solver are unchanged, so the default flag stays false; the original V417 measured COM response and V421/V423 physical learning remain active. Log marker `ZERO455_MATCHED_CONTROL V454_ABBA_OFF=true` proves this condition at test startup. This is an OFF control, not a claimed new correction. The only causal variable relative to V454 is V454's combined physical pulse schedule + estimator, not the estimator in isolation.

## Strict Android evaluation
Run original S21 33,312-decision restored checkpoint with 512 original cortex weights and original 194-sense 64-GRU3, V319 CH2, original 170-material body and 12 saves, for >=10,000 genuine autonomous decisions. Require signed/gross >=25%, >=9/10 adjacent 1000-decision windows >=10%, zero inverse windows and >=5 additional real blue. Run genuine newborn original wild >=10,000 decisions with actual red/blue collisions, signed/gross >=12%, red last 5000 < first 5000 and total red <=2*blue. Also require full original Android regressions and original V356 CRC PASS on the same exact final commit. Log all 20 windows, local gradients and red/blue/physical/GRU3 counters. Never count inherited S21 red contacts as new harm.

## Status
The OFF control is being staged on its own branch; V454 branch is untouched. Workflow conclusions and run IDs are **not yet verified**; no PASS, causal efficacy or release claim can be made. If either arm fails, compare same-source ABBA ON vs OFF in genuine 10k data before changing the model. No threshold relaxation or publication.

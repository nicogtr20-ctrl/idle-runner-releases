# ZERO443 — actual-original V442 motor evidence eligibility, READ-ONLY telemetry

2026-11-01. Isolated LAB branch `lab/zero443-motor-evidence-eligibility-telemetry`, based on original V442 source `14c3a7ffbdf508c7e4f60ea8b4fba389fd8573e1`. **NOT A BEHAVIORAL FIX. NO APK/OTA/STABLE MERGE.** The V442 original Android tests conclusively failed: S21 10,002 new decisions, 11.1428% signed/gross, 4 new blue, 6/10 acceptable windows and two inverse; wild 10,005 decisions, 1.4212%, 49 real red /4 blue, red first 5,000=29 and last 5,000=20. Android regression and exact 33,312 saved S21 V356 CRC/512 weights PASS. See [full V442 forensic](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/audit/zero442-real-10k-failure-and-drift-root-cause-20261101/docs/ZERO442_COMPLETED_ORIGINAL_10K_FAILURE_FORENSIC_20261101.md).

## Why one diagnostic change, rather than guessing a motor correction

The wild V442 ten 1k windows and 49 red/4 blue are effectively identical to V441. Source `Zero3InstrumentalCreditV371.score` censors the V434 delayed historical bank when PRE speed >=0.35 and V377 motor response is not independently verified. However the previous four-case regression used artificially constructed bank history. It never proved the censored value was nonzero in genuine original gameplay. Simultaneously `opticalPreferenceV374` returns zero in the same strict unverified-drift state. Without measured statistics, editing policy weights, physical damping or actor heading is not causally justified.

## Exactly one instrumentation-only intervention

- Add transient `motorEvidenceDiagnosticsV443()` counters to **the existing** `Zero3InstrumentalCreditV371` for candidate score evaluations, V377 verified predictions, PRE drift gating, nonzero/zero historical bank scores actually censored, nonzero bank scores not censored, optical drift suppressions, V377 reliable banks, V371 verified directions, and real delayed links.
- On a V442 branch that already returns zero, evaluate the pure historical score for observation **only**; never alter the original zero return, issued heading, sensory values, RNG, V369 contact valence, energy, reward, motor learning, V319 grip, 170-material solver or save layout. No new persisted field, sensor or neural weight.
- Expose diagnostics via original V353/GRU3/V356/GameActivity QA read-only accessors. The exact original S21 and wild 10k Android tests log `ZERO443_MOTOR_EVIDENCE` **before** behavioral assertions, so failed runs still provide evidence.
- Extend the V442 physical regression with a fifth test proving a nonzero historical negative score is counted while the returned censored score remains exactly zero. This is a telemetry invariant, not a survival pass.
- Four original Android Actions on **one final source commit**: S21 >=10k, wild >=10k, regressions and original saved S21 CRC. The workflow branch filters are enabled solely for this separate lab branch. No APK/OTA or stable merge regardless of diagnostic outcome.

## Precommitted analysis

If `censoredNonzeroHistorical=0` in original wild 10k, V442's behavioral effect was absent or negligible in that test, despite its unit PASS. If `verifiedV377Banks=0`, no heldout-certified motor mapping exists; assigning arbitrary desired headings as safe physical travel would be unsound. If there are nonzero censored scores yet behavior unchanged, next inspect planner/executive overrides and whether motor commands actually overcome PRE COM inertia. These counters are **score evaluations**, not distinct GRU decisions, and cannot be misrepresented as new pickups or movement.

Mandatory unchanged approval: exact original S21 33,312-trained +>=10k decisions, >=25% signed/gross, >=9/10 windows >=10%, zero inverse, >=5 new blue; original wild >=10k real decisions, >=12% signed/gross, red last 5k < first 5k and total red <=2× actual blue; regressions and CRC PASS. Original 194 sensors, 64 GRU, 512 weights, V356 CRC, CH2 V319, 170-material body and 12 saves preserved. No V2.2.

# ZERO444 — original V377 heldout motor-evidence eligibility, read-only experiment

2026-11-01. LAB branch `lab/zero444-original-v377-heldout-evidence-diagnostics`, based on exact V443 source `4e243b4b2e91d744f58a21185d87a59c97872bc0`. **Instrumentation only, not a locomotion fix. No APK, OTA, stable merge, V2.2 restore or save mutation.**

## Reason to measure before another behavioral patch

Original V443 2×10k Android tests were complete and failed: S21 10,003 genuine decisions, 11.1429% signed/gross, 4 new blue, 6/10 qualifying windows, 2 inverse; wild 10,005 decisions, 1.4212%, 49 red/4 blue, first red 29 last 20. Regressions and exact 33,312-trained S21 CRC/512 weights passed. [Complete forensic](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/audit/zero443-complete-original-motor-evidence-20261101/docs/ZERO443_COMPLETED_ORIGINAL_ANDROID_10K_MOTOR_EVIDENCE_FORENSIC_20261101.md).

V443 measured 413,987 wild candidate score evaluations, just 861 with V377 prediction, 323,649 unsupported PRE-drift evaluations, **0 nonzero historical delayed scores** and **0 delayed real links**, 1 reliable V377 bank/16, 0 V371 verified directions/8. In S21 74,271 nonzero historical scores were censored, 5 V377 reliable banks and 0 V371 verified directions. Hence changing V442's historical bank score cannot explain wild avoidance: the bank is empty. We must establish why the **actual original body's** action-conditioned response fails heldout certification.

## Exactly one read-only code intervention

In the existing `Zero3InertialResponseV377`, add transient counts for clean accepted TRAIN and independent HELDOUT samples by the original <2 / >=2 PRE speed regimes; and a read-only diagnostic of stored per-bank TRAIN>=4, HELDOUT>=3, joint sample sufficiency, actual coastLoss>.10, heldout loss<.90*coastLoss, reliable banks split low/high. Append to the existing V443 diagnostics logged at the END of original S21 and wild 10k runs. These counters are never used in choice, learning, physics, value, rewards, V369 contact learning, GRU, local cortex, motor command or serialized checkpoint. They are not new 194D sensors. Copy counters in memory for diagnostics only; `read/write` schema unchanged.

This distinguishes **missing clean data** from **sufficient data with invalid physical prediction**. A high number of accepted clean samples but low heldout loss pass would point to response nonstationarity/poor predictor; low accepted samples points to eligibility starvation. Neither outcome authorizes a scripted direction or fabricated motor reward.

## Original Android acceptance and stop conditions

Run exact saved user S21 33,312+>=10k and newborn wild >=10k, original 170-material body, original 194 sensors/GRU64/512 cortex weights/V319 CH2, and regressions and S21 CRC. Require original hard gates unchanged: S21 >=25% signed/gross, >=9/10 windows >=10%, 0 inverse, >=5 blue; wild >=12% signed/gross, red last 5k < first 5k and red <=2x blue. Four workflows include this branch only as a new push filter; no other behavior flag changes. A scientific FAIL still records V444 diagnostics; compare V443 baseline for any accidental instrumentation perturbation. No release until all gates pass.

**Run IDs to append only after GitHub actually creates them.**

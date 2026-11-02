# ZERO476 — transient intermediate PRE-drift V377 motor-response model (ONE LAB VARIABLE)

Date 2026-11-02. Branch `lab/zero476-v377-transient-mid-drift-heldout-model` based EXACTLY on V475 `0c164323a59d7c69770351a0b1e9c331bdba2f2f`. **Experimental, not a release.** Read AGENTS.md, ZERO419–ZERO423 and the complete V475 Android original 2×10k logs before changing code. [Full V475 audit](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/audit/zero475-complete-real-10k-and-v377-drift-regime-20261102/docs/ZERO475_COMPLETE_ORIGINAL_ANDROID_2X10K_REACHABILITY_AND_V377_SPEED_ALIAS_AUDIT_20261102.md).

## Why this ONE change, not another unvalidated optical override

V475 same-SHA Android: S21 10,003 decisions **9.6435%**, 6/10 >=10%, inverse 4/9, four blue; wild 10,005 decisions **1.1177%**, 30 RED / 3 blue, RED first 5000=13 and last 5000=17. Regressions and saved original V356 CRC both PASS, behavior both FAIL. Of 403,399 wild candidate motor evaluations, V474 reconstructed motor fallback used only **567 (0.14%)**; **258,716 (64.13%)** lacked trustworthy motor prediction under PRE drift. In 30 real red contacts, 24 had PRE physical momentum toward the hazard, and **25/30** had measured PRE speed in **0.35..2.0** U/s. The current V377 motor-response map collapses this entire physically moving range into its LOW bank (split at 2.0), but V441/V433 correctly treats speed >=0.35 as real drift requiring physical prediction before optical steering. This is a source-confirmed **state aliasing**, not proof that the fix will succeed.

## Isolated causal experiment

**ONE change** to V377's **action-conditioned physical response model** in an explicit QA-only original GRU3 mode: when measured PRE speed is 0.35<=speed<2.0, train/predict from eight **new transient mid-speed banks**, not from the pre-existing stationary (<2.0) banks. All bank means remain measured POST displacement minus measured PRE coasting; at least four real TRAIN and three independent HELDOUT observations, loss better than physical coast and positive forward response >0.10 remain mandatory. A bank with no independent real evidence returns NULL, so existing V433 optical-drift censor remains active; **never** infer movement from the desired heading.

Original V377 16 saved low/high banks, their indices, all data, and their original read/write schema remain byte-for-byte unchanged. The eight extra banks are **ephemeral in RAM**, copied across in-memory life states but intentionally NOT serialized to V356/12 user slots; a cold resume starts them empty and must earn heldout evidence again. This is a limitation of this safety-first ablation, not a proposed production migration. The original S21 pre-existing low/high data are never reinterpreted. Normal production mode remains OFF and has the original 2.0 split and 16 banks.

No changes to GRU3 194 sensors, 64 hidden units, 512 learned cortex weights, CH2 V319 adhesion, 170-material body, original rewards, red damping, original GRU actor, stochastic candidate distribution, local gradients, or physical forces. No preset heading, scripted escape, V2.2 restore, extra energy, checkpoint migration, APK/OTA/merge. The same original S21 and wild 10k Android tests opt in at an actual decision boundary; all other prior lab flags remain matched to V475.

## Android scientific checks and immutable gates

New `Zero476V377TransientMidDriftAndroidTest` checks (1) real TRAIN alone cannot validate, three HELDOUT can, and mid bank cannot masquerade as stationary; (2) original unflagged 2.0 regime unchanged; (3) exact original V377 serialized byte length and transient mid not saved, but in-memory copy preserved; (4) pure PRE coasting cannot become active motor authority. These synthetic physics formula checks are **only regressions**, not behavior proof.

The four original Android workflows must run on the **same final V476 SHA**: real restored 33,312-decision S21 for >=10,000 additional decisions and true wild newborn >=10,000; full regressions including V476; original V356/512-weight CRC. **S21 requires >=25% signed/gross, >=9/10 1000-decision windows >=10%, zero inverse, >=5 new blue. Wild requires >=12% signed/gross, actual blue pickups, red latter 5000 < first 5000, total red <=2× blue.** If a workflow fails or is running, no approval. Compare all 20 windows, motor bank reliability, PRE momentum at red contact, gradients, red/blue and original physics.

## Status

Source and regression added on this separate branch. **No behavioral success is claimed.** Actual workflow run IDs, same-SHA results and final verdict must be appended only after observing GitHub Actions. Never distribute APK/OTA or merge on technical-only PASS.

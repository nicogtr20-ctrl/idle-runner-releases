# ZERO 2.1 — Implementation ledger and executable source contract
**Date:** 13 October 2026. **Live production baseline:** 0.11.6-ota.112, versionCode 1112 (Android package com.overmind.zero.ota). **Research branch:** feature/zero21-unified-cognition based on feature/overmind-inapp-updater. **Release status:** NEVER published from this branch. Android profile/12 slots on the actual phone have not been touched.

## Source of truth and sequence
1. User requested a big, integrated research effort replacing piecemeal disconnected modules, and detailed GitHub papers that another chat can use.
2. Read repository AGENTS, OVERMIND_START_HERE, main and stable documentation, existing experiment papers 0095–0127 and ZERO_20_INTELLIGENCE_RESEARCH. Stable versus feature/zero-20-hierarchical-intelligence are *diverged*. Do NOT wholesale merge old experimental branch.
3. Retrieved latest published metadata from main/updater/public/latest.json at start: versionName 0.11.6-ota.112, versionCode 1112, APK SHA256 35829304a1245931eef55fd494170d03ca7760a6c00f697581601b497d32ce61, URL https://overmind-zero-updates.vercel.app/apk/overmind-zero-1112-35829304a124.apk. This is the reference release, not a NEW build.
4. Isolated new development branch feature/zero21-unified-cognition from existing stable Android source. No branch rewrite, Android release action or user save modification.
5. Added executable shared evidence kernel, legacy-integrated event feeds, optional persistence, scoped reset, dedicated regression and full Android/JVM staging workflow.
6. Initial dedicated QA run 38451441375 FAILURE at javac because the copied historical CI class list omitted *existing* ObjectExperienceV20.java and its regression; no runtime source failure was established from that run. Fixed class list without dropping existing tests in commit 0e6b6bb2d30e14ac47f3f5de009dc9209bc382e5. New run 38451873419 was launched by GitHub. Recheck its final status and log; CI green must NOT be assumed from workflow existence. After fixing any reported failure, rerun on the EXACT FINAL CODE SHA.

## Actual new code, not roadmap
### UnifiedCognitionV21.java
Path: app/src/main/java/com/overmind/zero/UnifiedCognitionV21.java
Pure Java, no Android API. Public KEY unified_cognition_v21, FORMAT uc21. Belief snapshot contains actually observed reserve fraction, actual material touch and held-grip fraction, nearest sensed obstruction, uncertainty and confidence, and a *tentative* object-hypothesis ID. Four coarse contexts (metabolic reserve <0.35 or not x actual tactile/near-solid evidence) times 16 action choices, 64 bounded action-outcome tables. Stores action opportunities, measured external effects, actual positive/negative clamped-contact events, and net actual energy over a completed action interval. Conservative observational statistics, NOT causality per instance.

Main APIs: chosen(action,26senses,16observedRaysOrNull,hypothesis,worldConfidence,objectConfidence); energyContact(realDelta); consequence(afterSenses,actualMeasuredExternalMovement,train,holdout); resetLife/resumeLife; encode/restore; confidence; summary. Trained cells are never updated by heldout intervals. Confidence 0 unless at least 80 heldout, 12 genuine external-motion events, baseline nontrivial and >8% heldout Brier improvement. Even if confidence positive, this source has NO policy score API. Every chosen/consequence pair is a single observed interval. Pending action intentionally not saved or replayed. No hidden object ID, ground truth position, category-specific physics, privileged resource occupancy or pretrained expert behavior.

Serialization: UTF-8 uc21 delimited representation of bounded arrays/counters, strict parse of finite floats/doubles/counters; atomic replace on valid full document only. Missing optional key = old profile, empty virgin model. Cross-life learned cells persist; on death only transient action and unverified identity reset.

### SensorimotorMind.java
Path: app/src/main/java/com/overmind/zero/SensorimotorMind.java
Added a *transient reference* to UnifiedCognitionV21, attachUnifiedCognitionV21, energyContact hook using actual clamped contactOutcome, chosen hook placed AFTER original action scoring+random exploration+final choice, and real consequence hook after original physical observation with the SAME LearningDiagnostics.shouldHoldout split. Before choosing next action, completed interval is recorded exactly once. Calls resetLife when real death begins and resumeLife after restart. Original learned model serialization stays intact; no new score term, reward term or physical actuation, no additional RNG use for action selection. Keep the original enableZero20ExperimentalAdvice(false) device boundary.

Potential research limitation: externalDisplacement supplied is generic original physicalEffect accumulated across visible simulation; NOT per-object identity; proven grip event may be shorter than a 0.65s decision. Later genuine causal tracking must consume physical-level time-aligned evidence without faking source attribution.

### GameActivity.java
Path: app/src/main/java/com/overmind/zero/GameActivity.java
Adds isolated field UnifiedCognitionV21 unifiedCognitionV21; instantiates/restores it while rebuilding SensorimotorMind from active SharedPreferences; attaches and saves via putString(KEY, encode()). SaveProfileCodec/SaveSlotStore already capture typed preferences and named profiles atomically, so no new slot versionCode/schema rewrite. Other model keys and 96-point physical simulation untouched. On a loaded old save absent key, optional restore(false) leaves a virgin ZERO21 evidence store while prior motor/model memory is left alone.

### ResetDomains.java
Explicit WORLD reset now includes UnifiedCognitionV21.KEY. No second reset domain or overlap. An intentional WORLD reset removes shared hypotheses, whereas death/respawn never removes consolidated learning. Full new ZERO clears everything by existing behavior.

### UnifiedCognitionV21Regression.java
Path: app/src/main/java/com/overmind/zero/UnifiedCognitionV21Regression.java
Stand-alone pure JVM main. Tests 26-channel sensory context separation, real mechanical displacement NOT assumed energetic, real positive and negative contact counts, provisional hypothesis/state, no phantom outcome on resume, exact encode/restore, heldout isolation from training, virgin zero confidence and appropriate abstention, atomic rejection of malformed/NaN/truncated/incompatible serialization, cross-life memory preservation and invalid sensor fail-closed. CI additionally tests existing world, grip, physics and old policy.

### zero21-unified-qa.yml
Path: .github/workflows/zero21-unified-qa.yml
Dedicated GitHub Actions trigger on push to feature/zero21-unified-cognition and manual workflow_dispatch. Based on existing full consolidated shadow QA: builds ALL historical pure Java model sources explicitly, invokes original physics/body/grip/world tests plus new unified model and ObjectExperienceV20 regression, runs static checks proving unified observer never enters choose candidate-scoring function, verifies attaches/persistence/reset and compiles full Gradle Android assembleDebug. No signing, release, manifest or Vercel production write. Artifact upload not required. CI cost limited by concurrency with cancel-in-progress.

## What is NOT coded in this phase
- Multi-view tracking of the same anonymous obstacle, learner-side tracking confidence.
- Per-object tactile/action/energy event attribution and long causal horizons.
- Object-conditional HAND+MEMBRANE technique archives with cross-world validation.
- Complete replacement of independent model scoring with a single trusted arbiter.
- Causal uncertainty estimates robust to extremely rare hidden reward.
- Validated bounded five-step predictive use in LIVE movement.
- Cost/battery/FPS profiling on real S21 and matched long-life uplift.
- Release APK / updater button / signed production distribution.

Do NOT confuse added shared ledger with finished unified cognitive architecture. Paper ZERO21_UNIFIED_COGNITION_MASTER.md contains the engineering contract for G2–G7. The current implementation is a safe first integration and evidence layer, not an AGI completion.

## Known scientific blockers from predecessor
1. Original grip loss could coexist with accepted hand/membrane weight changes, and current grip2 contextual protection is not a fully proven durable object-conditioned skill.
2. v20 shadow world model often lost heldout calibration against unchanged-state baseline; experimental planner abstained all 5,578 times in one recorded device window. Increasing model size or lowering thresholds to force decisions is not a valid solution.
3. Image/ray hypotheses group similar silhouettes but do not ensure single-object identity, particularly near overlapping boxes.
4. Energy under covers is physically hidden/occluded except optical rim clues; even successful cover motion may not provide immediate contact feedback, requiring cautious long-delay event tracing.
5. Some older handoff headings are stale. The actual OTA112 UI code and source must be treated as current baseline.
6. A GitHub CI pass proves build/test invariants; not behavior on real device or retention across 100s of lives.

## Evidence and QA checklist for every subsequent commit
- Pin exact source SHA, branch, parent/PR and file paths.
- Name every affected API and check current code before applying patches.
- Run the dedicated ZERO21 QA after all source modifications and check every job's logs on the SAME final head. Never hide a red test by removing it.
- Execute paired real physics A/B with identical original brain, seed, objects and hidden resources; report 0/0/ties honestly.
- Evaluate new habitats and already-effective original grip/locomotion; measure no regressions from protected external-effect records.
- Verify save/load old keys, two independent slots and reset domains, including interrupted action.
- Explicitly state SHADOW_ONLY or what actual bounded bias is enabled and how it can safely be rolled back.
- Ensure no signing key/token/private user-brain raw data in paper or CI logs.
- After empirical uplift, and not before, selectively promote via PR to feature/overmind-inapp-updater with official signed publisher. Use normal AGGIORNA only after manifest SHA and Vercel APK verification. No uninstall and no forced reset.

## Handoff / next concrete development
First finish all new-source CI on exact code SHA, including Android compile and legacy tests. Then implement G2–G4 as a coherent shared event/identity/tracing pipeline (not blind new per-module score additions). Produce evaluator-side novel object rotation/occlusion fixtures and proof of identity continuity before enabling world beliefs. Only then build G5–G7 skill archive and uncertainty-gated advisor, paired headless benchmarks and long-life device QA. Document source, runs, failures, and user observation in this file and docs/ZERO21_HANDOFF_NEXT_CHAT.md.


## Verified final executable QA / pull request — 13 October 2026
- CI [38452161103](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38452161103) on tested CODE commit `7361e67f3f82c45913f7263b3b9fee1eb8f6dfee`: **SUCCESS**. Verified GitHub job 115485537613: full Java compilation; all legacy physics/learning regressions and new ObjectExperienceV20 + UnifiedCognitionV21 regression; static non-interference against 16-action scoring and physical laws; Gradle Android assembleDebug; all check steps green. All later commits on feature/zero21-unified-cognition before the draft PR were Markdown-only handoff/whitepaper/roadmap updates, not source modifications.
- Draft research PR [#39](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/39) opened against `feature/overmind-inapp-updater`. **DO NOT MERGE OR RELEASE.** There is NO new signed APK/OTA/versionCode, no user phone upgrade and no proven cognitive uplift. Signed production remains OTA112.
- A code or workflow change after this timestamp requires NEW final-head QA; a documentation-only commit does not alter the executable snapshot. A successful Android Debug build is NOT a completed signed APK.

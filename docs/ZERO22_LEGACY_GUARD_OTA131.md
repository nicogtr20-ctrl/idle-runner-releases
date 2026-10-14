# OVERMIND ZERO 2.2 — OTA131 recovery: preserve decisions, keep continuous learning

Date: 14 October 2026. Candidate branch `feature/zero22-legacy-guard-ota131` before publisher merge.

## Data provenance and diagnosis
- User reported fewer effective box manipulations, deaths with visible blue/purple light on OTA130.
- Real pre-report `20261014-161415-e4b93716c12f` 63 finished lives; post-report `20261014-164053-e62d161841c1` 75 finished lives. 12 completed post lives average 21.415 simulated minutes vs previous 10 completed pre lives average 33.653 minutes; unequal windows, not a causal randomized test.
- Box uncover rate: 3.8474 per 1,000 simulated seconds prior vs 2.9994 after (−22.04%); finite energy pickup 15.273 vs 13.367 per 1,000s (−12.48%). Box motion remains mechanically possible, so this does not prove grip regression by itself.
- Decoded `rc22`: pre physical confidence valid 15/16, energy valid 0/16; post physical 16/16, energy valid 0/16. The summary's old 0.000 was NOT live confidence: `Zero20PassiveSnapshot` restores fresh `RecurrentCoreV22` without serializing lastConfidence.
- Decision flight record for post last death 457 rows, 456 nonzero `recurrentDelta`; 23 deltas exceed OTA130's physical-only max .12, and 50 rows have epsilon below the incumbent .09 limit. These prove transient energy confidence enabled deeper influence and exploration suppression even though the saved model no longer validates energy forecasting.
- In the last 24 actions before that death action 11 occurred 22 times despite multiple visible spectral signals. Visible wavelengths are not proof that accessible live energy was nearby or that ZERO knows which spectral channel is positive.
- Root issue: OTA130 `physicalTrust` validated general state prediction only but transferred authority to **unvalidated outcome/value Q heads**. Transient energy MSE can also briefly enable high-authority imagined rollouts without a stable independent outcome test. Thus exploration suppression and action preference distortions were not safe.

## OTA131 conservative intervention / no reset
1. Keep all rc22 state fields, weights (dynamics, Q/reward/risk), replay, learned body/hand/membrane controllers and 12 slots bit-compatible; don't copy user private checkpoint to source repository.
2. Recurrent model still collects new actual experience, trains physical forward dynamics, reward/risk/value heads, validates on heldout and can run isolated shadow evaluation `evaluate(null)`. Same OTA130 rebalanced rare replay; neither resets nor discards old evidence.
3. **Policy authority is disabled until an independent held-out outcome validation exists**: `evaluate(oldScores)` computes shadow scores, then returns exact clone of incumbents (`oldScores`) when provided. No physicalTrust→Q leakage, no transient energyTrust→control path. `exploration(previous)` returns exactly previous, so mature creatures retain their prior epsilon and loss of contact does not suppress discovery.
4. The external and internally accessible statistics label the model SHADOW POLICY / SHADOW_LEARNING; real physicalReady and energyReady are calculated from heldout measurements rather than relying on transient lastConfidence.
5. Permanent baseline protection has priority to a partial learning claim. No hand-scripted movement toward blue/purple light; ZERO still learns those correlations itself. No hard-coded objects/rewards, and no false evidence that manipulation improves.

## Regression acceptance gates
- JVM regression fixture recreates the OTA130 regression with `physicalConfidence>0` and `energyConfidence>0` on a contrived transient heldout spike, plus large learned Q bias, and verifies incumbent action scores exactly unchanged and epsilon exactly unchanged.
- 120 paired choices for a generated mature base policy before and after recurrent attaching must be identical, with core still preserving sample arrays and encoding/decoding.
- Legacy broad JVM suite, signed publisher static gates, Android debug build, signed release and public manifest/APK SHA, HTTP 200.
- Measured read-only simulated lab cannot by itself prove an in-vivo recovery; user needs new actual longitudinal telemetry, including per 1000s rates, true box holds (not command ON), deaths, and spectral visibility vs physical reachability.
- OTA131 is a **safety correction**, not a new cognitive level; only validated action-outcome modeling should later justify reintegration of ZERO2.2 into policy.

## Signed delivery / ready for direct HTTP verification
- Candidate PR #44 merged into canonical publisher branch, commit `02db7bf60203090193bd10301b49bc9045a0f30e`.
- Corrective QA run `38602807671` SUCCESS (full JVM, policy/physics invariants, Android assembleDebug).
- Signed publisher run `38603153465` SUCCESS (41 checks), GitHub Release tag `overmind-zero-v0.11.6-ota.131`.
- Android `com.overmind.zero.ota`, versionCode 1131, versionName `0.11.6-ota.131`, APK 398628 bytes, SHA-256 `8984cbe90c5af831b440346eb37933aa982556b5bdc9b9dcddc98e374a752bfd`.
- Manifest `main/updater/public/latest.json` matches release; Vercel production deployment `dpl_BFav84z1u8V9vchKQSGJz4eTtCmL` READY / alias `overmind-zero-updates.vercel.app`; SHA `45f58b99ae0a57ed0de336e9e61519a3ed8830a3`.
- Official signed APK URL `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1131-8984cbe90c5a.apk`.
- **HTTP GET verification is still pending separate live-delivery workflow**; do not claim download integrity before successful workflow.

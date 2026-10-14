# ZERO 2.2 / OTA130 candidate — split prediction confidence, causal audit
**Data audited:** real report `20261014-153138-e265da168a57` (ZERO 2.2 OTA129, checkpoint 50,217 decisions); binary `rc22` decoded from current telemetry, original GitHub release tag `overmind-zero-v0.11.6-ota.129`.

## Proven fault before patch
- Training samples, heldout counts, and model dynamics are real and persistent, 16/16 actions meet the incumbent >=4% world-state improvement threshold.
- All 16 *fail* the energy-specific MSE guard; validation counts range 175–2,098, train samples 722–8,374 (others likewise meet thresholds).
- Consequently every active authority coefficient was 0.0, despite improvement to general state MSE of 7.17% to 27.04%. The displayed summary `confidence=0` was genuinely an all-actions veto, but NOT proof of lack of sensory learning.
- Caveat: success on aggregate state prediction does not prove per-object knowledge, navigation quality or actual behavioral uplift. Outcome/value head could still be miscalibrated. Neither energy forecasting nor future energy imagination is trusted.

## Conservative change
1. `confidence(a)` derives **physical/sensory confidence** by subtracting saved energy squared-error component from saved aggregate MSE, dividing by 157 observed non-control, non-energy dimensions (`F=164, 6 control channels excluded`). Exactly the same heldout EMA/same checkpoint; no new slot.
2. `energyConfidence(a)` is a separate hard gate, retaining original energy baseline tolerance `energyError<=energyBase+0.00002`. `rollout` verifies energy confidence on every step; no multi-step energy imagination without validation.
3. Physical-only heldout confidence gives **bounded** influence to the existing real-experience-derived value/risk head: `min(0.16,0.35*maturity*physicalTrust)` blend, capped absolutely at +/-0.12 score/action. Invalid physics leaves legacy policy bit-exact. Full trusted-energy authority remains bounded by <=0.85.
4. Replay of rare events still updates learned **value, harm and Q** but no longer retrains the typical *forward dynamics* on an artificially frequent rare-event distribution. Real and ordinary recent transitions continue training forward dynamics. This is a causal hypothesis requiring matched validation; count of replays unchanged.
5. `planned` historical counter still counts fully energy-validated choices (not physical-only) and survives old saves, with `energyReady=n/16` appended to summary. Exploration of mature organisms remains legacy until energy confidence validates; no virgin reset. Old `rc22` compressed format and all other memory/profile slots preserved.

## Explicit risk boundary
- Real-energy risk has **not** been validated, even after this patch. Bounded action-value contribution is experimental; no fixed heading, scripted resource targeting, magic pick-up or fake reward.
- Never claim behavioral superiority without same-checkpoint A/B scenarios and in-device observation.
- QA requires all preexisting JVM fixtures, new energy-blocked/physical-validated fixture, Android debug/release-signing path, save/restore preservation, and signed OTA release validation.
- Preserve Galaxy S21 installation identity `com.overmind.zero.ota` and historic signing key. No uninstall/reset.

## Validation checklist and pending real-world checks
- [ ] JVM fixture passed in Actions
- [ ] Existing broad regression passed
- [ ] Android assembleDebug passed
- [ ] Publisher signed release succeeded
- [ ] Official public `latest.json` verified against released sha/bytes/version
- [ ] Official public APK verified HTTP 200 and correct hash
- [ ] Paired A/B on snapshot with 50k transitions assessed
- [ ] Galaxy S21 backup saved and in-place install/device behavior measured

### Technical diagnosis behind rare replay
`train(e,true)` on each special-event replay (up to one every decision) resampled energy collection/death with frequency not matching its observational frequency. Hence energy prediction may become worse than the 0-energy-change reference even as non-energy channels improve. It is a plausible mechanism, **not yet experimentally established**, and can affect the value head if over-generalization is present.

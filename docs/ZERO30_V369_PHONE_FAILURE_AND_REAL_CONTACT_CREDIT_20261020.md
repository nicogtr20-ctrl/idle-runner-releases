# ZERO3 V369 — Phone V368 failure / contact-valence missing bridge (20 Oct 2026)
Status: RESEARCH ONLY. Build/code changes DO NOT prove blue pursuit or red avoidance. Stable device test V368 user has reported FAILURE. Never label V369 cured without independent Android + phone evidence.

## Actual data from Android user upload, not emulator guesses
Source: zero-telemetry/history/20261019-230608-2775ea22f2d5b.json, created 20 Oct 2026 01:06 CEST (23:06 UTC). Installed 3.0-ota.368-test.1 (versionCode 1183); GRU CRC checkpoint valid.
- 30,081 closed GRU decisions, 24,066 train, 6,015 heldout, 18,739 cortical updates, 58 completed lives.
- Historical world counters: 199 REAL red contacts, 7 REAL blue contacts, 7 positive finite pickups and 0 passed rooms.
- Last 50 completed lives (the ring history): 194 harmful source contacts, 7 positive pickups, 1,304.1 energy damage. Five successive 10-life groups: (36 red,1 blue), (36,1), (48,2), (36,3), (38,0). NO visible improvement of red frequency with more lives; 0 blue in latest 10.
- Current-session motor diagnostic 9,796 decisions; 5,799 aligned, 2,213 opposed, 2,000 externally corrected; 3,998.49 signed along commanded intent vs 10,611.32 gross distance = 37.68% signed/gross. This number is a relative motor metric, NOT 37.68% chance of reaching a target.
- Predictor energy and damage are HELDOUT-TRUSTED yet survival is not. RMSE across interpolation is NOT action-conditioned causal discrimination over unseen alternatives.

## Verified missing pathway
Production original `GameActivity.resolveObjects` already measures `sightAtTouch` before `object.touch`, along with the exact realized `reserve-before`. Its learning call to the old `SensorimotorMind.contactOutcome(...)` is explicitly gated `if(brain3==null)`. GRU3 instead receives a net energy reward after a variable motor intention. This is insufficient to assign a several-choice approach history to the perceived frequency with certainty. V368 correctly credited terminal outcomes, but did not restore the original direct contact association. Reusing the V2.2 choice brain would violate GRU3 single-authority. Reusing its EVENT LINK, inside V353's cognition, is the approved repair direction.

## V369 code in separate NON-SIGNED branch
- `Zero3ContactValenceV369` starts strictly neutral for all 3 optical frequencies and 8 azimuths. At material contact only, records the authentic PRE-collision `SensorimotorMind.Senses.spectral8(...)` photons and actual clamped reservoir delta, never `object.signature`, `kind`, world coordinates, object IDs, goal vectors, or color class. Brightest wavelength must beat other visible bands by 1.15x to assign evidence; ambiguous and invisible contacts are not falsely assigned.
- Learns physically-grounded signed valence per wavelength only after >=2 unambiguous TRAIN-world contacts; heldout contacts do not train. V353's existing action chooser then adds small action-direction utility calculated from learned valence * visible photon bearing to its action-conditioned energy/damage/cost score. This is NOT added to physical metabolism or falsified as an actual energy reward.
- Allows broad unbiased exploration around GRU-generated actor candidates when physically learned wavelength values are visible; GRU remains sole issuer and Gaussian actor training accounts for actual exploratory width. NO hardwired west/east, red/blue steering, world cheating, external thrust, new neural brain or scripted oscillation.
- Existing `Zero3CognitionV353.encode()` appends optional V369 learned-contact memory, while `decode()` accepts original V353 checkpoints ending before that extension. GRU/V356 outer checkpoint, executive V367 options, V319 grip, 75:25 motor peripheral, saved profile slots, game body, world & UI unchanged.
- Export new `zero3Summary.contactValenceEvidenceV369`, `contactValenceMeansV369`, `contactValenceAcceptedV369`, `contactValenceAmbiguousV369` from the EXACT saved snapshot for observable future verification.
- Test `Zero369LearnedContactValenceAndroidTest`: newborn neutral; raw-photon association only; mirrored resource bearings; ambiguous contact no invented label; heldout/no training; byte-compatible V353 migration+roundtrip. CI workflow `zero369-contact-valence-android.yml` uses actual Android app.

## Remaining blockers
1. Real Android compile/QA PASS is needed, then blind unscripted 2000/10000 experience QA in original world with new random seeds and matched baseline; blue pickups/hazard avoidance per optical exposure rather than aggregate counts alone.
2. Broad-exploration action proposals need controlled learning and right/left empirical metrics; moving in four scripted QA headings does NOT prove natural motor learning.
3. Limited source visibility (210U), optical ambiguity, and 75:25 motor authority remain risks. Do not claim all 199 prior red incidents have been retroactively labeled or use a reset.
4. An OTA must only be published when user explicitly wants a test copy AND it is signed with old identity, monotonic version >1183, truthful test caveats. Never silently advance current public experimental manifest.

## Provenance: read before changes
Master blueprint / implementation contract / verified capability ledger apply as before. Do not revive second 2.2 policy, erase 16 option memories, override sensor evidence, or force survival actions. 

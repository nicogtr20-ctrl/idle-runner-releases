# ZERO472 — V471 complete original Android results and V371 heldout forward-scale ablation
Date: 2026-11-02. Branch: `lab/zero472-v371-heldout-forward-scale-correction` from exact V471 `02bad5018e82834cc09b0aee6ce1f1ecc6aa4eb5`. **EXPERIMENT ONLY — NOT APPROVED.** No APK, OTA, merge, trained-brain or user slot modifications.

## Required sources and history checked
Read `AGENTS.md`, `docs/ZERO419...` through `docs/ZERO423...`, `docs/ZERO471...` and actual GitHub Actions job logs. V421/V422/V423 technical regression and original CRC jobs all PASS; all six original >=10k behavioral jobs FAIL. Their exact original 1k S21 and wild 1k contact windows are recorded in ZERO471 and independently rechecked.

|Variant|S21 real decisions; signed/gross; qualifying 1k; inverse; blue|Wild real decisions; red/blue; red first/last 5k|Regression / CRC|
|---|---|---|---|
|V421|10001; 17.1723%; 8/10; windows 4,9; 4|10002; 87/3; 35/52|PASS / PASS|
|V422|10003; 11.2881%; 7/10; windows 4,9; 4|10003; 52/5; 19/33|PASS / PASS|
|V423|10002; 10.0486%; 7/10; windows 4,9; 4|10003; 26/4; 14/12|PASS / PASS|

## NEW actual V471 original Android results (all four Actions completed)
- [S21 41415406438](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41415406438) **FAIL**. Saved original 33,312-decision S21, 10,001 new self-generated decisions, signed **532.84922**, gross **3506.93284**, signed/gross **15.1942%**; 7/10 windows >=10%, inverse windows 4 and 9, 4 newly collected blue, 3,408 local weight updates, 4 genuine death/rebirth events. Per-1000 signed/gross: `+.140269, +.101769, +.412761, -.027659, +.334222, +.341721, +.175635, +.254383, -.128617, +.091777`. The final window also fails the 10% local threshold.
- [Wild 41415406411](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41415406411) **FAIL BEHAVIORALLY, NOT AT BOOT**. The original sole GRU3 successfully initialized (owner=true, originalBoot=0, no logged brain error), completed **10,001** real decisions. Signed **185.54399**, gross **3568.62454**, efficiency **5.1993%**, 3 blue, **30 red** (10× blue); first 5000 **13 red**, last 5000 **17 red**, hence harm increased. 5,242 local weight updates, 1,752 harm-rejected local credits. Per-1000 red: `0,0,0,9,4,0,8,9,0,0`; per-1000 blue: `3,0,0,0,0,0,0,0,0,0`; signed/gross per-1000: `+.033122,-.045663,+.053487,+.037501,-.052441,+.086737,-.062535,-.019771,+.218078,+.292399`. This supersedes V470's censored three failed wild starts; V471's boot diagnostic did not itself change the brain.
- [Original Android regressions 41415406506](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41415406506) **PASS**.
- [Original S21 V356 CRC / 512 weights 41415406489](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41415406489) **PASS**.

## Causal source-level issue isolated for V472
In `Zero3InstrumentalCreditV371.motorVerified(int bank)` the original requirement `baseline[bank] > 1.0` compares a **squared displacement** against an absolute floor that is larger than every bank's independently observed zero-motion baseline in V471. S21 original eight V371 baseline values (millisquared units) `77,50,421,581,14,174,335,48`; wild `146,163,137,59,23,32,68,113`. This makes **all eight V371 directions fail even when independent heldout error improves over baseline**. In contrast, V377 already uses measured heldout forward authority (V446) instead of a hard squared-error floor. Both V471 runs explicitly reported `verifiedV371Directions=0`.

**One change only:** replace the unjustified squared-displacement floor in V371 with independently measured heldout improvement `error < .90 * baseline` and **actual positive projected forward displacement >0.10** with >=4 TRAIN and >=3 HELDOUT samples. No innate compass, forced escape, fake reward, changed body, GRU topology, actor output, learned weights, serialized V371/V377 format, checkpoint or resource count. This permits evidence-backed optical motor direction **only where V433's existing pre-inertia guard allows it**; V377 heldout prediction remains authoritative under moving drift. It is NOT proof of causal muscle authority in every posture: V371's raw displacement can include coast, so independent original physical >=10k A/B behavior is essential and the experiment must be rejected if it worsens either test.

Added Android regression `Zero472V371HeldoutForwardScaleAndroidTest` (3 unit-level mathematical gate checks: small heldout forward, negative forward, contradictory heldout). The test fixtures are NOT survival results or rewards. Existing original regression suite, saved S21 CRC, 2×10k unchanged original Android workflows are required on the same SHA.

## Hard acceptance unchanged
S21 >=10k, >=25% signed/gross, >=9/10 local windows >=10%, zero inverse windows, >=5 newly picked blue. Wild >=10k original decisions, >=12% signed/gross, actual blue, last-5000 red < first-5000 red, total red <=2× blue. Android regression and original checkpoint CRC must PASS on same code. **No APK/OTA/merge until all pass.** Preserve S21 33,312 decisions, 512 synapses, V356 CRC, V319 CH2, 194 senses, original 170-material body, all 12 user slots. No V2.2 fallback.

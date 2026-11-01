# ZERO461 — original V319 CH2 random-grip confound, exact GRU3 170-material A/B

2026-11-01. Experimental research branch `lab/zero461-v319-grip-exploration-causal-ablation` based on V457 (known full source matched S21/Wild baseline). **Not approved. No new APK/OTA/merge or user saves changed.**

## Verified baseline
V457 S21 saved brain 33,312 plus original 10,004 decisions: signed/gross 18.0229%, 8/10 1k windows >=10%, one inverse, four blue. Wild 10,002 decisions: 33.7226%, 50 real red / five blue, red first 5k=37 last=13. Original regression and 33,312 S21 V356 CRC both PASS. V458 trust relaxed V371: S21 14.7629%, 2 inverse, four blue; wild 70 red/5 blue. Subsequent V459/V460 PRE-coast/low-momentum motor map altered S21 to only ~10.33%, wild unchanged ~50 red; therefore ZERO461 starts from V457, not failed V460. Latest V460 Wild rerun failed to finish brain initialization, NOT a valid wild 10k; no false claims.

## Distinct concrete physical confound
`Zero3OriginalBrainV356.actuate` executes the one actual 64-muscle cortex, then `Zero3ContextAdhesionV319.actuate` rewrites CH2, 16 sector commands, before the 170-site original material solver. In V319 `begin`, when `training=true`, a **shared random coarse logit up to +-1.35** plus **independent sector random logit up to +-1.45** is injected into all 16 CH2 grip channels **every neural motor decision**, including independent heldout. CH2 adhesion grip materially changes traction, steering, friction and contact. But V390->V421->V423 locally credits a single different CH0/CH1/CH3 muscle using actual whole-body COM acceleration; V371/V377 map the GRU3 direction to body movement while this other random physical control varies. Real physical interventional confounding is plausible; not proven until new full original A/B.

## ONE behavioral variable
Set V319 transient lab-only flag `enableDeterministicGripPhysicalEvidenceLabV461` at real original V356 boundary before experimental tests. Keep original learned sigmoid V319 head, actual CH2 adhesion forces, tactile/grip contact, real adhesion work/cost accounting and updating V319 synapses. Consume original V319 random draws *identically* (same 1 coarse and 16 sector RNG samples), but do not ADD these random perturbations to each CH2 efference. Other 48 actuators, entire GRU3 sampling, all user state/saves/194 sensors, every body physics step, rewards, negative energy contacts, 512 motor coefficients, V319 decision timing remain original. Default normal application remains V319 unmodified.

This is NOT physics replay, scripted heading, hardcoded brake, second brain or artificial negative/reward. It also changes V319 grip learning distribution and may remove beneficial exploration; if behavior worsens, reject V461. A clean original V457 A/B with matched source and original 2x10k decides.

## Hard test gates
- `Zero461V319OriginalGripExplorationAblationAndroidTest`: original random CH2 default intact; V461 CH2 perturb zero for TRAIN/HELDOUT while original RNG sequence and V319 learned weights remain bit identical; cannot switch mid action; frozen grip unchanged.
- Same original saved 33,312 S21 -> >=10k new truly autonomous Android physics decisions; signed/gross >=25%, at least 9/10 1000-decision windows >=10%, ZERO reverse, >=5 new blue. Preserve 12 slots and exact V356/512 cortical weights before any NEW experience.
- Same true fresh wild >=10k GRU3 decisions, signed/gross >=12%, real blue and red contact, later 5000 red < earlier 5000, total red <=2*blue.
- Entire original Android regression suite and exact user saved S21 CRC PASS. Same source SHA all four. Inspect S21 wall contact, local updates, real wild red 10 bins, V371/V377 verified banks, V461 suppressed V319 perturb counter. No unstable APK, no OTA/merge until all strict gates PASS.

Interpretation: a PASS in the 3 controlled V319 tests only proves mechanical isolation. A FAIL in either 10k prohibits approval even if another metric improves. Other causes such as active actuator Jacobian and nonlinear material memory remain open.

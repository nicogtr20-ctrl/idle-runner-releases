# ZERO409 — Correct original physical pickup/harm contact counting at physiological energy cap

**28 October 2026, experimental peripheral measurement fix.** Branch `fix/zero409-physical-contact-ledger-at-full-energy`; no signed APK, OTA, new cognitive rule or fabricated physical reward. All future behavioral acceptance **still requires >=10,000 consecutive genuine GRU3 decisions**; prior 2000-decision tests are only smoke/forensic.

## Confirmed actual source defect

`GameActivity.Lab.resolveObjects` had correctly invoked `UnknownWorld.Entity.touch()` on actually overlapping 170-body material and physically applied its returned -7 red/+22 blue/+rare source effect after reserve [0,100] clamp. It then fed `EnergyLifetimeCounters.record(reserve-before,...)`: **wrong input for a physical material CONTACT count**.

1. Real blue energy pellet touched when `reserve=100`: `touch()` returned +22 and consumed the source, `energyEaten++`, but `reserve-before=0`, so the cumulative BLUE pickup count was zero. The original 10,000 QA would falsely claim ZERO collected zero blue sources.
2. Same-frame true red collisions when `reserve` had already reached zero after another red: the second actual `touch()` returned -7 and physically set the red's cooldown, but `reserve-before=0`, so the RED material contact was silently dropped. This is particularly dangerous for 10,000-decision hazard avoidance metrics, whose red rate would be understated exactly in the worst situations.
3. The original rare violet chamber pickup at full reserve was also omitted from violet HUD count even after a true physical source touch.

**Fix:** `EnergyLifetimeCounters.recordPhysicalContact(actualTouchLawDelta,rare)` takes only the signed return of an ACTUALLY PERFORMED `UnknownWorld.Entity.touch()` (or the actual physically reached chamber `UnknownWorld.RARE_ENERGY`). `GameActivity` replaces only the HUD/QA counter call. `LifeStatistics.contact(reserve-before)`, `brain3.realMaterialContactV374(...,reserve-before)`, `mortality.energyFlows`, `reserve`, true world physics, speed slowdown on red, original 194 sensory inputs, neural choice, 512 cortical weights, V319 and every named save remain strictly unchanged. A saturated blue touch has NO fictitious biological energy advantage: its contact **count** increases by one because the gem really was consumed.

`elt1` persisted wire format stays exactly unchanged with cumulative nonnegative blue/red/violet counters. Prior version events cannot be reconstructed retrospectively; do not claim the user was given the uncounted old blue gems back.

## Tests

[ZERO409 physical touch Android regression](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero409-original-physical-gem-touch-ledger-android.yml): three real `UnknownWorld.Entity.touch()` cases, true source consumption at max energy, two red materials at physiological zero and violet at max energy. Also all original GRU3, proprioception/tactile, old cold-resume and V406 81-real-life cumulative count regressions.

**Behavioral approval is NOT gained from unit pass.** In addition, [original 10,000 wild test](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero406-original-gru3-10000-autonomous-gate.yml) and [trained actual user S21 10,000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero408-actual-user-s21-saved-gru3-10000.yml) remain required; V406/V408 old source and V409 raw-count semantics must not be mixed when interpreting blue/red rates. Any experiment must use matched source revision within its entire 10k run.

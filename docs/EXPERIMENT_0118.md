# OVERMIND ZERO — 0118 / genuine terminal experience

**Scope:** ONLY learning from the end of a real lived life. Never train ZERO that a gate, box, color, direction or last command necessarily caused its death. Preserve its learned world/motor/adhesion/decision memory, all 12 slots, existing 26 sensors and 16 actions.

## Observations and learning
- The physical world keeps mortality at energy <= 0, as already designed. This is the observed terminal outcome, not a rule in the new predictor.
- At every **completed real sensorimotor decision** (~0.65 simulated seconds), take the actual normalized sensed energy and change since the previous observation. Keep a rolling sequence of 17 observations. The model receives NO obstacle type, contact identity, room progress, hand/grip command, body direction or selected action.
- As time passes beyond 16 decisions, older observations become **verified lived nonterminal experiences**. An actual death records a **terminal event** and retrospectively marks only the last bounded observed sequence with a decreasing positive recency weight. The terminal event is not a negative reinforcement sent to the action values of the final grip/motor command.
- Learn a tiny logistic probabilistic terminal predictor from energy and energy change, initially neutral. Hold out **whole lives** (every fifth life), not neighboring actions. No inference or action-score contribution until at least 5 untouched lives, 30 evaluated terminal examples, 100 evaluated survivors, sufficient trained samples, balanced held-out Brier improvement and a stronger empirically learned predicted terminal risk for low vs high observed energy.
- Only then an additional maximum +/-0.18 per-action value can modestly modulate **already independently learned source-matched energetic outcomes**, never mechanically earned obstacle displacement, never geometry, never a negative last-action/death punishment. Old spontaneous exploration and all motor/adhesion controllers remain in charge.
- Physical observers additionally keep **separate decaying six-second traces** of passive metabolism, harmful direct contacts and energy gains. They appear as observational diagnostic data; the mortality predictor does NOT receive any semantic causes or object IDs. A negative energy consequence at a red object already trains the existing independent energy-source pathway. Moving a box/gate in the same interval cannot take ownership of that energy loss.
- If the data remain insufficient, confidence 0 = policy contribution exactly 0. This is NOT symbolic understanding of death nor proof of true causality.

## Lifecycle, compatibility and invariants
- Versioned additive key `mortality_learning_v118` in the existing working SharedPreferences, captured automatically by named game slots and explicit manual GPT telemetry (NOT uploaded in the background). Existing saves simply start without this model, retaining all other learning. Atomic decoder rejects corruption without modifying prior weights.
- Physical death calls terminal learning **before** `resetEpisode()`, which clears only current-life observations; terminal learned coefficients and holdout evidence survive respawn. App suspension and load of the **same** physical life do not count as death or create synthetic supervised events.
- WORLD selective reset includes only this NEW predictor in addition to existing WORLD keys. Other selective resets do not erase it.
- No change to the physics equation for energy depletion, contacts, rewards, named source-signature attribution, gate, box, animation or gameplay timing. No automatic avoidance rule and no new rewards.
- Dashboard exposes deaths, trained/holdout terminal and surviving examples, risk estimate at two energies, actual recent passive/contact energy, confidence and advice evaluations.

## QA acceptance
1. Virgin predictor produces no active bias.
2. Realistic alternating lived energy trajectories produce training and **whole-life** holdout samples, with an empirically learned risk difference.
3. Incidental harmful contact earlier in a life is distinct from a contact temporally close to death; both are observer-only, not action labels.
4. A changed gate/box physical situation is not an input to the terminal predictor. Existing independent energy/physical source regression and all three-box/gate tests must pass.
5. Death preserves long-term learned model; corrupted serial is rejected; Android debug compiles and previous neural tests pass.
6. Sign and publish only after full staging and stable publisher pass; compare true new report against pre-0118 baseline over multiple lives, not a single successful avoidance.

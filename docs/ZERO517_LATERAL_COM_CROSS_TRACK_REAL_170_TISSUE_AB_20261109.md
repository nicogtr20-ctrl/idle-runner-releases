# ZERO517 — measured lateral COM cross-track tendon servo (QA only)

Failures on exact original 10000+ decisions:
- V515 S21: 10,004 decisions after 33,312, signed/gross −15.467%, blue 11, no additional red; 8,541 verified-impasse candidate inspections, ZERO relief, 48,319 executive candidate vetoes. Doubling from 7 to 16 directions did not fix learned-body steering.
- V515 wild: 10,001 decisions, signed/gross −5.663%, blue 11, red 2; 239 relief candidates, 211 negative-wavelength safety vetoes, only 7 actual physical redirections.
- V516 identical original brain + corrected QA Android activity lifecycle: 10,003 decisions, −5.651%, blue 11, red 2; startup fix succeeded but locomotion remained negative.

V515's 16 candidates are NOT included here: that unapproved change also degraded the wild V511 10k baseline (+13.696%). Start from V514 original motor/actor, no change to GRU3, cortex, learned red safety, 12 slots, or release.

V499 physical motor currently steers exactly along neural heading while measured body drift is allowed to continue; earlier high-gain full-vector velocity-error feedback caused oscillatory deformation. V517 tests only bounded orthogonal COM-velocity correction, at four lateral feedback gains, each direction limited to ±0.35 radians of physical tendon-frame correction, same real 96-point tangent and 170-site material. Never modifies neural intent or supplies a target. Original V508 motor mode14 is the comparator, with 256 actual recorded user GRU decisions, same body state, same replay and full physical solver.

The 256 replay's directional tracking must beat baseline by at least +2 percentage points even to be *considered* for future 2×10k independent wild+S21 autonomous tests; physical A/B alone does not authorize APK/OTA.

No production feature enabled. No saves are overwritten.

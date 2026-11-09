# ZERO520: fix V384 current wall-touch blind spot (laboratory only)

V519 real S21 Android test 42722888971 confirmed 256 recorded neural intentions through real 94 training wall CCD reduce original GRU V499 open-loop directional tracking from 62.934% to 40.454%. This is NOT 10000-decision autonomy certification.

V515 original S21 10004 decisions: -15.467% real locomotion, 20k+ wall contact frames per 1000 decisions, while only 10 of 8541 candidate evaluations were detected as selected-bank physically blocked. The stored S21 sidecar contains ONLY 3 verified failed side+direction cells of 64 (train and heldout); full membrane snapshot shows sides 3/4/5 currently touching real walls, side 4 contact=1.

V384 learnedCost requires a matching historical confirmed side+heading pair; unseen but currently wall-facing neural proposals therefore receive ZERO cost. Increasing to 16 sampled headings V515 did not fix that blind spot and degraded wild baseline.

V520 adds a LAB-ONLY physical contact choice gate using actually observed 96 membrane tactile points per directional sector. Only after independent train + heldout V384 impasse, if the chosen GRU direction faces current pressure >=0.23, consider ONLY other already sampled GRU headings with at least 0.20 less measured frontal pressure. Reject all that increase independent physically learned harmful wavelength risk by >0.012. Keep V511, original seven actor proposals, existing planner, executive, original original-body tendon solver and local reward. Planner and executive may not reintroduce a more pressure-loaded direction once a safer sampled direction exists. Physically re-ranked action cannot corrupt actor heading credit. Zero change to source of action, saved state, 194 senses, GRU64, cortex512, 12 named slots, true impacts or world physics.

Standalone genuine saved S21 Android geometry test is only necessary scientific safety validation, NOT autonomous competence. Mandatory genuine separate wild + trained S21 >=10000 physical decisions and no danger-regression before OTA.

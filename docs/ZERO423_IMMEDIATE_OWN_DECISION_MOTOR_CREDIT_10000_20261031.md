# ZERO423 — fix same-decision eligible original motor learning (research only)
31 October 2026. Branch `lab/zero423-immediate-own-decision-motor-credit`. **No APK, no OTA, no stable merge, no user saved brain edits.**

## Source-confirmed zero learning loss despite real eligible movement
In original V417 `Zero3OriginalBrainV356.observed()`, one truly physically eligible action captures `probeV400` and its physically signed measured COM velocity change but is *NOT* trained yet. It waits for the entire following, separately chosen GRU3 action to be both fully executed and eligible. If THAT next action touches the front wall, a real red resource, the room, or is heldout/terminal, original V417 unconditionally discards the earlier successful single-muscle probe. This is not just V417's cross-decision causal confusion: it also discards **independently valid eligible motor samples** in a room with 70%+ interrupted/blocked motor windows. Original V418 zeroed cross-decision delayed acceleration but kept the `if(currentEligible){train(prior)} else {discard(prior)}` dependency, so its failure did NOT test immediate own-action credit.

## ONE isolated laboratory intervention
- The original existing 512-synapse 16×4 cortex and 194D/64GRU sole actor remain unchanged.
- `enableImmediateOwnDecisionCreditLabV423` is a nonpersisted opt-in after original V400 request and V417 true observed COM acceleration at a real decision boundary.
- At an original V401/V414 **strictly physically eligible** closed decision, use exactly that completed decision's `probeV400` and its own physically measured `localResponseV400` from V417, pass an exactly ZERO cross-action delayed tail into the EXISTING `cortex.learnV332`, increment local credit/update counters and do **not** retain that probe for a separate future action. The next entirely independent decision's contact outcome cannot erase this finished action's credit.
- Keep actual GRU high-level physical contact valence, harm, reward, death/terminal, original V319 grip, V412 inertia/proprioception, CH0/1/3 physical actuator, original real 170-mass solver and untouched 12-slot V356 CRC layout. No authored motor heading, rescue force, scripted target or new neural weights.
- V418/V420/V421/V422 alternative experiments **explicitly OFF** in both matched full original behavioral 10k scripts. They remain in source but their opt-in flags are false. V417 own measured COM response/strict original eligibility ON as required.

## Mandatory original Android acceptance
Same **original 33,312 trained user S21 / true 170 material training-room** 10k real decisions with 10 independent thousand-decision windows: >=25% signed/gross, >=9/10 >=.1 window signed/gross, ZERO negative windows, >=5 new actual blue pickups, true original single GRU3 body/saved state unchanged. Same truly newborn **wild GRU3** >=10k autonomous decisions with real red/blue physical material touches: >=12% signed/gross, real blue pickups, red latter 5k < first 5k, total red <= 2 × blue. All original Android regressions and exact user S21 V356 CRC/512 weights preserved.
- [True S21 10k](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685)
- [True wild red/blue 10k](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734)
- [Original regression including V420/V421/V422 isolation tests](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431)
- [Actual saved S21 checkpoint CRC](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279)

User phone V388 stable/12 saved slots untouched. No claims of successful gait until both 10k jobs genuinely PASS the precommitted gates, and no release unless all 4 complete PASS.

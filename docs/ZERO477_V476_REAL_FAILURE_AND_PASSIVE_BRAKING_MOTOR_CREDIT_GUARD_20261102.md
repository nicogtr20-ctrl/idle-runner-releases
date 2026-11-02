# ZERO477 — V476 2×10k original Android failure and passive viscous braking motor-credit confound
Date: 2026-11-02. **LAB ONLY** on `lab/zero477-mid-drift-passive-braking-confound-gate`, exact base `935af2e6fd4b53dbf38e0314f8d77dd46a80e16d`. Read AGENTS.md, docs/AGENTS.md, ZERO419–423, V475 and V476 dossiers. NO APK, OTA, stable merge, checkpoint migration or 2.2 fallback.

## V476 actual GitHub Actions, exact same SHA
- S21 [41453312514](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41453312514) **FAIL**: original 33,312-trained brain to 43,315 = **10,003** decisions; signed 603.73264 / gross 3309.15852 = **18.2443%**, 8/10 windows >=10%, opposite windows **4 and 9**, **4 new blue**. 2,965 original local muscle updates, 7,038 censored decisions, rear wall accepted 2,080, front wall excluded 2,768. V377 verified banks 6, V371 verified directions 2, mid transient TRAIN 1,605 / HELDOUT 358 / reliable 1. Motor candidate evaluations 311,759; unsupported drift 200,429, optical drift suppressed 194,040, fallback score reconstructed 4,469.
- Wild [41453312509](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41453312509) **FAIL**: original newborn brain **10,002** decisions, signed 357.47647 / gross 3764.24315 = **9.4966%** (below 12%), **3 blue / 65 red**, red **19 in first 5,000 → 46 in last 5,000** (increasing, 21.67 red per blue). 5,724 real local muscle updates, 1,634 local harm rejections; V377 verified banks 7, V371 directions 2, mid TRAIN 4,003 / HELDOUT 975 / reliable 2; 406,978 motor candidate evaluations, 285,279 unsupported drift, 284,985 optical suppressions, fallback score reconstructed 1,668. 6,633 learned-harm candidate expansions (offered=5), yet safety still failed.
- Original full Android regressions [41453312526](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41453312526) **PASS**; original S21 512 weights/V356 CRC [41453312513](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41453312513) **PASS**. All four share SHA `935af2e6fd4b53dbf38e0314f8d77dd46a80e16d`. PASS technical ≠ PASS behavioral.

## Every original S21 1000-decision window (V476)
| Window | Signed | Gross | signed/gross | New blue |
|---|---:|---:|---:|---:|
|1|49.98557|244.89867|20.4107%|0|
|2|68.68298|532.27411|12.9037%|1|
|3|116.21881|287.64584|40.4034%|0|
|4|-25.03584|362.20808|-6.9120%|1|
|5|121.53998|311.23674|39.0507%|0|
|6|31.42261|152.01344|20.6709%|0|
|7|74.21469|518.03768|14.3261%|1|
|8|77.96365|239.32601|32.5763%|0|
|9|-35.96229|364.24532|-9.8731%|1|
|10|124.70248|297.27263|41.9489%|0|

## Every original wild 1000-decision window (V476)
| Window | Signed | Gross | signed/gross | Red | Blue |
|---|---:|---:|---:|---:|---:|
|1|12.50780|379.11517|3.2992%|0|3|
|2|-12.86176|284.12161|-4.5269%|0|0|
|3|20.00250|373.96877|5.3487%|0|0|
|4|29.24644|402.03108|7.2747%|9|0|
|5|32.25751|416.32580|7.7481%|10|0|
|6|44.80866|382.94425|11.7011%|10|0|
|7|60.34838|365.81022|16.4972%|9|0|
|8|55.14067|388.28283|14.2012%|15|0|
|9|56.81378|399.20282|14.2318%|10|0|
|10|59.21249|372.44060|15.8985%|2|0|

## V421–V423 historical original Android cross-check (12 requested run IDs)
|Version|S21 / wild decisions|S21 signed/gross; good/inverse windows; new blue|Wild signed/gross; red first→last; total red/blue|Regressions|CRC|
|---|---|---|---|---|---|
|V421|10,001 / 10,002|17.1723%; 8/10, 2 inverse; 4 blue|61.6262%; 35→52; 87/3|PASS 41149404217|PASS 41149399737|
|V422|10,003 / 10,003|11.2881%; 7/10, 2 inverse; 4 blue|42.7053%; 25→27; 52/5|PASS 41149771615|PASS 41149767313|
|V423|10,002 / 10,003|10.0486%; 7/10, 2 inverse; 4 blue|31.0766%; 14→12; 26/4|PASS 41149995431|PASS 41149992279|

All six behavior runs FAIL: V421 S21 [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000), wild [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560); V422 S21 [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090), wild [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948); V423 S21 [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685), wild [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734). **Methodological caveat:** the four historical runs for each of V421–V423 do NOT share the same GitHub head SHA; never treat their four independent PASS/FAIL as one same-commit approval bundle. V423 wild was attempt 2. V476 is a genuine same-SHA set.

## Source-confirmed causal failure mode and one V477 correction
`Zero3InertialResponseV377.finish` uses `response = measuredTravelNormalizedTo0.30s - 0.30*PRE_COM_velocity`. The actual original `SymbioteMatterV1` applies passive viscous drag (e.g. `exp(-2.3*dt)` for fluid and `exp(-(1.8+66*traction)*dt)` for skin, plus contacts). Therefore a body already moving EAST can slow down with **zero WEST muscle effect**, producing a negative X residual. The V476 mid-speed bank groups this as WEST "propulsion"; four TRAIN and three independent HELDOUT repeats can still validate the wrong causal explanation because passive braking is reproducible. Concrete counterexample: PRE vx=+1 U/s, WEST issued, POST +0.15 U over 0.30 s, residual -0.15 U = apparent +0.15 WEST motor authority. Old V476 synthetic passive test used only ideal un-damped +0.30 U and missed this case.

**Single V477 behavioral difference:** for the transient 0.35<=PRE speed<2 V476 mid banks only, reject from both TRAIN and HELDOUT motor authority samples with PRE velocity projection opposite the commanded heading. This prevents pure passive viscous braking from masquerading as a positive learned counter-drift motor response. It is deliberately conservative: genuine braking/counter-drift motor actions also lack certification until a separately measured passive baseline exists. It does not command, force or reward any heading; original 16 persistent V377 low/high banks and their checkpoint schema remain unchanged. New ephemeral counter `v477MidOpposingPreRejected` reports actual rejection frequency. Non-opposed mid evidence still requires original >=4 TRAIN, >=3 HELDOUT, positive forward >.10, heldout loss improvement. This is a **falsifiable conservative eligibility repair**, not proof of locomotion.

New Android regression extends `Zero476V377TransientMidDriftAndroidTest` from 4 to 6 tests, including zero-muscle passive drag false authority and genuine aligned propulsion. The four original workflows on the **same final SHA** must complete: restored S21 >=10,000 real actions, wild >=10,000 real actions, full Android regressions, original V356/512 CRC. Strict unchanged gate: S21 >=25% signed/gross, >=9/10 windows >=10%, zero inverse, >=5 new blue; wild >=12%, red last 5k < first 5k and red total <=2× blue. Even with technical PASS, any behavior FAIL rejects V477.

## Preservation / release
Original S21 33,312 training decisions, 512 motor weights, 194 input sensors, 64-unit GRU3, CH2 V319, 170-material original body, V356 CRC, twelve save slots, original negative contact learning and world physics are not changed. No V2.2 return, authored heading, invented reward, APK, OTA or merge. **V477 is unapproved until all four actual original Android runs are verified on the same SHA.**

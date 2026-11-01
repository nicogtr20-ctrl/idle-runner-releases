# ZERO445 complete original Android forensics and ZERO446 isolated physical trust-gate hypothesis
2026-11-01. Source comparator: `lab/zero445-v377-bank-forward-heldout-forensic`, commit `72be940daf3a17f382b0b2309e737e530299fa73`. New experiment: `lab/zero446-heldout-forward-authority-gate`. RESEARCH ONLY; no APK/OTA/stable merge.

## Mandatory original state / provenance
Read `AGENTS.md` and `ZERO419` through `ZERO423` before editing. V445 original S21 used the actual 33,312-decision V356 checkpoint, 512 existing local cortex weights, CH2 V319, 194D/64-GRU3 senses, original 170-material solver and twelve saved slots, all untouched. No V2.2, fixed heading, fabricated valence or reward. Both original 10k behavioral Actions **FAIL**, while original regressions and CRC **PASS** on the same commit.

- [V445 S21 41277012207](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41277012207) — FAIL, **10,004 new decisions** (43,316 total), signed **307.47355**, gross **2758.99193**, signed/gross **11.1444%**, four new blue, 6/10 windows >=10%, **two inverse** windows (4,9), 2,754 real local updates. Original S21 cumulative 90 red are historical; zero new S21 red.
- [V445 wild 41277012302](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41277012302) — FAIL, **10,006** genuine new decisions, signed **52.06141**, gross **3667.51521**, signed/gross **1.4195%**, four blue, **49 red**; red first 5000 **29**, last 5000 **20** (5.8→4.0 contacts/1000 decisions); total red/blue **12.25** (must <=2). 5,450 real local updates, 1,675 local harmful samples rejected. All four blue by decision 3003; none in final 7000.
- [V445 regressions 41277012186](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41277012186) — PASS.
- [V445 original V356 CRC 41277012109](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41277012109) — PASS.

### Ten original 1000-decision windows (signed/gross, new blue, new red)
| Window | S21 efficiency | S21 blue | Wild efficiency | Wild blue | Wild red |
|---|---:|---:|---:|---:|---:|
| 1 | 22.9782% | 0 | 13.9158% | 3 | 0 |
| 2 | 1.1384% | 1 | 28.7984% | 0 | 4 |
| 3 | 24.6798% | 0 | 23.5648% | 1 | 15 |
| 4 | -19.6887% | 1 | 10.1142% | 0 | 10 |
| 5 | 30.6632% | 0 | 8.6119% | 0 | 0 |
| 6 | 28.7708% | 0 | -6.5806% | 0 | 0 |
| 7 | 8.9133% | 1 | -29.5247% | 0 | 11 |
| 8 | 30.3029% | 0 | -29.8824% | 0 | 9 |
| 9 | -3.6944% | 1 | -17.6865% | 0 | 0 |
| 10 | 14.7033% | 0 | -2.2331% | 0 | 0 |

Wild physical harm events: 49/49 real `ZERO404_HAZARD` contacts, **45/49** pre-contact momentum toward red >0, 12/49 current neural intention toward red >0.3. The body can continue into danger even while commanding away. This does not prove motor intent alone is defective.

### V445 V377 genuine TRAIN/HELDOUT per-bank evidence
Tuple: `bank:TRAIN/HELDOUT:forwardMilli:heldoutLossMilli:coastLossMilli`; banks 0–7 are PRE-speed <2, 8–15 >=2; forward means measured action-conditioned response along the actual issued heading, NOT a programmed movement. Losses are squared material units.

Wild:
`0:387/68:115:37:58,1:435/126:191:38:35,2:634/136:122:24:38,3:964/255:47:9:8,4:1205/242:56:12:16,5:730/200:42:7:8,6:357/114:30:10:12,7:300/79:17:11:16,8:18/8:-132:8:13,9:12/10:-59:20:11,10:24/4:-32:38:22,11:48/13:77:13:56,12:67/9:260:9:103,13:52/7:214:11:79,14:14/4:26:45:55,15:13/4:-74:8:64`.

S21:
`0:372/54:85:118:116,1:285/71:104:63:60,2:145/168:140:120:96,3:289/74:26:83:121,4:3787/579:2:3:3,5:4008/865:29:6:6,6:663/439:54:11:9,7:533/262:118:4:18,8:438/87:248:170:186,9:407/125:233:130:211,10:225/247:163:158:136,11:555/115:66:151:174,12:3855/692:-34:7:10,13:4886/1032:21:15:16,14:611/346:161:95:113,15:566/260:210:102:136`.

V445 wild 5,260 newly accepted TRAIN, 1,279 HELDOUT; all 16 banks have >=4/3 samples; 12/16 heldout-loss improvements, but **only 1/16** passes original V377 trust gate; 0/8 slow banks trusted. S21 2,173 newly accepted TRAIN, 559 HELDOUT; 5/16 trusted. In wild, 861/414,027 candidate evaluations used validated V377 prediction; 323,649 had unsupported inertial drift. 0 delayed real motor links, despite many genuine local muscle updates. Source: `Zero3InertialResponseV377.reliable` and `reliableSteeringDirectionsV383` require `coastLoss>0.10` squared units, a fixed *prediction error magnitude* floor. Wild low-speed banks 0 and 2 have real forward responses 0.115 and 0.122, 68/136 heldout samples, and independently beat coasting (loss .037 vs .058, .024 vs .038); nevertheless both are rejected **solely because their coast errors are smaller than .10**. This is a demonstrated gate mismatch, not proof the model will navigate well.

### Historic exact Actions V421–V423 (all inspected, including full behavior logs)
| Experiment | S21 action / result | Wild action / result | Regressions | CRC |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL: 10001 new, 17.1723%, 4 blue, 8/10 good, 2 inverse | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL: 10002, 61.6262%, 3 blue, 87 red (35→52) | [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS | [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS |
| V422 | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL: 10003, 11.2881%, 4 blue, 7/10 good, 2 inverse | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL: 10003, 42.7053%, 5 blue, 52 red (19→33) | [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS | [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS |
| V423 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL: 10002, 10.0486%, 4 blue, 7/10 good, 2 inverse | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL: 10003, 31.0766%, 4 blue, 26 red (14→12) | [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS | [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS |

## V446 one-variable causal experiment
The V377 trust gate currently treats *large residual error variance* as proof of steering authority, and refuses measured, statistically validated forward movement in slow-speed regimes. V446 replaces that absolute `coastLoss>0.10` floor with the **existing V383 physical forward-propulsion requirement `meanForward>0.10`** across all three V377 trust consumers (`reliable`, `reliableBanks`, `reliableSteeringDirectionsV383`), retaining >=4 real training, >=3 independent heldout and heldout prediction loss <90% of passive coast. A predictable brake (negative forward response) stays untrusted. The criterion is evaluated on actual measured material transitions; no added direction, resource label, reward, force, muscle parameter, 194D sensor, GRU weight or serialized checkpoint. Original V445 per-bank evidence predicts two low-speed wild headings may now be eligible; whether this improves or worsens behavior is unknown.

New Android `Zero446HeldoutForwardAuthorityAndroidTest` checks mathematically that low-coast but independently confirmed forward control becomes eligible, that negative forward braking is never mislabeled as steering, and that heldout disagreement blocks prediction. **These unit tests are not behavioral approval**. Mandatory 4 independent original Android workflows on a single V446 source commit: exact 33312 S21 >=10000 (>=25%, 9/10 >=10%, zero inverse, >=5 new blue), wild original >=10000 (>=12%, red latter 5000 < first, total red <=2× blue), regressions including V446 unit, and V356/512-weight CRC. Preserve original body, 12 saves, CH2 V319, no V2.2, no APK/OTA or stable merge if any fail.

## V446 results
PENDING original GitHub Actions. Record exact IDs, source SHA, ten windows, 5k red rates, bank trust and original regression/CRC only after actual completion. No approval presumed.

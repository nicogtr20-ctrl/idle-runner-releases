# ZERO437 — completed S21 original 10k FAIL; source audit overturns V437 candidate-0 premise
Date: 2026-10-31. **READ-ONLY FORENSIC, NO BEHAVIORAL PATCH** on `audit/zero437-s21-fresh-candidate0-source-contradiction-20261031`, branched from V437 commit `431ed5ac1d57f77a728365f822f479c0b60562c9`. Do not merge or release. AGENTS.md and ZERO419–ZERO423 reviewed before this audit. The user S21 33,312-decision checkpoint, V356 CRC, 512 weights, 194 sensors, V319 CH2, physical 170-material body, and 12 saves are untouched.

## Actual V437 CI status and completed original S21 Android proof
- [S21 original 41235121057](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41235121057), job `124637144917`, **FAIL after 10,003 real decisions**, from cursor 33,312 to 43,315. Original autonomous owner confirmed; signed **318.33499**, gross **3486.84455**, signed/gross **9.1296%**, vs hard >=25%; **5/10** 1k windows >=10%, vs >=9; **4 inverse windows**, vs zero; **4 new true blue**, vs >=5. S21 total red 90 is historical restored count, **not 90 new red contacts**. 3,627 local cortex updates, 6,376 censored, 1,353 rear-wall accepted, 1,897 front-wall excluded, 213 undelivered rear probes excluded, 635 passive rear retreats rejected, upper V420 motor map 3,972 accepted/4,145 wall excluded/1,886 other excluded, V428 random-uniform actor headings skipped 14.
- [Android regressions 41235121020](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41235121020), job `124637144911`: **PASS**, including V437 tests; passing these tests does not validate the incorrect source premise described below.
- [Original S21 CRC 41235121100](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41235121100), job `124637144707`: **PASS**; actual Android `Zero417SavedRealBrainBrakingCreditIntegrityAndroidTest` 1/1.
- [Wild 41235121080](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41235121080), first job `124637145216`: **BOOT FAILURE BEFORE DECISION 1**, `Original GRU3 owner vanished at physical burst 0 (cursor=-1,destroyed=true)`. This is **not** a valid 10k wild result. Re-ran only this failed job using GitHub rerun; new job `124653180031` was **IN PROGRESS** at last check. Preserve its original V437 commit; do not change behavior or infer red rates until completion.

### S21 original 1,000-decision physical windows
| Bucket | Signed | Gross | Signed/gross | New blue | Wall-contact frames | Static decisions |
|---|---:|---:|---:|---:|---:|---:|
| 1 | 48.22813 | 247.25781 | 19.5052% | 0 | 24,672 | 520 |
| 2 | 14.19363 | 536.24603 | 2.6469% | 1 | 7,686 | 229 |
| 3 | -101.12993 | 226.40929 | **-44.6669%** | 0 | 15,989 | 631 |
| 4 | -37.13852 | 301.98587 | **-12.2981%** | 1 | 17,931 | 605 |
| 5 | 210.28157 | 419.39080 | 50.1398% | 0 | 15,269 | 346 |
| 6 | 46.60106 | 205.11196 | 22.7198% | 0 | 24,448 | 634 |
| 7 | 114.11731 | 522.45687 | 21.8424% | 1 | 7,640 | 223 |
| 8 | -26.04098 | 205.15474 | **-12.6933%** | 0 | 4,152 | 668 |
| 9 | -157.06930 | 443.86242 | **-35.3869%** | 1 | 7,234 | 207 |
| 10 | 206.10448 | 378.76921 | 54.4143% | 0 | 14,712 | 420 |

The 10th bucket snapshot is at cursor 43,314; final completed decision 43,315 adds 0.18754 signed and 0.19955 gross. The four true new blue contacts at cursors 34,668 / 37,080 / 39,521 / 42,050 show issued heading **away from the blue** (`intentionToward` around -0.95 to -1.00) while PRE momentum points toward it (+0.75 to +2.26). This is evidence of substantial inertia/intention mismatch, not proof of voluntary blue-seeking.

### IMPORTANT SOURCE-CONFIRMED V437 DEFECT: its own mathematical premise is false
The V437 dossier and `Zero426JointSampleActorScore.headingScore` comments assert that candidate `k=0` replays the **previous decision's** `lastNoise` and must be omitted from current-decision score gradients. But the **actual original `Zero3RecurrentBrain.choose` source** does this at the **start of every new decision**, BEFORE candidate generation:

```java
for(int out=0;out<MOTOR;out++){
    int at=out*(HIDDEN+1);
    proposal[out]=dot(actor,at,state,HIDDEN);
    lastNoise[out]=sample(); // freshly drawn RIGHT NOW
}
...
noise[k][j]=k==0?lastNoise[j]:sample()*scale;
```

At the end of `choose`, `System.arraycopy(noise[selected],0,lastNoise,0,MOTOR)` does store the selected noise, but the **next invocation overwrites every one of those five entries with a fresh `sample()` before using candidate 0**. Therefore candidate 0 is in fact a **fresh current-decision sample** with scale 1, not a replay. `Zero426JointSampleActorScore.headingScore` V437 loop `for(int k=1;k<noise.length;k++)` incorrectly excludes a genuinely sampled current candidate from the joint score estimator. Its new tests `replayedPreviousHeadingCannotCreateCurrentActorGradient`, `learnedHarmSevenChoicesCreditExactlySixFreshDraws`, and `noFreshAlternativesHaveZeroCurrentHeadingScore` encode the false premise, so their PASS is not a valid mathematical verification of the actual generator.

This is a directly verified source/estimator inconsistency, **not proof that it alone caused** the behavioral regression. The V436 matched prior branch included candidate 0 in the score and had S21 14.7141%, 7/10 windows, 1 inverse, 6 blue; V437 changed the estimator only and produced S21 9.1296%, 5/10 windows, 4 inverse, 4 blue. Neither meets approval. The wild V437 rerun must finish before drawing the wild comparison. A next *single* justified change, **only after wild completes**, is to restore the mathematically valid current-decision candidate-0 score and replace false-premise tests with actual fresh-draw/scale tests. This may merely return to known failing V436 behavior; it is necessary estimator correctness, not a claimed cure.

### Historical 12 GitHub Actions independently rechecked this run
| Version | S21 original | Wild original | Android regressions | V356 CRC |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL (17.1723%, 8/10, 2 inverse, 4 blue) | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL (87 red/3 blue, 35→52) | [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS | [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS |
| V422 | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL (11.2881%, 7/10, 2 inverse, 4 blue) | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL (52 red/5 blue, 19→33) | [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS | [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS |
| V423 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL (10.0486%, 7/10, 2 inverse, 4 blue) | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL (26 red/4 blue, 14→12) | [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS | [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS |

## Release gate remains BLOCKED
No APK/OTA/merge; do not change original user brain, checkpoint CRC, CH2 V319, 194 sensors, physical solver, 512 cortex weights or 12 saves. S21 >=10k, >=25% signed/gross, >=9/10 windows >=10%, zero inverse, >=5 new blue; wild >=10k, red last 5k < first 5k, total red <=2x blue, actual blue contacts (and >=12% signed/gross as recorded in the existing experimental protocol); regressions and original V356 CRC PASS. This audit branch intentionally contains **no behavioral correction** while V437 wild rerun is active.

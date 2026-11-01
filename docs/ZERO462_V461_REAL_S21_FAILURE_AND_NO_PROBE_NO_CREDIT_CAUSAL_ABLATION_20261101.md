# ZERO462 — V461 original S21 failure; no delivered V319 CH2 probe, no positive exploratory credit

Date: 2026-11-01. **LAB ONLY — not approved, no APK/OTA, no merge.** Branch `lab/zero462-v319-no-probe-no-positive-credit` from original V461 final source `7f1b21bc70cd7f412cab30aa0e950647e2b38480`. Original single 194-sensor GRU3, 512 original local weights, saved S21 33,312 experiences, V356 CRC, CH2 V319, real 170-material physics and all 12 user slots retained. No V2.2, prescribed headings, fake rewards, threshold relaxation or user-slot writes.

## Verified historic V421–V423 Android Actions (complete job logs read)
| Version | S21 10k | Wild 10k | Regression | CRC |
|---|---|---|---|---|
| V421 | [41149391000](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149391000) FAIL 17.1723%, 4 new blue, 2 inverse, 1918 cortex updates | [41149393560](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149393560) FAIL 10002 decisions, 87 red / 3 blue, first ~35 / last ~52, 61.6262% signed/gross | [41149404217](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149404217) PASS | [41149399737](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149399737) PASS |
| V422 | [41149758090](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149758090) FAIL 11.2881%, 4 new blue, 2 inverse, 1851 cortex updates | [41149762948](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149762948) FAIL 10003 decisions, 52 red / 5 blue, first 19 / last 33, 42.7053% signed/gross | [41149771615](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149771615) PASS | [41149767313](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149767313) PASS |
| V423 | [41149983685](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149983685) FAIL 10.0486%, 4 new blue, 2 inverse, 2962 cortex updates | [41149987734](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149987734) FAIL 10003 decisions, 26 red / 4 blue, first 14 / last 12, 31.0766% signed/gross | [41149995431](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149995431) PASS | [41149992279](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41149992279) PASS |

The historic wild red first/last split above uses 5 original 1000-decision bucket cumulative counters, not HUD recent-lifetime counters.

## V461 original S21 actual 10,001+ decisions: behavior worsens, not approved

[Original Android S21 run 41350332988](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41350332988) **FAIL**, original brain loaded at exactly 33312, final decision 43313 (10001 new), real signed 417.99631, gross 3254.54515, efficiency **12.8435%**, **4 new blue**, **7/10** 1k windows >=10%, **2 reverse**. Cortex weight updates 2834; rear wall accepted 2089; front wall excluded 2041; verified V377 banks 3; V371 verified directions **0**; score evaluations 281677, predicted 27103, unsupported drift 191425. V461 grip perturbations suppressed for **10002** decisions; CH2 grip head remains present and learning.

Original 1000-decision windows, efficiency signed/gross (percent):
`[14.0239,17.7322,20.3393,-14.3760,25.5003,33.8371,1.6662,30.5327,-12.8537,36.5060]`.
Blue cumulative per window `[0,1,1,2,2,2,3,3,4,4]`.
Original S21 saved baseline had 90 historic red; redWindow=0 in this S21 room (not wild hazard avoidance).

V457 comparator (no V461 grip ablation) S21 18.0229%, 8/10 windows, 1 inverse, 4 new blue. Therefore eliminating random physical CH2 exploration alone is **not a locomotion fix**, and V461 should not ship.

[Original V461 wild 41350332990](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41350332990) first attempt FAIL **before decision 1** (Android Activity owner destroyed, cursor -1); no valid wild red/blue or 10k metric exists from that attempt. Retried the exact unchanged V461 SHA as attempt 2; pending at this writing. Do not count failed startup as 10000 wild, and do not infer safety. [V461 regressions 41350332995](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41350332995) PASS; [CRC 41350332850](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41350332850) PASS.

## New source-confirmed V319 credit inconsistency and one causal V462 intervention

`Zero3ContextAdhesionV319.begin` under V461 sets `perturb[s]=0` for all 16 physical CH2 sectors, consuming original RNG draws without delivering them. But `end` still calculates `credited = GRIP_CREDIT * gripRatio * gripFeature * Math.max(.15f, abs(perturb[s]))`. Therefore **even when no CH2 exploratory perturbation was physically delivered, positive exploratory grip credit persists with a fabricated minimum 0.15 factor**. This violates the implementation comment claiming credit comes from sampled neural-logit exploration. The positive grip contact itself is real, but the gradient cannot be attributed to a non-existent random intervention. It can shift learned CH2 weights and alter future grip traction, confounding the V461 A/B.

**ONE V462 behavioral difference vs V461:** when the V461 lab flag disables CH2 perturbation, make this *positive exploratory credit factor* exactly 0. Preserve negative true adhesion-work cost, decay, physical CH2 grip, sigmoid head, actual held-contact evidence, independent 194D GRU, CH0/1/3 muscle credit, all other gradients, unchanged default V319 path, identical RNG draws and persistence. This does not prove all grip learning is invalid; it specifically avoids crediting a suppressed intervention. Android unit regression asserts 0 positive probe factor under ablation and exact old 0.15/0.8 behavior with normal grip exploration.

**Pre-registered acceptance (unchanged):** Android original regression + CRC PASS, genuine S21 >=10000 with signed/gross >=25%, >=9/10 1000 windows >=10%, zero inverse and >=5 new blue; wild >=10000 genuine decisions with >=12% signed/gross, red last 5000 < first 5000 and total red <=2×blue. Compare V462 directly to V461; if still FAIL, reject. Do not merge or release.

## ADR 2026-11-01 / ZERO462

Decision: lab-only removal of *unsupported positive exploratory CH2 credit* when V461 physically suppresses the random CH2 perturbation.

Problem and evidence: 10002 V461 decisions with perturbation zero, yet source `Math.max(.15f,abs(perturb[s]))` guarantees nonzero positive credit on legal grip, despite no delivered perturbation.

Alternatives rejected: restore 2.2, force a heading, invent rewards, alter friction/physics, change V319 weights/checkpoint, relax movement thresholds, globally eliminate original V319 credit. These would violate owner/persistence or introduce multiple uncontrolled variables.

Risk: V319 may have learned legitimate contact association even without random perturbation; zeroing its positive exploratory term may reduce grip acquisition. Only the paired original 10k tests decide.

Verification: Android V461/V462 unit invariants, entire original Android regression, exact saved S21 CRC, original 10k S21 and 10k wild. Final outcomes to be appended only after complete logs are read.

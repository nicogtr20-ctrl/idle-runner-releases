# ZERO451 — V450 complete original Android 10k forensics and red-choice stage trace
Date: 2026-11-01. Isolated branch: `lab/zero451-original-choice-stage-red-contact-forensics`. **Research only. No APK, OTA, merge or save rewrite.**

## Mandatory source/history verification
Read AGENTS.md and ZERO419–ZERO423 source dossiers on V450 parent. Re-read complete GitHub Actions job logs, not just GitHub conclusions. All historical V421–V423 regressions and CRC PASS, all six actual original behavioral 10k runs FAIL:

| Version | S21 run | S21 signed/gross | 1k windows >=10% | inverse | blue new | wild run | wild signed/gross | red first/last 5k | red/blue | QA/CRC |
|---|---|---:|---:|---:|---:|---|---:|---|---|---|
| V421 | 41149391000 | 17.1723% | 8/10 | 2 | 4 | 41149393560 | 61.6262% | 35/52 | 87/3 | 41149404217 PASS / 41149399737 PASS |
| V422 | 41149758090 | 11.2881% | 7/10 | 2 | 4 | 41149762948 | 42.7053% | 19/33 | 52/5 | 41149771615 PASS / 41149767313 PASS |
| V423 | 41149983685 | 10.0486% | 7/10 | 2 | 4 | 41149987734 | 31.0766% | 14/12 | 26/4 | 41149995431 PASS / 41149992279 PASS |

The S21 'red=90' is inherited cumulative memory, not 90 new S21 red contacts. Historical wild first/last counts are sums of the actual 1000-decision redWindow counters. The V421/422/423 wild gradient updates were 5843/4587/4712; S21 1918/1851/2962. No historical result meets all release gates.

## V449 physical POST causal confirmation
The V449 wild retry [41295145681](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41295145681) actually reached 9001 decisions and then failed with `V356 timestep outside real physics`: `active=true,hasStep=true,dt=.016666668,steps=13,scale=.6666667`. Lifecycle forensic state: `lastStage=POST_SKIPPED_GENERATION,pre=254446,post=254445,terminalPost=17,resets=18,generationSkips=18,preEpisode=334,postEpisode=333,episode=334,lifeSerial=18`. This validates the missed PRE/POST pairing path described in ZERO450, not just a speculative crash. V449 S21 [41295145623](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41295145623) completed 10k but failed behavior.

## V450 completed actual Android original results (verified job logs)
- [S21 41296575088](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41296575088): **FAIL behavioral**, 10,002 new original decisions from real saved S21 33,312, 4 new blue, signed=570.09987, gross=3163.54081, **18.0209%**, 8/10 windows >=10%, 1 inverse. 3,084 genuine local cortex weight updates, 2,234 verified rear-wall accepted, 2,294 front-wall excluded, 345 undelivered rear probes excluded, 1,046 passive rear retreat rejected, 6,918 local censored. V377 predicted 638 / 279,632 score evaluations; 209,970 unsupported-drift score events; 194D original sensory GRU unchanged.
- [Wild 41296574988](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41296574988): **FAIL red safety**, 10,004 true newborn original decisions; signed=1610.06299, gross=4774.03927, **33.7254%**, 5 blue, **50 red**. Red first 5000 **37**, last 5000 **13** (improvement passes), but 50 > 2×5. 5,441 real cortex updates, 1,692 harm-rejected learning cases; V377 predicted 16,004 / 408,448 score evaluations, 341,498 unsupported drift evaluations; V377 5,391 train / 1,351 heldout motor samples. No V319 timestep exception through 10,004 decisions: evidence that V450 fixed the observed world-stream POST skip, **not** proof of universally error-free physics.
- [Regression 41296574964](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41296574964) **PASS**; [original saved S21 checkpoint CRC 41296575011](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41296575011) **PASS**. These do not override the behavioral FAIL.

### Every V450 original 1000-decision window
| Window | S21 signed/gross % | S21 blue window | S21 static/opposed decisions | Wild signed/gross % | Wild blue window | Wild red window |
|---|---:|---:|---|---:|---:|---:|
| 1 | 26.3334 | 0 | 539/54 | 13.9664 | 3 | 0 |
| 2 | 14.0010 | 1 | 207/244 | 28.7052 | 0 | 4 |
| 3 | 30.4752 | 0 | 490/44 | 21.4128 | 0 | 13 |
| 4 | **-3.3261** | 1 | 386/229 | 36.1545 | 0 | 9 |
| 5 | 28.6461 | 0 | 614/22 | 30.4846 | 0 | 11 |
| 6 | 23.6729 | 0 | 915/0 | 36.3101 | 0 | 7 |
| 7 | 16.1508 | 1 | 244/257 | 41.3985 | 0 | 5 |
| 8 | 31.7187 | 0 | 563/31 | 41.7832 | 0 | 0 |
| 9 | 3.5907 | 1 | 470/177 | 40.5460 | 1 | 1 |
| 10 | 25.3300 | 0 | 451/75 | 39.5154 | 1 | 0 |

### Actual V450 red-contact decision forensic (50 logged physical events)
Parsed every `ZERO404_HAZARD: REAL_MATERIAL_CAUSALITY` HARM line in wild log, including two repeated decision IDs representing separate real touches. 48/50 had previously learned visible contact evidence, 48/50 had seven offered GRU actor candidates, 36/50 had PRE physical momentum toward the red source >0.3, 23/50 had current intention toward >0.3, and 11/50 had momentum toward >0.3 while the neural intention was already away (<-0.3). **27/50** had chosen V369 optical utility >0.01 below the best offered; **18/50** were >0.05 worse; **16/50** were >0.10 worse. Ten were within 0.005 of the worst offered. These comparisons are **command-direction V369 optical scores, not verified physical escape outcomes**: do NOT simply force the maximum-optical option or assume a GRU scoring bug.

## V451 single diagnostic change: find the selection stage, no new reward or policy
The source chain is `Zero3RecurrentBrain.choose` -> V353 cognition selection -> optional V428 random exploration -> V354 planner -> V354 executive option recall -> issued physical original GRU3 motor command. V450's V405 red-contact log only includes the best offered and final authorized optical scores, not **which stage** caused their divergence.

V451 adds transient, read-only `choiceStageTraceForQAV451()` with the selected candidate indices and the real V369 learned optical utility after cognition, random exploration and planner, plus final utility and whether V354 executive recalled an option. The actual wild HARM log includes this alongside the existing V405 trace. No decision ranking, sampling, reward, V377 threshold, GRU3, 194 senses, 512 weights, V319 grip, 170-material body, checkpoint schema, 12 save slots or physics was modified. V450 world-stream repair remains present. Source commit at the end of code changes: `bdeda7a4c5691f56953520f6823250f18364d674`.

Original Android workflow push filters now include this isolated branch. **A successful push is not a PASS**; inspect the resulting four runs and full logs before any claim. For both behavioral runs require the same genuine 10k S21/wild hard gates. Specifically compare 50+ physical red contact stage traces: was optical disadvantage present already after V353, introduced by random V428, by V354 planner, or by executive recall? If most events have lower V353 score already, inspect the physical V377 forecast and competing V353 energy/damage scores; do not invent a safety direction or force a learned-harm override. If a test is running, keep branch and wait.

**Release blocked**: S21 18.02% <25%, only 8/10 qualifying windows, one inverse, 4<5 blue; wild 50 red >2×5 blue. No APK, OTA or stable merge.

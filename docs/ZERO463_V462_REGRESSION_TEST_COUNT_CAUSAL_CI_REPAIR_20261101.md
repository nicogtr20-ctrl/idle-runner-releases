# ZERO463 — V462 regression CI assertion repair (no gameplay change)

Date: 2026-11-01. Branch `lab/zero463-v462-regression-count-repair` forked from V462 `514025a5a0583e5400f14f9767332882502bdc2e`. **NOT RELEASE APPROVED.**

## Actual failure and root cause

Original V462 Android regression run [41354518195](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41354518195) finished `failure` even though its last class `Zero461V319OriginalGripExplorationAblationAndroidTest` reported `OK (4 tests)` and all four individual tests had `INSTRUMENTATION_STATUS_CODE: 0`. The workflow had a stale final count assertion `grep -q "OK (3 tests)"` for that class, inherited from V461, and exited 1 after the actual instrumentation. The fourth test is the new V462 no-probe/no-positive-credit assertion. This is an **incorrect CI verdict**, not an observed failing physical Android assertion. We correct ONLY the final count assertion to `OK (4 tests)`.

## Experimental isolation

No application source, original S21 trained 33,312 decision brain, 512 synapses, 194 senses, V356 CRC, CH2 V319 physics, V319 exploratory credit logic, body solver, reward, user saves or thresholds changed relative to V462. All four original workflow branch allowlists include this isolated CI-only branch to run the same-SHA full Android S21 10k, wild 10k, CRC and regression suite. Historical V421–V423 all six behavior FAIL and six regression/CRC PASS as documented in ZERO462. Do not claim V462 behavior improved until its real logs and 1k windows are inspected. No APK/OTA or stable merge.

## Verification pending

Full original regressions, CRC, S21 and wild must be independently checked from completed GitHub Actions logs. The previous V462 S21 [41354518251](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41354518251), wild [41354518230](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41354518230) and V461 wild retry [41350332990](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41350332990) were in progress at inspection; preserve them and do not count unfinished tests as PASS.

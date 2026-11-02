# ZERO471 — V470 third wild Android boot failure, original-GRU3 forensics
Date: 2026-11-02. Branch `lab/zero471-wild-gru3-boot-failure-forensics` from exact V470 commit `db79ea1f14238aff28789e7b011e91556ace7d72`. **LAB ONLY. No APK, OTA, merge, scripted steering, new reward, physics change or user saved-state modification.**

## Mandatory source review
Read AGENTS.md, docs/ZERO419..ZERO423 and master/implementation/ledger/tracker. The original 194D sensory mapping, 64 GRU, 512 learned motor weights, original V356 checkpoint CRC, CH2 V319 physical grip, 170-material body and 12 user slots are untouched.

## Independently verified historic 12 Actions, actual complete job logs
|Variant|S21 run and original 10k|Wild run and original 10k|Android regressions|Saved S21 CRC|
|---|---|---|---|---|
|V421|41149391000 FAIL: 10001 decisions, signed/gross 17.1723%, 8/10 windows, inverses 4/9, 4 new blue|41149393560 FAIL: 10002 decisions, 87 red/3 blue, first 5k 35 vs last 5k 52|41149404217 PASS|41149399737 PASS|
|V422|41149758090 FAIL: 10003 decisions, 11.2881%, 7/10 windows, inverses 4/9, 4 blue|41149762948 FAIL: 10003 decisions, 52 red/5 blue, 19 vs 33|41149771615 PASS|41149767313 PASS|
|V423|41149983685 FAIL: 10002 decisions, 10.0486%, 7/10 windows, inverses 4/9, 4 blue|41149987734 FAIL: 10003 decisions, 26 red/4 blue, 14 vs 12 but red/blue 6.5×|41149995431 PASS|41149992279 PASS|

The actual 1k S21 ratios from each job, windows 1..10:
- V421: +.165218, +.192875, +.198798, **-.152053**, +.439291, +.267355, +.126894, +.293715, **-.197862**, +.383081.
- V422: +.166078, +.161528, +.242661, **-.117919**, +.300526, +.148848, +.087885, +.253308, **-.196037**, +.263205.
- V423: +.198432, +.064892, +.160520, **-.410265**, +.265003, +.156450, +.101294, +.248111, **-.107413**, +.299148.
Actual wild red per 1k windows: V421 0/10/6/9/10/10/10/13/9/10; V422 0/10/9/0/0/6/4/12/10/1; V423 0/0/2/12/0/0/6/0/0/6. These are observed real contacts, not synthetic actor reward.

## V470 original Android, same exact db79ea1f commit
- S21 [41397116177](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116177): **FAIL**, 10,002 new decisions, signed 532.65651 / gross 3507.14343 = **15.1878%**, 7/10 windows >=10%, 2 inverse windows 4/9, 4 actual new blue, 3409 motor cortex updates. Window ratios: +.140269, +.103058, +.411530, **-.027461**, +.334222, +.341613, +.175907, +.253650, **-.128485**, +.090995. V443 scoreEvaluations=303460, predicted=21018, unsupportedDrift=205539, opticalDriftSuppressed=205539, verifiedV377Banks=1, verifiedV371Directions=0. No proven steering.
- Android regression [41397116373](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116373): **PASS** including coarse/fine V470 sensor assertions.
- Original S21 V356 checkpoint/512 weight CRC [41397116230](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116230): **PASS**.
- Wild [41397116200](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/41397116200): **FAIL on third attempt**, not a behavioral result. Instrumentation at 2026-11-02 08:08 UTC reported `AssertionError: Original sole GRU3 must really finish boot` at test line 114, before decision zero/any 1k window. All three attempts of this run failed before a valid 10k wild outcome. Do **not** infer wild red/blue improvement or worsening from a boot failure. No visible AndroidRuntime crash was reported in the filtered logcat.

## Source-confirmed diagnostic blind spot and single isolated correction
The wild Android test waits up to 300 checks with 100ms sleep, polling `originalV356NeuralOwnerForQA()`. It then asserts only the boolean and **throws away** `GameActivity.originalV356BrainErrorForQA()`, lifecycle state, cursor and fresh-birth checkpoint state. In the original `GameActivity.Lab.load` code, a failed V356 `load()`, checkpoint commit, or related initialization catches an exception and sets `brain3Error` without necessarily throwing an AndroidRuntime crash. This is a proven QA observability gap, **not proof that one particular initialization exception caused V470 wild**.

V471 changes only `Zero417WildOriginalVelocityCredit10000AndroidTest` to log read-only owner/cursor/error/destroyed/finishing, fresh-birth flag and serialized checkpoint byte length during the **same** 300-poll wait; the final assertion includes the latest real GameActivity error. It does not add retries, alter wait duration, force a new life, change the real brain or move any physical object. Four workflow branch filters are updated to run the exact same original Android gates on the same SHA. This is diagnosis, not a behavioral cure.

## Next action
Inspect actual V471 wild startup telemetry and 10k behavior if boot succeeds; if the error points to a deterministic source bug, isolate ONE causally justified fix in a fresh branch and rerun both original >=10000 Android conditions, regression and CRC. S21 still needs >=25% signed/gross, 9/10 >=10%, zero inverse and >=5 new blue. Wild must complete >=10000 original decisions, late 5000 red fewer than early and red <=2×blue (plus >=12% signed/gross). No APK/OTA/merge until all gates PASS.

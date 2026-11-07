# ZERO489 — OTA EXPERIMENTAL request on 7 November 2026 (NOT stable)

**User request:** “Voglio subito l apk tramite ota.” This authorizes a knowingly experimental phone update, **not a scientific claim** and not a merge into `release/zero30-ota`. Keep the previous OTA source branch and deployment safety lock unchanged.

## What this build really runs
- ApplicationId `com.overmind.zero.ota`; existing official V3 signing cert SHA-256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- User-requested experimental release `3.0-ota.489-exp.1`, **versionCode 1489**, monotone above publicly released manifest 1188 and preview build 1196. Users must **not uninstall** the existing app or Android deletes its local learned brain/world. Backup named slots first.
- Only CI candidate `R.bool.zero3_candidate` starts exactly the 19 explicit V489 lab research switches, in the original single 64-GRU3 brain; ordinary builds have all those flags off. Original 170 material nodes, 194D sensory topology, 512 original cortex weights, V319 grip and 12 saved slots remain structurally unchanged.
- This is the same research behavior from genuine V489 original 2×10000 Android trials; all V489 experimental flags remain on in the phone candidate after every lifecycle restore.

## Disclosure of known failed behavior — **do not describe as corrected**
Exact underlying lab SHA `46f93f20168294fd6c5c57dea56fee5dc495529a` measured:
- Original S21 trained previous 33312 and 10002 new real choices: **FAIL**, 12.3361% signed/gross, 6/10 sufficiently aligned, two inverse windows, only four new actual blue.
- New wild original 10002 genuine decisions: **FAIL**, **4.4736%** signed/gross, 39 actually touched red vs just 3 blue, even though red early22 and late17 decreased.
- Motor model V489 truly active: 4 heldout-predictive banks and 18,238 scores in wild, yet behavior much worse than V488's 28.18% signed/gross. Do not claim it made motor control better.
- Full Android regressions PASS and original saved V356/512 CRC PASS on that exact underlying SHA. Candidate-activation change requires its own Android compile, signed-release test, package/certificate/version verification.

## Distribution guardrails
- Experimental OTA publication requested in-app, so the public `main:updater/public/latest.json` will point to this test APK and **ALL connected app users can see it**; this is NOT an isolated per-user test channel.
- Publish only if Android compilation and instrumented test bundle succeed, official signing cert matches, appId matches, `versionCode=1489`, APK SHA256/bytes match the manifest, and current versionCode on main <1489. Use immutable filename. Publish an immutable GitHub prerelease matching SHA. NEVER commit the signing secret.
- **Do not weaken/remove** `tools/zero364_g3_physical_ota_gate.py` or original stable OTA publisher safeguards. New one-off workflow only on `lab/zero489-user-requested-signed-ota`; no merge into main except the OTA updater asset/manifest.
- VersionCode must monotonically increase to rollback via a future compatible APK; ordinary downgrade without uninstall is prohibited by Android.
- On real phone, preserve an export of current brain/slots before installing. If it moves poorly, report genuine physical logs; a signed update does not certify red avoidance.

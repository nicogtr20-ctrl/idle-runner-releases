# ZERO Symbiote OTA148 — permanent physical membrane lock: real telemetry, diagnosis, recovery, tests, signed release

**Date:** 15 October 2026 (Europe/Rome). **Status:** Signed OTA148 successfully published by GitHub; independent Galaxy S21 autonomous locomotion outcome still unverified. **Source:** [PR #52](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/52), merged as `f14c59974f791006e444c4827d3815af4de5ebb1`.

## Real newly uploaded phone evidence

Data were obtained from `zero-telemetry/current.json` on branch `zero-telemetry`, explicit Android user export `20261014-234158-fd2799d5a250` recorded at `2026-10-14 23:41:58 UTC`; read all four report parts and actual persisted `currentZero.physical_scene_v100` (not a synthetic simulation, previous report, or named historical save).

- `symbiote_body_v1_enabled = true`, active recurrent `ZERO 2.2 PRIMARY`. Cortex head made **4,569 physical action choices**, 3,644 trained intervals and 911 heldout.
- 13+ actual deaths/lives. **Zero real energy pickups, zero documented moving rigid objects, zero successful physical grips**. `gripCommand` max **0.30885** and 12/96 patches exceed .28 at the saved instant; physical `gripHold=0` at all sites.
- **ALL 96 physical membrane points have exactly ZERO X/Y velocities in the saved scene**, but 10 old plus 64 additional liquid parcels have nonzero velocities. A true 96-vertex geometrical crossing test found exactly **one intersecting pair of nonadjacent edges**, at signed area ~2458.98 vs rest area ~2461.25. `SymbioteMatterV1.invalid = 0` despite the physical impossibility.
- Logged decision trails continue to show cortex-selected actions and tiny, oscillating centroid movement, but negligible net travel compared with path length. During the last complete life 20-second windows typically showed 1–8 world units net displacement from 46–95 units gross path; these are internal/centroid motion, not useful locomotion.
- The motor head's `smh1` reward baseline is still **0.0**, and its 56 motor weights match newborn initial weights, as no actual energy/damage outcome has provided training. Do **not** reward motion simply to make it appear active. The physical cause here precedes effective reinforcement.

## Conclusive topology-lock mechanism

In `SymbioteMatterV1.repairTopology`, the old final fallback tested whether the proposed contour still self-crossed. If yes, it restored **every nonheld membrane site's position to `lastSkinX[i],lastSkinY[i]` and set `skin.vx[i]=skin.vy[i]=0`**. This is reasonable only when the previous physical contour is valid. Once a crossed shape exists in the previous frame or saved scene, the fallback simply restores an **already-crossed contour**, immediately zeroing all membrane velocities. The next step does the same. Nothing in the previous `invalid` counter detected or escaped this self-reinforcing lock. The user saw newborn post-reset oscillations for ~40 seconds, then apparently immobile until death. The observed per-frame 40-second onset is user observation; the exported schema does **not** contain 35ms full physics traces proving an exact onset time.

This diagnosis is grounded both in the actual scene geometry and the solver's zero-velocity fallback, not an invented lack of movement rewards or incapacity to learn.

## OTA148 correction

In `app/src/main/java/com/overmind/zero/SymbioteMatterV1.java`:
1. Before physical integration, detect a persistently crossed perimeter and apply **bounded, local membrane-neighbor surface-tension relaxation** on only the physically involved edge vertices. Enforce actual hold protections. The correction contains no goal, no desired travel, no food coordinate, no phase oscillator, no change to the primary neural policy, and no extra reward.
2. The final failed-topology response first checks whether the previous frame's contour had already been crossed (`previousContourCrossed()`). **Only a valid previous contour may be restored globally**. When the previous scene is already crossed, apply localized physical recovery rather than teleporting all 96 sites back to the same invalid configuration and setting all of their velocities to zero.
3. Preserve the body as 96 membrane nodes and 74 viscous internal particles, full material odometry, existing `smh1` trained weights, save schema, death continuity, true grip eligibility, energy model, app package `com.overmind.zero.ota`, and legacy bodies.

In `SymbioteMatterV1Regression.java`:
- A deliberately swapped adjacent cortex pair simulates the persistently crossed, immobile physical topology. Its shape must uncross and at least 8 membrane sites must regain measurable physical velocity under controlled muscle activation.
- **A second test embeds only the 96 perimeter x/y coordinates extracted from the user's newest actual saved body**, centered and anonymized (no account, position, device data), confirms *exactly one original crossed edge pair*, runs the real viscous solver to recover topology, then confirms the formerly frozen membrane responds to newly applied local physiological muscle commands. This fixture matches the actual observed failure rather than relying only on hypothetically favorable gaits.
- Existing full independent pseudopod extension, gel volume, self-intersection, death/weight persistence, no-autogrip and Android build tests remain enabled.

**Test results:** Pre-release comprehensive Java/Android QA SUCCESS [run 38656158954](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38656158954), including exported 96-node geometry; source PR #52 merged on `feature/zero22-active-recurrent-ota117`. Strict signed publisher QA+release SUCCESS [run 38656315634](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38656315634). All unrelated source-hash pins and signing/security gates remain.

## Signed release manifest

`main/updater/public/latest.json` checked after signed publication:
- `versionCode: 1148`, `versionName: 0.11.6-ota.148`.
- Official APK URL: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1148-34563e1ee2a2.apk`.
- Manifest SHA-256: `34563e1ee2a2943ef14a885a9668acfe75cd13b54b8f937d205fff0b0ab105ea`, size **419432** bytes.
- Signed publisher `publishedAt: 2026-10-14T23:51:07.586677+00:00`. GitHub Actions signed build and manifest publication **confirmed**. Independent public HTTPS APK download/hash is **not** confirmed in this assistant environment, so do not claim it.

## Next live-test criterion and preservation

**Keep the CURRENT ZERO with its ~4,569 decisions**, even though the user earlier inadvertently reset an older ~8K organism. Install OTA148 **over** existing signed app via built-in *Aggiorna* or its official APK (do not uninstall/delete app data). Observe one entire life for activity after 40 seconds and whether membrane can extend a pseudopod without trapping the rest. Compare actual net material locomotion, real resource acquisition, and physical contact, not misleading gross center movement. Export new explicit current-working-profile telemetry. If still not locomoting, the next distinction is *physically responsive cortex but ineffective autonomous motor control* (motor readout remains newborn/untrained due to lack of experienced energy, possible network/learning architecture) vs *another mass/surface mechanics issue*. Only change brain after proving mechanical prerequisites; **no movement-as-reward**.

**Limitation:** The regression proves recovery from the user's exact broken membrane and continued physical responsiveness under controlled cortex actuation. It does **not** prove autonomously beneficial movement or survival in the game on the user device.

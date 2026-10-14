# ZERO Symbiote locomotion postmortem and verified OTA146 release

**Date:** 15 October 2026 (Europe/Rome). **Status:** Android signed OTA146 published; independent real Galaxy S21 locomotion outcome NOT YET observed. Source PR [#49](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/49), release gate PR [#50](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/50).

## Why this fix was necessary (real saved virgin Symbiote data)

Explicit Android user telemetry branch `zero-telemetry`, index `zero-telemetry/current.json`, report `20261014-225348-fa86d86a56f7`, with four multipart JSON files. The only intended interpretation is that these are real observed saved working-profile data; do not conflate test/historical labs.

- `symbiote_body_v1_enabled=true`, primary learned chooser `ZERO22_PRIMARY_WITH_V20_V21_PASSIVE_EVIDENCE`.
- 8,294 selected cortex-head decisions, 6,615 training intervals, 1,653 heldout; 25 completed lives.
- No true positive energy contacts and no confirmed external object displacement, no legal grip hold. Snapshot gate had thousands of touch frames but zero grip commands/holds.
- Current saved surface `gripCommand` max approximately **0.153** while the real object solver demands `Morphology.GRIP_THRESHOLD = .28`.
- The 56 shared cortex motor weights after 8,294 choices were *bit-level numerically equal* (floating-point rendering error under 2e-9) to initial random weights derived from `seed=0x3910b15dceef41L`. This is expected from actual neutral reward zero and `symbioteHead.consequence(actualEnergySinceChoice, actualContactHarm, 0f, ...)` but means the readout has not yet trained. Do **not** fabricate movement rewards to solve this.
- The last completed life produced only ~38.5 world units of net displacement over 211 seconds, with many self-canceling displacements. This is a descriptive measurement, not a randomized causal A/B finding.

## Physics root causes found in the OTA143 code

1. **Substrate reaction effectively lost.** Legacy `MotorBrain.Body.step` uses strongly variable per-node physical damping `exp(-(2.1+grip²*100)*dt)`. OTA143 replaced that with `exp(-(2.4+adhesive*7.5)*dt)` applied to 96 membrane nodes. The fluid exchanges equal-and-opposite cortex/internal impulses, so weak/homogeneous traction largely cancels net travel. Internal movement alone is NOT a ground reaction.
2. **2.2 adhesion action-bank information was dropped.** `SymbioteMotorHeadV1.chosen` interpreted `action%8` as heading, but failed to propagate the high bank `action>=8` which is the PRIMARY recurrent brain's physically selected grip intention. The old surface received a proper controller-driven adhesion cue. Thus newly born heads output sub-threshold grip amplitudes even on valid contact.
3. **Centroid did not represent the new matter.** `MaterialGeometry.observe(body)` averaged the ten legacy body parcels despite Symbiote having 64 more free interior particles and 96 physical shell sites. Sensorimotor odometry was therefore biased by arbitrary ten-node sampling and distorted-body deformations. Physical position must be a derived aggregate, never an accelerated imaginary nucleus.
4. Original OTA143 physical regression demonstrated single-pseudopod extension, not whole-body locomotion under endogenous cortical physics. Android screen/thermal test was not performed.

## Exact changes landed

- `SymbioteMatterV1.java`: add a mass-weighted `observeBody` center/velocity (shell 50% total matter mass, 74 fluid parcels 50% total); introduce locally variable physically grounded substrate stick/slip `traction=clip(.50+2.75*cortex[sector*4+2],0,1)`, membrane damping `exp(-(1.8+66*traction)*dt)`. Cortex impulses and real particle reactions remain equal/opposite; no teleports, no automatic movement, no artificial reward and no named-object input. Contact with a discrete obstacle and a real holder remain independent.
- `SymbioteMotorHeadV1.java`: cortex actuator strength for newborn readout becomes `tanh(2.8*z+1.4*noise+(channel==2&&action>=8? .55 : 0))`. This maps the **existing self-selected** 2.2 grip action into its physical voluntary muscle channel without choosing a different action. No seeded walking pattern; learned weight schema `smh1` unchanged, existing 8K-life profile compatible.
- `GameActivity.java`: after resuming scenes, after negative contacts and after physical collisions, derive Symbiote center with `symMatter.observeBody` rather than old ten-node-only observer; legacy branch unchanged.
- `SymbioteMatterV1Regression.java`: add tests that action-bank 12 physically exceeds grip threshold compared with action 4 at same heading; actual change in 64 fluid particles affects center; cyclic nonrewarded local substrate actuation produces nonzero true mass displacement with no self-crossings and no invalid particles. Legacy pseudopod, area, persistence and model checks continue.

**Scientific caution:** A lab fixture with externally specified temporary cortex signals tests *physical possibility* only, not a scripted real in-game gait and not that the untrained high-level brain can use the new body yet. Phone training should quantify centroid displacement, actual resources, valid grips and corroborated physical contact. More data cannot be used as proof of autonomous behavior without proper baselines.

## Build, QA and release trace

1. Main physical corrective PR #49 merged into `feature/zero22-active-recurrent-ota117`, commit `3536e290140df441d3dcfa7fa9041d7cdf6a9aa1`.
2. Pre-release Java regression, corrective invariants and Android debug APK compile **SUCCESS**: GitHub Actions run [38652561483](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38652561483).
3. First signed publish run [38652749642](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38652749642) failed **intentionally** at unchanged source SHA pins of four modified reviewed source files; NO APK was published then.
4. PR #50 merged only the four reviewed new SHA-1 Git blob pins into the existing strict release gate; all unrelated pins and protective assertions were preserved. Commit `88c69a859dbbaf9ca4027005305ea0bfe692d4f6`.
5. Official signed build/publisher [38653053445](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38653053445) **SUCCESS**. `main/updater/public/latest.json` verified:
   - `applicationId=com.overmind.zero.ota`
   - `versionCode=1146`, `versionName=0.11.6-ota.146`
   - `apkUrl=https://overmind-zero-updates.vercel.app/apk/overmind-zero-1146-867fd07eff6d.apk`
   - `sha256=867fd07eff6d6676e5e8a4a538dd39d604086ea08c3102a2efd2bd8832583466`
   - `sizeBytes=416572`
   - `publishedAt=2026-10-14T23:18:58.802353+00:00`
   - **Caveat:** independent external HTTP download and local digest comparison could not be performed in this assistant runtime; the official GitHub publisher reports success and main manifest names the artifact. Do not claim independent network SHA verification.

## How to continue without destroying experimental history

On the Galaxy S21 tap the existing app's **Aggiorna** button (OTA update) or open the exact versioned signed URL in Chrome and install **over** the existing `com.overmind.zero.ota` app. **Never uninstall or clear app data** merely to apply the body fix. Preserve the same virgin Symbiote's neural state and 8,294+ experience history. Take short baseline visual observations at x1 and x5, check active interaction with boxes/doors and local pseudopod while stationary. Then run a moderate period at x20/TEST MAX if S21 thermal frame rate is acceptable and export new explicit telemetry using **CONDIVIDI DATI CON GPT**. Compare pre/post at the same organism age, with controls for environment and life state; verify nonzero actual pickups, grips and displacement, not raw choice count. If there is continued stalling, inspect zero-energy reward vs neuromuscular command and physically valid substrate contact **before** changing brain/reward. All legacy named veteran saves continue to use original mechanics.

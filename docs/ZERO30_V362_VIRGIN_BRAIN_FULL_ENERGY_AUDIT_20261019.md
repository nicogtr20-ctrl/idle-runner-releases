# ZERO 3.0 OTA362 — verified cause and repair of fake virgin brain and 69/100 birth

Date 19 October 2026. User reports: even OTA361 body is too slow, cannot reach blue resources or obstacles; new life begins 69 instead of 100; a new supposedly virgin brain shows ~4,000 experiences.

## Two source-level bugs (confirmed)
- `GameActivity.resetEpisode()` clamps `reserve=Math.max(70,Math.min(92,reserve))` and death sets `reserve=70`; the HUD truncates `(int)reserve`, so the first real physics step displays 69 after spending a fraction. This is a **fixed game design**, not ZERO's autonomously chosen hunger, learning, or damage.
- `SaveSlotStore.newGame()` clears the working profile, but `Zero3OriginalBrainV356.load(context, "")` looks up the separate V354/V355 experimental neural profile `zero3-ui-v354`, rehydrates the trained GRU/cortex/grip weights and decision cursor, which can start around 4,000. This VIOLATES the menu's explicit promise of a virgin new ZERO. The normal migrator is only valid when upgrading an existing profile, never for a new player-created brain.

## OTA362 source correction
- A new life starts with **real** 100 fuel and normal subsequent hunger and muscle consumption (no fictitious 100 reward). HUD rounds displayed energy (not physical fuel) so 99.99 displays 100, not 99.
- `SaveSlotStore.newGame()` leaves a one-shot, crash-safe `FRESH_ZERO3_BIRTH` in the working profile; old named slots and rescue copy remain untouched, and this marker is never copied into a named slot.
- The first `GameActivity.load()` after new game calls `Zero3OriginalBrainV356.load(..., allowHistoricalImport=false)`, creating a genuinely virgin 0-decision GRU/cortex/adhesion policy; saves the valid V356 neural checkpoint and clears the virgin marker in **one atomic commit**.
- `ResetDomains.ALL` likewise explicitly requests a genuine fresh V3 and retains the symbiote enabled, avoiding a silent old experimental brain from a parallel global prefs store.
- Existing saved `Zero3OriginalBrainV356.KEY` on a regular game reopen is still restored byte for byte; old V354/V355 experimental neural archives can still migrate for legitimate first-time old-profile upgrades, NEVER after explicit new game.
- Preserves exact original-game world renderer, monsters/doors, physics, 194 senses, GRU, 16x4 cortex, real adhesion, rewards, full V356 neural CRC format and signed Android package identity.

## Evidence/gates
- `.github/workflows/zero362-virgin-full-energy.yml` compiles entire Android app and instrumentation, invokes inherited V361 original-game tests, tests NEW GAME with true physical 100 energy and 0 decisions, cold activity restart preserving young GRU, old profile recovery, and RESET ALL preserving symbiote / fresh policy.
- No claim OTA362 fixes autonomous locomotion: 19 October latest explicitly uploaded phone telemetry is **OTA360** (43,018 decisions); V361 real intention versus motion diagnostics have NOT yet been shared, and the calibrated gain 2.4 motor fixture was never proof of autonomous finding resources. Do not blindly force movements, directions, artificial gem targets or add fake reward.
- After a validated signed OTA, user should share first V362 data. Calculate physically real chosen direction vs measured delta, gross/net displacement, work/impulse, and contact events and compare per-decision intervals to decide if actuator forces, cortex learning or GRU goal selection are at fault. Do NOT claim demonstrated on-device walking.

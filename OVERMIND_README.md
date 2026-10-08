# OVERMIND: ZERO — Android v0.1

A dark, top-down autonomous artificial-life simulation. The player does **not** control the organism and the organism does not know the player exists.

## Controls
- Watch ZERO decide to explore, harvest, investigate, and expand.
- Tap an intervention at the bottom, then touch the map to place it: energy (18 XP), artifact (35 XP), anomaly (28 XP).
- Swipe the world to pan the camera; tap FOLLOW to center on ZERO.
- Speed x1 / x3 / x6; pause.
- Tap world objects for contextual observations.

## Learning
The agent uses tabular Q-learning for action selection with state features, exploration, and reward-based updates, plus persistent spatial exploration memory. "Experience" (agent history) is distinct from player XP currency. This is not an LLM or consciousness. Persisted with Android SharedPreferences.

## Android Build
Branch: experiment/overmind-zero-v01.
GitHub Actions -> OVERMIND ZERO APK builds the APK and publishes a private GitHub Release.

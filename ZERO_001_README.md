# OVERMIND ZERO / Experiment 001

## Purpose
Test whether an artificial liquid organism can **discover** a useful way to move without a prerecorded gait or a pretrained model.

ZERO begins with a soft-body perimeter of 10 material particles and a 62-parameter neural controller with random weights. The body is rendered as dark ferrofluid, with no fixed appendage count. The network controls radial contraction and adhesion of each material particle. A top-down 2D physical simulation resolves spring forces, inertia, and friction.

## Training
An offline-running evolution-strategy trainer makes candidate controller mutations and evaluates their *physical* displacement over 9-second episodes in three training directions. It retains weights only on improved measured fitness. A separate **CONFRONTA** button evaluates both the original randomly initialized model and the learned model in three held-out directions.

- No pretrained language knowledge.
- No predefined gait / walking animation.
- No inference of consciousness or free will.
- The organism **is given** a sensory stimulus gradient and an innate objective to approach it. It does not discover its reasons for existing.
- Training scores are genuine empirical measurements; a higher training score does not guarantee better generalization.
- Persisted parameters restore after app restarts.
- This is a **motor learning experiment**. It does not yet implement independent curiosity, physics-based object manipulation, or a general world model.

## Controls
- ADDESTRA: pause/resume neuroevolution.
- TEMPO x1/x5/x20: simulation playback speed, not CPU training speed.
- CONFRONTA: benchmark original brain vs current brain on unseen directions.
- Pause/resume physical simulation at top-right.
- Tap header **twice** to reset the entire learned brain.
- ZERO-001 has a separate applicationId so it can coexist with the v0.1 app.

## Development and build
GitHub branch `experiment/overmind-zero-brain-v02`, workflow `OVERMIND ZERO brain APK`. The action runs a standalone JVM smoke test before building and publishing a debug Android APK in private GitHub Releases. Target Android 7+.

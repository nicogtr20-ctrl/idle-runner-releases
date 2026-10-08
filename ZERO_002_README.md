# OVERMIND ZERO / Experiment 002

ZERO-002 installs as a **separate Android app** from ZERO-001, because independently signed debug APKs can have different certificates. ZERO-001 remains installed with its motor learning data. ZERO-002 starts a new randomly initialized motor brain and learns its own motor and object association models, saving both for future sessions.

## What is new
The creature experiences initially unidentified objects with three distinct raw signatures. It can physically touch energy-changing objects or move an inert object with its body. ZERO's decision policy receives **only the observed sensor signature and position**. Hidden consequences are implemented in the simulated environment, not passed directly to the brain. After contact, an associative learner updates its preferences based on energy change and displaced-object motion. It balances novelty, current energy, previously learned value, and distance in selecting where to explore.

The environment contains two beneficial, two harmful, and two movable objects per episode, with positions varied at each reset. The simulation gives the organism finite energy. Upon depletion it restarts physically while retaining its learned memory. The player observes; it does not command ZERO.

### What it does NOT do
- It is not an LLM or an autonomous general intelligence.
- It is a tiny fixed-capacity associative memory, not a general-purpose world model.
- ZERO is given an innate desire to preserve simulated energy and to investigate novel signals, and its motor network gets a bearing to its selected target.
- Objects have pre-defined physical laws in the **environment**; ZERO learns their consequences from experience.
- We have not yet trained a motor policy specifically to push objects effectively or learn entirely new physical manipulations.

## Controls
- Left control starts/stops **background motor training**. It does not disable the world-learning observations.
- Center control changes playback speed. Right control compares motor training on held-out directions.
- The display shows energy, number of object contacts, and the number of raw signature classes whose effects have been encountered.
- Double-tap the header to erase brain and learned memories.
- The world's associative memory and motor weights acquired in ZERO-002 are persisted independently. The ZERO-001 app and its prior data are not changed.

## Testing
The GitHub workflow runs pure-JVM tests of motor training and of the associative learner on known synthetic contacts, then compiles the Android APK and creates a GitHub Release. Real-world Android behavior needs observation on device.

# ZERO-009 — Online sensorimotor learning instead of scripted targets

## Request and scientific integrity
The user wants ZERO to observe the environment, itself and consequences of its actions, and to decide what to try on its own. We **must not** claim this proves literal conscious free will or human symbolic reasoning. ZERO's body, sensory channels, intrinsic novelty and homeostatic energy objective are designed. The learned action policy itself must not receive the identity of a "correct" door solution or a scripted `keep pulling` directive.

## Implemented
- `SensorimotorMind.java`: independent online learner observing **26 continuous sensory readings**: its energy, x/y velocity, physical membrane stretching, tactile and adhesion fractions, 8 obstacle rays, 12 color-frequency channels in visual quadrants.
- **Occlusion**: food color behind solid walls/gate is invisible to the brain; no privileged object target or global direction is passed.
- **16 generic motor actions**: 8 directions × two adhesion-drive amplitudes. The motor planner samples its own actions from a learned temporal action-value estimator plus exploration, and predicts sensory consequences using a 11,232-parameter online linear forward model (plus 432 action-value parameters, **11,664 in all**).
- Old `DecisionBrain.choose(environment, ...)` removed from active navigation; no `atan2(target)` used to steer it, even though prior knowledge weights and stats remain stored. Old off-screen trainers are paused in favor of learning from actual observable life in the game. Preserves motor 62, manip 18, membrane 82, old decision 91+.
- Generic rewards from physiological energy change, overall sensory change and prediction-error progress: **no hardcoded reward for door movement, gripping twice, opening or clearing room**. If those cause visible sensory change, the learner can discover useful correlations; this is not guaranteed.
- A control channel changes local adhesion drive after the model chooses it. Adhesion still needs physical touch and sustained grip, and door requires two independent active patches.
- Model persists into `sensorimotor_model_v09` in SharedPreferences; 11k+ learned parameters, experiences and decisions carry over deaths and app restarts.
- In-app stats show actual world-model size and experiences. Active HUD shows autonomous motor direction, chosen grip drive, and measured prediction error.

## Important limits and expected test
- Current forward model predicts only the next sensory observation, not arbitrary futures, and temporal value learning is elementary. It does not have linguistic concepts of gates or meaning and does not reason "now opening is 37%". Its value-based persistence may improve with experience but must be tested on a Galaxy S21.
- The sensors/fitness/backprop/hunger are authored. This is a **prototype of autonomous sensorimotor agency**, not consciousness, not unrestricted self-rewriting or guaranteed puzzle solving.
- For on-device comparison, watch choices, model prediction error over time, whether grip actions adapt after feedback and whether the physical gate makes progress. A drop in prediction error alone is not proof of room-solving.

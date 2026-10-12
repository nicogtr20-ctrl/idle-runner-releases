# OVERMIND ZERO 0121 — Distributed tissue self-sensing and truthful bioluminescent readouts

## Motivation and evidence
The 0120 organism has ten real movable internal masses and 96 motorized connected
membrane particles. The previously trained proprioceptive predictor sees only
8 direction sectors x 6 summary channels (48 values). The legacy optical model
uses 8 basic rays, 16 body-mounted secondary rays, and occluded spectral signals.
The previous aqua perimeter spots illuminated *every other particle* and all
points farther than 48 world units from the center, irrespective of sensory
activation. Confirmed adhesion was painted instantly orange.

## Change — physical sensation, not a personality or behavioral script
- An additive **96 x 9 = 864 local tissue readout** measures EACH point's:
  collision pressure, motor extension command, neural adhesion drive, sustained
  real adhesion, relative radial and tangential speed, radial position,
  local edge strain, and an outward physical obstacle probe.
- The ninth sensor casts **six short probes** from each of the **96 actual
  membrane positions**, never the body centroid. The first solid hit terminates
  that probe. This is proximity vision without object names or invisible goals;
  existing color/spectral and 16-direction spatial vision still provide their
  original channels. The new probes do NOT provide 96 independent RGB eyes.
- The high-resolution map remains directly addressable by each particle. For
  economical learning it is pooled into **16 contiguous tissue sectors x 9
  modalities** with max-pooling for sparse touch/grip/sight and averaging for
  movement/shape signals. This is body-topological order rather than
  hardcoded hands or globally oriented octants.
- A **separately serialized, action-conditioned predictor/value readout** learns
  only from executed actions, observed bodily transitions and actual
  energy/object displacement. Untouched validation decisions never train.
  Influence is **zero until 32 held-out tests** outperform an unchanged-body
  baseline by at least 4%, with a low bound (+/-0.12 per action).
- Nothing in the new perception injects a direction to a resource, a door
  identifier, a scripted arm, contact-triggered adhesion or a new energy reward.
  All 26 existing brain inputs, 16 actions, principal, motor, grip, 82 membrane
  coefficients, 48-channel proprioception and 16-ray spatial memory persist.
- Correct an old new-life reset to initialize existing 16-ray spatial eyes
  FROM physical membrane sites, matching continuous/resume operation.

## Appearance (observer only)
- Black fluid body and black border/particles at rest, with restrained charcoal
  body shading. No permanent alternating teal lights.
- Cyan intensity follows the point's actual motor extension, material-relative
  velocity, physical touch/pressure and latest OUTWARD proximity measurement.
- Bright red **physical grip lamps** light only when the original adhesion
  threshold AND real contact are met. A soft radial lens/halo rises over
  roughly 0.15 seconds and decays over roughly 0.27 seconds of real render
  time. No instant spark and no fake grip event.
- Both glows are local rendering state, not saved cognition. Frame-independent
  exponential easing; user-selected x1/x20 speed does not alter LED kinetics.
  At rest the black dots are not replaced with default cyan.

## Lifecycle and compatibility
- New independent key: distributed_body_v121. Existing keys/weights untouched.
- Full working profile and all twelve existing named slots automatically capture
  the additional weights via ordinary SharedPreferences snapshotting.
- The selective PROPRIOCEPTION reset clears *both* the existing old body
  predictor and the new high-resolution model; WORLD, MOTOR, MEMBRANE,
  other selective resets do not clear either.
- On genuine death clear ONLY immediate sensory state; learned parameters,
  held-out evidence, other policy models and named saves survive.
- Reloaded physical life first re-samples the actual restored membrane. No
  synthetic "transition" is trained across pause/restore.
- No Android new permissions, remote upload, physics force, gate/collision
  thresholds, or resource outcome changes.

## Verification and caveats
- The new pure Java regression checks all 96 x 9 channels, independent
  touch/motor/adhesion/real hold, nearest-obstacle outward rays following a
  moved membrane site, no phantom empty-world sight, validated predictive
  improvement, deterministic persisted choice influence, malformed snapshot
  refusal, and preserved learned model after death.
- Existing full deterministic sensorimotor, 48-feature, 16-ray, grip/gate,
  multi-box collision, physical fluid and 10k+ material continuity regressions
  remain required for signed release, followed by real Android debug compile
  and publisher's signed APK hash/pin/OTA update checks.
- A 96x9 internal map is **not** equivalent to a consciously experienced 3D
  body. Outward micro-probes measure solid proximity, not individual full
  color/spectral images; concavities can limit their field of view. Learning
  advantage must still be confirmed from real long-lived trajectories.

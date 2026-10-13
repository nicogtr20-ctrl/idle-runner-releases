# ZERO 2.0 — Perceptual Associations (research stage, 13 October 2026)

**Status:** isolated experimental branch `feature/zero20-perceptual-associations`, derived from `feature/zero-20-hierarchical-intelligence` at `e5c5aed21cc58098cdf76ef3afbcc791a706b53d`. **NOT an OTA, not a phone update and not a proven new capability.**

## What the user wants

An organism that can gradually invent its own categories from what it sees and feels, recognize similar physical forms without being handed the label *box*, associate self-chosen membrane configurations and actions with genuinely observed effects, and later reuse sufficiently proven techniques. ZERO must be allowed to doubt and to revise a mistaken association.

**This phase is the passive prerequisite, not the last step.** No object identity, class label, scene coordinate, hidden energy or legal-grip predicate is fed into the association model. The living `SensorimotorMind` makes exactly the same motor decisions as before.

## Implemented boundaries

1. `PerceptualAssociationsV20` receives the current `SpatialPerception.ranges()` (16 first-solid 22-unit-step distance rays starting from physical membrane sites). Those rays already implement solid occlusion. The new learner does not iterate game entities and cannot know `isBox()` or `isConstruction()`.
2. It centers an 8-ray local depth contour on the nearest sensed surface, making the descriptor insensitive to a *global shift in viewing direction* for an otherwise identical ray silhouette. It retains approximate depth and angular span. A far-off second obstacle can remain in the scene; two similarly close, separated solid returns cause a deliberate abstention. Empty/invalid views do not create categories.
3. Up to **24 bounded, unnamed prototype hypotheses** are grouped by raw descriptor similarity, not by shape tags or visual object IDs. This is partial 2D silhouette matching, **not** full object segmentation, perspective invariance, an RGB camera, stable physical instance tracking, or actual evidence that two sights are the same box. A single ray or a wall may give ambiguous evidence.
4. `chosen(actualAction)` snapshots the currently recognized hypothesis after the incumbent real policy has chosen its action. `consequence(realExternalMotion,train,holdout)` records the physically measured scene effect *after* that action interval; no movement or mere grip command is counted as movement success. The resulting association is **scene-level correlation**, not verified action-on-this-object causation. Actual energy contacts are counted separately and explicitly **never bound to a claimed object cause**.
5. The existing `LearningDiagnostics.shouldHoldout()` gates model updating. Heldout intervals evaluate action-conditioned Brier error vs a same-action general baseline; heldout instances cannot update prototype vectors, category occurrence counts or action-outcome counts. Predictive confidence is conservative and informational: there is **no policy advice or change to forces, motor weights, energy, grip law or rewards**.
6. The saved working profile has a new optional key `perceptual_associations_v20` with format `pa20`: bounded arrays, strict nonfinite/size/schema checks, atomic restoration, no pending action saved as completed. Old profiles lacking it work unchanged. A simulated death clears only transient views/pending intervals, retaining learned prototypes. The existing selective SPATIAL reset includes the associated catalogue.
7. A new category overview is shown under CERVELLO; explicit-share-only `zero20Passive.perceptualAssociations` reports counts and error measurements **without exposing raw prototype or motor weights in the compact digest**. The unchanged complete working-profile export still contains the new optional saved key and remains user-consented/private.
8. No Android release, stable branch merge, automatic upload, saved-profile replacement or background activity is authorized by this experiment.

## Falsifiable regressions

- Empty world means no fabricated object; two similar-distance separated objects mean abstention.
- Circular shift of the exact same 16-ray silhouette reuses a category; a substantially different contour creates a new tentative category.
- Only observed external displacement contributes positive physical-outcome evidence; touching or pressing without displacement contributes a negative outcome.
- Heldout evaluation is independent of training; a learned pattern survives a simulated death, serializes exactly and rejects incompatible, malformed or nonfinite formats atomically.
- 440 original sensorimotor choices are identical with vs without the passive model; preexisting gate, two-red-box, one-red-panel, save and full Android regressions must pass.
- Source audit rejects any use of special object types, concealed resource position or new choice-score/physics code. Real-game and synthetic/headless unit tests use identical *ray descriptor logic*, but **a supervised on-device performance and ecological A/B experiment are still required**.

## Next scientific gates, not implemented

**P1 — Multi-view instance hypothesis:** use only successive actual range sweeps and self-odometry to merge stable boundaries, estimate obstacle width/height and uncertainty; verify rectangle vs circle under rotation, changing distance, partly occluded or adjacent shapes and moving body. Avoid using internal game IDs as correspondence labels. Require physical test scenes with objectively measured ground truth only on the **evaluator side**.

**P2 — True tactile object attribution:** align body contact footprints with the visible range cluster over time; distinguish observed movement of that same obstacle from unrelated objects and self-motion. Estimate verified two-sided grip *from own tissue*, not its legal engine result. Build evidence-backed per-category and per-configuration action-effect memory.

**P3 — Durable technique memory:** archive genuine hand+membrane coefficient snapshots only when repeated comparable contacts physically move an independently tracked object. Keep unsuccessful and out-of-context trials; add heldout replay/rollback and avoid discarding a proven technique during unrelated navigation. This is beyond OTA103's two-window incumbent protection.

**P4 — Advisory A/B:** only after reliable identity tracking, time-aligned causal effect and heldout generalization, run matched virgin/trained and familiar/novel-object tests, then full-life energy/survival comparisons with strict fallback. ZERO may propose but must never be forced to execute a technique or collect unearned energy.

## Acceptance status

**Code created and connected; CI is authoritative and must be checked on the exact final head.** Until completed green Java + Android and independent device A/B evidence, the model is experimental shadow instrumentation, not a demonstrated learned recognition of boxes or improved intelligence. Do not change OTA103 or the 12 existing user slots.
# ZERO388 — forensic motor root causes: nonconservative shell elasticity and mixed-time integration (25 ottobre 2026)

**STATE: forensic audit only, NO gameplay code changes, NO signed APK, NO stable OTA, no checkpoints rewritten.** Audit branch `audit/zero388-elastic-impulse-and-local-motion-s21`, based on `fix/zero387-contact-freshness-audit`. Preserve 170 body particles, 96 membrane nodes, original V356 GRU64/194 inputs, V319 grip and 12 named user slots. Most recent V387 manually signed 1195 remains experimental and NOT locomotion-certified.

## Source-proven physics flaw #1: unequal internal action/reaction
`app/src/main/java/com/overmind/zero/SymbioteMatterV1.java` ~lines 724–755:
```java
float stiffness=clip(cortex[sector*OUTPUTS+3],-1,1)*.5f+.5f;
fx[i]+=(skin.x[p]+skin.x[n]-2*skin.x[i])*(85f+stiffness*190f);
fy[i]+=(skin.y[p]+skin.y[n]-2*skin.y[i])*(85f+stiffness*190f);
```
Since stiffness differs between neighboring sectors by design, spring i-j's force contribution from i's K_i differs from that on j with K_j. The sum of purely internal elastic forces **need not be zero**: `sum_i K_i * (p_(i-1)+p_(i+1)-2p_i)`, which is not identically zero for nonuniform `K_i`. The shell receives a spurious center-of-mass force not caused by substrate traction, external obstacle, or voluntarily generated paired muscle-fluid reaction.

**Reproduction using exact user's authentic physical shape**: `zero-telemetry/reports/20261024-103000-33e93646acead/part-002.json` `currentZero.training_room_1_scene_v379` skin.x/y and 16 valid varying stiffness values `K[k]=85+190*(.5+.42*sin(1.7k+.3))`; calculate without integration:
- Original node-local spring force: net **(-809.908, +356.989)**, magnitude **885.095 physical solver force units**.
- Equal/opposite edge-paired spring implementation `K_edge=(K_i+K_j)/2` contributes +F to node i and -F to node j; net exactly 0 on the same shape.
- IMPORTANT: 885.095 is a **controlled example** with mathematically valid varying sector stiffness, NOT a measured FORCE from the original user's precise neural output at the screenshot instant. It proves the implementation can create artificial propulsion. Do not misreport magnitude as measured live.

**Required correction**: compute all spring forces from SAME frozen PRE positions. Per spring edge k=(K_i+K_j)/2, apply equal and opposite forces, with valid mass weighting and damping only where physical substrate permits. Preserve actual neural CH3 stiffness, do not replace policy, and include force sum invariant (abs ≤ floating point tolerance) with random heterogeneous stiffness and concave contact-ready shape.

## Source-proven physics flaw #2: sequential overwrite creates mixed-timestamp forces even for uniform stiffness
`SymbioteMatterV1.step()` `for(i=0;i<SURFACE;i++)` reads neighbor `skin.x[p]` and `skin.x[n]` and `actuatorDirectionAt(skin,...)`, **then immediately updates `skin.x[i],skin.y[i]`** inside that same loop, before processing i+1. So subsequent membrane points see an already-updated previous neighbor but still-old next neighbor. The result depends on iteration order, a nonphysical hidden steering/force perturbation, and contradicts the comments implying pairwise forces are equal/opposite. Frozen PRE positions or a two-pass integration are necessary.

**Controlled reproduction** on the same S21 training scene: with identical `K=180` at every site (which SHOULD conserve net internal momentum), zero initial shell velocity, simplified 60Hz drag factor exp(-10dt) and sequential Euler `x[i]+=K*(x[p]+x[n]-2*x[i])*dt^2*drag`, the aggregated internal spring forces become **(-1.722,+4.959), magnitude 5.250**. Using the single unchanged PRE geometry for all 96 computations yields (0,0). This isolates *in-place iteration bias*, even when stiffness is uniform. Values are controlled numeric reproduction, not run-time drift estimates.

Normal direction from `actuatorDirectionAt(skin,i,...)` also reads `skin.x[prev/next]` during that in-place mutation, changing how the SAME neural command projects into world coordinates depending on sequence. Fix all geometry sampling consistently (shell normal, spring and proprioceptive PRE) before integrating positions. Keep measured POST actual forces and velocity physically correct.

## Source-confirmed proprioceptive weakness #3: no local tangential velocity feedback
`app/src/main/java/com/overmind/zero/Zero3LearningFoundation.java` ~lines 384–425 `AdaptiveCortex.command` computes:
- local radial relative velocity only: `rv+=((s.vx[i]-b.cvx)*xx+(s.vy[i]-b.cvy)*yy)/rr`;
- physical feature `features[4]=limit(rv/per/100f,-1f,1f)`;
- **does not observe local tangential velocity** while independently commanding a CH1 tangential muscle action. Eight fixed local features are all occupied; do not replace meanings in existing pretrained 512 cortex weights or GRU checkpoint casually.

Measured in user's exact S21 saved scene: across 16 sectors, mean absolute radial velocity **8.882 units/s**, mean absolute tangential **5.301 units/s**; **5/16 sectors** have |tangential| > |radial|, but all 16 tangential components are absent from direct cortex proprioception. Normalized radial feature mean abs **0.0888**. An additional sensor path can expose tangential motion without rewriting trained 194 GRU input schema. Distinguish this well-founded limitation from a proven causal explanation of ALL trajectory errors.

## Actual autonomous long run on current V387 code, not an invented success
- GitHub Actions [40144511174](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40144511174), commit `cf8f1dc7a016565ad9bb11351873c5fb963f4573`: **FAILED** original Android 2000 unscripted decisions. Test reports `decisions=2000 blue=4 red=12 signed=8.74027 gross=1267.21928 efficiency=0.0069 actualX=+563.0680 actualY=+422.1162`. This is **0.69%** intent-aligned movement efficiency, cannot call V387 reliable.
- Earlier run [40144080551](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40144080551): **FAILED**, 2001 decisions and 3.99% alignment, blue=3 red=10. Not necessarily deterministic A/B, do not claim a precise regression ratio due to uncontrolled conditions.
- Android component tests [40144115260](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40144115260) pass but do not override failed long-run behavior.

## Next verifiable tests before another build
1. Exact Java old-vs-balanced shell net elastic force on the same user body, mixed stiffness incl. asymmetric wall contact. Ensure net internal force cancels and one-step index/order invariance. Do not manually cancel force with fake centroid thrust, which would mask physics.
2. Frozen-PRE normals + edge spring pairs, *same checkpoint* blind A/B 2k intentions; compare signed COM displacement / gross, local tangent-vs-radial velocity, motor energy, soft-tissue area, contacts/harm, realistic grip and reversals, 20x.
3. If tests improve, integrate only proven physical solver correction into a new review branch, then Android cold resume and signed experimental preview. **No release/OTA yet for V388**.

## Additional verification
`ResetDomains.MOTOR` currently does not list `Zero381MotorCalibration.DIRECT_KEY_V387` or `Zero381MotorCalibration.KEY`; scoped reset of MOTOR may leave both old and new motor response archives. Audit UI/reset semantics before modifications so no unrelated neural checkpoint is erased. This is a compatibility defect separate from actual locomotion.

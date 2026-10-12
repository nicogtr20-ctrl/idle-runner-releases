# ZERO 0126 — One confirmed RED point to move construction panels

## User observation
OTA 95 panels were moving without red LEDs because panel forces were driven
by incoming material momentum or target-motion penetration alone, regardless of
adhesion. The rule is now: ZERO's single confirmed local red grip moves a
construction panel; ordinary contact/pressure never moves one.

## Mechanics
- Applies ONLY to law 6 construction panels, NEVER the historic bilateral
  energy-cover square or the two-sided room gate.
- Each panel owns an independent 96-point grip timer. A timer increments only
  if the corresponding real tissue point touches the physically rotated panel
  and that tissue point intentionally commands local grip.
- A contact becomes physically HELD after the existing GRIP_HOLD threshold.
  Only such points may apply material impulses/torque; one suffices.
- GripSurfaceComposition reuses the exact same panel-owned timers to drive the
  visible red bulbs. Pressure/touch may glow cyan, but do NOT move the plate.
- Internal masses still collide, but their penetration correction cannot
  independently translate or rotate the panel.
- Last-red release resets plate translation and rotation velocity; restored
  non-gripped material cannot keep its previous spinning momentum.
- Collision projection protects ungripped panels from displacement by other
  solids, except intentional prevention of invalid penetrations in the
  surrounding movable solid.

## Persistence
- Existing 96-float per-object boxGripHold array is now written for law 6
  panels as well as law 5 boxes in physical scene snapshots.
- Earlier 0124 scenes with no panel timers resume with empty grip timers.
  Long-lived learned motor/body weights, sensory input, exact position and
  angular orientation are preserved. Distant archive timers are ephemeral
  and must be physically regained if a panel reappears.

## Regression contracts
1. Ordinary collision without red = zero impulse, zero spin, zero grant.
2. Exactly one red point = real translation, off-center torque and red bulb.
3. Dropped adhesion = black point and no continued movement.
4. No adhesion cross-talk from other boxes or the gate.
5. No unintended changes to shelter protection, scene saves, physics,
   visual effects, cognition or telemetry.
6. Run old pure Java regressions, Android compile and signed OTA hashes.

This change is a mechanics correction, not a claim that ZERO has learned
to build shelters.

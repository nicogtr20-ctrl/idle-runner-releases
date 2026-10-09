# ZERO-009.2 — In-vivo adaptation and longer survival
This staging branch is based on feature/overmind-inapp-updater (v0.9.1).
Changes:
- All 3 legacy controllers (motor, manipulation, membrane with adhesion) can improve again by trying small candidate weight changes in the very same running game world; no off-screen target-directed training.
- Each domain rotates between eight live experiences using saved weights and eight experiences using a tentative mutated candidate. Candidate only becomes persisted when reward/movement/contact improves in approximately comparable energy/room/direction contexts. Otherwise prior weights remain. This is **not** a controlled A/B and can still be confounded.
- Forward world model continues online training independently; all old memories are retained.
- Slower energy loss, smaller red stimulus damage; more energy at life start. Real death still respawns with learned weights intact.
- Room excludes all ordinary stimuli on activation, during spawning and when mobile; only new violet/gold exclusive +80 resource is permitted inside. Its sensory signature is a combination of two wavelengths in preexisting 26 inputs: no hard-coded knowledge of its benefit. Resource stays occluded behind walls until line of sight.
- New real Galaxy S21 gameplay evidence required before production signed OTA. CI physics/migration tests do not prove successful learned grasp.

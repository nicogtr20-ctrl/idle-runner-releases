# ZERO OTA157 — UI audit: one actual 2.2 brain everywhere
Date: 15 October 2026. Base OTA156 signed and live verified (run 38740538017 SUCCESS).

## Scope
Audited game HUD / bottom dock / settings, advanced physical details, BrainToolsActivity "Cervello/Analisi", BrainStatsActivity "Parametri", StatisticsActivity, TelemetryActivity, lab entry points, and ResetDomains against living source `GameActivity`, `RecurrentCoreV22`, `SymbioteMotorHeadV1`, `TemporalWorkingMemory`, `TemporalSkillBank`, `SpatialPerception`, `CausalSequenceV23`. UI must report **only data of actual 2.2 live cortex** or clearly label isolated historic experiments. Read-only displays never update brain state.

## Defects verified and changes
- **Game settings and advanced HUD** previously showed action as 1/8 although there are 16 policies, reported old `handTrainer.attempts`, `shapeTrainer.gateAttempts`, `episodic.replayed`, `gripBank.count` as if active on the new Symbiote; updated to 1/16 + heading 1/8, real recurrent transitions, true membrane 96/74 physical dimensions, actual contacts and actual 2.2 temporal skills. Renamed menu destinations and accurately specified isolated historical laboratory.
- **BrainToolsActivity** formerly ~662 lines of mixed old (0.9.x, 2.0 and 2.1) statistics and 2.2, including a displayed "unified 2.1 brain" that OTA156 retired. Replaced main diagnostics surface with compact accurate 2.2 core, trained 84-parameter cortex, independent body confidence channels, spatial/cause/navigation, anonymous object tracking and temporal goals. Physical grip/cancel metrics remain read-only. Names from older versions are *file history*, not independent controllers.
- **BrainStatsActivity** historically presented `DecisionBrain` old 7-input network growth and the obsolete 62-motor / 18-hand / 82-membrane DNA as the current neural strength. Replaced with real 2.2-only parameter browser (40,560 trainable scalars across one 16-action 164+24 reservoir), and the real 84 trained muscle weights. Model exposes read-only `parameterCount`/`parameterAt`; paginated 12 at a time for bounded mobile rendering. Only loaded checkpoint is considered valid. Other active sensor model sizes are labeled explicitly. No pseudo "intelligence level".
- **StatisticsActivity** measured simulations correctly but called short-term average "current ability" and 25-vs-25 comparison "evolution". Renamed observationally; exclude the retired legacy episodic recall measure from both the visible lists and trend score under living Symbiote. Do not erase historical physical measurements or change reward/stat capture.
- **TelemetryActivity** falsely called ZERO 2.0 the passive observer and the "original brain" controller. Text now explains live 2.2 + Symbiote motor, explicit private user-triggered export only. The old `zero20Passive` metadata from historical saved fields remains labeled retrospective, never an active alternative brain.
- **Reset buttons** now expose 2.2 core, cortex, temporal skills, body senses, spatial / object continuity, causality, navigation, goal continuity and nonlearning gate metrics, with precise destructive warnings. ResetDomains implementation untouched; deprecated old-brain reset buttons are hidden to prevent confusing a legacy genome with trained Symbiote cortex. Existing confirm dialog, safe scheduling and app signature unchanged.
- **Labs** are explicitly labeled isolated/historical test fixtures; lab results cannot be confused with on-device Symbiote gains.

## Hard gates and safeguards
`Zero157DashboardRegression` checks that every 2.2/core and real cortex parameter is finitely readable, read-only (serialization unchanged), has consistent group boundaries and throws for invalid indices. Both corrective CI and signed publisher invoke it. Both verify menu links, live model choice, no obsolete V2.0/2.1 representation, reset paths, stats terminology and private telemetry; all revised exact-source SHA pins remain fail-closed. Full JVM tests and Android release compilation must pass before official OTA157 signing. No physics change, learned weight reset, forced gait, session upload, or automatic loss of user-saved 2.2 brains.

## Interpreting the displays
Numbers and parameter counts reflect software architecture and learned weights, not proof of intelligence. Confidence is a heldout prediction gate; real on-device evidence of improved behavior still requires net displacement, energy approached after seeing it, contact timing, and repeated outcomes on the same living ZERO.

## Delivery
Pre-release test, official signed publish and external live HTTP/APK SHA proof are recorded in the release handoff after they pass; do not infer deployment from this design report.

## Signed rollout and proof — 2026-10-15
PR #64 merged `94b8270f356cc2a3e8655db0371ba1308cdcc5af`.
- Final corrective QA: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38744663405 **SUCCESS** (full historical Java physics tests, new read-only parameter inspection regression, UI source invariants and Android compilation).
- Signed publisher: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38744968728 **SUCCESS**. Package `com.overmind.zero.ota`, version `0.11.6-ota.157`, versionCode `1157`; signed APK 421704 bytes, SHA256 `80601672deed24899f560971675f6d2d7dc2c9f8c4ebb98e9dd4169770227218`.
- Vercel production deployment `dpl_Dvnq3CXLKC9XNj4AyFLyhPmaNqgP` READY on release main commit `91352c079229ef9e644ca74924dd36efc8beed11`.
- Independent public proof https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38745386123 **SUCCESS**: manifest HTTP 200, APK HTTP 200, valid version, MIME, package, 421704-byte length and matching SHA256.
- Install with existing app's **AGGIORNA**, do not uninstall or reset learned 2.2. Read-only UI coherence and compile success do not prove learned locomotion quality.

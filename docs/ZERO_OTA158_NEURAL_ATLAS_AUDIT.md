# ZERO OTA158 — Neural Atlas, skills and evolution restored and redesigned

Date: 2026-10-15. Base: signed OTA157. User requested real game-style 3D neural menus rather than merely removing the old Abilità/Evoluzione tabs, with nested Cerebello/Corpo/Percezione and every active submodule represented.

## Navigation contract: six tappable home tiles and every tested child screen
The bottom **Cervello** control (both ordinary and Settings-expanded touch cases) now launches `BrainStatsActivity`, the atlas hub. Animated lightweight 3D orbital neurons, dark grid, teal/violet/blue/gold neon cards, compact 2-column game-style navigation. No downloaded assets, graphic that falsely implies numbers of real neurons, or heavy inference GPU use. App UI is native Android, not a static mockup.

| Atlas section | Accessible modules | Actual persisted evidence |
| --- | --- | --- |
| **Cervello** | Nucleo 2.2, memoria temporale, esperimenti causali, sequenze causali, continuità degli obiettivi | F164/H24/A16 active `RecurrentCoreV22`; transitions, trainSteps, heldout, trusted actions and temporal confidence. No retired 2.0/2.1 arbiter. |
| **Corpo** | Corteccia motoria, propriocezione, membrana distribuita, resistenza, interocezione, tatto/prese, **vista dalla membrana** | 84 actual saved motor weights; 16 sectors, 96 surface nodes, 64 inner fluid nodes. Each sensory model's real saved transitions, heldout validation, choice influences and confidence; actual gate contact frames. |
| **Percezione** | Vista ottica, navigazione, mappa spaziale, continuità oggetti | 26D primary retinal origin + optical rays/8-sector signals, genuinely verified 16 action effects, remembered cells and physical object associations; no magic vision or hidden entities. Vision instantaneous world frame is deliberately *not* persisted. |
| **Abilità** | Up to 24 per-skill rows + drilldown, confirmed action sequence A1–A16, confirmations, failures, completions, best confidence; motor competence, goal and gate evidence | `TemporalSkillBank` new bound-checked, read-only per-index accessors; independent `TemporalWorkingMemory` trust and verified physical navigation. No old hand/skin mutation attempts passed off as current ability. |
| **Evoluzione** | Nucleus learning, cortex learning, independent validation, 25 recent vs previous 25 completed lives for survival, energy, net movement, disks/objects | Actual saved lifetime core `transitions/trainSteps/replays/heldout/decisionCount`, motor `decisionCount/updates/heldout` + `LifeStatistics`. No fake neural-node growth; where historical window is missing it explicitly says no comparison possible. |
| **Parametri** | Paged true 2.2 core weight groups and 84 muscle weights, exact sensor/inner-fluid model dimensions | Read-only `parameterAt`/`parameterCount` on *persisted* models. 12 per page, stable confidence semantics. No 7-input old brain, or 62/18/82 obsolete motor gene arrays. |

Also **Analisi completa** still opens `BrainToolsActivity` for advanced introspection and confirm-gated resets; **Statistiche vite** opens `StatisticsActivity`. Back button reliably goes module → group → home → game; no inert controls. All values come from last saved `SaveSlotStore.WORKING` and are labeled as **not live per-frame**. Decorative neural orbit ≠ count of neurons; deliberately light ~11fps and screen-visible only.

## Verification criteria
- New regression `Zero158EvolutionRegression`: exact fixed fluid model weight dimensions, true virgin counters, cortex and brain serialize/restore, no per-skill false positives, bounds-checked immutable skill vectors, no mutation of learned weights.
- Existing `Zero157DashboardRegression` remains; all full JVM/physical/body regressions continue.
- QA/publisher source audits verify each six section entries and eighteen functional submodule IDs plus individually inspected skill, real read-only weights, actual 25/25 comparisons, both bottom Cervello entry routes, and no obsolete controller in menu.
- Exact SHA pins for all changed sources and new regression preserved in signed publisher (including `TemporalSkillBank`).
- Android debug/release compilation, signed publisher, public HTTP manifest/APK SHA verification must all succeed before delivering OTA158.
- This is visualization and read-only API only: NO movement physics, recurrent model learning algorithm, lived reward, saved keys, motor commands, resets or trained weights altered. User remains on the same ZERO. UI/tests are not proof of on-device behavioral intelligence.

## State
Implementation committed on `fix/zero158-restore-real-skills-evolution-tabs`. Official release record should be appended only after final CI, signing and public URL validation.

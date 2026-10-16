# OVERMIND ZERO Android — Contratto agente per modifiche ad app/

**Per modificare QUALSIASI file sotto app/** leggere anzitutto la SPECIFICA CANONICA su \`main\`:
- \`docs/ZERO30_MASTER_BLUEPRINT.md\`
- \`docs/ZERO30_IMPLEMENTATION_CONTRACT.md\`
- \`docs/ZERO30_UI_UX_SPEC.md\`

Poi leggere \`AGENTS.md\`, \`OVERMIND_START_HERE.md\` e il vero codice/branch sorgente. L'attuale implementazione attiva è 2.2/OTA172 alla data della progettazione; il V3 NON è implementato.

**Stop critici:**
- No gait/attraction target hardcoded o moto artificiale del centro; corpo, collisioni, sensori e ricompense solo reali.
- No cancellazione dati, brain preesistenti o 12 slot, no modifica firma/appId/OTA senza verifica.
- Non accumulare altra logica neurale indipendente in \`GameActivity\`: usare coordinator e API tipizzate come progettato.
- Allineamento esatto dati tra policy, actuator e fisica; separazione train/heldout/replay.
- Non creare menu «3.0» che puntano alle metriche 2.2: nuova UI da provider read-only V3 per neuroni reali, skill, evoluzione, corpo, impostazioni.
- QA, benchmark e compatibilità prima di dire “funziona”, anche con test verdi.

Per lavorazioni di sola documentazione, **non avviare publisher OTA** né modificare app code. Un push sul branch sorgente dedicato può triggerare signed publisher.

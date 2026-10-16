# OVERMIND ZERO 3.0 — Istruzioni per assistenti di sviluppo

Per OGNI task che modifichi il progetto OVERMIND ZERO (sorgente, simulazione, cervello, fisica, allenamento, UI, salvaggi, OTA, benchmark, documenti):

1. **LEGGERE PER INTERO, PRIMA DI MODIFICARE**, su **main**:
   - [ZERO 3.0 MASTER BLUEPRINT](../docs/ZERO30_MASTER_BLUEPRINT.md)
   - [ZERO 3.0 IMPLEMENTATION CONTRACT](../docs/ZERO30_IMPLEMENTATION_CONTRACT.md)
   - [ZERO 3.0 UI/UX SPEC](../docs/ZERO30_UI_UX_SPEC.md)
2. Leggere il [README](../README.md), [AGENTS.md](../AGENTS.md), [OVERMIND_START_HERE](../OVERMIND_START_HERE.md) e audit storico pertinente.
3. Questo è un *progetto NON implementato*; non descrivere funzionalità V3 solo immaginate come presenti. Per ogni PR specificare gate, prove reali e limite.
4. Esigere unico decisore, stato condiviso, cortex learnable, causal credit univoco, heldout separato, benchmark fisica/autonomia. Nessuna andatura/script verso gemme, premio fasullo, falso neurone, perdita del cervello precedente.
5. **LA NUOVA UI ZERO 3.0 È OBBLIGATORIA**: impostazioni, neuroni realmente collegati al brain 3.0, skills, evoluzione, corpo, memoria, diagnostica e OTA. Per profili V3 sostituisce UI 2.2; niente retitling del vecchio codice.
6. Preservare 12 slot, profili, applicationId, certificato e updater. Prima di push al ramo sorgente stabile verificare workflow OTA (un push può rilasciare APK).
7. Scrivere il progresso nel contratto di implementazione e la motivazione di ogni deviazione significativa. Non confondere test sintetici, benchmark autonomi e telemetria Galaxy S21.
8. Se si opera su un ramo storico che non contiene questi file, **leggerli sempre da main** tramite GitHub, anziché assumere istruzioni vecchie.

Nessuna chat ChatGPT/Copilot può essere tecnicamente obbligata a consultare GitHub da un semplice file; l'operatore deve aprire i documenti esplicitamente. La repository homepage e AGENTS.md sono puntatori permanenti.

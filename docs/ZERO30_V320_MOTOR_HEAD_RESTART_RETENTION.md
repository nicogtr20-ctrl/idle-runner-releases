# ZERO 3.0 — V3.20: checkpoint del motoneurone e ripresa deterministica

**Protocollo congelato prima di codice, test e risultati. Data: 17/10/2026.** Contratti canonici: [Master](ZERO30_MASTER_BLUEPRINT.md), [Implementation](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.19](ZERO30_V319_CONTEXTUAL_GRIP_ENERGY.md).

## Quesito e scope

V3.19 ha ottenuto un **PASS scientifico circoscritto** sull'adesione contestuale, con 6.900 frame di presa su BOX e meno lavoro totale. Mancano prova di conservazione della competenza dopo restart, backup verificabile del motoneurone, e protezione delle sue variabili nascoste. **V3.20 riguarda esclusivamente checkpoint e ripresa del motor-head V3.19**, non un `BrainSnapshotV3` integrale: actor ricorrente V3.17, cortex altri 48 muscoli, esperienza replay V3.16, corpo/gel/mondo e UI **non** sono contenuti nel checkpoint.

Checkpoint solo ai confini delle decisioni completate, fra vite/episodi nella fixture. Se una decisione è in corso: fallire esplicitamente, non salvare un esperimento inesatto. Salvare: versione schema e firma architetturale, 128 pesi (16×8), flag training, PRNG 64 bit, numero updates/crediti, accumulatore norma aggiornamenti, lavoro adesivo contabilizzato e lavoro totale osservato. I vettori temporanei intra-decisione sono rigenerati da `begin`; il salvataggio non può avvenire mentre sono validi. Tutto finite/bounds/checksum; nessun numero futuro, oggetto segreto o `physicalLaw`.

Persistenza isolata da ogni profilo del gioco: generazioni alternate slot0/slot1, file temporaneo+fsync+rename atomico richiesto, manifest dell'ultima e precedente generazione con SHA-256 e numero generazione; loader accetta solo coppie consistenti e payload con architettura/versione valide, recovery alla precedente **convalidata** dopo corruzione della nuova. Checkpoint rollback deve poter essere salvato di nuovo senza spazzare via l'unica copia valida. `fsync` directory best effort: non chiamare questa implementazione garanzia Android power-loss.

## Prove preregistrate

Quattro fasi CI in **processi Java separati**:

1. `prefix`: nuovo motoneurone V3.19 seed fissato già nel laboratorio 0x31920261017, **16 episodi TRAIN × 96 = 1.536 decisioni selezionate dal vero actor**; checkpoint dopo episodi 8 e 16, generazioni 1 e 2. Il training usa esclusivamente `Zero3ContextAdhesionV319Regression.life` col vero solver del gel/collisioni. Persistenza integrale dello stato dopo 16 vite.
2. `resume`: nuovo processo legge la generazione 2, carica un oggetto V3.19 nuovo, allenamento episodi 16..31 (**ulteriori 1.536 scelte e 18.432 passi fisici**), checkpoint generazione 3. Scrive fingerprint binario dei pesi/stato finale, senza numeri sintetici.
3. `reference`: nuovo processo crea un motoneurone V3.19 dal seed originale e allena ininterrottamente gli stessi 32 episodi/3.072 decisioni/36.864 passi gel. Il fingerprint finale deve essere **identico bit-per-bit** a quello del processo `resume`. Se differisce anche un singolo bit: **gate FAIL**, niente claim di retention.
4. `corrupt`: tronca/danneggia l'ultimo slot su disco e prova rollback alla seconda generazione esattamente a **1.536 update**, poi salva solo lo stato recuperato; se anche il manifest è danneggiato, accetta soltanto il precedente blob SHA-256 verificato. Corrompendo tutti gli slot, fail closed senza inventare uno stato migliore.

In `resume` e `reference`, valutare ciascun head **congelato**, senza nuove perturbazioni o gradienti, sugli **stessi 24 episodi di valutazione indipendenti dalla V3.19**, usando ordinal 24..47 e pose/condizioni/gap corrispondenti; sei episodi BOX. Non guardare le metriche per regolare il campione. Confrontare bitwise tutti i parametri e le metriche/trace fisiche prodotte da test separati. Richiedere almeno una presa legale BOX come controllo di retention, e **zero** false prese FREE/DISK/FAR; riportare veri frame con presa generica, bilaterale, `gel.work`, effort e duty. Questo non è prova della persistenza di una *vita fisica* attraverso crash, ma soltanto capacità di ricreare la policy alla decision boundary fra vite.

**Gate V3.20 checkpoint del motoneurone PASS** solo se i quattro processi sono completati, il fingerprint ripreso combacia *bit a bit* col training ininterrotto, le metriche fisiche dei due test congelati sono identiche, vi sono prese BOX realmente osservate dopo il ripristino, tutti i test contro duplicati/snapshot in corso/versione errata/corruzione falliscono correttamente e non viene scritto alcun profilo legacy. Se nessun contatto significativo nelle nuove scene o nessuna vera presa, esito **FAIL scientifico retention**, senza creare un nuovo blind ad hoc.

**Non-gate:** G3 locomozione, G4 completa actor-critic, G5 world model, G6 memoria procedurale, G7 migrazione/brain snapshot, G8 UI e G9/G10 APK restano **BLOCKED** anche in caso di PASS; prove Android Galaxy S21 e true same-life resume restano mancanti.

## Esiti

*Da inserire solo dopo esecuzione e lettura GitHub Actions.*
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

Dati CI effettivamente verificati nel rapporto seguente.

## RISULTATO VERIFICATO — CI 39018773241 (17/10/2026)

**[GitHub Actions 39018773241](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39018773241) — SUCCESS; gate V3.20 motor-head-only restart PASS.** Job 117356200863, tutti i quattro processi Java completati. Codice `Zero3MotorHeadStoreV320.java` e `Zero3MotorHeadRestartV320Regression.java`, workflow `.github/workflows/zero30-v320-neural-restart-qa.yml`, sul ramo sperimentale Draft PR #87. Riuso della vera `Zero3ContextAdhesionV319` e dei solver `SymbioteMatterV1`, `SquareGripPuzzle` e `Morphology` non modificati.

### Prove effettivamente superate

| Fase | Evidenza osservata |
|---|---|
| Process 1 PREFIX | 16 vite vere, **1.536 decisioni**, **18.432 passi gel nelle decisioni**. Due checkpoint integri con generazione 1 dopo 768 decisioni e gen 2 dopo 1.536; test di difesa copia pesi e snapshot proibito durante decisione |
| Process 2 RESUME | Nuovo processo e nuovo oggetto motoneurone, recupero **gen 2, 1.536 update**, poi altre **1.536 decisioni e 18.432 passi gel** reali; salvataggio **gen 3, 3.072 updates** |
| Process 3 REFERENCE | Processo indipendente allena per **3.072 decisioni consecutive**; fingerprint SHA-256 dello stato completo **identico** al ramo ripreso |
| Prova fisica EVAL congelata | Entrambi i rami eseguono **24 episodi nuovi, 2.304 decisioni e 27.648 passi gel per ramo**, e producono un file di evidenza **identico byte per byte**, includendo i risultati di presa/lavoro |
| Test di corruzione | Danneggiando il checkpoint gen 3 si recupera la **gen 2 con 1.536 update**, mai uno stato parzialmente inventato. Nuovo checkpoint dopo rollback conserva esattamente 1.536 update; manifest primario corrotto ricade su backup verificato; entrambe le generazioni invalide ⇒ **fail closed** |
| Codice e compatibilità | Due slot alternativi su directory temporanea di CI, **SHA-256 del payload e del manifest**, `FileDescriptor.sync` e rename `ATOMIC_MOVE`; nessuna scrittura dei 12 profili legacy/OTA |
| Training continuity | Stato persistito include 128 pesi (16×8), modalità training, stato PRNG 64 bit, numero decisioni/crediti, accumulatore updates e contributi lavoro fisico reale; resuming at decision boundary, non nel mezzo |

**Fingerprint finale condiviso** (SHA-256 del solo payload motor-head V320): `45a07e5f6f3a1c50d989c3a50892bb27206a5645e591d0012442981cb0be4f34`. Il confronto file `cmp` ha verificato effettivamente l'identità di tutta l'evidenza tra RESUME e REFERENCE, non solo una stampa che dichiara parità.

**Retenuta effettiva su ulteriori nuove pose/geometrie di laboratorio**: 24 episodi eval con **2.304 scelte motorie**, **27.648 veri passi gel**, **4.987 frame BOX con tocco**, **4.778 frame BOX con vera presa**, **5/6 episodi BOX con grip legale**, **90 frame bilaterali**, **2,828716% di comandi adesivi oltre soglia fuori BOX**, lavoro BOX **0,176780806** e totale **0,523866168**. Le metriche coincidono esattamente con il modello addestrato senza interrompersi. Gli ordinali 24..47 con flag valutazione creano nuove pose/gap rispetto ai train, **ma usano le stesse famiglie FREE/BOX/DISK/FAR**: non è generalizzazione a nuove topologie.

### Limiti da preservare per ogni futuro sviluppatore

**PASS significa solo V3.20 neural motor-head recovery e prova di comportamento invariato dopo riavvio.** Non è ancora `BrainSnapshotV3` dell'organismo intero. NON sono salvati o restaurati il controller actor ricorrente V3.17, corteccia degli altri 48 canali, stato completo fluid body, ambiente/oggetti/contatti, timer di presa, contatori dell'Authority, stato semantico/skills o stato RNG del mondo. Il test riprende **fra episodi** con nuove vite simulate, non un frame interrompibile nel corso di una decisione o nel mezzo di una vita. La state immutabile è un primo sottocomponente da incorporare in un completo snapshot atomico multi-modulo.

Il recupero è verificato nel runner CI Linux con rename atomico richiesto; **non** provato su Android/Galaxy S21 o contro perdita improvvisa di alimentazione. Nessun modello 3.0 è ancora collegato al gameplay o alla nuova UI. Il vero G3 locomozione e il world model G5 restano **BLOCKED**; il degrado V3.19 su prese bilaterali (382 vs 1.617 frame V3.18 nel precedente BLIND) non è risolto semplicemente salvando meglio la testa motoria.

### Prossimi passi suggeriti in ordine

1. Contratto unico di `BrainSnapshotV3` che includa actor recurrent, motoneuroni, cortex, plasticità, RNG e aggiornamenti, e recovery coordinato con world-body/episodio/authority a limite di decisione.
2. Verifica di retention su nuove morfologie/oggetti e **presa bilaterale, rilascio volontario, vera manipolazione** mantenendo selettività/costo fisico.
3. Ripresa di G3 locomozione coordinata dei 64 muscoli, confronto reale con brain frozen e 2.000+ esperienze, poi valutazione su dispositivo, migrazione/UI e successivo OTA solo dopo i gate.

**Release immutata:** PR #87 Draft, zero merge, OTA **0.11.6-ota.172**, tutti i 12 cervelli 2.2 intatti.

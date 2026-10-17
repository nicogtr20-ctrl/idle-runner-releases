# ZERO 3.0 — V3.22/B | Record PRE/ACTION×12/POST completo e checkpoint atomico stesso organismo

**Protocollo e baseline fissati il 17 ottobre 2026 prima della CI conclusiva.** Questa fase resta **LAB sperimentale E2/E3** sul ramo `feature/zero30-foundation-g0-g3-20261016` della PR #87 Draft. **NESSUN OTA, NO MERGE**; 12 slot legacy e versione pubblicata OTA172 invariati.

## Prima di cambiare codice in una nuova chat

Leggere nell'ordine:

1. [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [Contratto tecnico](ZERO30_IMPLEMENTATION_CONTRACT.md), [Specifiche UI](ZERO30_UI_UX_SPEC.md).
2. [Rapporto V3.21 memoria neurale a tre moduli](ZERO30_V321_FULL_NEURAL_BUNDLE.md).
3. [V3.22/A — ripresa neuronale/fisica nella stessa vita](ZERO30_V322_SAME_LIFE_SNAPSHOT.md).
4. **Questo contratto V3.22/B**, i file sperimentali effettivi e i log completi CI, distinguendo build verde da validazione scientifica.

## Problema e ipotesi falsificabile

La V3.22/A salvava un solo organismo nell'arena BOX, ma conservava per le esperienze soltanto `decisionId`, hash PRE/POST, impronta efferenza aggregata e costi. Non era possibile ricostruire un singolo PRE e POST sensoriale, le 12 efferenze 64D intermedie o il lavoro per sottopasso. Inoltre `lastRngState` era un seed di fixture, non lo **stato di un RNG consumato davvero dal mondo dopo il checkpoint**.

V3.22/B deve mostrare che in uno **stesso file transazionale** sono recuperabili **tutti** questi dati e il vero prossimo stato del generatore casuale del mondo di prova. Deve farlo non solo per BOX ma per FREE/BOX/DISK/FAR/BOX+DISK e più pose/seed. La ripresa a `decision=48` su altro processo JVM deve essere **bytewise uguale** alla vita da 96 decisioni mai interrotta, includendo le nuove nascite ambientali fra la decisione 48 e 96. Un mismatch anche su una feature sensoriale, sul corpo, sulla distribuzione di forza, sull'RNG o su una sola efferenza deve fallire il gate.

## Sorgenti nuovi (non toccano il solver di produzione)

- `Zero3FullCausalExperienceV322B.java`: payload immutabile di ogni singola decisione con PRE sensoriale locale 96×12, vista 16, propriocezione 48, interocezione 74×7, eventuali 64 comandi precedenti, metadata/tempo/ticks e POST nello stesso formato, **tutti i 12×64 comandi realmente inviati** al gel e 12 incrementi del vero `gel.work`. Identità, PRE/POST, durata solver, hash azioni, frame presa e bilaterali vengono verificati contro `Zero3OrganismStoreV322.Trace`; nessuna futura percezione nell'input preazione. I flags energia/danno sono **indisponibili come nella V315**, senza valori inventati.
- `Zero3FullArchiveStoreV322B.java`: record completi **nello stesso checkpoint** dei pesi encoder+actor/cortex/head, fisica `Body/Surface/Gel`, mondi `Entity`, cursore dell'intenzione, efferenza precedente e hash/cost trace già presenti in V3.22/A. Due generazioni con SHA-256 del blob, manifest SHA-256, `fsync` del file, `ATOMIC_MOVE`, recupero generazione precedente solo se integralmente verificata, fail closed se entrambe corrotte. Il replay non può appartenere a un episodio diverso né contenere decisioni duplicate o saltate.
- `Zero3MultiSceneRestartV322BRegression.java`: cinque famiglie di scene FREE, BOX vicino, DISK vicino, BOX distante, BOX+DISK. Ogni esperienza usa l'actor e cortex della V3.17, motoneuroni adesione e veri solver, senza impostazione di andamento/gait, forza diretta al COM, esito futuro o contatti fittizi. Il generatore xorshift proprio della **fixture V3.22/B** viene consumato per creare nuovi oggetti fisici alle decisioni 24 e 60 nei mondi con oggetti, e il suo stato finale è davvero salvato/ripreso. Confronto matched di piú semi e condizioni.
- `.github/workflows/zero30-v322b-full-replay-qa.yml`: processo JVM `prefix`, `resume`, `reference`, `corrupt` per ciascun caso, `cmp` sui file di prova, più build Android Debug senza publisher.

### Precisione sullo stato casuale

**La nuova implementazione salva il vero RNG *della fixture V3.22/B***, consumato per spawn reali prima e dopo il riavvio. Questo NON significa che sia già stato integrato o salvato il `java.util.Random` / scheduler dei nuovi habitat in `GameActivity`. Non attribuire a ZERO un RNG gameplay correttamente migrato senza integrare il relativo proprietario e dimostrare il ripristino su Android.

## Gates fissati prima dei risultati

- Ogni caso deve compilare e completare 48+48 decisioni nella medesima vita, con aggiornamenti encoder e head attribuibili a decisioni reali.
- L'esecuzione mai interrotta deve avere identico file dei risultati alla ripresa, compresa **impronta SHA-256 congiunta** del checkpoint con tutti i record sensoriali e motori. `fullSensoryActionRecords=96`.
- Sia con che senza oggetti, nessuna falsa presa può essere scritta a mano: `gripHold` autentico solo dove supportato dal proprietario BOX del mondo e relativo comando. Riportare espressamente zero o eventuali numeri reali senza trasformarli in skill.
- Un checkpoint con intera decisione PRE aperta non è permesso. Corruzione/troncamento ultima generazione deve riportare all'ultima completa senza inventare esperienze; due generazioni non valide → nessun restore.
- Compilazione Android Debug `BUILD SUCCESSFUL` senza distribuzione; lasciare G3 locomozione, G5 world model e release-gates aperti anche se tutto passa.

## Esito e limiti

**Esito conclusivo da inserire dopo lettura della CI** con ID run, SHA commit, matrici/scene e valori reali. La compilazione sola NON è un pass di V322/B.

Non sono integrati: energia/danno REALI di `GameActivity`, completo `BrainSnapshotV3`, checkpoint ogni tick fisico, esperienze in database Android, spawn/eventi dell'intera partita, release OTA, migrazione 2.2/slot, UI V3, neurogenesi, skill consolidation, 20+ seed se non verificati da CI, vero G3/G5. Questo è un passo circoscritto verso la continuità del corpo/cervello/esperienza, non una prova di miglioramento motorio.

## Ordine successivo

1. Integrare il mondo/scheduler/metabolismo reali tramite un adapter versionato senza scritture ai 12 slot legacy.
2. Test di stress crash/power loss e risposta a ripristino corpo+world+energia su vero Galaxy S21; più seed/scene nuovi su fisica identica, senza reciclaggio dei blind.
3. Validazione motoria E3/G3 su mondo nuovo e policy frozen vs train: net/gross, allineamento, costo, vera presa/rilascio e generalizzazione 20+ seed; niente gait prescritta.
4. Solo dopo world-model G5, neuroni/skill realmente appresi, nuova UI e futuri gates Android/OTA.

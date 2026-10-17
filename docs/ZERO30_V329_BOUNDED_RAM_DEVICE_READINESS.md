# ZERO 3.0 — V3.29 | RAM bounded historical replay + Galaxy S21 research readiness

**17 ottobre 2026 — SOLO PR #87, branch `feature/zero30-foundation-g0-g3-20261016`, DRAFT.** Prima leggere [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [contratto tecnico](ZERO30_IMPLEMENTATION_CONTRACT.md), [specifiche UI](ZERO30_UI_UX_SPEC.md), [V328 segmented long life](ZERO30_V328_SEGMENTED_LONG_LIFE.md). Non alterare i 12 cervelli 2.2, `GameActivity` stabile o la release OTA172; niente merge/pubblicazione.

## Problema reale

La V3.28 aveva dimostrato due vite da 2.000 decisioni neurofisiche in laboratori separati, ma la memoria era ancora linearmente proporzionale al numero di esperienze complete: `Zero3RuntimeV325.replay` e `Zero3SegmentedArchiveV328.Bundle` copiava i riferimenti di tutti i `Zero3FullCausalExperienceV322B` in strutture Java residenti anche **dopo** il commit dei segmenti su disco. Ogni esperienza contiene il frame sensoriale PRE completo, 12 vettori di 64 efferenze realmente eseguite, rispettivi costi misurati, il POST completo e prova della causalità: questa memoria non deve essere persa, ma non deve neppure occupare permanentemente tutta la RAM di Galaxy S21.

## Soluzione implementata senza migrazione di formato

- **`Zero3ReplayJournalV329.java`** nuovo `AbstractList<Zero3FullCausalExperienceV322B>` con un segmento storico decodificato di massimo **32 esperienze** in cache e una coda delle decisioni completate ma ancora non confermate nel manifest. `get(index)` legge su richiesta un chunk V3.28 già presente e verifica SHA-256, schema, lunghezza di ogni record e indice di ogni decisione; non accetta dati corrotti, riordinati o parziali. Il contenuto è **identico al precedente**, non riassunto o troncato.
- **`Zero3SegmentedArchiveV328.java`** carica, valida e scarta in streaming le copie temporanee dei segmenti; la struttura `Bundle` espone una vista immutabile di lunghezza fissa, non un `new ArrayList<>(esperienze)`. Il nuovo `openReplay()` offre il registro storico indicizzato per un archivio già verificato, evitando di rimaterializzare 2.000 record all'apertura.
- **`Zero3RuntimeV325.java`** conserva una `List` V3.29 anziché sempre una `ArrayList` integrale: una sessione nata vergine mantiene i record fino al primo checkpoint, poi `saveSegmented` li sostituisce con l'indice dei segmenti confermati su disco e libera i riferimenti ai vecchi frame. Le decisioni nuove si aggiungono solo alla coda. Dopo ogni riavvio `loadSegmented` ricostruisce neural+fisica+energia+room e un indice lazy; il vecchio `snapshot()/save()` V324 resta disponibile per i test legacy scientifici.
- La UI V3 di prova mostra **`REPLAY COMPLETO IN RAM`** e **`NON SALVATI`**, dati letti dal vero runtime e non contatori fittizi. I pulsanti x1/x5 e la navigazione 2.2 rimangono separati. Nessun'altra Activity viene pubblicata di nascosto.
- **Nessun cambio di layout dei file V328**, segmenti, hashes, manifest o semantica di rollback. La compatibilità con i checkpoint prodotti prima della V329 è conservata.

## Evidenza dai test

**[CI V328/V329 39033635925 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39033635925)**:
- Due episodi fisici reali **ordinal 1 e 2**, ciascuno **2.000 decisioni**: la sequenza 1.000 in processo JVM A, riapertura in JVM B e altre 1.000 ha esito **byte-per-byte identico** alla vita continua di JVM C per cerebello ricorrente, corpo gel 96/74, 64 efferenze, stato ambiente, energia, sala/porta e tutti i full replay.
- I test ora asseriscono `residentFullExperiences()<=32` immediatamente dopo ogni checkpoint da 128 decisioni e dopo `cold restore`, e non più di 160 durante la successiva coda di scelte non ancora persistite. Log: `V328 V329_BOUNDED_FULL_REPLAY_RESIDENCY=PASS` per **entrambe** le vite. Il valore massimo è per i record storici completi, NON per l'heap intero.
- Rollback dopo manifest corrotto alla decisione **1.920** per entrambi i casi; manomissione di un segmento reale, segmento orfano, doppia corruzione → tutti rilevati, nessun credito falso.
- `BUILD SUCCESSFUL in 1m 2s` Android Debug, senza firma release e senza OTA.

**VERIFICA FINALE SUL CODICE DELLA UI CONTEGGI RAM:** [CI 39033772568 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39033772568), SHA `a354c54cc0e0746a149e6f66c9dfdce0f8aab202`: 2 vite × 2.000 decisioni fisiche con assert sul massimo di 32 record completi in RAM dopo checkpoint e 160 durante le decisioni non ancora salvate, confronto bitwise a 1.000+restart+1.000, rollback a 1.920, alterazione segmento e manifest, doppia corruzione, Android Debug tutti PASS. La [CI V3.26 39033772570](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39033772570) e la [CI V3.27 39033772617](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39033772617) sono entrambe SUCCESS sullo **stesso commit di UI**; anche la precedente V3.25 [39033745520](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39033745520) è SUCCESS dopo la modifica del journal.

**Il gate `V329_BOUNDED_FULL_REPLAY_RESIDENCY_LAB` è SUPERATO con le regressioni V3.25–V3.28 verificate.** La UI Android è compilata ma **non è stata installata o collaudata sul Galaxy S21**; process death, storage pieno, risparmio heap/PSS dell'intero processo e latenza IO su dispositivo rimangono aperti.

## Preparazione della prima prova reale su Galaxy S21 (G9 — NON eseguita)

Quando sarà autorizzata una **build Debug V3 installabile non pubblicata come OTA**, eseguire in ambiente isolato, senza cambiare il cervello 2.2:

1. Registrare prima di aprire la modalità V3 la versione OTA installata, l'elenco dei 12 slot V2.2 e il loro stato; dopo prova, confrontarli: nessuna migrazione o sovrascrittura consentita.
2. Aprire da Impostazioni → LABORATORI → ZERO 3.0; creare una vita e verificare che il primo checkpoint sia durabile a una decisione completa, Canvas mostri davvero membrana e mondo e l'energia/il danno derivino dal metabolismo reale.
3. Eseguire checkpoint a 128, 512, 1.000, 2.000 decisioni, con misure separate di PSS/Java heap, tempo per decisione, latenza massima fsync, dimensione totale dei 63+ segmenti, temperatura e batteria. La UI da sola mostra il numero di record, **non** prova la riduzione dell'intero heap.
4. Mettere la app in background, forzare chiusura/process death e riaprirla tramite pulsante **RIPRENDI SOLO V3**. Verificare episodio, contatore decisioni, energia, numero record, hash dei segmenti e stato del corpo contro la generazione salvata; se manca il checkpoint, non inventare una vita vergine.
5. Durante un salvataggio, interrompere il processo in condizioni controllate: o si recupera la nuova generazione completamente verificata oppure quella precedente, **mai un ibrido**. Prove di storage pieno e app kill durante fsync richiedono Android Instrumentation reali; non sono state eseguite qui.
6. Misurare x1/x5 (la velocità nella UI è nominale), fluidità Canvas, ANR, out-of-memory e comportamento termico Galaxy S21; recuperare i log senza includere informazioni private degli slot.
7. Controllare infine che OTA172 e i 12 cervelli 2.2 siano identici a prima; qualsiasi perdita blocca ogni pubblicazione.

## Gate che rimangono obbligatori

**Superato finora SOLO `V329_BOUNDED_FULL_REPLAY_RESIDENCY_LAB`.** Non si è ancora verificata la memoria totale del telefono, né Android process-death, crash filesystem, effettiva capacità di imparare locomozione, comparazione trained/frozen su 20+ ambienti, world model, pieno ecosistema/shelter o UI neurale 3D definitiva. Il runtime conserva in RAM le **trace compatte, la storia metabolica e il cervello**, e la verifica dei manifest scansiona segmenti su disco: la latenza IO, il consumo di RAM totale e la scalabilità molto oltre 3.500 decisioni rimangono da misurare e ottimizzare.

**Prossimo intervento:** test Android instrumented reale e profilo heap/IO, quindi valutazione G3 dell'apprendimento motorio con confronto rigoroso di policy contro frozen/vergine. MAI dichiarare APK OTA V3 disponibile prima del relativo gate.

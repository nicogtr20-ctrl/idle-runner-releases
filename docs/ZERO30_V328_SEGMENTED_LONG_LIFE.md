# ZERO 3.0 — V3.28 | Journal causale segmentato e lunga continuità dell'organismo

**17 ottobre 2026.** Sviluppo ESCLUSIVAMENTE sul ramo sperimentale `feature/zero30-foundation-g0-g3-20261016`, PR #87 Draft, nessuna OTA. Leggere prima [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [specifiche UI](ZERO30_UI_UX_SPEC.md) e [report V3.27](ZERO30_V327_VITAL_LIFECYCLE.md). Non toccare i 12 slot cerebrali 2.2 né il publisher OTA 172.

## Problema materiale individuato

V3.27 ha dimostrato che il vero gel può realizzare 240 decisioni con cervello, corpo, energia e replay completo, ma `Zero3FullArchiveStoreV322B` e `Zero3RoomArchiveV324` salvano tutti i record PRE/12×64/POST in un unico blocco. Il decoder V322B rifiuta più di 256 esperienze, con un archivio limitato a 8 MiB. Il vecchio runtime fermava volontariamente la vita di laboratorio a 240 decisioni per non produrre apprendimento non salvabile.

La V3.28 introduce un **percorso di archiviazione nuovo e OPT-IN per V3**, mantenendo V324 disponibile per test preesistenti e per una migrazione specifica dei precedenti soli slot di ricerca V3; **mai** caricare o migrare gli slot della 2.2.

## Implementazione codice nel ramo sperimentale

### `Zero3SegmentedArchiveV328.java`

- Esperienze motorie reali divise in **segmenti di 32 decisioni**, ogni blocco contiene i byte immutabili di tutti i sensori PRE e POST, 12 vettori di 64 efferenze realmente attuate, 12 costi fisici, hash sensoriali, verifiche di tempi e stato delle prese. Nome dell'oggetto calcolato dal suo SHA-256, mai modificato dopo essere stato pubblicato.
- Il **manifest** atomico versione V328 contiene stato neuronale ricorrente, cortex 64 output, adesione, material body/surface/gel, oggetti del mondo, RNG effettivo dell'ambiente di prova, comandi precedenti, cursore causale di TUTTE le decisioni, storia metabolica completa e `ScarcityLab`/porta con i suoi latch, insieme a riferimenti ordinati e hash dei segmenti. Il replay non viene inglobato nel blob del corpo. Il manifest è protetto da un SHA-256 e viene pubblicato mediante file temporaneo sincronizzato e rinomina atomica.
- **Due manifest di generazioni indipendenti** permettono rollback su quella precedente integralmente verificata; se entrambi non possono essere convalidati, non si inventa un cervello vergine. Il loader verifica ogni segmento richiamato e associa ogni decisione, tempo e hash ai corrispondenti trace fisici.
- I segmenti già chiusi non vengono riscritti. Si ricrea solamente la porzione ancora parziale, deduplicando il contenuto immutabile e recuperando i file orfani soltanto dopo la pubblicazione delle generazioni valide. Le **decisioni attualmente effettuate rimangono disponibili anche come array in RAM**, quindi questa è **la prima scalabilità del disco**, NON ancora una soluzione streaming RAM illimitata. Configurazione prudenziale iniziale **3.500 decisioni** (metabolismo V323 ha limite 4.096); il limite resta dichiarato e controllato.

### Runtime Android isolato

`Zero3RuntimeV325.enableSegmented/saveSegmented/loadSegmented` preserva le vecchie prove e lo schema V324. `Zero3AndroidSessionV325` utilizza il formato V328 in `getNoBackupFilesDir()/zero3-research-v325/v3-isolated-v326`; se incontra un vecchio salvataggio sperimentale V324 lo può leggere e confermare come nuovo V328 **solo dopo un commit completo verificato**, senza cancellare automaticamente l'originale. Se trova file V328 esistenti e corrotti non torna silenziosamente al vecchio V324 e non crea una nuova vita. La UI sperimentale comunica il limite provvisorio e distingue arresto della capacità archivio da morte fisica.

### Verifica della vita lunga (E3 storage)

Il test `Zero3LongLifeV328Regression` usa il **vero runtime neurofisico**: 96 patch/74 masse, encoder ricorrente con segnali di energia V327, cortex con 64 efferenze, 12 sottopassi per decisione, mondo `UnknownWorld`, metabolismo fisico, stanza e replay completo. NON inventa esperienze né movimento del corpo. Il mondo di prova rifornisce occasionalmente **vere fonti blu `UnknownWorld.Entity(law=0)` posizionate su un punto di membrana** quando serve sostenere l'organismo: il guadagno energetico deve comunque provenire da una collisione fisica e dal normale metodo `Entity.touch`, non da scrittura diretta di `reserve`. Tale ambiente è intenzionalmente facilitato e **NON dimostra la capacità di sopravvivere o cercare energia autonomamente**.

Prove obbligatorie:
1. Vita unica con **2.000 decisioni realmente compiute** e rispettive 24.000 integrazioni gel per braccio, due processi indipendenti: A vive fino a 1.000 e salva; B riapre lo stesso cervello, porta a 2.000; C vive senza interruzioni fino a 2.000. Impronte neuronali, materiali, metaboliche, sensoriali e tutti gli attuatori devono essere **identici byte per byte**.
2. Le copie salve del manifest devono essere di **dimensione limitata (max 2 MiB)** e i segmenti indipendenti leggibili; controllare che blocchi già chiusi non siano modificati dalla successiva generazione.
3. Introduzione di un segmento orfano non pubblicato: ignorato dal loader e rimosso in sicurezza. Danneggiare una generazione → recupero della precedente; danneggiarle entrambe → fail closed, mai nuova vita o crediti inesistenti.
4. Ricompilare **l'intero APK Android Debug**, senza firmare, pubblicare, unire i rami o modificare OTA.
5. Ripetere e preservare la compatibilità degli smoke V3.25, V3.26 e V3.27, inclusa la separazione dei salvataggi legacy.

**Risultati definitivi: in attesa dell'esecuzione completa della CI dedicata.** Non dichiarare PASS da una compilazione sola, né da un'esecuzione che si ferma prima delle 2.000 decisioni o ricarica un nuovo cervello.

## Limiti che devono rimanere visibili

- Non è stata ancora eseguita alcuna prova vera di **process death, filesystem pieno, crash di Android, fsync directory o installazione sul Galaxy S21**. Il primo gate resta JVM/Android build, G9 non concluso.
- Non abbiamo dimostrato locomozione migliore dopo apprendimento rispetto a frozen/vergine, generalizzazione motorie su 20+ seed o abilità spontanee di presa. **G3, G4, G5 restano aperti.**
- **La memoria RAM di `Zero3RuntimeV325` contiene ancora una copia completa degli oggetti `Experience`**, e il manifesto duplica trace e log metabolico leggero; successive iterazioni dovranno ottimizzare streaming, garbage collection, memoria episodica selettiva e CPU/power budget per esperienze molto superiori a 3.500.
- L'interocezione energetica V327 è una fusione provvisoria negli input ricorrenti esistenti; non una rete homeostatica completa. L'ecosistema/shelter/vento e l'interfaccia 3D neurale finale non sono ancora connessi.

**Prossima fase dopo PASS:** estendere il test su più episodi/seed non usati in sviluppo, verificare Android reale, quindi G3 motricità senza moti prescritti, mantenendo PR Draft e niente OTA finché i gate non saranno tutti superati.

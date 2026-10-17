# ZERO 3.0 — V3.26 | Prima schermata Android V3 opt-in e nascita fisica autonoma

**Data 17 ottobre 2026 — IMPLEMENTAZIONE SPERIMENTALE PR #87 DRAFT. NON PUBBLICATA SU OTA.** Leggere prima [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI V3](ZERO30_UI_UX_SPEC.md), [V3.25](ZERO30_V325_RUNTIME_BOUNDARY.md) e i file sorgenti sul branch `feature/zero30-foundation-g0-g3-20261016`.

## Obiettivo e decisioni di sicurezza

Aggiungere un *vero ingresso Android* per il runtime neurofisico 3.0 già verificato su JVM in V3.25, senza rinominare il cervello 2.2, sostituire `GameActivity`, caricare nessuno dei 12 slot legacy o pubblicare un aggiornamento prima della prova su Galaxy S21. L'interfaccia è deliberatamente una **prova tecnica separata**, non la UI definitiva V3 prevista dal Master.

## Codice creato, confini effettivi

- **`Zero3WorldBirthV326.java`**: primo factory di nascita di vita 3.0 che non invoca alcuna classe `*Regression`/fixture. Crea un corpo `MotorBrain.Body`, membrana `Morphology.Surface` con 96 punti e gel `SymbioteMatterV1` con 74 masse interne, usa `gel.reset` e 96 reali passi di quiete; crea BOX, DISK e una stanza fisica `ScarcityLab` con alimento canonico come oggetto unico del mondo. Riserva iniziale 100 e tempo biologico 0. Non impone direzione, ricompensa, traslazione del centro, movimento ritmico o presa: il cervello decide da sé, anche quando non sa camminare. **Non è ancora l'intero habitat/shelter/vento/tempo di `GameActivity`.**
- **`Zero3RuntimeV325.Display`**: fotografia read-only/defensiva del vero `Body`, 96 patch membrana, reali oggetti, posizione porta, energia, conteggi di decisioni e passi gel, e 16 stati interni dell'encoder. Copiare o alterare gli array della vista non può modificare la fisica. Le forme e posizioni sono il mondo live della 3.0, non una grafica sintetica né conteggi di neurogenesi inventati.
- **`Zero3AndroidSessionV325`**: nuovo metodo `display()`, più metadati read-only e stato terminale; unico accesso filesystem in `getNoBackupFilesDir()/zero3-research-v325/v3-isolated-v326`. Nessun accesso a `SaveSlotStore`, shared prefs / V2, vecchie partite o updater.
- **`Zero3LabActivityV326.java`**: Activity autonoma con vista Canvas del vero corpo/membrana, colore degli oggetti per legge fisica, porta reale, energia, neural-state count e decisioni reali. Pulsanti: CREA ZERO 3.0 VERGINE, RIPRENDI SOLO V3, AVVIA x1/x5 (approssimativi, velocità effettiva da misurare), PAUSA E SALVA, TORNA ALLA 2.2. Ogni vita usa un thread worker esclusivo; una decisione è fatta di 12 integrazioni fisiche e l'archivio unico si aggiorna ogni 8 decisioni complete. `onPause` richiede stop e salvataggio sul worker, senza bloccare il thread UI; il processo Android può morire prima di terminare il checkpoint e in quel caso si recupera l'ultima generazione completata.
- **`AndroidManifest.xml`**: `Zero3LabActivityV326` con `android:exported=false`, non launcher e non deep link esterno.
- **`GameActivity.settingsChoice(4)`**: solo la voce LABORATORI ora apre un dialogo che preserva il **confronto 2.2 precedente** oppure consente di aprire esplicitamente ZERO 3.0 sperimentale con seconda conferma. Prima di avviare la nuova Activity, mette in pausa la vecchia simulazione e chiama gli stessi `save/saveScene` legacy esistenti. Nessun cervello 2.2 passa alla 3.0. **Questo è il solo punto di ingresso nel gioco 2.2 modificato.**

### Vincolo salvataggi

`V324` non può salvare un cervello con zero decisioni; quindi alla pressione CREA ZERO V3 l'app esegue **una decisione fisica completa con cervello nuovo e salva il checkpoint**, poi mostra lo stato. Non spaccia quella decisione per vita ancora vergine in esperienze. `startNew` rifiuta di sovrascrivere qualsiasi file V3 precedentemente esistente; `resume` rifiuta di fabbricare una nuova vita da dati corrotti; l'authority è di un solo organismo.

### Prove

`Zero3BirthV326Regression` usa il vero `Zero3WorldBirthV326` e **nessuna Scenario**. In processi JVM indipendenti: nasce la vita fisica (verificati 96 punti, 74 masse, 3 oggetti, 100 energia, 96 passi fisici iniziali), vive 6+6 decisioni e salva due generazioni, si riapre su nuova JVM e prosegue da 12 a 24. Un processo di controllo esegue 24 senza stop; `cmp` richiede identico fingerprint completo. Test di rollback su payload tronco deve recuperare esattamente 12 esperienze, e un episodio straniero non deve scrivere nel relativo archivio. Test della vista: alterare un array restituito non deve muovere l'organismo.

Il workflow `.github/workflows/zero30-v326-android-entry-qa.yml` compila via JVM la vera materia e i moduli V3, verifica la route UI opt-in e manifest non exported, e tenta `gradle assembleDebug` senza firma/pubblicazione. **Il build Android è indispensabile per dire che la schermata compila; un problema di dipendenze di rete su Maven Central non equivale a errore Java, ma comunque blocca il gate Android finché una compilazione non riesce.**

## Stato e blocchi

- **ESITO FINALE VERIFICATO — [CI 39030669596 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39030669596)**, commit `8a4fb7969ee179978dfada85573ae92b31094083`, job 117390303717. Dal log:
  - `V326 FRESH_BIRTH_SAVED_12=PASS`, `FRESH_BIRTH_RESUMED_24=PASS`, `FRESH_BIRTH_CONTINUOUS_24=PASS`. Il fingerprint congiunto del nuovo organismo nato da un factory reale è stato confrontato byte-per-byte fra **12+riavvio JVM+12** e **24 senza interruzione**; dati della vista di sola lettura non possono alterare i 96 punti fisici.
  - `V326 NEWBORN_FAULT_ROLLBACK_12=PASS NO_LEGACY_REWRITE=PASS` dopo corruzione dell'ultima generazione, con identità episodio straniero bloccata.
  - `ZERO3 V326 OPT_IN_ROUTE_LEGACY_PRESERVED=PASS`: manifest Activity non esportata, link esplicito dalle impostazioni, opzione del confronto 2.2 ancora presente, sandbox `getNoBackupFilesDir`.
  - `BUILD SUCCESSFUL in 55s`, `V326_ANDROID_DEBUG_COMPILED=PASS`. La build Android Debug include `Zero3LabActivityV326`, factory V3 e bridge. **Nessuna build release firmata o OTA è stata avviata.**
- **Prima CI non riuscita [39030554126](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39030554126):** JVM e checks UI PASS, ma Gradle non ha superato il download plugin da Maven Central per HTTP 403 prima della compilazione Android. Il retry è stato aggiunto senza indebolire l'asserzione `assembleDebug`; la run 39030669596 ha compilato al primo tentativo dopo tale modifica.
- **Non ancora testato sul Galaxy S21:** apertura Activity, pause/resume Android, protezione process-death, performance x1/x5, stato termico, visualizzazione reale e latenza di salvataggio.
- **Sensori energia nel cervello ancora assenti:** V315 `energyAvailable=false`; la UI può leggere una riserva reale, ma l'actor non riceve automaticamente un valore energetico. Il metabolismo V323 è un osservatore delle leggi reali dei contatti, non un critic/reward di sopravvivenza validato.
- **Mancano:** ambiente completo `GameActivity` V3 con tempeste/shelter/spawn multipli, memoria abilità ed esperienze generalizzate, neurogenesi, G3 locomozione validata, G4 critic, G5 world-model, UI finale 3D neurale, G9 device e G10 release OTA.
- **Mai dichiarare un APK V3 installato né un 3.0 disponibile al pubblico:** PR #87 resta Draft e il rilascio OTA172 in produzione con 12 profili 2.2 non deve essere alterato.

## Prossimo sottogate

Verificare build Android Debug (in CI) e una sessione Android Galaxy S21 vera che nasca, accumuli almeno 2.000 decisioni, salvi, venga terminata brutalmente, torni nella **stessa vita**, recuperi il journal sensoriale, e confronti conteggi/energia/contatti senza trucchi. Poi estendere l'habitat agli elementi mancanti tramite un adapter V3 e costruire la UI 3.0 da provider reali. Dopo serve G3 locomozione matched contro frozen, non premio movimento.

## Verdetto aggiornato

**Promossi solo i sottogate `V326_OPT_IN_ANDROID_ENTRY_COMPILES=PASS` e `V326_PHYSICAL_BIRTH_RESTART_JVM=PASS`.** Il percorso della schermata è aggiunto e raggiungibile nel codice ma NON ancora pubblicato su OTA172; è quindi improprio dire all'utente che appare già nell'APK attualmente installato. `G9_REAL_ANDROID_LIFECYCLE`, `G3_MOTOR_LEARNING`, `G5_WORLD_MODEL` e la UI finale 3D restano BLOCCATI. Nessun salvataggio 2.2 è stato modificato.

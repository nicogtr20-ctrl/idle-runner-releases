# ZERO 3.0 — V3.25 | Unico proprietario runtime neurofisico e ciclo di vita Android isolato

**17 ottobre 2026 — ramo sperimentale `feature/zero30-foundation-g0-g3-20261016`, PR #87 DRAFT.** Questa tappa NON modifica `GameActivity`, `SaveSlotStore`, `updater/public/latest.json` o una OTA. I 12 slot di ZERO 2.2 rimangono separati. Il ramo contiene codice nuovo V3, non ancora collegato alla UI, e non è stato installato sul Galaxy S21.

## Documenti vincolanti in ordine

1. [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [Implementation Contract](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI/UX 3.0](ZERO30_UI_UX_SPEC.md).
2. [V3.21 neural checkpoint](ZERO30_V321_FULL_NEURAL_BUNDLE.md), [V3.22/A same-life state](ZERO30_V322_SAME_LIFE_SNAPSHOT.md).
3. [V3.22/B full causal replay](ZERO30_V322B_FULL_CAUSAL_REPLAY.md), [V3.23 energia](ZERO30_V323_SURVIVAL_CONTINUITY.md), [V3.24 stanza](ZERO30_V324_ROOM_WORLD_CHECKPOINT.md).
4. **Questo handoff V3.25** e i log effettivi della [CI V3.25](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero30-v325-runtime-qa.yml). Le prove già viste non diventano automaticamente prove su dispositivo.

## Limite che si è scelto di eliminare

In V3.24 i processi JVM erano controllati da `Zero3RoomRestartV324Regression`, che chiamava un'altra classe `*Regression` per ogni ciclo fisico. È stato quindi verificato il contenitore di memoria, ma **non esisteva ancora un runtime V3 autonomo che fosse proprietario dei moduli di scelta e dei loro checkpoint**. Un semplice wrapper di `GameActivity` legacy rischierebbe di mescolare i due cervelli e distruggere dati. Per questa ragione si è creata una seam V3 rigorosamente isolata prima di intervenire sulla UI e sugli slot.

## Implementazione su PR #87

### `Zero3RuntimeV325.java` — componente di simulazione reale senza dipendenza dai test

Una istanza rappresenta una singola vita/episodio e possiede:

- **Unico owner decisionale:** `Zero3MotorIntentV316.Authority`, ID episodio, cursore di decisione, refusal delle duplicazioni. Su recupero l'owner nasce nuovo e il cursore arriva dal checkpoint verificato, non dalla ricostruzione fittizia di un altro cervello.
- **Tre moduli di apprendimento attivi:** encoder ricorrente/actor `Zero3AutonomousLoopV317.Encoder`, cortex 16×4 `Zero3LearningFoundation.AdaptiveCortex` e head adesivo `Zero3ContextAdhesionV319`. Non sono osservatori: i 64 output della cortex sono attuati sul gel tramite il solver.
- **Materia e mondo reali:** `MotorBrain.Body`, `Morphology.Surface` (96 patch), `SymbioteMatterV1` (74 interne), `UnknownWorld.Entity` e `ScarcityLab`, con `SquareGripPuzzle`, `MaterialContactConstraints`, `SymbioteWorldStepV171`, collisioni disco e `room.solveGate`. I segnali di presa vengono composti per proprietario con `GripSurfaceComposition.compose` per non far cancellare la BOX dalla stanza.
- **12 passi per decisione:** osservazione `Zero3SensoryFrameV315` PRE → scelta autonoma del recurrent actor → comandi autentici cortex+grip → 12 passi `gel.step` / contatti / debit costo fisico / metabolismo → osservazione POST e credito apprendimento. Un record contiene PRE/POST e **ogni** vettore motorio 64D dei 12 passi con costo misurato e integrità sensorimotoria verificata.
- **Metabolismo transazionale:** `Zero3SurvivalLedgerV323` calcola costi passivi, costo meccanico e vere interazioni di contatto, ma resta una replica sperimentale delle equazioni del gioco, non il `updateHabitat` / weather di `GameActivity`. Quando la riserva tocca zero, la vita finisce a confine decisionale e il runtime non consente ulteriori decisioni; lo stato terminale è ricostruibile.
- **Generatore di mondo posseduto dall'episodio:** `spawnDisk` avanza un vero RNG V3 soltanto a confine di decisione e crea oggetti fisici, persistiti come parte dello stesso stato. Il programma di prova controlla solamente **quando** invocare spawn, non forza il comportamento motorio.
- **`snapshot` unico:** senza richieste di salvataggio nel mezzo di una decisione, compone `Zero3OrganismStoreV322` + replay V322/B + energia V323 + stanza V324. Salva in una cartella specifica tramite due generazioni atomiche, SHA-256, rollback e rifiuto di archivio appartenente ad altro episodio. Se esistono file corrotti e nessuna generazione valida, `save` rifiuta di sovrascrivere una storia senza autorizzazione.
- **`close` e `recovered`:** restituiscono l'authority esattamente una volta e rifiutano un recupero parziale di cervello, corpo o metabolismo. `newborn` non carica altri profili e richiede una risorsa stanza `room.food` realmente presente nella lista oggetti per identità.

### `Zero3AndroidSessionV325.java` — ingresso Android separato, volutamente non registrato

L'applicazione futura potrà chiamare `create(Context,slot)`, `startNew`, `resume`, `step`, `suspend`. I file sono dentro `Context.getNoBackupFilesDir()/zero3-research-v325/v3-<slot>`, non nei vecchi SharedPreferences. Ogni `suspend` salva uno stato completo verificato e chiude l'owner, senza dichiarare successo se fallisce la scrittura. `resume` non genera un cervello vergine quando trova un archivio corrotto. `startNew` rifiuta di sovrascrivere file preesistenti.

**IMPORTANTE:** la classe compila come parte dell'APK Debug ma NON è richiamata da un'Activity. Non esiste ancora test instrumented Android di pausa/chiusura/ripresa, né editor UI per V3. Non dichiarare la piattaforma G9 completata.

## Prove di accettazione sul codice effettivamente condiviso dalla futura app

- `Zero3RuntimeV325Regression`: usa il **nuovo runtime di produzione sperimentale**, non `Zero3MultiSceneRestartV322BRegression.run`; i test forniscono soltanto mondo iniziale e tempi di spawn.
- Per ogni vita: processo A esegue 24+24 decisioni con due generazioni salvate; processo B chiude/restituisce `Authority`, carica episodio e procede altre 48; processo C esegue 96 senza stop; `cmp` confronta integralmente fingerprint V324 (neuroni/corpo/stanza/energia/RNG/replay), stato dei 64 comandi, contatori e timestamp; processo D corrompe la generazione corrente e deve ritrovare precisamente le 48 decisioni complete precedenti, non inventare esperienze, poi bloccare il doppio guasto. Cinque tipologie mondo FREE/BOX/DISK/FAR/BOX+DISK.
- `Zero3RuntimeV325TerminalRegression`: riserva energetica quasi nulla, una decisione realmente eseguita fino al limite; nessun secondo credito o riavvio da morto, anche dopo caricamento del checkpoint.
- Compilazione JVM con solver e moduli reali; compilazione Android Debug comprensiva del bridge isolato, senza APK firmato o OTA.
- Primo screening di 10 vite, **poi** ampliamento a 25 casi sulla base dell'esito: non presentare il secondo blocco come holdout completamente non consultato.

**Esito finale:** da compilare solo dopo lettura dei risultati GitHub Actions della versione consolidata, controllando anche la compilazione dell'Android bridge e l'esito del test terminale. Non confondere il numero totale delle decisioni sui 25 casi con un unico organismo allenato così a lungo.

## Ancora indispensabile prima di dire «ZERO 3.0 pronto»

- Collegare una vera Activity V3 opzionale che usa `Zero3AndroidSessionV325`, con fisica e stato esposti via provider, thread lifecycle e interruzioni anche a metà frame; effettuare test effettivi su Galaxy S21, stress di storage pieno/process death e recupero slot V3.
- Integrare `ShelterEcology`, vento/tempeste e il resto delle regole `GameActivity` in un adapter del **nuovo** runtime, senza duplicare o riscrivere quello della 2.2. Versionare il mondo intero, RNG del world placement e scheduler, con convalida delle entità.
- Attivare il sensore energia/interocezione autentico: `Zero3SensoryFrameV315.energyAvailable` rimane false. Il cervello V3 al momento **non percepisce ancora direttamente la riserva energetica**, anche se il metabolismo viene registrato correttamente. Non chiamarlo apprendimento di sopravvivenza autonomo provato.
- G3: valutazione cieca trained-vs-frozen in 20+ vite nuove con spostamento netto, lavoro, allineamento e vera manipolazione mantenuta, non soltanto 48→96 identiche. G4 critic, G5 world model, memoria skill, neurogenesi, nuova UI 3.0 e G9-G10 su dispositivo, solo nei rispettivi gate.

**Rimane PR #87 DRAFT. Nessun merge nella produzione OTA, nessun reset/migrazione degli slot 2.2; la versione pubblicata nota è OTA 172.**

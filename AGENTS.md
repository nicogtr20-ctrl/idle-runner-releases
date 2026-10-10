# Istruzioni permanenti agli assistenti che lavorano su questo repository

## AUTONOMIA ZERO — REGOLA PERMANENTE DALLA CORREZIONE 0.9.8
Consultare `docs/EXPERIMENT_0098.md` sezione **CORREZIONE 0098-A**, che SOPPIANTA le precedenti descrizioni del bonus visivo-direzionale 0.9.8-ota.42. L'utente non vuole che ZERO sia attratto automaticamente da una sfera nota quando ha fame: deve elaborare da sé la decisione. Eliminato `SensoryValenceGuide.java` e chiamata `sensoryGuide.bias`; implementato `ExperienceCredit.java` (ultime 12 azioni realmente eseguite, ricompense solo da energia guadagnata/persa al contatto fisico, Q wm1 esistente; no calcoli coseno heading-stimolo). La memoria delle 6 tecniche motorie e il recupero delle azioni ripetute in stagnazione restano. Attenzione: un rinforzo energetico è una motivazione generica progettata, non «libero arbitrio», né prova di capacità di trovare cibo. Pubblicare APK solo dopo JVM/Android+firma+GitHub Release e verificare SHA/versionCode. Aggiornare `main` documenti a ogni modifica.


**PRIMA DI QUALSIASI MODIFICA** apri `OVERMIND_START_HERE.md`, `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/CHANGELOG.md`, `docs/ROADMAP.md`, `docs/HANDOFF.md`, `docs/UPDATES.md`.

## Ambito
- `main` contiene ancora Idle Runner e la documentazione condivisa. Non sovrascrivere Idle Runner e non convertire `main` in OVERMIND senza richiesta esplicita.
- OVERMIND ZERO attuale: `experiment/overmind-zero-world-v03`; seguire `docs/HANDOFF.md` per identificare la versione più recente.
- L'utente vuole una vera simulazione di apprendimento (limiti dichiarati onestamente), corpo nero fluido, nessuna animazione prefabbricata di zampe, nessuna conoscenza implicita degli oggetti, osservatore invisibile e interventi indiretti.
- Il cervello motorio iniziale è `MotorBrain.java` (62 parametri, ring di 10 punti fisici); `UnknownWorld.java` conserva preferenze apprese per segnali osservati. Non descrivere questi sistemi come AGI/coscienza.
- Preserva sempre l'apprendimento salvato e considera versionCode, package name, firma APK e migrazione dati; non cancellare memorie senza consenso.
- Il progetto è Android Java, Gradle, GitHub Actions; Vercel serve esclusivamente il manifest e, una volta configurata la distribuzione, gli APK.
- Non committare keystore, password, token, credenziali o dati sensibili.
- Per ogni versione: eseguire test JVM e build Android, annotare prove reali vs ipotesi, aggiornare CHANGELOG / ROADMAP / WHITEPAPER (se cambiano obiettivi) e pubblicare una release verificata.
- Aggiorna sempre `docs/HANDOFF.md` con branch, tag, stato firma/OTA, problemi aperti e prossimo test. Non affermare che un aggiornamento automatico funziona senza test APK→APK su dispositivo.
- Per Android non è consentito un aggiornamento APK silenzioso ordinario: l'utente dovrà confermare la schermata di installazione. La chiave di firma deve restare identica tra le versioni.


## Nuova lettura obbligatoria per 0.9.5
- Leggere **`docs/EXPERIMENT_0095.md`** dopo `OVERMIND_START_HERE.md`; contiene le osservazioni concrete del Galaxy S21, il confronto fra 0.9.1 e 0.9.5, il motivo della memoria episodica, le chiavi SharedPreferences, le richieste dell'utente e il protocollo di verifica.
- Ogni nuova feature impatta il paper: aggiornare `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/ROADMAP.md`, `docs/CHANGELOG.md`, `docs/HANDOFF.md` e `OVERMIND_START_HERE.md` insieme al codice e mantenere **copie leggibili sul branch `main`**.
- Non confondere predizione del mondo, replay episodico e pianificazione: al 10 ottobre v0.9.5 non ha immaginazione multistep generale. Le statistiche 268 prese, porta max 111U, 0 stanze risolte sono osservazioni, non dimostrazione di “capire la porta”.
- Non inventare continuità di strategie prima di una verifica APK reale. Non cambiare il package `com.overmind.zero.ota` né la firma degli upgrade.

## Pubblicazione verificata v0.9.5-ota.39
La compilazione Android firmata `38003676123` è verde: versionCode `1039`, SHA-256 `86caeb85b6eba94aa657d6f19ac7dff5dd0e4e8d2974bf71e8c6c12a3201c211`, 92.692 byte. Test in-game su Galaxy S21 ancora pendente, nessun reset richiesto.


## Procedura obbligatoria dopo 0.9.6
Leggere `docs/EXPERIMENT_0096.md` prima di iniziare un altro aggiornamento. Documenta richiesta utente, osservazioni del dispositivo (0.9.4 max porta 111U, non ingresso), differenze 0.9.5→0.9.6, memorie positive/dannose, nuovo formato em2 compatibile em1, schema reset e criteri di validazione. Non descrivere i colori come regole hardcoded, non confondere contatori di avversione con capacità dimostrate, non sovrascrivere i salvataggi o firmare con nuova chiave. Mantenere AGENTS, START_HERE, WHITEPAPER, ARCHITECTURE, HANDOFF, ROADMAP, CHANGELOG, EXPERIMENT_0096 aggiornati e accessibili su `main` per le future chat.


## Regressione motoria e regole permanenti — ZERO 0.9.7
Consultare `docs/EXPERIMENT_0097.md` in ogni nuova chat prima di modificare il codice. Dopo 0.9.4 (movimento relativamente rapido), la 0.9.5/0.9.6 appariva quasi ferma su Galaxy S21. Non attribuire questo alla volontà/intelligenza di ZERO: era possibile override dei 62 pesi motori da ricordi e abort eccessivo delle prove. La 0.9.7 usa sempre il controllore motore appreso, limita le riprove di mano/membrana al contatto reale, non blocca gli esperimenti per mere azioni ricordate, introduce guard antistallo che toglie solo bias positivo senza guidare azioni. Verificare TEST reali dopo firma: conservazione dati e distanza netta nella stessa durata, x1/x5. Non inventare riuscita o qualità del comportamento. Aggiornare sempre START_HERE/AGENTS/WHITEPAPER/ARCHITECTURE/HANDOFF/ROADMAP/CHANGELOG e EXPERIMENT_0097 su **main** per passare l'intero stato a nuove chat.

## Handoff obbligatorio v0.9.8
LEGGERE `docs/EXPERIMENT_0098.md` e `OVERMIND_START_HERE.md` prima di intervenire. Ultimo feedback S21: ZERO ritorna alla stessa zona, molto percorso ma bassa distanza netta, morte con energia vicina. La v0.9.8 introduce archivio di controller motori realmente provati, protezione delle capacità motorie, diversità anti-loop, bias delle scelte basato su conseguenze energetiche realmente apprese. NO blu=buono / rosso=cattivo / target prefissati. Non dichiarare miglioramento di comportamento prima del test sul telefono. Aggiornare sempre whitepaper/architecture/roadmap/changelog/handoff su main; usare per codice Android branch `feature/overmind-inapp-updater` e GitHub CI firmata per APK, controllare SHA/asset.

## REQUISITO HANDOFF 0.9.9 — regressione codice, non attribuire volontà
Leggere `docs/EXPERIMENT_0099.md` prima di ogni futura modifica. La perdita simultanea da 0.9.5 di locomozione rapida e punti arancioni riferita dall'utente ha cause architetturali concrete plausibili: comparazione git 9b0e716cab4→e5ef6752072 rileva introduzione replay di peso motore/mano/membrana e alterazione dei trial, senza cambiamento fisica porta. In 0.9.9 GameActivity usa solo `liveBody.weights(0/1/2)` per il corpo, mai `episodic.weights`. GripSkillBank conserva 5 coppie (mano 18 + membrana 82) solo dopo tenuta verificata ≥0.075 s, con recovery condizionale dopo adesione *indipendentemente scelta*. HUD conta PRESA SCELTA, COMANDO, TENUTA. Vecchi cervelli/memoria Q/em1/em2, 6 motor skills rimangono. NO script verso cancello. Documentare S21 prima di affermare recupero capacità. APK firmata stessa app/firma, SHA da latest manifest/release. Aggiornare sempre main papers.

## REQUIRED HANDOFF — ZERO 0.10.0 saved games
READ `docs/EXPERIMENT_0100.md` before modifications. The user requested game-like named save slots and ability to make a virgin ZERO without losing the currently trained one. There are 12 named slots, immutable until explicitly overwritten, in `SharedPreferences(zero_save_slots_v100)` separate from the old LIVE working profile `zero_motorbrain_001`. Full reset of the LIVE brain does not wipe the slot file. `SaveProfileCodec` roundtrips ALL typed preference fields (including long world model strings) and SHA-256; `SaveSlotStore` protects the outgoing ZERO in a one-deep recovery snapshot before atomic load or new game. `SaveSlotsActivity` is non-exported, portrait and accessed via PARTITE HUD or ANALISI. The profile includes `physical_scene_v100`: `GameSceneSnapshot` captures body, 96-point membrane, room/gate, objects, current physical positions and MotionExploration current life. GameActivity onPause/suspend saves brain then scene before SaveSlotsActivity opens, and onResume consumes `pending_profile_reload_v100` to discard old objects/policy before any autosave. Same APK signature/package, no new permissions. In-progress mutation and RNG exact replay remain outside snapshots; never promise bitwise deterministic replay. Do not lose previous 0.9.9 grip fixes / 0.9.8 motor archive, and don't claim S21 test success without evidence.

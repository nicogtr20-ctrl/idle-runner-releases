# CHANGELOG — storico OVERMIND ZERO

## 0.9.8 AUTONOMY PATCH — rettifica dopo prima OTA.42 (10 ottobre 2026)
- Rimosso `SensoryValenceGuide.java`: eliminate preferenze direzionali determinate matematicamente da vicinanza angolare a una fonte visiva nota.
- Nuovo `ExperienceCredit.java` registra fino a 12 coppie percezione+azione REALI e aggiorna i normali pesi Q soltanto dopo che un contatto energetico reale produce guadagno o danno.
- `SensorimotorMind.choose` non aggiunge più `sensoryBias`; nessun `goto(sfera)` né tabella innata colore→azioni. Un comportamento favorevole può emergere dai Q appresi, ma non è garantito.
- Conservati gli altri miglioramenti 0.9.8 (archivio motore max6, anti-stallo, retention), i vecchi 11.664 pesi del predittore e la memoria episodica `em1/em2`.
- TELEMETRIA: veri contatti energetici positivi/negativi e numero di azioni passate cui l'esito viene attribuito, invece dei precedenti contatori di bonus visivi. Reset WORLD include nuovi contatori.
- Limiti, test e motivazione scientifica: `docs/EXPERIMENT_0098.md`, sezione CORREZIONE 0098-A. Nessuna convalida del comportamento su Samsung S21 ancora ottenuta.


> Le versioni seguenti sono *esperimenti*, non un prodotto finito. Aggiornare questa pagina in OGNI release insieme al file HANDOFF.

## 2026-10-09 — Infrastruttura aggiornamenti (in preparazione)
- Stop temporaneo al ramo gameplay `experiment/overmind-zero-physics-v04`.
- Creati whitepaper permanente, architettura, changelog, handoff, AGENTS.md e roadmap.
- Vercel attivo: `https://overmind-zero-updates.vercel.app/latest.json`, risposta 200 con `available:false`.
- Client Android con pulsante Aggiorna, download HTTPS/SHA-256, installer con autorizzazione Android: branch `feature/overmind-inapp-updater` (build da validare).
- Nuovo applicationId `com.overmind.zero.ota` per non toccare i dati locali delle APK sperimentali già installate.
- Pulsante **Aggiorna** e download + controllo SHA-256 + installazione con conferma Android implementati sul branch `feature/overmind-inapp-updater` (build CI).
- Configurazione della firma permanente richiede il secret `OVERMIND_SIGNING_BUNDLE`; senza quello il server resta disabilitato. Evitare promesse di aggiornamenti silenziosi.

## ZERO-002 / v0.3.5 — 2026-10-09
- Branch `experiment/overmind-zero-world-v03`.
- Release privata `zero-world-v0.3.5` / APK `OVERMIND-ZERO-WORLD-v0.3.apk`.
- Tre classi di oggetti sconosciuti e memoria associativa degli effetti del contatto; nuova app `com.overmind.zero.world`.
- Test JVM e build riusciti; test sul telefono: **4.772 tentativi motor learning, +2,9→+204,4 su benchmark, 49 contatti, 3/3 tipi incontrati**, energia 99/100.

## ZERO-001 / v0.2.2 — 2026-10-09
- Branch `experiment/overmind-zero-brain-v02`.
- Release privata `zero-brain-v0.2.2`.
- Rete neurale vergine da 62 parametri, simulazione meccanica su punti elastici, allenamento per neuroevoluzione e benchmark.
- Fix background / persistenza.
- Test sul telefono: 2.467 tentativi, benchmark -3,6→+179,7; l'utente conferma che il corpo si deforma e raggiunge il bersaglio.

## Prototipo precedente / v0.1.2 — 2026-10-09
- Branch `experiment/overmind-zero-v01`; release `overmind-v0.1.2`.
- Simulazione top-down con organismi, energia, memoria spaziale rudimentale, preferenze tramite Q-learning, XP e modifiche al mondo.
- Sostituito sperimentalmente: comportamento troppo preimpostato e creatura graficamente troppo simile a ragno prefabbricato.

## Prima di OVERMIND
- Branch principale contiene ancora il progetto distinto **Idle Runner**; non mescolarlo.

## OTA 0.3.6-ota.8 — 2026-10-08T22:43:28.351618+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1008).
- SHA-256: `118f0983c8644aa5f628db52dea1a813b84cdd6043e11c4aced881a50fd405c2`.
- APK: `/apk/overmind-zero-1008-118f0983c864.apk`.

## OTA 0.3.6-ota.9 — 2026-10-08T22:45:54.022194+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1009).
- SHA-256: `d97b478825d66767c02003fd1c229ce84b0e03d2058be731ecdb91ab5138a21c`.
- APK: `/apk/overmind-zero-1009-d97b478825d6.apk`.

## OTA 0.3.7-ota.10 — 2026-10-08T22:47:14.160606+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1010).
- SHA-256: `7f012383aee3b29c0cd5c4b72e192de6bd9732c4089e3fea0e515f79020c4376`.
- APK: `/apk/overmind-zero-1010-7f012383aee3.apk`.

## OTA 0.3.7-ota.11 — 2026-10-08T22:53:16.673945+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1011).
- SHA-256: `c3ed391916479052c16e8172f518e9aa65bbef31b30ebb45ab5905fa3af744e9`.
- APK: `/apk/overmind-zero-1011-c3ed39191647.apk`.

## OTA 0.3.7-ota.12 — 2026-10-08T22:54:46.140624+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1012).
- SHA-256: `4e2a741614d0b81ef22738bc1998a35806d9a01054731eab2a39ebf7277bc2c2`.
- APK: `/apk/overmind-zero-1012-4e2a741614d0.apk`.

## OTA 0.3.8-ota.13 — 2026-10-08T22:57:55.065588+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1013).
- SHA-256: `b98857c77d5a4362f3696d4dd432965287593120382fdc28f26b032dafe58777`.
- APK: `/apk/overmind-zero-1013-b98857c77d5a.apk`.

## 2026-10-09 — OTA Android provato su dispositivo
- L'utente segnala che l'aggiornamento dalla release correttiva alla v0.3.8 **funziona** sul Galaxy S21.
- Il problema precedente era il Manifest privo di INTERNET, REQUEST_INSTALL_PACKAGES e UpdateFileProvider; corretti e verificati sulla APK firmata da CI.
- Release 0.3.8-ota.13 resta baseline OTA; gameplay ZERO-003 può riprendere in un ramo separato, preservando `com.overmind.zero.ota` e la firma.

## OTA 0.4.0-ota.15 — 2026-10-08T23:07:12.242277+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1015).
- SHA-256: `1adaf54b59b3d9a48d9f2bdbd0dc31ea0570879a82b4c75aba56fa3443d9d6fe`.
- APK: `/apk/overmind-zero-1015-1adaf54b59b3.apk`.

## OTA 0.4.1-ota.16 — 2026-10-08T23:22:39.482124+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1016).
- SHA-256: `030fc487f8988043e7927584af30b0eaa714e3790f12f5f8e947d54d056a4406`.
- APK: `/apk/overmind-zero-1016-030fc487f898.apk`.

## OTA 0.5.0-ota.17 — 2026-10-08T23:34:32.865396+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1017).
- SHA-256: `dbfe1e0ad71140098551eeb0161310849a0bff1e5924b3a9e9b69a4879133397`.
- APK: `/apk/overmind-zero-1017-dbfe1e0ad711.apk`.

## OTA 0.5.0-ota.18 — 2026-10-08T23:36:02.181313+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1018).
- SHA-256: `5ebc1bc064f8aad562da150f73830ec0b03127e56b4ce82c4a366e886288763a`.
- APK: `/apk/overmind-zero-1018-5ebc1bc064f8.apk`.

## OTA 0.6.0-ota.19 — 2026-10-09T00:04:07.789084+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1019).
- SHA-256: `4922de216534059ca58c0521902b7ab71cb683e3ac6c5c6ec214c6f36e7829ba`.
- APK: `/apk/overmind-zero-1019-4922de216534.apk`.

## OTA 0.6.0-ota.20 — 2026-10-09T00:05:20.499825+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1020).
- SHA-256: `27dec96315ab247ac6af59b036ed787e90346a5d9d8ea34badc4796627ad5964`.
- APK: `/apk/overmind-zero-1020-27dec96315ab.apk`.

## OTA 0.7.0-ota.21 — 2026-10-09T07:29:14.714819+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1021).
- SHA-256: `cdda73e19855aeb167f7a76989232212c4488d2e20ea9b8305bc272aa53f393d`.
- APK: `/apk/overmind-zero-1021-cdda73e19855.apk`.

## OTA 0.7.0-ota.22 — 2026-10-09T07:30:22.568706+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1022).
- SHA-256: `74610481e4504e9b230ff5c8db3ee1a196f2336c720c99fe4d2eb2e7cb9a8eac`.
- APK: `/apk/overmind-zero-1022-74610481e450.apk`.

## OTA 0.8.0-ota.23 — 2026-10-09T07:57:05.201505+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1023).
- SHA-256: `941873ee1d0837965f77dfc42040d0e21cc69e2aade308db432a7a73cf902057`.
- APK: `/apk/overmind-zero-1023-941873ee1d08.apk`.

## OTA 0.8.1-ota.24 — 2026-10-09T08:25:59.783021+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1024).
- SHA-256: `0e10bb4ce202aa8a323b30757123b2de7e556c7e592e12a2d8fa90cf02d678ba`.
- APK: `/apk/overmind-zero-1024-0e10bb4ce202.apk`.

## OTA 0.8.2-ota.25 — 2026-10-09T08:47:02.469750+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1025).
- SHA-256: `b8d81b42a94d3ba70699c1d6e9e581645380f2ad181c2ed04d82cc5cf36ee63e`.
- APK: `/apk/overmind-zero-1025-b8d81b42a94d.apk`.

## OTA 0.8.3-ota.27 — 2026-10-09T09:08:57.060228+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1027).
- SHA-256: `d4d0955c4968bf5798026b611f69b098146a230cd337517956d92af20b9b7475`.
- APK: `/apk/overmind-zero-1027-d4d0955c4968.apk`.

## OTA 0.9.0-ota.29 — 2026-10-09T09:39:10.287144+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1029).
- SHA-256: `582a7d2a427ff6f4c5a866f73ae1cd39125e29de23d6171523ba41ce34b1f2c3`.
- APK: `/apk/overmind-zero-1029-582a7d2a427f.apk`.

## OTA 0.9.1-ota.32 — 2026-10-09T10:29:59.007659+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1032).
- SHA-256: `fa2dadbf1f074ccb1026dd8c89e12e7609a9e50a0168859c109b814b33eacd52`.
- APK: `/apk/overmind-zero-1032-fa2dadbf1f07.apk`.

## OTA 0.9.2-ota.34 — 2026-10-09T11:05:22.069451+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1034).
- SHA-256: `6269cc3be2f80e320ca1cbcc4c0adb1a246cf6a0e4c725eed299aafae2fda51a`.
- APK: `/apk/overmind-zero-1034-6269cc3be2f8.apk`.

## OTA 0.9.2-ota.34 — 2026-10-09
- Build Android firmata con chiave OTA stabile, versionCode 1034 (GitHub Actions 37921437274, successo).
- SHA-256: `6269cc3be2f80e320ca1cbcc4c0adb1a246cf6a0e4c725eed299aafae2fda51a`.
- APK: `/apk/overmind-zero-1034-6269cc3be2f8.apk` (83.960 byte).
- Attuatori motore, manipolazione, membrana e presa riaddestrabili esclusivamente con prove reali nel gioco; memoria e apprendimento predittivo conservati. Minore fame/danni rossi, nessun oggetto ordinario nella stanza e risorsa rara esclusiva +80.

## OTA 0.9.3-ota.35 — 2026-10-09T11:29:39.654695+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1035).
- SHA-256: `6f8111405753c0f13c28d9a0c21a51783f1939367e079c0c5b98f8bd1e79c82c`.
- APK: `/apk/overmind-zero-1035-6f8111405753.apk`.

## OTA 0.9.4-ota.36 — 2026-10-09T22:31:18.217322+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1036).
- SHA-256: `c44377b633854bee3b1ea3ad9715e8499f7e4202ae956cdd84ea85fd44b3c58d`.
- APK: `/apk/overmind-zero-1036-c44377b63385.apk`.

## OTA 0.9.4-ota.36 — 2026-10-09
- Signed Android build (versionCode 1036) via OTA stable workflow 37999520662, green.
- SHA-256: `c44377b633854bee3b1ea3ad9715e8499f7e4202ae956cdd84ea85fd44b3c58d`.
- APK: `/apk/overmind-zero-1036-c44377b63385.apk` (87.864 byte).
- NETTO, VIA, nuove zone e spesa energetica misurata; scoring motorio anti-oscillazione basato su spostamento netto ed effetti fisici, mantenendo la libertà di rotazione. Pulsante PROVE/ANALISI migliorati; statistiche trial per ogni rete isolate da reset selettivi. Stessi salvataggi/chiave OTA.


## v0.9.5 — memoria episodica e conseguenze (10 ottobre 2026)
- Nuovo modulo `EpisodicMemory.java` (max 18 ricordi, 9 azioni + sensi, snapshot dei 3 attuatori) costruito esclusivamente da eventi vissuti: spostamento effettivo di oggetti/porta o energia reale.
- Recupero contestuale con bias morbido, replay dei pesi fisici soltanto per azione realmente selezionata, fallback se mancato progresso e durata oltre morte.
- Rinforzo da variazione fisica dell'ambiente rilevata DOPO la simulazione; il vecchio motore/sensori e 11.664 pesi restano compatibili. Trial mutazione motoria sospeso durante l'esecuzione ricordata per non falsare accettazioni.
- Nuovo archivio preferenze `episodic_memory_v095`, schermata ANALISI e reset selettivo MEMORIA EPISODICA.
- **Non vi è prova in-game di porta attraversata o di controllo causale:** primo test su Galaxy S21 ancora da realizzare dopo release. Per dettagli vedere `docs/EXPERIMENT_0095.md`.

## Pubblicazione verificata v0.9.5-ota.39
La compilazione Android firmata `38003676123` è verde: versionCode `1039`, SHA-256 `86caeb85b6eba94aa657d6f19ac7dff5dd0e4e8d2974bf71e8c6c12a3201c211`, 92.692 byte. Test in-game su Galaxy S21 ancora pendente, nessun reset richiesto.


## 0.9.6 — 10 ottobre 2026 · Memorie complete positive e negative
- Episodi di 2–36 passi (ex 9), fino a 24 ricordi (ex 18); accettazione basata su effetti di contatto energetico reali, benefici o nocivi, oltre a spostamenti fisici.
- Richiami positivi e avversione appresa dalle conseguenze dannose, senza alcuna regola specifica per blu/rosso, né script per la porta.
- Esperienze confermate rafforzate, conservazione dei ricordi ad alta priorità, migrazione da `em1` a `em2` nella chiave `episodic_memory_v095`.
- Memorie negative registrate anche se il danno causa la morte; vecchi cervelli e firma APK rimangono. Diagnostica ANALISI ampliata.
- Test JVM e Android staging riusciti: nessuna dimostrazione di evitamento o completamento stanza sul telefono ancora disponibile.
Per prove e prossimi step: `docs/EXPERIMENT_0096.md`. VersionCode e digest release: controllare `updater/public/latest.json`.

## OTA 0.9.6-ota.40 — 2026-10-09T23:43:09.736607+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1040).
- SHA-256: `0e1c4dfbcd4f77cd2575b5cc5d0ca9f6dd6189054f19066501cf9180c730efcb`.
- APK: `/apk/overmind-zero-1040-0e1c4dfbcd4f.apk`.


### Release Android firmata 0.9.6-ota.40 — 10 ottobre 2026
- **Stato:** build e firma stabile GitHub Actions `38005290019` **success**, JVM e Android staging `38005143609` e `38005224595` success; validazione pacchetto Android/update provider superata.
- **versionCode 1040**, package `com.overmind.zero.ota`, **94.984 byte**, **SHA-256** `0e1c4dfbcd4f77cd2575b5cc5d0ca9f6dd6189054f19066501cf9180c730efcb`.
- Asset sorgente `updater/public/apk/overmind-zero-1040-0e1c4dfbcd4f.apk`, manifest `updater/public/latest.json`; GitHub Release privata con tag `overmind-zero-v0.9.6-ota.40` dopo verifica dell'azione fallback.
- Conserva firma, package e SharedPreferences da 0.9.5 e precedenti. **Non disinstallare, non azzerare**. La nuova memoria utilizza `em2` e recupera `em1`.
- **Ancora da dimostrare su Samsung S21:** preferenza effettiva per punti energetici, evitamento di quelli dannosi, apprendimento e recupero di prese utili dopo la morte e attraversamento del cancello. I test CI NON dimostrano tutto ciò.


## 0.9.7 — stabilità locomotoria dopo introduzione dei ricordi (10 ottobre 2026)
- Corretto conflitto architetturale: il motore appreso ora prevale sempre sugli snapshot episodici, che rimangono richiamabili per mano e membrana solo durante contatto tattile e lontano da mutation-trial.
- Corretto starvation: il solo richiamo di un'azione non interrompe più `OnlineBodyLearning`; un'esperienza con **reale** replay tattile interrompe invece il test contaminato.
- Nuovo `MotorAutonomyGuard`: osserva immobilità netta su due finestre di 7 secondi ed eventuale spostamento utile di oggetti, sopprime temporaneamente i soli suggerimenti positivi in caso di vero stallo, senza direzione predefinita.
- ANALISI e salvataggi: recuperi `motor_recoveries_v097`, decisioni libere `motor_free_choices_v097`, riprove `physical_replays_v097`, reset selettivi senza alterare il formato em1/em2.
- Test JVM/Android staging eseguiti; il miglioramento reale sul Galaxy S21 resta da convalidare. Dettaglio `docs/EXPERIMENT_0097.md`.

## OTA 0.9.7-ota.41 — 2026-10-10T00:04:28.555714+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1041).
- SHA-256: `4de8ef5a9f22dd6e4caf503fc7543410a5e829cab5beb1e9b0a0bcdea3a9bf1d`.
- APK: `/apk/overmind-zero-1041-4de8ef5a9f22.apk`.


### RILASCIO OTA 0.9.7-ota.41 — 10 ottobre 2026 (verificato)
- **GitHub Actions firmata 38007333273 = SUCCESS**, JVM/staging 38007100048 e 38007156421 = SUCCESS. Package `com.overmind.zero.ota`, **versionCode 1041**, peso **96.936 byte**, checksum SHA-256 `4de8ef5a9f22dd6e4caf503fc7543410a5e829cab5beb1e9b0a0bcdea3a9bf1d`.
- APK verificata nel repository `updater/public/apk/overmind-zero-1041-4de8ef5a9f22.apk`; manifest `updater/public/latest.json` (GitHub) contiene esattamente SHA, versionCode, versione e dimensioni. GitHub Release fallback `overmind-zero-v0.9.7-ota.41` dopo trigger apposito.
- **Nessun test di comportamento su Galaxy S21 della v0.9.7 ancora effettuato**. I pesi e le memorie esistenti restano installabili in place, ma recupero della velocità prima vista con 0.9.4 deve essere verificato, non garantito.

## 0.9.8 — ottobre 2026: recupero locomotorio e sopravvivenza appresa
- `MotorSkillBank` con 6 tecniche verificabili e recupero su stallo, salvataggio `motor_skills_v098`.
- Nuovo criterio per accettare mutazioni motorie: NETTO>=max(7U,80% precedente).
- Antiripetizione temporanea delle scelte su stagnazione (senza direzioni programmate).
- `SensoryValenceGuide` proietta sugli heading il valore energetico osservato per frequenze visive, senza etichette innate; bisogno energetico aumenta il peso.
- Nuove statistiche e reset selettivi, vecchie reti/episodi inalterati.
- Test automatici e Android compilation passati; miglioramento reale ancora da provare. Esperimento: `docs/EXPERIMENT_0098.md`.

## OTA 0.9.8-ota.42 — 2026-10-10T00:40:03.804411+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1042).
- SHA-256: `81cb0480de8b47e3cf781c6ddc5e771377ebcfa9d811679a6c202d201bfbf124`.
- APK: `/apk/overmind-zero-1042-81cb0480de8b.apk`.


### RELEASE OTA FIRMATA 0.9.8-ota.42 — 10 ottobre 2026
- **VersionName** `0.9.8-ota.42`; **versionCode** 1042, package `com.overmind.zero.ota`, peso **101.320 byte**.
- **Checksum SHA-256 firmato:** `81cb0480de8b47e3cf781c6ddc5e771377ebcfa9d811679a6c202d201bfbf124`.
- **GitHub Actions** staging `38009632805` = success (JVM, motore, sopravvivenza, Gradle Android) e stable firmata `38009921490` = success (stessa firma e manifest OTA pubblicato).
- **Manifest GitHub**: `updater/public/latest.json`; APK in `updater/public/apk/overmind-zero-1042-81cb0480de8b.apk`. Release GitHub con tag `overmind-zero-v0.9.8-ota.42` via pipeline fallback su `main`.
- Il corretto funzionamento sul Samsung Galaxy S21 **deve ancora essere provato**. L'archivio di skill non può recuperare automaticamente motorii della 0.9.4 già sovrascritti prima di questa versione; potrà salvare quelli efficaci osservati d'ora in poi.
- **Installare sopra la versione precedente SENZA disinstallare né azzerare alcuna memoria.**

## OTA 0.9.8-ota.43 — 2026-10-10T01:07:29.554019+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1043).
- SHA-256: `a0c31e1e5fa6a4849bf79dbf3cfd75e8ea5e24ee6256c026380962ad93869082`.
- APK: `/apk/overmind-zero-1043-a0c31e1e5fa6.apk`.


### CORREZIONE FIRMATA DISPONIBILE — `0.9.8-ota.43`
- VersionName **`0.9.8-ota.43`**, versionCode **1043**, package invariato `com.overmind.zero.ota`, **101.868 byte**, SHA256 **`a0c31e1e5fa6a4849bf79dbf3cfd75e8ea5e24ee6256c026380962ad93869082`**.
- Pipeline JVM/Android staging **38011700356 = SUCCESS**; pipeline APK firmata **38011816381 = SUCCESS**. Il manifesto firmato `main/updater/public/latest.json` include APK `overmind-zero-1043-a0c31e1e5fa6.apk` con stesso digest.
- Il GitHub Release pubblico per gli utenti autorizzati è tag `overmind-zero-v0.9.8-ota.43` con asset `OVERMIND-ZERO.apk`; verifica effettiva del rilascio e digest da fare prima di condividere il link.
- L'utente può aggiornare direttamente sopra la vecchia versione **senza azzerare**. Se la 0.9.7 è attualmente in fase di miglioramento spontaneo del movimento, può lasciarla continuare e installare questa patch in un momento successivo; l'APK corretta non forza nuovi obiettivi.
- Nessun risultato di sopravvivenza o maggiore libertà decisionale del dispositivo ancora osservato sulla patch: i test automatici validano componenti e firma, non intelligenza.

## 0.9.9 — 10 ottobre 2026: corretta interferenza del replay episodico con la presa
- Confronto sorgenti v0.9.4→v0.9.5 rivela introduzione di controller episodici sovrascrittivi nel momento in cui iniziano le regressioni simultanee di grip e movimento, fisica porta invariata.
- GameActivity non usa più snapshot degli episodi per nessuno dei 3 controller corporei (motore, mano, membrana): solo controller appreso o mutazione in prova; ricordi delle esperienze/azioni preservati.
- `GripSkillBank`: fino a5 vere coppie hand+membrane conservate con tenuta di adesione fisica e prova di recupero dopo ripetuti tentativi autonomi falliti, senza comandi programmatici di presa.
- Nuova riga HUD porta con contatori frame PRESA SCELTA/COMANDO/TENUTA/TECNICHE e metriche archivi/recuperi in ANALISI.
- Persistenza `grip_skills_v099`, reset selettivo della membrana; old brains e 6 motor skills invariati.
- Test JNI/JVM, fisica cancello, Android staging e firma OTA; conferma comportamento reale S21 ancora da raccogliere. Dettagli `docs/EXPERIMENT_0099.md`.

## OTA 0.9.9-ota.46 — 2026-10-10T01:45:05.085161+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1046).
- SHA-256: `f4524305cdea4094e23c2eb12f664e21f6fe07cff6c3f3e949c0c4e035e6ec1e`.
- APK: `/apk/overmind-zero-1046-f4524305cdea.apk`.


### RILASCIO VERIFICATO 0.9.9-ota.46 — 10 ottobre 2026
- GitHub Actions staging #38013890946: **success**. Publisher firmato #38014277535: **success**. GitHub Release checksum verificato #38014422904: **success**.
- App **`com.overmind.zero.ota`**; versionName **`0.9.9-ota.46`**, versionCode **1046**; `OVERMIND-ZERO.apk` **104.528 byte**.
- SHA256: **`f4524305cdea4094e23c2eb12f664e21f6fe07cff6c3f3e949c0c4e035e6ec1e`**, esattamente uguale in release GitHub e `updater/public/latest.json`.
- Link esistente (NON ipotetico): `https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.9.9-ota.46/OVERMIND-ZERO.apk`.
- Installare direttamente sopra la vecchia APK senza disinstallare, **senza premere alcun reset**. Nessun test comportamentale sul Galaxy S21 della 0.9.9 ancora disponibile. La conferma necessaria è la catena PRESA SCELTA→COMANDO→TENUTA e la velocità/NETTO.

## ZERO 0.10.0 — Partite salvate con nome
- Nuova schermata PARTITE nel gioco e CERVELLO/ANALISI: **12 slot** con nomi, data e Salva/Carica/Sovrascrivi/Rinomina/Elimina; Nuova partita indipendente.
- Salvataggio automatico del personaggio in uso e backup RECUPERA ZERO PRECEDENTE prima di un caricamento/nuovo ZERO (backup ultimo cambio sostituito al cambio seguente).
- Profili neurali e progressi completi a checksum SHA256, serializzazione sicura di modelli da oltre 64KB, pref slot separati dai reset.
- Nuovo salvataggio fisico `GameSceneSnapshot`: stato nucleo 10 masse, membrana 96 punti, cancello e fisica presa, oggetti reali/risorse consumate, vita/posizione/energia/aree esplorate, velocità/pausa; caricamento delle scene e progressi con nuovo `SaveSlotsActivity` e token di reload.
- Nessuna cancellazione degli archivi da RESET TUTTO, nessun cambiamento alla logica di apprendimento, alla firma/OTA o alle memorie em1/em2. Prove evolutive transitorie e random bit-identico non preservati.
- QA JVM/Android e gate firmata; test reale sul Galaxy S21 da svolgere. Documento completo `docs/EXPERIMENT_0100.md`.

## OTA 0.10.0-ota.47 — 2026-10-10T09:55:11.154998+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1047).
- SHA-256: `243cf70ce8a0164b63cef91c8755f846dcbdd9b7f37415290a88147d88f0da43`.
- APK: `/apk/overmind-zero-1047-243cf70ce8a0.apk`.


### RILASCIO FIRMATO 0.10.0-ota.47 — 10 ottobre 2026 (verificato)
- Staging GitHub Actions **38042930799 = SUCCESS** (codec, full scene regression gate, JVM storici e Android). Publisher firmato **38043017718 = SUCCESS** (chiave OTA originale, permission/manifest, asset + latest.json). GitHub Release fallback **38043121519 = SUCCESS** (SHA-256 verificato).
- Package stabile: **`com.overmind.zero.ota`**, versionName **`0.10.0-ota.47`**, versionCode **1047**, dimensione **122.008 byte**.
- SHA-256 verificato release + `updater/public/latest.json`: **`243cf70ce8a0164b63cef91c8755f846dcbdd9b7f37415290a88147d88f0da43`**.
- APK firmata diretta: `https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.10.0-ota.47/OVERMIND-ZERO.apk`.
- Non disinstallare la precedente: installazione **in-place** mantiene memoria e progressi in `zero_motorbrain_001`; poi aprire **PARTITE → SALVA QUI**, denominare lo ZERO attuale, e solo dopo **NUOVA PARTITA**. Non serve RESET TUTTO per creare nuovo individuo. Gli slot sopravvivono ai reset. Slot manuali congelati, working auto evolve.
- **Non ancora effettuata verifica comportamentale sul Samsung Galaxy S21**: QA automatico e APK firmata non garantiscono che UI, restore di forma/posizione o tutti i flussi manuali siano perfetti.

## OTA 0.10.1-ota.48 — 2026-10-10T11:17:03.858879+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1048).
- SHA-256: `90d3d1b89408611ab668837783f1ea00c9432a7c2a38d8ac22a597371645166c`.
- APK: `/apk/overmind-zero-1048-90d3d1b89408.apk`.

## OTA 0.10.2-ota.49 — 2026-10-10T12:55:55.422889+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1049).
- SHA-256: `238f4366e07eb4518cc6aed2b9c536ab8f77fa6ffc0070663b5c9caa5e335de2`.
- APK: `/apk/overmind-zero-1049-238f4366e07e.apk`.

## OTA 0.10.2-ota.50 — 2026-10-10T13:01:24.986995+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1050).
- SHA-256: `fc3dd7f8f754a7e7e530d64ba63b6d82ba1a24f19bfcea9fb17aa5a0f11d497d`.
- APK: `/apk/overmind-zero-1050-fc3dd7f8f754.apk`.

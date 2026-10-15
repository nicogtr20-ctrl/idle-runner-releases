## CANDIDATO ZERO 2.2 OTA160 — 15 ottobre 2026 (NON PUBBLICATO)
Correzione sperimentale della durata motoria del simbionte, senza movimenti prefabbricati: il nucleo 2.2 rimane l'unico arbitro, un nuovo modello `MotorDurationV22` impara se mantenere per 1, 2 o 4 intervalli una sua azione, con interruzione immediata per danni, riserva critica, esplorazione o scelte neurali chiaramente migliori. Persistenza opzionale `motor_duration_v160`, stessa app/salvataggi, valori reali visibili in Corpo/Evoluzione. Baseline Android: report `20261015-152553-130aa19184fe2`, 95.3–96.6% di cambi d'azione su due vite, ultimi 25 episodi con calo della locomozione e aumento dei contatti rossi. QA preliminare del ramo `fix/zero160-learned-motor-duration` **non eseguita**: Actions run `38765532027` fallito prima di qualsiasi step (infrastruttura). Nessuna APK160 firmata o prestazione post-correzione rivendicata. Paper completo: [ZERO_OTA160_LEARNED_DURATION_FORENSICS](ZERO_OTA160_LEARNED_DURATION_FORENSICS.md). Non unire al ramo che pubblica l'APK finché QA e build Android non sono verdi.

# CHANGELOG — storico OVERMIND ZERO

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

## OTA 0.10.5-ota.54 — statistiche 25-vs-25 e storico permanente (10 ottobre 2026)
- Pagina **STATISTICHE** riorganizzata in vita corrente, capacità attuale (ultime 25), trend 25-vs-25, storico totale, efficienza/minuto e grafici ultime 50.
- Il confronto di miglioramento si attiva a 50 vite complete, riducendo la sensibilità a una singola partita casualmente favorevole/sfavorevole.
- Nuovo accumulatore permanente per tutte le vite misurate: media storica e record non vengono più persi oltre la 50ª vita.
- Formato persistente `ls2` con lettura/migrazione `ls1`; chiave SharedPreferences invariata. Il passato non misurato di ZERO pre-telemetria viene marcato come incompleto, non inventato.
- “Tendenza evolutiva” è un indice diagnostico esterno e NON modifica reward/apprendimento.
- VersionName base portata a `0.10.5-ota`.
- QA staging GitHub Actions **38059707433**: LifeStatistics/JVM regression, controlli sorgente e Android Debug tutti verdi.
- Publisher stabile firmato **38060026469** interamente verde; versionCode **1054**, package `com.overmind.zero.ota`.
- APK pubblica: `/apk/overmind-zero-1054-34bd2eb5261f.apk`, **138.636 byte**, SHA-256 `34bd2eb5261f499b196037832dce744f37b7b3822a49a7f655c3b6347f0c4e7a`.
- Manifest e APK Vercel verificati HTTP 200 dopo la pubblicazione; GitHub Release `overmind-zero-v0.10.5-ota.54` creata prima dell'aggiornamento manifest.

## OTA 0.10.6-ota.55 — memoria di lavoro temporale ricorrente (10 ottobre 2026)
- Aggiunto `TemporalWorkingMemory.java`: 16 stati ricorrenti e 7.344 pesi appresi.
- ZERO può ora rappresentare contesti recenti differenti anche quando la lettura sensoriale presente è uguale.
- Predizione temporale e valore d'azione sono appresi da esperienze reali; una trace di 8 decisioni riceve credito solo da energia o spostamenti fisici realmente osservati.
- Il contributo temporale alla policy parte da zero e si abilita soltanto dopo >=25 transizioni held-out se l'errore contestuale è almeno 4% migliore del modello one-step; influenza limitata a ±0,45.
- Stato di lavoro azzerato alla morte, pesi conservati; riapertura della stessa scena ripristina il contesto serializzato.
- Nuova chiave `temporal_working_memory_v106`, inclusa nel reset MODELLO DEL MONDO e automaticamente negli slot completi.
- CERVELLO mostra parametri/transizioni/confidenza della memoria di lavoro. **STATISTICHE resta la 25-vs-25 della 0.10.5 senza modifiche.**
- versionName base `0.10.6-ota`.
- Staging run **38064278116** e **38064377747** verdi: test temporale sintetico, regressioni cognitive/fisiche precedenti, persistenza e Android Debug.
- Publisher stabile firmato **38064599838** interamente verde; versionCode **1055**, package `com.overmind.zero.ota`.
- APK pubblica `/apk/overmind-zero-1055-bdc0dc5fd1dc.apk`, **143.732 byte**, SHA-256 `bdc0dc5fd1dc9dce992d5a9cf139efd560421091f8ada01cec1bcbe47b48ff30`.
- Manifest e APK Vercel verificati HTTP 200; GitHub Release `overmind-zero-v0.10.6-ota.55` creata.

## OTA 0.10.7-ota.56 — skill temporali autonome (10 ottobre 2026)
- Aggiunto `TemporalSkillBank.java`: max 24 chunk di 2–8 azioni e relativi contesti ricorrenti.
- Candidati estratti esclusivamente da sequenze realmente eseguite prima di conseguenze positive; una singola occorrenza non può guidare ZERO.
- Consolidamento dopo almeno due ricorrenze compatibili.
- Replay soft: bias limitato, policy libera di scegliere altro, abort su divergenza del contesto.
- Successo del replay richiede un nuovo effetto reale; timeout/danno vengono contabilizzati come fallimenti.
- Nuova persistenza `temporal_skills_v107`, nuovo reset selettivo **ABILITA TEMPORALI** e diagnostica in CERVELLO/ANALISI.
- Nessuna modifica a STATISTICHE 25-vs-25.
- versionName base `0.10.7-ota`.
- Staging **38065646624** verde, incluse regressioni e Android Debug.
- Publisher stabile firmato **38066032322** verde; versionCode **1056**.
- APK pubblica `/apk/overmind-zero-1056-a433978f8420.apk`, **149.392 byte**, SHA-256 `a433978f842054c71c2744075f3c5a0105a4bad2f825b7ba7d6a7d1629e2030e`.
- Manifest/APK Vercel verificati HTTP 200; GitHub Release `overmind-zero-v0.10.7-ota.56` creata.


## Staging ZERO 0127 — 12 ottobre 2026 (NON pubblicato)
- Corretto il caricamento dei vecchi checkpoint spaziali con celle occupazione neutra `0`: le predizioni apprese e i contatori vengono recuperati, non azzerati silenziosamente.
- Isolati gli spostamenti energetici illegittimi dalle collisioni lastra/cassa e cassa/cassa: vecchi coperchi mobili soltanto con presa bilaterale reale; lastre soltanto con presa rossa reale confermata. Timer caricati non costituiscono prova di presa.
- Ridotta densità di lastre procedurali presso gli oggetti vitali senza rimuovere le costruzioni persistenti.
- Bilanciamento sperimentale: opportunità di nutrimento finito in scarsità ogni quattro espansioni; costo metabolico di tempesta da 0.23 a 0.18 e burden injury da 0.11 a 0.09 per secondo.
- Documentazione [EXPERIMENT_0127](EXPERIMENT_0127.md); test JVM, Android, firma APK, deploy OTA, prova reale S21 **ancora da certificare**. Ultima installazione confermata dall'utente: OTA 97.

## Release preparation — ZERO 2.0 passive observation, OTA 100 candidate (2026-10-13)

Official OTA path is `feature/overmind-inapp-updater` → `.github/workflows/overmind-ota-publish.yml` → owner-key signed APK → GitHub Release → `main/updater/public/latest.json` and immutable APK → Vercel manifest consumed by in-app AGGIORNA. This candidate is staged separately from research PR #31. Original `com.overmind.zero.ota` signature and persistent 12 saved slots must remain intact. VersionCode derives from the next official publisher run number (expected ≥1100), not the old signed experimental preview 1099.

The candidate includes exactly the already tested v20 passive observers, existing base motor policy, and `TelemetryGitHub.partition()` fix preserving `extras.zero20Passive` after multipart upload. No model advice biases the motor controller; no change to energy rules or sensor physics. Source-pinned stable checks are UPDATED to the SHA of the actually reviewed source files, never disabled; five new JVM regressions are added before official release signing.

The initial Galaxy S21 report confirms the original working profile is still present and v20 models have collected real observations. No independent skill or survival improvement is established. Release acceptance: independent staging JVM/Android QA GREEN, official stable publisher GREEN, signed APK certificate/package/version and SHA verified, downloadable manifest/APK responsive, user-controlled install and slot continuity confirmed on phone. If any gate fails, **do not claim availability through Aggiorna**.

## 2026-10-13 — ZERO 2.0 experimental shadow perception (NON RELEASE)
- Ramo indipendente `feature/zero20-perceptual-associations`, derivato da ZERO2 PR #31.
- Classi `PerceptualAssociationsV20` e `PerceptualAssociationsV20Regression`: raggruppamento incerto di contorni parziali realmente rilevati, esperienze fisiche osservate, persistenza atomica, esclusione holdout, test 440 decisioni immutate.
- Collegato `SensorimotorMind`/GameActivity, reset SPAZIO, CERVELLO e digest privato `zero20Passive`. Nessun valore/azione motoria imposto; no riconoscimento semantico certo.
- Non fa parte della OTA103 e non è una release. Le prove di compilazione CI e dispositivo vanno annotate separatamente; non attribuire un miglioramento biologico ai test JVM.


### Confirmed QA of first passive prototype — 2026-10-13
**Actual consolidated GitHub Actions run:** [38391024117](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38391024117), executable-source SHA `3621aedc7447d16286c903671593a72aba29288c`, **SUCCESS**. It compiled the full preexisting Java model set plus `PerceptualAssociationsV20` and its dedicated regression; passed the old body, grip, gate, plates, memory, save, privacy/telemetry, reset, physics and decision tests; verified 440 unchanged choice decisions and static absence of any new observer policy score/physics override; built the complete Android Debug APK with `assembleDebug`. Earlier staging run `38390903157` failed only the overly broad source-only assertion that also excluded passive `chosen()` bookkeeping; assertion was narrowed to forbid view/training/scoring within the action-choice policy and the final run passed. No bypass of any real physics test was used.

**What is not verified:** realistic rotation and 3D shape recognition on the Galaxy S21, same-instance tracking, independently proven causal manipulation, long-lived technique retention, battery/thermal overhead, installer signature, unchanged 12 slots after actual upgrade, and real behavioral benefit. **Promotion remains BLOCKED.** Release/OTA103 unchanged.

**OTA applicativa:** questo modulo percettivo passivo è stato selezionato per il canale AGGIORNA come candidato successivo alla 103. La build ufficiale resta soggetta a Java/Android QA, stessa firma e pubblicazione SHA/manifest prima di dirla disponibile.


## 13 ottobre 2026 — ZERO 2.2 attivo (candidato OTA117)
Richiesta utente: cervello rivisto funzionante e APK, x5/x20 fluidi, TEST MAX circa x29. Nuovo ramo `feature/zero22-active-recurrent-ota117` da `fe6d7497eb5b53055b46c474dd5470028bc97f88`. Leggere **[ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md)** per architettura, contratto sensoriale, replay, competenze, ottimizzazione TEST MAX e limitazioni. `RecurrentCoreV22` guida realmente la scelta prima dell'azione; i moduli 2.1 restano osservatori compatibili. Nessun reset del profilo o slot. 71 test JVM locali PASS; release, Android build e prova S21 da verificare separatamente. Non chiamare questo sistema Dreamer completo o intelligenza generale.

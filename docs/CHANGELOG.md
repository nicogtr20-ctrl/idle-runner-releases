## ULTIMO RILASCIO ZERO — OTA161 FIRMATA, 15 OTTOBRE 2026

**Stato verificato:** OTA161 `0.11.6-ota.161`, versionCode **1161**, applicationId `com.overmind.zero.ota` INVARIATO; [APK ufficiale](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1161-eea4e20b0975.apk), SHA256 `eea4e20b09751ff5f8655e6767067383aa0b5eaaf8a17ed1c17e9e3811e16796`, 451656 byte. [Publisher firmato SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38799181044), [verifica HTTPS pubblica indipendente SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38799646079): manifest HTTP200, APK HTTP200, dimensioni, applicationId e SHA256 esatti. Hosting Vercel production READY, alias ufficiale aggiornato. PR #71 integrata nel ramo `feature/zero22-active-recurrent-ota117`, senza migrazione distruttiva dei 12 slot o dei pesi 2.2.

**Corpo:** fisica con autononintersezione/repulsione membrane realmente materiali e compliance della tensione sulle estensioni; 320 **pesi corticali regionali** aggiuntivi, NON 320 neuroni, tutti inizializzati a zero; 84 pesi motori originali e cervello ricorrente con neurogenesi conservati. Contrazioni muscolari più coerenti e parametri regionali effettivamente salvati, visualizzati in Corpo/Parametri. Nessuna andatura o attrazione al cibo preprogrammata.

**Benchmark laboratorio:** [QA 38798647619](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38798647619) SUCCESS JVM e Android Debug; test stimolati con elettrodi solo in laboratorio: tre pseudopodi distinti, retrazione ~9,99U, netto su percorso controllato **21,26U** rispetto **13,73U** OTA160; nessuna dimostrazione ancora di locomozione autonoma sul telefono. Il test reale Galaxy S21 rimane necessario, specialmente NETTO/VIA, varietà delle forme, blu/rossi a pari minuti simulati.

**Installazione:** Impostazioni → Aggiorna, sopra app esistente; NON disinstallare né azzerare memoria. Dettagli: [audit OTA161](ZERO_OTA161_BODY_PHYSICS_AUDIT.md) e [PR #71](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/71).

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

## OTA 0.10.8-ota.57 — 2026-10-10T16:28:55.321179+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1057).
- SHA-256: `50b45e37a1cc7d801408c0800000a0f957ea2c89159afbff4fb0ba22df3eddc3`.
- APK: `/apk/overmind-zero-1057-50b45e37a1cc.apk`.

## ZERO 0.10.8-ota.57 — bug persistenza apprendimento temporale (10 ottobre 2026)
- GameActivity.save() ora salva temporal_working_memory_v106 tramite workingMemory.encode() e temporal_skills_v107 tramite temporalSkillBank.encode(), per evitare perdite delle nuove memorie al riavvio.
- Nessun cambiamento al controller fisico, ai salvataggi nominati (12), agli altri cervelli o ai metodi di apprendimento della 0.10.7.
- Nuovo TemporalPersistenceRegression e CI gate obbligatori: test 38067546249, 38067671221, 38067752564 SUCCESS.
- APK firmata v1057 da 150816 byte, SHA256 50b45e37a1cc7d801408c0800000a0f957ea2c89159afbff4fb0ba22df3eddc3; manifest e APK Vercel HTTP200. Prova Samsung S21 pendente. Paper docs/EXPERIMENT_0108.md.

## OTA 0.10.9-ota.58 — 2026-10-10T16:55:42.583124+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1058).
- SHA-256: `9bad1270ef3dae172858d55a1f90e3d92cc44c89e1559c9b18ce508f94b933e5`.
- APK: `/apk/overmind-zero-1058-9bad1270ef3d.apk`.

## OVERMIND ZERO 0.10.9-ota.58 — propriocezione reale del corpo (10 ottobre 2026)
- `BodyProprioception.java`: 32 input derivati da 96 punti fisici in 8 settori (pressione/contatto, adesione realmente tenuta, estensione, velocità radiale). Modello indipendente da 3088 pesi, predizioni verificate fuori addestramento e consigli limitati solo dopo gate; nessun concetto di porta/target codificato.
- `Morphology.Surface.touchPulse` conserva per breve tempo segnali da effettive collisioni con i dischi, senza comandare presa o cambiare forze. Consumo ridotto: scansione completa solo alla decisione.
- Nella vecchia policy S=26 i pesi e il formato restano. `GameActivity.save/load` e 12 slot includono `body_proprioception_v109`. In ANALISI nuova sezione corpo + reset autonomo; CERVELLO mostra parametri.
- Staging GitHub Actions 38069416680 SUCCESS; signed 38069513724 SUCCESS. APK 0.10.9-ota.58 code1058, 155816 byte sha256 9bad1270ef3dae172858d55a1f90e3d92cc44c89e1559c9b18ce508f94b933e5. Vercel prima 0.10.8, corretto con deployment Git manuale dpl_CHFimoyKSiLUR7mAn587GexvZypS e verificato HTTP200. S21 prova utente ancora necessaria.
- Canonical docs/EXPERIMENT_0109.md.

## OTA 0.11.0-ota.60 — 2026-10-10T17:22:05.295285+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1060).
- SHA-256: `076d315c484cd11d695a40219a26a4a30508de1478a3a31b5abee5a488fee84e`.
- APK: `/apk/overmind-zero-1060-076d315c484c.apk`.

## ZERO 0.11.0-ota.60 — percezione dello spazio circostante (10 ottobre 2026)
- `SpatialPerception.java`: 16 raggi di prossimità con occlusione reale (176 U) e memoria locale massimo 1024 celle costruite da osservazioni e odometria, non dalla mappa nascosta. Merge simultaneo dei raggi con precedenza a hit fisicamente osservati.
- Nuovo modello spaziale action-conditioned apprende dalle conseguenze di 16 azioni vere; consigli neutrali finché non migliora su almeno 30 heldout, poi max ±0.19. Nessun percorso/cancello predefinito.
- `SensorimotorMind` conserva esattamente 26 input e reti vecchie; `GameActivity.save/load` salva `spatial_perception_v110` nel vecchio profilo, disponibile automaticamente ai 12 slot. Reset `SPATIAL` separato, geometria della mappa solo per vita.
- ANALISI mostra mini-mappa vera delle celle viste, 16 distanze e confidenza; CERVELLO mostra pesi nuovi.
- Staging `38071087243` SUCCESS, GitHub signed `38071321701` SUCCESS. APK 0.11.0-ota.60 code1060, 162004 B, SHA256 `076d315c484cd11d695a40219a26a4a30508de1478a3a31b5abee5a488fee84e`, Vercel manifest/APK HTTP200. Galaxy S21 test pendente. Paper `docs/EXPERIMENT_0110.md`.

## OTA 0.11.1-ota.61 — 2026-10-10T17:52:57.955474+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1061).
- SHA-256: `8d8f121d985735ca3d7cac507007fb85a1ee192603309c2e867dc39e638e53c3`.
- APK: `/apk/overmind-zero-1061-8d8f121d9857.apk`.

## OVERMIND ZERO 0.11.1-ota.61 — lived cause/effect experiments (10 October 2026)
- Added `CausalExperiments`: learned per-action hypotheses from 13×13 previously observed occupancy grid aligned by body odometry; distinguish movement of ZERO versus change in surrounding solids, no privileged door identity.
- Observed blocker turning to free is a candidate only; physical crossing requires actual subsequent body movement into that location. Real energy/physical outcomes are assessed from existing sensorimotor path.
- Holdout gate >=30 with action-conditioned error at least 5% better than same-context baseline, randomized trials of multiple alternative actions, max learned choice bias ±0.15. No forced door plan.
- Save/restore new `ce1` / `causal_experiments_v111` per unchanged 12 named ZERO profiles. New isolated reset and ANALISI section; old physics, 26-input network, temporal memory/skills, proprioception/spatial map, other game saves and signing unchanged.
- QA 38073279447 & 38073332255 SUCCESS, official signed 38073421560 SUCCESS. Version code1061, size169280, SHA256 8d8f121d985735ca3d7cac507007fb85a1ee192603309c2e867dc39e638e53c3, live Vercel manifest/APK HTTP200. S21 test pending; see `docs/EXPERIMENT_0111.md`.

## OTA 0.11.2-ota.62 — 2026-10-10T19:15:49.866322+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1062).
- SHA-256: `bbec605688215fea99c450aeb91314e437f197456f6b104b15c2e3388cc07f38`.
- APK: `/apk/overmind-zero-1062-bbec60568821.apk`.
## OVERMIND ZERO 0.11.2-ota.62 — goal-directed instrumental learning (10 October 2026)
Added `InstrumentalNavigation`: energy outcomes per physically observed visual frequency AND real resultant motion per freely chosen motor command; no target coordinates, object identity or scripted red/blue preference. 3×8 visual sensors with correct angular sectors and physically accurate occlusion, no new inputs to original 26-sensor weight array. Heldout motor reliability gate and score bounded ±1.6. Actual chosen policy tested for energy-seeking and harm-avoidance after evidence. Removed direct `+.24f*realForward` motor reward from TD; actual observed energy provides instrumental value, tiny generic exploration preserved. Old Q bounded prior, no weights deleted. Cooling red remains visually observable. Stored `instrumental_navigation_v112` per unchanged 12 slots; separate NAVIGATION reset and ANALISI/CERVELLO screens. Legacy `DecisionBrain`/old `UnknownWorld.Mind` are passive archives, not active motor decision makers. Staging `38078898196` SUCCESS, signed `38078981840` SUCCESS, version1062, APK174824 bytes, SHA bbec605688215fea99c450aeb91314e437f197456f6b104b15c2e3388cc07f38, OTA/public Vercel HTTP200 after manual deployment `dpl_7ukm6M55KeVaxPq9naHZUpSTeUkT`. Real S21 improvement unknown; full `docs/EXPERIMENT_0112.md`.

## OTA 0.11.3-ota.63 — 2026-10-10T21:19:37.066695+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1063).
- SHA-256: `35d2ced944ac1125967b46ae67588a305b136984f1232edd59f14a9632b30d95`.
- APK: `/apk/overmind-zero-1063-35d2ced944ac.apk`.

## ZERO 0.11.3-ota.63 — Laboratorio comportamentale A/B isolato (10 ottobre 2026)
- Aggiunto `BehavioralLaboratory.java`: tre scenari fisici ripetibili energia, pericolo, porta, 3 varianti appaiate ciascuno, nav strumentale ON/OFF, 18 prove × 32 s simulati. Brain e motor controllers clonati da profilo salvato, stessi componenti fisici veri Body/Surface/ScarcityLab e stessi sensori reali; misure di energia, danni, contatti, vicinanza, porta e attraversamenti reali.
- `SensorimotorMind.setNavigationAdviceEnabled(false)` esclude **solo il bias decisionale in controllo benchmark**; valore di default true nel gioco e vecchi S=26, memorie, motor weights e OTA firma immutati.
- Nuova `BehavioralLabActivity` privata, link da ANALISI, avvio thread in background con progresso/annullamento. Report in preferenze `zero_behavior_lab_v113`, nessuna scrittura su `zero_motorbrain_001` né sugli slot personaggi.
- QA staging `38086989808` SUCCESS (fisica e 18 trial, tutti i test precedenti e compilazione Android). Firma/publisher `38087100529` SUCCESS, versionCode1063, signed APK184108 bytes checksum 35d2ced944ac1125967b46ae67588a305b136984f1232edd59f14a9632b30d95; JSON/APK Vercel HTTP200. Non testato su Galaxy S21 e non prova ancora miglioramenti in-game. `docs/EXPERIMENT_0113.md`.

## OTA 0.11.4-ota.64 — 2026-10-10T21:58:42.768367+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1064).
- SHA-256: `255727583121d49c07463a29aea07d4f99db06912cdc2f6aeff40cdd103cdab8`.
- APK: `/apk/overmind-zero-1064-255727583121.apk`.

## OTA 0.11.5-ota.66 — 2026-10-10T22:41:25.278498+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1066).
- SHA-256: `e393e44bdccd68bd6ecb5e9020227a5906760b7ea44e3e6e055e39a16040ec1c`.
- APK: `/apk/overmind-zero-1066-e393e44bdccd.apk`.

## OVERMIND ZERO 0.11.5-ota.66 — reazione appresa a danni reali (10 ottobre 2026)
Nuova `AdaptiveSafety`: solo dopo contatto con perdita energetica reale e frequenza sperimentata negativa, bias prudente ±0.95 da veri effetti degli attuatori provati su campioni heldout e stimoli visibili, per 4.5s simulati. Una perdita può abbreviare la decisione corrente a 0.18s simulati rispetto a 0.65, senza contaminare l'apprendimento del normale moto da campioni brevi. Esplorazione temporaneamente preferisce azioni con punteggio totale entro 0.75 dal massimo, non comanda una fuga e non sopprime la curiosità ordinaria. Conta recupero solo dopo vero movimento corpo e luce dannosa meno intensa; le scene non vengono memorizzate durante la vita successiva. Nuova persistenza `adaptive_safety_v115`, pannello ANALISI e reset SAFETY; tutte le 12 partite, modelli S=26 e memorie precedenti conservati. Lab 5 scenari (aggiunti ROSSI INTORNO e BLU PIU ROSSO), 30 prove fisiche A/B, misura danni reiterati, zona fuga, micro-spostamenti, veri contatti. Staging `38092034409` e publisher `38092242587` SUCCESS; versione 1066, APK194712B SHA e393e44bdccd68bd6ecb5e9020227a5906760b7ea44e3e6e055e39a16040ec1c, manifest+APK live HTTP200. Nessun successo comportamentale su Galaxy S21 ancora dimostrato. Vedi `docs/EXPERIMENT_0115.md`.

## OTA 0.11.6-ota.67 — 2026-10-10T23:52:45.555065+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1067).
- SHA-256: `bdb5060eb291cd43a5d1276076009bef8f87afbed98df433d29cf1501d0e5b8c`.
- APK: `/apk/overmind-zero-1067-bdb5060eb291.apk`.

## OTA 0.11.6-ota.68 — 2026-10-11T11:02:51.476277+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1068).
- SHA-256: `96b6c10ef394be2912b092a712b9f2225b72b0f981731578377934bc161a7fca`.
- APK: `/apk/overmind-zero-1068-96b6c10ef394.apk`.

## OTA 0.11.6-ota.69 — 2026-10-11T11:25:49.893750+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1069).
- SHA-256: `291a33031d3137e47ccc044b7e35fc34658e015b4c7593f635e15203b2a48b57`.
- APK: `/apk/overmind-zero-1069-291a33031d31.apk`.

## OTA 0.11.6-ota.70 — 2026-10-11T12:51:46.824582+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1070).
- SHA-256: `9d12bd1c266017cde8a20d90067f6a106c60fddc45209311a3c3d25275f3ae33`.
- APK: `/apk/overmind-zero-1070-9d12bd1c2660.apk`.

## OTA 0.11.6-ota.71 — 2026-10-11T13:28:09.843796+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1071).
- SHA-256: `f54fc3144a4626cace8d1bfdc120a5eebc6768df5ccf0c3b624d0536e48763f3`.
- APK: `/apk/overmind-zero-1071-f54fc3144a46.apk`.

## OTA 0.11.6-ota.72 — 2026-10-11T13:55:18.114825+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1072).
- SHA-256: `14426811e02107792b55b045b9a834d13bb2e4c04cbce5454f80ccfcab150599`.
- APK: `/apk/overmind-zero-1072-14426811e021.apk`.

## OTA 0.11.6-ota.74 — 2026-10-11T14:19:39.431626+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1074).
- SHA-256: `81c5e312e59036c036adbbbbb6ba05ab90d22e6df447daf135b80081bfc851e5`.
- APK: `/apk/overmind-zero-1074-81c5e312e590.apk`.

## OTA 0.11.6-ota.75 — 2026-10-11T14:49:39.632746+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1075).
- SHA-256: `db5045f32c442efe9ff0d25c2756266489cdc898927cf99c7d1d35f289f6efd0`.
- APK: `/apk/overmind-zero-1075-db5045f32c44.apk`.

## OTA 0.11.6-ota.76 — 2026-10-11T18:55:10.148430+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1076).
- SHA-256: `41bd576d6dba04387f568a0f5c6690cb6751aa563b91203e21dffa9cf776ec53`.
- APK: `/apk/overmind-zero-1076-41bd576d6dba.apk`.

## OTA 0.11.6-ota.78 — 2026-10-11T19:21:49.038881+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1078).
- SHA-256: `fbbbc58e7d06eebd2f16ca6ca66acc6f45d34df291e2d47db2b5e44b4e819129`.
- APK: `/apk/overmind-zero-1078-fbbbc58e7d06.apk`.

## OTA 0.11.6-ota.80 — 2026-10-11T20:27:04.374141+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1080).
- SHA-256: `8f5295c5c59ff7c34842366fcc16259b9680f2a72f804ca12c7ef61cfec23992`.
- APK: `/apk/overmind-zero-1080-8f5295c5c59f.apk`.

## OTA 0.11.6-ota.82 — 2026-10-11T20:45:03.915419+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1082).
- SHA-256: `2ed4c7429ccafcd26429a9e4444e56d1e4ef8c1a32f77c57579914343050b2b1`.
- APK: `/apk/overmind-zero-1082-2ed4c7429cca.apk`.

## OTA 0.11.6-ota.83 — 2026-10-11T21:26:12.360248+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1083).
- SHA-256: `ccbba18f618369cdcd644809f61bd4927fe4862704d78aa04d356ac215de6679`.
- APK: `/apk/overmind-zero-1083-ccbba18f6183.apk`.

## OTA 0.11.6-ota.84 — 2026-10-11T22:19:14.137596+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1084).
- SHA-256: `edfa2ee566e3553f92866f124a49c8ff2211a73bc15aa13574e1bb04cdca1a40`.
- APK: `/apk/overmind-zero-1084-edfa2ee566e3.apk`.

## OTA 0.11.6-ota.85 — 2026-10-11T23:14:48.843879+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1085).
- SHA-256: `ffa41275af0be408f69f12245a25cc87230b44e296dbc23623b45a94148d217f`.
- APK: `/apk/overmind-zero-1085-ffa41275af0b.apk`.

## OTA 0.11.6-ota.89 — 2026-10-12T00:02:58.210407+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1089).
- SHA-256: `3d601e41df0cfc493e404683cb100c2984d38ffe25a9d226dddac7cbfa58cff0`.
- APK: `/apk/overmind-zero-1089-3d601e41df0c.apk`.

## OTA 0.11.6-ota.91 — 2026-10-12T00:39:00.604378+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1091).
- SHA-256: `39a9bc2a33ea0ce8cf56a65e33dae0f03a878c52fa41011fc8b3ec353ab58679`.
- APK: `/apk/overmind-zero-1091-39a9bc2a33ea.apk`.

## OTA 0.11.6-ota.93 — 2026-10-12T11:42:46.703750+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1093).
- SHA-256: `060c51fb3c2fb6f865c5728b529e368f681e4747fd3d51453afd46e416804206`.
- APK: `/apk/overmind-zero-1093-060c51fb3c2f.apk`.

## OTA 0.11.6-ota.94 — 2026-10-12T12:23:38.664422+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1094).
- SHA-256: `df0ceb1b00934f0180c343b83793631aa2e4ec950a7301308696ef3431f78c3f`.
- APK: `/apk/overmind-zero-1094-df0ceb1b0093.apk`.

## OTA 0.11.6-ota.95 — 2026-10-12T12:50:57.263996+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1095).
- SHA-256: `5eaedb870372c317a467d94bc415ce02c017e14e00cff7519588994d9f103edd`.
- APK: `/apk/overmind-zero-1095-5eaedb870372.apk`.

## OTA 0.11.6-ota.96 — 2026-10-12T13:20:50.591993+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1096).
- SHA-256: `0915185f8986addf0245c13a815a87603395be3e6680ee7f9b017ed81e27b5f1`.
- APK: `/apk/overmind-zero-1096-0915185f8986.apk`.

## OTA 0.11.6-ota.97 — 2026-10-12T15:09:31.166083+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1097).
- SHA-256: `7bea2e4b8702817d754bb438edfa46242758e34fc4760c01750c125d74693c03`.
- APK: `/apk/overmind-zero-1097-7bea2e4b8702.apk`.

## OTA 0.11.6-ota.99 — 2026-10-12T16:23:19.814725+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1099).
- SHA-256: `02c6670305764209eb6d3daff9805a33a131a264e2bc2a917925b1f8f19376f6`.
- APK: `/apk/overmind-zero-1099-02c667030576.apk`.

## OTA 0.11.6-ota.102 — 2026-10-12T23:07:46.662161+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1102).
- SHA-256: `89c30159b3318e3f7cac2050a49103e1b3b327a885ff0a75145ed77c3a323254`.
- APK: `/apk/overmind-zero-1102-89c30159b331.apk`.

## OTA 0.11.6-ota.103 — 2026-10-13T10:25:55.639727+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1103).
- SHA-256: `39c4f1107618447b806232c31dc67e21dc45e1a520fffdd2efa115844c0261f3`.
- APK: `/apk/overmind-zero-1103-39c4f1107618.apk`.

## OTA 0.11.6-ota.105 — 2026-10-13T12:18:52.398652+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1105).
- SHA-256: `57ca7827638fd7a4cc09863c994f03916dadb98c8aa5395f82607e643bbe7917`.
- APK: `/apk/overmind-zero-1105-57ca7827638f.apk`.

## OTA 0.11.6-ota.109 — 2026-10-13T15:26:23.628307+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1109).
- SHA-256: `8804da1489eba04c6d53a5f4b91a92e365e52c02e14ce705f4ab9df0cdbaecad`.
- APK: `/apk/overmind-zero-1109-8804da1489eb.apk`.

## OTA 0.11.6-ota.110 — 2026-10-13T16:24:34.005941+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1110).
- SHA-256: `5e2ec3b0133a7a0bc1e753ac4d1ed207ec28bcb1614d593c24d6d06eeb8a0c4f`.
- APK: `/apk/overmind-zero-1110-5e2ec3b0133a.apk`.

## OTA 0.11.6-ota.111 — 2026-10-13T16:56:31.650754+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1111).
- SHA-256: `f875fae40a7ed0209ed8e1aca5cd823b714a1cf2e9f57392d27969653053d3e1`.
- APK: `/apk/overmind-zero-1111-f875fae40a7e.apk`.

## OTA 0.11.6-ota.112 — 2026-10-13T17:25:31.959247+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1112).
- SHA-256: `35829304a1245931eef55fd494170d03ca7760a6c00f697581601b497d32ce61`.
- APK: `/apk/overmind-zero-1112-35829304a124.apk`.

## OTA 0.11.6-ota.115 — 2026-10-13T19:57:25.584354+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1115).
- SHA-256: `71741340eaa7c8edf81be87843f3cd06776819f83f1d44aa444fa1e30e7a5fc2`.
- APK: `/apk/overmind-zero-1115-71741340eaa7.apk`.

## OTA 0.11.6-ota.116 — 2026-10-13T20:11:32.102571+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1116).
- SHA-256: `1d1475650e2878c0a3eb91bc712a89e6be3accc8e096ec8c9570257dcb68f9c1`.
- APK: `/apk/overmind-zero-1116-1d1475650e28.apk`.

## OTA 0.11.6-ota.118 — 2026-10-13T21:09:11.605196+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1118).
- SHA-256: `c12528432b0e59432a390847deb3d1482d1ea4760c59d1b19049abea11d61f4c`.
- APK: `/apk/overmind-zero-1118-c12528432b0e.apk`.


## ZERO 2.2 attivo — OTA118 verificata, 13 ottobre 2026
Versione APK `0.11.6-ota.118`, codice **1118**, package `com.overmind.zero.ota`. Fonte rilasciata `685031f7b8856f8eb351401b99e22b0de9604330`, PR #41; branch ufficiale `feature/overmind-inapp-updater`. Nucleo ricorrente prima della scelta reale, corpo/raggi/energia nella previsione, replay persistente con holdout separati, recupero temporaneo di tecniche consolidate con interruzione e isolamento delle finestre corporee, campionamento sensoriale ai confini della decisione per TEST MAX. È un reservoir fisso con readout appresi (40.560 pesi), non Dreamer completo o superintelligenza dimostrata.

Verifiche: **70 test JVM locali PASS**, controlli del publisher PASS, [Android staging 38467622938 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467622938), [publisher firmato 38467965975 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467965975). APK pubblica HTTP200, **382.200 byte**, SHA-256 `c12528432b0e59432a390847deb3d1482d1ea4760c59d1b19049abea11d61f4c`; firma V2 verificata crittograficamente e fingerprint del certificato identico alla OTA116: `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`. Manifest pubblico verificato a 1118. [APK immutabile](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1118-c12528432b0e.apk). [Release GitHub](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.118).

Installare sopra l'app attuale tramite AGGIORNA o APK; non disinstallare e non azzerare le memorie. Nuovo tasto **CONFRONTO CERVELLO 2.2** in CERVELLO/ANALISI: 24 prove fisiche isolate (12 coppie) sul profilo salvato; stesso seed/scena, controllo precedente contro core attivo+recupero. Non modifica il personaggio; report separato `zero_core_lab_v22`, upload solo esplicito tramite CONDIVIDI DATI, conservato nei multipart. L'arena ha controllori fissi, senza tempeste/streaming: screening, non equivalenza integrale al mondo.

**Ancora da verificare sul Galaxy S21:** installazione in-place e continuità dei 12 slot, FPS/termica e nuovo moltiplicatore TEST MAX rispetto al x29 riportato, risultati A/B, retention e adattamento a scene nuove. Nessun uplift comportamentale è già dimostrato. [Paper completo e checklist](ZERO22_ACTIVE_CORE_AND_DEVICE_VALIDATION.md). Il publisher 117 sul diverso branch `feature/zero22-active-recurrent-ota117` non è questa release: ultimo rilascio verificato qui è OTA118.

## OTA 0.11.6-ota.119 — 2026-10-13T21:18:16.833904+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1119).
- SHA-256: `6593561d5593d1b047b73022506be0203c9fe603b46f1f7de9cd7ba52b215b68`.
- APK: `/apk/overmind-zero-1119-6593561d5593.apk`.


## ULTIMO STATO VERIFICATO — ZERO 2.2 / OTA119 — 13 ottobre 2026, 23:20 CEST

Richiesta utente: APK pronta con centro decisionale rivisto attivo, conservazione del personaggio e ottimizzazione TEST MAX (misura precedente S21 circa x29). **Rilascio realmente concluso**: versione `0.11.6-ota.119`, codice1119, package `com.overmind.zero.ota`.
- Sorgente APK esatto: `582030833113f698933fbe83327c843e992a6ea0`, ramo `feature/zero22-active-recurrent-ota117`, [PR42 draft](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/42). Il nome del ramo conserva il numero candidato117; la release effettiva è119.
- Publisher [38469244134](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38469244134) **SUCCESS**: tutti71 entrypoint JVM, tutti26 gate Python originali aggiornati, Android Debug+Release firmata, permessi/provider, GitHub Release e manifest main. Il primo117/38467848821 aveva passato JVM/Debug ma falliva una stringa statica della stabilità motoria; non pubblicò APK.
- [APK pubblica](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1119-6593561d5593.apk),382708byte; SHA256 `6593561d5593d1b047b73022506be0203c9fe603b46f1f7de9cd7ba52b215b68`. Scaricata e hash/dimensione verificati. ApkVerifier verifica crittografica API24+: firme119 e116 valide, stesso certificato SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- Manifest pubblico HTTP200/code1119 verificato, Vercel productionREADY progetto `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, teamprime-1323, alias corretto, deployment `dpl_FhBaHF4P4WVcQ3u1ZpCCgRDEzS7z`, commit hosting `111dbcf3bafb6e35933e882620ab217a020712b9`. DEX pubblico contiene nucleo, esecutore skill, confronto esteso, observationDue e learningBlend.
- Installare SOPRA tramite Impostazioni→AGGIORNA APP o link diretto, **non disinstallare**. Prova APK→APK, dodici slot, velocità/temperatura sul S21 non ancora osservate.
- La nuova priorità comprende la migrazione graduale della policy esperta: senza evidenza nuova preserva scelte/epsilon storico; aumenta quota del nucleo con train reali. Non riavvia esplorazione vergine su ZERO esperto. Non ricompilare un ramo precedente perdendo questa correzione o il confronto esteso.
- Leggere [ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md), [ZERO22_PHYSICAL_SCREENING](ZERO22_PHYSICAL_SCREENING.md), [ZERO22_JVM_RESULTS](ZERO22_JVM_RESULTS.json). È un nucleo ricorrente leggero con reservoir fisso e readout appresi, non Dreamer completo/RSSM o intelligenza generale. Autorità prima dell'azione, memoria corpo+raggio, previsione energetica, replay recente/raro, recupero contestuale con feedback.
- Screening fisico vergine12pareggi. Primo checkpoint esperto0vittorie/1sconfitta/11pareggi, regressione di transizione documentata e corretta. Stesso checkpoint dopo correzione12pareggi su energia/danni/sopravvivenza, prese opposte-5.917frame medi. **Nessun uplift dimostrato**. Fixture sintetica37/40 scelte benefiche dimostra soltanto autorità/apprendimento del nucleo.
- Sul telefono ANALISI→LABORATORIO COMPORTAMENTALE (18 prove32s) e CONFRONTO2.2ESTESO/CASSE (24prove fino170s); copie isolate, report distinti inclusi in Condividi dati. TEST MAX rimane separato, conferma/STOP/thermal, substep1/60; sensi completi solo ai confini0.65s, nessun substep fisico saltato. Velocità maggiore di x29 non ancora misurata.
- Memorie/slot legacy preservati, chiave opzionale `recurrent_core_v22` atomica compressa. Riavvio conserva hidden e cancella solo pending non verificabile. Esperienze heldout e immaginate mai nel replay. Main conserva Idle Runner; soltanto hosting+paper aggiornati.


## OTA 0.11.6-ota.122 — 2026-10-13T23:05:17.018875+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1122).
- SHA-256: `24d7e3357a184e43fedf9cf1516a1007ff1ef520e484bac7e58ad329895bc419`.
- APK: `/apk/overmind-zero-1122-24d7e3357a18.apk`.

## OTA 0.11.6-ota.123 — 2026-10-14T10:09:09.432707+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1123).
- SHA-256: `c00d288bf78903d89f2f11f725b048679d9a4b6d28bbd0e64db2e4174ea3e99c`.
- APK: `/apk/overmind-zero-1123-c00d288bf789.apk`.

## OTA 0.11.6-ota.124 — 2026-10-14T10:37:51.748754+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1124).
- SHA-256: `7a9d4d3f8f46fe0a89a2d0020418c6426f8dbf615efb3036c250e43fc7ef939d`.
- APK: `/apk/overmind-zero-1124-7a9d4d3f8f46.apk`.

## OTA 0.11.6-ota.126 — 2026-10-14T12:47:02.640411+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1126).
- SHA-256: `d703ee9cb094a5aaac8ba723766f7463bc99e63261c8311e318ff7b99d28f264`.
- APK: `/apk/overmind-zero-1126-d703ee9cb094.apk`.

## OTA 0.11.6-ota.127 — 2026-10-14T13:40:54.117187+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1127).
- SHA-256: `d879edcd15e7b6e72873b06c278110c518a8c36e9df9269227aed1d79708e4e4`.
- APK: `/apk/overmind-zero-1127-d879edcd15e7.apk`.

## OTA 0.11.6-ota.129 — 2026-10-14T15:25:28.063101+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1129).
- SHA-256: `8588e7f28dcf991049a6b4e1c79a87b6f5adc6bb1f598557982c11296a76b6c2`.
- APK: `/apk/overmind-zero-1129-8588e7f28dcf.apk`.

## OTA 0.11.6-ota.130 — 2026-10-14T16:15:51.163573+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1130).
- SHA-256: `5b9d61e2d2b83e1df1b731aa34d8aeac3cf09c7d4c3086d9119b324bbd8bca71`.
- APK: `/apk/overmind-zero-1130-5b9d61e2d2b8.apk`.

## OTA 0.11.6-ota.131 — 2026-10-14T17:04:33.068621+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1131).
- SHA-256: `8984cbe90c5af831b440346eb37933aa982556b5bdc9b9dcddc98e374a752bfd`.
- APK: `/apk/overmind-zero-1131-8984cbe90c5a.apk`.

## OTA 0.11.6-ota.135 — 2026-10-14T19:05:25.694598+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1135).
- SHA-256: `f306246fbac259cb518277a1fca253372a09b5c9ccefb89855c1c838cceae109`.
- APK: `/apk/overmind-zero-1135-f306246fbac2.apk`.

## OTA 0.11.6-ota.137 — 2026-10-14T20:49:21.279887+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1137).
- SHA-256: `0e99613779ec73b019a8b01274fea2b7592b6a2c0546b257fd83e4b5d3f97dea`.
- APK: `/apk/overmind-zero-1137-0e99613779ec.apk`.

## OTA 0.11.6-ota.143 — 2026-10-14T22:34:33.159773+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1143).
- SHA-256: `a5acce3b97f895387990cb1ab05db190a01303a9288da12efcf1a8e779ad5c8f`.
- APK: `/apk/overmind-zero-1143-a5acce3b97f8.apk`.

## OTA 0.11.6-ota.144 — 2026-10-14T22:44:37.701445+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1144).
- SHA-256: `61f359419902b4a7f5fe808b092b941fdde9df40143f694197539766284d98ff`.
- APK: `/apk/overmind-zero-1144-61f359419902.apk`.

## OTA 0.11.6-ota.146 — 2026-10-14T23:18:58.802353+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1146).
- SHA-256: `867fd07eff6d6676e5e8a4a538dd39d604086ea08c3102a2efd2bd8832583466`.
- APK: `/apk/overmind-zero-1146-867fd07eff6d.apk`.

## OTA 0.11.6-ota.147 — 2026-10-14T23:34:31.936539+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1147).
- SHA-256: `27d25ee5d7f07027af571b42996f67359fcb7f49e0f0cc982ef19c657f048f8e`.
- APK: `/apk/overmind-zero-1147-27d25ee5d7f0.apk`.

## OTA 0.11.6-ota.148 — 2026-10-14T23:51:07.586677+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1148).
- SHA-256: `34563e1ee2a2943ef14a885a9668acfe75cd13b54b8f937d205fff0b0ab105ea`.
- APK: `/apk/overmind-zero-1148-34563e1ee2a2.apk`.

## OTA 0.11.6-ota.149 — 2026-10-15T00:12:14.439134+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1149).
- SHA-256: `bf1aa0388c8ba90ac75cce9ee1976583dd88001f18df6f3b14de4a4da54fe4a7`.
- APK: `/apk/overmind-zero-1149-bf1aa0388c8b.apk`.

## OTA 0.11.6-ota.150 — 2026-10-15T00:17:47.865513+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1150).
- SHA-256: `10e679b397e88d0ba8c6baa31ee696324f9840b73fbbffc66abce265e58d5e83`.
- APK: `/apk/overmind-zero-1150-10e679b397e8.apk`.

## OTA 0.11.6-ota.151 — 2026-10-15T08:16:16.353768+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1151).
- SHA-256: `56bd400aa05c6d3b807fc13f040d77bd2aa50921aefb9d81eb874418117a6c27`.
- APK: `/apk/overmind-zero-1151-56bd400aa05c.apk`.

## OTA 0.11.6-ota.152 — 2026-10-15T09:41:21.999944+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1152).
- SHA-256: `2334fd2e7244caa5f6d09fe2a0ee29608438cf7e16dca15ea44f65c3d75ab640`.
- APK: `/apk/overmind-zero-1152-2334fd2e7244.apk`.

## OTA 0.11.6-ota.153 — 2026-10-15T10:22:26.659492+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1153).
- SHA-256: `167ced105a13b92ace70f283681e88db8ac2c1a6fd6aba1d6c9b9eebbaeeca6b`.
- APK: `/apk/overmind-zero-1153-167ced105a13.apk`.

## OTA 0.11.6-ota.154 — 2026-10-15T11:26:52.620172+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1154).
- SHA-256: `c849b0629e29955af69fb40e8d44039cf44cbacb0b2e1f0c2ae0e10683d954ca`.
- APK: `/apk/overmind-zero-1154-c849b0629e29.apk`.

## OTA 0.11.6-ota.155 — 2026-10-15T12:28:01.029327+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1155).
- SHA-256: `5ab73ca0a4e9a183641868736cef85dc38fd880d83f64488e7d865e829d1ef8d`.
- APK: `/apk/overmind-zero-1155-5ab73ca0a4e9.apk`.

## OTA 0.11.6-ota.156 — 2026-10-15T13:08:50.109237+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1156).
- SHA-256: `42cb65d9880551538b882a0b84ace3fd601770a6fd80359dc3dc9f5e9586a138`.
- APK: `/apk/overmind-zero-1156-42cb65d98805.apk`.

## OTA 0.11.6-ota.157 — 2026-10-15T13:44:15.786027+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1157).
- SHA-256: `80601672deed24899f560971675f6d2d7dc2c9f8c4ebb98e9dd4169770227218`.
- APK: `/apk/overmind-zero-1157-80601672deed.apk`.

## OTA 0.11.6-ota.158 — 2026-10-15T14:27:17.469795+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1158).
- SHA-256: `11ce827a1990f43f8ccaab93409e87b62d7122ae1d49bed875b2ea4ca843adef`.
- APK: `/apk/overmind-zero-1158-11ce827a1990.apk`.

## OTA 0.11.6-ota.159 — 2026-10-15T15:19:53.621240+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1159).
- SHA-256: `a63009251d3439f628e26f56dca8b689264393370ec4f7757d497080b0f8c33a`.
- APK: `/apk/overmind-zero-1159-a63009251d34.apk`.

## OTA 0.11.6-ota.160 — 2026-10-15T16:27:54.092132+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1160).
- SHA-256: `e40804e1e7532f7250bdaa25e30cb1ee729b827aeda82379ad5f53c4dd68dfd1`.
- APK: `/apk/overmind-zero-1160-e40804e1e753.apk`.

## OTA 0.11.6-ota.161 — 2026-10-15T20:08:02.437056+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1161).
- SHA-256: `eea4e20b09751ff5f8655e6767067383aa0b5eaaf8a17ed1c17e9e3811e16796`.
- APK: `/apk/overmind-zero-1161-eea4e20b0975.apk`.

## OTA 0.11.6-ota.162 — 2026-10-15T20:51:19.529622+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1162).
- SHA-256: `b179186aadfb006c3bce9051cf8af9f4f58d85616619bbe800bb90c2e5639720`.
- APK: `/apk/overmind-zero-1162-b179186aadfb.apk`.

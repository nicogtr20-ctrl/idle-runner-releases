# PRIORITÀ SCIENTIFICA ATTUALE: [ZERO21_VALIDATION_RESULTS.md](ZERO21_VALIDATION_RESULTS.md) contiene il run 38455726406 SUCCESS, 440 decisioni identiche e 12 A/B su casse reali con 0 vantaggi, e il run di sopravvivenza 170 s 38456274262 da verificare alla conclusione. NO OTA oltre 112, NO merge della PR #39 senza prove attive di beneficio.

# ZERO 2.1 — HANDOFF OPERATIVO PER NUOVE CHAT (LEGGERE PER PRIMO)
**Aggiornamento:** 13 ottobre 2026, sera Italia. **Non confondere questa ricerca con un'OTA.** Questo file è il punto di ingresso *attuale* per il ramo feature/zero21-unified-cognition.

## 0 — Messaggio da copiare in una nuova chat
"Riprendi OVERMIND ZERO 2.1 dalla repository privata nicogtr20-ctrl/idle-runner-releases. Prima leggi integralmente i documenti docs/ZERO21_HANDOFF_NEXT_CHAT.md, docs/ZERO21_UNIFIED_COGNITION_MASTER.md e docs/ZERO21_IMPLEMENTATION_LOG.md sul branch feature/zero21-unified-cognition. Poi leggi AGENTS.md, OVERMIND_START_HERE.md, i paper ZERO20 sulla versione stabile e docs/ZERO_20_INTELLIGENCE_RESEARCH.md nel branch feature/zero-20-hierarchical-intelligence. Guarda lo stato reale della CI, codice e manifest Android: non usare vecchi handoff come se fossero gli ultimi. Prosegui costruendo il cervello integrato con esperimenti reali e paper aggiornati; preserva il cervello originale e tutti i dodici slot. Non pubblicare un'APK né dichiarare nuova intelligenza senza QA e prove A/B. Gli aggiornamenti destinati al telefono devono arrivare dal pulsante AGGIORNA con firma originale, non tramite APK manuale."

## 1 — Identificatori canonici (non indovinare i nomi)
- Repository privata: https://github.com/nicogtr20-ctrl/idle-runner-releases
- Android stabile *del codice*: feature/overmind-inapp-updater
- Android release metadata, Vercel manifest e whitepaper storico: main
- Base pubblica controllata all'inizio: 0.11.6-ota.112, versionCode 1112, com.overmind.zero.ota, APK SHA256 35829304a1245931eef55fd494170d03ca7760a6c00f697581601b497d32ce61
- Ramo NUOVO integrazione: feature/zero21-unified-cognition (derivato dalla stable OTA112, NON dal ramo vecchio ZERO20)
- Ramo VECCHIO solo ricerca, DIVERGENTE: feature/zero-20-hierarchical-intelligence; PR #31 rimane bozza e non deve essere unito in blocco.
- Workflow QA ricerca: .github/workflows/zero21-unified-qa.yml (nessuna firma/distribuzione).
- Workflow publisher ufficiale: .github/workflows/overmind-ota-publish.yml sulla stable.
- Ultimo manifest produttivo da verificare OGNI VOLTA: main/updater/public/latest.json; endpoint https://overmind-zero-updates.vercel.app/latest.json
- Telemetria privata inviata SOLO su richiesta esplicita dell'utente: branch zero-telemetry (non inviare automaticamente, non pubblicare dump di pesi).
- App e dati sul Samsung Galaxy S21. Package, certificato APK, contatore versionCode e salvataggi NON vanno alterati senza un vero protocollo di migrazione.

## 2 — Obiettivo autentico voluto dall'utente
ZERO non deve essere un personaggio comandato da script, bensì una macchia fluida autonoma che apprende dall'esperienza a percepire sé stesso e il mondo, sperimentare, manipolare e usare quanto apprende. Gli esempi di cassa/cancello/energia sono STRESS TEST, non istruzioni o categorie cablate nel cervello. Non fornire azioni "apri porta", "affera blu" o target segreti. Vogliamo un solo sistema cognitivo integrato che colleghi sensazioni, memorie, previsione, valutazione di costo/energia, azioni e correzioni dopo errori. Deve imparare tecniche robuste, non dimenticarle quando cambia la probabilità di trovare energia, e correggere la strategia quando il mondo muta. La forma resta composta da tessuto fluido indipendente (96 punti membrana, 10 masse interne), non corpo a nucleo rigido. Gioco Android con UI neon molto compatta come OTA112. In gioco il cervello impara autonomamente; l'utente è solo osservatore.

L'utente ha anche imposto un requisito di continuità: mantenere paper esaustivi, storico tecnico accurato e questo HANDOFF su GitHub dopo ogni cambiamento; non chiedergli continuamente di ripetere progetti/scelte. Quando una modifica è davvero destinata al telefono, terminare anche l'intera pubblicazione ufficiale via AGGIORNA con verifiche, ma NON distribuire esperimenti che non superano i gate. Non dire "fatto" quando c'è solo un commit.

## 3 — Cosa è stato REALMENTE implementato sul ramo nuovo
- Nuova classe UnifiedCognitionV21.java: unico registro di stato percettivo del corpo/ambiente e provenienza action→physical effect→contact energy; conserva distinzione abilità meccanica vs contatto energetico. Quattro contesti derivati SOLO da 26 input originali: riserva bassa/alta x ostacolo realmente prossimo/distante. Tiene 64 celle (4 x 16 azioni), frequenza di spostamento fisico, veri eventi di energia positiva/negativa e previsioni heldout rispetto a baseline "nessun cambiamento".
- I sensori originali restano identici: nessun input segreto, nessuna categoria oggetto cablata. Gli attuali identificatori sono ipotesi provvisorie del modello percettivo, NON prova di aver riconosciuto una specifica cassa. Nessuna correlazione temporale viene presentata come causalità.
- SensorimotorMind.java: attachUnifiedCognitionV21 e hook in contactOutcome/choose/observe. Si registra la decisione DOPO la scelta finale, non si tocca il punteggio delle 16 azioni. Al prossimo intervallo di 0,65s simulati si registra la conseguenza reale con lo stesso holdout già usato dal vecchio cervello. Inizio nuova vita/resume pulisce gli eventi incompleti, conserva quello che è stato davvero appreso.
- GameActivity.java: carica e salva unified_cognition_v21 negli stessi SharedPreferences del profilo attivo; i 12 slot precedenti preservati, nessun reset massivo.
- ResetDomains.java: la cancellazione esplicita WORLD elimina anche questa nuova memoria; non contaminare altre categorie di reset.
- UnifiedCognitionV21Regression.java: test JVM per contesti, non attribuzione indebita degli effetti, eventi realmente accaduti, heldout, zero confidenza iniziale, parsing atomico, salvataggi e morte.
- zero21-unified-qa.yml: tutte le precedenti regressioni JVM compresi grip, masse, scatole, lastre, sensori, save, e nuovo modello; controllo non-interferenza; Android assembleDebug. NO firma, release o distribuzione.
- Paper principali: ZERO21_UNIFIED_COGNITION_MASTER.md, ZERO21_IMPLEMENTATION_LOG.md, QUESTO file.

Questa è una **base condivisa verificabile**. NON è ancora l'intero nuovo cervello capace di interpretare ogni oggetto, causare un'apertura e pianificare efficacemente. Soprattutto NON ha un proprio punteggio d'azione, così da poter misurare senza pregiudicare l'originale.

## 4 — Catena degli eventi e fasi fisiche
FISICA (massimo ~35ms subframe): motore originale, tessuto, collisione reale, grip sui contatti, porta, cassa, lastre, energia al contatto e vento.
SENSORI / SCELTA (~0,65 secondi simulati): 26 sensi + raggi spaziali osservati, scelta da SensorimotorMind originale e dei moduli già attivi, eventuale esplorazione casuale.
SUBITO DOPO LA SCELTA: UnifiedCognitionV21.chosen e snapshot del mondo percepito, non del mondo vero.
DURANTE IL PASSO FISICO: contatto clamped positivo/negativo → sensorMind.contactOutcome → unified.energyContact; normale drenaggio energia/vita e vento NON diventano falsi contatti.
DOPO LA FISICA/AL DECISION TICK: SensorimotorMind.observe ottiene il solito LearningDiagnostics.shouldHoldout e il vero effetto esterno accumulato; unified.consequence misura train oppure heldout, senza inventare fisica.
NUOVA VITA: resetLife ripulisce solo l'azione pendente e la visione provvisoria. SALVA/CARICA: persiste uc21, non l'azione non conclusa; ID categoria provvisorio non deve diventare identità di un oggetto.

ATTENZIONE: il movimento "physicalEffect" è generico, non associato ad un oggetto identificato. I futuri miglioramenti devono legare istanze percettive, impronte di contatto e spostamento effettivo con misure fisiche di controllo, SENZA consegnare all'agente il vero ID della cassa.

## 5 — Bug storici e dati che NON dobbiamo dimenticare
- Nella v0.9.4 ZERO ha spostato il cancello fino a ~111U senza necessariamente attraversarlo; il criterio >115U non prova da solo ingresso. Ci sono successivamente stati passaggi parziali reali; distinguere spostamento, soglia attraversata e contatto energetico.
- La locomozione peggiorò aggiungendo memoria episodica, perché scelte e parametri potevano interferire. Vietato sovrascrivere apprese competenze solo per un ricordo.
- Dopo modifiche alle casse/lastre, moto/energia e memoria spaziale avevano regressioni; furono corretti checkpoint spaziale, prese reali e spostamenti illegali senza punto RED. Distinguere legge 5 copertura energetica (due lati) da legge 6 lastra costruzione (un RED).
- Sul S21 alcuni punti aderenti scomparivano e ZERO tornava scarso nelle prese: statistiche private documentate mostrano comando locale grip sulle casse passato da ~78,4% a ~4,3% nei diversi report, mentre l'adesione globale rimaneva alta. Coincideva con continue mutazioni HAND/MEMBRANA; causalità OTA non dimostrata. OTA103 preserva coppie di finestre con presa comprovata, OTA109/110 introducono archivio coppie e richiamo con contesto, non perfetta memoria motoria.
- Il cervello vergine testato in OTA recente sceglieva azione 8 per 245/369 decisioni nell'ultima vita e moriva spesso vicino all'energia; il credito meccanico dominava. OTA110 separa abilità meccanica e bisogno energetico, ma continuiamo a validare sul telefono.
- Il modello futuro v20 ha visto 44.574 transizioni reali, ma non batteva adeguatamente baseline; 5.578 pianificazioni shadow → 0 abbastanza affidabili. NON abilitarlo forzatamente né abbassare soglie per vantarsi di intelligenza.
- I test sul mondo con 100% casse piene e 1% piene devono insegnare probabilità e costo nel tempo, NON fissare un tipo di mondo dentro il cervello.
- OTA112 è soprattutto ridisegno interfaccia: tre contatori blu/rosso/viola verticali a lato, minima freccia trasparente dettagli, navigazione inferiore cervello/tempo/impostazioni, tutte le attività dietro impostazioni.

## 6 — Fatti dimostrati vs ancora sconosciuti
DIMOSTRATO DAL CODICE: nuovi hooks, chiavi salvataggio, generazione fisica vecchia, apprendimenti precedenti e workflow QA. La prima esecuzione CI 38451441375 è FALLITA in compilazione solo per un file preesistente ObjectExperienceV20 non elencato nel vecchio script; corretta lista nel test 38451873419. La seconda esecuzione ha superato compilazione e test Java ma è FALLITA su asserzione statica troppo generale che vietava persino registrazione post-scelta. Il check è stato ristretto rigorosamente al loop dei punteggi in commit 7361e67f3f82c45913f7263b3b9fee1eb8f6dfee. Esaminare lo stato dell'esecuzione 38452161103 e di eventuali run successivi PRIMA di dichiarare verde.
NON DIMOSTRATO: incremento della sopravvivenza, ragionamento causale, competenza motoria migliore, stessa-cassa multivista, reale APK installata S21, firma nuova, CPU/batteria x20, sanità dei dodici slot in un vero upgrade su smartphone.

## 7 — Azioni operative della prossima chat (precise)
A. Leggi i tre paper ZERO21, recupera la HEAD corrente del ramo research e confrontala con stable; leggi tutte le nuove classi indicate sopra e SOLO poi proponi refactor.
B. Controlla https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero21-unified-qa.yml (se UI disponibile) oppure GET api.github.com/repos/nicogtr20-ctrl/idle-runner-releases/actions/runs, filtra name 'ZERO 2.1 unified cognition'. Apri jobs/log e correggi ogni errore effettivo. Se il commit dopo un QA verde ha cambiato codice, ripeti test sull'ultimo codice, non citare il run vecchio.
C. Crea PR DRAFT verso feature/overmind-inapp-updater; vietato merge/release fino al piano completo e ai test comportamentali. Scrivi nell'issue la reale natura SHADOW_ONLY e i blocker.
D. Implementa G2–G4 come sequenza COERENTE sullo stesso blackboard: tracciamento istanza in viste successive dai raggi+odometria, attribuzione dell'effetto tattile dello stesso oggetto, collegamento temporale alla fonte energetica con ipotesi e controprove. Ricorrere all'oracolo geometrico esclusivamente negli assert del BENCHMARK, non in funzioni del cervello.
E. Implementa G5–G6: libreria durevole di coppie HAND/MEMBRANA verificate con probabilità contestuale di successo, rollback dal deterioramento, modello futuro con 1–5 decisioni e costi/energia/novità ben separati, incertezza calibrata su heldout. Nessun "premio per spostare sempre casse", nessuna guida target.
F. Confronta cervello originale, osservatore ZERO21, consigli sperimentali abilitati solo su laboratorio, su seed e profilo IDENTICI, scene note e nuove, coperchi con vera fisica, vite oltre fame naturale, due cervelli vergini/trained. Report quantitativo + file paper dei fallimenti.
G. Solo dopo evidenza stabile e approvazione/accettazione, seleziona modifiche nel ramo OTA, esegui ALL legacy and new CI, firma con publisher originale, verifica release + hash + manifest Vercel+APK disponibili, aggiorna solo tramite AGGIORNA install in-place e chiedi riscontro S21. Se QA/efficacia insufficienti, mantieni la bozza e la OTA112.

## 8 — Come aggiornare i paper per non perdere nulla
Al termine di ogni cambiamento tecnico, aggiornare:
1. docs/ZERO21_IMPLEMENTATION_LOG.md: data, classe/metodo, esito, esatta SHA, test run, bug, metrica, pre/post sullo stesso scenario.
2. docs/ZERO21_UNIFIED_COGNITION_MASTER.md: se cambiano architettura, assunti, regole causali, affidabilità e soglie preregistrate.
3. Questo file: stato realmente più recente, ultimo punto riprendibile, rami, QA, blocked releases, sessioni S21, decisioni dell'utente, TODO prioritari.
4. docs/HANDOFF.md e OVERMIND_START_HERE.md: inserire link prominenti ai nuovi documenti, senza riscrivere storia pregressa.
5. Dopo e SOLTANTO dopo OTA firmata: docs/CHANGELOG.md, docs/UPDATES.md, manifest e hash reali. Mai falsificare "versione ultima" semplicemente dal nome del file.

**Sicurezza:** non copiare pesi utente, PAT GitHub, keystore o dump di memoria da zero-telemetry nei paper. Il robot non è cosciente o AGI, è un prototipo di ricerca che deve dimostrare generalizzazione fisica e adattiva.


## 9 — Ultimo risultato effettivamente verificato
- [GitHub Actions ZERO21 run **38452161103**](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38452161103): **SUCCESS**. Commit testato `7361e67f3f82c45913f7263b3b9fee1eb8f6dfee`. Tutte le compilazioni e regressioni pure Java, asserzioni anti-influenza sul punteggio e build completa Android Debug riuscite. I commit di paper successivi sono solo Markdown.
- **PR DRAFT [#39](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/39)** aperta verso `feature/overmind-inapp-updater`. NON fonderla e NON lanciarne il publisher per adesso: G2–G7 mancanti e nessun uplift provato. Ultima versione ufficiale verificata nella ricerca: OTA112, non una nuova APK ZERO21.
- Prossimo intervento: tracciamento sensoriale multivista + contatto attribuito allo stesso oggetto, prima di collegare skill e modelli futuri all'azione. Usare i test simmetrici e non cercare scorciatoie per generare punti RED.


## 10 — Ultimo grande intervento dopo PR #39 (13 ottobre 2026)
Nuovo paper fondamentale: **[ZERO21_G2_G7_INTEGRATION.md](ZERO21_G2_G7_INTEGRATION.md)**. Sono stati sviluppati assieme:
- `ObjectContinuityV21`: ipotesi anonime sui medesimi ostacoli tra viste a 16 raggi con odometria, astensione per ambiguità; nessuna prova di identità vera, corpo di forma mutevole introduce errore nei raggi.
- `CompetenceMemoryV21`: conserva coppie di pesi HAND+MEMBRANA osservate dopo REALI prese bilaterali e spostamento, con conteggi su vite differenti, non comanda nulla. Il vecchio `GripSkillBank` continua a decidere eventuali ripristini.
- `UnifiedArbiterV21`: unica valutazione energetica/rischio/costo/meccanica, richiede predizioni fuori addestramento e pianificazione valide, può ASTENERSI, non altera ancora alcuna scelta fisica.
- Test di 440 decisioni identiche al controllo e porting selettivo dell'arena con casse con vera presa bilaterale, 12 coppie stesso cervello/seed, ZERO21 in sola osservazione; vietata promozione per un pareggio.
- `Zero20PassiveSnapshot` e pagina CERVELLO/ANALISI ora includono nuovi contatori aggregati, solo con esplicita condivisione e senza rivelare tracce/spostamenti raw nel digest.
- Salvataggi nuovi `unified_cognition_v21`, `object_continuity_v21`, `competence_memory_v21`, `unified_arbiter_v21`. Compatibilità opzionale con tutti i dodici slot e reset separati WORLD, SPATIAL, MEMBRANE.

ATTENZIONE VERIFICHE: il primo run di arena `38455325053` ha scoperto crash quando due oggetti appaiono per la prima volta nel medesimo sweep. Non dichiarare mai la build verde citando quel test: fix commit `50d032951b548f8e50fec09c3f1e019c7ec07104`, regressione commit `d80bcac1258c2addd0583f482560380de724154a`. Controlla il run nuovo **dopo** quei commit, leggi log finali, conferma il build Android e tutti i 12 scenari. Se un gate fallisce correggi e riesegui. Non firmare, mergiare o pubblicare questa PR senza test su mondi mai visti, prestazioni e vantaggio reale. Ultima OTA sul telefono rimane 112, e il sistema resta SHADOW.


## 13 ottobre 2026, sera — TEST MAX su Galaxy S21, tasto SEPARATO da x1/x5/x20
Richiesta esplicita dell'utente: testare velocemente sul cellulare per risparmiare i minuti GitHub Actions, con tasto dedicato non accanto al selettore normale perché troppo facile premerlo per sbaglio. **Implementato** sul ramo sperimentale `feature/zero21-unified-cognition` commit `b432254ed924be51c563cedd8eb5e049878bc910`; quindi trasferito selettivamente al ramo Android stabile `feature/overmind-inapp-updater`, SOLO UI+accelerazione del vecchio cervello, commit `3ca84527c5b42f0977ab3efda969594bbf01fd8e` (nessun merge delle capacità non validate ZERO21). La pipeline firmata stabile è partita: GitHub Actions `OVERMIND stable OTA publisher` run [38457970983](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38457970983). **Verificare esito del run, APK firmata, manifest e disponibilità Vercel prima di dire "premi AGGIORNA"; se fallisce, OTA112 rimane invariata.**

Funzione:
- Controllo `AVVIA TEST MAX` disegnato dentro Impostazioni come pulsante separato, lontano da tempo x1/x5/x20 e con AlertDialog di conferma `AVVIA TEST`/`ANNULLA`; velocità ordinaria intatta.
- Durante il test piccolo `TEST MAX xNN • STOP TEST` fluttuante a sinistra sotto gli indicatori energetici; tasto STOP con hitbox separata. Anche Impostazioni permette arresto.
- Simulazione a passaggi FISSI `1/60` secondo simulato ciascuno; budget di lavoro massimo `38ms` a ogni disegno, massimo `1024` passaggi consecutivi; rendering meno frequente non implica perdita di frame simulati, consumo energetico e morte proporzionali al tempo SIMULATO. Velocità effettiva indicativa ricavata da secondi simulati / secondi reali; NON è x costante predeterminato.
- Temperature Android API29+ controllate circa ogni 2s; a livello MODERATE il budget scende a 18ms; a SEVERE il test viene arrestato. Restituzione del controllo all'UI a ogni burst.
- La modalità TEST è SOLO temporanea, non aggiunta al salvataggio scene/nominati; `suspend()` la disabilita; normale velocità 1/5/20 sopravvive. Il caricamento vecchio salva speed 1/5/20, senza migrazioni. Salvataggi apprendimenti esistenti continuano normali.
- Non cambia alcuna fisica, sensore, reward, rete neurale o regola di presa; aumenta quanti passi fisici eseguiamo per secondo REALE. Trattare prestazioni, surriscaldamento, efficienza e opportunità di apprendimento come DATI DA PROVARE sul vero Galaxy S21, non come benchmark già dimostrato.
- I test CI del ramo ricerca sono stati resi `workflow_dispatch` soltanto; NON lanciarli a ogni commit. Una build firmata OTA stabile è necessaria una tantum per installare sul telefono, ma consume minuti GitHub; non duplicare build soltanto per aggiornare paper Markdown sul ramo stabile.

IMPORTANTE: OTA stabile contiene soltanto TEST MAX con cervello preesistente. I moduli cognitivi `UnifiedCognitionV21`, `ObjectContinuityV21`, `CompetenceMemoryV21` e `UnifiedArbiterV21` sono sempre nel ramo ricerca, non installati attraverso la build stabile. Per raccogliere i dati ZERO21 sul cellulare occorrerà una futura build di prova firmata che includa esplicitamente il ramo sperimentale dopo gate e conservazione salvataggi, non confondere il pulsante TEST MAX con l'attivazione dei nuovi algoritmi.


## 13 ottobre 2026, 21:57 CEST — TEST MAX pubblicato ufficialmente come OTA115
**Pubblicazione REALMENTE conclusa** nel ramo stabile `feature/overmind-inapp-updater` senza mergiare ZERO21 sperimentale. L'utente ha chiesto un pulsante TEST MAX separato da TEMPO x1/x5/x20, nascosto in Impostazioni e con conferma e STOP. Il codice contenuto nella OTA stable è stato compilato e firmato nel job GitHub Actions [38458629153](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38458629153) **SUCCESS**, dopo due run falliti su asserzioni statiche obsolete (il primo pretendeva `for(int i=0;i<speed;i++)`, il secondo il SHA blob storico immutabile di `GameActivity.java`). Correzione puntuale: mantenuti i vecchi test di reale `lifeStats.tick(dt)`, aggiunto controllo del passo fisso `1/60`, limite CPU, conferma, STOP e protezione termica; SHA pin di GameActivity aggiornata alla vera impronta `edb82b9f106307ca0722aa78570d21e0e1a22ef1`, invariati tutti gli altri pin e le regole fisiche.

Manifest GitHub `main/updater/public/latest.json` realmente aggiornato: `versionName=0.11.6-ota.115`, `versionCode=1115`, `applicationId=com.overmind.zero.ota`, `sha256=71741340eaa7c8edf81be87843f3cd06776819f83f1d44aa444fa1e30e7a5fc2`, `sizeBytes=342640`, APK `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1115-71741340eaa7.apk`. Tree main contiene il binario con dimensione coerente. Vercel autenticato `get_deployment(overmind-zero-updates.vercel.app)` restituisce **production READY**, commit `e01fec62af29050e5124de4c0a730174c6bf2ad8`, alias corretto, messaggio release `Publish signed OVERMIND 0.11.6-ota.115 OTA build`. Il controllo HTTP pubblico diretto via web/container in questa sessione non era disponibile, ma manifest GitHub, deploy Vercel READY e publisher concluso confermano la pubblicazione. Sul telefono l'utente dovrà premere **AGGIORNA APP** e concedere installazione: non dichiarare installato prima della conferma Android.

TEST MAX è disponibile solo dentro Impostazioni con conferma; durante gioco appare `TEST MAX x... • STOP TEST` a sinistra, modalità non persistente (si ferma alla pausa uscita Android), step fisico fisso `1/60s` simulati, deadline ~38ms per batch, minimo 18ms in thermal moderate, stop automatico a thermal severe. *Solo il numero di substep per secondo REALE cambia.* L'effettiva velocità e temperatura sul Samsung Galaxy S21 richiedono test dell'utente.

**Attenzione critica:** questa OTA115 NON include le nuove classi ZERO21 (continuity, unified cognition, competence, arbiter). Queste rimangono soltanto nel ramo sperimentale `feature/zero21-unified-cognition`, per cui la data collection su telefono con OTA115 usa il cervello e la telemetria consolidati (precedente) e non valida ancora G2–G7 su device. Pubblicare in seguito le classi ZERO21 richiederà un'ulteriore build specifica e verifica dei salvataggi.


## 13 ottobre 2026 — ZERO 2.2 attivo (candidato OTA117)
Richiesta utente: cervello rivisto funzionante e APK, x5/x20 fluidi, TEST MAX circa x29. Nuovo ramo `feature/zero22-active-recurrent-ota117` da `fe6d7497eb5b53055b46c474dd5470028bc97f88`. Leggere **[ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md)** per architettura, contratto sensoriale, replay, competenze, ottimizzazione TEST MAX e limitazioni. `RecurrentCoreV22` guida realmente la scelta prima dell'azione; i moduli 2.1 restano osservatori compatibili. Nessun reset del profilo o slot. 71 test JVM locali PASS; release, Android build e prova S21 da verificare separatamente. Non chiamare questo sistema Dreamer completo o intelligenza generale.

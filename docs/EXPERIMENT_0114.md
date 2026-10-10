# ZERO Experiment 0114 — Progresso verificato, intenzioni brevi e catena fisica del cancello

**10 ottobre 2026.** Versione Android firmata `0.11.4-ota.64`, versionCode `1064`, package `com.overmind.zero.ota`, APK **190132 byte**, SHA256 `255727583121d49c07463a29aea07d4f99db06912cdc2f6aeff40cdd103cdab8`. GitHub Release `https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.4-ota.64`; asset firmato `https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.11.4-ota.64/OVERMIND-ZERO.apk`. Repository PRIVATO, download richiede autenticazione GitHub. Staging finale GitHub Actions **38089466225 SUCCESS**, signed publisher **38089553377 SUCCESS**. Galaxy S21 e comportamento effettivo ancora NON testati.

### Importante: Vercel NON pronto per OTA
Il publisher GitHub ha aggiornato `main/updater/public/latest.json` a `0.11.4-ota.64`, checksum/size sopra. **Ma la produzione Vercel serve ANCORA `0.11.3-ota.63`** sul dominio `https://overmind-zero-updates.vercel.app`, e il nuovo asset `/apk/overmind-zero-1064-255727583121.apk` risponde **HTTP404**. Un tentativo esplicito `mcp__Vercel__create_deployment` su progetto `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg` dal commit main `ff8ce6e90747d4ac835426f50550cd675a4cfcd5` ha restituito **HTTP402 / api-deployments-free-per-day / più di 100 deploy giornalieri, retryAfter 86400**. Non dichiarare il pulsante AGGIORNA funzionante per v0.11.4 né fornire l'URL Vercel 404 come download. Installare IN-PLACE solo APK firmato GitHub; NON disinstallare, cancellare dati o azzerare. Quando la quota Vercel torna disponibile, ridistribuire il `main` più recente e verificare **sia** `latest.json` sia il path APK e i relativi HTTP200, lunghezza e MIME.

## Perché
L'utente ha fornito vere schermate del **laboratorio comportamentale v0.11.3**:
- Energia: su 3 coppie A/B, navigazione attiva migliore in 1, parità in 2; +22 energia in una prova attiva #31 dopo 6.5 s, differenza media +7.3 energia.
- Pericolo: 2 coppie attive migliori, 1 controllo migliore; danno totale 7 attivo vs 14 controllo, piccola numerosità senza significatività statistica.
- Cancello: 3 pareggi, 0 movimento porta, 0 attraversamenti, 0 scelte nav. **La vicinanza precedente misurava la distanza dalla risorsa dietro il cancello e NON diceva se il corpo aveva mai toccato la porta.**
Questo ha motivato l'aggiunta prima di tutto di metriche fisiche verificabili e di una continuità breve delle azioni con progresso, NON script di apertura né un'altra ricompensa di movimento.

## Nuovo modulo `GoalContinuity.java`
Nuova preferenza autonoma `goal_continuity_v114` (encoding versionato `gc1`), collegata via `SensorimotorMind.attachGoalContinuity`. L'obiettivo temporaneo viene estratto soltanto da segnali ottici **davvero visibili** nella `InstrumentalNavigation` (3 firme grezze × 8 settori), con valenza già appresa da **contatti che hanno modificato la riserva di energia**. Non passa identità, coordinate, nomi o legge fisica di un oggetto né etichette "food", "hazard" o "gate".

Il modulo è neutro all'inizio; una volta visto un segnale con valore appreso, ZERO sceglie comunque autonomamente un'azione. SOLO se:
1. quell'azione aveva una previsione di utilità energetica strumentale positiva dal modello di moti realmente appresi;
2. il corpo si è realmente spostato almeno **3 unità**;
3. la luminosità ottica massima del segnale scelto è cambiata nella direzione vantaggiosa di almeno **0.009** (più luminoso per stimolo positivo, meno luminoso per negativo),
l'intenzione diventa eleggibile per un bonus decisionale **limitato massimo a +0.48**, sui comandi che il modello di navigazione già ritiene utili. Non aggiunge reward per avanzare al TD principale e non forza il motore.

Se il segnale è occluso/scompare o la luminosità peggiora per due osservazioni, interrompe l'intenzione. Non garantisce un tracking visivo rigoroso: intensità luminosa è un'approssimazione della distanza apparente, può variare con occlusioni e geometria; nessun piano generale è stato dimostrato. Il modello memorizza solo **contatori di successi, continuazioni, tentativi falliti, abbandoni e decisioni influenzate**; **nessun vecchio segnale, direzione o obiettivo attivo viene ripreso da un salvataggio**.

Il vecchio `SensorimotorMind.S=26`, tutti i pesi e valori, le memorie temporali, episodi, propriocezione, mappa spaziale, apprendimento causale e le 12 partite nominate rimangono inalterati. Il benchmark originale spegne `navigationAdviceEnabled` nel controllo, dunque spegne anche l'effetto delle intenzioni; l'osservazione fisica e gli altri cervelli restano attivi.

## Nuovo modulo `GateBehaviorMetrics.java`
Nuova preferenza indipendente `gate_behavior_v114` (`gb1`), **SOLO osservatore esterno, MAI parte del punteggio della policy**. Campiona la stessa `ScarcityLab` realmente simulata nel gioco DOPO `chamber.constrain` e anche nel `BehavioralLaboratory` dopo `room.constrain`. Registra:
- distanza minima dal **rettangolo vero del cancello**, non dalla sfera nascosta; numero di frame entro 45 unità (spawn iniziale è ~67 U, NON considerato avvicinamento);
- frame nucleo vicino/in contatto, contatto della membrana, adesione selezionata mentre la membrana tocca la porta, comandi di presa fisica, presa che persiste per tempo, presa effettivamente bilaterale su facce opposte;
- massimo **vero** spostamento `room.maxDoorTravel`, apertura fisica e attraversamento effettivo del varco.

La schermata ANALISI ora visualizza **CONTINUITA' DELLE INTENZIONI** e **DIAGNOSI FISICA DEL CANCELLO** con l'ultimo stadio osservato e contatori già salvati. Nuovi reset indipendenti `ResetDomains.INTENTION` e `ResetDomains.GATE_METRICS`, senza modificare alcun vecchio cervello.

Il report del laboratorio mostra per ogni prova CANCELLO le metriche fisiche concrete del blocco e il minimo dalla porta: se `vicino=0`, ZERO non si è avvicinato; se membrana>0 e comando presa=0, serve osservare la politica di adesione; se grip>0 ma bilaterale=0, manca una presa sufficiente secondo la fisica attuale; se spostamento>0 ma attraversamenti=0, il problema è nel seguire il passaggio. **Questi sono indicatori, non motivi causali dimostrati.** La meccanica del cancello richiede punti indipendenti ai lati opposti, perciò l'accessibilità della manovra può essere un collo di bottiglia fisico, da indagare con i dati, non aggirare da programma.

## Test realmente superati
- `GoalContinuity.main`: ZERO vergine NON inventa obiettivi, il valore energetico da contatto e la prova reale di movimento sono prerequisiti, la continuità compare solo dopo cambiamento ottico vantaggioso misurato, viene conservata per una seconda azione vantaggiosa, abbandona dopo ripetuti allontanamenti, restore conserva soltanto dati storici validati e non un obiettivo fantasma.
- `GateBehaviorMetrics.main`: zero apertura o vicinanza al semplice spawn, si rileva avvicinamento vero, contatto del nucleo, comandi/tenute bilaterali separati da movimento effettivo, salvataggio riproducibile, rifiuto encoding corrotto.
- `BehavioralLaboratory.main` continua a eseguire **18 prove fisiche A/B** senza offrire al cervello le coordinate dello stimolo. Il report A/B ora esplicita che confronto ATTIVA vs CONTROLLO misura i consigli di navigazione + intenzioni; ciascuna prova cancello ha diagnostica per tutti gli stadi. I modelli non sono mai allenati su ricompense inventate durante il laboratorio.
- Staging finale **38089466225 SUCCESS**: JVM new+legacy regressions, test statici su policy/legacy save/branch di controllo, compilazione `assembleDebug`. Official signed publisher **38089553377 SUCCESS**, stesso package, stable signer, GitHub Release 64 e manifesto nella repo main.
- **Non esistono ancora prove sul Galaxy S21** che il nuovo comportamento ottenga più risorse, eviti più danni o sposti il cancello. Necessario eseguire di nuovo laboratorio e raccogliere l'output.

## Passi successivi senza cancellare le partite
1. In Android **salvare ZERO in uno slot nominato**.
2. Installare in place l'APK v0.11.4 da **GitHub Releases**; il pulsante AGGIORNA su Vercel resta stale a 0.11.3 finché non viene ripristinato l'hosting.
3. ANALISI → CONTINUITA' DELLE INTENZIONI e DIAGNOSI FISICA DEL CANCELLO; dopo esperienze fisiche e un salvataggio, controllare dati. Se i progressi continuati restano 0, osservare anche `MOVIMENTO COME MEZZO / ENERGIA` (valenze e moti verificati).
4. ANALISI → LABORATORIO COMPORTAMENTALE → AVVIA CONFRONTO A/B; inviare schermate dell'output ENERGIA/PERICOLO/CANCELLO e sotto ogni prova CANCELLO la diagnosi "fisica". Non confrontare solo gli esiti vincitori 1/3.
5. Distinguere limiti fisici del gate (presa bilaterale) dalla capacità cognitiva di scegliere una manovra; non presumere che un generico incentivo a movimento generi la risorsa.

Repo privato `nicogtr20-ctrl/idle-runner-releases`. Codice Android `feature/overmind-inapp-updater` (stable); staging `feature/overmind-v114-continuity-gate-diagnostics`; GitHub APK+manifest/docs su `main`. Prima di lavorare ancora leggere `AGENTS.md`, `OVERMIND_START_HERE.md`, `docs/HANDOFF.md`, `docs/EXPERIMENT_0112.md`, `docs/EXPERIMENT_0113.md` e questo rapporto. **Un aggiornamento Vercel non è completato fino a HTTP200 reale dei due endpoint.**

## Aggiornamento finale: OTA produzione disponibile
Dopo il primo fallimento 404 sul nuovo APK pubblico, il deploy Vercel è stato aggiornato manualmente a `dpl_4KmbNc3nikDq5EPd2i8v2JMTqBM7` (READY, HEAD main `725d1920505c3c004280f0f8d6b5950fd1474566`). Gli URL `/latest.json` e `/apk/overmind-zero-1064-255727583121.apk` hanno risposto entrambi **HTTP200** sul dominio ufficiale; manifest versione1064 con SHA atteso, APK 190132 byte e MIME Android. Si può usare il pulsante AGGIORNA; resta da verificare la prova reale su Galaxy S21. Non cancellare dati o disinstallare.

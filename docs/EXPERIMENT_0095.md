# ZERO-009.5 — Registro sperimentale e progetto memoria delle conseguenze

> **Documento di handoff scientifico/tecnico aggiornato al 10 ottobre 2026.**
> Non dichiarare che il prototipo sia un'intelligenza generale, pensiero cosciente, causalità dimostrata o pianificazione a lungo raggio.
> Le prove del codice dimostrano invarianti; il comportamento reale del Galaxy S21 va ancora verificato.

## 1. Obiettivo di OVERMIND
Realizzare una creatura 2D autocontrollata, deformabile, persistente, che apprende **esclusivamente dal mondo che vive**: sensi, corpo, ambiente e conseguenze. ZERO non deve ricevere destinazioni, segnali espliciti "questo è il cibo" o procedure "apri il cancello". L'ambiente fisico può contenere un cancello a doppia presa e una risorsa rara; ZERO deve scoprirne gli effetti.

**Dispositivo reale:** Samsung Galaxy S21 Android. **Repository privato:** `nicogtr20-ctrl/idle-runner-releases`. L'app è Android Java nativo, package `com.overmind.zero.ota`, Gradle 8.11.1 e firma persistente via CI.

## 2. Cronologia tecnico-sperimentale
| Versione | Modifica introdotta | Osservazione/limite |
|---|---|---|
| 0.8.x | Corpo fisico, membrana 96 punti, porta a doppia presa e 82 pesi morfologici | Alcune aperture osservate, difficoltà a entrare; vecchi trainer usavano rollout offline con direzione nota. |
| 0.9.1 | Modello del mondo online da **11.664 pesi**; ANALISI con campioni *holdout*, grafici, reset per dominio | Errore predittivo migliorava rispetto a baseline su alcuni dati; non prova attraversamento, né qualità del movimento. |
| 0.9.2-ota.34 | Ripristino apprendimento di corpo/manipolazione/membrana **in vita**; meno fame e meno danno; ricompensa viola/oro +80 nella stanza, niente oggetti normali dentro | Da zero, ~11.000 esperienze senza locomozione: mutazioni motorie troppo piccole. |
| 0.9.3-ota.35 | Mutazioni più ampie, punteggio motore che richiede traduzione reale; HUD distanze effettive | ZERO ha iniziato a camminare e deformarsi, ma spesso in cerchio; vecchio test CONFRONTA poteva risultare negativo. |
| 0.9.4-ota.36 | Distanza **netta** distinta dalla via totale, celle nuove da 75U, energia e prova reale PROVE; contatori varianti conservate/scartate | Sul telefono: 38.070 esperienze, 93 dischi spostati, 268 prese, ~111U spostamento porta, nessuna stanza risolta, 185 miglioramenti membrana; apertura *visiva* ma ZERO non entra, poi muore, non torna alla tattica. |
| **0.9.5 (in sviluppo / test)** | Memoria episodica di sequenze sensoriali-azioni realmente riuscite e pesi corporei presenti; richiamo debole in situazioni simili; credito da conseguenze fisiche generiche | Non c'è ancora dimostrazione empirica di ripetizione tecnica sul cancello né di passaggio: test manuale richiesto. |

**Fatto fisico cruciale:** ScarcityLab `openingFree()` richiede `gateTop() > oy + OPEN_HALF_HEIGHT + 3`, che con altezza porta corrente equivale a una corsa **superiore a 115U**. Un massimo di 111U **non prova apertura completamente libera**: la porta può apparire quasi aperta. La porta è spostata da forze tangenziali di due prese bilaterali reali, non animazione precalcolata.

## 3. Diagnosi del limite visto dall'utente
L'algoritmo precedente apprende pesi e prevede variazioni sensoriali; **non salva una autobiografia di ciò che ha fatto subito prima di provocare un effetto fisico**. Alla morte la configurazione fisica si azzera; le reti persistono, ma manca la traccia della strategia. La valutazione corporea v0.9.4 usa contatti, distanza netta, nuove celle e bilancio energetico; uno spostamento tangenziale della porta non riceveva sufficiente ricompensa specifica come generico **cambiamento osservato nel mondo**.

## 4. Cosa implementa davvero la v0.9.5
### EpisodicMemory.java
- Anello volatile delle ultime **9** coppie `[26 sensi, azione scelta 0..15]` rilevate **dopo azioni realmente effettuate**, non in simulazioni future. Ogni sequenza memorizzata include anche copia dei **62 pesi motori**, **18 della manipolazione**, **82 della membrana/presa** in uso all'evento, eventualmente una variante sotto test.
- Un nuovo ricordo nasce solo se dopo la fisica viene misurato un evento fisico sufficiente: traslazione della porta, spostamento confermato di un disco o reale recupero energetico. Non riceve codice “apri porta” né “soluzione”. La porta **non viene identificata come ricompensa**: il segnale è genericamente `environmentEffectSinceAction`. Al massimo 18 eventi con deduplica, formato testuale `em1`, cap sul caricamento di ~220 KB.
- Confronto dei sensi correnti con snapshot precedenti, limitato a stati percettivamente salienti (ostacoli prossimi, contatto, colori). Se abbastanza simili, propone una preferenza **+0.72** alla valutazione di un'azione già realmente osservata. ZERO conserva completamente selezione neurale, variabilità ed esplorazione random. La memoria **non esegue comandi obbligatori** e non conosce coordinate del cancello.
- Quando l'azione proposta è effettivamente scelta, applica temporaneamente anche i veri pesi motore/manipolazione/membrana registrati all'evento. È un tentativo di riusare una configurazione fisica, **non la promessa che produca la stessa conseguenza**.
- Sequenza può proseguire soltanto se le sensazioni successive rimangono compatibili. Dopo ~12 richiami senza conseguenze attiva 30 decisioni di pausa; morte resetta soltanto la traccia e lo stato di richiamo, non i ricordi precedentemente salvati.
- Il modulo salva contatori: eventi catturati, riconoscimenti sensoriali, azioni effettivamente richiamate e **nuovi effetti fisici** osservati durante una prova richiamata. “Effetti riprodotti” **non significa attraversamento riuscito né causalità**.

### SensorimotorMind.java
- Lo storico Q/value e il modello previsionale `wm1` restano **binariamente invariati**, per migrare senza azzerare.
- Un hook `attachMemory` non serializzato consulta la memoria all'inizio della scelta. Bonus soffice solo sull'azione riconosciuta, senza forzarla.
- Osservatore riceve inoltre il reale spostamento fisico di un oggetto dopo collisione/adesione, come componente generica della ricompensa; nessuna destinazione/identità oggetto passata come input.
- Il merito del risultato può rafforzare anche azioni antecedenti attraverso l'episodio ricordato, ma non è ancora vero algoritmo di pianificazione controfattuale; il Q-learning esistente contribuisce tramite TD discount `GAMMA=.78`.

### GameActivity.java
- Misura spostamento `abs(doorY-after - doorY-before)` **DOPO** il motore fisico di ScarcityLab, e il superamento reale della soglia 12U di un disco; aggrega fino al completamento della transizione sensoriale (~0.65 s sim).
- Registra `observedBefore` e **azione precedente**, non la nuova action scelta dal modello alla fine della transizione. Salva soltanto effetti reali e configurazioni dei pesi realmente applicati.
- Durante il richiamo, `OnlineBodyLearning` interrompe eventuali prove in corso: il replay non può falsare la selezione di una mutazione.
- Salva la nuova chiave `episodic_memory_v095` su SharedPreferences, preservando `sensorimotor_model_v09`, `best`, `hand_brain_v04`, `morph_brain_v07` e tutte le statistiche precedenti.

### Interfaccia & reset
- `CERVELLO → ANALISI` mostra un pannello "MEMORIA DELLE ESPERIENZE" con contatori e limiti statistici.
- Aggiunto reset **MEMORIA EPISODICA** indipendente in `ResetDomains`. Le altre reti non vengono cancellate; `AZZERA TUTTO` rimane esplicito e richiede doppia conferma.

## 5. Limiti e rischi noti
1. Due ambienti sensoriali simili **possono richiedere movimenti diversi**. Ripetere una vecchia azione può essere peggiore o far perdere energia. Il soft bonus + cooldown limita, ma non risolve.
2. La memoria non conosce identità del cancello né ricostruisce la topologia di stanze sconosciute. Firme sensoriali da sole possono confondere ostacoli.
3. La relazione “sequenza → conseguenza” è **correlazionale** e non dimostra causalità. Un oggetto può spostarsi per inerzia dopo la presa; si deve migliorare la misura della qualità.
4. Copiare i pesi motori da un'altra postura può rompere la locomozione momentaneamente; per questo il replay dura solo quando una scelta proposta è davvero seguita.
5. Nessun modello immagina ancora più stati futuri concatenati e poi li pianifica: la v0.9.5 è **memoria/recall**, non vera `model-based planning`. Quella resta una tappa futura.
6. Gli eventi storici pre-0.9.5 **non possono essere ricostruiti retroattivamente**: il nuovo archivio parte vuoto; NON azzerare gli altri cervelli.
7. Il dato di app `PORTA 111U` mostrato dall'utente corrisponde al **massimo registrato**; non prova che sia entrato, che abbia previsto l'esito, o che sapesse ripetere il comportamento.

## 6. Test automatici vs test biologico sul telefono
Test JVM `EpisodicMemory.main`: evento autentico sopra soglia, non acquisire eventi insignificanti, salvataggio e ripristino, lettura sensoriale, bias, pesi copiati, outcome positivo osservato durante replay, ricordi conservati dopo morte, niente richiami in spazio percettivamente vuoto e serializzazione corrotta rigettata. Test esistenti su ScarcityLab devono restare verdi. Build Android + firma CI obbligatorie; mantenere SHA e versione release corretti nel CHANGLELOG.

**Piano di prova manuale Galaxy S21**:
1. Installare **sopra 0.9.4** e controllare che 38k+ esperienze e pesi sopravvivano all'update, senza AZZERA.
2. Attivare il test stanza, lasciare ZERO sperimentare con 1x/5x per vedere la porta (20x per accumulare dati), monitorare porta max, prese e centro del corpo oltre la soglia.
3. Dopo **il primo evento fisico importante** controllare CERVELLO → ANALISI → MEMORIA DELLE ESPERIENZE: deve crescere `Sequenze significative`. Il contatore `Situazioni riconosciute` può aumentare senza richiamo di azione.
4. Dopo una morte o nuovo cancello osservare `Azioni realmente richiamate` e `Effetti fisici riprodotti` senza pretendere un successo. Verificare eventuale nuova apertura e **attraversamento della porta con raccolta energia**.
5. Verificare che non rimanga a oscillare/cambiare pesi incessantemente, che il reset SOLO MEMORIA EPISODICA non cancelli altre reti e che un riavvio dell'app mantenga i ricordi.
6. Se non torna alla tattica, aumentare qualità del contesto (sensori dei moti relativi degli ostacoli) e controllare la finestra pre-evento, NON introdurre tasti o script per aprire il cancello.

## 7. Prossime tappe
- Rafforzare memoria episodica con **sequenze a lunghezza dinamica** e verifica di outcome a parità di sensi invece di rassomiglianza media su tutti i canali.
- Confrontare cervello vergine, 0.9.4 senza recall e 0.9.5 con recall nelle **identiche mappe/stati iniziali** con random seed replicabile e metriche di distanza netta, energia, spostamento porta e stanze concluse.
- Trasformare il modello `11.664` pesi in vero pianificatore a più passi (*imaginative rollouts*) soltanto dopo averne verificato affidabilità in situazioni mobili: evitare predizioni false ricorsive.
- Separare “aperto” da “attraversato” e “raccolta risorsa”, senza premi semantici non osservabili; instrumentare efficacia delle prese dalla prospettiva tattile di ZERO.
- Migliorare robustness prestazioni Galaxy S21 in x20, limitare e profilare numero memorie e costi di confronto.

## 8. Come riprendere in una NUOVA CHAT
1. Aprire questo documento, `OVERMIND_START_HERE.md`, `docs/HANDOFF.md`, `docs/ARCHITECTURE.md`, `docs/WHITEPAPER.md`, `docs/ROADMAP.md`, `docs/CHANGELOG.md`, `AGENTS.md`.
2. Leggere il branch `feature/overmind-inapp-updater` per il codice android ufficiale, non il `main` (main ospita il vecchio Idle Runner + documenti/releases/updater).
3. Per nuove modifiche creare branch di prova a partire dalla versione stabile, passare JVM/Android CI, usare firma storica e pubblicare release GitHub APK verificata. Non inventare un link APK senza asset pubblicato.
4. NON affermare che ZERO ha già appreso a riprodurre apertura/ingresso: controllare screenshot e telemetria dal telefono. Documentare ogni osservazione successiva qui o HANDOFF prima di chiudere la chat.


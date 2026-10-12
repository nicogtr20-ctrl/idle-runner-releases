# Architettura tecnica di OVERMIND ZERO

## Applicazione
- Android Java nativo, Gradle 8.11.1, AGP 8.7.3, Android minSdk 24.
- Repository `nicogtr20-ctrl/idle-runner-releases` (privato), OVERMIND su branch `experiment/...`.
- Per v0.2 Android applicationId `com.overmind.zero.brain`, per v0.3 `com.overmind.zero.world` (app separata a causa di potenziali certificati debug diversi).
- Dati motor learning in `SharedPreferences` (`zero_motorbrain_001`), memoria mondo in chiave `world_knowledge_v03`; app ID diverso = area dati Android diversa.

## Controller motorio: MotorBrain.java
- Rete neurale feedforward con **62 parametri**, inizializzati casualmente e senza pretraining.
- Corpo composto da **10 punti di superficie** elasticamente vincolati, massa centrale, forze di contrazione, attrito/aderenza variabile.
- Il cervello controlla tensione muscolare locale e grip di ciascun punto.
- Addestramento con **neuroevoluzione**: mutazioni dei parametri, valutazioni di displacement e conservazione del miglior candidato. Non è backpropagation e non è AGI.
- Fitness su tre direzioni di addestramento; benchmark su altre tre direzioni. Miglioramento su benchmark non basta da solo per dimostrare robustezza generale.

## Mondo sconosciuto: UnknownWorld.java
- Tre segnali distinguibili attraverso sensori simulati. Le regole fisiche reali sono nell'Environment e non sono rese note alla policy.
- Impara una media aggiornata delle conseguenze (energia, movimento) per classe di segnale.
- Sceglie target combinando valore stimato, novità e distanza; conserva conteggi ed estimatori.
- L'ambiente reale è ancora troppo semplice per inferenze causali e modelli di esplorazione sofisticati.

## UI e test
- `GameActivity.java` disegna corpo e mappa via Canvas Android e contiene controlli per addestramento, playback e benchmark.
- Workflow di CI `zero-brain-release.yml` per ZERO-001; `zero-world-release.yml` per ZERO-002.
- Gli script testano il cervello via `javac`/JVM prima del build Android. Ci vuole poi una prova di uso reale e consumo di memoria sul Galaxy S21.

## Principi per le prossime versioni
- Scindere `World`, `CreatureBody`, `Sensors`, `MotorPolicy`, `Memory`, `Motivation` e `Renderer`.
- Evitare falsi effetti appresi: legare spinta/manipolazione ai contatti reali tra masse e oggetti.
- Persistenza con versionamento formato memoria e migrazioni.
- Registrare in debug decisione, input sensoriali, previsione, azione, risultato; mantenere verificabile la reale esperienza.
- Non confondere trasparenza UI dei risultati con "pensieri" narrati dal soggetto.

## ZERO-003 / v0.4: fisica e apprendibilità della manipolazione
- `ContactLearning.java` contiene collisioni con oggetti mobili mediante perimetro elastico e nucleo, impulso da velocità relativa e correzione dell'interpenetrazione.
- `UnknownWorld.Entity.touch` non aggiunge più velocità agli oggetti mobili.
- `MotorBrain.Body.step` accetta opzionalmente 18 nuovi pesi di controllo per contrazione e attrito, senza modificare i 62 pesi motorii salvati.
- Neuroevoluzione di `ContactLearning.Trainer` esegue rollout fisici e confronti holdout su direzioni non usate per imparare.
- `GameActivity` aggiunge contatore di oggetti realmente mossi e benchmark di manipolazione, conserva vecchi dati `SharedPreferences` e nuove chiavi `hand_*_v04`.
- Solo simulazione; nessun LLM, nessun pensiero astratto, nessun ragionamento causale generale.
- Branch firmato OTA: `feature/overmind-inapp-updater`; manifest https://overmind-zero-updates.vercel.app/latest.json. Prima prova sul dispositivo della 0.4 pendente.

## ZERO-005 / DecisionBrain
- `DecisionBrain.java`: rete 7 ingressi, 10 neuroni nascosti, 1 uscita (91 parametri) casuali. Apprendimento online tramite backpropagation del valore previsto degli stimoli in un contextual bandit, con esplorazione epsilon-greedy.
- `GameActivity` chiama `decisionBrain.choose`, non più `mind.choose`. Aggiorna la rete quando un contatto cambia energia o un oggetto viene spostato fisicamente; penalizza lievemente tentativi abbandonati.
- Salvataggio separato `decision_brain_v05`, preservando i vecchi pesi. La rete NON inventa autonomamente la propria architettura, gli obiettivi fisiologici o una lingua.

## ZERO-006 — Cervello decisionale autoespandibile
- Architettura dinamica del contextual bandit: ingresso 7, nascosti da 10 a 24, uscita 1; da 91 a 217 parametri (9 aggiuntivi per neurone). La rete nasce random, eredita esattamente i pesi da v0.5 attraverso migrazione serializzazione v1→v2.
- Controllo di crescita progettato: valutazione media errore recente, finestra replay fino a 48 esempi, holdout ogni quarto, candidato con output zero per preservare comportamento, training soltanto nuovi pesi, accettazione se migliora su holdout. Criteri fissi nel codice, non crescita illimitata/non autocoscienza.
- `BrainStatsActivity` legge il modello e pesi salvati: ABILITA mostra metriche vere, PARAMETRI elenca pesi corpo (62), manipolazione (18), scelta (91→217), EVOLUZIONE mostra topologia e tentativi. `initialSnapshot()` conserva i pesi al momento in cui sono nati. La pagina è uno snapshot preso all'apertura e pausa temporaneamente il loop di gioco.
- Android manifest registra `BrainStatsActivity`, accessibile dalla UI `CERVELLO`. Nessun cambio applicationId, chiave firma o formato dei vecchi dati motori.

## ZERO-007 — corpo deformabile con pseudopodi emergenti
- `Morphology.Surface`: 32 nodi perimetrali reali con x/y/velocità e molle fra vicini, elasticamente connessi al precedente nucleo + anello motorio di 10 nodi; nessuna parte predefinita come braccio.
- `Morphology.output`: rete locale comune 9 ingressi × 6 neuroni nascosti × 2 uscite = 74 pesi, inizialmente senza estensione, capace di contrazione radiale ed effetto tangenziale indipendenti per ciascuno dei 32 punti.
- `Morphology.collide`: impulso fisico reale sui segmenti della membrana mobile. Il movimento degli oggetti deriva dalle collisioni, non dal numero di tentativi.
- `Morphology.Trainer`: neuroevoluzione su rollout corporei con avvicinamento, spinta e consumo energetico; scelta migliore conserva i pesi. Il vecchio `MotorBrain` (62) e `ContactLearning` (18) sono mantenuti, `DecisionBrain` (91+) continua invariato. Corpo e membrane sono simulati separatamente per mantenere compatibilità dei salvataggi.
- `GameActivity` ora renderizza il vero contorno esterno di 32 masse e aggiorna le statistiche; `BrainStatsActivity` espone MEMBRANA/74 pesi in più e parametri iniziali/attuali. Chiavi persistent nuove `morph_brain_v07`, `morph_attempts_v07`, `morph_accepted_v07`.

## ZERO-008 — ScarcityLab e fisica della stanza
- `ScarcityLab` crea una stanza rettangolare persistente in coordinate del mondo e una porta scorrevole rigida vincolata lungo y. Collisioni AABB proiettano fuori dalle pareti nucleo, 10 masse interne e 96 nodi esterni. `solveGate` misura nodi su entrambe le facce e la loro componente tangenziale di velocità; unica forza che muove il blocco se stretto. Nodi opposti devono essere materialmente separati lungo la membrana.
- `UnknownWorld.Entity` ora è consumabile per segnali nutritivi, `newHabitat(...,scarce)` limita spawn. Salvataggi di esperimenti indipendenti dalla crescita dei cervelli.
- Allenamento morfologico aggiunge `attemptGate` e `ScarcityLab.trainingFitness`: la simulazione ottimizza avvicinamento, contatti bilaterali, effettivo spostamento della porta e accesso al nutrimento. Una terza posizione è usata per verificare capacità di generalizzazione.
- Compromessi: senza rilevazione esplicita dei muri e senza world model gli episodi potrebbero non essere risolti; reward e navigazione sono ancora impostati nel codice. Prestazioni 96 nodi/colpi da misurare sul Galaxy S21.

## ZERO-008.3 — Adesione locale come nuova abilità appresa
- `Morphology`: terza uscita di rete, non un pulsante del giocatore né una preimpostazione di arti: i 96 punti condividono gli stessi 8 nuovi parametri e ricevono un segnale tattile locale prodotto dai contatti fisici. La memoria della precedente rete 74 parametri è preservata append-only.
- `ScarcityLab`: attrito porta possibile solo tra due attuatori distinti su facce opposte, con soglia di comando e hold di 75 ms. Contatti passivi non muovono la porta. Vecchio benchmark a contatto automatico non è più valido.
- Addestramento su simulazioni fisiche con premi misurati dopo adesione attiva e spostamento; nessuna istruzione semantica di utilizzare due braccia. L'agente potrebbe non scoprire mai il comportamento complesso con la sola evoluzione.
- `GameActivity`: punti dorati quando la presa è fisicamente ingaggiata, HUD con ADESIONI/TOCCHI/SENZA PRESA, memoria e morte per esaurimento non toccate.

## ZERO-009 — Nuovo ciclo percettivo-azione-conseguenza
- `SensorimotorMind.Senses.read(...)`: 26 canali fisici, senza accesso a `doorY` come segnale di successo, senza target assoluto nascosto. Stato interno (energia, velocità, deformazione, feedback adesivo), 8 distanze ostacoli per raycast e 12 canali di visione cromatica; energia dietro ostacolo opaco occlusa.
- `SensorimotorMind`: 16 movimenti con o senza amplificazione adesione, selezionati tramite punteggio TD appreso con esplorazione. Predittore lineare multi-output del prossimo vettore sensoriale; 11.232 parametri di transizione e 432 di valore azione. Apprendimento online su osservazioni reali al completamento di ciascun intervallo motorio, non sulla regola «cancello aperto».
- `Morphology.Surface.step(...,gripDrive)` eredita tutti i pesi pre-v0.9; aggiunge una modulazione tattile dell'adesione generata dall'azione del modello, non da un trigger automatico del cancello. Restano obbligatori contatto fisico, hold e due punti distinti.
- `GameActivity` non calcola più la direzione esatta del target: nessun `decisionBrain.choose(environment,...)` e niente trials offscreen su un target noto. Pesi preesistenti di locomozione/manipolazione/membrana/valori storici restano nel database. Modello nuovo `sensorimotor_model_v09` persistente; vita mortale.
- Importante: previsione associativa di *un solo passo* + apprendimento TD non equivale a un world model generale capace di ragionare verbalmente «più spazio ora». Va dimostrata la generalizzazione e la capacità fisica sul dispositivo.

## ZERO-009.1 — Persistence and evaluation interfaces
- `LearningDiagnostics`: bucket heldout ogni quinta transizione reale, escluso dai gradienti forward/value, errore RMSE modello e baseline no-change; serie 48 gruppi, serializzazione separata in `sensorimotor_diagnostics_v091`. Non modificarla indipendentemente dal modello di vita per evitare duplicati di osservazioni.
- `BrainToolsActivity`: vista nativa Android per il grafico e i comandi reset, con richieste disaccoppiate dal ciclo foreground. Manifest aggiunge activity privata.
- `ResetDomains`: elenco esplicito e testato di chiavi SharedPreferences non sovrapposte per modello, choice, motor, manipulation, membrane, exploration, room; reset ALL azzera intero profilo, salvaguardando però package e firma Android esterni al profilo.
- `GameActivity.load/consumeResetRequest`: applica reset atomico prima di ricaricare i parametri dai dati persistenti, poi resetEpisode; preserva altri sottosistemi e obbliga conferma UI. L'apprendimento online rimane sempre attivo.
- Nota: risultati comparativi “azione policy vs scelta casuale” sono osservazionali; per valutare causalità occorre campionare stessi stati e intervenire sul mondo con gruppi di controllo espliciti.

## ZERO-009.2 — apprendimento fisico durante il gameplay
- `OnlineBodyLearning` usa soltanto transizioni della stessa simulazione visibile: 8 esperienze con pesi memorizzati, 8 con piccoli pesi mutati, poi confronto correlazionale di premio sensoriale, spostamento e contatti. I criteri di contesto comparano energia, stessa stanza e direzione media; non è equivalente a randomizzazione né esperimento A/B su medesima condizione. I nuovi pesi vengono applicati e persistiti solo dopo accettazione, gli altri rimangono; i tentativi non conclusi si cancellano alla morte/reset.
- Il modulo `SensorimotorMind` da 11.664 pesi continua a imparare ogni ~0.65 s. Il vecchio `DecisionBrain` rimane memorizzato e continua a ricevere osservazioni storiche, ma non sceglie la direzione attiva.
- La rarità non aggiunge una categoria semantica al cervello: `UnknownWorld.Entity` ha un effetto fisico nuovo, e `Senses.read` presenta la sua firma con due segnali cromatici esistenti. Totale ingressi sempre 26, nessuna migrazione dei modelli.

## ZERO-009.4 — ambiente vissuto, ricompensa motoria e diagnostica
La telemetria `MotionExploration` prende soltanto coordinate e riserva *dopo* i passaggi della fisica. Calcola distanza netta dall'origine vita e celle nuove visitate; la griglia 75-unità è globale nello spazio ma azzerata soltanto alla morte/riavvio dell'habitat, con totale storico sommato. Il cervello non vede indici di cella/posizioni privilegiate, solo `SensorimotorMind.Senses`. L'ottimizzatore corporeo `OnlineBodyLearning` osserva finestra di 8 azioni e compara un candidato in 8 azioni seguenti: score con forte priorità net, novità, energia/contatti rispetto al percorso totale. È selezione correlazionale *in vita*, non controfattuale sincronizzato. Stato trial conteggiato separatamente in pref per i tre gruppi, vecchi 62+18+82 parametri rimangono migrabili.


## v0.9.5 — EpisodicMemory e rinforzo da effetto ambientale generico
- `EpisodicMemory` (JVM/Android puro) conserva un anello di 9 step `[float[26] senses, int action]`, creati dopo ogni azione reale, e al massimo 18 eventi autobiografici con copia dei parametri `motor[62]`, `hand[18]`, `morph[82]` realmente attivi nel frame. Limiti anti-corruzione per restore da SharedPreferences `episodic_memory_v095` formato `em1`, memoria locale persistente oltre morte, senza cancellare `wm1`.
- In `GameActivity`, `environmentEffectSinceAction` è la **variazione fisica misurata DOPO ScarcityLab.constrain** e i movimenti di dischi confermati da una soglia fisica; `energyGainedSinceAction` arriva dai bilanci della riserva. Nessun comando di apertura imposto. Il sensore 26 non riceve coordinate privilegiate: i valori fisici aggiuntivi sono target di ricompensa e selezione offline **soltanto dopo la transizione**, non descrizioni della soluzione all'azione futura.
- `SensorimotorMind.attachMemory` inietta la memoria senza cambiare modello serializzato. `choose` può attribuire +0.72 alla proposta contestuale, senza disabilitare epsilon o azioni alternative; `observe(after,dt,effect)` può accreditare il cambiamento fisico generico misurato. Il vecchio overload `observe(after,dt)` e i 11.664 pesi rimangono compatibili.
- Il replay fisico riusa temporaneamente un set di pesi registrati quando la proposta è **davvero scelta** dalla rete. È proibito confonderlo con un candidate di `OnlineBodyLearning`: `abortTrial()` durante richiamo e nessuna accettazione di mutanti in quel passo. Morte `resetLife()` azzera solo buffer/traccia transient, gli eventi restano.
- `ResetDomains.EPISODES` contiene la chiave esclusiva `episodic_memory_v095`; non condivide chiavi con gli altri 7 reset. `BrainToolsActivity` mostra contatori distinti: catture, recognition, azioni richiamate ed effetti fisici ulteriori osservati; non interpreta mai questi contatori come completamento della stanza.
- Prestazioni: retrieval limitato a 18 eventi × 9 azioni × 26 input, formato persistente limitato <220k caratteri e cooldown anti-stallo; non usa un planner di alberi di ricerca.
- Vedi `docs/EXPERIMENT_0095.md` per handoff dettagliato.

## Telemetria vita v0.10.5
`LifeStatistics` è un osservatore separato dai controller. Il tempo è simulato: ogni slice fisica chiama `tick(dt)`, quindi x5/x20 produce rispettivamente 5x/20x secondi di gioco per unità di tempo reale; la pausa non aggiunge tempo.

Persistenza:
- chiave invariata: `life_statistics_v104` per non rompere slot e aggiornamenti;
- formato nuovo `ls2`, con migrazione read-only del precedente `ls1`;
- ring delle ultime 50 vite individuali per grafici e due finestre 25+25;
- accumulatori permanenti `lifetimeSum[11]`, `lifetimeMax[11]` e `lifetimeCount` per medie/record oltre la 50ª vita;
- flag `lifetimeComplete`: i personaggi che esistevano prima della telemetria non vengono presentati falsamente come storici “dalla nascita”.

`StatisticsActivity` legge questi dati e calcola trend/indice sintetico. Nessun valore della pagina viene restituito a `SensorimotorMind`, `OnlineBodyLearning`, `EpisodicMemory`, planner o reward.

## Memoria di lavoro temporale v0.10.6
`TemporalWorkingMemory` è collegata a `SensorimotorMind` ma serializzata separatamente nella chiave `temporal_working_memory_v106`.

Struttura:
- 16 unità di stato ricorrente; input = sensi correnti, delta rispetto al passo precedente e azione autonoma appena eseguita;
- connessioni del reservoir deterministiche/fisse, senza etichette del mondo;
- 7.072 pesi appresi per predire 26 delta sensoriali per 16 azioni da 16 stati + bias;
- 272 pesi appresi di valore contestuale per 16 azioni;
- totale nuovi parametri appresi: 7.344;
- traccia ritardata: ultime 8 decisioni contestualizzate, usata solo quando arriva un vero effetto energetico o spostamento fisico.

Gating:
- le predizioni temporali sono valutate sugli stessi eventi held-out usati per evitare auto-valutazione del modello one-step;
- minimo 25 campioni held-out;
- nessuna influenza se errore temporale >= 96% dell'errore del modello istantaneo;
- confidenza massima 0,70 e contributo alla scelta limitato a ±0,45.

Ciclo vita:
- `resetEpisode` (morte/nuova vita) azzera stato ricorrente e trace, non i pesi;
- `resumeEpisode` conserva il contesto serializzato quando si riapre la stessa scena fisica;
- il reset “MODELLO DEL MONDO” cancella anche la memoria temporale; gli altri domini restano separati.

## Skill temporali autonome v0.10.7
`TemporalSkillBank` è un archivio dinamico separato dalle banche di pesi motori `MotorSkillBank` e `GripSkillBank`.

Dati per skill:
- lunghezza 2–8;
- azione discreta realmente scelta a ogni passo;
- vettore hidden da 16 elementi della memoria temporale prima di ogni azione;
- conferme, fallimenti, attivazioni, completamenti, utilità media.

Scoperta: dopo un effetto positivo energetico/fisico reale vengono estratti i suffissi disponibili della trace recente; la prima occorrenza resta dormiente; il matching richiede stessa sequenza d'azioni e vicinanza dei contesti temporali; servono almeno due conferme prima di produrre bias.

Replay: richiede anche `TemporalWorkingMemory.confidence()>0`; bias massimo **0,64**, mai azione forzata. Divergenza del nuovo hidden rispetto al passo successivo appreso causa abort + fallimento. Dopo l'ultima azione attende al massimo 3 decisioni per una conseguenza positiva reale; danno o timeout abbassano l'affidabilità.

Persistenza: `temporal_skills_v107`. Reset vita/ripresa cancella soltanto la trace e l'esecuzione in corso; l'archivio consolidato persiste. Reset selettivo dedicato disponibile.


## ZERO 0127 — separazione fra contatto, presa e correzione di collisione
- Le `UnknownWorld.Entity` di legge 5 espongono `bilateralHeldNow` **solo durante la vita fisica corrente**, dopo `SquareGripPuzzle.constrain` e due lati opposti effettivamente confermati. Una cassa energetica non può essere traslata da un risolutore di overlap soltanto per essere una square.
- Le costruzioni di legge 6 espongono `panelHeldNow` **solo dopo il vero contatto e il comando locale RED verificato** da `PanelPhysics.constrain`. Il vecchio `boxGripHold` serializzato resta informazione storica per la ripresa ma non autorizza una spinta dopo reload finché non riconfermato.
- I risolutori `ShelterEcology.separateBoxes` e `PanelPhysics.separate` proiettano solo corpi con una propria presa corrente; correggere geometria non genera reward, spostamento non autorizzato o un sigillo energetico aperto.
- `SpatialPerception` canonizza celle neutre zero durante encode, accetta vecchie snapshot sp1 contenenti zero e non perde le reti di predizione/valore.
- Modifiche conservative a spawn/fame/vento sono leggi ambientali, senza cambiare gli input sensoriali o insegnare una procedura. Vedi [EXPERIMENT_0127](EXPERIMENT_0127.md) per test e limiti.

## Release preparation — ZERO 2.0 passive observation, OTA 100 candidate (2026-10-13)

Official OTA path is `feature/overmind-inapp-updater` → `.github/workflows/overmind-ota-publish.yml` → owner-key signed APK → GitHub Release → `main/updater/public/latest.json` and immutable APK → Vercel manifest consumed by in-app AGGIORNA. This candidate is staged separately from research PR #31. Original `com.overmind.zero.ota` signature and persistent 12 saved slots must remain intact. VersionCode derives from the next official publisher run number (expected ≥1100), not the old signed experimental preview 1099.

The candidate includes exactly the already tested v20 passive observers, existing base motor policy, and `TelemetryGitHub.partition()` fix preserving `extras.zero20Passive` after multipart upload. No model advice biases the motor controller; no change to energy rules or sensor physics. Source-pinned stable checks are UPDATED to the SHA of the actually reviewed source files, never disabled; five new JVM regressions are added before official release signing.

The initial Galaxy S21 report confirms the original working profile is still present and v20 models have collected real observations. No independent skill or survival improvement is established. Release acceptance: independent staging JVM/Android QA GREEN, official stable publisher GREEN, signed APK certificate/package/version and SHA verified, downloadable manifest/APK responsive, user-controlled install and slot continuity confirmed on phone. If any gate fails, **do not claim availability through Aggiorna**.

## ULTIMO RILASCIO ZERO — OTA161 FIRMATA, 15 OTTOBRE 2026

**Stato verificato:** OTA161 `0.11.6-ota.161`, versionCode **1161**, applicationId `com.overmind.zero.ota` INVARIATO; [APK ufficiale](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1161-eea4e20b0975.apk), SHA256 `eea4e20b09751ff5f8655e6767067383aa0b5eaaf8a17ed1c17e9e3811e16796`, 451656 byte. [Publisher firmato SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38799181044), [verifica HTTPS pubblica indipendente SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38799646079): manifest HTTP200, APK HTTP200, dimensioni, applicationId e SHA256 esatti. Hosting Vercel production READY, alias ufficiale aggiornato. PR #71 integrata nel ramo `feature/zero22-active-recurrent-ota117`, senza migrazione distruttiva dei 12 slot o dei pesi 2.2.

**Corpo:** fisica con autononintersezione/repulsione membrane realmente materiali e compliance della tensione sulle estensioni; 320 **pesi corticali regionali** aggiuntivi, NON 320 neuroni, tutti inizializzati a zero; 84 pesi motori originali e cervello ricorrente con neurogenesi conservati. Contrazioni muscolari più coerenti e parametri regionali effettivamente salvati, visualizzati in Corpo/Parametri. Nessuna andatura o attrazione al cibo preprogrammata.

**Benchmark laboratorio:** [QA 38798647619](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38798647619) SUCCESS JVM e Android Debug; test stimolati con elettrodi solo in laboratorio: tre pseudopodi distinti, retrazione ~9,99U, netto su percorso controllato **21,26U** rispetto **13,73U** OTA160; nessuna dimostrazione ancora di locomozione autonoma sul telefono. Il test reale Galaxy S21 rimane necessario, specialmente NETTO/VIA, varietà delle forme, blu/rossi a pari minuti simulati.

**Installazione:** Impostazioni → Aggiorna, sopra app esistente; NON disinstallare né azzerare memoria. Dettagli: [audit OTA161](ZERO_OTA161_BODY_PHYSICS_AUDIT.md) e [PR #71](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/71).

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

## 0.10.9 — backward-compatible physical proprioceptive modality
`BodyProprioception` owns its independent `bp1` weights and real 8-sector x 4-feature local mechanical sensory map. Measurements derive solely from existing `MotorBrain.Body` center, `Morphology.Surface` 96 masses, `touchSense` from door contact, new non-actuating `touchPulse` from actual movable disk physics, `gripHold`, `extension` and radial `vx/vy` relative to core velocity. Only the sampling schedule within `SensorimotorMind.observe()` at actual ~0.65 simulated-second decision intervals scans all points; x20 remains physical simulation, tactile pulse decays by simulated dt. Independent 3088-weight local learned proprioceptive predictor and action values produce zero choice bias until >=30 heldout predictions and >=4% improvement over unchanged-body forecast, then max ±0.23. NO named-door, food, reward shaping or action scripts. `SensorimotorMind.S=26` and model serialization wm1 are unchanged, plus old em3/tw1/ts1/ls2. `GameActivity.save/load` stores `body_proprioception_v109` alongside old keys; the typed existing 12-slot SaveProfileCodec automatically captures it. `ResetDomains.PROPRIOCEPTION` resets only the new module. UI BrainToolsActivity shows each of 8 sectors and confidence; BrainStatsActivity counts the new model. See docs/EXPERIMENT_0109.md for tests and limits.

## 0.11.0 — independent spatial learning with observed occupancy, no map leak
`SpatialPerception` samples **16** physical raycasts from the nucleus using the legacy `SensorimotorMind.Senses.solid` predicate and eight 22-unit increments; ray stops at first obstacle and cannot infer geometry behind it. An independent bounded 1024-cell map (36 U/cell) integrates sightline occupancy and actual nucleus displacement (odometry). A simultaneous ray-fusion two-pass method ensures neighboring rays traversing the same cell do not erase confirmed solid evidence from the same frame. Local geometry reset on true death; learned action-conditioned prediction and value weights retained. Bias to `SensorimotorMind.choose` is zero before >=30 heldout samples and >=4% improvement over unchanged ray forecast, then bound ±0.19. `SensorimotorMind.S=26` and its wm1/tw1/ts1/bp1/em3/ls2 schemas remain unchanged; spatial state uses `sp1` in pref `spatial_perception_v110` as another atomically captured member of `zero_motorbrain_001` and the previous `SaveProfileCodec` 12 named slots. Sensor samples only on real decisions, ~0.65 simulated seconds apart, never each x20 physics subframe. `BrainToolsActivity.SpatialMapView` renders only observed cell evidence, not hidden exact world; independent `ResetDomains.SPATIAL`. Detail `docs/EXPERIMENT_0110.md`.

## 0.11.1 — empirical cause/effect separate from world geometry and semantics
`SpatialPerception` already stores only 16 occluded rays, odometry-aligned observed occupancy and 0.11.0 learned spatial values. `CausalExperiments` now reads read-only perceived odometry and a 13×13 observed grid at the start/end of *actual* motor decisions; corresponding same-grid-cell evidence is compared to avoid egomotion mistaken for environmental motion. A blocked-to-free observation becomes only a candidate, with retrospectively confirmed passage after the organism physically occupies that cell on a subsequent movement. 16 selected actions × three rough obstacle-range contexts maintain learned moving/changed/actual-outcome averages. Old `SensorimotorMind` exploratory epsilon randomization supplies intervention samples. Additional model bias requires >=30 heldout comparisons (>=5% improvement over same-context null) and >=3 randomized selected-action vs >=5 different-action cases, bounded ±0.15; no goal labels, route script or exact gate classification. `causal_experiments_v111` has bounded validated `ce1` serialization and is stored in the existing shared working preferences and old named 12-slot codec; no changes to legacy models `wm1/tw1/ts1/bp1/sp1/em3`, original 26-sense inputs, application ID, keystore or physics. Independent CAUSAL reset. See `docs/EXPERIMENT_0111.md`.
## 0.11.2 — sensor to real energy to learned motility, not locomotion reward
`InstrumentalNavigation` is independent of legacy `SensorimotorMind.S=26` weights and all existing serialized branches. At chosen-action sampling, `Senses.spectral8(b,room,entities)` exposes only physically visible raw light frequencies in 8 directions with occlusion, including cooling objects that remain visibly present. Real `GameActivity.resolveObjects()/chamber.food` contact energy flows into `SensorimotorMind.contactOutcome()`, updates both legacy episodic/energy credit and new frequency-valence store. A separate sensorimotor action learned effect is real nucleus displacement `body.cx/cy` per actually chosen motor command, evaluated on heldout transitions against no-motion. Navigation predicts useful access from learned body displacement projected onto observed directional light and learned signed energy effect, with bounded score ±1.6; it does not use true object coordinates or preassigned hue utility and does not force any action. The main TD drive now consumes ACTUAL interval energy + tiny prediction/novelty reward - step cost, no `.24f*realForward`; existing Q value enters as small bounded prior to prevent old locomotion preference domination. Persisted `in1` at `instrumental_navigation_v112` in existing GameActivity save/load and 12 named slots; on restart sensory sample is reconstructed from actual saved scene rather than persisted as stale truth. Separate `ResetDomains.NAVIGATION`, ANALISI UI and CERVELLO parameters. `DecisionBrain` and `UnknownWorld.Mind` continue to collect historical data but are not motor policies; physically grounded active modules remain in SensorimotorMind. Tests and signed OTA `docs/EXPERIMENT_0112.md`.

## 0.11.3 — physically grounded benchmark isolation, not hidden knowledge
`BehavioralLaboratory.java` is a pure Java simulation harness with no Android APIs or preferences. It clones the same saved 26-dimensional SensorimotorMind, the separate instrumentality, episodic/temporal/body/spatial/causal predictors, and frozen previously learned motor/hand/membrane controllers; uses actual `MotorBrain.Body.step`, `Morphology.Surface.step`, true energy and damage contacts `UnknownWorld.Entity.touch`, true `ScarcityLab.constrain` gate mechanics and resource core contact. Three trial cases (energy, harm, gate) × three fixed initial variants × two otherwise-identical choices (only `InstrumentalNavigation.bias` excluded from `SensorimotorMind.choose` in A) × 32 simulated seconds; no target angle or coordinates fed to policy, no trained or real user weights changed by trial. `BehavioralLabActivity` starts a cancellable background worker, takes a read-only snapshot of existing `zero_motorbrain_001` preferences and stores formatted result exclusively in `zero_behavior_lab_v113`. Live policy `navigationAdviceEnabled=true` by default and app's 12 named slots unchanged. The harness intentionally does not call `OnlineBodyLearning` controller-mutation search and cannot model all live GameActivity events, making results controlled evidence, not a conclusive gameplay success certificate. See `docs/EXPERIMENT_0113.md`.

## 0.11.5 — recovery from real contact injury, not semantic avoidance
Added independent `AdaptiveSafety` (key `adaptive_safety_v115`) to `SensorimotorMind`. `GameActivity.resolveObjects` real negative deltaEnergy via `contactOutcome`, AFTER `InstrumentalNavigation.energyOutcome` => mark short transient injury signature only if actually learned sign <0. `InstrumentalNavigation.learnedHarmExposure(a)` integrates 3 frequency ×8 sector actually visible light with physically learned nucleus dx/dy for validated action (3+train/2+holdout beating stationary), signed projection for approaches/withdrawals, no entity metadata/target coordinates. Safety bias `-.95*urgency*exposure` clipped ±.95, active at most 4.5 simulated sec following actual loss. Lower decision interval from .65 to .18 ONLY when actual lost contact energy in current action; don't update ordinary actuator effect model with such short transitions. Under recent injury, epsilon still selects at random, among actions with full score≥best−.75 IF safety model has advice, otherwise original uniform random. Physical withdrew counter only if moved ≥3 units and negative wavelength optical intensity fell>.012; remains an uncertain optical proximity proxy. Counters persistent, transient perceived scene and danger forgotten across real life resets/restart. UI read-only for safety and separate reset; 12 named saves, original 26 input policy/motor and old memories unchanged. `BehavioralLaboratory` adds CLUSTER and CHOICE real scenes to existing 3 for 30 paired genuine body-physics trials; measurements observer-only, laboratory doesn't modify live preferences. Important limitation: still not general multistep escape planning and body controllability can prevent actual retreat. More `docs/EXPERIMENT_0115.md`.


## ZERO 2.2 attivo — OTA118 verificata, 13 ottobre 2026
Versione APK `0.11.6-ota.118`, codice **1118**, package `com.overmind.zero.ota`. Fonte rilasciata `685031f7b8856f8eb351401b99e22b0de9604330`, PR #41; branch ufficiale `feature/overmind-inapp-updater`. Nucleo ricorrente prima della scelta reale, corpo/raggi/energia nella previsione, replay persistente con holdout separati, recupero temporaneo di tecniche consolidate con interruzione e isolamento delle finestre corporee, campionamento sensoriale ai confini della decisione per TEST MAX. È un reservoir fisso con readout appresi (40.560 pesi), non Dreamer completo o superintelligenza dimostrata.

Verifiche: **70 test JVM locali PASS**, controlli del publisher PASS, [Android staging 38467622938 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467622938), [publisher firmato 38467965975 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467965975). APK pubblica HTTP200, **382.200 byte**, SHA-256 `c12528432b0e59432a390847deb3d1482d1ea4760c59d1b19049abea11d61f4c`; firma V2 verificata crittograficamente e fingerprint del certificato identico alla OTA116: `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`. Manifest pubblico verificato a 1118. [APK immutabile](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1118-c12528432b0e.apk). [Release GitHub](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.118).

Installare sopra l'app attuale tramite AGGIORNA o APK; non disinstallare e non azzerare le memorie. Nuovo tasto **CONFRONTO CERVELLO 2.2** in CERVELLO/ANALISI: 24 prove fisiche isolate (12 coppie) sul profilo salvato; stesso seed/scena, controllo precedente contro core attivo+recupero. Non modifica il personaggio; report separato `zero_core_lab_v22`, upload solo esplicito tramite CONDIVIDI DATI, conservato nei multipart. L'arena ha controllori fissi, senza tempeste/streaming: screening, non equivalenza integrale al mondo.

**Ancora da verificare sul Galaxy S21:** installazione in-place e continuità dei 12 slot, FPS/termica e nuovo moltiplicatore TEST MAX rispetto al x29 riportato, risultati A/B, retention e adattamento a scene nuove. Nessun uplift comportamentale è già dimostrato. [Paper completo e checklist](ZERO22_ACTIVE_CORE_AND_DEVICE_VALIDATION.md). Il publisher 117 sul diverso branch `feature/zero22-active-recurrent-ota117` non è questa release: ultimo rilascio verificato qui è OTA118.


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


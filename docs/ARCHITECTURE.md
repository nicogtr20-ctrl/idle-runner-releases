# Architettura tecnica di OVERMIND ZERO

## ZERO 0.9.8 PATCH — eliminazione incentivo angolare alle risorse
`SensoryValenceGuide.java` rimosso, `SensorimotorMind.attachSensoryValues` e `sensoryGuide.bias` rimossi. `SensorimotorMind.choose` usa ora esclusivamente i Q appresi per i 26 sensi/16 azioni, curiosità sensoriale, memoria episodica e penalità temporanea durante stalli, epsilon esplorativo. `ExperienceCredit.java` registra un ring max12 (sensi+azione realmente scelta) in `choose`; `GameActivity.resolveObjects` invia `sensorMind.contactOutcome(reserve-before)` soltanto dopo contatto fisico effettivo, e `chamber.foodTouchedByCore` invia l'analogo delta raro. `ExperienceCredit.consequence` fa l'aggiornamento dei pesi Q `value` nello storico sensoriale realmente visto con fattore di età 0.83; `resetEpisode` azzera solo lo storico transitorio. I nuovi contatori persistent `real_energy_credits_v098`, `real_damage_credits_v098`, `real_action_credits_v098` vengono azzerati solo da RESET WORLD. Non sono apportate modifiche al formato `wm1` delle 11.664 reti, né al `motor_skills_v098` (6 motori), alle 62/18/82 weights, a `em1/em2` o alla firma/package OTA. I precedenti contatori visuali restano residui storici e non vengono più prodotti. Diagnostica e paper `docs/EXPERIMENT_0098.md` correzione 0098-A.


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

### Release ZERO 0.9.5-ota.39 — 2026-10-10
APK firmata, versionCode 1039, 92.692 byte; SHA-256 `86caeb85b6eba94aa657d6f19ac7dff5dd0e4e8d2974bf71e8c6c12a3201c211`; CI firmata `38003676123` verde, file `updater/public/latest.json` aggiornato. Il test manuale sul Samsung Galaxy S21 resta da fare. Non attribuire retroattivamente le 111U di apertura della v0.9.4 alla nuova memoria.


## v0.9.6: memoria valenziata e riuso sensomotorio
Modulo `EpisodicMemory`: ring live MAX_STEPS/DEPTH=36, archivio MAX_EPISODES=24, 26 canali sensoriali/16 azioni e snapshot pesi controller (62, 18, 82). `consequence(effect, energyDelta, weights)` riceve l'effetto fisico generico più il delta di riserva provocato da una **risorsa effettivamente toccata**; la fame non crea memoria negativa. `suggested` e `bias` interrogano sia ricordi positivi (azione favorita/replay temporaneo se scelta) sia dannosi (penalità senza replay). Consolidamento aumenta conferme; eviction sceglie il più debole solo se il nuovo lo merita. Persistenza nella chiave `episodic_memory_v095` con codifica `em2`, lettura `em1` e `em2`, parsing atomico e cap lunghezza; gli altri formati di salvataggio non cambiano. Sorgenti `GameActivity` (tocchi + fatalità), `SensorimotorMind` (preferenza/avversione) e `BrainToolsActivity` (telemetria). Non inferire conoscenza semantica o efficacia generalizzata. Per piano test e limitazioni leggere `docs/EXPERIMENT_0096.md`.


## 0.9.7 — separazione netta di locomozione, memoria e varianti corporee
In `GameActivity`, il motore di `MotorBrain.Body.step` riceve SEMPRE `liveBody.weights(0,trainer.best)` (controller motorio accettato oppure candidato realmente in prova). `EpisodicMemory.weights(0,normal,true)` ritorna sempre `normal`, a tutela contro override da ricordi passati. Mano `ContactLearning` e membrana `Morphology` possono riusare snapshot dal ricordo soltanto quando esiste `surface.touchSense[k]>.20` su un punto, il richiamo sensoriale è stato effettivamente scelto e `!liveBody.testingCandidate()`. Se una riprova fisica è stata davvero applicata nell'intervallo, si abortisce la prova `OnlineBodyLearning` perché i parametri erano misti; il semplice richiamo di un'azione lascia invece proseguire tutti i trial.
`MotorAutonomyGuard` è un osservatore puro: 7s/finestra, progressi netti nucleo o variazioni di posizione degli ostacoli, 2 finestre senza effetto -> sospensione dei suggerimenti episodici positivi per 12s, senza direzioni/oracoli o cambiamento dei pesi. `SensorimotorMind.choose` continua a usare il modello a 26 sensi/16 azioni, curiosità e valori appresi; avversioni negative restano attive anche durante la sospensione dei suggerimenti positivi. Nuovi pref: `motor_recoveries_v097`, `motor_free_choices_v097`, `physical_replays_v097`. Chiavi storiche motore `best`, mano, morfologia e mondo NON migrate né sovrascritte da reset. Vedi `docs/EXPERIMENT_0097.md`.

## 0.9.8 — MotorSkillBank + SensoryValenceGuide
`MotorSkillBank` conserva max6 `float[62]` dopo azioni REALI con distanza NETTA ≥9U (`skills1` in `motor_skills_v098`). Quando `MotorAutonomyGuard` segnala stagnazione, può recuperare una configurazione precedente già osservata e diversa dall'attuale, con cooldown. `OnlineBodyLearning` impedisce che una mutazione motoria sostituisca il precedente controller se la traslazione nella finestra è <max(7U,0.80*NETTO baseline); manipolazione/membrana non seguono questa regola. `MotorAutonomyGuard` conta le ultime32 azioni e, soltanto quando fermo, penalizza l'azione ripetuta e aumenta esplorazione random. `SensoryValenceGuide` riceve SOLO 26 sensi (12 frequenze visive x settori), `UnknownWorld.Mind.estimated/contacts` appresi e energia. Senza contatti per firma bias=0. Non legge coordinate degli oggetti o leggi fisiche nascoste. Nuovi contatori `visual_guided_v098`, `visual_avoided_v098`; reset MOTOR e EXPLORATION aggiornati. Vedi `docs/EXPERIMENT_0098.md`.

## Architettura 0.9.9 — mano/membrana non sostituite da vecchi episodi
`GameActivity`: `lastAppliedMotor=liveBody.weights(0,trainer.best)`, `lastAppliedHand=liveBody.weights(1,handTrainer.best)`, `lastAppliedMembrane=liveBody.weights(2,shapeTrainer.best)`; **mai `episodic.weights` su attuatori**. Decisioni Q/episodi, 16 scelte e canale di adesione autonomo intatti. In `ScarcityLab.constrain(body,surface,dt,gripDrive)` il quarto argomento è soltanto telemetria dell'azione scelta, non modifica fisica del cancello; `solveGate` usa sempre `skin.gripCommand[i] > .28`, `skin.gripHold[i] >= .075s` e due lati opposti per forze tangenziali. Metriche frame `gateDriveFrames` (volontà+contatto), `gateCommandFrames` (attuazione sopra soglia), `gateHeldFrames` (tenuta). `GripSkillBank` serializzato `grip_skills_v099`/`grip1`: max5 coppie reali hand[18] + membrane[82] provenienti da tenuta al cancello, rank con tenuta, opposizione contemporanea e spostamento; restore prudente soltanto dopo molti frame di grip scelto dall'agente senza hold e fuori da trial, cooldown. Reset selettivo MEMBRANE azzera banca, non Q/em1/em2, motor skills, modello 11.664 pesi. Full analysis `docs/EXPERIMENT_0099.md`.

## ZERO 0.10.0 — two persistence layers and genuine world snapshots
1. Working autosave: original `SharedPreferences(zero_motorbrain_001)`, loaded by GameActivity. No key names for the old neural networks or episodic/motor/grip banks changed.
2. Independent named archives: `SharedPreferences(zero_save_slots_v100)`, entries `slot_v100_0..11`, and a single previous-personage recovery record, unaffected by all ResetDomains incl. ALL. Slots encode ALL preference types using `SaveProfileCodec` 8 MB bounded, deterministic version 1, SHA-256 integrity, and Base64 JSON metadata (name/date). Reader validates complete motor model and actual `physical_scene_v100` before installing.
3. Game scene: `GameSceneSnapshot` serializes real world Entities, 10 core masses, 96 surface masses, tactile/grip channel, doorY/velocity/hold history, room offset, consumed resource state; `MotionExploration.saveLife/loadLife` retains visited cells; GameActivity supplements episode/phase/time, life metrics, action and simulation speed/paused flag. `GameActivity.suspend` calls brain.save then saveScene; also periodic scene checkpoint. On normal restart or slot switch, `restoreScene` installs physical bodies. Legacy no-scene profile resets only transient world while preserving all learned weights.
4. Swap safety: before overwrite active working profile, SaveSlotStore validates snapshot, `protectCurrent()` synchronously commits the outgoing working state as recovery; then a single working `.edit().clear()` installs loaded map with `pending_profile_reload_v100`. GameActivity resume/load removes marker, reconstructs every controller and scene before another frame, preventing stale controller resurrection. New game uses identical path except it replaces working with empty prefs plus marker, never touches named slots.
5. UI: SaveSlotsActivity (portrait, not exported), four footer buttons [PARTITE, ANALISI, TEMPO, PROVE], brain-page shortcut, double confirm new person, confirm overwrite/delete/load, safe fallback if slot checksum broken. No new permissions. Full protocol `docs/EXPERIMENT_0100.md`.

## ZERO 0.10.1: separate causal-evidence channels
`SensorimotorMind.choose` adds `contactCredit.value(senses,a)` to existing generic action Q and curiosity/memory biases; `ExperienceCredit` `ec2` stores two separate 16*27 arrays: **energetic** weights on visual channels 14..25 conditioned on signature `object.signature` actually contacted + visually observed (6 past own actions), **mechanical** weights on proprioceptive/tactile/proximity channels 3..13 conditioned on real object displacement (5 past own actions). No hardcoded movement direction or object coordinate. General `wm1` state prediction remains; `lastReward` no longer mixes energy/distance effects already learned separately. `GameActivity` maintains `energySinceAction[3]` and passes to `episodic.consequenceSeparated(physicalEffect, energySinceAction,...)`; `em3` episode `kind` distinguishes legacy mixed, real energy-specific and physical effects, filters recall by observable source, preserves em1/em2 read. `outcome_credit_v101` saved/restored by original preferences, select reset WORLD only, automatically included in 12 SaveSlotStore immutable named snapshots. No source-specific planner or controlled causal A/B interventions. `docs/EXPERIMENT_0101.md` contains limits/tests.


## 0.10.3 — cronologia episodica rolling nel ciclo reale
`EpisodicMemory.action(stateBefore,actionBefore)` aggiunge ogni passo a `recent` limitato a `DEPTH=36`. In `GameActivity`, dopo ogni nuova esperienza di `SensorimotorMind.observe`, `consequenceSeparated(physicalEffect,energySinceAction,...)` verifica eventi senza eliminare `recent` nei tick neutri (nella 0.10.2 lo cancellava sempre). Al superamento della soglia `MIN_EFFECT=3` e con almeno 2 azioni già vissute, `storeEffect` crea/consolida `em3` separando eventi fisici ed energetici condizionati dalla sorgente osservata; `resetLife()` cancella il transient dopo morte, mentre `encode` persiste gli episodi in `episodic_memory_v095`, salvata nei vecchi 12 slot. I dati temporanei non sono serializzati attraverso la chiusura app, ma i ricordi permanenti sì. `consequence()` legacy, motori, sensori, salvataggi e planner immutati. Regressione testata decisione-per-decisione, vedi `docs/EXPERIMENT_0103.md`.

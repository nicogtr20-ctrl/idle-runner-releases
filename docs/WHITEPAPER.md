# OVERMIND: ZERO — Whitepaper di progetto

## 0.9.8-autonomia — da orientamento ingegnerizzato a credito temporale delle scelte
La versione firmata `0.9.8-ota.42` usava `SensoryValenceGuide`: somma di bonus lungo un settore geometrico che mostrava un segnale visuale associato all'energia. Non veniva obbligata una direzione, ma la formula comunque suggeriva un comportamento. Il feedback del ricercatore esige che ZERO possa scegliere autonomamente se ottenere una sfera anche quando conosce un beneficio e ha fame. Nella correzione `SensoryValenceGuide` è stato rimosso: nessuna moltiplicazione per il coseno della direzione rispetto a un oggetto è più addizionata ai Q. La nuova `ExperienceCredit` rinforza esclusivamente azioni *realmente eseguite*, in contesti sensoriali *realmente osservati*, a seguito di aumenti/diminuzioni di energia per CONTATTO FISICO; conserva le ultime 12 azioni con decadimento della responsabilità `0.83` per distanza temporale. Le scelte restano governate dalla rete Q appresa e dalle componenti di curiosità, ricordi e antistallo, con esplorazione stocastica. È temporal-difference/eligibility credit semplificato, non inferenza causale certa, AGI o libero arbitrio. Nessun bonus fisso blu/rosso. `docs/EXPERIMENT_0098.md` sezione CORREZIONE 0098-A definisce limiti, test, persistenza.


**Versione:** 0.9.6 (documento vivo) · **Data:** 10 ottobre 2026 · **Stato:** prototipo di ricerca Android

## Sintesi
OVERMIND: ZERO è un esperimento di *artificial life* trasformato gradualmente in gioco mobile 2D. Il protagonista è una piccola massa scura dall'aspetto di ferrofluido/organismo simbionte. Il giocatore **non** controlla ZERO né le parla; è un osservatore invisibile che può modificare la simulazione, finanziare interventi con esperienza guadagnata e osservare come l'entità si comporta ed evolve.

La vera promessa è l'apprendimento **verificabile**: ZERO non deve recitare un'intelligenza con animazioni prefabbricate. Deve provare contrazioni e interazioni, rilevare gli effetti e aggiornare una propria rappresentazione del mondo.

## Visione a lungo termine
1. ZERO nasce senza memoria episodica, conoscenza del mondo o sequenze motorie pre-addestrate.
2. Possiede sensori elementari, un corpo deformabile, esigenze minime di energia/integrità e capacità di contrarre/espandere materia.
3. Impara relazioni causa-effetto: deformazione → spostamento, contatto → energia/danno, contatto multiplo → movimento di oggetti.
4. Apprende modalità per afferrare/spostare oggetti, aggirare ostacoli, sfruttare risorse e distribuire il proprio corpo nella mappa.
5. Può sviluppare preferenze, strategie locali, esplorazione e persistenza della memoria senza ricevere una lista prestabilita di obiettivi narrativi.
6. Il giocatore guadagna XP da scoperte e sviluppo e li spende su elementi del mondo, non su comandi diretti a ZERO.
7. ZERO non sa dell'esistenza del giocatore; variazioni anomale del mondo potrebbero condurla a elaborare ipotesi causali *solo se supportate dall'architettura futura*.

## Principi di design
- **Autonomia simulata, non teatro:** dichiarare quali decisioni vengono apprese e quali vincoli sono fissi.
- **Corpo organico realmente deformabile:** massa nera e pseudopodi procedurali, no braccia predefinite o sprite animati.
- **Nessun obbligo di conversazione:** niente chat o notifiche antropomorfiche attribuite alla creatura come suoi pensieri.
- **Mondo parzialmente ignoto:** le proprietà degli oggetti sono nel motore della simulazione e si scoprono con esperienze.
- **Esperienza come due sistemi:** memoria/apprendimento di ZERO persistente, XP del giocatore spendibili e indipendenti.
- **Approccio offline-first:** no API a pagamento, piccolo cervello allenabile sul telefono, priorità a fluidità e stabilità.
- **Misure empiriche:** confronti virgin vs trained e su direzioni/mappe non usate in addestramento.
- **Nessuna falsa pretesa:** non è cosciente, non è AGI, non possiede ragionamento linguistico autonomo.

## Risultati effettivamente osservati
- ZERO-001 ha addestrato da zero un controllore neurale da 62 parametri su corpo simulato con 10 punti di superficie.
- Sul Galaxy S21 l'utente ha osservato spontanee contrazioni e spostamento verso uno stimolo, anziché una camminata animata.
- Dopo 2.467 tentativi, confronto su direzioni nuove: controllo vergine -3,6 vs addestrato +179,7 (screenshot utente).
- ZERO-002, 4.772 tentativi: baseline +2,9 vs addestrata +204,4, 49 contatti ed effetti incontrati 3/3 (screenshot utente).
- Ciò dimostra progresso motorio nel **simulatore specifico**, non causalità generale o intelligenza general-purpose.
- Il sistema di oggetti attuale usa appena tre firme e apprendimento associativo: il numero 3/3 indica tipi incontrati, **non** la comprensione del mondo.

## Limiti tecnici correnti
- Le azioni del controllore sono contrazione radiale e aderenza di punti del corpo, in un simulatore semplificato.
- Il controllore motorio riceve ancora una direzione di stimolo. Non ha scoperto spontaneamente l'obiettivo di cercarlo.
- L'ambiente ha leggi fisiche predisposte; il classificatore degli effetti è molto semplice.
- La grafica è stilizzata, non una simulazione visivamente fotorealistica di vero ferrofluido.
- Il processo di addestramento è neuroevoluzione di pesi con fitness misurato, non un LLM e non un modello del mondo completo.
- L'algoritmo va stressato su mappe fisicamente diverse: alcune strategie possono sfruttare particolarità del simulatore.

## Cosa rende il gioco interessante
Osservare ZERO imparare a usare il proprio corpo, scoprire elementi del mondo, sopravvivere ed espandersi. Il giocatore modifica il mondo spendendo XP ottenuti da esperienze della simulazione, senza impartire ordini alla creatura e senza sapere in anticipo come reagirà.

## Milestone
- M0: controller motorio vergine e apprendimento verificato (raggiunta a livello di prototipo).
- M1: conseguenze di oggetti ignoti e memoria associativa (primo prototipo disponibile).
- M2: oggetti spostati da forze fisiche del corpo, apprendimento di contatto/manipolazione (codice e test CI nella OTA v0.4.0; verifica sul Galaxy S21 ancora pendente).
- M3: modello predittivo dello spazio e corpo con pseudopodi dinamici.
- M4: vera simulazione persistente con ciclo energetico, espansione e nodi.
- M5: gioco completo con UX, grafica ferrofluid, interventi divini e XP.

## Come aggiornare questo documento
Quando cambiano la visione, gli assunti sull'IA, i rischi o i traguardi, modificare questa pagina; aggiungere **ogni release** a `docs/CHANGELOG.md`; aggiornare il passaggio di consegne `docs/HANDOFF.md`.

## Esperimento ZERO-003 (rilascio OTA v0.4.0-ota.15)
La v0.4 implementa collisioni fisiche fra membrana deformabile e dischi mobili e un ulteriore controllore di manipolazione a 18 pesi, inizializzato a zero e addestrato mediante neuroevoluzione. L'interazione non è più una spinta programmata sul contatto: l'oggetto reagisce soltanto alla dinamica corporea del simulatore. In CI il fitness in training è migliorato da 22,301 a 101,837 su 70 tentativi; fitness heldout 50,940. I benchmark sui nuovi parametri non costituiscono prova di intelligenza generale. L'APK firmata è su Vercel; la validazione sul telefono deve ancora essere svolta.

## ZERO-005 — Decisioni apprese, non più selezione fissa
La nuova rete decisionale riceve segnali elementari, distanza, riserva energetica e quante esperienze ha effettuato. Parte da 91 pesi casuali e impara tramite aggiornamenti numerici sui risultati reali dei contatti. Può scegliere azioni di esplorazione anche se la stima corrente è bassa. Non possiede conoscenze linguistiche e non genera scopi astratti autonomamente: i bisogni biologici simulati e l'interfaccia sensoriale restano progettati. L'app mantiene la direzione del bersaglio calcolata dal motore e i vecchi pesi motori già addestrati. CI e firma Android superate; verifica sul S21 pendente.

## ZERO-006 — Autoespansione controllata e trasparenza dei parametri
La rete decisionale non riceve semplicemente più parametri in modo automatico e prestabilito: quando gli errori non diminuiscono può proporre una nuova unità. La nuova connessione verso l'uscita nasce a zero per non modificare la funzione appresa, poi la nuova unità si allena su replay di esperienze e viene tenuta solo se migliora su esempi esclusi dal suo addestramento. Il numero massimo di neuroni è limitato sperimentalmente per prestazioni e qualità. La schermata Cerebro/Abilità espone i valori reali di ogni peso, il loro cambiamento dalla nascita e i contatori di crescita, senza assegnare livelli artificiali alla coscienza. È un meccanismo di progettazione/adattamento della rete, non prova di capacità di ragionamento generale.

## ZERO-007 — evoluzione del corpo, non solo del cervello
Per esplorare l'idea di una massa artificiale che scopre autonomamente come modificarsi, ZERO conserva gli attuatori delle versioni precedenti e aggiunge una superficie materiale composta da 32 punti interconnessi. Ogni punto ha la possibilità fisica di allungarsi/ritirarsi e inclinarsi in modo continuo, ma non esistono arti preset. La decisione locale dipende da una nuova rete di 74 pesi condivisa fra tutti i punti, inizializzata senza allungamenti e selezionata mediante esperimenti su un simulatore fisico. Fitness e sensori sono ancora scelte progettuali. In CI la fitness morfologica ha mostrato 23.471→48.139 in 16 tentativi (9 accettati), non costituisce prova che sul telefono emergano arti funzionali: la v0.7 deve ancora essere osservata dall'utente.

## ZERO-008 — Esperimento con motivazione energetica ed ostacoli
Per ridurre la ricompensa ripetitiva dei pallini, il nutrimento è ora una risorsa consumabile che scompare alla prima raccolta e si rigenera sporadicamente in condizioni di fame. Una stanza con blocco mobile richiede per passare un contatto su due lati opposti della porta, ottenuto da punti reali della membrana deformabile, non un'azione precodificata «due braccia». Il test differenzia **toccare un blocco**, **stringerlo su due lati**, **spostarlo quanto basta ad aprire l'accesso**, e infine **raggiungere il nutrimento con il nucleo**. La rete morfologica viene testata su episodi fisici con ricompense per i risultati misurati, ma non ha ancora dimostrato di acquisire la capacità sul telefono. La direzione del bersaglio resta un'indicazione esterna; si tratta di un ambiente di ricerca su locomozione e apprendimento, non di intelligenza generale emergente.

## ZERO-008.3 — Apprendere l'azione, non solo cercare l'oggetto
L'utente ha rilevato che non basta avvolgere il cancello: questo potrebbe muoversi per puro contatto casuale. La nuova simulazione distingue sensazione tattile e comando di adesione. Ciascun punto del corpo può, tramite una uscita neurale inizialmente inattiva, tentare di aderire al cancello. Solo la sincronizzazione di due prese fisicamente separate e una forza tangenziale può trascinarlo. La struttura motoria e i pesi esistenti rimangono validi. Questo è un esperimento di scoperta di una nuova affordance motoria, non la dimostrazione che il sistema possieda comprensione umana. Il successo funzionale resta da verificare su Galaxy S21.

## ZERO-009 — Prime relazioni fra azione e percezione
ZERO non viene più indirizzato verso le coordinate esatte di un oggetto: un agente con sensori di prossimità, visione cromatica, energia corporea, segnali tattili e velocità sceglie da solo azioni motorie e adesive. Un predittore impara quale cambiamento del quadro sensoriale segue ciascun comando; un modello dei valori esperiti aggiorna la probabilità di ripetere certe azioni. Le regole di apprendimento restano scelte dagli sviluppatori (stato corporeo, curiosità e aggiornamento di stima). Non sono codificate azioni «sposta la porta» o «continua se si è aperta»; non significa però che ZERO possieda già un concetto astratto di porta, futuro o volontà.

## ZERO-009.1 — Il valore del test, non dei soli contatori
Per separare l'apprendimento misurabile dalla semplice collezione di tentativi, un nuovo protocollo di valutazione trattiene una transizione sensoriale su cinque dall'aggiornamento dei pesi. Su questi eventi misura errore predittivo e lo confronta con un'ipotesi ingenua di nessun cambiamento, mantenendo uno storico visuale. Le ricompense ottenute da comandi scelti dalla policy e comandi esplorativi sono riportate con cautela perché gli stati vissuti differiscono e il confronto non stima da solo la causalità. L'agente resta online learning durante la vita; i reset selettivi permettono esperimenti successivi su una sola capacità senza cancellare gli altri modelli. Nessuna conclusione sull'effettiva autonomia del cancello è giustificata finché non vi saranno test A/B e sessioni su dispositivo.

## ZERO-009.2 — apprendere da movimenti effettivamente vissuti
A differenza dei rollout precedenti, ogni esperimento sulla locomozione, manipolazione e presa ha luogo durante la vita di ZERO, nello stesso habitat del giocatore. Una mutazione di pochi pesi sostituisce temporaneamente i pesi salvati e può essere accettata dopo una finestra di esperienza, se il risultato supera quello precedente in un contesto approssimativamente simile. Il metodo conserva l'autonomia dell'esperimento ed evita di fornire implicitamente coordinate esatte del premio, ma NON garantisce causalità: il mondo può mutare fra le finestre e la selezione può sovraadattarsi. La risorsa speciale in stanza produce energia attraverso una nuova legge fisica, osservabile soltanto via conseguenze e sensori spettroscopici, senza un'azione programmata per la porta. Necessaria convalida in-game prima di parlare di comportamento emergente.

## ZERO-009.4 — valutazione dei movimenti utili
La mera distanza integrata non distingue esplorazione da giri locali. ZERO ora sperimenta esattamente come prima, ma il criterio di accettazione misura la distanza netta dal punto iniziale della finestra, novità spaziale fisicamente verificata, energia e interazioni tattili reali. Le spire circolari non sono proibite: un giro capace di muovere oggetti o raccogliere una risorsa può essere utile. Il comportamento può restare inefficiente: le finestre consecutive differiscono nello stato iniziale, gli incentivi sono progettati e un incremento di punteggio non prova pianificazione o generalizzazione.


## Ricordi contestuali e credito temporale nella versione 0.9.5
La v0.9.4 separava locomozione netta da movimenti ciclici, ma non conservava il percorso sensorimotorio che portava a modifiche importanti nell'ambiente. Il test reale del 10 ottobre 2026 registra fino a 111 unità di traslazione del cancello con 268 prese, senza ingresso o riapplicazione sistematica dopo la morte. È un'osservazione promettente **ma non dimostra né comprensione né porta interamente libera** (soglia geometrica >115U).
La nuova ipotesi verificabile: memorizzare **solo dopo l'azione** le sequenze realmente vissute che hanno prodotto un effetto fisico e rendere questi esempi disponibili alle azioni future sotto contesto sensoriale simile. Lo snapshot include 26 ingressi sensoriali, una delle 16 azioni e lo stato dei tre controllori motori (62, 18, 82 parametri). Non include coordinate premiate, etichette “cancello aperto” o un piano per raccogliere l'energia. Le memorie hanno capacità limitata (18 eventi, 9 azioni per evento) per calcolo/archiviazione.
Un recupero avviene solo sotto sufficiente somiglianza sensoriale; un bias +0,72 si somma ai valori della politica già appresa, che rimane stocastica. Se l'azione suggerita viene davvero selezionata, possono essere utilizzati temporaneamente i pesi memorizzati. Un esperimento che ripete azioni senza nuovi effetti entra in cooldown. La retroattribuzione di valore è ancora euristica e correlazionale, non causalità dimostrata: distinguere «ricordato», «riprovato» ed «effetto di nuovo osservato». I 11.664 pesi di predizione non svolgono ancora pianificazione generativa multistep.
**Criterio di successo ancora aperto:** misurare che dopo morte/nuova stanza ZERO riconosca uno stato pertinente, provi realmente una configurazione ricordata, generi di nuovo uno spostamento e infine raccolga la risorsa rara oltre il cancello, più spesso rispetto a un controllo a pari condizioni senza memoria. Vedi `docs/EXPERIMENT_0095.md` per protocollo riproducibile e confondenti.

### Release ZERO 0.9.5-ota.39 — 2026-10-10
APK firmata, versionCode 1039, 92.692 byte; SHA-256 `86caeb85b6eba94aa657d6f19ac7dff5dd0e4e8d2974bf71e8c6c12a3201c211`; CI firmata `38003676123` verde, file `updater/public/latest.json` aggiornato. Il test manuale sul Samsung Galaxy S21 resta da fare. Non attribuire retroattivamente le 111U di apertura della v0.9.4 alla nuova memoria.


## Ipotesi di apprendimento autobiografico con valenza — ZERO v0.9.6
Le emozioni umane e la comprensione causale **non** sono implementate. La creatura costruisce una traccia neurale e un archivio di episodi (fino a 36 azioni su 24 eventi) conservando azioni, 26 sensazioni e parametri motorii. Solo conseguenze ambientali realmente misurate possono attribuire un segno favorevole o dannoso: contatto con risorsa produce delta energia; uno spostamento fisico verificato può rinforzare. Non è programmata alcuna categoria semantica del colore. Un ricordo positivo corrispondente può influenzare l'azione e il riuso temporaneo dei controllori; un negativo può ridurre la preferenza per l'azione che ha preceduto l'effetto nocivo. Ricordi confermati vengono consolidati invece di essere eliminati per anzianità. Il risultato atteso (preferire risorse, evitare pericoli, ricordare una tecnica) è **ipotesi da verificare**, non capacità dimostrata. Limiti: matching percettivo ambiguo, segmentazione approssimata, episodi oltre 36 step troncati, premio euristico, nessun planner multistep verificato. Protocollo e risultati: `docs/EXPERIMENT_0096.md`.


## 0.9.7 — rischio interferenza fra memoria episodica e competenza procedurale
La memoria episodica 0.9.5/0.9.6 conserva sequenze e snapshot dei controllori al momento di un effetto. Per un corpo adattivo, il riutilizzo indiscriminato di vecchi pesi neuromotori può introdurre **regressione comportamentale**: lo stato sensoriale simile non garantisce una postura o una dinamica interna identica. La v0.9.7 separa la base motoria conservata dall'autobiografia: le azioni possono essere suggerite in base ai ricordi, ma il controllore locomotorio (62 parametri) non è mai soppiantato; i vecchi pesi per presa/membrana possono essere verificati temporaneamente *solo con contatto fisico reale* e senza candidatə di mutazione corporea. Il criterio di recupero misura lo spostamento netto del nucleo (su 2 finestre di ~7 s) e la mobilitazione reale del mondo, quindi sospende brevemente l'influenza positiva della memoria soltanto in caso di mancanza di progressi, senza fornire una direzione o una soluzione della stanza. È un meccanismo di protezione/diagnostica, NON una prova che abbia già imparato ad accelerare o scegliere il miglior controller globale. Resta il limite delle prove evolutive in finestre successive e non in mondi fisici identici. Da verificare in un A/B su dispositivo. Protocollo: `docs/EXPERIMENT_0097.md`.

## ZERO 0.9.8 — conservazione procedurale e valore percettivo appreso
La selezione motoria da una sola finestra successiva a un'altra può scartare motori realmente efficaci: la v0.9.8 conserva un archivio di sei configurazioni già osservate con traslazione netta significativa; la mutazione deve conservare almeno 80% del NETTO precedente (e almeno 7U) per essere accettata. Durante stagnazione il cervello riduce il valore delle azioni ripetitive senza assegnare un vettore predefinito. I valori energetici positivi/negativi appresi attraverso il tocco di firme visive sono ora proiettati sui quattro settori percepiti da ciascuno dei tre canali; la fame ne modula il peso. Nessuna categoria cromatica o coordinata del target è fornita innatamente. Sono ancora euristiche correlate, da verificare empiricamente sullo stesso stato di mondo. Documento scientifico `docs/EXPERIMENT_0098.md`.

## 0.9.9 — testare l'interferenza fra memoria episodica e competenze procedurali
Nel confronto v0.9.4 (git `9b0e716cab4`) e v0.9.5 (`e5ef6752072`), la fisica cancello/membrana è invariata, mentre la nuova memoria episodica poteva sovrascrivere *tutti* i controller fisici con pesi storici e interrompere esperimenti in corso, e influenzare le azioni. La 0.9.7 proteggeva locomozione ma non la mano/membrana durante contatto; la scomparsa simultanea di movimento e presa riferita dall'utente è coerente con questa regressione, pur non provandone la causa esclusiva. 0.9.9 fa sì che motore, mano, membrana ricevano solo pesi live o candidati testati; la memoria conserva la propria influenza sulle decisioni. GripSkillBank conserva skill fisici solo quando c'è vera adesione sostenuta, preservando max5 controllori 18+82 parametri e consentendo recupero da tentativo volontario ripetutamente fallito, non imponendo obiettivi. Nuova telemetria multi-stadio distingue scelta dell'adesione, attuazione neurale, tenuta fisica e spostamento reale. Un archivio inizialmente vuoto NON può recuperare abilità precedenti non registrate. Test S21 pendente. Documento tecnico: `docs/EXPERIMENT_0099.md`.

## 0.10.0 — Persistent individuals and controlled reinitialization
Overmind ZERO now implements a game-style MULTI-INDIVIDUAL experimental workflow. A manual save slot is a checksum-protected snapshot of the complete learned controller/sensorimotor, episodic and procedural weight sets plus the current physical world. There are twelve independently named slot snapshots, preserved outside the working preference file so a trial with an entirely virgin controller no longer destroys a prior organism's history. Named load atomically changes the working profile after persisting a one-depth recovery copy; GameActivity recreates the old 10-mass body and 96-point perimeter, room gate physics, live world objects, energy and statistics, and scene timer, avoiding falsely treating an autosave as a brand-new room. `MotionExploration` restores the current life's visited-cell set so loading doesn't create unexplained novelty rewards. Pseudorandom generator state and transient unaccepted mutation windows are not reproduced bit-identically, so two reloaded saves are not rigorously paired counterfactual experiments; controlled comparison will require fixed seeds and full RNG state later. This is not a forked multiagent simulator: only one ZERO runs at a time, and named saves are frozen until manually overwritten. Mechanism, limitations and QA in `docs/EXPERIMENT_0100.md`.

## 0.10.1: evidenza di provenienza, non causalità simbolica
The source-conditioned trace learner has two independently learned action-value modalities: visually witnessed frequency of real energy contact and tactile/proximity correlation with actual external-object displacement. The previously shared global reward failed to distinguish a physical gate success and a simultaneously touched red danger, and old `ExperienceCredit` retroactively penalized up to 12 unrelated steps. `ExperienceCredit` ec2 now limits observed energetic signature windows and tracks ambiguity of unseen sources; physical motion is a separate nonnegative learned effect. `EpisodicMemory` em3 tags each episode's provenance `kind=1/source=observed frequency`, `kind=2 physical`, historical legacy `kind=0`, preserving em1/em2. Selective harmful recall only applies where harmful source is sensibly present. No built-in gate reward for completion nor predetermined food-seeking heading; learning of useful plans or multi-step counterfactual causal prediction not demonstrated. Reference `docs/EXPERIMENT_0101.md`. Signed APK `0.10.1-ota.48` validated automatically, on-phone tests pending.


## ZERO 0.10.3 — prerequisito tecnico alla formazione autobiografica
Osservazione S21 v0.10.2: 32.573 percezioni e 171 dischi spostati, ma 0 memorie episodiche. Non era un deficit cognitivo emergente: il software cancellava il buffer di tracce a ogni controllo delle conseguenze, impedendo il minimo di 2 azioni reali richieste per consolidare un evento. Nella 0.10.3 la lista temporanea conserva gli ultimi 36 passi e consente di associare un effetto energetico o meccanico verificato a una finestra di azioni realmente vissuta, senza introduzione di conoscenza degli oggetti. La morte chiude la finestra senza cancellare gli episodi. La registrazione di episodi è verificata da test JVM in condizioni simili al ciclo di gioco; il recupero e l'uso efficace da parte di ZERO in ambiente non sono ancora stati osservati sul Galaxy S21. Paper completo `docs/EXPERIMENT_0103.md`.


## ZERO 0.10.4 — misurare senza attribuire automaticamente causalità
Nella 0.10.4 introduciamo una misurazione puramente osservazionale della storia di vita, separata da fitness/reward dei cervelli: durata in tempo simulato (anche se il rendering è x5/x20), risorse realmente raccolte, danni da contatto (non fame passiva), manipolazione, esplorazione netta, porta, ricordi e sopravvivenza. Confrontiamo ogni vita **completata** con le precedenti 10 e tracciamo 50 campioni, senza assegnare un punteggio unico o premiare direttamente l'organismo per ottimizzare i grafici. Vita già in corso prima dell'upgrade marcata parziale, esclusa, per non falsare la baseline. I dati rendono possibili ipotesi di miglioramento, NON costituiscono una prova di progresso cognitivo senza controllo della scena, del seed e della difficoltà. `docs/EXPERIMENT_0104.md`.

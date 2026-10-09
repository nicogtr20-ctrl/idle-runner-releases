# OVERMIND: ZERO — Whitepaper di progetto

**Versione:** 0.2 (documento vivo) · **Data:** 9 ottobre 2026 · **Stato:** prototipo di ricerca Android

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

<!-- Current authoritative release status; older sections below are historical. -->
# Verified release — OTA170 / Android1170, runtime ZERO171 — 16 October 2026

The user explicitly authorized source and signed APK OTA publication. **Published and downloaded from BOTH Vercel and GitHub: `0.11.6-ota.170`, versionCode `1170`, package `com.overmind.zero.ota`.** The runtime correction revision **171** is not the OTA number. Install through **AGGIORNA**, over the existing app; do not uninstall or reset saved brains/slots.

APK: 497912 bytes, SHA256 `5fd2bb087e55fe7eae4d5b74e5ce7b7c31106bf71eb2077f6c1ee4ed915ba02e`. Original stable certificate SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583` matched; APK v2 signature AND signed content digest independently verified on both public downloads. Both manifests and exact bytes match, HTTP200. Main Idle Runner app/build/settings remain unchanged.

[Release](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.170) · [successful signed publisher](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38926908265) · [integrated source review #83](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/83).

Read [ZERO171 integrated correction and release evidence](ZERO_171_INTEGRATED_SENSORIMOTOR_CORRECTIONS.md) for all audit findings, integrated fixes, optional persistence keys, verification and behavioral limits. 96 JVM entrypoints, seven staging integration gates, 34 publisher source gates, Gradle debug/release builds and original signing identity passed. One living recurrent cortex and 724 motor weights; no authored gait, food coordinate, forced confidence or brain reset. **Improved autonomous locomotion/food seeking on the Galaxy S21 is still unproven.** Preserve that distinction in future work. Earlier publication holds and version/status claims below are superseded by this verified release.

## OTA162 — CANDIDATA: MOTOR COORDINATION, 15 ottobre 2026

**Stato**: PR in preparazione, QA obbligatoria e successiva firma OTA NON ANCORA PUBBLICATA. Fino alla pubblicazione, OTA161 resta la release ufficiale. Report reale OTA161 `zero-telemetry/reports/20261015-202651-1411664defd37`: 81% di cambi di scelta tra decisioni adiacenti nell'ultima vita, 31% esplorazione, blackbox ~734U di percorso campionato ma ~63U di spostamento risultante; nuovi 320 pesi con media assoluta ~0.0000455, ~473 volte inferiore agli 84 originali. **Lentezza e antagonismo sono sospetti fondati, non ancora diagnosi certa di forze interne dai dati raccolti.**

**Modifiche branch `fix/zero162-sensorimotor-coordination`**: `SymbioteCoordinationV162` registra veri frame del baricentro dopo fisica, box e collisioni, misura l'annullamento vettoriale NOMINALE dei comandi sui 16 settori e la realizzazione della direzione scelta solo dall'attivo cervello 2.2. Apprendimento locale di 320 pesi su segnale di controllo sensorimotorio misurato: solo azioni motorie 0-7, grip 8-15 esclusi, heldout non aggiorna nulla, niente direzione al cibo, nessun reward positivo di distanza in core. Limiti al gradiente, esclusione grandi salti anomali, modelli `smh1` e `cl161` preservati. Nuovo persistente `symbiote_coordination_v162` con validazione atomica, corretto reset membrana e autosave. UI **Corpo → Corteccia muscolare → Coordinazione reale** distingue opposizione nominale da efficacia, non spaccia parametri per neuroni.

**Test**: `Zero162CoordinationRegression` con vera fisica `SymbioteMatterV1`, 640 step, movimento misurato, plasticità regionale e nessun credito alle prese/holdout/teleport, preserva ricordi e parametri vecchi; alla prima QA [run 38804260990](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38804260990) JVM+Android debug SUCCESS: netto ~21,26 U, indice opposizione NOMINALE ~0,786. Questo NON dimostra ancora miglioramento dell'azione autonoma sul telefono: misurare con nuovi report, preferibilmente 25+25 vite, NETTO/VIA, blu, episodi e metrica coordinazione.

**Riferimento completo**: `docs/ZERO_OTA162_MOTOR_COORDINATION_AUDIT.md`. **Non disinstallare né azzerare ZERO**. OTA161 ufficiale firmata, OTA162 disponibile solo dopo publisher firmato e verifica pubblica SHA256.

## ZERO OTA161 — corpo fluido e plasticità regionale (CANDIDATO, NON FIRMATO)

**Stato al 15 ottobre 2026:** ramo `fix/zero161-symbiote-physics-capability`, [PR #71](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/71). OTA160 **è stata firmata e pubblicata** (1160): manifest `updater/public/latest.json`. OTA161 è distinta, e NON sarà disponibile con AGGIORNA finché il publisher firmato non termina con successo.
- Verifica fisica su VERO `SymbioteMatterV1`: corpo a riposo quasi fermo, tre appendici realmente raggiungibili, retrazione indipendente, elasticità/topologia e locomozione misurate sul medesimo scenario; `Zero161PhysicalCapabilityRegression` e `Zero161OverstretchRegression` sono prove di laboratorio, **non andature preprogrammate**.
- Cortex 2.2 esistente preservato; 84 pesi `smh1` invariati. Aggiunti 320 **parametri allenabili** specifici per settore, inizializzati a zero, con base spaziale più espressiva e plasticità dalle vere conseguenze. Questi pesi **non sono 320 neuroni** e non modificano la neurogenesi del nucleo. Memoria separata `symbiote_cortex_local_v161`, salvataggi, 12 slot e firma preservati.
- UI Corpo/Parametri mostra read-only i 320 pesi regionali e i vecchi 84; nessuna ricompensa di spostamento imposta al cervello né bersaglio cibo conoscibile dall'attuatore.
- Prove pre-release: GitHub Actions [38777660956](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38777660956) **SUCCESS** JVM/Android Debug. Misure: in scenario test, 3 protrusioni, retrazione ~-9,99U, netto crawling 21,26U su 16s vs 13,73U originali; nessuna prova ancora della locomozione AUTONOMA sul Galaxy S21.
- Firma, publisher e manifest reale sono i gate successivi. **Non disinstallare l'app né azzerare il personaggio.** Paper tecnico: [ZERO_OTA161_BODY_PHYSICS_AUDIT](ZERO_OTA161_BODY_PHYSICS_AUDIT.md).

## CANDIDATO ZERO 2.2 OTA160 — 15 ottobre 2026 (NON PUBBLICATO)
Correzione sperimentale della durata motoria del simbionte, senza movimenti prefabbricati: il nucleo 2.2 rimane l'unico arbitro, un nuovo modello `MotorDurationV22` impara se mantenere per 1, 2 o 4 intervalli una sua azione, con interruzione immediata per danni, riserva critica, esplorazione o scelte neurali chiaramente migliori. Persistenza opzionale `motor_duration_v160`, stessa app/salvataggi, valori reali visibili in Corpo/Evoluzione. Baseline Android: report `20261015-152553-130aa19184fe2`, 95.3–96.6% di cambi d'azione su due vite, ultimi 25 episodi con calo della locomozione e aumento dei contatti rossi. QA preliminare del ramo `fix/zero160-learned-motor-duration` **non eseguita**: Actions run `38765532027` fallito prima di qualsiasi step (infrastruttura). Nessuna APK160 firmata o prestazione post-correzione rivendicata. Paper completo: [ZERO_OTA160_LEARNED_DURATION_FORENSICS](ZERO_OTA160_LEARNED_DURATION_FORENSICS.md). Non unire al ramo che pubblica l'APK finché QA e build Android non sono verdi.

## Evoluzione ZERO 2.0 — memoria degli oggetti e incertezza (13 ottobre 2026)

Il nuovo livello `ObjectExperienceV20` unisce **i 16 raggi realmente percepiti e le firme energetiche visibili** in ipotesi percettive non etichettate. Memorizza separatamente spostamenti meccanici, fonti energetiche effettivamente toccate e possibili legami; un danno casuale non elimina automaticamente la competenza nello spostare un ostacolo. La confidenza predittiva è verificata su intervalli esclusi dall'apprendimento prima che un consiglio possa influenzare debolmente la decisione.

Il sistema riutilizza il deposito fisico HAND/MEMBRANA per archiviare prese bilaterali effettivamente riuscite su casse e cancello; eventuale recupero richiede azioni di adesione volontarie e fallimenti ripetuti. Le memorie sono salvate con il personaggio, e non passano da un profilo all'altro. **Limitazione:** categorie di scena non sono identità individuali persistenti, correlazione non è causazione, e non si dichiara ancora raggiunto un ragionamento probabilistico o causale generale. Scopo e limiti: [ZERO20_OBJECT_CAUSAL_MEMORY](ZERO20_OBJECT_CAUSAL_MEMORY.md).

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


## Ricordi contestuali e credito temporale nella versione 0.9.5
La v0.9.4 separava locomozione netta da movimenti ciclici, ma non conservava il percorso sensorimotorio che portava a modifiche importanti nell'ambiente. Il test reale del 10 ottobre 2026 registra fino a 111 unità di traslazione del cancello con 268 prese, senza ingresso o riapplicazione sistematica dopo la morte. È un'osservazione promettente **ma non dimostra né comprensione né porta interamente libera** (soglia geometrica >115U).
La nuova ipotesi verificabile: memorizzare **solo dopo l'azione** le sequenze realmente vissute che hanno prodotto un effetto fisico e rendere questi esempi disponibili alle azioni future sotto contesto sensoriale simile. Lo snapshot include 26 ingressi sensoriali, una delle 16 azioni e lo stato dei tre controllori motori (62, 18, 82 parametri). Non include coordinate premiate, etichette “cancello aperto” o un piano per raccogliere l'energia. Le memorie hanno capacità limitata (18 eventi, 9 azioni per evento) per calcolo/archiviazione.
Un recupero avviene solo sotto sufficiente somiglianza sensoriale; un bias +0,72 si somma ai valori della politica già appresa, che rimane stocastica. Se l'azione suggerita viene davvero selezionata, possono essere utilizzati temporaneamente i pesi memorizzati. Un esperimento che ripete azioni senza nuovi effetti entra in cooldown. La retroattribuzione di valore è ancora euristica e correlazionale, non causalità dimostrata: distinguere «ricordato», «riprovato» ed «effetto di nuovo osservato». I 11.664 pesi di predizione non svolgono ancora pianificazione generativa multistep.
**Criterio di successo ancora aperto:** misurare che dopo morte/nuova stanza ZERO riconosca uno stato pertinente, provi realmente una configurazione ricordata, generi di nuovo uno spostamento e infine raccolga la risorsa rara oltre il cancello, più spesso rispetto a un controllo a pari condizioni senza memoria. Vedi `docs/EXPERIMENT_0095.md` per protocollo riproducibile e confondenti.

## ZERO-010.5 — distinguere progresso recente e storia totale
La telemetria non deve diventare una nuova funzione obiettivo. La versione 0.10.5 tratta quindi le statistiche come **strumento esterno di misura**, senza collegarle ai pesi o alle ricompense di ZERO. La singola vita è troppo rumorosa per indicare apprendimento: la capacità attuale viene descritta con una finestra mobile di 25 vite e il trend con due blocchi disgiunti di 25 vite ciascuno. Il confronto si attiva solo dopo 50 vite complete.

Separatamente, un accumulatore permanente conserva media e record di tutte le vite misurate anche quando le singole osservazioni più vecchie escono dal grafico a 50 punti. Questo evita l'errore opposto: usare soltanto la media totale, che dopo molte vite diventerebbe troppo lenta per mostrare un miglioramento recente. Un indice sintetico di “tendenza evolutiva” combina cambi relativi osservati con uguale peso, ma è esplicitamente diagnostico e invisibile all'agente.

Per i ZERO nati prima dell'introduzione della telemetria completa non si ricostruiscono dati inesistenti. La migrazione conserva ciò che è realmente disponibile e marca lo storico come incompleto. Queste misure descrivono correlazioni nel comportamento, non dimostrano da sole causalità, comprensione o generalizzazione.

## ZERO-010.6 — stato interno temporale, non un'etichetta del mondo
Un agente puramente reattivo può vedere due volte la stessa configurazione sensoriale senza sapere se ci è appena arrivato, se la sta mantenendo o se un oggetto si sta già muovendo in seguito alle sue azioni. La 0.10.6 introduce quindi una memoria di lavoro ricorrente che integra soltanto la sequenza recente di sensazioni e azioni.

L'implementazione usa un piccolo reservoir ricorrente deterministico di 16 unità privo di semantica preprogrammata. Sono appresi 7.344 parametri di lettura: uno strato predice il successivo cambiamento sensoriale condizionato dal contesto; un altro apprende valore d'azione nello stesso contesto. Gli effetti energetici e gli spostamenti esterni realmente misurati possono essere retroattribuiti a una traccia di otto decisioni recenti. Non esistono ingressi “porta”, “sta aprendosi”, “continua a tirare” o coordinate del premio.

Per evitare che una rappresentazione temporale casuale peggiori il comportamento, il suo contributo alle scelte resta esattamente zero finché le predizioni contestuali non risultano migliori del modello istantaneo su transizioni escluse dal training. Lo stato transitorio viene azzerato alla morte, mentre ciò che la rete ha imparato rimane. Questa capacità rende rappresentabili dipendenze del tipo “la stessa percezione attuale è diversa a seconda di ciò che stava accadendo poco fa”, ma non equivale a linguaggio, concetti simbolici, causalità dimostrata o pianificazione generale.

## ZERO-010.7 — chunking autonomo delle sequenze vissute
La memoria temporale rende rappresentabile una dinamica; da sola, però, la policy continua a scegliere micro-azioni isolate. La 0.10.7 introduce un livello di chunking che cerca regolarità nelle sequenze effettivamente eseguite da ZERO prima di conseguenze positive reali.

Ogni evento utile esamina i suffissi recenti di 2–8 decisioni e i rispettivi stati interni ricorrenti. La prima occorrenza rimane una semplice ipotesi. Solo una seconda occorrenza compatibile consolida il candidato abbastanza da permettergli di proporre la prima azione in un contesto simile. Il replay procede passo per passo: la policy può rifiutare ogni suggerimento, e una divergenza dello stato temporale interrompe immediatamente il chunk. Al termine, la tecnica non viene considerata riuscita finché non ricompare un effetto fisico o energetico realmente misurato.

Questo meccanismo non attribuisce significati simbolici alle skill e non conosce l'identità degli oggetti. È una forma limitata di scoperta di macro-azioni sensomotorie basata su ricorrenza e conseguenze, non una dimostrazione di comprensione astratta o pianificazione generale.


## ZERO 0127 — integrità dell'esperimento prima di nuove abilità
Le ultime osservazioni in OTA 97 mostrano una regressione di sopravvivenza e manipolazione, concomitante all'introduzione di lastre e tempeste. È stata trovata una causa software indipendente: il caricatore spaziale rigettava un checkpoint legittimo con celle neutre; inoltre il risolutore di collisioni poteva spostare un vecchio coperchio energetico senza la sua vera presa bilaterale. La correzione distingue i contatti fisici dalle collisioni geometriche, preservando l'assenza di premi per la costruzione. L'ambiente viene riequilibrato con fonti ancora finite e vento realmente lesivo e schermabile. **Questi sono interventi staged, non prove di migliorata cognizione o sopravvivenza.** L'esito va osservato su telefono e confrontato contro scenari accoppiati; documentazione in `docs/EXPERIMENT_0127.md`.

## Release preparation — ZERO 2.0 passive observation, OTA 100 candidate (2026-10-13)

Official OTA path is `feature/overmind-inapp-updater` → `.github/workflows/overmind-ota-publish.yml` → owner-key signed APK → GitHub Release → `main/updater/public/latest.json` and immutable APK → Vercel manifest consumed by in-app AGGIORNA. This candidate is staged separately from research PR #31. Original `com.overmind.zero.ota` signature and persistent 12 saved slots must remain intact. VersionCode derives from the next official publisher run number (expected ≥1100), not the old signed experimental preview 1099.

The candidate includes exactly the already tested v20 passive observers, existing base motor policy, and `TelemetryGitHub.partition()` fix preserving `extras.zero20Passive` after multipart upload. No model advice biases the motor controller; no change to energy rules or sensor physics. Source-pinned stable checks are UPDATED to the SHA of the actually reviewed source files, never disabled; five new JVM regressions are added before official release signing.

The initial Galaxy S21 report confirms the original working profile is still present and v20 models have collected real observations. No independent skill or survival improvement is established. Release acceptance: independent staging JVM/Android QA GREEN, official stable publisher GREEN, signed APK certificate/package/version and SHA verified, downloadable manifest/APK responsive, user-controlled install and slot continuity confirmed on phone. If any gate fails, **do not claim availability through Aggiorna**.

## ZERO 2.0 — ipotesi percettive, non nomi di oggetti (13 ottobre 2026)
Prima tappa sperimentale dell'apprendimento centrato sugli oggetti: ZERO può costruire un insieme limitato di prototipi percettivi da 16 raggi di distanza originati dalla membrana e verificare se configurazioni apparentemente simili ricorrono. Una corrispondenza tra sagome parziali NON è ancora una classificazione geometrica certa né l'identità persistente di un oggetto; le variazioni di prospettiva, le occlusioni e più ostacoli ravvicinati possono confonderla. Le conseguenze fisiche sono correlate alla scena e alle azioni eseguite, non ancora attribuite causalmente a una specifica cassa. Nessuna tecnica motoria viene imposta o richiamata dal nuovo modulo. Validare su scene multivista, oggetti mai incontrati e A/B di sopravvivenza prima di qualsiasi rilascio.

**OTA applicativa:** questo modulo percettivo passivo è stato selezionato per il canale AGGIORNA come candidato successivo alla 103. La build ufficiale resta soggetta a Java/Android QA, stessa firma e pubblicazione SHA/manifest prima di dirla disponibile.


## 13 ottobre 2026 — ZERO 2.2 attivo (candidato OTA117)
Richiesta utente: cervello rivisto funzionante e APK, x5/x20 fluidi, TEST MAX circa x29. Nuovo ramo `feature/zero22-active-recurrent-ota117` da `fe6d7497eb5b53055b46c474dd5470028bc97f88`. Leggere **[ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md)** per architettura, contratto sensoriale, replay, competenze, ottimizzazione TEST MAX e limitazioni. `RecurrentCoreV22` guida realmente la scelta prima dell'azione; i moduli 2.1 restano osservatori compatibili. Nessun reset del profilo o slot. 71 test JVM locali PASS; release, Android build e prova S21 da verificare separatamente. Non chiamare questo sistema Dreamer completo o intelligenza generale.

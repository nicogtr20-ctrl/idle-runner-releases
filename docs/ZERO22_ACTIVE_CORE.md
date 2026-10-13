# ZERO 2.2 — nucleo ricorrente attivo e validazione sul telefono

Data: 13 ottobre 2026. Richiesta esplicita: sostituire il centro decisionale della 2.1 con un sistema funzionante che usi memoria, previsioni e competenze, consegnare APK firmata; x5/x20 sono fluidi, TEST MAX sul Galaxy S21 arriva circa a x29. Il rilascio è una **build di ricerca attiva**, non una certificazione di intelligenza generale né di aumento della sopravvivenza.

## Base, ramo, continuità

Repository `nicogtr20-ctrl/idle-runner-releases`. Base Android esatta `fe6d7497eb5b53055b46c474dd5470028bc97f88`, ramo `feature/zero21-mobile-ota116`. Nuovo ramo `feature/zero22-active-recurrent-ota117`. Non convertire main in ZERO: main mantiene Idle Runner e ospita solo infrastruttura OTA/documenti condivisi. OTA116 aveva i moduli 2.1 **osservatori**, non un nuovo controllore attivo. Il paper 2.1 con 12 pareggi verifica non-interferenza, non miglioramento.

Package invariato `com.overmind.zero.ota`, minSdk24, firma dal bundle originale esclusivamente nel publisher GitHub. Versione prevista OTA117/code1117, pubblicazione subordinata a test JVM, controllo delle invarianti fisiche, assemble Android e firma. Non dichiarare disponibile prima di publisher concluso, manifest aggiornato e APK verificata. Installazione manualmente confermata su S21 resta da provare.

## Ciclo effettivamente implementato

1. A ogni confine di decisione si campionano i sensi reali dopo la fisica.
2. Si aggiornano corpo distribuito, raggi e ipotesi anonime di continuità.
3. `RecurrentCoreV22.consequence` confronta la previsione precedente con la conseguenza reale; apprende esclusivamente dai campioni train. Il campione held-out aggiorna soltanto la valutazione, mai pesi o replay.
4. `SensorimotorMind.choose` costruisce ancora i suggerimenti storici per preservare una prior del profilo; passa l'intero vettore al nuovo nucleo **prima** della selezione greedy e dell'esplorazione.
5. Il nucleo valuta tutte le 16 azioni, includendo valore energetico, rischio, previsione convalidata e informazione ottenibile a costo compatibile con la riserva.
6. Soltanto dopo la scelta definitiva `chosen` registra l'azione reale. L'immaginazione non viene registrata come esperienza e non può addestrare nulla.
7. Gli attuatori fisici eseguono l'azione e le tecniche contestuali compatibili; la prossima osservazione chiude il ciclo.

I vecchi `UnifiedCognitionV21`, `UnifiedArbiterV21`, `PredictiveWorldV20` restano osservatori compatibili. La nuova autorità è `RecurrentCoreV22`: non si sta fingendo di rendere attivo un arbitro che continua a essere chiamato dopo la scelta.

## Stato comune e memoria

164 caratteristiche reali:
- 26 sensi storici: riserva, velocità del materiale, estensione, contatto, adesione, prossimità, frequenze luminose.
- 80 misure corporee: 16 settori × pressione/contatto, presa realmente trattenuta, forma, strain, vista locale; derivate dai 96 nodi fisici, non da un corpo ideale.
- 32 offset delle vere origini dei 16 raggi sulla membrana, normalizzati sulla scala del corpo.
- 6 proiezioni dei coefficienti motore/manipolazione/membrana **effettivamente applicati**, per segnalare variazioni del controllore.
- 16 distanze reali dei raggi.
- 4 caratteristiche dell'ipotesi anonima di ostacolo: offset relativo x/y, ampiezza del cluster, fiducia. L'assenza di focus non inventa un oggetto.

24 unità ricorrenti deterministiche integrano stato e azione, producendo una rappresentazione da 188 valori per i readout appresi. La stessa funzione ricorrente evolve nelle traiettorie immaginate: non viene riutilizzata una memoria temporale immobile per tutti i futuri. Alla morte si pulisce soltanto lo stato transitorio. Alla ripresa dello stesso salvataggio si conserva hidden, eliminando l'intervallo pendente che non può essere ricostruito onestamente.

**Limite architetturale esplicito:** è un reservoir fisso con readout appresi online; non è un modello latente addestrato con backpropagation temporale, né una implementazione di Dreamer/RSSM. Le sei proiezioni dei controlli sono una descrizione compatta e possono aliasare coefficienti differenti. Anche la rappresentazione corporea è aggregata, non conserva tutta la geometria dei 96 nodi nel nucleo. Sono compromessi mobili da misurare, non capacità generali già dimostrate.

## Modello corporeo e futuro

Per ogni azione e caratteristica il predittore apprende una variazione con una testa locale/ricorrente da 12 coefficienti. Prevede anche energia, velocità, contatto, deformazione, presa, raggi e stato delle ipotesi; non lascia fisiologia e corpo artificialmente invariati come il vecchio osservatore v20. I coefficienti dell'attuatore non mutano spontaneamente nelle previsioni.

Il pianificatore usa al massimo tre decisioni immaginate, con policy continuativa aggiornata sul nuovo stato e bootstrap dal valore appreso. Non esiste ricerca esaustiva, ricetta della cassa, bersaglio privilegiato o energia assegnata per aver immaginato un successo. Le previsioni restano approssimazioni numeriche, non simulazioni fedeli dell'intero motore fisico.

Un'azione ottiene fiducia solo dopo almeno 24 campioni train reali e 8 campioni held-out; la previsione deve battere la persistenza dello stato del 4% e non degradare la previsione energetica rispetto alla baseline. La correzione del valore immaginato è limitata e ponderata per la fiducia. Il decadimento temporale è gamma0.90. Un test iniziale ha rilevato che moltiplicare il discount per la fiducia penalizzava paradossalmente le azioni con modello buono rispetto a quelle prive di modello: corretto mantenendo gamma costante e applicando la fiducia alla correzione finale. Non è stata indebolita la baseline per ottenere un risultato positivo.

La fiducia è per azione, non ancora calibrata per ogni contesto individuale: non basta a certificare extrapolazione a posture o mondi nuovi. I rollout non hanno un ensemble probabilistico o distribuzioni esplicite degli esiti.

## Utilità, danno, esplorazione

Il rinforzo viene dalla variazione **reale** di riserva, già comprensiva di fame, tempo, vento e contatti, con penalità terminale generica. Il danno da contatto viene accumulato separatamente: un successivo guadagno energetico non cancella l'osservazione di un urto nocivo nello stesso intervallo. Un readout del rischio modula il valore; non attribuiamo automaticamente a una presa il danno di un'altra sorgente.

Non assegniamo un premio generale a spostare casse o lastre. La componente informativa è piccola, dipende dalla riserva e diminuisce con le esperienze dell'azione. Non è ancora un algoritmo completo di information gain bayesiano; l'incertezza sul modello impedisce invece di usare previsioni non convalidate. Esplorazione epsilon dal 28% al minimo4%, modulata dal guardiano motorio storico contro stagnazione.

La policy storica è una prior limitata durante la migrazione, con peso che scende con le nuove transizioni; non domina per somma di moduli illimitata. I pesi precedenti restano appresi e salvati. Le azioni rimangono 8 direzioni × 2 adesioni; questo spazio discreto è un limite del progetto e non un controllo motorio continuo generale.

## Esperienze rare e replay

72 transizioni recenti più 24 transizioni con contatto energetico significativo o morte; ogni elemento contiene stato ricorrente prima/dopo, azione, energia netta, danno separato e terminalità. Due aggiornamenti replay per ogni transizione train reale: uno dalla banca recente, uno dalla rara quando disponibile. Nessun campione held-out, nessun risultato immaginato e nessun esito del laboratorio del giocatore vengono importati nel personaggio reale.

Il replay è durevole attraverso le vite e i riavvii, ma resta piccolo; non risolve da solo credito causale molto ritardato, priorità ottimale o dimenticanza su un archivio enorme. I due replay sono un budget fisso per il telefono.

## Capacità conservate e controllo a feedback

`CompetenceMemoryV21` mantiene coppie HAND+MEMBRANA osservate con presa opposta reale e spostamento. Il richiamo richiede contesto compatibile, almeno sei frame e almeno due vite distinte; migliaia di frame in una vita non diventano migliaia di successi indipendenti.

`SkillExecutionV22` utilizza temporaneamente la tecnica senza sostituire i coefficienti incumbent. Avvio soltanto con adesione volontariamente scelta, contatto e candidato consolidato. A ogni decisione rivaluta compatibilità e conseguenza; interrompe dopo tre intervalli senza effetto, perdita di contatto, altra azione o esperimento corporeo. Cooldown quattro secondi simulati. Non forza LED, presa o risultati fisici. Gli esperimenti di `OnlineBodyLearning` mantengono precedenza: una tecnica ricordata non falsifica la prova di una mutazione.

Questa è una capacità contestuale a feedback, non una politica motoria autonoma multistep addestrata a uno scopo. L'effetto misurato è scene-level, non una prova causale che quella tecnica abbia mosso proprio quell'oggetto. Il ritorno negativo non cancella la tecnica conservata dopo un episodio sfortunato.

## Origini fisiche dei raggi

`SpatialPerception.eyeOrigins()` espone gli offset già misurati dal sensore materiale. `ObjectContinuityV21.view` li somma alla proiezione di ogni raggio prima di associare cluster fra viste. La deformazione non viene più trattata come se tutti gli occhi fossero fissi al centro. L'overload storico senza origini rimane compatibile per vecchi test.

Risoluzione22U, occlusioni e cluster parziali continuano a rendere incerta l'identità. Pareti e ambiguità non diventano oggetti certi. Nessun ID `Entity`, posizione vera del centro dell'oggetto o etichetta cassa/porta entra nel nucleo.

## TEST MAX e costo reale

Problema dimostrato dal codice: `GameActivity` chiamava `Senses.read` a ogni substep, poi `SensorimotorMind.observe` ignorava il campione finché non raggiungeva0.65s. Raggi/occlusioni scansionavano ripetutamente 96 siti e gli oggetti senza utilità per la decisione.

Nuovo `observationDue`: vista completa solo quando serve la decisione; contatti energetici e terminali continuano a campionare vista quando realmente necessario. `elapsed` e tick della scatola nera avanzano in ogni substep. Il test di parità eager/lazy su1200 substep produce stesse azioni e serializzazione del vecchio modello. **La fisica non perde substep e il numero di decisioni per secondo simulato non aumenta.**

TEST MAX resta tasto separato nelle Impostazioni, conferma e STOP, passo1/60s, massimo4096 substep/burst, budget80ms (30ms se thermal moderate), arresto a thermal severe o pausa Android. Contatore di secondi simulati passato a double. x5/x20 restano cicli normali. Il valore x29 dell'utente è una misura della OTA116; non viene promesso x100 né certificata una nuova velocità senza dati S21. Il nuovo nucleo aggiunge lavoro per decisione e può compensare parte del guadagno dei sensori.

## Salvataggi, report, privacy

Nuova chiave opzionale `recurrent_core_v22`, payload `rc22`, float binari compressi e CRC. Decode atomico in oggetto temporaneo, limiti su caratteri e decompressione, finitezza e dimensioni; corruzione lascia intatto il nucleo valido. Codec Base64 portabile per AndroidAPI24. Replay, readout e hidden salvati; nessun pending non concluso restaurato. Vecchi slot senza la chiave mantengono tutti gli apprendimenti e iniziano il nucleo vuoto. I dodici slot nominati non vengono sovrascritti.

Reset WORLD include il nuovo nucleo perché i suoi pesi/replay condividono tutte le esperienze; gli altri reset restano separati. Il digest mostra contatori aggregati, modo ACTIVE e fiducia, senza pubblicare pesi. Upload sempre tramite comando esplicito Condividi dati. I report A/B sono conservati in preferenze separate e inclusi nell'export multipart; non addestrano il personaggio.

## Due test locali sul telefono

ANALISI → LABORATORIO COMPORTAMENTALE: screening breve,18 prove di32s, controllo precedente vs2.2, energia/pericolo/cancello. Ridotto e censurato prima della morte per fame; non misura sopravvivenza lunga.

ANALISI → CONFRONTO2.2 ESTESO/CASSE:12 coppie × due policy, stesso snapshot iniziale e stessi semi/scenari; fino a170s o morte. Motore reale96nodi, prese bilaterali, energia finita sotto casse, lastre e pericoli. Metriche energia netta, danni, sopravvivenza e frame di prese opposte; report indica vittorie/sconfitte/pareggi e valori grezzi. Cancellabile, stop all'uscita e thermal severe. Non replica streaming, vento o mutazioni della partita: dichiarato nel report. Copie e apprendimento isolati. Una vittoria non prova generalizzazione; servono più campioni e mondi nuovi.

## Prove eseguite e limiti di consegna

71 entrypoint JVM del publisher e nuove regressioni eseguiti localmente con ECJ17:71/71PASS; output completo `ZERO22_JVM_RESULTS.json`. Nuove prove: corpo e origini cambiano lo stato, energia+hidden evolvono nel futuro, dati immaginati non modificano pesi/contatori, heldout escluso dal replay, terminali, memoria conservata al resume, restore corrotto atomico, compatibilità slot codec, scelta benefica attiva38/40 in fixture sintetica, conservazione delle tecniche e abort dopo tre fallimenti, copie fisiche ripetibili. **38/40 è una fixture sintetica, non un risultato nel gioco.**

Compilazione completa Android e publisher devono essere annotati a SHA/run esatti nel handoff, senza usare vecchi run verdi. Nessuna prestazione del S21, continuità dei dodici slot dopo installazione o aumento di capacità del personaggio è dimostrata da questi test. Il nuovo cervello ha autorità ed esegue il ciclo; “super intelligente”, comprensione generale, causalità robusta e adattamento a tutti i mondi restano obiettivi sperimentali.

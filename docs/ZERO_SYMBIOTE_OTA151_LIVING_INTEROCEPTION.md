# ZERO OTA151 — interocezione reale del fluido e integrazione neuromuscolare

Data: 15 ottobre 2026. Origine: richiesta utente di completare davvero la percezione interna del corpo simbionte, non limitandosi a registrare sensori passivi.
PR sorgente: https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/55 . Merge 0610c7b1403022940d44c9d9a5ee7502a0b3afd5. Pre-release QA https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38703888024 **SUCCESS**, includes extensive Java/old-body/muscle migration regression and Android assembleDebug. Signed publisher run 38704153530 initiated; inspect updater/public/latest.json and run conclusion before claiming release.

## Stato precedente OTA150
Il corpo è fluido: 96 masse membranarie, 10 vecchie masse *senza più ancoraggi* e 64 particelle aggiuntive realmente mobili. Fino alla OTA150 il cervello vedeva direttamente solo membrana, odometria globale da 170 masse, contatti e energia. Non aveva recettori delle 74 masse interne e quindi non poteva attribuire variazioni di compressione, densità, scorrimento interno e distanza dalla pelle a specifiche parti del proprio tessuto. OTA148 aveva sbloccato un blocco topologico; non toccare la fisica funzionante.

## Nuovo recettore reale `FluidInteroceptionV1`
Campionamento ogni confine di decisione della rete principale (~0,65 secondi **simulati**), senza alterare alcuna particella o forza. Per **ognuna delle 74 particelle fluide effettive** (MotorBrain.N=10 + SymbioteMatterV1.INNER=64) si misura:
1. `DENSITY`: numero di masse interne entro 12 unità, diviso 18 e limitato a 1. Due masse che coincidono contano comunque come accumulo; nessun normale inventato se distanza nulla.
2. `COMPRESSION`: velocità relativa d'avvicinamento tra vicini entro 12 unità, normalizzata; **proxy cinematico della pressione**, non pressione fisica in pascal.
3. `SHEAR`: differenza di velocità tangenziale tra particella e vicini, normalizzata.
4. `RADIAL_V`: velocità della particella rispetto al baricentro proiettata verso esterno.
5. `TANGENTIAL_V`: velocità trasversale rispetto al baricentro.
6. `BOUNDARY_GAP`: distanza reale minima da un segmento della membrana viva, non da una sfera ideale centrata su ZERO.
7. `AGITATION`: velocità relativa della particella rispetto alla velocità globale.

Ogni dato deriva solo da `body.x/y/vx/vy`, `liquid.x/y/vx/vy` e `skin.x/y`: nessuna fonte conosce dove sono alimenti, spostamenti desiderati, oggetti semantici o azioni corrette. Il recettore espone una mappa grezza **74 × 7 = 518 misure** e una rappresentazione distribuita **16 × 7 = 112** raggruppata secondo la posizione effettiva delle particelle nel corpo. I 518 valori non sono falsamente descritti come input separati del nucleo 2.2.

## La rete interocettiva è attiva, non un mero observer
- Il cervello principale `SensorimotorMind.choosePrimaryV22` riceve un segnale per ogni azione da `FluidInteroceptionV1.choiceBias`, un modello addestrabile sulle conseguenze fisiche delle reali 16 scelte. Il segnale entra in `RecurrentCoreV22.primaryScores`, che resta l'unico selettore.
- Valutazione azione `choiceBias` rigorosamente zero finché il modello del fluido non supera almeno 32 intervalli indipendenti *heldout*, con errore predittivo inferiore alla persistenza naive. Altrimenti l'esperienza nuova non può inventare una direzione da seguire.
- Il modello apprende transizioni fisiche attuali con una regressione locale per ogni settore, e una previsione di valenza basata su **energia realmente acquisita o danno reale**, non su distanza percorsa, oscillazione, estensione di una pendice o movimento imposto come premio.
- Anche il controllore muscolare `SymbioteMotorHeadV1`, per ognuno dei 16 settori, riceve **tutti i 7 canali locali dell'interocezione**, oltre ai 14 ingressi precedenti (totale 21). Nuove connessioni apprendibili per i quattro canali continui: pressione, torsione tangenziale, adesione volontaria, rigidezza. L'azione 2.2 resta unica; nessun riflesso prestabilito, nessuna forza fantasma.
- I valori sensoriali diventano così parte del circuito completo **materiale fluido reale → sensibilità → previsione → scelta ricorrente → attuazione neuromuscolare → nuova sensibilità**.

## Persistenza compatibile senza distruggere il vecchio ZERO
- Nuova preferenza `fluid_interoception_v151`, payload binario compresso con CRC-32, validato in memoria temporanea prima di ripristinare i pesi, salvata automaticamente insieme al working ZERO e ai 12 slot nominati tramite il meccanismo generale esistente.
- Il modulo mantiene i pesi attraverso le morti; reset di vita cancella solo la memoria transitoria degli intervalli. Un reset esplicito della categoria PROPRIOCEPTION include ora questo modulo; le altre reti non vengono azzerate.
- `SymbioteMotorHeadV1` conserva il prefisso `smh1|` con estensione compatibile dei pesi: dai vecchi **4 × 14 = 56** a **4 × (14+7) = 84**. Se un save porta i 56 originali, vengono ricopiati **esattamente in tutti i quattro blocchi originari**; ogni nuovo coefficiente parte **esattamente zero**. Anche l'inizializzazione del motore vergine preserva lo stesso numero/ordine di estrazioni casuali del vecchio algoritmo. Così il nuovo sensore non modifica improvvisamente il moto corrente. Un save già esteso viene restaurato nativamente.
- **Invariati**: ID `com.overmind.zero.ota`, `RecurrentCoreV22.F=164` e pesi learned, vecchi sensori, massa/attrito/solver OTA148, contatti/grip fisici, ricompensa di sopravvivenza, visione e la semantica degli slot.
- `GameActivity` ripristina e collega i recettori alle particelle reali durante nascita, ripresa di una partita salvata, decisioni periodiche e checkpoint; in modalità vecchio corpo i recettori sono inattivi.

## Regressione documentata
`FluidInteroceptionRegression` (eseguita da QA pre-release e firmata) verifica:
- 74x7 canali con valori non inventati e 16x7 rappresentazione apprendibile.
- Perturbazione della velocità di **una vera particella** varia l'agitazione solo perché la velocità è cambiata; compressione vera del materiale varia il sensore di densità.
- Prima della verifica indipendente il modulo ha **zero autorità decisionale**; una volta simulata nella sola fixture una validazione heldout attendibile, il modulo entra nel punteggio di una vera azione del cervello 2.2 con coefficiente 0,40, senza creare valori per altre azioni.
- Conseguenza di una contrazione realmente eseguita modifica la memoria degli effetti; forma e pesi memorizzati sopravvivono al salvataggio, ricaricamento e morte; payload corrotto non muta il modello.
- Migrazione reale da 56 vecchi coefficienti muscolari a 84, senza differenza nei comandi di movimento prima dell'apprendimento dei nuovi ingressi.
- Tutti i vecchi test della superficie e del corpo, e compilazione Android, restano obbligatori. Sorgenti nuovi/modificati sono protetti da pin SHA Git, controlli di firma e conservazione del package.

## Limiti fisici dichiarati e prossimo test sul cellulare
Questa versione fornisce interocezione distribuita **cinematica** basata sui segnali che il motore 2D sa realmente calcolare. Non è un sensore della pressione idrostatica misurata o del metabolismo intracellulare, perché il modello fisico non possiede grandezze fisiche corrispondenti. Chiamarle tali sarebbe un dato falso. Il contributo delle nuove connessioni non garantisce prestazioni migliori immediatamente: sono nuove e devono acquisire esperienze reali; il sistema è già fisicamente collegato, ed è ZERO a impararne l'uso. L'affidabilità è gated on heldout. Per confermare efficacia **in vivo**: installare sopra app precedente senza cancellare dati o creare altro organismo; osservare la dinamica del corpo e le prese, caricare telemetria del working profile e confrontare energia acquisita, azioni validate e sopravvivenza a pari intervalli di esperienza. Non descrivere la sola compilazione come prova definitiva di apprendimento sul telefono.

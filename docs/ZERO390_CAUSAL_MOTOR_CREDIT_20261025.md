# ZERO390 — vero controllo causale dei muscoli del cervello 3.0

**25 ottobre 2026. STATO: codice sperimentale REALMENTE MODIFICATO, NO OTA e NO APK V390, locomozione NON ancora convalidata.**

Branch `fix/zero390-isolated-motor-credit-wall-validity`; genitore `audit/zero389-v388-actual-33312-obstacle-motor-causal`. L'utente ha condiviso il V388 autentico `zero-telemetry/reports/20261025-104220-36e86668b9aff`: 33.312 decisioni, 19.452 modifiche corteccia, 0/8 direzioni V371 validate, 13.961 stalli, 256/256 ultimi contatti parete, 165 intenzioni a EST con dx medio −0.022U verso OVEST, vera luce gemma blu senza parete fra agente e risorsa. Riproduzione Android A/B restaurando anche i 64 stati ricorrenti: cortex appresa EAST davanti al muro dx=−0.4154U, cortex vergine EAST dx=+0.7486U. Senza muro cortex appresa EAST dx=−2.6028U, vergine −0.7489U. **19k aggiornamenti non provano controllo utile**.

## Correzione 1: localizzare davvero il perturbatore muscolare
`Zero3LearningFoundation.AdaptiveCortex.isolatedProbeV390`: soltanto **una** perturbazione di una uscita su 64 durante una decisione, campionata attraverso tutte le 16 regioni e i tre canali neuromuscolari CH0, CH1 e CH3. CH2 rimane ESCLUSIVAMENTE sotto V319, non si reinstalla il 75% dell'attuatore V2.2. Nella V388 la perturbazione colpiva contemporaneamente **16 muscoli dello stesso canale**, sebbene la misura della risposta fosse un solo scalare globale del COM: attribuzione non-identificabile. Nel V390 solo l'unica sinapsi/output perturbata è eleggibile per credito attraverso l'attuale aggiornatore; nessun target preprogrammato, oscillatore, passo/gait, forza esterna, modifica ai 512 pesi o 194 ingressi GRU64. Il contatore `perturbationEpisodes` salvato continua da 19k update senza reset. Il precedente metodo multi-muscolo viene preservato nel laboratorio per A/B diretto.

## Correzione 2: non premiare l'inerzia fermata da un muro
`Zero3LearningFoundation.Coordinator.observeContactV390`: legge la vera superficie **POST** dopo il risolutore fisico e classifica in ogni frame contatto dei punti davanti/dietro rispetto all'intenzione scelta dal GRU3, usando solo normali ricavate dalla reale membrana e `skin.touchSense` istantaneo. Il training sulla stessa scelta usa gli stessi PRE/POST reali e:
- **muro davanti:** non assegna gradiente al muscolo perché l'arresto del preesistente scivolamento può sembrare falsamente accelerazione utile nel vecchio `dx − velocitàPRE × durata`;
- **contatto dietro, senza contatto anteriore e senza correzioni del mondo:** consente il gradiente solo quando il centro di massa avanza per davvero di oltre 0.20 unità lungo l'intenzione, con score misurato da **progresso ASSOLUTO reale**, non dalla baseline fittizia dell'inerzia costante;
- **corpo fermo o in regressione:** nessun aggiornamento locomotorio da contatto, ma nessuna decisione comportamentale viene sovrascritta;
- **mondo libero:** conserva il precedente punteggio motorio e sostituisce soltanto la perturbazione combinata con la misura a sito isolato.
Questo non dimostra causalità perfetta né la capacità di camminare, ma elimina due errori di credito source-confirmed senza forzare il percorso.

Il predittore del moto V353, l'archivio DIRETTO V387, il senso V384, GRU64 unico, i 170 punti reali, 12 slot, checkpoint V356 CRC e possesso CH2 original grip non cambiano. La mappa diretta V387 mantiene il veto di contatto: è una **mappa di risposta a mondo libero**, non un modello di appoggio alla parete. Futuro lavoro deve costruire osservazione separata affidabile della trazione sui contatti reali.

## Verifiche
- [ZERO390 Android 18/18 PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40234592208): **4 test nuovi** (esattamente un'uscita perturbata, 16 settori e 3 canali, fuga posteriore valida, correzione blocco frontale e inerzia, immobilità), più 14 regressioni V382, V384, V386 e V387, app Android originale compilata.
- [Gate 2.000 decisioni originali autonome](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40234759899): il test `Zero366OriginalAutonomousSurvival2000Test` deve superare rapporto allineamento >=12%, raccolta blu e meno danni rossi. **Non dichiarare PASS senza risultato ufficiale**.
- [Test su cortex vera salvata](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40235047044): A/B sperimentale SOLO laboratorio e read-only, 128 perturbazioni testate sulla vera geometria S21 e vera cortex originale, rispetto alle precedenti 16 perturbazioni correlate. Ripristina 170 particelle, inizializzazione identica per ogni trial, non agisce mai sui file/slot dell'utente. Le quattro direzioni sono *schedule di prova*, non azioni prescritte al giocatore nel prodotto. Non equivale a una prova di navigazione autonoma.

## Rischi scientifici ancora aperti
- Le forze reattive interne devono tradursi in attrito/appoggio fisico; isolare il gradiente non garantisce che il corpo riesca a uscire dal muro con il checkpoint già degradato.
- In presenza di 256/256 wall touch, gli aggiornamenti possono essere rari perché le correzioni geometriche esterne restano censurate. Non inventare una falsa `wall escape` per aumentare il numero.
- L'evidenza della mappa di traiettorie V371 ancora 0/8; la memoria V384 risulta troppo conservativa in alcune celle. Aggiungere ulteriore discernimento fisico/affordance *solo* con vera controprova.
- Non cambiare la soglia dell'accettazione autonoma solo perché la simulazione fallisce. Se 2k non raggiunge la soglia, lo stato V390 è **NON FUNZIONANTE**, anche se tutti i test unitari passano.

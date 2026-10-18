# ZERO 3.0 V3.7 — MEMORIA TEMPORALE DELLE CONTRAZIONI E PREDIZIONE A 384 FRAME

**Data:** 17 ottobre 2026. **Stato iniziale:** benchmark sperimentale separato, non una nuova skill autonoma, non un aggiornamento OTA.  
**Documenti obbligatori:** [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI 3.0](ZERO30_UI_UX_SPEC.md), [risultati V3.6](ZERO30_MOTOR_V36_LONG_HORIZON.md).  
**Codice:** [Zero3MotorTemporalHistoryRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorTemporalHistoryRegression.java).  
**Workflow isolato:** [ZERO3 motor temporal memory QA](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/.github/workflows/zero30-motor-temporal-memory-qa.yml).

## 1. Ipotesi falsificabile

La V3.6 ha tentato di prevedere l'effetto a 384 frame con 52 feature (postura, velocità istantanea, stato della membrana). In 256 coppie non viste il suo MSE tardivo è stato **0,022099238**, contro **0,023833805** della baseline senza effetto: solo **7,28%** di riduzione, meno del gate del 10%. Il semplice incremento di sensori istantanei non risolve l'incertezza sulle conseguenze a lungo termine.

**Ipotesi V3.7:** una memoria verificabile di **muscoli effettivamente contratti e stati osservati PRIMA** dell'azione migliora la predizione delle conseguenze ritardate rispetto al modello che legge soltanto le informazioni istantanee, sullo stesso identico dataset di fisica e sulle stesse partizioni.

Non introdurre gait preprogrammati o interventi occulti sul corpo; nessun futuro entra negli input.

## 2. ADR ZERO3-M37 — intervento unico dopo storia fisica registrata

Il nuovo esperimento esegue **96 frame fisici di storia prima dell'intervento**, con quattro piccoli impulsi esplorativi *irregolari*, generati da seed deterministici che dipendono esclusivamente dalla condizione iniziale. I quattro impulsi precedenti sono applicati **in modo identico a entrambi** i corpi fisici gemelli, senza alcuna indicazione di risorse o direzione del premio. Il seed NON cambia tra candidate muscolari dello stesso contesto: il confronto fra candidate non è contaminato da storie fisiche differenti.

Durante i 96 frame di storia si registrano **i 64 comandi realmente eseguiti** e il movimento/velocità realmente risultante dalla fisica, *dopo ogni frame*. Al momento della decisione, **prima** di applicare l'impulso test, vengono congelati:
- 52 feature correnti della V3.6, realmente derivate da `MotorBrain.Body`, `Morphology.Surface` e `SymbioteMatterV1`;
- **16 feature temporali aggiuntive**: quattro proiezioni dei cambiamenti di posizione lungo l'intenzione ai ritardi di 4/12/32/80 frame; quattro differenze di velocità sugli stessi ritardi; otto riassunti delle contrazioni locali realmente eseguite nei quattro canali, su finestre recenti di 12 e 80 frame.

Il laboratorio continua con un impulso di **12 frame** in un solo gruppo muscolare, attraverso l'array reale di 64 attuatori. La conseguenza viene misurata fino a **384 frame dopo l'inizio** dell'intervento, confrontando le due simulazioni fisiche gemelle indipendenti. L'esito controfattuale esiste soltanto in laboratorio: ZERO non dispone di una seconda vita parallela nell'app.

## 3. Confronto corretto e dati esclusi

| Gruppo | Posture | Stato fisico iniziale | Coppie baseline/intervento |
|---|---|---|---:|
| TRAIN | 0°, 90° | 0, 1, 2 | 384 |
| CALIBRAZIONE | ±22,5° | 0, 1, 2 | 384 |
| TEST FINALE CIECO | ±30°, ±60° | 3, mai nel fit | 256 |
| **Totale** | | | **1.024** |

Ogni coppia comprende 96 frame di preazione e 384 frame di conseguenza, per **983.040 integrazioni** del vero solver tra controllo e intervento, escluse le precondizioni preliminari.

Si addestrano, **sulle stesse 384 coppie**, due modelli ridge con identica penalizzazione e stessi obiettivi osservativi: il modello **52 (solo stato corrente)** e quello **68 (stato corrente + memoria)**. Entrambi prevedono spostamento immediato, spostamento tardivo e costo motorio incrementale.

La calibrazione autorizza un modello di laboratorio solo se il MSE supera la baseline zero di almeno 10% su **tutti e tre** gli output. Il test finale resta escluso da addestramento e calibrazione. Si misura separatamente se la predizione tardiva a 68 feature supera quella del modello 52 **sullo stesso insieme non visto**. L'eventuale successo della selezione di piccoli impulsi non cancella un fallimento del gate MSE. **Non cambiare le soglie dopo aver letto il test finale.**

Il risultato su 1.024 coppie NON è il test di 2.000 esperienze in una singola vita autonoma: nessuno dei due controllori esegue ancora apprendimenti/salvataggi lungo una vita nel gioco.

## 4. Cosa conterà come risultato e cosa no

- **Progresso parziale:** MSE tardivo inferiore sul blind con la memoria rispetto al 52, senza regressioni importanti sugli altri due output.
- **Superamento del gate di predizione:** modello con memoria almeno 10% migliore della baseline zero su tutte e tre le uscite *anche sul blind*; verifica in scene/seed indipendenti ancora necessaria.
- **Superamento G3 locomozione:** NON deducibile da questo laboratorio. Richiede il cortex che impara online da sequenze vere, valutazione senza esplorazione su corpi indipendenti, energia, molte vite/scene, 2.000+ esperienze e poi Galaxy S21.
- **Protezione dell'utente:** preservare OTA172, identità app, firma, 12 slot, cervelli storici; niente upload OTA e nessuna sostituzione UI 2.2 per una versione non funzionante.

## 5. Esiti verificati

**Prima dell'esecuzione:** in attesa dei log GitHub del nuovo workflow; registrare qui numeri precisi, eventuali errori di compilazione e il gate reale. Compilazione SUCCESS non significa apprendimento motorio SUCCESS.

**Decisione sino a nuove prove:** G3 e G5 completi **BLOCCATI**. UI 3.0 e persistenza V3 **ancora da implementare**.


## 6. RISULTATI REALI E VERIFICATI — CI 38982032827

**[GitHub Actions 38982032827 — SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38982032827):** compilazione JVM, **1.024 coppie** (384 train, 384 calibrazione, 256 test) con **983.040 integrazioni della fisica reale** fra controllo e intervento, storia pre-azione di 96 frame e osservazione del futuro per 384 frame. Lo stato stabile non è stato modificato. **SUCCESS tecnico NON è PASS dell'efficacia**.

| Test | MSE baseline zero immediata | MSE immediata (52) | MSE immediata (68) | MSE baseline zero ritardata | MSE ritardata (52) | MSE ritardata (68) |
|---|---:|---:|---:|---:|---:|---:|
| CALIBRAZIONE (384) | 0,003654589 | 0,000470035 | **0,000466536** | 0,032314788 | 0,027753204 | **0,027701058** |
| TEST CIECO (256) | 0,003400356 | 0,000547288 | **0,000544011** | 0,027482904 | 0,026684365 | **0,026679250** |

Costo finale: baseline zero MSE **0,000000007806**, entrambi i modelli **0,000000000429**.

La memoria a 68 feature migliora l'MSE della risposta ritardata nel test finale di appena **0,019% rispetto alle 52 feature**. Rispetto al riferimento zero l'MSE ritardata migliora del solo **2,92%**, contro la soglia preregistrata del **10%**. Il modello con memoria aveva passato il gate sul set di calibrazione, ma **FALLISCE il gate sul set non usato nell'allenamento/calibrazione**, come fallisce il 52 nello stesso ambiente.

Selezionando piccoli impulsi solo dalle predizioni, **13/16** hanno avuto una conseguenza firmata positiva a costo sotto 0,005 in **16/16** casi (le scelte nulle sono comprese nel conteggio dei casi sotto budget). Guadagno firmato aggregato **+3,91126 U**, lavoro extra **0,0019250**. **Non costituisce una abilità utile al gioco**, non è una sequenza di decisioni autonoma e l'esito è nettamente inferiore a una previsione affidabile sul lungo periodo.

### Analisi e decisione

- **Confermato:** il registro dei **veri 64 comandi pregressi** è tecnicamente realizzato; il target è calcolato esclusivamente dal vero solver; identiche storie e stati fisici prima dell'azione eliminano il confondimento da differenze iniziali tra i due bracci.
- **Confermato in questo esperimento:** aggiungere 16 riassunti di storia a una regressione statica ridge non produce un miglioramento sostanziale della generalizzazione ai corpi precondizionati non visti.
- **Da verificare prima di concludere che «la memoria non serve»:** normalizzazione delle 16 feature temporali (possono avere scale molto diverse), capacità non-lineare/ricorrente di attribuzione agli impulsi, previsione multi-step condizionata dalle future azioni effettivamente eseguite, eventi di adesione/contatto e stabilità oltre il set visto. Non modificare le soglie in funzione del risultato finale già conosciuto: eventuali esperimenti di scaling richiedono **NUOVE pose/seed finali non già esaminate**.
- **Gate G3 motorio e G5 world model completo: BLOCCATI.** Non aggiungere il test-ridge della V3.7 alla corteccia del gameplay; no skill, no nuovi neuron count fittizi, no UI V3, no OTA.

**Passo tecnico motivato:** se si sperimenta una normalizzazione della memoria o un modello di stato dinamico, stimare tutti i parametri esclusivamente dal train, effettuare il gate sulla calibrazione e confrontare su **nuovi** corpi/pose/sequenze non usati per ottimizzare; solo dopo test di 2.000+ decisioni e apprendimento davvero online.

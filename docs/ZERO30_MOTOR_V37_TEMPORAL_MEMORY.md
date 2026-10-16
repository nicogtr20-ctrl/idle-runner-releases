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

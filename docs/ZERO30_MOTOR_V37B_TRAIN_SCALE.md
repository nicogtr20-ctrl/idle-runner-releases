# ZERO 3.0 V3.7-B — NORMALIZZAZIONE DELLA MEMORIA MOTORIA, NUOVO HOLDOUT

**Data:** 17/10/2026. **Stato:** laboratorio (non gameplay, no Android OTA). Leggere [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [SPEC UI 3.0](ZERO30_UI_UX_SPEC.md), [fallimento V3.7 originale](ZERO30_MOTOR_V37_TEMPORAL_MEMORY.md) prima di intervenire.

## Perché serve

Il benchmark V3.7 ha mostrato MSE ritardata su 256 corpi finali non visti di **0,026684365** con stato 52D e **0,026679250** con stato+storia 68D: appena **0,019% di miglioramento**. Il modello non supera la baseline zero a 384 frame. Non si deve concludere che la memoria sia inutile: le feature della storia di velocità/distanza sono spesso su scale minuscole rispetto agli input geometrici; in regressione ridge con penalità uniforme possono ricevere pesi artificialmente piccoli.

## ADR ZERO3-M37B — ablation test a feature normalizzate, dati nuovi

Sorgente: [Zero3MotorTemporalScalingRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorTemporalScalingRegression.java). Workflow: [zero30-motor-temporal-scaling-qa.yml](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/.github/workflows/zero30-motor-temporal-scaling-qa.yml).

Si confrontano **tre modelli sulle stesse prove reali**:
1. **52D istantaneo** (regressione ridge con la base sensoriale corrente).
2. **68D storia motoria grezza** (la V3.7 originale, senza scaling delle 16 feature storiche).
3. **68D con normalizzazione della sola storia**, usando media e varianza **stimate esclusivamente dai 384 esempi di TRAIN**, deviazione standard minima fissata a **0,005** per non amplificare rumore quasi costante. La penalizzazione ridge resta **0,5**, uguale per tutti.

Tutti i modelli usano soltanto la storia di **96 frame effettivamente osservati prima del comando**, mai effetti futuri o coordinate privilegiate. L'intervento sperimentale dura 12 frame e il risultato è misurato fino a 384 frame dopo l'inizio.

Le 384 coppie TRAIN (0° e 90°, precondizioni 0/1/2) e le 384 coppie CAL (±22,5°, precondizioni 0/1/2) mantengono protocollo e seed V3.7 per permettere il confronto. **Il nuovo holdout non ripete il vecchio finale**: nuovi orientamenti **±15° e ±75°** e stato fisico precondizionato 3, per **256 coppie nuove**. La storia esplorativa deterministica è derivata dai nuovi contesti, non riutilizzata come esperienza passata. Totale **983.040 passi del solver** su 1.024 coppie fisiche prima dei precondizionamenti.

Il modello può risultare calibrato solo se batte la baseline che prevede effetto nullo **di almeno il 10% sui tre output** nel set CAL. Sul nuovo blind sono riportati separatamente MSE immediate/ritardata/costo, variazione percentuale **contro 52D e 68D grezzo**, e variazione verso baseline zero. Un punteggio favorevole sui 256 casi nuovi **non dimostra** generalizzazione statistica multiseme o capacità di movimento continuativo.

**Soglie fissate prima della lettura dei risultati della nuova prova; non retuning sul blind.** Se la predizione tardiva non supera il gate sul blind, G3/G5 restano non superati, e si passerà alla dinamica esplicita ricorrente e a scene differenti, non a nuovi sensori arbitrari.

## Risultati verificati

Da popolare SOLO dopo il completamento della relativa CI GitHub, distinguendo SUCCESS compilazione/JVM dalla qualità del modello. Nessun utilizzo di V3.7B nella app, nuova UI e neurogenesi V3 non realizzate.


## RISULTATI V37-B VERIFICATI — CI 38982506125

**[GitHub Actions 38982506125 — SUCCESS tecnico, gate qualitativo FALLITO](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38982506125)**. Stesso vero solver materiale, 96 frame di storia preazione, target a +384 frame; **384 coppie TRAIN + 384 CAL + 256 NUOVE coppie blind ±15°/±75°, stato fisico 3**, per **983.040 integrazioni** di controllo e intervento. Media e deviazione standard della memoria stimate **solo dal training**, con pavimento 0,005; min scala 0,005 max 0,033801. Nessuna contaminazione della normalizzazione con outcome del test.

Il modello con storia normalizzata supera la baseline zero sui tre output **in calibrazione**: MSE risposta immediata **0,000458268** vs zero **0,003654589**; ritardata **0,028134822** vs zero **0,032314788**; costo **0,000000000207** vs zero **0,000000007768**. L'approvazione di calibrazione NON equivale a validità nelle nuove condizioni.

| Test FINALE mai visto (256 coppie) | MSE immediata | MSE ritardata +384 | MSE lavoro |
|---|---:|---:|---:|
| Baseline senza effetto | 0,003286281 | **0,033472508** | 0,000000007783 |
| 52D stato corrente | 0,000568402 | 0,035810702 | 0,000000000495 |
| 68D memoria grezza | 0,000563285 | 0,035728649 | 0,000000000495 |
| **68D memoria normalizzata sul solo train** | **0,000540796** | **0,035384825** | **0,000000000490** |

La normalizzazione migliora l'MSE ritardata di **0,962% rispetto a memoria grezza** e **1,189% rispetto al modello 52D**, ma il risultato è **circa 5,71% PEGGIORE** del semplice predittore zero sull'effetto ritardato. Il gate su nuovi corpi e pose è quindi **FALLITO**.

Interventi selezionati esclusivamente dalle predizioni calibrate: **10/16 utili con costo accettabile** su nuove posture, non 16/16. Questa selezione non è stata addestrata su un mondo reale autonomo e non ha dimostrato alcuna skill.

### Decisione scientifica esplicita

- **La V3.7 e V3.7-B sono completate come prove di laboratorio, NON come locomozione appresa.** Il vantaggio di rappresentare la storia motoria è reale ma molto piccolo entro questa famiglia di ridge lineari; la generalizzazione sul lungo orizzonte peggiora fuori distribuzione.
- Non effettuare ulteriore retuning su ±15°/±75°: anche questa partizione è stata consumata come test finale. Per nuovi algoritmi preparare altre scene/seed/perturbazioni, senza riciclare i due blind come dati decisionali.
- **Prossima ipotesi da implementare:** modello **dinamico ricorrente / evoluzione multi-step** che predica stato e risultato in dipendenza dalle **azioni realmente successive**, con osservazione di tensioni, adesione, velocità, errori causali e interferenza dei muscoli. Misurare previsione passo per passo, lungo termine e prestazioni motorie in una sola vita prima di promuovere una skill.
- La fisica mostra effetti residui e inversioni di segno dopo le finestre di 48 frame, quindi una sola etichetta 384-frame da stato iniziale statico è un obiettivo insufficiente.
- **G3 locomozione autonoma e G5 world model complessivo BLOCCATI; nessuna integrazione GameActivity, nessun cervello 2.2 modificato, nessun APK V3 e nessuna nuova UI.**

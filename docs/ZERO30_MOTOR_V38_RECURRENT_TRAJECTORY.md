# ZERO 3.0 V3.8 — MODELLO DINAMICO RICORRENTE MOTORIO, 32 PASSAGGI FISICI

**Stato:** prototipo sperimentale Java isolato. **NON** attivo in gameplay, non incluso nell'OTA, non integrato nei 12 slot o nella nuova UI/UX. Data: 17 ottobre 2026.

**Lettura vincolante:** [ZERO30 MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [SPEC UI](ZERO30_UI_UX_SPEC.md), [V3.7](ZERO30_MOTOR_V37_TEMPORAL_MEMORY.md), [V3.7-B](ZERO30_MOTOR_V37B_TRAIN_SCALE.md).

Codice: [Zero3MotorRecurrentTrajectoryRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorRecurrentTrajectoryRegression.java).  
CI senza APK: [zero30-motor-recurrent-trajectory-qa.yml](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/.github/workflows/zero30-motor-recurrent-trajectory-qa.yml).

## Motivazione dai dati, non da congetture

V3.7 e V3.7-B hanno registrato 96 frame reali di storia muscolare preazione, ma il modello statico ridge con 68 ingressi non generalizza i risultati a 384 frame. V3.7-B su nuove pose ha MSE **0,035384825** sulla risposta tardiva contro **0,033472508** della baseline zero (modello peggiore del riferimento). Anche 52 input istantanei mostrano una previsione insufficiente. Ciò NON dimostra che il cervello sia incapace di imparare; smentisce **quel tipo di regressione diretta a lungo orizzonte**.

## ADR ZERO3-M38 — predizione di *traiettoria*, non solo di esito finale

La V3.8 è il primo laboratorio che apprende una traiettoria del corpo, campionata a **32 scadenze ogni 12 frame** di `SymbioteMatterV1` (32 × 12 = 384 frame). Il test distingue:

- **52 ingressi corporei preazione** (orientamento reale, velocità, strain, adesione, geometria membrana e canale muscolare), più **16 tracce reali** provenienti dai 96 frame precedenti (contrazioni fisicamente eseguite e stati corporei post-solver).
- Uno **stato ricorrente di 24 unità a pesi interni deterministici e fissi**, aggiornato in ciascuno dei 32 intervalli da stato precedente, sensori preazione e stimolo muscolare noto nell'intervallo corrente. Non contiene le coordinate future osservate.
- Una **testa di previsione allenabile ridge**, supervisionata sulla reale differenza della traiettoria fra due corpi fisici abbinati: una volta ogni 12 frame misura la differenza firmata di spostamento e di lavoro muscolare. Si allena su tutta la traiettoria, non soltanto sul risultato a +384.
- In fase di test, lo stato ricorrente **prosegue con le proprie ricorrenze senza teacher forcing dalle posizioni future reali**. La futura sequenza dei comandi esplorativi è nota perché è un singolo impulso predeterminato nel laboratorio; nel gameplay le azioni future autonome NON sono note a priori. Nessun mondo parallelo esiste nell'app.
- Il riferimento statico ridge a 68 ingressi predice lo stesso obiettivo finale a partire dagli **stessi** casi di fisica. Un riferimento zero su spostamento, risposta tardiva e costo viene sempre riportato.
- Si valuta anche MSE dell'**intera traiettoria a 32 scadenze** e non solo l'MSE del risultato tardivo. L'esito rilevante per la scelta di muscoli resta la qualità a lungo termine e l'efficienza, **non** il solo colore del test GitHub.

**Limitazione sostanziale e documentata:** questo è un *echo-state style recurrent model* con ricorrenze fisse e readout addestrato, NON un GRU addestrato end-to-end, NON un cervello motorio vivo e NON un sostituto della struttura ZUAB 3.0 prevista. È un esperimento per stabilire se modellare la traiettoria migliori il segnale di credito. Le ricompense confrontate tra due simulazioni identiche sono note soltanto al ricercatore.

## Protocollo preregistrato e separazione dei dati

Le traiettorie provengono da **due istanze fisiche indipendenti** dello stesso organismo. Entrambe ricevono lo **stesso identico** precondizionamento e la stessa sequenza deterministica di contrazioni irregolari per i **96 frame prima dell'intervento**. Poi una sola delle due riceve un impulso fisico di 12 frame tramite i suoi veri 64 attuatori; il resto evolve nel vero solver fino a 384 frame. La sequenza dei comandi del cortex differisce eventualmente DOPO che i corpi si differenziano: questo laboratorio valuta l'effetto fisico **totale** dell'intervento, incluso il feedback del controllore baseline, non l'effetto meccanico isolato a comandi futuri congelati.

| Partizione | Pose iniziali | Stato precondizionato | Coppie reali |
|---|---|---|---:|
| TRAIN | 0°, 90° | 0, 1, 2 | 384 |
| CALIBRAZIONE | ±22,5° | 0, 1, 2 | 384 |
| TEST NUOVO | **±10°, ±80°** | 3 | 256 |
| **TOTALE** | | | **1.024** |

Ogni coppia genera 2 × (96+384) = 960 integrazioni fisiche; **983.040 integrazioni** complessive, più eventuali stati precondizionati. I dati del test finale non sono impiegati per pesi né per calibrazione, e sono pose non usate nei test V3.7 e V3.7-B. La penale della regressione ricorrente è fissata a 3, senza tuning sui risultati finali.

**Gate V3.8:** calibrazione supera baseline zero almeno 10% su effetti immediati, tardivi e costo. Il test nuovo riporta lo stesso gate e la differenza dell'MSE tardiva rispetto alla regressione statica 68. Se peggiora anche solo uno dei target indispensabili, NON si promuove la risposta motoria come affidabile. La validazione su sequenze autonome, molti seed, scene, vita intera ≥ 2.000 esperienze, memoria persistente, obiettivi/risorse e Galaxy S21 è **ancora assente**.

## Stato effettivo e prossima attività

Il workflow specifico è stato avviato: [runs ZERO3 3.8](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions). Registrare il numero di CI, MSE per tutte e tre le teste, la traiettoria e il risultato positivo/negativo soltanto dopo la lettura dei log conclusi. Se il risultato è negativo, non rifare i test sullo stesso blind ottimizzando le costanti; procedere su una nuova partizione preregistrata e analizzare l'origine del drift.

**G3 locomozione appresa / G5 modello del mondo generale: NON SUPERATI. UI 3.0 e OTA: NON IMPLEMENTATI.**

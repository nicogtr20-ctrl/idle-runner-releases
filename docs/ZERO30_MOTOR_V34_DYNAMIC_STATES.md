# ZERO 3.0 V3.4 — TEST DI STATO CORPOREO NON VERGINE (17/10/2026)

> **Leggere obbligatoriamente**: [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI/UX 3.0](ZERO30_UI_UX_SPEC.md) e [Risposta muscolare V3.3](ZERO30_MOTOR_V33_DELAYED_RESPONSE.md). Questo documento misura il **prototipo isolato**, non il cervello V3 attivo e non un aggiornamento OTA.

## Perché serve
La V3.3 ha predetto gli effetti di piccoli impulsi muscolari su corpi inizialmente vergini e ruotati. Su 512 coppie finali separatamente tenute fuori da training e calibrazione, 16/16 interventi hanno prodotto spostamento incrementale firmato positivo. Il solo angolo del corpo non rappresenta deformazioni, trascinamento inerziale o contatti dinamici; il risultato non autorizzava una skill motoria di produzione.

## Procedura fisica V3.4 (test non adulterato)
Codice: [Zero3MotorDynamicResponseRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorDynamicResponseRegression.java), con fixture fisiche introdotte in [Zero3MotorImpulseResponseRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorImpulseResponseRegression.java).

- Partenza da due corpi clonati **indipendentemente ma deterministici**; stessa forma iniziale, stesso campo muscolare e stesso ritmo.
- Si generano stati non vergini tramite **16, 40 o 64 frame** di contrazioni localizzate realmente integrate da `SymbioteMatterV1.step`. Non si aggiunge alcuna velocità o forza fittizia al baricentro e non esiste gait preimpostato nel cervello: sono precondizionamenti del laboratorio.
- Solo una delle due istanze riceve un impulso di 12 frame, poi entrambe evolvono per 48 frame senza ulteriori perturbazioni. La differenza delle traiettorie fisiche è la risposta attribuibile a quell'intervento nel test controllato.
- Allenamento del modello originale V3.3 limitato a corpi **vergini** ruotati a 0°/90° (256 coppie); calibrazione separata ±22,5° vergine (256 coppie). Le **768 coppie di stress** usano ±45° con tre precondizionamenti, **mai** utilizzati per adattare i pesi.
- Confronto MSE del modello e baseline «nessun effetto» per immediate, delayed e costo; selezione tramite soli output predetti e verifica a posteriori dello spostamento utile/energia.

## Cosa costituisce una regressione
Se il modello originale non batte il riferimento zero su stati già deformati o in moto, la V3.3 ha appreso un tratto limitato della forma vergine; **non va integrata come controllore 3.0**. La prova non dimostra comunque l'intelligenza: non include mondi autonomi con ostacoli, raccolta energia e trasferimento persistente della skill.

## Stato e prossimo passo
[Workflow isolato dedicato](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions) sul ramo feature ZERO3: leggere i log effettivi prima di scrivere «PASS» o pubblicare un valore. Se emerge una regressione, rendere il modello sensibile alle sensazioni vere pre-azione (velocità, distribuzione della tensione, contatto, strain, geometria 16 settori), poi ripetere train/calibration/holdout con **stati non visti**. La specifica ZERO3 prevede anche la nuova UI neurale, ma questa NON è ancora realizzata.

**Gate G3 apprendimento autonomo ed eventuale G5 generale: BLOCCATI fino a dimostrazioni con corpi, scene e seed variabili. NO OTA.**


## RISULTATI VERIFICATI — RUN 38977113632

**[GitHub Actions run 38977113632 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38977113632)** sul branch ZERO3 per compilazione JVM/solver, stress deforme e dinamico. Questo PASS conferma **l'esecuzione**, la qualità del predittore va letta dagli MSE e dagli esiti fisici sotto.

La prova mantiene il modello preaddestrato solamente su **256 coppie vergini** con posture iniziali 0°/90°, calibra su **256 coppie vergini** a ±22,5° e valuta **768 nuove coppie fisiche** (4 direzioni, ±45°, tre differenti precondizionamenti di 16/40/64 frame attivi).

| Categoria di corpo precondizionato | Coppie | MSE immediata V3.3 | Baseline zero | MSE ritardata V3.3 | Baseline zero |
|---|---:|---:|---:|---:|---:|
| 16 frame attivi | 256 | 0,00049465 | 0,00500209 | 0,00122351 | 0,01924865 |
| 40 frame attivi | 256 | 0,00058917 | 0,00504684 | 0,00136304 | 0,02256033 |
| 64 frame attivi | 256 | 0,00060719 | 0,00326789 | 0,00246424 | 0,01415535 |
| **Aggregato 768** | **768** | **0,00056367** | **0,00443894** | **0,00168360** | **0,01865478** |

Costo: MSE **0,000000000393** vs baseline zero **0,000000007762**.
La selezione basata sui soli output previsti ha riportato **24/24** impulsi migliorativi e sotto soglia di costo in 4 direzioni × 2 pose × 3 precondizionamenti: incremento firmato totale **+12,1233 U**, lavoro aggiuntivo **0,0036109**, confronto con una singola alternativa deterministica non scelta **+0,0124 U**.

**Interpretazione rigorosa:** nessuno dei nuovi stati è stato usato nel fit. Sono precondizionamenti muscolari fisici, non teletrasporti: la prova indica maggiore robustezza del modello alla deformazione/movimento rispetto alla sola rotazione, **ma usa gli stessi materiali e il medesimo mondo, senza oggetti né seed ambientali**, quindi NON è prova di locomozione autonoma o di world model generale. Non è stato misurato un benchmark 2.000 decisioni continue con questo modello connesso al cortex. Le due condizioni "moving/deformed" sono generate con impulsi locali di laboratorio, non campioni casuali di molte vite.

**Prossimo lavoro necessario:** estrarre il predittore in modulo pure Java riutilizzabile, vincolarne l'uso a calibrazione verificata, creare un test a ciclo chiuso e un protocollo di credito motorio da vere transizioni online; poi nuovi corpi, urti, energia, skill persistenti e UI 3.0. **G3 e G5 restano aperti e ZERO 2.2/OTA intatti.**

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


## ADR V3.4-B — Modello condiviso, kernel della risposta corporea e primo ciclo chiuso

**File nuovi sul ramo feature:** [Zero3MotorResponseKernel.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorResponseKernel.java), [Zero3MotorKernelContractRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorKernelContractRegression.java), [Zero3MotorClosedLoopRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorClosedLoopRegression.java).

- Il kernel pure-Java espone **42 feature biomeccaniche**, modello ridge di tre conseguenze (spostamento immediato, ritardato, differenziale costo), inferenza read-only, orientamento ricavato dalla superficie materiale, scelta di una perturbazione muscolare **locale** e applicazione a un array di **64 comandi reali**.
- **La sola predizione non abilita alcuna contrazione:** prima di proporre un impulso richiede \`calibrate()\` sui dati fisici *non* usati nel fit. L'API conserva la distinzione tra peso neurale addestrato, validazione e una *candidata*; non pretende di dimostrare ontologicamente la provenienza delle osservazioni né di costruire abilità V3 durante il gioco.
- Il nuovo test di parità usa precisamente il **modello estratto** nelle regressioni di prova precedenti, invece di mantenere una copia dell'algoritmo dentro il test. Casi invalidi e comandi fuori range sono rifiutati; nessun riferimento alle coordinate delle risorse né gait hardcoded.
- **[CI 38977676288 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38977676288):** il kernel estratto preserva i benchmark \`16/16\` su pose vergini non usate nel fit e \`24/24\` sul corpo precondizionato; 256 coppie fit, 256 calibrazione, 24 prove esplicite dei suggerimenti con **+12,12326 U** incremento firmato e **0,0036109** lavoro addizionale totale. Confermati peso **immutabile** su test e impossibilità di esecuzione senza calibrazione.

### Prova di ciclo chiuso richiesta prima dell'integrazione

Il test aggiuntivo \`Zero3MotorClosedLoopRegression\` non giudica un singolo impulso: mantiene lo **stesso corpo in movimento per 80 decisioni consecutive** su ciascuna combinazione di 4 direzioni × 2 pose × 2 condizioni iniziali = **16 esperimenti A/B**, ognuno con controllo zero-probe e suggerimenti del modello. La scelta è rifatta a ogni decisione leggendo la posa fisica corrente, 12 frame di impulso locale e 48 di assestamento per decisione. Si confrontano **spostamento firmato, distanza lorda, lavoro muscolare, positività delle finestre e topologia valida**.

**Attenzione:** il kernel è inizialmente allenato su coppie fisiche precedenti, **non impara ancora nuove competenze durante le 80 decisioni di ciascuna vita**. La direzione fissa esiste solo nel laboratorio. Anche se il pilota ha valori positivi non si deve dichiarare G3 superato: manca l'apprendimento durante una vita, memoria, generalizzazione ambientale e reale scelta autonoma di obiettivi. Nessuna implementazione GameActivity/OTA.

**Risultati del ciclo chiuso:** da aggiungere dopo il completamento e la lettura integrale del relativo log GitHub. La CI compilata non equivale al gate di apprendimento superato.


## RISULTATO CRITICO: IL PILOTA A CICLO CHIUSO NON SUPERA IL GATE

**[GitHub Actions 38977817743 — SUCCESS tecnico, FAIL qualitativo motorio](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38977817743).**

\`Zero3MotorClosedLoopRegression.java\` ha verificato 16 condizioni fisiche (4 intenzioni di laboratorio × 2 pose × 2 stati iniziali), **80 decisioni consecutive per ciascun controllo** con stesso organismo senza reset all'interno della sequenza, 60 frame/decisione. Per ogni condizione confronto baseline normale (corteccia senza perturbazione extra) vs kernel predittivo che propone un impulso locale a ogni decisione previa calibrazione. **Le 80 decisioni sono in serie, non 80 episodi indipendenti.** Il kernel NON aggiorna i suoi parametri durante il test.

| Misura aggregata, 16 condizioni | Baseline senza aggiunta | Modello guidato a ogni decisione |
|---|---:|---:|
| Somma spostamento firmato lungo intenzione (U) | **+164,8396** | **+34,8890** |
| Lavoro muscolare cumulativo | **1,24822** | **1,46557** |
| Condizioni in miglioramento entro tolleranza costo | — | **4/16** |
| Riparazioni topologiche osservate | 0 | 0 |

Esempio di regressione: \`Nord -Y, posa 0°, condizione iniziale 0\`: baseline \`-3,992 U\`, modello \`-29,763 U\`; lavoro \`0,07331\` vs \`0,08830\`. Alcune situazioni migliorano (Est +X in posa 0°, Sud +Y in posa 45°), ma la generalizzazione è **insufficiente**.

**Interpretazione obbligatoria:** il precedente 24/24 (impulsi isolati sul corpo precondizionato) NON era una dimostrazione di controllo sequenziale. La policy che riapplica il miglior impulso locale a ogni decisione provoca spesso deriva o lotta contro le altre contrazioni, pur mantenendo il corpo fisicamente valido. **Questo algoritmo NON VA COLLEGATO al gameplay.** Lo stato G3 rimane BLOCCATO, non promuovere alcuna abilità.

### Come proseguire — diagnosi concreta

1. Misurare la **coda temporale degli effetti** di una contrazione ben oltre i 48 frame di assestamento, includendo l'inerzia del corpo già in movimento. Senza questo, il predittore ignora le conseguenze residue quando è scelta la prossima azione.
2. Introdurre uno **stato motorio osservabile e un temporizzatore di credito** che non consenta di attribuire esiti non conclusi alla prossima contrazione. La latenza è un limite fisico misurabile, NON una andatura precodificata.
3. Testare esplorazione con **pochi impulsi adattivi, non spinta ogni decisione**, confrontata con baseline senza impulsi e con stessa energia. Scoprire se migliora l'allineamento senza costo netto maggiore, in pose e scene differenti.
4. Rendere il modello predittivo **state-/history-conditioned**, con velocità, strain e contatti, e apprendere dalle conseguenze effettivamente post-fisica anche tra decisioni. Non proporre patch "tutti i muscoli verso obiettivo" o attrazioni artificiali.
5. Prima della futura integrazione, dimostrare **A/B su molte vite con scelte autonome**, skills persistenti e criterio multi-seed. Nessun OTA, nessuna sostituzione delle schermate 2.2.

**Conclusione documentale:** la capacità di prevedere un impulso localizzato e quella di controllare un organismo distribuito in sequenza sono due cose diverse; la prima è supportata da dati, **la seconda oggi è smentita dal benchmark**.


## ADR V3.4-C — protezioni di attribuzione e calibrazione nel modulo condiviso

Il modulo sperimentale \`Zero3MotorResponseKernel.Predictor\` ora mantiene **identificatori episodio/decisione** separati in training e calibrazione; vieta campioni duplicati, gli stessi identificativi riutilizzati nei due gruppi e la **ricalibrazione ripetuta** della stessa istanza dopo il primo test. Non può suggerire impulsi prima dell'esito positivo della calibrazione. I parametri sono immutabili durante predizione e valutazione; nessun salvataggio/app/OTA usa ancora questa API.

**[CI 38978039795 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38978039795)** conferma la regressione contro fuga train→calibration e doppia verifica, oltre a 24/24 casi dinamici. **Questo certifica l'integrità del sistema di misure, non la locomozione.** Le tre proprietà sono comunque utili quando si introdurrà il vero meccanismo di credito online.

La nuova prova \`Zero3MotorTailLatencyRegression\` misura gli effetti residuali fino a **360 frame dopo la contrazione**, sempre con due corpi e le stesse effettive forze del solver; risultati quantitativi da riportare dal CI una volta verificati. Nessun intervento di quel laboratorio è una camminata predefinita: serve a costruire una timeline causale con latenza sufficiente e prevenire gratificazioni premature.

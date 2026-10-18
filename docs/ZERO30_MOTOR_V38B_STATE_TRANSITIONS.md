# ZERO 3.0 — V3.8-B: DINAMICA APPRESA DELLA RISPOSTA MOTORIA, 32 PASSI

**Stato:** ESPERIMENTO ISOLATO, NO OTA. **Non confondere con V3.8 iniziale**, il cui reservoir ricorrente ha fallito il gate su tutti i modelli. [Risultati precedenti V3.8](ZERO30_MOTOR_V38_RECURRENT_TRAJECTORY.md). Leggere prima [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [Spec UI V3](ZERO30_UI_UX_SPEC.md).

**Codice sperimentale:** [Zero3MotorDynamicStateV38Regression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorDynamicStateV38Regression.java). [Workflow dedicato](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/.github/workflows/zero30-motor-dynamic-v38-qa.yml). [PR #87 — ancora draft](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

## 1. Problema empirico dimostrato

Il modello a risposta muscolare isolata predice bene gli impulsi brevi, ma a ciclo chiuso su 80 decisioni peggiora spesso locomozione e lavoro. La coda causale della perturbazione resta attiva dopo 48 frame e qualche volta cambia segno. La V3.7 (memoria di 96 frame + previsione a 384) ha fallito sul blind. La V3.8 reservoir a 24 unità ha ottenuto MSE sulla traiettoria finale **0,055894499** vs **0,031732958** del controllo che predice nessun effetto; il ricorrente non ha rispettato il gate.

## 2. Ipotesi V3.8-B — identificare uno stato dinamico, non regredire direttamente sul futuro

Nuova regressione di transizione fisica a **12 frame**: ogni provino misura la risposta firmata differenziale fra due solver reali partiti dallo stesso stato. L'unico intervento differenziale è una perturbazione muscolare locale per 12 frame. Nei 96 frame precedenti, entrambi i corpi ricevono la stessa sequenza di piccole contrazioni realmente eseguite. Nessun motore di gioco è guidato da una ricompensa artificiale.

La differenza causale di posizione viene registrata ai checkpoint a 12,24,...,384 frame; si addestra un modello **autoregressivo discreto** (72 feature) che predice **l'incremento di spostamento firmato del blocco successivo**, condizionato sull'intenzione del laboratorio, lo stato e l'attuatore prima della perturbazione, il tempo e le due ultime risposte motorie. **Durante l'addestramento** utilizza gli stati reali dei checkpoint (teacher forcing). **Durante la validazione** ricostruisce tutti e 32 i checkpoint usando esclusivamente le proprie predizioni precedenti: nessuna osservazione futura entra nel rollout.

Questa è una vera *transizione temporale appresa* rispetto alla regressione statica, ma non è ancora un GRU addestrato, un world model corporeo completo né un controllore attivo: predice soltanto una **risposta firmata differenziale di laboratorio** a un singolo impulso, non l'intero stato della materia né il feedback dei successivi comandi volontari.

## 3. Esperimento preregistrato

| Gruppo | Pose iniziali | Precondizione fisica | Coppie di solver |
|---|---|---|---:|
| FIT | 0° e 90° | 0, 1, 2 | 384 |
| CAL | +22,5° e −22,5° | 0, 1, 2 | 384 |
| BLIND NUOVO | +10°, −10°, +55°, −55° | 3 | 256 |

Ogni coppia usa 96 frame fisici prima dell'impulso e 384 frame dopo, per un totale di **983.040 step del solver**, oltre al precondizionamento. Le due copie sono state controllate identiche prima del comando; i dati di validazione/holdout non aggiornano i pesi né i limiti numerici. L'intervento isolato NON viene ripetuto su una vita: non sono 2.000 esperienze autonome.

Si confrontano a **parità di input e dataset**:
- **MODELLO DINAMICO**: ridge su singoli incrementi osservati durante FIT, poi rollout libero per 32 blocchi; ridge=8, ampiezza massima dell'incremento dedotta soltanto dal train.
- **BASELINE STATICA 52D**: ridge sullo spostamento differenziale finale a 384 frame, usando soltanto sensori preazione.
- **BASELINE ZERO**: predice nessun effetto motorio.

Metrica primaria: MSE dell'effetto differenziale finale a 384 frame e MSE della **traiettoria intermedia completa** su CAL e BLIND. Requisito gate preregistrato: entrambe almeno 10% inferiori alla baseline zero, con verifica separata contro il modello statico, e non solo buona accuratezza del singolo intervallo. Nessun tuning dei pesi usando i risultati BLIND. Un risultato mediocre o pessimo blocca G3 e G5.

## 4. Cosa manca per ZERO 3.0

Anche se la V3.8-B passasse, non si potrebbe ancora dire che ZERO sappia locomoversi o imparare online: manca il predittore degli **stati corporei completi con azioni future reali**, valutazione in nuovi mondi, capacità di apprendere nella stessa vita, cervello unico collegato, consolidamento e memoria persistente, neurogenesi/skills, interfaccia V3 e APK/OTA. Non integrare un laboratorio direttamente nel gameplay.

## 5. Risultati verificati

**Da compilare solo leggendo i log CI terminati.** Successo compilazione non significa successo motorio.

**Gate G3 locomozione autonoma: BLOCCATO. Gate G5 world model completo: BLOCCATO. ZERO 2.2 / OTA 172 invariati.**


## 6. RISULTATI VERIFICATI — CI 38984305222, 17 ottobre 2026

**[GitHub Actions run 38984305222 — SUCCESS di compilazione ed esecuzione](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38984305222).** Il benchmark comprende davvero **384 coppie FIT, 384 CAL, 256 BLIND su nuove pose ±10°/±55° con precondizione 3**, 32 intervalli di 12 frame e **983.040 integrazioni fisiche del solver** senza modificare il corpo con teletrasporti. Nel FIT la transizione è allenata su valori reali misurati (teacher forcing); in CAL e BLIND la rete deve ricostruire **tutti i 32 passaggi autonomamente**, senza leggere la traiettoria vera dei checkpoint futuri.

| Partizione | MSE risultato finale, zero | MSE finale, statico 52D | MSE finale, dinamico V3.8-B | MSE traiettoria, zero | MSE traiettoria, dinamico |
|---|---:|---:|---:|---:|---:|
| CAL 384 coppie | 0,036640959 | **0,030029654** | **0,124206957** | 0,031608393 | 0,031721382 |
| BLIND 256 coppie | 0,034327758 | **0,029057080** | **0,160072801** | 0,031625985 | **0,038481022** |

**Esito:** gate calibrazione = **FAIL**; gate blind = **FAIL**. Modello dinamico sul finale del blind **~450,9% peggiore del statico 52D in errore quadratico**; sulla traiettoria complessiva peggiore di zero. Il segno dello spostamento finale è corretto in **122/256** prove blind. Nessun suggerimento viene autorizzato in gameplay: **i risultati di calibrazione non rispettano la soglia preregistrata**, ancora prima del blind.

### Interpretazione tecnica, senza affermazioni eccessive

- Il test separa davvero `teacher forcing` nel solo FIT da **rollout autoregressivo libero** nei dati non visti. È sufficiente a diagnosticare che **l'attuale modello lineare discreto di risposta differenziale non è un simulatore dinamico stabile e generalizzabile**.
- Un errore piccolo sui singoli incrementi osservati **non garantisce** che l'integrazione di 32 incrementi predetti resti vicina alla fisica reale. Qui l'errore finale cresce molto e la conoscenza della sola intenzione iniziale non rappresenta le successive variazioni di comando.
- Il modello rappresenta **differenze controfattuali fra due solver di laboratorio**, non direttamente lo stato di ZERO osservabile durante una vita. Non si può convertire ingenuamente quel delta in esperienza autonoma; nemmeno incrementare soltanto il numero di campioni o di neuroni risolve l'inadeguatezza delle variabili dinamiche.
- La spiegazione `accumulo degli errori`, la dipendenza dalle azioni effettive future, elasticità/attrito/contatti e mancata modellazione 2D sono **ipotesi**, non cause provate individualmente dal test. Non ritoccare i pesi o il ridge sulle pose blind ormai consumate.

**Decisione obbligatoria:** non integrare V3.8-B nella corteccia, non unire PR #87 sul ramo publisher, niente OTA o interfaccia 3.0 fittizia. Il prossimo prototipo va progettato intorno a **transizioni realmente osservabili state(t), action(t) → state(t+1)** con modello biomeccanico multi-canale e comandi fisici eseguiti al frame, misure separate di previsione a uno e più passi e rollout su nuove scene/seed. Solo dopo tornare a locomozione autonoma e memorie procedurali.

**G3 locomozione appresa: BLOCCATO. G5 modello del mondo generale: BLOCCATO.** La versione stabile ZERO 2.2 rimane invariata.

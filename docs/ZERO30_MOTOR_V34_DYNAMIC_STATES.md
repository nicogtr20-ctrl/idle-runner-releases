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

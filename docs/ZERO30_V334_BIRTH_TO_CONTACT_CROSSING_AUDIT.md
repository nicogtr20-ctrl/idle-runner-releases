# ZERO 3.0 — V3.34 | Forense delle autointersezioni comuni di membrana

**Protocollo preregistrato il 17 ottobre 2026, prima del codice dell'audit.** Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.33](ZERO30_V333_LOCAL_DELAY_BILATERAL_ABLATION.md), [Spec UI](ZERO30_UI_UX_SPEC.md).

## Problema osservato
V3.33 BLIND BOX_NEAR ordinal=645, TRAIN ordinal=337: **tutti i cinque cervelli** rilevano `MaterialGeometry.selfCrossings(skin)=1` dopo il primo sottopasso (BLIND frame0, TRAIN frame1), nonostante `gel.invalid=0`. Prima di spiegare il fenomeno con apprendimento/neuroni, verificare lo stato dell'intero percorso di **nascita, pose anatomiche, precondizionamento, 96 frame di warmup, collocazione BOX, primo vero comando cortex/head, integrazione gel, composizione/collisione fisica**. Non riciclare questa batteria come holdout per migliorare una policy.

## Prove congelate
I due ordinal e quattro controlli accoppiati: 333, 337, 341 e 641, 645, 649 (tutti BOX_NEAR). Nessun intervento sui parametri di nascita o sulle pose dei precedenti BLIND; stessa `Zero3MotorImpulseResponseRegression.Scenario` e stessa formula V333 per rotazione, body/gel/warmup, gap e yaw. Ripetere due istanze della stessa condizione per controllare determinismo bitwise di crossing, coordinate e lavoro.

Reportare `selfCrossings`, `gel.invalid`, `gel.topologyRepairs`, `gel.selfContactPairs`, min distanze reali fra BOX/shell, `skin.t`, `gel.ticks`, area del contorno e coppia di edge che si incrociano al primo fallimento, in:
1. `AFTER_SCENARIO` (possibile preconditioning già applicato dal costruttore);
2. `WARMUP_BEGIN` e ad **ognuno dei 96** passi reali senza muscoli;
3. `BOX_SPAWN` e `BEFORE_MOTOR`, senza mutare geometria;
4. `AFTER_GEL` e `AFTER_WORLD` del primo vero passo (solo se siamo arrivati senza crossing preesistente).

Isolamenti causali del primo passo da mondi fisicamente **ricostruiti identici**: (A) comando reale V317 actor + V319 FROZEN trained head, BOX e collisione reale; (B) **stessi 64 output**, ma nessun oggetto presente/collisione BOX; (C) zero comandi, BOX e collisione reale; (D) stesso comando reale, BOX ma omettendo solamente `actualWorldStep` (gel puro). Questi controfattuali sono soltanto strumenti di misurazione; né brain né fisica né server OTA sono modificati. Se la membrana è già intrecciata al momento del BOX SPAWN, non attribuire causalità a BOX, cortex o grip.

**Condizione di successo audit:** test compilato con solver fisico produzione, riproduce esattamente il fallimento V333 almeno nei due ordinal incriminati, identifica l'**esatto primo stadio** e le edge pair, presenta risultati dei sei ordinal anche se nessuno riproduce, nessun cambiamento ai solver/materiali/algoritmo V332/V333 o seed. Distinguere `ROOT_STAGE_FOUND` (solo primo stadio in cui appare un crossing) da `ROOT_MECHANISM_PROVEN` (solo se l'ablation esclude gli altri stadi). Invariante: non usare la prova diagnostica per proclamare una capacità G3 appresa.

**Dopo V3.34:** se la causa è nello spawn/pre-warmup, predisporre una fixture di generazione indipendente dichiarata prima di nuovi BLIND, e diagnosticare il solver solo se evidenza con stato PRE non intrecciato. Se è nella collisione, isolare confronto BOX/no-BOX su nuova fixture, senza correzioni hardcoded di locomozione. In ogni caso restano necessari nuovi mondi e almeno 2.000 decisioni con restart matched trained-v-frozen e controllo bilaterale.

Tutto in PR #87 Draft, nessun merge/OTA, 12 salvataggi 2.2 e produzione OTA172 invariati.

## Risultati
*Da compilare solo dopo lettura dei log reali.*

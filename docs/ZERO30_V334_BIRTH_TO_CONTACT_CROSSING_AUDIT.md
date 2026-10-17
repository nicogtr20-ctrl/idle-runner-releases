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

## Risultati verificati — 17 ottobre 2026

**[CI definitiva V3.34 39042522334 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39042522334)**, job 117424699833: test fisico originale, stage-by-stage solver **e confronto bitwise della firma di coordinate shell, tick e contatori di riparazione** rispetto alla pipeline non strumentata, compilazione Android Debug **BUILD SUCCESSFUL in 1m 3s**. La prima [CI 39042284332](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39042284332) aveva già isolato BOX-vs-no-BOX, e l'aggiunta finale ha risolto lo stadio specifico senza cambiare \`SquareGripPuzzle\`/gel/sensori/neuroni.

### Esito meccanico accertato

**La prima comparsa della topologia incrociata è dentro \`SquareGripPuzzle.constrain()\`, NON durante la nascita, i 96 passi di warmup, il comando motorio del cortex o il gel.** Le due pose hanno \`crossing=0\` dopo costruttore, 96 veri passi a comando zero, BOX_SPAWN, prima della decisione e **dopo \`p.gel.step\`**. \`SquareGripPuzzle.constrain\` viene chiamato dall'effettivo \`Zero3MultiSceneRestartV322BRegression.actualWorldStep\`; i successivi \`MaterialContactConstraints.reconcile\`, \`SymbioteWorldStepV171.interior\`, \`GripSurfaceComposition.compose\` e \`Entity.tick\` NON eliminano la topologia incrociata. \`gel.invalid=0\`, \`gel.selfContactPairs=0\`.

| Identità originale V333 | Primo frame fisico | Prima coppia di segmenti incrociati | Dopo GEL | Dopo \`SquareGripPuzzle.constrain\` | Dopo resto WORLD |
|---|---:|---:|---:|---:|---:|
| TRAIN \`ordinal=337\` | frame **1** della prima decisione | **45 ↔ 47** | 0 | **1** | **1** |
| BLIND \`ordinal=645\` | frame **0** della prima decisione | **41 ↔ 43** | 0 | **1** | **1** |

**Controprove causali vere e identiche in ingresso** sulla stessa inizializzazione e primi 2 substep:
- BOX vero, comando scelto dal recurrent actor V317 e head V319 addestrata ma congelata → crossing 1 al frame incriminato in entrambi i casi.
- **Gli stessi 64 comandi fisici realmente emessi**, ma senza chiamare la collisione WORLD → **0 incroci** ai due frame.
- **Tutti i 64 comandi muscolari pari a ZERO**, collisione BOX originale → **1 incrocio** agli stessi frame e **stesse coppie di edge 45,47 / 41,43**.
- Ricalcolo della WORLD a stadi \`constrain\` → \`reconcile\` → \`interior\` → \`compose\` → \`tick\` produce **identiche coordinate dei 96 punti della membrana, tick e contatori delle riparazioni bit per bit** rispetto all'implementazione originale e non perturba l'esito per effetto della strumentazione.

Le 4 pose BOX di controllo \`333,341,641,649\` rimangono senza incroci per i primi due substep, con autentica pressione BOX. Quindi non ogni collisione è problematica: dipende dalla geometria specifica di quelle due nascite.

**Dettaglio aggiuntivo:** la BOX è già parzialmente sovrapposta alla fascia di membrana prima del primo step (BOX shell gap firmato −9,880600 per 337, −9,875433 per 645), ma anche alcune pose controllo hanno gap negativi senza incrocio. Pertanto l'overlap iniziale è una **condizione della fixture** da considerare, NON prova che l'overlap sia l'unica causa o una giustificazione per cambiare retroattivamente l'holdout.

### Distinzione sulle cause

**\`ROOT_STAGE_FOUND=PASS\`**: il primo stadio che muta una membrana non incrociata in incrociata è **\`SquareGripPuzzle.constrain\`**. **\`ROOT_MECHANISM_IN_SUBROUTINE=NOT_YET_PROVEN\`**: quella routine esegue \`SquareCollisionSolver.constrain\`, \`resolveCrossingLinks\`, tether/stabilizzazione di prese pregresse, \`pushOut\` e \`SquareCollisionSolver.finish\`. È necessario isolarne ogni sottooperazione o verificarne gli effetti su coppie di membrane, prima di proporre una correzione fisica. Il codice attuale ha infatti logiche di non-penetrazione e continuità di presa che NON si possono aggirare con una scorciatoia di locomozione o cancellando i contatti.

\`SymbioteMatterV1.step\` ha già una riparazione dei contorni incrociati all'avvio del **passo successivo**; in 645 i contatori mostrano \`topologyRepairs=1\` al frame1, ma la collisione della BOX reintroduce **subito** l'incrocio dei segmenti 41 e 43. Questo può spiegare perché il controllo \`gel.invalid\` non intercetta la posizione finale dopo WORLD; non è una prova di difetto del sistema di apprendimento.

### Cosa NON è stato modificato

Solo nuovi \`Zero3BoxCrossingForensicV334Regression.java\`, \`.github/workflows/zero30-v334-crossing-forensics-qa.yml\` nel ramo Draft sperimentale; nessun cambiamento a \`SquareGripPuzzle.java\`, \`SquareCollisionSolver.java\`, \`MaterialContactConstraints.java\`, \`SymbioteMatterV1.java\`, \`Zero3RuntimeV325.java\`, V3.33 learning policy, fixture BLIND storica, produzione APK, OTA o salvataggi 2.2.

### Passaggio obbligatorio V3.35

1. **Audit subroutine** delle quattro operazioni interne di \`SquareGripPuzzle.constrain\`: snapshot diagnostici prima/dopo \`SquareCollisionSolver.constrain\`, \`resolveCrossingLinks\`, \`pushOut\`, \`finish\` con coppia topologica e grip/velocity ownership; non interrompere il contatto, non inventare ricompense.
2. Proporre eventuale **protezione topologica locale nel solver** soltanto dopo aver identificato il primo sottointervento e con regressioni grip V319 + mondo multi-oggetto, impossibilità penetrazione parete, fisica normale FREE/DISK, 2.000 step gel e costo energetico.
3. Progettare nuova batteria BLIND (non manipolare i set V332/V333 consumati) per G3: locomozione positiva anche nei BOX nuovi, 6/6 vite valide, bilaterale autentica, matched trained vs frozen e restart stessa vita/2.000+ decisioni. Qualsiasi fail resta esplicito.

**Gate del progetto:** V334 **PASS diagnostico**, G3 **BLOCKED**, G5 **BLOCKED**, PR #87 ancora Draft, no merge/OTA; release \`0.11.6-ota.172\`, dodici salvataggi 2.2 invariati.


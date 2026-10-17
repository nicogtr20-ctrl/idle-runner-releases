# ZERO 3.0 — V3.41 | Risultati: coerenza locale NON migliora locomozione né presa

**18 ottobre 2026 — esperimento V3.41 concluso, G3 FAIL.** Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [protocollo V341 preregistrato](ZERO30_V341_PROPRIOCEPTIVE_COHERENCE_PROTOCOL.md), [V340](ZERO30_V340_BILATERAL_CAUSAL_RESULTS.md). Evidenza tecnica verificata: [GitHub Actions 39066678635 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066678635), commit sperimentale `5dcc3e1750e8884746c188adce4249d7c2335bc2`. Un primo compile/test run [39066495067](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066495067) è fallito, poi gli ultimi due run successivi [39066536193](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066536193) e [39066678635](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066678635) hanno verificato i risultati. **Green CI è tecnico, il gate di apprendimento resta negativo.**

## Protocollo realizzato

Bracci indipendenti `OLD_GLOBAL`, `FULL_LOCAL_DELAY`, `FROZEN`, `LOCAL_COHERENCE`, ciascuno con veri propri feedback sensoriali, actor ricorrente V317, cortex, 64 muscoli, 96 patch, gel e fisica BOX+solver V338 post WORLD. La testa adesiva V319 pretrained identica è congelata. L'unica differenza nell'apprendimento del candidato è una piccola penalità `0.12*clamp(||R_s - 0.5(R_prev+R_next)|| / (4+||R_s||),0,1)` sull'evidenza locale fisica, dove `R_s` rappresenta il reale movimento relativo al centro delle 6 patch di ciascun settore. Nessuna presa programmata, posizionamento prescritto, bonus sulla BOX o modifica diretta del corpo.

- Nuove TRAIN 1240..1271: **32×96=3.072 decisioni/arm**, 36.864 real-world substep/arm (12.288 decisioni per tutti i bracci); aggiornamenti OLD=3071, FULL=2272, FROZEN=0, COHERENCE=2272; **589.824 osservazioni di penalità** e somma penalità 183.42107270 nel solo COHERENCE; il FULL standard ha penalità 0.
- Nuove BLIND 1320..1343: **24×96=2.304 decisioni/arm**, **27.648 veri substep/arm**, 6 vite per ciascuna delle quattro famiglie, 4/4 famiglie fisicamente complete senza censura, 0 plasticità nel BLIND. Gli ordinal TRAIN e BLIND sono ora **consumati** e non valgono più come nuovi test ciechi.
- Controlli reali `MultiObjectGripRegression`, `SquareCollisionSolver`, `SquareGripPuzzle` PASS, Android `assembleDebug BUILD SUCCESSFUL in 1m 6s`. Non è stato firmato/pubblicato alcun APK.
- In BLIND `jointAttempts=0 jointRepaired=0 jointUnresolved=0`: non c'era necessità di riparare crossing nell'insieme di scene. Questo non testa l'efficacia del repair in un difetto attivo; la validazione meccanica precedente rimane separata.

## BLIND — dati fisici verificati, non soltanto punteggi

`signed` = spostamento del centro vero proiettato nella direzione appena decisa dall'attore, **non** distanza percorsa verso risorse. `RAW work` = consumo non normalizzato, gate <=110% di OLD, FROZEN e FULL; `costNorm=work/(1+max(0,signed))` è solo diagnosi, non può sostituirlo. Ogni riga ha 576 decisioni e 6912 frame fisici.

| Ambiente | signed OLD | signed FULL | signed FROZEN | signed COHERENCE | efficienza FULL→COHERENCE | RAW work OLD | RAW work FULL | RAW work FROZEN | RAW work COHERENCE |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| FREE | −4.9335 | +33.6513 | +1.6822 | +32.4331 | 0.338532→0.323124 | 0.114194 | 0.119440 | 0.112133 | 0.120376 |
| BOX_NEAR | +0.4323 | +28.9112 | −2.1364 | +30.2372 | 0.214192→0.223519 | 0.185243 | 0.224599 | 0.187830 | 0.226212 |
| DISK_NEAR | +23.3498 | +89.7996 | +24.0723 | +88.8016 | 0.544678→0.534032 | 0.131058 | 0.162922 | 0.132933 | 0.164934 |
| BOX_FAR | −5.0397 | +32.3207 | −0.6017 | +31.2210 | 0.289052→0.274647 | 0.115028 | 0.125383 | 0.113737 | 0.126741 |

**Gait gate >=10% migliore di tutti e tre i riferimenti: 0/4 famiglie.** FULL standard è superiore al nuovo candidato in FREE, DISK_NEAR, BOX_FAR, mentre COHERENCE ha un piccolo vantaggio in BOX_NEAR ma non raggiunge +10% di efficienza richiesta.

**RAW work entro +10% rispetto a OLD, FULL e FROZEN: solo FREE (1/4 famiglie).** BOX_NEAR FULL 0.224599 / COHERENCE 0.226212; ma OLD 0.185243 e FROZEN 0.187830 rendono chiaramente non soddisfatta la soglia. DISK_NEAR e BOX_FAR anch'esse FAIL per lavoro RAW. Le penalità di coerenza NON hanno ridotto il costo reale; in tutte e quattro le famiglie il lavoro di COHERENCE supera quello di FULL.

### Manipolazione: progressi geometrici ma zero bilaterale

| BOX_NEAR nei 6912 real WORLD frames/arm | OLD | FULL | FROZEN | COHERENCE |
|---|---:|---:|---:|---:|
| Contatto geometrico su facce opposte | 0 | 1033 | 0 | **1026** |
| Presa bilaterale REAL BOX | **0** | **0** | **0** | **0** |
| Frame di grip generico | 6900 | 6900 | 6900 | 6900 |

Nel nuovo gruppo di nascite, diversamente dalla V340, **anche il FULL ha raggiunto facce opposte in 1033 frame**. Pertanto l'assenza di bilateral non dipende più sempre dalla mancanza di copertura simultanea: **esiste una seconda barriera, nel passaggio geometria→comando adesione→ownership→bilaterale autentica**, da misurare con gli stessi osservatori read-only della V340. NON è dimostrato che la penalità introdotta sia in grado di correggerla. Poiché tutti e tre i riferimenti hanno 0 frame bilaterali nel BLIND V341, il confronto di *conservazione* bilateral con un reference valido è **NOT_TESTED**; il candidato comunque fallisce il criterio pre-registrato di bilateral positiva `c.bilateral>0`. Non presentare 0 contro 0 come PASS.

Il risultato stampato nel benchmark completo è: `V341 SCIENCE_FINAL G3=BLOCKED_FAIL physicalFamilies=4/4 directionalWins=0/4 RAW_WORK_FAMILIES=1/4 directionalPlusRAWwins=0/4 BOX_positiveBetter=true BOX_trueBilateral=false BOX_geometry=1026 COHERENT_bilateral=0 FULL_bilateral=0 OLD_bilateral=0 FROZEN_bilateral=0 trueGrip=true BLIND_allAutonomous=true realTrainDecisionsAll=12288 coherencePenalty=183.42107270 jointAttempts=0 jointRepaired=0 jointUnresolved=0 G5_BLOCKED NO_OTA NO_MERGE`.

## Conseguenze per il prossimo sviluppo

Il V341 ha **falsificato l'ipotesi** che una piccola penalità locale di discontinuità tra settori migliori insieme camminata, energia e presa. **Non** alzare il peso, scegliere seed favorevoli, o cambiare il reward nello stesso holdout già consumato. Per il prossimo V342:
1. **Audit sulla transizione contatto→comando→timer→bilaterale** nel nuovo contesto V341 (scene BLIND ormai consumate) sui frame con `geometryOpp=true` ma `bilateralHeldNow=false`, identificando quanto sia carenza di comando, durata, reale ownership, distanza nell'anello o dinamica propriocezione. Un secondo audit può separare proiezione HEAD adesiva e output muscolari effettivi, senza modificarli.
2. Bloccare **nuovi** TRAIN/BLIND multi-famiglia prima di cambiare l'architettura. Modificare **una sola** proprietà dopo vera diagnosi, mantenere 0 reward e grip artificiali, confrontare feedback chiusi autonomi, lavoro RAW <=110%, sensori e true BOX ownership.
3. Full-world checkpoint/restart (attore, gel/body, BOX, head, cortex, RNG, physical journal) + prova di almeno 2000 decisioni effettive nella **stessa** vita, non sommate fra vite; il cortex checkpoint soltanto era già PASS ma insufficiente. Nessuna pubblicazione finché i gate non siano completi.

**G3/G5 ancora BLOCKED. PR #87 DRAFT, NON merged, NO APK/OTA, OTA172 e 12 cervelli 2.2 invariati, nessun Galaxy S21 live test.**

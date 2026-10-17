# ZERO 3.0 — V3.41 | Coerenza propriocettiva: 4 cervelli reali, scientific FAIL

**17 ottobre 2026 — risultato definitivo**. Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.40](ZERO30_V340_BILATERAL_CAUSAL_RESULTS.md), [protocollo V3.41 preregistrato](ZERO30_V341_PROPRIOCEPTIVE_COHERENCE_PROTOCOL.md). **[GitHub Actions 39066678635, SUCCESS tecnico / FAIL SCIENTIFICO](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066678635)**, head SHA `5dcc3e1750e8884746c188adce4249d7c2335bc2`. PR #87 è DRAFT non merged; NESSUN APK/OTA/TEST Galaxy S21, stable OTA172 e 12 legacy brain 2.2 non toccati. G3/G5 BLOCCATI.

## Implementazione effettiva

Nuova classe sperimentale `Zero3CoherenceAblationV341Regression` derivata dalla V339: confronto di **quattro** organismi con corpo/gel, vero mondo, proprio V317 actor ricorrente, sensori PRE/POST individuali, comandi 64-channel auto-generati e V319 adhesive head pretrained e congelata. OLD_GLOBAL, FULL_LOCAL_DELAY, FROZEN, LOCAL_COHERENCE. V338 joint solver invocato alla fine del WORLD. **Non sono copiati i comandi BASE**. L'unico intervento è nel segnale di update V333 in TRAIN della LOCAL_COHERENCE: dati i vettori locali osservati `R_s=(sum6(postSkin-preSkin-dCOM)/6)`, penalità `0.12 × clamp(norm(R_s − (R_prev+R_next)/2)/(4+norm(R_s)),0,1)`, sottratta al credito di ogni settore per ogni vero frame; **nessun motor override, nessuna azione o grip esplicito, nessuna ricompensa BOX**. Nessun cambiamento al learning rate, collisioni e vero body. Il quarto braccio ha raccolto **589824** valutazioni di coerenza, somma penalità **183.42107270**, dimostrando che la modifica è veramente attiva; FULL/OLD/FROZEN hanno zero conteggi.

Nuovi TRAIN **1240..1271**, 32×96=**3072 decisioni per arm** e 12 solver step per decisione, totale TRAIN 12288 decisioni across 4. Synaptic updates OLD **3071**, FULL **2272**, FROZEN **0**, LOCAL_COHERENCE **2272**; chiamate credito locale FULL=3040, COHERENCE=3040. Nuovi BLIND **1320..1343**, 24×96=**2304 vere decisioni per arm** = 27648 genuine world substeps per arm, 4 famiglie con 6×96 decisioni per cella. Tutti completati **0 censura, 0 invalid, 0 attraversamento/crossing post-WORLD**, 0 updates di cortex in BLIND e 0 joint attempts/repairs/unresolved (nessun intervento necessario nelle pose osservate). Vite TRAIN/BLIND V341 da ora **CONSUMATE**, vietato tuning e nuova promozione usando lo stesso holdout.

Due CI:
- [39066536193](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066536193): SUCCESS tecnico, ma la versione preliminare del reporter di scienza non richiedeva ancora `rawFamilies==4`.
- [39066678635](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39066678635): CI finale corretta (solo **boolean raw-work gate**, commit 5dcc3e... prima di leggere qualsiasi metrica BLIND), stesse azioni/ambiente/parametri e numeri identici. **`G3=BLOCKED_FAIL, physicalFamilies=4/4, directionalWins=0/4, RAW_WORK_FAMILIES=1/4, directionalPlusRAWwins=0/4, BOX_positiveBetter=true, BOX_trueBilateral=false, BOX_geometry=1026, COHERENT_bilateral=0`.** Original `MultiObjectGripRegression` 3 BOX+gate, ordine e 7 maschere PASS; Android `gradle assembleDebug` **BUILD SUCCESSFUL in 1m 6s** ma nessun APK pubblicato.

## Dati finali reali BLIND (6 vite, 576 decisioni, 6912 passi fisici per cella)

### Progresso fisico firmato verso intenzione autonoma, NON verso gemma

| Famiglia | OLD signed | FROZEN signed | FULL signed | LOCAL_COHERENCE signed | FULL efficienza | COHERENCE efficienza |
|---|---:|---:|---:|---:|---:|---:|
| FREE | −4.933545 | +1.682212 | **+33.651293** | +32.433116 | 0.338532 | **0.323124** |
| BOX_NEAR | +0.432324 | −2.136421 | +28.911184 | **+30.237158** | 0.214192 | **0.223519** |
| DISK_NEAR | +23.349819 | +24.072302 | **+89.799621** | +88.801584 | 0.544678 | **0.534032** |
| BOX_FAR | −5.039685 | −0.601659 | **+32.320708** | +31.221040 | 0.289052 | **0.274647** |

Il nuovo braccio non batte FULL di +10% in **nessuna famiglia**: persino in BOX_NEAR dove signed migliora di +1.325975 rispetto a FULL, efficienza cresce solo da 0.214192 a 0.223519 (+4.35% circa), sotto il gate prereg. Nei restanti tre mondi FULL è migliore. **0/4 vittorie direzionali** rispetto ai migliori OLD/FROZEN/FULL. Positive signed non equivale a true target-seeking o apprendimento generalizzabile.

### Lavoro RAW del gel: veto indipendente e NON normalizzato

| Famiglia | OLD RAW | FROZEN RAW | FULL RAW | COHERENCE RAW | COHERENCE vs OLD | COHERENCE vs FROZEN | <=+10% su tutti e 3 |
|---|---:|---:|---:|---:|---:|---:|---|
| FREE | 0.114194327 | 0.112132824 | 0.119439677 | **0.120376029** | +5.41% | +7.35% | PASS |
| BOX_NEAR | 0.185242616 | 0.187830223 | 0.224598812 | **0.226211537** | +22.12% | +20.43% | **FAIL** |
| DISK_NEAR | 0.131058199 | 0.132933044 | 0.162921977 | **0.164934185** | +25.85% | +24.07% | **FAIL** |
| BOX_FAR | 0.115028254 | 0.113736672 | 0.125383222 | **0.126740930** | +10.18% | +11.43% | **FAIL** |

**RAW_WORK_FAMILIES=1/4**; l'uso del `workPerUseful` normalizzato (ad es. BOX_NEAR FULL 0.007508857 vs COHERENCE 0.007241745) non autorizza a dichiarare PASS del vero costo, come già corretto nel V339. Per ogni famiglia il nuovo braccio spende leggermente più RAW di FULL stesso (+0.72%/+1.24%/+1.08% nelle tre scene non FREE, +0.78% in FREE), anche dove produce meno efficienza di spostamento.

### Contatto della BOX: informazione nuova ma insufficiente

BOX_NEAR: **6912** WORLD frame per braccio, **6900** frame di presa BOX generic real per OLD, FROZEN, FULL e COHERENCE, **6/6 vite con almeno una presa**, 6912 contatti. In questa NUOVA coorte, il nuovo COHERENCE ha **1026 frame di geometria simultanea su facce opposte**, FULL **1033**. **MA entrambi restano bilateral effettiva=0**, e anche OLD e FROZEN hanno bilateral=0 in queste sei nuove vite e geometria opposta=0. **Questa coorte NON fornisce un baseline positivo di manipolazione bilaterale**, dunque il gate di conservazione bilaterale va classificato **NOT_TESTED**, non una perdita dimostrata *relativa* a OLD/FROZEN. Il fatto che geometria opposta compaia ma presa bilaterale no mostra un secondo possibile collo di bottiglia fisico/temporale: comando di adesione su entrambi i lati, loro proprietari/timer BOX, distanza fra indici e sincronizzazione. La diagnosi V340 su altra coorte consumed aveva invece FULL geometry=0 e OLD/FROZEN positive: **non trasferire conclusioni numeriche fra scenari diversi**. Non modificare meccanica o inventare doppia presa per soddisfare un gate.

### Limiti non soddisfatti del master 3.0

- ZERO V341 è un esperimento di apprendimento con corpo reale, non APK autonoma pronta o architettura finale di produzione.
- 2304 decisioni BLIND sono **ripartite su 24 vite**; nessuna prova di >=2000 decisioni nella stessa vita.
- Non sono state certificate le snapshot/restart bitwise di **intero** gel/world/actor/V319+cortex/RNG nello stesso episodio. Un restore dei soli pesi non basta.
- Nessun world model G5 verificato, nessun OTA/merge, e i menu neurali 3.0 ancora da finalizzare.

## Prossimo lavoro scientifico

**V3.42, prima di nuove mutazioni al cervello:** audit SOLO DIAGNOSTICO usando le scene V341 1320..1343 ora consumate e il replay completo (tutte 24 in identico ordine) per isolare perché ci sono 1026 frame di doppio contatto geometrico senza `box.bilateralHeldNow`: osservare true commands su entrambi lati, `boxGripHold`, distanza indici, primo frame del contatto e rilascio, sforzo/stiramento. Distinguere mancanza di comando dal timer e dai vincoli di separation reali, senza cambiare la fisica o la testa V319. Lo zero bilaterale di tutti i quattro bracci in V341 richiede inoltre un protocollo nuovo con BOX_NEAR difficoltà variabile e vero reference positive, preregistrato **prima** dei futuri nuovi seed; non retunare l'attuale coorte. Studiare energia RAW tramite reward/work fisico per un solo intervento alla volta, no gait imposto. Su ramo successivo certificare l'intero organismo salvo/resume per >=2000 decisioni reali nello stesso episodio, e la UI/UX ZERO 3.0 indicata nel Master. **G3/G5=BLOCKED, PR #87 DRAFT, NO OTA.**

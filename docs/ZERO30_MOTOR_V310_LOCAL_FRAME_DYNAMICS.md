# ZERO 3.0 — V3.10: dinamica locale a un frame (protocollo preregistrato, 17/10/2026)

**Stato prima dei risultati: ESPERIMENTO E2 ISOLATO.** Leggere [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI V3](ZERO30_UI_UX_SPEC.md), [esito negativo V3.9](ZERO30_MOTOR_V39_OBSERVED_STATE_ACTION.md). Codice su PR #87, non nel gioco e non nell'OTA.

## Cosa è dimostrato dalla V3.9 e nuova ipotesi

V3.9: sul BLIND MSE XY one-step 0,002513731 vs 0,002582060 della semplice velocità costante, ma la traiettoria a 32 blocchi dà 3,116409 vs baseline zero 0,837473. Il vettore di 18 feature aggregate e la media su 12 frame dei 64 comandi perdono informazioni morfologiche e temporali; che questo spieghi il fallimento è una **ipotesi**, non prova.

La V3.10 campiona **ogni frame fisico da 0,025 s**, registra l'efferenza **64D effettivamente applicata per quel singolo frame** e una rappresentazione locale orientata nel mondo: stato 72D = 8 feature globali + per 16 settori (vettore posizione x/y e velocità x/y rispetto al corpo, 64 feature). Fonte: vera membrana e interior di SymbioteMatterV1, senza accesso a gemme, ID di condizioni, pose laboratorio, etichette obiettivo, futuro o controfattuali. Lo stato locale non è il materiale completo: grip e contatto compaiono come globali in questa prima ablation; documentare questa limitazione.

Ridge multivariato con `bias + stato corrente + 64 comandi` predice delta stato 72D e spostamento COM X/Y di **un** frame. La normalizzazione e i coefficienti derivano soltanto dal FIT. Regressioni indipendenti identicamente regolarizzate: (A) **full**, (B) **senza azioni**, (C) **senza geometria locale**. Baseline (D) velocità attuale, (E) posizione ferma; (F) persistenza stato. Non inserire una gait programmata in ZERO: le perturbazioni temporali sono **eccitazione controllata esclusiva al laboratorio**.

## Protocollo di misurazione congelato prima dell'esecuzione

- Le traiettorie hanno **96 frame di precondizionamento + 384 frame consecutivi** di simulazione reale, uno stesso corpo fisico, con impulsi settoriali variati nel tempo e comandi registrati frame per frame. Ogni traiettoria è unità indivisibile nello split per prevenire train/test leakage.
- Le condizioni FIT: 2 pose (0, 90°), 3 precondizioni (0,1,2), quattro direzioni di laboratorio, 4 canali, due zone e due polarità = **384 traiettorie**; CAL: ±22,5° con le stesse tre precondizioni e quattro direzioni, 4 canali, due zone e due polarità = **384**; BLIND: **pose 13°, −29°, 53°, −77°**, precondizione 3, quattro direzioni e 4 canali × due zone × due polarità = **256 traiettorie**. Ogni gruppo genera un proprio seed delle contrazioni dipendente dal gruppo e dalle caratteristiche, per evitare duplicazioni di tracce tra FIT/CAL/BLIND.
- Complessivamente **1.024 traiettorie e 491.520 step fisici** (contando 96+384 per ogni traiettoria). FIT 384×384 transizioni (subsampling deterministico di 1 ogni 4 frame per l'ottimizzatore), CAL 384×384, BLIND 256×384. Nessuna stessa traiettoria o stesso seed nei tre gruppi.
- **Test one-step teacher forced**: input stato pre-step reale e efferenza reale, outcome misurato DOPO solver. Primary MSE per-dimensione XY rispetto a ballistic (cvx*dt,cvy*dt) e MSE delle feature corporee rispetto alla semplice persistenza.
- **Test rollout senza stati futuri**: azioni future già registrate da un corpo reale, ma soltanto le **predizioni precedenti** come input di stato. MSE della posizione cumulativa per i primi 16, 96 e 384 frame contro una baseline immobile e una a velocità iniziale costante. Il nastro delle azioni future è noto dal dataset, non un simulatore capace di predire quali comandi sceglierà un cervello vivo.
- **Gate V3.10**: sul CAL one-step XY almeno 10% meglio di ballistic; errore one-step dello stato almeno 10% sotto persistenza; A almeno 3% meglio di entrambe B/C su XY one-step; rollout 16 e 96 frame almeno 10% meglio della migliore baseline immobile/ballistica. Gli stessi requisiti devono riuscire sul BLIND senza selezione, fit o hyperparameter tuning basati su quel test. Il rapporto a 384 frame è diagnostico, non può mascherare l'eventuale fallimento breve. **Qualsiasi requisito mancato = FAIL**, anche se la CI GitHub è verde.
- Verifica NaN/Inf, ordine snapshot before → executed actuator → true post-step; non collegare al gioco, non alterare fisica, IA di profilo, 12 slot, firma o publisher.

## Limiti dell'evidenza

Anche un eventuale PASS di questo laboratorio non equivale a camminata autonoma: **G3 e G5 restano bloccati** fino a test E3 con actor/cortex, obiettivi autonomi, nuovi mondi e seed indipendenti, memoria, energia e sopravvivenza. Le pose BLIND sono nuove rispetto ai recenti laboratori, ma **la scena fisica resta una stessa arena aperta**: non spacciare la prova per generalizzazione a nuovi mondi. I replay di test non devono essere riutilizzati come holdout vergine per un modello successivo scelto guardando i risultati.

## Risultati CI

*Da compilare solo dopo che la CI è terminata e sono stati letti i log numerici.*

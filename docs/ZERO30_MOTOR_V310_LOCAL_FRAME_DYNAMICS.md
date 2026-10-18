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

Risultati numerici disponibili qui sotto, trascritti dai log della CI completata.


## RISULTATI VERIFICATI — CI 38986306415, 17 ottobre 2026

[GitHub Actions 38986306415 — SUCCESS tecnico, gate di qualità FALLITO](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986306415). Codice: `Zero3MotorLocalFrameV310Regression.java` sul ramo `feature/zero30-foundation-g0-g3-20261016`, PR #87. La prima esecuzione [38986223583](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986223583) era stata **interrotta da un assertion di conteggio sbagliato (192 anziché 384)**, prima di calcolare qualsiasi MSE. Corretto unicamente l'assert dei campioni e il resoconto (4×2×3×4×2×2=384), senza cambiare modello, soglie, pose o regolarizzazione. La run conclusiva ha compilato tutto e completato il test numerico.

**Test realmente concluso:** 384 FIT + 384 CAL + 256 BLIND = **1.024 traiettorie da un corpo fisico** e **491.520 step del solver**, 72D stato (8 globali + 16 settori × 4 valori), 64 comandi applicati al frame, 384 frame predetti per traiettoria. Regressioni `FULL` (stato locale+globali+azioni), `NO_ACTION` (stato locale+globali) e `NO_LOCAL` (globali+azioni). Nessuna modifica al corpo o ai dati legacy. Il BLIND ha pose 13°, -29°, 53°, -77° e precondizione 3, con seed di eccitazione separati; **non sono nuovi mondi**.

| MSE, split CAL | FULL | NO_ACTION | NO_LOCAL | Riferimento |
|---|---:|---:|---:|---:|
| XY prossimo frame | 0,000000551 | 0,000000566 | **0,000000458** | ballistica 0,000000554 |
| Stato corporeo prossimo frame | 0,000101816 | 0,000117854 | **0,000086478** | persistenza 0,000099891 |
| Posizione rollout 16 frame | 0,001405880 | 0,001773422 | **0,000665166** | migliore baseline ballistica 0,001040230 |
| Posizione rollout 96 frame | 0,466468330 | 0,585176449 | **0,081123431** | migliore baseline immobile 0,123388358 |
| Posizione rollout 384 frame | 0,950722664 | 2,944320989 | 0,955712034 | migliore baseline immobile 0,769405879 |

**Calibrazione: FAIL.** La FULL migliora la ballistica one-frame soltanto di circa 0,54%, non del 10% richiesto, peggiora la persistenza sulle feature corporee, e peggiora le previsioni a 16 e 96 frame. `NO_LOCAL` migliora significativamente molti confronti, ma il gate del **modello predesignato FULL** non può essere cambiato dopo aver letto i risultati.

| MSE, split BLIND | FULL | NO_ACTION | NO_LOCAL | Riferimento |
|---|---:|---:|---:|---:|
| XY prossimo frame | **0,000001303** | 0,000001234 | **0,000000490** | ballistica 0,000000519 |
| Stato corporeo prossimo frame | 0,000263959 | 0,000239035 | **0,000098983** | persistenza 0,000100804 |
| Posizione rollout 16 frame | 0,006535769 | 0,007084495 | **0,000716411** | migliore baseline ballistica 0,000851196 |
| Posizione rollout 96 frame | 2,813636208 | 3,222549192 | **0,095153510** | migliore baseline immobile 0,085788830 |
| Posizione rollout 384 frame | 4,207230556 | 13,782333849 | 1,052036940 | migliore baseline immobile 0,571044539 |

**BLIND: FAIL.** Il modello FULL sbaglia lo spostamento del frame **circa 2,51 volte più della ballistica**, e il proprio rollout 96 frame è **circa 32,8 volte più impreciso della previsione immobile**. Il modello più piccolo `NO_LOCAL` ottiene MSE one-frame 0,000000490 (meglio della ballistica 0,000000519), ma **non raggiunge la soglia primaria del 10%**, e a 96 frame non batte la baseline migliore (immobile). Il vantaggio di `NO_LOCAL` emerge soltanto come **risultato diagnostico**: nessun modello è stato autorizzato nel gioco. Anche in calibrazione l'ablation `NO_LOCAL` batteva FULL; il test blind conferma un'indicazione, non autorizza il riuso dello stesso blind come nuova selezione.

### Diagnosi verificata vs ipotesi

1. **Verificato:** la registrazione di stato anatomico settoriale, 64 efferenze *pre-solver* e stato *post-solver* a ogni frame funziona su centinaia di migliaia di veri step fisici; non è un semplice contatore di tentativi.
2. **Verificato:** 16×4 dati locali aggiunti alla regressione lineare RIDGE peggiorano la generalizzazione rispetto alla variante global-only. Non equivale a dimostrare che i sensori locali non siano utili per una rete nonlinear o ricorrente.
3. **Verificato:** il drift long-horizon persiste: perfino il global-only che migliora alcuni errori brevi non batte la baseline zero sui 96 frame blind.
4. **Ipotesi NON provate individualmente:** multicollinearità delle feature settoriali, simmetria/rotazione non trattata, dinamica non lineare, saturazioni e contatti, variabili latenti fluide non osservate, errore del modello integrato e overfitting a pose FIT/CAL.
5. **Limite decisivo:** valutazione con nastro delle azioni **realmente registrate**, noto in anticipo all'esperimento; il modello non sa generare i futuri comandi volontari né risolvere il problema della locomozione autonoma in loop chiuso.

### Decisione di progetto

**V3.10 = IMPLEMENTAZIONE E2 VERIFICATA, VALIDAZIONE QUALITÀ RESPINTA (CAL FAIL / BLIND FAIL). G3 e G5 rimangono BLOCCATI. NO MERGE della PR #87 verso publisher, NO APK/OTA V3.** Nessun valore fittizio di neuron/skill, nessun reset del 2.2.

Prima di una V3.11 è necessario progettare una rappresentazione morfologica **equivariante alle rotazioni**, un modello più compatto / induttivo dei settori condivisi e un predittore di movimento condizionato su velocità e contatti, separando regime di adesione/no-grip e fenomeni meccanici. Valutare su **nuovi seed e ambienti** con vera dinamica di contatto, e non riciclare le pose blind V3.10 per tuning; progettare un test breve autonomo E3 soltanto dopo un buon gate E2. L'architettura V3 unificata e la UI restano obiettivi contrattuali **non implementati**.

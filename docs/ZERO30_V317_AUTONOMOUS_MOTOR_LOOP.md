# ZERO 3.0 — V3.17 | Primo loop sensori → intenzione scelta → muscoli → fisica → apprendimento

**Protocollo fissato il 17/10/2026 prima del test.** Leggere [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.16](ZERO30_V316_INTENT_PERSISTENCE.md). Esperimento **E3 preliminare** su PR #87: non sostituisce G3, G4, G5, nessuna integrazione Android o OTA.

## Ipotesi falsificabile

La V3.16 ha registrato 2.048 esperienze reali **pilotando** le intenzioni. Qui per la prima volta si vuole misurare un circuito con **intenzione decisa dal proprio stato sensoriale/ricorrente** e una corteccia che possa aggiornare i pesi solo sulla base di conseguenze vere. Il risultato sperato è una migliore realizzazione delle intenzioni **scelte dal controller**, non una andatura prescritta. Nessuna gemma, reward sintetico, direzione verso oggetto né spinta al COM. Gli input tattili derivano dai solver autentici; le azioni applicate solo dal gel.

## Architettura e limiti definiti prima dei risultati

- Un nuovo `Zero3AutonomousLoopV317` su Java puro. `Zero3SensoryFrameV315` (96×12+16+48+74×7; già testato), `Zero3MotorIntentV316.Authority` unica (una decisione per frame alto). Campionamento propriocezione **una volta per decisione**, non ogni sottopasso; reale `SymbioteMatterV1.step` a 0,025 s e `SquareGripPuzzle/MaterialContact/GripComposition/Morphology.collide` in ciascuno dei 12 sottopassi della decisione. 12×0,025 = **0,30 s**. Cortical outputs 16×4 continui senza sine/oscillatore/gait hardcoded.
- Encoder ricorrente leggero a **16 unità tanh** con 20 input fisici normalizzati (dalla membrana/propriocezione; senza segreti), con readout predittivo su velocità/touch del **prossimo** frame. Allenamento con gradiente locale troncato a un passo, clipping, **solo sulle esperienze completate TRAIN**; non leggere mai `post` durante il decision. Il successo di questa testa si misura con MSE su ambienti indipendenti rispetto alla persistenza.
- Actor singolo seleziona un vettore continuo (o intenzione zero se ritiene opportuno) come funzione del proprio stato ricorrente + osservazioni correnti, con esplorazione temporale filtrata da seed proprio; **niente direzione target e niente fase ritmica**. L'actor in V3.17 è una *politica sensoriale iniziale fissa* con memoria ricorrente che cambia tramite training dell'encoder: **non è ancora un actor–critic con target networks, survival reward o obiettivi appresi**. La corteccia `AdaptiveCortex` 16×4 del progetto, trainabile con il suo `MotorIntent`, riceve feedback fisico una volta per decisione e aggiorna soltanto TRAIN. Baseline congelata: stessi seed/scenari ed esplorazione senza updates del cervello/corteccia.
- NO_HELPER_GAIT: nessuna sinusoide, 8 heading forzati, centraggio artificiale, impulso COM o `target.x/y`. `MotorIntent` deve risultare da una sola `Authority`. Output ausiliari/esperienze sempre tracciabili `episodeId, decisionId, revision, begin→action→physics→observe→train`.
- Conservare il modello dopo più episodi TRAIN (test memoria **tra vite**); valutare su **episodi nuovi** con parametri congelati usando copie del cervello/corteccia. Nessun valore di validation entra nei pesi o nella scelta iperparametri; holdout usato una volta.

## Split, scala e metriche

- TRAIN: **16 episodi × 128 decisioni autonome = 2.048 intenzioni realmente scelte** dall'actor sensorimotor, ognuna dura **12 integrazioni fisiche** (24.576 passi del gel) dopo 96 warmup gel/episodio (1.536 passi): **26.112 passi** totale TRAIN. Quattro famiglie FREE, BOX vicino, DISK vicino, BOX lontano, ripetute con pose/seed vari. Bloccare invalid/NaN.
- EVAL (BLIND): **8 episodi × 96 decisioni = 768 decisioni** per sistema, modello TRAIN congelato e baseline congelata con stessa init, seed, scenario, posa e durata. Totale ~9.216 passi gel per braccio, warmup aggiuntivo. EVAL **senza gradienti, senza replay in training**.
- Misurare `trainedCortexUpdates`, norma differenza pesi, `encoderUpdates`, previsione prossimo stato (velocity+contact), `motorIntentAgreement` condizionato solo sulle vere direzioni con sforzo, `net/gross`, somma lavoro, grip veri, `censored/invalid`, tentativi con controlli equivalenti, raw dx/dy fisici, primo/ultimo segmenti training; riportare ogni famiglia di mondo. NESSUNA energia/danno inventata: in E2 risultano mancanti.
- **Gate scientifico preliminare per dichiarare un vantaggio motorio**: almeno 2.048 decisioni TRAIN senza duplicazioni, `encoderUpdates>0`, `cortexUpdates>0` e parametri cambiati realmente; BLIND migliora almeno **10%** score di allineamento/intenzione rispetto alla baseline, non peggiora MSE predittiva rispetto alla persistenza, lavoro per unità di avanzamento utile non peggiore, e supera almeno **3 su 4** famiglie di scene. Se qualsiasi condizione manca = **FAIL**, anche con CI verde. Il credito motorio non è survival reward. `G3` resta comunque bloccato finché non si dimostrano apprendimento/retention robuste 20+ seed, nuova topologia e costi energetici, vera gestione di pause/death e abilità.
- **Invarianti**: no leakage futuro, zero training in BLIND, outcome dal solver reale, zero robot steering/com-tricks, no stato 2.2 mutato, prove su immutabilità/autorità e equivalenza per più velocità ancora pending. La V3.17 NON è una prova di coscienza, di goal learning, di memory procedurale o di neurogenesi.

## RISULTATI VERIFICATI

Risultati verificati nei log CI riportati nella sezione seguente.


## RISULTATI VERIFICATI — CI DEFINITIVA 39015868260 (17/10/2026)

**[GitHub Actions 39015868260](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39015868260) SUCCESS TECNICO, ma `scientificMotorGate=FAIL`.** Compilazione reale del codice di produzione per il gel e sensori; nuovi file sperimentali `Zero3AutonomousLoopV317.java`, `Zero3AutonomousLoopV317Regression.java` e `.github/workflows/zero30-v317-autonomous-motor-qa.yml`. Nessuna modifica al solver fisico, nessun codice Android aggiornato. Prima CI completa 39015745094, seguita da run diagnostico finale 39015868260 con sola telemetria supplementare delle soglie di presa, **senza cambiare pesi iniziali, iperparametri, split, scene, motori né learning**. Il BLIND è **consumato**: non usarlo per tuning successivo.

### Il circuito effettivamente realizzato

**PRE fisico** da `Zero3SensoryFrameV315.capture` → encoder ricorrente tanh (16 stati, 20 feature fisiche, readout predittivo allenato con esiti reali) → singolo actor sensorimotor con readout iniziale fisso → `Zero3MotorIntentV316.Authority.issue` una sola volta per decisione → `Zero3LearningFoundation.AdaptiveCortex` 64D continui → **12 passi consecutivi del solver originale** 0,025s, incluse vere collisioni/scatole/dischi → POST reale → credito una volta con `Zero3LearningFoundation.Coordinator.finish()` e apprendimento locale ricorrente. Intervallo di intenzione **0,30 s di tempo simulato**, senza sinusoidi, impulso al COM, bussola verso la gemma, label dell'oggetto o reward inventati.

**Limite importante:** la policy del singolo actor usa un readout iniziale fisso, la sua selezione evolve solo indirettamente se cambia la rappresentazione ricorrente. Non è ancora un actor–critic addestrato e non impara preferenze biologiche di sopravvivenza: il laboratorio E2 non fornisce energia/danno. Il recurrent encoder non è una GRU completa, e il credito motorio ottimizza la realizzazione dell'intenzione senza provare survival intelligence. **Nessun checkpoint/replay V3.16 del cervello ricorrente** è stato collegato: il journal V3.16 resta una infrastruttura separata, non si può dichiarare che pesi/optimizer sopravvivano a un riavvio.

### Dataset / aggiornamenti veri

| Proprietà | Misura |
|---|---:|
| TRAIN | **16 vite × 128 intenzioni scelte = 2.048** decisioni dell'actor |
| Passi fisici all'interno delle intenzioni TRAIN | **24.576** (+ warmup e precondizionamento iniziale) |
| Updates vera corteccia motoria | **2.048** |
| Updates readout/encoder ricorrente | **2.048** |
| Norma L2 modifica pesi corteccia | **0,0142741966** |
| Norma L2 update encoder riportata | **0,0367080023** |
| BLIND | **8 vite × 96 intenzioni = 768** per braccio, A/B matched pose/seed/scene |
| Fisica realmente eseguita BLIND | **9.216** passi gel/braccio, **18.432** complessivi, oltre ai warmup |
| Conteggio direzioni non-zero selezionate | TRAIN **2.048**, BLIND **768** per braccio |
| Updates su BLIND | **0**: congelate entrambe le reti |
| Scenari | corpo libero, scatola vicina, disco vicino, scatola lontana |

I due bracci partono da pesi iniziali uguali e valutano esperienze in nuove condizioni; solo un braccio ha avuto gradienti sulle 16 vite TRAIN. Il controller non riceve una direzione verso gli oggetti né forze speciali. La prova conferma una selezione autonoma **sensor-driven** nell'ambito del laboratorio, non la formazione di una competenza motoria efficace.

### BLIND: risultati motori, predittivi e costi

| Metrica | Actor + corteccia addestrati | Controller congelato | Interpretazione |
|---|---:|---:|---|
| Allineamento medio movimento/intenzione (maggiore è meglio) | **−0,01577877** | −0,00179231 | Il modello addestrato **è peggiore** |
| Distanza netta sommata dei movimenti, senza obiettivo (unità) | 86,57924 | 72,82631 | **NON** equivale a riuscire nelle intenzioni |
| Lavoro fisico cumulato | 0,13595073 | 0,13441053 | Maggiore lavoro nel modello addestrato |
| Progresso positivo direzionale aggregato (in intenzioni auto-scelte) | 18,95943 | 18,62008 | Piccola differenza, NON controllo superiore |
| Lavoro/(1+progresso utile) | 0,00681135 | 0,00685066 | Leggermente inferiore, ma metriche motorie fallite |
| MSE predizione next-state (minore è meglio) | **0,008395506** | 0,015635454 | L'encoder predice meglio del non allenato |
| MSE baseline persistenza sullo stesso BLIND | **0,000139328** | 0,000138838 | **Entrambi i predittori sono peggiori della persistenza**, quindi non è un world model affidabile |
| Famiglie che migliorano allineamento ≥10% | **1 su 4** | — | Gate richiesto 3 su 4 → FAIL |

**Allineamento per famiglia (addestrato vs congelato)**:

- FREE: −0,03072672 vs −0,00968751 → FAIL.
- BOX vicino: +0,02645406 vs +0,02393498 → PASS solo in questa famiglia.
- DISK: −0,01225083 vs +0,00391542 → FAIL.
- BOX lontano: −0,04659162 vs −0,02533213 → FAIL.

### Root cause diagnostica della presa, verificata senza riscrivere BLIND

Il run finale ha aggiunto una metrica di telemetria *soltanto osservativa* durante gli stessi comandi e mondi. **Contatti fisici numerosi, presa nulla**:

| Metrica | TRAIN | BLIND addestrato | BLIND congelato |
|---|---:|---:|---:|
| Eventi locali di pressione/tatto nei 12 sottopassi | 101.980 | 50.021 | 48.834 |
| Eventi locali con `gripHold` reale | **0** | **0** | **0** |
| Comandi adesivi attuatoriali per settore sopra `Morphology.GRIP_THRESHOLD=0,28` | **0** | **0** | **0** |
| Patch con comando adesivo sopra soglia mentre esiste un contatto | **0** | **0** | **0** |

**Causa sufficiente verificata:** nel circuito V3.17 il comando adesivo `cortex[sector*4+2]` resta SEMPRE sotto soglia; `SymbioteMatterV1.step` non può abilitare una presa reale senza quel comando. Il touch è presente, quindi non è una scarsa copertura di contatto come V3.11. La presa non va forzata a mano: la corteccia deve imparare a produrre comandi realmente sufficienti nel contesto giusto. Rimane da verificare il comportamento durante la presa dopo avere imparato il canale corretto. Non usare questi stessi test BLIND per scegliere nuove costanti.

### Gate e lettura corretta

**`learningOccurred=true`, `holdoutImproved=false`, `holdoutPredictionBeatsPersistence=false`, `costNotWorse=true`, `matchedSceneFamiliesWon=1`, `scientificMotorGate=FAIL`.** L'apprendimento dei parametri avviene, ma non è dimostrato il miglioramento della competenza motoria, non appare alcuna presa autonoma, non c'è world model migliore della baseline e non c'è retention validata dopo restart. È un progresso della **connessione causale** ed un fallimento dell'**efficacia del controllo**. Nessuna rivendicazione di G3 o G5.

### Decisione successiva secondo il Master — V3.18

Senza ripetere ancora un fitting generico dei movimenti, costruire un **test separato di controllabilità del canale adesione** con nuovi seed, nuovi ambienti e distribuzioni. Prima misurare input e ampiezza motor head, poi introdurre un readout adesivo *trainabile* e un credito locale che usa veri `gripHold` e adeguato costo, con ablation vs head congelata e falsa ricompensa vietata. Nessuna semplice assegnazione `gripCommand=0.92` fuori dal controllo neurale per far sembrare che abbia imparato. Separatamente rivedere la previsione ricorrente in forma residuale/azioni-conditioned contro persistenza; non selezionare l'architettura su BLIND V3.17. Poi vero actor–critic guidato da energia/danno reali e nuovo G3 multiseme (non solo 8 vite).

**Stato:** codice solo in PR #87 Draft, nessun merge e nessun aggiornamento APK; ZERO 3.0 non attivo nel gameplay, OTA172 e 12 cervelli 2.2 intatti. Il Master/Contratto/UI restano gli obiettivi di progetto integrale.

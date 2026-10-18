# ZERO 3.0 — V3.19 | Adesione contestuale e costo misurato del lavoro fisico

**Preregistrato 17 ottobre 2026 prima del codice e dei risultati V3.19.** Leggere nell'ordine [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.18](ZERO30_V318_LEARNED_ADHESION.md). Stato: PR #87 Draft, OTA172 non cambia.

## Fatti verificati di partenza
V3.17: adesione motoria sotto soglia, 0 vere prese. V3.18: adesione via neuroni 16×6 veramente sopra soglia e 4.600 frame con grip BLIND (2.021 bilaterali) ma attivazione inutile 45,58% fuori BOX (massimo accettazione 30%), lavoro BOX +56,9% rispetto a head congelata (massimo +20%), gate FAIL. Il `SymbioteMatterV1.step` NON cambia; il suo codice contiene **`work+=(abs(extend)+.25*abs(lateral)+adhesive*.1)*dt*.0002` per patch**, assegnando a ogni gruppo di sei patch un costo adesione esattamente quantificabile `6*adhesion*.1*dt*.0002`. Differenza di lavoro complessiva include anche conseguenze indirette e possibili diverse posture: monitorare entrambe, non chiamare la differenza totale «lavoro locale puro».

## Contratto implementativo
Nuova classe `Zero3ContextAdhesionV319`: testa neurale distinta dal vecchio head V3.18, 16 settori, 8 segnali **locali misurati prima dell'azione** (bias, preferenza adesiva dell'actor, `touchSense` BOX, `touchPulse` fisico, `gripHold` vero, raggio locale da `firstSolidDistance`, velocità radiale di avvicinamento del tessuto, accoppiamento osservato contatto-solido×vista). Nessun tipo/ID oggetto, gemma, reward o coordinate future. Sigmoid di pesi trainabili, effetto **solo su 16 uscite motorie channel2** effettivamente inviate al gel; altri 48 comandi dall'attuale `AdaptiveCortex` congelata, actor ricorrente V3.17 congelato. Divieto esplicito di gate hardcoded `if touch → grip`, di modificare contatti e `gripHold` o di spostare artificialmente il corpo. Azioni sempre associate a una `MotorIntentV316.Authority`.

Credit learning:
- Esplorazione TRAIN con perturbazioni continue del logit da RNG prima dell'azione e non durante EVAL. Credito di prese autentiche `skin.gripHold` confermate da `box.boxGripHold` proprietario nel solver, attribuito **soltanto dopo** l'intero frame di collisione.
- Penalità proporzionale alla quota esatta di adesione realmente sommata al lavoro del gel (`6*cmd*.1*.025*.0002`) su TUTTE le scene, non una funzione del nome dell'oggetto e non un «falso zero» di energia. Normalizzarla a costo massimo di comando per settore per frame per stabilità numerica, lasciando sia la quantità originale sia la normalizzazione nelle metriche. `gel.work` cumulativo vero controllato separatamente.
- Gradiente localmente troncato con clip, peso regolarizzato, baseline per settore; coefficiente fissato in protocollo. Non dare premio a `touchSense` o visione: sono SOLO ingressi di policy; evento positivo significa **vera presa**. Credit di primo contatto ottenuto da storia di efferenze reale, mai da future letture nell'input.
- Prova negativa: sensore corrente FREE/FAR non deve essere falsamente spacciato per contatto; GRIP reale su DISK/FREE/FAR proibito.

## Scala e comparazioni preregistrate
- TRAIN V3.19 32 episodi×96 decisioni=**3.072 decisioni scelte dall'actor** (12 sottopassi fisici per decisione; 36.864 passi reali più warmup), quattro famiglie FREE/BOX_NEAR/DISK/BOX_FAR; scenario stesso schema ma **pose e seed nuovi** rispetto V3.18; otto episodi per tipo. Parametri cambiano solo nel TRAIN.
- BLIND: 24 episodi×96 decisioni=**2.304 decisioni per braccio**, 27.648 veri step gel per braccio, **tre bracci** sulle stesse condizioni non viste: (A) head V3.19 addestrata, (B) head V3.18 addestrata separatamente dal suo corpus TRAIN precedente, (C) head congelata non addestrata. Sei vite per ogni tipo, con nuove pose/distanze non usate come TRAIN né da V3.18. Tutte e tre con actor e 48 comandi IDENTICI, no gradienti EVAL, no perturbazioni esplorative. Poiché V3.18 aveva visto il proprio BLIND in esperimenti precedenti, non usarlo come calibrazione e riportare esplicitamente che è un benchmark incumbent e non un «nuovo modello ignoto».
- METRICHE: numero vite BOX con hold legale, frame sostenuti, true bilateral, comandi sopra soglia, tempo oltre soglia fuori BOX, vera quota di adesione aggiunta al `gel.work`, costo totale `gel.work` per famiglia, reale contatto solido, falsi grip, intensità delle intenzioni, eventuale lavoro del movimento. Il confronto su costo utile usa rapporto totale lavoro per frame con grip, e verifica mantenimento di almeno 60% dei grip BOX dell'incumbent V3.18 a parità di mondo.

**GATE A/B BLIND, tutti indispensabili:** (1) ≥4/6 vite BOX con grip e ≥60% dei frame con grip dell'incumbent V3.18, con floor assoluto 100 frame; (2) duty-cycle comandi >0,28 nei 3 mondi senza BOX **≤25%** e almeno **30% inferiore** all'incumbent; (3) lavoro fisico totale BOX della V3.19 non superiore all'incumbent V3.18 e **lavoro globale** non superiore all'incumbent; (4) lavoro totale BOX / (1+frame grippati) **almeno 15% inferiore** rispetto all'incumbent; (5) nessun grip falso fuori BOX, ≥10% frame con tocco BOX nella BLIND, numeri finiti, nessuna alterazione del vero solver; (6) pesi realmente cambiati in TRAIN e positivi `gripHold` reali, nessuna scelta/futuro leak, scenari e ablation verificati.
Una condizione mancante ⇒ **scientificGripGate=FAIL**, anche se CI è verde. Il confronto con head congelata va mostrato ma **non** usato per ignorare la baseline più forte V3.18. Se il campione di contatto BLIND è insufficiente, FAIL e non ripetere sugli stessi seed.

Non è G3 locomozione: non attribuire controllo del movimento, capacità di usare l'oggetto per sopravvivenza, world-model o retention da un miglior controllo del solo canale presa. Mancano policy actor-critic, vera energia/danno, neurogenesi, UI e checkpoint neurali.

## Risultati
Risultati effettivi della CI, riportati sotto dopo verifica completa.


## RISULTATI VERIFICATI — GitHub Actions 39017391971 (17/10/2026)

**[GitHub Actions V3.19, run 39017391971](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39017391971) — SUCCESS tecnico e `scientificGripGate=PASS`.** Job 117352212228: compilazione Java del vero solver, fixture a tre bracci e tutte le soglie preregistrate passate. Il codice `Zero3ContextAdhesionV319.java` e `Zero3ContextAdhesionV319Regression.java` è solo nel ramo Draft di PR #87; lo schema di prova è nel presente documento su `main`, non esegue aggiornamenti del gioco Android.

### Quantità di esperienza reali

- TRAIN V3.19 **32 vite ×96=3.072 intenzioni** scelte dall'actor sensorimotor, **36.864 passi fisici gel** (senza contare i 96 tick warmup per vita); `learnedUpdates=3072`; `positiveHeldCredits=2633` contributi/decisioni del singolo settore con presa legale, NON 2.633 nuovi eventi indipendenti. `cumulativeUpdateL2=4.092610727` è la radice della somma dei quadrati degli update, NON distanza netta dei pesi iniziali.
- BLIND **24 vite ×96=2.304 intenzioni per braccio** in tre bracci matched (V3.19 addestrata, V3.18 addestrata dalla *precedente* distribuzione training, V3.18 mai addestrata), **27.648 tick reali per braccio**, con 6 vite per FREE, BOX_NEAR, DISK, BOX_FAR. Nessun update BLIND, nessuna perturbazione nel run EVAL. Gli altri 48 muscoli e l'actor sono identici; una sola autorità `MotorIntentV316` per decisione.

### Confronto vero su nuovi mondi

| Indicatore BLIND | V3.19 contestuale | V3.18 addestrata | V3.18 non addestrata |
|---|---:|---:|---:|
| Vere prese mantenute su BOX — frame con qualunque `gripHold` | **6.900** | 6.900 | 0 |
| Vite BOX con presa su 6 | **6/6** | 6/6 | 0/6 |
| Frame con presa bilaterale (NON skill/oggetti manipolati) | 382 | **1.617** | 0 |
| Frame BOX con vero tocco fisico | 6.912 | 6.912 | 6.179 |
| Comandi motori di adesione super-soglia nel BLIND, tutti gli ambienti | **38.440** | 214.897 | 0 |
| Quota super-soglia in FREE+DISK+FAR | **3,085214%** | 45,114475% | 0% |
| Lavoro totale `gel.work` su BOX | **0,200176236** | 0,255425971 | 0,162100202 |
| Lavoro `gel.work` su FREE | **0,110362505** | 0,198926347 | — |
| Lavoro `gel.work` su DISK | **0,128038638** | 0,203810819 | — |
| Lavoro `gel.work` su FAR | **0,110006117** | 0,198594161 | — |
| Lavoro globale su tutte le scene | **0,548583495** | 0,856757299 | 0,652091010 |
| Quota di lavoro adesivo effettivamente contabilizzata dal solver | **0,150827614** | 0,457844853 | 0,262522565 |
| Lavoro BOX / (1+frame hold reali) | **0,000029006845** | 0,000037012893 | 0,162100202 / 1 |

**Interpretazione quantitativa:** a grip BOX equivalente, V3.19 riduce il costo fisico BOX del **21,63%**, il costo fisico totale di **35,97%**, il costo per frame con presa di **21,63%**, la quota di output super-soglia non necessaria dal **45,11% al 3,09%** (riduzione relativa ≈93,16%). **Ma riduce anche i frame di presa bilaterale da 1.617 a 382 (−76,38%)**. Il gate preregistrato richiede conservazione delle prese vere *generiche* e costo, non conservazione dei grip bilaterali: **questo limite è reale, non va nascosto**. Le prese su una faccia non garantiscono capacità di trasportare una scatola.

### Gate preregistrato

- `gripRetained=true`: almeno 4/6 vite BOX, almeno 60% hold vs V3.18 con floor100 → **6/6, 6.900/6.900**.
- `selective=true`: duty fuori BOX ≤25% e almeno 30% più basso vs incumbent → **3,09% vs 45,11%**.
- `workNotWorse=true`: lavoro BOX e lavoro globale non superiori incumbent → **0,20018 vs 0,25543**, **0,54858 vs 0,85676**.
- `efficient=true`: lavoro/(1+hold) ≤85% incumbent → **0,00002901 vs 0,00003701** (~78,37%).
- `realCoverage=true`: 6.912 veri frame di contatto BOX (≥10% necessario), nessun falso hold in FREE/DISK/FAR e solo input realmente osservati.
- `actualLearning=true`: 3.072 update di testa V3.19 su TRAIN e positivi veri `gripHold`, nessuna soglia mutata o uso del tipo fisico come osservazione.

**`scientificGripGate=PASS`**. Le soglie sono state fissate prima dell'esecuzione nel protocollo V3.19, non rifissate in base al risultato. Questa è la prima **promozione di risultato scientifico limitata al canale adesivo contestuale in laboratorio**, **NON promozione del cervello in produzione**. G3 e G5 generali restano **BLOCKED**.

### Diagnosi della fisica e rischi residui

La V3.19 usa il contributo al lavoro reale `gel.work` definito dal solver, **6 patch × adhesive ×0,1×0,025×0,0002 per settore/frame**, con controllo del delta lavoro misurato. Non modifica il solver. Concentrare la penalità sul lavoro realmente contabilizzato anziché su un proxy di "non contatto" spiega plausibilmente la riduzione di adesione diffusa; tuttavia si tratta di un nuovo modello con feature e funzione di credito diverse, non di un trial fattoriale che separi causalmente *solo* la penalità dal resto. **Non asserire una causa unica dimostrata**.

La riduzione delle prese bilaterali è un difetto osservato importante: serve un criterio futuro che misuri **presa contro due facce, mantenimento sotto trazione, rilascio volontario e spostamento intenzionale dell'oggetto** e non soltanto la permanenza `gripHold`. Il mondo BLIND usa nuove pose/geometrie *entro le stesse famiglie*; non testa trasferimento a forme radicalmente diverse.

Inoltre manca il vero `BrainSnapshotV3`: la rete del motoneurone V3.19 non è ancora collegata al journal persistente V3.16 né al cervello live, quindi la **retention dopo processo/riavvio non è stata dimostrata**. L'actor sensoriale V3.17 è congelato e non sceglie ancora obiettivi di sopravvivenza; i canali della locomozione 0/1/3 sono congelati, e l'esperimento non dimostra camminata efficace né aumento neuroni.

### Prossimo sviluppo coerente col Master

**V3.20:** (1) implementare checkpoint dei pesi del motor-head V3.19 + stato optimizer/seed e ripristino, compatibile con `BrainSnapshotV3` e doppia generazione V3.16, verificarne continuità fisica e numerica (non semplicemente caricamento byte); (2) prove longitudinali di mantenimento dopo restart con stesso decoder e nuovi seed; (3) riabilitare un gate specifico bilaterale/trascinamento e rilascio; solo in seguito integrare actor/critic e moto 16×4. Accettare solo scenari blind non visti. NON ridurre la soglia grip, non forzare contatti/oggetti e non adattare il modello V3.19 alle vite BLIND ora consumate.

**Stato produzione:** Draft PR #87; nessun merge, `0.11.6-ota.172` invariata, tutti i 12 profili ZERO2.2 intatti, UI ZERO3 non ancora implementata.

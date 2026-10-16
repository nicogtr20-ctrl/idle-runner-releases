# ZERO 3.0 — MOTOR LEARNING V3.1: DIAGNOSI, CORREZIONI SPERIMENTALI, TEST E HANDOFF

> **TEST 2.000 ESPERIENZE VERIFICATI:** [Report completo motorio e autonomo, checkpoint 400/1000/2000](ZERO30_LONG_HORIZON_2000_20261017.md). **G3 ANCORA BLOCCATO**: a 2.000 decisioni la corteccia non supera il vergine in nessuna delle 4 direzioni nella postura iniziale; misure coerenti ma negative. Nessun OTA V3.


**Stato:** SVILUPPO SPERIMENTALE; nessuna attivazione in gameplay, nessuna nuova OTA.  
**Sorgente:** [feature/zero30-foundation-g0-g3-20261016](https://github.com/nicogtr20-ctrl/idle-runner-releases/tree/feature/zero30-foundation-g0-g3-20261016).  
**Review protetta:** [PR #87 — DRAFT, non fare merge sul ramo publisher OTA](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).  
**Leggere prima:** [Progetto master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI/UX V3 obbligatoria](ZERO30_UI_UX_SPEC.md), [Handoff precedente](ZERO30_BUILD_STATUS_20261016.md).

## 1. Problema, evidenza, inferenza corretta

Nei dati del primo prototipo, su 400 decisioni con intenzione costante:
- **FROZEN-CLEAN** senza esplorazione produceva spesso grande deriva non finalizzata;
- **FROZEN-PROBE** con perturbazioni simultanee su 64 attuatori peggiorava drasticamente il comportamento;
- **TRAIN-PROBE** con aggiornamenti online peggiorava ulteriormente il controllo in tutte le quattro direzioni rispetto al frozen-probe.

Risultati benchmark vecchio prototipo (direzione, firmato lungo intenzione in unità fisiche): Est +26.058 -> -18.549, Sud +28.703 -> -32.237, Ovest -17.470 -> -90.140, Nord -13.693 -> -61.956 (frozen-probe -> train-probe). Sono misure da CI fisica reale simulata, NON prova sul telefono.

**Diagnosi architetturale:** esplorazione eccessivamente simultanea, attribuzione globale di un unico risultato a molti parametri, miscelazione fra intenti/contesti, possibili disturbi da inerzia/stato, necessità di separare UPDATE EFFICACE e performance verificata. Non sostenere che una singola causa sia già dimostrata in modo esclusivo: servono esperimenti separati.

## 2. ADR ZERO3-M31 — esplorazione spaziale correlata a canale

**Alternativa scartata:** mantenere 64 perturbazioni indipendenti di ampiezza .22 per decisione con learning rate .055 e baseline globale. Generava un alto livello di moto non coerente, costo e non identificabilità del contributo motorio.

**Scelta implementata a livello PROTOTIPO:**
1. In `Zero3LearningFoundation.AdaptiveCortex` la perturbazione è distribuita spazialmente con interpolazione di **quattro ancore casuali**, sui 16 settori ma **un solo tipo di attuatore per decisione** (indice di canale ciclico 0..3).
2. Il rumore è stabile durante la decisione simulata, non viene rimescolato ogni frame, ha ampiezza di progetto .14 (più bassa e distribuita su un sottogruppo); **non contiene direzioni verso gemme o ritmo di camminata prefissato**.
3. La conseguenza modifica soltanto i pesi associati ai comandi realmente perturbati; il resto non riceve la stessa «colpa» indiscriminata.
4. La baseline del punteggio è separata per **otto fasce angolari dell'intenzione**, con media adattiva; è un primo raggruppamento, **non** un modello completo della postura/inerzia.
5. L'aggiornamento è limitato per coefficiente e tracciato con norma effettiva; learning rate iniziale .013, esplorato su griglia dichiarata `{0, .013, .04, .10, .22}`. La ricerca su questa griglia è **tuning**, non holdout finale indipendente.
6. La modalità `FROZEN-PROBE` mantiene lo **stesso processo esplorativo** ma non aggiorna pesi; così misura separatamente rumore e plasticità.
7. `forkForEvaluation` clona soltanto i pesi del controllore in una nuova istanza con stato muscolare resettato per confronto con un cervello vergine **sullo stesso punto di partenza e senza rumore**.

**Invariante importante:** in fase normale la corteccia dispone di un'unica autorità `MotorIntent`; la perturbazione è esplorazione, non una seconda policy che sceglie di andare da qualche parte. Il corpo si muove soltanto tramite i 64 comandi realmente consumati da `SymbioteMatterV1.step`.

## 3. ADR ZERO3-M32 — gate di promozione indipendente

Un contatore `updates=400` non significa apprendimento efficace. Si implementa `Zero3MotorPromotionGate` per **rifiutare** candidati che peggiorano il controllo.

Il confronto fisico è eseguito a corpo freddo con inizializzazione **identica**, rumore disattivato, stesso orizzonte e intenzione costante in quattro direzioni. Si conservano performance firmata lungo l'intenzione, distanza lorda, distanza netta, consumo e revisione dei pesi. Il criterio preliminare di promozione è:
- almeno 3 direzioni su 4 migliorano di oltre 0.25 U,
- guadagno complessivo firmato superiore a 2 U,
- nessuna direzione peggiora oltre 2 U,
- costo muscolare totale non oltre +15% rispetto al controllo.

**Queste soglie sono provvisorie e non costituiscono validazione definitiva;** devono essere poi sostituite o confermate con molti seed e scene non usate nel tuning. Questo gate per ora è un esito nel LAB: **non attiva automaticamente i pesi nella partita, non scrive salvataggi**, non dichiara abilitata una skill.

## 4. Test su CI obbligatori

- `Zero3FoundationRegression`: 20 frame di fisica reale, nessun doppio accredito, niente update da heldout, esternalità, teletrasporto.
- `Zero3RecurrentBrainRegression`: 64 GRU con forward actor -> cortex -> vera fisica 96+74 -> consequence e differenza pesi vera. Non prova intelligenza superiore.
- `Zero3LearningCurveRegression`: 40/400 esperienze train vs frozen senza gemme; misurare netto/lordo e performance intenzionale.
- `Zero3MotorSkillAB`: 400 intenzioni costanti, `FROZEN CLEAN`, `FROZEN PROBE`, `TRAIN PROBE`; tre condizioni per ciascuna direzione, non scegliere solo la migliore.
- `Zero3MotorRetentionRegression`: 400 decisioni addestramento e **160 decisioni di valutazione fresh-body senza rumore** per ogni direzione; stampa `PROMOTION=REJECTED/CANDIDATE` dal gate.
- `Zero3MotorRateSweepRegression`: confronto su **tutti** i 5 rate dichiarati, con output di **tutte** le direzioni, stessa fisica, stessi seed. Una scelta del rate da qui è **ottimizzazione su dati di laboratorio** e va riverificata su mondi/seed nuovi.
- Tutti questi test devono passare con la vera fisica e la build Android Debug senza pubblicare l'APK. Un «PASS» per integrità non deve essere confuso con l'accettazione G3 per apprendimento motorio.

**Log CI di partenza:** [38969432748](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38969432748). **Nuove CI V3.1:** consultare gli ultimi run sul ramo; mantenere qui i risultati effettivi verificati.

## 5. Limiti non ancora risolti

- Il feedback usa ancora uno score da spostamento netto/traffico lordo su intervallo: **non** è prova di attribuzione causale precisa del singolo muscolo quando inerzia, contatti o stato sono cambiati.
- Un cambiamento nell'intenzione del cervello e una postura diversa possono rendere un confronto senza clone non perfettamente omogeneo.
- Il nuovo motore non dispone ancora di un **modello dinamico causale per attuatore**, test di azione vs baseline passiva, gradiente temporale su ritardi e credito differenziale su scene con oggetti.
- Non sono presenti memoria di skill di produzione, world model V3 pieno, actor-critic off-policy finale, neurogenesi, checkpoint V3 e nuova UI 3.0. Non dichiararli completi.
- No generalizzazione multiseme/scene, benchmark Android S21, OTA firmato; non toccare il ramo publisher stabile.
- Se i test mostrano netto peggiorato o una maggioranza di direzioni in regressione, **G3 resta BLOCCATO**. Aggiornare documento con numeri reali e implementare un modello di risposta fisica contestuale prima della promozione.

## 6. Prossimi passi richiesti

1. Leggere i nuovi log CI e confrontare i **tre controlli** in quattro direzioni, il confronto zero-noise dopo training e tutta la griglia rate.
2. Se G3 non passa, realizzare **causal response model temporale per comando effettivo**, includendo risposta elastica, inerzia iniziale e contatto, con interventi localizzati e campioni effettivamente osservati; nulla di privilegiato.
3. Accettare modifiche ai pesi solo dopo confronti indipendenti e controllare regressioni; documentare il motivo e la reversibilità dell'accettazione.
4. Test indipendenti con 20+ seed, posture diverse, ostacoli, tempi simulati, finché la convergenza non è ripetibile.
5. Solo dopo integrare nello ZERO3 completo, memoria/skills, neurogenesi, salvataggi e **nuova UI di apprendimento** (neuroni, abilità, evoluzione, corpo, impostazioni).

**Verdetto di questo documento finché non ci sono benchmark indipendenti positivi: correzione sperimentale dell'esplorazione e del processo di promozione; locomozione appresa non dimostrata.**


## 7. RISULTATI CI VERIFICATI — 16/10/2026, 21:52–21:54 UTC

**[GitHub Actions run 38972240462 — SUCCESS per compilazione/test infrastrutturali](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38972240462).** Il successo della CI NON equivale a promozione del learner.

La correzione che usa sondaggio correlato su un singolo canale, baseline per direzione e passi di aggiornamento limitati ha ridotto molto il danno rispetto alla perturbazione originaria, ma il test **senza esplorazione** su corpo appena inizializzato, dopo 400 decisioni di allenamento, riporta:

- Direzioni in miglioramento: **1/4**.
- Somma spostamento firmato lungo le quattro intenzioni su 160 decisioni di valutazione per direzione: **vergine 27.970 U**, **allenato 9.047 U**.
- Peggioramento aggregato: **-18.923 U**; peggior direzione **-9.002 U**.
- Rapporto dei costi muscolari: **1.015**.
- Risposta automatica del gate di promozione: **REJECTED**, nessun inserimento nel controller live.
- Partizione "fresh body" significa nuova istanza fisica con stato iniziale equivalente, **non** scena diversa, né prova statistica multiseme.

### Griglia preregistrata di learning rate (TUNING, non holdout indipendente)

| Tasso `lr` | Somma spostamento firmato (U) | Direzioni con risultato positivo | Consumo cumulativo |
|---:|---:|---:|---:|
| 0.000 (no plasticità, esplorazione consentita in training) | **27.970** | 2/4 | 0.209209 |
| 0.013 | 9.047 | 2/4 | 0.212320 |
| 0.040 | -1.959 | 2/4 | 0.224586 |
| 0.100 | -14.692 | 2/4 | 0.258019 |
| 0.220 | -66.928 | 1/4 | 0.321856 |

**Diagnosi rafforzata:** aumentare l'intensità dell'aggiornamento peggiora sistematicamente la prestazione aggregata in questa batteria. Non è un semplice problema di aggiornamenti troppo piccoli: il **segnale/estimatore del credito motorio è insufficiente o mal allineato al controllo**. Congelare i pesi è meglio che continuare la plasticità corrente, in attesa di un algoritmo alternativo con evidenza autonoma.

### Test aggiuntivo ancora sperimentale

Sul ramo sperimentale è presente `Zero3MotorEpisodicSearchRegression`: un esperimento di ricerca di **modifiche locali dei parametri**, accettate solo sulla base di risultati del vero solver, con 60 candidate per direzione e valutazione separata su un orizzonte di 160 decisioni. Non è un'andatura codificata né una nuova policy gameplay. Anche se migliora, **non bastano same-scene e same-initial-body per convalidare una vera abilità generalizzabile**; richiede molte scene, seed e controllabilità contestuale.

**Gate G3: BLOCCATO**, indipendentemente dal risultato della compilazione. ZERO 2.2/OTA resta intatto.

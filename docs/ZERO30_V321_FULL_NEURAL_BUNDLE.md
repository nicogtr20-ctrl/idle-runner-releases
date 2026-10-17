# ZERO 3.0 — V3.21 | Checkpoint congiunto del circuito neurale sperimentale

**Protocollo preregistrato il 17/10/2026, prima del codice e dei test.** Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.20](ZERO30_V320_MOTOR_HEAD_RESTART_RETENTION.md), [UI](ZERO30_UI_UX_SPEC.md).

## Ipotesi falsificabile

V3.20 conserva e riprende bit per bit SOLO 128 pesi e lo stato del motoneurone adesivo. La V3.21 deve conservare **insieme**, con schema di versione unico e un solo commit atomico, (A) `Zero3AutonomousLoopV317.Encoder` ricorrente: matrici, bias, readout, readout actor, 16 unità ricorrenti, snapshot delle osservazioni, RNG, update/counters e accumulo norm gradient; (B) `Zero3LearningFoundation.AdaptiveCortex`: pesi 64×8, slow states motor, output e feature, perturbations, PRNG, context baselines e campioni, revisioni, update e scheduler; (C) `Zero3ContextAdhesionV319`: 16×8 pesi, RNG, credit/optimizer accumulator e work. Nessuna copia parziale inconsistente tra le tre componenti. NON ricostruire il cervello dal solo file motor-head V3.20; migrazione completa future separata.

## Invarianti

- Checkpoint soltanto quando encoder ha completato `after`, cortex/Coordinator hanno completato `finish`, motor-head ha completato `end`: **tutti e tre ai confini tra decisioni**. Rifiutare snapshot e restore mentre una transizione PRE→ACTION→POST è in corso. Restore solo su istanze nuove (o reset freddo verificato) e modalità training/learningRate congrue.
- Serialization binaria canonica strict-version, shapes fisse, `float` bitwise, contatori/seed e policy config con valori finiti; rifiutare NaN, dimensioni invalide, dati extra/omessi. Immutabilità/copie difensive. SHA-256 di payload e manifest con due generazioni alternate; fsync e `ATOMIC_MOVE`, fallback convalidato, mai promuovere checkpoint incompleti.
- **Una sola intenzione** `MotorIntentV316.Authority` per decisione, scelta dall'actor a partire da `SensoryFrameV315` pre-azione; cortex 48 canali + head adesivo canale2, gel e collisioni autentici, after/learning soltanto post-solver. Zero gait/collision override, zero premio finto. Parametri sempre coerenti all'interno del frame. Il prototipo del cortex non ha memoria optimizer Adam né actor-critic, conservare solo lo stato che ESISTE in questa implementazione.
- Il ripristino a limite tra episodi **non equivale a riavviare una vita fisica**: corpo, oggetti, contatti, authority/counter corrente, fisica e RNG mondo non sono in questo file. Non chiamarlo ancora `BrainSnapshotV3` finale.

## Test preregistrati

4 processi JVM separati dentro GitHub Actions:

1. PREFIX: addestra il loop integrato TRAIN per **8 episodi × 96 decisioni = 768** intenzioni realmente scelte, checkpoint gen1 dopo 4 vite e gen2 dopo 8. Verificare aggiornamenti di encoder, cortex e motor head, e che almeno qualche contatto e presa fisica avvengano.
2. RESUME: nuovo processo e moduli nuovi, `load` gen2 e altre **8 vite × 96 = 768** scelte fisiche, checkpoint gen3; Stato completo in un file fingerprint e risultati di una valutazione **congelata** su nuovi pose/seed (nessun update).
3. REFERENCE: nuovo processo, stessi **16 episodi ×96=1536** senza interruzioni; serializzazione e tracce/metriche di valutazione devono essere **bitwise identiche** a RESUME. Se l'encoder/cortex non ricevono update o la ripresa non è identica, gate FAIL.
4. FAULT: corrompere/troncare gen3, recuperare solo gen2 e **768** scelte confermate, ricommit salvando solo dati convalidati; corrompere primary manifest, fallback verificato, corrompere entrambi gli slot → fail closed; test snapshot durante decisione e doppio restore.

Valutazione EVAL **8 vite ×64 decisioni=512 intenzioni** per braccio (4 tipi con 2 vite ciascuna), sulla stessa distribuzione sconosciuta a TRAIN, senza fit o esplorazione, output fingerprint del sequenziamento degli intent e degli output fisici, veri hold/adesione, `gel.work`, costo muscolare, contatti e differenze spaziali. Se non ci sono contatti e grip effettivi nel test, riportare come **retenzione biologico-motoria NON CONFERMATA**, anche se il checkpoint è bitwise corretto.

**Gate V3.21 neural bundle checkpoint:** codice reale compila; ≥1536 vere scelte motorie; aggiornamenti reali dei 3 moduli; integrità e recovery PASS; file completo RESUME vs REFERENCE identico bitwise e nessun evento inventato. Non dichiarare controllo della locomozione (G3) o world-model G5: entrambi rimangono bloccati indipendentemente dal risultato. G6 memoria skill, neurogenesi, UI, Android/OTA e 12 slot originali invariati.

## Risultati

Risultati verificati e limiti nel rapporto seguente.


## RISULTATI VERIFICATI — GATE NEURALE V3.21

**[GitHub Actions V3.21 definitiva 39020043151](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39020043151) — COMPLETED SUCCESS**; log verificato della precedente run identica sullo stesso esperimento [39019851548](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39019851548), job 117359326117, con risultati fisici ed esito senza errori. I due interventi successivi nel test hanno rinforzato il rifiuto dei checkpoint durante una decisione non-esplorativa e la compatibilità Java dei test con la compilazione Android; la CI finale V3.21 ha confermato la stessa prova PASS. L'ulteriore [Android / foundation smoke](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39020104719) è un controllo separato sul ramo, non un'OTA.

Sorgenti aggiunti o estesi **solo nel ramo Draft PR #87**:
- `Zero3AutonomousLoopV317.Encoder.State`: snapshot difensivo di tutte le matrici (320 + 256), bias, readout sensoriale, hidden recurrent, osservazioni/lastPrediction, quattro readout dell'actor, PRNG, updates/steps/norma cumulativa; recupero su encoder nuovo soltanto con stato non-pending.
- `Zero3LearningFoundation.AdaptiveCortex.State`: pesi 64×8, stato lento 64D, output/feature/noise/probe accumulator, baseline e sample count contestuali, revisione/update/scheduler/RNG, learning rate; vietato snapshot anche con `begin(false)` finché non c'è `finish`/`discard`.
- `Zero3ContextAdhesionV319.State`: 128 weights, RNG, contatori, credit e lavoro meccanico già provati in V3.20.
- `Zero3NeuralBundleStoreV321.java` inserisce i **tre snapshot nello STESSO payload**, con schema/shape fissi e controverifica canonica binaria, SHA-256, due slot, manifest hashati e recupero della generazione integra, `ATOMIC_MOVE` richiesto. Né i salvataggi Android 2.2 né il solver fisico vengono modificati.
- `Zero3IntegratedRestartV321Regression.java` esegue il vero loop sensori PRE → recurrent actor sceglie intenzione → Authority unico → cortex 64 canali (16 comandi adesione sostituiti dal trainable head) → **12 reali tick del gel, con collisioni/tatto/grip originali** → outcome fisico → aggiornamento dopo le conseguenze. Test distribuito su 4 JVM separate con replay deterministico, valutazione a pesi congelati e corruzione intenzionale.

### Numeri REALI, tutti letti dai log CI

| Fase | Evidenza |
|---|---|
| PREFIX (processo 1) | **8 vite × 96 = 768 scelte dell'actor**, 9.216 tick gel nelle decisioni, checkpoint generation 1 dopo 4 vite e generation 2 dopo 8. **768 updates della rete ricorrente, 768 cortex e 768 motoneurone** |
| RESUME (processo 2) | Riprende gen2 in un altro processo e allena 8 vite aggiuntive; **1.536 decisioni totali**, **1.536 updates per ciascuno dei 3 moduli**, checkpoint generation3 |
| REFERENCE (processo 3) | 1.536 decisioni continue e le stesse condizioni. **Identico SHA-256 dello stato congiunto** a RESUME |
| Fingerprint SHA-256 comune (payload congiunto) | `9e31ae75d12cd32904cc42db265f0da5ad50fe19dc035f51963fecb5252fa4de` |
| EVAL separata, congelata | **8 vite × 64 = 512 scelte per ramo**, 6.144 passi gel per ramo, identica sequenza muscolare `motorTapeHash=cdeab6dbd77d4f23`, e **intero file `cmp` bytewise** dei risultati, non soltanto stesso punteggio |
| Misure EVAL sul corpo | **1.536 frame BOX con contatto**, **1.532 frame BOX con `gripHold` reale**, **2/2 vite BOX con presa**, **0 frame bilaterali**, `gel.work` totale **0,1234752223**, spostamento complessivo **50,54503663** unità nella fixture |
| Processo 4 — corruzione | Corruzione gen3 → recupero autentico gen2 e soli **768 updates**; recommit conserva esattamente 768, manifest primario corrotto → fallback validato, entrambi gli slot corrotti → **fail closed** |
| Negative tests | Rifiuta snapshot con motoneurone attivo, cortex non-esplorativa ancora dentro `begin` e encoder con PRE senza POST; controlla no missing/duplicate Authority intent |

**GATE tecnico V3.21 `NEURAL_BUNDLE_GATE=PASS`.** Le due versioni del cervello producono gli stessi output numerici e comportamento fisico su eval dopo processo riavviato. È una prova forte di **continuità dell'apprendimento dei tre moduli esistenti**, non di intelligenza completa.

### Confini scientifici e ingegneristici

Questo è **il checkpoint congiunto dei moduli neurali sperimentali** (recurrent actor/encoder, cortex del corpo e motoneurone adesivo). **NON è ancora il `BrainSnapshotV3` integrale** del Master, perché mancano: il brain ZUAB definitivo, l'actor–critic vero, world model/certainty, normalizer/adaptive growth, ricordi procedurali, replay atomicamente associato alla stessa generazione, energia/danno reali e la possibilità di ricostruire **la stessa vita fisica** da body/gel/world objects, RNG, Authority e pose. Gli episodi ripartono con nuove istanze fisiche; il cortex conserva lo slow state addestrato, mentre l'encoder azzera esplicitamente la state ricorrente alla nuova vita, secondo la semantica del laboratorio.

**Nessun passo in avanti dimostrato su G3 locomozione**: non sono state misurate superiorità motoria su frozen né camminata direzionale utile. Il problema della presa bilaterale resta serio: **0 frame bilaterali nel nuovo EVAL** anche in presenza di 1.532 frame con presa. Zero nuove abilità dichiarate per questo fenomeno. Il predittore/world-model non ha superato le baseline in V3.17; **G5 BLOCKED**.

Il checkpoint è testato su Linux/JVM e con lavoro fisico reale del solver; non si è fatto un crash/powerloss test Android Galaxy S21. L'archivio ha fsync dei file e rename atomico ma non dimostra durability completa su ogni filesystem; ancora nessuna UI V3 e nessun uso dei vecchi 12 profili o della OTA.

### Ordine successivo

1. **V3.22: snapshot causale unico neuroni + stato corpo/mondo/RNG/authority e replay con generation id comune**, verificato con vera interruzione **a metà vita**, senza avvalersi di nuovi spawn e senza perdita/duplicazione di decisione; snapshot solo su decision boundary finché l'atomizzazione intra-fisica non è dimostrata.
2. Obiettivi e feedback di locomozione veramente collegati al corpo, credito motorio non distorto, multi-seed frozen vs trained; non chiamare corsa il grip.
3. Riprendere l'incompleta presa bilaterale/manipolazione, vera actor–critic, world model, neurogenesi e UI collegata ai dati reali; G3/G5 ed ETA di produzione ancora non autorizzati.

**Stato produzione: nessun merge/OTA, PR #87 Draft, `0.11.6-ota.172` stabile, 12 cervelli 2.2 intatti.**

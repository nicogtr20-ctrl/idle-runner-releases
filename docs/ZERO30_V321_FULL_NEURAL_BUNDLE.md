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

*Compilare dopo i log reali CI; nessun risultato presunto.*

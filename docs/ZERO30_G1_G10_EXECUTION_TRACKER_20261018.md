# ZERO 3.0 — ESECUZIONE ORIGINALE G1→G10: tracker vincolante (18 ottobre 2026)

**Ordine di autorità: [MASTER](ZERO30_MASTER_BLUEPRINT.md) → [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md) → [UI](ZERO30_UI_UX_SPEC.md) → [AUDIT REALE](ZERO30_IMPLEMENTATION_GAP_AUDIT_20261018.md).** NON creare altri requisiti di compatibilità dalla 2.2: la conservazione è già prescritta dal MASTER. Non interpretare una versione di laboratorio come un gate chiuso. L'utente ha autorizzato a **procedere con l'intero progetto**, non a saltare benchmark/devices/safety né a firmare un'OTA prima della validazione finale.

**Ramo sorgenti attuale:** `feature/zero30-foundation-g0-g3-20261016`, PR #87 in stato **DRAFT** su base publisher `feature/zero22-active-recurrent-ota117`. **NON MERGIARE** nella base finché G1-G9 non raggiungono gate master e non viene effettuata validazione dispositivo. Manifest stable `main/updater/public/latest.json` osservato: versione `0.11.6-ota.172`, versionCode1172. 12 brain slots V2.2 segregati e intoccabili. Il MASTER già stabilisce le uniche funzionalità da preservare.

## Lavoro eseguito in questa fase di implementazione, non solo nuovi documenti

### V344 — Unica sorgente causale di vera energia per la sessione Android LAB [G2; esito tecnico PASS]

**ADR preregistrato:** [ZERO30_V344_RUNTIME_INTEGRATION_ADR.md](ZERO30_V344_RUNTIME_INTEGRATION_ADR.md).

Moduli branch:
- NUOVO `Zero3CausalVitalBoundaryV344.java`: `Before` autentico dall'immutable `Zero3SensoryFrameV315` PRE e `Zero3SurvivalLedgerV323` completato, `After` post-12 passi con bilancio `reserveDelta = actualGain − basal − motorDebit − damage` entro tolleranza numerica; verifica frame/episode/decision hash e rifiuta outcome futuro o intervallo non concluso.
- **INTEGRATO IN RUNTIME EFFETTIVO** `Zero3RuntimeV325.advanceOneDecision`: gli stessi valori V327 di fuel/previousDamage vengono passati al `V317.Encoder` tramite `Before` tipizzato; `After` costruito solo dopo `metabolism.finish` e accreditamento effettivo della fisica; nessuna differenza su input motori, reward, 64 attuatori, solver, archivio.
- `Zero3RuntimeV325.Display` e Android `Zero3LabActivityV326`: riportano il bilancio misurato dell'ULTIMA decisione solo quando davvero presente, indicano risultato **NON DISPONIBILE dopo cold restore**, non sostituiscono un risultato assente con fake zero e non accedono a vecchie pref V2.2. Questa è una strumentazione LAB, NON la UI G8.
- `Zero3CausalVitalBoundaryV344Regression`: vero runtime 16 esperienze/192 substep dopo warmup, restore a decisione8 e ripresa fino a16 bytewise identica a vita mai interrotta, test che rileva e rifiuta future-data/leak, due regression originali su mondo real/fisica V325 e Android debug build.

**CI [39079361100 — SUCCESS complessivo](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39079361100)**:
`V344 CAUSAL_PRE_POST_NO_FUTURE=PASS realBasal=0.027008057 realNet=-0.027008057`;
`V344 SAME_RUNTIME_REFERENCE=PASS decisions=16 physicalTicks=288 cortexUpdates=16` (include 96 warmup ticks);
`V344 COLD_RESTART_BITWISE_IDENTICAL=PASS at8Then16=true`;
`V344 ORIGINAL_REAL_PHYSICS_AND_RESTART mode=0,1 PASS`;
`BUILD SUCCESSFUL in 1m4s`.
CI preliminare 39079314732 FAIL perché elenco javac mancava vecchio modulo fisico V336 usato da `SquareCollisionSolver`; aggiunto **solo** al workflow, non al runtime.

**Questa è una reale integrazione di G2**, non un completamento definitivo del contratto sensoriale (ancora V315 energyAvailable=false, 16 rays senza rappresentazione spettrale unificata).

### V345 — Salvataggio GRU64 + doppio critic + actor per futura integrazione G4/G7 [PREREQUISITO, non G4 completo]

**ADR preregistrato:** [ZERO30_V345_GRU_CHECKPOINT_ADR.md](ZERO30_V345_GRU_CHECKPOINT_ADR.md).

Il prototipo **esistente** `Zero3RecurrentBrain` ora dispone di `checkpointAtBoundaryV345` e `restoreAtBoundaryV345`: snapshot completo del true 64-unit GRU gates, actor, dual critic, one-step world predictor, stato ricorrente/buffer decisionale, contatori, metrics heldout e RNG, con MAGIC/version/dimensioni + CRC32, size cap, verifica integrale prima di mutare pesi. Rifiuta snapshot pending, restore su istanza in uso, doppio restore, corruzione e troncamento. NUOVA `Zero3RecurrentBrainCheckpointV345Regression` confronta 56 future decisioni e apprendimenti dopo copia della GRU con tutti i float esatti (incluso stato hidden), holdout, contatori, patologie dei blob. Si integra con gli esperimenti G4 esistenti e il runtime V344 come regression non-interference.

**CI:** [39079675581 — V345 GRU CHECKPOINT](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39079675581). Leggere outcome finale prima di marcare Android PASS; i controlli JVM G4 existing, checkpoint56 e V344 in stato PASS nella sequenza osservata. **Il prototipo GRU non è ancora chiamato da `Zero3RuntimeV325`**. Non spacciare una nuova serializzazione isolata per un vero migration/brain V3 attivo.

## Stato dei gate e PROSSIME OPERAZIONI da eseguire nell'ordine master

| Gate | Verifica tecnica esistente | Blocco non aggirabile / prossima operazione concreta |
|---|---|---|
| **G1 fisica** | corpo 96/74, veri 16×4 output; V319 presa appresa e V338 joint post-world validati in scenari limitati | integrare solver fisico verificato nel V325 solo dopo parity test con `room.solveGate`/BOX multipli/dischi/storm; riparare crossing unresolved provato 4054 vecchio policy in V342, mai fissare locomozione con motore esterno |
| **G2 contratto** | trace PRE→12×64→POST, owner unico e V344 actual metabolic boundary testati | sensori tipizzati completi, vista spettrale non-privilegiata, hash e versioning stabile, una singola esperienza comprensiva di energia e fisica |
| **G3 cortex/skill motoria** | `AdaptiveCortex` vera, `V319` grip learning PASS E2/E3 parziale, test veri multi seed | G3 scientifica FAIL come benchmark autonomo; verificare skill motorie per intenzioni autonomamente selezionate, non obbligo di spostare BOX; implementare candidate/champion/recall, controllo energia RAW |
| **G4 cognition** | `V317` active recurrent LAB, `Zero3RecurrentBrain` GRU64 actor dual critic esistente; snapshot V345 in prova | **INTEGRARE come unico owner** GRU actor/critic nel `Zero3RuntimeV325`, senza duplicare `MotorIntent`; adapter V315→sensor SENZA hidden object ID; outcome reward V323 reale→critic; versione archivio full new, training partition/ablation, cold resume total. Non collegare senza G1/G2 e checkpoint. |
| **G5 world model** | predittori local delayed e one-step modello dentro prototipo G4 | aggregare vero modello condiviso multi-output + uncertainty per testa; holdout independent vs ballistic/unchanged, planner bounded SOLO dopo accuratezza e test impatto |
| **G6 obiettivi/memoria** | episodio in causal journal e metrica energia V323; fonte sotto BOX V343 meccanicamente testata | ricompensa reale al critic, value learning su cue spettro anonimo, curiosità sui miglioramenti predittivi, procedural skill candidates/champion/recall, obiettivi temporali, doppia prova su BOX utili vs vuote senza grip hardcoded |
| **G7 neurogenesi/persistenza** | V328/329 segmenti SHA256 e 2k cold restart fisico **già presenti**; V345 GRU standalone | snapshot unificato actor GRU+critic/optimizer+world model+skill+growth+gel+BOX+energy+RNG; aumento dimostrato di unità e connessioni neurali reali, retention, cap archive3500 ancora sotto10k |
| **G8 UI** | Activity LAB V326, V344 bilancio reale displayed | **implementare SPECIFICA UI completa**: Panoramica, cervello + 3D neuroni reali, Corpo, Percezione, Memoria, Abilità, Evoluzione, analisi/benchmark, settings/profili/TEST MAX, provider V3 veri; eseguire UI-01..UI-16. |
| **G9 Android** | build Debug compilate, salvataggi JVM, vecchia OTA non toccata | test su Galaxy S21 attuale, vere metriche battery/heap/thermal, offline, restart process, accelerazione x1/x5/x20/testMAX, 20+ seed independent e UI/perf |
| **G10 OTA** | installazione/manifest OTA172 e firma preesistenti fuori da PR | bloccare fino a G9 completato e consenso finale, build signed verified manifest, aggiornamento non distruttivo e rollback preservato |

**Criterio di avanzamento:** una modifica G4 effettivamente nel runtime con checkpoint V3 richiede test JVM/Android e poi dimostrazione scientifica, non solo compilazione. G5–G10 non vengono dichiarati completati senza risultati oggettivi. Nessuna promessa di fine automatica fra conversazioni: stato deve essere registrato qui e in AGENTS, con prova verificabile.

**NO OTA, NO MERGE, no modifiche a stable o ai 12 cervelli V2.2.**

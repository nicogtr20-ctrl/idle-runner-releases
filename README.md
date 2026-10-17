# OVERMIND ZERO — MASTER PROJECT ZERO 3.0

> **V3.17 — PRIMO LOOP DI DECISIONI MOTORIE SCELTE DALL’ACTOR (17/10/2026):** [protocollo + risultati ufficiali](docs/ZERO30_V317_AUTONOMOUS_MOTOR_LOOP.md) · [CI finale 39015868260](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39015868260) **SUCCESS tecnico, `scientificMotorGate=FAIL`**. Nuovi `Zero3AutonomousLoopV317` e regression/CI su PR #87: **2.048 vere intenzioni selezionate dal circuito ricorrente**, 24.576 solver ticks sotto comando 64D, 2.048 update encoder + 2.048 cortex (pesi realmente modificati); BLIND 768 decisioni per ramo su scene/pose matched, nessun training BLIND. Allineamento motorio medio **−0,01577877** addestrato vs **−0,00179231** frozen; 1/4 famiglie migliorata; predizione next-state 0,008395506 vs persistenza 0,000139328, quindi nessuna competenza validata. **Diagnosi concreta:** >100k eventi contatto training ma zero `gripHold`: l'adesione corticale non supera MAI soglia fisica 0,28 (0 settori sopra soglia). **Prossimo audit V3.18**: head adesione trainabile/credito reale su nuovi seed, non hardcodare presa. Modulo NON actor–critic completo né app, G3/G5 bloccati, NO OTA/merge, 12 slot 2.2 preservati.

> **V3.16 — INTENZIONE UNICA + REPLAY PERSISTENTE A DUE GENERAZIONI (17/10/2026):** [rapporto e contratto preregistrato](docs/ZERO30_V316_INTENT_PERSISTENCE.md) · [CI DEFINITIVA 39015142772 — SUCCESS / gate infrastrutturale PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39015142772). Su PR #87 creati `Zero3MotorIntentV316`, `Zero3ExperienceJournalV316`, `Zero3IntentPersistenceV316Regression`: **2.048** intenzioni di laboratorio collegate a vere azioni/outcome, 3.584 step gel, **4 checkpoint completi** bitwise, 96×12+16+48+74×7+64D conservati, SHA-256 con commit atomico alternato e recovery di **1.536** esperienze dopo corruzione dell'ultima generazione; re-checkpoint dopo rollback provato, false autorità e replay falsi rifiutati. **NON sono intenzioni scelte autonomamente dall'AI, NON un checkpoint del cervello e NON un salvataggio Galaxy S21 testato**. Encoder/actor/cortex/neuroni/UI ancora incompleti, G3/G5 bloccati; PR Draft, NO APK/OTA, 12 slot 2.2 intatti.

> **V3.15 — PRIMA BASE VERA DEL CONTRATTO SENSORIALE UNIFICATO (17/10/2026):** [protocollo, sorgenti, QA e risultati](docs/ZERO30_V315_SENSORY_CAUSAL_CONTRACT.md) · [CI DEFINITIVA 38989788378 — SUCCESS, gate infrastrutturale PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38989788378). Rami PR #87: nuovi `Zero3SensoryFrameV315`, `Zero3CausalRecorderV315`, `Zero3SensoryContractV315Regression`: **2.048 esperienze realmente registrate** PRE→64 muscoli→POST da **3.584 passi del solver autentico** in 16 episodi, 96×12 canali cutanei immutabili, vista 16 raggi dalla membrana, 48 propriocezioni globali, 74×7 segnali fluidi, tatto solidi/impulso e presa distinti, timestamp/gel ticks e checksum; prove di duplicazione, leak e fake step rifiutate. **Queste 2.048 sono decisioni di laboratorio, NON abilità o locomozione autonomamente appresa**. Persistenza atomica, ownership dell'intenzione, policy ricorrente, memoria, neurogenesi e UI V3 ancora da realizzare. G3/G5 bloccati, PR #87 Draft, zero merge, OTA172 e 12 slot invariati.

> **V3.14 — ESPERIMENTO VISIVO PRE-AZIONE (17/10/2026):** [ADR e criteri preregistrati](docs/ZERO30_MOTOR_V314_PREACTION_VISION.md) · [CI](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988832620). Auditato il vero sensore ottico: `MaterialGeometry.eye` dalla membrana e `SensorimotorMind.Senses.firstSolidDistance` usati anche da `SpatialPerception`; 16 raggi riletti prima di ogni azione, separati da propriocezione 25D, tocco/pressione/presa e 64 efferenze. Prototipo E2 di sola ricerca: FULL vs NO_VISION/NO_ACTION/NO_TOUCH, esito primo impatto e fisica post-contatto su 320 traiettorie; **CI 38988832620 SUCCESS TECNICO, ma gate scientifico CAL=FAIL/BLIND=FAIL**. In BLIND 52 primi urti autentici, 16 vite con presa e 9.216 scansioni con oggetti visibili prima dell'azione. FULL XY one-step MSE 0,000201097 contro ballistica 0,000191035; Brier onset 0,009176898 contro modello senza vista 0,008148665; rollout 48 frame FULL 0,133198 contro ballistica 0,156992. Il nuovo input visivo aiuta alcune traiettorie ma non generalizza sul prossimo urto. [Rapporto](docs/ZERO30_MOTOR_V314_PREACTION_VISION.md). **Prossimo: realizzare il contratto vero SensoryFrameV3 e il recorder causale**, non ripetere fitting lineare senza nuova architettura. G3/G5 bloccati, PR #87 Draft, nessun OTA.

> **V3.13 — LABORATORIO REALE DI APPRENDIMENTO STATO/AZIONE/CONTATTO (17/10/2026):** [protocollo preregistrato](docs/ZERO30_MOTOR_V313_CONTACT_ACTION_LEARNING.md) · [CI esperimento](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988290751). 320 traiettorie pianificate su gel+collisioni vere: FREE, BOX, DISK; `touchSense`, `touchPulse` e `gripHold` distinti; modello corpo-frame+efferenza contro senza azioni e senza tatto. **[CI 38988290751 SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988290751), ma gate scientifici CAL=FAIL e BLIND=FAIL.** La V3.13 su 76.800 passi reali ha 63/80 traiettorie BLIND con contatto e 16/80 con presa. Movimento XY BLIND full MSE 0,000126998 vs ballistica 0,000125836; su frame tattili 0,000009129 vs 0,000006829. Il modello NO_TOUCH va meglio di FULL; il corpo predice meglio lo stato generale, ma non l'effetto del contatto sulla locomozione. [Rapporto definitivo](docs/ZERO30_MOTOR_V313_CONTACT_ACTION_LEARNING.md). Prossimo audit: vista/prossimità sincronizzata; E3/G3/G5 bloccati, NO OTA.

> **V3.12 — CONTATTO E PRESA FISICI VERIFICATI (17/10/2026):** [protocollo e risultati](docs/ZERO30_MOTOR_V312_REAL_CONTACT_FIXTURE.md) · [CI 38987814698 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38987814698). Vero mondo di oggetti e solver: 96 prove / 23.040 step, 44/48 scatole vicine con contatto, 21/48 con presa, 15/48 con presa bilaterale, 13/16 dischi con `touchPulse`; nessuna presa senza comando adesivo né falso contatto lontano. **Gate di COPERTURA E2 PASS, NON gate G3/G5.** V3.11 non osserva `touchPulse` e richiede un nuovo modello/percorso sensoriale da validare. Nessun apprendimento autonomo di presa, nessun APK/OTA, PR #87 resta Draft.

> **V3.11 — ESPERIMENTO MOTORIO IN LABORATORIO (17/10/2026):** [protocollo preregistrato e rapporti](docs/ZERO30_MOTOR_V311_BODY_FRAME_DYNAMICS.md) · [CI V3.11](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986991649). Nuovo stato corporeo nel riferimento anatomico (22 valori), 64 efferenze reali per frame sintetizzate in 24 caratteristiche, sensori fisici di adesione/contatto, previsioni di risposta del corpo e confronto A/B. **[CI V3.11 38986991649](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986991649): SUCCESS tecnico e notevole progresso predittivo su corpo libero, ma GATE COMPLETO FAIL perché contactFrames=0 e gripFrames=0.** Sul BLIND la MSE XY a 1 frame è 0,000000335 vs 0,000000511 ballistica; a 96 frame 0,023664 vs 0,082884 baseline immobile. La variante NO_GRIP_TOUCH è identica perché non ci sono contatti. Nessun apprendimento di locomozione autonoma provato, nessun merge/OTA.

> **V3.10 — TEST LOCALE PER FRAME (17/10/2026):** [Protocollo preregistrato](docs/ZERO30_MOTOR_V310_LOCAL_FRAME_DYNAMICS.md) · [GitHub Actions CI](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986306415). Nuovo modello di ricerca su 16 settori corporei + 64 comandi per frame, ablation geometria/azioni e rollout fino a 384 frame. **[CI 38986306415](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986306415) SUCCESS tecnico, gate CAL/BLIND FALLITI.** Su nuovi stati FULL XY1 MSE 0,000001303 vs ballistica 0,000000519 e traiettoria 96 frame 2,813636 vs zero 0,085789. Il modello con soli globali+azioni è molto migliore (0,000000490 one-frame), ma fallisce il gate 96-frame. Nessuna integrazione nel gioco, G3/G5 bloccati, nessun OTA.

> **V3.9 — PROTOTIPO DI TRANSIZIONI REALI (17/10/2026):** [ADR e protocollo preregistrato](docs/ZERO30_MOTOR_V39_OBSERVED_STATE_ACTION.md) · [CI esperimento](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38985522666). Il codice sperimentale registra lo stato del singolo corpo, 64 comandi effettivi e outcome ogni 12 frame; confronta previsione a un passo e rollout libero su stati con nastro di azioni future note. **CI 38985522666 SUCCESS tecnico, ma qualità FALLITA (CAL e BLIND).** In BLIND MSE XY a 1 blocco 0,002513731 (vs 0,002582060 velocità costante), e traiettoria 32 blocchi 3,116409 (vs 0,837473 baseline zero). Nessuna promozione. NON è apprendimento autonomo, non attivo in gameplay; G3/G5 bloccati, PR #87 DRAFT, **nessun OTA**.

> **V3.8-B APPRENDIMENTO DINAMICO VERIFICATO (17/10/2026):** [Rapporto e test su 983.040 passi fisici](docs/ZERO30_MOTOR_V38B_STATE_TRANSITIONS.md). Il modello autoregressivo a 32 transizioni **FALLISCE** il gate: errore finale su nuovi stati **0,16007** vs **0,02906** del modello statico. ZERO 3.0 NON ATTIVO, G3/G5 non superati, nessuna nuova OTA.


> **V3.8 VERIFICATA, GATE FALLITO:** [modello ricorrente delle traiettorie, test 983.040 frame fisici](docs/ZERO30_MOTOR_V38_RECURRENT_TRAJECTORY.md). La ricorrenza ha MSE ritardata blind **0,033926** contro baseline zero **0,029724** e time-only **0,029652**; previsione meno affidabile. Nessuna locomozione appresa né OTA 3.0: **G3 BLOCCATO**. [GitHub Actions verificata](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38983664233).


> **ULTIMO ESPERIMENTO MOTORIO V3.7:** [Memoria delle 96 contrazioni pre-azione, test 384-frame](docs/ZERO30_MOTOR_V37_TEMPORAL_MEMORY.md): codice e CI corretti, ma **gate di affidabilità FALLITO** sulle 256 prove non viste, miglioramento della memoria su modello statico solo **0,019%**. [V3.7-B: nuove pose con memoria normalizzata, GATE FALLITO](docs/ZERO30_MOTOR_V37B_TRAIN_SCALE.md) **verificata su 256 nuove prove**: MSE ritardata 0,035384825 contro 0,033472508 della baseline zero; **NON supera il gate**. **ZERO 3.0 NON ATTIVO, G3 BLOCCATO, OTA172 INVARIATA.**


> **ESITO V3.6 VERIFICATO:** [Predizione muscolare a 384 frame su 1.024 coppie fisiche](docs/ZERO30_MOTOR_V36_LONG_HORIZON.md): il modello 52 sensori migliora la previsione ma **non supera il gate di generalizzazione del 10% sui risultati ritardati** (miglioramento 7,28%). Nessun nuovo cervello 3.0 attivo, **G3 BLOCCATO e nessun OTA**.


> **ESITO V3.5 VERIFICATO:** [32 test a ciclo chiuso](docs/ZERO30_MOTOR_V35_ADAPTIVE_CREDIT.md) — astensione adattiva supera leggermente lo spostamento aggregato senza impulsi, ma **consuma di più e migliora soltanto 6/16 condizioni nuove**. **G3 NON SUPERATO, ZERO 3.0 NON ATTIVO E NESSUNA OTA.**


> **STATO SPERIMENTALE V3.5:** [Finestra di credito fisico 384 frame + test di astensione dopo feedback reale](docs/ZERO30_MOTOR_V35_ADAPTIVE_CREDIT.md). Il sistema **non è attivo in gameplay** e G3 resta BLOCCATO: i test positivi sugli impulsi isolati non valgono come locomozione autonoma.


> **ULTIMO STATO MOTOR V3.4 (17/10/2026):** [Corpi deformati + kernel condiviso + fallimento a ciclo chiuso (4/16)](docs/ZERO30_MOTOR_V34_DYNAMIC_STATES.md). Le previsioni degli impulsi isolati sono attendibili, **MA il modello guidato a ogni decisione peggiora locomozione e costo**. Sono attive nuove prove sul ritardo cumulativo. G3 e nuova UI V3 NON completati; niente OTA.


> **RISULTATO MOTORIO 3.3 (17/10/2026):** [Confronto fisico dei comandi, attribuzione dell'effetto ritardato, test su sei posture escluse dall'allenamento](docs/ZERO30_MOTOR_V33_DELAYED_RESPONSE.md). **24/24 piccoli interventi riusciti nel laboratorio**, previsione migliore di baseline; **NON dimostra locomozione autonoma o G3 completato**. Code branch sperimentale, nessuna nuova OTA.


> **NUOVO ESPERIMENTO MOTORIO V3.2:** [Ricerca muscolare con vincoli energetici e verifica su posture escluse dall'allenamento](docs/ZERO30_MOTOR_V32_COST_CONSTRAINED.md). Restano prototipo isolato e gate G3 bloccato finché non arrivano prove indipendenti.

> **TEST 2.000 ESPERIENZE VERIFICATI:** [Report completo motorio e autonomo, checkpoint 400/1000/2000](docs/ZERO30_LONG_HORIZON_2000_20261017.md). **G3 ANCORA BLOCCATO**: a 2.000 decisioni la corteccia non supera il vergine in nessuna delle 4 direzioni nella postura iniziale; misure coerenti ma negative. Nessun OTA V3.

> **APPRENDIMENTO MOTORIO V3.1 (ATTUALIZZAZIONE 16/10/2026):** [Documento tecnico, prove A/B, risultati e limiti](docs/ZERO30_MOTOR_V31_ENGINEERING.md). Il modello online iniziale è stato **rifiutato dal gate indipendente**; il progetto ricerca un apprendimento motorio effettivamente più efficace. Nessun OTA ZERO3 pubblicato.

> **SVILUPPO ZERO 3.0 IN CORSO:** [Stato reale, primi moduli Java, benchmark 400 esperienze e prossimi gate](docs/ZERO30_BUILD_STATUS_20261016.md) · [PR sperimentale #87 (DRAFT, non pubblicare OTA)](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

> **📌 CHATGPT / AI DEVELOPMENT — START HERE:** prima di modificare una sola riga relativa a OVERMIND ZERO, leggere i documenti di progettazione canonici su **main**. Questa documentazione è intenzionalmente visibile in cima al repository per consentire alle chat future di riprendere l'intero progetto, anche senza il contesto della conversazione originale.

**AGGIORNAMENTO MOTOR V3.1:** [Modifiche e risultati oggettivi, gate G3 bloccato](docs/ZERO30_MOTOR_V31_ENGINEERING.md) · [CI di verifica](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38972240462). Un test compilato non significa locomozione appresa; nessuna OTA V3 pubblicata.

## 📘 Progetto da realizzare: ZERO 3.0

| Ordine | Documentazione ufficiale | Contenuto |
|---|---|---|
| **1 — OBBLIGATORIO** | **[ZERO 3.0 — PROGETTO MASTER COMPLETO](docs/ZERO30_MASTER_BLUEPRINT.md)** | Requisiti, nuova architettura cognitiva, corpo e fisica, percezione, memoria, motor learning, world model, obiettivi, neurogenesi, salvaguardie |
| **2 — OBBLIGATORIO** | **[CONTRATTO DI IMPLEMENTAZIONE E TEST](docs/ZERO30_IMPLEMENTATION_CONTRACT.md)** | Tipi/contratti, check obbligatori, roadmap G0–G10, benchmark, criteri per avanzare, stato della realizzazione e handoff |
| **3 — OBBLIGATORIO** | **[NUOVA INTERFACCIA ZERO 3.0](docs/ZERO30_UI_UX_SPEC.md)** | Nuove schermate di cervello, neuroni 3D reali, abilità, evoluzione, corpo, percezione, memoria, analisi e **impostazioni V3 che sostituiscono quelle V2.2** |
| **4 — ISTRUZIONI AGENTI** | **[AGENTS.md](AGENTS.md)** · **[OVERMIND_START_HERE.md](OVERMIND_START_HERE.md)** | Regole per ChatGPT, conservazione dei dati, codice/stato OTA e cronologia |

**DIREZIONE DI PROGETTO:** mantenere mondo e corpo simbionte (con verifica degli eventuali bug fisici); rifare in maniera unificata il cervello e l'apprendimento; differenziare apprendimento delle decisioni, della locomozione, delle conseguenze e delle competenze; **niente camminata o caccia alla gemma scritta a mano**; rendere tutto osservabile nella nuova UI V3.

**STATO V3:** documentazione/spec, **non implementato né pubblicato**. La progettazione non è prova di performance: i passi iniziali sono baseline, controllabilità del corpo e prove di apprendimento autonomo da vergine.

## ✅ Ultima app firmata nella baseline documentale

- **OVERMIND ZERO 0.11.6-ota.172** — Android **1172**, applicationId \`com.overmind.zero.ota\`.
- GitHub [release firmata](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.172) e [publisher completato con successo](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38956731145).
- In app: **Impostazioni → Aggiornamenti → AGGIORNA**, mantenendo cervelli/12 slot. **Non disinstallare** per aggiornare.
- Per versioni successive **verificare** il manifest \`updater/public/latest.json\` di \`main\`; questo paragrafo è solo la baseline storica del 16/10/2026.
- **OTA172 non è ZERO 3.0.** Miglioramenti di locomozione autonomi dopo OTA172 non ancora dimostrati su telemetria Galaxy S21 al momento della redazione.

## 🔍 Per riprendere il lavoro in una nuova chat ChatGPT

1. Aprire questo repository GitHub via connessione GitHub ChatGPT.
2. Leggere integralmente le **tre specifiche ZERO 3.0** nell'ordine indicato, **AGENTS.md** e **OVERMIND_START_HERE.md**.
3. Verificare stato reale di commit, branch sorgente, QA, \`latest.json\`, report telemetria se aggiornati.
4. Aprire il [Contratto](docs/ZERO30_IMPLEMENTATION_CONTRACT.md) per conoscere il gate corrente e **continuare dalla fase non completata**.
5. Per ogni modifica descrivere test/competenze, conservazione dei checkpoint, impatti sulla **nuova UI 3.0** e rischi. Non dire che un progetto o un test è completo senza prove.

> **Nota operativa importante:** il codice che alimenta il publisher OTA è stato sviluppato nel ramo \`feature/zero22-active-recurrent-ota117\` (baseline 16/10/2026) e una scrittura sul suo ramo può avviare una build firmata. Verificare prima di operare. La documentazione **canonica** rimane su \`main\`: se si lavora in un altro ramo, leggerla comunque da \`main\`. Non pubblicare OTA solo per allineare documentazione.

## Codice, contesto e cronologia

Il repository conserva il codice Java/Android, la simulazione di ZERO, i documenti degli esperimenti storici in \`docs/\`, gli asset updater in \`updater/\`, e la cronologia di altri prototipi Android più vecchi (origine **Idle Runner / Aurora Dash**).

Per la storia del 2.2 consulta [ARCHITECTURE](docs/ARCHITECTURE.md), [ROADMAP](docs/ROADMAP.md) e gli audit \`docs/ZERO_*\`. Sono **storici** rispetto alla direzione ZERO 3.0 e non sostituiscono la nuova specifica.

**Prima di intervenire sul codice, la priorità è il MASTER BLUEPRINT, non la documentazione legacy in ordine cronologico.**

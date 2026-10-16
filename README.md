# OVERMIND ZERO — MASTER PROJECT ZERO 3.0

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

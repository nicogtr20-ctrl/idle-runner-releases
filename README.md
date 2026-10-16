# OVERMIND ZERO — MASTER PROJECT ZERO 3.0
> **SVILUPPO ZERO 3.0 IN CORSO:** [Stato reale, primi moduli Java, benchmark 400 esperienze e prossimi gate](docs/ZERO30_BUILD_STATUS_20261016.md) · [PR sperimentale #87 (DRAFT, non pubblicare OTA)](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

> **📌 CHATGPT / AI DEVELOPMENT — START HERE:** prima di modificare una sola riga relativa a OVERMIND ZERO, leggere i documenti di progettazione canonici su **main**. Questa documentazione è intenzionalmente visibile in cima al repository per consentire alle chat future di riprendere l'intero progetto, anche senza il contesto della conversazione originale.

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

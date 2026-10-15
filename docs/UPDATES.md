## OTA162 — CANDIDATA: MOTOR COORDINATION, 15 ottobre 2026

**Stato**: PR in preparazione, QA obbligatoria e successiva firma OTA NON ANCORA PUBBLICATA. Fino alla pubblicazione, OTA161 resta la release ufficiale. Report reale OTA161 `zero-telemetry/reports/20261015-202651-1411664defd37`: 81% di cambi di scelta tra decisioni adiacenti nell'ultima vita, 31% esplorazione, blackbox ~734U di percorso campionato ma ~63U di spostamento risultante; nuovi 320 pesi con media assoluta ~0.0000455, ~473 volte inferiore agli 84 originali. **Lentezza e antagonismo sono sospetti fondati, non ancora diagnosi certa di forze interne dai dati raccolti.**

**Modifiche branch `fix/zero162-sensorimotor-coordination`**: `SymbioteCoordinationV162` registra veri frame del baricentro dopo fisica, box e collisioni, misura l'annullamento vettoriale NOMINALE dei comandi sui 16 settori e la realizzazione della direzione scelta solo dall'attivo cervello 2.2. Apprendimento locale di 320 pesi su segnale di controllo sensorimotorio misurato: solo azioni motorie 0-7, grip 8-15 esclusi, heldout non aggiorna nulla, niente direzione al cibo, nessun reward positivo di distanza in core. Limiti al gradiente, esclusione grandi salti anomali, modelli `smh1` e `cl161` preservati. Nuovo persistente `symbiote_coordination_v162` con validazione atomica, corretto reset membrana e autosave. UI **Corpo → Corteccia muscolare → Coordinazione reale** distingue opposizione nominale da efficacia, non spaccia parametri per neuroni.

**Test**: `Zero162CoordinationRegression` con vera fisica `SymbioteMatterV1`, 640 step, movimento misurato, plasticità regionale e nessun credito alle prese/holdout/teleport, preserva ricordi e parametri vecchi; alla prima QA [run 38804260990](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38804260990) JVM+Android debug SUCCESS: netto ~21,26 U, indice opposizione NOMINALE ~0,786. Questo NON dimostra ancora miglioramento dell'azione autonoma sul telefono: misurare con nuovi report, preferibilmente 25+25 vite, NETTO/VIA, blu, episodi e metrica coordinazione.

**Riferimento completo**: `docs/ZERO_OTA162_MOTOR_COORDINATION_AUDIT.md`. **Non disinstallare né azzerare ZERO**. OTA161 ufficiale firmata, OTA162 disponibile solo dopo publisher firmato e verifica pubblica SHA256.

## ZERO OTA161 — corpo fluido e plasticità regionale (CANDIDATO, NON FIRMATO)

**Stato al 15 ottobre 2026:** ramo `fix/zero161-symbiote-physics-capability`, [PR #71](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/71). OTA160 **è stata firmata e pubblicata** (1160): manifest `updater/public/latest.json`. OTA161 è distinta, e NON sarà disponibile con AGGIORNA finché il publisher firmato non termina con successo.
- Verifica fisica su VERO `SymbioteMatterV1`: corpo a riposo quasi fermo, tre appendici realmente raggiungibili, retrazione indipendente, elasticità/topologia e locomozione misurate sul medesimo scenario; `Zero161PhysicalCapabilityRegression` e `Zero161OverstretchRegression` sono prove di laboratorio, **non andature preprogrammate**.
- Cortex 2.2 esistente preservato; 84 pesi `smh1` invariati. Aggiunti 320 **parametri allenabili** specifici per settore, inizializzati a zero, con base spaziale più espressiva e plasticità dalle vere conseguenze. Questi pesi **non sono 320 neuroni** e non modificano la neurogenesi del nucleo. Memoria separata `symbiote_cortex_local_v161`, salvataggi, 12 slot e firma preservati.
- UI Corpo/Parametri mostra read-only i 320 pesi regionali e i vecchi 84; nessuna ricompensa di spostamento imposta al cervello né bersaglio cibo conoscibile dall'attuatore.
- Prove pre-release: GitHub Actions [38777660956](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38777660956) **SUCCESS** JVM/Android Debug. Misure: in scenario test, 3 protrusioni, retrazione ~-9,99U, netto crawling 21,26U su 16s vs 13,73U originali; nessuna prova ancora della locomozione AUTONOMA sul Galaxy S21.
- Firma, publisher e manifest reale sono i gate successivi. **Non disinstallare l'app né azzerare il personaggio.** Paper tecnico: [ZERO_OTA161_BODY_PHYSICS_AUDIT](ZERO_OTA161_BODY_PHYSICS_AUDIT.md).

## CANDIDATO ZERO 2.2 OTA160 — 15 ottobre 2026 (NON PUBBLICATO)
Correzione sperimentale della durata motoria del simbionte, senza movimenti prefabbricati: il nucleo 2.2 rimane l'unico arbitro, un nuovo modello `MotorDurationV22` impara se mantenere per 1, 2 o 4 intervalli una sua azione, con interruzione immediata per danni, riserva critica, esplorazione o scelte neurali chiaramente migliori. Persistenza opzionale `motor_duration_v160`, stessa app/salvataggi, valori reali visibili in Corpo/Evoluzione. Baseline Android: report `20261015-152553-130aa19184fe2`, 95.3–96.6% di cambi d'azione su due vite, ultimi 25 episodi con calo della locomozione e aumento dei contatti rossi. QA preliminare del ramo `fix/zero160-learned-motor-duration` **non eseguita**: Actions run `38765532027` fallito prima di qualsiasi step (infrastruttura). Nessuna APK160 firmata o prestazione post-correzione rivendicata. Paper completo: [ZERO_OTA160_LEARNED_DURATION_FORENSICS](ZERO_OTA160_LEARNED_DURATION_FORENSICS.md). Non unire al ramo che pubblica l'APK finché QA e build Android non sono verdi.

## ULTIMO STATO VERIFICATO — ZERO 2.2 / OTA119 — 13 ottobre 2026, 23:20 CEST

Richiesta utente: APK pronta con centro decisionale rivisto attivo, conservazione del personaggio e ottimizzazione TEST MAX (misura precedente S21 circa x29). **Rilascio realmente concluso**: versione `0.11.6-ota.119`, codice1119, package `com.overmind.zero.ota`.
- Sorgente APK esatto: `582030833113f698933fbe83327c843e992a6ea0`, ramo `feature/zero22-active-recurrent-ota117`, [PR42 draft](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/42). Il nome del ramo conserva il numero candidato117; la release effettiva è119.
- Publisher [38469244134](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38469244134) **SUCCESS**: tutti71 entrypoint JVM, tutti26 gate Python originali aggiornati, Android Debug+Release firmata, permessi/provider, GitHub Release e manifest main. Il primo117/38467848821 aveva passato JVM/Debug ma falliva una stringa statica della stabilità motoria; non pubblicò APK.
- [APK pubblica](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1119-6593561d5593.apk),382708byte; SHA256 `6593561d5593d1b047b73022506be0203c9fe603b46f1f7de9cd7ba52b215b68`. Scaricata e hash/dimensione verificati. ApkVerifier verifica crittografica API24+: firme119 e116 valide, stesso certificato SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- Manifest pubblico HTTP200/code1119 verificato, Vercel productionREADY progetto `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, teamprime-1323, alias corretto, deployment `dpl_FhBaHF4P4WVcQ3u1ZpCCgRDEzS7z`, commit hosting `111dbcf3bafb6e35933e882620ab217a020712b9`. DEX pubblico contiene nucleo, esecutore skill, confronto esteso, observationDue e learningBlend.
- Installare SOPRA tramite Impostazioni→AGGIORNA APP o link diretto, **non disinstallare**. Prova APK→APK, dodici slot, velocità/temperatura sul S21 non ancora osservate.
- La nuova priorità comprende la migrazione graduale della policy esperta: senza evidenza nuova preserva scelte/epsilon storico; aumenta quota del nucleo con train reali. Non riavvia esplorazione vergine su ZERO esperto. Non ricompilare un ramo precedente perdendo questa correzione o il confronto esteso.
- Leggere [ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md), [ZERO22_PHYSICAL_SCREENING](ZERO22_PHYSICAL_SCREENING.md), [ZERO22_JVM_RESULTS](ZERO22_JVM_RESULTS.json). È un nucleo ricorrente leggero con reservoir fisso e readout appresi, non Dreamer completo/RSSM o intelligenza generale. Autorità prima dell'azione, memoria corpo+raggio, previsione energetica, replay recente/raro, recupero contestuale con feedback.
- Screening fisico vergine12pareggi. Primo checkpoint esperto0vittorie/1sconfitta/11pareggi, regressione di transizione documentata e corretta. Stesso checkpoint dopo correzione12pareggi su energia/danni/sopravvivenza, prese opposte-5.917frame medi. **Nessun uplift dimostrato**. Fixture sintetica37/40 scelte benefiche dimostra soltanto autorità/apprendimento del nucleo.
- Sul telefono ANALISI→LABORATORIO COMPORTAMENTALE (18 prove32s) e CONFRONTO2.2ESTESO/CASSE (24prove fino170s); copie isolate, report distinti inclusi in Condividi dati. TEST MAX rimane separato, conferma/STOP/thermal, substep1/60; sensi completi solo ai confini0.65s, nessun substep fisico saltato. Velocità maggiore di x29 non ancora misurata.
- Memorie/slot legacy preservati, chiave opzionale `recurrent_core_v22` atomica compressa. Riavvio conserva hidden e cancella solo pending non verificabile. Esperienze heldout e immaginate mai nel replay. Main conserva Idle Runner; soltanto hosting+paper aggiornati.


# OVERMIND ZERO — aggiornamenti Android / OTA

## Stato 12 ottobre 2026 — ZERO 0127

Il branch `feature/zero-0127-spatial-persistence-box-recovery` contiene modifiche sperimentali di ripristino fisica, memoria e bilanciamento. **Non esiste ancora una release APK firmata/verificata per ZERO 0127**. Conservare OTA 97 e gli slot nominati; non disinstallare, non azzerare. Prima della pubblicazione bisogna superare PR QA, Android debug, publisher firmato, verifica SHA-256 ed endpoint Vercel. Le bozze e i merge senza prova non sono release.

## Invarianti
- Package Android: `com.overmind.zero.ota`.
- Gli aggiornamenti devono essere installati **sopra** l'app esistente; non disinstallare se si vogliono conservare i dati.
- La firma APK deve restare la stessa fra versioni. Keystore e credenziali non devono mai entrare nel repository.
- Android ordinario richiede conferma utente per installare l'APK; nessun aggiornamento silenzioso viene promesso.
- Il publisher firmato ufficiale è `.github/workflows/overmind-ota-publish.yml` sul branch `feature/overmind-inapp-updater`.
- Prima di dire che il pulsante AGGIORNA offre una versione, verificare realmente manifest e APK pubblici.

## Stato 0.10.5-ota.54 — 10 ottobre 2026
La modifica “STATISTICHE 25-vs-25 + storico lifetime” è nello stable `feature/overmind-inapp-updater`.
- Staging QA: **38059707433**, verde.
- Publisher firmato: **38060026469**, verde.
- versionCode **1054**, versionName **0.10.5-ota.54**.
- SHA-256: `34bd2eb5261f499b196037832dce744f37b7b3822a49a7f655c3b6347f0c4e7a`.
- APK: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1054-34bd2eb5261f.apk`, **138.636 byte**.
- `https://overmind-zero-updates.vercel.app/latest.json` e APK verificati HTTP 200 dopo la pubblicazione.
È quindi corretto usare **AGGIORNA** nell'app. Installare sopra la versione esistente; non disinstallare.

## Stato 0.10.6-ota.55 — verificato
La memoria di lavoro temporale ha superato staging **38064278116** e **38064377747** e publisher firmato **38064599838**.
- versionCode **1055**, versionName **0.10.6-ota.55**.
- SHA-256 `bdc0dc5fd1dc9dce992d5a9cf139efd560421091f8ada01cec1bcbe47b48ff30`.
- APK: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1055-bdc0dc5fd1dc.apk`, **143.732 byte**.
- Manifest e APK verificati HTTP 200.
È corretto usare **AGGIORNA** nell'app. Installare sopra la versione esistente; non disinstallare.

## Stato 0.10.7-ota.56 — verificato
Skill temporali autonome validate su staging **38065646624** e publisher firmato **38066032322**.
- versionCode **1056**, versionName **0.10.7-ota.56**.
- SHA-256 `a433978f842054c71c2744075f3c5a0105a4bad2f825b7ba7d6a7d1629e2030e`.
- APK: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1056-a433978f8420.apk`, **149.392 byte**.
- Manifest e APK verificati HTTP 200.
È corretto usare **AGGIORNA** nell'app. Installare sopra la versione esistente; non disinstallare.

## Release preparation — ZERO 2.0 passive observation, OTA 100 candidate (2026-10-13)

Official OTA path is `feature/overmind-inapp-updater` → `.github/workflows/overmind-ota-publish.yml` → owner-key signed APK → GitHub Release → `main/updater/public/latest.json` and immutable APK → Vercel manifest consumed by in-app AGGIORNA. This candidate is staged separately from research PR #31. Original `com.overmind.zero.ota` signature and persistent 12 saved slots must remain intact. VersionCode derives from the next official publisher run number (expected ≥1100), not the old signed experimental preview 1099.

The candidate includes exactly the already tested v20 passive observers, existing base motor policy, and `TelemetryGitHub.partition()` fix preserving `extras.zero20Passive` after multipart upload. No model advice biases the motor controller; no change to energy rules or sensor physics. Source-pinned stable checks are UPDATED to the SHA of the actually reviewed source files, never disabled; five new JVM regressions are added before official release signing.

The initial Galaxy S21 report confirms the original working profile is still present and v20 models have collected real observations. No independent skill or survival improvement is established. Release acceptance: independent staging JVM/Android QA GREEN, official stable publisher GREEN, signed APK certificate/package/version and SHA verified, downloadable manifest/APK responsive, user-controlled install and slot continuity confirmed on phone. If any gate fails, **do not claim availability through Aggiorna**.

## ZERO 2.0 — Box-grip retention candidate (13 ottobre 2026)

The OTA102 phone reports showed rapid accepted mutations of all hand/membrane weights with no box-specific skill retention. This candidate adds *real physics measured* box-touch and simultaneous opposing-grip evidence to the live online trial evaluator. When a previous hand/membrane trial demonstrably sustained a box grip, a replacement cannot win only by collecting energy or exploring elsewhere; it must show comparable valid box-contact/grip evidence. No prerecorded grip actions, free energy, changes to the bilateral box law, or forced motor direction. Earlier controller save formats and named slots are unchanged. Research [BoxGripRetentionRegression] tests baseline-with-proven-hold vs an exploratory but unproven replacement and preservation of valid alternative learning. Real-world effect not yet established; on-device A/B and saved profile verification pending. Release only after full staging Java+APK QA, official publisher signed gates, manifest, and in-app installer. Research details: `docs/ZERO_20_INTELLIGENCE_RESEARCH.md` in `feature/zero-20-hierarchical-intelligence`.

## 2026-10-13 — vincolo distribuzione ZERO2 percezione
`feature/zero20-perceptual-associations` è soltanto ricerca; non pubblicare APK, manifest Vercel o OTA da questo branch. La versione pubblica resta `0.11.6-ota.103` (package `com.overmind.zero.ota`, code 1103). Non installare APK sperimentali su profili esistenti senza verifica firma/code, backup di slot e approvazione esplicita. Sia i prototipi percettivi sia la loro telemetria sono facoltativi per i salvataggi storici.

**OTA applicativa:** questo modulo percettivo passivo è stato selezionato per il canale AGGIORNA come candidato successivo alla 103. La build ufficiale resta soggetta a Java/Android QA, stessa firma e pubblicazione SHA/manifest prima di dirla disponibile.


## 13 ottobre 2026 — ZERO 2.2 attivo (candidato OTA117)
Richiesta utente: cervello rivisto funzionante e APK, x5/x20 fluidi, TEST MAX circa x29. Nuovo ramo `feature/zero22-active-recurrent-ota117` da `fe6d7497eb5b53055b46c474dd5470028bc97f88`. Leggere **[ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md)** per architettura, contratto sensoriale, replay, competenze, ottimizzazione TEST MAX e limitazioni. `RecurrentCoreV22` guida realmente la scelta prima dell'azione; i moduli 2.1 restano osservatori compatibili. Nessun reset del profilo o slot. 71 test JVM locali PASS; release, Android build e prova S21 da verificare separatamente. Non chiamare questo sistema Dreamer completo o intelligenza generale.

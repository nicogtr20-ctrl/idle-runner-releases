# ZERO 3.0 — PRIORITÀ MASTER E CAPACITÀ APPROVATE (18/10/2026)

> **REGOLA OBBLIGATORIA PRIMA DI OGNI MODIFICA:** leggere [MASTER](docs/ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](docs/ZERO30_IMPLEMENTATION_CONTRACT.md), [UI G8](docs/ZERO30_UI_UX_SPEC.md), [REGISTRO APPROVAZIONI](docs/ZERO30_VERIFIED_CAPABILITY_LEDGER_20261018.md) e [TRACKER G1–G10](docs/ZERO30_G1_G10_EXECUTION_TRACKER_20261018.md), tutti ora copiati **byte-identici dal ramo main**. Confrontare requisito→classe reale→caller V325→CI preregistrata→solo gap effettivo. Non riscrivere GRU64 V345, grip appreso V319, replay V328, o attività dimostrate; non promuovere physics V346 fallita. V348 GRU+organismo cold resume PASS; V349 GRU 4100 vita lunga PASS e capienza codec 10k PASS; V349B 10k reali recuperati nel primo run, test completo di fault in riesecuzione; V350 blocco credito duration non eseguita PASS. Tutti **gate G1–G10 ancora aperti nel complesso**: no merge, APK, OTA, nessuna alterazione 2.2 OTA172, 12 slot intatti.

> **NOTA:** il testo seguente contiene release e procedure storiche precedenti al Master 3.0. Non prevale sulla documentazione canonica del progetto.

---

# Verified release — OTA171 / Android1171, runtime ZERO172 — 16 October 2026

Published and independently downloaded from Vercel and GitHub: **0.11.6-ota.171**, code1171, `com.overmind.zero.ota`,502916 bytes. APK SHA256 `62e0b5a96916fc8005d5e21aa91de4194ba57ec8b8c1ebf6f27c5bc2a51a0bb0`; original stable certificate `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`. Both public manifests/bytes match, HTTP200; APK v2 RSA signature, signed content digest and packaged Android identity/permissions independently verified. Update with **AGGIORNA**, without uninstalling or resetting brains/slots.

Fixes: safe motor experiments complete before random replacement; incomplete durations cannot train a completed-duration label; current context distinguishes signed residual velocity and reclaims obsolete controller-epoch capacity. New passive `motor_response_journal_v172` records actual command response and does not actuate or train. Read [ZERO172 timing audit and correction](docs/ZERO_172_PHYSICAL_COMMAND_TIMING.md) for full causal reproductions and before/after results, including rejected changes and adverse cases.

**Mixed behavioral results: no general walking/navigation solution claim.** Mean net displacement improves in four open resource scenes, but panel direction worsens and one baseline pickup is lost. Corrected screens still have0 pickups. This release fixes reproduced code defects and enables precise phone verification; on-device walking/learning efficacy remains open. No authored gait, target coordinate, fake reward, forced confidence, brain reset or changed world generation. All97 JVM entrypoints,8 staging gates,35 publisher source gates, Gradle debug/release and stable signing checks PASS.

[Review #84](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/84) · [Signed publisher](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38937559068) · [Release](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.171). Earlier statuses below are historical and superseded by this block. Runtime172 and OTA171 are different numbering systems.

<!-- Current authoritative release status; older sections below are historical. -->
# Verified release — OTA170 / Android1170, runtime ZERO171 — 16 October 2026

The user explicitly authorized source and signed APK OTA publication. **Published and downloaded from BOTH Vercel and GitHub: `0.11.6-ota.170`, versionCode `1170`, package `com.overmind.zero.ota`.** The runtime correction revision **171** is not the OTA number. Install through **AGGIORNA**, over the existing app; do not uninstall or reset saved brains/slots.

APK: 497912 bytes, SHA256 `5fd2bb087e55fe7eae4d5b74e5ce7b7c31106bf71eb2077f6c1ee4ed915ba02e`. Original stable certificate SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583` matched; APK v2 signature AND signed content digest independently verified on both public downloads. Both manifests and exact bytes match, HTTP200. Main Idle Runner app/build/settings remain unchanged.

[Release](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.170) · [successful signed publisher](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38926908265) · [integrated source review #83](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/83).

Read [ZERO171 integrated correction and release evidence](docs/ZERO_171_INTEGRATED_SENSORIMOTOR_CORRECTIONS.md) for all audit findings, integrated fixes, optional persistence keys, verification and behavioral limits. 96 JVM entrypoints, seven staging integration gates, 34 publisher source gates, Gradle debug/release builds and original signing identity passed. One living recurrent cortex and 724 motor weights; no authored gait, food coordinate, forced confidence or brain reset. **Improved autonomous locomotion/food seeking on the Galaxy S21 is still unproven.** Preserve that distinction in future work. Earlier publication holds and version/status claims below are superseded by this verified release.

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
- Firma, publisher e manifest reale sono i gate successivi. **Non disinstallare l'app né azzerare il personaggio.** Paper tecnico: [ZERO_OTA161_BODY_PHYSICS_AUDIT](docs/ZERO_OTA161_BODY_PHYSICS_AUDIT.md).

## CANDIDATO ZERO 2.2 OTA160 — 15 ottobre 2026 (NON PUBBLICATO)
Correzione sperimentale della durata motoria del simbionte, senza movimenti prefabbricati: il nucleo 2.2 rimane l'unico arbitro, un nuovo modello `MotorDurationV22` impara se mantenere per 1, 2 o 4 intervalli una sua azione, con interruzione immediata per danni, riserva critica, esplorazione o scelte neurali chiaramente migliori. Persistenza opzionale `motor_duration_v160`, stessa app/salvataggi, valori reali visibili in Corpo/Evoluzione. Baseline Android: report `20261015-152553-130aa19184fe2`, 95.3–96.6% di cambi d'azione su due vite, ultimi 25 episodi con calo della locomozione e aumento dei contatti rossi. QA preliminare del ramo `fix/zero160-learned-motor-duration` **non eseguita**: Actions run `38765532027` fallito prima di qualsiasi step (infrastruttura). Nessuna APK160 firmata o prestazione post-correzione rivendicata. Paper completo: [ZERO_OTA160_LEARNED_DURATION_FORENSICS](docs/ZERO_OTA160_LEARNED_DURATION_FORENSICS.md). Non unire al ramo che pubblica l'APK finché QA e build Android non sono verdi.

## ULTIMO STATO VERIFICATO — ZERO 2.2 / OTA119 — 13 ottobre 2026, 23:20 CEST

Richiesta utente: APK pronta con centro decisionale rivisto attivo, conservazione del personaggio e ottimizzazione TEST MAX (misura precedente S21 circa x29). **Rilascio realmente concluso**: versione `0.11.6-ota.119`, codice1119, package `com.overmind.zero.ota`.
- Sorgente APK esatto: `582030833113f698933fbe83327c843e992a6ea0`, ramo `feature/zero22-active-recurrent-ota117`, [PR42 draft](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/42). Il nome del ramo conserva il numero candidato117; la release effettiva è119.
- Publisher [38469244134](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38469244134) **SUCCESS**: tutti71 entrypoint JVM, tutti26 gate Python originali aggiornati, Android Debug+Release firmata, permessi/provider, GitHub Release e manifest main. Il primo117/38467848821 aveva passato JVM/Debug ma falliva una stringa statica della stabilità motoria; non pubblicò APK.
- [APK pubblica](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1119-6593561d5593.apk),382708byte; SHA256 `6593561d5593d1b047b73022506be0203c9fe603b46f1f7de9cd7ba52b215b68`. Scaricata e hash/dimensione verificati. ApkVerifier verifica crittografica API24+: firme119 e116 valide, stesso certificato SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- Manifest pubblico HTTP200/code1119 verificato, Vercel productionREADY progetto `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, teamprime-1323, alias corretto, deployment `dpl_FhBaHF4P4WVcQ3u1ZpCCgRDEzS7z`, commit hosting `111dbcf3bafb6e35933e882620ab217a020712b9`. DEX pubblico contiene nucleo, esecutore skill, confronto esteso, observationDue e learningBlend.
- Installare SOPRA tramite Impostazioni→AGGIORNA APP o link diretto, **non disinstallare**. Prova APK→APK, dodici slot, velocità/temperatura sul S21 non ancora osservate.
- La nuova priorità comprende la migrazione graduale della policy esperta: senza evidenza nuova preserva scelte/epsilon storico; aumenta quota del nucleo con train reali. Non riavvia esplorazione vergine su ZERO esperto. Non ricompilare un ramo precedente perdendo questa correzione o il confronto esteso.
- Leggere [ZERO22_ACTIVE_CORE](docs/ZERO22_ACTIVE_CORE.md), [ZERO22_PHYSICAL_SCREENING](docs/ZERO22_PHYSICAL_SCREENING.md), [ZERO22_JVM_RESULTS](docs/ZERO22_JVM_RESULTS.json). È un nucleo ricorrente leggero con reservoir fisso e readout appresi, non Dreamer completo/RSSM o intelligenza generale. Autorità prima dell'azione, memoria corpo+raggio, previsione energetica, replay recente/raro, recupero contestuale con feedback.
- Screening fisico vergine12pareggi. Primo checkpoint esperto0vittorie/1sconfitta/11pareggi, regressione di transizione documentata e corretta. Stesso checkpoint dopo correzione12pareggi su energia/danni/sopravvivenza, prese opposte-5.917frame medi. **Nessun uplift dimostrato**. Fixture sintetica37/40 scelte benefiche dimostra soltanto autorità/apprendimento del nucleo.
- Sul telefono ANALISI→LABORATORIO COMPORTAMENTALE (18 prove32s) e CONFRONTO2.2ESTESO/CASSE (24prove fino170s); copie isolate, report distinti inclusi in Condividi dati. TEST MAX rimane separato, conferma/STOP/thermal, substep1/60; sensi completi solo ai confini0.65s, nessun substep fisico saltato. Velocità maggiore di x29 non ancora misurata.
- Memorie/slot legacy preservati, chiave opzionale `recurrent_core_v22` atomica compressa. Riavvio conserva hidden e cancella solo pending non verificabile. Esperienze heldout e immaginate mai nel replay. Main conserva Idle Runner; soltanto hosting+paper aggiornati.


# OVERMIND: ZERO — punto di ingresso per tutte le nuove chat

> Ultimo aggiornamento della documentazione: 12 ottobre 2026.
>
> **OBIETTIVO DEL PROGETTO:** creare su Android una forma di vita artificiale ("ZERO"), rappresentata da una macchia nera simile a ferrofluido, con corpo deformabile e apprendimento autonomo da esperienze in un mondo simulato. Il giocatore è una presenza invisibile, non impartisce ordini alla creatura e ne modifica il mondo.

## Ripresa immediata — ZERO 0127

Per la versione osservata dall'utente (OTA 97), il fix recupero memoria, l'isolamento delle prese reali delle lastre e delle vecchie casse energetiche, il bilanciamento prudente delle tempeste e la pipeline di test non ancora eseguita, iniziare da [EXPERIMENT_0127](docs/EXPERIMENT_0127.md) e dalla sezione aggiornata di [HANDOFF](docs/HANDOFF.md). Il rilascio APK non è ancora certificato.

## Leggi in quest'ordine
1. [Whitepaper](docs/WHITEPAPER.md) — visione e limiti scientifici.
2. [Architettura](docs/ARCHITECTURE.md) — cervello, fisica, memoria.
3. [Roadmap](docs/ROADMAP.md) — priorità e criteri di accettazione.
4. [Changelog](docs/CHANGELOG.md) — storico delle versioni.
5. [Handoff per nuove chat](docs/HANDOFF.md) — branch, workflow, regole operative.
6. [Aggiornamenti Android/Vercel](docs/UPDATES.md) — firma stabile, OTA e manifest.

## Repository e rami
Il repository `nicogtr20-ctrl/idle-runner-releases` è **privato** e contiene un altro prototipo Android nel branch `main`; per OVERMIND sono stati usati **branch sperimentali separati**. Non confondere il codice in `main` (Idle Runner) con OVERMIND.
- `experiment/overmind-zero-v01`: simulazione iniziale con agenti e Q-learning.
- `experiment/overmind-zero-brain-v02`: ZERO-001, corpo elastico e cervello neurale casuale.
- `experiment/overmind-zero-world-v03`: ZERO-002, oggetti ignoti e memoria degli effetti. **Base applicativa attualmente validata su telefono**.
- `experiment/overmind-zero-physics-v04`: branch creato ma sviluppo del gameplay **sospeso** finché updater e documentazione non sono pronti.

**Server Vercel attivo:** https://overmind-zero-updates.vercel.app/latest.json, manifest attualmente disabilitato finché non è disponibile un APK firmato. **Branch client updater:** `feature/overmind-inapp-updater`.

**Priorità attuale:** **v0.4.1-ota.16** firmata e pubblicata via Vercel, con mondo senza reset temporizzati e prove visibili dello spostamento degli oggetti. In attesa di test su Galaxy S21. Il precedente test v0.4.0 ha mostrato reset ogni 26s e contatore degli urti fuorviante. Codice più recente in `feature/overmind-inapp-updater`, APK/manifest in https://overmind-zero-updates.vercel.app/latest.json. Verificare il confronto `CONFRONTA` prima di avanzare oltre.

Il codice, gli asset e i release APK restano di proprietà e sotto controllo del repository privato. Non pubblicare automaticamente codice sorgente o chiavi di firma.

## Stato più recente (2026-10-09)
- **ZERO v0.5.0-ota.18**: cervello di selezione degli stimoli appreso dalle esperienze, 91 parametri vergini, vecchia formula di selezione disattivata. Codice: `feature/overmind-inapp-updater`. Test e build firmata verificati.
- Il server Vercel ha esaurito il limite gratuito di deployment giornalieri: il manifest pubblico potrebbe restare alla 0.4.1 finché il servizio non riprende.
- Download temporaneo della APK firmata nel repository privato: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.5.0-ota.18. Installare sopra, non disinstallare.
- Per dettagli su verifiche, rischi, test e prossimi passi leggere la sezione ZERO-005 in `docs/HANDOFF.md`.

## Stato più recente — ZERO-006 / 2026-10-09
- Nuova pagina **CERVELLO** a tre schede (abilità, parametri originali/attuali, evoluzione), file `BrainStatsActivity.java` sul branch `feature/overmind-inapp-updater`. Rete DecisionBrain dinamica 91→217, migrazione v1/v2 e conservazione del modello.
- APK firmata v0.6.0-ota.19 pronta: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.6.0-ota.19 . Installare sopra l'app esistente, non disinstallare.
- Manifest su GitHub avanzato a v0.6.0-ota.20 ma produzione Vercel ancora alla v0.4.1 per limite di deploy del piano. Consultare docs/HANDOFF.md, NON dichiarare v0.6 già disponibile tramite pulsante AGGIORNA finché non verificato pubblicamente.

## Stato aggiornato: ZERO-007 / 2026-10-09
- **v0.7.0-ota.22**: corpo con 32 masse perimetrali deformabili e rete di 74 pesi locali (nessun braccio predefinito), nuovi strumenti nella pagina CERVELLO. Vecchio sistema motorio (62), manipolazione (18) e decisioni (91+) preservati. Codice su `feature/overmind-inapp-updater` e ramo `feature/overmind-v07-plastic-body`.
- Distribuzione OTA Vercel **verificata online**: https://overmind-zero-updates.vercel.app/latest.json (1022); APK firmata https://overmind-zero-updates.vercel.app/apk/overmind-zero-1022-74610481e450.apk . Premere `AGGIORNA` nell'app corrente; non disinstallarla.
- Prima prova effettiva sul Galaxy S21 in attesa. Priorità: filmato di sporgenze locali e valore ALLUNGAMENTO, numero TEST della morfologia dopo addestramento, parametri MEMBRANA appresi. Per dettagli leggere docs/HANDOFF.md.

## Ultimo sviluppo — ZERO-008 / 2026-10-09
- Branch `feature/overmind-v08-scarcity-grasp-chamber`: membrana a 96 nodi, energia finita, emergenza di nuove risorse rara, camera con porta scorrevole che richiede contatti fisici su due lati, e addestramento mirato delle capacità di manipolazione.
- La build viene firmata tramite `feature/overmind-inapp-updater`, package invariato `com.overmind.zero.ota`; verificare https://overmind-zero-updates.vercel.app/latest.json prima di usare AGGIORNA, salvando la precedente memoria. L'installazione su Galaxy S21 è da validare.
- Leggere `docs/HANDOFF.md` per dettagli tecnici e limiti; NON dire che ZERO ha imparato a usare due braccia finché non si vede spostare e superare la porta senza spinte prefissate.

### Stato confermato 0.8.0-ota.23
- **Nuova release OTA online verificata:** https://overmind-zero-updates.vercel.app/latest.json (versionCode 1023) e APK https://overmind-zero-updates.vercel.app/apk/overmind-zero-1023-941873ee1d08.apk (63.556 byte). Puoi usare AGGIORNA in-app senza disinstallare; la vecchia memoria rimane.
- Dopo aggiornamento: attendere scarsità oppure toccare test stanza nel HUD; verificare PRESE / PORTA / STANZE RISOLTE e comportamento reale vicino al blocco, non solo i numeri di training. Dettagli completi in docs/HANDOFF.md.

## Ultimo rilascio verificato — 0.8.1-ota.24 (2026-10-09)
- Corrette collisioni nella stanza: tutte le porzioni di membrana devono rispettare il blocco, anche tra punti consecutivi; punti veloci fermati sulla faccia d'impatto; rendering aderente ai 96 segmenti fisici.
- **La morte per energia zero e il ritorno al centro sono intenzionali** e non sono stati rimossi (chiarimento utente).
- Installazione in-place: AGGIORNA o https://overmind-zero-updates.vercel.app/apk/overmind-zero-1024-0e10bb4ce202.apk; stessa firma e salvataggi.
- Per il report tecnico completo vedere `docs/HANDOFF.md`. Attende prova manuale Galaxy S21, soprattutto presa a due facce.

## Stato più recente (2026-10-09): v0.8.3-ota.27
- Nuova presa neurale sui 96 punti della membrana, 8 pesi aggiuntivi (74→82); vecchi pesi e apprendimento preservati. Doppio contatto da solo NON sposta più il cancello.
- Versione firmata in GitHub Release privata: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.8.3-ota.27 ; installare SOPRA app esistente senza disinstallare.
- Il manifest GitHub è aggiornato alla 1027, mentre Vercel pubblico era ancora alla 1024 al controllo; verificare prima di promettere AGGIORNA. Test fisici/JVM e build Android OK, prova su Galaxy S21 pendente. Leggere `docs/HANDOFF.md` per dettagli.

## Stato più recente — ZERO-009 / v0.9.0-ota.29 (2026-10-09)
- **Nuova autonomia sensori→azioni→conseguenze:** 26 canali sensoriali, 16 azioni motorie/adesive, 11.664 pesi inizialmente vergini con memoria a lungo termine `sensorimotor_model_v09`. Nessun calcolo di target heading nella vita di ZERO; memoria dei cervelli precedenti conservata. Il pre-addestramento offscreen precedente è messo in pausa.
- Signed CI superata, manifest GitHub v0.9.0-ota.29 versione 1029. Il manifest pubblico Vercel al controllo è fermo alla 1024; serve Release privata GitHub come fallback finché OTA non riprende.
- Per studio e limiti leggere `docs/HANDOFF.md`; per installazione puntare alla nuova release una volta creata, senza disinstallare l'app attuale.

## Stato più recente — ZERO-009.1 (2026-10-09)
- Signed **v0.9.1-ota.32** 1032, GitHub Release privata: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.9.1-ota.32 .
- Nuova pagina ANALISI accessibile dal gioco e dal fondo di CERVELLO, con confronto errori fuori training e reset selettivi (7 sezioni) più reset totale a doppia conferma.
- Toggle FERMA/ADDESTRA sostituito da ANALISI; apprendimento del modello mondo sempre attivo quando la simulazione gira. Memorie precedenti conservate.
- Vercel pubblico risultava ancora alla 0.8.1 per deploy stale: non usare AGGIORNA per il 0.9.1 finché non verificato live. Test manuale S21 pendente.
- Lettura completa in `docs/HANDOFF.md`.

## Ultima versione — ZERO-009.2 (2026-10-09)
- Firmata `0.9.2-ota.34` (`versionCode=1034`), branch ufficiale `feature/overmind-inapp-updater`, APK in manifest GitHub `updater/public/latest.json`. GitHub Release fallback da verificare, server Vercel pubblico potenzialmente fermo; NON promettere il tasto AGGIORNA senza prova live.
- Addestramento di corpo, manipolazione e membrana avviene in condizioni realmente vissute (finestre comparative approssimate) anziché tramite i vecchi rollout con obiettivo noto. I vecchi pesi e le memorie restano. Consumo energetico rallentato, danno rosso ridotto, stanza senza stimoli ordinari e risorsa rara viola/oro +80 soltanto all'interno.
- **Per dettagli aggiornati e limiti sperimentali leggere l'ultima sezione di docs/HANDOFF.md.** Test sul Galaxy S21 ancora richiesto.

## Ultimo aggiornamento — 0.9.4-ota.36 (2026-10-09)
- Ultima APK firmata (package com.overmind.zero.ota): versionCode 1036, sha256 `c44377b633854bee3b1ea3ad9715e8499f7e4202ae956cdd84ea85fd44b3c58d`.
- Staging `feature/overmind-v094-efficient-exploration`; stable `feature/overmind-inapp-updater`. CI ufficiale firmata 37999520662 superata.
- ZERO conserva pesi e memoria della 0.9.3, non richiede azzeramento; cambia la valutazione degli esperimenti corporei per non premiare giri in tondo inutili. NETTO, VIA, ZONE NUOVE, EFFICIENZA in HUD; ANALISI conta accettate/scartate; PROVE sostituisce il vecchio comparatore sintetico. Leggere HANDOFF per limitazioni e test S21; Vercel OTA pubblico può essere in ritardo, GitHub Release fallback verificata da usare.


## OVERMIND ZERO 0.9.5 — memoria episodica delle conseguenze (10 ottobre 2026)
**Prima lettura obbligatoria: `docs/EXPERIMENT_0095.md`** (resoconto dettagliato delle screenshot, versioni, codice, stato dei test e criteri del prossimo esperimento).
Il 9-10 ottobre, Galaxy S21 ha mostrato ZERO che sposta una porta fino a **111U** (la soglia per passaggio interamente libero è **>115U**), effettua 268 prese, sposta 93 dischi, ma non entra nella stanza e, dopo la morte, non ripete la tattica. Questo è comportamento fisico reale, NON prova che ZERO abbia compreso la porta.
La v0.9.5 crea ricordi di sequenze **effettivamente vissute** (fino a 9 sensazioni/azioni) dopo conseguenze fisiche, con snapshot dei 62+18+82 pesi corporei in uso; ricorda situazioni sensoriali simili e suggerisce azioni, senza imporre script o una direzione. Il modello predittivo da 11.664 parametri, le memorie v0.9.4 e i salvataggi restano compatibili. Aggiornamento dell'APK `com.overmind.zero.ota` in place e firma stabile obbligatori.
**Non azzerare i cervelli.** La memoria episodica parte vuota; non può ricostruire i 111U della prova storica. I test automatici verificano correttezza, non successo autonomo della porta. Prossimo test: episodio importante → morte → sensi simili → richiamo → nuovo effetto fisico → eventualmente ingresso/energia.

## Release verificata — ZERO 0.10.5-ota.54 / statistiche robuste (10 ottobre 2026)
- Branch di prova: `feature/overmind-v105-lifetime-statistics`, derivato da `feature/overmind-inapp-updater`.
- La pagina **STATISTICHE** separa ora tre scale: vita in corso, capacità attuale come media delle **ultime 25 vite**, e storico permanente.
- Il trend principale è **25 vite recenti vs 25 precedenti** e si attiva soltanto con 50 vite complete, per non scambiare poche casualità per miglioramento.
- `LifeStatistics` conserva ancora le ultime 50 vite individuali per grafici e confronto, ma aggiunge somme/record permanenti: dopo la 51ª vita le medie storiche non perdono più i campioni vecchi.
- Migrazione `ls1 -> ls2` senza cambiare la chiave SharedPreferences `life_statistics_v104`. Per ZERO già esistenti il passato precedente alla telemetria non viene inventato: l'interfaccia dichiara lo storico come incompleto.
- Tutta la statistica resta **observer-only**: non entra in reward, policy, world model, memoria episodica o selezione delle azioni.
- QA staging: run **38059707433** verde. Publisher stabile firmato **38060026469** verde.
- **OTA pubblico verificato HTTP 200**: versionCode **1054**, versionName **0.10.5-ota.54**, SHA-256 `34bd2eb5261f499b196037832dce744f37b7b3822a49a7f655c3b6347f0c4e7a`, APK **138.636 byte**. Installare sopra l'app esistente; non disinstallare.

## Release verificata — ZERO 0.10.6-ota.55 / memoria di lavoro temporale (10 ottobre 2026)
- Obiettivo: dare a ZERO uno stato interno del **prima/dopo** senza insegnargli concetti come porta, trascina, cibo o soluzione.
- Nuovo `TemporalWorkingMemory`: **16 stati ricorrenti** e **7.344 pesi appresi** di predizione/valore contestuale. Il reservoir ricorrente usa soltanto variazioni dei 26 sensi e azioni realmente scelte; nessuna identità di oggetto o coordinata obiettivo.
- Due situazioni con sensi istantanei uguali possono produrre stati interni diversi se la storia recente è diversa.
- Il contesto NON influenza le decisioni all'inizio: serve almeno 25 transizioni held-out e l'errore temporale deve battere il modello one-step di almeno il 4%. Bias massimo sulla scelta: ±0,45.
- Conseguenze fisiche/energetiche realmente osservate possono dare credito alle ultime 8 azioni contestualizzate; nessuna ricompensa immaginata.
- Alla morte si cancella solo lo stato di lavoro della vita corrente; i pesi appresi restano. Riavvio/salvataggio della stessa vita conserva il contesto tramite `temporal_working_memory_v106`.
- La pagina **STATISTICHE 25-vs-25 della 0.10.5 non viene modificata**. In CERVELLO compare soltanto una scheda diagnostica della nuova memoria.
- Staging: run **38064278116** e **38064377747** verdi. Publisher firmato **38064599838** verde.
- **OTA pubblico verificato HTTP 200**: versionCode **1055**, versionName **0.10.6-ota.55**, SHA-256 `bdc0dc5fd1dc9dce992d5a9cf139efd560421091f8ada01cec1bcbe47b48ff30`, APK **143.732 byte**. Aggiornare in-place; non disinstallare.

## Release verificata — ZERO 0.10.7-ota.56 / skill temporali autonome (10 ottobre 2026)
- Nuovo `TemporalSkillBank`: fino a **24 skill**, ciascuna composta da **2–8 azioni realmente scelte** insieme al contesto ricorrente della 0.10.6.
- Una singola conseguenza positiva crea soltanto candidati dormienti. Una sequenza può influenzare le decisioni solo dopo almeno **due ricorrenze compatibili** con conseguenze reali.
- Il replay non forza ZERO: fornisce un bias limitato alla policy. Se ZERO sceglie altro o il contesto vissuto diverge da quello appreso, la skill viene interrotta.
- Una skill completata viene rinforzata soltanto se compare di nuovo un effetto positivo reale entro la finestra prevista; assenza dell'effetto o danno riducono affidabilità.
- Nessun ingresso/etichetta per porta, cancello, oggetto o procedura. La banca vede soltanto hidden temporale + azioni + conseguenze fisiche/energetiche.
- Persistenza `temporal_skills_v107`; morte cancella solo esecuzione/trace corrente. Reset indipendente **ABILITA TEMPORALI**.
- STATISTICHE 25-vs-25 e memoria temporale 0.10.6 restano inalterate.
- Staging run **38065646624** verde; publisher firmato **38066032322** verde.
- **OTA pubblico verificato HTTP 200**: versionCode **1056**, versionName **0.10.7-ota.56**, SHA-256 `a433978f842054c71c2744075f3c5a0105a4bad2f825b7ba7d6a7d1629e2030e`, APK **149.392 byte**. Aggiornare in-place; non disinstallare.

## Release preparation — ZERO 2.0 passive observation, OTA 100 candidate (2026-10-13)

Official OTA path is `feature/overmind-inapp-updater` → `.github/workflows/overmind-ota-publish.yml` → owner-key signed APK → GitHub Release → `main/updater/public/latest.json` and immutable APK → Vercel manifest consumed by in-app AGGIORNA. This candidate is staged separately from research PR #31. Original `com.overmind.zero.ota` signature and persistent 12 saved slots must remain intact. VersionCode derives from the next official publisher run number (expected ≥1100), not the old signed experimental preview 1099.

The candidate includes exactly the already tested v20 passive observers, existing base motor policy, and `TelemetryGitHub.partition()` fix preserving `extras.zero20Passive` after multipart upload. No model advice biases the motor controller; no change to energy rules or sensor physics. Source-pinned stable checks are UPDATED to the SHA of the actually reviewed source files, never disabled; five new JVM regressions are added before official release signing.

The initial Galaxy S21 report confirms the original working profile is still present and v20 models have collected real observations. No independent skill or survival improvement is established. Release acceptance: independent staging JVM/Android QA GREEN, official stable publisher GREEN, signed APK certificate/package/version and SHA verified, downloadable manifest/APK responsive, user-controlled install and slot continuity confirmed on phone. If any gate fails, **do not claim availability through Aggiorna**.

## ZERO 2.0 — associazioni percettive sperimentali (13 ottobre 2026)
Sviluppo in `feature/zero20-perceptual-associations` (basato sul ramo sperimentale ZERO 2.0, non sullo stabile). Nuovo modello `PerceptualAssociationsV20` capace di ipotesi di somiglianza fra silhouette parziali da 16 raggi realmente percepiti, prototipi anonimi persistenti, associazioni osservazionali fra azioni e spostamento fisico, holdout e diagnosi. Nessuna etichetta preimpostata cassa/lastra/porta, nessun comando motore o modifica a energia/ricompense, nessuna OTA o installazione. Leggi [protocollo e limiti](docs/ZERO20_PERCEPTUAL_ASSOCIATIONS.md) e il registro `docs/ZERO_20_INTELLIGENCE_RESEARCH.md` prima di sviluppare il riconoscimento multivista e la memoria delle tecniche. La OTA103 stabile resta intatta.

**OTA applicativa:** questo modulo percettivo passivo è stato selezionato per il canale AGGIORNA come candidato successivo alla 103. La build ufficiale resta soggetta a Java/Android QA, stessa firma e pubblicazione SHA/manifest prima di dirla disponibile.


## 13 ottobre 2026 — ZERO 2.2 attivo (candidato OTA117)
Richiesta utente: cervello rivisto funzionante e APK, x5/x20 fluidi, TEST MAX circa x29. Nuovo ramo `feature/zero22-active-recurrent-ota117` da `fe6d7497eb5b53055b46c474dd5470028bc97f88`. Leggere **[ZERO22_ACTIVE_CORE](docs/ZERO22_ACTIVE_CORE.md)** per architettura, contratto sensoriale, replay, competenze, ottimizzazione TEST MAX e limitazioni. `RecurrentCoreV22` guida realmente la scelta prima dell'azione; i moduli 2.1 restano osservatori compatibili. Nessun reset del profilo o slot. 71 test JVM locali PASS; release, Android build e prova S21 da verificare separatamente. Non chiamare questo sistema Dreamer completo o intelligenza generale.

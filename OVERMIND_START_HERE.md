# OVERMIND: ZERO — punto di ingresso per tutte le nuove chat

## CORREZIONE PRIORITARIA: ZERO 0.9.8 AUTONOMIA (10 ottobre 2026)
**PRIMA DI TUTTO leggere `docs/EXPERIMENT_0098.md`, sezione CORREZIONE 0098-A.** La prima 0.9.8 (`0.9.8-ota.42`) attribuiva un bonus geometrico alle direzioni orientate verso segnali visivi valutati benefici/dannosi. L'utente ha esplicitamente rifiutato questo intervento comportamentale: ZERO deve avere informazioni apprese sull'energia, ma poter decidere autonomamente se avvicinarsi a una sfera anche quando ha bisogno di energia. Nella 0.9.8 corretta è stato rimosso `SensoryValenceGuide` e **NON ESISTE più `sensoryBias` per gli heading**; la nuova `ExperienceCredit` accredita i veri guadagni/perdite energetiche alle ultime 12 azioni effettivamente compiute, aggiornando le normali reti Q con i 26 sensi di quel momento. Le ricompense energetiche fisiologiche restano definite dal mondo e dal sistema di apprendimento, NON è autonomia assoluta filosofica. Motori, memoria, protezione antistallo e archivi di competenze v0.9.8 restano invariati. APK patch sulla medesima linea 0.9.8 con versionCode aumentato, firma identica. Nessun progresso su S21 dimostrato finché non viene testato.


> Ultimo aggiornamento della documentazione: 10 ottobre 2026.
>
> **OBIETTIVO DEL PROGETTO:** creare su Android una forma di vita artificiale ("ZERO"), rappresentata da una macchia nera simile a ferrofluido, con corpo deformabile e apprendimento autonomo da esperienze in un mondo simulato. Il giocatore è una presenza invisibile, non impartisce ordini alla creatura e ne modifica il mondo.

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

### Release ZERO 0.9.5-ota.39 — 2026-10-10
APK firmata, versionCode 1039, 92.692 byte; SHA-256 `86caeb85b6eba94aa657d6f19ac7dff5dd0e4e8d2974bf71e8c6c12a3201c211`; CI firmata `38003676123` verde, file `updater/public/latest.json` aggiornato. Il test manuale sul Samsung Galaxy S21 resta da fare. Non attribuire retroattivamente le 111U di apertura della v0.9.4 alla nuova memoria.


## Ultimo progetto attivo: ZERO 0.9.6 (10 ottobre 2026)
**Documento prioritario:** `docs/EXPERIMENT_0096.md` (descrive fatti osservati, implementazione, migrazione, limiti, test e prossime tappe). L'ultima modifica introduce ricordi episodici di durata variabile **2–36 azioni**, fino a **24** eventi, valenza favorevole/dannosa appresa dai **cambiamenti energetici realmente provocati dal contatto**, bias positivo e penalità negativa senza hardcode dei colori. Consolidamento per conferme: gli eventi non vengono sostituiti semplicemente perché vecchi. Migrazione `em1→em2` nella stessa chiave `episodic_memory_v095`: installare sopra 0.9.5 senza azzerare. Non sostenere che già preferisca i punti blu/eviti i rossi: da dimostrare in gioco.
Le osservazioni sperimentali del Galaxy S21 restano quelle della v0.9.4 (93 dischi, porta max 111U, 0 stanze); `docs/EXPERIMENT_0096.md` distingue il visto dai risultati soltanto automatizzati. Per il build firmato leggere `updater/public/latest.json` e la corrispondente release GitHub.


### Release Android firmata 0.9.6-ota.40 — 10 ottobre 2026
- **Stato:** build e firma stabile GitHub Actions `38005290019` **success**, JVM e Android staging `38005143609` e `38005224595` success; validazione pacchetto Android/update provider superata.
- **versionCode 1040**, package `com.overmind.zero.ota`, **94.984 byte**, **SHA-256** `0e1c4dfbcd4f77cd2575b5cc5d0ca9f6dd6189054f19066501cf9180c730efcb`.
- Asset sorgente `updater/public/apk/overmind-zero-1040-0e1c4dfbcd4f.apk`, manifest `updater/public/latest.json`; GitHub Release privata con tag `overmind-zero-v0.9.6-ota.40` dopo verifica dell'azione fallback.
- Conserva firma, package e SharedPreferences da 0.9.5 e precedenti. **Non disinstallare, non azzerare**. La nuova memoria utilizza `em2` e recupera `em1`.
- **Ancora da dimostrare su Samsung S21:** preferenza effettiva per punti energetici, evitamento di quelli dannosi, apprendimento e recupero di prese utili dopo la morte e attraversamento del cancello. I test CI NON dimostrano tutto ciò.


## 0.9.7 — PROTEZIONE DELLA LOCOMOZIONE (10 ottobre 2026)
**Leggi prima `docs/EXPERIMENT_0097.md`.** Sul Galaxy S21 l'utente ha osservato locomozione nettamente più veloce nella 0.9.4 che nella 0.9.5/0.9.6. Ricontrollando il codice abbiamo trovato due possibili regressioni: la memoria episodica sovrascriveva periodicamente **anche il motore di 62 pesi** con snapshot vecchi e abortiva prove `OnlineBodyLearning` anche quando veniva ricordata soltanto un'azione senza applicare vecchi pesi. Dalla 0.9.7 il motore **usa sempre i pesi appresi attuali** (o quelli candidati durante una prova reale); mano e membrana possono riprovare vecchie configurazioni soltanto durante contatto tattile e al di fuori di una prova di mutazione. Un guard basato sullo spostamento netto e sugli effetti fisici realmente osservati sospende brevemente i soli suggerimenti positivi della memoria in caso di stallo, senza direzioni programmate. Nuove metriche ANALISI: recuperi, decisioni libere da ricordi positivi, riprove tattili. Memoria positiva/negativa `em1/em2`, mondo da 11.664 pesi, corpo e 96 punti preservati. **Il miglioramento effettivo della velocità deve ancora essere verificato sul dispositivo; non prometterlo sulla sola CI.**


### RILASCIO OTA 0.9.7-ota.41 — 10 ottobre 2026 (verificato)
- **GitHub Actions firmata 38007333273 = SUCCESS**, JVM/staging 38007100048 e 38007156421 = SUCCESS. Package `com.overmind.zero.ota`, **versionCode 1041**, peso **96.936 byte**, checksum SHA-256 `4de8ef5a9f22dd6e4caf503fc7543410a5e829cab5beb1e9b0a0bcdea3a9bf1d`.
- APK verificata nel repository `updater/public/apk/overmind-zero-1041-4de8ef5a9f22.apk`; manifest `updater/public/latest.json` (GitHub) contiene esattamente SHA, versionCode, versione e dimensioni. GitHub Release fallback `overmind-zero-v0.9.7-ota.41` dopo trigger apposito.
- **Nessun test di comportamento su Galaxy S21 della v0.9.7 ancora effettuato**. I pesi e le memorie esistenti restano installabili in place, ma recupero della velocità prima vista con 0.9.4 deve essere verificato, non garantito.

## ULTIMA VERSIONE IN SVILUPPO: ZERO 0.9.8 (10 ottobre 2026)
**LEGGERE `docs/EXPERIMENT_0098.md`.** Sul Galaxy S21 ZERO torna sempre nella stessa zona, gira in cerchio con bassa distanza netta e spesso muore vicino a sfere energetiche. Sviluppata v0.9.8: archivio max6 tecniche motorie realmente osservate, protezione da mutanti meno mobili, recupero di una tecnica alternativa dopo stallo, diversità di azioni durante stagnazione e bias dei 16 heading da segnali visivi il cui valore energetico è stato appreso per contatto. Nessun colore buono/cattivo, destinazione o procedura di cancello innati. Mantiene pesi di tutte le reti e memorie em1/em2. Non si possono ricostruire vecchie abilità della 0.9.4 eventualmente già sovrascritte. Test sul dispositivo ancora da fare.


### RELEASE OTA FIRMATA 0.9.8-ota.42 — 10 ottobre 2026
- **VersionName** `0.9.8-ota.42`; **versionCode** 1042, package `com.overmind.zero.ota`, peso **101.320 byte**.
- **Checksum SHA-256 firmato:** `81cb0480de8b47e3cf781c6ddc5e771377ebcfa9d811679a6c202d201bfbf124`.
- **GitHub Actions** staging `38009632805` = success (JVM, motore, sopravvivenza, Gradle Android) e stable firmata `38009921490` = success (stessa firma e manifest OTA pubblicato).
- **Manifest GitHub**: `updater/public/latest.json`; APK in `updater/public/apk/overmind-zero-1042-81cb0480de8b.apk`. Release GitHub con tag `overmind-zero-v0.9.8-ota.42` via pipeline fallback su `main`.
- Il corretto funzionamento sul Samsung Galaxy S21 **deve ancora essere provato**. L'archivio di skill non può recuperare automaticamente motorii della 0.9.4 già sovrascritti prima di questa versione; potrà salvare quelli efficaci osservati d'ora in poi.
- **Installare sopra la versione precedente SENZA disinstallare né azzerare alcuna memoria.**

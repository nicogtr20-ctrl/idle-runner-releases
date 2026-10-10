# OVERMIND: ZERO — punto di ingresso per tutte le nuove chat

> Ultimo aggiornamento della documentazione: 9 ottobre 2026.
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

## Release verificata — ZERO 0.10.5-ota.54 / statistiche robuste (10 ottobre 2026)
- Branch di prova: `feature/overmind-v105-lifetime-statistics`, derivato da `feature/overmind-inapp-updater`.
- La pagina **STATISTICHE** separa ora tre scale: vita in corso, capacità attuale come media delle **ultime 25 vite**, e storico permanente.
- Il trend principale è **25 vite recenti vs 25 precedenti** e si attiva soltanto con 50 vite complete, per non scambiare poche casualità per miglioramento.
- `LifeStatistics` conserva ancora le ultime 50 vite individuali per grafici e confronto, ma aggiunge somme/record permanenti: dopo la 51ª vita le medie storiche non perdono più i campioni vecchi.
- Migrazione `ls1 -> ls2` senza cambiare la chiave SharedPreferences `life_statistics_v104`. Per ZERO già esistenti il passato precedente alla telemetria non viene inventato: l'interfaccia dichiara lo storico come incompleto.
- Tutta la statistica resta **observer-only**: non entra in reward, policy, world model, memoria episodica o selezione delle azioni.
- QA staging: run **38059707433** verde. Publisher stabile firmato **38060026469** verde.
- **OTA pubblico verificato HTTP 200**: versionCode **1054**, versionName **0.10.5-ota.54**, SHA-256 `34bd2eb5261f499b196037832dce744f37b7b3822a49a7f655c3b6347f0c4e7a`, APK **138.636 byte**. Installare sopra l'app esistente; non disinstallare.

## In sviluppo — ZERO 0.10.6 / memoria di lavoro temporale (10 ottobre 2026)
- Obiettivo: dare a ZERO uno stato interno del **prima/dopo** senza insegnargli concetti come porta, trascina, cibo o soluzione.
- Nuovo `TemporalWorkingMemory`: **16 stati ricorrenti** e **7.344 pesi appresi** di predizione/valore contestuale. Il reservoir ricorrente usa soltanto variazioni dei 26 sensi e azioni realmente scelte; nessuna identità di oggetto o coordinata obiettivo.
- Due situazioni con sensi istantanei uguali possono produrre stati interni diversi se la storia recente è diversa.
- Il contesto NON influenza le decisioni all'inizio: serve almeno 25 transizioni held-out e l'errore temporale deve battere il modello one-step di almeno il 4%. Bias massimo sulla scelta: ±0,45.
- Conseguenze fisiche/energetiche realmente osservate possono dare credito alle ultime 8 azioni contestualizzate; nessuna ricompensa immaginata.
- Alla morte si cancella solo lo stato di lavoro della vita corrente; i pesi appresi restano. Riavvio/salvataggio della stessa vita conserva il contesto tramite `temporal_working_memory_v106`.
- La pagina **STATISTICHE 25-vs-25 della 0.10.5 non viene modificata**. In CERVELLO compare soltanto una scheda diagnostica della nuova memoria.
- Staging iniziale: run **38064278116** verde (test temporali, regressioni precedenti e Android Debug). Release firmata non ancora dichiarata in questa nota.

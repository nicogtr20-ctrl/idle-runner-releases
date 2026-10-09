# OVERMIND ZERO — Esperimento 0096: apprendimento da benefici e pericoli

**Data:** 10 ottobre 2026. **Stato:** codice 0.9.6 integrato nel branch OTA, test automatici JVM/Android completati; test sul telefono ancora necessario. Ultima release firmata va verificata dal manifest `updater/public/latest.json` e asset GitHub.

## Perché esiste la 0.9.6
L'esperimento mira a sviluppare la capacità della creatura ZERO di **conservare esperienze più lunghe**, consolidare ripetizioni riuscite e distinguere esiti **benefici o dannosi** attraverso conseguenze effettivamente vissute, senza programmare cosa significhi ogni colore.

### Evidenza pregressa dal Samsung Galaxy S21
- 0.9.2: dopo ~11.000 esperienze ZERO quasi immobile → mutazioni corporee insufficienti.
- 0.9.3: locomozione reale, deformazioni della membrana e oggetti mossi, ma la distanza totale premiava anche i giri sul posto.
- 0.9.4: distanza netta, nuove zone, efficienza, prove corporee osservate invece di test sintetici. Screenshot utente: ~38.070 esperienze, 93 dischi spostati, 268 prese, porta spostata fino a 111U, 0 stanze concluse. Soglia geometrica per cancello interamente libero **>115U**. Dopo la morte ZERO non ha ripetuto la tecnica.
- 0.9.5: memoria episodica fino a **9 azioni / 18 eventi**, richiamo contestuale di azioni/pesi fisici che precedevano conseguenze positive, ma **niente memoria episodica specifica dei danni**. Il test Galaxy S21 sulla riproduzione della presa non è ancora documentato come riuscito.

## Implementazione 0.9.6
1. **Sequenze vissute di lunghezza variabile (2–36 azioni)**: archivio di 24 eventi. Si memorizzano 26 letture sensoriali, una fra 16 azioni effettivamente scelte e uno snapshot dei controllori (62 pesi motori, 18 manipolazione, 82 membrana). La finestra comincia dal primo segnale rilevante disponibile nel buffer, inclusi fino a due passi precedenti; non è garanzia di individuare l'inizio causale della tattica.
2. **Valenza energetica positiva e negativa**: `GameActivity` raccoglie il cambiamento di energia *dopo un contatto reale con la risorsa*, ad esempio +22 per un impulso favorevole, -7 per uno dannoso; la ricompensa rara della stanza può dare fino a +80. La fame naturale NON viene etichettata come pericolo di un oggetto. Un contatto dannoso che provoca morte è registrato prima del reset. Una porta/disco che si sposta fisicamente genera anch'essa un segnale positivo di cambiamento nel mondo.
3. **Preferenze acquisite, non comandi**: `EpisodicMemory` suggerisce azioni collegate a contesti sensoriali benefici (bias morbido, non obbligatorio; si possono riprovare pesi storici). Un episodio dannoso può invece penalizzare la scelta dell'azione vicina al danno. Un episodio negativo **non viene riprodotto** come script. ZERO conserva sempre la possibilità di esplorare. Nessuna regola “blu=buono, rosso=cattivo”, nessuna conoscenza delle coordinate delle risorse.
4. **Consolidamento**: eventi simili della stessa valenza vengono rafforzati con un conteggio di conferme anziché occupare nuovi slot. L'archivio pieno valuta forza/conferme: possono essere sostituiti i ricordi meno prioritari, invece di eliminare automaticamente il più vecchio. **Non è memoria eterna garantita**: un ricordo raro può comunque perdersi.
5. **Migrazione senza azzeramento**: `episodic_memory_v095` rimane la stessa chiave. Il parser riconosce sia `em1` (0.9.5) sia `em2` (0.9.6), con parsing atomico e limiti di dimensione; nuovi salvataggi scrivono `em2`. Nessun reset di 11.664 pesi del modello del mondo, corpo, manipolazione, membrana o altre statistiche. `ResetDomains.EPISODES` agisce solo su quella memoria.
6. **Nuove statistiche nell'ANALISI**: ricordi favorevoli/dannosi, lunghezza massima di sequenza, consolidamenti e conferme, scelte diverse dopo riconoscimento di una memoria negativa. Il numero di scelte diverse NON prova che ZERO abbia effettivamente evitato un oggetto o capito la morte.

## Dettagli tecnici
Repository privato: `nicogtr20-ctrl/idle-runner-releases`. Codice Android SOLO nel branch `feature/overmind-inapp-updater`, branch sperimentale `feature/overmind-v096-valenced-experience`; `main` conserva documentazione, il vecchio Idle Runner e pubblicazione delle release.

File modificati: `app/src/main/java/com/overmind/zero/EpisodicMemory.java`, `SensorimotorMind.java`, `GameActivity.java`, `BrainToolsActivity.java`, `app/build.gradle`; workflow `.github/workflows/overmind-v096-staging.yml` e `overmind-ota-publish.yml`.

Memoria episodica: fino a 24 eventi × 36 coppie sensazione/azione × 26 ingressi + snapshot corporei, persistenza formato `em2`, protezione contro dati corrotti. Somiglianza dei sensi può confondere scene diverse. Retroattribuzione dell'esito ancora euristica e correlazionale; non è prova di causalità né planning controfattuale multistep.

## Protocolli di test
**Test automatizzati:** positivi +22, negativi -7, traccia >9 azioni, consolidamento ripetizioni, esclusione di replay dannosi, migrazione di vero formato em1 sintetico, rigetto input corrotti, ricordi persistenti dopo morte, regressioni motori/membrana/mondo e compilazione Android. I test NON mostrano da soli competenze spontanee nel gioco.

**Test manuale su Galaxy S21 ancora da effettuare:**
1. Installare firmata 0.9.6 sopra 0.9.5, **senza disinstallare o azzerare**, verificare che i vecchi ricordi e cervelli restino visibili.
2. Osservare raccolta energia e contatti dannosi; in ANALISI devono crescere rispettivamente ricordi favorevoli e dannosi.
3. Dopo morte, riavvio e nuove condizioni sensoriali simili, controllare preferenze effettive e frequenza di contatto blu/rosso; non basta che crescano i contatori.
4. Verificare una sequenza memorizzata >9 azioni e consolidamento dopo eventi simili. Stress-test memoria/cpu a x20.
5. In stanza osservare separatamente apertura reale >115U, passaggio attraverso la soglia e raccolta energia. Nessuno dei tre risultati deve essere dato per acquisito.

## Roadmap futura
- A/B rigoroso sui medesimi stati iniziali e seed, con e senza memoria; misure di sopravvivenza, risorse raccolte, danni, porta e ingressi.
- Migliorare sensori di movimento relativo e segmentazione di sequenze (evitare di confondere scene simili) senza etichette speciali.
- Pianificazione attraverso immaginazione di conseguenze su più passi usando il modello del mondo da 11.664 parametri, distinguendo esperienze realmente vissute da simulazioni interne.
- Migliorare permanenza di strategie causali veramente confermate e gestione memoria su dispositivi Android.

## Ripresa in nuove chat
Leggere `OVERMIND_START_HERE.md`, `AGENTS.md`, questo documento, `docs/HANDOFF.md`, `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/CHANGELOG.md`, `docs/ROADMAP.md`. Controllare sempre branch, manifest SHA256 e stato workflow/release, senza inventare URL APK o successi sul telefono. Aggiornare ogni documento e annotare ogni screenshot utente con versione, metrica e incertezza.

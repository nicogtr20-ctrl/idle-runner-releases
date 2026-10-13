# ZERO OTA112 — Minimal neon game HUD / navigation

**13 ottobre 2026**, branch `feature/zero-ui-neon-nav-ota112` → `feature/overmind-inapp-updater`.

## Obiettivo, come da immagine approvata dall'utente

- Eliminare totalmente dalla HOME la grande intestazione `ZERO / LASTRE 0124`, i pulsanti CERVELLO/AGGIORNA nell'header, il vecchio pannello a larghezza intera di tre energie e la barra piena "DETTAGLI AVANZATI".
- Tre contatori energetici autenticamente **verticali**, 🔵 🔴 🟣, senza pannello di sfondo condiviso, sul margine sinistro e sopra il mondo. Mantengono `EnergyLifetimeCounters` e le vite/slot cumulativi. Non falsificare gli eventi pregressi non attribuibili.
- Pannello vitale compatto in alto a destra: **netto, via, record, vita, energia reale, integrità tessuto reale contro il vento**, poi tre righe di progresso. Piccolo controllo pausa incluso.
- Piccola **freccia flottante trasparente** sul lato destro, con area tattile sufficientemente ampia; al tap appare un pannello avanzato, al secondo tap sparisce. Nessun log tecnico visibile durante il gioco nella vista di default.
- Navbar inferiore in stile gioco: **CERVELLO** a sinistra, **TEMPO x1/x5/x20** al centro con pulsante circolare luminoso, **IMPOSTAZIONI** a destra. Mantiene touch target estesi.
- Menu **IMPOSTAZIONI** navigabile senza Activity aggiuntive: Partite / Salvataggi, Analisi, Statistiche, Cervello / Parametri, Prove corporee, Prova stanza, Aggiorna e Versione. Il menu non modifica apprendimento o fisica, e conserva gli stessi passaggi `save()`/`saveScene()` necessari prima di cambiare Activity.
- Lingua italiana, sfondo mondo originale senza alterazione fisica, tema scuro con linee e gradienti neon teal/blu/viola disegnati da Canvas. Nessun asset/font di terze parti.

## Architettura / contenimento dei rischi

L'intervento interessa soltanto `GameActivity.java` nei metodi di rendering e dei tocchi. I moduli sensomotori, il modello ZERO2, le firme energetiche, le memorie, i dati statistici e le proprietà di vento e tessuto **non vengono cambiati**. Il pulsante AGGIORNA chiama il **medesimo** `UpdateClient.checkAndInstall()` già verificato sulla OTA111 e non distribuisce APK esterne, né reinstalla l'app.

I nuovi state flag `settingsExpanded` e `detailsExpanded` sono puramente visuali/transitori, deliberatamente non persistenti. Il banner di stato dell'updater appare sopra il dock e continua a riportare eventuali errori.

## QA

- Android `assembleDebug` e tutte le regressioni pure JVM di ZERO2 (staging branch).
- Almeno 7 controlli storici PR per mondo, fisica, sensori, presa e telemetria.
- Publisher firmato: controllo pin `GameActivity.java` sul nuovo SHA, controllo dei touch/route, firma di produzione e pubblicazione SHA-256 su manifest `latest.json`.
- Verificare lo stato READY in Vercel e coerenza della GitHub Release prima di dichiarare disponibile **AGGIORNA**.
- Rimane necessaria la verifica visuale sul Galaxy S21: numero grandi, orientamento reale, pressione del nuovo pulsante e spazio centrale libero.

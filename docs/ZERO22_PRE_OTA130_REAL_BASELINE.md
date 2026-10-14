# ZERO 2.2: baseline reale pre-OTA130, 14 ottobre 2026

## Provenienza e integrità
- L'utente ha esplicitamente dichiarato di aver caricato questo checkpoint **prima dell'installazione della OTA130**, per un confronto longitudinale. La versione APK specifica non è codificata nel campo `currentZero`; la classificazione pre-OTA130 dipende dalla dichiarazione dell'utente.
- Branch privato `zero-telemetry`, report completo immutabile `20261014-161415-e4b93716c12f`, timestamp `2026-10-14T16:14:15.780Z` (18:14:15 CEST), 14 JSON part, origine `ANDROID_EXPLICIT_SHARE`.
- La OTA130 è stata verificata nell'ambiente di pubblicazione come APK `0.11.6-ota.130` con codice 1130. Il baseline è stato caricato **prima dell'installazione** secondo l'utente, non necessariamente prima della compilazione/distribuzione.
- Valori ripresi integralmente dal `zero-telemetry/current.json` e `part-002.json` (campo `life_statistics_v104`) e `part-014.json` (`extras.zero20Passive.zero22`). Il record esiste nei dati, non deriva da video.

## Baseline pre-OTA130 (stessa entità ZERO)
- Vite concluse: **63**; vita in corso al checkpoint: **69,11 s simulati**.
- **Ultima vita conclusa: 8.648,6434 secondi simulati = 144 minuti e 8,6 secondi**, record attuale. Rispetto al precedente record di 3.099,0573s (51m39s) è **2,79 volte** più lunga.
- Nell'ultima vita: 132 contatti energetici positivi, 7 nocivi, +2.974,766 energia da contatto, -49,0 danno da contatto; 46 oggetti spostati, 542 zone nuove esplorate, massima distanza netta 6.477,169, 0 porte attraversate; 258 ricordi con nuovo effetto positivo nel registro.
- Ultime 10 vite: media durata **33,7 minuti**; contatti positivi medi 29,1 per vita, nocivi 2,6; rapporti per 1000 secondi simulati +14,4 positivi e 1,3 dannosi.
- Precedenti 10 (posizioni -20:-10 nello storico attuale): media durata **7,2 minuti**, positivi 3,8 per vita, nocivi 2,3; per 1000 secondi +8,8 positivi e 5,3 dannosi.
- Ultime 25 vite: media durata 17,4 min, +13,0 contatti positivi/1000s, +2,0 nocivi/1000s.
- 25 vite precedenti: 7,8 min, +10,1 positivi/1000s, +8,8 nocivi/1000s.
- Totali cumulativi da `currentZero`: 476 fonti energetiche finite consumate, 124 casse scoperte, 250 oggetti spostati, 3021 celle esplorate, 522 adattamenti di membrana accettati, 498 mano accettati, 445 motore accettati, 3 stanze risolte, 15.106,317 energia spesa, riserva istantanea 47,682.
- CORE 2.2 prima di OTA130: **63.292** transizioni, **101.262** replay, 96 memorie replay attive, **2001** contatori storici planned, `confidence=0.000`, 63 eventi terminali, 0 invalidi.
- Il numero `planned=2001` conta candidati con gate energetico convalidato in momenti precedenti, NON 2001 azioni deliberate direttamente dal planner; l'ultima confidenza visualizzata 0 non prova qualità nulla di tutte le abilità.

## Come confrontare dopo OTA130
1. Non cancellare dati, non creare un organismo differente e non disinstallare; conservare gli stessi slot e scenario, annotare velocità e condizioni.
2. Raccogliere report **dopo effettiva installazione** e almeno più vite/decisioni, registrando `recordedAtUtc`, `reportId` e app versione visibile.
3. Confrontare differenze *dei contatori cumulativi* tra i checkpoint, non i totali come se fossero intervalli autonomi. Usare `LifeStatistics` con vite concluse nello specifico intervallo successivo, evitando finestre sovrapposte.
4. Confrontare tassi per 1000 secondi e distribuzione per vita (non soltanto record massimo); distinguere durata delle vite da tempo reale del telefono; annotare condizioni e difficoltà di confronto.
5. Dopo correzione valutare `confidence` (fisica), `energyReady` (previsione energetica indipendente), effettiva variazione dei punteggi per azione e reali contatti/danni/casse/prese/cancellate.
6. Nessun incremento di intelligenza o superiorità causale è dimostrato da un record isolato. L'aggiornamento modifica politica e training senza cancellare gli stati rc22 precedenti, ma cambia il regime: effetti devono essere validati.

## Riferimenti
- Report: `zero-telemetry/reports/20261014-161415-e4b93716c12f/` (branch `zero-telemetry`).
- Diagnosi e correzione: `docs/ZERO22_SPLIT_CONFIDENCE_OTA130.md`.
- OTA130 QA: `38595974967` SUCCESS; publisher `38596282565` SUCCESS; test HTTP pubblico `38597154936` SUCCESS.

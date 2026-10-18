# ZERO 3.0 — integrazione V351/V352, stato reale del 18 ottobre 2026

Il master, il contratto e la specifica UI restano autorevoli. Questo rapporto
aggiorna le descrizioni precedenti di clock, sensi e training del valore;
non dichiara completato il progetto né autorizza una OTA.

## Codice integrato nel vero runtime

- V351 riusa GRU64, V315, V344 e ottica del mondo: 194 ingressi misurati
  includono 24 canali spettrali, 16 raggi con stanza/occlusione, tatto/impulso/
  presa/estensione settoriali, 64 efferenze reali, riserva e danno. I dati
  grezzi 96x12, 74x7 e propriocezione rimangono nel journal esistente.
- Replay registra gli stessi input PRE/POST ricevuti dalla GRU. Schema 351
  opzionale e checkpoint GRU dimensionati; vecchi GRU26/345 e archivi
  322/328/348 restano compatibili. Nuove vite LAB non convertono quelle salvate.
- V352 esegue davvero 10..26 passi da 0.025s secondo la durata della GRU.
  Danno o morte interrompono al primo passo osservato; causa, durata prevista
  ed eseguita sono nel record 352. Cortex censura l'intenzione interrotta;
  readout durata non viene premiato per tempo ineseguito.
- Critic riceve soltanto netEnergy autentica, discount temporale e bootstrap
  zero alla morte; due reti bersaglio Polyak 0.02 e replay bounded di 128
  transizioni reali del valore, quattro campioni deterministici per update.
  Holdout non aggiorna replay o pesi. Embedding del replay fotografati alla
  raccolta: il loro invecchiamento è un limite da valutare scientificamente.
- Target/replay, GRU, actor, critic, cortex, corpo, mondo, stanza, metabolismo
  e tutti i sensi sono nella medesima generazione atomica 352.
- Android LAB nuove nascite V352, x1/x5/x20 con pacing dal tempo simulato
  effettivo, TEST MAX separato. Questo NON è la UI finale G8.

## Prove effettive

V351: compilazione JVM, ottica fisica identica a Senses.spectral8, occlusione
vera, snapshot senza alias, PRE/POST/next PRE coerenti, replay neurale completo.
1000+nuovo processo JVM+1000 identici al controllo 2000, 63 segmenti,
rollback a1920 e doppia corruzione rifiutata. CI/Android Debug:
https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39118399048
SUCCESS. V345/V348/V350 e regressioni compatibili PASS.

V352 locale: durate osservate 16/17/18/19 substep nella prova96; danno e morte
interrompono a1, raccolta successiva al danno letale non resuscita, morte
conservata nel restore, nessun gradiente alla durata censurata. 48+restore+48
identici a96. Batteria completa con target/replay: 1000+nuovo processo+1000
identici al controllo2000, 63 segmenti e fault rollback/fail-closed PASS.
La fixture fornisce energia con contatto reale per verificare STORAGE:
nessuna prova di ricerca autonoma delle risorse da questi 2000 passi.

CI V352 e Android Debug:
https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39119103894
SUCCESS: sensori, clock, legacy, 2000 con restart, fault e Android Debug PASS. Le CI storiche con elenchi incompleti di sorgenti
sono state corrette aggiungendo sourcepath; non sono stati rimossi test.

## Sole lacune residue, senza ricreare componenti approvati

| Gate | Stato residuo |
|---|---|
| G0 | Confronto sistematico baseline2.2 sulla stessa fisica/scene/tempo |
| G1 | Fix robusto post-world multioggetto; V346 respinta resta esclusa |
| G2 | Nuova integrazione sensoriale reale; validare intera matrice rotazione/morfologie/maschere |
| G3 | Locomozione robusta, costi raw, nuove scene, retention abilità non approvati |
| G4 | Durata/critic/replay ora implementati; valore e beneficio decisionale indipendenti non approvati |
| G5 | World model multioutput/uncertainty/planner affidabili non completati |
| G6 | Obiettivi appresi, memoria procedurale/candidate/champion/recall/opzioni non completati |
| G7 | GRU26 10k già approvata; V352 2k provata; neurogenesi/retention skill da completare |
| G8 | UI finale della specifica non implementata; laboratorio non equivale a G8 |
| G9 | Galaxy S21 reale, stress Android, performance e benchmark finali non verificati |
| G10 | Nessuna OTA3.0 o merge su publisher; stable2.2 OTA172 e12 slot preservati |

Non presentare questo incremento come cervello3.0 completo. Non ripetere
GRU, ottica, journal o presa V319 già approvati; continuare dai gap sopra.

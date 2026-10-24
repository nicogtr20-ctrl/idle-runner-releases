# ZERO385 — 100% motori originali del cervello ZERO 3.0 (24 ottobre 2026)

**Stato:** commit codice sperimentale su `fix/zero385-full-gru3-muscle-authority`. **NESSUNA OTA PUBBLICATA**. È una modifica di instradamento, NON prova che la locomozione sia stata riparata. I 22.081 eventi/pesi sono preservati.

## Motivazione con prove reali
Dal checkpoint utente originale `zero-telemetry/reports/20261024-103000-33e93646acead`: 22.081 decisioni e 12.797 aggiornamenti GRU3/corteccia, contro zero aggiornamenti della periferica V2.2. Inoltre 320 pesi locali vecchi e 352 componenti del circuito veloce salvati a 0. `Zero3MotorPeripheralBridgeV364` mescolava 75% periferica storica congelata con 25% cortical V3 sui canali contrazione, tangenziale, rigidità, lasciando la presa interamente a V319. Questa era una scorciatoia nata durante il recupero della locomozione, non la specifica finale per un attore neurale fisico unificato.

## Unica variazione su codice produzione
`GameActivity` chiama `brain3.actuate(..., null, ...)` nel solo ramo `brain3!=null`. Il `null` disabilita integralmente l'avvio e il blending della periferica V364; i 64 comandi prodotti dalla corteccia GRU3, con CH2 presa V319 del cervello 3.0, vengono inviati direttamente a `SymbioteMatterV1.step`. Rimane invariato il ramo legacy 2.2, ma NON parte quando è attivo ZERO3. Mantenuti V356/GRU, 194 input appresi, motore a 170 punti, salvataggi, collisioni, grip, freno fisico e controllo scientifico originale.

## Test differenziale PRECEDENTE su salvataggio autentico
Run GitHub Actions [40137154713](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40137154713): compilazione e confronto Android 25 prove PASS sul vero salvataggio S21. Su 2 secondi identici senza ostacoli, confronto solo cortex V3 / vecchio 75/25 / solo vecchia periferica / coast passivo:
- EAST solo cortex V3: X -1,184, versus 75/25: X -0,589, coast -0,327 — **scelta verso Est non produce avanzamento X Est affidabile**.
- NORTH solo cortex V3: Y +4,651, versus 75/25 +11,935.
- WEST solo cortex V3: X -0,362 vs 75/25 X -9,668.
- SOUTH solo cortex V3: Y +2,514 (contro direzione sud) vs 75/25 Y +3,376; le masse mantengono inerzia/drag. Tutti sono valori COM grezzi, non misure di causazione pura.
- Vecchia periferica crea significativamente più lavoro muscolare; cortex solo crea lavoro positivo e impulso >0, quindi NON è fisicamente scollegata, ma il controllo direzionale è ancora scarso. La prima A/B erroneamente azzerava anche cortex-only ed è stata ritirata/corretta e ripetuta; non riutilizzare.
- Vicino al muro 120/120 frame con molti contatti solidi anche solo cortex: **eliminare 75% non dimostra fuga dal muro**.

## Blocchi prima della release
- Compilazione originale e QA regressioni branch.
- Migliorare misure di forza/velocità sotto intenti veri anziché supporre che aumentare autorità equivalga a locomozione.
- Una prova di 2.000+ decisioni autonome originali dello stesso cervello con graduale controllo motorio dovrebbe dimostrare orientamento, inversione/frenata, contatti e raccolta energia e non solo movimenti minimi in 48 secondi.
- Analizzare membrana: nel checkpoint di stanza del S21 due spigoli della membrana (15–16 e 39–40) si intersecano mentre 36 punti sono schiacciati contro il muro; la topologia dev'essere trattata a livello fisico separatamente, non scaricata sulla GRU.
- Cold restore su S21, stessi dati e 12 slot; solo poi preview firmata, senza toccare main/stabile fino a verifiche reali.

**Conclusione:** per rispettare il progetto, il comando motorio deve essere esclusivo del cervello 3.0; il cambio di percorso è necessario, ma da solo potrebbe peggiorare la locomozione. Non pubblicare come fix completo finché i controlli corporei non superano i test.

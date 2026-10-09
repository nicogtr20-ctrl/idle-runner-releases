# ZERO-008.1 — Solidità della porta e tentativi nello stesso mondo

## Feedback dalla prova reale sul Galaxy S21
- Parte del corpo attraversa visivamente il blocco (fisica discontinua).
- ZERO resta a tentare, poi la mappa viene ricreata e l'organismo torna al centro, impedendo un esperimento continuativo.
- Risultati precedenti: nessuna prova osservata di una presa bilaterale genuina in condizioni di gioco.

## Cause confermate nel codice
1. `ScarcityLab.project` corregge soltanto vertici dopo il movimento. Un punto molto veloce può finire oltre il solido e venire espulso dal lato sbagliato. Peggio, un **segmento** della membrana può attraversare la porta anche quando entrambi gli estremi sono fuori dall'ostacolo.
2. `ScarcityLab.solveGate` veniva eseguito PRIMA della correzione delle collisioni. Una seconda particella attraversata illegittimamente poteva essere conteggiata come una presa valida.
3. A esaurimento `GameActivity.resolveObjects` chiamava `resetEpisode()` che cancellava la stanza, l'oggetto e la posizione.
4. Toccando accidentalmente la riga di HUD poteva essere creata una nuova stanza anche se quella attuale era ancora attiva.
5. Il salvataggio della stanza non includeva geometria, spostamento della porta e posizione di ZERO.

## Correzioni
- Porta/barriere rigide: collisioni swept-velocity per particelle motorie e membrana, proiezione anti-penetrazione, restrizione del passaggio del nucleo quando la porta è ancora chiusa e correttore sui segmenti che intersecano direttamente i solidi.
- Il controllo della presa a due lati avviene **dopo** la risoluzione delle penetrazioni, non prima.
- Quando l'energia finisce dentro la stanza, la simulazione si mette in pausa e lo dichiara. `RIPROVA` è manuale: il nuovo organismo compare all'ingresso della STESSA stanza, con la porta allo stesso livello. Il cervello e il mondo non vengono cancellati.
- Save/reload di origine della stanza, posizione del blocco, progressi, stato della risorsa, posizione del corpo e stato esaurito. La versione 0.8.0 non conteneva questi dati: non è possibile recuperare una stanza già cancellata in quella versione.
- La riga HUD crea la stanza solo se assente o già risolta; durante i tentativi non può cancellarla.

## Test
- JVM: due contatti opposti per spostare il blocco, singolo contatto inefficace, prova high-speed particle anti-tunneling, segmento che attraversa il solido rilevato, core bloccato finché il cancello è chiuso, contatto attraverso varco libero ammesso.
- CI: test dei vecchi cervelli e build Android; firma tramite pipeline OTA stable.
- **Non dichiarare ancora riuscita la presa in live:** il test sintetico dimostra la solidità di alcune invarianti, non la capacità appresa dalla rete di produrre due pseudopodi genuini.

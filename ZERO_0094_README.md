# OVERMIND ZERO 0.9.4 — misurare il movimento utile
Branch dedicato: `feature/overmind-v094-efficient-exploration`, derivato da `feature/overmind-inapp-updater`.

## Cambiamenti
- Il motore continua a sperimentare mutazioni dei propri 62 pesi **nella vera simulazione in corso**, non in rollout invisibili con target noto. La manipolazione dei dischi e la membrana/presa continuano ad apprendere.
- Il voto di un esperimento utilizza **distanza netta** percorsa nell'intera finestra (da punto iniziale a finale), scoperte di nuove celle da 75 U (una volta per vita), contatto fisico, guadagni reali di energia e consumo reale di energia. La distanza totale percorsa ha peso minimo; un grande numero di giri in tondo senza nuove conseguenze NON è più considerato automaticamente un miglioramento.
- Una rotazione o manipolazione locale può ancora essere selezionata se trova nuove zone, sposta oggetti, migliora contatti o energia. Nessuna direzione o oggetto specifico viene dichiarato 'corretto'.
- 8 esperienze sensoriali con pesi memorizzati e 8 con mutazione; contesto energetico/direzione/stanza approssimativamente simile. Non è un A/B causale controllato.
- Distanza netta del nucleo e distanza reale totale visibili assieme nell'HUD. Zona/e esplorate ed efficienza energetica (netto massimo/energia spesa), record netto.
- Pagina ANALISI con progressi reali, contatori separati di varianti conservate/scartate per corpo, manipolazione, membrana; valori persistenti con chiavi **nuove**, non retrodatati.
- Tasto ex CONFRONTA -> PROVE: mostra due vere finestre di esperienze consecutive, NON le vecchie prove offline con target noto.
- Reset dei 7 domini indipendenti aggiornato con le chiavi v094. AZZERA TUTTO continua a chiedere doppia conferma.

## Test da eseguire su Galaxy S21
1. Installare la versione firmata sopra 0.9.3, senza disinstallare: controllare conservazione di cervelli, conteggi e abilità.
2. A x1/x5/x20 osservare NETTO e VIA/PERCORSO: se ZERO gira in tondo, VIA sale ma NETTO dovrebbe restare basso.
3. Toccare PROVE: controllare score base/variante ed esito e confrontarlo in ANALISI, che registra accettati/scartati.
4. Osservare se nuove aree o energie vengono raggiunte, se i movimenti circolari utili continuano, e se diminuendo la perdita energetica persistono le vite.
5. Confermare che la porta resta solida e la ricompensa rara rimane nel recinto. Nessuna dimostrazione di soluzione autonoma del cancello è implicata dalla CI.

Le nuove metriche iniziano dal primo avvio 0.9.4: i dati storici della 0.9.3 vengono preservati ma non trasformati in statistiche inventate.

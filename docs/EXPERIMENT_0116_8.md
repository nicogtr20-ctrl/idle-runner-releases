# OVERMIND ZERO — Esperimento 0116.8: simmetria reale della presa al cancello e alle casse

**11 ottobre 2026.** Base `0.11.6-ota.75`, branch di prova `feature/overmind-v1168-grip-parity-audit`. L'utente ha osservato adesione arancione immediata al cancello anche con cervello vergine, ma solo sporadica sulle casse; ha poi confermato alcuni rari punti arancioni sulla cassa.

## Che cosa dimostra l'arancione

Il render disegna arancione solo se `surface.gripHold[i]>=Morphology.GRIP_HOLD` (0,075 secondi simulati) e `surface.touchSense[i]>.5`. L'azione `SensorimotorMind.adhesion()` restituisce automaticamente 0,82 per le azioni 8–15, selezionabili fin dalla prima decisione anche per esplorazione. `Morphology.Surface.step` combina l'attuatore locale con `gripDrive*touchSense`; la soglia è 0,28. La presa iniziale può quindi attivarsi subito *senza che ZERO sappia cosa sia un cancello*. Il colore non certifica una presa bilaterale, l'apertura né una decisione intenzionale basata su esperienza.

## Difetto trovato

La nuova fisica continua per le casse in OTA.75 usa `SquareCollisionSolver.constrain()` e, per ogni `project(...)` che riporti il punto fuori dal quadrato, azzera incondizionatamente `skin.gripHold[i]`. Questo succede anche se un punto già appoggiato alla stessa faccia preme leggermente contro il blocco: si tratta di contatto valido, non attraversamento del materiale. `SquareGripPuzzle` aggiunge poi un solo incremento `dt=0.035` al tempo di presa, ricominciando continuamente e non raggiungendo la soglia 0,075. Il cancello `ScarcityLab.solveGate` non annulla analogamente il tempo di adesione per pressione contro una faccia corretta. Questo spiega la disparità osservata e la comparsa solo occasionale dell'arancione sulla cassa quando alcuni punti non richiedono una correzione di collisione.

### Prova riproducibile, prima della modifica

Stesso comando manuale di adesione 0,82 e contatto su due facce, ripetuto 8 passi fisici:
- cancello: **6** frame con presa valida, 0,28 s accumulati;
- cassa: contatto rilevato in **8** frame ma **zero** frame con presa bilaterale, tenuta bloccata a **0,035 s**.

La prova iniziale GitHub Actions `38164622087` fallisce intenzionalmente con `BUG: box CCD erases ordinary sustained two-sided adhesive contact`.

### Correzione proposta (NON ancora rilasciata)

In `SquareCollisionSolver`, dopo la correzione della posizione, preservare il tempo già accumulato **soltanto** se:
1. al frame precedente il punto era già su una faccia esterna legittima;
2. il tentativo di movimento rispetto al frame precedente era contenuto (meno di mezza larghezza della cassa);
3. la correzione lo riporta alla **stessa** faccia, e non a quella opposta.

Altrimenti azzerare la presa come prima (es. attraversamento completo, provenienza interpenetrata o movimento troppo grande). Non viene concessa alcuna nuova presa: restano intatti soglia 0,28, durata richiesta 0,075, condizione fisica bilaterale, 40 energia nascosta e i pesi neurali.

### Nuove prove

`GripParityRegression` produce, dopo la correzione:
- cancello: **6/8** frame di tenuta;
- cassa: **6/8** frame di tenuta, due lati confermati, colore arancione consentito;
- attraversamento integrale vietato: la presa illegittima viene azzerata.

Workflow sperimentale `38164791215` **SUCCESS**: `GripParityRegression`, `SquareCollisionSolver` (anti-tunneling), `SquareGripPuzzle` (una sola faccia non sposta la cassa, doppia presa sì, ricompensa unica), tutti i vecchi test JVM e `assembleDebug`.

Questa è una correzione delle collisioni, **non un'istruzione di apprendimento** né un potenziamento arbitrario. Può tuttavia cambiare i risultati futuri di ZERO, perché rende fisicamente valido e sostenibile un contatto che prima veniva interrotto dal solver; va prima sperimentata e poi installata sopra l'APK, mantenendo gli slot esistenti. La fonte energetica, il cervello e l'app già installata non devono essere resettati.

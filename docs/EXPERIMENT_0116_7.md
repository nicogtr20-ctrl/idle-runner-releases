# OVERMIND ZERO — Esperimento 0116.7: collisioni continue con casse

**11 ottobre 2026.** Base OTA.74, con memoria e personaggio intatti. Segnalazione utente: membrana e corpo a volte sembrano attraversare le casse e il colore arancione delle prese non appare anche con `PRESA ON`.

## Diagnosi dal codice

`SquareGripPuzzle` verificava inizialmente se un punto o un segmento fossero dentro un quadrato, prima della presa, quindi applicava correzioni di collisione di nuovo ai punti e al nucleo. Erano presenti due difetti: (1) nessun test completo del cammino precedente→attuale, e quindi un punto veloce poteva attraversare tutta la cassa tra due frame e terminare fuori dal lato opposto; (2) il nuovo spostamento dei punti dopo il primo controllo dei segmenti poteva nuovamente far attraversare una parete alle linee del perimetro.

L'arancione conferma un contatto con adesione fisicamente sostenuta, non che sia riuscita una presa bilaterale; una sovrapposizione o il tunnel di un punto possono rendere imprevedibile questa osservazione.

## Soluzione fisica senza modifiche al cervello

- Per ogni frame si fotografano **solo come buffer temporaneo** le posizioni precedenti delle 96 masse della membrana, delle 10 masse corporee e del nucleo centrale. I buffer NON sono memoria appresa o nuove informazioni della policy.
- Nuovo `SquareCollisionSolver.project`: intersezione del segmento temporale reale contro un AABB, con test anche quando entrambe le estremità risultano esterne e quando l'ingresso è esattamente al tempo 0 perché la massa era già appoggiata alla parete. Ferma il movimento alla **prima faccia raggiunta**, anziché espellere la massa sull'eventuale faccia opposta.
- `repairLinks`: dopo aver corretto i punti, ogni segmento fra masse adiacenti della membrana e del corpo è confrontato con l'interno rigido del quadrato; se lo attraversa, viene proiettato verso una faccia esterna. Il controllo è iterato, con un ultimo passaggio anche **dopo** l'ordinaria fisica di contatto.
- La dimensione fisica del campione della membrana è considerata puntiforme, per mantenere valido il contatto esatto sul piano della faccia e la necessaria adesione bilaterale. Un margine erroneamente non nullo azzerava ogni presa reale; un test storico l'ha rilevato e la regressione è stata corretta prima della pubblicazione.
- La cassa **non riceve impulsi dalla soluzione della collisione**. Resta impossibile spostarla tramite semplice spinta: il comportamento di `SquareGripPuzzle.constrain` continua a richiedere due prese neurali sostenute su facce opposte, e `SquareGripPuzzle.uncover` conserva il premio da +40 soltanto dopo spostamento vero di 55 unità.
- Modelli di apprendimento, 26 sensori, 16 azioni, colori della presa, luce dal bordo, stanze, cancelli, fonti energetiche, punti, reset, salvataggi e updater non sono cambiati.

## Test

- `SquareCollisionSolver.main`: tunnel completo da sinistra a destra con due estremi fuori, partenza esattamente sul piano della faccia, attraversamento dei segmenti con due punti esterni, correzione dei 96 punti e del nucleo/10 masse.
- `SquareGripPuzzle.main`: contatto e pressione senza presa non muovono la cassa, adesione su una sola faccia non sposta, una vera presa su due facce sposta e sblocca una sola energia +40. Test storico critico contro regressioni eccessivamente restrittive.
- Tutta la suite JVM precedente, test non-interferenza cognitiva e Android `assembleDebug`. CI verifica inoltre che il contenuto dei file dei controller, reward e dei salvataggi è byte-identico a OTA.74.
- Test sul telefono ancora richiesto: osservare se le casse restano impermeabili anche con braccia molto estese e movimento x20; verificare che i segnali arancioni compaiano quando una presa reale viene mantenuta, non per un semplice urto.

**Limiti**: le correzioni dei collegamenti non rappresentano una simulazione tridimensionale di membrana, e altri oggetti mobili/dinamici possono richiedere separati test di interferenza. Questa patch riguarda specificamente le casse quadrate; non introduce una nuova policy, la soluzione agli ostacoli o una capacità intelligente automaticamente acquisita.

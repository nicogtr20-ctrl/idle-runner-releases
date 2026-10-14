# ZERO OTA127 — INCASRO FISICO E RACCOLTA "SCIA": PROVA DEFINITIVA

14 ottobre 2026, analisi del video `1000107449.mp4` (8.857 s) caricato dall'utente e telemetria `zero-telemetry/reports/20261014-134328-dc7ec014062a` (14 parti), inviata su GitHub 2026-10-14 13:43:28 UTC. APK del registratore `0.11.6-ota.127` effettivamente in esecuzione: `currentZero.contact_geometry_forensics_v127` presente in part-001.json. Non serve un altro ciclo diagnostico generico.

## RISULTATO CERTO: membrana collassata completamente, interne attive

`contact_geometry_forensics_v127` contiene contatori `cg1|1|23`; ring buffer: 1 pickup e 9 trap. Il primo pickup:
`P,31917,290959,11,habitat,220,law=0,gem=-17539,-3319,radius=150,source=extra18,material=0,extra=1,edge=1341,mass=324,patch=1341,near=324`.

Tutti i valori spaziali del log sono *decimi di unità*, il tempo è decimi di secondo simulato. Ne segue:
- Prima risorsa di tipo blue/law0: effettivamente +22, posizione (-1753.9,-331.9), raggio 15.
- La distanza dal bordo della membrana è **134.1**, dal punto fisico della membrana più vicino **134.1**, dalla massa interna più vicina **32.4**.
- La collezione è avvenuta SOLO per `nearest < object.radius + 18` = 33; la fonte non era realmente toccata secondo `MaterialGeometry.touches(..., radius+2)` (`material=0`, `source=extra18`). È una ricompensa realmente concessa a distanza, non un contatto fisico diretto, in aggiunta allo schema normale.
- I 96 `membrane=...` del record sono **tutti identici**: la superficie non era visivamente invisibile, era *matematicamente collassata in un solo punto*. `held=96;redSites=96;crossings=0` non prova integrità: un poligono degenerato non può incrociarsi.
- Le 10 masse reali interne restavano distinte, con estensione X ~55.0 e Y ~31.5 nelle coordinate relative alla gemma; il centro segue il loro baricentro. La **scia** è un mero campionamento `trailX[0]=creature.cx; trailY[0]=creature.cy`, quindi mostrava i movimenti di questi 10 elementi non renderizzati singolarmente. La scia non compare in alcun test di pickup o collisione.
- Sì: il video mostra un singolo punto rosso presso le due casse, e la scia di puntini verde-acqua che si muove separatamente. Il nuovo dato ne certifica la causa in fisica.

## PROVA doppio vincolo e successivo rilascio

I 9 snapshot T salvati rappresentano campioni di contatto per due casse. **5 campioni consecutivi distanziati di 8 secondi simulati** (t29092.1, 29100.1, 29108.2, 29116.2, 29124.2) mostrano:
- `boxA=...:96, boxB=...:96, shared=96`;
- `uniqueMembrane=1`: TUTTI I 96 VERTICI ALLO STESSO PUNTO;
- masse interne ancora in 10 posizioni differenti; la scia mostra la loro traiettoria.
- Le due casse non sono state evidentemente assegnate a 96 strutture indipendenti: i due `box.boxGripHold[i]` della *stessa patch* possono risultare fisicamente trattenuti contemporaneamente. Gli stessi 96 nodi collassati sul punto di contatto doppio sono incompatibili con una membrana estesa.

Successivi 4 snapshot T a t29132.2, 29140.2, 29148.2, 29156.2:
- punti membrana distinti rispettivamente **24, 38, 42, 95**; doppie tenute **73, 57, 55, 0**; quindi si osserva liberazione graduale NON improvviso reset/teletrasporto.
- self-crossings in questi frame **1, 5, 3, 40**. Il valore40 nel quarto frame significa una membrana ampiamente auto-intersecata quando i nodi si dispiegano; grande anomalia geometrica.
- Essendo solo campioni, questi dati **non provano** che ci fosse intenzione cosciente di fuggire o che il core sapesse identificare lo stato "incastrato". Dimostrano invece i componenti e la sequenza fisica.

## Sorgente codice e azioni possibili (non ancora applicate)

1. `SquareGripPuzzle.constrain` copia il timer `box.boxGripHold[]` di ogni oggetto nella superficie scratch e poi lo ritrascrive. Contatti legalmente validi per la prima cassa possono risultare validi anche per una seconda, e ogni box ha timer autonomi sullo stesso indice, senza prova di fattibilità del doppio vincolo *per la stessa porzione di membrana*. `GripSurfaceComposition.compose` prende soltanto il max dei timer per la vista. Possibile conflitto geometrico nell'unione di vincoli.
2. `SquareCollisionSolver.correctLink` e `SquareGripPuzzle.resolveCrossingLinks` proiettano entrambi gli estremi di un segmento verso la faccia più conveniente; soluzioni iterate su solidi multipli possono progressivamente schiacciare un poligono sul punto comune. `BodyIntegrity.membrane` evita di rilassare i punti quando sono in presa, il che conserva la degenerazione. La sorgente specifica del collasso va confermata con test riproducibile a due casse; non inventare patch o separazione geometrica.
3. `Morphology.Surface.step` applica dinamica alle 96 particelle e azione muscolare, ma la logica di interparticle tension contiene `if(d<.0001f)continue`. Quando due particelle coincidono esattamente, la loro repulsione non può ristabilire una distanza positiva. Spiega la persistenza del nodo collassato anche se la rete cerca di riaprire.
4. `GameActivity.resolveObjects` autorizza il pickup se `MaterialGeometry.touches(... radius+2)` **OR** `nearest < 18+radius` (extra18). Il registro prova che questo secondo ramo ha pagato +22 senza materiale a contatto. È difetto della regola di contatto che si può correggere **senza cambiare il valore appreso o la politica**.
5. Correzione fisica richiesta SOLO dopo test di riproduzione, che deve preservare: corretto contatto a faccia +0 di una cassa, due prese opposte di punti distinti, libertà di scelta, movimento delle masse, nessuna forza di fuga impostata, nessun reset pesi. Test di invariante: 96 patch non collassano tutte nello stesso punto, doppio vincolo di una singola patch è geometrico e temporalmente coerente, non si aggiunge energia senza vero contatto; i test sui quadrati già presenti restano verdi.

## Disambiguazione decisiva

La SCIA non era un organo nuovo e non raccoglieva direttamente le gemme. Ma L'UTENTE AVEVA RAGIONE sulle osservazioni: punto rosso immobile = membrana completamente collassata, mentre 10 masse interne invisibili si muovevano e disegnavano una scia direzionale. La raccolta è stata autorizzata quando una massa interna invisibile è arrivata a 32.4 unità di una gemma raggio15 per via della tolleranza aggiuntiva18: `material=0`. La scia appariva quindi protagonista di una raccolta che in realtà era attivata da una regola permissiva sui nodi interni.

Stato: NESSUNA correzione a cervello, attuatori, fisica o energia in questo paper. La prossima modifica deve essere una **correzione del mondo dimostrato**, non un controllo sulle intenzioni di ZERO.

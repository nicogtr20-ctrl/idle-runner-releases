# ZERO 3.0 — ESPERIMENTO V3.2: APPRENDIMENTO MOTORIO CON CONFRONTO FISICO E VINCOLI DI COSTO

**Riferimenti obbligatori:** [Master blueprint](ZERO30_MASTER_BLUEPRINT.md) · [Contratto tecnico](ZERO30_IMPLEMENTATION_CONTRACT.md) · [UI V3](ZERO30_UI_UX_SPEC.md) · [Test 2.000 decisioni](ZERO30_LONG_HORIZON_2000_20261017.md).

**Stato:** PROTOTIPO SPERIMENTALE, **non attivo nel gioco**, non validato come nuova politica generale, non collegato alla nuova UI o ai salvataggi. [Sorgente sperimentale](https://github.com/nicogtr20-ctrl/idle-runner-releases/tree/feature/zero30-foundation-g0-g3-20261016), [PR #87 DRAFT](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

## Motivazione documentata

Il learner motorio online V3.1 dopo **2.000 vere decisioni di prova** nel gel ha peggiorato il rendimento heldout da un aggregato **+27,970 U** della corteccia vergine a **−19,284 U** di quella allenata; 0/4 intenzioni migliorate al checkpoint finale. La GRU autonoma allenata ha avuto allineamento cumulativo **−5,059 U**, contro **+18,237 U** con pesi congelati. Aumentare il numero delle esperienze per l'algoritmo attuale NON ha reso l'apprendimento vantaggioso.

Una ricerca episodica precedente aveva ottenuto miglioramenti apparenti in tre direzioni, ma con gross path e consumo spesso circa un ordine di grandezza superiori; poteva selezionare movimenti dispendiosi e con grande thrashing. Servono vincoli di qualità, e prove su posture **non** usate per la selezione.

## ADR ZERO3-M32 — candidato locale + rollback + qualità energetica

**File:** `Zero3MotorEfficientSearchRegression.java`.

1. Intenzione Est/Sud/Ovest/Nord imposta **solo da laboratorio**, al fine di separare il problema della corteccia da quello del brain. Non fornisce gait/target posizioni al controllore del gioco.
2. Ogni proposta modifica **solo il bias** di un canale muscolare in una regione e vicine, con interpolazione spaziale e ampiezza limitata. Le candidate sono casuali, nessuna sequenza biomeccanica programmata.
3. Si valuta il risultato col **vero solver materiale** in due pose iniziali diverse, 0° e 90°, usando 24 decisioni fisiche a posa per candidato. 64 candidate per direzione: **64 × 2 × 24 = 3.072 decisioni fisiche di addestramento per direzione**, almeno 12.288 su quattro direzioni, più verifiche. Non confondere queste prove con una singola vita ininterrotta di 3.072 decisioni: ogni prova parte dal proprio stato fisico noto.
4. La funzione di selezione penalizza **mancata traslazione, distanza lorda improduttiva e lavoro muscolare reale**, con coefficiente `score = forward - .34*max(0,gross-max(0,forward)) - 90*work`. Non c'è ricompensa fittizia, la funzione misura la qualità motoria relativa alla **intenzione del laboratorio**.
5. Non si accetta una candidata se in una posa causa più di 2 U di regressione direzionale, oppure se richiede oltre circa +20% lavoro muscolare rispetto alla candidata precedente (con piccola tolleranza). In caso contrario la candidata sostituisce la precedente solo se la qualità aggregata migliora di oltre 0.035 unità.
6. Verifica successiva su **±45°**, mai usati nella selezione, per 160 decisioni senza rumore contro il cervello vergine, con verifica anche del costo. L'esito di questo controllo **NON è inserito** nel processo di selezione delle candidate.
7. Questa è una strategia di ricerca parametrica empirica e locale (analoga a esplorazione evolutiva controllata), non un world model completo, actor-critic di produzione, continuità interepisodica online o abilità già dimostrata nel mondo con gemme.
8. Non promuovere la nuova politica nel gioco neppure se in laboratorio alcune metriche migliorano. È indispensabile prova **20+ seed, più pose, ostacoli, energy & damage, ritenzione dopo morte, nuova UI e checkpoint**.

## Esperimento GitHub

Sul ramo ZERO3 il workflow `.github/workflows/zero30-foundation-qa.yml` include ora il test **cost-constrained skill discovery with unseen rotated poses**. [Pagina delle Actions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions).

## Risultati

**Da compilare solo dopo la lettura di CI conclusa**, riportando tutte e quattro le direzioni, costi, esiti negativi, sintesi e limiti. La compilazione riuscita NON è una prova di apprendimento riuscito.

**Gate G3 resta BLOCCATO** finché non esiste evidenza più forte che la nuova proposta controlli davvero il gel meglio, a parità di intenzione, energia e condizioni non viste.

## RISULTATI VERIFICATI CI 38974164028 — 16/10/2026 UTC

**[GitHub Actions SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38974164028):** tutte le prove del corpo e 2.000 decisioni, ricerca locale energeticamente penalizzata, build Android Debug superate. La **qualità delle skill resta in parte insufficiente**.

**Dati reali:** 64 proposte per ciascuna delle quattro direzioni, **12.288 decisioni fisiche totali usate per selezionare i pesi** sulle due pose di training, più valutazione su ±45° mai impiegate nella scelta. Il corpo viene reinizializzato a ogni proposta (NON una vita continua di 12.288 scelte).

| Intenzione | Proposte accettate | Utility vergine su ±45° | Utility candidata su ±45° | Rispetta costo in entrambe le pose? | Skill valida? |
|---|---:|---:|---:|---|---|
| Est +X | 15/64 | +28.806 | +31.108 | **No** (0.0473→0.0697 e 0.0562→0.0723) | **NO** |
| Sud +Y | 9/64 | +4.476 | +52.749 | **Sì** (0.0562→0.0549 e 0.0510→0.0543) | **SOLO candidata di laboratorio** |
| Ovest −X | 10/64 | −49.582 | +30.913 | **No** (0.0510→0.0882 e 0.0525→0.0894) | **NO** |
| Nord −Y | 6/64 | −24.705 | −11.633 | **No** (0.0525→0.0556 e 0.0473→0.0599) | **NO** |
| **Aggregato** | 40/256 | **−41.005** | **+103.137** | **1/4** | **G3 BLOCCATO** |

L'aggregato di utility cresce, ma **il lavoro muscolare totale valutato sale da 0.4139 a 0.5443** (circa +31.5%). È scorretto presentare il risultato come locomozione generalizzata o maggiore efficienza del corpo. Solo Sud supera il vincolo di costo in entrambe le pose mai usate per la selezione; è un segnale preliminare promettente **su quella famiglia di pose**, non una competenza operativa.

### Diagnosi ingegneristica

- **Confronto di scala:** a differenza del learner online V3.1, la ricerca di bias locali produce miglioramenti direzionali robusti in alcuni contesti dopo molti test fisici, ma tende ancora a scegliere modi molto costosi di muovere il gel.
- **Conclusione verificabile:** un sistema di selezione e rollback offre un'alternativa plausibile alla plasticità online non controllata; le soglie di costo e gli stati corporei **devono essere resi parte della competenza contestuale**, non solo un filtro a posteriori.
- **Limitazione cruciale:** tutte le prove avvengono nello stesso mondo di fisica, senza risorse e ostacoli; le pose ±45° sono tenute fuori dalla selezione, ma non sono mondi/test ambientali indipendenti e **non** dimostrano generalizzazione multiseme.
- **Prossimo blocco:** ricerca e credito che tenga conto di inerzia iniziale, contatto reale e previsione condizionata dalla postura, più convalida ambientale; proibita la promozione delle altre tre direzioni.

**Dopo CI:** nessun aggiornamento della vera app, nessun collegamento al controllo ZERO 2.2/3.0 in gameplay, nessun OTA firmato.

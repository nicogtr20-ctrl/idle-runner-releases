# ZERO 3.0 — V3.31 | Prova fisica controfattuale del credito motorio locale

**Protocollo fissato prima del codice e prima della CI il 17/10/2026.** Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.30](ZERO30_V330_MOTOR_CAUSAL_AUDIT.md), [V3.19](ZERO30_V319_CONTEXTUAL_GRIP_ENERGY.md).

## Diagnosi motivante
V3.30 ha rilevato un fallimento **scientifico** della corteccia motor-learning (0/4 ambienti migliorati, TRAIN signed +2,281474 vs FROZEN +5,478730). La regola attuale di `AdaptiveCortex.learn` moltiplica la perturbazione del canale per un vantaggio **globale** della decisione. Non identifica quale muscolo abbia spostato il corpo, quanto ritardo abbia avuto né quanto lavoro addizionale abbia richiesto. L'indice di comandi opposti è soltanto un proxy; non giustifica correzioni arbitrarie di locomozione.

## Intervento V331 registrato prima dell'esito
Costruire una fixture **puramente diagnostica, fuori dal runtime mobile** a partire da tre corpi veri fisicamente identici, tutti creati con il medesimo solver gel e stessa condizione iniziale, stesso mondo (FREE, BOX_NEAR, DISK_NEAR, BOX_FAR) e medesima intenzione *autonomamente scelta* dal recurrent actor V317 usando soltanto la propria osservazione sensoriale PRE. Usare la stessa corteccia non-plastica e lo stesso motoneurone adesivo V319 congelato in ogni braccio. In 12 substep fisici, applicare a UN solo comando muscolare non-adesivo di un settore anatomico una perturbazione rispettivamente **+0,20** e **−0,20**, oppure nessuna (CONTROL); per 24 ulteriori substep nessuna perturbazione. Il motore calcola davvero l'uscita a ogni sottopasso usando la propria propriocezione, poi il vero solver gel+contatto, **mai** spostamento manuale del COM, modifiche degli attriti, reward artificiale o override dell'intenzione.

Design deterministico: **4 mondi × 6 condizioni pose × 8 settori × 3 canali non adesivi (0/1/3) = 576 triplette indipendenti**, ciascuna 3 × 36 = 108 integrazioni fisiche del solver; 62.208 substep in totale. Almeno un braccio CONTROL duplicato per controllare determinismo. Le scene sono nuove rispetto ai 20 ordinal V330, ma provengono dalle stesse quattro famiglie: non chiamarle validazione di generalizzazione a nuove topologie.

Misurare direttamente per braccio: posizioni corpo pre/dopo impulso t12 e dopo inerzia/relax t36; vera energia lavoro `gel.work`, presa BOX fisica e tocco; intensità effettivamente **consumata dal gel**; confronto differenziale `plus-minus` a tempi t12 e t36, `responseAlongActorIntent`, costo differenziale, non-linearità `plus+minus-2*control`, eventuale invertibilità del segno. Contrasto triplo sul medesimo identico stato isola la perturbazione del motore da qualunque velocità iniziale, ma può anche misurare la successiva retroazione della propriocezione — NON chiamarlo gradiente esatto o predittore validato. Tutti gli outcome guardano il POST fisico; il cervello non legge differenze controfattuali come se fossero proprie esperienze.

**Invarianti tecniche PASS**: tutte le 576 triplette finite, stesso stato iniziale, nessun contatto/grip fittizio, almeno una perturbazione nonzero *effettivamente applicata* in ogni tripletta non saturata, CONTROL deterministico, modello/solver/fit V3.19 rimangono invariati. Report per ognuna delle quattro famiglie: risposta immediata e ritardata, costi, casi in cui l'impulso peggiora l'esito, dipendenza settore/canale e sensibilità di contatto; NON ripulire le misure negative.

**Gate scientifico separato e preregistrato**: un impulso locale è solo *causalmente rilevabile* se almeno 50% delle triplette ha differenza fisica t36 maggiore di 1e-5 unità; fallire altrimenti. Questo non misura abilità appresa e **non può promuovere G3**, qualunque sia il risultato. Una successiva V332 può usare soltanto i dati TRAIN di V331 per progettare un credito locale con controfattuali durante l'addestramento, dovrà fissare prima test BLIND matched non consumati e superare trained-v-frozen con costi non peggiori.

No nuove GUI finte. Nessun merge, build APK pubblicata o OTA; PRODUCTION v0.11.6-ota.172 e 12 salvataggi 2.2 immutati.

## Risultati

## RISULTATI EFFETTIVI CI — 17/10/2026

**[GitHub Actions run 39038248576 — SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39038248576)**; job 117412327574: compilazione solver gel/contatti reali, esecuzione completa, compare di CONTROL deterministici e **Android Debug BUILD SUCCESSFUL in 1m 11s**. Gate tecnico `technicalCounterfactualIntegrity=PASS`; gate fisico preregistrato `scientificPhysicalSensitivity=PASS`; G3 locomozione **resta BLOCCATO**.

### Numeri osservati, senza promuovere un'abilità

**576 triplette +CONTROL/−** su 4 famiglie, ciascuna con 6 condizioni di orientamento/pose, 8 settori fisici (0,2,...14), 3 canali non adesivi (0,1,3): **62.496 veri passi gel** (62.208 nella batteria tripla e 288 nei 4 controllo-determinismo duplicati), senza alcuna forza diretta al COM, andatura prescritta, nuova ricompensa o intervento sul solver. Le **576 triplette condividono 24 configurazioni pose/mondo**: sono interventi distinti, NON 576 osservazioni statisticamente indipendenti per dimostrare generalizzazione o significatività. L'attore V317 sceglie l'intenzione dal proprio vero PRE, poi è mantenuta identica nei tre bracci della stessa tripletta.

| Famiglia | Triplette con effetto t36 maggiore di 1e-5 | Casi risposta differenziale lungo intento negativa | Casi con risposta ritardata più forte dell'immediata | Contatto vero nella CONTROL | Grip fisico vero CONTROL | Differenza media in norma al t36 | Differenza media in norma al t12 |
|---|---:|---:|---:|---:|---:|---:|---:|
| FREE | 144/144 | 72/144 | 130/144 | 0 | 0 | 0,190192849 | 0,064944820 |
| BOX_NEAR | 144/144 | 71/144 | 111/144 | 144 | 0 | 0,194433487 | 0,066185489 |
| DISK_NEAR | 144/144 | 76/144 | 118/144 | 144 | 0 | 0,188419898 | 0,064729940 |
| BOX_FAR | 144/144 | 72/144 | 122/144 | 0 | 0 | 0,185154467 | 0,064498762 |
| **TOTALE** | **576/576** | **291/576** | **481/576** | **288/576** | **0** | **0,189550175** | **0,065089753** |

- **Sensibilità fisica verificata in tutte le famiglie:** differenza di traiettoria t36 per +0,20 vs −0,20 supera 1e-5 in 576/576, ben oltre la soglia preregistrata 50%. È dimostrata **controllabilità locale non nulla**, NON coordinazione ottimale dei 64 canali.
- **Ritardo effettivo:** 481/576 (83,51%) hanno contributo dopo la fine del polso (t12→t36) maggiore della differenza durante il polso. Questo rende un *credito puntuale contemporaneo* poco difendibile. Media norma del differenziale da t12 a t36 = **0,124762147** unità; media norma differenza finale **0,189550175**. Sono medie di **norme**, non l'uguaglianza vettoriale fra le due quantità.
- **Segno variabile rispetto alla direzione scelta:** la differenza del cambiamento **PLUS − MINUS** proiettata sull'intenzione è negativa in 291/576 (50,52%). Non chiamarli 291 «movimenti sbagliati» o 291 episodi falliti: misura solamente che il **segno +0,20 per quello specifico muscolo** aveva una proiezione peggiore del segno −0,20 in quella coppia. La somma signed globale delle differenze è −0,780821417; in altre pose/canali il segno può invertire.
- **Non linearità e retroazione:** 576/576 differiscono dal centro semplice `(PLUS+MINUS)/2 = CONTROL` sopra 1e-5, indice medio di curva **0,044709260**. Poiché gli output dei canali non perturbati dipendono dalla propriocezione aggiornata dopo gli impulsi, il differenziale complessivo è un **effetto causale di intervento con feedback**, non la derivata isolata e stazionaria di un muscolo.
- **Contatto/adesione:** tutte le 144 triplette BOX_NEAR e le 144 DISK_NEAR hanno contatto fisico autentico nella CONTROL, ma il motoneurone adesivo era **congelato e inizialmente non addestrato**, quindi non emergono `gripHold` legali. Questa prova **NON** confuta il precedente PASS V3.19 sulla presa addestrata, né dice nulla sulla sua efficienza.
- **Costi:** differenza totale di lavoro PLUS-MINUS −0,000201299 per tutte le 576 triplette; è una somma firmata non un risparmio della policy addestrata. Comparazioni fisiche pertinenti tra +/- e baseline sono nei log. Non attribuire miglioramento energetico a ZERO V3.

**Conclusione scientifica:** il vecchio punteggio globale della corteccia non tiene conto dell'elevata dipendenza settore/segno e del ritardo misurato. La V3.31 sostiene l'utilità di **attribuzione differenziale locale con memoria temporale**, ma non dimostra ancora che essa migliori l'apprendimento. **Il gate G3 resta FAIL** dal confronto trained-v-frozen V3.30, G5 predizione FAIL/non validato; nessun nuovo neurone, abilità o policy V3 live è stato introdotto.

### Prossima fase V3.32, criteri non negoziabili

1. Usare prove paired TRAIN fisiche per stimare il **credito del settore/canale/ritardo** senza far diventare l'actor un controller che legge il futuro. In una vera partita sono ammissibili soltanto osservazioni PRE e POST vissute, e update solo dopo un outcome autentico; eventuali controfattuali di banco vanno dichiarati **offline**.
2. Definire in anticipo **nuovi mondi holdout** e comparare cortex tradizionale V3.30 vs nuova regola di credito vs congelata, sempre stesso V319 adesivo, costi reali, intenzione non prescritta, stessa nascita fisica, includendo inerzia e contatti.
3. Richiedere progressi di direzione realizzata e costo per movimento utile, retention dopo restart (non solo training online), robustezza multiscena e presa non peggiorata; **se falliscono, riportare FAIL e non integrare Android**.

**No OTA/merge, PR #87 Draft, versione stabile OTA172 e tutti i 12 salvataggi 2.2 intatti.**

# ZERO 3.0 — V350 | Credito causale: durata GRU non eseguita

**18 ottobre 2026, PR #87 DRAFT; G4 ancora aperto.** Riferimenti Master §§5,9,13 e V347/V348/V349. Bug di coerenza confermato nel codice precedente: `Zero3RecurrentBrain.choose` produce quinto output `duration` in 0.25–0.65 s ma `Zero3RuntimeV325` esegue rigidamente 12×0.025=0.30 s. La `Zero3RecurrentBrain.consequence` precedente applica comunque il gradiente REINFORCE anche alla quinta riga `actor`, **attribuendo reward reale a una durata non eseguita**.

## Modifica strettamente necessaria
- Il nuovo `consequenceFixedClockV350` viene chiamato **solo** dal runtime GRU V325 a timing fisso. Mantiene training di tutti i gates GRU, di entrambe le critic, del predittore world e dei quattro output **realmente eseguiti**. La quinta riga duration non riceve credito inventato.
- La firma `consequence` di laboratorio originale rimane disponibile per esperimenti con durata esplicitamente modellata; dimensioni, RNG, checkpoint V345, fisica, controller, V319, sensori e 12 substep sono invariati.
- Non confondere questa correzione con **implementazione della vera durata variabile**: richiede nuova versione con timer/interruzioni, esperienza con frame count variabile, meccaniche/metabolismo e checkpoint coerenti. La V350 non chiude G4.
- Nessun target imposto, bonus di movimento, attrazione verso oggetti o override delle scelte autonome; preserva 2.2 OTA172 e 12 cervelli.

## Gate verificabile
`Zero3DurationCreditV350Regression`: in due GRU inizialmente identiche, un braccio mantiene il vecchio credito fittizio mentre il secondo lo nega. 40 decisioni riproducibili con premi numerici **solo nel test di unità** dimostrano: primi quattro output fisici e loro pesi identici bytewise, gates/critic/world identici, quinta riga del candidato invariata e modificata dal vecchio learner. Poi salva `V345` e riprende 24 scelte e update identici. I premi simulati del test **non** vengono usati dal runtime né costituiscono prova di survival/value learning.
**VERIFICATO:** [CI V350 39086546044 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39086546044): V350 NO_FICTITIOUS_DURATION_CREDIT=PASS, 64 update con 5° readout non addestrato, prima 4 output identici, dopo 24 scelte restore identiche, V347/V348/V349 schema e Android Debug PASS. CI aggiunge inoltre V347 512 decisioni fisiche, V348 cold restore, V349 codec e Android assembleDebug, sempre con reward reale `V344.After.netEnergy` nel runtime.

**Non promuovere G4/G5/G6 o OTA senza ulteriori test scientifici** su duration realmente eseguita, value learned, skill e nuovi mondi.

# ZERO OTA151 — locomozione: può farlo, deve imparare, o bug?

**Data:** 15 ottobre 2026, orario italiano. **Nessuna modifica alla fisica né APK prodotto in questo audit.** L'utente ha creato un nuovo ZERO e riferisce ondeggiamento "serpente nello spazio", deformazioni evidenti ma poco movimento netto.

## Fonti e limiti
- Codice *reale attuale* su `feature/zero22-active-recurrent-ota117`: `SymbioteMatterV1.java`, `SymbioteMotorHeadV1.java`, `SensorimotorMind.java`, `RecurrentCoreV22.java`, `GameActivity.java`.
- Il repository `zero-telemetry/current.json` espone ancora report **20261014-234158-fd2799d5a250**, pre-OTA151. NON descriverlo come telemetria del nuovo ZERO nato oggi, e non dichiarare verificato il suo comportamento fisico su Android senza un upload aggiornato.
- Benchmark deterministico pre-release nella branch **`investigate/zero152-locomotion-capability`**: `app/src/main/java/com/overmind/zero/ZeroLocomotionCapacityDiagnostic.java`, GitHub QA [38712289045](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38712289045) **SUCCESS** (Java diagnostica + invarianti + APK debug). **Nessuna modifica ai sorgenti dell'app, firma, reward o mappa.**
- 40 secondi simulati, scena senza contatti oggetti/food e senza full gameplay. Tutte le prove utilizzano la stessa `SymbioteMatterV1.step` e centro di massa delle 170 masse. Per la prova "virgin-core" il core non è allenato, produce punteggio iniziale; si applica sempre la stessa azione scelta, senza esperienza con ostacoli, cibo o danni. La prova "virgin-explore" seleziona nuove azioni con probabilità 25% ogni 0.65s, e "random-actions" tutte le azioni casualmente. La prova "lab-cycle" alterna comandi esterni; **questi non fanno parte di ZERO, quindi non dimostrano competenza imparata**.

## Risultati misurati in 40s (world units, non pixels)
| Caso | Spostamento netto | Percorso globale | Efficienza net/percorso | Deformazione finale media | Decisioni | Azioni distinte | Invalid/Folds |
|---|---:|---:|---:|---:|---:|---:|---|
| Passivo senza comandi | 0.119 | 12.580 | 0.009 | 0.079 | 0 | 0 | 0/0 |
| Motore vergine, core stabile | **295.908** | 305.610 | **0.968** | 38.019 | 62 | 1 | 0/0 |
| Motore vergine, esplorazione 25% | 38.196 | 160.312 | 0.238 | 14.905 | 62 | 11 | 0/0 |
| Motore vergine, azioni casuali | 11.175 | 180.152 | 0.062 | 20.184 | 62 | 15 | 0/0 |
| Motore, sequenza ciclica SOLO in laboratorio | 13.760 | 367.210 | 0.037 | 9.911 | 0 | 0 | 0/0 |

### 1. PUÒ spostarsi fisicamente? **SÌ, provato nell'arena fisica vuota.**
La fisica consente un avanzamento netto più che abbondante già con la testa corticale vergine e un'unica azione stabile. Non sono necessari altri attuatori, gambe, arti, modelli preaddestrati, forze al baricentro o premi per il movimento per *rendere possibile* la traslazione. La stessa fisica con comandi variabili produce soprattutto oscillazione; questo corrisponde al comportamento dell'utente senza provarne ancora la causa puntuale sul cellulare. Limite: la simulazione non include i contatti e gli obiettivi della partita intera.

### 2. Deve imparare a usare il corpo? **SÌ, per coordinamento, direzione, presa, ostacoli, sopravvivenza.**
Nello stato vergine non possiede una politica affidabile di selezione e sequenziamento di azioni per cercare energia o manovrare solidi. Le azioni 2.2 cambiano anche per esplorazione. Movimento grossolano ≠ progressi verso risorse. Un primo periodo di ondeggiamento non implica un guasto meccanico.

### 3. Il codice gli permette DAVVERO di imparare il movimento? **NON COMPLETAMENTE: difetto dimostrato nei segnali del motor head.**
Nel `SensorimotorMind.observe` attivo:
`symbioteHead.consequence(actualEnergySinceChoice, actualContactHarm, 0f, learning&&!holdout, holdout);`
Il terzo parametro (effort) è **sempre zero**, anche quando il corpo spende lavoro reale. `actualEnergySinceChoice` è energia raccolta da contatto, `actualContactHarm` danni di contatto; in una lunga fase senza pickup né danno entrambe sono zero. Nel `SymbioteMotorHeadV1.consequence`:
`reward=clip(actualContactEnergy/35-actualDamage/90-.0008*effort,-2,2)` e `advantage=clip(reward-meanReward,-2,2)`; `meanReward` di un nuovo brain è zero. Quindi `advantage=0`, le 84 connessioni del cortex (56 vecchie +28 nuove) ricevono un gradiente esattamente zero, malgrado `updates` aumenti. Il cervello 2.2 *può* modificare altri parametri tramite errori e segnali di energia, ma **il controllore muscolare resta immutato senza conseguenze che il suo collegamento attuale sappia riconoscere**. Questo NON è "ZERO deve solo fare più esperienza": un numero arbitrario di azioni senza pickup/danno non può migliorare quel controllore. Passare semplicemente il lavoro meccanico come costo negativo non garantirebbe locomozione: rischia di insegnargli solo a non muoversi. Non introdurre reward per il solo spostamento.

### 4. I test precedenti erano sufficienti? **NO, avevano un limite.**
Il vecchio controllo `SymbioteMatterV1Regression` dimostrava 0.8 unità di spostamento con una sequenza temporale artificiale. Non provava che ZERO sapesse controllarla. Il nuovo test di capacità prende comandi vergini del controller reale e confronta la traslazione netta con l'oscillazione. Diagnosi migliore ma non sostituisce il test in vivo con ostacoli/risorse e azioni realmente decise durante la vita dell'organismo.

## Conclusione ingegneristica
- **Capacità fisica: dimostrata.** Nessun bisogno di cambiare corpo per questa ragione.
- **Competenza attuale: non dimostrata; deve imparare.** Una "serpe" che ondeggia può essere normale all'inizio.
- **Capacità di apprendimento neuromuscolare: parzialmente ostruita da un difetto di segnale.** Non aspettarsi miglioramenti della testa muscolare con sole scelte che non producono eventi energetici/danni, benché il contatore cresca.
- **Non pubblicare la modifica speculativa di attrito in `fix/zero152-material-traction`.** La fisica base ha già capacità dimostrata; questa modifica è in branch **non integrata** e non è stata testata/giustificata in vivo.
- Prima di un altro APK, progettare una conseguenza fisica *vera* e attribuibile alle azioni per il cortex: contabilizzazione dell'energia di sopravvivenza realmente persa/acquisita e del lavoro materiale reale, bilanciata per non premiare o penalizzare automaticamente il movimento; preservare motivazione energetica primaria e validazione heldout. Verificare con test che `weights` cambino quando ci sono effetti REALI e non per pura deformazione, senza inventare punteggio di movimento, quindi confrontare lo stesso cervello sul telefono prima/dopo.
- Richiedere esplicito upload del nuovo `zero-telemetry` OTA151 quando disponibile; analizzare gli spostamenti netti per vita/azione e valori di rewards/meanReward/pesi, gli eventi reali di energia, pressioni e actual damage. Non confondere grandi distanze accumulate con spostamento netto utile.

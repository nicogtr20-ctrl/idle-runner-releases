# ZERO 3.0 — V3.43 | Scelta autonoma ≠ manipolazione obbligata

**18 ottobre 2026.** Fonti obbligatorie [master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [protocollo prereg V343](ZERO30_V343_AUTONOMY_ENERGY_CAUSALITY.md), [V342 long horizon](ZERO30_V342_TEN_THOUSAND_CONTIGUOUS_BOX_RESULTS.md).

## Valutazione scientifica del principio richiesto dall'utente

**Non spostare una cassa non è automaticamente un errore.** Il test V342 ha mostrato che la testa V319 fisicamente riesce a produrre una vera presa bilaterale e che il FULL lo fa durante le prime ~2000 decisioni; non ha provato che il FULL *volesse* tornare a spostarla quando non c'erano più motivi specifici. Valutare separatamente `can`, `chooses`, `learnsConsequences`; non misurare "autonomia" come numero di casse toccate. Né una postura immobile prova preferenza, né più prese prova intelligenza.

**Gap d'architettura già verificato dal codice:** il laboratorio V342 usa `Zero3SensoryFrameV315.capture` e `V317.Encoder.before(pre)`. Il contratto V315 imposta `energyAvailable=false`, non inserisce riserva nelle ray e misura solo distanza agli ostacoli senza colore/sorgente violetta; la V317 può in teoria ricevere energia tramite `before(pre,measuredFuelRatio,previouslyObservedDamage)` introdotta in V327 ma NON è usata dal V342. Il `learnV333` della cortex premia soltanto la coerenza del movimento locale e la correlazione verso l'intenzione scelta: **non è un value-learner dimostrato per conseguenze di raccolta energetica**. Quindi oggi un esperimento utile-vuota che generi *solo* energia nel WORLD, senza un sensore di energia causale né credito associativo, non è un test sensato della capacità di ZERO di preferire la cassa conveniente.

## V3.43-A — fisica e conseguenza naturale, CI verificata

[GitHub Actions **39076384397 SUCCESS tecnica**](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39076384397) su rama LAB PR #87 (il primo run [39076270142](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39076270142) era fallito nella compilazione per un'inutile inclusione della classe V326 dipendente dal runtime Android, rimossa senza cambiare fisica). Aggiunti `Zero3CoveredEnergyV343` e `Zero3CoveredEnergyV343Regression` esclusivamente in test:
- Riconoscono l'energia nascosta law4 e la sua vera BOX law5 attraverso le coordinate **spawn identiche** della loro coppia, come già previsto da `UnknownWorld.newHabitat`. Assicurano **nessun rilascio se BOX immobile, singolo punto di presa, spostamento senza pregressa vera flag `bilateralHeldNow` o distanza inferiore a `SquareGripPuzzle.UNCOVER_DISTANCE`**.
- Dopo vera prova di grip bilaterale (flag proveniente dalla fisica, **simulata manualmente solo nella fixture per testare il bridge**, NON nella policy reale), BOX deve allontanarsi almeno dalla soglia geometrica; allora la fonte **originale** cambia `concealed true→false` una volta sola, senza teletrasportare corpo, fonte, cassa o cambiare i comandi.
- `Zero3SurvivalLedgerV323.physicalStep` su una membrana realmente a contatto raccoglie +**40** unità di energia autentica **una volta** da `UnknownWorld.Entity.touch`, aggiornando contatori, riserva e `consumed`; nessun premio per il solo spostamento della BOX, nessuna raccolta prima dell'uncover. Un'altra cassa identica **senza fonte** resta senza premio anche dopo il movimento. `OpticalCues` conserva debole rim reale e occlusione, la fonte non viene mostrata come premio anticipato.
- MultiObjectGripRegression con 3 casse/gate e tutte 7 combinazioni PASS, OpticalCues, UnknownWorld e Android `assembleDebug BUILD SUCCESSFUL in 1m 5s`; **NO signed APK/OTA**. Questa fixture dimostra coerenza fisica e metabolica locale, NON che l'attore abbia scoperto o scelto di muovere la BOX, perché la prova usa flag sintetiche per simulare un evento che nel mondo vero proverrà dal solver.

## V3.43-B — disimpegno spaziale reale: identica riproduzione 10.000 ×2, PASS diagnostico

**[CI 39076406020 — SUCCESS tecnica, replica scientifica V342 PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39076406020)**, classe laboratorio `Zero3DisengagementV343Regression`. È la stessa vita **1601 già consumata** del V342, con cervello/attore/64 motori/96-patch gel/BOX/fisica V338/pesi e reward identici; l'unica differenza è un osservatore READ-ONLY del rapporto fisico fra COM del corpo e posizione aggiornata della cassa. FULL e FROZEN: **10.000 decisioni autentiche consecutive ciascuno, 120.000 substep reali ciascuno, zero censura, test Android Debug BUILD SUCCESSFUL in 1m 3s, NO APK OTA**.

**Replicati esattamente, senza tuning né modifica del circuito**: FULL true bilateral **1.931**, BOX gross **13.190067**, RAW work **5.786657810**; FROZEN true bilateral **7.934**, BOX gross **58.221730**, RAW work **4.032796383**. Se fossero divergenti, il test avrebbe fallito. Definizione *zona vicina* pre-registrata = distanza COM↔centro BOX `<= SquareGripPuzzle.HALF+64`; questo è una metrica spaziale grossolana e NON un criterio di volontà.

| VERO indicatore spaziale post-WORLD | FULL_LOCAL_DELAY | FROZEN |
|---|---:|---:|
| Primi 1.000, distanza dalla BOX | 38.043547 | 39.676398 |
| **2.000**, distanza dalla BOX | **92.932789** (fuori zona) | **41.737338** |
| **3.000**, distanza dalla BOX | **451.802845** | 40.141470 |
| **5.000**, distanza dalla BOX | **1426.879404** | 41.111001 |
| **8.000**, distanza dalla BOX | **3.704.920367** | 37.989407 |
| **10.000**, distanza dalla BOX | **5.744.799070** | **38.905236** |
| Minimo COM↔BOX lungo 120.000 frame | 37.843734 | 37.395702 |
| Massimo COM↔BOX lungo 120.000 frame | 5.744.799070 | 50.952128 |
| Frame nelle vicinanze della BOX, su 120.000 | **23.637** | **120.000** |
| Uscite dalla zona | **1** | **0** |
| Ritorni dopo uscita | **0** | 0 (non è mai uscito) |
| Primo frame fisico di uscita | **23.637** (~decisione 1.970) | nessuno |
| Presa bilaterale autenticata | **1.931** | **7.934** |
| BOX gross movement nel periodo 2.000→10.000 | **0** | **42.733024** |

Per FULL, nel 1º blocco di mille decisioni vi sono 12.000/12.000 frame vicini e 499 bilaterali, nel 2º 11.637/12.000 frame vicini e 1.432 bilaterali; **dalla decisione 2.000 alla 10.000: 0 frame vicini, 0 frame owner BOX, 0 bilaterali, 0 BOX displacement per ogni blocco da mille**. Il cervello rimane fisicamente lontano perché il COM prosegue per molte migliaia di unità. Per FROZEN, **tutti i 120.000 frame** restano nella fascia vicina, ed è possibile osservare nuove prese autentiche anche molto tardi, per esempio +2471 bilaterali fra 8.000 e 9.000 decisioni.

**CORREZIONE INTERPRETATIVA OBBLIGATORIA DEL V342:** non dire «il FULL ha dimenticato come muovere una cassa» sulla base dei 0 movimenti dopo 2.000: **si è allontanato dalla BOX e non vi è più tornato**, quindi la successiva assenza di manipolazione è prima di tutto un'assenza di opportunità fisica, non dimostrazione di perdita della capacità. Nemmeno la vicinanza costante del FROZEN prova motivazione o preferenza: è una proprietà della traiettoria osservata, non una sua dichiarazione d'intenzione. La V342 **dimostra** che FULL sa afferrare e muovere una BOX quando ne ha contatto e che le traiettorie di FULL e FROZEN divergono nettamente nel lungo periodo, ma **non discrimina** esplorazione, scelta basata su valore o errore di navigazione. In V342 non esistono fame/premio sotto quella BOX accessibili alla policy, dunque nessun test della convenienza di tornarvi è ancora valido.

V343-B è **diagnostica su scena consumata** e **NON** nuovo holdout, non prova G3 né G5; la causalità fra valori energetici, sensori e azioni richiede la futura V344.

## Prossimo requisito prima del confronto utile/vuota

Implementare un **contratto sensoriale e un learner di valore** che:
1. Leggano energia e danno solo dall'ultimo vero `Zero3SurvivalLedgerV323` decisione completata (PRE; no retroazione del futuro).
2. Permettano vedere soltanto weak cue ottico reale `OpticalCues` con occlusione da altri solidi, non l'etichetta `physicalLaw` o `concealed`, e nessun segnale artificiale che dica `questa è la cassa giusta`.
3. Aggiornino preferenze e scelte da ricompense energetiche realmente raccolte **e** costi veri; per il primo confronto useranno coppie nuove di casse identiche e condizioni controbilanciate; non dare bonus per semplici prese, trascinamento o ritorno.
4. Eseguano test 10k nella stessa vita con backup completo (corpo, gel, cervello, actor, BOX, reward source e RNG) a più boundary, verificando restart, non soltanto 10k in memoria. Nei dati V342 il limite archivio V322B `<=256` impedisce di chiamare crash-safe un replay di 10k decisioni.

**G3/G5 BLOCKED, PR #87 DRAFT non merged, NO OTA, legacy OTA172 e 12 cervelli V2.2 intatti.**

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

## V3.43-B — disimpegno spaziale su vita V342 già consumata

`Zero3DisengagementV343Regression` è un'osservazione **read-only** della stessa singola vita LONG ordinal1601 per FULL e FROZEN, 10k decisioni/arm con esattamente stessi policy/gel/work/BOX del V342, senza hidden reward né modify actor. Ad ogni 1000 decisioni misura distanza COM↔BOX, frame entro `HALF+64`, uscita/ritorno dalla zona e spostamento BOX, e richiede come gate repliche esatte in tolleranza di BOX gross FULL13.190067/FROZEN58.221730, bilateral FULL1931/FROZEN7934 e RAW work FULL5.786657810/FROZEN4.032796383. **[GitHub Actions 39076406020](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39076406020)** — da riportare dal vero log dopo conclusione, senza affermare ancora intenzioni.

## Prossimo requisito prima del confronto utile/vuota

Implementare un **contratto sensoriale e un learner di valore** che:
1. Leggano energia e danno solo dall'ultimo vero `Zero3SurvivalLedgerV323` decisione completata (PRE; no retroazione del futuro).
2. Permettano vedere soltanto weak cue ottico reale `OpticalCues` con occlusione da altri solidi, non l'etichetta `physicalLaw` o `concealed`, e nessun segnale artificiale che dica `questa è la cassa giusta`.
3. Aggiornino preferenze e scelte da ricompense energetiche realmente raccolte **e** costi veri; per il primo confronto useranno coppie nuove di casse identiche e condizioni controbilanciate; non dare bonus per semplici prese, trascinamento o ritorno.
4. Eseguano test 10k nella stessa vita con backup completo (corpo, gel, cervello, actor, BOX, reward source e RNG) a più boundary, verificando restart, non soltanto 10k in memoria. Nei dati V342 il limite archivio V322B `<=256` impedisce di chiamare crash-safe un replay di 10k decisioni.

**G3/G5 BLOCKED, PR #87 DRAFT non merged, NO OTA, legacy OTA172 e 12 cervelli V2.2 intatti.**

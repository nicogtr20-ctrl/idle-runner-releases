# ZERO 3.0 — V3.43 | Libertà di scelta, causalità energia, audit di disimpegno

**Preregistrazione prima di toccare sorgenti o eseguire nuovi benchmark. 18 ottobre 2026.** Obbligatori: [master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.42 10k](ZERO30_V342_TEN_THOUSAND_CONTIGUOUS_BOX_RESULTS.md), [UI](ZERO30_UI_UX_SPEC.md). Solo laboratorio PR #87 DRAFT, mai OTA, nessun cambio ai 12 slot 2.2.

## La distinzione imposta dall'utente

**ZERO può scegliere di NON muovere una cassa.** Non considerare un numero elevato di prese, trasporti o ritorni alla cassa come obiettivo intrinseco, e non etichettare automaticamente come regressione l'interruzione di tale condotta. Testare separatamente:
- **COMPETENZA**: fisica reale consente trasporto bilaterale? Già confermato da V3.42.
- **OPPORTUNITÀ/MOTIVAZIONE**: il mondo fornisce un valore ottenibile tramite l'interazione oppure è una BOX inutile? Che cosa può effettivamente percepire il cervello?
- **APPRENDIMENTO DI CONSEGUENZE**: dopo aver scoperto che un oggetto può liberare energia, con successiva necessità energetica, sceglie più spesso un'azione utile senza che alcun target, oggetto identificato, posizione, timer, velocità o premio per la sola presa venga fornito dall'esaminatore?

**Audit delle attuali capacità (stabilito prima del codice):** in `UnknownWorld.newHabitat` esiste già una rara energia legge4 +40 al medesimo spawn della BOX legge5 con `concealed=true`; `OpticalCues.coverOf` e `SquareGripPuzzle.UNCOVER_DISTANCE` rappresentano copertura e debole cue luminoso. **MA** `Zero3SensoryFrameV315.energyAvailable=false`, i raggi V315 contengono **soltanto distanza al solido senza colore/cue di energia**, V317 usa la sola `encode(pre)` nella V342 e non `before(pre,fuelRatio,previousDamage)`, la policy non riceve reali valori energetici. Inoltre `Zero3LearningFoundation.AdaptiveCortex.learnV333` aggiorna credito motorio relativo, non un goal-reward associativo per la gemma; V317 è un predittore di stato, non un attore con prova documentata di value learning. Quindi un benchmark con BOX utile vs inutile **NON È ANCORA INTERPRETABILE** come test della decisione informata: sarebbe più propaganda che scienza. NON inventare un goal o instradare hidden world truth ai neuroni.

## V3.43-A: infrastruttura fisica e contabilità falsificabile, nessuna azione suggerita

Un helper LAB `Zero3CoveredEnergyV343` lavora solo sulla vera WORLD+BOX, DOPO fisica V338 post-world:
1. Una sorgente legge4 +40 con `concealed=true` e coppia BOX legge5 di **stesso spawn** non deve essere visibile/collezionabile attraverso il solido. Non emettere una energia prima che il mondo mostri una dislocazione vera di `>=SquareGripPuzzle.UNCOVER_DISTANCE`.
2. Prima della rivelazione, il helper registra se BOX stessa ha dimostrato almeno un frame di `bilateralHeldNow` reale; un `box.x` manipolato arbitrariamente senza questa prova non deve dare credito a manipolazione. Dopo vera rimozione della copertura, rivelare *quella stessa* sorgente esistente e **una volta sola**. Non scrivere BOX.x/y/v, owner, timer, comando o cervello. Una volta scoperta l'energia rimane al proprio spawn, non segue BOX.
3. Con `Zero3SurvivalLedgerV323.physicalStep` e contatto materiale reale `MaterialGeometry.touches`, `UnknownWorld.Entity.touch` fornisce soltanto l'energia **effettivamente ricevuta**, massimo+40 e soggetta al tetto riserva100; nessun bonus per tocco alla BOX o per spostamento/adesione. Una sorgente consumata non può essere raccolta due volte. L'energia grezza non è input V315; il bridge conserva il fatto sperimentale ma **NON** dimostra apprendimento valore.
4. Fixtures controfattuali: BOX fermo, unico grip non opposto, movement teletrasportato senza proof, source già usato, source di BOX altra, BOX realmente spostata dopo grip verificato, pickup al contatto del corpo; nessun reward se non fisicamente toccato; prova negativa BOX senza premio. Contare "opportunity revealed", "consumption", e no leak della legge4 al controllo V317; no manipolazione artificiale nel test neurale.

## V3.43-B: audit dei vecchi episodi, SOLE SCENE CONSUMATE

Rieseguire V342 ordinal1601 **solo a fini di diagnosi** di disimpegno, non chiamarla nuova blind e non modificare model/policy/reward:
- FULL e FROZEN dall'identico brain V319 pretrained, stesso true Physics/Gel/Box, da n0 a n10000 salvo censura.
- Ogni 1000 vere decisioni: distanza centro fisico↔BOX, min/max della distanza, frame dentro una fascia geometrica di vicinanza <=BOX half+64, blocchi di avvicinamento/abbandono e rientro, vero grip/ownership/bilateral, spostamento BOX, RAW work, numero decisioni; non dedurre volontà o preferenza da semplice distanza.
- Nuova fisica **nessuna**, stesso indirizzo ordinal1601 già consumato. Confrontare hash/metriche V342 alla fine e vietare conclusioni se non replicano integralmente il run originale. Non deviare bracci o reward.

## Decisione V343 e futura V344

Una scelta informata BOX utile vs BOX inutile necessita **un sensore onesto di fame/energia, una debole cues via ottica fisica, e un meccanismo di apprendimento di valore dalle vere conseguenze**, distinti dal credito motorio. Nessuno di questi può esporre `physicalLaw`, `concealed`, `rewardAmount`, o una posizione goal/target al controller, né obbligare a manipolare. Prima di prove lunghe indipendenti: preregistrare nuove scene TRAIN/BLIND utili/inutili *pari per geometria* e controbilanciare la posizione delle reward, vita 10k con reale energia/reserve e criteri di sopravvivenza, resuming same-life pieno, e valutare scegliere/ignorare con costo-opportunità, non il conteggio raw di casse spostate.

**G3/G5 BLOCKED.** Una CI green valida solo la contabilità e il logging meccanico, non l'apprendimento. PR draft non merged, nessun APK/OTA, stable OTA172 e 12 vecchi brain invariati.

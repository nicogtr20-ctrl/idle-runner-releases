# ZERO 3.0 — V3.35 | Audit forense delle subroutine di collisione BOX

**Protocollo e diagnosi tracciabile — 17 ottobre 2026.** Leggere [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [Implementation Contract](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.33](ZERO30_V333_LOCAL_DELAY_BILATERAL_ABLATION.md) e [V3.34](ZERO30_V334_BIRTH_TO_CONTACT_CROSSING_AUDIT.md). Solo PR #87 DRAFT `feature/zero30-foundation-g0-g3-20261016`: nessun merge sul publisher, **NO OTA**, OTA172 e 12 cervelli 2.2 intatti.

## Motivo

V3.34 ha riprodotto gli autoincroci osservati in V3.33: TRAIN ordinal 337, primo substep **1**, edge 45/47; BLIND ordinal 645, primo substep **0**, edge 41/43. Membrana integra dopo warmup e `gel.step`, ma incrociata dopo `SquareGripPuzzle.constrain`, persino con 64 output motori a zero. Il `gel.invalid=0` non rileva la topologia introdotta successivamente dalla fase WORLD. Si tratta di **anomalia della fisica di contatto da isolare**, non prova di cattivo apprendimento motorio e nemmeno di causa neuronale.

## Misurazione V3.35 — senza cambiare la matematica fisica

Aggiungere un **observer nullo per impostazione predefinita** alla routine vera `SquareGripPuzzle.constrain` e `SquareCollisionSolver.constrain`; nessun passo extra nella produzione, nessuna nuova forza, reward, differenza di contatto, soglia di grip o mutation attraverso l'observer.

Ordine delle 11 fotografie READ-ONLY dopo le operazioni originali:

1. Import dei timer `boxGripHold` del solo oggetto.
2. CCD proiezione particelle membrana.
3. CCD proiezione delle 10 masse corpo.
4. Correzione dei link del contorno membrana a contatto con la scatola.
5. Correzione link tra masse corpo.
6. Fine CCD.
7. `resolveCrossingLinks` esterno.
8. `stabilizeExistingGrip` su contatto legalmente preesistente.
9. Rilevazione facce e `pushOut` membrana.
10. `pushOut` delle masse corporee + osservazione centro.
11. `SquareCollisionSolver.finish`.

Ogni foto registra `selfCrossings`, prima coppia di edge, link che attraversano il solido `SquareCollisionSolver.intersections`, grip legali (membrana e box), `bilateralHeldNow`, COM effettivo, ordinal/modalità/sottopasso. Mai presumere che un singolo link attraverso la scatola coincida con una vera autointersezione del contorno: sono due invarianti **distinti**.

## Controlli bloccati e criteri di validità

- Sei birth esatte V333/V334: `333,337,341,641,645,649`. Due sottopassi reali ciascuna, sia 64 muscoli a zero sia intent+64 azioni del recurrent actor V317 e head V319 congelata. 12 traiettorie; **non sono 12 seed indipendenti**.
- A ogni sottopasso la stessa azione vera viene applicata a due corpi generati identicamente: A chiama `actualWorldStep` originale, B chiama la stessa sequenza di WORLD con callback diagnostiche. Confronto bitwise su shell XY/velocity/grip/touch, massa corpo, 74 masse fluide, contatori fisici, stato e timer del BOX. Qualsiasi differenza = **test fallito**, non un nuovo risultato.
- Controllo C: stessi muscoli, `gel.step` senza WORLD, per escludere la sola origine muscolare. I quattro ordinal vicini non censurati devono restare intatti nei primi due sottopassi.
- Test CI: riesecuzione `SquareCollisionSolver.main`, `SquareGripPuzzle.main`, `MultiObjectGripRegression`, regressione appresa V319 (gate scientifico PASS invariato), Android `assembleDebug`. Nessun publisher APK.
- La CI tecnica positiva **non** basta a dimostrare il meccanismo causale geometrico, né una correzione. Annotare esattamente quale callback introduce per prima l'incrocio, le eventuali successive riparazioni e la reintroduzione. Non applicare una riparazione al solver prima di accertare il meccanismo e verificare solidità, adesione bilaterale, mondo multi-oggetto, costo e 2.000+ veri step.
- Le BLIND V332 e V333 restano consumate e sono usate qui esclusivamente per forensics, **mai** per ottimizzare politica/reward o vantare generalizzazione G3.

## Risultati V3.35 — **CI VERIFICATA** il 17 ottobre 2026

**[CI 39046324164 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39046324164)**, job `117435664950`, head SHA `aa22a87572d2b9367254dc4ff9b20490cf29a1df`. Tutte le 12 traiettorie a due sottopassi sono completate; **compilazione Java, stage observer contro solver originale con parità bitwise, controlli multi-oggetto, CCD, presa adesiva V319 e Android Debug BUILD SUCCESSFUL**. Non è stato prodotto né pubblicato un APK firmato.

### Primo cambio topologico osservato — due meccanismi differenti

| Scenario V333 riprodotto | Substep della prima decisione | Prima operazione che cambia crossing 0→1 | Coppia di edge | Attraversamenti solidi `SquareCollisionSolver.intersections` |
|---|---:|---|---|---:|
| TRAIN `ordinal 337` | **1** | **`SquareCollisionSolver.repairLinks` sulla membrana**, callback `CCD_AFTER_SHELL_LINKS` | 45↔47 | **0** |
| BLIND `ordinal 645` | **0** | **`SquareGripPuzzle` rilevamento contatto + `pushOut` membrana**, callback `AFTER_FACE_CONTACT_AND_PUSHOUT` | 41↔43 | **0** |

Nei due casi il risultato è **identico come classificazione anche con i comandi reali del recurrent actor V317/head V319**, e con tutti i 64 muscoli a zero. Nessun nodo ottiene presa durante i primi passi in esame: `skinHeld=0`, `boxHeld=0`, `boxBilateral=false`. L'operazione CCD di proiezione delle singole particelle non produce da sola il crossing del primo frame nel 337; qui è la successiva correzione dei **link**. Nel 645, al primo frame, né CCD particle, né link repair, né `resolveCrossingLinks`, né tether introducono l'intersezione: compare soltanto dopo il contatto/`pushOut` delle masse del contorno.

**Dettaglio temporale importante per 645:** al substep seguente, `gel.step` rimuove la topologia incrociata, ma la fase WORLD genera nuovamente la coppia 41↔43, stavolta nel `CCD_AFTER_SHELL_LINKS`. È quindi sbagliato attribuire l'intera storia di 645 a una singola routine o al cervello.

I quattro ordinal controllo `333/341/641/649` non introducono crossing nei due frame esaminati. Nel controllo senza WORLD, con gli stessi comandi, crossing=0; dopo la WORLD strumentata ogni stato osservato coincide **bit per bit** col solver originale, incluse coordinate e velocità del contorno, corpo, gel, contatti, box, timer e contatori fisici. Pertanto l'observer non ha corretto né provocato artificialmente il problema.

`throughWallLinks=0` nei frame incrociati è un fatto diagnostico cruciale: i metodi che impediscono alle corde di attraversare l'interno della BOX **non garantiscono affatto che due segmenti non adiacenti della membrana non si incrocino fra loro**. Sono due vincoli geometrici diversi. Nessuna prova indica che basti disattivare la collisione con la BOX: ciò violerebbe il requisito di solidità.

### Regres​sioni conservate con numeri verificati

- `SquareCollisionSolver.main`, `SquareGripPuzzle.main`, `MultiObjectGripRegression`: tutti PASS senza modificare soglie, comandi o vincoli, compresi isolamento delle tre casse e delle prese del cancello.
- V319 head adesiva originale, **eseguita per intero**: train 3.072 decisioni e 36.864 frame gel; blind 2.304 scelte e 27.648 frame gel per braccio, `scientificGripGate=PASS`, `boxGripNew=6900`, `boxLivesNew=6`, `outsideDutyNew=0.03085214`, lavoro BOX `0.200176236` vs vecchio `0.255425971`. L'instrumentation della V3.35 **non modifica il risultato già dimostrato della V3.19**.
- Android Debug: **BUILD SUCCESSFUL in 1m 2s**. Questo NON è un test sul Galaxy S21, né una prova di persistenza in processo Android reale.

### Decisione tecnica e prossimo gate

- `ROOT_SUBSTAGE_IDENTIFIED=PASS`: localizzati due differenti punti fisici dell'introduzione della self-intersection, non solo il metodo genitore `SquareGripPuzzle.constrain`.
- `ROOT_INNER_INDEX_AND_SAFE_FIX=OPEN`: non è ancora determinato l'esatto link corretto all'interno dei pass/indici di `repairLinks` e l'esatto punto/face `pushOut` che genera il crossing; una correzione del solver senza queste verifiche e senza limiti di energia, assenza di attraversamento solido, prese bilaterali e 2.000+ sottopassi è **non giustificata**. V3.35 **non applica fix né cambia il solver attivo**: gli overload strumentati passano `null` in ogni chiamata ordinaria.
- **Prossima fase V3.36:** audit per indice, prima/dopo ogni correzione vera dei link della membrana e prima/dopo il `pushOut` di ogni patch; poi proporre la minima correzione topologica conservativa usando soltanto stati fisici già noti, con test su forme indipendenti, prevenzione della compenetrazione, costo, 2.000 frame continuativi, V319/multi-oggetto e nuovi holdout preregistrati. Se la fisica o il grip peggiorano, non promuovere la correzione.
- **Nessun nuovo risultato G3** deducibile dalla sola risoluzione di un bug: restano locomozione positiva BOX, esperienza long-run trained/frozen, confidenza multi-seed, G5 world model, sensor integration, neurogenesi, UI e Android S21.

**PR #87 ancora DRAFT, G3/G5 BLOCCATI, NO MERGE e NO OTA; stabile OTA172 e i 12 salvataggi 2.2 restano intatti.**

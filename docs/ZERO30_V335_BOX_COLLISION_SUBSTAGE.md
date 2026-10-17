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

## Stato risultati

Prove avviate tramite [workflow V3.35](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero30-v335-box-substage-qa.yml), nel solo branch DRAFT. Si devono aggiungere qui i numeri **verificati** dai log conclusivi, distinguendo `ROOT_SUBSTAGE_IDENTIFIED` e `CONSERVATIVE_PHYSICS_FIX`, senza trasformare la compilazione in un gate scientifico superato.

**G3 locomozione generale BLOCCATO; G5 world-model BLOCCATO; nessun test Galaxy S21, nessun OTA, UI finale non attestata.**

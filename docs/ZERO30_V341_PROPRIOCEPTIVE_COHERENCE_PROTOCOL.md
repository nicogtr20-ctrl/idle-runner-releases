# ZERO 3.0 — V3.41 | Coerenza propriocettiva locale, un solo intervento, gate energie RAW

**17 ottobre 2026, preregistrazione effettuata PRIMA di cambiare il motore e PRIMA di usare nuove nascite.** Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.39](ZERO30_V339_AUTONOMOUS_GAIT_RESULTS.md) e [V3.40](ZERO30_V340_BILATERAL_CAUSAL_RESULTS.md). PR #87 DRAFT, `feature/zero30-foundation-g0-g3-20261016`; nessun merge/OTA, OTA172 e 12 legacy brain invariati.

## Problema e ipotesi falsificabile

Il FULL V339 apprende meglio locomozione intenzionale nelle famiglie FREE/DISK_NEAR/BOX_FAR ma spreca >10% RAW work in FREE e BOX_FAR, e in BOX_NEAR ha 0/6912 frame con due facce opposte geometricamente raggiunte (vs OLD3858/FROZEN3584). I timer e l'ownership non possono creare un secondo lato se il corpo non vi arriva. L'evidenza locale nel V333 misura sector motion relativo al centro, ma **non** penalizza esplicitamente differenze di velocità sproporzionate fra settori adiacenti; l'ipotesi V341 è che uno stimolo apprenditivo basato su **coerenza intersettoriale misurata** riduca movimenti contraddittori senza imporre pose, gait, reward BOX o grip.

### UNICO intervento V341

Conservare **identici** V317 actor, pretrained frozen V319 grip head, 16×4 efferenze, corpo a 96 punti, `SymbioteMatterV1`, `SquareGripPuzzle`, V338 late joint solver, learning rate .013, delay .82, credito V333, tempi, decisioni e reward di intenzione. **Solo** per il quarto braccio `LOCAL_COHERENCE`, sottrarre dall'evidenza del settore corrente, DOPO ogni vero substep di fisica, una penalità di incoerenza meccanica fra i vettori di movimento delle sue 6 patch e i movimenti dei due settori geometricamente adiacenti della stessa membrana. Definire `R_s = average_{6 patches}(postSkin_i - preSkin_i - displacementCOM)` per vettore 2D; `P_s=clamp(hypot(R_s - (R_{s-1}+R_{s+1})/2) / (4 + hypot(R_s)),0,1)`; `localEvidence_s -= 0.12 * P_s` ogni frame, esclusivamente per il braccio nuovo in TRAIN. Nessuna dipendenza da posizione BOX, nome oggetto, direzione della gemma, timer adesione, ora del futuro o coordinate globali; è feedback negativo su discontinuità di velocità della *propria* membrana. L'algoritmo deve vedere soltanto osservazioni post-real-world e non alterare fisica né comandi direttamente. Registrare penali medie/somme, per provare il braccio veramente diverso. Il parametro **0.12 è congelato**; anche un FAIL non autorizza tuning sulla BLIND.

### 4 bracci realmente indipendenti e nuove nascite

`OLD_GLOBAL`, `FULL_LOCAL_DELAY` V339 immutato, `FROZEN`, `LOCAL_COHERENCE` V341. Quattro cervelli con propri skin/gel/world/sensori PRE/POST/stato V317/64 comandi e learning (bloccato BLIND); vero V319 pretrain congelato comune. Non copiare motori e non imporre coppie di adesione. Congelare nuove nascite prima della CI:

- TRAIN 32 ordinal **1240..1271** (8 per ciascuna famiglia FREE, BOX_NEAR, DISK_NEAR, BOX_FAR) × 96 decisioni × 12 real substep = 3072 decisioni e 36864 world ticks **per braccio**. Registrare censura, mai interpolare.
- BLIND 24 ordinal **1320..1343** (6 per famiglia), × 96 decisioni ×12 substep = **2304 decisioni/arm, 27648 passi fisici/arm** se nessuna censura. **Eseguire TUTTE le 24 scene nell'ordine originale per ogni braccio**: la corteccia slow-state è condivisa fra vite. Holdout 900..983 V339, 1240..1271 TRAIN e 1320..1343 BLIND non possono essere riusati come nuove prove dopo questa CI.

### Gate NON NEGOZIABILI (prima del test)

1. True physics ogni substep su ogni vita: 0 self-cross/through-BOX/embedded/gel.invalid, 0 censura, >0 iniziative reali 64-motor per tutti, 96×24 decisioni indipendenti per arm e zero plasticità nel BLIND.
2. Gait: `LOCAL_COHERENCE` deve superare FULL, OLD e FROZEN come signed-efficiency nelle prove nuove in **>=3/4 famiglie** con margine >=10% rispetto al miglior riferimento (stessa metrica prereg V339). BOX_NEAR signed positivo **e** efficienza migliore di tutti e tre. Non dichiarare locomozione se corpo resta fermo o naviga solo spostamento apparente.
3. **Energia grezza prima della metrica normalizzata:** per ognuna delle 4 famiglie, il lavoro gel RAW di COHERENCE deve essere <= 110% del lavoro grezzo di **OLD, FROZEN e FULL** singolarmente (+1e-6). Distinguere `rawWorkGate` da `workPerUseful` nelle righe CI. Non fare passare normalized-cost come raw energy.
4. BOX_NEAR: vera BOX `bilateralHeldNow>0` nella COHERENCE e >=80% della migliore OLD/FROZEN/FULL; 6 vite con true BOX owned grip e durata frame >=80% di migliori riferimenti; **contatto geometrico simultaneo TOP/BOTTOM o LEFT/RIGHT** realmente misurato, NO stage falso. Se i reference zero, gate bilateral NOT_TESTED e G3 BLOCKED.
5. **Persistenza**: checkpoint cortex a boundary test byte/bitwise, ma non dichiarare whole body save/restart testato se actor/gel/BOX/RNG non serializzati. Per full G3 serve un altro test di 2000 decisioni nello stesso organismo, con checkpoint reale dell'intero mondo e confronto restart. Le 2304 BLIND sono sparse su 24 vite, non equivalgono a 2000 nella stessa.
6. Anche con screening positivo, **non** abilitare subito G3 o Android: servono completezza full organism/continuous life, mondo indipendente e V3 UI del master; G5 world model separato.

## Controlli e falsificazione

La CI deve stampare quattro serie di metriche per famiglia, `RAW_WORK`, `NORMALIZED_COST`, `direction`, `bilateral`, `geometry`, `censor`, `jointAttempt/Repair/Unresolved`, `learnedUpdates`; deve dichiarare `G3=BLOCKED_FAIL` se un veto non è soddisfatto. Le vecchie regressioni di 3 BOX/gate, V319 original learned grip e `gradle assembleDebug` devono restare verdi; workflow SUCCESS tecnico non è promozione scientifica. **Mantenere OTA172 e i 12 slot del 2.2.**

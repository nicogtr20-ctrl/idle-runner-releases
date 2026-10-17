# ZERO 3.0 — V3.36 | Causa puntuale e prova di protezione topologica BOX

**17 ottobre 2026 — Diagnostica e candidate fisiche sperimentali**. Leggere prima: [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.33](ZERO30_V333_LOCAL_DELAY_BILATERAL_ABLATION.md), [V3.34](ZERO30_V334_BIRTH_TO_CONTACT_CROSSING_AUDIT.md), [V3.35](ZERO30_V335_BOX_COLLISION_SUBSTAGE.md), [UI 3.0](ZERO30_UI_UX_SPEC.md). PR [#87](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87) Draft, ramo isolato `feature/zero30-foundation-g0-g3-20261016`. **Non unire nel ramo OTA, non modificare i 12 cervelli 2.2**.

## Causa precisa per indice verificata — NO nuovi holdout

[CI diagnostica V3.36 39047257292 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39047257292) ha aggiunto callback per **ogni vero link corretto** e per **ogni punto proiettato dopo `pushOut`**, con controllo bitwise contro il solver originale. TUTTI i motori a zero vs uscite vere del V317/V319 producono la stessa classificazione del difetto. Le scene usate erano già consumate dalla V3.33: **non sono BLIND nuove**.

| Nascita forense | Primo frame difettoso | Operazione precisa | Primo elemento mosso | Self-crossing |
|---|---:|---|---|---|
| TRAIN ordinal337 | sottopasso 1 | `SquareCollisionSolver.repairLinks`, pass 0 | link **45 → 46** | edge 45↔47 |
| BLIND ordinal645 | sottopasso 0 | `SquareGripPuzzle.pushOut` | patch **42** | edge 41↔43 |
| BLIND ordinal645 | sottopasso 1 | `SquareCollisionSolver.repairLinks`, pass 0 | link **41 → 42** | edge 41↔43 |

Nell'ordinal645 lo stesso punto 42 **prima** del `pushOut` ha XY `(27.28033,7.3555765)` con box `(44.643272,-12.6444235)`: il bordo inferiore della BOX è esattamente `7.3555765` — contatto geometricamente **già lecito**. La proiezione aggiuntiva lo porta inutilmente a `y=8.505576`, determinando il primo self-crossing. `SquareCollisionSolver.POINT_CLEARANCE=0` riconosce già il contatto esatto come lecito: la post-proiezione con +1.15 era incoerente sul bordo. L'incrocio post-WORLD non è intercettato da `gel.invalid`, e `throughWallLinks=0` non implica `selfCrossings=0`.

**Nessuna modifica alla rete, reward, training, corpo, attriti, APK o salvataggi** per effettuare queste prove.

## Controprove fisiche locali — NON ATTIVATE NEL GIOCO

Per distinguere ogni soluzione fisica da effetti del cervello sono presenti due parametri **solo laboratorio**, chiamabili esclusivamente tramite overload espliciti di `SquareGripPuzzle.constrain`. Tutti gli overload usati da produzione e GameActivity mantengono `false`.

1. **Face-only:** preserva il contatto del punto di membrana già esattamente sul bordo del solido, evitando espulsione gratuita. Nelle 6 nascite×2 frame=12 prove a 64 muscoli zero, riduce il primo crossing del caso 645 ma lascia **2 frame incrociati** (337 e 645 successivo); **REJECTED**. Nessuna compenetrazione osservata.
2. **Face + conservative-link alternative:** solo quando la correzione di un link crea uno self-crossing prima inesistente, un esperimento `Zero3ContactTopologyExperimentV336` prova le quattro facce di supporto e sceglie esclusivamente una proiezione locale con movimento singola patch <=12 unità, membrana globale semplice, nessun bordo/patch nel solido e nessuna rottura volontaria di hold legale. Se non esiste alternativa verificabile, ripiega sul solver originale senza nascondere il fallimento. Non imposta target, gait, reward o spinta di controllo. **Nelle 12 prove brevi** elimina tutti i 3 frame problematici dei due ordinal e lascia regolari i 4 controlli; `worsenedFrames=0`, `unresolvedFrames=0`, `penetration=0`, `controlRegressions=0`, gate = `SHORT_SCREEN_ONLY_NOT_PROMOTION`. [CI 39047804342 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39047804342).

**ATTENZIONE:** questi sono esperimenti su condizioni V333 già viste e due soli frame per corpo, **non una dimostrazione che la modifica sia robusta nel tempo**. La selezione di una faccia alternativa potrebbe alterare forze fisiche, durata di presa, lavoro o continuità su deformazioni diverse. L'attivazione resta quindi vietata.

## Protocollo preregistrato per stress muscolare attivo — prima del codice

Perché i 5.024 frame zero-motor possono confermare solamente la stabilità a muscoli fermi, V3.36 richiede anche un esperimento **ACTUATED** usando vere azioni del recurrent actor V317, cortex 64 comandi e head adesiva V319 **congelata e preaddestrata**, nessuna policy/gait prescritti. Stesse morfologie storiche 337 e 645 (forensics, NON nuovo holdout). Due coppie baseline/candidato: per ogni decisione il cervello osserva solo il PRE originale; tutte le 64 efferenze realmente generate vengono copiate immutate ad entrambe le fisiche, quindi il confronto isola il solver ma **la candidata non sceglie autonomamente la propria azione**. 168 decisioni complete da 12 solver step = 2.016 frame per organismo e per braccio (totale 4.032 frame/arm), più 96 warmup non conteggiati.

Metriche da registrare per braccio e nascita: completamento, autoincroci dopo WORLD, link attraverso BOX, patch incorporate, `gel.invalid`, `gel.work` autentico, frame di contatto/hold con box proprietaria, frame bilaterali autentici, intenzione scelta ed efferenze realmente eseguite. `screenPhysical` richiede 0 crossing/0 attraversamento/0 invalid per candidata; `screenWork` <=110% del controllo stesso ordine (se controllo lavoro=0, qualsiasi aumento >1e-6 fallisce); `screenBilateral` richiede >=80% frame bilaterali baseline **soltanto se il baseline ne mostra davvero**, altrimenti `NOT_TESTED`. Il gate completo resta negativo se mancano prese bilaterali e nuove scene, persino se le condizioni tecniche passano. Mai sostituire grip generico a bilaterale, e non chiamarlo miglioramento G3.

## Stress a 2.000 frame e guardrail

Aggiunta `Zero3ContactLongStressV336Regression.java`, [workflow dedicato](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero30-v336-long-stress-qa.yml): usa la vera integrazione `SymbioteMatterV1`, 96 shell + 74 masse fluide, `SquareGripPuzzle`, WORLD e gel per **2×2.000 frame consecutivi** sui casi storici 337/645, più 4×256 sui controlli = **5.024 frame fisici**. Confronta baseline contro candidato a muscoli zero e misura ad ogni frame self-crossing, corde attraverso BOX, particelle incorporate, `gel.invalid`, lavoro e grip. **Non sono 2.000 decisioni autonome né 2.000 esperienze apprese**.

La CI esegue anche il vero test di presa V3.19, `MultiObjectGripRegression`, CCD e Android `assembleDebug` senza firma/OTA. La V319 esegue il solver normale (non l'overload sperimentale), perciò conferma la **non regressione dei default**, **non** l'adeguatezza delle prese dopo l'eventuale attivazione del candidato. Questo rimane un blocco necessario prima del merge.

## Risultato stress fisico — CI conclusiva verificata

**[GitHub Actions 39048002311 — SUCCESS tecnico e SCREEN fisico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39048002311)** (commit `87469dc4c558c1af728510e1d2719f36a5325963`).

| Ordinal | Frame fisici del candidato | Crossing baseline | Crossing candidato | Corde nel solido / patch incorporate / gel invalid |
|---|---:|---:|---:|---:|
| 333 | 256 | 0 | 0 | 0 / 0 / 0 |
| 337 | **2.000** | **1** | **0** | 0 / 0 / 0 |
| 341 | 256 | 0 | 0 | 0 / 0 / 0 |
| 641 | 256 | 0 | 0 | 0 / 0 / 0 |
| 645 | **2.000** | **2** | **0** | 0 / 0 / 0 |
| 649 | 256 | 0 | 0 | 0 / 0 / 0 |

Totale **5.024 frame reali** per braccio, due corpi storici con 2.000 integrazioni continue ciascuno, candidati `failingCases=0`, `totalViolations=0`; controllo originale 3 frame con self-crossing. Tutti i frame avevano **64 efferenze ZERO**, dunque lavoro muscolare totale `0.000000000`, `gripFrames=0` e `bilateralFrames=0`: il risultato è un **gate di consistenza fisica statica prolungata**, NON una prova di lavoro attivo, adesione, apprendimento o generalizzazione.

Nella stessa CI: `SquareCollisionSolver`, `SquareGripPuzzle`, tre oggetti + gate, prova V319 originale (`scientificGripGate=PASS`, 6.900 grip autentici, 6/6 vite BOX, lavoro originale 0.200176236 vs 0.255425971 incumbent) e Android Debug `BUILD SUCCESSFUL in 1m 4s`. La V319 usa il solver originale: la candidata è ancora da verificare **SOTTO CARICO NEURALE**. Nessuna APK firmata, nessuna OTA.

## PRIMO TEST MUSCOLARE ATTIVO: SCIENTIFIC FAIL, NON ATTIVARE

**[CI 39048377502 — SUCCESS tecnico, FALLIMENTO salvaguardia presa bilaterale](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39048377502)**. I veri moduli V317/V319 hanno emesso 64 comandi per frame su 168 decisioni ×12 passi ×2 casi (2.016 frame per caso e braccio), senza aggiornare i pesi: attuatore reale, stesso identico nastro di comandi su baseline e candidato sperimentale.

| Caso già noto V333 | Azione L1 vera | Crossing BASE→CAND | Work reale BASE→CAND | Grip generico BASE→CAND | Frame BILATERALI BASE→CAND |
|---|---:|---:|---:|---:|---:|
| ordinal 337 | 11.327,8444 | 1 → 0 | 0,054355998 → 0,054355998 | 2.014 → 2.014 | 0 → 0 (non testato) |
| ordinal 645 | 12.593,9508 | 2 → 0 | 0,058657911 → 0,058657911 | 2.014 → 2.014 | **1.192 → 0** |

Quindi `topology=PASS`, `workGate=PASS`, **`bilateralGate=FAIL`** sul caso 645. È gravissimo: **nessuna correzione topologica può essere promossa sacrificando completamente la manipolazione bilaterale**, anche se conserva tutti i frame di adesione generica. La parità del lavoro del gel riflette efferenze identiche ed è insufficiente a certificare assenza di effetti su trazione/BOX; la locomozione G3 NON è stata validata. **La combinazione di face+alternative links è REJECTED per attivazione**, indipendentemente dal positivo zero-motor. Non ritoccare retroattivamente V333 come holdout e non ignorare la regressione.

La V3.36 esegue un'ulteriore *ablazione diagnostica meccanica* per separare BASE, solo contatto faccia e faccia+alternative links, alle medesime decisioni già note. Questo non crea nuova evidenza BLIND: serve soltanto a capire quale interazione modifica le prese.

## Blocchi e prossimi passi

1. Leggere il risultato finale dello stress, riportando tutti gli errori senza rimuovere semi/pose fallite.
2. Se stress passa, provare davvero **l'overload candidato** sul benchmark learned-head V319 e su 3 casse+gate, misurando bilaterale, grip selettivo, penetrazione, lavoro e ordine di elaborazione.
3. Generare **nuove** morfologie/scene/seed preregistrati e non consumati da V332/V333; confrontare trained/FROZEN/OLD per >=2.000 decisioni reali con save/restart, locomozione positiva BOX e costo massimo preregistrato.
4. Solo dopo i gate fisici e scientifici, procedere con persistenza device, G5 modello del mondo, skill memory, neurogenesi e UI definitiva; nessun OTA 3.0 prima della chiusura delle specifiche.

**Stato al momento della stesura:** sub-index `ROOT_CAUSE_TRACE=PASS`; candidate breve `LAB_SHORT_SCREEN_PASS` ma **`SAFE_PHYSICS_PROMOTION=NOT_AUTHORIZED`, `G3=BLOCKED`, `G5=BLOCKED`**, PR #87 resta Draft, OTA172 e 12 slot 2.2 invariati.

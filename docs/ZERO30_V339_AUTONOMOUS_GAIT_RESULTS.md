# ZERO 3.0 — V3.39 | Risultati verificati: locomozione 3/4, bilaterale BOX FAIL

**17 ottobre 2026 — Esperimento sperimentale concluso.** Leggere [protocollo preregistrato, conservato separatamente](ZERO30_V339_AUTONOMOUS_GAIT_ABLATION.md), [master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [meccanica V338](ZERO30_V338_JOINT_CONSTRAINT_PROTOCOL.md). Ramo lab `feature/zero30-foundation-g0-g3-20261016`, Draft PR #87, G3/G5 BLOCCATI, NESSUN OTA. Evidenza reale [GitHub Actions **39055246094** (SUCCESS tecnico)](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39055246094); commit `a4bcd6b5c09df8c98e664490f9657234e13d471e`.

## Che cosa è stato effettivamente eseguito

- Tre organismi con **mondo, gel, membrana, corteccia, attore ricorrente V317, input sensoriali PRE e POST, aderente V319, 64 comandi motori e stato di apprendimento INDIPENDENTI**: `OLD_GLOBAL`, `FULL_LOCAL_DELAY`, `FROZEN`. Non si copiano output o trajectory BASE: confronto con **closed loop vero**. L'adesione V319 era pretrained su **3072 decisioni reali** e poi congelata per tutti, per isolare la differenza del credito motorio.
- 32 TRAIN nuove `900..931`, **3072 decisioni per braccio**, 36.864 veri step fisici per braccio; totale 9216 decisioni. Pesi FULL aggiornati realmente **2272 volte**, credito V333 full locale-temporale **3040 chiamate**, OLD **3072 updates**, FROZEN **0**.
- 24 BLIND nuove `960..983` (6 per FREE/BOX_NEAR/DISK_NEAR/BOX_FAR), **2304 decisioni e 27648 substep fisici PER braccio**; tutte le vite complete, 4/4 famiglie con 576 decisioni per arm, **0 censor, 0 self-cross finale, 0 attraversamento BOX, 0 gel.invalid**, e nessun aggiornamento di sinapsi BLIND. Le scene 900..931 e 960..983 sono **ora consumate** e NON possono venire ottimizzate e ritestate come nuovo holdout.
- `Zero3JointWorldV339` replica veri BOX, interior gel, vincoli e gel.observeBody, con l'overload selettivo V337 e **`Zero3JointBoxConstraintV338` alla fine del WORLD**; 13 operazioni joint tentate, 13 riuscite, 0 irrisolte complessivamente.
- Verificato checkpoint + restore reale dello stato **della sola corteccia** al boundary della vita 915, con pesi bitwise uguali e stesso revision/updates: OLD 1536, FULL 1136, FROZEN 0. Questo **non equivale** a una prova di ripartenza della vita fisica interrotta (world/gel/actor/head/RNG e journal), ancora richiesta dal master V328/V329 e da V340.
- Regressioni originali `MultiObjectGripRegression` 3 BOX+gate e `SquareCollisionSolver`, `SquareGripPuzzle` PASS; Android `assembleDebug BUILD SUCCESSFUL in 1m 3s` ma **NESSUN APK distribuito o testato su S21**.

## Dati originali BLIND, senza nascondere il risultato negativo

La metrica `signed` è lo spostamento realmente misurato del centro fisico proiettato sulla direzione decisa dall'attore, **NON** la distanza verso una gemma e **NON** una prova di locomozione universalmente funzionale. 6 vite×96 decisioni e 6912 veri frame per cella.

| Famiglia | signed OLD | signed FROZEN | signed FULL | efficienza OLD | efficienza FROZEN | efficienza FULL | work OLD | work FROZEN | work FULL |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| FREE | −18.5664 | −2.1672 | **+98.3065** | −0.299438 | −0.050502 | **0.696813** | 0.109034 | 0.107229 | **0.138405** |
| BOX_NEAR | +1.5969 | **+3.2090** | +1.3754 | 0.019264 | **0.047936** | 0.014794 | 0.214004 | 0.209228 | 0.212309 |
| DISK_NEAR | −19.1919 | −1.8280 | **+108.9978** | −0.205932 | −0.024903 | **0.713550** | 0.136777 | 0.134729 | **0.148168** |
| BOX_FAR | −21.9152 | −3.4494 | **+114.0526** | −0.287432 | −0.063298 | **0.723682** | 0.110888 | 0.108723 | **0.142859** |

### FALLIMENTO del gate di manipolazione, nonostante vera locomozione relativa

BOX_NEAR, 6912 frame fisici per braccio, contatto692? (vedi log), `heldFrames=6900` per tutti i tre, `contacts=6912` e 6/6 vite con presa almeno una volta per ognuno. **Bilaterale autentico**: `FULL=0`, `OLD=1546`, `FROZEN=1384`. FULL non è migliore: `signed=+1.3754` contro OLD+1.5969/FROZEN+3.2090, `efficiency=0.014794` contro +0.019264/+0.047936. Le prese *generiche* persistono ma le prese di lati opposti spariscono sul candidato: **il cervello local-delay non conserva la manipolazione bilaterale in autonomia**. Questo non è un difetto dimostrato nel solver V338: nella V338D con *nastro BASE condiviso*, le prese 340/718/8 erano conservate. È invece un limite osservato dall'interazione fra azioni neurali indipendenti, deformazioni e contatto reale: la causa specifica della perdita è ancora da isolare.

### Erratum scientifico sui costi: controllo grezzo più severo del codice V339

Il log `workWithin10pct=true` per FREE, DISK_NEAR e BOX_FAR è ottenuto dalla funzione **normalizzata** V333 `EpisodeStats.cost() = work / (1 + max(0, signed))`, NON dalla soglia di `work` grezzo congelata nel [protocollo](ZERO30_V339_AUTONOMOUS_GAIT_ABLATION.md). Non presentarlo come prova di consumo energetico non peggiorato. La lettura conservativa del vero lavoro relativo FULL vs OLD/FROZEN è:

| Famiglia | FULL work vs OLD | FULL work vs FROZEN | Soglia grezza entro +10% di entrambi |
|---|---:|---:|---|
| FREE | **+26,94%** | **+29,07%** | **FAIL** |
| BOX_NEAR | −0,79% | +1,47% | PASS |
| DISK_NEAR | +8,33% | +9,98% | PASS (limite stretto) |
| BOX_FAR | **+28,83%** | **+31,40%** | **FAIL** |

Questa incongruenza di reporting NON può convertire la conclusione G3 negativa in positiva: con la soglia lavoro grezzo, il FULL supera il requisito direzionale **e** il requisito energetico in **una sola famiglia (DISK_NEAR)**, invece delle 3/4 registrate dal codice usando lavoro utile normalizzato. **La decisione corretta è G3 FAIL**, sia per manipolazione sia per costo, senza fare nuovi tuning sui BLIND consumati. Correggere il reporting raw-vs-normalized *prima* del nuovo trial preregistrato V340, senza modificare retroattivamente i dati V339.

## Esito/limiti e prossimo passo progettuale

`V339 SCIENCE_FINAL G3=BLOCKED_FAIL completedFamilies=4/4 familyWins=3/4 (NORMALIZED) BOX_positive=true BOX_betterThanOLDandFROZEN=false bilateralPreserved=false gripPreserved=true, jointAttempts=13, jointRepaired=13, jointUnresolved=0.`

**V340 necessario prima di G3:**
1. Audit **causale non interventista** usando solo gli ordinal V339 ormai CONSUMATI: confronto primo tempo/face-owner/timer/gripCommand nel BOX_NEAR fra FULL/OLD/FROZEN, correlazione con estensione della membrana, drag, propagazione trazione, local eligibility e possibile conflitto dei 64 comandi. Non impostare artificialmente due grip.
2. Prima di nuove sperimentazioni registrare scenari multi-famiglia completamente nuovi, split TRAIN/BLIND, criterio di bilaterale autentico e **lavoro grezzo <=110%** e costo normalizzato registrati entrambi, ma con veti indipendenti.
3. Verificare vero save/restart con persistenza di gel/world/actor/head/cortex/RNG e checkpoint fisico, non soltanto bitwise weights; almeno 2000 decisioni nella stessa vita con memoria e durata realmente conservate.
4. Solo una futura validazione indipendente 4/4 fisica senza censura + locomozione + presa vera + energia entro limiti può autorizzare integrazione del runtime 3.0, nuovi menu e successivamente device S21; G5 world model resta separato/bloccato.

**PR #87 DRAFT, G3/G5 BLOCCATI, NO merge, NO OTA, ZERO 2.2 (OTA172 e 12 slot) invariato.**

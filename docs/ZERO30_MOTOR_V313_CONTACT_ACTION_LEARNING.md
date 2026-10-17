# ZERO 3.0 — V3.13 | Apprendimento della dinamica con contatto reale
**Protocollo preregistrato il 17 ottobre 2026 prima di scrivere il test / leggere risultati.**
Documenti obbligatori: [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.11](ZERO30_MOTOR_V311_BODY_FRAME_DYNAMICS.md), [V3.12](ZERO30_MOTOR_V312_REAL_CONTACT_FIXTURE.md). Il test è **E2, isolato**, sul branch della PR #87: non integra il cervello dell'app, non altera produzione o slot.

## Motivazione e ipotesi

- V3.11: predizione molto migliore sulla fisica libera, ma touch/hold = ZERO in tutta l'esperienza; gate scientifico contatto = FAIL.
- V3.12: vera collisione e presa fisica su `SquareGripPuzzle`; tocco scatola 44/48 casi, presa 21/48, disco `touchPulse` 13/16. Distinzione `touchSense` (solidi), `touchPulse` (dischi), `gripHold` (presa posseduta).
- V3.13 verifica che un predittore *stato corporeo/efferenza → esito fisico un frame dopo* generalizzi **su scene con oggetti**, non soltanto nella fisica libera. Il predittore non vede ID fisici nascosti, futuri stati, posizione privilegiata dell'oggetto, ricompensa o gemme. Il modello **non sceglie azioni**: tape noto, non world-model G5 operativo.

## Modello congelato

Riutilizza V3.11 senza tuning: ridge `λ=8`, normalizzazione **solo FIT**, backbone 22 feature in coordinate anatomiche + **3 nuove** (media e primo armonico di `touchPulse`) = 25 feature, 64 comandi effettivamente eseguiti aggregati a 24 statistiche di efferenza come V3.11. Predizione residuale rispetto alla velocità, in coordinate corporee. Due regressioni di ablation identiche, stesso FIT e ridge: **NO_ACTION** (senza efferenza) e **NO_TOUCH** (senza `touchSense`, `gripHold`, armonici del contatto e `touchPulse`, né interazione azioni × contatto). Persistenza corporea, zero-motion e velocità costante sono baseline separate.

## Corpus e protocollo, split per traiettoria

Fisica **di produzione non modificata**, corpo+gel reali, 64 comandi/frame, durata 96 warm-up +144 frame osservati, dt 0,025s; reale `SquareGripPuzzle.constrain`, `MaterialContactConstraints.reconcile`, `SymbioteWorldStepV171.interior`, `GripSurfaceComposition.compose`; reale `Morphology.collide` per dischi. Le prove di laboratorio forzano intenzionalmente il **canale motore adesione** ON o OFF (mai `gripHold` e mai `touchSense`), perturbazioni muscle deterministic con seed; **non** sono esperienze autonome. Scene FREE (senza oggetto), BOX (due distanze), DISK (due distanze). Oggetto generato dopo warm-up rispetto al COM iniziale, corpo mai spostato verso di esso.

- FIT: pose 0° e 90°, condizioni 0/1/2, due seed, adesione ON/OFF, cinque scene (FREE 1, BOX 2, DISK 2) = **120 traiettorie**.
- CAL: pose −31° e +38°, condizioni 0/1/2, due seed nuovi, ON/OFF, 5 scene = **120 traiettorie**.
- BLIND: pose +19°, −56°, +67°, −81°, condizione 3, due seed distinti e nuovi, ON/OFF, 5 scene = **80 traiettorie**.
- Distanze (rispetto al COM del singolo corpo, al momento di comparsa): FIT scatole 46/50 e dischi 46/50; CAL scatole 48/53 e dischi 47/52; BLIND scatole 45/51 e dischi 44/49. Scene e seed di perturbazione distinti dai 96 casi V3.12, senza riuso di sue traiettorie intere, ma si usa **lo stesso mondo fisico con geometrie diverse**, non una nuova classe di legge fisica.
- Totale **320 traiettorie × 240 step = 76.800 step solver**; 46.080 frame post warm-up. Training FIT campionato deterministic 1/2 frame, unità indivisibile nello split. CAL e BLIND completi, nessun tuning dopo CAL o BLIND; considerare il BLIND consumato dopo la run.
- Per ogni istante: stato `s_t` prima del comando, 64 comandi `a_t` realmente applicati, stato post-collisione `s_(t+1)`, COM `dx,dy`. Verificare che i sensori siano conseguenze del vero mondo, inclusa origine di `touchPulse`; vietato assegnarli.

## Valutazione congelata

Misurare MSE XY one-step e MSE stato, separando:
- **CONTATTO OSSERVABILE**: `touchSense` o `touchPulse` o `gripHold` già positivi nello stato **pre-step**; non leggere il post-step per definire input. I primi eventi di collisione sono separatamente diagnosi `contactOnset`, impossibile pretenderne la previsione con sole feature corpo senza vista dell'oggetto.
- **SENZA CONTATTO**: gli altri frame, con split per scene FREE/BOX/DISK.
- **Rilascio/aderenza**: quante prove/frame/hold realmente presenti nel singolo split.
- **Rollout 16, 48, 144 frame**: stato/asse iniziali + state prediction ripetuta, action-tape futuro registrato, ma nessun futuro fisico o eventi di contatto del mondo; confronto con migliore tra COM fermo e inerzia iniziale. La geometria dell'oggetto NON è un input al rollout, quindi deriva su contatti futuri sconosciuti va dichiarata e non mascherata.

**Gate scientifico E2**: sia su CAL sia su BLIND, almeno 10 traiettorie con contatto vero e 50 frame osservati pre-step con vero contatto; MSE XY **in contact subset** FULL ≤90% ballistica, ≤97% NO_ACTION, ≤97% NO_TOUCH; MSE XY **complessiva** ≤90% ballistica; MSE stato complessivo ≤90% persistenza; rollout 16 e 48 frame ≤90% della migliore baseline. Una sola condizione mancante = **FAIL** anche se CI SUCCESS. Se calcolo contatti <50 o uno split non ha grip fisici, registrare precisamente: la validazione di presa rimane incompleta indipendentemente dal resto. Se un modello fallisce non ritoccare FIT/BLIND a posteriori.

**Guardrail**: nessuna preimpostazione di andatura o target gemma, nessun reward sintetico, nessun update di `GameActivity` o fisica, memorie, 12 slot, UI o firmware. `G3` locomozione autonoma e `G5` world-model completo sempre bloccati in questa fase; anche l'eventuale gate E2 PASS non prova che ZERO abbia imparato a camminare o trattenere nuove abilità.

## Risultati effettivi

Risultato verificato a seguire, dopo conclusione della pipeline.

## RISULTATI VERIFICATI — CI 38988290751 (17/10/2026)

[GitHub Actions 38988290751 — SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988290751). Codice laboratorio `Zero3MotorContactWorldV313Regression.java` solo sul ramo di PR #87. Compilazione Java e **76.800 step reali** di `SymbioteMatterV1`, con veri `SquareGripPuzzle.constrain`, `MaterialContactConstraints.reconcile`, `SymbioteWorldStepV171.interior`, `GripSurfaceComposition.compose` e collisione disco `Morphology.collide` eseguiti. 320 traiettorie indipendenti come unità intere: FIT=120, CAL=120, BLIND=80, 46.080 transizioni osservate, ciascuna con **64 comandi reali per frame**; nessun accesso allo stato futuro nella previsione. Osservazione 25D: corpo-frame della V3.11 (22D) + `touchPulse` reale medio e due armoniche anatomiche. Il nastro di azioni future usato nel rollout è **registrato**, non predetto.

**Copertura reale:** CAL 81/120 traiettorie con segnali di contatto (`touchSense`/`touchPulse`), 24/120 con grip; BLIND 63/80 contatti, 16/80 grip. `pulseTraces` 81/120 CAL e 63/80 BLIND include impulsi tattili prodotti anche dal solver delle scatole, **non** solo dai dischi. Split CAL 7.349 frame pre-step già in contatto, BLIND 5.692 frame; il test dispone di vere esperienze di contatto per valutare la relazione causale. Contare solo un contatto **già osservato prima dell'azione**, non classificare il frame di una nuova collisione futura come input noto.

### Previsione a un passo: MSE, più basso è meglio

| Gruppo e metrica | FULL (corpo+azioni+tatto) | NO_ACTION | NO_TOUCH | Baseline |
|---|---:|---:|---:|---:|
| CAL XY globale | 0,000038929 | 0,000039056 | 0,000038534 | **0,000038254** ballistica |
| CAL XY pre-contatto osservato | 0,000008538 | 0,000009052 | 0,000007721 | **0,000007299** ballistica |
| CAL stato completo | **0,000319024** | 0,000623451 | 0,000293508 | 0,000870136 persistenza |
| BLIND XY globale | 0,000126998 | 0,000127160 | 0,000126227 | **0,000125836** ballistica |
| BLIND XY pre-contatto osservato | 0,000009129 | 0,000009520 | 0,000007761 | **0,000006829** ballistica |
| BLIND stato completo | 0,000667154 | 0,000959907 | **0,000614220** | 0,001468678 persistenza |

**Interpretazione rigorosa:** il modello FULL migliora la previsione delle 25 variabili di stato rispetto alla persistenza (BLIND 0,000667 vs 0,001469), ma **non migliora il movimento XY** rispetto alla ballistica: sul BLIND l'errore XY FULL è circa 0,9% peggiore, e nel sottoinsieme di contatto osservato circa **33,7% peggiore**. I comandi hanno un effetto predittivo rispetto al modello senza efferenza, ma il vantaggio XY è troppo debole globalmente. L'ablation senza tatto supera FULL sulla previsione XY sia overall sia sui frame tattili: `NO_TOUCH` BLIND 0,000007761 vs FULL 0,000009129 sui contatti. Questo dimostra il fallimento della **rappresentazione lineare attuale**, non l'inutilità del tatto biologico o software.

### Predizione autoregressiva con action tape noto: MSE di posizione

| Gruppo / orizzonte | FULL | NO_ACTION | NO_TOUCH | Baseline migliore |
|---|---:|---:|---:|---:|
| CAL 16 frame | 0,009614102 | 0,009748025 | **0,009533620** | 0,009751161 ballistica |
| CAL 48 frame | 0,024650304 | 0,033523380 | **0,024482867** | 0,034820816 ballistica |
| CAL 144 frame (diagnosi) | 0,149383274 | 0,234662262 | **0,143595513** | 0,201959564 immobile |
| BLIND 16 frame | 0,039718961 | 0,040813428 | **0,039652052** | 0,040168105 ballistica |
| BLIND 48 frame | 0,083430441 | 0,116379118 | **0,081871675** | 0,094900309 ballistica |
| BLIND 144 frame (diagnosi) | 0,345754773 | 0,717641393 | **0,318125279** | 0,378847245 ballistica |

V3.13 FULL ha miglioramenti sul rollout 48 frame su scene nuove (BLIND ≈12,1% rispetto a ballistica) e sullo stato, ma il rollout 16 frame migliora solo ≈1,1% e NON raggiunge il preregistrato 10%. La linea `NO_TOUCH` supera FULL anche nei tre rollout BLIND. Confronti post hoc sono **diagnostici**, non autorizzano promozione della variante selezionata dopo aver visto BLIND.

### Gate e causa da ricercare

**CAL = FAIL; BLIND = FAIL. Gate E2 di predizione delle collisioni NON superato.** I minimi dati tattili e le prese veri **ci sono** in entrambe le partizioni, ma il modello FULL manca almeno la condizione di qualità XY su contatto, di vantaggio sui sensori tattili, XY globale e rollout a 16 frame. **La CI verde dimostra esecuzione corretta, non gate scientifico verde.**

**Fatto strutturale confermato dal codice:** il modello riceve soltanto stato corporeo+efferenza corrente; **non percepisce il relativo oggetto esterno prima che venga toccato**, non sa se l'oggetto si muove, se è davanti o dietro, e nel rollout non viene interrogato il solver delle collisioni del mondo. Nuove collisioni possono quindi essere incalcolabili da queste feature sole. **Ipotesi, non causa singola dimostrata:** mancanza di osservazione visiva/spaziale legittima, modello lineare troppo povero per le discontinuità dei contatti, istanti di impatto/onset e scarso effetto marginale del tatto rispetto a inerzia e azione, geometria fisica non osservata. Il modello non deve mai ricevere il `physicalLaw`, il tipo segreto di oggetto né coordinate future dell'oggetto.

### Prossimo passo obbligato dal Master Blueprint

**Prima di altro fitting** auditare i sensori di vista/prossimità di ZERO nel vero gioco e definire un `SensoryFrameV3` in cui lo stato corporeo, `touchSense`, `touchPulse`, `gripHold`, propriocezione e **oggetti visibili fisicamente** (posizione relativa osservata, direzione, distanza, stima di movimento/occlusione) siano sincronizzati *prima* dei comandi. Un futuro laboratorio V3.14 deve distinguere previsione di contatto *onset* da prevedere la risposta a contatto già esistente e includere un'ablation della **vista**. Richiedere nuovi seed e scene HOLDOUT non esaminate. Non collegare ancora un nuovo brain all'app, non fare tuning sul BLIND consumato V3.13. Successivamente soltanto un E3 in ciclo chiuso potrà mostrare scelta volontaria, locomozione, retenzione e generalizzazione.

**Stato finale: modello E2 rifiutato per controllo live; G3 e G5 BLOCKED; PR #87 DRAFT; OTA172 e tutti gli slot 2.2 invariati.**

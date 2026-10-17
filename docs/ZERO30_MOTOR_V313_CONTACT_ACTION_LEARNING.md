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

*Da compilare soltanto dopo lettura log della CI.*

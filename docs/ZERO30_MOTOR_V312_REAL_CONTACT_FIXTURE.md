# ZERO 3.0 — V3.12 E2: verifica fisica del contatto autentico

**Protocollo congelato il 17/10/2026 prima del primo test.** Seguono [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI 3.0](ZERO30_UI_UX_SPEC.md), [V3.11 e suo risultato](ZERO30_MOTOR_V311_BODY_FRAME_DYNAMICS.md). Esperimento di *copertura causale dei sensori* prima di ogni altro training motorio. Non una prova di locomozione autonoma.

## Difetto specifico dimostrato nella V3.11

Con 491.520 passi reali del solo solver gel, `contactFrames=0` e `gripFrames=0` su FIT/CAL/BLIND. Il modello V3.11 ha migliorato molto la previsione della **materia libera**, ma non ha esercitato il solver di collisioni esterne. Nel gameplay, l'ordine fisico include `SquareGripPuzzle.constrain`, `MaterialContactConstraints.reconcile`, `SymbioteWorldStepV171.interior`, `GripSurfaceComposition.compose`; i dischi mobili passano da `Morphology.collide`. `skin.touchSense`, `touchPulse` e `gripHold` non sono la stessa cosa. Il solo `SymbioteMatterV1.step` non deve inventare contatti.

## Esperimento V3.12 preregistrato: vera collisione, nessuna presa finta

Ogni prova usa UN corpo `Scenario` con autentico `SymbioteMatterV1`, 96 frame di warm-up e 144 di osservazione, 64 comandi attuatoriali legali/frame, step 0,025 s. Tutte le perturbazioni di efferenza sono controlli scientifici espliciti, non una gait o abilità inserita in ZERO. Conservare i veri array `gripCommand`, `touchSense`, `touchPulse`, `gripHold` derivanti dai solvers; nessun test può scrivere valori positivi in `touchSense` o `gripHold` o cambiare la fisica produttiva. Registrare anche spostamento del corpo e dell'oggetto, contatti per frame, tentativi adesione, hold legalmente ottenuti, tempo di contatto, eventi due-facce, invalidità.

Quattro gruppi su corpo con pose 0°, 90°, +25°, −37°:
- **NEAR BOX**: scatola bilaterale vera `UnknownWorld.Entity(law=5)` a distanze dal COM di 47,5 / 49,5 / 52 unità, 2 controlli adesione (OFF; impulso comandato positivo), 2 seed, 4 pose = **48 sequenze**. Vero `SquareGripPuzzle.constrain` e composizione fisica. Vietato teletrasportare il corpo per far toccare la scatola. Nessun premio per prossimità.
- **FAR BOX**: scatola a distanza 350, 2 controlli adesione, 2 seed, 4 pose = **16 sequenze**, negativo rigoroso: niente `touchSense` o `gripHold`, nessun box move.
- **DISK**: disco mobile reale `UnknownWorld.Entity(law=2)` vicino al bordo (distante 47-49 unità), stesse opzioni di adesione/seed/pose = **16 sequenze**. Evento autentico nel canale `touchPulse` attraverso `Morphology.collide`, senza attribuirgli automaticamente `gripHold` o `touchSense`.
- **FREE**: nessun solido esterno, 2 adesioni, 2 seed, 4 pose = **16 sequenze**, negativo rigoroso senza touch/hold esterno.

Totale **96 traiettorie / 23.040 step gel** (96+144 step ciascuno), oltre ai solver di collisione. Il controller emette comandi **legali**, incluso un probe deliberato di canale adesione quando richiesto. La tenuta nasce solo dopo soglia di adesione e contatto ripetuto alla vera faccia; un contatto isolato, un'intenzione di adesione o un solo punto non possono spostare una scatola (richiesti due lati validi).

### Condizioni di accettazione

1. **Integrità:** tutti i corpi e gli oggetti finiti, `gel.invalid=0`; azioni e misure senza NaN; nessuna modifica alla fisica produttiva.
2. **Copertura:** almeno un vero `touchSense` nel gruppo NEAR BOX e un vero `touchPulse` nel gruppo DISK; registrare quanti frame/prove/seed, senza inventare una percentuale.
3. **Specificità:** FREE e FAR BOX devono avere **zero** contatti esterni e grip fisici; DISK deve avere touch pulse senza scatola-adesione e senza hold box.
4. **Causalità adesione:** con canale grip OFF, zero hold in tutti i gruppi; un eventuale hold ON va verificato da `gripCommand > threshold`, prossimità reale alla faccia e `box.boxGripHold`; la scatola NON può muoversi in assenza di presa bilaterale certificata. Se nessun hold ON avviene, la copertura della presa **resta NON VALIDATA**, senza forzare un esito verde.
5. **Confronto della telemetria:** il V3.11 vede `touchSense`/grip ma non `touchPulse`: dichiarare come difetto reale del contratto sensoriale qualsiasi pressione da disco che non sia rappresentabile al world model; nessun aggiustamento silenzioso del sensore in produzione.

Questa fase è un **gate di copertura fisica E2**, non ancora test predittivo CAL/BLIND della rete. Se la copertura riesce, si potrà progettare un dataset motorio a contatto per validare un modello nuovo su **scene e seed realmente nuovi**; se fallisce, diagnosticare l'ordine dei solver e i contatti. Non usare gli stessi campioni V3.12 come holdout blind di un futuro modello scelto guardando questi risultati. **G3/G5 restano bloccati; PR Draft, nessun merge/OTA, 12 slot e legacy intatti.**

## Esito

Esito effettivo e registrato in seguito, dopo esecuzione GitHub Actions completa.


## RISULTATI VERIFICATI — CI 38987814698 (17/10/2026)

**[GitHub Actions 38987814698 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38987814698)**. Codice E2 di laboratorio: `Zero3MotorRealContactV312Regression.java` nel branch sperimentale di PR #87, con reale solver `SymbioteMatterV1` non modificato, `SquareGripPuzzle.constrain`, `MaterialContactConstraints.reconcile`, `SymbioteWorldStepV171.interior`, `GripSurfaceComposition.compose`, `Morphology.collide` per il disco e `ContactLearning.collide`. Passati 96 casi, **23.040 integrazioni del gel reale** di cui 13.824 frame osservabili dopo warm-up, frame dt=0,025 s.

| Gruppo | Prove | Con tocco reale | Con adesione vera | Presa bilaterale | Altri risultati |
|---|---:|---:|---:|---:|---|
| Scatola vicina | 48 | **44** | **21** | **15** | 5.873 frame con `touchSense`; 2.764 frame con hold; 1.208 con presa bilaterale; somma delle distanze nette di tutte le scatole 5,513 unità |
| Scatola lontana | 16 | **0** | **0** | **0** | Nessuna falsa collisione; distanza scatole 0 |
| Disco mobile | 16 | 0 sul canale *box* `touchSense` | **0** | **0** | **13** prove con pressione meccanica `touchPulse`, 1.196 frame; somma distanze oggetto 67,867 |
| Nessun ostacolo | 16 | **0** | **0** | **0** | Nessun contatto o presa esterni |

**Verifica critica dell'assenza del comando adesione:** nelle 24 prove di scatola vicina `adhesion=OFF`, **23** hanno un tocco reale, **zero** hanno presa `gripHold`, **zero** prese bilaterali, **zero movimento della scatola**. L'intenzione di aderire e la collisione semplice da sole non diventano falsamente una presa. Nelle 16 prove con ostacolo lontano, 8 avevano intenzione adesiva positiva: nessuna presa. La verifica di proprietà del contatto dopo collisione passa soltanto se il comando fisico supera la soglia reale, la patch tocca la faccia di quella scatola e il suo timer è posseduto dall'oggetto. Nessun hold o touch viene iniettato dal test. Nessuna anomalia `invalidBoxMove`.

**Esito protocollato: `coverage=PASS`, `completeContactGate=PASS`.** Qui il gate è esplicitamente la **copertura E2** di contatto/tatto/presa; NON un pass di G3/G5, di locomozione autonoma o di generalizzazione neurocognitiva. In ON il laboratorio imposta deliberatamente una pressione adesiva muscolare di prova che ZERO dovrà invece imparare a decidere da sé nel vero gioco. Sommare le distanze nette di scatole diverse non misura movimento di una singola scatola; la quantità viene riportata come somma diagnostica, non come prestazione.

### Problema sensoriale concreto identificato

I due canali **NON** sono sinonimi:
- `skin.touchSense`: contatto con una faccia solida (scatola/gate/pannello) composto dal mondo fisico; valori positivi osservati nella prova di scatola.
- `skin.touchPulse`: pressione fisica dei dischi mobili, generata nel vero solver `Morphology.collide`, anche **senza** `touchSense` e senza adesione. V3.11 `observe` usa `touchSense`, ma **non** `touchPulse`: una quota di conseguenze fisiche con dischi è quindi invisibile a quel predittore.

**L'implicazione per l'architettura ZERO3 è misurabile:** un unico segnale tattile da scatola sarebbe incompleto. Il contratto `PhysicsOutcome / sensory` deve includere entrambe le modalità fisiche con provenienza e tempistica, senza far credere al brain che pressione = presa. La prossima V3.13 deve raccogliere stati/azioni/esiti di V3.11 con i due sensori distinti e solver oggetti, usare training e holdout indipendenti su diverse scene+seed e confrontare ablation su contatto/presa, scegliendo il modello **prima di leggere i risultati**. Non riutilizzare questi 96 casi come una prova blind incontaminata.

**Limitazioni:** alcuni casi NEAR non toccano la scatola; gli oggetti vengono generati in fixture vicine e non raggiunti da una policy esplorativa autonoma; la copertura della presa bilaterale è verificata ma non mostra abilità appresa. Soltanto le fixture di box/disk, non l'intera ecologia del gioco. Non eseguire un passaggio diretto da questi risultati alla UI/OTA. **PR #87 Draft, G3/G5 bloccati, nessun aggiornamento di produzione.**

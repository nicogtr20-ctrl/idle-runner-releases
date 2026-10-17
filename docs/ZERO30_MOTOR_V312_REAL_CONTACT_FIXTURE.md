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

*In attesa della prima esecuzione CI e della lettura dei log. Il successo tecnico della pipeline non basta a superare il gate.*

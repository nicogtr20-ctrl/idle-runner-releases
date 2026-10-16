# ZERO 3.0 V3.6 — PREVISIONE MUSCOLARE A 384 FRAME SU CORPI REALMENTE IN MOVIMENTO

**Stato:** benchmark esplorativo isolato su fisica \`SymbioteMatterV1\`; nessuna integrazione nel cervello 3.0 durante una vita, nessun cambiamento OTA o salvataggio. [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI 3.0](ZERO30_UI_UX_SPEC.md), [Test V3.5 e risultati](ZERO30_MOTOR_V35_ADAPTIVE_CREDIT.md) sono prerequisiti di lettura.

## Evidenza che motiva la prova

- L'effetto di una contrazione può cambiare **dopo** il vecchio orizzonte di 48 frame (13/16 casi con differenza >0,1 U, 2/16 inversioni del segno a +360).
- L'astensione adattiva V3.5 produce qualche progresso aggregato in 32 condizioni, ma **non supera il gate energia e replicabilità**: screen +165,552 vs controllo +164,840 U con lavoro 1,264525 vs 1,248223; su 16 nuove condizioni +163,316 vs +160,333 U con lavoro 1,261322 vs 1,246740, soltanto 6/16 migliorate.
- Una previsione che stima la conseguenza **prima che sia terminata** può approvare interventi che peggiorano il comportamento successivo. Il kernel V3.5 ha soltanto 42 feature prevalentemente geometriche e non modella adeguatamente lo stato iniziale di velocità/strain e la storia.

## ADR ZERO3-M36 — risposta fisica a orizzonte adeguato + propriocezione locale

**Sorgente:** [Zero3MotorLongHorizonRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorLongHorizonRegression.java).

Per ogni prova il laboratorio genera **due veri corpi fisici deterministici** in stato iniziale equivalente. Solo uno riceve **12 frame di intervento localizzato** su uno dei 64 comandi reali; entrambi sono poi osservati fino a **384 frame totali** con lo stesso campo muscolare ordinario. Il modello non riceve il delta controfattuale durante una vera vita: il controllo gemello è disponibile **soltanto nel laboratorio**.

- Prima dell'impulso si osservano **42 feature di orientamento/attuatore + 10 nuove feature reali**, fra cui velocità del centro materiale, proiezione dell'inerzia sull'intenzione, ampiezza e variabilità dei raggi di membrana, velocità radiale globale e locale, presa e stato di strain/area. Valori leggibili da \`Body\`, \`Morphology.Surface\`, \`SymbioteMatterV1\`, mai da coordinate di gemme o parametri nascosti.
- Tre target distinti: spostamento firmato *durante* l'impulso, contributo di spostamento **post-impulso fino a frame 384**, incremento di lavoro muscolare reale rispetto a controllo simulato.
- Due modelli ridge differenziati: **42 feature** con stesso target a 384 frame (baseline senza i nuovi sensori), **52 feature** con propriocezione/inerzia/strain. Confronto con una terza baseline: prevedere effetto zero.
- **TRAIN**: posture 0° e 90°, stati iniziali generati da simulazione fisica con precondizioni 0,1,2, 4 direzioni × 4 canali × 2 settori rappresentativi × 2 segni = **384 coppie fisiche**. **CALIBRAZIONE**: ±22,5° con gli stessi tre stati precondizionati = **384 coppie**, mai inserite nel fit.
- **TEST FINALE NON USATO PER FIT/CALIBRAZIONE**: posture ±30°/±60° con precondizione 3 = **256 coppie**. Totale **1.024 prove gemelle** e **786.432 integrazioni fisiche** (2 solver × 384 × 1.024), esclusi i frame di precondizionamento.
- Il modello a 52 feature viene autorizzato a selezionare un **singolo** intervento in ciascuno dei 16 contesti finali solo se nella calibrazione migliora di almeno 10% l'MSE rispetto alla baseline zero *su tutti e tre i target*. L'effetto osservato in finale NON viene usato per scegliere se il modello fosse affidabile.
- Non confondere un progresso di un impulso **sul solver di laboratorio** con locomozione, abilità riutilizzata o apprendimento corporeo in una vita completa. G3 rimane bloccato; anche un predictor perfetto sarebbe soltanto un componente.

## Criteri di esito

Registrare esattamente MSE immediate, delayed, cost su calibrazione e test finale per 42/52 feature e baseline zero; capacità di scegliere interventi con spostamento firmato positivo/costo sostenibile su 16 condizioni; percentuale di miglioramento della previsione. Se il modello 52 feature NON supera le baseline nel test a 384 frame, non va integrato, e la classe rimane benchmark negativo. Anche se le supera, occorre un **A/B a ciclo chiuso con vere decisioni consecutive** e più seed/scene prima di qualsiasi promozione.

## Risultati effettivi

Esecuzione tramite [workflow dedicato ZERO3 384-frame motor QA](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions), senza APK. Compilazione o test eseguito non equivale a qualità motoria. Inserire qui i risultati numerici **solo dopo lettura completa dei log**.

**G3 locomozione autonoma, G5 world model generale, UI V3 e OTA: NON COMPLETATI.**

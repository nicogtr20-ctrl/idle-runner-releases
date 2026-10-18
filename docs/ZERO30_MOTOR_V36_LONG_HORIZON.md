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


## RISULTATI VERIFICATI — CI 38981238274

**[GitHub Actions 38981238274 — SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38981238274)**. Compilazione JVM e **1.024 coppie fisiche** su solver reale a 384 frame/prova = **786.432 integrazioni fisiche** di soli due rami matched (esclusi warmup precondizionamenti). È un benchmark di laboratorio: né crescita neurale né esperienze di una singola vita autonoma.

| Split e modello | MSE immediata | MSE ritardata fino a 384 frame | MSE costo | MSE ritardata baseline zero |
|---|---:|---:|---:|---:|
| Calibrazione, 42 feature | 0,000429242 | 0,017958093 | 0,000000000191 | 0,022563066 |
| Calibrazione, **52 feature (stato/inerzia)** | **0,000429052** | **0,017813648** | 0,000000000193 | 0,022563066 |
| Finale non usato nel fit, 42 feature | 0,000492590 | 0,022757698 | **0,000000000390** | 0,023833805 |
| Finale non usato nel fit, **52 feature** | **0,000492345** | **0,022099238** | 0,000000000393 | 0,023833805 |

Errore immediato baseline zero: **0,004761218** sul set calibrazione e **0,003995303** sul finale. Errore costo baseline zero: **0,000000007763** sul set calibrazione, **0,000000007784** sul finale.

**Verifica delle soglie:**
- Il modello 52 feature supera il gate del **10%** contro la baseline zero su tutti e tre i target in *calibrazione*, quindi solo lì risulta «calibrato».
- Sul set finale a pose ±30/±60 con precondizionamento 3, migliora la previsione ritardata contro zero del **7,28%**, insufficiente per superare la stessa soglia del 10% fuori distribuzione. **Il gate indipendente di robustezza NON è superato.**
- Il miglioramento della MSE ritardata passando da 42 a 52 feature sul set finale è circa **2,89%**: aggiungere semplicemente dieci sensori non risolve la complessità della coda fisica.
- Le perturbazioni scelte soltanto sulla base delle previsioni sono positive in 16/16 situazioni finali e ciascuna costa meno di 0,005 nell'unità registrata; delta firmato complessivo **+6,11873 U**; extra work **0,0024375**; utilità penalizzata **+6,00905**. Questi risultati riguardano **un singolo impulso gemello su corpi inizialmente precondizionati**, non prove di locomozione a lungo termine, e **non ribaltano** il fallimento del gate di previsione.

**Conclusione di implementazione:** usare +384 frame invece di +48 rende la predizione scientificamente più corretta ma mette alla luce un limite del modello statico: il suo errore di evoluzione ritardata non generalizza abbastanza bene su nuovi stati. **NON integrare il modello 52 nel controllo motorio di produzione**, non addestrare sinapsi o promuovere skill con i risultati di questo laboratorio.

### Ipotesi per la fase successiva, non ancora implementate

1. Modello dinamico ricorrente/temporale con stato storico *preazione*: velocità locali, collassi, distribuzione di tensione, adesione vera, impulsi precedenti e tempo da ultimo intervento, non solo feature statiche a un unico istante.
2. Allenamento esclusivamente su transizioni fisiche realmente osservate in evoluzione, con bootstrap e maschere delle perturbazioni effettivamente applicate, senza usare lo stesso contesto due volte per train e certificazione.
3. Selezione dell'azione conservativa con **incertezza calibrata**: astenersi quando il lungo orizzonte non batte una previsione nulla o quando il costo domina; leggere la qualità su scene e seed differenti dall'attuale stessa fisica.
4. Benchmarks di 2.000+ decisioni per più vite/seed con actor autonomo, memoria procedurale vera e migliori capacità in 3/4 direzioni + vincolo di costo **prima** della promozione nel gioco.
5. Nuova UI V3 e persistenza: progettate nei documenti, **non ancora create**. App stabile OTA172 non alterata.

**Gate G3 apprendimento motorio e G5 modello del mondo completo: BLOCCATI.**

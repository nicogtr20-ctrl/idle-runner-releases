# ZERO 3.0 — V3.33 | Ablazione locale, ritardo e salvaguardia della presa bilaterale

**Protocollo congelato il 17/10/2026 prima di qualunque codice V3.33 o accesso a nuovi risultati.** Obbligatori [Master](ZERO30_MASTER_BLUEPRINT.md), [Contract](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.31](ZERO30_V331_LOCAL_ACTUATOR_COUNTERFACTUAL.md), [V3.32](ZERO30_V332_TEMPORAL_LOCAL_CREDIT.md), [UI](ZERO30_UI_UX_SPEC.md).

## Perché
V3.32 ha un miglioramento BLIND preregistrato 4/4 famiglie ma **BOX_NEAR signed -11,648**, e presa bilaterale ridotta a **1 frame** vs OLD 55 e FROZEN 588; quindi G3 è ancora bloccato. V3.32 ha confuso causalmente quota spaziale locale, ritardo 0,82 ed esclusione del canale adesivo 2. V3.33 deve isolare i primi due elementi e aggiungere esplicitamente la metrica che mancava.

## Bracci e intervento di apprendimento
Cinque bracci accoppiati per nascita, attore V317 e motoneurone V319 preaddestrato e congelato identici:
- OLD_GLOBAL = algoritmo `AdaptiveCortex.learn` invariato.
- FROZEN = stessi comandi a parità di stimoli, zero update.
- FULL_LOCAL_DELAY = formula V3.32 invariata: `localNow[sector] + 0.82 * localNext[sector]`.
- NO_DELAY_LOCAL = **stesso credito locale** ma `localNow[sector]` soltanto (peso ritardo = 0). La plasticità viene applicata solo dopo il post-solver di ogni decisione terminata.
- NO_LOCAL_DELAY = stessa eligibility/perturbazione e stesso ritardo 0.82 di FULL, ma il punteggio è **la media misurata dei 16 settori**, uguale per i 16: distingue valore di attribuzione *spaziale*, senza introdurre un nuovo global score fittizio. Il 2° canale rimane sempre protetto per tutte e tre le varianti V3.33.
Tutte le varianti: stessa architettura cortex 64×8, learning rate .013, perturbazione campionata dal cortex, esperienza PRE→12 real solver substeps→POST, `gel.work` vero, bound cambio singolo peso <= .002 e nessun ulteriore controllo del corpo. L'eligibility/POST locale è un proxy, NON la vera derivata causalmente identificata e non può leggere la fixture world/oggetti come target. In V3.33 le tre varianti ricevono la **stessa delayed-probe ownership**: se non si verifica un secondo POST non esiste credito «tardivo»; nessun gradiente futuro in un singolo frame. NO_DELAY è aggiornato al completamento della successiva decisione per evitare un vantaggio di timing non preregistrato.

## Dataset chiuso e confronto
TRAIN = 32 vite × 96 decisioni/arm su quattro scene FREE, BOX_NEAR, DISK_NEAR, BOX_FAR con pose/scene diversi da V3.32. Censurare vera autointersezione senza simulare le decisioni mancanti. BLIND = **24 nuove vite × 96 = 2304 decisioni/arm e 27.648 solver steps/arm**, id/pose/seeds/gap nuovi e disgiunti da TRAIN, V3.30, V3.31 e V3.32; frozen weights/no exploration, input actor non prescritti. Una sola lettura BLIND; nessuna modifica di reward, seed, fisica o soglie dopo aver visto i risultati. Riferire signed displacement, gross, work, cost/positive progress, decisioni svolte vs censurate, grip genérico e bilaterale BOX, HOLD frame con owner fisico.

## Gate di promozione V3.33, deliberatamente più severo
FULL passa come candidato di integrazione SOLO se:
1. nessuna vita BLIND censurata, almeno 2.304 decisioni e 27.648 tick veri per ciascun braccio;
2. signed displacement di FULL **strettamente positivo** su BOX_NEAR; sulla stessa famiglia maggiore di OLD, FROZEN, NO_DELAY e NO_LOCAL; costo muscolare per avanzamento positivo <= 110% di OLD e FROZEN (se baseline signed <=0 si usa comunque costo assoluto e si dichiara esplicitamente);
3. almeno 3/4 famiglie direzionalmente migliori di **tutti e quattro** i controlli con +10% efficienza e costo non peggiorato oltre +10% vs OLD e FROZEN;
4. BOX bilateral frames FULL >= **80% del maggiore fra OLD e FROZEN** sulle stesse scene BLIND, e 6/6 vite BOX con hold legale oppure copertura e hold >=80% OLD e FROZEN; non accettare presa generica come sostituto di bilaterale;
5. reali update peso >0 in tutti e tre gli ablation learner, zero in FROZEN; nessun hardcoded gait, thrust, premio di avvicinamento, sopralluogo dato futuro o regressione dell'head V319;
6. esito confronti HOLDOUT pubblicato anche se negativo. Se uno dei criteri fallisce: **SCIENCE FAIL, nessuna integrazione live**.

L'eventuale PASS è SOLO `G3_ABLATION_CANDIDATE_NOT_DEVICE` (non G3 definitivo): mancano stessa-vita 2.000+ decisioni con resume e nuova geometria, stable bilateral manipulation, survival skill, evoluzione neuroni, veri device Android G9 e G5 world model. La CI distingue successo tecnico da successo scientifico.

## Output
Ablation source/QA nel ramo `feature/zero30-foundation-g0-g3-20261016` Draft PR #87; questo protocollo visibile su main. Non pubblicare APK o OTA, non toccare 12 vecchi cervelli 2.2, né inserire V3.33 nel gameplay.

## Risultati
*Inserire soltanto dopo aver letto i log reali CI.*

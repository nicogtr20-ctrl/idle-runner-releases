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

## Risultato verificato — 17 ottobre 2026

**[GitHub Actions finale 39040988120 — SUCCESS tecnico, SCIENZA FAIL](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39040988120)**, job 117420286921, compilazione reale del solver originale, cinque bracci, Android Debug **BUILD SUCCESSFUL in 1m 2s**. Log completo letto, senza correggere pose, scene, fisica o criteri dopo il BLIND. La prima CI falliva in compilazione per un nome di costruttore copiato e una variabile \`grip\` locale duplicata: corretti prima di eseguire qualunque BLIND. La CI finale è dunque la prima esecuzione integrale del protocollo.

**Codice ramo isolato:** \`Zero3FiveArmAblationV333Regression.java\`; \`Zero3LearningFoundation.AdaptiveCortex.learnV333\` aggiunto ma **non chiamato dal runtime V325**; workflow \`.github/workflows/zero30-v333-ablation-qa.yml\`. Preservati solver/gel/attriti, attore ricorrente V317 frozen, head adesivo V319 preaddestrato frozen, 64 efferenze reali, azioni PRE→12 gel step→POST e ownership dell'unica decisione. \`FULL\` usa segnale locale attuale+0,82 successivo, \`NO_DELAY\` solo locale attuale e \`NO_LOCAL\` media dei 16 settori con ritardo 0,82; in tutti i casi il gradiente si aggiorna solo quando il secondo POST esiste. Nessuna informazione futura nel cervello.

**TRAIN (32 vite per braccio)**, aggiornamenti veramente applicati ai pesi:
| Braccio | Veri updates | Chiamate credito |
|---|---:|---:|
| OLD_GLOBAL | 2.976 | procedura legacy |
| FULL_LOCAL_DELAY | 2.201 | 2.945 |
| FROZEN | 0 | 0 |
| NO_DELAY_LOCAL | 2.201 | 2.945 |
| NO_LOCAL_DELAY | 2.201 | 2.945 |

Gli stessi **cinque bracci** incontrano autointersezione \`selfCrossings=1\` in scena \`BOX_NEAR ordinal=337\`, già al primo comando \`sub=1, completed=0\`; solver \`gel.invalid=0\`. Nessuna decisione viene inventata. Contributo delle 31 vite restanti del TRAIN conservato. La parità degli aggiornamenti per i tre ablation learner è un fatto osservato, **non prova uguaglianza dei pesi né dei risultati**, che divergono nel BLIND.

**BLIND:** 24 nuove vite per braccio, ordinal 640..663 (in quattro famiglie bilanciate), 96 intenzioni per vita. **2.208 decisioni completate e 26.496 veri passi gel per ciascuno dei cinque bracci**. Nella scena \`BOX_NEAR ordinal=645\` **tutti e cinque i bracci** hanno autointersezione al primo substep di decisione zero (\`selfCrossings=1, solverInvalid=0\`), perciò la vita viene censurata **0/96**. Non è un effetto dimostrato della policy FULL: essendo condiviso dai cinque metodi alla partenza, la prima ipotesi verificabile è una fragilità del contatto/morfologia nella fixture. Non è lecito rimuoverla a posteriori dal BLIND. Il requisito 2.304 decisioni/arm e 6/6 vite BOX con presa è **FALLITO**.

### Confronto a parità delle nuove scene e della fisica

Valori = **spostamento netto firmato lungo l'intenzione che l'actor ha scelto autonomamente**, non distanza verso una gemma/script.

| Famiglia | OLD_GLOBAL | FULL_LOCAL_DELAY | FROZEN | NO_DELAY_LOCAL | NO_LOCAL_DELAY |
|---|---:|---:|---:|---:|---:|
| FREE | +26,28099 | **+168,92092** | +28,59219 | +134,68873 | +72,65750 |
| BOX_NEAR* | −7,60633 | **−2,13765** | −8,47134 | −4,82326 | −8,14991 |
| DISK_NEAR | −11,64813 | **+41,53364** | −9,22134 | +27,62774 | +2,75018 |
| BOX_FAR | +25,38982 | **+149,03441** | +26,57558 | +118,02098 | +65,49824 |

\*BOX_NEAR contiene soltanto 5/6 vite complete e **480 decisioni/arm**: è più vicino allo zero con FULL, ma **rimane negativo**. Non chiamarlo movimento coordinato intorno alla scatola. FULL ha efficienza direzionale maggiore in valore assoluto rispetto a NO_DELAY e NO_LOCAL in tutte e quattro le scene (\`FREE 0,751999\`, \`BOX -0,034580\`, \`DISK 0,328746\`, \`FAR 0,732236\`). Tuttavia il miglioramento relativo **+10% preregistrato su tutti i quattro controlli** fallisce in FREE e BOX_FAR; le due famiglie in cui passa la soglia sono BOX_NEAR (ma censurata) e DISK_NEAR. **Solo 1/4 famiglia supera congiuntamente la soglia e la copertura fisica.** Nessuna selezione ex post di soli risultati favorevoli.

### Guardrail bilaterale recuperato in questo campione, ma non il gate

BOX_NEAR, 5 vite valide per braccio, autentiche prese con owner:
| Braccio | Frame di presa generica | Frame bilaterali |
|---|---:|---:|
| OLD_GLOBAL | 5.750 | 681 |
| FULL_LOCAL_DELAY | **5.750** | **586** |
| FROZEN | 5.750 | 331 |
| NO_DELAY_LOCAL | 5.750 | 524 |
| NO_LOCAL_DELAY | 5.750 | 502 |

Il guardrail bilaterale relativo **PASSA**: \`586/681=86,0%\` rispetto al migliore fra OLD e FROZEN, sopra la soglia 80%. I cinque metodi condividono una vera vita censurata: \`gripLives=5\` e il requisito preregistrato di sei vite BOX con hold FAIL; dunque \`genericGripGuardrail=false\` per coverage, pur con frame hold autentici. **I 586 frame di V3.33 e l'unico frame V3.32 vengono da scene/pose differenti**: NON dimostrano che un cambiamento mirato di algoritmo abbia «riparato» le prese perse nel BLIND V3.32. Dimostrano soltanto che, sul NUOVO set e con identica V319, il candidato non annulla la bilaterale. Il comportamento è evidentemente molto sensibile allo scenario e richiede robustezza multiseed.

### Gate e implicazioni

Da log CI: \`technicalFiveArmBenchmark=PASS\`, \`preregCandidateGate=FAIL\`, \`fullyValidFamilies=3/4\`, \`directionalFamilyWins=1/4\`, \`BOXPositive=false\`, \`BOXBestOfFive=true\`, \`bilateralGuardrail=true\`, \`genericGripGuardrail=false\`, \`G3_GENERAL_BLOCKED G5_BLOCKED NO_OTA\`.

**Interpretazione rigorosa:** su questa nuova batteria l'effetto combinato spaziale+temporale ha prodotto una migliore traiettoria media firmata dei suoi due ablation learners, su ogni famiglia, ma ciò non prova ancora l'effetto indipendente ciascun termine in ambienti nuovi. La manipolazione bilaterale non è annullata sulla nuova distribuzione, ma **la locomozione BOX non è positiva** e una geometria ha creato un'incertezza fisica condivisa a inizio vita. NON è dimostrata locomozione autonoma robusta, G3 resta BLOCCATO; non integrare V332/V333 in GameActivity.

**Prossimo passo V3.34:** prima di ritoccare credito/neuroni/solver, condurre un **audit isolato della fixture d'intersezione** per ordinal 337/645 su cinque bracci, separando nascita geometria, step col box, self-crossing pre/post, solver/collisione e grip; nessun tuning dell'holdout V3.33. Poi disegnare una NUOVA verifica con morfologie/pose indipendenti, true positive BOX, bilateral robusta, 2.000+ scelte long-horizon nella stessa vita con persist/resume, cervello frozen/learned. Ogni successiva batteria BLIND deve essere preregistrata e non ritoccata. Nell'immediato solo codice QA e report, no merge/OTA.

**Produzione verificata dalla CI:** Android Debug compilata, PR #87 Draft, OTA \`0.11.6-ota.172\` invariata, 12 cervelli 2.2 preservati.


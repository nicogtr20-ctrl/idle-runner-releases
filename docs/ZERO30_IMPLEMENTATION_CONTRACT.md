# ZERO 3.0 — CONTRATTO DI IMPLEMENTAZIONE, TEST E HANDOFF TRA CHAT

> **V3.34 STAGE-ROOT DIAGNOSTIC PASS, G3 SCIENTIFIC FAIL PERSISTE:** [rapporto dettagliato](ZERO30_V334_BIRTH_TO_CONTACT_CROSSING_AUDIT.md), [CI finale 39042522334](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39042522334). Audit sequenziato sui falliti V333 ordinal337/645 e 4 controlli; zero crossing fino a post-`gel.step`; `SquareGripPuzzle.constrain` introduce prima coppia 45/47 o 41/43 e altre fasi WORLD non eliminano crossing, anche senza comandi muscolari. Senza WORLD con identici 64 muscoli il crossing non accade. Su substep gel successivo riparazione può avvenire, poi BOX lo reintroduce. Schema stage localizzato, meccanismo interno della routine ancora aperto. Nessuna modifica codice solver/brain, Android Debug compile PASS, no merge/OTA. Next V3.35 strumentare sotto-operazioni e solo dopo applicare eventuale conservativa riparazione topologica provata senza degradare V319 grip.

> **V3.33 VALIDAZIONE E GATE: [rapporto scientifico](ZERO30_V333_LOCAL_DELAY_BILATERAL_ABLATION.md) · [CI 39040988120 TECH PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39040988120)**. Tre ablation `FULL, NO_DELAY, NO_LOCAL` + baseline OLD/FROZEN, vera PRE/POST fisica, credit solo su outcome local già vissuto. 32 vite TRAIN×5; BLIND 24 vite×5 nuove, **2.208 decisioni/arm anziché 2.304** per self-crossing indipendente dalla policy, BOX ordinal 645 decision0 sub0 in tutti i 5; TRAIN ordinal337 identico. Solo DISK supera ogni vincolo tra 4 famiglie complete: `directionalFamilyWins=1/4`, `BOXPositive=false`; bilateral 586 FULL/681 OLD/331 FROZEN **PASS 80%** sulle medesime nuove scene ma `genericGripGuardrail=false` per solo 5/6 vite valid BOX. `preregCandidateGate=FAIL`; vietato promuovere G3 anche se perfino Android Debug compila; NEXT V3.34 audit materiale/ispezione fixture condivisa e altro BLIND prereg. NO MERGE/OTA.

> **V3.32 — GATE G3 PRELIMINARE PASS, NON SKILL APPROVATA (17/10/2026):** [report protocollato](ZERO30_V332_TEMPORAL_LOCAL_CREDIT.md), [CI finale 39039987557](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39039987557). 32 TRAIN episodi per cortex OLD vs NEW vs FROZEN, 24 BLIND episodi per braccio, 2304 vere scelte/27648 gel tick/arm, 4/4 famiglie soddisfano confronto direzionale+cost per useful e coverage; 2.258 cambi reali pesi NEW, non 3.021 chiamate. 3 TRAIN censor per vere autointersezioni, zero BLIND. **BOX_NEAR rimane NEGATIVO, bilaterali 1 NEW vs 55 OLD/588 FROZEN** pur con 6900 grip generici ciascuno: G3 resta BLOCKED. Metodo V332 solo package LAB, NON disponibile a V325, no modifica law/solver/head V319. NEXT V3.33 nuova ablation e holdout obbligatoria prima di discutere la promozione.

> **V3.31 | CONTROFATTUALE MOTORIO FISICO PASS, SKILL G3 FAIL (17/10/2026):** [protocollo+log+limiti](ZERO30_V331_LOCAL_ACTUATOR_COUNTERFACTUAL.md), [CI 39038248576 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39038248576). 576 triple CONTROL/+/− su 4 scene, 24 pose totali, 8 settori, 3 canali non-grip, 12+24 solver step; totale 62.496 tick fisici. 576/576 differenze finale >1e-5, 481/576 dominanza del delayed, 291/576 segno PLUS−MINUS negativo lungo l'intento, 288 veri contatti e zero grip per frozen V319 untrained; Android Debug PASS. Riconosciuti causali gli interventi reali con retroazione, NON le assegnazioni di merito neurale. G3 non superato, vecchio confronto V330 trained vs frozen resta 0/4. NEXT V332 temporally/locally causal credit train + nuovi holdout: protocollo fissato PRIMA di qualunque cambiamento brain.

> **Checkpoint V3.30 (17/10/2026):** [rapporto motorio e preregistrazione](ZERO30_V330_MOTOR_CAUSAL_AUDIT.md) · [CI 39036094748 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39036094748). Nuovo audit motorio NON INTERVENTISTA legge vera intenzione della rete V317, 64 efferenze del solver e outcome fisico COM, scostamento rispetto alla velocità iniziale, lavoro, presa e indice di contrasto comandi; LAB HUD mostra intenzione↔spostamento dopo ogni decisione senza inventare misure al restore. Cortex matched train .013 e frozen 0 a pari world/init/attore/head V319 ed esplorazione, 20 scenari (5 pose per FREE/BOX/DISK/FAR), 96 decisioni per caso e braccio. **0/4 famiglie con miglioramento preregistrato; signedIntent TRAIN +2.281474 vs frozen +5.478730; SCIENTIFIC MOTOR G3 FAIL**. Il gate positivo V319 grip contestuale non viene annullato. Nessuna nuova andatura, no modifica reward/solver, G3 resta BLOCCATO; prossimo tentativo sul credito motorio differenziale temporale e settoriale, mai tuning sul BLIND ora consumato. G5/Android-device/G9/UI3D ancora aperti, NO OTA172/12 cervelli intatti.


> **Checkpoint V3.29 (17/10/2026):** [rapporto tecnico e protocolli Galaxy S21](ZERO30_V329_BOUNDED_RAM_DEVICE_READINESS.md), [CI 39033772568 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39033772568). `Zero3ReplayJournalV329` mantiene max 32 full esperienze sensoriali PRE/12×64/POST già salvate in RAM, mentre le altre vengono lette pigramente da segmenti V328 immutabili SHA-256; il tail prima del commit rimane in memoria. `Zero3RuntimeV325` e `Zero3SegmentedArchiveV328` evitano le vecchie copie integrali e dopo ogni checkpoint passano al journal lazy senza cambiare il formato su disco. 2 vite da 2.000 esperienze vere con 1.000+restart+1.000 identiche a 2.000 continue, max 32 record dopo commit, max 160 fra checkpoint ogni128; corruzioni/fail closed PASS, Android Debug compilato. V325–V327 regressioni PASS. UI V3 mostra resident/unsaved reali. Non riduce a 32 l'intero heap: rimanenti trace, ledger e modello; Galaxy S21 G9 mai testato, G3 locomozione e G5 world model non validati, UI finale aperta; no merge/OTA172 o slot 2.2.


> **Checkpoint ZERO V3.28 (17 ottobre 2026):** [rapporto completo](ZERO30_V328_SEGMENTED_LONG_LIFE.md) · [CI 39032930838 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39032930838). Il nuovo `Zero3SegmentedArchiveV328` separa il journal motorio autentico in 32 record per segmento immutabile SHA-256 dal manifest neuronale/fisico/energetico/room, con doppia generazione e hash verificati. **2 vite autonome distinte da 2.000 decisioni vere ciascuna** passano cold JVM restart a 1.000 contro controllo 2.000 senza interruzioni, confronto integrale bytewise e 63 segmenti per vita; corruzione segmento o manifest, orfani e fail closed PASS; Android Debug compile PASS. Vita di test sostenuta da vere fonti blu collocate nell'ambiente dal banco, non prova di sopravvivenza appresa. Runtime mantiene ancora esperienze in RAM, Android S21/G9 NON testato, G3 locomozione e G5 non validati, UI 3D finale mancante, nessuna OTA.


> **Checkpoint ZERO 3.27 (17/10/2026):** [rapporto verificato](ZERO30_V327_VITAL_LIFECYCLE.md), [CI 39031488412 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39031488412). I due segnali di interocezione aggiunti al percorso PRE del recurrent encoder sono fuel/100 e danno di contatto realmente misurato nella decisione precedente; fusione 25% in due feature 20D esistenti, senza cambiare topology/weights di V321, senza future leakage. Test dimostra scelta motoria diversa al cambiare del fuel, 7 energia danno da hazard reale, 10/10 vite matched restart e Android Debug compilato. **Scoperto limite di replay V322B**: oltre 256 record/8 MiB il checkpoint non è valido, perciò V3.27 protegge l'archivio con stop e salvataggio sicuro a **240 decisioni**; prova 240 fisiche+resume+no credito al 241 PASS. **V3.28 journal scalabile 2.000+ esperienze è ora il blocker prioritario**. Non è ancora prova Android Galaxy S21, né vero critic/homeostasi appresa, G3 locomozione/G5 world-model/UI3D/G9. Nessuna OTA, 12 slot 2.2 invariati.


> **Checkpoint V3.26 (17/10/2026):** [rapporto](ZERO30_V326_ANDROID_ENTRY.md) · [CI SUCCESS 39030669596](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39030669596). Un Activity V3 sperimentale non esportato è accessibile con conferma dal menu LABORATORI di GameActivity, senza usare i 12 slot legacy. Una nuova nascita di gel reale 96 patch/74 particelle non usa più le classi Regression; runtime decide e salva su cartella privata V3, mostra al Canvas solo misure reali del corpo e del mondo e offre creazione/ripresa/x1/x5/pausa e salvataggio. In JVM indipendenti, 12+restart+12 decisioni = 24 ininterrotte bitwise, rollback e rifiuto episodio estraneo PASS, build Android Debug PASS. **Non è ancora sull'APK installato**, no test su Galaxy S21, energia ancora non percepita dal cervello, G3/G5/G9/UI finale aperti. PR #87 Draft, no merge/OTA172, 12 cervelli 2.2 intatti.


> **Checkpoint V3.25 (17 ottobre 2026):** [rapporto tecnico](ZERO30_V325_RUNTIME_BOUNDARY.md) · [CI definitiva 39029780647 PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39029780647). Primo `Zero3RuntimeV325` indipendente dal runner di regressione: unico owner di scelte/rete/cortex/adesione/gel fisico/stanza/riserva e journaling causale+snapshot, con `Zero3AndroidSessionV325` compilata per un percorso Android privato V3 non collegato a GameActivity. 25/25 vite, 96 decisioni ciascuna con riavvio a 48, confronto bytewise, terminale da energia zero e rifiuto episodi estranei/doppia corruzione, Android Debug SUCCESS. **Solo gate LAB JVM; G3 locomozione, G5 world model, G9 device e UI 3.0 NON chiusi.** OTA172 e 12 slot 2.2 non toccati.


> **Sottogate V3.24/LAB verificato 17/10/2026:** [rapporto](ZERO30_V324_ROOM_WORLD_CHECKPOINT.md) · [CI 39026713027 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39026713027). Commit unico corpo+neuroni+survival+stanza ScarcityLab/door hysteresis+room.food, 25/25 vite a 96 decisioni con interruzione a 48 vs riferimento continuo, confronto bytewise, doppi guasti e Android Debug PASS. Nessuna migrazione o merge produzione; Gate G3 locomozione e G5 world model NON promossi. Niente G9 Android/device reale, OTA172 e 12 salvataggi 2.2 invariati.


> **V3.23 LAB PASS (17/10/2026):** [protocollo/risultati](ZERO30_V323_SURVIVAL_CONTINUITY.md), [CI 39025228383](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39025228383). 20/20 riprese della stessa vita 48+48 vs 96 senza stop, archivio unico con neuroni/gel/world/replay e metabolismo, esperienze e riserva autenticamente misurate dalla fisica di fixture, SHA rollback a 48 e fail closed, Android Debug PASS. Equazioni derivate da GameActivity, ma NON runtime GameActivity live. Energia non ancora in SensoryFrame V315; G3/G5 e integrazione device bloccati.

> **V3.23 — CONTINUITÀ METABOLICA LAB PASS (17/10/2026):** [protocollo/CI/risultati](ZERO30_V323_SURVIVAL_CONTINUITY.md), [Actions 39025228383](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39025228383), Android Debug PASS. 20 vite fisiche, ognuna 24+24→restart JVM→48 vs 96 ininterrotte, unico SHA256 che riunisce archivio V322B (brain+body+world/RNG fixture+PRE/ACTION/POST) con riserva e 96 trace costi/energia; veri contatti +22 raccolta, −28 in 4 contatti pericolo, cambio fame a 70s, costo 8×gel.work, fault recovery 48. **Solo adapter laboratorio**, manca collegamento live GameActivity/metabolismo/scheduler, V315.energyAvailable resta false, nessuna nuova skill di sopravvivenza. G3/G5/gates Android non chiusi, nessuna OTA.

> **ZERO 3.22/B (17 ottobre 2026):** [Rapporto verificato](ZERO30_V322B_FULL_CAUSAL_REPLAY.md), [CI 39023478559 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39023478559). Superate 20/20 verifiche di ripresa della stessa vita in cinque scene di laboratorio, con archivio unico del cervello, corpo, RNG degli eventi fisici del test e esperienze complete PRE/12×64 comandi/POST. Corruzione e rollback superati; Android Debug compila. È un sottogate di persistenza in laboratorio, non la soluzione finale G3/G5, né il salvataggio completo dell'app Android o la nuova UI. Non modificare OTA172 o i 12 slot 2.2.

> **V3.22/A CHECKPOINT CAUSALE STESSA VITA — PASS LIMITATO (17/10/2026):** [report e limiti](ZERO30_V322_SAME_LIFE_SNAPSHOT.md) · [CI finale 39022300684 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39022300684). Quattro JVM separate: 24+24 decisioni/2 generazioni; ripresa dalla stessa vita su nuova JVM a 48 e prosecuzione fino a 96; confronto bitwise di neuroni+cortex+presa, gel/96 shell+74 inner, BOX/timer, actuator tape, ID causali, authority; 1.288 step gel e real grip 1.108, bilateral 8, Android Debug PASS. Fail closed se due gen corrotte, no doppio credito. **E2/E3-proto persistence ONLY**: mancano replay V316 completo nel payload, RNG world live, energia/danni/altre scene, migrazione legacy, QA Android reale. **G3/G4/G5/G6/G7/G8/G9/G10 non chiusi**; prossima V3.22/B, nessuna OTA/merge, mantenere 12 slot 2.2 intatti.


> **V3.21 | GATE TECNICO SNAPSHOT 3 MODULI PASS (17/10/2026):** [protocollo e risultati](ZERO30_V321_FULL_NEURAL_BUNDLE.md) · [CI 39020043151 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39020043151). `Zero3NeuralBundleStoreV321` rende atomico UN file completo di encoder recurrent+actor, cortex adattiva 64 canali (stato lento, RNG, baselines, revisioni) e head adesivo V3.19: training 768+restart JVM+768 vs 1536 decisioni senza restart, impronta binaria/metriche fisiche identiche, 1536 updates per modulo; EVAL 512 scelte, 1532 frame grip veri, **0 frame bilaterali**; rollback SHA256 a 768 e fail-closed. NON è completo `BrainSnapshotV3`: mancano mondo/body/sensori/replay+episodio/Authority coordinati, energia/danno, skills/neurogenesi e Android live. G3/G5 blocked, PR Draft, nessun merge/OTA.

> **GATE HEAD CHECKPOINT V3.20 PASS / 17-10-2026:** [protocolli e dati confermati](ZERO30_V320_MOTOR_HEAD_RESTART_RETENTION.md), [CI 39018773241](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39018773241). `Zero3MotorHeadStoreV320`: serializzazione integrale del solo motor head 16×8, 64-bit RNG, decisioni, credit/optimizer accumulators; doppio commit atomico/SHA-256, rollback fail closed. Cross-process 1536+1536 decisioni fisiche vs 3072 uninterrupted con full-state SHA256 identica, EVAL matched 24 vite e metriche bitwise identiche, 4778 frame presa legale. **NON è ancora il contratto completo BrainSnapshotV3** con actor ricorrente, altri 48 muscoli, corporeità, mondo, salvataggio multi-componente, e retry in mezzo alla vita; né rete completa, neurogenesi o UI. G3/G5 BLOCKED, PR Draft, no OTA.

> **V3.19 — GATE ADESIONE CONTESTUALE IN LAB PASS / 17-10-2026:** [preregistrazione e rapporto verificato](ZERO30_V319_CONTEXTUAL_GRIP_ENERGY.md), [CI 39017391971](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39017391971): 3.072 intenzioni TRAIN, BLIND 2.304 per braccio (V3.19 learned, V3.18 incumbent, V3.18 untrained), vero gel/box/disk. `Zero3ContextAdhesionV319`: motoneurone addestrabile 16×8, costo localizzato **formula esatta `SymbioteMatterV1.work`**, nessuna manipolazione di `gripHold` o vista futura; 6.900 frame grip BOX per V3.19 e incumbent, 6/6 vite, duty fuori BOX **3,09% vs 45,11%**, lavoro fisico BOX **0,20018 vs 0,25543**, globale **0,54858 vs 0,85676**. **CRITICO:** 382 frame bilaterali contro 1.617 incumbent. Il gate PASS riguarda selettività+efficienza di grip generico, NON locomozione G3, manipolazione, vera memoria/retention o G5. Nuovo passo: `BrainSnapshotV3` completo con optimizer/seed e verifica resume, poi prove specifiche grip bilaterale/rilascio e controllo del corpo. NO OTA/merge.

> **V3.18 — HEAD MOTORIO ADESIVO FISICAMENTE EFFICACE MA NON SELETTIVO (17/10/2026):** [protocollo e risultati](ZERO30_V318_LEARNED_ADHESION.md), [CI 39016675337](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39016675337): **technical SUCCESS / scientificGripGate=FAIL**. Solo motor ch2 neurale trainato (16×6): sigmoid(logit+esplorazione solo TRAIN), credito di `gripHold` proprietario box, real `SquareGripPuzzle`; altri 48 + actor frozen, nessuna alterazione dei veri sensori/ticks. BLIND 1.536×2 decisioni: appreso 4.600 frame `gripHold` e 2.021 bilateral (frozen 0); ma FREE+DISK+FAR duty adesione 45,58% contro max 30%, e BOX work 0,171031921 vs 0,108989737 (+56,9%) contro max +20. Distinguere **padronanza del canale fisico** da **abilità utile e selettiva**. Il costo TRAIN è un proxy della richiesta comandata, mentre `gel.work` EVAL è lavoro vero. V3.19 dovrà usare nuove scene/seed e credito contestuale/costo fisico reale. G3/G5 BLOCKED, niente merge/OTA/UI live.

> **V3.18 — TEST DI CAUSALITÀ DEL MOTOR HEAD ADESIVO (17/10/2026):** [piano sperimentale fissato prima di CI](ZERO30_V318_LEARNED_ADHESION.md). Codice PR #87 nuovo `Zero3LearnedAdhesionV318` con 16 motoneuroni×6 feature fisiche e logit trainabili; azioni sotto sua esclusiva responsabilità sul solo canale 2, gli altri 48/actor sono frozen; 3.072 scelte TRAIN, BLIND 1.536 per braccio. Ricompensa motoria locale valida SOLO con autentico `gripHold` e costo fisico, niente premio al semplice threshold/touch, né output manuale forzato; reale `SquareGripPuzzle` non modificato. Gate scientifico calibrato e split preregistrati nel documento; attendere log CI prima di promuovere. G3/G5 bloccati, zero OTA.

> **V3.17 — CONTRATTI SCELTA/PERCEZIONE/CREDITO IN CICLO REALE, SCIENTIFIC GATE FAIL (17/10/2026):** [ADR e dati reali](ZERO30_V317_AUTONOMOUS_MOTOR_LOOP.md) · [CI 39015868260](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39015868260). Branch PR #87 contiene actor sensorimotor ricorrente 16-unità/20 feature con intento unico V3.16, output volontario in 64 comandi per veri 12 solver steps; 2.048 scelte+updates TRAIN, 768 BLIND per ramo; pesi veramente cambiati. **Ma 1/4 scene migliora, allineamento addestrato −0,01577877 vs frozen −0,00179231, predizione 0,008395506 peggiore di persistenza 0,000139328; `G3` NON superato.** Diagnosi: `cortex[ch=2]` non supera mai `GRIP_THRESHOLD=.28`, zero prese, pur con contatti autentici. Architettura solo E3-proto, non critic/goal completo, non GRU vera, non recovery del brain o UI. Prossimo V3.18 su nuovi seed/scene: readout adesione trainabile, credito motorio sotto vero grip, confronto vs frozen; nessun hardcoded `gripCommand`. APK/OTA e 12 salvataggi invariati.

> **V3.16 — STATO CONTRATTI MOTOR INTENT / REPLAY (17/10/2026):** [rapporto e scenari di corruzione preregistrati](ZERO30_V316_INTENT_PERSISTENCE.md) · [CI 39015142772](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39015142772) **SUCCESS e GATE E2 INFRA PASS**. Creati e verificati in isolamento `Zero3MotorIntentV316` (una intenzione/decisione, autorità unica per episodio *nel processo*) e `Zero3ExperienceJournalV316` (snapshot 96×12/16 raggi/74×7 fluido/48 propriocezione e 64D efferenze PRE+POST reali, 2 gen SHA-256/atomic move, manifest rollback), test **2.048 esperienze fisiche da 3.584 step**, 4 checkpoint bitwise, ripristino 1.536 dopo ultima gen corrotta anche col manifest danneggiato, recupero/recommit senza sovrascrivere backup valido. NON sono presenti ownership persistente cross-restart, vero `BrainSnapshotV3`, `MotorIntent` scelto dall'actor, training/retention, energia/danno gameplay o UI V3. **Non dichiarare G3/G5 validati, non fare APK/OTA.**

> **V3.15 — IMPLEMENTAZIONE CONCRETA DEL PRIMO `SensoryFrameV3` E `ExperienceV3` SPERIMENTALI (17/10/2026):** [protocollo e risultati](ZERO30_V315_SENSORY_CAUSAL_CONTRACT.md) · [CI 38989788378 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38989788378). **Gate E2 infrastrutturale PASS** su 16 episodi/2.048 decisioni realmente eseguite (3.584 step del gel), 96 patch×12 sensori, 16 raggi, 74×7 interocezione, 48 propriocezione, 64 efferenze; snapshot profondamente copiato, `gel.ticks`, begin/commit una volta, test falsi step e duplicati rifiutati. **Attenzione**: è recorder IN RAM e decisioni pilotate dal laboratorio, non apprendimento autonomo; mancano `MotorIntentV3`/owner, feed energia/danno vero, snapshot completo per gameplay, persistenza atomica/recovery, recurrent encoder, skills, crescita neuroni e UI vera. G3/G5 non superati; PR sperimentale Draft, OTA172 invariata, 12 profili storici preservati.

> **V3.14 — AUDIT VISIVO E SEQUENZA CAUSALE (17/10/2026):** [protocollo V3.14](ZERO30_MOTOR_V314_PREACTION_VISION.md) · [CI sperimentale](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988832620). 16 raggi anatomici dal vero sensore ottico + 25 osservazioni corpo/tatto + 64 comandi per-frame; problema separato *first-contact onset* vs *response to ongoing contact*. Vedere la differenza sensore-readout E2 vs **contratto SensoryFrameV3 completo** che resta da implementare con schema, time stamps, local 96xN, provenance, immutable snapshots. **[CI 38988832620](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988832620) SUCCESS tecnico / CAL FAIL / BLIND FAIL scientifici.** Report: [V3.14](ZERO30_MOTOR_V314_PREACTION_VISION.md). BLIND FULL XY1 0,000201097 vs ballistica 0,000191035, evento primo contatto 52, Brier onset FULL 0,009176898 vs NO_VISION 0,008148665; path48 FULL 0,133198 vs migliore baseline 0,156992. **Prossimo: implementare tipizzazione reale `SensoryFrameV3` + replay frameId/timestamp/provenance causali prima di nuovo world model**, poi E3 con decisioni realmente autonome, non iterare regressori senza nuovo contratto. G3/G5 bloccati, nessun update OTA.

> **V3.13 — PROVA MOTOR-TACTILE E2 PREREGISTRATA (17/10/2026):** [ADR e protocollo](ZERO30_MOTOR_V313_CONTACT_ACTION_LEARNING.md) · [CI](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988290751). Dati causali da solver di corpo e mondo reali, segnale sensoriale a 25 dimensioni (con `touchPulse`), azioni muscolari autentiche, ablation e pose/scene separate. **CI 38988290751 SUCCESS tecnico; CAL=FAIL e BLIND=FAIL scientifici** dopo 76.800 step fisici reali e vere prese (CAL 24/120, BLIND 16/80). BLIND XY FULL MSE 0,000126998 vs ballistica 0,000125836; pre-step contatto FULL 0,000009129 vs ballistica 0,000006829; NO_TOUCH batte FULL. Migliora stato corporeo e alcuni rollout, ma non l'effetto tattile sul prossimo spostamento. [Numeri e decisione](ZERO30_MOTOR_V313_CONTACT_ACTION_LEARNING.md). Auditare vista/prossimità e `SensoryFrameV3` pre-azione, senza leggi oggetto privilegiate, poi nuovi ambienti e E3; G3/G5 bloccati, OTA172 invariata.

> **V3.12 — NUOVO FATTO VALIDATO (17/10/2026):** [collisori reali per scatola/disco, adesione fisica e separazione sensori](ZERO30_MOTOR_V312_REAL_CONTACT_FIXTURE.md), [CI 38987814698](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38987814698). **Gate di copertura E2 PASS:** 44/48 prove con tocco scatola, 21/48 con presa, 15/48 bilaterali, 13/16 con contatto disco, controlli negativi puliti, zero false prese senza comando. **ATTENZIONE:** `touchPulse` del disco NON è `touchSense` della scatola: V3.11 ignora il primo. Prossima prova E2: dataset sulle collisioni vere + azioni/stati/outcome e ablation, prima dell'E3 autonomo. Non si promuovono G3/G5, UI 3.0 o OTA.

> **NUOVA PROVA V3.11 (17/10/2026):** [stato e risposta in coordinate corporee, con presa/contatto reali](ZERO30_MOTOR_V311_BODY_FRAME_DYNAMICS.md), [CI](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986991649). Branch sperimentale PR #87, zero integrazione gameplay. **[CI 38986991649](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986991649) SUCCESS tecnico, modello V3.11 migliora la predizione libera in CAL/BLIND, ma gate preregistrato COMPLETO FAIL:** contactFrames=0/gripFrames=0 rendono NO_GRIP_TOUCH identico. Sul BLIND one-step XY 0,000000335 vs 0,000000511 ballistica, traiettoria 96 frame 0,023664 vs 0,082884 immobile. [Risultati e vincolo](ZERO30_MOTOR_V311_BODY_FRAME_DYNAMICS.md). G3/G5 bloccati; richiede fixture con vere collisioni prima di ulteriore claim. OTA172 invariata.

> **V3.10 — PR #87, ESPERIMENTO PREREGISTRATO:** [16 settori e comandi realmente eseguiti ad ogni frame](ZERO30_MOTOR_V310_LOCAL_FRAME_DYNAMICS.md); [CI isolata](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986306415). [CI 38986306415 SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986306415), **CAL FAIL e BLIND FAIL**: FULL MSE XY frame 0,000001303 vs ballistica 0,000000519 su BLIND; 96-frame 2,813636 vs zero 0,085789. Ablation NO_LOCAL 0,000000490 frame, 0,095154 percorso 96 frame, ancora non supera il gate. [Rapporto completo](ZERO30_MOTOR_V310_LOCAL_FRAME_DYNAMICS.md). G3/G5 bloccati, OTA172 invariata.

> **V3.9 — STATO TRANSITORIO (17/10/2026):** [Protocollo preregistrato state(t)+64 comandi eseguiti→state(t+1)](ZERO30_MOTOR_V39_OBSERVED_STATE_ACTION.md). Modello e CI isolati nel ramo PR #87, [CI 38985522666](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38985522666) SUCCESS tecnico su 491.520 step fisici, ma **gate scientifico CAL=FAIL, BLIND=FAIL**: one-step XY BLIND 0,002513731 vs ballistic 0,002582060, rollout 32 blocchi 3,116409 vs zero 0,837473. Dettagli nel rapporto V3.9; modello rifiutato per controllo. G3/G5 bloccati, nessun OTA.

> **V3.8-B APPRENDIMENTO DINAMICO VERIFICATO (17/10/2026):** [Rapporto e test su 983.040 passi fisici](ZERO30_MOTOR_V38B_STATE_TRANSITIONS.md). Il modello autoregressivo a 32 transizioni **FALLISCE** il gate: errore finale su nuovi stati **0,16007** vs **0,02906** del modello statico. ZERO 3.0 NON ATTIVO, G3/G5 non superati, nessuna nuova OTA.


> **V3.8 VERIFICATA, GATE FALLITO:** [modello ricorrente delle traiettorie, test 983.040 frame fisici](ZERO30_MOTOR_V38_RECURRENT_TRAJECTORY.md). La ricorrenza ha MSE ritardata blind **0,033926** contro baseline zero **0,029724** e time-only **0,029652**; previsione meno affidabile. Nessuna locomozione appresa né OTA 3.0: **G3 BLOCCATO**. [GitHub Actions verificata](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38983664233).


> **ULTIMO ESPERIMENTO MOTORIO V3.7:** [Memoria delle 96 contrazioni pre-azione, test 384-frame](ZERO30_MOTOR_V37_TEMPORAL_MEMORY.md): codice e CI corretti, ma **gate di affidabilità FALLITO** sulle 256 prove non viste, miglioramento della memoria su modello statico solo **0,019%**. [V3.7-B: nuove pose con memoria normalizzata, GATE FALLITO](ZERO30_MOTOR_V37B_TRAIN_SCALE.md) **verificata su 256 nuove prove**: MSE ritardata 0,035384825 contro 0,033472508 della baseline zero; **NON supera il gate**. **ZERO 3.0 NON ATTIVO, G3 BLOCCATO, OTA172 INVARIATA.**


> **ESITO V3.6 VERIFICATO:** [Predizione muscolare a 384 frame su 1.024 coppie fisiche](ZERO30_MOTOR_V36_LONG_HORIZON.md): il modello 52 sensori migliora la previsione ma **non supera il gate di generalizzazione del 10% sui risultati ritardati** (miglioramento 7,28%). Nessun nuovo cervello 3.0 attivo, **G3 BLOCCATO e nessun OTA**.


> **ESITO V3.5 VERIFICATO:** [32 test a ciclo chiuso](ZERO30_MOTOR_V35_ADAPTIVE_CREDIT.md) — astensione adattiva supera leggermente lo spostamento aggregato senza impulsi, ma **consuma di più e migliora soltanto 6/16 condizioni nuove**. **G3 NON SUPERATO, ZERO 3.0 NON ATTIVO E NESSUNA OTA.**


> **STATO SPERIMENTALE V3.5:** [Finestra di credito fisico 384 frame + test di astensione dopo feedback reale](ZERO30_MOTOR_V35_ADAPTIVE_CREDIT.md). Il sistema **non è attivo in gameplay** e G3 resta BLOCCATO: i test positivi sugli impulsi isolati non valgono come locomozione autonoma.


> **ULTIMO STATO MOTOR V3.4 (17/10/2026):** [Corpi deformati + kernel condiviso + fallimento a ciclo chiuso (4/16)](ZERO30_MOTOR_V34_DYNAMIC_STATES.md). Le previsioni degli impulsi isolati sono attendibili, **MA il modello guidato a ogni decisione peggiora locomozione e costo**. Sono attive nuove prove sul ritardo cumulativo. G3 e nuova UI V3 NON completati; niente OTA.


> **RISULTATO MOTORIO 3.3 (17/10/2026):** [Confronto fisico dei comandi, attribuzione dell'effetto ritardato, test su sei posture escluse dall'allenamento](ZERO30_MOTOR_V33_DELAYED_RESPONSE.md). **24/24 piccoli interventi riusciti nel laboratorio**, previsione migliore di baseline; **NON dimostra locomozione autonoma o G3 completato**. Code branch sperimentale, nessuna nuova OTA.


> **NUOVO ESPERIMENTO MOTORIO V3.2:** [Ricerca muscolare con vincoli energetici e verifica su posture escluse dall'allenamento](ZERO30_MOTOR_V32_COST_CONSTRAINED.md). Restano prototipo isolato e gate G3 bloccato finché non arrivano prove indipendenti.

> **TEST 2.000 ESPERIENZE VERIFICATI:** [Report completo motorio e autonomo, checkpoint 400/1000/2000](ZERO30_LONG_HORIZON_2000_20261017.md). **G3 ANCORA BLOCCATO**: a 2.000 decisioni la corteccia non supera il vergine in nessuna delle 4 direzioni nella postura iniziale; misure coerenti ma negative. Nessun OTA V3.

> **G3 MOTOR V3.1 — AGGIORNAMENTO:** [Progetto degli esperimenti, perturbazioni strutturate, benchmark 4 direzioni e gate di promozione](ZERO30_MOTOR_V31_ENGINEERING.md). L'ultimo controllo a 400 decisioni ha rifiutato la promozione del learner online: ancora nessuna evidenza sufficiente di locomozione appresa.

> **STATO IMPLEMENTAZIONE AGGIORNATO:** [Primo prototipo ZERO3 + risultati CI e limiti (16/10/2026)](ZERO30_BUILD_STATUS_20261016.md). Leggere questo handoff prima di riprendere da altre chat. ZERO3 NON È ANCORA ATTIVO NEL GIOCO O PUBBLICATO.

**OBBLIGATORIO** insieme a [MASTER BLUEPRINT](ZERO30_MASTER_BLUEPRINT.md) e [SPECIFICA UI/UX](ZERO30_UI_UX_SPEC.md). Aggiornare questo documento durante l'implementazione, mai spacciarlo per report di test eseguiti.

**Stato:** SPECIFICA v1.0, 16 ottobre 2026. **Produzione V3: NON ATTIVA.** Sono presenti prototipi isolati per G2-G4 sul ramo sperimentale; **G3 APPRENDIMENTO MOTORIO NON SUPERATO** (vedi MOTOR V3.1). La nuova UI, persistenza V3 e rilascio restano non implementati.

## 0. Prima operazione di ogni ChatGPT / agente

1. Aprire, leggere per intero e poter riassumere MASTER BLUEPRINT, questa checklist e UI/UX spec **da main** (non da un ramo vecchio).
2. Leggere AGENTS.md, OVERMIND_START_HERE.md e audit specifici della parte da cambiare. Controllare il ramo da cui viene generata l'OTA. Al 16/10/2026 branch sorgente \`feature/zero22-active-recurrent-ota117\` e release stabile su main manifest OTA172.
3. Consultare il codice effettivo delle classi dipendenti; non assumere che la proposta esista già. \`app/src/main/java/com/overmind/zero/v3\` è percorso PROPOSTO.
4. Dichiarare fase in corso, file toccati, comportamento invariato, prove richieste, criteri per rollback; conservare almeno un checkpoint legacy.
5. Lavorare in branch dedicato; evitare push non richiesti al ramo che **attiva automaticamente l'OTA publisher**. La sola documentazione su main non deve cambiare manifest firmato, sorgenti o OTA.
6. Ogni PR: lista dei test realmente eseguiti, output PASS/FAIL, versioni, QA Android e dati veri; mai lasciare una modifica "completa" perché compila. L'utente testa sul proprio Galaxy S21 quando serve.

**Regola di handoff:** aprendo una nuova chat, la nuova istanza deve poter proseguire da questi file e dallo stato GitHub/CI, senza ricostruire requisiti ricordati solo in una chat. Ogni PR deve aggiornare la sezione progresso e le decisioni tecniche.

## 1. Mappa del progetto e del repository

| Oggetto | Percorso | Fonte autorevole |
|---|---|---|
| Specifica architetturale | \`docs/ZERO30_MASTER_BLUEPRINT.md\` | Requisiti obbligatori |
| Contratto operativo | Questo file | Implementation gates, API, test |
| Nuova interfaccia | \`docs/ZERO30_UI_UX_SPEC.md\` | UI ZERO 3.0, provider reali |
| Istruzioni GitHub ChatGPT | \`AGENTS.md\`, \`OVERMIND_START_HERE.md\` | Leggere questi tre file PRIMA |
| Stato OTA effettivo | \`updater/public/latest.json\` su main | Versione Android pubblicata |
| Telemetria | \`zero-telemetry/current.json\` su \`zero-telemetry\` | Verificare OTA e timestamp del report |
| Audit passato | \`docs/ZERO_172_PHYSICAL_COMMAND_TIMING.md\`, \`docs/ZERO_OTA153_FULL_SENSORIMOTOR_FORENSIC_AUDIT.md\`, \`docs/ZERO_OTA161_BODY_PHYSICS_AUDIT.md\`, \`docs/ZERO_OTA162_MOTOR_COORDINATION_AUDIT.md\` | Background empirico |
| Android reale | Sorgente sul branch stabile/OTA | Ispezionare ogni volta, non dedurre da main |

**Governo delle discrepanze:** il *progetto desiderato* è nel master; il *comportamento attuale* è nel codice del ramo di rilascio; il *progresso* è in questo contratto; il *risultato percepito* richiede telemetria/device. Non confondere queste quattro fonti.

## 2. Contratti dati e tipi (da definire in Java, testabili senza Android)

### SensoryFrameV3

Campi obbligatori: schema, episodeId, simulationTime, frameId, input sensoriale globale normalizzato, matrice sensoriale locale 96xN con maschere validità, 16 regioni con orientamento dinamico world-space, delta velocità e segnali grip, energia/danno, ultimi comandi applicati, tracciabilità sensori. Immutabile all'uscita dall'adapter. Garantire assenza di privileged target data.

Prove: 96 siti distinti, corretto segno world/body, feature finite, non aliasing, snapshot coerenti, dinamiche su spostamento/rotazione.

### MotorIntentV3

\`{episodeId, decisionId, controllerRevision, directionX, directionY, desiredEffort, adhesionPreference, intendedDurationSec, confidence}\`.
- Se vector non nullo normalizzazione esplicita; range finite e unità documentate.
- Intentione non è forza/push esterno, neppure reward.
- IntentId creato una volta dal decisore; planner non può duplicare scelta.

### ActuatorFrameV3

\`{decisionId, physicsFrameId, motorRevision, outputs[64], perturbationId, randomSeed, stateHash}\`.
- 16 settori × 4 canali nell'ordine esatto del solver; nessuna attrazione/pull verso oggetto.
- Output finite, clamped in intervalli fisicamente accettati.
- \`requested\` e \`executed\` registrati separatamente quando saturazioni o vincoli cambiano il comando.

### PhysicsOutcomeV3

\`{decisionId, physicsFrameStart, physicsFrameEnd, elapsedSimSeconds, truePostCollisionBody, impulseBeforeSolver, postSolverDelta, contacts, gripState, disturbanceTypes, passiveMomentumEstimate, energyGained, energySpent, damage, terminal, aborted}\`.
Tutte le misure devono provenire dal solver reale, non da valori dell'intenzione.

### ExperienceV3

Oggetto persistibile che lega esattamente **una intenzione, una o più azioni fisiche e un esito** con la corretta revisione cortex/cervello. \`train / heldout / censored\` è un attributo generato prima del learning, irrevocabile nell'esperimento. Il recorder non può inventare reward, spostamento o tempo.

### BrainSnapshotV3

\`{schemaVersion, configHash, brainWeights, cortexWeights, optimizerState, normalizerState, growthGraph, replayManifest, proceduralMemory, counters, savedEpisodeContext, integrityHash}\`. Checkpoint atomico e reversibile, con sistema legacy intatto.

### V3DiagnosticSnapshot

Solo dati REALI dei componenti, immutabile/read-only, accompagnato da \`sourceModule\`, \`schemaVersion\`, \`observedAt\`, \`sampleCount\`, \`availability/validationState\`. Se non verificato: etichetta \`IN APPRENDIMENTO\`, \`NON VALIDATO\`, \`DATI NON DISPONIBILI\`, mai numeri inventati o sostituiti da contatori 2.2.

## 3. Proprietà che vanno dimostrate automaticamente

| ID | Invariante | Test iniziale |
|---|---|---|
| I-01 | Una sola politica emette MotorIntent per decisionId | Due policy stub devono fallire, no overwrite |
| I-02 | 64 comandi finiti e mappati ai 16x4 attuatori reali | Property/shape/range tests |
| I-03 | Zero heading / gait / gem position hardcoded | Static audit + ablation/hard scene |
| I-04 | Ogni outcome vive DOPO tutte le collisioni | Physics integration trace |
| I-05 | Numero scelto = azione applicata = azione accreditata oppure censura esplicita | Trace join |
| I-06 | Non si allena con validation o imaginary rollout | Hash pesi prima/dopo holdout |
| I-07 | Inerzia, contatto, vento, correzioni, grip non collassati in un solo veto | Matrix eventi causali |
| I-08 | Perdita abilità su morte/restore assente | Snapshot roundtrip test |
| I-09 | Accelerazioni x1/x5/x20/testMax conservano risultati a pari simTime/seed entro tolleranza | Determinism test |
| I-10 | Ogni update applica peso/gradient e traccia modifica effettiva | Delta norm, finite, no silent no-op |
| I-11 | Validazione modello contro baseline vera | Error diff heldout |
| I-12 | Ricordo abilità contestuale, rollback dopo regressione | Recall A/B and restore |
| I-13 | Nuovi neuroni = unità di calcolo + connessioni e beneficio misurabile | Neurogenesis integration |
| I-14 | Nessun reset silenzioso del 2.2 o dei 12 slot | Full-save before/after, schema migrate |
| I-15 | UI 3.0 usa provider 3.0 reali | No fake counters / provider contract |
| I-16 | Test e produzione non vanno confusi | Trace manifests + device reporting |

Ogni regressione precedente va mantenuta e non rimossa per far passare la nuova architettura.

## 4. Matrice delle verifiche del corpo

Prerogativa P0: **non iniziare sviluppo actor-critic se non si dimostra controllabilità fisica**. Modalità laboratoriale esterna al normale ZERO; niente manipolazioni invisibili.

| Scenario | Osservazione obbligatoria | Fallimento da identificare |
|---|---|---|
| Nessun comando | Deriva, invarianza e decadimento della velocità | Energia/iniezione spontanea |
| Un settore radiale | Deformazione vera locale, reazione distribuita | Spostamento fittizio del COM |
| Un settore tangenziale | Rotazione/traslazione attribuibile | Cancellazione eccessiva |
| Adesione su suolo reale | Trattamento diverso comando/contatto/hold | Grip finto e veto 'external' |
| Adesione assente | Libera deformazione senza trazione prescritta | Motion hardcoded |
| Pseudopodio | Estensione + retrazione + contatto | Membrana incastrata |
| Corpo concavo/ruotato | Direzioni effettive del tessuto | Birth angle alias |
| Muro/box/pannello | Collisione + solver correction registrati | Causal label errata |
| Vento/tempesta | Separabilità disturbo | False skill proof |
| Comandi temporali | Almeno una sequenza con netto significativo | Corporeal ungovernability |
| Stesso seed | Evoluzione deterministica | Race/temporal alias |
| FPS accelerato | Equivalenza in tempo simulato | Missed frame/dup training |

Il confronto di dinamica passiva serve a isolare contributi causali; la trazione da contatto reale non deve essere scartata solo perché c'è touch.

## 5. Come si prova l'efficacia dell'apprendimento

### 5.1 Misura corretta, non conteggio grezzo

Un update con \`trainSteps++\` non significa miglioramento: registrare \`L2(weightsAfter-weightsBefore)\`, \`gradientNorm\`, campioni abilitanti e qualità su heldout. Quota di esperienza train e holdout iniziale 80/20, con **scene e seed esclusi completamente** dalla scelta parametri.

Nessuna fiducia globale obbligatoria prima di poter allenare. La confidence dei singoli output controlla **l'uso nella pianificazione**, non interrompe i gradienti.

### 5.2 Misure motorie

- \`netDisplacement\`, \`grossDistance\`, \`netToGross\`, \`intentAgreement\`, \`motorWork\`, \`energyPerUsefulDistance\`, \`timeStalled\`;
- progresso da vergine dopo 400/2k/5k/10k esperienze;
- prove a matched seeds, stessi ambienti e ablation: no cortex learning, no shared recurrence, no memory, no motor exploration;
- retention: death, save, restart, load, nuova scena, diverso orientamento del corpo;
- generazione skill candidate/holdout/champion, successful context-based reapplication.

### 5.3 Misure cognitive

- prediction error e baseline **per testa**: percezione/movimento/contatti/energia/danno;
- variabilità/in-calibration uncertainty, failure di rollout;
- goal intention survival, tempo senza cambio incoerente; raccolta blu real contact; danni evitati;
- actor/critic vs random actions e V2 su **stesso corpo**, se integrabile;
- planner vs no-planner matched-benchmark senza dati leak;
- no reward hacking (altre metriche esterne: energia reale, durata, distanza, contatti).

### 5.4 Livelli di evidenza

- **E0 Spec**: questo documento; NON codice.
- **E1 Unit**: classi pure, forme, invarianti, save/restore.
- **E2 Physics Laboratory**: solver vero guidato da laboratorio; mostra possibilità fisica, non autonomia.
- **E3 Autonomous Simulation**: cervello vergine allenato, no percorsi scripted; A/B multiseme.
- **E4 Android device**: Galaxy S21, prestazioni, telemetry, vita e apprendimento reali.
- **E5 Signed OTA**: verified build/signature/package/updater live, **non implica** performance E4.

Ogni messaggio di rilascio deve indicare il livello realmente raggiunto. Mai chiamare E2/E5 "prova che ZERO impara a camminare".

## 6. Roadmap e gate (da aggiornare, non barrare senza prova)

- [ ] **G0 — Baseline**: snapshot immutabile OTA172, slot, telemetria, test e readme; audit main/source branch. *Gate*: baseline riproducibile.
- [ ] **G1 — Fisica**: controllabilità, motion causale, comparazione fisica old/symbiote. *Gate*: locomozione ottenibile con 64 outputs normali.
- [ ] **G2 — Contratti**: SensoryFrame/Intent/Actuator/Outcome/Experience + coordinator e test anti-leak. *Gate*: trace univoca e no duplicazioni.
- [ ] **G3 — Corteccia**: recurrence veloce allenabile + perturbazioni temporali + motor metric + skill archive. *Gate*: motor learning autonomo meglio di untrained su almeno scene note e nuove.
- [ ] **G4 — Cognizione**: encoder + recurrent state + actor critic. *Gate*: stabile numericamente, goal/value/test.
- [ ] **G5 — World model**: previsioni/ensemble/uncertainty + bounded planner. *Gate*: error better than relevant baselines e beneficial ablations.
- [ ] **G6 — Memoria/goal**: episodio, skill, opzioni temporali, stimoli appresi. *Gate*: raccolta e retention superiori al caso.
- [ ] **G7 — Plasticità evolutiva/persistenza**: growth, optimizer, migration. *Gate*: growth validated, legacy intact.
- [ ] **G8 — Nuova UI ZERO3**: nuovo sistema di schermate e provider, menu 2.2 sostituiti per profili v3. *Gate*: audit navigation/readout/3D atlas/actual skills.
- [ ] **G9 — Integrazione/pre-release**: Android CI, <Galaxy S21>, 20+ seeds, offline/x1/x5/x20/testMax, full save regression. *Gate*: criteri documentati raggiunti.
- [ ] **G10 — Signed OTA**: solo dopo autorizzazione e prove, same signature/appId, verified manifest/live APK. *Gate*: updater Android e rollback preservati.

I limiti proposti (64 GRU, 48 motori, 80/20 holdout, 4.096 replay, +32 neuroni, 20 semi) sono **punti di partenza**. Se il test mostra di meglio, cambiare tramite Decision Record, non per intuizione.

## 7. Moduli software e responsabilità

\`\`\`text
v3/core/ZeroBrainV3.java             // only MotorIntent owner
v3/core/LearningCoordinator.java    // timeline, causal joins, train/holdout/trace
v3/perception/SensoryEncoder.java   // no privileged labels
v3/cognition/RecurrentState.java    // trainable temporal context
v3/cognition/WorldModel.java        // multihead predictions + uncertainty
v3/cognition/ValueNetwork.java      // two critics + targets
v3/cognition/IntentionPolicy.java   // continuous actor
v3/cognition/ShortHorizonPlanner.java // gated imagined advice only
v3/motor/AdaptiveMotorCortex.java   // 16x4 actuator commands
v3/motor/MotorLearning.java         // actual intervention credit
v3/memory/EpisodicMemory.java       // lived events and replay
v3/memory/ProceduralMemory.java     // skills proven by real A/B
v3/learning/ValidationManager.java  // immutable split
v3/learning/NeuralGrowth.java       // added genuine neurons
v3/persistence/BrainCheckpointV3.java // all state, rollback
v3/diagnostics/LearningHealth.java  // read-only snapshot
v3/ui/...                           // VIEW layer ONLY, see UI spec
\`\`\`

Package names are proposed. Keep core JVM-testable, Android adapter thin. No direct \`SharedPreferences\` read inside training code, no training as a side effect of UI reads, no central \`GameActivity\` giant method that implements multiple learning algorithms.

## 8. Nuova UI = deliverable reale, non optional

Leggere [ZERO30_UI_UX_SPEC.md](ZERO30_UI_UX_SPEC.md). Prima di dichiarare G8 completo verificare:
- nuova navigazione menu e impostazioni SPECIFICHE 3.0, sostituzione coerente di \`BrainStatsActivity\`, abilità, evoluzione, corpo e analisi per un cervello v3;
- ogni card/indicatore punta al provider reale, versione e disponibilità; nessuna etichetta ritoccata sopra il 2.2;
- Atlante 3D con vere unità e connessioni, contatori allineati a \`NeuralGrowth\`;
- corpo visibile con 96+74 massa/sensori, 16 regioni, 64 outputs, compass intention vs outcome;
- grafici learning, skills candidate/champion/reused, old-vs-new segregati;
- preservare AGGIORNA, slot, diagnostica dati condivisi, accessibilità e costi runtime;
- test di coerenza schermata -> modello -> checkpoint -> restore.

## 9. Protocollo PR e decision record

PR template testuale (descrizione minima):
\`\`\`text
ZERO3 Plan: docs/ZERO30_MASTER_BLUEPRINT.md §...
Phase/Gate:
Change scope:
Baseline/source branch:
Physics / reward / autonomous choice invariants:
Legacy saves/slots migration:
UI V3 impacts:
Tests unit/physics/autonomy/Android and actual output:
New data from device? OTA version:
Known limitations / risks:
Rollback:
ADR reference:
\`\`\`

ADR format:
\`\`\`text
YYYY-MM-DD / ADR-N
Decision:
Problem and evidence:
Options and non-chosen alternatives:
Impacted modules + UI:
Test hypotheses and acceptance:
Persistence + OTA risks:
Outcome and follow-up:
\`\`\`

## 10. Stato / handoff corrente

**Documento pubblicato nella fase di PROGETTAZIONE** (nessuna implementazione V3 alla data originale). Compilare progressivamente:

- **Ultima specifica:** v1.0 (16/10/2026), titolo ZERO 3.0.
- **Ultimo rilascio firmato osservato alla redazione:** OTA172 \`0.11.6-ota.172\`, Android versionCode1172.
- **Branch di sviluppo V3:** da creare soltanto dopo l'approvazione della costruzione, partendo da sorgente stabile, senza push accidentale al publisher OTA.
- **Gate attualmente superati:** nessuno del V3 (esistono test legacy ma non bastano).
- **Prossimo gate:** G0 baseline + G1 physics controllability.
- **Ultima telemetria analizzata:** OTA171 report del 16/10/2026; la validazione comportamento OTA172 richiede nuovi dati.
- **UI V3:** progettata in documento, non implementata.
- **Ottimizzazioni decise:** nessuna fuori da ipotesi specificate.
- **Bloccanti:** controllabilità fisica non ancora provata come percorso autonomo v3, valori ottimali non ancora misurati, salvataggio V3 non ancora implementato.


V3.20: [verified head-only restart](ZERO30_V320_MOTOR_HEAD_RESTART_RETENTION.md); G3/G5 blocked.

# OVERMIND ZERO — Esperimento 0097: preservare la locomozione acquisita

**Data:** 10 ottobre 2026. **APK firmata:** 0.9.7-ota.41, versionCode 1041. **Problema reale segnalato dal test sul Galaxy S21:** nella v0.9.4 ZERO viaggiava piuttosto rapidamente, mentre dalla 0.9.5 l'utente lo osserva spesso quasi fermo o muoversi molto più lentamente. La v0.9.6 conserva l'architettura sospetta. Non è stato dimostrato se la causa sia *soltanto* la memoria o anche l'evoluzione dei pesi/politica: non attribuire intenzioni o competenze sulla base di telemetria non confrontata.

## Diagnosi effettuata sul codice
Nelle versioni 0.9.5/0.9.6, `GameActivity` applicava in ogni frame:
- `lastAppliedMotor=episodic.weights(0,liveBody.weights(0,trainer.best));`
- `lastAppliedHand=episodic.weights(1,liveBody.weights(1,handTrainer.best));`
- `lastAppliedMembrane=episodic.weights(2,liveBody.weights(2,shapeTrainer.best));`

Se `SensorimotorMind` sceglieva un'azione compatibile con un ricordo, la memoria poteva sovrascrivere **tutti e tre** i controllori con pesi **vecchi** registrati durante un'altra postura: ZERO possedeva ancora i migliori pesi conservati, ma in quel momento poteva non usarli. Inoltre `if(actedFromMemory)liveBody.abortTrial()` sospendeva ogni confronto motorio anche quando ZERO ricordava soltanto un'azione e non aveva effettivamente sostituito nessun attuatore. Con ricordi frequenti questo poteva ridurre la crescita delle abilità. Non si tratta di una scelta deliberata della creatura, ma di un'interferenza implementativa plausibile.

## Intervento implementato nella v0.9.7
1. **Motore protetto per impostazione predefinita.** Il controllo motorio vivo usa SEMPRE `liveBody.weights(0,trainer.best)`: la configurazione motoria accettata (oppure il mutante temporaneo attualmente in prova). I ricordi non possono sostituire i 62 pesi del motore, nemmeno se ZERO riconosce una vecchia scena. La rete sensoriale può ancora scegliere nuove azioni; la memoria non decide il vettore di movimento.
2. **Richiamo corporeo soltanto durante contatto fisico.** Pesi vecchi di manipolazione (18) e membrana (82) possono essere testati temporaneamente soltanto quando `Morphology.Surface.touchSense` verifica contatto tattile reale e non è già in corso una valutazione di pesi mutati. Un mero ricordo visivo nel vuoto non modifica i controllori fisici. La validità della presa non è dichiarata senza collisioni osservate.
3. **Addestramento corporeo non più sospeso da un semplice ricordo.** Il confronto `OnlineBodyLearning` si interrompe esclusivamente se effettivamente sono stati applicati pesi storici di mano/membrana durante l'intervallo motorio; non si interrompe se la memoria ha semplicemente orientato la scelta dell'azione. Le mutazioni motorie restano selezionate tramite effetti reali, non allenamento offline con target.
4. **Controllo autonomo della stagnazione**, `MotorAutonomyGuard.java`: ogni ~7 secondi simulati misura la distanza NETTA del nucleo e lo spostamento concreto provocato nell'ambiente. Se per **due finestre consecutive** non rileva né spostamento netto significativo (16U) né effetti fisici (1,25U), sospende per 12 secondi simulati i **soli suggerimenti positivi della memoria**, lasciando ZERO libero di scegliere attraverso la propria rete neurale e l'esplorazione. Se progredisce ripristina anticipatamente l'accesso alla memoria. Eventi di manipolazione utile non vengono interpretati automaticamente come immobilità inutile. Le associazioni ai pericoli già apprese restano operative. Non vengono mai comandate direzioni “giuste” o forze preimpostate.
5. **Persistenza**: continuano inalterate le chiavi motore `best`, mano `hand_brain_v04`, membrana `morph_brain_v07`, modello del mondo `sensorimotor_model_v09` (11.664 pesi), memoria `episodic_memory_v095` (formato em1/em2), 24 ricordi/36 azioni, RESET selettivi. Nuovi contatori `motor_recoveries_v097`, `motor_free_choices_v097` e `physical_replays_v097`; nessun reset automatico.
6. **Telemetria:** ANALISI mostra quanti recuperi dall'immobilità sono stati attivati, quante decisioni sono state prese con ricordi positivi temporaneamente sospesi e quante prove con memoria fisica si sono svolte durante contatto tattile. Sono semplici diagnostiche, **non attestati di miglioramento**.

## Limiti e possibili failure mode
- Non è provato che i pesi motori principali fossero rimasti ottimali: l'algoritmo motorio può aver accettato una variante peggiorativa per confronto di finestre consecutive in contesti diversi. La v0.9.7 evita il rimpiazzo da parte della memoria e riduce l'interferenza sui trial, ma **non ricostruisce necessariamente il vecchio cervello migliore della 0.9.4** se esso è già stato sovrascritto.
- Un corpo può rimanere quasi fermo perché il policy network sceglie azioni non efficaci anche senza memoria. Il controllo antistallo disattiva i ricordi positivi temporaneamente ma non impone movimenti “veloci”.
- 16U di movimento netto in 7 secondi e 1,25U di effetto esterno sono **soglie ingegneristiche del recupero**, non segnali di successo/porta aperta passati come oracle alla rete.
- Durante una vera presa la creatura può rimanere ferma e muovere la porta: il guard misura **effetti fisici** per non interrompere un comportamento utile.
- Il richiamo hand/membrane non è ancora dimostrato come strategia fisica riproducibile; la memoria episodica è correlazionale, non pianificazione multistep/AGI.
- In luoghi simili ma fisicamente diversi i ricordi possono essere irrilevanti. La predizione `SensorimotorMind` resta a un passo; la nuova protezione non introduce planner a più passi.
- I contatori antistallo partono dalla 0.9.7: non sono dati retroattivi della 0.9.4.

## Test automatici obbligatori
Test JVM `MotorAutonomyGuard.main`: almeno due finestre prive di effetti attivano scelta libera; un reale spostamento disattiva il recupero; manipolazione effettiva senza locomozione non viene marcata come stallo; reset vita e conteggi persistenti. `EpisodicMemory.main` testa che i 62 pesi motori non vengano mai sostituiti, che la mano/membrana possano riprovare pesi solo con contatto, em1/em2, risultati energetici, pericoli e conservazione della memoria dopo la morte. `ResetDomains.main` esclude chiavi condivise fra domini. CI Android prova compilazione e invarianti.
Il controllo statico in staging verifica anche che il progetto **non abbia più** `if(actedFromMemory)liveBody.abortTrial()`. I test non sostituiscono una prova fisica sul dispositivo.

## Prova sul Galaxy S21
1. Aggiornare **sopra la versione precedente**, senza disinstallare e senza azzerare. I migliori pesi motori e le memorie del 0.9.6 rimangono.
2. Prima di stimare il miglioramento, fissare la velocità simulazione (x1 o x5, confrontare alla pari con 0.9.4/0.9.6). Lasciare evolvere 10–20 minuti e registrare **NETTO**, **VIA**, distanza massima, nuove zone, esperienza / tempo, motore accettate/scartate, recuperi.
3. Se ZERO non si muove, controllare ANALISI → Recuperi da immobilità e Decisioni libere dai ricordi. Se aumentano ma ZERO resta immobile, è possibile problema al policy network, alla morfologia o ai pesi motori, non semplicemente replay vecchi.
4. Durante un'interazione con porta, verificare che **Riprove fisiche con membrana in contatto** cresca solo quando c'è contatto; mai mentre si muove indisturbato nello spazio aperto.
5. Verificare continuità dei progressi dopo morte e riavvio; evitare di premere RESET al primo test.
6. Non dichiarare risolta la porta senza spostamento >115U, attraversamento del nucleo e ricompensa rare, in ordine.

## Per riprendere in un'altra chat
Repository: `nicogtr20-ctrl/idle-runner-releases`.
- Codice applicazione Android nel branch `feature/overmind-inapp-updater`, aggiornamenti e paper nel branch `main`. Branch staging v0.9.7: `fix/overmind-v097-motor-retention`.
- Leggere `OVERMIND_START_HERE.md`, `AGENTS.md`, questo documento, `docs/HANDOFF.md`, `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/ROADMAP.md`, `docs/CHANGELOG.md`.
- Verificare versione e SHA della APK **soltanto** tramite `updater/public/latest.json` e un GitHub Release asset esistente. Installare in place con la stessa firma e package `com.overmind.zero.ota`.
- Obiettivo prossimo dopo test S21: A/B motorio su stati di mondo identici e seed condivisi, rollback di controllori sotto-effettivi senza cancellazione indebita, modello causa-effetto multistep su esperienze **reali**.


### RILASCIO OTA 0.9.7-ota.41 — 10 ottobre 2026 (verificato)
- **GitHub Actions firmata 38007333273 = SUCCESS**, JVM/staging 38007100048 e 38007156421 = SUCCESS. Package `com.overmind.zero.ota`, **versionCode 1041**, peso **96.936 byte**, checksum SHA-256 `4de8ef5a9f22dd6e4caf503fc7543410a5e829cab5beb1e9b0a0bcdea3a9bf1d`.
- APK verificata nel repository `updater/public/apk/overmind-zero-1041-4de8ef5a9f22.apk`; manifest `updater/public/latest.json` (GitHub) contiene esattamente SHA, versionCode, versione e dimensioni. GitHub Release fallback `overmind-zero-v0.9.7-ota.41` dopo trigger apposito.
- **Nessun test di comportamento su Galaxy S21 della v0.9.7 ancora effettuato**. I pesi e le memorie esistenti restano installabili in place, ma recupero della velocità prima vista con 0.9.4 deve essere verificato, non garantito.

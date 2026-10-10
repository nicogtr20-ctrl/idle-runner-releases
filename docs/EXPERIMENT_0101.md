# OVERMIND ZERO — Esperimento 0101: attribuzione degli effetti alle azioni
**2026-10-10.** Correzione implementata su `feature/overmind-v101-outcome-attribution` per rilascio 0.10.1. Test automatici **verificano il codice**, non l'effettiva intelligenza di ZERO sul Samsung S21.

## Risultato osservato e domanda dell'utente
Dopo 5 minuti dall'avvio del **nuovo ZERO vergine** nella versione 0.10.0, ZERO è riuscito a **muovere fisicamente il cancello**, anche con piccoli spostamenti, ma si è poi apparentemente disinteressato. Domande:
- Se muove il cancello senza aprirlo o raccogliere energia, lo considera inutile oppure capisce che un piccolo progresso fisico è interessante?
- Se sta manipolando il cancello e tocca nello stesso momento una sfera rossa dannosa, riesce a distinguere l'effetto dell'ostacolo dalla perdita di energia?

## Bug concettuale diagnosticato in 0.10.0
Il codice originale assegnava a `SensorimotorMind.observe` un **reward scalare con energia osservata e fisica combinati** (`deltaEnergy` + `physicalEffect` con TD learning), mentre `EpisodicMemory.consequence` utilizzava `positivePhysical+.25*energyGain-.60*energyLoss` in **un solo episodio**. Inoltre `ExperienceCredit` retropropagava la conseguenza energetica a fino a 12 azioni precedenti, indipendentemente da quale frequenza fosse stata vista. Questa correlazione poteva erroneamente sfavorire la presa sul cancello se nello stesso intervallo avveniva un danno rosso. **Non esisteva un vero sistema generale di inferenza causale**, solo apprendimento da conseguenze correlate temporalmente.

## Correzioni del software 0.10.1

### 1. Due stimatori di valore indipendenti (ExperienceCredit)
- Canale **energetico** `energetic[16*(26+1)]`, aggiornato solo dopo contatto reale con cambiamento positivo o negativo della riserva. Il contatto trasmette l'identità della **frequenza fisicamente osservata** (`object.signature`), non `object.physicalLaw()`, destinazione o classe food/danger. L'algoritmo aggiorna soltanto le quattro componenti dei sensori visivi corrispondenti a quella frequenza **che sono state effettivamente percepite** fra le ultime sei decisioni; se la frequenza non è visibile, si registra ambiguità ma **non** un'associazione inventata. Pesi [-2.5,+2.5]; contatori positivi, negativi, fonte visiva associata, eventi ambigui.
- Canale **meccanico** `mechanical[16*(26+1)]`, aggiornato quando la **fisica degli urti/cancello** misura spostamento effettivo, attribuendolo alle ultime cinque azioni realmente compiute nelle condizioni tattile/prossimità (sensori 3..13). Pesi [0,+2.5]. Nessuna scorciatoia che riconosca “cancello da aprire” o una direzione migliore. Lo spostamento è esperienza positiva di interazione fisica, ma NON una previsione automatica di maggiore energia futura.
- I due canali restano **separati durante l'apprendimento**. Soltanto nella valutazione di una possibile azione vengono sommati i valori appresi del contesto sensoriale. ZERO può continuare a ignorare una sfera o abbandonare il cancello.
- Il modello generale `wm1`, 26 sensi, 16 azioni, Q TD e predittore delle percezioni **restano nel vecchio formato**; nel reward scalare TD non si sommano più i cambiamenti energetici e fisici già attribuiti dai rispettivi canali. Curiosità, avanzamento reale e correzione predittiva restano. Evita doppi crediti e contaminazioni.
- Salvataggio separato `outcome_credit_v101` formato `ec2`, numero delimitato di pesi con validazione completa, stessa SharedPreferences originale di ZERO e inclusione automatica nei **12 slot nominati** della versione 0.10.0. Ricarica una vecchia partita senza `ec2` inizializzando soltanto i due nuovi canali, NON cancella `wm1`, `em1/em2`, abilità, corpo o scena.
- **Limite:** se un oggetto non è percepito visivamente il valore delle azioni verso di esso non viene aggiornato dal nuovo credito energetico. Può essere utile scoprire in seguito sensori più espressivi anziché inventare un nesso causale. Il mondo `UnknownWorld.Mind` continua comunque ad apprendere la conseguenza reale della frequenza al tocco.

### 2. Memoria episodica con provenienza degli eventi (EpisodicMemory)
- Nuovo formato **`em3`** che conserva la provenienza `kind,source` per ogni episodio; parser **retrocompatibile em1/em2**, tutti gli episodi storici ricevono `kind=0` (conseguenza mista storica, non falsamente classificata).
- `kind=1`: episodio energetico associato alla frequenza effettivamente **vista nel contesto di azioni vissute**. `source=signature`.
- `kind=2`: episodio di cambiamento fisico esterno verificato, indipendente dal danno simultaneo; `source=-1`.
- La stessa finestra con porta mossa e danno rosso può generare **due ricordi distinti di segno opposto**, senza fare il bilancio aritmetico prima di memorizzare. Consolidamento richiede **stessa provenienza, stesso segno e azione/contesto comparabile**, così il ricordo di danno rosso non cancella quello di manipolazione.
- Il suggerimento episodico dannoso da contatto energetico richiede che la frequenza dannosa sia percepita nel **nuovo contesto**; non deve automaticamente sconsigliare la stessa presa in un contesto di solo cancello. Le memorie storiche miste non vengono retro-corrette.
- Il vecchio metodo `consequence()` rimane per test/migrazione; nel gioco si usa `consequenceSeparated(physicalEffect, energySinceAction[3], ...)`.
- L'effetto esterno “porta spostata” è ancora un feedback fisico **generico**: non è una pianificazione causale a più passaggi che deduce “se apro la porta ottengo energia”. Per questo ZERO può muoverla di poco e poi allontanarsi.

### 3. Integrazione GameActivity / sicurezza dei salvataggi
- Ogni contatto energetico reale aggiunge il **delta effettivamente guadagnato/perso** alla frequenza fisicamente toccata; nessuna legge dell'oggetto entra nella policy.
- Alla vera osservazione dopo una scelta, `EpisodicMemory` riceve **separatamente** il displacement fisico e il vettore di effetti energetici delle 3 frequenze. I contatori vengono azzerati dopo la consumazione. Durante morte imminente, la perdita appena subita viene memorizzata prima del reset dell'episodio.
- `save()` include `outcome_credit_v101` con commit dell'apprendimento esistente e della scena; RESET MODELLO DEL MONDO pulisce SOLO questa banca con la Q se espressamente richiesto. Named save/load preserva cervello, posizione, cancello, corpo, riserva e statistiche pregresse, ma non garantisce identica sequenza RNG né replica esatta di un trial in corso.

### 4. Test automatici
- `ExperienceCredit.main`: stessa sequenza simulata “presa/porta mossa + successivo contatto rosso” conserva il credito meccanico e distingue penalità della firma visiva rossa; associazioni non viste non producono credito fittizio; serializzazione `ec2` e rifiuto dati corrotti.
- `EpisodicMemory.main`: due ricordi separati e indipendenti, filtro dell'avversione sulla corretta fonte, persistenza `em3`, lettura dei vecchi `em1/em2`, consolidamento.
- CI `ZERO 0.10.1 Outcome Attribution and Save-Compatibility QA`: test JVM, regressioni motore, manipolazione, cancello, memoria, salvataggi e compilazione Android (passata sul branch).
- Test su Galaxy S21 da fare con **partite in slot separate e senza resettare**. Verificare spostamento porta, doppia presa e contatti con stimoli dannosi separatamente; nessun risultato comportamentale garantito prima di prova reale.

## Limitazioni scientifiche / prossimi esperimenti
Questa è **attribuzione di ricompense con provenienza osservabile** (“source-conditioned credit assignment”), non inferenza causale rigorosa: un evento e un'azione correlati possono non esserne causa e la modalità di rappresentazione è progettata dall'ingegnere. Non è capacità di prevedere se un cancello apre un passaggio alla risorsa. In futuro pianificazione controfattuale, memoria di transizioni a lungo termine e confronto reali di esiti in contesti simili potrebbero migliorare l'attribuzione senza codificare “apri la porta”.

## Continuità progetto
Repo `nicogtr20-ctrl/idle-runner-releases`. Codice Android pubblico firmato sul branch `feature/overmind-inapp-updater`, lavoro di sviluppo `feature/overmind-v101-outcome-attribution`, documentazione/update metadata `main`. Consultare `OVERMIND_START_HERE.md`, `AGENTS.md`, `docs/HANDOFF.md`, `docs/EXPERIMENT_0099.md`, `docs/EXPERIMENT_0100.md`, questa nota, `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/ROADMAP.md`, `docs/CHANGELOG.md`. Non linkare APK finché GitHub Actions firmata e GitHub Release non confermano SHA e versionCode.

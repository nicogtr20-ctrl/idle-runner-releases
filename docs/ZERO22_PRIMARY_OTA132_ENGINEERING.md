# ZERO 2.2 OTA132 — unico cervello primario, apprendimento e correzioni nel mondo reale

Data: 14 ottobre 2026. Riferimento: richiesta esplicita dell'utente di non continuare a rimbalzare tra un vecchio cervello e un 2.2 laterale; attivare veramente il 2.2 in gioco e svilupparlo correggendo gli errori osservati nelle vite reali.

## Nuovo patto architetturale

**UN SOLO DECISORE DI AZIONI IN PRODUZIONE: `RecurrentCoreV22`.**

Il nuovo `SensorimotorMind.choose()` passa immediatamente a `choosePrimaryV22(senses)` quando trova il core attaccato. `GameActivity.load()` crea sempre il core (recuperando il salvataggio compatibile `rc22`) e lo collega alla mente. Il vecchio blocco che calcola e arbitra tutti i punteggi TD `model`, `value`, `planner`, `episodic`, `mortality`, `skillBias` rimane nel sorgente solo per leggere salvataggi/test isolati senza core; **non viene eseguito durante la normale esperienza Android 2.2**. Le operazioni di aggiornamento dei pesi `model` e `value` sono inoltre congelate sotto `activeCore != null`. Ciò rende il vecchio decisore né primario, né un controllore concorrente allenato in parallelo.

Il core `RecurrentCoreV22.primaryScores(...)` decide tutte le 16 azioni sulla base di:
- stato percepito 164D (26 sensi comuni, 96 membrane ridotte in 16×5, 32 origini ottiche, 6 controlli corporei, 16 profondità visive, 4 indizi anonimi di continuità) più ricorrenza H24;
- teste persistenti Q/reward/risk aggiornate con esperienze **vere** (`consequence`), approvazione di risultati fisici e vera energia;
- valutazione del futuro quando disponibile da modelli validati, altrimenti valore diretto appreso;
- piccolo incentivo alla prova di azioni meno campionate, senza attribuire ricompense false a casse o contatti;
- indizi **subordinati** di navigazione, causalità e riconoscimento oggetti, derivati dai moduli sperimentali: questi sono sensori/abilità specializzate e non scelgono mai l'azione indipendentemente.

`primaryExploration` appartiene al 2.2; aumenta quando la riserva scende o mancano contatti energetici reali (non diminuisce quando la fiducia nel futuro sale). Nessuna mappa di colori preinstallata, nessun target privilegiato, nessun tasto esterno che scelga azioni. Le sedici azioni controllano ancora attuazione motoria e adesione del corpo fisico precedente: cambiare il cervello non richiede rifare fisica e membrana.

## Effetti operativi reali

- Tutti i *nuovi* comandi del Galaxy S21 provengono dal core. Non è più una shadow policy, né un controller di influenza marginale.
- Memoria `recurrent_core_v22`: esattamente stesso formato `rc22`, CRC, arrays, states, samples, errori, replay, progressi precedenti e nomi dei 12 slot. Non si scartano i 96 episodi replay o pesi già appresi.
- Rete `SensorimotorMind` 26D precedente congelata nel gameplay, non cancellata; potrà essere rimossa solo dopo conferma di compatibilità e migrazione futura, senza costringere l'utente a ricominciare.
- Moduli sensoriali, `PhysicalConsequenceV20`, `ObjectContinuityV21`, `CausalExperiments`, `ObjectExperienceV20`, `InstrumentalNavigation`, `GripSkillBank` osservano gli stessi comandi e continuano a imparare; alcuni forniscono segnali appresi e convalidati, **mai propri comandi**.
- Firma pacchetto Android e flusso OTA ufficiale restano invariati; non disinstallare, non cancellare dati.
- Blackbox `db2`: campi schema invariati; **nel codice OTA132 il termine 16 ora registra il punteggio primario assoluto del nucleo**, anziché un `recurrentDelta` incrementale al vecchio TD. I report storici restano immutati; le analisi devono interpretare il campo in base alla versione dell'APK.

## Criteri prima di firmare

1. Regressione di **120 azioni**: due controller con vecchi pesi TD differenti ma lo stesso `rc22` scelgono sempre la stessa azione; contatore `primaryDecisionsSession` incrementa, nessuna decisione dal vecchio arbitro.
2. Non alterare `rc22`: save/restore CRC, ripresa, morte, replay rari e heldout non entrano nel train.
3. Sotto riserva energetica bassa, il 2.2 aumenta esperimenti propri; dati veri, non euristiche di colore/target.
4. JVM suite integrale, classi Android compilate, invarianti fisica/grip/lastre, QA, APK ufficiale firmata e prova manifest+APK server HTTP200/size/SHA.

## Rischio accettato e metodologia futura

Con 2.2 primario lo stato attuale Q/reward/risk **NON È DIMOSTRATO superioriore**: quando le predizioni sono sbagliate la partita può peggiorare, le vite possono essere corte e la fase di apprendimento può essere instabile. Questo è esplicitamente voluto dall'utente per migliorare il cervello reale in campo anziché lasciare il modello in sola osservazione. Il nuovo controllo **non ripiega automaticamente** sul vecchio TD se muore o sbaglia; salva i dati del 2.2 per correggere il comportamento dall'evidenza reale.
Non usare confronti sistematici contro il vecchio cervello come blocco di sviluppo; monitorare invece il miglioramento dello STESSO core 2.2 dopo ogni correzione:
- vita mediana/percentile, contatti positivi/negativi per 1000 secondi simulati;
- casse realmente scoperte, percentuale di gemme rare toccate dopo esposizione;
- perseverazione di azioni e tempo senza energia; movimento realmente misurato;
- coerenza della memoria tra vita, morte, salvataggio, rilancio, OTA;
- errore heldout per energia fisica e valore delle scelte.
Il difetto delle gemme viola non è automaticamente risolto dalla priorità architetturale. Va indagato durante uso 2.2 attraverso sensori reali e conseguenze, senza attribuire arbitrariamente il colore viola a valore predefinito.

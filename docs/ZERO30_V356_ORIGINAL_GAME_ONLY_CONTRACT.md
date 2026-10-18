# ZERO 3.0 V356 — TRAPIANTO DEL SOLO CERVELLO NEL GIOCO ORIGINALE

## Ordine inderogabile dell'utente (18 ottobre 2026)
- NON ricostruire l'app. La UI, il canvas del gioco, HUD, pulsanti, impostazioni, disposizione e grafica sono quelli dell'app precedente all'installazione di V354/V355.
- NON cambiare corpo/simbionte, solver fisico, oggetti, habitat, porte, energia, presa, vento e gestione delle vite.
- NON modificare i salvataggi originari con un nuovo formato del mondo. Conservare e ripristinare la normale scena e il ciclo di vita originali, inclusa la nascita successiva alla morte e tutte le gestioni dei profili.
- SOSTITUIRE il cervello attivo V2.2 con il V3.0: un unico attore, percezioni da contatto/ottica/propriocezione/interocezione originali, comandi muscolari realmente consumati dal vecchio SymbioteMatterV1, apprendimento dalle conseguenze reali, persistenza del cervello.
- UNICA aggiunta visiva autorizzata: l'atlante 3D dei neuroni reali, nelle impostazioni originali. La pagina laboratorio con pulsanti rettangolari non può essere usata come menu principale o come sostituto di pagine storiche.
- Mai più costruire due mondi/creature, una vita a secco senza rinascita o una seconda schermata Gioco. Nessuna OTA/APK consegnata finché simulazione originale e comportamento dei menu non superano prove vere.

## Base verificata su GitHub
Il branch `feature/zero30-brain-only-original-game` deriva dalla candidata V355 per conservare le classi cognitive sperimentali. `GameActivity.java` è stato ripristinato **byte-identico** dalla versione originale `feature/zero22-active-recurrent-ota117` (HUD, fisica chiamante, menu, vite, salvataggi). Sono state inizialmente ripristinate anche `ScarcityLab.java`, `SquareCollisionSolver.java` e `SquareGripPuzzle.java`, ma la compilazione dei test scientifici V3 ha dimostrato che sono necessarie API aggiuntive per il laboratorio. Sono state quindi riapplicate le **overload diagnostiche V3 con flag di laboratorio disattivati nel percorso originale**. Serve ancora un test A/B dinamico di parità fisica, non va dichiarata equivalenza completa senza esso.

Il trapianto neurale non è ancora cablato nel gameplay; questo branch non è pronto per l'installazione. Il launcher originale torna a essere GameActivity. Il controller V355 che chiamava Zero3RuntimeV325.step() sull'Android UI thread e fermava la vita a riserva zero **è stato rimosso dalla partita**.

## Contratto di trapianto (non ancora certificato)
1. L'unico ciclo fisico deve rimanere GameActivity.Lab.onDraw()/updateHabitat()/resolveObjects()/resetEpisode(); ogni sua evoluzione deve essere un diff minimo e provato dalla versione originale.
2. Il V3.0 non può chiamare un suo `Zero3RuntimeV325.advanceOneDecision()` con un secondo corpo: è ammesso soltanto un adapter di neuroni puri, riusando GRU64 V354, 194 sensori reali, corteccia motoria64 e adesione appresa V319.
3. Il medesimo buffer 64D deciso dall'adapter deve essere l'unico buffer mandato a `SymbioteMatterV1.step`. Le scelte MotorDurationV22, RecurrentCoreV22, SensorimotorMind e SymbioteMotorHeadV1 non devono condizionare l'azione né apprendere in parallelo.
4. I dati devono essere PRE prima dell'esecuzione e POST dopo collisioni, energia e costo muscolare della stessa scelta. Nessun movimento propulsivo esterno e nessun reward per il solo movimento.
5. La morte deve continuare a chiamare il medesimo resetEpisode originale, che conserva pesi e neuroni V3 e crea una nuova vita. Nessuna finestra morta che richieda `NUOVA VITA` da laboratorio.
6. Salvataggi/named slots originali conservati. Il bundle del solo cervello V3 deve entrare nello stesso profilo atomico, essere rigorosamente convalidato prima di ogni ripristino, e non cancellare la copia 2.2. Eventuale import dei pesi dalla V355 deve essere **solo neuronale**, mai il mondo/livello fisico.
7. La vista 3D deve leggere il grafo GRU/corteccia/adesione del vero cervello in uso, non il runtime sperimentale inattivo.

## Gate BLOCCANTI prima di APK/OTA
- Diff strettamente minimo del gameplay e delle schermate vs originale.
- Identità fisica e vite: scene, 20 nascite/morti automatiche, controllo sensori/pressioni, casse, luci e vento su test ripetibili.
- Esclusività: un solo attore V3 e zero learning/suggerimenti V2 mentre V3 è attivo.
- Salvataggi: nome slot, ricarica, chiusura forzata, morte, installazione APK senza cancellazione, integrità/doppio profilo.
- Atlante 3D reali nel menu Impostazioni originale e nessuna dashboard come launcher.
- Emulatore e prova sul Galaxy S21, firma stabile, versionCode crescente; nessuna pubblicazione stabile senza autorizzazione.

## Stato
Base di codice originale ripristinata nel branch di lavoro. Il vero adapter neurale e i gate end-to-end devono ancora essere realizzati/testati. Non chiamare questo stato «finito» o «APK pronto».

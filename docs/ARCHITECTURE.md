# Architettura tecnica di OVERMIND ZERO

## Applicazione
- Android Java nativo, Gradle 8.11.1, AGP 8.7.3, Android minSdk 24.
- Repository `nicogtr20-ctrl/idle-runner-releases` (privato), OVERMIND su branch `experiment/...`.
- Per v0.2 Android applicationId `com.overmind.zero.brain`, per v0.3 `com.overmind.zero.world` (app separata a causa di potenziali certificati debug diversi).
- Dati motor learning in `SharedPreferences` (`zero_motorbrain_001`), memoria mondo in chiave `world_knowledge_v03`; app ID diverso = area dati Android diversa.

## Controller motorio: MotorBrain.java
- Rete neurale feedforward con **62 parametri**, inizializzati casualmente e senza pretraining.
- Corpo composto da **10 punti di superficie** elasticamente vincolati, massa centrale, forze di contrazione, attrito/aderenza variabile.
- Il cervello controlla tensione muscolare locale e grip di ciascun punto.
- Addestramento con **neuroevoluzione**: mutazioni dei parametri, valutazioni di displacement e conservazione del miglior candidato. Non è backpropagation e non è AGI.
- Fitness su tre direzioni di addestramento; benchmark su altre tre direzioni. Miglioramento su benchmark non basta da solo per dimostrare robustezza generale.

## Mondo sconosciuto: UnknownWorld.java
- Tre segnali distinguibili attraverso sensori simulati. Le regole fisiche reali sono nell'Environment e non sono rese note alla policy.
- Impara una media aggiornata delle conseguenze (energia, movimento) per classe di segnale.
- Sceglie target combinando valore stimato, novità e distanza; conserva conteggi ed estimatori.
- L'ambiente reale è ancora troppo semplice per inferenze causali e modelli di esplorazione sofisticati.

## UI e test
- `GameActivity.java` disegna corpo e mappa via Canvas Android e contiene controlli per addestramento, playback e benchmark.
- Workflow di CI `zero-brain-release.yml` per ZERO-001; `zero-world-release.yml` per ZERO-002.
- Gli script testano il cervello via `javac`/JVM prima del build Android. Ci vuole poi una prova di uso reale e consumo di memoria sul Galaxy S21.

## Principi per le prossime versioni
- Scindere `World`, `CreatureBody`, `Sensors`, `MotorPolicy`, `Memory`, `Motivation` e `Renderer`.
- Evitare falsi effetti appresi: legare spinta/manipolazione ai contatti reali tra masse e oggetti.
- Persistenza con versionamento formato memoria e migrazioni.
- Registrare in debug decisione, input sensoriali, previsione, azione, risultato; mantenere verificabile la reale esperienza.
- Non confondere trasparenza UI dei risultati con "pensieri" narrati dal soggetto.

## ZERO-003 / v0.4: fisica e apprendibilità della manipolazione
- `ContactLearning.java` contiene collisioni con oggetti mobili mediante perimetro elastico e nucleo, impulso da velocità relativa e correzione dell'interpenetrazione.
- `UnknownWorld.Entity.touch` non aggiunge più velocità agli oggetti mobili.
- `MotorBrain.Body.step` accetta opzionalmente 18 nuovi pesi di controllo per contrazione e attrito, senza modificare i 62 pesi motorii salvati.
- Neuroevoluzione di `ContactLearning.Trainer` esegue rollout fisici e confronti holdout su direzioni non usate per imparare.
- `GameActivity` aggiunge contatore di oggetti realmente mossi e benchmark di manipolazione, conserva vecchi dati `SharedPreferences` e nuove chiavi `hand_*_v04`.
- Solo simulazione; nessun LLM, nessun pensiero astratto, nessun ragionamento causale generale.
- Branch firmato OTA: `feature/overmind-inapp-updater`; manifest https://overmind-zero-updates.vercel.app/latest.json. Prima prova sul dispositivo della 0.4 pendente.

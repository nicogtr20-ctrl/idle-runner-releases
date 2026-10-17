# ZERO 3.0 — V3.22/A | Snapshot congiunto neuroni + materia + oggetto + traccia causale, ripresa nella STESSA vita

> **ZERO 3.22/B (17 ottobre 2026):** [Rapporto verificato](ZERO30_V322B_FULL_CAUSAL_REPLAY.md), [CI 39023478559 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39023478559). Superate 20/20 verifiche di ripresa della stessa vita in cinque scene di laboratorio, con archivio unico del cervello, corpo, RNG degli eventi fisici del test e esperienze complete PRE/12×64 comandi/POST. Corruzione e rollback superati; Android Debug compila. È un sottogate di persistenza in laboratorio, non la soluzione finale G3/G5, né il salvataggio completo dell'app Android o la nuova UI. Non modificare OTA172 o i 12 slot 2.2.

> **CI DEFINITIVA DOPO HARDENING — 17/10/2026:** [39022300684 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39022300684), commit `c122be6493ec6008771169fc05a10918d9371a9d`. Tutte le 4 JVM, comparazione `cmp` bitwise, 48→96 decisioni nella stessa vita, rollback e Android Debug (`BUILD SUCCESSFUL`) di nuovo PASS dopo immutabilità della traccia e rigetto di dati incompatibili. Fingerprint e risultati fisici invariati. Gate **SOLO V322/A LAB**, non `BrainSnapshotV3` di produzione, G3/G5 bloccati, no OTA.


**Data:** 17 ottobre 2026. **Ambito:** laboratorio E2/E3-prototipo nella PR #87 **DRAFT**, branch `feature/zero30-foundation-g0-g3-20261016`; produzione ZERO 2.2/OTA172 immutata.

## Contratti vincolanti letti prima dell'intervento

- [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md) §§ 3, 5, 7, 11–17, 21.
- [Implementation Contract](ZERO30_IMPLEMENTATION_CONTRACT.md) §§ 0, 2–6, 9.
- [UI/UX V3](ZERO30_UI_UX_SPEC.md) §§ 5, 8–13.
- [V3.21 checkpoint congiunto dei tre moduli](ZERO30_V321_FULL_NEURAL_BUNDLE.md).

**Scopo ristretto e falsificabile:** V3.21 salva i tre moduli neurali ma, al riavvio, riparte con un altro corpo/episodio. Dimostrare che, a confine di decisione, la **stessa vita** continua da memoria neurale + corpo gel reale + contatti/oggetti + cursore dell'unico decisore + traccia dei comandi, producendo *lo stesso identico esito* di un processo non interrotto. Fallimento se una sola impronta o una sola misura fisica differisce.

## Implementazione sperimentale effettiva

Sorgenti della PR #87:

- `Zero3OrganismStoreV322.java`: payload binario V322 con encoder ricorrente/actor V317, cortex adattiva V321 completa, head adesivo V319, tutti i campi fisici primitivi (pubblici e privati) di `MotorBrain.Body`, `Morphology.Surface`, `SymbioteMatterV1`, e `UnknownWorld.Entity` presenti nel laboratorio; includes 96 membrane, 10 particelle corpo, 64 fluidi, memoria delle forze e timer reali di `boxGripHold`. Schema verificato per nome/tipo/dimensione campo con rigetto di forme non riconosciute; il codec non modifica algoritmi del solver.
- Lo stesso payload include `episodeId`, `nextDecision`, ultime 64 efferenze applicate e **traccia causale per ogni decisione completa** (ID unici, tick fisici adiacenti, hash PRE/POST sensoriali, fingerprint 64×12 muscoli, frame presa/bilaterali, lavoro misurato). È un **journal diagnostico compatto**, NON ancora il replay integrale delle due osservazioni sensoriali di `ExperienceV3`.
- `Zero3MotorIntentV316.Authority.restoreCompletedDecisions` accetta il cursore verificato soltanto su istanza nuova che non ha ancora emesso decisioni; mantiene l'unico proprietario per episodio nel processo. Il cursore è nello stesso commit delle reti e della fisica.
- `Zero3SameLifeRestartV322Regression.java`: esegue 96 decisioni scelte dal vero encoder ricorrente, senza heading, gait, spinta al COM, coordinate privilegiate di gemme o premio sintetico. Ogni decisione è composta da 12 veri passi del solver `SymbioteMatterV1.step`, con `SquareGripPuzzle.constrain`, `MaterialContactConstraints.reconcile`, `SymbioteWorldStepV171.interior`, `GripSurfaceComposition.compose` e tick dell'oggetto `UnknownWorld`. Sono testate prese reali, non scritte su `gripHold`.
- File e manifest SHA-256, due generazioni alternate, temp + file fsync + `ATOMIC_MOVE`, verifica completa prima di avanzare il manifest. Corruzione dell'ultima generazione riporta a quella precedente verificata; entrambe invalide → **fail closed**. Un decoder non riconosciuto non deve scegliere uno stato parziale.
- Workflow sperimentale `.github/workflows/zero30-v322-same-life-qa.yml`, distinto dal publisher OTA: JVM Linux in 4 processi indipendenti, `cmp` bytewise di tutta l'evidenza e build Android Debug **non firmata/pubblicata**.

## Protocollo congelato

1. **PREFIX JVM:** una vita nella fixture BOX reale; 24 decisioni e commit gen1; altre 24 decisioni e commit gen2; uscita dal processo a metà della vita, non morte o nuova nascita. Controllo esplicito del divieto di snapshot con `encoder.before()` pending.
2. **RESUME JVM:** carica con hash la gen2 (decision 48), ricrea nuova istanza del corpo e del solver, ripristina tutti i campi della materia e dell'oggetto/timer, ricrea le tre reti e l'authority, quindi esegue la stessa vita fino alla decision 96. Un cortex con learning-rate sbagliato deve essere rifiutato prima di alterare encoder o head. Commit gen3.
3. **REFERENCE JVM:** dalla stessa nascita, 96 decisioni senza interruzione; produce la medesima serializzazione completa, tracce, impronte e statistiche in un file. `cmp` tra EVAL/REFERENCE obbligatorio.
4. **FAULT JVM:** tronca e corrompe gen3, recupera gen2 con **esattamente 48** tracce/decisioni, ripubblica gen valida senza inventare scelte, prova manifest primario danneggiato e rigetto finale con entrambi i file corrotti.

Non adattare un test al suo risultato per chiamare un gate scientifico superato. Per la promozione finale richiedere nuove scene e seed, non soltanto questa fixture.

## Risultato effettivamente letto dai log GitHub Actions

**CI sperimentale [39022162716 — SUCCESS tecnico e gate di ripresa stessa vita PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39022162716)**, commit `3c004ba7a7d67db7a42380db375d5d46fdf4eed5`. Tutte le quattro fasi JVM e compilazione Android Debug (`BUILD SUCCESSFUL`) completate. In seguito il codice è stato ulteriormente rafforzato con getter difensivo della traccia e gestione di errori di checkpoint; vedere l'ultima CI associata al commit del ramo per il risultato del rinforzo.

| Prova | Misura autentica |
|---|---|
| Segmento salvato / ripreso | 24 + 24 → **48 decisioni**, nuovo processo → 48 ulteriori = **96** decisioni nella stessa vita |
| Fisica complessiva | **1.288 veri `gel.ticks`** (di cui 1.152 nelle decisioni, resto inizializzazione fisica) |
| Encoder / cortex / head | **96/96/96 update** in questa fixture, senza attribuire valore biologico al mero conteggio |
| Sorgente sensorimotoria | 96 patch membrana, 10 masse corpo, 64 fluidi, 16×4 canali motori |
| Presa reale BOX | **1.108 frame** con `gripHold` autentico |
| Presa bilaterale BOX | **8 frame** con `bilateralHeldNow` fisico |
| Lavoro meccanico cumulato delle decisioni | **0,041234952281**, differenza di `gel.work` post-azione, non ricompensa |
| Impronta fisica completa | `3824f1c6cfa6a90a77f3aa08b22a8be88643ee5e4dc2e6fa850aac85f0e7f639` |
| Impronta payload congiunto finale | `5126ebf5d4d6cf65fc515d26ba9c86aa049e440b0981eb06425e16e2ff8c2ce5` |
| Comparazione processo RESUME/REFERENCE | **`cmp` byte-per-byte PASS**, compresi pesi, memoria corta, vel. particelle, contatti, oggetto, tracce, decision count |
| Rollback fault injection | gen3 guasta → gen2, esattamente **48** decisioni; doppio guasto → fail closed |
| Android Debug | **BUILD SUCCESSFUL**, niente firma/distribuzione OTA |

Il fatto che nella stessa fixture compaiano 8 frame bilaterali dopo 96 decisioni non significa che ZERO abbia appreso la manipolazione: è una osservazione fisica, non un A/B contro il congelato.

## Confini di validità da non oltrepassare

**Gate dichiarabile:** `V322_SAME_LIFE_CHECKPOINT_LAB=PASS` nel solo scenario BOX con il medesimo corpo e seeded actor, fra decisioni completate. Questo è un nuovo sottogate di **integrità della continuità**, non G3 locomozione, G4 actor–critic completo, G5 world model, G6/G7 interi o G9 dispositivo.

**NON è ancora `BrainSnapshotV3` dell'organismo completo in partita**:

- Un solo oggetto BOX della fixture; non sono stati ancora salvati tutti gli stati del gameplay (spawn, risorse, metabolismo, danni, ambiente multiplo, altri controller, global event stream). Il `lastRngState` V322 è **metadato di seed della fixture**, non lettura/ripresa del generatore RNG attivo del mondo di `GameActivity`.
- Traccia di replay **compatta con hash**, non il journal integrale `Zero3ExperienceJournalV316` delle osservazioni PRE/POST associate allo stesso commit. Non è dimostrato re-training ripartendo da replay campionato.
- Il nuovo `Authority` mantiene unicità durante un processo e cursore da checkpoint verificato; non è ancora un coordinatore crash-safe multi-istanza di gameplay Android.
- Snapshot consentito solo su **decision boundary**. Non ripristina un frame fisico a metà contrazione, né una decisione PRE/POST incompleta: il modello finale dovrà censurarla e ripartire dall'ultima decisione committata.
- Java reflection nel codec è una soluzione di laboratorio: è schema-rigida e protegge contro nuovi campi non previsti, ma l'integrazione di produzione dovrà usare adapter versionati espliciti e testare limiti CPU/RAM e persistenza su filesystem Android.
- Il test JVM Linux (con Android debug compile) NON è riavvio reale Galaxy S21, power-loss, migrazione 2.2→3.0, né prova su 20+ seed/scene. Non esiste UI V3, skill memory, world model validato, neurogenesi o nuova OTA.
- L'apprendimento motorio resta **scientificamente NON VALIDATO**: anche un salvataggio perfetto potrebbe preservare una policy di locomozione inefficace. Non chiamare 96 decisioni `camminata` né usare questi 8 frame come nuova competenza.

## Prossimo incremento obbligatorio V3.22/B

1. Integrare `ExperienceV3` completo PRE/POST insieme ai tre moduli e al materiale nello **stesso transactionId/generationId**, con censura dei cicli incompleti.
2. Snapshot di **world RNG reale**, spawn e ambiente di gameplay, metabolismo/energia/danno, ulteriori oggetti e authority persistente; nessuna ripresa con nuova vita nascosta.
3. Prove di ripresa multi-mondo (FREE, BOX, DISK e più oggetti) e 20+ seed, poi Android Galaxy S21 con pause, chiusura forzata, bassa memoria, riapertura, slot V3 isolati.
4. Riprendere G3 con confronto matched motor trained/frozen su nuove scene, allineamento intenzione-spostamento, costi, presa bilaterale/rilascio; no gait hardcoded.
5. Actor–critic, world model, memorie di skill, crescita neurale e nuova UI reale secondo Master e contratto, poi G9/G10 in ordine. **PR #87 DRAFT, non fare merge sul ramo OTA, preservare OTA172 e tutti i 12 slot 2.2.**

**Nessuna modifica è stata fatta a `GameActivity`, `SymbioteMatterV1`, codice OTA, `updater/public/latest.json`, APK firmato o salvataggi legacy per questo esperimento.**

# ZERO 3.0 — V3.24 | Continuità della stanza, porta e identità della risorsa (laboratorio)

**17 ottobre 2026.** Ramo `feature/zero30-foundation-g0-g3-20261016`, PR #87 ancora DRAFT. Leggere prima [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [contratto di implementazione](ZERO30_IMPLEMENTATION_CONTRACT.md), [specifiche UI](ZERO30_UI_UX_SPEC.md), [V3.22/B](ZERO30_V322B_FULL_CAUSAL_REPLAY.md) e [V3.23](ZERO30_V323_SURVIVAL_CONTINUITY.md).

## Perché serve

I gate di V322/B hanno dimostrato ripresa di 20 vite per neuroni, memoria PRE/POST, 96 patch, 74 particelle interne e oggetti. V323 ha aggiunto lo stato metabolico. Restava fuori la stanza `ScarcityLab`: la porta non è un semplice punto visuale. Il suo stato comprende velocità, posizione, timer di presa, memoria privata dei contatti (`gateTouchPrevious`), latch (`wasGrasping`), numerosi contatori fisici e la risorsa `room.food`, che deve riferirsi allo STESSO oggetto presente in `environment`.

`GameSceneSnapshot` legacy salva un sottoinsieme dei campi della stanza: non sarebbe corretto spacciarlo per un checkpoint identico frame-per-frame. ZERO 3.0 richiede uno stato rigido versionato e transazionale.

## Implementazione sperimentale sul ramo Draft

- `Zero3RoomStateV324.java`: codec binario con schema per nome/tipo/dimensione di tutti i campi primitivi/array non statici di `ScarcityLab`, inclusi campi privati di contatto e memoria del solver. Ogni nuovo campo non supportato fallisce esplicitamente, anziché venire ignorato. Il serializer verifica la roundtrip e convalida i numeri. `room.food` non viene clonato indipendentemente: è obbligatorio trovare esattamente la stessa istanza nella lista degli oggetti fisici e ricostruire il riferimento nel mondo dopo il ripristino.
- `Zero3RoomArchiveV324.java`: archivio unico versionato che contiene V323 (cervello, cortex, 64 comandi, gel, mondo, RNG del laboratorio, replay sensoriale completo ed energia) **e il checkpoint della stanza**. Manifest + payload SHA-256, due generazioni, fsync, rinomina atomica, fallback sull'ultima generazione verificata e rifiuto di doppia corruzione. Nessuna scrittura su profili 2.2.
- `Zero3RoomRestartV324Regression.java`: nello stesso organismo aggiunge vera `ScarcityLab` e la sua risorsa all'ambiente prima di iniziare la vita, esegue 48 decisioni, salva e riapre in nuova JVM, prosegue fino a 96; una JVM di controllo compie 96 decisioni senza stop; il risultato è confrontato **byte per byte**. Il test del room snapshot è associato al medesimo checkpoint di neuroni, corpo, energia e replay. Esegue il solver della stanza e **`GripSurfaceComposition.compose` dopo**, perché altrimenti `room.solveGate` potrebbe sovrascrivere nel campo condiviso le prese ancora possedute dalla BOX.
- `Zero3RoomMechanicsV324Regression.java`: test separato del vero `ScarcityLab.solveGate`: posiziona due patch con comandi controllati per attivare una presa bilaterale reale di laboratorio, salva la porta e i suoi timer, poi compara 8 frame di fisica senza stop contro 8 frame dopo restore. È una **fixture deterministica** e non dimostra che ZERO abbia autonomamente imparato la presa o l'apertura.

## Protocollo e verifiche

**Gate preregistrato prima della lettura conclusiva dei risultati**: 5 tipologie fisiche × 2 vite ciascuna; ciascuna vita contiene 96 decisioni e 12 passi del gel per decisione. Passare solo se per tutte le vite:
1. Due generazioni 24+24 decisioni, riavvio JVM alla 48, stessa vita fino a 96, test di riferimento 96 ininterrotte.
2. Identità dello snapshot V324 completa, hash corpo e stanza, replay integrale e variabili metaboliche identici; `cmp` sui file prova.
3. NESSUN oggetto `room.food` duplicato/orfano, checkpoint rifiutato quando il food non appartiene alla lista degli oggetti.
4. Scena BOX mantiene la presa reale dopo composizione `GripSurfaceComposition`; portare separatamente i contatti della porta già verificati nel test meccanico.
5. Fault-injection: payload nuovo corrotto → torna esattamente alla vecchia generazione 48 senza inventare aggiornamenti; due generazioni corrotte → fail closed.
6. Android Debug compila, **non viene pubblicato**.

[CI V3.24 del ramo sperimentale](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/workflows/zero30-v324-room-qa.yml). Riportare il numero esatto della run e il suo esito dopo verifica completa; compilazione riuscita o qualche singolo scenario non autorizza il gate.

## Limiti e prossimi gate

**Non è integrazione V3 live in `GameActivity`**, né aggiornamento di `GameSceneSnapshot` versione legacy. I 12 slot 2.2 sono protetti e OTA172 non si tocca. La camera nello screening multi-vita è **deliberatamente posizionata lontano dal corpo**, così si testa la persistenza con mondo multi-oggetto e senza introdurre una manipolazione artificiale del gate mentre si addestra il cervello. La meccanica dei contatti bilaterali viene verificata separatamente con due patch controllate, che non possono essere contate come abilità autonoma di ZERO.

Per il futuro: adapter esplicito sul runtime GameActivity 3.0 e il suo ciclo fisico completo (room/shelter/world/metabolism/risorse), provenienza di ogni decisione ed energia/danno reali negli input neurali quando disponibili, crash/restart su Galaxy S21, più seed e scene, benchmark G3 su locomozione e presa **acquisite** contro policy vergine/frozen. G3 locomozione, G5 world model, crescita neurale e UI 3.0 rimangono aperti. Nessun merge verso main OTA e nessuna pubblicazione APK.

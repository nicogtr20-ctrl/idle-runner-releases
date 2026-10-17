# ZERO 3.0 — V3.16 | Intenzione unica e checkpoint di esperienze reali

**Protocollo fissato il 17/10/2026 prima della prima CI.** Vincoli da [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.15](ZERO30_V315_SENSORY_CAUSAL_CONTRACT.md).

## Punto di partenza reale

V3.15: 2.048 decisioni fisiche **comandate dal laboratorio**, registratore RAM exactly-once, snapshot 96×12, 16 raggi fisici, 74×7 fluido, 48 propriocezione, 64 efferenze; [CI 38989788378](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38989788378) SUCCESS. **Non persiste su disco**, e non esiste ancora una singola autorità tipizzata dell'intenzione. G3/G5 sono bloccati.

## Implementazione e confini V3.16

1. **`Zero3MotorIntentV316` immutabile e authority unica**: `episodeId, decisionId, controllerRevision, directionX/Y` con normata unitaria o zero, `desiredEffort`, `adhesionPreference`, `durationSimSeconds`, `confidence` come stima dichiarata, `sourceRevision`; `IntentAuthority` assegna una e una sola intenzione per decisione, senza consentire un secondo decisore, un cambio owner, frame skipping, decisionId duplicati o intenzioni incompatibili con l'episodio. La direzione è una intenzione, mai forza aggiunta al centro del corpo, né vettore verso la gemma. Il controller del test produce richieste NON apprese: ogni claim di autonomia è vietato.
2. **`Zero3ExperienceJournalV316`**, API Java non-Android che prende eventi V3.15 validi + intenzione correlata, serializza un record completo e ricaricabile: entrambi gli snapshot **locali 96×12, propriocezione 48, interno 74×7, 16 raggi, ultimi comandi 64 dove disponibili**, coordinate proprio corpo/tempo/ticks/metadati, le 64 efferenze applicate, pressione/presa e delta post-collisione, fonte/identità dell'intenzione, split irrevocabile. Schema e lunghezze fissi, bounds e float finiti, SHA-256 dell'intero payload, append causale frame-ordinato e ownership univoca. Nessun oggetto fisico nascosto, ID della gemma, reward inventato. Il replay legge **tutti i dati salvati**, non solo somme o digest. Checkpoint full-state-only del replay, **non checkpoint del cervello**.
3. **Persistenza a due generazioni** in *directory di test isolata*, ogni slot scritto su file temporaneo, flush + `FileDescriptor.sync`, switch tramite `Files.move(... ATOMIC_MOVE ... REPLACE_EXISTING)` (fallisce in assenza di atomic move); manifest con numero generazione, slot, dimensione, SHA-256 e digest verificabile, recovery scegliendo solo generazioni complete e validate; se ultimo slot corrotto, ripristinare precedente e segnalare perdita dell'ultima generazione (non perdere né inventare record). Niente scritture `SaveProfileCodec`, pref 2.2 o directory APK. **Non promettere crash-consistency su ogni filesystem Android senza prova specifica; directory sync best-effort, nessun release.**
4. **Regole di tentativo**: record V3.15 già validato dal vero gel, intenzione con la stessa coppia (episodeId, decisionId), 64 comandi applicati, nessun alterato, impossibile duplicare o riassegnare un intention a decision. `journal.add` in RAM, `checkpoint` copia atomicamente tutti i record; `load` verifica lengths/schema/sha/split/invarianti. Se crash prima di checkpoint, solo la generazione confermata è recuperabile: la coda RAM va considerata non durabile. Non promuovere a E3/learning.

## Prova preregistrata

16 episodi con quattro ambienti reali [FREE, BOX_NEAR, DISK, BOX_FAR], 128 decisioni fisiche per episodio e 96 warmup: **2.048 veri step decisione e 3.584 passi gel**, partizione FIT 8 / CAL 4 / BLIND 4 attribuita prima dell'episodio. Test ad ampia copertura usa i veri solver di V3.15 senza cambiare la fisica; può riusare le pose/seed già osservati per la **QA infrastrutturale**, NON per un nuovo blind neuroscientifico. Per ogni decisione il controller sperimentale manda 64 comandi reali e riceve un'intenzione tipizzata, senza direzione privilegiata o target oggetto.

Checkpoint dopo 4, 8, 12, 16 episodi (intero replay e SHA256); verificare che gli stessi 2.048 dati completi ricaricati siano bitwise uguali dove pertinente, con hash di frame originali e checksum. Ogni checkpoint usa slot alternativo. Verificare split immutabile. Dopo ultimo checkpoint, tranciare/corrompere l'ultimo slot e verificare recovery del **precedente** checkpoint (1.536 esperienze), MAI 2.048 inventate. Corrompere manifest senza toccare i blob: recovery da blob integro non usa dati non convalidati. Test negativi: due owner, duplicate decision/intent, mismatch episodio, NaN/Inf e direzioni invalide, replay mancanti, record tronchi, digest errato e trailing bytes. Tamper test non altera il ramo stable.

**Gate V3.16**: CI compila le vere classi; 2.048 esperienze con intento + fisica autentici; quattro checkpoint senza errore, replay totale senza perdita/cambiamento, dup/second owner rifiutati, fault injection ripristina solo precedente gen, hash SHA-256 controllati e dati sensory preservati, no merge/OTA, nessuna operazione su 12 slot. Qualsiasi requisito obbligatorio fallito => **FAIL**.

**Limiti esatti**: questo verifica un **contratto di intenzione e persistence dell'esperienza**, non prova che un'AI scelga le intenzioni, ricordi in modo funzionale, migliori a muoversi, mantenga policy/optimizer nel checkpoint, gestisca crash Android/SQLite effettivi o esegua G3/G5. Mancano ancora vero actor ricorrente, critic, memoria skill, neurogenesi e UI 3.0.

## Risultati verificati

*Solo dopo verifica GitHub Actions effettiva.*

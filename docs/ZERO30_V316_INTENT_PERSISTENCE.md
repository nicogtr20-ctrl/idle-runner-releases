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

Risultati della CI registrati a seguire, dopo verifica dei log.


## RISULTATI VERIFICATI — CI definitiva 39015142772 (17/10/2026)

**[GitHub Actions 39015142772](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39015142772): SUCCESS TECNICO E GATE INFRASTRUTTURA V3.16 PASS.** Risultato definitivo letto dai log della job `117345602962`. Le classi Java nuove `Zero3MotorIntentV316`, `Zero3ExperienceJournalV316`, `Zero3IntentPersistenceV316Regression` sono state compilate contro le reali sorgenti del solver e della percezione V3.15, senza stub e senza modificare la fisica produttiva.

### Evidenza fisica e persistente

| Proprietà | Risultato |
|---|---|
| Episodi | **16**, split per episodio FIT=8 / CAL=4 / BLIND=4 |
| Decisioni/azioni effettivamente eseguite nel solver | **2.048**; *generate dal controller di laboratorio, NON apprese* |
| Integrazioni fisiche comprese le 96 warm-up per episodio | **3.584** |
| Checkpoint completi | **4**, ogni 512 esperienze |
| Replay ricaricato in byte integrali, non sommari | **2.048/2.048**; ogni payload di esperienza confrontato bit per bit con la versione RAM |
| Sottosistemi serializzati per frame | 96×12 membrana, 16 raggi, 48 propriocezioni, 74×7 fluido, 64 efferenze precedenti, corpo fisico/time/ticks/provenance/availability flags; PRE e POST entrambi |
| Comando corrente e intenzione | 64 richiesti + 64 applicati, id episodio/decisione/revisione, vettore di intenzione, adesione, durata e confidenza esplicitamente stimata |
| Proprietario unico delle intenzioni | Seconda istanza autorità e doppia emissione decisionId rifiutate **nel processo corrente**; owner post-chiusura non può emettere |
| Scatole: frame con vero contatto | **512** |
| Scatole: frame con presa fisica valida | **252** |
| Dischi: frame con `touchPulse` | **314**; valore specifico di questa V3.16, non confondere con 229 della V3.15 |
| SHA-256 e generation switch | File intero e manifest verificati; un checkpoint usa lo slot opposto, scrittura+fsync e `ATOMIC_MOVE` obbligatorio, manifest aggiornato dopo il blob |
| Ultima generazione troncata/corrotta | Recuperate **1.536** esperienze precedenti verificabili, NON le 2.048 danneggiate |
| Manifest principale corrotto | Recuperati **1.536** record verificati tramite manifest di backup |
| Nuovo checkpoint dopo rollback | Conservate esattamente **1.536** esperienze, senza reinserire la coda persa; backup ricostruito dalla generazione verificata |
| Entrambe le generazioni non valide / nessun manifest verificabile | **Fail closed**, non inventa esperienze o sceglie un file candidato non confermato |

**Checksum diagnostico V3.16:** post-hash dell'ultima esperienza `67256990ad945ffc`, che NON è una firma crittografica. I file e il manifest usano invece **SHA-256** per integrità accidentale (non prova di autenticità contro chi controlla il filesystem). **CI definitiva: `ZERO3 V316 GATE=PASS`; `G3_BLOCKED G5_BLOCKED NO_OTA`.**

### Revisione critica prima della decisione

Una prima CI [39014965037](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39014965037) era tecnicamente verde già con 2.048 record. Nell'audit successivo ho corretto **due debolezze reali**: (a) il contratto owner allora vietava duplicati per istanza, ma consentiva due diverse istanze di Authority per lo stesso episodio; ora la registrazione di ownership è univoca **nel processo** e il test ne prova il rigetto; (b) dopo ripresa da backup con `active.mf` corrotta, la creazione della nuova generazione non deve copiare un manifest non valido sopra quello di rollback: il precedente viene ricostruito **solo dal blob verificato**. La CI finale 39015142772 ha superato una prova aggiuntiva di *recommit dopo rollback*; la versione precedente è superata. Il formato ora conserva anche `sourceRevision` dell'intenzione. Non effettuare patch posteriori al corpo o ricontare le scelte come neuro-apprendimento.

### Limiti precisi e prossimo gate

- Questo è un **journal di esperienze E2**, **NON un `BrainSnapshotV3`**. Non contiene pesi/optimizer/gru/neuroni/skills, né può far ripartire una vita fisica dal medesimo corpo: per ciò serviranno snapshot veri di corpo, RNG, world e policy.
- L'atomicità provata è su GitHub Actions Linux con `ATOMIC_MOVE` e `fsync`; **non** è validazione Android/Galaxy S21, battery-loss o filesystem di produzione. Un evento in RAM successivo al checkpoint non è durabile. Le proprietà di sync della directory sono best effort dove supportate: non promettere recupero garantito da power loss su ogni dispositivo.
- `IntentAuthority` impedisce un secondo proprietario **nello stesso processo**; dopo morte/restart le autorizzazioni devono derivare da recovery di episodeId/controllerRevision e da un vero coordinatore centrale, non da una static map. Il nome del controller del laboratorio resta una fonte *test-only*, non un actor neurale.
- Energia/danno restano assenti (flag non disponibili); la serializzazione rifiuta falsi flag di disponibilità nel lab, non inventa uno stato di sopravvivenza. Da integrare gli input reali prima del rilascio.
- La nuova UI 3.0, memoria di abilità, GRU, encoder neurale, plasticità, neurogenesi, decisioni autonome e G3/G5 rimangono **NON completati**. I 2.048 eventi NON provano miglioramento di ZERO nel camminare.

**Prossimo deliverable secondo il Master:** prototipo di coordinatore `MotorIntentV3` → cortex con provenienza univoca e vero encoder temporale su questi frame, train/holdout disgiunti e confronto con baselines, poi E3 **closed-loop** su minimo 2.000 decisioni **scelte dal nuovo actor**, con retained skill/memoria e mondi mai usati per fitting. Prima dell'APK servono anche checkpoint completi/resume del brain e QA salvataggi 12 slot, CPU/RAM Galaxy S21 e UI 3.0 con dati read-only.

**Stato release:** PR #87 resta DRAFT, nessun merge su produzione; OTA stabile **0.11.6-ota.172**; i 12 slot 2.2 non sono stati modificati.

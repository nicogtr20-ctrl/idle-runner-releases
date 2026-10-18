# ZERO 3.0 — V3.15 | SensoryFrame reale e recorder causale exactly-once

**Protocollo congelato 17/10/2026 prima di implementare ed eseguire il laboratorio.**

Consultare [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [Specifiche UI](ZERO30_UI_UX_SPEC.md), [V3.14 — failed CAL/BLIND](ZERO30_MOTOR_V314_PREACTION_VISION.md). La V3.14 ha dimostrato che aggiungere 16 raggi alla regressione non è sufficiente: evitare altre regressioni finché non esistono contratti dati accurati e registrazioni causali.

## Scopo e confini

Implementare **codice Java concreto** isolato e tipizzato (`Zero3SensoryFrameV315`, `Zero3CausalRecorderV315`) destinato a costituire una base *in prova* per `SensoryFrameV3`, `ActuatorFrameV3`, `PhysicsOutcomeV3` e `ExperienceV3`. Non è ancora l'intera architettura ZUAB, non decide intenzioni, non addestra un cervello, non espone rete neurale/G3/G5, non modifica `GameActivity`, fisica o 12 slot 2.2, non cambia l'OTA.

Il frame:
- Riporta schema version, `episodeId`, `physicsFrameId`, `simulationTime`, sampling phase `pre/post`, centro materiale/velocità misurati, `bodyAngle` dedotto dal tessuto, e provenienza esatta per i moduli sensoriali, con valid-mask e indisponibilità esplicita: **energia/danno non sono inventati se il laboratorio non li simula**.
- Integra da vere classi di produzione gli array indipendenti **96×12 locali**, `touchSense`, `touchPulse`, `gripHold`, `gripCommand` separati, forma/deformazione e velocità locali, sguardo per patch dal `DistributedBodySense`. Usa propriocezione `BodyProprioception.sense` reale, 74 particelle/7 canali `FluidInteroceptionV1.sense` se presente, **16 raggi** dalla vera membrana attraverso `MaterialGeometry.eye` e `SensorimotorMind.Senses.firstSolidDistance`, come il rilevatore `SpatialPerception`; ultimo vettore **64 efferenze eseguite**, distinto dall'azione *che verrà scelta*.
- Sono lecite le coordinate del PROPRIO corpo per la prova di cinematica/odometria, ma NON arrivano al frame il `physicalLaw`, ID segreto, obiettivo, coordinate di risorse, esito futuro, reward fittizio o stato futuro. Copie difensive su input/output, snapshot congelato, checksum deterministico. Nessun getter deve offrire un alias mutabile al solver. Campionare non deve compiere azioni o aggiornare pesi, retina o geometria.

Recorder con machine-state:
1. `begin(preFrame)` una volta quando `phase=PRE`, verifica indice e passo attesi;
2. viene realmente eseguito `64D action(t)` sul gel;
3. `commit(action(t), postFrame)` una volta dopo *tutti* i solver mondo, verifica `post.frameId=pre.frameId+1`, timestamp `dt=0,025s`, `post.lastExecuted==action(t)`, stesso episodio, revisioni/provenance, calcola effettivo delta COM, condizioni di contatto/prese osservate;
4. non accetta duplicati, salti di passo, epoche incoerenti, action NaN/out of range, state hash alterato, snapshot da un altro episodio o esiti pre-solver. Non sovrascrive una decisione esistente. `train/cal/blind` attribuito SOLO all'episodio prima delle osservazioni, mai al singolo frame arbitrariamente.

La specifica Master richiede anche `MotorIntentV3`, persistenza atomica, encoder, neurogenesi, memoria e UI futura: **qui la fonte delle intenzioni è il controller di eccitazione fisica del laboratorio** esplicitamente dichiarato, non una politica 3.0 autonoma. Gli output sono actions realmente eseguiti, non un claim di apprendimento.

## Campione preregistrato

**16 episodi × 128 decisioni fisiche = 2.048 registrazioni causali complete**, ciascun episodio ha 96 frame di warm-up prima della prima decisione (`16×(96+128)=3.584 step del gel`), `dt=0,025`. Quattro famiglie di mondo, quattro episodi ciascuna: `FREE`, `BOX_NEAR`, `DISK_NEAR`, `BOX_FAR`, tutte sul solver reale e con traiettorie/corpi/seed fisici indipendenti. Comandi di corteccia eseguiti per frame veri (tutti i 64, con eccitazione deterministica per identificazione motoria e comando di grip ON/OFF); prese prodotte SOLO da `SquareGripPuzzle.constrain`, `MaterialContactConstraints.reconcile`, `SymbioteWorldStepV171.interior`, `GripSurfaceComposition.compose`; dischi generano `touchPulse` tramite `Morphology.collide`. Visione da sensori veri, mai iniettata. Nessun corpo teletrasportato verso un bersaglio.

Split **per interi episodi**, etichetta assegnata prima di creare l'organismo: 8 FIT / 4 CAL / 4 BLIND, con almeno una scena di ogni tipo in ciascuna, `episodeId` non riutilizzato. **Nessun parametro viene addestrato o scelto qui**, dunque non si deve vantare generalizzazione neuronale in base ai 4 episodi BLIND.

## Gate obbligatorio del contratto V3.15

- CI compila i componenti con i veri moduli di produzione, non con stub; sorgenti Android di `GameActivity` esclusi, nessuna build OTA.
- 2.048 `Experience` unici, ognuno PRE → 64 comandi realmente applicati → POST, indice e tempo crescente, checksum stabile; 3.584 step di gel e nessun invalid material.
- Array 96×12, propriocezione globale vera, 74×7 interocezione reale, 16 raggi con provenienza, e 64 comandi precedenti *non confusi* col nuovo output; valori finiti dove maschera vera. Attributi `energy`/`damage` indisponibili e NON normalizzati in falsi zero; assenza dichiarata nei rapporti.
- Mutare array sorgente o copie restituite non modifica `SensoryFrame`; il solo read dei sensori non muove corpo/solidi o `ticks`; proprietà di immutabilità.
- Controlli negativi: FREE e BOX_FAR zero `touchSense`/`gripHold`; BOX_NEAR e DISK_NEAR almeno un contatto vero `touchSense`/`touchPulse`, senza imporre che avvenga artificialmente; hold richiede comando legale e timer di oggetto. Raggi FREE nessun ostacolo, BOX/DISK con visione leggibile prima di urto in almeno una scena. Bloccare eventuali due proprietari simultanei.
- Provare tentativi di duplicazione di `begin`, `commit`, index skip, azioni illegali, episode mismatch, timestamp e action-hash manomessi: tutti devono essere **rifiutati**; su errori la registrazione valida precedente resta valida, senza un falso evento.
- Reportare conteggio delle esperienze genuine, sensor provenance, frame invalidi, contatti, tenute, checksum/immutabilità e percentuale di metadata mancanti. Se uno dei test obbligatori fallisce, **GATE INFRASTRUTTURA V3.15 FAIL**; nessuna promozione delle componenti come E3.

Nota critica: le **2.048 sono decisioni/azioni fisiche reale in laboratorio**, NON 2.048 esperienze **autonome apprese da ZERO**. Anche con gate PASS, niente promozione G3/G5, OTA o UI, finché un actor/cortex che *sceglie* e *impara* non supera test in ciclo chiuso, memoria e retenzione.

## Risultati verificati

Risultati verificati sul run finale riportati sotto.

## RISULTATI VERIFICATI — RUN DEFINITIVO 38989788378 (17/10/2026)

**[GitHub Actions run 38989788378 — CI SUCCESS e gate infrastrutturale V3.15 PASS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38989788378)**. Compilazione reale dei sorgenti di produzione e delle classi nuove `Zero3SensoryFrameV315.java`, `Zero3CausalRecorderV315.java` e `Zero3SensoryContractV315Regression.java`, sul branch sperimentale di PR #87. **Nessun file della fisica produttiva è stato modificato.**

Il primo run `38989685989` non ha completato l'esperimento: il test si aspettava `IllegalArgumentException` dalla lista `Collections.unmodifiableList`, mentre Java genera correttamente `UnsupportedOperationException`; correzione unicamente del gestore di eccezione del test, senza cambiare codice fisico/soglie del protocollo. Il run successivo `38989721423` aveva già passato tutta la prova; il run definitivo `38989788378` ha soltanto corretto il nome di un contatore: il `touchPulse` può essere prodotto sia da **scatole** sia da **dischi**, non soltanto dai dischi.

### Copertura concreta

| Invariante/prova | Esito misurato |
|---|---|
| Esperienze fisiche complete (PRE→azione→POST) | **2.048/2.048**, su 16 episodi |
| Partizione irrevocabile per episodio | **FIT 8 / CAL 4 / BLIND 4**, ognuna con tutte e quattro le scene |
| Step effettivi del gel di produzione | **3.584** (96 warm-up+128 decisioni per episodio) |
| Snapshot sensoriale locale | **96 siti × 12 canali** non aggregati |
| Vista ottica reale (origini membrana) | **16 raggi**; **1.024** frame pre-azione con un ostacolo rilevato |
| Propriocezione globale | **48 canali** da `BodyProprioception` |
| Interocezione fisica | **74 particelle × 7 canali** da `FluidInteroceptionV1` |
| Efferenza | **64 comandi realmente inviati al solver per decisione**, precedente efferenza distinta dall'attuale |
| Frame con `touchSense` scatola reale | **512** (solo BOX_NEAR) |
| Frame con `touchPulse` in tutte le scene | **741**, di cui **229** nel gruppo DISK e **512** nel gruppo BOX_NEAR |
| Frame con `gripHold` legale | **252**, esclusivamente BOX_NEAR; ogni patch held ha un comando di adesione sopra soglia e proprietario scatola reale |
| Onset di contatto distinti | **7** (diagnosi, troppo pochi per una prova rigorosa di predizione onset) |
| Contatti falsi in FREE e BOX_FAR | **0**; nessun ostacolo ottico in FREE |
| Energia e danno non forniti dal laboratorio | **2.048/2.048 dichiarati NON DISPONIBILI**, senza valori inventati |
| Mutazione copie e aliasing | **PASS**, valori ritornati come copie difensive, originale integro |
| Rejection duplicati/out-of-order/mismatch | **PASS**: double begin, double commit, frame saltato, episodio errato, tempo falsificato, comando NaN o diverso da efferenza dichiarata, post senza incremento reale `gel.ticks` |
| Checksum diagnostico aggregato | `761c0f0ff3746651` (non è una firma crittografica) |

**Confermata continuità post→pre:** lo stato fisico di `POST(t)` deve coincidere con quello di `PRE(t+1)` (senza tick nascosti) per coordinate/velocità, tutti i 96×12 valori, 16 raggi, 48 dati di propriocezione, 74×7 segnali interni e ultimo output 64D. Un frame non può essere registrato due volte né saltato. `gel.ticks` cresce di **un solo tick autentico** tra PRE e POST, quindi non basta falsificare `frameId` o `simulationTime` per inventare una esperienza.

**Confine scientifico esatto:** la misura è una registrazione in RAM e il controllo dei comandi forniti allo stesso solver nel *lab*. Non è ancora un archivio persistente su disco resistente a crash, un sistema crittografico anti-manomissione, un certificato indipendente delle azioni sul dispositivo, né un cervello che apprende autonomamente. E2 usa un controller di eccitazione predefinito e controlli grip ON/OFF per produrre esperienze verificabili. L'indicazione `energyAvailable=false`, `damageAvailable=false` rappresenta dati assenti nel laboratorio, non un'energia fisiologica falsamente zero.

### Decisione GATE e ordine di sviluppo

**V3.15 INFRASTRUCTURE GATE = PASS**: esiste una prima implementazione Java reale e verificata del confine `SensoryFrameV3` / `ActuatorFrameV3` / `PhysicsOutcomeV3` / `ExperienceV3` per E2. **Non è l'implementazione completa** del contratto Master: `MotorIntentV3` e il suo single decision owner, validità/maschere per tutti i sensori in gameplay, energia/danno reali, identità temporale di decisione su più subframe, replay persistente atomico, `BrainSnapshotV3`, memoria procedurale, encoder ricorrente, corteccia motoria che sceglie, neurogenesi e UI 3D restano da implementare e testare. La futura vista ufficiale va alimentata da snapshot veri, non contatori dimostrativi.

**Passaggio successivo:** rendere persistenti e recuperabili atomicamente le esperienze, definire il vero `MotorIntentV3` e la proprietà dell'intenzione, poi integrare l'encoder ricorrente e costruire E3 a ciclo chiuso con una politica che genera *le proprie* 2.000+ decisioni, retention su nuovi seed e reali differenze contro una baseline congelata. Non chiamare mai i 2.048 replay V3.15 una locomozione imparata.

**Stato produzione: ZERO 3.0 NON ATTIVO; G3/G5 BLOCCATI; PR #87 Draft; nessun merge, OTA172 invariata, 12 slot 2.2 intatti.**

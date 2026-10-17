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

*Da aggiungere soltanto dopo GitHub Actions e lettura dei log.*

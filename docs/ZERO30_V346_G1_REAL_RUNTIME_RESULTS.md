# ZERO 3.0 — V346 G1 real runtime post-WORLD: 2/4 FAIL, no physics promotion

**18 ottobre 2026.** [MASTER §16/§20](ZERO30_MASTER_BLUEPRINT.md), [protocollo prereg](ZERO30_V346_WORLD_JOINT_RUNTIME_PROTOCOL.md), [tracker G1–G10](ZERO30_G1_G10_EXECUTION_TRACKER_20261018.md). Evidenza: **[GitHub Actions 39080557811 tecnicamente SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39080557811)**; successo tecnico della suite non significa G1 PASS.

Confronto con **il vero `Zero3RuntimeV325.advanceOneDecision`**, non col solo driver V339, stessa scena/seed/attore per ciascun braccio ma politica a feedback proprio indipendente. Quattro scenari da 400 decisioni vere = massimo 4.800 passaggi materiali/scena, con stanza e cancello `room.solveGate`, BOX, dischi, seconda BOX nello scenario 2. Baseline standard salva validamente la sua snapshot V328; il candidato experimental V338D è abilitabile SOLO prima di una nuova vita, è esplicitamente non persistibile, incontra contatto con la stanza e applica vincoli BOX post-tick, nessuna mossa/presa pilotata.

| Mondo | Baseline `ORIGINAL_V325` | `POST_WORLD_CANDIDATE` | Soluzioni candidate | Joint non risolte |
|---|---:|---:|---:|---:|
| scena0 1 BOX | **400/400** | **324/400 — FAIL** | 4 | 1 |
| scena1 1 BOX+DISK | **400/400** | **400/400 — completo** | 0 | 0 |
| scena2 2 BOX+DISK | **400/400** | **352/400 — FAIL** | 181 | 2 |
| scena3 1 BOX+DISK | **400/400** | **400/400 — completo** | 0 | 0 |
| **totale** | **4/4 scenari completi** | **2/4 completi** | **185** | **3** |

Ciascuna interruzione candidate è un veto reale `V346 real final-WORLD membrane topology unresolved`, non un numero mancante riempito con fake esperienze. Il primo CI test di questa scena [39080436314](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39080436314) rilevò che il fixture cercava di leggere il metabolismo V323 **a metà esperienza fisica** dopo un veto; correggendo il fixture per misurare solo l'ULTIMO stato completato, il corpo non è stato alterato. Un secondo run [39080517637](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39080517637) fallì per collisione fra due nomi di variabili Java; corretto solo il test. Infine i dati fisici sopra sono stati validati dal CI 39080557811 e Android Debug `BUILD SUCCESSFUL in 54s`.

**Conclusione obbligatoria:** V338D ***non*** è stato dimostrato robusto nel gameplay runtime V325 real e NON deve sostituire il solver stabile. 185 repair positivi non compensano i 3 unresolved. Inoltre lo screening non ha misurato in modo preregistrato vera bilateral di entrambi i bracci, quindi **bilateral preservation NOT_TESTED**: non attribuire "PASS" per mancanza dati. G1 resta APERTO. V346 codice di ricerca è solo flag lab e fail-closed senza snapshot, default physics immutata. Nessun APK firmato, no OTA, no merge e i 12 slot 2.2 non vengono toccati.

**Prossima causa da isolare, senza force motor:** raccogliere primo `Zero3JointBoxConstraintV338.measure` impossibile al frame fisico dei casi candidati324 e352, con classi di conflitto membrane vs BOX vs gate e stato dei patch che hanno vera owner grip; verificare se risolutore monotono non può trovare una trasformazione che soddisfi TUTTI i solidi simultaneamente. Non far finta che un solver che risolve una BOX alla volta possa sempre risolvere due BOX e una porta. Il test 400×4 è ancora sotto i 10k richiesti per affermazioni sull'apprendimento, ma sufficiente a RIFIUTARE promozione di questa candidata G1. Restano G3–G10 aperti.

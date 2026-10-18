# ZERO 3.0 — V348 | GRU64 + organismo nella stessa generazione atomica

**18 ottobre 2026 — PR #87 DRAFT — NON OTA.** Vincoli: Master §§1,3,13,14,20; Contratto G4/G7; registro capacità verificate. Non rifare GRU V345 né archive V328/329: questo intervento completa il solo collegamento mancato della persistenza V347.

## Realizzazione
- `Zero3OrganismStoreV322.State` mantiene il formato 322 bytewise invariato per il precedente actor V317. Introduce il formato alternativo 348, con la **stessa** fisica 96/74, gel, 64 efferenze, V319 grip, cortex, trace e un blob integrale GRU V345 (64 hidden, gates, actor, due critic, predittore, PRNG, counters).
- `Zero3SegmentedArchiveV328` mantiene i segmenti immutabili V328. Il **manifest unico** ora distingue 328 oppure 348, con SHA-256 e due generazioni. Non salva una GRU in un sidecar indipendente che possa divergere dal corpo. Snapshot V324 legacy continua a rifiutare GRU, anziché sovrascriverla.
- `Zero3RuntimeV325` salva la GRU come unico decision owner attivo; dopo cold restore ripristina authority V347 al cursore verificato. L'encoder V317 rimane **inattivo** (zero updates). `Zero3AndroidSessionV325.startNewGruResearch` è opt-in e isolato nel percorso privato V3, non nei 12 profili 2.2.
- Default V317 e archivi V322/V328 storici rimangono compatibili e senza modifica comportamentale. Il solver V346 RESPINTO resta non persistibile.

## Test preregistrato V348
`Zero3GruWholeLifeV348Regression` confronta due vite fisiche identiche e indipendenti: 48 decisioni ininterrotte contro 24 + checkpoint + cold JVM reload + altre 24. Condizioni: stessi byte completi organo/GRU/cortex/grip/gel/BOX, metabolismo, stanza, tutti i replay sensoriali PRE/12×64/POST, un solo owner, contatori coerenti, niente fake fuel, schema 348 verificabile. Corruzione ultimo manifest -> rollback **esatto** a 24 e prosecuzione; doppia corruzione -> rifiuto senza reset. CI nuova con compilazione e regressioni V344/V345/V347 + Android Debug.

## Limiti / G4 G7
Questo non prova locomozione autonoma G3, valore/critic appresi sul blind, GRU duration 0.25–0.65 realmente rispettata (nel V325 rimane 12×0.025=0.30s), modello del mondo affidabile G5, memoria delle skill/neurogenesi G6–7, 10k resume, mondo completo, UI G8, device S21 G9 o OTA G10. **Non promuovere G4/G7 completi finché questi gate non passano**.

### Stato
**RISULTATI VERIFICATI:** [CI V348 39084739084 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39084739084): `V348 GRU_WHOLE_LIFE_ATOMIC=PASS`, 48 ==24+reboot+24, true GRU/physical/metabolic journal bitwise equal, rollback valido, doppia corruzione rifiutata; Android Debug compilato. È un gate di integrazione/restart di laboratorio, NON gate cognitivo e NON prova Galaxy S21. Nessun merge/publisher/OTA e zero modifiche ai vecchi slot.

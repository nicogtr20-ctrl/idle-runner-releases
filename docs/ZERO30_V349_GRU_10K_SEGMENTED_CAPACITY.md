# ZERO 3.0 — V349 | Archivio GRU64 a 10.000 decisioni, senza regressioni V317

**Protocollo 18 ottobre 2026, PR #87 DRAFT.** Non modificare le funzionalità già approvate dal Master né i 12 cervelli ZERO 2.2. G7 rimane aperto: il requisito qui è soltanto la capienza persistente della **stessa vita V348**.

## Deficit verificato prima del codice
La V348 serializza correttamente la GRU64 + organismo + energia + mondo + replay, ma `Zero3SegmentedArchiveV328.MAX_DECISIONS=3500` limita entrambe le varianti. `Zero3SurvivalLedgerV323.State.decoded` rifiuta `n>4100` e una traccia di lunghezza maggiore di 262.144 byte. Per 10.000 decisioni autentiche servono **560.053** byte metabolici (`53+56*n`), oltre a 10.000 tracce fisiche in uno snapshot verificato.

## Implementazione delimitata
1. Manifest e organismo **schema 348 GRU**: `MAX_DECISIONS=10000`, validazione schema-scoped `maxFor(true)`; 10.000 record metabolici con lunghezza esatta fino a 560.053 byte. Manifest massimo 2 MiB, blob organismo massimo 2 MiB, chunk 32, checksum e due generazioni **immutati**.
2. Organismi e manifest **schema 322/328 V317**: limite 3.500, decoding manifest storico e medesimi byte di V328. Nessun passaggio automatico o migrazione dei 12 slot 2.2.
3. Android Lab mostra limite specifico, non pubblica OTA.
4. Nessuna ricompensa di movimento, nessuna manipolazione automatica, nessun nuovo controllo su gel/GRU/GRIP.

## Gate di verifica
- Invarianti `LEGACY_MAX_DECISIONS=3500`, `MAX_DECISIONS=10000`, `53+56*10000=560053`.
- Regressioni complete V348, 2000 decisioni legacy V328, G2/G4 e compilazione Android debug.
- **Nuova CI V349**: stessa vera GRU64 attiva, 4100 decisioni in una vita con 12 substep fisici ogni decisione, vera fisica e consumo/contatto energetico. Confronto 2050 + restart JVM + 2050 vs vita mai interrotta; SHA256 dell'intera memoria, cervello, corpo, energia, stanza e tutte le esperienze; corruption rollback e fail-closed.
- Le fonti blu create nella fixture sono fisicamente raccolte attraverso il solver (nessuna energia fittizia aggiunta). Questa è una prova **del salvataggio**, NON di sopravvivenza acquisita o ricerca autonoma.
- **Non dichiarare 10.000 esperienze verificate** prima di un ulteriore run effettivo a 10.000; questa modifica aumenta la capienza, la prova iniziale indipendente supera 3.500.

## Gate che restano aperti
G1 integrità solver e BOX; G3 locomozione migliorata; G4 clock GRU variabile / critic e policy scientificamente provati; G5 world model; G6 value/skill; G7 neurogenesi; G8 UI completa; G9 Galaxy S21; G10 OTA. Nessun merge prima del superamento degli altri gate.

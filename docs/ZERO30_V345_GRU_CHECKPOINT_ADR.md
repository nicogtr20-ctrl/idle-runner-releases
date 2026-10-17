# ZERO 3.0 — ADR-345 | Checkpoint integrale GRU actor–critic, prerequisito G4/G7

**18 ottobre 2026.** [MASTER](ZERO30_MASTER_BLUEPRINT.md) §§3, 6, 9, 11, 13, 14, 20; [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md) §§2, 3, 6; [audit G0–G10](ZERO30_IMPLEMENTATION_GAP_AUDIT_20261018.md). PR #87 DRAFT.

## Esito del V344 e blocco che impedisce attivazione prematura

[CI V344 runtime reale 39079361100 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39079361100): `Zero3CausalVitalBoundaryV344` verifica PRE e POST di vera energia V323 con bilancio fisico, senza alterare i valori V317; `Zero3RuntimeV325` lo usa realmente. L'archivio V328 conserva in byte-equivalenza il cervello/corpo/energia della vecchia sessione; l'outcome di display ephemeral non viene inventato al restore. Android Debug PASS. La prima CI 39079314732 fallì per lista incompleta di dipendenze di compilazione (`Zero3ContactTopologyExperimentV336`), non per scienza; correzione solo workflow.

Il MASTER vuole un solo decisore con GRU 64, critic e world model. Esiste `Zero3RecurrentBrain` con GRU/actor/doppi critic/predittore **fuori** dal runtime `Zero3RuntimeV325`. Collegarlo senza serializzarne **tutto** lo stato (pesi, hidden context, RNG, contatori, metriche di validation) farebbe perdere memoria al `saveSegmented/loadSegmented` della app.

## Prereg ADR V345

- Aggiungere SOLO al componente esistente `Zero3RecurrentBrain` la serializzazione `checkpointAtBoundaryV345/restoreAtBoundaryV345` **solo quando non è pending alcuna scelta**. Schema MAGIC+VERSION+dimensioni SENSOR/HIDDEN/MOTOR/ACTION+RNG+contatori+metriche+parametri+stato ricorrente e checksum CRC32 (e limite dimensione), nessun nuovo neurone fittizio.
- Leggere e validare il pacchetto **interamente prima di modificare** un qualsiasi parametro dell'istanza; corruzione, troncamento, formato/dimensione sbagliato, residui, non-finite o restore su un'istanza già allenata devono fallire senza mutazione.
- Verifica: stessa sequenza di dati reali o synthetically generated bounded observations dopo checkpoint => scelta `MotorIntent`, tutti i pesi, 64 neuroni ricorrenti, conteggi aggiornamenti e metriche riprodotti **bit-for-bit** per almeno 32 decisioni successive. Test di heldout che non alleni, stop e resume quando non c'è una decisione in corso; zero doppio credito.
- Non modificare fisica, motori, corpo, `Zero3RuntimeV325`, formati V328/OTA o reward. Questo è prerequisito tecnicamente dimostrabile, **NON equivale a integrare il brain GRU come owner runtime**, né a completare G4 o G7. Successiva integrazione richiede versione separata/upgrade dell'archivio V3 e test di ricostruzione totale neurone+corpo+metabolismo prima di assegnargli scelte reali.
- Testare `Zero3RecurrentBrainRegression` esistente ed eventuale `Zero3RecurrentBrainCheckpointV345Regression`, più Android Debug; dichiarare esplicitamente gate ancora aperti.

**No OTA, no merge, stable OTA172 e 12 slot 2.2 intatti.**

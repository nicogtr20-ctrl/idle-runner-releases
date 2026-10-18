# ZERO 3.0 — ADR-344 | Primo collegamento runtime, fonti sensoriali vitali e credito vero

**18 ottobre 2026. Pre-commit design record, vincolato a [MASTER](ZERO30_MASTER_BLUEPRINT.md) §3, §5, §9, §13, §20 e [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md) I-01..I-09.** Seguire [audit dei gap](ZERO30_IMPLEMENTATION_GAP_AUDIT_20261018.md). Implementazione da effettuare solo in `feature/zero30-foundation-g0-g3-20261016` e PR #87 DRAFT, nessuna scrittura alla branch OTA né ai 12 slot V2.2. **NON è un nuovo programma di training sui seed consumati.**

## Problema accertato e ordine di progetto

Il sistema runtime Android V3 è `Zero3LabActivityV326 → Zero3AndroidSessionV325 → Zero3RuntimeV325`. Esso HA già vera `Zero3SurvivalLedgerV323` e passa la riserva misurata e il danno PRE completato in `V317.Encoder.before(pre,ratio,previousDamage)` dal V327. Gli ultimi benchmark V342 non usavano questo percorso e falsamente facevano apparire l'interocezione inesistente. Inoltre gli hash V315 dichiarano correttamente vital `unavailable` **nello snapshot puramente muscolare**; la sovrapposizione V327 è reale ma non è ancora un oggetto causale unico con informazione di provenienza e esito.

**Intervento singolo, senza cambiare il comportamento:** creare il confine tipizzato `Zero3CausalVitalBoundaryV344` di sola lettura: PRE cattura e valida in modo immutabile `Zero3SensoryFrameV315` reale + `Zero3SurvivalLedgerV323` prima della decisione; il runtime usa **gli stessi valori numerici** già inviati al V317. Dopo 12 vere substep e `metabolism.finish`, POST produce un risultato misurato e coerente (delta riserva, raccolta, basale, lavoro convertito in spesa, danno, fonti effettivamente consumate), senza alcuna ricompensa per mera locomozione/grip. Verificare bilancio energetico e hash, episodio e numero decisione, decisioni contigue, marker POST, ticks e impossibilità di leggere esito futuro come PRE. Il diagnostico runtime `Display` espone i valori POST dell'ultima esperienza **solo quando misurati**, non li copia in nuove sessioni riavviate come se fossero misurati; il prossimo passo ripopola i dati.

**Non** cambiare forme e hash degli archivi V315/V328, dimensione 20D V317, ottica, nessun output motorio, solver/collisioni, learning rate, raccolta, formato salvataggio, posizione BOX, ricompensa. Una CI PASS deve dimostrare impronta della vita intera bytewise identica al codice precedente tramite test di cold restore e determinismo. Gli esiti in sola RAM della diagnostica non sono nuova memoria.

## Gate preregistrati V344

1. Prova fisica PRE/POST contro contatori reali V323 (12 substeps/decision): deltaFuel = gain − basal − motor − damage (tolleranza 0.002 per arrotondamento float); nessun premio inventato e `energyGained` non viene attribuito prima del contatto.
2. PRE deve avere solo riserva misurata in `[0,100]` e danno **della decisione precedente**, non del ciclo incompleto; `V315.preAction=true`, identity/decisionId coerenti; POST ha id=PRE+1 e 12 ticks. Reject mismatch e fake future.
3. Diagnostic preview READ-ONLY `Display` non modifica prestazioni/motori/pesi/physics. Dopo cold restore il vecchio post-outcome ephemeral è `unavailable` (non fake zero), ma V323 conserva l'energia vera.
4. Regressioni obbligatorie V327 vital, V325 runtime 96 e 2000+ V328/V329 se compatibile in CI, test originali fisica/prese, `gradle assembleDebug`. Dati numerici identici al riferimento in reference vs cold resume.
5. Non dare G4 per chiuso: `Zero3RecurrentBrain` actor-critic rimane separato e richiede **nuovo checkpoint e adattatore causale**. Non tentare di sostituirlo nel runtime senza salvare hidden state, parametri, RNG, critic, optimizer e train/validation. G3/G5...G10 ancora gate distinti.

**Rollback:** eliminare la nuova boundary e ripristinare le 4 chiamate/field aggiunti al V325/V326; nessuna migrazione dei dati V3/V2, nessun OTA.

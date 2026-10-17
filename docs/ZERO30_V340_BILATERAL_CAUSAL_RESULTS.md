# ZERO 3.0 — V3.40 | Risultato diagnostico: il FULL non raggiunge simultaneamente due lati della BOX

**17 ottobre 2026.** Leggere [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [preregistrazione V3.40](ZERO30_V340_BILATERAL_CAUSAL_AUDIT.md), [G3 fail V3.39](ZERO30_V339_AUTONOMOUS_GAIT_RESULTS.md). **Esperimento completato e riprodotto**: [CI finale 39056801428 SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39056801428), branch sperimentale PR #87, NESSUN merge/OTA, G3/G5 ancora bloccati.

## Come è stata verificata l'identità dell'esperimento

`Zero3BilateralFaceAuditV340` misura read-only DOPO ogni vero WORLD le 96 patch `skin`, `skin.gripCommand`, i contatti geometrici contro le quattro superfici effettive di ciascuna BOX, `boxGripHold`, possesso per faccia, comando reale fra le 64 efferenze e `bilateralHeldNow`. **Non modifica** efferenze, reward, fisica, rete, timer o testa V319. `Zero3BilateralForensicV340Regression` replica il vero [test V3.39] con 32 nascite TRAIN 900..931 × OLD/FULL/FROZEN, poi tutte le 24 nascite BLIND 960..983 nell'**ordine originale**, leggendo le sei BOX_NEAR 961,965,969,973,977,981. Ogni braccio mantiene l'intero contesto cortex fra scene come nel benchmark originale. Ciascuna BOX ha 96×12=1152 frame fisici, cioè **6912 frame per braccio**. Non è un nuovo holdout: TRAIN e BLIND erano **CONSUMATI** e vengono soltanto riprodotti in osservazione.

Prima CI [39056222416 FAIL intenzionale](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39056222416): train pesa OLD3072, FULL2272, FROZEN0; ma al momento della valutazione erano state saltate le 18 vite non BOX, cambiando lo stato di contesto condiviso tra vite della corteccia. I bilateral OLD1556/FULL0/FROZEN1379 differivano dai dati originali OLD1546/FULL0/FROZEN1384: test corretamente FAIL. Dopo un erratum preregistrato, la seconda CI ha incluso tutte le 24 BLIND nell'ordine immutato, senza modificare le politiche: **OLD1546 / FULL0 / FROZEN1384 esattamente**, stesso training OLD3072/FULL2272/FROZEN0 e nessuna censura. Questo check è obbligatorio per ogni futura forensic con contesto ricorrente.

## Dati finali esatti osservati nei 6912 frame BOX per braccio

| Metrica geometrica/fisica | OLD_GLOBAL | FULL_LOCAL_DELAY | FROZEN |
|---|---:|---:|---:|
| Qualunque vero BOX grip | 6900 | 6900 | 6900 |
| **Contatto geometrico su lati opposti (TOP+BOTTOM oppure LEFT+RIGHT)** | **3858** | **0** | **3584** |
| Su lati opposti **e** comando adesivo >soglia | 1781 | **0** | 2179 |
| Su lati opposti con timer ownership BOX >=hold | 1767 | **0** | 2168 |
| Ownership su lati opposti con ≥10 patch di distanza | 1767 | **0** | 2168 |
| **Bilaterale reale riconosciuta dalla BOX** | **1546** | **0** | **1384** |
| Frame con entrambe facce coperte ma non entrambe comandate | 2077 | **0** | 1405 |
| Entrambe comandate ma una senza timer hold | 14 | **0** | 11 |
| Frame di contatto geometrico TOP | 4927 | 2562 | 4998 |
| Frame di contatto geometrico BOTTOM | 5818 | 3280 | 5473 |
| Frame RIGHT della BOX | 0 | 0 | 0 |
| Frame LEFT della BOX | 6912 | 6912 | 6912 |

Nelle vite FULL la TOP compare per 2562 frame e la BOTTOM per 3280, ma **non nello stesso frame nemmeno una volta** (`bothOppGeometric=0`). La presa generica LEFT resta valida per 6900 frame: il problema bilaterale **precede causalmente il verificarsi di due reali grip su facce opposte** e non può essere spiegato unicamente come scadenza prematura di due timer già presenti. Nemmeno una volta l'algoritmo è nella condizione geometrica sufficiente per afferrare entrambi i lati. Questa è una prova del **collo di bottiglia misurato**, non ancora della causa di apprendimento precisa che produce la forma.

Somma dei `Math.abs` delle 64 efferenze eseguite, raggruppate per canale muscolare (controllo di provenienza, non confronto esperimenti causali con un solo attuatore isolato):

| Canale delle 16 sezioni | OLD | FULL | FROZEN |
|---|---:|---:|---:|
| 0 | 3311.958 | 3654.347 | 3136.550 |
| 1 | 2334.965 | 2943.243 | 2385.198 |
| **2 — adesione V319** | **32378.831** | **26867.932** | **32412.278** |
| 3 | 2411.788 | 2165.086 | 2386.535 |

Il totale di comando al canale adesivo FULL è **−17,02% rispetto a OLD** e **−17,11% rispetto a FROZEN**. Questo differenziale da solo NON dimostra che sia la causa dell'assenza di copertura simultanea: la testa V319 è congelata con gli stessi pesi ma riceve **sensori e contatti differenti** dalle tre traiettorie indipendenti, quindi la differenza può essere causa, effetto o un'interazione con la morfologia. **Vietato** alzare arbitrariamente `gripCommand`, allungare `boxGripHold`, imporre due facce o cambiare cassa/punteggio per ottenere un PASS fittizio.

**Nota sulla categoria facce:** nessun braccio ha incontrato la faccia RIGHT in queste pose; la coppia opposta che produce adesione è TOP+BOTTOM e il LEFT resta il contatto singolo sempre presente. Distanza membrana ≥10 indici richiesta, ma l'assenza geometrica precede anche quel vincolo.

## Decisione e prossimi controlli

**Esito V3.40 diagnosi riprodotta PASS, ma G3 SCIENTIFICO ANCORA FAIL.** Nessun intervento motorio abilitato, perché la diagnosi non certifica ancora quale cambiamento dell'apprendimento preservi la presa e riduca il lavoro grezzo senza peggiorare la locomozione.

Per **V3.41** pre-registrare PRIMA del codice nuovi TRAIN/BLIND multi-famiglia, FULL/OLD/FROZEN con real closed-loop individuale e **lavoro muscolare grezzo `RAW_WORK<=110% OLD e FROZEN`** (non `work/(1+positiveSigned)` come nell'errore di reporting V3.39). Una modifica alla volta, preferibilmente analisi controfattuale *solo con true motor output* di una sottosezione della membrana, evitando qualsiasi target temporale forzato o forma prenotata su BOX. Richiedere copertura contemporanea TOP+BOTTOM vera, adesione bilaterale confermata, costo energetico e locomozione su 4 famiglie, con holdout indipendente non consumato. Il mondo/unità V338D andrà testato su reale ultimo WORLD e restart fisico+memoria dell'organismo completo: un checkpoint dei soli pesi cortex non basta a soddisfare la prova di continuità per 2000 decisioni in una vita.

**Stato branch PR #87 DRAFT, NO MERGE, NO OTA, G3/G5 BLOCCATI, legacy OTA172 e 12 slot V2.2 invariati, niente Galaxy S21.**

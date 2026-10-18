# ZERO3 — completamento cognitivo integrato V353

18 ottobre 2026. **ADR di obiettivo, non attestazione di completamento.**
Implementazione e limiti effettivi: [rapporto V353](ZERO30_V353_COGNITIVE_MODEL_RESULTS_20261018.md).
Opzioni/skill/probation del paragrafo seguente restano da implementare;
la memoria effettiva V353 contiene soltanto episodi TRAIN.

Master §§3,8–13,18–20; contratto G2/G4/G5/G6/G7/G8.

Riutilizzare GRU64, attore unico, cortex, presa V319, sensi V351 e clock V352.
Aggiungere al medesimo owner un modello del mondo con tre piccoli predittori
nonlineari action-conditioned, 12 uscite fisiche distinte e baseline di
persistenza sullo stesso campione. Partizione prima dell'azione: 1/5 decisioni
heldout, nessun update GRU/cortex/adesione/modello/replay del valore su heldout.

Pianificazione breve su proposte emesse dall'attore, permessa soltanto per
uscite affidabili: energia, danno e costo, con incertezza ensemble. Nessuna
coordinata o classe segreta. Previsioni non diventano esperienze. Diagnostica
read-only di pesi, unità effettive, campioni, errori, baseline e uso del planner.

Memoria episodica limitata agli esiti veri; opzioni motorie candidate sono
parametri scelti dall'attore e contesto osservato, con train/heldout distinti,
prove indipendenti e probation/rollback prima di diventare disponibili al
richiamo. La semplice frequenza non certifica una competenza.

Crescita shadow nel modello predittivo: aggiunta reale di unità e connessioni,
allenamento solo su train, comparazione appaiata su heldout non usati in train,
accettazione solo per miglioramento e nessuna regressione delle teste. La
crescita di un predittore non viene descritta come crescita della GRU.

Schema nuovo353 dell'intero checkpoint: GRU352 + modelli/memoria/crescita e
provenienza train/heldout nel replay. Vecchi schemi immutati e nessuna
conversione automatica. Budget massimo32 nuove unità, memoria bounded.

Prove: no-holdout-training, world targets da outcome reali, cold restart
bytewise, corruzione/fail-closed, crescita accettata/respinta con output reale,
planner disabilitato senza evidenza. Benchmark fisico indipendente e Android.
Non dichiarare G3 locomozione o valore appreso dalla sola build.

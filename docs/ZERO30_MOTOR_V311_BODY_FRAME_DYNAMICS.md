# ZERO 3.0 — V3.11: modello residuo motorio in riferimento corporeo, contatto e adesione

**Protocollo e soglie congelati il 17 ottobre 2026 PRIMA della prima esecuzione.** Consultare [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [esito V3.10](ZERO30_MOTOR_V310_LOCAL_FRAME_DYNAMICS.md). **Laboratorio E2 separato**: non modifica `GameActivity`, `SymbioteMatterV1`, 2.2, slot, OTA o UI.

## Ipotesi verificabile

La V3.10 ha fallito la generalizzazione: il modello FULL con 16 settori (72D) peggiora drasticamente rispetto a global-only, nonostante usi i veri 64 comandi per frame. L'ipotesi V3.11 NON è che i sensori siano inutili: la regressione lineare grezza in coordinate mondo può essere fragile a rotazioni, con dimensioni ridondanti e senza una prior inerziale.

Sviluppare una rappresentazione **compatta, nel riferimento anatomico misurato** con 22 variabili corporee: velocità nel sistema del tessuto, raggio/deformazione e primi armonici morfologici, indicatori reali di contatto/presa e distribuzione, moto del fluido, tangenziale del tessuto e area/forza. Il vettore azione riduce i veri 64 comandi applicati su ciascun frame a 24 statistiche per canale (media, prima armonica cos/sin, valore assoluto e pesi da grip/contact); NON nasconde i comandi e conserva il nastro 64D originale. Il modello è una regressione residuale **sulla ballistica** che prevede delta-stato, correzione allo spostamento del COM e variazione di asse anatomico. La trasformazione delle uscite dal frame corporeo al mondo usa l'asse della membrana; invarianti di rotazione da testare separatamente.

**Ablazioni preregistrate:** FULL (compatta + azioni + contatto), NO_ACTION (stesso stato senza efferenza), NO_GRIP_TOUCH (efferenza con azioni non condizionate e senza features di contatto/adesione), e riferimenti semplici (persistenza, zero spostamento, ballistica). Stessi dati e regularization fissa 8, normalizzazione esclusivamente FIT.

## Dati e separazione

Un solo vero gel per traiettoria: 96 frame di precondizionamento + 384 frame di 0,025 secondi. Perturbazioni temporali SOLO nel laboratorio, nessuna gait codificata nel cervello. Split per **traiettorie intere**, non frame:
- FIT: pose 0°/90°, condizioni del corpo 0/1/2, 4 direzioni, 4 canali, 2 zone, 2 polarità = **384 traiettorie**.
- CAL: pose ±22,5°, condizioni 0/1/2, stesse combinazioni = **384 traiettorie**.
- BLIND: pose **+17°, −39°, +71°, −86°**, condizione corporea 3, 4 direzioni/4 canali/2 zone/2 polarità = **256 traiettorie**.
- Totale 1.024 traiettorie × (96+384) = **491.520 step reali del solver**, con seed indipendenti per split, posa, condizione e perturbazione, e **nessuna informazione su posa nominale, ID dello split, risorse o outcome futuri nel predittore**.
- La separazione garantisce pose/sequenze distinte, ma NON nuovi mondi: l'arena del laboratorio e il solver restano quelli degli esperimenti precedenti. Le pose del BLIND diverranno consumate dopo il test.

## Metriche e regole decisionali congelate

- **One-step:** stato reale prima, 64 comandi applicati *prima* del solver, stato reale e dx/dy *dopo* il solver. Misurare MSE XY sul mondo, confronto ballistica (`v_world * 0,025`) e previsione di corpo persistente. Contatto e adesione sono **osservazioni fisiche**, non etichette nascoste.
- **Rollout libero (con action tape noto):** solo stato corporeo e asse iniziali + previsioni successive; per ciascuno dei 384 passi sono noti i comandi storici realmente eseguiti, NON gli stati futuri o le decisioni autonome future. Registrare posizione cumulata e MSE **a 16, 96, 384 frame** contro centro immobile e moto a velocità iniziale costante. Se il modello produce NaN/Inf, la prova fallisce. Vietato leggere caratteristiche corporee future nella catena.
- Calcolare ablation su CAL e BLIND senza riaddestrare o scegliere il modello in base al BLIND. Reportare n, MSE, regione grip/contact presente, spostamento; non usare un successo di CI come PASS di qualità.
- **Gate FULL preregistrato (CAL e poi BLIND indipendente):** 1) MSE XY one-step ≥10% migliore della ballistica; 2) MSE stato one-step ≥10% migliore della persistenza; 3) FULL ≥3% migliore di NO_ACTION e NO_GRIP_TOUCH sul movimento one-step; 4) MSE rollout 16 e 96 frame ≥10% migliore della migliore tra le baseline immobile/ballistica. Se uno fallisce, `FAIL` e nessun controller attivo.
- Controllo addizionale: *equivarianza matematica di frame*, rotazione artificiale dell'orientamento del medesimo stato/predizione di 90° senza cambiare feature body-frame. Questo è un test di coordinate, NON fisica di scene ruotate. Il vero test di fisica resta separato.

## Limiti espliciti

Modello lineare ridotto, non una rete neurale GRU, non una locomozione appresa, non un world model completo, non E3. Non utilizzare contatti/adesione simulati falsamente come presente se i valori restano zero: contarli. I benchmark precedenti V3.9/3.10 non sono baseline indipendenti in nuovi mondi. Non cambiare la fisica per far passare il gate. Anche un eventuale PASS richiederebbe vera locomozione autonoma e convalida con mondi e seed differenti prima di integrare il brain.

## Esiti verificati

*Da aggiornare esclusivamente dopo CI e lettura log, con evidenza numerica e difetti.*

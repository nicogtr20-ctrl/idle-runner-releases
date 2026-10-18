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

Risultati osservati e verificati qui sotto, senza promozione automatica.


## RISULTATI VERIFICATI — GitHub Actions 38986991649 (17/10/2026)

**[CI V3.11 38986991649 — SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38986991649): compilazione e 491.520 passi fisici completati.** FIT 384, CAL 384, BLIND 256 traiettorie, state 22D in coordinate del corpo, 64 comandi eseguiti ogni frame ridotti a 24 caratteristiche, regolarizzazione train-only. Test BLIND su pose +17°, −39°, +71°, −86°, precondizione 3, seed di eccitazione distinti. Anche rotazione geometrica di 90° dei vettori body→world verificata come proprietà di coordinate: **non è un test di locomozione sotto mondo ruotato**.

**ATTENZIONE CRITICA: contactFrames=0, gripFrames=0 sia in calibrazione sia nel BLIND per tutti i modelli.** `NO_GRIP_TOUCH` ha risultati **esattamente identici** a FULL. Il laboratorio usa il solver del gel ma NON esegue la parte di collisione con oggetti che, nel gameplay, popola i sensori di contatto e presa. Di conseguenza la capacità *contact-aware* NON può essere rivendicata o promossa, qualunque sia il successo sugli altri valori. In particolare, `touchSense` e `gripHold` rimangono nulli nell'arena libera; questo non implica che tali sensori siano rotti in produzione.

| CAL 384 traiettorie, MSE | FULL | NO_ACTION | NO_GRIP_TOUCH | Baseline pertinente |
|---|---:|---:|---:|---:|
| XY one-step | **0,000000350** | 0,000000418 | 0,000000350 | ballistica 0,000000545 |
| Stato one-step | **0,000059524** | 0,000444842 | 0,000059524 | persistenza 0,000466326 |
| Percorso 16 frame | **0,000285231** | 0,000708747 | 0,000285231 | ballistica 0,001020557 |
| Percorso 96 frame | **0,022706656** | 0,071493327 | 0,022706656 | immobile 0,135333921 |
| Percorso 384 frame | **0,235001864** | 0,311522015 | 0,235001864 | immobile 0,759308610 |

| BLIND 256 traiettorie, MSE | FULL | NO_ACTION | NO_GRIP_TOUCH | Baseline pertinente |
|---|---:|---:|---:|---:|
| XY one-step | **0,000000335** | 0,000000397 | 0,000000335 | ballistica 0,000000511 |
| Stato one-step | **0,000055284** | 0,000447383 | 0,000055284 | persistenza 0,000469229 |
| Percorso 16 frame | **0,000294541** | 0,000743767 | 0,000294541 | ballistica 0,000925721 |
| Percorso 96 frame | **0,023664288** | 0,064856512 | 0,023664288 | immobile 0,082884093 |
| Percorso 384 frame | **0,227423314** | 0,366619503 | 0,227423314 | immobile 0,539894823 |

**Risultato oggettivo positivo, circoscritto:** il modello FULL supera ballistica/persistenza e NO_ACTION con ampio margine in queste prove **senza contatti**, anche su pose nuove: nel BLIND migliora MSE XY a un frame del **34,4%** rispetto alla ballistica, stato one-step dell'**88,2%** rispetto alla persistenza, percorso a 96 frame del **71,4%** rispetto al controllo immobile. L'azione osservata è predittivamente utile rispetto all'ablation NO_ACTION. Su 384 frame migliora il riferimento immobile (0,2274 vs 0,5399), ma non è una vita con obiettivi o un cervello che sceglie nuove azioni.

**Gate FULL preregistrato = FAIL in CAL e BLIND** nonostante il miglioramento delle altre metriche, perché il requisito FULL ≥3% migliore di `NO_GRIP_TOUCH` è impossibile nel campione (zero eventi reali). La CI non inserisce automaticamente questo risultato nel gameplay. **G3 locomozione autonoma NON superato, G5 world model generale NON superato; ZERO 3.0 ancora sperimentale; NO OTA.**

### Diagnosi di integrazione del laboratorio (verificata sul sorgente del gioco)

Il vero solver `SymbioteMatterV1.step` comanda materia e muscoli, ma non sintetizza un falso tocco di scatola: il contatto oggetto viene elaborato dal mondo/collisioni dopo la fisica. In `GameActivity` il ramo delle scatole passa da `SquareGripPuzzle.constrain`, `GripSurfaceComposition.compose` e `SymbioteWorldStepV171.interior`; i dischi mobili passano da `Morphology.collide`. Senza quel mondo, i segnali specifici non possono apparire. L'assenza è **un difetto di copertura del test**, non prova che il modello non sappia usare il tatto. L'aderenza di presa vera deve essere causata da contatto e comando, mai scritta nel sensore dal benchmark.

### Decisione e passo successivo

Non ripetere le medesime pose ormai usate come BLIND, né ritoccare ridge o soglie a posteriori. Mantenere V3.11 come **primo modello libero/azione-condizionato con vantaggio predittivo E2 indipendente entro la stessa arena**; non promuovere il claim più grande. **Prima di sviluppare V3.12:** isolare una fixture di oggetti/box che richiami ESATTAMENTE i solver di collisione usati dal gioco e produca eventi fisici autentici; misurare la distribuzione contact/grip prima dell'apprendimento, poi validare la risposta a nuovi contatti con nuovi seed/pose e senza futuro nascosto. Separare generalizzazione alle pose, ai mondi e alla dinamica del contatto. Un esito E2 positivo non basta ancora per G3: servono vita autonoma, controllo in ciclo chiuso, skill appresa con retention e nuovi ambienti.

**GitHub: PR #87 Draft, OTA172 invariata.**

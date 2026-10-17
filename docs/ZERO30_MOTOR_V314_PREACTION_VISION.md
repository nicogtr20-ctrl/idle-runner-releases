# ZERO 3.0 — V3.14 | Vista fisica pre-azione + dinamica motoria con ostacoli

**Protocollo preregistrato il 17/10/2026 PRIMA dell'implementazione del test e dei risultati.** Riferimenti obbligatori: [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.12](ZERO30_MOTOR_V312_REAL_CONTACT_FIXTURE.md), [V3.13](ZERO30_MOTOR_V313_CONTACT_ACTION_LEARNING.md).

## Audit del sensore visivo esistente

`SpatialPerception` legge 16 raggi che partono dalla **vera membrana fisica**, usando `MaterialGeometry.eye`, `SensorimotorMind.Senses.firstSolidDistance` e una scansione da 176 unità (valore `RANGE`). Il raggio si arresta sul primo solido visibile, non vede oltre l'occlusione; `Senses` fornisce anche 8 raggi di prossimità e canali spettrali non semantici, `DistributedBodySense` può sondare 96 siti reali. Non fornire mai al learner identità fisiche, `physicalLaw`, nome oggetto, ricompensa o posizione di gemme; un raggio letto dal solver prima del movimento è una **percezione legittima**.

Contratto di causalità V3.14: `objectSpawn → physicalVision(s_t) → chooseExperimentalMotorCommand(a_t) → gel.step → world collisions → physicalVision(s_(t+1)) → record true outcome`. Tutti i dati `s_t` e i raggi devono essere campionati **prima del comando che produce l'esito**; i comandi sono veri e registrati, non un movimento fittizio. Il replay multi-step può ricevere il nastro di azioni già eseguite ma **NON** riceve raggi futuri/posizione oggetti post-step. Il corpo non è mosso o orientato verso la scatola dallo strumento.

## Domanda scientifica e modello congelato

Il modello V3.13 su vere collisioni falliva: `BLIND FULL XY1=0,000126998` contro `ballistic=0,000125836`; con contatto pre-esistente `0,000009129` contro `0,000006829`, e ablation senza tatto batteva FULL. L'ipotesi verificabile è che lo stato del solo corpo non possa predire un urto **in arrivo** senza vista sincronizzata.

V3.14 usa la stessa base V3.13 di **25 feature anatomiche** con 3 sensori separati `touchSense`, `touchPulse`, `gripHold` più **16 raggi visivi** su ogni frame (`41D` complessivi) nel **riferimento del corpo** tramite riallineamento deterministico di raggi-world misurati, *senza conoscere la posa nominale del laboratorio*. Per un evento di primo urto, `contactOnset` è etichetta valutata **solo a posteriori**, mai nel frame di ingresso del predittore. 64 comandi veri a ciascun frame, ridotti in 24 statistiche corticali; ridge multivariato con `lambda=8` fisso, normalizzazione FIT-only e predizione residua rispetto alla velocità. Le 16 osservazioni possono essere ricostruite dal sensore fisico di membrana `SpatialPerception.observeEnvironment`; il modello NON accede alla geometria geometrica dell'oggetto per collisione.

**Ablazioni predefinite:** FULL (corpo+vista+azione), NO_VISION (azzeramento 16 raggi e interazioni), NO_ACTION (stessa percezione senza efferenza), NO_TOUCH (come FULL, senza tattile/hold/pulse). Baseline principali: XY balistica in coordinate mondo, persistenza anatomica, COM immobile; onset usa un classificatore ridge allenato esclusivamente su FIT con letture visive a 16 settori + velocità e un ablation senza vista; Brier su tutta la partizione e sui soli onset veri, comparato con predizione prevalenza FIT e con NO_VISION. Tutte le metriche di regressione separate in totale, contact-onset e contact-already-present.

## Partizioni e budget fisico

**Un singolo corpo e solver reale per traiettoria**, warm-up 96 frame, misurazione 144 frame, dt 0,025 s; stessi veri solver del mondo della V3.13. Oggetti nascono **al solo inizio della misurazione** in posizioni fisiche fissate nell'arena rispetto al corpo, senza ri-orientare la volontà di ZERO. Cinque scene per posa: FREE, due BOX fisiche, due DISK fisici; posizione oggetto nelle cinque scene lungo azimut di laboratorio ±0,25/±0,45 rad rispetto ad asse mondo per evitare di addestrare soltanto un raggio orizzontale; i segni dipendono dal seed e dallo split. NON includere il segno, la distanza nominale o la classe fisica negli input del predittore. Le 16 letture visive sono gli unici dati prospettici esterni.

- FIT: pose **0° e +100°**, corpo condizioni 0/1/2, due seed e due stati comando adesione (ON/OFF), cinque scene = **120 traiettorie**.
- CAL: pose **−24° e +46°**, corpo condizioni 0/1/2, due seed distinti e comandi ON/OFF, cinque scene = **120**.
- BLIND: pose **+12°, −53°, +69°, −84°**, corpo condizione 3, due seed distinti, ON/OFF, cinque scene = **80**.
- Distanze (al momento spawn, prima dello step): FIT BOX 45/52, DISK 46/53; CAL BOX 47/54, DISK 45/51; BLIND BOX 44/50, DISK 47/52. Sono geometrie **nuove rispetto alle partizioni V3.13**, anche se le leggi del mondo e il pattern di eccitazione del laboratorio rimangono noti.
- Totale **320 traiettorie**, **76.800 passi veri del gel**, **46.080 transizioni post warm-up**, per l'ottimizzatore FIT prendere 1 frame ogni due, CAL/BLIND integrali. Nessuna miscela di frame delle stesse traiettorie fra i gruppi, nessun tuning CAL/BLIND. I test blind diventano «consumati» a conclusione.

## Gate rigorosi

- Tutte le feature e pesi finite; 64 comandi effettivi/frame; nessuna alterazione di `SymbioteMatterV1`, collisioni, body, salvataggi. Test geometrico: scena BOX avvicinata deve produrre segnali visivi pre-impatto non-zero, FREE zero ostacoli; un BOX dietro un altro ostacolo non deve vedere oltre il primo; riconoscere che raggio che parte già dentro un corpo solido può dare distanza zero reale.
- CAL e BLIND ciascuno ≥10 traiettorie con contatto e ≥8 eventi di *first contact onset*; se copertura insufficiente, **FAIL senza adattare scene sul blind**.
- **Predizione movimento** FULL MSE XY totale ≤90% della ballistica e ≤97% di NO_VISION e NO_ACTION; sui **frame di primo contatto** FULL MSE XY ≤90% della ballistica e ≤97% NO_VISION.
- **Risposta a contatto già presente** FULL MSE XY ≤90% balistica e ≤97% NO_TOUCH.
- **Rollout replay** di 16 e 48 frame (non vera pianificazione) FULL ≤90% della migliore baseline COM immobile/inziale balistica; 144 frame diagnostico. La versione senza vista deve avere il medesimo criterio di comparazione, e nessun modello promosso da sola CI verde.
- **Onset** prima dell'impatto: Brier complessivo almeno 5% migliore di un modello NO_VISION e della prevalenza FIT, non peggiorare il Brier sui soli onset veri (rispetto a NO_VISION). Onset è **poco frequente**: nessuna conclusione su detection se non ci sono almeno 8 eventi indipendenti nella partizione.
- Ogni requisito **CAL e BLIND** contemporaneamente; tutto ciò che manca = FAIL. La valutazione è E2 predizione, **NON** locomozione autonoma G3 né world model generale G5.
- Contare e dichiarare anche `contactOnset`, `contactAlready`, campioni withVisRays, HOLD veri, collisioni senza vista prospettica. Distinguere comparsa spontanea di oggetto da scelta del cervello, non attribuire apprendimenti inesistenti.

**Stato iniziale:** nessun modello V3.14 attivo nell'APK; l'UI ZERO 3.0 rimane in specifica; PR #87 Draft; OTA172 stabile e 12 slot intatti.

## Risultati finali CI

*Da aggiungere dopo un'esecuzione realmente verificata; nessun risultato presunto.*

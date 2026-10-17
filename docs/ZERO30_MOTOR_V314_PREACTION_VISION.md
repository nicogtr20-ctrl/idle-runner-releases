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

Risultati della CI completata riportati in seguito, con confronti preregistrati.


## RISULTATI VERIFICATI — CI 38988832620 (17/10/2026)

**[GitHub Actions 38988832620 — SUCCESS TECNICO](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38988832620)**. Compilati il vero gel, vista dalla membrana, `SensorimotorMind.Senses.firstSolidDistance` e solver di collisioni non modificati; completati **76.800 veri passi fisici** e **46.080 transizioni state/action/outcome**, 120 FIT + 120 CAL + 80 BLIND. Il modello usa 25 dimensioni di stato anatomico/tatto, 16 distanze ottiche reali della membrana, 64 muscoli eseguiti per frame compressi a 24 statistiche, split irreversibili e ridge normalizzato solo sui campioni FIT. La CI è verde perché il codice **ha completato la prova**, non perché abbia superato le soglie scientifiche.

**Copertura:** CAL ha 86/120 traiettorie con veri contatti, 24/120 con vere prese, **86 nuovi onset** dopo uno stato pre-step senza contatto, 8.162 frame con contatto già presente, 13.824 frame con un ostacolo visibile *prima* dell'azione. BLIND ha 52/80 traiettorie con contatto, **16/80** con presa, **52** nuovi onset, 5.155 frame già in contatto e 9.216 frame con ostacolo visto. La base FIT ha prevalenza primo-impatto **0,0072254** tra i frame ancora liberi. Nessun comando sensoriale o outcome viene inventato.

### Errore XY: one-step (MSE, minore è meglio)

| Partizione/metrica | FULL con vista | NO_VISION | NO_ACTION | NO_TOUCH | Ballistica |
|---|---:|---:|---:|---:|---:|
| CAL tutti i frame | 0,000099519 | 0,000091067 | 0,000099453 | 0,000099745 | **0,000089716** |
| CAL soli 86 onset | 0,011054272 | 0,011313556 | 0,011056069 | 0,011093020 | 0,011414981 |
| CAL contatto già osservato | 0,000084754 | 0,000071647 | 0,000085002 | 0,000084227 | **0,000069069** |
| BLIND tutti i frame | 0,000201097 | 0,000192722 | 0,000201075 | 0,000198028 | **0,000191035** |
| BLIND soli 52 onset | 0,033392324 | 0,033773282 | 0,033391249 | 0,033419703 | 0,033897774 |
| BLIND contatto già osservato | 0,000100472 | 0,000087031 | 0,000100517 | 0,000095575 | **0,000084383** |

La vista porta un miglioramento modesto sullo spostamento **nei singoli primi urti** (BLIND 0,033392 vs 0,033773 senza vista; ≈1,1%, inferiore al 3% preregistrato; rispetto alla ballistica ≈1,49%, lontano dal 10%). Ma su **tutti i frame** FULL è ≈5,3% peggiore della ballistica; su contatto già in corso ≈19% peggiore. Per ora NO_VISION generalizza meglio di FULL sulle metriche complessive: aggiungere sensori non implica ancora modellarli correttamente.

### Rollout libero con le future azioni *registrate*: posizione MSE

| Partizione/orizzonte | FULL | NO_VISION | NO_ACTION | NO_TOUCH | Baseline migliore |
|---|---:|---:|---:|---:|---:|
| CAL 16 frame | 0,016698244 | 0,019085680 | 0,017206503 | 0,016705489 | 0,020470200 |
| CAL 48 frame | 0,037584018 | 0,046380485 | 0,048384259 | 0,038315539 | 0,069178995 |
| CAL 144 frame | 0,173073284 | 0,212960337 | 0,202408145 | 0,185243057 | 0,290728898 |
| BLIND 16 frame | 0,068098691 | 0,072037743 | 0,068871054 | 0,068166027 | 0,072105080 |
| BLIND 48 frame | **0,133197992** | 0,146320063 | 0,150042770 | 0,134776057 | 0,156992247 |
| BLIND 144 frame | **0,373495177** | 0,421233855 | 0,528909936 | 0,401343196 | 0,533924827 |

Il percorso BLIND a 48 frame migliora ≈15,2% vs migliore baseline e ≈9% vs NO_VISION, risultato **positivo ma circoscritto**. A 16 frame FULL migliora ≈5,6%, meno del minimo 10%. A 144 frame migliora ≈30,1% vs baseline immobile. Questi replay **conoscono a priori il nastro delle azioni future**; non provano la capacità di ZERO di pianificare autonomamente azioni future.

### Predizione *primo contatto*: Brier (minore è meglio)

| Partizione | FULL con vista | NO_VISION | NO_TOUCH | Prevalenza FIT |
|---|---:|---:|---:|---:|
| CAL (frame prima del contatto) | 0,011011355 | 0,009150306 | 0,010562673 | **0,009347801** |
| BLIND (frame prima del contatto) | 0,009176898 | 0,008148665 | 0,008637200 | **0,008103826** |
| CAL soli eventi positivi | **0,865549021** | 0,958845770 | 0,904071157 | N/D |
| BLIND soli eventi positivi | **0,911415494** | 0,958247850 | 0,944011158 | N/D |

Il segnale visivo risulta **associato a migliore probabilità assegnata agli onset effettivi** (Brier condizionato su positivi inferiore a NO_VISION), ma crea troppi errori probabilistici sui frame senza onset: Brier complessivo FULL peggiore sia di NO_VISION sia della prevalenza FIT. Non si può dichiarare un rilevatore di impatto affidabile. Un modello a frequenza evento così sbilanciata richiede calibrazione e verifica esplicita di falsi allarmi, senza ottimizzazione sulle pose BLIND già esaminate.

### Diagnosi e decisione

- **Fatti verificati:** 16 scansioni ottiche effettivamente ottenute dalla membrana fisica prima dell'efferenza; test vision sanity passato (nessun oggetto → nessun ostacolo fittizio, box visibile → segnale ottico); 52 onset veri BLIND; risultati XY e Brier separati; nessuna lettura della vista futura nel rollout.
- **Fatto negativo:** il modello lineare residuo con 16 nuovi ingressi visivi, benché utili per alcuni rollouts e segnali onset, non raggiunge predizione fine robusta dello spostamento fisico e onsets calibrati.
- **Ipotesi non ancora dimostrate:** la proiezione compressa di distanza su 16 raggi non rappresenta bene la superficie effettiva di collisione, la linearità non modella il salto impulsivo/solido o l'interazione con le 96 patch, manca la velocità di avvicinamento misurata su almeno due scansioni, la distribuzione di eventi rara penalizza un regressore lineare per classificazione onset, e il controllo motorio dovrebbe ragionare su orizzonti temporali anziché ottimizzare soltanto il delta immediato.
- **Vincolo architetturale Master Blueprint:** queste sono *feature fisiche sperimentali*, **NON** l'intero `SensoryFrameV3` immutabile con 96 siti, provenance, timestamp, valid-mask ed esperienza causale exactly-once. Non sostituire la specifica con questo prototipo.

**Gate scientifico V3.14 = CAL FAIL, BLIND FAIL.** Sono stati superati i requisiti di copertura (onset e vera visione), ma falliscono criteri XY one-step, effetto vista sui contatti già in corso, Brier calibrato e rollout 16 frame BLIND. **REJECTED for live motor control**.

### Prossimo sviluppo coerente con il progetto, non altro fitting fine a sé stesso

Realizzare finalmente il **contratto sensoriale unificato immutabile `SensoryFrameV3`** con campionamento `t` della membrana 96×N e vista 16 raggi, tatto distinta, efferenza 64D, stato fluido/energia e provenance, presenza/assenza reale degli input, frameId e orologio simulato. Aggiungere registrazione causale exactly-once `Perception(t) → MotorAction(t) → PostCollisionOutcome(t+1)` con invarianti e prove di simultaneità, senza ancora decidere automaticamente azioni o alterare l'app legacy. Successivamente usare i dati per il vero encoder ricorrente e la corteccia motoria e fare **E3 closed-loop autonomo** con 2.000+ decisioni reali e prova di retenzione su nuovi mondi. Non allenare su BLIND V3.14 consumato.

**Stato:** solo ramo sperimentale PR #87 Draft, nessun merge, nessun cambio al motore di gameplay, nessuna nuova UI o abilità fittizia, nessuna OTA. G3 locomozione autonoma e G5 world model generale **bloccati**.

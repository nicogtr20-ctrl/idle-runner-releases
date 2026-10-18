# ZERO 3.0 — ADR M39: modello di transizione del corpo con efferenza reale (17/10/2026)

**STATO: esperimento concluso; gate CAL e BLIND FALLITI.** Protocollo preregistrato prima della prima esecuzione, risultati sotto. Esperimento di laboratorio E2, non un cervello attivo. Riferimenti obbligatori: [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [INTERFACCIA V3](ZERO30_UI_UX_SPEC.md), [fallimento V3.8-B](ZERO30_MOTOR_V38B_STATE_TRANSITIONS.md).

## Perché il prossimo test cambia rappresentazione
V3.8-B tenta di ricostruire 32 blocchi di risposta *controfattuale* partendo da una sola azione e da un delta differenziale osservabile soltanto con **due** corpi abbinati. Sul blind l'MSE finale 0,160072801 è peggiore del statico 0,029057080. Un corpo autonomo reale non può osservare il proprio doppio controfattuale. Occorre misurare lo stato proprio, **ogni comando effettivamente applicato** e il cambiamento fisico successivo, senza accesso allo stato futuro al momento della predizione.

## Nuovo test isolato V3.9, prima della lettura dei risultati

- **Un solo corpo fisico per traiettoria** con `SymbioteMatterV1` invariato; 96 frame di precondizionamento motorio + 32 blocchi di 12 frame.
- Per ogni blocco: snapshot **pre-fisica** di 18 feature corporee misurate (velocità del corpo, deformazione e velocità della membrana, grip, contatti, area, reazioni, asse anatomico e fluido), **64 comandi reali** aggregati dai 12 frame che il solver consumerà, snapshot **post-fisica** della stessa natura, dx/dy reali del centro e differenza di lavoro del materiale. Nessuna posizione delle gemme, direzione imposta come feature, ID della posa o condizione di laboratorio negli input del modello.
- Il fit lineare multivariato con ridge su `[stato(t), 64 comandi eseguiti]` predice **delta delle 18 osservazioni, delta X/Y e lavoro** alla fine del blocco. Normalizzazione e regularizer stimati/fissati **solo sul train**. Si usa anche un modello a pari capacità **senza azione**, una baseline nessun movimento e una baseline cinematica di velocità costante.
- **Tre split di sequenze intere**: FIT pose 0°/90° con precondizioni 0,1,2 (384 traiettorie); CAL pose ±22,5° con precondizioni 0,1,2 (384); BLIND pose ±10°/±55° e precondizione 3 (256). Le pose finali coincidono in parte con V3.8-B già esaminato: questo esperimento cambia target e protocollo, ma **non si può chiamare test di generalizzazione incontaminato rispetto alla ricerca storica**. Necessaria una successiva replica preregistrata su nuove scene/seed prima di promuovere un modello.
- Test **teacher-forced one-step**: il modello osserva stato e comandi veri *prima* di ogni blocco, poi predice l'esito non ancora visto; calcolare errore dx/dy e nuove feature. Test **free-state rollout**: usa soltanto stato iniziale e le proprie 32 predizioni per lo stato successivo; gli ingressi di azione futura sono le azioni **registrate** nel replay. Questo non è un forecast autonomo dei futuri attuatori: è un replay open-loop con azioni note, dichiaratamente non utilizzabile come planner nel gioco.
- **Gate di laboratorio preregistrato**: CAL con MSE dx/dy un passo inferiore del 10% alla baseline cinematica; errore di traiettoria a 32 passi inferiore del 10% alla migliore baseline no-motion/ballistica; vantaggio sul modello senza azione almeno del 3% in one-step e rollout. Stesse condizioni verificate sul BLIND senza riaddestrare né ritoccare iperparametri. Nessun fail di qualità rende verde un gate.
- Escludere rigorosamente fit/normalizzazioni sui dati CAL e BLIND; controllare integrità fisica, 64 uscite finite, outcome finiti, nessuna lettura dei futuri stati in rollout. Se fallisce, registrare il fallimento, separando errore di predizione a un passo, deriva multi-step, dipendenza dalle azioni e insufficienza delle feature.

## Vincoli
Questi impulsi deterministici sono **eccitazioni di laboratorio**, non andature immesse nel cervello. Zero3 non viene collegato a `GameActivity`, non si modifica `SymbioteMatterV1`, non si effettua training del profilo utente, non si toccano gli slot, la UI, la firma o l'OTA. Anche un eventuale esito positivo resta E2 e **NON** supera da solo G3 locomozione autonoma o G5 world-model generale.

**Handoff dopo CI:** riportare commit, run, CAL/BLIND, confronto con baseline e ablation, esito gate e debiti residui; non promuovere automaticamente alcuna rete.


## RISULTATI VERIFICATI — CI 38985522666 (17/10/2026)

**[GitHub Actions 38985522666 — SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38985522666).** Compilazione Java riuscita e test fisico concluso su **491.520 step del solver** da 1.024 traiettorie indipendenti come sequenze (384 FIT, 384 CAL, 256 BLIND). Gli input vengono rilevati sullo stesso corpo, senza doppio controfattuale: 18 feature del corpo, 64 comandi effettivamente eseguiti, 32×12 frame di fisica. Il test free-state a 32 passi ricostruisce gli stati senza leggere gli stati futuri ma usa i comandi futuri già registrati. Non è previsione autonoma di azioni future.

| Split / metrica MSE | Con comandi | Senza comandi | Baseline zero | Baseline velocità costante |
|---|---:|---:|---:|---:|
| CAL one-step XY | **0,002050217** | 0,002413431 | 0,010055146 | 0,002708753 |
| CAL traiettoria 32 blocchi | 1,611285331 | 1,206286667 | **0,864146898** | 2,689974765 |
| BLIND one-step XY | 0,002513731 | **0,002498632** | 0,009088465 | 0,002582060 |
| BLIND traiettoria 32 blocchi | 3,116409090 | 1,839797331 | **0,837472781** | 2,138810561 |

**Gate CAL = FAIL. Gate BLIND = FAIL.** La previsione one-step XY con azioni migliora del **24,3%** rispetto al riferimento a velocità costante in calibrazione; contro la stessa baseline sul BLIND migliora solo del **2,65%**, al di sotto della soglia preregistrata del 10%, e il modello **senza azioni è leggermente migliore** sulla metrica XY one-step. Anche il modello con azioni supera la previsione zero in un intervallo breve, ma **è 3,72 volte peggiore della baseline zero sulla traiettoria BLIND a 32 blocchi** (3,116409 / 0,837473). La metrica dello stato completo one-step BLIND (18 dimensioni) è 0,006254709 con azioni, 0,006729719 senza azioni, ma questo non riscatta la deriva XY.

### Diagnosi fattuale e inferenze non ancora dimostrate

- **È dimostrato** che la registrazione e l'associazione stato/comando/esito reale funziona a livello di laboratorio ed è compilabile; non dimostra che l'apprendimento sia autonomo.
- **È dimostrato** che il modello lineare con 18 feature + comandi aggregati a 12 frame generalizza poco sul movimento immediato e non è un simulatore biomeccanico affidabile nel rollout di 32 passi.
- **Non è dimostrata la causa singola:** perdita della morfologia spaziale nelle 18 feature, media dei 64 comandi su 12 frame, contatto che cambia regime, riduzione dello stato continuo a delta lineari, errore cumulativo, distribuzione diversa delle posture, bias delle azioni rilevate.
- Il confronto con **senza comandi** indica che aggiungere efferenza a un modello lineare aggregato non dà ancora un vantaggio robusto sulla locomozione; **non** indica che i comandi siano irrilevanti nella fisica.
- Il doppio problema è **apprendimento one-step insufficientemente generalizzabile** e **rollout non stabile**. Ri-addestrare sulla partizione BLIND usata qui non può essere spacciato per validazione indipendente.

### Decisione e prossimo passo

**REJECTED FOR MOTOR CONTROL. Non integrare il modello in `GameActivity`, non promuovere G3/G5, non unirlo nel branch che pubblica l'OTA.** ZERO 2.2/OTA172 e i 12 slot rimangono intatti.

Per un successivo laboratorio: osservazione localizzata della morfologia (settori, velocità, grip e contatti), azioni **per-frame** non solamente mediate sui 12 frame, confronto di transizioni brevi contro baseline persistente, previsione in circuito chiuso testata con **nuove scene e seed** (non soltanto nuove pose). Preregistrare test e misure prima di calcolare risultati; limitare il training a dati fisici e verificare anche il valore marginale di ciascuna famiglia sensoriale. L'eventuale passaggio ad actor/cortex vivo richiede successo E3 in cicli autonomi, memoria e riuso, non solo un E2 numerico.

**Esito definitivo V3.9: CI tecnica PASS, gate scientifico FAIL, G3 BLOCKED, G5 BLOCKED, NO OTA.**

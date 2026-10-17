# ZERO 3.0 — V3.30 | Audit delle intenzioni e del controllo motorio sul runtime autentico

**Protocollo prima dell'esecuzione. 17/10/2026.** Dopo [Master Blueprint](ZERO30_MASTER_BLUEPRINT.md), [Implementation Contract](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.19 grip](ZERO30_V319_CONTEXTUAL_GRIP_ENERGY.md), [V3.17 autonomia](ZERO30_V317_AUTONOMOUS_MOTOR_LOOP.md), [V3.29 RAM](ZERO30_V329_BOUNDED_RAM_DEVICE_READINESS.md).

## Stato probatorio precedente da NON perdere

- V3.19 ha superato un **gate scientifico circoscritto**, adesione BOX contestuale, riducendo lavoro e attivazione inutile; presa bilaterale inferiore alla V3.18. La testa resta parte del runtime 3.0 sperimentale, non va sostituita o riscritta.
- V3.17 e le vecchie 2.000 esperienze della corteccia hanno rilevato parametri realmente aggiornati ma **mancato progresso affidabile nel realizzare le intenzioni**. Aumentare genericamente il learning rate è già stato smentito dai benchmark, non ripetere.
- V3.29 ha superato la persistenza di due vite da 2.000 decisioni con 32 record completi storici in RAM post-checkpoint. Nessuna prova di nuova locomozione, dispositivo G9 ancora aperto.

## Obiettivo V3.30

Rendere **misurabile sul vero runtime unificato** la divergenza fra intenzione scelta autonomamente e conseguenza fisica. Isolare almeno un sospetto causale testabile, senza inventare una camminata, il target di una gemma o un premio globale a ogni spostamento. Il primo benchmark è un DIAGNOSTICO di G3, non autorizza automaticamente a modificare i pesi; usare i risultati per decidere un nuovo learner con dati TRAIN diversi da nuovi holdout.

### Componenti proposti

- `Zero3MotorEvidenceV330`: osservazioni di una decisione completa: ID episodio/decisione, vettore di intenzione reale, velocità iniziale COM, traslazione finale reale, distanza lorda misurata durante 12 substep, lavoro del gel, impulso muscolare, comando realmente eseguito, grippaggio BOX e indicatore di contrasto fra i canali muscolari. Nessun accesso post alla policy precedente, nessun ritocco del corpo, nessun reward sintetico; distinguere chiaramente proxy inerziale da controfattuale passivo (che richiederebbe un altro esperimento reale).
- Collegare tale misurazione come telemetria **derivata** e read-only a `Zero3RuntimeV325` con getter, senza cambiare formato dei salvataggi, seed, cortex, head adesivo o impianto neuronale. Snapshot precedente non include `lastOutcome`, si aggiorna alla prima decisione dopo restore; nessun credito d'esperienza derivante dalle letture UI.
- `Zero3MotorAuditV330Regression`: più ambienti fisici e pose nuove in un confronto **paired** fra stesso actor/adesione e cortex con update fisici .013 contro cortex con learning rate 0 e stessa esplorazione. I due bracci usano stesso seed, episodio e mondo iniziale, non ricevono direzione voluta dal laboratorio. Tutte le metriche devono essere stampate per scena, comprese negative, invalid, gross, net, net/gross, work, alignment e residuo rispetto alla velocità iniziale. Per le prime prove: almeno 4 famiglie FREE, BOX_NEAR, DISK, BOX_FAR, almeno 5 pose/seed per famiglia, almeno 96 decisioni ciascuna per braccio.
- Nessun ritocco di rate/soglie usando i 20 scenari di valutazione. G3 preliminare: almeno 3/4 scene con +10% di allineamento realizzato vs frozen, spostamento netto utile e costo per unità utile non peggiore, almeno 20 seed validi; se una condizione manca **`motorGate=FAIL`**, anche se CI compilazione/esecuzione SUCCESS.
- Ancora necessario dopo V3.30: prova longitudinale TRAIN/FROZEN + test BLIND senza esplorazione su corpi freschi e mondi mai visti per promuovere abilità, seguito da G3 più ampio. Il presente benchmark tra due bracci online con esplorazione non prova retention di una skill autonoma. V3.19 scientificGripGate=PASS rimane un risultato indipendente.

**Sicurezza:** non mutare OTA172, 12 slot della 2.2, né modificare solver gel/fisica per far passare le metriche. Solo PR #87 DRAFT. I risultati reali si aggiungono dopo CI, numeri e casi negativi inclusi.

## Risultati effettivi della prima batteria — 17 ottobre 2026

**[GitHub Actions 39035949311 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39035949311)**, primo commit dei tre moduli motor V330 e `Zero3RuntimeV325` modificato con sola telemetria. Il bench ha eseguito 20 coppie matched da 96 decisioni fisiche per ciascun braccio: **1.920 decisioni TRAIN e 1.920 FROZEN, 23.040 reali integrazioni gel per braccio** (+warmup), training V319 adesione presente in entrambi. Sono stati verificati efferences 64D reali, PRE inerzia, centro di massa ottenuto, lavoro misurato, proprietà della presa. Il codice **non** prescrive una direzione di marcia, né modifica il solver fisico.

| Famiglia (5 casi/480 decisioni per braccio) | Somma spostamento FIRMATO lungo le intenzioni TRAIN | FROZEN | Efficienza TRAIN (signed/(4+gross)) | FROZEN | Miglioramento preregistrato |
|---|---:|---:|---:|---:|---|
| FREE | −2,3942 | −1,4995 | −0,03720 | −0,02307 | **NO** |
| BOX vicino | +3,7220 | +3,4179 | +0,04365 | +0,04057 | **NO**, vantaggio inferiore alla soglia preregistrata |
| DISK | +3,7908 | +5,1577 | +0,05124 | +0,07073 | **NO** |
| BOX lontano | −2,8371 | −1,5974 | −0,04322 | −0,02469 | **NO** |

**Sintesi fisica:** `cortexUpdatesTrain=1920`, `cortexUpdatesFrozen=0`; tutte le 1.920 decisioni per braccio hanno `physical=true`. Allineamento sommato TRAIN `+2,281474` contro FROZEN `+5,478730`. In tutte e quattro le scene le soglie scientifiche non sono raggiunte; **`trainedSceneWins=0/4, MOTOR_GATE=FAIL`**. Il confronto TRAIN/FROZEN è un ablation del *learning rate della cortex*, non della V319, che rimane addestrabile in entrambi e conserva il proprio risultato di apprendimento sull'adesione.

Il nuovo indice descrittivo `cancellationProxy` dei comandi radiali e tangenziali osservati è circa **0,58–0,64** nei quattro mondi. Non è una misura diretta delle forze sul centro di massa (reazioni del fluido e contatti sono più complessi), quindi **non dimostra da solo che «parti del corpo vadano in direzioni opposte» come causa della scarsa locomozione**. Analogamente `residualBeyondInertiaProxy` sottrae una stima di traslazione inerziale e **non è un controfattuale fisico del corpo passivo**. Il report completo per i 20 matched episodi è nel log della CI, senza selezionare i mondi favorevoli.

**Verdetto:** l'implementazione dell'audit è verificata, ma il `scientificMotorGate` resta **FAIL/G3 BLOCCATO**. Si conserva la testa adesiva V319, i checkpoint V328/V329, i sensori V327, e si evita di aumentare genericamente il learning rate. Il prossimo intervento deve usare il risultato per una **attribuzione del credito locomotorio temporalmente e spazialmente locale**, comparata a un vero matched non-intervention fisico, prima di proporre promozioni di pesi o skill.

La compilazione Android per il primo SHA è `BUILD SUCCESSFUL`; la versione consolidata con HUD `intenzione vs spostamento` e regressioni V325–V329 è verificata solo dopo conclusione delle CI finali. Nessuna OTA.

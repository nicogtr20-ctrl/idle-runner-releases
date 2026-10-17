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

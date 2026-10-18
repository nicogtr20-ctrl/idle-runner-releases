# ZERO 3.0 — V352: durata fisica scelta e interruzioni causali

18 ottobre 2026. Master §§6,9,13–14; G2/G4/G7. Design prima del codice.

V351 rimane configurazione a clock fisso compatibile; GRU26 e archivi storici
rimangono bytewise invariati. Nuova configurazione GRU194 V352 dichiara nel
checkpoint il clock variabile, sceglie 10..26 substep da 0.025s (durata
0.25..0.65s), ed esegue i comandi solo per quei substep reali. Nessuna durata
viene premiata prima di essere effettivamente conclusa.

Un contatto dannoso o riserva zero interrompe l'intenzione al primo substep
misurato; registra causa, durata pianificata ed eseguita. Nessuna raccolta
successiva alla morte può rianimare l'organismo nello stesso intervallo.
La corteccia censura l'intenzione incompleta; il critic riceve la conseguenza
reale e non usa bootstrap dopo morte; il readout durata non riceve credito
per un'interruzione. Discount basato sul tempo fisico effettivo.

Nuovi schemi 352 per organismo, manifest, metabolismo e replay. Tutto rimane
nella stessa generazione atomica; vecchi 322/328/348/351 restano leggibili con
le loro regole originarie. Nessuna conversione automatica di una vita in corso.

Prove: conteggi variabili effettivi, interruzione danno/morte autentica,
assenza credito ineseguito, rifiuto intervalli falsi, ripresa stessa vita e
replay identici bytewise dopo nuovo processo; regressioni legacy e Android.
Non equivale a G4 scientificamente chiuso: reti bersaglio/replay, valore e
competenze richiedono ancora i rispettivi gate.

## Completamento del training del valore, prima dell'implementazione

La sola correzione del clock non completa il learner G4. Nella medesima nuova
configurazione 352 si aggiungono due critic target con Polyak 0.02 e replay
bounded di 128 transizioni REALI del valore (hidden PRE, hidden POST, reward
misurato, discount temporale/terminale). Quattro campioni deterministici per
conseguenza; nessuna fantasia o holdout nel replay, nessuna riscrittura
retroattiva della policy. Target e replay fanno parte del blob atomico GRU.
La GRU e l'actor mantengono gli aggiornamenti online; il replay del critic
usa embedding osservati alla raccolta, con limite esplicito di staleness.
Prove di apprendimento del valore e beneficio sulle scelte restano separate
(non dichiarare G4 approvato dalla sola presenza del replay).

## Risultati locali

- Durate realmente diverse: 16,17,18,19 substep nel test di 96 decisioni.
- Danno reale interrompe in 1 substep; contatto letale interrompe in 1 substep.
- Fonte energetica successiva al danno letale non resuscita l'organismo.
- Morte e configurazione clock restano dopo restore; nessun credito post-mortem.
- Intenzione interrotta: readout durata invariato, critic aggiornato da esito vero.
- 48+restore+48 identici al controllo 96 per cervello/corpo/metabolismo.
- Batteria locale completa PASS: legacy V345/V348/V350, V351, V352 e vita 2000.
- Con target/replay: 1000+JVM restart+1000 identici al controllo continuo;
  63 segmenti, rollback reale a1920, doppia corruzione rifiutata.
- CI remota 39119103894 SUCCESS: include Android Debug compilata.

Non dichiarare G4 scientificamente chiuso. Mancano ancora le prove indipendenti
di valore appreso e beneficio delle decisioni; G1/G3/G5/G6/G7 crescita/G8/G9/G10
rimangono aperti. Il clock variabile NON corregge da solo il solver fisico.

# ZERO388 — Correzioni fisiche del corpo, V3 esclusivo, zero perdita di apprendimento

**Stato:** correzioni di codice su `fix/zero388-conservative-shell-proprio`, PR #119 DRAFT, nessuna nuova app firmata/OTA pubblicata. Verifica Android e gate 2.000 decisioni avviati [S21 QA](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40146073788), [autonomia](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/40146073790). Non indicare SUCCESS finché i job non restituiscono successo.

## Modifiche applicate in modo effettivo
1. `SymbioteMatterV1.step`: salva tutte le 96 coordinate PRE in `lastSkinX/Y` prima di qualunque integrazione. Passata 1 calcola 64 muscoli e 96 normali esterne sulla **medesima geometria fisica**, passata 2 accumula forze elastiche da coppie vicine, passata 3 integra moto e forza/reazione contro il liquido. Nessun punto legge più una posizione vicina cambiata precedentemente nello stesso substep.
2. Molle con rigidità neurale CH3 heterogenea: `accumulateBalancedElasticV388` applica a ogni spigolo `K_ij=(K_i+K_j)/2` e forze `+F` e `-F`. Non invia l'elasticità al fluido una seconda volta: solo la forza vera di muscolo resta reazionata ai 74 fluidi interni. Energia, substrato e attrito non ricevono compensazioni globali inventate.
3. Corteccia `Zero3LearningFoundation` CH1 (tangenziale): misura velocità tangenziale della vera membrana relativa al COM, la normalizza e la somma in misura limitata al feedback esistente di stato del SOLO CH1. 8 feature e 512 coefficienti dei pesi originali restano invariati; CH0/CH3 non sono influenzati direttamente. La regola di apprendimento non cambia. Non viene aggiunto un target, presa automatica né locomozione codificata.
4. Reset `ResetDomains.MOTOR`: aggiunti `Zero381MotorCalibration.KEY`, `Zero381MotorCalibration.DIRECT_KEY_V387`, `Zero381MotorFlightRecorder.KEY`. L'archivio V356 principale e i 12 slot non sono nel reset selettivo.

## Prova numerica locale con SALVATAGGIO UTENTE REALE
Origine `zero-telemetry/reports/20261024-103000-33e93646acead/part-002.json`, campo `currentZero.training_room_1_scene_v379`. Rigidità variabili ammissibili `K_i=85+190*(.5+.42*sin(1.7*sector+.3))`.
- Implementazione originale: somma artificiale del campo elastico (-809.908,356.989), modulo 885.095 (numero di una PROVA riproduttiva, non forza misurata sull'ultimo smartphone).
- Algoritmo V388 con aritmetica Float32: (-0.00002098,0.00001526), norma 0.00002594.
- Rotazione della numerazione materiale di 17 punti: differenza massima per-punto = 0. Riproduzione numerica conserva sia le molle sia le differenti rigidità.
- Questi valori verificano una **proprietà locale**. Il valore di allineamento della locomozione originale V387 era 0.69% (2.000 decisioni, 4 blu, 12 rossi); la nuova locomozione deve essere provata con test Android, non può essere inferita.

## Gate di rilascio
`Zero388ConservativeMusclesAndroidTest`: vera forma S21, reciprocità rigidità eterogenea e relabel, feedback tangenziale CH1, reset selettivo. Workflow QA rifà anche gli storici V385/386, V387 contatti, cold-resume e propriocezione. Workflow separato `Zero366OriginalAutonomousSurvival2000Test` esegue 2.000 decisioni native non scriptate. In caso di test falliti: correggere il codice, non addolcire le soglie e non dichiarare completato il movimento. Non toccare il canale OTA stabile e non cancellare pesi, memoria, esperienza o slot.

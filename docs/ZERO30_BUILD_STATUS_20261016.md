# ZERO 3.0 — HANDOFF TECNICO 16 OTTOBRE 2026

> **LEGGERE DOPO** [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md) e [UI/UX](ZERO30_UI_UX_SPEC.md).
> **Stato:** primo prototipo in corso, ZERO 3.0 NON IMPLEMENTATO nel gioco e NON PUBBLICATO via OTA.
> **Sorgente sperimentale:** [branch ZERO3](https://github.com/nicogtr20-ctrl/idle-runner-releases/tree/feature/zero30-foundation-g0-g3-20261016).
> **Revisione aperta DRAFT DA NON MERGERE NEL BRANCH OTA:** [PR #87](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

## Implementazione verificabile

Sono state create nuove classi Java isolate con prefisso Zero3, non collegate al gameplay attuale:

- **Zero3LearningFoundation.java**: intenzione continua, 64 comandi muscolari (16 x 4), controllo neurale locale, perturbazioni registrate, coordinatore con causal credit, separazione train/holdout/disturbi, exactly-once.
- **Zero3RecurrentBrain.java**: rete GRU di 64 unità ricorrenti allenabili, actor continuo esplorativo, doppio critic TD e prima testa di previsione sensoriale action-conditioned. Truncated BPTT ad un passo. Non ancora off-policy con replay o reti target.
- **Zero3FoundationRegression.java**, **Zero3RecurrentBrainRegression.java**: prove che la rete aggiorni pesi da esiti fisici reali sul vero corpo a 96 punti di membrana e 74 masse interne, blocco holdout, contaminazione, doppie decisioni.
- **Zero3LearningCurveRegression.java**: confronto onesto con cortex e cervello congelati, dopo 40 e 400 decisioni. Non nasconde peggioramenti.
- **Zero3MotorSkillAB.java**: esperimento SOLO di laboratorio su quattro intenzioni costanti, senza iniettare andature nel controller.
- **zero30-foundation-qa.yml**: CI isolata sul branch ZERO3, compila JVM e Android Debug, testa reale fisica 96+74, protegge dal rilascio OTA anticipato.

**Motivo del package temporaneo:** le nuove classi vivono per ora nel package com.overmind.zero, con prefisso Zero3, perché i solver legacy espongono alcune strutture package-private. Per la futura migrazione nella cartella v3 introdurre un adapter stabile e testato, non aprire indiscriminatamente accesso al solver.

## CI e dati reali simulati

- [CI iniziale G2/G3/G4 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38968378595): contratti e reale fisica, GRU loop, Android build.
- [CI 40/400 esperienze SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38968617696): tutti i test, Android Debug. Il test passa perché gli invarianti sono rispettati, NON perché ha provato una locomozione superiore.
- CI più recente con direzioni multiple: cercare sul [branch ZERO3 Actions](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions) run di Zero 3.0 experimental QA e verificare risultati prima di modificare.

**Evidenze misurate nella CI 38968617696:**

| Esperimento | Condizione | Lordo (U) | Netto (U) | Allineamento cumulativo (U) |
|---|---|---:|---:|---:|
| 40 decisioni in arena libera | congelato | 3.592 | 1.118 | -0.415 |
| 40 decisioni in arena libera | apprendimento | 11.824 | 1.521 | 0.348 |
| 400 decisioni in arena libera | congelato | 56.759 | **50.211** | 3.043 |
| 400 decisioni in arena libera | apprendimento | **126.721** | 19.586 | **5.802** |

Altre misure: fisica laboratoriale a sequenza predefinita (NON autonoma) 19.20U netti su 640 frame contro corpo passivo netto 0.001U. Nella prova V3 a 8 decisioni, lordo 2.54669U e netto 0.47918U, 8 aggiornamenti della corteccia e 5 degli stati ricorrenti.

**Interpretazione obbligatoria:** dopo 400 decisioni il cortex addestrato deforma/muove di più e mostra un migliore allineamento cumulativo con la propria direzione, ma il **netto complessivo peggiora** rispetto al riferimento. In questa arena non ci sono gemme/obiettivi energetici; l'actor cambia intento, quindi il netto finale non è da solo metrica di qualità motoria. Il risultato è MISTO e non costituisce prova di autonoma locomozione o intelligenza migliore. Non nasconderlo e non ritoccare i test per farlo sparire.

## Stato dei gate

| Gate | Stato | Cosa manca |
|---|---|---|
| G0 Baseline | Parziale | Riproducibilità completa multiscena e multi-seed |
| G1 Controllabilità fisica | Screening positivo, NON chiuso | Direzioni/posture/scene, dinamica causale rigorosa |
| G2 Contratti e causal join | Prototipo testato, NON chiuso | Formato persistente, interruzioni e integrazione completa |
| G3 Cortex motorio | Prototipo e benchmark MISTO, NON superato | Miglioramento verificato su nuovi scenari e capacità riutilizzabili |
| G4 Cervello ricorrente actor-critic | Prototipo, NON chiuso | Replay, target network, reward ambientale reale, validazioni |
| G5 World model e planner | NON superato | Multi-testa, baseline migliore, uncertainty e rollout bounded |
| G6 Memoria e obiettivi | NON iniziato | Episodi, skill, intent persistence, energie |
| G7 Neurogenesi, salvataggi | NON iniziato | Crescita reale, dati v3, migrazione reversibile |
| G8 NUOVA UI ZERO3 | NON iniziata | Cervello, neuroni, abilità, evoluzione, corpo, impostazioni nuovi e collegati |
| G9 Device benchmark | NON iniziato | 20+ seed, 400/2k/5k/10k, Galaxy S21 |
| G10 APK OTA | NON AUTORIZZATO | Tutti i gate, compatibilità e distribuzione ufficiale |

## Componenti legacy intatti

Non modificati: GameActivity, SensorimotorMind, SymbioteMatterV1, fisica di produzione, mondo, firme, APK ufficiale, manifest OTA, salvataggi/12 slot, schermate ZERO 2.2. Branch sorgente OTA di produzione storico: feature/zero22-active-recurrent-ota117, che **attiva il publisher firmato a ogni push**. NON fondere PR #87 in quel ramo: resti DRAFT finché G3-G10 non sono veramente chiusi.

## Prossimi passi per ogni futura chat

1. **Leggere i tre documenti canonici**, questo stato e la PR #87. Verificare l'ultimo CI per i risultati del test motor-only 400 x 4 direzioni.
2. Riprogettare dove necessario l'apprendimento motorio per migliorare movimento verso l'intenzione richiesta su più posture, confrontato con motore vergine/congelato; zero gait programmati.
3. Integrare credito causale rigoroso e world model valido, poi actor con vere conseguenze di energia/danno, memoria replay, skills consolidate, intenzioni persistenti, neurogenesi.
4. Salvataggi atomici V3 senza toccare 12 slot e cerebri legacy.
5. Creare la **nuova UI/UX ZERO3 obbligatoria** con provider veri; sostituire UI 2.2 solo per profili V3.
6. Test completi multi-seed, device S21, comparazione con vecchio ZERO, release OTA firmata e AGGIORNA solo dopo le prove.

Non dichiarare la versione V3 finita o pronta all'installazione finché questi requisiti non sono verificati.

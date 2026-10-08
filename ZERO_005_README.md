# OVERMIND ZERO / v0.5 — Scelta neurale degli stimoli

## Richiesta
ZERO deve scegliere autonomamente quale pallino investigare, non ricevere il bersaglio da una formula scritta a mano.

## Implementazione verificabile
- **DecisionBrain.java**: una rete feedforward di 91 parametri, casualmente inizializzati, non pre-addestrata. Input: firma sensoriale (tre canali), distanza, energia corporea, quante volte il segnale è già stato sperimentato; output: utilità prevista dell'interazione.
- Sostituisce il vecchio `UnknownWorld.Mind.choose()` nell'app. Il vecchio sistema `Mind` rimane come registro storico di associazioni, ma NON decide più i bersagli.
- L'apprendimento online usa **le conseguenze osservate** dei contatti: reale variazione di energia, reale spostamento di un disco e novità percepita; ottimizza i 91 pesi con backpropagation su errore di previsione.
- Esplorazione epsilon-greedy per tentare anche alternative meno promettenti: la creatura non conosce preventivamente la natura dei segnali.
- Finestra per tentare una scelta aumentata a 9 secondi, poi penalità piccola per il tentativo fallito. Corpo motorio, abilità manipolative e salvataggi precedenti NON vengono resettati.
- Memoria decisionale persistente nello stesso `SharedPreferences`, chiave `decision_brain_v05`. Nella UI: numero di decisioni, esperienze, esplorazioni, ultima previsione e segnale scelto.

## Limiti scientifici
- Questo è un **contextual bandit** che apprende le preferenze dalle esperienze; non un LLM, non autocoscienza, non linguaggio e non un agente che inventa obiettivi arbitrari.
- I bisogni e gli impulsi di apprendimento (riserva di energia, curiosità, penalità per esperimenti falliti) sono progettati dall'autore; ZERO apprende preferenze e valori ma NON scopre automaticamente che vivere sia il suo obiettivo.
- La simulazione fornisce ancora al corpo motorio la **direzione geometrica** del target scelto dal cervello decisionale. Un world model emergente rimane un obiettivo futuro.
- Tre segnali elementari sono già definiti dal mondo: serve aggiungere varietà in futuro.
- Il test automatico mostra la capacità di apprendere una preferenza da esiti sintetici, ma il test sul Galaxy S21 resta necessario.

## Prerequisito release
Test di DecisionBrain, regressioni MotorBrain/UnknownWorld/ContactLearning e build Android in branch staging. Solo dopo CI verde: merge nel branch `feature/overmind-inapp-updater`, build Release firmata con **stessa chiave e stesso package** `com.overmind.zero.ota`, OTA Vercel, aggiornamento CHANGELOG/HANDOFF.

## Prova sul telefono
Premi `AGGIORNA`, aspetta alcuni contatti. Il display mostrerà `SCELTE`, `ESPERIENZE`, `ESPLORAZIONI`, `SCELTO DALLA RETE`. Guarda se ZERO cambia scelta nel tempo e non torna sempre immediatamente all'energia. Misurare e confrontare con un cervello appena inizializzato è un esperimento successivo, non è già integrato.

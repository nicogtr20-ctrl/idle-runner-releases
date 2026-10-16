# ZERO 3.0 — BENCHMARK A 2.000 ESPERIENZE (17/10/2026)

**Leggere prima:** [MASTER](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO](ZERO30_IMPLEMENTATION_CONTRACT.md), [SPEC UI/UX](ZERO30_UI_UX_SPEC.md), [MOTOR V3.1](ZERO30_MOTOR_V31_ENGINEERING.md).

**Stato:** test sperimentali su fisica reale simulata, NON integrazione app, NON OTA. Il ciclo di integrazione va mantenuto separato da ZERO 2.2 e dai suoi salvataggi. [Codice test](https://github.com/nicogtr20-ctrl/idle-runner-releases/tree/feature/zero30-foundation-g0-g3-20261016) · [PR #87 DRAFT](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

## Perché 400 non bastano

Le 400 decisioni erano uno screening di errori evidenti: gradienti vani, danni strutturali, perdita del credito, regressioni catastrofiche. Non sono una prova di mancata apprendibilità dopo più tempo. Per rispondere al dubbio dell'utente introduciamo **almeno 2.000 decisioni reali**, con tracciamento di curva durante training e competenze recuperate in validazione.

**Unità:** una «esperienza» in questi benchmark significa **una intenzione/decisione conclusa**, non un singolo frame, un neurone o un ricordo. Nel laboratorio a intenzione costante una decisione dura 20 frame da 0,025 s = 0,5 secondi simulati. Per ogni direzione: 2.000 esperienze = **40.000 integrazioni del vero solver** = 1.000 secondi simulati. Nell'esperimento GRU autonomo la durata viene scelta dall'actor (non è sempre esattamente 20 frame).

## Esperimenti introdotti

1. **Autonomia (GRU + corteccia):** `Zero3LearningCurveRegression` verifica 40, 400 e **2.000 decisioni** in un vero corpo gel; condizioni TRAIN e FROZEN, senza gemme/obiettivi. Misura movimento netto/lordo, proiezione delle intenzioni e costo.
2. **Apprendimento motorio in condizioni isolate:** `Zero3Motor2000Regression` esegue 2.000 decisioni **consecutive per direzione** (Est/Sud/Ovest/Nord); confronto *stessa intenzione, stesso processo di perturbazione*: `TRAIN + PROBE` e `FROZEN + PROBE`. Registrazione a 400, 1.000 e 2.000 decisioni.
3. **Verifica delle abilità senza rumore:** a ciascun checkpoint la corteccia allenata viene **clonata senza mutarla** e confrontata con un controllore vergine, da corpi freschi identici, 160 decisioni a rumore zero; confronto ripetuto su un corpo iniziale ruotato di 90°.
4. **Metriche e sicurezza:** distanza nella direzione richiesta con segno, distanza lorda, sforzo, aggiornamenti effettivi, numero decisioni complete, topologia valida; nessun reward da spostamento del giocatore verso gemme nascoste, nessun training su validation e nessun OTA.

## Interpretazione vincolante

- Se il TRAIN percorre più strada ma è peggiore nel movimento netto orientato e nel costo, **NON** ha dimostrato migliore locomozione.
- Un vantaggio durante l'esplorazione non basta: il **test zero-noise** deve dimostrare che il cervello **ha ricordato** la competenza.
- L'intenzione costante del laboratorio è imposta dal ricercatore SOLO per misurare il cortex e non entra nel cervello autonomo; il test GRU valuta invece decisioni generate autonomamente.
- Valutazione ruotata 90° = variazione di postura iniziale, NON equivalenza a scena indipendente con risorse e ostacoli. Sono ancora necessari multiseed, nuove scene e retention cross-life.
- Le curve TRAIN e FROZEN con esplorazione identica sono un A/B fisico *matched* per il test di controllo, **non** una dimostrazione di intelligenza generale.
- `BUILD SUCCESSFUL` certifica compilazione/invarianti, **non** che G3 sia superato. Un esito negativo va mostrato, non nascosto.

## Esecuzione verificabile

La CI `.github/workflows/zero30-foundation-qa.yml` sul ramo sperimentale include questi test (e compila Android Debug senza distribuire APK). [Runs ramo sperimentale](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions). Alla redazione è stata avviata la run [38973440670](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38973440670), **i valori effettivi si devono leggere dai log finali, non presumere**.

## Risultati (da compilare esclusivamente dopo lettura log CI)

| Test | Esito | Note |
|---|---|---|
| Motor 400/1000/2000 × 4 direzioni | IN ESECUZIONE | Esito quantitativo da log |
| Cold start senza rumore a 3 checkpoint | IN ESECUZIONE | Virgin vs learned |
| Corpo ruotato di 90° | IN ESECUZIONE | Non è un ambiente mai visto |
| GRU autonomo 2.000 esperienze TRAIN vs FROZEN | IN ESECUZIONE | Nessun obiettivo energetico |
| Android Debug | IN ESECUZIONE | No OTA |

**Gate G3 finché non documentato:** BLOCCATO. Se il risultato è negativo, cambiare l'algoritmo di credito motorio, non aggiungere esperienze indefinite a un learner che degrada le prestazioni.

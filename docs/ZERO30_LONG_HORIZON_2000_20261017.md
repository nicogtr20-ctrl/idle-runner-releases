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

## Risultati COMPLETATI E VERIFICATI — CI 38973440670

**[GitHub Actions 38973440670 — SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38973440670):** compilazione JVM, laboratori fisici, GRU autonoma con 2.000 esperienze, 4×2.000 training motori, confronti ai 3 checkpoint e compilazione Android Debug. **SUCCESS certifica integrità ed esecuzione, NON il gate di abilità G3**.

**Test autonomo, 2.000 esperienze TRAIN vs FROZEN** (arena libera senza gemme, GRU e cortex):
| Misura | FROZEN | TRAIN |
|---|---:|---:|
| Distanza lorda (U) | 295.026 | 462.372 |
| Spostamento netto complessivo (U) | 275.325 | 259.502 |
| Proiezione cumulativa sulle intenzioni scelte (U) | +18.237 | **-5.059** |
| Lavoro muscolare registrato | 0.505 | 0.703 |
| Update cortex | 0 | 2.000 |

L'aumento del lordo NON dimostra controllo. Il TRAIN segue peggio la propria intenzione e consuma di più anche con 2.000 esperienze.

### Per ciascuna direzione: 2.000 decisioni consecutive, senza reset del corpo di addestramento

La misura «forward» è **spostamento firmato lungo la direzione richiesta**, non il modulo del netto. Valutazione dopo training su **corpo fresco identico** con rumore zero, 160 decisioni. Numeri REALI del log:

| Direzione intenzionale | Virgin a 160 decisioni | Learned dopo 400 | Learned dopo 1.000 | Learned dopo 2.000 |
|---|---:|---:|---:|---:|
| Est +X | +34.898 | +35.779 | +37.401 | +32.541 |
| Sud +Y | +16.341 | +12.143 | -0.950 | **-9.318** |
| Ovest -X | -21.170 | -30.172 | -34.067 | -29.307 |
| Nord -Y | -2.100 | -8.703 | -12.236 | -13.200 |
| **Totale** | **+27.970** | - | - | **-19.284** |

**Solo 2 confronti su 12 checkpoint×direzione battono il vergine.** Dopo 2.000 decisioni, **0 direzioni su 4** superano il controllo vergine senza rumore in quella stessa postura. La performance totale scende da **+27.970 U a -19.284 U**.

### La postura conta: corpo ruotato di 90° (senza rumore, 160 decisioni)

| Direzione | Virgin ruotato | Learned dopo 2.000 ruotato |
|---|---:|---:|
| Est +X | -2.301 | -8.405 |
| Sud +Y | +34.635 | +42.211 |
| Ovest -X | +16.488 | +20.212 |
| Nord -Y | -21.162 | -7.029 |
| **Totale** | **+27.659** | **+46.989** |

L'aggregato sulla postura ruotata migliora (+19.330 U), ma il comportamento non è uniformemente migliore: Est peggiora; l'organismo può avere forti asimmetrie, dipendenze dalla posa e dal contesto. **Non è dimostrata generalizzazione**, né esiste ancora valutazione statistica multiseme. La rotazione è un test di contrasto, non una validazione indipendente di un mondo nuovo.

### Training online vs frozen con probe, a 2.000 decisioni

| Intenzione | TRAIN forward online | FROZEN-PROBE forward online |
|---|---:|---:|
| Est | +372.463 | +311.301 |
| Sud | -116.972 | +3.566 |
| Ovest | -152.265 | -165.561 |
| Nord | +187.471 | +248.904 |

Le prestazioni online sono **miste**, non tutte negative, ma la verifica cold-start indipendente è peggiore su tutte le quattro direzioni della postura normale. L'aggiornamento esiste (2.000 updates per direzione), ma **non è un meccanismo affidabile per conservare abilità**.

### Diagnosi e prossimo criterio di lavoro

- **Confermato:** la semplice quantità di esperienze NON basta a rendere efficace l'algoritmo corrente; il sistema modifica pesi ma non consolida capacità stabili.
- **Sospetto da testare, non «causa dimostrata»:** cattivo segnale di credito causale sul singolo muscolo, ritardi temporali elastici e del movimento, baseline dipendente da postura/inerzia, risultato globale che penalizza comandi locali utili.
- **Passo tecnico:** NON aumentare semplicemente il learning rate (griglia precedente peggiora). Progettare e verificare attribuzione differenziale del singolo gruppo motorio, condizioni corporee, lag e prova di retention con rumore zero; confrontare con ricerca locale a candidato e rollback ma impedire consumo eccessivo e thrashing.
- **Gate G3: BLOCCATO**. Niente promozione skill, niente V3 nel gameplay, nessun OTA.
- **Non inferire** che 2.000 decisioni siano sempre abbastanza per qualunque architettura futura; il risultato riguarda **questo** learner, **questa** arena e questi test.

**Fonte primaria:** [log GitHub della CI 38973440670](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38973440670). Data test 16/10/2026 UTC, documentato il 17/10/2026 (Europe/Rome).

# ZERO 3.0 — V3.42 | LONG 10.000 esperienze consecutive BOX — esito empirico

**18 ottobre 2026.** Leggere [master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [protocollo prereg V342](ZERO30_V342_TEN_THOUSAND_CONTIGUOUS_BOX_PROTOCOL.md), [V341 results](ZERO30_V341_PROPRIOCEPTIVE_COHERENCE_RESULTS.md). GitHub Actions **[39073887058 SUCCESS tecnico](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39073887058)**, commit sperimentale `a22042d4eb2f5a8b99cf733c230d303733558d7b`. Primo run [39073812119 FALLITO](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39073812119) solo per nome errato del file `Zero3JointWorldV342.java` nel workflow: corretto a `Zero3JointWorldV339.java`, **nessun tuning né modifica della fisica**. Il run successivo ha compilato e simulato tutte e tre le vite e Android Debug `BUILD SUCCESSFUL in 41s`, senza APK firmata, merge o OTA.

## Perché questo esperimento differisce dal 3.39/3.41

Questa prova è nata dalla richiesta esplicita dell'utente: **almeno 10.000 esperienze autentiche nella stessa vita** per valutare se il cervello impara a spostare le casse. Vecchi test = 96 decisioni/vita; V342 usa unica nascita BOX_NEAR nuova `ordinal=1601`, gli stessi oggetti/posizioni iniziali per OLD_GLOBAL / FULL_LOCAL_DELAY / FROZEN, ma tre organismi con propri 96 punti, gel, WORLD, cortex, actor V317/sensori e vera testa adesiva V319 pretrained/frozen identica. Nessun output motore condiviso. `10.000 decisioni ×12 substep ×dt0.025=120.000 veri substep` = 50 minuti **virtuali** per ciascuno dei due organismi che completa la vita; 30.000 scelte rappresentano il massimo teorico, non il numero reale se uno è censurato.

Il prototipo non implementa tutta la morte per energia, gemme di premio viola sotto la BOX o obiettivo di trasporto; misura fisiologia locomotoria/BOX in mondo fisico. Quindi non affermare che movimento BOX casuale provi pianificazione o intenzione di prendere un premio.

### Risultati e metriche BOX verificate

| Metrica | OLD_GLOBAL | FULL_LOCAL_DELAY | FROZEN |
|---|---:|---:|---:|
| Decisioni **REALMENTE completate** | **4054 (censura)** | **10000** | **10000** |
| Real physical substeps | 48655 (ultima decisione interrotta al substep 6) | 120000 | 120000 |
| Peso cortex aggiornato | 4053 | **7499** | **0** |
| Frame autentici `box.bilateralHeldNow` | 4003 | **1931** | **7934** |
| Frame qualsiasi BOX owned grip | 48653 | 23048 | 119998 |
| BOX netto da birth XY | 5.351556 | 3.760618 | **12.810951** |
| Percorso BOX cumulato per real physical step | 17.901072 | 13.190067 | **58.221730** |
| BOX percorrenza **mentre true bilateral** | 17.229059 | 11.595377 | **52.149631** |
| BOX percorrenza dopo/senza bilateral (include inerzia) | 0.672013 | 1.594690 | 6.072099 |
| Numero frame con BOX movimento rilevato >1e-4 | 3295 | 2867 | **11248** |
| Primo frame di movimento BOX/bilateral | 10185 | 9125 | 10184 |
| RAW work gel accumulato | 1.750923157 (non comparabile durata) | **5.786657810** | **4.032796383** |
| Signed progress COM vs desiderio motorio | −1.73758 (4054 decisions) | **+5787.16914** | −4.42068 |
| Joint V338 attempts/repaired/unresolved nel braccio | 12 /11 / **1** | 8 /8 / 0 | 1 /1 / 0 |
| **Integrità per 10k** | **FAIL** crossing non risolto a decisione4054 | PASS | PASS |

La BOX `law=5` riceve effettivamente nuova velocità da `SquareGripPuzzle` **SOLO con presa opposta autentica**, quindi le due varianti che la spostano lo fanno con veri comandi e geometria, non con falsa `skin.gripHold`. Il moto residuo può persistere grazie alla velocità BOX precedente che viene frenata da `Entity.tick`; la separazione `grossWithTrueBilateral` vs `grossWithoutBilateral` ne tiene conto. **Questo resta un confronto comportamentale, non prova causale che il cervello abbia imparato una tecnica**.

### Evoluzione nel tempo (cumulati dopo ogni mille decisioni)

| Braccio | Decisioni | BOX gross | BOX bilaterale frames | RAW work | Cortex weight updates |
|---|---:|---:|---:|---:|---:|
| OLD | 1000 | 9.331886 | 1610 | 0.407322 | 999 |
| OLD | 2000 | 17.685649 | 2927 | 0.804949 | 1999 |
| OLD | 3000 | 17.751545 | 3053 | 1.244482 | 2999 |
| OLD | 4000 | 17.901072 | 4003 | 1.726003 | 3999 |
| OLD | **4054 CENSOR** | 17.901072 | 4003 | 1.750923 | 4053 |
| FULL | 1000 | 3.740999 | 499 | 0.437502 | 749 |
| FULL | 2000 | **13.190067** | **1931** | 0.859312 | 1499 |
| FULL | 3000 | 13.190067 | 1931 | 1.203400 | 2249 |
| FULL | 5000 | 13.190067 | 1931 | 2.086296 | 3749 |
| FULL | 8000 | 13.190067 | 1931 | 3.994823 | 5999 |
| FULL | 10000 | **13.190067** | **1931** | **5.786658** | **7499** |
| FROZEN | 1000 | 9.036219 | 1491 | 0.413603 | 0 |
| FROZEN | 2000 | 15.488706 | 2408 | 0.821178 | 0 |
| FROZEN | 3000 | 15.488706 | 2408 | 1.219224 | 0 |
| FROZEN | 5000 | 24.452497 | 3522 | 2.048261 | 0 |
| FROZEN | 7000 | 32.638907 | 4529 | 2.828666 | 0 |
| FROZEN | 9000 | 54.693572 | 7495 | 3.632352 | 0 |
| FROZEN | 10000 | **58.221730** | **7934** | **4.032796** | **0** |

**Conferma direttamente che il FULL riesce a spostare una cassa con presa bilaterale nelle prime 2000 decisioni, ma poi non la sposta più tra 2000 e 10000**: `BOX_gross=13.190067`, `BOX_net=3.760618`, `BOX_bilateralFrames=1931`, `BOX_ownedFrames=23048` restano tutte invarianti da decisione2000 al termine, mentre il centro continua a progredire e la corteccia riceve **6000 ulteriori aggiornamenti** 1499→7499. Il problema principale **non è che il corpo non possa spostare la BOX, né che 10000 decisioni siano troppo poche**: è che la politica FULL non mantiene contatto e manipolazione con la BOX nel lungo episodio. Non sappiamo ancora se la abbandoni fisicamente, cambi intenzione, o fallisca la riconquista: la V342 non ha registrato la distanza COM↔BOX per decile, dunque non attribuire una causa definitiva senza nuova forensic sui dati già consumati.

**FROZEN**, con head di adesione V319 già pretrained su vere prove ma cortex weights immutati, riprende a spostare la BOX anche molto più avanti nel rollout, con **circa 4.42 volte** il gross FULL (58.22/13.19) e senza alcun aggiornamento sinaptico del cortex. Il FULL consuma **circa +43.5% RAW work** rispetto al FROZEN, ma ottiene net box movement **3.76 vs 12.81** e circa 24% delle sue frame bilateral (1931/7934). La locomozione intenzionale signed del FULL +5787 vs FROZEN −4.42 non coincide con successo sul task BOX.

**OLD** ha vera presa e movimento iniziali ma al passo fisico `48655` (decisione 4054, substep6) compare `selfCrossings=1` e il joint guard V338 non trova soluzione (`12 attempts/11 repaired/1 unresolved` in OLD). La regressione censura correttamente, non inventa le 5946 decisioni mancanti. Sono completate **24054 decisioni** reali complessive fra i tre bracci, non 30000.

### Giudizio scientifico e prossimi passi

`V342 LONG_HORIZON_VERDICT completeAll3=false physicalGate=FAIL boxMovementGate=NOT_YET_CAUSAL_PROOF crossScenarioGeneralization=NOT_TESTED fullBodySaveRestart=NOT_TESTED fullSurvivalModel=NOT_TESTED G3_BLOCKED G5_BLOCKED NO_OTA NO_MERGE`.

È una svolta diagnostica contro i precedenti risultati inconcludenti delle vite da 96, ma **G3 scientific gate NON SUPERATO**: una vita è censurata da intreccio, la politica addestrata FULL muove la BOX meno del riferimento FROZEN, non è dimostrata generalizzazione su nuove BOX, e il salvataggio completo a 10k è ancora bloccato dal limite delle 256 esperienze del vecchio `Zero3FullArchiveStoreV322B`.

Per **V343** preregistrare un vero programma motivazionale ad alta durata con premi/energie nascoste sotto BOX invece di sola locomozione senza scopo manipolativo; separare premio vero della vita da reward per singola presa/gait e non forzare efferenze/forma, monitorare **COM↔BOX distance** e countdown delle competenze (approach/contact/two-sided grip/traction/disengagement/reacquisition), ispezionare selfCrossing old al 4054 come DIAGNOSTIC consumed pose, non per rituning sul seed1601. Prima del prossimo blind introdurre segmenti archivio/checkpoint che possano eseguire restart metà stessa vita e provare body+gel+BOX+actor+cortex+head+RNG con replay hash, perché il vecchio formato non supera256 record. **Non abilitare app/OTA, nessuna modifica ai 12 brain V2.2.**


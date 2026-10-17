# ZERO 3.0 — V3.32 | Credito motorio locale e temporale: confronto reale contro vecchio e frozen

**Preregistrazione 17/10/2026, prima di creare codice o guardare BLIND.** Leggere Master, implementation contract, V3.19, V3.30, V3.31. Nessuna modifica a `GameActivity`, `SymbioteMatterV1` o head V319: lo sviluppo è un gate isolato per G3.

## Ipotesi
V331 ha dimostrato risposte fisiche settoriali (+/−0,20) non lineari e ritardate: 481/576 con componente tardiva dominante; 291/576 hanno segno differenziale opposto all'intenzione. Il vecchio `AdaptiveCortex.learn` usa un unico `score` dell'intera decisione e `noise[out]`/feature aggregate per pesare ciascun settore. Prova falsificabile: una **eligibility trace locale** dei motoneuroni non adesivi, modulata da **dinamica realmente osservata delle patch e del COM dopo il solver** e mantenuta per un intervallo temporale, può ridurre il credito erroneo e migliorare movimento utile rispetto al vecchio algoritmo.

## Implementazione
- `Zero3TemporalMotorCreditV332` deve ricevere solo il PRE (decisione spontanea dell'attore V317), efferenze *realmente* mandate al solver e POST di ogni substep. Niente accesso a futuri oggetti/gemme, coordinate del mondo come target, labels del laboratorio, segreto del solver, duplicazione del corpo, e nessuna modifica diretta a COM/physics.
- Prototipo su **stessa architettura 64×8 di AdaptiveCortex** con sole differenze nell'attribuzione del credito, 3 canali motori per settore (0/1/3); canale2 resta posseduto dal motoneurone adesivo V319 congelato e preaddestrato. La nuova classe può usare una nuova istanza di corteccia e un'interfaccia di update localizzato, ma non alterare i default del runtime. Parametri fissati: perturbazioni fisiche stocastiche del vecchio cortex; aggiornamento bounded ≤0,002 per peso; eleggibilità accumulata dai comandi/feature PRE per settore, response POST locale con componente di deformazione/velocità e COM, costo fisico reale; decadimento 0,82 fra decisioni per tenere conto del ritardo. La formula finale va esplicitata nel codice e rapportata come ipotesi, non spacciata per gradiente o causa attribuita esattamente.
- Confronto **3 bracci** a stessi seed di nascita e actor FROZEN (identico V317 in tutte le condizioni), head V319 preaddestrata e FROZEN identica, stesse 64 uscite cortex e solver, stessi parametri di esplorazione per TRAIN; variabile unica `LEGACY_GLOBAL_CREDIT`, `NEW_LOCAL_TEMPORAL_CREDIT`, `FROZEN_NO_CREDIT`. Niente training sul BLIND.
- TRAIN: 32 episodi ×96 decisioni per braccio, quattro famiglie in ordine bilanciato, nuovi seed/pose disgiunti da V330/V331. Solo confronto di esperienza, nessuna valutazione di successo da soli contatori di update.
- BLIND: 24 vite ×96 decisioni per braccio (6/famiglia), nuovi seed/pose/gap diversi da TRAIN/V330/V331, pesi cortex congelati, esplorazione OFF e attore/head identici per tutte le condizioni. A fine vita misurare reale locomozione `signedAlongActorIntent`, rapporto netto/lordo, `gel.work`, costo per progresso utile, wrong-way, distanza, grip vero BOX (generico e bilaterale), vite censurate/morte. Confronti familiari e aggregato pubblicati anche se negativi.
- Preregistro un **gate G3 preliminare**: ≥3/4 famiglie con efficienza direzionale almeno +10% rispetto **a entrambi** old e frozen, senza aumentare costo per progresso utile >10%, almeno 80% decisioni fisiche valide per famiglia, nessuna regressione ≥20% di grip BOX rispetto al controllo old a parità di coverage. In BLIND niente refit/variazioni soglie. Successo eventuale vale solo `E3_PRELIMINARY_PASS_NOT_LONG_HORIZON`; G3 definitivo richiede 2.000+ esperienze, brain retention, trasferimento nuovi mondi e prova Android.
- Se la formulazione locale non è trainabile senza cambiare ownership delle scelte/feature o se risultati FAIL, conservare solo la diagnosi e le evidenze; NON inventare gate PASS o aggiornare la produzione. Android Debug compilation su ramo Draft, zero OTA, 12 slot legacy 2.2 intatti.

## Risultati

## RISULTATI VERIFICATI — GitHub Actions 39039655430 (17/10/2026)

**[CI definitiva V3.32, run 39039655430](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39039655430) — SUCCESS tecnico e `preregG3Preliminary=PASS_NOT_LONG_HORIZON`.** Job 117416353016, log letto. Android Debug **BUILD SUCCESSFUL in 1m**. Le precedenti run non concluse riportavano un errore di scope nel contatore delle prese, poi un'autointersezione corporea reale (non segnalata da `gel.invalid`), rispettivamente corrette con fix di test e regola di **censura esplicita**, SENZA modificare il solver, i mondi, i semi, le soglie o il reward dopo avere visto il BLIND. Il gate finale ha **quattro famiglie migliorate su quattro** sulla distribuzione preregistrata; questo NON è un G3 definitivo.

### Progetto realmente eseguito

- Preaddestramento indipendente head V319 tramite **3.072 intenzioni e 8.996 frame hold veri**, poi congelata con identici pesi nei 3 bracci. L'adesione non viene ristudiata o regolata dalla nuova regola e resta esterna al canale di apprendimento della locomozione.
- TRAIN: **32 episodi per braccio**, massimo 3.072 scelte spontanee del recurrent actor V317 ciascuno; 3 bracci su stesse pose/scene/inizializzazione motoria: `OLD_GLOBAL` (credit global V330), `LOCAL_TEMPORAL` (V332), `FROZEN` (zero gradiente). Contatori: **3.050 updates vecchio**, **3.021 updates locale ritardato**, **0 congelato**. Tre episodi TRAIN hanno manifestato **autointersezione materiale** (OLD BOX ordinal1 dopo 75 decisioni, LOCAL dopo 77, FROZEN dopo 75). La decisione incompleta **NON è contata**: log `V332 PHYSICAL_CENSOR`. È una differenza fisica reale, non un errore risolto cambiando attriti o eliminando quelle scene.
- BLIND: **24 vite × 96 = 2.304 scelte per braccio**, 6 vite in ciascuna di FREE, BOX_NEAR, DISK_NEAR, BOX_FAR. **27.648 integrazioni reali gel per braccio**, 0 vite censurate e tutte le 576 decisioni valide per famiglia. Pesature neural cortex sono caricate in istanze di valutazione vergini; nessuna esplorazione, nessun refit e nessun update. Stesso actor V317 frozen e inizializzazione, stesse condizioni fisiche di partenza, stessa V319 preaddestrata frozen.
- `Zero3LearningFoundation.AdaptiveCortex` conserva esattamente il percorso OLD per tutto il runtime V325: due metodi separati lab-only `captureProbeV332` e `learnV332` costruiscono eleggibilità del settore SOLO da feature/comando effettuato e risultati fisici PRE→12 veri tick→POST; l'update su decisione n viene concesso dopo le conseguenze reali della decisione n+1, pesate 0,82. Il segnale locale include deformazione relativa della membrana e movimento COM proiettati sull'intenzione realmente scelta, costo misurato `gel.work` ripartito come proxy di peso dei comandi. **La ripartizione del lavoro per settore è solo una stima, NON misura di forza causale identificata**. Il canale2 resta della V319 e non riceve update del V332; ampiezza singolo weight update ≤0,002. Attore senza training in questa fixture, né vero world model, critic, replay skill.

### Metriche BLIND — log reale, non valori attesi

**Spostamento firmato nella direzione *scelta* dall'attore**, sommato sulle 576 decisioni in ciascuna famiglia:

| Famiglia | Vecchio cortex | Nuovo locale | Congelato | Efficienza vecchio | Nuovo | Congelato |
|---|---:|---:|---:|---:|---:|---:|
| FREE | +5,42045 | **+135,85516** | +17,31112 | 0,08551 | **0,64093** | 0,24799 |
| BOX_NEAR | −11,69400 | **−11,64830** | −15,98410 | −0,20932 | **−0,14487** | −0,28782 |
| DISK_NEAR | +7,37862 | **+69,41936** | +14,00207 | 0,16292 | **0,55991** | 0,28504 |
| BOX_FAR | +15,32782 | **+136,06696** | +27,49205 | 0,28421 | **0,71391** | 0,43632 |

Per ciascuna famiglia il log conferma `directionBetter=true`, `costNotWorse=true`, `physicalCoverage=true`, `gripRetained=true`, `scientificWin=true`. Quindi **`trainedFamWins=4/4`** per la soglia preregistrata. Il progresso utile complessivo non è stimato per extrapolazione: si tratta di spostamenti fisici registrati nel solver. **La famiglia BOX_NEAR rimane NEGATIVA anche col nuovo modello: perciò non dimostra locomozione utile in presenza della scatola.**

**Costo lavoro per progresso (lavoro reale / [1 + max(0, avanzamento firmato)]):**

| Famiglia | OLD | V332 NEW | FROZEN |
|---|---:|---:|---:|
| FREE | 0,016761896 | **0,001021747** | 0,006054803 |
| BOX_NEAR | 0,196563926 | 0,215637304 | 0,199908382 |
| DISK_NEAR | 0,014946389 | **0,002022469** | 0,008445108 |
| BOX_FAR | 0,006508913 | **0,000989247** | 0,003832891 |

**Caveat costo BOX:** V332 spende **0,215637304** vs OLD **0,196563926** (circa +9,70%) e FROZEN **0,199908382** (circa +7,87%), quindi passa il massimo preregistrato del +10% ma non è più efficiente su BOX. Il criterio di costo usa denominatore 1 quando l'avanzamento firmato è negativo; NON etichettarlo «lavoro per movimento utile» vicino alla scatola.

**Contatti / presa BOX:** tutti e 3 i bracci hanno **6.900 frame di vera presa generica su scatola** e 6/6 episodi BOX con hold; 6.912 contatti BOX nella fixture, e quindi la soglia di ritenzione grip generico passa. **Ma i frame di presa bilaterale sono 1 V332 contro 55 OLD e 588 FROZEN**. Questo è un **grave difetto non coperto dal gate preregistrato**, che riguarda presa generica, e impedisce di promuovere il controllo del corpo come abilità manipolativa. Non minimizzarlo, né cancellare la prova V3.19 che aveva superato la sua selettività adesiva distinta.

### Conclusioni da rispettare

1. **PASS limitato:** V3.32 fornisce la prima evidenza BLIND che, sulla distribuzione sperimentale testata e con actor V317 frozen, il nuovo credito locale/ritardato può produrre movimento direzionale netto più forte del vecchio cortex e del congelato nelle famiglie FREE, DISK_NEAR e BOX_FAR. Sono 24 vite nuove, non migliaia di mondi indipendenti, non prova sul Galaxy S21 o su corpo/oggetti più complessi.
2. **G3 generale NON PROMOSSO:** BOX_NEAR resta nella direzione contraria; la presa bilaterale quasi scompare, e la performance addestrata dopo ripresa intero cervello+organismo per 2.000+ nuove esperienze NON è ancora stata verificata. Non integrare questo credit learner in `Zero3RuntimeV325` o `GameActivity` né pubblicare un APK.
3. **Identificazione causale limitata:** il V331 prova l'effetto degli impulsi +/- in mondi duplicati; V332, invece, usa solo le sensazioni reali di un singolo organismo e una ripartizione locale di work stimata. L'effetto della regola di credito e l'esclusione di channel2 V319 sono modificati insieme rispetto a OLD; quindi questo A/B **NON isola causalmente ogni singolo termine** del nuovo algoritmo. Occorre un'ablation sul delay e sul credito locale separatamente.
4. **Prossimo V3.33 prima di qualunque applicazione live:** protocollo a prova nuova con tre ablation (`NO_DELAY`, `NO_LOCAL`, `FULL`) e baseline OLD/FROZEN, focus bilaterale BOX e movimento positivo anche in contatto; richiedere guardrail sulla presa bilaterale, non soltanto presa generica. Ulteriori seed/geometrie radicalmente nuove e durate 2.000 decisioni con resume. Non utilizzare il BLIND V332 consumato per ritoccare pesi/soglie del learner.

**Stato produzione:** Draft PR #87, nessun merge, OTA `0.11.6-ota.172` invariata, 12 cervelli legacy 2.2 non toccati.

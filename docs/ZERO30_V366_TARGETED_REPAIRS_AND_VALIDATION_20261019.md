# ZERO3 V366 — Correzioni mirate: direzioni fisiche, memoria visuale e selezione delle conseguenze

**19 ottobre 2026.** Stato: **candidato in PR #99 DRAFT; NON pubblicato e NON dichiarato recupero della sopravvivenza autonoma**. Confrontare questo report con `ZERO30_V366_RIGHTWARD_DRIFT_AND_MIRRORED_BLUE_RED_FORENSIC_20261019.md` e il master. La versione ufficiale OTA363 resta invariata; blocco firma OTA da PR100 già nel ramo release.

## 1. Attuatore a quattro direzioni — ripristino limitato, non un cervello 2.2 alternativo
Preservato il GRU64 V356 come unico soggetto che genera i 5 parametri d'intenzione. Il prototipo V364 sperimentale usa `SymbioteMotorHeadV1` come **sola periferica neuromuscolare**, congelata: 75% dei comandi effettivi di movimento CH0,CH1,CH3 vengono dal vecchio readout motorio capace, il restante 25% dalla corteccia V3. Il canale CH2 è **esclusivamente della V319**. Il vecchio `SensorimotorMind.choose()` non decide nulla. Percorso originale `GameActivity → Zero3OriginalBrainV356.actuate(...,symMotor,...) → gel.step` condiviso col test. Valori di laboratorio (240 direzioni fisiche ciascuna, stesso gel) est/nord/ovest/sud tutti +349..355U; il vecchio solo V3 muoveva ovest e sud al contrario. **L'actor non ha ancora dimostrato apprendimento di ricerca di risorse nell'ambiente originale.**

## 2. Modello della vista — correggere perdita laterale SENZA sostituire ricordi
`Zero3VisionContextV366` riusa **byte-identici** i 12 ingressi `Zero3VisionContextV361` e aggiunge tre coefficienti `sum(spectral[frequency][angle]*cos(angle))/8`, uno per ciascuna firma fisicamente osservata. Non contiene alcun giudizio «rosso cattivo / blu buono», bersaglio, coordinate di oggetto o motricità.

- Prima: blue/west + red/east e blue/east + red/west producevano lo **stesso** contesto V354 a 12 valori.
- Dopo: le nuove posizioni 12,13,14 differiscono per ogni firma. I 194 ingressi visuali diretti del GRU restano identici, la fisica e le risorse sono invariate.
- `Zero3ExecutiveV354` usa 15 feature per **nuove** esperienze, conservando i primi 12 pesi originali e l'originario bias (nuovo indice15); le tre nuove pesature partono da 0 dopo la migrazione. Decoder legge checkpoint V354, V361 e V366; encoder standard salva schema V366 con 15 valori, ricordi nuovi marcati schema366.
- Opzioni già imparate V354 e V361 **non vengono reinterpretate come se conoscessero informazioni che non possedevano**. Continuano a confrontarsi coi propri contesti originali (12 feature e la medesima distanza media /12, preservando soglie). Le vecchie memorie non sono cancellate, mantenute come eventi realmente appresi.
- Se due spettri differenti sono osservati da lati X opposti, **vecchie opzioni 12D** vengono temporaneamente escluse da richiamo e credito: la rappresentazione storica non consentiva discriminazione e potrebbe richiamare un'azione per la scena capovolta. Le nuove opzioni 15D e le vecchie in ambienti non ambigui restano utilizzabili; nessun colore è associato a ricompense fisse.
- Sono vietati reset di GRU/cortex/grip, cambio degli attributi del mondo, perdita di slot nominati. I test forniscono un vero serializzatore V361 **solo per il QA**, mai invocato nei salvataggi di produzione.

## 3. Modello V353 GIÀ approvato: controllo rischio cost-head anziché veto totale
`Zero3CognitionV353` rimane l'originale tre-predittori a 12 uscite (PRE194/azione→POST reale), senza nuovo modulo. Prova telefono OTA363: trusted energy=true, harm=true, cost=false per baseline heldout quasi piatta. Il vecchio `select` non considerava NESSUNA azione diversa se il costo risultava inaffidabile.

Nuovo percorso per **sole tre azioni spontaneamente campionate dal GRU**:
- Richiede 64+ veri heldout e validazione superiore alla baseline per **entrambi** i segnali energia e danno (vecchie `trusted(0)`, `trusted(1)` rigorosamente immutate).
- Se costo è già fidato: **algoritmo originale** di selezione invariato.
- Se il costo non è fidato: non lo ignora né suppone consumo=0. Continua a sottrarne la previsione ma richiede che l'alternativa superi la scelta originaria di almeno **4×RMSE dell'errore reale heldout sul costo**. Se l'errore non è finito o è troppo grande, conserva l'azione originale. L'incertezza dei predittori energia/danno è anch'essa penalizzata.
- Il pianificatore multistep V354 continua a esigere tutte le sue prove a orizzonti 2/3; nessuna falsificazione del contatore di confidenza, dei ricordi o delle ricompense. Nessun override di azioni fuori dalle tre proposte originali.

## Evidenza e limiti
- [CI V364/V366 Android 39310101081](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39310101081): 3/3 motore e grip + 4/4 modello ottico/vecchia V361 migrazione/valenza costa = PASS su APK Android originale. Il primo test di migrazione confrontava impropriamente il contributo delle **nuove** sinapsi contro un checkpoint antico che non poteva codificarle; corretta la fixture per confrontare *solo* i 12 pesi realmente storici e il bias, test passato.
- Un nuovo test effettivo `trustedEnergyAndHarmCanImproveActorProposalWithoutBlindCostGuess` controlla la selezione V353 al livello del vero modello: energia+damage trusted e costo low-uncertainty permettono opzione migliore; cost-variance alta o energia inaffidabile fanno conservare GRU originale. In esecuzione via `.github/workflows/zero364-motor-bridge-android.yml`.
- Workflow finale ripete TUTTI i test veri `tools/zero362_android_ui.sh` (V356, V361, V362) e successivamente 3 motor/periphery + 5 V366. Questo può rilevare regressioni di vista e checkpoint nei vecchi test, e bloccare la promozione.
- Il numero di decisioni neurali in un test non prova l'evitamento di red. L'utente ha segnalato 35 contatti; ultimo report OTA363 conferma 30 contatti su 6 vite e 0 blu. Per approvare G4/G5 servono 2k e 10k intenzioni veramente autonome in mondi di risorse nuovi con misure per vita: frutto blu raccolto, contatti rossi, sopravvivenza, progresso firmato reale, distribuzione delle prime 12 scelte e confidenza predittiva, resume dei vecchi checkpoint.
- **NON PUBBLICARE OTA** finché i risultati di comportamento non supportano un vantaggio sulla sopravvivenza; l'app deve restare installabile come OTA363 senza reinstallazione né reset.

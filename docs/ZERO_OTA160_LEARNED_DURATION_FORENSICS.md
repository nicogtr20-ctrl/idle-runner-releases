# ZERO OTA160 — controllo della durata motoria con esperienza reale (candidato, NON distribuito)

Data: 15 ottobre 2026. Ramo isolato `fix/zero160-learned-motor-duration`; origine OTA159 firmata, stesso cervello 2.2, corpo simbionte, package Android, 12 slot e fisica. **Nessun APK 160 può essere dichiarato pronto finché JVM, Android, publisher firmato e prova HTTPS non superano le verifiche.**

## Evidenza reale di partenza
Ultimo export Android volontario ramo `zero-telemetry`, `20261015-152553-130aa19184fe2`. Contatti energia salvati `elt1|22|45|1|0|1`: 22 blu, 45 rossi, 1 viola (storico del profilo). Corteccia `smh1`: 15.686 selezioni, 12.545 aggiornamenti e 3.137 heldout. Ultimi 25 vs precedenti 25 episodi completi: blu 11 vs 12, rossi 28 vs 16, spostamenti oggetti 1 vs 5, distanza netta massima per vita media 119.1 vs 147.1 unità, durata 196.7 vs 211.9 secondi. Senza un controllo del mondo casuale e del tempo di vita i confronti NON sono causalmente equivalenti.

Blackbox delle ultime due vite `decision_flight_current_v1162` e `last_v1162`: 149 e 292 scelte, cambi azione 95.3% e 96.6%, cambia anche l'argmax prima di epsilon nell'83.8% e 84.9% dei confini di decisione. I vettori di movimento accumulati suggeriscono forte cancellazione, pur senza costituire un benchmark fisico matched. Sensori fotonici non muti: canali blu presenti 147/149 nel primo e 272/292 nel secondo; gli stessi frame possono contenere il rosso. Non segue che il modello distingua correttamente *quale* avvicinarsi: è un problema ancora da verificare.

Benchmark fisico preesistente `docs/ZERO_OTA152_TRUE_MOTOR_SURVIVAL_CREDIT.md`: locomozione possibile, comando costante 40 secondi ≈296 unità, controllo variabile ≈11 unità. Test su impulsi artificiali, non prova di comportamento spontaneo Android.

## Ipotesi causale e limiti
Non trattare il peggioramento soltanto come inesperienza, né come prova di un bug unico della fisica: il nucleo 2.2 riconsidera una delle 16 azioni ogni 0.65 secondi simulati; la corteccia distribuita ne converte i segnali in nuove spinte. Penalità per ripetizione e forte esplorazione possono portare a cambi frequenti e scarso movimento netto. L'argomento `motion_context_shadow_v1164` resta un osservatore; nessuna ricompensa `cammina a nord` o attrazione fissa al blu viene introdotta.

## Implementazione candidato
Nuovo `MotorDurationV22` separato con sola durata dell'azione selezionata dal **medesimo** `RecurrentCoreV22`. Una nuova scelta 2.2 può scegliere, mediante sperimentazione locale, una durata tra **1, 2, 4** intervalli (≈0.65, 1.30, 2.60 s simulati). Durante l'intervallo la stessa azione 2.2 viene riemessa ai confini periodici, con aggiornamenti reali dei sensori e del modello, e il motore continuo riceve sempre le letture del corpo attuale. Questo non fornisce una direzione, un obiettivo, una risorsa o una tecnica manuale.

- Condizioni di interruzione: contatto dannoso dell'ultimo intervallo, riserva normalizzata ≤0.16, esplorazione causale nuova del nucleo, oppure nuovo argmax 2.2 migliore di 0.24 nel punteggio. Nessun blocco permanente. L'azione del nuovo cervello in caso di pericolo o nuova scelta forte prevale.
- Il valore della durata è valutato esclusivamente su osservazioni del tessuto, spostamento del baricentro fisico, contatto reale, spesa muscolare realmente sottratta e spostamento esterno verificato. Per la durata si usa un proxy limitato di coerenza e spostamento netto, non una ricompensa sintetica nel Q del cervello principale.
- Tre durate sono esplorate senza etichette; un'opzione può essere preferita solo dopo almeno 16 risultati di training e 6 risultati non usati per allenarla, con controllo della differenza tra medie. Continua una quota di esplorazione. Questi valori sono iperparametri di controllo: non provano ottimalità né competenza spontanea.
- Un quinto esperimento di durata è escluso dall'aggiornamento delle stime di training; la fase heldout sopravvive alle riaperture. I check su `mean` descrivono uno screening predittivo modesto, non una stima causale A/B.
- Save opzionale `motor_duration_v160` con stime, verifiche, contatori e validazione completa prima di restore. Slot e mondo persistono; morte o riapertura eliminano solo un esperimento attivo non completamente attribuibile.
- `SensorimotorMind.choosePrimaryV22` mantiene il solo arbitro neurale; `observe()` addebita conseguenze motorie realmente osservate a ogni 0.65 s; nessun cambiamento a `RecurrentCoreV22` o ai suoi pesi/replay/ricompense, alla fisica XPBD, al sensore visivo, al contatto delle energie o alla legge dei colori.
- Atlante `Corpo → Durata del movimento`: valori realmente salvati, avvii, prosecuzioni, interruzioni, opzioni e holdout; vista sola lettura. `Evoluzione` mostra contatori, non una valutazione inventata della bravura.

## Gate e criteri di rilascio
1. `Zero160MotorDurationRegression`: autonomia delle azioni, prosecuzioni reali selezionate senza target, interruzione danno/rischio/nuovo argmax, crescita contatori e train vs heldout, roundtrip esatto, save invalido senza perdita, continuità attraverso le morti.
2. Pre-release workflow `zero22-corrective-qa.yml` per il ramo candidato: compilazione completa di JVM/Android, tutte regressioni vecchie e nuova. Publisher `overmind-ota-publish.yml` aggiunge la classe e la regressione al comando JVM e pin SHA esatti per ogni Java modificato, mantenendo invariati gli altri gate.
3. Merge SOLO se QA e Android build riusciti. Publisher firmato su `feature/zero22-active-recurrent-ota117` può produrre OTA160 a versionCode 1160 tramite app's existing in-place AGGIORNA (mai disinstallare). Richiesta verifica SHA APK pubblica, manifest live, package e firma.
4. Solo DOPO prova Galaxy S21: confrontare 25 vite precedenti/post, percentuale cambi di azione, movimento netto/effettivo, blu/rosso per minuti simulati, true physical contact, durate apprese per azione, aborti per pericolo, per-zone effetti. Abbattere i falsi pick-up vicini e distinguere prestazione dal numero di aggiornamenti.

## Stato al momento della proposta
Codice e regression sul ramo separato. Al primo tentativo di QA GitHub Actions `38765532027` ha concluso dopo circa 4 secondi con **zero step eseguiti**, come altri job del repository nelle vicinanze; impossibile inferire errori JVM/Android da quel risultato, né considerarlo pass. Ripetere quando runner disponibili. **Non annunciare una release firmata né incremento effettivo dei risultati su Android prima della prova.**

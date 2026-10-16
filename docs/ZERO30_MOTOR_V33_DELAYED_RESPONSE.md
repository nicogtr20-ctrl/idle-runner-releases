# ZERO 3.0 — G3/G5 LAB V3.3: RISPOSTA MUSCOLARE CAUSALE RITARDATA

**Stato:** laboratorio Java/CI sul ramo \`feature/zero30-foundation-g0-g3-20261016\`; NON attivo in gameplay, nessun salvataggio migrato, nessuna OTA V3. Data: 17 ottobre 2026 (Europe/Rome).

**Lettura vincolante preventiva:** [MASTER BLUEPRINT](ZERO30_MASTER_BLUEPRINT.md), [CONTRATTO IMPLEMENTAZIONE](ZERO30_IMPLEMENTATION_CONTRACT.md), [NUOVA UI 3.0](ZERO30_UI_UX_SPEC.md), [MOTOR V3.2](ZERO30_MOTOR_V32_COST_CONSTRAINED.md), [BENCHMARK A 2.000](ZERO30_LONG_HORIZON_2000_20261017.md).

**Codice:** [Zero3MotorImpulseResponseRegression.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorImpulseResponseRegression.java) — [Workflow](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/.github/workflows/zero30-foundation-qa.yml) — [PR #87 (DRAFT)](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/87).

## 1. Perché questa modifica

Il learner online esaminato fino a 2.000 esperienze modifica i pesi ma non conserva movimenti controllati: posture normali a 2.000: somma forward vergine **+27,970 U** vs allenato **−19,284 U**; cervello autonomo GRU, allineamento alle intenzioni congelato **+18,237 U** vs allenato **−5,059 U**. La ricerca locale V3.2 ha dato utility migliore ma 3/4 direzioni superano il limite di spreco. **Non basta aggiungere esperienze; serve un segnale di conseguenza specifico, non un reward globale.**

## 2. ADR ZERO3-M33: intervento reale + coppia controllata + ritardo

Il nuovo laboratorio non utilizza ricompense o traiettorie inventate. Per ogni prova:

1. Genera **due istanze realmente indipendenti** del solver \`SymbioteMatterV1\` in condizioni identiche, stesso corpo a 96 punti e 74 masse, stesso comando volontario e identici parametri baseline. Registra stato preintervento.
2. Prima di intervenire lascia entrambe evolvere per 12 frame a \`dt=.025\` secondi simulati.
3. In una sola istanza aggiunge per 12 frame una **piccola perturbazione localizzata di un solo tipo di attuatore** (radiale/tangenziale/adesione/tensione), in cinque settori vicini con pesi scalati. Applica la perturbazione esclusivamente nell'array dei **64 comandi effettivamente consumati** dal solver; **non** sposta mai artificialmente il centro di massa e non introduce forze occulte.
4. Dalla fine della perturbazione, continua altre **48 integrazioni fisiche** a muscoli baseline in entrambe le copie. Registra spostamento incrementale firmato durante impulso, contributo **ritardato**, distanza lorda differenziale e lavoro muscolare aggiuntivo.
5. Calcola l'effetto relativo alla traiettoria **controfattuale effettivamente simulata in un controllo fisico indipendente**: \`delta = outcome_pulse - outcome_matched_baseline\`, non deduce il risultato dall'intenzione nominale. Queste prove gemelle sono per il LABORATORIO, **non replay immaginari per l'allenamento di ZERO nel gioco**.
6. Addestra con regressione ridge un **modello leggero del segno/ampiezza della risposta**, condizionato su intenzione, orientamento iniziale, posizione della regione, tipo di muscolo e segno dell'intervento. Output distinti: effetto immediato, effetto ritardato, costo aggiunto. Non sono inserite coordinate delle gemme, comportamenti predefiniti o fasi di andatura.
7. L'allenamento usa pose **0° e 90°**. Test delle previsioni e scelta sperimentale della perturbazione su pose **+45° e −45°** (tenute fuori dai fit). La scelta è fatta dal modello **prima** di leggere il risultato fisico di quella prova.
8. Confronta heldout MSE del modello contro baseline **zero effetto** per ogni testa; registra anche un'opzione **nessun intervento** e la differenza osservata rispetto ad alternativa casuale. Nessun auto-promote nel cervello né salvataggio di skill.
9. Test automatico su solver reale, limiti numerici, topologia, segnalazioni degli effetti negativi, compilazione Android Debug senza pubblicazione.
10. Il laboratorio registra comunque forti limiti: stesse proprietà del mondo, niente ostacoli/gemme/storm, nessun seed ambientale indipendente, niente training online da esperienza vissuta normale, nessun model-based control di produzione. **Risolvere G3 e G5 richiederà scene nuove, seed e misure multiseme.**

## 3. Metriche e criteri falsificabili

Campioni previsti: 4 intenzioni × 2 pose training × 4 canali × 4 zone × 2 polarità = **256 coppie train**; stessa struttura nelle 2 pose unseen = **256 coppie heldout**. Ogni coppia ha 72 frame per istanza; **73.728 integrazioni del solver reale** complessive.

**Predizioni necessarie per parlare di progresso parziale:**

- MSE heldout immediate/ritardata inferiore a baseline zero, separatamente; in caso contrario il modello NON può consigliare azioni (nessuna falsa confidence).
- Direzione e costo misurati realmente per ciascuna decisione nel laboratorio; non premiare la deformazione interna se manca locomozione netta.
- Il vantaggio osservato delle perturbazioni suggerite dal modello va dimostrato su pose heldout senza campionare/allenare sul loro esito.
- Verificare che il costo non annulli il beneficio; un segnale utile ma con consumo eccessivo resta competenza non promossa.
- Per risultati del test fisico estesi: 400/1.000/2.000 decisioni precedenti restano validi come benchmark comparativo, ma non vanno attribuiti al nuovo predictor.
- Non dichiarare «modello causalmente generalizzabile» o «ZERO sa camminare» sulla base di appena due pose di train e due pose di validation.

## 4. Stato dei risultati

**CI avviata:** [Workflow ZERO 3.0 sperimentale](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions) su commit del ramo che introduce il modello. Scrivere di seguito il run, l'esito e ogni valore numerico dopo verifica del log. **La compilazione può essere verde anche quando il predictive gate è FALLITO**.

### Risultati verificati e ADR successive

### CI reali e risultato del modello corretto

- **[Primo modello 22 feature — CI 38975682552 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38975682552):** MSE immediate 0.005319504 (baseline zero 0.005320521); MSE ritardata 0.018453053 (zero 0.018610250). Miglioramento insufficiente, selezione vera di interventi utile in 4/8 condizioni.
- **[Modello corretto 42 feature — CI 38975833848 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38975833848):** aggiunte proiezioni geometriche **radiali/tangenziali dell'intenzione nel frame del corpo** e interazioni differenziate per ciascuno dei 4 tipi muscolari (non un filtro sul colore o un gait). Stesso dataset di 256 coppie train, 256 pose esclusivamente validation. MSE effetto immediato **0.000486950** vs baseline zero **0.005320521**; MSE effetto ritardato **0.000766748** vs baseline zero **0.018610250**. Miglioramento relativo circa **90.8% / 95.9%** sul confronto zero per l'MSE, nelle pose testate. MSE costo 0.000000000380 vs baseline zero 0.000000007780.
- Effetto fisico: somma ampiezze risposta immediata **10.589 U**, effetto aggiuntivo accumulato **dopo la contrazione** 21.759 U nel set heldout; il secondo contributo è circa 2.05 volte il primo (somme delle ampiezze, non singola traiettoria).
- **Selezione degli interventi:** 8/8 contesti direzione × posa testata producono incremento direzionale positivo, con lavoro aggiuntivo sotto 0.005; incremento complessivo fisicamente misurato **+4.24760 U**, lavoro addizionale **0.00120341**, utilità dopo penalizzazione costo **+4.19345**. Il singolo candidato casuale predefinito per ogni contesto ha somma firmata **+0.04763 U**; campione casuale è un riferimento esplorativo, non una distribuzione random completa.
- **Esito ingegneristico:** il modello predittivo specializzato **supera nettamente il baseline zero su due output motori** e propone interventi utili in questa famiglia ristretta di posture. È una prima **dimostrazione di controllabilità per piccoli interventi**, non G3 superato: non sono testate vite complete, competenze persistenti, shock, diversi corpi/scene, multi-seed, ricompensa di sopravvivenza o integrazione del brain 3.0. Il predictor è attualmente **interno alla regression Java**, non modulo di produzione, e non modifica alcun peso del cervello nel gioco.

**Decisione:** G3 apprendimento autonomo della locomozione **BLOCCATO**. G5 modello del mondo generale **BLOCCATO** (pur essendoci risultati positivi per la piccola testa di risposta motoria). **Nessun OTA e nessuna UI V3 simulata come implementata.**

## 5. Prossimo sviluppo necessario

1. Misurare esattamente cosa succede quando il modello prevede un intervento muscolare, inclusi effetti nelle 48 integrazioni successive. Correggere bias e normalizzazione se non batte baseline su holdout, senza contaminare pose tenute da parte.
2. Integrarlo eventualmente come **modello di risposta fisica per singolo gruppo** basato su osservazioni reali, non come un secondo cervello che sceglie autonomamente mete.
3. Produrre nuove prove con posture e urti/adesione differenti e variabilità non vista, confrontare gradienti vs perturbazione-paired e validazione a lungo termine.
4. Solo dopo introdurre memoria procedurale, life-long learning, neurogenesi, integrazione \`GameActivity\`, persistenza schema V3 e **UI V3 realmente collegata**.
5. Nessun merge nella branch publisher OTA né attivazione silenziosa del nuovo motore prima dei benchmark G3/G5 completi.

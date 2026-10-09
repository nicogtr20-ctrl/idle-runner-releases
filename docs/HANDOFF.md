# HANDOFF — punto di ripresa per un'altra chat o agente

**Ultimo aggiornamento:** 2026-10-09

### Riferimenti
Repository: `nicogtr20-ctrl/idle-runner-releases` (privato).
- Branch `main` = Idle Runner + `docs/` + infrastruttura updater; **NON** il codice OVERMIND attuale.
- Branch `experiment/overmind-zero-world-v03` = ultima versione OVERMIND realmente provata dall'utente.
- Branch `experiment/overmind-zero-physics-v04` = creato, NON procedere sul gameplay prima che updater sia funzionante.
- `experiment/overmind-zero-brain-v02` = esperimento motorio originale.

### Esperienze e feedback utenti
L'utente ha osservato ZERO-001: una forma nera deformabile, il nucleo spinge sulla membrana con un movimento arcuato, la membrana si gonfia, ZERO raggiunge effettivamente il punto stimolo. Dopo ~2.467 episodi benchmark -3,6→+179,7. ZERO-002 ha registrato 4.772 tentativi, 49 contatti, 3/3 tipi, benchmark +2,9→+204,4. Ha richiesto corpi tipo ferrofluido reale, nessuna appendice prefissata, scoperta autonoma del movimento e delle funzioni degli oggetti.

### Aggiornamento infrastruttura (9 ottobre 2026)
- Vercel **online**: https://overmind-zero-updates.vercel.app/latest.json (HTTP 200, `available:false`).
- Vercel project `overmind-zero-updates` `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, team `prime-1323`, root `updater/`, branch di produzione `main`.
- Codice del pulsante e downloader: branch `feature/overmind-inapp-updater`; package della nuova app **separata** `com.overmind.zero.ota`, per conservare la precedente `com.overmind.zero.world` con i suoi dati.
- Workflow firma/pubblicazione: `.github/workflows/overmind-ota-publish.yml`; richiede il secret unico `OVERMIND_SIGNING_BUNDLE` documentato in `docs/UPDATES.md`. Senza secrets CI compila ma non rilascia APK.
- La v0.4 del gameplay resta sospesa fino a prova updater Android su S21. Il client updater Android si trova nel branch `feature/overmind-inapp-updater`, ancora da provare sul telefono.

### Decisione attuale
**FERMARE LA v0.4 DI GAMEPLAY. PRIMA:**
1. Documentazione GitHub permanente.
2. Vercel come endpoint di aggiornamenti e distribuzione delle APK.
3. Pulsante Aggiorna nell'app. Android chiede il consenso alla schermata d'installazione.
4. Firma persistente per ogni build e sicurezza integrità.
5. Prova reale sul Galaxy S21. Se cambio package name o certificato, non è aggiornamento in-place.

### Limite Android / sicurezza
Le release debug di GitHub Actions spesso usano certificati diversi. È irrealistico garantire aggiornamento in-place su APK già installate senza stessa firma; in quel caso servirà un'ultima installazione pulita (preservare prima salvataggi, se necessario). Non inserire credenziali GitHub, password signing o chiavi in APK o repository. Il repo è privato, mentre Vercel e gli APK pubblicati su CDN possono essere accessibili pubblicamente — valutare prima la diffusione.

### Per una nuova chat
Scrivere: «Apri su GitHub `nicogtr20-ctrl/idle-runner-releases`, leggi `OVERMIND_START_HERE.md` e `AGENTS.md`, riprendi OVERMIND dalla sezione HANDOFF e continua la priorità OTA senza modificare Idle Runner.»


### OTA deployment verificato (2026-10-09)
- Vercel: https://overmind-zero-updates.vercel.app/latest.json (manifest pronto, production pubblica; preview protette).
- Base installabile: 0.3.6-ota.8 / code 1008, file: https://overmind-zero-updates.vercel.app/apk/overmind-zero-1008-118f0983c864.apk
- Nuovo update: 0.3.7-ota.10 / code 1010, file: https://overmind-zero-updates.vercel.app/apk/overmind-zero-1010-7f012383aee3.apk
- Firmware Android: build Release con certificato costante e numeri di versione crescenti; pipeline GitHub riuscita.
- Branch Android: feature/overmind-inapp-updater; package com.overmind.zero.ota. Prima installazione separata dalle app storiche.
- Test sul Galaxy S21 PENDENTE: installare 1008, premere AGGIORNA, consentire l'installazione Android, verificare codice 1010 e preservazione salvataggi. Nessuna installazione silenziosa.
- NON procedere con v0.4 gameplay finché il test non è superato.


### Fix verificato — 2026-10-09: pulsante AGGIORNA, errore Permission denied
- Galaxy S21 screenshot su 0.3.6-ota.8: `IMPOSSIBILE AGGIORNARE: Permission denied (missing I...)`. L'app NON ha richiesto permessi perché **la Manifest Android del bootstrap mancava completamente** di `android.permission.INTERNET`, `android.permission.REQUEST_INSTALL_PACKAGES`, provider `UpdateFileProvider`.
- Root cause verificata nel codice del branch `feature/overmind-inapp-updater`; corretto `app/src/main/AndroidManifest.xml`.
- CI ora controlla `aapt dump permissions` per entrambe le autorizzazioni e `aapt dump xmltree` per il provider nel pacchetto Release **già compilato**; test superati.
- **Recovery manuale una volta:** 0.3.7-ota.12 / versionCode 1012, https://overmind-zero-updates.vercel.app/apk/overmind-zero-1012-4e2a741614d0.apk (firma stabile, stesso package `com.overmind.zero.ota`, installare sopra app esistente SENZA disinstallarla per mantenere il salvataggio).
- **OTA test pronto:** 0.3.8-ota.13 / versionCode 1013, https://overmind-zero-updates.vercel.app/apk/overmind-zero-1013-b98857c77d5a.apk. Manifest https://overmind-zero-updates.vercel.app/latest.json → 1013 HTTP 200, APK 200.
- **Da verificare sul telefono:** dopo installazione 1012, premi AGGIORNA, autorizza "Install unknown apps" per OVERMIND se richiesto, torna nell'app, ripremi AGGIORNA e conferma la schermata Android, controlla versione 0.3.8-ota.13 e dati persistenti.
- INTERNET è un permesso normale e non genera una richiesta runtime. L'installazione da origini esterne è invece confermata dal sistema. Gli aggiornamenti silenziosi senza conferma Android NON sono promessi.
- v0.4 gameplay resta fermo fino a test completato.

### 2026-10-09 — OTA confermato sul Galaxy S21
- L'utente conferma esplicitamente: «Ok fatto funziona» dopo installazione correttiva 0.3.7-ota.12 e prova del pulsante **AGGIORNA** alla 0.3.8-ota.13.
- Aggiornamento Android in-app **verificato dall'utente sul dispositivo**, compresa la procedura di installazione. Non è stata eseguita una verifica indipendente del numero di versione né della persistenza dei dati; chiedere screenshot in caso di anomalie.
- L'utente chiede se può eliminare vecchie app: sì, quelle con applicationId distinti (v0.1, ZERO-001 `com.overmind.zero.brain`, ZERO-002 `com.overmind.zero.world`) possono essere disinstallate, **ma la disinstallazione cancella i loro dati di apprendimento**. Conservare `com.overmind.zero.ota` e aggiornarla in-place; i pesi delle app precedenti non sono migrati automaticamente.
- **Passo completato:** ZERO-003/v0.4 pubblicata. Prossimo test da effettuare sul dispositivo, poi sviluppare il controllo del corpo e un modello del mondo più ricchi.

### 2026-10-09 — ZERO-003 / OTA v0.4.0-ota.15 PUBBLICATA
- **ULTIMA BUILD OTA:** https://overmind-zero-updates.vercel.app/latest.json → applicationId `com.overmind.zero.ota`, versionCode `1015`, versionName `0.4.0-ota.15`.
- **APK firmata su Vercel e verificata HTTP 200:** https://overmind-zero-updates.vercel.app/apk/overmind-zero-1015-1adaf54b59b3.apk (40.856 byte). Eseguita CI `OVERMIND stable OTA publisher` run 15: test fisica e IA, Debug, Release firmata, controllo permessi e pubblicazione verde.
- **Codice app operativo:** branch `feature/overmind-inapp-updater`, ultima SHA `97916693c4327073bf3aa27e0205abea286e7316`. Branch di sperimentazione `feature/overmind-v04-physical-learning` (test staging verdi).
- Funzionalità: `ContactLearning.java` risolve collisioni del corpo elastico con dischi mobili, senza impulso preprogrammato su semplice tocco. Nuova microrete con 18 parametri vergini (valore iniziale zero) modifica contrazione/grip vicino all'oggetto. Neuroevoluzione separata dal cervello motorio; entrambi i pesi persistono. Popup CONFRONTA ora misura manipolazione anche su direzioni non usate in training.
- **CI staging misurato:** ZERO-003 70 tentativi, 8 miglioramenti, fitness addestramento manipolazione 22,301 → 101,837; heldout = 50,940 (NON confrontato con heldout baseline nella log stampata). Test senza contatto e test contatto fisico passati. Solo risultati di simulazione, non dimostrazione di ragionamento generale.
- Dati `zero_motorbrain_001`, `world_knowledge_v03` e `zero_energy_v03` preservati; chiavi aggiunte `hand_brain_v04`, `hand_attempts_v04`, `hand_successes_v04`, `physical_pushes_v04`. Nessun cambio del package Android o della firma.
- **DA VALIDARE SUL TELEFONO:** premere `AGGIORNA`, installare v0.4.0-ota.15, verificare che memoria vecchia rimanga e fare screenshot dopo raccolta contatti con **CONFRONTA** aperto. Non dire «provata sul Galaxy S21» finché l'utente non lo conferma.
- **PROSSIMA PRIORITÀ:** mettere alla prova se la deformazione del corpo migliora davvero la spinta in situazioni nuove; poi progettare pseudopodi e manipolazione più ricca senza falsi «pensieri» o azioni programmate.

### 2026-10-09 — ZERO-004 / v0.4.1-ota.16 CORREZIONE
- **Report utente su 0.4.0:** si vedevano movimenti ma non oggetti realmente spostati, episodi resettati e riposizionamenti improvvisi. Punteggio di "apprendimento" apparentemente sceso: il valore Training e il valore su direzioni di confronto erano due test differenti. Nel medesimo run `MotorBrain.Trainer.bestFitness` non può scendere, ma la generalizzazione può peggiorare. Se diminuisce `PUNTEGGIO TRAINING` dopo il riavvio, occorre investigare persistenza / condizioni fisiche.
- **Cause reali trovate:** `GameActivity` chiamava `resetEpisode()` dopo 26 secondi o a distanza >520, teletrasportando organismo e habitat. `physical_pushes_v04` conteggiava impulsi/collisioni (non dischi mossi). Politica preferiva energia nota anche a 99/100.
- **Fix:** ZERO continua nello stesso mondo; ogni ~13s espande la mappa ATTORNO alla posizione attuale senza reimpostare corpo/velocità. Solo morte energetica reale ripristina il corpo. Oggetti mobili hanno riferimento di spawn, traccia verso la posizione corrente e indicatore unità spostate. `DISCHI SPOSTATI` conta solo un disco toccato e spostato fisicamente di almeno 12 unità; `URTI` è un contatore separato. Il vecchio `physical_pushes_v04` NON viene migrato fra i successi. Preferenze energetiche ridotte a energia alta per favorire esperimenti.
- Interfaccia ora distingue `MOVIMENTO • PUNTEGGIO TRAINING` da `VERIFICA SU DIREZIONI NUOVE`.
- **CI:** branch `feature/overmind-v04-visual-proof`, test fisica/neuroevoluzione, regressione reset e spinta fasulla, Android staging OK. Merge fast-forward sul branch firmato `feature/overmind-inapp-updater`, run OTA n.16: JVM tests, APK Debug/Release, verifica permissions, publish tutti verdi.
- **Manifest live:** https://overmind-zero-updates.vercel.app/latest.json indica `0.4.1-ota.16`, versionCode 1016. APK firmata `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1016-030fc487f898.apk`, 42.132 B, verificato HTTP 200.
- **STATO:** pubblicata e pronta via pulsante `AGGIORNA`, ma NON testata ancora sul Galaxy S21. Chiedere screenshot con `DISCHI SPOSTATI`, `URTI`, `DISTANZA MASSIMA` e registrazione senza reset dopo >26s. Continuare le misure prima di introdurre nuove evoluzioni.

### 2026-10-09 — ZERO-005 / v0.5.0-ota.18
- **Scelta imparata:** nuovo `DecisionBrain.java` (MLP 7-10-1, 91 pesi inizializzati casualmente). Input: segnali non etichettati, distanza, energia, numero esperienze. Output: utilità prevista; comportamento epsilon-greedy. SGD su conseguenze osservate di contatti, perdite di energia e movimento fisico. `UnknownWorld.Mind.choose` NON viene più chiamato dalla app, resta solo il vecchio registro storico.
- Memoria nuova: `decision_brain_v05`. Salvati e conservati i vecchi cervelli motori, manipolazione e statistica. UI mostra `SCELTE`, `ESPERIENZE`, `ESPLORAZIONI`, segnale selezionato.
- Codice in branch `feature/overmind-inapp-updater`, esperimento `feature/overmind-v05-learned-decisions`. Test neurali e Android superati. Build Release firmata `0.5.0-ota.18`, versionCode 1018, SHA-256 `5ebc1bc064f8aad562da150f73830ec0b03127e56b4ce82c4a366e886288763a`.
- **Blocco infrastruttura:** Vercel ha esaurito i deployment gratuiti giornalieri (HTTP 402, massimo 100/giorno). GitHub ha già manifest e APK 1018, ma la produzione Vercel mostrava ancora la 1016. Non dire che il pulsante Aggiorna trova la 0.5 finché /latest.json non mostra 1018 in pubblico.
- **Fallback verificato:** GitHub Release privata https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.5.0-ota.18 ; download firmato https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.5.0-ota.18/OVERMIND-ZERO-v0.5.apk (45.160 B). Essendo lo stesso APK con package `com.overmind.zero.ota` e firma originale, installare sopra 0.4.1 SENZA disinstallare.
- **Da verificare su S21:** funziona la scelta appresa su nuovi contatti e non si perde la memoria; raccogli screenshot di scelte/esplorazioni. Quando il limite Vercel si azzera, verificare deploy 1018 e OTA.
- Limite scientifico: questo è apprendimento di utilità contestuale, non ragionamento generale, AGI o coscienza. Il moto riceve ancora l'angolo del target e conosce tre tipi di segnali.

## 2026-10-09 — ZERO-006 / v0.6 cervello evolutivo e pagina abilità
- **Codice OTA pronto:** branch `feature/overmind-inapp-updater`; ramo staging `feature/overmind-v06-evolving-brain-dashboard`. CI staged e signed: JVM, migrazione v1→v2, prova neurone a output nullo (non altera previsioni), compilazione Android, permessi e firma passati.
- `DecisionBrain.java` ora ha architettura variabile: 7 ingressi, 10→24 neuroni nascosti, 1 uscita, inizialmente 91 pesi, massimo 217. Può tentare +1 neurone (+9 pesi) su plateau dell'errore; inserimento iniziale preserva la funzione esistente. Addestra soltanto nuovi pesi su replay, valuta sul 25% escluso, scarta regressioni. Non garantire crescita se l'errore è già basso o il candidato non migliora.
- Persistenza `decision_brain_v05` mantiene lo stesso nome di chiave ma serializzazione `v2` (compatibile in lettura con `v1`). Conserva valori originali e correnti anche dopo crescita, contatori tentativi/successi. Il vecchio motore di movimento da 62 pesi, manipolazione da 18, statistiche e riserva sono mantenuti. Non eseguire downgrade a v0.5 una volta scritto v2.
- Nuova activity `BrainStatsActivity.java` accessibile dal pulsante **CERVELLO** (vicino AGGIORNA). Schede ABILITA, PARAMETRI, EVOLUZIONE; per ogni peso mostra origine/attuale/delta. La pagina legge uno snapshot persistito, mostra veri numeri e non livelli inventati. Torna a ZERO per continuare l'esperimento.
- **APK firmata verificata:** `0.6.0-ota.19` (versionCode 1019) GitHub Release privata: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.6.0-ota.19 ; download: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.6.0-ota.19/OVERMIND-ZERO.apk (53.408 B).
- Workflow OTA ha inoltre creato `0.6.0-ota.20` (versionCode 1020; sha256 `27dec96315ab247ac6af59b036ed787e90346a5d9d8ea34badc4796627ad5964`) nel repository e manifest GitHub. **NON presumere deploy live:** produzione https://overmind-zero-updates.vercel.app/latest.json al controllo mostrava ancora v0.4.1 (1016), per limite piano gratuito. La release privata 1019 è già installabile, stessa firma e package `com.overmind.zero.ota`.
- **Test utente da fare:** installare APK v0.6 sopra l'app attuale SENZA disinstallare; aprire CERVELLO, scegliere PARAMETRI, scorrere e verificare pesi originali/attuali e salvataggi. In EVOLUZIONE il numero può restare 10 (91 pesi) finché nessuna crescita viene accettata. Verificare back → gioco. Non ancora testata manualmente su Galaxy S21.
- Nota workflow: la nuova GitHub Actions di fallback è attivabile manualmente/dalla modifica del suo YAML. I push eseguiti da altre Actions tramite GITHUB_TOKEN non attivano automaticamente altri workflow. Non promettere fallback automatico sulle release future senza passo esplicito nella stessa pipeline.

## 2026-10-09 — ZERO-007 / v0.7.0-ota.22 — corpo plastico completo
- **RICHIESTA UTENTE:** il corpo deve avere più punti liberi e poter allungare localmente la massa per generare pseudopodi tipo braccia **senza braccia o posizioni preimpostate**. Focus sulla capacità corporea, non sulla grafica finta.
- Codice in `feature/overmind-inapp-updater`; branch di studio `feature/overmind-v07-plastic-body`. Nuova classe `app/src/main/java/com/overmind/zero/Morphology.java`, documentazione tecnica `ZERO_007_README.md` nel ramo di studio.
- **Fisica:** le vecchie 10 masse motorie + nucleo NON sono rimpiazzate: conservano i 62 pesi motori già appresi. Sopra c'è una membrana continua con **32 masse elastiche fisiche** e molle fra vicini, coordinate proprie, deformazioni tangenziali e radiali, bilancio limitato di materiale e lunghezza. Nessun insieme di slot denominati braccia/gambe.
- **Apprendimento:** `Morphology.Trainer` ottimizza **74 nuovi pesi** di una rete condivisa localmente dalle 32 masse; input prossimità/direzione/velocità/stretch/tempo e 2 uscite contrazione radiale e deviazione laterale. Uscite inizialmente zero; addestramento con vere simulazioni fisiche e fitness basato su approccio/contatto/spinta/costo. Nuove chiavi persistenti `morph_brain_v07`, `morph_attempts_v07`, `morph_accepted_v07`. Il nuovo organismo apprende e NON viene eseguita una sequenza prefissata per creare tentacoli.
- **Interazioni reali:** render del contorno basato sulle coordinate `surface.x/y`, collisioni del disco contro i segmenti della superficie. Anche il contatto con stimoli energetici può avvenire mediante la membrana.
- **UI:** `GameActivity` versione esperimento 006, HUD `MEMBRANA 32 PUNTI / ALLUNGAMENTO / TEST`. `BrainStatsActivity` aggiunge abilità MEMBRANA PLASTICA e quarta tab PARAMETRI/MEMBRANA con valori iniziali/attuali dei 74 pesi. Totale reti ora 245+ (62 moto + 18 manipolazione + 74 membrana + minimo 91 decisioni).
- **TEST REALI DI CI:** `ZERO-007 passed: motorPoints=10 shellPoints=32 morphWeights=74 extendedRadius=38.72 contacts=13.02 fitness=23.471 -> 48.139 accepted=9` in 16 prove simulatori. JVM anche verifica che la membrana vergine non genera arti prefabbricati, che l'estensione sia possibile, assenza di impulsi senza contatto, memorie e compatibilità, build Android Debug/Release firmata e manifest.
- **APK FIRMATA LIVE:** `0.7.0-ota.22`, versionCode `1022`, SHA-256 `74610481e4504e9b230ff5c8db3ee1a196f2336c720c99fe4d2eb2e7cb9a8eac`, 57.904 byte. Vercel manifest https://overmind-zero-updates.vercel.app/latest.json risponde HTTP 200 con 0.7.0-ota.22; APK https://overmind-zero-updates.vercel.app/apk/overmind-zero-1022-74610481e450.apk verificata HTTP 200.
- **TEST ANDROID PENDENTE:** su Galaxy S21 aprire OVERMIND e premere AGGIORNA; installare in-place, non disinstallare. Avviare addestramento e osservare corpo e HUD allungamento, schema delle 32 masse e CERVELLO/MEMBRANA. Fotografare o filmare dopo ~30-100 tentativi di nuova morfologia. Non dichiarare braccia apprese senza prova su dispositivo: la rete può fallire o creare solo piccoli rigonfiamenti.
- Il cervello di scelta riceve ancora la direzione geometrica del bersaglio dal mondo. NON AGI, non coscienza, non intenzionalità astratta.

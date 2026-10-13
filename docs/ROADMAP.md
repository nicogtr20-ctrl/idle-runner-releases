# Roadmap e criteri di accettazione

## Priorità immediata — updater e documentazione
- [x] Whitepaper, architettura, storico e handoff su GitHub.
- [x] Vercel: endpoint HTTPS stabile `/latest.json` (risponde HTTP 200, manifest disabilitato finché manca signing).
- [x] APK pubbliche firmate via Vercel, versioni 1008 e 1010.
- [x] Firma Android permanente configurata nel workflow GitHub Actions.
- [x] Pulsante **Aggiorna**, download HTTPS e verifica SHA-256 implementati nel branch `feature/overmind-inapp-updater` (build CI riuscita).
- [ ] Verifica effettiva del pulsante su dispositivo e installazione firmata end-to-end.
- [x] Build firmate con applicationId invariato e versionCode crescente (1008 → 1010).
- [x] L'utente conferma aggiornamento in-app funzionante su Galaxy S21 (0.3.7 → 0.3.8).
- [ ] Test separato della persistenza dei dati e regressione dopo ulteriori versioni.
- [x] Manifest su Vercel aggiornato dopo CI firmata con SHA-256.
- [ ] Pipeline aggiorna questo CHANGELOG/HANDOFF ad ogni release.

## ZERO-003 — comportamento fisico reale
- [x] Rimuovere la spinta oggetti "magica" al semplice contatto (0.4.0 CI).
- [x] Collisioni simulate tra segmento della membrana, nucleo e oggetti (0.4.0 CI).
- [x] Controller di 18 pesi vergini che apprende a modulare contrazioni e grip vicino agli oggetti (simulazione e CI).
- [x] Test JVM fisica (contatto/no-contatto) e confronto di manipolazione su direzioni diverse (CI).
- [x] Test 0.4.0 ricevuto: urti contabilizzati, ma reale spostamento non visibile e reset dell'episodio ogni 26s riscontrato da codice.
- [x] Fix 0.4.1 pubblicato con tracce visibili, contatori separati e mondo continuo; test CI superati.
- [ ] Verificare sul Galaxy S21 0.4.1 con screenshot video e contatore DISCHI SPOSTATI dopo 10+ minuti.

## ZERO-004 — estensioni del corpo
- [ ] Pseudopodi dinamici legati alla fisica e ai parametri apprenderibili.
- [ ] Vincoli di materia, energia di deformazione e controllo coesione.
- [ ] Verificare efficacia su spinta, trazione e varchi.

## ZERO-005 — modello del mondo e simulazione persistente
- [ ] Previsioni sensoriali e confronto errore predittivo, curiosità intrinseca.
- [ ] Memoria causale e di posizione più ricca.
- [ ] Energia, rischio, crescita e espansione con regolazione dell'autonomia.
- [ ] XP del giocatore non coincidenti con memoria apprenditiva; interventi indiretti.

## ZERO-006 — UX e gioco
- [ ] Grafica tipo ferrofluido nero credibile con animazioni organiche.
- [ ] Mappa interessante e osservatore invisibile; zero comandi alla creatura.
- [ ] Gameplay bilanciato, eventi emergenti, salvataggi robusti.

## Definizione di "fatto"
**Build CI + test JVM + prova APK su S21 + misure + changelog + nuovo file HANDOFF.** Distinguere sempre "realizzato", "validato CI" e "validato sul dispositivo".

## ZERO-005 — Apprendimento delle scelte (v0.5)
- [x] Rete neurale vergine per valutare quale segnale esplorare (91 pesi, senza preferenze pre-addestrate)
- [x] Rimossa la vecchia selezione con formula fissa dal loop di gioco
- [x] Apprendimento online su conseguenze osservate e memoria persistente
- [x] Test CI e APK Release firmata su GitHub
- [ ] Test reale dell'aggiornamento 0.5 sul Galaxy S21
- [ ] Verificare che Vercel possa pubblicare nuovamente dopo esaurimento del limite giornaliero
- [ ] Navigazione autonoma senza direzione geometrica suggerita

## ZERO-006 — Pagina abilità e crescita del cervello
- [x] V0.6: pesi del cervello decisionale da 91, estensibili fino a 217, +9 alla volta dopo verifica errore e validazione separata
- [x] Migrazione della memoria v0.5 senza cancellare i pesi già appresi
- [x] Pagina CERVELLO con ABILITA, PARAMETRI (partenza/attuale/delta) ed EVOLUZIONE
- [x] Test JVM, verifica UI, firma APK Android (run 19 e 20)
- [x] Release APK 0.6.0-ota.19 nel repository privato GitHub
- [ ] Verifica su Galaxy S21 della pagina e dell'apprendimento dopo aggiornamento senza disinstallare
- [ ] Esperimento che verifichi sul dispositivo se e quando viene davvero accettato un neurone aggiuntivo (nessuna crescita garantita)
- [ ] Vercel: ripristinare manifest pubblico/OTA quando limite gratuito si libera

## ZERO-007 — apprendimento del corpo deformabile (v0.7)
- [x] Passare da 10 soli punti motori a 32 punti addizionali sulla membrana senza perdere i pesi appresi.
- [x] Nuova rete condivisa per estensione radiale e flessione laterale locale; nessun arto preimpostato.
- [x] Membrana elastica fisica, interazioni con collisioni reali e controllo dei limiti di allungamento.
- [x] Controllo e salvataggio di 74 nuovi parametri in CERVELLO/ABILITA e CERVELLO/PARAMETRI/MEMBRANA.
- [x] Test su JVM e Android, release firmata e pubblicata via Vercel 0.7.0-ota.22.
- [ ] Test manuale su Galaxy S21: si vedono pseudopodi locali utili dopo l'addestramento? Le interazioni cambiano?
- [ ] Eventuale successiva evoluzione: controllare l'acquisizione di appendici che si piegano e possono afferrare un disco su direzioni nuove, non soltanto allungarsi.

## ZERO-008 — Scarsità, presa bilaterale e ambiente vincolato
- [x] Membrana raffinata a 96 punti senza perdere i 74 parametri salvati.
- [x] Energia raccolta sparisce; nuova energia diventa rara in scarsità.
- [x] Stanza visibile con muri fisici, porta scorrevole e nutrimento dietro.
- [x] Porta può muoversi SOLO grazie a contatti reali simultanei su due lati differenti della membrana, con velocità tangenziale misurata.
- [x] Addestramento separato su stanza con controllo di holdout.
- [x] Contatori verificabili di prese, scorrimento, stanze risolte, nutrimento consumato.
- [x] JVM smoke tests e staging Android.
- [ ] Test manuale su Galaxy S21 (almeno 5 tentativi di stanza): verificare l'effettiva presa, assenza di teletrasporto, accesso fisico e performance FPS.
- [ ] Migliorare successivamente percezione ostacoli e pianificazione senza fornire posizione perfetta del nutrimento.

## ZERO-008.1 — Rigidità fisica della porta
- [x] Conservare esplicitamente il respawn per esaurimento energetico (voluto dall'utente)
- [x] Verificare collisione continua di tutti i 96 segmenti, non soltanto i singoli nodi
- [x] Bloccare attraversamenti rapidi tra fotogrammi e correggere bordo grafico durante stanza
- [x] Test di collisione con segmento che taglia il blocco e test di percorso libero attorno al bordo; CI Android verde
- [x] APK 0.8.1-ota.24 firmata e verificata pubblicamente su Vercel
- [ ] Osservazione sul Galaxy S21: vedere se ZERO avvolge l'ostacolo e forma due contatti fisici indipendenti

## ZERO-008.3 — Contatto NON significa presa
- [x] Inserita terza uscita neurale nei 96 punti della membrana (74→82 pesi, compatibile con vecchi salvataggi).
- [x] Sensore tattile separato, comando di adesione autonomo, azione inizialmente spenta.
- [x] Porta trascinabile solo quando due regioni indipendenti mantengono la presa neurale attiva.
- [x] Test: due contatti opposti senza comando non aprono; una presa rilasciata blocca la trazione; due comandi attivi possono aprire.
- [x] Pesi del cervello in pagina abilità, contatori tocchi/adesioni e feedback dorato dei punti.
- [x] CI e build firmata 0.8.3-ota.27; APK GitHub Release verificata.
- [ ] Test sul Galaxy S21 dell'apprendimento reale della presa e salvataggio di tutti i parametri.
- [ ] Ripristinare la versione live Vercel 1027 e il pulsante AGGIORNA se il server è rimasto arretrato.
- [ ] Generalizzare l'adesione appresa ad altri oggetti, non solo al cancello della camera.

## ZERO-009 — Apprendimento dalle conseguenze proprie
- [x] Rimuovere dalla navigazione attiva il heading calcolato verso il bersaglio (`atan2` del target).
- [x] Sensori fisici propri e del mondo, 26 canali, con raggi ostacoli e visione occlusa da muri.
- [x] Politica che sceglie fra 16 azioni motorie e adesive, esplora e impara in vita.
- [x] Modello predittivo online 11.664 parametri (transizioni + valori azione), salvataggio persistente.
- [x] Mantenere stati motori appresi, membrana 96 punti, morte per fame/respawn.
- [x] CI di convergenza predittiva/persistenza/occlusione e Android firmato v0.9.0-ota.29.
- [ ] Pubblicazione live aggiornata Vercel (manifest risultava 0.8.1).
- [ ] Test vero Galaxy S21: le azioni apprese avvicinano alla risorsa? La strategia del cancello migliora?
- [ ] Misurare apprendimento causale con interventi controllati e transizioni su configurazioni mai viste, non soltanto errori ridotti in training.

## ZERO-009.1 — Statistiche nuove e azzeramenti espliciti
- [x] Rimuovere FERMA/ADDESTRA e rendere sempre attivo l'apprendimento online mentre il mondo simula
- [x] Pagina ANALISI con grafico errore predittivo heldout (campioni NON allenati) versus baseline persistente
- [x] Mostrare confronto esplorazioni vs policy con avviso di NON controllabilità della situazione
- [x] Reset indipendente per 7 sottosistemi e reset totale con doppia conferma, via richiesta consumata a foreground
- [x] Eliminare reset nascosto che poteva cancellare tutta la memoria senza avviso
- [x] Android Debug + signed release 0.9.1-ota.32, fallback GitHub verificato per hash
- [ ] Test manuale su Galaxy S21 (pulsante ANALISI, grafico dopo 25 nuove esperienze, prova reset selettivo su richiesta)
- [ ] A/B di policy contro casuale a parità di stato: ancora NON implementato
- [ ] Ripristinare manifest pubblico Vercel alla v0.9.1 quando deployment libero

## ZERO-009.2 — apprendimento nel mondo di gioco
- [x] Riattivato l'apprendimento di motore, manipolazione e membrana/presa senza usare vecchi trainer con target noto; mutazioni selezionate da esperienze vissute con controllo approssimativo delle condizioni.
- [x] Memorie precedenti intatte e rollback dei tentativi sfavorevoli; morte mantiene reset fisico senza cancellare reti.
- [x] Vite più lunghe: fame meno rapida, pericoli rossi attenuati, respawn con energia maggiore.
- [x] Escludere stimoli ordinari dalla stanza e introdurre unica risorsa speciale +80 con impronta visiva distinta, senza anticipare il valore alla rete.
- [x] Staging e APK Android firmata 0.9.2-ota.34 via GitHub Actions.
- [ ] Galaxy S21: verificare se gli attuatori migliorano davvero senza rollout invisibili e se ZERO raggiunge la ricompensa, registrando prove spontanee.
- [ ] Eseguire comparazioni controllate su stato identico e direzioni/stimoli nuovi per distinguere vera competenza da rumore ambientale.
- [ ] Verificare la distribuzione OTA Vercel di 1034, o usare GitHub Release privata fino a ripristino.

## ZERO-009.4 — locomozione utile ed esplorazione misurabile
- [x] Fare tracking di spostamento netto del nucleo distinto da distanza totale per vita e record.
- [x] Introdurre celle nuove visitate una sola volta per vita e indicatore di efficienza basato su energia effettivamente spesa.
- [x] Nuova funzione di selezione dei controllori: cammino circolare senza nuove conseguenze NON costituisce progresso; rotazione utile per risorsa/contatto resta possibile.
- [x] Numeri accettate/scartate separati per motore/manipolazione/membrana e reset realmente selettivi; all'upgrade vecchi pesi conservati.
- [x] PROVE mostra gli ultimi test realmente vissuti anziché risultati del vecchio benchmark con destinazione predefinita.
- [x] JVM e Android CI staging + firma stable 0.9.4-ota.36 riuscita.
- [ ] Testare sul Galaxy S21 sia locomozione utile sia rischio di accettazione falsa in condizioni diverse; implementare A/B rigoroso su scene iniziali identiche senza addestramento nascosto.
- [ ] Validare che l'apprendimento non provochi regressione di presa, manipolazione e stanza nei tentativi prolungati.


## ZERO-009.5 — memoria di strategie realmente vissute (10 ottobre 2026)
- [x] Definire criterio di evento basato su conseguenza fisica **generica** (movimento oggetto, porta, energia), mai su una procedura prescelta.
- [x] Memorizzare ultimo episodio: 26 sensi, azioni selezionate, pesi motori, manipolazione e membrana realmente attivi; max 18 ricordi di 9 azioni.
- [x] Recuperare eventi tramite sensi simili, offrire preferenza debole non obbligatoria, riusare per poco i pesi registrati, cooldown se nessun risultato.
- [x] Persistenza sopravvivente alla morte e agli update, nuova sezione ANALISI e reset specifico senza cancellare altre reti.
- [x] Rinforzo fisico generico e isolamento trial di mutazione dal replay per evitare falso positivo.
- [ ] Su Galaxy S21: verificare ripetizione della tecnica fisica dopo morte, trasferimento a porta ricreata, attraversamento e raccolta dell'energia rara.
- [ ] A/B rigoroso con medesima fisica e seed: base vs cervello precedente vs recall, conteggio esiti fisici e percentuale tentativi con recupero riuscito.
- [ ] Sviluppare sensori per movimento relativo e storia temporale, non etichette semantiche “questa è una porta”.
- [ ] Solo dopo le prove: planning controfattuale multistep sul modello del mondo, senza fondere simulazioni immaginate con dati reali.
Documentazione dettagliata e limiti scientifici: `docs/EXPERIMENT_0095.md`.

## Misurazione robusta — 0.10.5
- [x] Sostituire “ultima vita vs ultime 10” con capacità attuale su **25 vite**.
- [x] Trend stabile su **25 recenti vs 25 precedenti**, attivo solo a 50 campioni completi.
- [x] Storico permanente con medie e record oltre il limite grafico di 50 vite.
- [x] Migrazione compatibile dal formato statistico 0.10.4 senza cancellare cervelli o slot.
- [x] Mantenere l'intero sistema observer-only, invisibile alla policy.
- [x] Build firmata e OTA pubblica 0.10.5-ota.54 verificata (publisher 38060026469; manifest/APK HTTP 200).
- [ ] Verifica manuale Galaxy S21 dopo 50 vite: controllare che trend, medie storiche e grafici descrivano correttamente una sessione lunga e che x5/x20 usi esclusivamente tempo simulato.

## ZERO-010.6 — memoria di lavoro temporale
- [x] Stato ricorrente capace di distinguere due storie recenti con identica percezione presente.
- [x] Predizione contestuale allenata solo su esperienze vissute e valutata su transizioni held-out.
- [x] Influenza sulle scelte bloccata finché il modello temporale non batte il modello one-step.
- [x] Valore contestuale e credito ritardato di effetti fisici/energetici sulle ultime 8 decisioni.
- [x] Persistenza dei pesi e del contesto nella stessa vita; cancellazione del contesto transitorio alla morte.
- [x] Reset integrato nel dominio MODELLO DEL MONDO e salvataggi con nome compatibili.
- [x] STATISTICHE 0.10.5 lasciate inalterate.
- [x] Build firmata e OTA pubblica 0.10.6-ota.55 verificata (publisher 38064599838; manifest/APK HTTP 200).
- [ ] Verifica Galaxy S21: dopo abbastanza esperienze, controllare in CERVELLO che la confidenza temporale esca da 0% soltanto se l'errore held-out migliora.
- [ ] Verificare sul mondo reale simulato se ZERO mantiene più a lungo sequenze con effetto fisico continuativo (es. spostamento progressivo) senza regola specifica.
- [ ] Solo dopo questa prova: valutare “skill/chunk” di sequenze apprese e successiva integrazione del contesto nel planner multistep.

## ZERO-010.7 — skill temporali autonome
- [x] Estrarre candidati di 2–8 azioni esclusivamente da sequenze realmente vissute prima di effetti positivi.
- [x] Richiedere almeno due ricorrenze compatibili prima dell'influenza sulla policy.
- [x] Replay come bias, non comando obbligatorio.
- [x] Interrompere la skill quando il contesto temporale diverge.
- [x] Confermare replay solo con una nuova conseguenza reale; timeout/danno riducono affidabilità.
- [x] Persistenza, slot nominati e reset indipendente ABILITA TEMPORALI.
- [x] CERVELLO/ANALISI mostrano candidati, consolidate, lunghezza, replay riusciti/falliti.
- [x] STATISTICHE 0.10.5 e memoria temporale 0.10.6 lasciate inalterate.
- [x] Build firmata e OTA pubblica 0.10.7-ota.56 verificata (publisher 38066032322; manifest/APK HTTP 200).
- [ ] Test Galaxy S21: lasciare vivere ZERO abbastanza da ottenere confidenza temporale >0 e osservare se compaiono skill consolidate.
- [ ] Verificare se una skill viene richiamata in un contesto simile e mantiene un effetto fisico progressivo senza essere stata programmata per quell'oggetto.
- [ ] Passo successivo solo dopo verifica: permettere al planner di valutare skill consolidate come macro-azioni.

## 0.10.8 — Memorie temporali persistenti, verificare su telefono
- [x] Verifica razionale 0.10.5–0.10.7: funzionalità utili/coerenti, senza assumere progresso cognitivo dimostrato.
- [x] Fix delle due chiavi mancanti in GameActivity.save(): temporal_working_memory_v106 e temporal_skills_v107; storico, vecchi pesi e slot invariati.
- [x] Regression test completo SaveProfileCodec di due ZERO indipendenti, modello temporale, hidden e skill; CI staging 38067546249/38067671221 SUCCESS.
- [x] Release firmata 38067752564 SUCCESS, 0.10.8-ota.57 (1057), APK 150816 byte, Vercel manifest/APK HTTP200.
- [ ] Samsung S21: aggiornamento in-place, verificare contatori e capacità salvate dopo riavvio e cambio slot.
- [ ] Prova con scenari controllati: confrontare energia, danno/minuto, attraversamento porta e ripetizione di skill davvero vantaggiose; non basta uno spostamento del cancello.
Riferimento docs/EXPERIMENT_0108.md.

## ZERO 0.10.9 — propriocezione fisica non semantica
- [x] 96 punti di membrana fisica → 8 settori x 4 sensi locali: contatto, presa, estensione, slittamento radiale; rilevare pressioni reali da disco senza imporre grip.
- [x] Nuova rete da 3088 pesi con previsione tattile e valore d'azione; gate predittivo >=30 holdout, errore <=0.96 baseline, bias max ±0.23; nessun hardcoding del cancello.
- [x] Non cambiare i 26 sensi preesistenti, i pesi, la memoria em3/tw1/ts1, le statistiche ls2, i 12 slot o la firma; salvare/restaurare `body_proprioception_v109` con reset autonomo.
- [x] Mostrare 8 settori e diagnostica in ANALISI, numero parametri CERVELLO; campionamento solo per decisione per evitare sovraccarico x20.
- [x] QA staging 38069416680 SUCCESS, signed publisher 38069513724 SUCCESS, 0.10.9-ota.58 v1058, SHA 9bad1270ef3dae172858d55a1f90e3d92cc44c89e1559c9b18ce508f94b933e5, 155816 byte.
- [x] Recuperare Vercel stantio con Git deployment dpl_CHFimoyKSiLUR7mAn587GexvZypS READY: manifest e APK produzione HTTP200.
- [ ] Prova reale Galaxy S21: tocchi localizzati, porta/dischi, contatori e apprendimento, pausa/x20, riavvio senza cancellare pesi, slot indipendenti.
- [ ] Confronto controllato delle 25 vs 25 vite, vera apertura+attraversamento porta e risorse, non solo contatore mosso.
Documento dettagliato docs/EXPERIMENT_0109.md.

## OVERMIND ZERO 0.11.0 — percezione dello spazio non programmata
- [x] Aggiungere 16 raggi a visibilità fisica e occlusione, costruire da soli una mappa locale di massimo 1024 celle (libero/occupato/sconosciuto) tramite odometria del corpo reale.
- [x] Non svelare oggetti dietro muri o posizione del cibo/porta; conservare 26 vecchi sensi, propriocezione del corpo, memoria temporale/episodica, 12 slot e firma Android.
- [x] Predittore e valore d'azione appresi separatamente, scelta invariata fino a >=30 validazioni fuori addestramento e >=4% migliore di baseline; bias max ±0.19.
- [x] Mappa locale azzerata solo a vita effettivamente nuova, pesi appresi conservati e serializzati nella chiave `spatial_perception_v110` per ogni ZERO; reset autonomo e pagina ANALISI con mini-mappa e metriche.
- [x] Correggere conflitto fra letture libere e colpite dello stesso scan e variabile omonima in UI; test JVM + compilazione debug `38071087243` SUCCESS.
- [x] Signed official pipeline `38071321701` SUCCESS, release `0.11.0-ota.60` code1060, 162004 byte, SHA `076d315c484cd11d695a40219a26a4a30508de1478a3a31b5abee5a488fee84e`; Vercel manifest/APK HTTP200 verificati.
- [ ] Test S21 AGGIORNA e controlli: celle conosciute/sconosciute, occlusione, segnali dal movimento, riavvio e slot differenti, x1/x20 e pausa.
- [ ] Misurare miglioramenti sulle vite in condizioni comparabili: energia, danni/minuto, esplorazione netta, spostamento cancello e vero attraversamento stanza. Predizioni accurate non equivalgono a capacità dimostrate.
Dettagli `docs/EXPERIMENT_0110.md`.

## 0.11.1 — concrete causal experimentation, not gate scripts
- [x] Distinguish observed body travel from change at the SAME previously observed spatial map coordinates after a real action.
- [x] Learn generic 16 action × 3 sensor-context experimental consequences from lived reward and real energy; require randomized same-context alternatives and heldout predictive improvement before bounded action bias.
- [x] Provisional observed blocked-to-free cells are NOT passability success; verify actual subsequent physical traversal separately.
- [x] Save `causal_experiments_v111` for each ZERO using the existing 12 named slots; unchanged old S=26 brain, spatial/body/temporal episodic memories, motor and simulated timing.
- [x] Separate CAUSAL reset, ANALISI diagnostics, CERVELLO statistical parameter count.
- [x] Staging `38073279447` and `38073332255` SUCCESS (synthetic interventions + old regressions + Android debug); stable signed `38073421560` SUCCESS.
- [x] Production APK `0.11.1-ota.61` 1061, 169280 B, SHA256 8d8f121d985735ca3d7cac507007fb85a1ee192603309c2e867dc39e638e53c3. Vercel manifest and APK HTTP200.
- [ ] Physical Galaxy S21: AGGIORNA in place, inspect ANALISI causality counters, compare actual traversals with mere gate motion, verify persistence across restarts and independent named ZERO slots.
- [ ] Quantify 25 vs 25 comparable lives with genuine energy uptake and room crossings, not solely opening candidates or higher prediction confidence.
Full scientific/implementation report `docs/EXPERIMENT_0111.md`.
## 0.11.2 — movement learned as instrument to get actual energy
- [x] Root-cause analysis: old 0.8 object/heading geometric scripted target removed 0.9; temporary 0.9.8 visual steering removed; live 0.11.1 Q-reward still incentivized forward locomotion itself and historic chooser disconnected.
- [x] Introduce independent `InstrumentalNavigation` with 3×8 actually visible spectral observations, energy valence only from actual received/lost energy and measured actual per-action displacement. Gated after 3+ trained/2+ heldout real motor moves beating zero-change reference; no hidden target coordinate, no hardcoded blue/red.
- [x] Main SensorimotorMind TD reward now includes real energy obtained/lost, NOT old `.24*realForward`; small curiosity remains for unknown stimuli; old Q dampened as bounded prior without wiping learned history.
- [x] Correct red cooldown visible in raw senses, allow disc occlusion in open world, preserve old 26-input weights and temporal/motor/episodic models.
- [x] Separate `instrumental_navigation_v112` `in1` saves/loads across 12 profiles, isolated reset NAVIGATION, ANAlISI valence/movement indicators and CERVELLO total count.
- [x] JVM behavioral tests validate genuine SensorimotorMind.choose() preference for beneficial E vs W and harmful N vs S after experienced energy and actual motor movements, plus older regressions and debug Android: staging `38078898196` SUCCESS.
- [x] Signed release pipeline `38078981840` SUCCESS, code1062 0.11.2-ota.62, APK 174824 B SHA256 bbec605688215fea99c450aeb91314e437f197456f6b104b15c2e3388cc07f38. Vercel stale 404 fixed by production manual deployment `dpl_7ukm6M55KeVaxPq9naHZUpSTeUkT`, public JSON/APK HTTP200.
- [ ] On Galaxy S21 confirm AGGIORNA, backup and restore two named slots, real behavior after beneficial and harmful touches, travel-to-resource measured after learned values, x1/x20.
- [ ] Gate remains a separate general planning challenge: resource hidden behind physical wall is invisible; do not substitute hardcoded gate target/path. Add empirical evaluation before further modules.
Source/report `docs/EXPERIMENT_0112.md`.

## 0.11.3 — verifica fisica prima di aggiungere nuove intenzioni
- [x] Laboratorio A/B ripetibile isolato: energia, danno, cancello; ognuno 3 posizioni/stati iniziali × controllo/attivo = **18 prove di 32 secondi SIMULATI**, condizioni appaiate identiche e stesse reti salvate.
- [x] Misurare SOLO risultati veri con componenti Body.step, Surface.step, ScarcityLab.constrain, entity.touch e rete SensorimotorMind reale: contatti, guadagni/danni energetici, distanza, cancello effettivamente spostato e attraversamento attraverso porta aperta.
- [x] Navigazione disattivabile **esclusivamente** in una copia di benchmark tramite `setNavigationAdviceEnabled(false)`; nel gioco live rimane attiva di default. Memoria episodica, spatial/temporal e policy generale presenti in entrambe le condizioni.
- [x] Android ANALISI → LABORATORIO COMPORTAMENTALE, avvio in background con progresso e arresto; leggere il profilo attuale, eseguire copie in memoria, salvare il report **solo** in `zero_behavior_lab_v113` e mai in `zero_motorbrain_001` / vecchi 12 slot.
- [x] Staging `38086989808 SUCCESS` (regressioni JVM + Android debug + prova 18 scene); publisher signed `38087100529 SUCCESS`. Pubblicata `0.11.3-ota.63` code1063, APK184108 B SHA256 35d2ced944ac1125967b46ae67588a305b136984f1232edd59f14a9632b30d95, live manifest/APK HTTP200 verificati.
- [ ] Utente Galaxy S21: installazione in place e completamento benchmark, raccolta screenshot/report dalle tre sezioni + scelte nav.
- [ ] Dopo esito dei test, correggere i limiti effettivamente osservati e solo **poi** aggiungere continuità di intenzioni apprese, non script a bersagli.
Limite dichiarato: non comprende le mutazioni fisiche runtime OnlineBodyLearning o il mondo dinamico complesso della partita, e tre coppie non dimostrano causalità statistica. Dettagli `docs/EXPERIMENT_0113.md`.

## 0.11.4 — earned intentions + gate blockage diagnosis
- [x] Use provided v0.11.3 real A/B report: energy nav improves 1/3, danger 2/3, gate 0/3; identify that prior "min distance" for gate meant distance to hidden energy, NOT actual approach to gate.
- [x] `GoalContinuity`: transient motivation only after real observed positive sensory progress to signal with energy valence already learned + measured body displacement; bounded decision advice and automatic abandonment on regressions. No gate/color script, no motor reward.
- [x] `GateBehaviorMetrics`: physical gate distance (not hidden energy), approach threshold 45 U excluding spawn, membrane/core touch, grip command, adhesion, actual hold, bilateral grip, physical door translation/opening/crossing. Observer-only, fed to ANALISI and A/B reports without changing policy.
- [x] Persist separate `goal_continuity_v114` and `gate_behavior_v114` statistics in original 12 slots and resets; no old brain erased.
- [x] Staging `38089466225` SUCCESS all old/new JVM tests, 18 A/B and Android debug; signed GitHub `38089553377` SUCCESS version1064 APK190132B SHA256 `255727583121d49c07463a29aea07d4f99db06912cdc2f6aeff40cdd103cdab8`.
- [ ] **BLOCKER** Vercel production deployment quota 402 >100/day. The Vercel updater still offers 0.11.3, v0.11.4 APK route 404. Re-deploy latest main when quota restored; verify BOTH endpoints HTTP200 before informing user.
- [ ] Galaxy S21 live test: install GitHub signed APK over existing, backup named slot, inspect live gate diagnostic (is approach/contact possible?), run 18 A/B and share screenshot/report. Correlate tentative intentions with actual energy collected, harm avoided, and room crossing. No claims of solved gate yet.
See `docs/EXPERIMENT_0114.md`.

## 0.11.5 — priorità sicurezza appresa e scelta dopo contatto energetico negativo (10 ottobre 2026)
- [x] Controllata catena danno reale → `SensorimotorMind.contactOutcome` → valenza strumentale sperimentata → consiglio motorio → successiva decisione.
- [x] Nuova `AdaptiveSafety`: nessuna fuga prescritta, solo 4.5s di maggiore peso ai 16 movimenti REALMENTE verificati che riducono esposizione a frequenze già sperimentate come dannose; bonus bounded ±0.95, nessun premio per camminare.
- [x] Accelerare la prossima decisione a >=0.18s simulati dal danno fisico, senza corrompere la raccolta dei modelli motori che richiedono campioni 0.65s; post-danno epsilon sceglie alternative con punteggio totale entro 0.75 dal massimo, lasciando vera esplorazione.
- [x] Salvare solo contatori `adaptive_safety_v115` nel profilo e 12 slot, lasciare vecchi pesi/cervelli. UI **ANALISI → RISPOSTA APPRESA AL DANNO**, reset isolato SAFETY.
- [x] Lab 5 scenari ×3 varianti ×2 ablations = **30 prove fisiche**: energia, una fonte nociva, cancello, tre nocivi circostanti, risorsa+pericolo; rileva secondo danno, uscite zona rischio, micro-movimenti, energia recuperata e contatti.
- [x] Staging `38092034409 SUCCESS`, publisher signed `38092242587 SUCCESS` dopo precedente run65 fermato da assert testuale vecchia; release `0.11.5-ota.66`, code1066 APK194712 byte SHA e393e44bdccd68bd6ecb5e9020227a5906760b7ea44e3e6e055e39a16040ec1c, LIVE Vercel manifest+APK HTTP200.
- [ ] Galaxy S21: aggiornare in-place nuovo ZERO promettente, confrontare episodi ripetuti e report 30 prove sullo stesso cervello. NON dichiarare fuga, sopravvivenza migliorata o scelta robusta finché non osservati risultati ripetibili.
Dettagli `docs/EXPERIMENT_0115.md`.


## ZERO 2.2 attivo — OTA118 verificata, 13 ottobre 2026
Versione APK `0.11.6-ota.118`, codice **1118**, package `com.overmind.zero.ota`. Fonte rilasciata `685031f7b8856f8eb351401b99e22b0de9604330`, PR #41; branch ufficiale `feature/overmind-inapp-updater`. Nucleo ricorrente prima della scelta reale, corpo/raggi/energia nella previsione, replay persistente con holdout separati, recupero temporaneo di tecniche consolidate con interruzione e isolamento delle finestre corporee, campionamento sensoriale ai confini della decisione per TEST MAX. È un reservoir fisso con readout appresi (40.560 pesi), non Dreamer completo o superintelligenza dimostrata.

Verifiche: **70 test JVM locali PASS**, controlli del publisher PASS, [Android staging 38467622938 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467622938), [publisher firmato 38467965975 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467965975). APK pubblica HTTP200, **382.200 byte**, SHA-256 `c12528432b0e59432a390847deb3d1482d1ea4760c59d1b19049abea11d61f4c`; firma V2 verificata crittograficamente e fingerprint del certificato identico alla OTA116: `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`. Manifest pubblico verificato a 1118. [APK immutabile](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1118-c12528432b0e.apk). [Release GitHub](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.118).

Installare sopra l'app attuale tramite AGGIORNA o APK; non disinstallare e non azzerare le memorie. Nuovo tasto **CONFRONTO CERVELLO 2.2** in CERVELLO/ANALISI: 24 prove fisiche isolate (12 coppie) sul profilo salvato; stesso seed/scena, controllo precedente contro core attivo+recupero. Non modifica il personaggio; report separato `zero_core_lab_v22`, upload solo esplicito tramite CONDIVIDI DATI, conservato nei multipart. L'arena ha controllori fissi, senza tempeste/streaming: screening, non equivalenza integrale al mondo.

**Ancora da verificare sul Galaxy S21:** installazione in-place e continuità dei 12 slot, FPS/termica e nuovo moltiplicatore TEST MAX rispetto al x29 riportato, risultati A/B, retention e adattamento a scene nuove. Nessun uplift comportamentale è già dimostrato. [Paper completo e checklist](ZERO22_ACTIVE_CORE_AND_DEVICE_VALIDATION.md). Il publisher 117 sul diverso branch `feature/zero22-active-recurrent-ota117` non è questa release: ultimo rilascio verificato qui è OTA118.


## ULTIMO STATO VERIFICATO — ZERO 2.2 / OTA119 — 13 ottobre 2026, 23:20 CEST

Richiesta utente: APK pronta con centro decisionale rivisto attivo, conservazione del personaggio e ottimizzazione TEST MAX (misura precedente S21 circa x29). **Rilascio realmente concluso**: versione `0.11.6-ota.119`, codice1119, package `com.overmind.zero.ota`.
- Sorgente APK esatto: `582030833113f698933fbe83327c843e992a6ea0`, ramo `feature/zero22-active-recurrent-ota117`, [PR42 draft](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/42). Il nome del ramo conserva il numero candidato117; la release effettiva è119.
- Publisher [38469244134](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38469244134) **SUCCESS**: tutti71 entrypoint JVM, tutti26 gate Python originali aggiornati, Android Debug+Release firmata, permessi/provider, GitHub Release e manifest main. Il primo117/38467848821 aveva passato JVM/Debug ma falliva una stringa statica della stabilità motoria; non pubblicò APK.
- [APK pubblica](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1119-6593561d5593.apk),382708byte; SHA256 `6593561d5593d1b047b73022506be0203c9fe603b46f1f7de9cd7ba52b215b68`. Scaricata e hash/dimensione verificati. ApkVerifier verifica crittografica API24+: firme119 e116 valide, stesso certificato SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- Manifest pubblico HTTP200/code1119 verificato, Vercel productionREADY progetto `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, teamprime-1323, alias corretto, deployment `dpl_FhBaHF4P4WVcQ3u1ZpCCgRDEzS7z`, commit hosting `111dbcf3bafb6e35933e882620ab217a020712b9`. DEX pubblico contiene nucleo, esecutore skill, confronto esteso, observationDue e learningBlend.
- Installare SOPRA tramite Impostazioni→AGGIORNA APP o link diretto, **non disinstallare**. Prova APK→APK, dodici slot, velocità/temperatura sul S21 non ancora osservate.
- La nuova priorità comprende la migrazione graduale della policy esperta: senza evidenza nuova preserva scelte/epsilon storico; aumenta quota del nucleo con train reali. Non riavvia esplorazione vergine su ZERO esperto. Non ricompilare un ramo precedente perdendo questa correzione o il confronto esteso.
- Leggere [ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md), [ZERO22_PHYSICAL_SCREENING](ZERO22_PHYSICAL_SCREENING.md), [ZERO22_JVM_RESULTS](ZERO22_JVM_RESULTS.json). È un nucleo ricorrente leggero con reservoir fisso e readout appresi, non Dreamer completo/RSSM o intelligenza generale. Autorità prima dell'azione, memoria corpo+raggio, previsione energetica, replay recente/raro, recupero contestuale con feedback.
- Screening fisico vergine12pareggi. Primo checkpoint esperto0vittorie/1sconfitta/11pareggi, regressione di transizione documentata e corretta. Stesso checkpoint dopo correzione12pareggi su energia/danni/sopravvivenza, prese opposte-5.917frame medi. **Nessun uplift dimostrato**. Fixture sintetica37/40 scelte benefiche dimostra soltanto autorità/apprendimento del nucleo.
- Sul telefono ANALISI→LABORATORIO COMPORTAMENTALE (18 prove32s) e CONFRONTO2.2ESTESO/CASSE (24prove fino170s); copie isolate, report distinti inclusi in Condividi dati. TEST MAX rimane separato, conferma/STOP/thermal, substep1/60; sensi completi solo ai confini0.65s, nessun substep fisico saltato. Velocità maggiore di x29 non ancora misurata.
- Memorie/slot legacy preservati, chiave opzionale `recurrent_core_v22` atomica compressa. Riavvio conserva hidden e cancella solo pending non verificabile. Esperienze heldout e immaginate mai nel replay. Main conserva Idle Runner; soltanto hosting+paper aggiornati.


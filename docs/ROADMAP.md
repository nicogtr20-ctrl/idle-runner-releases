# Roadmap e criteri di accettazione

## Rettifica approvata v0.9.8 — autonomia decisionale
- [x] Rimuovere il bonus programmato che spinge heading verso/contro una sorgente visuale nota (`SensoryValenceGuide`).
- [x] Aggiungere `ExperienceCredit`: vere azioni+contesti visivi possono imparare dai contatti energetici con ritardo fino a 12 decisioni, senza direzioni corrette predefinite.
- [x] Preservare MotorSkillBank max6, anti-loop, mutazioni/rollback e salvataggi storici.
- [x] Aggiornare CERVELLO/ANALISI con eventi veri energia/danno accreditati, anziché contare un bonus visivo di scelta.
- [x] Regressione JVM, compilazione Android, avvio firma e OTA.
- [ ] Verificare su S21 che ZERO mantenga locomozione e si orienti efficacemente con apprendimento NON guidato. È ancora possibile che muoia vicino a una sfera.
- [ ] Migliorare il modello di conseguenze delle azioni a medio termine attraverso esperienze fisiche, non bonus geometrici.
Documento chiave: `docs/EXPERIMENT_0098.md` sezione 0098-A. Le righe precedenti della ROADMAP relative al bias angolare descrivono soltanto la prima `0.9.8-ota.42`.


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

## Pubblicazione verificata v0.9.5-ota.39
La compilazione Android firmata `38003676123` è verde: versionCode `1039`, SHA-256 `86caeb85b6eba94aa657d6f19ac7dff5dd0e4e8d2974bf71e8c6c12a3201c211`, 92.692 byte. Test in-game su Galaxy S21 ancora pendente, nessun reset richiesto.


## ZERO 0.9.6 — memoria delle conseguenze benefiche e nocive
- [x] Episodi vissuti di durata variabile 2–36 azioni e archivio ampliato a 24.
- [x] Misurare vantaggi e danni energetici a contatto, compreso danno che precede morte; non attribuire la fame a un oggetto.
- [x] Associazioni sensoriali positive e negative senza etichette fisse blu/rosso; richiamo favorevole debole e avversione per azioni dannose.
- [x] Consolidamento delle memorie confermate e rimpiazzo per forza, non pura età; migrazione em1→em2 senza reset.
- [x] ANALISI: positivi, negativi, ricordi confermati, lunghezza, scelte diverse da un pericolo.
- [x] Test JVM su em1 migrazione, red/blue da energia, morte, lunghe sequenze, regressioni e Android build.
- [ ] Prova real-world Galaxy S21: acquisizione dati, confronto su scene identiche e preferenza effettiva per energie/utilità.
- [ ] Verificare il ritorno alla tecnica di presa e completamento del cancello, non solo contatori.
- [ ] Ricerca su capacità autonoma di pianificazione a più passi dal modello del mondo, separata dai dati esperienziali.
Dettagli: `docs/EXPERIMENT_0096.md`.


## 0.9.7 — correggere regressione di movimento da memoria
- [x] Eliminare la sostituzione dei 62 pesi motorii con snapshot vecchi della memoria episodica, mantenendo gli attuali pesi e l'evoluzione autonoma in vita.
- [x] Limitare il replay fisico di mano/membrana a contatti realmente rilevati, fuori da tentativi di mutazione, senza script per il cancello.
- [x] Non abortire prove evolutive per un mero richiamo di azione; abortire soltanto quando si sono applicati davvero vecchi pesi sulla presa/membrana.
- [x] Introduzione guard antistallo basato su spostamento netto ed effetto fisico, che permette azioni indipendenti temporanee senza direzioni programmate.
- [x] Persistenza metriche diagnostiche antistallo e replay fisico; reset per dominio e vecchie memorie em1/em2 conservati.
- [x] Test JVM automatici sul guard, ricordi e protezione motore; compilazione Android staging.
- [ ] Confermare su Galaxy S21 che NETTO e VIA su tempo equivalente recuperino rispetto alla 0.9.5/0.9.6, senza regressioni nella presa.
- [ ] Se lo stallo persiste, separare effetto dei pesi storici, della politica neurale e dei trial non-controllati con A/B a stesso stato iniziale e seed.
- [ ] Valutare archivio delle abilità motorie con rollback misurato e comparazioni rigorose, non hardcode delle strategie.
Protocollo nel documento `docs/EXPERIMENT_0097.md`.

## 0.9.8 — priorità pratiche
- [x] Banca max6 controller motori verificati su NETTO e recupero senza gait programmata.
- [x] Evitare mutanti motori che riducono il NETTO oltre 20% nella prova relativa.
- [x] Penalizzare scelte ripetitive in stagnazione; stimolare esplorazione libera.
- [x] Usare valori energetici dei segnali imparati dai contatti per influenzare i 16 heading tramite settori visivi, senza blu=buono/rosso=cattivo.
- [x] JVM e compilazione Android staging, persistenza e reset.
- [ ] Verificare sul Galaxy S21 se aumentano distanza NETTA, sfere raccolte, tempo di sopravvivenza e diminuiscono ritorni allo stesso punto.
- [ ] A/B con stato e seed uguali; eventuale sensore del moto relativo e apprendimento efficacia tatto/membrana.
Vedi `docs/EXPERIMENT_0098.md`.

## 0.9.9 — risolvere regressione 0.9.5 senza script
- [x] Verificare differenza 0.9.4→0.9.5 nel codice GitHub: nessuna modifica alla fisica porta, nuovi controller replay episodici potenzialmente distruttivi.
- [x] Vietare override episodico dei pesi motore, mano, membrana, ma conservarne l'influenza sulle scelte.
- [x] Salvare 5 configurazioni di presa/membrana realmente funzionanti sul cancello, recuperarle solo dopo fallimenti reali della presa volontaria.
- [x] Aggiungere diagnostica di PRESA SCELTA, COMANDO, TENUTA e TECNICHE, rispettando soglia .28 e 0.075s, senza aprire automaticamente la porta.
- [x] Salvataggio/reset selettivo e test JVM fisici più compilazione Android staging.
- [ ] Verificare su Galaxy S21 con stesso cervello persistente e screenshot quale stadio fallisce e se ricompare colore arancione.
- [ ] Se ancora PRESA SCELTA=0 pur con molti TOCCHI, indagare la politica Q e l'influenza delle memorie come suggerimenti, non mutare la fisica per aggirare il problema.
- [ ] Verificare che movimento netto migliori e porta raggiunga >115U/attraversamento fisico. Documento `docs/EXPERIMENT_0099.md`.

## OVERMIND ZERO 0.10.0 — complete game save/load system
- [x] Separate working autosave from 12 permanent named snapshots for independently trained ZERO characters.
- [x] UI Save, Load, Overwrite, Rename, Delete per slot, New ZERO with double confirmation; named save buttons game/analysis.
- [x] Automatic one-step recovery backup before any load/new ZERO; saved slot file cannot be wiped by reset ALL.
- [x] Persist all controllers (62/18/82), 11,664 world weights, Q values, episodic memory, skills, stats/energy and exact physical body/room/gate/objects; life visited cells preserved.
- [x] Restore active game from named slot before physics/autosave using explicit reload request; checksum and bounds reject corrupted saves.
- [x] JVM codec large-model integrity test and physical-life restoration, Android Gradle QA, signed OTA gate.
- [ ] Validate all workflows on Galaxy S21 in-place with 0.9.9 data and 2 named slots: save, new game, load back, rename, overwrite, delete, recovery, reset ALL.
- [ ] Later: import/export save file for transfer to a different device and deterministic RNG/trial snapshots for scientific A/B testing.
See `docs/EXPERIMENT_0100.md`.

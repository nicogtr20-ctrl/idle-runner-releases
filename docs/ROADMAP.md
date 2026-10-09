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

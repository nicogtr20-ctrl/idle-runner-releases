# ZERO 2.2 — OTA136: oggetti, ricompense ritardate, sequenze causali, anti-correlazioni

**Decisione dell'utente:** Correggere tutti i quattro limiti architetturali *prima di creare eventualmente un nuovo ZERO vergine*. Non ripiegare sul vecchio cervello, non azzerare mai automaticamente la rete già addestrata.

## Il problema osservato
Nel caricamento del 14 ottobre 2026 (rapporto `20261014-201619-f1eea9e32146`), i moduli precedenti avevano 43 legami energetici ritardati meramente ipotizzati su 1.298 contatti. Il 2.2 viveva davvero (457/457 decisioni primary), spostava oggetti, raccoglieva occasionalmente energia rara, ma mancava un ponte attendibile fra un'azione fisica e una ricompensa ottenuta dopo più scelte. Più esperienza non può colmare da sola un'assenza del codice. Il periodo resta troppo breve per giudicare la capacità di apprendere del 2.2.

## Risoluzione concreta in OTA136
1. **Continuità di un oggetto fra osservazioni**. In `ObjectContinuityV21` un'ipotesi di superficie isolata rimane disponibile per 14 sweep (prima 8) attraverso brevi occlusioni: matching multivista, differenza spaziale, ampiezza, profondità e controllo delle assegnazioni ambigue. La nuova API `corroboratedMovementTrack()` emette un ID anonimo *solo dopo* vista dello stesso cluster prima e dopo, movimento ottico misurato, tocco e indipendente spostamento fisico. Nessun `UnknownWorld.Entity`, ID oggetto della fisica o coordinate di un obiettivo entra in questa identificazione.

2. **Conseguenza ritardata e credito corretto**. Il modulo `CausalSequenceV23` conserva ipotesi di movimento di superficie tracciata fino a **64 decisioni reali (~41,6 s simulati)**; aggiorna cambiamenti spettrali percettivi indipendenti e aspetta un vero contatto che aumenti l'energia. Un esito positivo richiede una singola ipotesi eleggibile, movimento validamente corroborato, comparsa/aumento ottico osservato della frequenza effettivamente toccata, contatto energetico reale, assenza di competitori, intervallo successivo 2–64 decisioni. Con più possibili cause la sequenza viene censurata, senza credito né finto fallimento. Le altre sequenze scadono come prove non riuscite; morte o riavvio censurano le ipotesi ancora aperte.

3. **Memoria delle sequenze, non solo premio immediato**. Persistono tentativi/esiti per ciascuna delle sedici azioni intervento e **16×16 coppie** (prima azione che altera l'oggetto + azione osservata dopo un cambiamento ottico). La memoria dura fra vite, caricamenti, salvataggi con nome e OTA; la storia di un singolo oggetto viene azzerata con la vita o col riavvio perché un ray-track locale non è un identificatore permanente. Il valore della seconda azione può diventare una piccola evidenza per il **solo** `RecurrentCoreV22.primaryScores`. Nessuna scelta viene delegata a un secondo arbitro.

4. **Anti-correlazione e verifica prima di guidare azioni**. Ogni ipotesi viene assegnata a train o heldout **al momento dell'intervento** e non può cambiare insieme. Il modello di probabilità condizionata deve battere un predittore di base basato su tutti gli interventi di addestramento di **almeno 6% Brier**, con almeno 60 prove heldout e almeno 14 contatti positivi heldout; confidenza limitata 0–0.35. Fino ad allora `choiceBias` è esattamente zero e nessuna testa Q riceve credito aggiuntivo. Solo dopo questa verifica, `RecurrentCoreV22.learnVerifiedDelayedCredit` aggiorna lievemente la **testa di valore Q** dell'azione antecedente, usando energia realmente raccolta, lo stato ricorrente realmente visto, l'età della sequenza e fiducia convalidata. Non allena la fisica su transizioni immaginate, non dichiara causalità certa, non aggiunge ricompense in base al colore di un oggetto.

## Installazione e compatibilità
- `GameActivity` collega il nuovo modulo al `SensorimotorMind`, lo ripristina dalla stessa preferenza dell'attuale ZERO, persiste `causal_sequence_v23` insieme a `rc22`, corpo e 12 slot.
- `SensorimotorMind`: un solo selettore (2.2) invariato nel suo contratto; termina e censura sequenze locali in caso di morte; non rinuncia mai all'attuale modello per ripiegare sul vecchio. Osserva il contatto reale con firma spettrale conseguente, non un token target prima della decisione.
- `BrainToolsActivity` espone statistiche di movimenti verificati, legami unici, ambigui, non attribuiti, heldout, confidenza.
- `Zero20PassiveSnapshot` riporta un riepilogo `causalSequences23` estratto soltanto dal profilo, senza pubblicare stati ricorrenti, tracce del mondo o ID temporanei.
- `ResetDomains.CAUSAL` cancella anche i nuovi pesi causali SOLO quando richiesto esplicitamente. La creazione di un nuovo ZERO resta una scelta utente e, in tal caso, inizia anche la nuova memoria a zero.
- Nessuna migrazione di formato necessaria per `rc22`: la nuova chiave `cs23` è opzionale. Backup e 12 slot già salvati rimangono validi.

## Test
`CausalSequenceV23Regression` verifica:
- luce/contatto senza movimento di oggetto non crea cause;
- spostamento esterno non corroborato dallo stesso ray-track non crea cause;
- train di 120 sequenze positive e 120 negative, **nessuna influenza** sulla policy prima di validazione;
- ulteriori 45+45 sequenze nel solo heldout, **nessun training dalle prove heldout**, miglioramento predittivo positivo contro baseline;
- sopravvivenza statistiche/coppie dopo save/restore e ripristino atomicamente indenne da file corrotto;
- controllo della scelta del 2.2: in assenza di evidenza le azioni del nuovo modulo non alterano il nucleo primario; dopo prove convalidate l'indizio ha influenza limitata SOLO in un contesto fisico pertinente;
- retropropagazione Q ritardata esclusivamente da energia reale e sequenze validate;
- suite fisica esistente, debug APK Android, firmata OTA, controllo live manifest e hash.
Le fixture provano invarianti architetturali, NON che ZERO abbia già appreso nel mondo fisico: le nuove competenze emergeranno soltanto dopo ore/vite di esperienze valide.

## Limiti non mascherati
Un ray-track è una stima e può confondere superfici fisiche; le soglie sono conservative per evitare credito inventato. Un'energia realmente scoperta ma senza aumento ottico percettibile **non** sarà ancora legata alla manipolazione. La valutazione individuale dei veri oggetti e l'efficienza nel lungo periodo richiedono nuovi dati dopo la OTA; non rivendicare capacità cognitive superiori dai soli test sintetici. Migliorie sul tracciamento nel caso di occlusioni complete lunghe potranno richiedere ulteriori interventi.

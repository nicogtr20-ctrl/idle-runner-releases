# ZERO 2.0 — Oggetti, incertezza causale, rischio e tecniche di presa

**13 ottobre 2026.** Branch sperimentale `feature/zero2-object-causal-learning-ota106`.
**Obiettivo:** connettere ciò che ZERO può realmente percepire (16 raggi + frequenze spettali + tatto) a ricordi di interazioni fisiche, danni/benefici ed efficacia delle azioni, evitando che la vicinanza temporale sia trattata come prova di causalità.

## Principi obbligatori

- Nessuna etichetta del simulatore ("cassa", "utile", "pericoloso", legge fisica, oggetto ID, posizione del premio, generatore del mondo) viene trasmessa al modello `ObjectExperienceV20`. Gli input sono soltanto raggi spaziali normalizzati, i 26 sensi già letti dal corpo, azioni effettivamente scelte e conseguenze fisiche registrate.
- La categoria riconosciuta **non identifica un singolo esemplare**: più ostacoli simili possono essere raggruppati; prospettiva e occlusione possono rendere sconosciuto il confronto. Finché manca un tracciamento multivista, gli esiti restano **di scena**, non dimostrazioni che una specifica cassa causò un evento.
- Il modello mantiene separati: tentativi, veri spostamenti esterni, fonti di energia fisicamente toccate, danni e **soltanto possibili** collegamenti movimento→scoperta. Una fonte comparsa per caso non diventa automaticamente la "ricompensa della cassa"; una lesione casuale non annulla la competenza meccanica.
- Effetti valutati **senza addestramento** tramite errore di previsione sul movimento e confronto con baseline action-only. Il massimo contributo al punteggio di scelta è ±0,18 e vale 0 se non ci sono 80 campioni holdout, 12 eventi fisici, miglioramento misurabile e almeno 6 esempi dell'azione nella categoria.
- Anche la componente energetica richiede almeno 20 collegamenti *plausibili*, 80 tentativi di interazione e la confidenza di cui sopra. Rimane una *correlazione ripetuta*, non un oracolo.
- L'assenza di premi in una serie di tentativi abbassa la stima empirica, ma non è codificata come impossibilità assoluta. Il peso del rischio dipende anche dalla riserva energetica.
- Il recupero fisico non attiva una presa automatica: i pesi HAND/MEMBRANA vengono archiviati solo dopo RED bilaterale e reale spostamento della cassa; possono essere recuperati dopo tentativi ripetuti di adesione scelta volontariamente senza nessuna presa confermata, escludendo candidati motori in prova.
- Alla morte sparisce soltanto l'esperienza temporanea incompleta; categorie, evidenze, pesi appresi e archivi delle tecniche restano. `CREA NUOVO ZERO` genera invece un profilo di lavoro completamente vergine. Gli slot con nome sono separati.

## Modifiche tecniche

- `ObjectExperienceV20.java`: descrittori di contorno + firme spectral, prototipi probabilistici limitati, episodi action→motion, credito delle fonti di energia espresso come ipotesi, stime condizionate a costo/rischio, holdout, serializzazione `oe2` con parsing atomico.
- `SensorimotorMind.java`: alimentazione solo da sensori, comportamento invariato quando la confidenza è 0, contributo graduato alle scelte solo quando verificato.
- `GameActivity.java`: foto degli spettri **prima** del consumo energetico, archivio di vere tecniche di presa sulle casse, ripristino protetto, reset dello stato volatile tra profili/vite, persistenza.
- `ResetDomains.java`: nuova chiave inclusa nell'azzeramento della percezione spaziale.
- `BrainToolsActivity.java`: contatori visibili in ANALISI senza mostrare posizioni o proprietà nascoste.
- `ObjectExperienceV20Regression.java`: spostamento mantenuto quando si tocca per coincidenza una fonte dannosa, frequenze distinguibili, campioni senza premio, gating a freddo, isolamento tra vite/profili, serializzazione e rifiuto di snapshot corrotti.

## I quattro mondi sono specifiche, NON regole nel cervello

1. Sempre energia positiva: il cervello dovrebbe progressivamente imparare l'utilità condizionata all'esperienza e al costo.
2. 1 su 100 positivo: il cervello deve distinguere rarità da impossibilità e valutare costi opportunità.
3. Positivo/negativo incerto: la firma percettiva, se realmente informativa, deve poter rendere le categorie differenziabili; altrimenti ZERO può solo stimare probabilità e rischio.
4. 99% vuoto, 0,5% positivo, 0,5% negativo: nessun sistema deve promettere che riconoscerà in anticipo una cassa veramente indistinguibile dalle altre.

Non è stata modificata la generazione del mondo per adattarlo ai quattro esempi: la progettazione deve valere anche fuori da scenari preparati.

## Evidenze, limiti e prossimi traguardi

**Staging JVM + Android Debug:** GitHub Actions `ZERO2 Object Cognition Staging QA` run 38418060491, verdi alla prima passata. Ricompilare dopo qualsiasi ritocco al recupero della presa prima di promuovere la release. Il test sul Samsung Galaxy S21 e il miglioramento effettivo del comportamento restano da verificare.

- Non abbiamo ancora segmentazione di più oggetti adiacenti o **continuità provata dello stesso oggetto** fra viste differenti.
- La nuova memoria non conserva una politica specifica di manipolazione per ogni categoria: riutilizza il vecchio archivio generale di prese fisiche. Il legame esclusivo oggetto↔tecnica resta un obiettivo.
- Le fonti energetiche raggiunte molto tempo dopo lo spostamento dell'ostacolo possono ancora essere non attribuibili, per prudenza. Serve memoria causale locale a intervalli lunghi e confronto con eventi indipendenti.
- Le stime probabilistiche non garantiscono l'ottimo matematico, una completa inferenza bayesiana o capacità di comunicare pensieri. Vanno misurate empiricamente nel mondo vivo e in contesti nuovi.
- La regressione vista in OTA 105 (pochi comandi di presa locali) non viene dichiarata risolta per il solo fatto di aver aggiunto una memoria. Misurare lo stesso rapporto grip-contact normalized sui report successivi.

## Criterio di distribuzione

No release da branch di lavoro. PR verso **`feature/overmind-inapp-updater`**, firma originale nel publisher stabile, manifest SHA-256 pubblicato su `main` tramite CI, verifica pubblica di `https://overmind-zero-updates.vercel.app/latest.json` e APK. Solo allora dire che **AGGIORNA** è disponibile. Non cancellare slot o cervelli appresi.

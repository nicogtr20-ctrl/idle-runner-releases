# ZERO 2.2 — nucleo ricorrente attivo e verifica su telefono

Data: 13 ottobre 2026. Base ufficiale: `f462ee90ddd8b61645d367c985be1ef51bf391b7`, OTA116. Questo documento descrive il codice candidato, non certifica miglioramento comportamentale.

## Differenza dalla 2.1
La 2.1 registra ipotesi dopo la scelta. `RecurrentCoreV22.evaluate()` viene eseguito prima della selezione definitiva e confronta tutte le 16 azioni. Il punteggio precedente è una prior limitata e decrescente, non l'unico decisore. Corpo, collisioni e leggi energetiche restano nel simulatore; nessuna identità di oggetto, destinazione o soluzione di cassa entra nella policy.

## Rappresentazione condivisa
164 caratteristiche: 26 sensi originali; cinque misure reali (contatto, presa mantenuta, forma, tensione, ostacoli) aggregate da 96 siti a 16 settori; 32 offset delle origini fisiche dei raggi; sei proiezioni dei coefficienti corporei realmente applicati; 16 raggi di profondità; quattro caratteristiche di focus percettivo anonimo. La rappresentazione ricorrente ha 24 unità. Gli offset evitano di interpretare una deformazione della membrana come spostamento dell'ostacolo. Il focus rimane una stima incerta, non identificazione semantica dimostrata.

## Modello e decisione
Reservoir ricorrente deterministico fisso e readout appresi condizionati dall'azione. Non è un'implementazione di Dreamer/RSSM: l'encoder ricorrente non viene addestrato con backpropagation. Il modello prevede variazioni sensoriali, postura, energia e memoria, mentre i coefficienti di controllo non mutano spontaneamente negli scenari immaginati. Le valutazioni sono pure: non creano esperienze, non allenano pesi e non modificano il generatore casuale.

Readout distinti: valore a ritorno scontato, variazione energetica reale e rischio da danno di contatto effettivo. Il segnale energetico netto comprende fame e condizioni ambientali; il danno resta separato, così un guadagno nello stesso intervallo non cancella un evento nocivo. Alla morte l'ultima transizione viene chiusa con esito terminale, senza bootstrap da una vita nuova.

Il planning esamina al massimo tre decisioni future (circa 1,95 secondi), ricalcolando lo stato ricorrente. Non è pianificazione generale a lungo termine. Per ogni azione richiede almeno 24 transizioni allenate e otto di verifica, errore medio inferiore alla baseline immutata e controllo specifico dell'errore energetico. Confidenza limita l'influenza della previsione. Il confronto delle conseguenze reali sul telefono resta necessario.

## Esperienze e competenze
Replay persistente: 72 transizioni recenti e 24 eventi rari, due aggiornamenti addizionali per nuova esperienza allenabile. I campioni holdout non entrano né nel training né nel replay. Capacità e costi sono limitati intenzionalmente per Android.

`CompetenceMemoryV21` conserva coefficienti HAND/MEMBRANA osservati durante presa opposta e spostamento fisico. `SkillExecutionV22` ne permette uso temporaneo soltanto in contesto compatibile, con adesione scelta dall'agente e contatto reale. Non sovrascrive gli incumbenti. Interrompe per perdita del contatto/adesione, esperimento corporeo o tre intervalli senza effetto. Ricerca una tecnica nuovamente a ogni confine decisionale: non riproduce una sequenza cieca.

Una finestra di adattamento corporeo che usa una tecnica archiviata viene scartata (`techniqueUsedSinceAction`), evitando falso credito alla baseline. L'archivio rimane intatto dopo un fallimento singolo. Questi vincoli riducono i rischi di regressione; non garantiscono conservazione perfetta delle abilità in qualunque mondo.

## Persistenza e migrazione
Nuova chiave `recurrent_core_v22` nel profilo del solo ZERO corrente. Payload compresso, checksum, validazione prima dell'installazione dei pesi. Codec Base64 compatibile con API24. Gli slot catturano già il profilo completo: nessuna cancellazione delle chiavi precedenti. Riavvio nella stessa vita conserva hidden e pesi; morte azzera solo contesto transitorio. Package `com.overmind.zero.ota` e chiave ufficiale devono rimanere invariati. Installazione APK sopra quella corrente; mai disinstallare per provare l'upgrade.

## TEST MAX
Sensori costosi campionati ai confini della decisione, evitando calcoli identici su ogni subframe. Il test di parity confronta lettura continua e campionamento ai confini a parità di osservazioni. TEST MAX usa passi fisici fissi di 1/60 s, budget limitato per restituire controllo alla UI, stop separato e gestione termica. x5/x20 restano nel ciclo ordinario. Il rapporto visualizzato è tempo simulato / tempo reale; non è un limite software né un obiettivo di qualità. Il precedente x29 riferito dall'utente non può essere migliorato nominalmente aumentando il tempo fisico. Misurare velocità, temperatura e responsività sul Galaxy S21.

## Verifica sul telefono
CERVELLO / ANALISI → CONFRONTO CERVELLO 2.2 esegue 24 prove (12 coppie) su copie del profilo corrente, stessa scena e seed. Controllo: policy precedente. Attivo: nuovo nucleo e recupero contestuale. Arena fisica da 170 s con casse, risorse nascoste, lastre e danni; misure: energia, danno, sopravvivenza e prese opposte. Controllori fissi, senza tempeste, mutazioni o streaming: è screening ridotto, non equivalenza completa al gioco. Non modifica il personaggio e può essere interrotto.

Risultato nel deposito separato `zero_core_lab_v22`; inviato solo tramite CONDIVIDI DATI e mantenuto anche nei report multipart. Il vecchio laboratorio navigazione rimane separato. Servono più coppie, scene nuove, lunga retention e condizioni rare prima di sostenere generalizzazione. Non confondere test verde del software con superiorità del cervello.

## Prove disponibili e stato del rilascio
67 test JVM storici eseguiti localmente senza errori prima delle ultime integrazioni. Il test dedicato dimostra autorità delle scelte, energia prevista, hidden immaginato, esclusione holdout, reset/resume, corruzione e codec slot. Test indipendente delle tecniche verifica contatto/adesione, protezione degli esperimenti, timeout e archivio immutato. Ripetere suite e build Android sul commit finale e registrare run/SHA dell'APK prima di dichiarare disponibilità.

Al momento della stesura firma e pubblicazione della nuova APK non sono ancora verificate. Non ci sono ancora dati di installazione, prestazioni o uplift della 2.2 dal Galaxy S21. La promessa di un agente superintelligente non è dimostrata da questa implementazione.

## Ripresa in altra chat
Leggere AGENTS, OVERMIND_START_HERE, HANDOFF, questo paper e ZERO21_VALIDATION_RESULTS. Identificare l'ultimo commit effettivamente compilato, manifest pubblico, SHA dell'APK e certificato storico. Non confondere la 2.1 passiva con la 2.2 attiva. Rieseguire solo i controlli giustificati da nuove modifiche; lasciare le prove lunghe al telefono per ridurre minuti CI. Prossimo lavoro: rapporto A/B reale, retention in nuove posture e affidabilità delle previsioni fuori distribuzione; non abbassare i gate per inventare intelligenza.

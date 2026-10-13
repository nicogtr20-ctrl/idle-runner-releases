# ZERO 2.2 — correzioni su evidenza del primo individuo vergine (14 ottobre 2026)

**Origine**: upload esplicito GitHub branch zero-telemetry report 20261013-222649-aa796f8367d4 (14 parti). ZERO vergine, circa 5 minuti reali a x20 secondo utente. Sessione indicata dai contatori: 18491 intervalli elaborati dal recurrent, 48 terminali, 74 contatti energetici positivi, 153 dannosi, 322 chiamate con fiducia positiva in passato e fiducia finale 0.000; 25950 interruzioni di contatto BOX (25922 associate a cambi di geometria), nessuna cassa energetica completamente scoperta. Alcuni contatori storici non possono essere confrontati con i checkpoint di individui precedenti.

## Cause rilevate nel codice OTA119

1. RecurrentCoreV22.evaluate usava learningBlend() soltanto dipendente dal volume training, fino a 90% anche con confidenza validata nulla. Il controllo precedente era sovrascritto dal nucleo acerbo.
2. SquareGripPuzzle conta gripHold per frame ma non vincola un punto già aderente alla faccia rigida. Deformazioni minime resettano la presa.
3. SensorimotorMind inoltrava energia/danno al core ma non il movimento esterno fisicamente verificato; GameActivity riconosceva spostamento delle casse coperchio per archiviare tecniche ma non lo attribuiva alla decisione fino allo scoprimento.

## Correzioni implementate su branch di test

- Per ogni azione, impatto del core limitato a maturity * trust, con trust validato soltanto da predizione heldout migliore della baseline e errore energetico. Se trust=0, prior storico esattamente invariato, indipendentemente dal numero di campioni. Anche exploration non sostituisce la policy se trust=0.
- Nel solver dei coperchi energetici, breve tether fisico per punti con presa già reale e comando volontario ancora attivo: solo stessa faccia, spostamento continuo <=9, allungamento esterno <=8, distacco oltre limite; niente agganci nuovi, passaggio attraverso solidi o spostamento con sola presa. Collision solver e vincolo bilaterale intatti.
- Inoltrato motion effect reale al core: feedback ausiliario max 0.018 normalizzato per intervallo; spostamento del cover accumulato solo se presa bilaterale confermata e movimento effettivamente misurato. Nessuna etichetta di oggetto, colore, posizione o premio su contatto.

## Test richiesti / gate di rilascio

- RecurrentCoreV22Regression: 2000 campioni senza validazione non alterano la policy storica; evidenza heldout valida esercita decisioni.
- BoxGripRetentionRegression: la presa reale mantiene un breve contatto, si stacca quando comando OFF o distacco eccessivo.
- Tutti i test originali su collisioni, 96 punti, memoria, oggetti, struttura, cover e firmatario; build APK Android e confronto fisico 2.2. QA branch di test prima di pubblicazione.
- Al rilascio: test sul Galaxy S21 + confronto A/B esteso, verifica mantenimento slot. Nessun miglioramento comportamentale dichiarato finché i dati non lo dimostrano.

**Stato attuale**: patch proposta per QA; non dichiarare successo finché workflow QA e publisher non finiscono.

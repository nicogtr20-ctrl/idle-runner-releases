# ZERO 2.2 — Perdita delle prese apprese, causa trovata e OTA125

Data: 14 ottobre 2026. Questa è la diagnosi di una **regressione fisica appresa**, non un cambiamento intenzionale della policy e non una memoria Q cancellata.

## Evidenza indipendente di tre report sulla stessa continuità di ZERO

`zero-telemetry/reports/20261014-100046-d057ad79b120` (~10:00:46Z),
`zero-telemetry/reports/20261014-102432-d1a3b1f11f55` (~10:24:32Z),
`zero-telemetry/reports/20261014-122450-d8341d17d413` (~12:24:50Z).

- `online_accepted_membrane_v094`: 152→182→209, quindi il selettore fisico ha ammesso **57 mutazioni della membrana** tra il primo e l'ultimo report.
- `square_uncovered_v1163`: 30→37→38. ZERO sapeva realmente aprire casse energetiche, poi la progressione quasi si arresta.
- Il coefficiente tattile della **testa neuronale della presa della membrana** (`morph_brain_v07` peso n. 81, `Morphology.output`, `touch*w[81]`) è passato da **+2.546899** a **+2.4425557** e infine a **−2.3697898**. Non è un neurone simbolico, è il moltiplicatore del contatto fisico nel comando locale.
- Nel codice fisico `Morphology.Surface.step` calcola `gripCommand[i] = clamp(tanh(grab)+gripDrive*touchSense[i],0,1.4)`. Una scelta ON dà `gripDrive=0.82`, ma con testa locale `tanh(grab)≈−0.98` il risultato viene clampato a **0**, sotto `GRIP_THRESHOLD=0.28`. Un calcolo di controllo usando gli attuali 82 pesi e tre configurazioni sensoriali generiche con contatto vero dà `gripCommand=0`; con i due precedenti genotipi dà `gripCommand=1.4`. Questo è un test di sensibilità, NON la riproduzione esatta delle 96 condizioni durante le partite.
- In `gripContactObserver` dell'ultimo report, campioni degli oggetti `41,44,45` mostrano spesso `on=1` (scelta presa attiva), `touchPoints=1..55`, `commandPoints=0`, `heldPoints=0`, `vetoPoints=touchPoints`. Questo conferma il divario **intenzione globale vs attuazione locale**; nei report precedenti erano visibili prese con decine di punti realmente attivi, incluso un episodio di 96/96. Il cervello non ha necessariamente "dimenticato" il valore della cassa: il motore locale si è deteriorato.
- `GripSkillBank` aveva archivio pieno (`grip2|4|19|9` all'ultimo report) e solo modelli il cui peso tattile era compreso fra **−2.033 e −0.796**, nessuno dei precedenti due genotipi tattili fortemente positivi. L'archivio non proteggeva necessariamente il vincitore corrente.
- **BUG del codice**: in `GripSkillBank.observe`, con 9 slot pieni, una tecnica mai vista veniva stimata sulla sua **prima occasione valida** con `heldFrames=1`. Se non superava *subito* il peggior modello storico, veniva scartata senza memoria provvisoria. Centinaia di ulteriori successi della stessa tecnica ripartivano ogni volta da 1: nessuna possibilità di archiviarla quando diventava affidabile. Inoltre `GripSkillBank.retrieve` poteva rifiutare tutte le tecniche storiche per una distanza sensoriale >0.28, anche dopo 120 frame consecutivi di un tentativo di presa realmente comandato ma fallito.

## Correzione strettamente strumentale: archivio e recupero senza comandare ZERO

1. **Provisional evidence**: `GripSkillBank` tiene al massimo 5 potenziali nuove tecniche in una lista **transiente** (non attua il movimento). Aggiorna la loro efficacia solo quando la fisica ha dimostrato adesione vera. Dopo almeno 5 frame di tenuta e 3 frame di tenuta opposta, se un nuovo modello ha una prova di efficacia superiore a un elemento storico entra nell'archivio dei 9. Nessun acquisto per semplice contatto, nessun premio inventato.
2. **Dopo fallimento ripetuto e volontario**, al cancello o sulle casse il gioco attiva `retrieveAfterRepeatedFailure` anziché la ricerca normale. Prima ricerca modello storicamente verificato con contesto visivo compatibile; solo se non c'è **e** l'appello è avvenuto dopo il già esistente timeout dei tentativi ON reali, può considerare il miglior modello precedentemente verificato con `opposedFrames>=8` e `doorMotion>=2`. Resta una sostituzione di pesi del corpo, NON un comando `adhesion=ON`, un target o un cambio d'azione.
3. **Non** alterati `SensorimotorMind.choose`, il reward del cervello, i 16 comandi, la scelta ON/OFF, l'epsilon, il motore deformabile, la fisica delle casse, le gemme e la vista. Non altera `Morphology.output` né trasforma un contatto in presa obbligatoria. Non rimette nel cervello i parametri +2.44 perduti: quelli sono nei report precedenti, non nella cassetta degli attrezzi attuale dell'app; una restituzione personale dall'archivio remoto richiederebbe un intervento separato, esplicito e scientificamente tracciato.

## Qualifica del limite

I dati dimostrano regressione della capacità motoria e archivio insufficiente. Non sappiamo quale precisa mutazione abbia invertito il coefficiente: si osservano soltanto checkpoint distinti nel tempo, non uno storico di ogni peso a ogni frame. E neppure garantiamo che la tecnica storica archiviata più simile abbia identica efficacia nel mondo presente. OTA125 **riduce il rischio** di perdita permanente delle prossime competenze e migliora il recupero delle già archiviate; non obbliga la riuscita alla prossima presa. Valutare sul telefono dopo aggiornamento tramite `Condividi dati con GPT`, concentrandosi su `on=1`, `commandPoints`, `heldPoints`, successi delle casse e numero di recuperi, sempre preservando lo stesso individuo.

Test JVM: archivio pieno di 9 con tecnica nuova che non supera il primo frame e che entra solo dopo ripetuti successi, blocco di recupero normale su contesto sbagliato e fallback solo dopo reali prese dimostrate, compatibilità archivio grip1/grip2, e regressioni fisiche precedenti. Richiesti QA verde, build Android e firma ufficiale per distribuzione.

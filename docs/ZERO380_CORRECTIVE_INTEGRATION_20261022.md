# ZERO380 — Correzioni concrete ai blocchi e alle gemme (22 ottobre 2026)

Branch: `fix/zero380-training-physical-spectral-recovery` (NON è il canale OTA stabile). Base: OTA379 Training 1.
Revisione: https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/110

## Difetti verificati prima della correzione

Nella build 379 tre riproduzioni Android passavano proprio perché dimostravano il difetto:
1. Due precedenti contatti dannosi reali con la banda rossa, fotografia ottica a 100 unità e due opzioni EAST/NORTH: il brain V353 teneva EAST (pericolo), perché la differenza punteggio 0.0245 restava sotto la soglia 0.025.
2. Due contatti positivi reali con la banda blu: il brain V353 preferiva continuare NORTH piuttosto che una disponibile direzione EAST migliorativa.
3. Una particella del vero simbionte viveva dentro una mattonella solida Training; la fisica della stanza proiettava 96 membrana + 10 corpo, non tutte le 64 particelle fluide. La correzione posizione fisica poteva censurare un intero tentativo motorio come disturbo esterno.

## Soluzioni adottate

- Zero3CognitionV353.select: quando esiste un'evidenza visibile affidabile appresa da contatti veri, soglia prudenziale ridotta da 0.025 a 0.005. In assenza di contatti affidabili la soglia rimane 0.025; nessuna preferenza innata per colori, nessun goal o impulso guidato. I margini del modello attendibile continuano a essere rispettati.
- TrainingRoomV379: collisione con i muri AABB usando il CCD `SquareCollisionSolver.project`, stessi strumenti già testati per casse; correzioni su tutti i 96 punti membrana e 10 nodi corporei, più segmenti di connessione (per evitare attraversamento); nessuna forza aggiunta.
- SymbioteWorldStepV171.interior: anche `law=7` ora applica contatto fisico alle 64 particelle interne; il broadphase valuta il segmento precedente→attuale, non solo i due valori assoluti, per impedire tunneling ad alta velocità.
- GripSurfaceComposition: la percezione tattile dei muri veri non viene più cancellata dal compositore delle prese.
- Zero3OriginalBrainV356 + Zero3RecurrentBrain + Zero3CognitionV353 + Zero3InstrumentalCreditV371: registra un effettivo blocco tattile durante la decisione, mantenendo censurato il modello motorio se la scena ha perturbazioni. Tre fallimenti della stessa direzione motoristica possono alimentare il solo segnale di stallo e ampliare le opzioni del singolo cervello GRU; niente ricompense positive false, niente curva o fuga preimpostata.
- GameActivity: passa le coordinate pre-collisione per CCD, evita di considerare come vera tempesta un evento ambientale disabilitato nella stanza Training.

## Salvataggi e compatibilità

Persistenza dei pesi `zero3_original_brain_only_v356` e 12 slot **INVARIATA**. Nessuna rinascita forzata né riassegnazione dei significati dei sensori o delle forme di oggetto. La scena libera non è sovrascritta dal Training 1. I nuovi segnali di blocco sono transitori, non cambiano il formato dei checkpoint GRU.

## Evidenza QA / limiti

Test Android introdotti e invertiti in `Zero380FailureReproductionAndroidTest` (6 test): scelta rossa, scelta blu, nessuna preferenza in cervello vergine, blocco reale senza apprendimento falso, CCD solido e 64 particelle. Integrazione Training/Scene + regressioni V378, V377, V375 e V374.
Prima esecuzione revisione base: 20 Android tests PASS, https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39851684137
**Attenzione:** esecuzione successiva (ultimo fix broadphase inner) ha FALLITO nel cold-start del test V379; causa diagnostica sotto indagine. **Non pubblicare né fare merge finché non verificato.**

Questi test sono prove di causalità software e di regressione, non 2.000 decisioni/autonomia dimostrata nella stanza su Galaxy S21. Non dichiarare un navigatore o un istinto di fuga funzionante senza test comportamentale continuo su telefono, con dati condivisi.

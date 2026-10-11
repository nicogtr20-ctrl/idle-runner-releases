# OVERMIND ZERO — Esperimento 0116.4

**Data: 11 ottobre 2026.** Diagnostica del cancello e modello contestuale di previsione del movimento in sola osservazione. Base: OVERMIND 0.11.6-ota.70, con casse e scatola nera. L'esperimento 0117 sul piano causale resta separato.

## Motivo

ZERO ha raccolto la ricompensa della stanza nella vita 95 con spostamento massimo del cancello 76 U, nonostante il monitor precedente richiedesse un'apertura completa >115 U per contare il passaggio. Ha poi spostato il cancello solo parzialmente in altre vite (es. 21,9, 14, 40,8 U) e ha abbandonato. Non è possibile inferire dal solo contatore precedente se abbia sfruttato un'apertura parziale fisicamente legittima o una collisione difettosa. La scatola nera attuale copre soltanto gli ultimi cinque minuti simulati: la prima stanza è stata risolta dopo oltre venti minuti, e non possiamo ricostruirla retroattivamente.

## 1. Ingresso parziale osservato dalla fisica

`GateBehaviorMetrics` ora tiene separati gli eventi:
- `opened`: cancello completamente aperto secondo la soglia storica;
- `passages`: precedente criterio di passaggio *con apertura completa*, rimasto invariato;
- `gatePlaneCrossings`: nucleo che passa davvero oltre il piano del cancello;
- `realEntrances`: nucleo che raggiunge l'interno dalla parte esterna dopo aver superato il cancello, con movimento fisico plausibile e senza teletrasporti;
- `partialEntrances`: ingressi osservati quando `openingFree()` è ancora falso;
- `questionableEntrances`: ingressi fisici osservati anche se la luce del varco è minore del diametro nominale del nucleo (54 U), possibile segnale di imperfezioni del collision solver. Questo **non** certifica che la geometria fosse valida.

La serializzazione `gb2` preserva i dati storici `gb1` con un decoder retrocompatibile. Il rilevamento è un puro osservatore: non modifica coordinate, forze o ricompense.

## 2. Diario completo della vita (non soltanto cinque minuti)

Nuovo `GateLifeJournal`, con sequenza cronologica sintetica della vita: `ROOM`, `APPROACH`, `TOUCH`, `GRIP_CMD`, `HELD`, `BILATERAL`, `MOVE_5U`, `STALLED`, `RELEASE`, `LEAVE`, `GATE_PLANE`, `ENTER_PART`, `ENTER_FULL`, `CHECK_CLEARANCE`, `ROOM_ENERGY`, `DEATH`.

Ogni riga è timestamp in decimi di secondo simulato, evento, spostamento e massimo spostamento del cancello, posizione relativa del nucleo, energia e frame di presa confermata/bilaterale. Solo eventi significativi, massimo 260 per vita e massimo 30.000 caratteri per snapshot. Se ci sono oltre 260 eventi, i più antichi vengono scartati: una limitazione esplicita, non un registro infinito. Persiste `gate_journal_current_v1164` nei checkpoint di scena e nei salvataggi nominati e `gate_journal_last_v1164` congelato immediatamente prima del reset della vita.

Il diario non riconosce obiettivi, non dà suggerimenti a ZERO e non comanda il cancello. Anche se l'energia viene raccolta dopo molti minuti, la sequenza saliente viene registrata, entro i limiti dichiarati.

## 3. Movimento condizionato dal contesto (shadow)

Nuovo `ContextualMotionProbe`: osserva gli stessi 26 sensori all'istante di una decisione e il vero delta fisico del nucleo dopo 0,65 secondi. Impara una coppia di predittori lineari per ciascuna delle 16 azioni con stato e bias, senza nuove capacità motorie, senza utilizzare informazioni sulle identità degli oggetti o risultati futuri simulati.

- Il 20% dei campioni per azione è *holdout*: non allena il nuovo modello.
- Sugli stessi campioni misura l'errore del modello contestuale, il riferimento di immobilità e, quando il modello di navigazione precedente risulta verificato, il vecchio spostamento medio per azione.
- Usa un criterio di miglioramento di almeno il 10% su 80 verifiche comparabili e almeno quattro azioni sufficientemente allenate **soltanto per il report di affidabilità**. Superarlo non abilita nulla nella policy.
- Peso e errori restano nel nuovo profilo `motion_context_shadow_v1164`, non sono combinati con la rete attiva. Nessuna modifica alla funzione di punteggio o all'epsilon casuale.

## 4. Invarianti e rollout

- Caricare e salvare tutti i 12 slot senza perderne i pesi e la scena preesistente.
- Non modificare `Morphology`, `MotorBrain`, `ScarcityLab`, `DecisionBrain`, la funzione di ricompensa, i criteri della presa, né la sequenza di scelte motorie.
- Test dedicati `GateBehaviorMetrics.main` verificano la migrazione `gb1` e un attraversamento sintetico osservativo con gate non completamente aperto; `GateLifeJournal.main` verifica registrazione, lunga durata, morte, ripristino, corruttibilità; `ContextualMotionProbe.main` verifica addestramento su diversi contesti, holdout, persistenza; `ShadowNonInterferenceRegression.main` confronta 320 transizioni con e senza il probe verificando azioni, esplorazione casuale e serializzazione dei pesi identiche. Si aggiungono i test storici JVM e compilazione Android.
- Pubblicazione firmata soltanto dopo CI e verifica del percorso OTA; mai disinstallare ZERO. La 11.7 shadow causale rimane ramo indipendente.

Il significato empirico delle future osservazioni rimane aperto: un unico successo può derivare da comportamento appreso, circostanze favorevoli o debolezza collisionale. Misurare invece di presumere.

# OVERMIND ZERO — Experiment 0116.2: scatola nera delle decisioni

Data: 11 ottobre 2026. Obiettivo: studiare perché ZERO, dopo una vita lunga, talvolta finisce nei pericoli nonostante abbia imparato l'associazione negativa con i loro segnali ottici.

## Principio inviolabile

L'osservatore DecisionBlackbox NON seleziona o modifica azioni, non influenza il calcolo dei punteggi, non modifica premi, memoria, pesi fisici o numeri casuali. È un registratore, non un cervello aggiuntivo. Modulo di pianificazione sperimentale 11.7 resta escluso da questa release.

## Dati campionati

Ogni circa 0,65 secondi di TEMPO SIMULATO salva un campione per la vera azione completata. La finestra mobile ha durata massima 300s (5 minuti), non più di 475 azioni, e un limite di testo di 145.000 caratteri per finestra. Nel caso di esaurimento della riserva, registra anche il comando terminale eventualmente ancora incompleto e congela una copia della finestra della vita appena terminata.

Per ogni campione:
- 26 sensi nel momento esatto della scelta (energia, velocità, deformazione, tattile, raggi di ostacoli e segnali luminosi raw), senza etichette predefinite blu/rosso o posizioni reali degli oggetti.
- Azione prescelta tra 16, azione col miglior score PRIMA dell'eventuale epsilon random, flag di azione casuale, probabilità epsilon, flag anti-stallo.
- Punteggio totale dell'azione eseguita e della migliore alternativa.
- I 14 contributi gia' calcolati nel metodo choose per l'azione eseguita, nell'ordine: legacyQ, energeticCredit, threeStepLookahead, curiosity, jitter, episodic, antiStall, temporal, temporalSkill, proprioception, spatial, causal, instrumental, goalContinuity. Nessun modulo viene interrogato di nuovo a scopo diagnostico.
- Risultato dopo la fisica: energia prima e dopo, delta energetico da CONTATTO (separato dal metabolismo), effetto fisico esterno, traslazione reale del nucleo dx/dy oppure flag movimento non misurabile, errore del modello e segnale di terminalità.

Persistenza: decision_flight_current_v1162 alla scrittura della scena (ogni ~12s e quando si esce dall'Activity), decision_flight_last_v1162 con commit alla morte. I nuovi campi sono preferenze del personaggio ATTIVO e seguono i 12 salvataggi con nome. Reset totale/nascita nuova partita puliscono il profilo, ma non gli altri slot congelati. Lo storico della vita terminata sopravvive alla morte successiva solo finché non viene sostituito dalla morte seguente; resta disponibile come archivio del checkpoint nominato.

Il pulsante CONDIVIDI DATI CON GPT invia esplicitamente una copia dei campi currentZero inclusi i due nuovi snapshot su GitHub PRIVATO, senza inviare token, slot non attivi o account. I due snapshot sono ciascuno limitati a 145k caratteri: verificati rispetto alla dimensione ~450k del profilo reale e limite massimo 900 KiB dell'upload. Il lettore GitHub corretto nella OTA.68 accetta metadata di risposta Base64 fino a 2 MiB.

## Schema tecnico compatto

Formato db1: riga iniziale 'db1|elapsed_cs|count', poi una riga CSV per decisione con 57 campi:
0 time_cs; 1 action; 2 best_action_before_random; 3 exploratory; 4 anti_stall; 5 terminal; 6 movement_observed; 7 epsilon_milli; 8 chosen_score_milli; 9 best_score_milli; 10 energy_before_milli; 11 energy_after_milli; 12 contact_energy_centi; 13 external_effect_centi; 14 dx_decimi; 15 dy_decimi; 16 model_prediction_error_milli; 17–42 sensi (26 interi x1000); 43–56 componenti punteggio (14 interi x1000).

I numeri rappresentano dati quantizzati; la differenza dei punteggi non significa necessariamente causalità e i contatti dannosi possono essere più recenti dell'ultima decisione completa. I moduli inattivi su heldout hanno contributo zero. La distanza del nucleo è osservata, non dedotta da direzione desiderata. In caso di riapertura durante un'azione già in corso il breakdown dell'azione originaria non viene inventato, riprende dalla decisione successiva.

## Test e limiti

La classe DecisionBlackbox.main verifica 180 scelte identiche (stesso seed) su due sensorimotor controller CON/SENZA registratore, numero di esplorazioni e serializzazione dei vecchi modelli perfettamente invariati; inoltre verifica finestra temporale/cap, movimento reale, snapshot terminale, morte senza perdita della vita congelata, restore e payload corrotto. CI esegue tutte le vecchie prove JVM, budget upload e Gradle Android assembleDebug.

Il tracciato **non** recupera le vite precedenti all'installazione di questa versione. Per stabilire se il rischio deriva dall'esplorazione o da una scelta appresa, osservare flag exploratory e il divario bestScore-chosenScore; per un conflitto di reti confrontare instrumental vs Q/episodic; per un fallimento motorio confrontare azione direzionale e dx/dy fisico. Non si deve progettare una correzione specifica prima dei dati.

La 11.7 di pianificazione causale shadow resta in un ramo di ricerca separato.
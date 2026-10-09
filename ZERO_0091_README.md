# ZERO-009.1 — Metrica di apprendimento e azzeramento controllato

Richiesta: capire *se* l'agente apprende (non solo quante esperienze) e consentire di azzerare ciascuna memoria singolarmente o tutte, mantenendo l'apprendimento del modello del mondo sempre abilitato.

## Cambiamenti
- Elimina toggle `FERMA/ADDESTRA`, ora la pagina gioco mostra sempre `APPRENDIMENTO ON` durante la simulazione e il tasto `ANALISI`. Pausa fisica rimane separata per l'utente.
- `LearningDiagnostics.java`: un'esperienza reale su cinque è **holdout**: si valuta l'errore senza usarla per modificare i pesi del modello o dei valori azione. Confronto con una baseline di previsione “nessun cambiamento”, sullo stesso identico stato/azione osservato. Media per gruppi di 5 osservazioni holdout e buffer cronologico circolare 48 punti. Persistenza `sensorimotor_diagnostics_v091` separata da `sensorimotor_model_v09`.
- La nuova UI `BrainToolsActivity` mostra grafico degli errori di previsione, baseline, conteggi reali e le ricompense medie delle azioni esplorative vs azioni della policy **con avvertenza che il confronto delle ricompense non è controllato per stato**. Il grafico di predizione non misura ancora la capacità di risolvere la porta. Non inventare esperienze prima dell'installazione della 0.9.1: la serie inizia da zero.
- `ResetDomains.java` elenca chiavi disgiunte, sette reset parziali: nuovo modello + diagnostica, vecchio cervello decisionale, motorio, manipolazione, membrana, associazioni mondo, progressi stanza. `AZZERA TUTTO` elimina l'intero profilo locale. Reset parziali richiedono conferma, TUTTO due conferme.
- La schermata non modifica parametri in memoria mentre il gioco è sospeso; invia una richiesta persistente, poi riapre il gioco. `GameActivity.consumeResetRequest()` usa `commit()` sincrono per rimuovere SOLO le chiavi richieste prima di ricaricare oggetti e rinascere col resto delle memorie.
- La versione precedente consentiva un reset generale con doppio tap nascosto nell'intestazione: quel percorso senza dialoghi è stato eliminato.
- Gli aggiornamenti di APK rimangono installabili sopra la stessa app firmata: i salvataggi della v0.9 persistono a meno che un reset venga confermato.
- La pagina `CERVELLO/ABILITÀ` mostra anche gli errori di holdout e apre `ANALISI / AZZERA` dal bordo inferiore.
## Limiti
Una previsione più accurata del riferimento non significa conoscenza causale o capacità di aprire la porta; servirebbero interventi controllati e test su ambienti nuovi. La valutazione off-policy casuale contro modello a parità di identiche condizioni **non è ancora implementata**: il confronto con azioni esplorative è solo osservazionale, senza conclusioni causali.

## Test
- JVM: integrità delle reti precedenti, controllo holdout/history serialize, indipendenza chiavi reset.
- Android: interfaccia di reset e analisi, manifest, comportamento al ritorno da activity, build.
- Test manuale S21 ancora necessario: salvataggio dopo pausa, reset singolo senza alterare reti altrui, reset TUTTO solo dopo prova dei singoli e consenso esplicito utente.

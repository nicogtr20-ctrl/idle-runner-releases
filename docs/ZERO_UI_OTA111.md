# ZERO 2.0 — HUD compatta, contatori energetici e tessuto

**13 ottobre 2026** — `feature/zero-ui-compact-energy-ota111`, destinazione `feature/overmind-inapp-updater`.

## Richiesta

La Home non deve mostrare sempre il muro di log tecnici. Un controllo **Dettagli avanzati** con freccia deve aprire/chiudere le righe tecniche. Lasciare visibili soltanto stato vitale, progressi, contatori essenziali e navigazione app. Contatori blu, rossi e viola cumulativi **di tutte le vite del profilo attivo**, non della vita corrente. Due barre: energia reale, resistenza tessuto realmente misurata rispetto al vento.

## Implementazione

- **GameActivity.java**: HUD disegnata su canvas a larghezza logica 480, header invariato (CERVELLO, AGGIORNA), tre indicatori cromatici, movimento, energia/tessuto, statistiche compatte, pulsanti invariati, menu avanzato espandibile a freccia. Nessuna modifica a cervelli, fisica, spawns o tempi. Nuova barra `shelter.integrity` usa esattamente il valore fisico già salvato e mostrato nel precedente testo TESSUTO.
- **Dettagli avanzati**: azione, presa, errore predizione, urti, tentativi manipolazione, richiami, estensione, porta, tocchi/adesioni, scarsità e materiali. Il vecchio punto invisibile per attivare prove stanza viene sostituito da `ATTIVA PROVA STANZA` esplicito quando la stanza non è presente.
- **EnergyLifetimeCounters.java**: `elt1` sul pref `energy_lifetime_types_v111` — numero di **contatti con energia effettivamente ricevuta**, non quantità di unità energetiche. Blu: `delta > 0` da risorsa ordinaria non rara. Rosso: `delta < 0` da risorsa dannosa. Viola: `delta > 0` da fonte con fisica `isRare()`, inclusa la fonte della stanza. Contatori separati da `energyEaten` preesistente, che non è riscritto. Si conta solo il delta effettivamente applicato alla riserva per non conteggiare bonus quando la riserva è già a 100.
- **Compatibilità**: se un profilo OTA110 non contiene ancora `elt1`, si importano i precedenti contatti dannosi noti e i contatti positivi del `LifeStatistics`, questi ultimi **non attribuiti erroneamente a blu o viola** perché l'archivio storico non conservava la loro distinzione. Vengono mostrati come `PRECEDENTI NON DIVISIBILI`. Se LifeStatistics era incompleto, si dichiara `STORICO ANTERIORE INCOMPLETO`. All'installazione, ogni nuova raccolta viene accumulata nel colore corretto e resta conservata in vite/slot futuri.
- Nessun nuovo campo o privilegio nell'input di `SensorimotorMind`. Tutto è contabilità visuale/diagnostica, non una ricompensa, nessuna conoscenza nascosta data a ZERO.
- `SaveSlotStore` cattura le preferenze della vita corrente e gli slot nominati includono automaticamente `energy_lifetime_types_v111`. CREA NUOVO ZERO cancella il working profile, che riparte con tutti i contatori a 0.
- Il caricamento di `grip1/grip2`, memoria ZERO2, statistiche e tutti gli altri sistemi resta invariato.

## Verifiche obbligatorie

- JVM: `EnergyLifetimeCounters.main` per migrazione, salvataggio, rifiuto di dati corrotti, nuova vita e conteggio per tipo.
- Android: `assembleDebug` e tutte le regressioni, più asserzioni statiche sulla UI e sulla persistenza.
- CI firmata: aggiornamento pin `GameActivity.java`, nuova classe, Java regression, verifica pulsante AGGIORNA e package originale.
- Pubblicazione: pipeline stabile, manifest JSON `main`, APK con SHA-256, deployment Vercel produzione READY, aggiornamento in-app; non dichiarare disponibile prima della verifica.
- Test visuale sul Galaxy S21 da confermare dopo installazione: overflow numeri grandi, dimensione reale dei controlli e apertura tendina.

## Limitazioni

- Non si possono ricostruire accuratamente i colori delle energie raccolte prima di OTA111 se i dati vecchi li aggregavano. Non inventare numeri storici; l'utente viene avvisato.
- Il tessuto NON è una stamina nuova da consumare: è la preesistente integrità simulata (danno vento, riparazione naturale).
- La UI non altera le decisioni di ZERO né migliora di per sé l'apprendimento.

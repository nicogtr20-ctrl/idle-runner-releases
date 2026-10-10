# OVERMIND ZERO — EXPERIMENT 0103: memoria episodica nella vera sequenza di gioco

**Data:** 10 ottobre 2026. **Stato:** correzione implementata, test JVM/staging Android superati, APK firmata 0.10.3-ota.51 pubblicata. Non esiste ancora una prova manuale del comportamento su Galaxy S21 dopo l'upgrade.

## Evidenza dall'utente e domanda
Screenshot della 0.10.2-ota.50 (il problema esisteva già almeno nella 0.10.1): **32.573 esperienze sensoriali, 32.663 decisioni, 171 dischi spostati, 124 unità energetiche raccolte, 663 tentativi**, ma la schermata ANALISI indica **0/24 ricordi, 0 esperienze registrate, 0 positive/negative, 0 richiami**. La domanda era se ZERO dovesse «scoprire» la memoria o se fosse un guasto. La registrazione è un meccanismo del software, non una capacità che il personaggio deve imparare ad attivare.

## Causa esatta nel codice
In `EpisodicMemory.action(...)` un'azione realmente eseguita viene aggiunta alla lista `recent` (massimo 36). `GameActivity`, a ogni transizione valida del `SensorimotorMind` (circa 0,65 s di simulazione), chiama `episodic.action(observedBefore,actionBefore)` e subito `episodic.consequenceSeparated(...)`. La 0.10.2 invocava incondizionatamente `recent.clear()` alla fine di `consequenceSeparated`, anche quando **non era avvenuto alcun evento significativo**. `storeEffect` richiede invece almeno **2 azioni reali**. Nel ciclo reale del gioco arrivava quindi sempre e soltanto 1 azione: nessuna memoria poteva essere creata.

I test precedenti chiamavano `action` molte volte consecutivamente e solo poi `consequence`, e per questo non riproducevano il problema del ciclo reale. La condizione di significatività `MIN_EFFECT=3` resta invariata; per energia positiva conta `min(delta,80)*0.25`, per danno `-min(-delta,80)*0.60`, per effetti fisici almeno 3U; contatto visivo con la vera fonte richiesto per episodi energetici.

## Correzione 0.10.3
- Rimosso **soltanto** il `recent.clear()` incondizionato di `consequenceSeparated`. Le valutazioni senza conseguenze mantengono il buffer cronologico degli ultimi 36 passi fino a nuovo aggiornamento, senza creare falsi eventi.
- La lista resta strettamente limitata in memoria da `action()`: `if(recent.size()>=DEPTH)recent.remove(0)`.
- `resetLife()` continua a cancellare soltanto la cronologia temporanea alla morte fisica, **mai** `events` memorizzati. Il vecchio metodo `consequence()` per test storici resta invariato.
- Contesti di danno e movimento fisico nello stesso intervallo restano separati (`em3`, energia `kind=1` con firma osservata, spostamento `kind=2`). Nessun comando innato, orientamento a risorse, categoria porta o obiettivo noto aggiunto.
- Identici `applicationId=com.overmind.zero.ota`, firma stabile, formato `episodic_memory_v095`, salvataggi a 12 slot `zero_save_slots_v100`, formato mondo `wm1`, `ExperienceCredit ec2`, motori, membrane e planner 0.10.2. Nessun reset e nessuna migrazione distruttiva.

## Verifiche automatiche realmente eseguite
- Nuovo test in `EpisodicMemory.main()`: **44 passi** con esito nullo e verifica che il buffer resti a 36, senza ricordi fittizi; poi +24 di energia reale visibile (ricordo favorevole), 10 passi neutrali e -7 di danno visibile (ricordo dannoso). Inoltre morte: buffer svuotato ma episodi conservati; serializzazione `em3` e `restore` mantengono i due ricordi.
- Staging GitHub Actions `38055704183`: **SUCCESS**, test JVM su tutti i cervelli e compilazione Android.
- Stable publisher `38055836537`: **SUCCESS**, test JVM e build Android, firma privata costante, Release pubblicata prima del manifest, SHA-256 verificato, manifest `main/updater/public/latest.json` aggiornato.
- Release: [0.10.3-ota.51](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.10.3-ota.51), versionCode **1051**, **129.180 byte**, SHA-256 `3bcf4c2674c73a7de134158fea3064e5b31b953d899ba96af4344f2b6fb8a522`.
- Verifica hosting Vercel del 10 ottobre: `/latest.json` restituisce **HTTP 200** versione 1051 e digest esatto; `/apk/overmind-zero-1051-3bcf4c2674c7.apk` restituisce **HTTP 200**, 129.180 byte. Il telefono non è stato ancora testato.

## Test manuale prioritario Galaxy S21
1. **Non disinstallare e non azzerare**; se necessario conservare lo ZERO attuale in uno dei dodici slot, poi premere AGGIORNA. Non è possibile ricostruire eventi mai registrati dalla 0.10.1/0.10.2.
2. Dopo l'upgrade controllare versione **0.10.3-ota.51**.
3. Lasciare ZERO vivere per alcune decine di vere decisioni, poi osservare nuovi eventi verificati (disco spostato, presa che fa muovere la porta, contatto energetico con fonte percepita); aprire ANALISI e verificare `Esperienze registrate nel tempo`, `Esperienze significative`, tipo favorevole/dannoso e `Lunghezza ultimo ricordo`.
4. Confrontare con 0.10.2: la nuova generazione di ricordi **non implica** che ZERO li userà bene, né che entrerà nella stanza. Verificare richiamo dopo morte, attribuzione corretta se contemporaneamente tocca il rosso e nuove capacità con A/B separati.
5. Se i contatori restano a zero non azzerare tutto: rilevare screenshot dell'HUD e ANALISI, versione installata, energia al contatto e `dischi spostati`, poi investigare `validWeights`, visibilità `seenSource`, soglia `MIN_EFFECT` e persistenza.

## Handoff
Repository `nicogtr20-ctrl/idle-runner-releases` PRIVATO, codice Android stabile `feature/overmind-inapp-updater`; staging prova `feature/overmind-v103-episodic-memory-retention`; documentazione e distribuzione Vercel/GitHub nel branch `main`. Leggere sempre `AGENTS.md`, `OVERMIND_START_HERE.md`, `docs/HANDOFF.md`, esperimenti 0100–0103. Non presentare CI come prova di intelligenza su dispositivo, non cambiare package, firma, salvataggi, motori senza test.

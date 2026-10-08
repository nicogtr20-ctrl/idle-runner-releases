# HANDOFF — punto di ripresa per un'altra chat o agente

**Ultimo aggiornamento:** 2026-10-09

### Riferimenti
Repository: `nicogtr20-ctrl/idle-runner-releases` (privato).
- Branch `main` = Idle Runner + `docs/` + infrastruttura updater; **NON** il codice OVERMIND attuale.
- Branch `experiment/overmind-zero-world-v03` = ultima versione OVERMIND realmente provata dall'utente.
- Branch `experiment/overmind-zero-physics-v04` = creato, NON procedere sul gameplay prima che updater sia funzionante.
- `experiment/overmind-zero-brain-v02` = esperimento motorio originale.

### Esperienze e feedback utenti
L'utente ha osservato ZERO-001: una forma nera deformabile, il nucleo spinge sulla membrana con un movimento arcuato, la membrana si gonfia, ZERO raggiunge effettivamente il punto stimolo. Dopo ~2.467 episodi benchmark -3,6→+179,7. ZERO-002 ha registrato 4.772 tentativi, 49 contatti, 3/3 tipi, benchmark +2,9→+204,4. Ha richiesto corpi tipo ferrofluido reale, nessuna appendice prefissata, scoperta autonoma del movimento e delle funzioni degli oggetti.

### Aggiornamento infrastruttura (9 ottobre 2026)
- Vercel **online**: https://overmind-zero-updates.vercel.app/latest.json (HTTP 200, `available:false`).
- Vercel project `overmind-zero-updates` `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, team `prime-1323`, root `updater/`, branch di produzione `main`.
- Codice del pulsante e downloader: branch `feature/overmind-inapp-updater`; package della nuova app **separata** `com.overmind.zero.ota`, per conservare la precedente `com.overmind.zero.world` con i suoi dati.
- Workflow firma/pubblicazione: `.github/workflows/overmind-ota-publish.yml`; richiede il secret unico `OVERMIND_SIGNING_BUNDLE` documentato in `docs/UPDATES.md`. Senza secrets CI compila ma non rilascia APK.
- La v0.4 del gameplay resta sospesa fino a prova updater Android su S21. Il client updater Android si trova nel branch `feature/overmind-inapp-updater`, ancora da provare sul telefono.

### Decisione attuale
**FERMARE LA v0.4 DI GAMEPLAY. PRIMA:**
1. Documentazione GitHub permanente.
2. Vercel come endpoint di aggiornamenti e distribuzione delle APK.
3. Pulsante Aggiorna nell'app. Android chiede il consenso alla schermata d'installazione.
4. Firma persistente per ogni build e sicurezza integrità.
5. Prova reale sul Galaxy S21. Se cambio package name o certificato, non è aggiornamento in-place.

### Limite Android / sicurezza
Le release debug di GitHub Actions spesso usano certificati diversi. È irrealistico garantire aggiornamento in-place su APK già installate senza stessa firma; in quel caso servirà un'ultima installazione pulita (preservare prima salvataggi, se necessario). Non inserire credenziali GitHub, password signing o chiavi in APK o repository. Il repo è privato, mentre Vercel e gli APK pubblicati su CDN possono essere accessibili pubblicamente — valutare prima la diffusione.

### Per una nuova chat
Scrivere: «Apri su GitHub `nicogtr20-ctrl/idle-runner-releases`, leggi `OVERMIND_START_HERE.md` e `AGENTS.md`, riprendi OVERMIND dalla sezione HANDOFF e continua la priorità OTA senza modificare Idle Runner.»


### OTA deployment verificato (2026-10-09)
- Vercel: https://overmind-zero-updates.vercel.app/latest.json (manifest pronto, production pubblica; preview protette).
- Base installabile: 0.3.6-ota.8 / code 1008, file: https://overmind-zero-updates.vercel.app/apk/overmind-zero-1008-118f0983c864.apk
- Nuovo update: 0.3.7-ota.10 / code 1010, file: https://overmind-zero-updates.vercel.app/apk/overmind-zero-1010-7f012383aee3.apk
- Firmware Android: build Release con certificato costante e numeri di versione crescenti; pipeline GitHub riuscita.
- Branch Android: feature/overmind-inapp-updater; package com.overmind.zero.ota. Prima installazione separata dalle app storiche.
- Test sul Galaxy S21 PENDENTE: installare 1008, premere AGGIORNA, consentire l'installazione Android, verificare codice 1010 e preservazione salvataggi. Nessuna installazione silenziosa.
- NON procedere con v0.4 gameplay finché il test non è superato.

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


### Fix verificato — 2026-10-09: pulsante AGGIORNA, errore Permission denied
- Galaxy S21 screenshot su 0.3.6-ota.8: `IMPOSSIBILE AGGIORNARE: Permission denied (missing I...)`. L'app NON ha richiesto permessi perché **la Manifest Android del bootstrap mancava completamente** di `android.permission.INTERNET`, `android.permission.REQUEST_INSTALL_PACKAGES`, provider `UpdateFileProvider`.
- Root cause verificata nel codice del branch `feature/overmind-inapp-updater`; corretto `app/src/main/AndroidManifest.xml`.
- CI ora controlla `aapt dump permissions` per entrambe le autorizzazioni e `aapt dump xmltree` per il provider nel pacchetto Release **già compilato**; test superati.
- **Recovery manuale una volta:** 0.3.7-ota.12 / versionCode 1012, https://overmind-zero-updates.vercel.app/apk/overmind-zero-1012-4e2a741614d0.apk (firma stabile, stesso package `com.overmind.zero.ota`, installare sopra app esistente SENZA disinstallarla per mantenere il salvataggio).
- **OTA test pronto:** 0.3.8-ota.13 / versionCode 1013, https://overmind-zero-updates.vercel.app/apk/overmind-zero-1013-b98857c77d5a.apk. Manifest https://overmind-zero-updates.vercel.app/latest.json → 1013 HTTP 200, APK 200.
- **Da verificare sul telefono:** dopo installazione 1012, premi AGGIORNA, autorizza "Install unknown apps" per OVERMIND se richiesto, torna nell'app, ripremi AGGIORNA e conferma la schermata Android, controlla versione 0.3.8-ota.13 e dati persistenti.
- INTERNET è un permesso normale e non genera una richiesta runtime. L'installazione da origini esterne è invece confermata dal sistema. Gli aggiornamenti silenziosi senza conferma Android NON sono promessi.
- v0.4 gameplay resta fermo fino a test completato.

### 2026-10-09 — OTA confermato sul Galaxy S21
- L'utente conferma esplicitamente: «Ok fatto funziona» dopo installazione correttiva 0.3.7-ota.12 e prova del pulsante **AGGIORNA** alla 0.3.8-ota.13.
- Aggiornamento Android in-app **verificato dall'utente sul dispositivo**, compresa la procedura di installazione. Non è stata eseguita una verifica indipendente del numero di versione né della persistenza dei dati; chiedere screenshot in caso di anomalie.
- L'utente chiede se può eliminare vecchie app: sì, quelle con applicationId distinti (v0.1, ZERO-001 `com.overmind.zero.brain`, ZERO-002 `com.overmind.zero.world`) possono essere disinstallate, **ma la disinstallazione cancella i loro dati di apprendimento**. Conservare `com.overmind.zero.ota` e aggiornarla in-place; i pesi delle app precedenti non sono migrati automaticamente.
- **Prossimo passo prioritario:** riprendere sviluppo ZERO-003/v0.4 sul codice OTA stabile con collisioni fisiche e apprendimento di manipolazione; mantenere firma, package e salvataggi.

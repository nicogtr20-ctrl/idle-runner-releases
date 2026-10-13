# ZERO21 validazione (13 ottobre 2026): leggere anche [docs/ZERO21_VALIDATION_RESULTS.md](docs/ZERO21_VALIDATION_RESULTS.md) per risultati REALI GitHub, limiti e gate prima di ogni eventuale pubblicazione. Modulo nuovo ancora SHADOW; non autorizzare merged/release per compilazione soltanto.

# Priorità nuova chat — ZERO 2.1 (13 ottobre 2026)

Se il task riguarda il cervello integrato leggere PRIMA e integralmente: [HANDOFF ZERO21](docs/ZERO21_HANDOFF_NEXT_CHAT.md), [ARCHITETTURA ZERO21](docs/ZERO21_UNIFIED_COGNITION_MASTER.md), [LOG E TEST ZERO21](docs/ZERO21_IMPLEMENTATION_LOG.md). Branch ricerca `feature/zero21-unified-cognition`; baseline stabile OTA112 (`feature/overmind-inapp-updater`). NON confondere il ramo di ricerca con l'APK installabile, NON unire in blocco il vecchio ZERO20 divergente e non presumere una prova di intelligenza solo perché CI è verde. QA iniziale [38452161103](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38452161103) SUCCESS su codice testato `7361e67`. Tutti i restanti paper e vincoli di sicurezza restano in vigore.

---

# Istruzioni permanenti agli assistenti che lavorano su questo repository

**PRIMA DI QUALSIASI MODIFICA** apri `OVERMIND_START_HERE.md`, `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/CHANGELOG.md`, `docs/ROADMAP.md`, `docs/HANDOFF.md`, `docs/UPDATES.md`.

## Ambito
- `main` contiene ancora Idle Runner e la documentazione condivisa. Non sovrascrivere Idle Runner e non convertire `main` in OVERMIND senza richiesta esplicita.
- OVERMIND ZERO attuale: `experiment/overmind-zero-world-v03`; seguire `docs/HANDOFF.md` per identificare la versione più recente.
- L'utente vuole una vera simulazione di apprendimento (limiti dichiarati onestamente), corpo nero fluido, nessuna animazione prefabbricata di zampe, nessuna conoscenza implicita degli oggetti, osservatore invisibile e interventi indiretti.
- Il cervello motorio iniziale è `MotorBrain.java` (62 parametri, ring di 10 punti fisici); `UnknownWorld.java` conserva preferenze apprese per segnali osservati. Non descrivere questi sistemi come AGI/coscienza.
- Preserva sempre l'apprendimento salvato e considera versionCode, package name, firma APK e migrazione dati; non cancellare memorie senza consenso.
- Il progetto è Android Java, Gradle, GitHub Actions; Vercel serve esclusivamente il manifest e, una volta configurata la distribuzione, gli APK.
- Non committare keystore, password, token, credenziali o dati sensibili.
- Per ogni versione: eseguire test JVM e build Android, annotare prove reali vs ipotesi, aggiornare CHANGELOG / ROADMAP / WHITEPAPER (se cambiano obiettivi) e pubblicare una release verificata.
- Aggiorna sempre `docs/HANDOFF.md` con branch, tag, stato firma/OTA, problemi aperti e prossimo test. Non affermare che un aggiornamento automatico funziona senza test APK→APK su dispositivo.
- Per Android non è consentito un aggiornamento APK silenzioso ordinario: l'utente dovrà confermare la schermata di installazione. La chiave di firma deve restare identica tra le versioni.


## Nuova lettura obbligatoria per 0.9.5
- Leggere **`docs/EXPERIMENT_0095.md`** dopo `OVERMIND_START_HERE.md`; contiene le osservazioni concrete del Galaxy S21, il confronto fra 0.9.1 e 0.9.5, il motivo della memoria episodica, le chiavi SharedPreferences, le richieste dell'utente e il protocollo di verifica.
- Ogni nuova feature impatta il paper: aggiornare `docs/WHITEPAPER.md`, `docs/ARCHITECTURE.md`, `docs/ROADMAP.md`, `docs/CHANGELOG.md`, `docs/HANDOFF.md` e `OVERMIND_START_HERE.md` insieme al codice e mantenere **copie leggibili sul branch `main`**.
- Non confondere predizione del mondo, replay episodico e pianificazione: al 10 ottobre v0.9.5 non ha immaginazione multistep generale. Le statistiche 268 prese, porta max 111U, 0 stanze risolte sono osservazioni, non dimostrazione di “capire la porta”.
- Non inventare continuità di strategie prima di una verifica APK reale. Non cambiare il package `com.overmind.zero.ota` né la firma degli upgrade.

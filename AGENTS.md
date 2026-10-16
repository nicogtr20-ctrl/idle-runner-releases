<!-- Current authoritative status; older sections below are historical. -->
# Current release status — ZERO171, 16 October 2026 — AUTHORIZED, VALIDATING

Official installed/public baseline: **OTA169 / 1169**. Current correction branch:
`fix/zero171-integrated-sensorimotor-corrections` from unpublished ZERO170 `249563fd`.
Read [ZERO_171_INTEGRATED_SENSORIMOTOR_CORRECTIONS.md](docs/ZERO_171_INTEGRATED_SENSORIMOTOR_CORRECTIONS.md) for the complete current architecture, all thirteen audit findings, fixes, optional persistence keys, tests and remaining device validation.
**User explicitly authorized source publication and signed APK OTA publication on 16 October 2026: “Si autorizzo. Pubblica anche apk tramite ota”.** Complete release checks, promote the reviewed sources to the stable source branch, preserve the installed signing identity and named slots, then verify the public manifest and APK. Keep Idle Runner sources on main intact. OTA169 remains the published baseline until the next release is verified. Earlier no-publication instructions below are historical and superseded by this explicit authorization.

One active RecurrentCoreV22 + actual 724-synapse motor head; corrected panel/concave geometry, fixed 1/60s integration, context-conditioned movement, physically attributed motor learning, distinct verified manipulation memory and genuine current-symbiote comparison. No scripted gait or food target, no forced confidence, no reset of existing brains/slots. Passing tests is not proof of improved autonomous behavior on the Galaxy S21.

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

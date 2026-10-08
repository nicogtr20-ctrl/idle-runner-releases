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

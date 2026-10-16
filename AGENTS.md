<!-- Current authoritative release status; older sections below are historical. -->
# Verified release — OTA170 / Android1170, runtime ZERO171 — 16 October 2026

The user explicitly authorized source and signed APK OTA publication. **Published and downloaded from BOTH Vercel and GitHub: `0.11.6-ota.170`, versionCode `1170`, package `com.overmind.zero.ota`.** The runtime correction revision **171** is not the OTA number. Install through **AGGIORNA**, over the existing app; do not uninstall or reset saved brains/slots.

APK: 497912 bytes, SHA256 `5fd2bb087e55fe7eae4d5b74e5ce7b7c31106bf71eb2077f6c1ee4ed915ba02e`. Original stable certificate SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583` matched; APK v2 signature AND signed content digest independently verified on both public downloads. Both manifests and exact bytes match, HTTP200. Main Idle Runner app/build/settings remain unchanged.

[Release](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.170) · [successful signed publisher](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38926908265) · [integrated source review #83](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/83).

Read [ZERO171 integrated correction and release evidence](docs/ZERO_171_INTEGRATED_SENSORIMOTOR_CORRECTIONS.md) for all audit findings, integrated fixes, optional persistence keys, verification and behavioral limits. 96 JVM entrypoints, seven staging integration gates, 34 publisher source gates, Gradle debug/release builds and original signing identity passed. One living recurrent cortex and 724 motor weights; no authored gait, food coordinate, forced confidence or brain reset. **Improved autonomous locomotion/food seeking on the Galaxy S21 is still unproven.** Preserve that distinction in future work. Earlier publication holds and version/status claims below are superseded by this verified release.

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

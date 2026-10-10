# OVERMIND ZERO — aggiornamenti Android / OTA

## Invarianti
- Package Android: `com.overmind.zero.ota`.
- Gli aggiornamenti devono essere installati **sopra** l'app esistente; non disinstallare se si vogliono conservare i dati.
- La firma APK deve restare la stessa fra versioni. Keystore e credenziali non devono mai entrare nel repository.
- Android ordinario richiede conferma utente per installare l'APK; nessun aggiornamento silenzioso viene promesso.
- Il publisher firmato ufficiale è `.github/workflows/overmind-ota-publish.yml` sul branch `feature/overmind-inapp-updater`.
- Prima di dire che il pulsante AGGIORNA offre una versione, verificare realmente manifest e APK pubblici.

## Stato 0.10.5 — 10 ottobre 2026
La modifica “STATISTICHE 25-vs-25 + storico lifetime” è validata solo in staging sul branch `feature/overmind-v105-lifetime-statistics` (run 38059707433 verde). Finché non viene incorporata nello stable e non passa il publisher firmato, non presentarla come OTA disponibile.

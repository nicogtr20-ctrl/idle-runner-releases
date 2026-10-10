# OVERMIND ZERO — aggiornamenti Android / OTA

## Invarianti
- Package Android: `com.overmind.zero.ota`.
- Gli aggiornamenti devono essere installati **sopra** l'app esistente; non disinstallare se si vogliono conservare i dati.
- La firma APK deve restare la stessa fra versioni. Keystore e credenziali non devono mai entrare nel repository.
- Android ordinario richiede conferma utente per installare l'APK; nessun aggiornamento silenzioso viene promesso.
- Il publisher firmato ufficiale è `.github/workflows/overmind-ota-publish.yml` sul branch `feature/overmind-inapp-updater`.
- Prima di dire che il pulsante AGGIORNA offre una versione, verificare realmente manifest e APK pubblici.

## Stato 0.10.5-ota.54 — 10 ottobre 2026
La modifica “STATISTICHE 25-vs-25 + storico lifetime” è nello stable `feature/overmind-inapp-updater`.
- Staging QA: **38059707433**, verde.
- Publisher firmato: **38060026469**, verde.
- versionCode **1054**, versionName **0.10.5-ota.54**.
- SHA-256: `34bd2eb5261f499b196037832dce744f37b7b3822a49a7f655c3b6347f0c4e7a`.
- APK: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1054-34bd2eb5261f.apk`, **138.636 byte**.
- `https://overmind-zero-updates.vercel.app/latest.json` e APK verificati HTTP 200 dopo la pubblicazione.
È quindi corretto usare **AGGIORNA** nell'app. Installare sopra la versione esistente; non disinstallare.

## Stato 0.10.6-ota.55 — verificato
La memoria di lavoro temporale ha superato staging **38064278116** e **38064377747** e publisher firmato **38064599838**.
- versionCode **1055**, versionName **0.10.6-ota.55**.
- SHA-256 `bdc0dc5fd1dc9dce992d5a9cf139efd560421091f8ada01cec1bcbe47b48ff30`.
- APK: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1055-bdc0dc5fd1dc.apk`, **143.732 byte**.
- Manifest e APK verificati HTTP 200.
È corretto usare **AGGIORNA** nell'app. Installare sopra la versione esistente; non disinstallare.

## Stato 0.10.7-ota.56 — verificato
Skill temporali autonome validate su staging **38065646624** e publisher firmato **38066032322**.
- versionCode **1056**, versionName **0.10.7-ota.56**.
- SHA-256 `a433978f842054c71c2744075f3c5a0105a4bad2f825b7ba7d6a7d1629e2030e`.
- APK: `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1056-a433978f8420.apk`, **149.392 byte**.
- Manifest e APK verificati HTTP 200.
È corretto usare **AGGIORNA** nell'app. Installare sopra la versione esistente; non disinstallare.

## 0.10.8-ota.57 — disponibilità verificata 10 ottobre 2026
Pubblicazione stable GitHub Actions 38067752564 SUCCESS, code 1057, identità com.overmind.zero.ota invariata, APK 150816 byte SHA256 50b45e37a1cc7d801408c0800000a0f957ea2c89159afbff4fb0ba22df3eddc3. Latest manifest pubblico https://overmind-zero-updates.vercel.app/latest.json HTTP200 versione 0.10.8-ota.57, e https://overmind-zero-updates.vercel.app/apk/overmind-zero-1057-50b45e37a1cc.apk HTTP200 content-length 150816. In-app AGGIORNA su Galaxy S21 ancora da provare, installare in-place senza disinstallare, salvataggi invariati. Fix dettagliato docs/EXPERIMENT_0108.md.

## 0.10.9-ota.58 / OTA recuperato e verificato — 10 ottobre 2026
Signed GitHub Actions publisher 38069513724 SUCCESS, versionCode 1058, versionName 0.10.9-ota.58, package `com.overmind.zero.ota`, APK firmato 155816 byte, SHA256 `9bad1270ef3dae172858d55a1f90e3d92cc44c89e1559c9b18ce508f94b933e5`. Release `overmind-zero-v0.10.9-ota.58`. Il manifest su `main` era già 1058 mentre Vercel pubblico era ancora 1057 e nuovo APK dava HTTP404. Creato deploy production Vercel da commit Git esatto `fb8920cc338e85f8382806837ba33c5db293858f`, ID `dpl_CHFimoyKSiLUR7mAn587GexvZypS` READY/alias `overmind-zero-updates.vercel.app`. Nuova prova finale: `https://overmind-zero-updates.vercel.app/latest.json` HTTP200, versione 1058 e sha corretto; `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1058-9bad1270ef3d.apk` HTTP200, content-length 155816 e MIME Android. Il pulsante AGGIORNA e la nuova percezione devono ancora essere verificati sul Samsung Galaxy S21. Non disinstallare e non resettare. Paper docs/EXPERIMENT_0109.md.

## OVERMIND 0.11.0-ota.60 / verified OTA — 10 ottobre 2026
Signed workflow `38071321701` SUCCESS, code1060, versionName `0.11.0-ota.60`, APK `162004` byte, SHA256 `076d315c484cd11d695a40219a26a4a30508de1478a3a31b5abee5a488fee84e`. `main/updater/public/latest.json` publica stessa firma/dimensione. Vercel `https://overmind-zero-updates.vercel.app/latest.json` HTTP200 con codice1060, `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1060-076d315c484c.apk` HTTP200 length 162004 e MIME applicazione Android. Package originale `com.overmind.zero.ota` e firma invariati. Aggiornare in-place, **non disinstallare o azzerare**. Da testare sul Galaxy S21 installazione via AGGIORNA, memoria spaziale, 12 slot e ripresa da checkpoint. Paper `docs/EXPERIMENT_0110.md`.

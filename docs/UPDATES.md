# OVERMIND ZERO — aggiornamenti Android / OTA

## Stato 12 ottobre 2026 — ZERO 0127

Il branch `feature/zero-0127-spatial-persistence-box-recovery` contiene modifiche sperimentali di ripristino fisica, memoria e bilanciamento. **Non esiste ancora una release APK firmata/verificata per ZERO 0127**. Conservare OTA 97 e gli slot nominati; non disinstallare, non azzerare. Prima della pubblicazione bisogna superare PR QA, Android debug, publisher firmato, verifica SHA-256 ed endpoint Vercel. Le bozze e i merge senza prova non sono release.

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

## Release preparation — ZERO 2.0 passive observation, OTA 100 candidate (2026-10-13)

Official OTA path is `feature/overmind-inapp-updater` → `.github/workflows/overmind-ota-publish.yml` → owner-key signed APK → GitHub Release → `main/updater/public/latest.json` and immutable APK → Vercel manifest consumed by in-app AGGIORNA. This candidate is staged separately from research PR #31. Original `com.overmind.zero.ota` signature and persistent 12 saved slots must remain intact. VersionCode derives from the next official publisher run number (expected ≥1100), not the old signed experimental preview 1099.

The candidate includes exactly the already tested v20 passive observers, existing base motor policy, and `TelemetryGitHub.partition()` fix preserving `extras.zero20Passive` after multipart upload. No model advice biases the motor controller; no change to energy rules or sensor physics. Source-pinned stable checks are UPDATED to the SHA of the actually reviewed source files, never disabled; five new JVM regressions are added before official release signing.

The initial Galaxy S21 report confirms the original working profile is still present and v20 models have collected real observations. No independent skill or survival improvement is established. Release acceptance: independent staging JVM/Android QA GREEN, official stable publisher GREEN, signed APK certificate/package/version and SHA verified, downloadable manifest/APK responsive, user-controlled install and slot continuity confirmed on phone. If any gate fails, **do not claim availability through Aggiorna**.

## ZERO 2.0 — Box-grip retention candidate (13 ottobre 2026)

The OTA102 phone reports showed rapid accepted mutations of all hand/membrane weights with no box-specific skill retention. This candidate adds *real physics measured* box-touch and simultaneous opposing-grip evidence to the live online trial evaluator. When a previous hand/membrane trial demonstrably sustained a box grip, a replacement cannot win only by collecting energy or exploring elsewhere; it must show comparable valid box-contact/grip evidence. No prerecorded grip actions, free energy, changes to the bilateral box law, or forced motor direction. Earlier controller save formats and named slots are unchanged. Research [BoxGripRetentionRegression] tests baseline-with-proven-hold vs an exploratory but unproven replacement and preservation of valid alternative learning. Real-world effect not yet established; on-device A/B and saved profile verification pending. Release only after full staging Java+APK QA, official publisher signed gates, manifest, and in-app installer. Research details: `docs/ZERO_20_INTELLIGENCE_RESEARCH.md` in `feature/zero-20-hierarchical-intelligence`.

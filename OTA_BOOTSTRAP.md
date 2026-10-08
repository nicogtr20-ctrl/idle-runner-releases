# Android updater bootstrap for OVERMIND ZERO

This feature branch is the future OTA-enabled Android application. Its separate application ID is `com.overmind.zero.ota` so the previously installed v0.3 experiment remains installed with its original learning data.

## UI behavior
- Tap **AGGIORNA** near the top-right of the game.
- Fetch `https://overmind-zero-updates.vercel.app/latest.json` over HTTPS.
- If `available:false`, show "Nessun aggiornamento disponibile" and do nothing.
- If a newer compatible version exists, fetch only from the official Vercel hostname, validate SHA-256 and byte size, open Android package installer with a read-only ContentProvider URI.
- Android requests confirmation before installation. This is **not** a silent/self-executing APK replacement.
- Under Android 8+, first-time users may need to grant "Install unknown apps" permission to OVERMIND.
- The APK and manifest on Vercel must be public to anonymous Android devices, but the GitHub source stays private.

## Stable signing requirement (BLOCKING)
No credible in-place update is possible until all builds of the `.ota` app use the same signing key. We do not store signing material in GitHub source.
GitHub Actions repository **Secrets**:
- `OVERMIND_KEYSTORE_B64` (base64 encoding of a private .jks keystore)
- `OVERMIND_KEYSTORE_PASSWORD`
- `OVERMIND_KEY_ALIAS`
- `OVERMIND_KEY_PASSWORD`

The signing key MUST be backed up securely; losing it prevents future updates to installed builds. Do not distribute secrets in conversations or commit to Git.
The GitHub Action will **not publish an update** until configured. Smoke/debug builds alone cannot guarantee future installability.

## Existing v0.3 knowledge
The old `com.overmind.zero.world` app stores brain and world memory locally; it is NOT uninstalled or modified when installing the new `.ota` bootstrap. The first `.ota` install starts its own model. Later signed updates of `.ota` retain its own data.

## Main update endpoint
Vercel project: `overmind-zero-updates` (team `prime-1323`).
GitHub repo `main`, root `updater`, serves `/latest.json`.
Future signed builds publish immutable files under `updater/public/apk/` and update manifest only after signing/testing succeed.

## Validation
Build with Gradle; verify appId, target code and SHA-256; install bootstrap; publish a test signed increment; press Aggiorna; grant Android installer approval; verify version and preserved SharedPreferences.

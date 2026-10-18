# OVERMIND ZERO 3.0 — CANALE OTA STABILE (V358+)

## Decisione dell'utente 18/10/2026
Riprendere a distribuire gli aggiornamenti **direttamente da "Aggiornamenti" del gioco originale**, non APK candidati scaricati manualmente. Il cervello attivo rimane 3.0 GRU, nessun ritorno al 2.2.

## Fonte di rilascio
- **Branch di pubblicazione:** `release/zero30-ota`
- **Workflow di pubblicazione:** `.github/workflows/zero30-stable-ota-channel.yml`
- **Evidenze obbligatorie PR #91:** confronto fisico A/B guadagno 1.0 / 2.4, app originale + GRU + quattro zone mentre lo stesso corpo è vivo + morte/rinascita + checkpoint + riavvio.
- **Non eseguire** il vecchio publisher `.github/workflows/overmind-ota-publish.yml` sulla base 2.2 per gli aggiornamenti della serie 3.0; non usare il branch di release V2.2 come sorgente.
- **Destinazione OTA:** `main:updater/public/latest.json` e `main:updater/public/apk/<hash>.apk`, distribuiti via `https://overmind-zero-updates.vercel.app/latest.json`.
- **Fallback:** GitHub Release immutabile `overmind-zero-v<versionName>`, asset `OVERMIND-ZERO.apk`; l'app cerca sia manifest Vercel che manifest GitHub raw.
- **VersionCode:** V357 candidato già installato su Galaxy S21 = 1175. Prima OTA V358 = **1176**, versionName `3.0-ota.358`. Per OTA successive usare un codice maggiore dell'ultima V358 e dell'ultimo `main/updater/public/latest.json`, mai pari o inferiore.
- **Identità firma:** `com.overmind.zero.ota`, SHA256 certificato `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- **Migrazione:** installazione aggiornamento sopra V357 senza disinstallare, senza cancellare dati e senza resettare cervello o profili.
- **Immutabili:** grafica originale, menu originali (l'unica nuova vista è 3D neuroni), fisica, muscoli 680/210, collisioni, casse, stanza, vite, seriale delle vite distinto dai cambi di zona.

## Modifica V358
`Zero3LearningFoundation.AdaptiveCortex.command` usa `tanh(2.4*(z+noise))` al posto di `tanh(z+noise)`: cambia **solo** l'escursione del comando motore neurale, non il limite [-1,1] né la forza della fisica. Nessuna andatura preprogrammata. A/B: 1200 decisioni fisiche a gain 1.0 (V357) vs gain 2.4 (V358), ciascuno learned/frozen; con guadagno2.4 prima contrazione estensione 0.1258 vs 0.0527, impulso ~131 vs ~57; nessuna dimostrazione di migliori abilità autonome sul Galaxy S21 ancora.

## Gate di pubblicazione obbligatori
1. `original-app-compile` verde.
2. `motor-ab-and-android` verde.
3. APK release firmato e verificato, pacchetto e permessi aggiornamenti corretti.
4. SHA256 esatto tra APK firmato e manifest pubblico.
5. Release GitHub immutabile e manifesto `main` aggiornato solo se versionCode è maggiore.
6. Verifica URL Vercel su HTTPS, poi prova installazione diretta su Galaxy S21 e confronto salvataggi.

Non chiamare completata la consegna OTA finché `main` e gli endpoint visibili pubblicamente non espongono la versione 1176. Se la CI non riesce a pubblicare, segnalare il blocco senza affermare che è pubblicato.

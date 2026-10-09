# CHANGELOG — storico OVERMIND ZERO

> Le versioni seguenti sono *esperimenti*, non un prodotto finito. Aggiornare questa pagina in OGNI release insieme al file HANDOFF.

## 2026-10-09 — Infrastruttura aggiornamenti (in preparazione)
- Stop temporaneo al ramo gameplay `experiment/overmind-zero-physics-v04`.
- Creati whitepaper permanente, architettura, changelog, handoff, AGENTS.md e roadmap.
- Vercel attivo: `https://overmind-zero-updates.vercel.app/latest.json`, risposta 200 con `available:false`.
- Client Android con pulsante Aggiorna, download HTTPS/SHA-256, installer con autorizzazione Android: branch `feature/overmind-inapp-updater` (build da validare).
- Nuovo applicationId `com.overmind.zero.ota` per non toccare i dati locali delle APK sperimentali già installate.
- Pulsante **Aggiorna** e download + controllo SHA-256 + installazione con conferma Android implementati sul branch `feature/overmind-inapp-updater` (build CI).
- Configurazione della firma permanente richiede il secret `OVERMIND_SIGNING_BUNDLE`; senza quello il server resta disabilitato. Evitare promesse di aggiornamenti silenziosi.

## ZERO-002 / v0.3.5 — 2026-10-09
- Branch `experiment/overmind-zero-world-v03`.
- Release privata `zero-world-v0.3.5` / APK `OVERMIND-ZERO-WORLD-v0.3.apk`.
- Tre classi di oggetti sconosciuti e memoria associativa degli effetti del contatto; nuova app `com.overmind.zero.world`.
- Test JVM e build riusciti; test sul telefono: **4.772 tentativi motor learning, +2,9→+204,4 su benchmark, 49 contatti, 3/3 tipi incontrati**, energia 99/100.

## ZERO-001 / v0.2.2 — 2026-10-09
- Branch `experiment/overmind-zero-brain-v02`.
- Release privata `zero-brain-v0.2.2`.
- Rete neurale vergine da 62 parametri, simulazione meccanica su punti elastici, allenamento per neuroevoluzione e benchmark.
- Fix background / persistenza.
- Test sul telefono: 2.467 tentativi, benchmark -3,6→+179,7; l'utente conferma che il corpo si deforma e raggiunge il bersaglio.

## Prototipo precedente / v0.1.2 — 2026-10-09
- Branch `experiment/overmind-zero-v01`; release `overmind-v0.1.2`.
- Simulazione top-down con organismi, energia, memoria spaziale rudimentale, preferenze tramite Q-learning, XP e modifiche al mondo.
- Sostituito sperimentalmente: comportamento troppo preimpostato e creatura graficamente troppo simile a ragno prefabbricato.

## Prima di OVERMIND
- Branch principale contiene ancora il progetto distinto **Idle Runner**; non mescolarlo.

## OTA 0.3.6-ota.8 — 2026-10-08T22:43:28.351618+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1008).
- SHA-256: `118f0983c8644aa5f628db52dea1a813b84cdd6043e11c4aced881a50fd405c2`.
- APK: `/apk/overmind-zero-1008-118f0983c864.apk`.

## OTA 0.3.6-ota.9 — 2026-10-08T22:45:54.022194+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1009).
- SHA-256: `d97b478825d66767c02003fd1c229ce84b0e03d2058be731ecdb91ab5138a21c`.
- APK: `/apk/overmind-zero-1009-d97b478825d6.apk`.

## OTA 0.3.7-ota.10 — 2026-10-08T22:47:14.160606+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1010).
- SHA-256: `7f012383aee3b29c0cd5c4b72e192de6bd9732c4089e3fea0e515f79020c4376`.
- APK: `/apk/overmind-zero-1010-7f012383aee3.apk`.

## OTA 0.3.7-ota.11 — 2026-10-08T22:53:16.673945+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1011).
- SHA-256: `c3ed391916479052c16e8172f518e9aa65bbef31b30ebb45ab5905fa3af744e9`.
- APK: `/apk/overmind-zero-1011-c3ed39191647.apk`.

## OTA 0.3.7-ota.12 — 2026-10-08T22:54:46.140624+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1012).
- SHA-256: `4e2a741614d0b81ef22738bc1998a35806d9a01054731eab2a39ebf7277bc2c2`.
- APK: `/apk/overmind-zero-1012-4e2a741614d0.apk`.

## OTA 0.3.8-ota.13 — 2026-10-08T22:57:55.065588+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1013).
- SHA-256: `b98857c77d5a4362f3696d4dd432965287593120382fdc28f26b032dafe58777`.
- APK: `/apk/overmind-zero-1013-b98857c77d5a.apk`.

## 2026-10-09 — OTA Android provato su dispositivo
- L'utente segnala che l'aggiornamento dalla release correttiva alla v0.3.8 **funziona** sul Galaxy S21.
- Il problema precedente era il Manifest privo di INTERNET, REQUEST_INSTALL_PACKAGES e UpdateFileProvider; corretti e verificati sulla APK firmata da CI.
- Release 0.3.8-ota.13 resta baseline OTA; gameplay ZERO-003 può riprendere in un ramo separato, preservando `com.overmind.zero.ota` e la firma.

## OTA 0.4.0-ota.15 — 2026-10-08T23:07:12.242277+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1015).
- SHA-256: `1adaf54b59b3d9a48d9f2bdbd0dc31ea0570879a82b4c75aba56fa3443d9d6fe`.
- APK: `/apk/overmind-zero-1015-1adaf54b59b3.apk`.

## OTA 0.4.1-ota.16 — 2026-10-08T23:22:39.482124+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1016).
- SHA-256: `030fc487f8988043e7927584af30b0eaa714e3790f12f5f8e947d54d056a4406`.
- APK: `/apk/overmind-zero-1016-030fc487f898.apk`.

## OTA 0.5.0-ota.17 — 2026-10-08T23:34:32.865396+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1017).
- SHA-256: `dbfe1e0ad71140098551eeb0161310849a0bff1e5924b3a9e9b69a4879133397`.
- APK: `/apk/overmind-zero-1017-dbfe1e0ad711.apk`.

## OTA 0.5.0-ota.18 — 2026-10-08T23:36:02.181313+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1018).
- SHA-256: `5ebc1bc064f8aad562da150f73830ec0b03127e56b4ce82c4a366e886288763a`.
- APK: `/apk/overmind-zero-1018-5ebc1bc064f8.apk`.

## OTA 0.6.0-ota.19 — 2026-10-09T00:04:07.789084+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1019).
- SHA-256: `4922de216534059ca58c0521902b7ab71cb683e3ac6c5c6ec214c6f36e7829ba`.
- APK: `/apk/overmind-zero-1019-4922de216534.apk`.

## OTA 0.6.0-ota.20 — 2026-10-09T00:05:20.499825+00:00
- Pacchetto Android firmato pubblicato su Vercel (codice 1020).
- SHA-256: `27dec96315ab247ac6af59b036ed787e90346a5d9d8ea34badc4796627ad5964`.
- APK: `/apk/overmind-zero-1020-27dec96315ab.apk`.

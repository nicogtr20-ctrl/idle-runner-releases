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

## OVERMIND ZERO 0.11.1-ota.61 — publisher and production delivery verified
Signed GitHub Actions run `38073421560` SUCCESS, versionName `0.11.1-ota.61`, versionCode `1061`, applicationId `com.overmind.zero.ota`, 169280-byte signed `OVERMIND-ZERO.apk` checksum `8d8f121d985735ca3d7cac507007fb85a1ee192603309c2e867dc39e638e53c3`. GitHub Release `overmind-zero-v0.11.1-ota.61`. Production `https://overmind-zero-updates.vercel.app/latest.json` HTTP200 version 1061; exact `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1061-8d8f121d9857.apk` HTTP200, MIME Android, content-length 169280. The official OTA serves this build now. Phone AGGIORNA and in-app restart/slot validation on S21 not yet independently checked; install in place, do not clear app data, preserve important named saves. The new `causal_experiments_v111` remains isolated and backward compatible. Full paper `docs/EXPERIMENT_0111.md`.
## 0.11.2-ota.62 production deployment verified — 2026-10-10
Publisher GitHub Actions 38078981840 SUCCESS, release overmind-zero-v0.11.2-ota.62, Android original applicationId com.overmind.zero.ota with stable keystore, versionCode1062, 174824 bytes, SHA256 bbec605688215fea99c450aeb91314e437f197456f6b104b15c2e3388cc07f38. Initial Vercel public APK URL was HTTP404 because previous production deployment was stale; manually created production deployment `dpl_7ukm6M55KeVaxPq9naHZUpSTeUkT` from exact main Git commit `f21a0330db06517572ce18d9658980cf23a256f4`. State READY alias `overmind-zero-updates.vercel.app`, `https://overmind-zero-updates.vercel.app/latest.json` HTTP200 version 1062 and `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1062-bbec60568821.apk` HTTP200, MIME Android, content-length174824. No uninstall/clear data; named save backup first. Galaxy S21 in-app update/behavior still unverified. See `docs/EXPERIMENT_0112.md`.

## OTA 0.11.3-ota.63 — verified signed behavior lab
Signed GitHub Actions publish run `38087100529` SUCCESS, release `overmind-zero-v0.11.3-ota.63`, original app ID `com.overmind.zero.ota`, versionCode `1063`, signed 184108-byte `OVERMIND-ZERO.apk`, SHA256 `35d2ced944ac1125967b46ae67588a305b136984f1232edd59f14a9632b30d95`. Live `https://overmind-zero-updates.vercel.app/latest.json` returned HTTP200 with 0.11.3-ota.63/code1063 and exact APK `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1063-35d2ced944ac.apk` returned HTTP200, content-type `application/vnd.android.package-archive`, Content-Length 184108. A previous version required manual Vercel redeploy; **v0.11.3 did not**. This OTA changes only independent read-only behavioral lab and benchmark-only nav bias switch (default true); no reinstall or profile reset, keep named save as precaution. Samsung Galaxy S21 update and quantitative comparison still pending. Report `docs/EXPERIMENT_0113.md`.

## 0.11.4-ota.64 — GitHub signed release SUCCESS, Vercel production BLOCKED (2026-10-10)
GitHub Actions official publisher `38089553377` SUCCESS; signed tag `overmind-zero-v0.11.4-ota.64` versionCode1064, package/signing identity unchanged, APK190132B, SHA256 `255727583121d49c07463a29aea07d4f99db06912cdc2f6aeff40cdd103cdab8`, private authenticated download `https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.11.4-ota.64/OVERMIND-ZERO.apk`. GitHub `main/updater/public/latest.json` updated to 0.11.4. **PRODUCTION VERCEL NOT YET DEPLOYED**: `https://overmind-zero-updates.vercel.app/latest.json` still HTTP200 with 0.11.3-ota.63, NEW `/apk/overmind-zero-1064-255727583121.apk` HTTP404. Manual deploy POST `mcp__Vercel__create_deployment` to project `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, git SHA `ff8ce6e90747d4ac835426f50550cd675a4cfcd5`, blocked HTTP402 `api-deployments-free-per-day` / >100 daily / `retryAfter:86400`. No Vercel CLI locally. When quota restores, deploy LATEST main SHA to production, verify both manifest version1064 and APK HTTP200 application/vnd.android.package-archive size190132 before claiming in-app updater success. For now ONLY authenticated GitHub Release file, install in place, preserve saves. Documentation `docs/EXPERIMENT_0114.md`.

## 0.11.4-ota.64 — OTA Vercel RECUPERATO E VERIFICATO (10 ottobre 2026)
Dopo publisher firmato GitHub Actions **38089553377 SUCCESS**, manifest già su main ma Vercel era rimasto sulla versione 1063: `latest.json` pubblico 1063 e nuovo APK 1064 HTTP404. **Problema risolto** creando un nuovo deployment production Vercel `dpl_4KmbNc3nikDq5EPd2i8v2JMTqBM7` dalla HEAD di `main` `725d1920505c3c004280f0f8d6b5950fd1474566`, risultato **READY** e alias ufficiale `overmind-zero-updates.vercel.app`. Nuova verifica finale **HTTP200**: `https://overmind-zero-updates.vercel.app/latest.json` versionName `0.11.4-ota.64` code1064 SHA256 `255727583121d49c07463a29aea07d4f99db06912cdc2f6aeff40cdd103cdab8`; `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1064-255727583121.apk` HTTP200, MIME `application/vnd.android.package-archive`, Content-Length **190132**. Eventuali precedenti annotazioni di impossibilità Vercel sono **superate**: OTA ufficiale ora serve v1064, senza necessità di usare il link GitHub. Galaxy S21 installazione in-app e qualità del comportamento restano da verificare, non disinstallare/azzerare, salvare slot prima dell'aggiornamento.

## 0.11.5-ota.66 — verified signed production OTA (10 ottobre 2026)
Stable signed Android package originale `com.overmind.zero.ota`, publisher `38092242587 SUCCESS`, `versionName=0.11.5-ota.66`, `versionCode=1066`, GitHub release tag `overmind-zero-v0.11.5-ota.66`, APK **194712 bytes**, SHA256 `e393e44bdccd68bd6ecb5e9020227a5906760b7ea44e3e6e055e39a16040ec1c`. Official `https://overmind-zero-updates.vercel.app/latest.json` HTTP200 version 1066/hash corretto, `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1066-e393e44bdccd.apk` HTTP200, MIME Android, Content-Length194712. **Nessuna reinstallazione, reset o wipe dei vecchi 12 slot**. Salva lo ZERO promettente prima di premere AGGIORNA. Il run precedente 65 `38092125490` si è interrotto prima della firma/pubblicazione per una vecchia assertion con codici testuali/3 scenari obsoleti, modificata in workflow mantenendo tutti i controlli; per questo il numero ora è 66. Staging `38092034409 SUCCESS`. Nessun test user-device Galaxy S21 ancora effettuato. Dettagli `docs/EXPERIMENT_0115.md`.


## ZERO 2.2 attivo — OTA118 verificata, 13 ottobre 2026
Versione APK `0.11.6-ota.118`, codice **1118**, package `com.overmind.zero.ota`. Fonte rilasciata `685031f7b8856f8eb351401b99e22b0de9604330`, PR #41; branch ufficiale `feature/overmind-inapp-updater`. Nucleo ricorrente prima della scelta reale, corpo/raggi/energia nella previsione, replay persistente con holdout separati, recupero temporaneo di tecniche consolidate con interruzione e isolamento delle finestre corporee, campionamento sensoriale ai confini della decisione per TEST MAX. È un reservoir fisso con readout appresi (40.560 pesi), non Dreamer completo o superintelligenza dimostrata.

Verifiche: **70 test JVM locali PASS**, controlli del publisher PASS, [Android staging 38467622938 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467622938), [publisher firmato 38467965975 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38467965975). APK pubblica HTTP200, **382.200 byte**, SHA-256 `c12528432b0e59432a390847deb3d1482d1ea4760c59d1b19049abea11d61f4c`; firma V2 verificata crittograficamente e fingerprint del certificato identico alla OTA116: `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`. Manifest pubblico verificato a 1118. [APK immutabile](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1118-c12528432b0e.apk). [Release GitHub](https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.118).

Installare sopra l'app attuale tramite AGGIORNA o APK; non disinstallare e non azzerare le memorie. Nuovo tasto **CONFRONTO CERVELLO 2.2** in CERVELLO/ANALISI: 24 prove fisiche isolate (12 coppie) sul profilo salvato; stesso seed/scena, controllo precedente contro core attivo+recupero. Non modifica il personaggio; report separato `zero_core_lab_v22`, upload solo esplicito tramite CONDIVIDI DATI, conservato nei multipart. L'arena ha controllori fissi, senza tempeste/streaming: screening, non equivalenza integrale al mondo.

**Ancora da verificare sul Galaxy S21:** installazione in-place e continuità dei 12 slot, FPS/termica e nuovo moltiplicatore TEST MAX rispetto al x29 riportato, risultati A/B, retention e adattamento a scene nuove. Nessun uplift comportamentale è già dimostrato. [Paper completo e checklist](ZERO22_ACTIVE_CORE_AND_DEVICE_VALIDATION.md). Il publisher 117 sul diverso branch `feature/zero22-active-recurrent-ota117` non è questa release: ultimo rilascio verificato qui è OTA118.


## ULTIMO STATO VERIFICATO — ZERO 2.2 / OTA119 — 13 ottobre 2026, 23:20 CEST

Richiesta utente: APK pronta con centro decisionale rivisto attivo, conservazione del personaggio e ottimizzazione TEST MAX (misura precedente S21 circa x29). **Rilascio realmente concluso**: versione `0.11.6-ota.119`, codice1119, package `com.overmind.zero.ota`.
- Sorgente APK esatto: `582030833113f698933fbe83327c843e992a6ea0`, ramo `feature/zero22-active-recurrent-ota117`, [PR42 draft](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/42). Il nome del ramo conserva il numero candidato117; la release effettiva è119.
- Publisher [38469244134](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38469244134) **SUCCESS**: tutti71 entrypoint JVM, tutti26 gate Python originali aggiornati, Android Debug+Release firmata, permessi/provider, GitHub Release e manifest main. Il primo117/38467848821 aveva passato JVM/Debug ma falliva una stringa statica della stabilità motoria; non pubblicò APK.
- [APK pubblica](https://overmind-zero-updates.vercel.app/apk/overmind-zero-1119-6593561d5593.apk),382708byte; SHA256 `6593561d5593d1b047b73022506be0203c9fe603b46f1f7de9cd7ba52b215b68`. Scaricata e hash/dimensione verificati. ApkVerifier verifica crittografica API24+: firme119 e116 valide, stesso certificato SHA256 `45010684b4fd74de00f87fb81ca89e1e79b58409cf9fefc497b445b0fe19f583`.
- Manifest pubblico HTTP200/code1119 verificato, Vercel productionREADY progetto `prj_fremJAX6ODfjsSkoA6TMjdlWRZwg`, teamprime-1323, alias corretto, deployment `dpl_FhBaHF4P4WVcQ3u1ZpCCgRDEzS7z`, commit hosting `111dbcf3bafb6e35933e882620ab217a020712b9`. DEX pubblico contiene nucleo, esecutore skill, confronto esteso, observationDue e learningBlend.
- Installare SOPRA tramite Impostazioni→AGGIORNA APP o link diretto, **non disinstallare**. Prova APK→APK, dodici slot, velocità/temperatura sul S21 non ancora osservate.
- La nuova priorità comprende la migrazione graduale della policy esperta: senza evidenza nuova preserva scelte/epsilon storico; aumenta quota del nucleo con train reali. Non riavvia esplorazione vergine su ZERO esperto. Non ricompilare un ramo precedente perdendo questa correzione o il confronto esteso.
- Leggere [ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md), [ZERO22_PHYSICAL_SCREENING](ZERO22_PHYSICAL_SCREENING.md), [ZERO22_JVM_RESULTS](ZERO22_JVM_RESULTS.json). È un nucleo ricorrente leggero con reservoir fisso e readout appresi, non Dreamer completo/RSSM o intelligenza generale. Autorità prima dell'azione, memoria corpo+raggio, previsione energetica, replay recente/raro, recupero contestuale con feedback.
- Screening fisico vergine12pareggi. Primo checkpoint esperto0vittorie/1sconfitta/11pareggi, regressione di transizione documentata e corretta. Stesso checkpoint dopo correzione12pareggi su energia/danni/sopravvivenza, prese opposte-5.917frame medi. **Nessun uplift dimostrato**. Fixture sintetica37/40 scelte benefiche dimostra soltanto autorità/apprendimento del nucleo.
- Sul telefono ANALISI→LABORATORIO COMPORTAMENTALE (18 prove32s) e CONFRONTO2.2ESTESO/CASSE (24prove fino170s); copie isolate, report distinti inclusi in Condividi dati. TEST MAX rimane separato, conferma/STOP/thermal, substep1/60; sensi completi solo ai confini0.65s, nessun substep fisico saltato. Velocità maggiore di x29 non ancora misurata.
- Memorie/slot legacy preservati, chiave opzionale `recurrent_core_v22` atomica compressa. Riavvio conserva hidden e cancella solo pending non verificabile. Esperienze heldout e immaginate mai nel replay. Main conserva Idle Runner; soltanto hosting+paper aggiornati.


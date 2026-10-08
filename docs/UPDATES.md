# Aggiornamenti Android tramite Vercel — contratto tecnico

## Esperienza utente richiesta
Un pulsante **Aggiorna** nell'app verifica su Vercel se esiste un APK nuovo. Se sì, scarica il file **dentro l'app** su HTTPS, verifica SHA-256 e avvia `PackageInstaller`/intent di installazione, richiedendo conferma di Android. Non promettere installazione silenziosa: con APK installate fuori Play Store, Android richiede autorizzazione alla fonte e consenso utente.

## Architettura
- Manifest pubblico HTTPS Vercel: `https://overmind-zero-updates.vercel.app/latest.json`.
- File APK HTTPS distribuito dalla stessa origine, `/apk/overmind-zero.apk`, pubblicato solo dopo CI verde e firma certificato costante.
- `latest.json` con `schemaVersion`, `available`, `applicationId`, `versionCode`, `versionName`, `apkUrl`, `sha256`, `sizeBytes`, `notes`.
- Il client esamina codice versione e `applicationId` prima di scaricare; calcola SHA-256 su bytes APK; richiede permessi all'installazione (Android 8+ `REQUEST_INSTALL_PACKAGES` e `canRequestPackageInstalls`).
- Installazione tramite `FileProvider` content URI (non `file://`), `Intent.ACTION_VIEW` con `application/vnd.android.package-archive`, `FLAG_GRANT_READ_URI_PERMISSION`. Il Package Installer mostra UI di sistema.
- Manifest deve avere risposta `Cache-Control: no-store` o breve TTL; rilasci atomici per non puntare mai a APK incompleto.
- Dopo verifica installazione, la nuova app dovrebbe mantenere lo stesso package id, firma e dati persistenti.

## Firma APK: prerequisito
Le release storiche ZERO-001/002 sono DEBUG e non garantiscono certificato stabile fra run GitHub Actions. Serve **una keystore release persistente** con password/alias custoditi in **GitHub Actions Secrets**, non in repository o in Vercel pubblico.
**Un solo secret obbligatorio**: `OVERMIND_SIGNING_BUNDLE` (Base64 di un JSON contenente keystore PKCS12, password e alias). Un pacchetto di firma e il backup privato sono stati generati per il proprietario, ma non vengono inseriti nel repository. Configuralo una volta in GitHub → Settings → Secrets and variables → Actions → New repository secret. Il workflow `overmind-ota-publish.yml` legge il secret e firma sempre con lo stesso certificato.

Mai rivelare, stampare o committare la chiave. Prima versione con firma nuova potrebbe richiedere una sola reinstallazione da parte del proprietario.

## Manifest scheletro in updater/public/latest.json
Finché non esiste un APK firmato e pubblicamente distribuito, `available` è `false`: il pulsante deve mostrare "Nessun aggiornamento disponibile" senza link fittizi.

## Vercel e GitHub
- Il GitHub repository è privato; GitHub Release APK non è scaricabile direttamente da app Android senza autenticazione. Vercel deve realmente ospitare il binario o usare un backend autenticato e autorizzato.
- Un deployment Vercel pubblico con APK rende l'APK accessibile a chi conosce l'URL, **non il codice sorgente privato**.
- Pipeline prevista: CI build → test → firma costante → verifica SHA-256 → pubblicazione binario statico Vercel → manifest aggiornato → verifica HTTPS → release tag + changelog.
- Se l'host è configurato per proteggere le preview Vercel, il manifest di produzione deve essere pubblicamente raggiungibile dal telefono.

## Test end-to-end da eseguire su Galaxy S21
- Installazione della prima app **firmata con chiave stabile**.
- Aggiorna con nessuna versione nuova → messaggio esplicito.
- Pubblica un APK con versionCode maggiore e stesso applicationId/certificato.
- Premi Aggiorna → verifica HTTPS → download automatico → hash → schermata conferma Android → installazione riuscita.
- Verifica salvataggi/memoria non cancellati.
- Prova rete assente, versione uguale, pacchetto errato, hash invalido e APK non accessibile.

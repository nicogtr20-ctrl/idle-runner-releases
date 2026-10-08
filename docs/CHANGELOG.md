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

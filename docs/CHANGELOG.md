# CHANGELOG — storico OVERMIND ZERO

> Le versioni seguenti sono *esperimenti*, non un prodotto finito. Aggiornare questa pagina in OGNI release insieme al file HANDOFF.

## 2026-10-09 — Infrastruttura aggiornamenti (in preparazione)
- Stop temporaneo al ramo gameplay `experiment/overmind-zero-physics-v04`.
- Creati whitepaper permanente, architettura, changelog, handoff, AGENTS.md e roadmap.
- Vercel attivo: `https://overmind-zero-updates.vercel.app/latest.json`, risposta 200 con `available:false`.
- Client Android con pulsante Aggiorna, download HTTPS/SHA-256, installer con autorizzazione Android: branch `feature/overmind-inapp-updater` (build da validare).
- Nuovo applicationId `com.overmind.zero.ota` per non toccare i dati locali delle APK sperimentali già installate.
- **Firma persistente e installer in-app ancora da validare**; evitare di promettere aggiornamenti totalmente silenziosi.

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

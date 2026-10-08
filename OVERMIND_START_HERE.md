# OVERMIND: ZERO — punto di ingresso per tutte le nuove chat

> Ultimo aggiornamento della documentazione: 9 ottobre 2026.
>
> **OBIETTIVO DEL PROGETTO:** creare su Android una forma di vita artificiale ("ZERO"), rappresentata da una macchia nera simile a ferrofluido, con corpo deformabile e apprendimento autonomo da esperienze in un mondo simulato. Il giocatore è una presenza invisibile, non impartisce ordini alla creatura e ne modifica il mondo.

## Leggi in quest'ordine
1. [Whitepaper](docs/WHITEPAPER.md) — visione e limiti scientifici.
2. [Architettura](docs/ARCHITECTURE.md) — cervello, fisica, memoria.
3. [Roadmap](docs/ROADMAP.md) — priorità e criteri di accettazione.
4. [Changelog](docs/CHANGELOG.md) — storico delle versioni.
5. [Handoff per nuove chat](docs/HANDOFF.md) — branch, workflow, regole operative.
6. [Aggiornamenti Android/Vercel](docs/UPDATES.md) — firma stabile, OTA e manifest.

## Repository e rami
Il repository `nicogtr20-ctrl/idle-runner-releases` è **privato** e contiene un altro prototipo Android nel branch `main`; per OVERMIND sono stati usati **branch sperimentali separati**. Non confondere il codice in `main` (Idle Runner) con OVERMIND.
- `experiment/overmind-zero-v01`: simulazione iniziale con agenti e Q-learning.
- `experiment/overmind-zero-brain-v02`: ZERO-001, corpo elastico e cervello neurale casuale.
- `experiment/overmind-zero-world-v03`: ZERO-002, oggetti ignoti e memoria degli effetti. **Base applicativa attualmente validata su telefono**.
- `experiment/overmind-zero-physics-v04`: branch creato ma sviluppo del gameplay **sospeso** finché updater e documentazione non sono pronti.

**Server Vercel attivo:** https://overmind-zero-updates.vercel.app/latest.json, manifest attualmente disabilitato finché non è disponibile un APK firmato. **Branch client updater:** `feature/overmind-inapp-updater`.

**Priorità attuale:** **v0.4.1-ota.16** firmata e pubblicata via Vercel, con mondo senza reset temporizzati e prove visibili dello spostamento degli oggetti. In attesa di test su Galaxy S21. Il precedente test v0.4.0 ha mostrato reset ogni 26s e contatore degli urti fuorviante. Codice più recente in `feature/overmind-inapp-updater`, APK/manifest in https://overmind-zero-updates.vercel.app/latest.json. Verificare il confronto `CONFRONTA` prima di avanzare oltre.

Il codice, gli asset e i release APK restano di proprietà e sotto controllo del repository privato. Non pubblicare automaticamente codice sorgente o chiavi di firma.

## Stato più recente (2026-10-09)
- **ZERO v0.5.0-ota.18**: cervello di selezione degli stimoli appreso dalle esperienze, 91 parametri vergini, vecchia formula di selezione disattivata. Codice: `feature/overmind-inapp-updater`. Test e build firmata verificati.
- Il server Vercel ha esaurito il limite gratuito di deployment giornalieri: il manifest pubblico potrebbe restare alla 0.4.1 finché il servizio non riprende.
- Download temporaneo della APK firmata nel repository privato: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.5.0-ota.18. Installare sopra, non disinstallare.
- Per dettagli su verifiche, rischi, test e prossimi passi leggere la sezione ZERO-005 in `docs/HANDOFF.md`.

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

## Stato più recente — ZERO-006 / 2026-10-09
- Nuova pagina **CERVELLO** a tre schede (abilità, parametri originali/attuali, evoluzione), file `BrainStatsActivity.java` sul branch `feature/overmind-inapp-updater`. Rete DecisionBrain dinamica 91→217, migrazione v1/v2 e conservazione del modello.
- APK firmata v0.6.0-ota.19 pronta: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.6.0-ota.19 . Installare sopra l'app esistente, non disinstallare.
- Manifest su GitHub avanzato a v0.6.0-ota.20 ma produzione Vercel ancora alla v0.4.1 per limite di deploy del piano. Consultare docs/HANDOFF.md, NON dichiarare v0.6 già disponibile tramite pulsante AGGIORNA finché non verificato pubblicamente.

## Stato aggiornato: ZERO-007 / 2026-10-09
- **v0.7.0-ota.22**: corpo con 32 masse perimetrali deformabili e rete di 74 pesi locali (nessun braccio predefinito), nuovi strumenti nella pagina CERVELLO. Vecchio sistema motorio (62), manipolazione (18) e decisioni (91+) preservati. Codice su `feature/overmind-inapp-updater` e ramo `feature/overmind-v07-plastic-body`.
- Distribuzione OTA Vercel **verificata online**: https://overmind-zero-updates.vercel.app/latest.json (1022); APK firmata https://overmind-zero-updates.vercel.app/apk/overmind-zero-1022-74610481e450.apk . Premere `AGGIORNA` nell'app corrente; non disinstallarla.
- Prima prova effettiva sul Galaxy S21 in attesa. Priorità: filmato di sporgenze locali e valore ALLUNGAMENTO, numero TEST della morfologia dopo addestramento, parametri MEMBRANA appresi. Per dettagli leggere docs/HANDOFF.md.

## Ultimo sviluppo — ZERO-008 / 2026-10-09
- Branch `feature/overmind-v08-scarcity-grasp-chamber`: membrana a 96 nodi, energia finita, emergenza di nuove risorse rara, camera con porta scorrevole che richiede contatti fisici su due lati, e addestramento mirato delle capacità di manipolazione.
- La build viene firmata tramite `feature/overmind-inapp-updater`, package invariato `com.overmind.zero.ota`; verificare https://overmind-zero-updates.vercel.app/latest.json prima di usare AGGIORNA, salvando la precedente memoria. L'installazione su Galaxy S21 è da validare.
- Leggere `docs/HANDOFF.md` per dettagli tecnici e limiti; NON dire che ZERO ha imparato a usare due braccia finché non si vede spostare e superare la porta senza spinte prefissate.

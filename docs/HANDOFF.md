# HANDOFF — punto di ripresa per un'altra chat o agente

**Ultimo aggiornamento:** 2026-10-09

### Riferimenti
Repository: `nicogtr20-ctrl/idle-runner-releases` (privato).
- Branch `main` = Idle Runner + `docs/` + infrastruttura updater; **NON** il codice OVERMIND attuale.
- Branch `experiment/overmind-zero-world-v03` = ultima versione OVERMIND realmente provata dall'utente.
- Branch `experiment/overmind-zero-physics-v04` = creato, NON procedere sul gameplay prima che updater sia funzionante.
- `experiment/overmind-zero-brain-v02` = esperimento motorio originale.

### Esperienze e feedback utenti
L'utente ha osservato ZERO-001: una forma nera deformabile, il nucleo spinge sulla membrana con un movimento arcuato, la membrana si gonfia, ZERO raggiunge effettivamente il punto stimolo. Dopo ~2.467 episodi benchmark -3,6→+179,7. ZERO-002 ha registrato 4.772 tentativi, 49 contatti, 3/3 tipi, benchmark +2,9→+204,4. Ha richiesto corpi tipo ferrofluido reale, nessuna appendice prefissata, scoperta autonoma del movimento e delle funzioni degli oggetti.

### Decisione attuale
**FERMARE LA v0.4 DI GAMEPLAY. PRIMA:**
1. Documentazione GitHub permanente.
2. Vercel come endpoint di aggiornamenti e distribuzione delle APK.
3. Pulsante Aggiorna nell'app. Android chiede il consenso alla schermata d'installazione.
4. Firma persistente per ogni build e sicurezza integrità.
5. Prova reale sul Galaxy S21. Se cambio package name o certificato, non è aggiornamento in-place.

### Limite Android / sicurezza
Le release debug di GitHub Actions spesso usano certificati diversi. È irrealistico garantire aggiornamento in-place su APK già installate senza stessa firma; in quel caso servirà un'ultima installazione pulita (preservare prima salvataggi, se necessario). Non inserire credenziali GitHub, password signing o chiavi in APK o repository. Il repo è privato, mentre Vercel e gli APK pubblicati su CDN possono essere accessibili pubblicamente — valutare prima la diffusione.

### Per una nuova chat
Scrivere: «Apri su GitHub `nicogtr20-ctrl/idle-runner-releases`, leggi `OVERMIND_START_HERE.md` e `AGENTS.md`, riprendi OVERMIND dalla sezione HANDOFF e continua la priorità OTA senza modificare Idle Runner.»

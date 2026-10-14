# ZERO 2.2 — regressione sospetta OTA130 (dati reali 14 ottobre 2026)

## Stato: incident report, NON risolto / nessuna OTA131 pubblicata
L'utente segnala morte con energia visibile vicina e forte peggioramento nella manipolazione delle casse. Dati privati dell'organismo caricati tramite CONDIVIDI DATI CON GPT (senza trasferire salvataggi nel repository principale).

### Snapshot di provenienza
- Pre-update, dichiarato dall'utente: `zero-telemetry/reports/20261014-161415-e4b93716c12f`; 2026-10-14 16:14:15 UTC; 63 vite complete; 63292 transizioni rc22.
- Post-update, dichiarato dall'utente: `zero-telemetry/reports/20261014-164053-e62d161841c1`; 2026-10-14 16:40:53 UTC; 75 vite; 86650 transizioni rc22.
- Riferimento precedente `20261014-153138-e265da168a57`: `scarcity_elapsed` circa 33010.05s, 345 risorse, 91 casse.
- OTA130 realmente pubblicata e firmata da pipeline 38596282565; nuovo controllo HTTP200 38597154936. L'utente conferma installazione.

### Performance reale (correggere sempre per durata simulata)
- Pre ultimi10 conclusi: media 2019.2s=33.65min; +14.41 contatti energetici/1000s, 1.29 dannosi/1000s.
- Dopo prime12 vite complete (63->75): media 1284.9s=21.42min; +13.23 contatti energetici/1000s, 0.778 dannosi/1000s.
- Record pre 8648.6s=144m09s; massima vita post nei 12 eventi 4469s=74.5m.
- Intervallo pre recente da 33010.05 a 41587.2s: risorse +131 (15.273/1000s), casse scoperte +33 (3.847/1000s).
- Intervallo post da 41587.2 a 56923.492s: risorse +205 (13.367/1000s, -12.48%), casse scoperte +46 (2.999/1000s, -22.04%). Anche se i totali crescono, l'efficienza delle casse è diminuita. Causa non identificata, mondo/scena possono differire e capacità corpo è mutata (+200 accettazioni mano, +214 membrana, +159 motore).
- La mortalità per contatto dannoso NON è salita: 1.29->0.78 contatti nocivi/1000s, mentre il problema segnalato è fame in presenza di luce.

### Confronto CAUSALE di tracce locali (non A/B controllato)
- `decision_flight_last_v1162` (part-001.json): prima della OTA130, ultima finestra delle decisioni registrate 454 righe; campo `recurrentDelta` index 16 tra 19 fattori: **0/454** nonzero; epsilon sempre .09; ultime24 azioni non più di 9 identiche.
- Dopo OTA130, ultima finestra registrata nella vita di 482.62s: 457 righe; campo `recurrentDelta` nonzero **456/457**, max modulo .406 (i numeri sono quantizzati /1000). Ultime24 azioni: **22 azioni n.11**. Vista `Senses.read` nei canali 14..25 segnali nonzero durante gli ultimi 30s e fino a terminale, quindi NON è una totale cecità agli stimoli. Non provata la raggiungibilità fisica delle risorse dalla sola traccia.
- **23/457 decisioni** registrano un `recurrentDelta` con modulo > .12, che supera il limite della sola influenza fisica nella OTA130; questo implica che il ramo `energyTrust>0` sia stato usato ALMENO in quei momenti (assumendo che la quantizzazione/tutte le formule del codice distribuito siano coerenti).
- **50/457** scelte presentano `epsilon<0.09` fino a .075 nel finale, mentre il calcolo originale in `SensorimotorMind.choose()` impone minimo .09; questa riduzione può arrivare solo da `activeCore.exploration()` quando l'`energyConfidence` diventa transitoriamente positiva.
- Le variazioni di score sono quindi REALI nel loop decisionale, e la convalida energetica è intermittente o incongruente sul medesimo regime esperto; non è corretto descrivere il problema come puramente grafico.
- Da decodifica binaria verificata di `rc22`: al checkpoint pre-OTA130 15/16 modelli fisici sopra la soglia, 0/16 modelli energetici; post-OTA130 **16/16 fisici**, **0/16 energetici**. Vantaggio fisico holdout post 10.5%-31.3%. Valutazioni cumulative `planned` 2001->4822 (non vere azioni eseguite, conteggio candidate `evaluate()`), indicano qualche convalida nel mezzo benché il post checkpoint ne abbia zero.
- **IMPORTANTE CORREZIONE DI OSSERVABILITÀ**: nel `zero20Passive.zero22.summary` il display `confidence=0.000 margin=0` viene da un NUOVO oggetto RecurrentCoreV22 ripristinato con `Zero20PassiveSnapshot`. Il formato `rc22` **non persiste lastConfidence/lastMargin**: quindi i valori mostrati al report sono 0 PER DEFINIZIONE dopo la ricostruzione e NON misurano il valore corrente durante gameplay. Il `energyReady=0/16` è reale perché ricalcolato dagli errori serialized.
- Q/risk readouts sono appresi con reward energetico reale ma non hanno validazione indipendente (heldout solo per il modello dinamico). Legarli al `physicalTrust` con `authority=.16` anche senza affidabilità energetica permette di alterare azioni gravemente durante la fame/contatto e in 457 scelte morte. Questo è un rischio architetturale emerso.

### Ipotesi principali, ordine di priorità
1. **Arbitro miscalibrato**: `confidence(a)` misura previsioni fisiche, ma la OTA130 trasferisce l'autorità anche al valore energetico `value()` senza validarlo separatamente. Non prova causalità ma è contraddizione di validazione.
2. **EnergyConfidence volatilità**: una EMA holdout alpha .08 con tolleranza assoluta .00002 può superare il gate per brevi finestre e quindi attivare `rollout`, .85 authority, riduzione epsilon anche se la previsione generale resta energeticamente pessima. 23 score deviations >0.12 e 50 epsilon<.09 lo attestano nei registri.
3. **Deriva manuale del corpo**: continui `OnlineBodyLearning` e skill archives attivi, con centinaia di parametri accettati tra snapshot; possibile peggioramento delle prese senza un bug di collisioni. Verificare guard per grip dopo candidata che ha misurato meno opportunità.
4. **Sensori vs azioni**: 26-dimensionali segnali luminosi registrati anche durante fame, ma non dimostrano riconoscimento affidabile né accesso spaziale. Non imporre ricerca scripted dell'energia per falsare il comportamento.

### Azione tecnica RACCOMANDATA (non ancora effettuata)
- **OTA131 protettiva** se autorizzata: nessun reset, niente downgrade APK impossibile; mantenere tutta la memoria persistente e l'apprendimento passivo `rc22`. Inibire *temporaneamente* la modifica decisionale da `physicalTrust` finché Q/risk non sono indipendentemente validati; proteggere le policy esperte per basso stato energetico e durante tentativi fisici effettivi. Sostituire il gate energetico transitorio con verifica robusta su finestre indipendenti/contesto: no energy-rollout e no epsilon suppression con performance miscalibrata. Conservare osservazione e log delle 16 modifiche per test A/B senza alterare l'essere.
- Eseguire test regression con fixture che riproduce errori energie: valuto in un template con 16/16 accuratezza fisica ma pessima energia, nessuna autorità energetica; dimostrare il blocco dei delta >0.12 e dell'epsilon<0.09.
- Rieffettuare test app su stesso snapshot in A/B fisico prima di rivendicare recupero. Fornire APK solo dopo QA, firma stabile, release, Vercel manifest+APK HTTP200.
- I dati quantitativi sono reali; conclusione *probabile* regressione policy OTA130, NON prova causale definitiva né promessa di successo.

# ZERO OTA153 — vera orientazione muscolare dei 96 settori della membrana

**Data:** 15 ottobre 2026. **Stato sorgente:** PR [#57](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/57) integrata sul ramo ufficiale `feature/zero22-active-recurrent-ota117`, commit `1f8c7c1bb82a918d9d3578314f4f95f97030a4c3`. Pre-release Java e Android PASS [run 38718867671](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38718867671). **Rilascio firmato:** GitHub publisher [run 38719468808](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38719468808) in esecuzione al momento di scrivere; controllare conclusione e `main/updater/public/latest.json` prima di distribuire.

## Perché siamo intervenuti

ZERO neonato (OTA152) secondo l'utente “si allunga e ruota come una lancetta ma non avanza”, e le 11 energie prese possono essere contatti fortuiti con spawn vicino. Ultimo export corrente `zero-telemetry/reports/20261015-100119-11ef3f7dec7d7`, acquisito **dopo OTA152**:

- 19.930 decisioni del cortex, 15.892 periodi di training, 63 vite, 11 contatti energetici, 34 danni, 5 oggetti mossi.
- 96 punti di membrana con velocità nonzero, corpo non fisicamente congelato, zero incroci; il vecchio bug OTA147/148 non è tornato.
- Nel tracciato dell'ultima vita, cammino grossolano del centro ~645 unità contro spostamento netto ~8,5. Il cammino cumulato non dimostra esplorazione spaziale, e 11 pickup totali NON dimostrano ricerca intenzionale.
- Ricalcolando geometricamente i 16 settori della membrana vera dal salvataggio, il loro azimut corrente relativo al baricentro differisce dall'azimut fisso inventato per numerazione nel motor head di **~146,5° in media assoluta**, con molti settori >150°. Questo valore riguarda la **geometria del salvataggio** e non attribuisce automaticamente tutti i fallimenti a un unico bug.

## Difetto concreto

Nel vero `SensorimotorMind.choosePrimaryV22`, la direzione dell'intento ricorrente è `action % 8 * π/4`, ancorata allo spazio mondo. Ma `SymbioteMotorHeadV1.chosen` riempiva `feature[9]`/`feature[10]` per ciascuno dei 16 gruppi muscolari usando `a=(sector+0.5)*2π/16` **per tutta la vita**, ignorando dove fossero effettivamente quei sei nodi della membrana. La dinamica viscosa può ruotare, estendere o ripiegare i settori. L'intento spaziale applicato sulla mappa muscolare poteva quindi riferirsi a muscoli ormai ubicati altrove. Questo è **disallineamento di coordinate del codice**, non assenza di esperienza.

## Correzione OTA153 (soltanto segnali, stessa fisica)

1. Nuovo metodo `SymbioteMotorHeadV1.measuredSectorAngles(body,skin)` ottiene per ogni gruppo di sei punti veri di membrana il loro azimut corrente rispetto al baricentro fisico pesato di tutte le 170 particelle. Ogni direzione è la media di unit vector locali; se la media risulta degenerata, usa la posizione del punto più lontano del medesimo settore (sempre reale).
2. `SensorimotorMind.choosePrimaryV22` al confine di una decisione ~0,65s **usa proprio `observedBody` e `lastActualGripSurface` osservati dopo fisica e collisioni** e passa i 16 azimut reali alla testa muscolare. Gli input cos/sin dell'orientamento si riferiscono ora al tessuto realmente presente, non all'anello di nascita.
3. `SymbioteMotorHeadV1.chosen` conserva le overload per i vecchi test/sistemi che non hanno skin reale, ma l'attivo usa gli angoli veri. **Non** sono aggiunti forza al baricentro, trazione del pavimento, movimento autonomo, premi per distanza, conoscenza della posizione delle gemme, obiettivi codificati, nuove dimensioni della rete o nuova memoria.
4. I **84 pesi** della testa motoria rimangono esattamente gli stessi, `smh1` compatibile, rete primaria ZERO 2.2 identica. Quando il corpo ruota, può cambiare legittimamente l'uscita muscolare poiché cambia la propriocezione, ma non viene cancellato l'apprendimento.

## Test vero di orientamento

`SymbioteHeadingGeometryRegression` compilata ed eseguita dalla suite correttiva Java/Android:
- Verifica che gli azimut reali coincidano con la disposizione iniziale dei nodi prima della deformazione.
- Ruota fisicamente di 90° tutti i 96 punti della membrana attorno al centro reale, **senza ruotare l'azione scelta dal cervello**: tutti i 16 sensori di orientazione devono seguire la rotazione, e cambiare i canali effettivi `cos(a-goal)` / `sin(a-goal)` che alimentano i muscoli e il loro output.
- Allunga localmente sei punti, verifica che il nuovo angolo sia calcolato da quella deformazione e trasmesso al corrispondente gruppo muscolare.
- Confronta letteralmente i valori di tutti i 84 pesi salvati: non devono essere mutati dall'operazione sensoriale. Nessuna differenza nella larghezza del formato di persistenza.
- Tutti gli altri test: fisica della membrana, interocezione 74 particelle, apprendimento energetico OTA152, Android compileDebug, invariati e **PASS**.

## Limiti e protocollo

Questa correzione ha fondamento fisico ma il **test sul telefono** rimane indispensabile per determinare se il corpo in partita acquisisce davvero movimento netto controllato verso risorse e ostacoli. Un test rotazionale non equivale a una vita in partita con apprendimento continuo. L'azione dell'AI può ancora esplorare male; non cambiare in automatico trazione/fisica se ci sono poche esperienze. La camera segue sempre il baricentro, quindi il movimento sul display va confrontato con reticolo mondiale, spawn e spostamento netto registrato, non con posizione sullo schermo.

Installare la versione firmata SOPRA `com.overmind.zero.ota`, usare pulsante Aggiorna, **non disinstallare, non azzerare ZERO e non cancellare le vecchie reti**. Registrare prima/dopo e telemetria del medesimo profilo; soprattutto rapporto net/gross per vita, energia raggiunta anziché spawn vicini, azioni, grip effettivi e variazioni dei pesi. Gli attuali risultati statici/non interattivi non provano ancora intelligenza spaziale.

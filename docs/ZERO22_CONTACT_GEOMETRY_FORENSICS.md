# OVERMIND ZERO — Verifica materialità scia, corpo bloccato e raccolte

Aggiornamento diagnostico successivo alla OTA126, 14 ottobre 2026. Nessuna modifica all'algoritmo decisionale, alla mutazione dei muscoli, alle collisioni, all'energia o alla grafica.

## Problema osservato e perché il registratore OTA124 non bastava

In una partita ZERO era apparentemente un unico punto rosso incastrato fra due casse; la scia di puntini verdi rimasta a sinistra sembrava dirigersi verso gemme blu generate successivamente e al contatto apparente l'energia veniva raccolta. Il video terminava prima degli eventi energetici raccontati dall'utente. I report esistenti `20261014-124058-d915b2967020` con 15 parti registrano due gemme blu raccolte ai tempi 1513 e 1518 secondi, un'altra a ~1640 e contatti multipli con due casse. I log attuali NON contengono le coordinate di ogni singolo punto nel frame della raccolta: non permettono di affermare se la scia stessa abbia interagito o se una massa invisibile/perimetro/tolleranza di contatto abbia provocato il pickup. Il vecchio `DecisionBlackbox` registra cause cognitive, NON geometria. `GripContactObserver` registra conteggi aggregati per cassa, NON le pose di tutte le 96 masse e tutte le traiettorie nel frame di raccolta.

Nel codice preesistente `GameActivity` crea una scia grafica dalla successione delle coordinate del baricentro `creature.cx/cy` e la DISEGNA con `drawCircle`. La scia non è passata ad alcun metodo di collisione, energia o scelta. Viceversa una risorsa ordinaria era raccolta se `MaterialGeometry.touches(..., radius+2)` OPPURE se la distanza minima fra una massa reale e la risorsa era <`radius+18`. Non correggere subito questa tolleranza: ora dobbiamo misurare il caso esatto, non cancellare un comportamento osservato sulla base di deduzioni.

Fotografia periodica del report a t~1669 (non simultanea alle raccolte) mostrava 8 autointersezioni della membrana e molte prese simultanee: ciò suggerisce geometria difficile, ma non prova che sia successo il medesimo fenomeno al pickup.

## Nuovo `ContactGeometryForensics` — solo osservazione

- `GameActivity` invoca `contactGeometry.pickup(...)` SOLO DOPO un guadagno reale e *già autorizzato dall'attuale codice*. Nessuna altra condizione di energia è cambiata. Include pickup nel mondo e nelle stanze, etichettati separatamente.
- Ogni evento include life, tempo simulato, azione scelta, legge fisica e posizione/raggio della risorsa, guadagno effettivo, le 10 posizioni delle masse motrici, tutte le 96 posizioni della membrana e le posizioni della scia grafica, identificate **separatamente** e relative alle coordinate della risorsa (decimi di unità). Include baricentro assoluto, punti in presa, punti rossi visibili, numero di autointersezioni.
- Registra con i **predicati veri preesistenti** se esisteva: corpo racchiudente il punto (`interior`), distanza dal segmento della membrana (<radius+2, `membraneEdge`), massa interna vicina (<radius+2, `internalMass`), oppure soltanto criterio di `extra18`. Il log conserva anche le distanze minime effettive e i due predicati booleani (material / near). Un evento `extra18` è prova di raccolta prima del contatto geometrico, ma non prova che la scia l'abbia provocata: la scia rimane un render passivo.
- La rilevazione `trap(...)` campiona al più una volta ogni otto secondi simulati; registra soltanto se **due casse distinte** hanno almeno quattro adesioni fisiche per cassa. Conta anche la condivisione delle stesse patch fra casse, e conserva tutte le coordinate della membrana, masse e scia nel momento dell'incastro. Non rilascia prese o induce movimenti.
- Ring buffer limitati a 18 pickup e 9 stati doppi di blocco; lunghezza massima 75.000 caratteri; rigetto soft di restore corrotto. Persistenza chiave `contact_geometry_forensics_v127` nei SharedPreferences della partita attiva (normal save, checkpoint scene, named save slots). Incluso automaticamente nel report `currentZero` inviato SOLO tramite condivisione esplicita già esistente, senza nuove autorizzazioni, permessi o connessioni.
- Il registratore non è consultato dal cervello, da `MaterialGeometry.touches`, dal motore né dal sistema energetico. `trailX,Y` rimane **soltanto renderizzata**; non è promossa a nuovo organo. Nessun reset di dati esperienziali.

## Test e limiti

QA preliminare: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38572931510 (SUCCESS, JVM incl. test di 96 punti doppi, serializzazione, cap memoria, non-mutazione corpo, Android debug build).

Questo aggiornamento **non dimostra retroattivamente** il punto esatto che ha raccolto le gemme nei filmati passati; abilita la misura prospettica del prossimo episodio senza modificare il comportamento. La ricostruzione usa coordinate arrotondate al decimo, che preservano il margine tra r+2 e r+18 salvo casi limite sub-decimali. Non è un video di ogni frame, ma fotografa gli istanti delle raccolte e campioni fisici degli incastri.

Prossimo passo: aggiornare l'APK sulla stessa installazione, far correre ZERO nel TEST MAX o x20 senza cancellare salvataggi, aspettare un evento simile, usare `Condividi dati con GPT`. Leggere `currentZero.contact_geometry_forensics_v127` e classificare ciascuna raccolta per origine, confrontare con scia e doppie prese. SOLO in caso di reale errore fisico dimostrato proporre un fix della geometria, non una correzione delle scelte di ZERO.

## Distribuzione finale firmata

Release `0.11.6-ota.127` (versionCode1127), APK `OVERMIND-ZERO.apk` 391948 byte, digest SHA256 `d879edcd15e7b6e72873b06c278110c518a8c36e9df9269227aed1d79708e4e4`.
Pre-release JVM+Android SUCCESS: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38572931510 .
Publisher ufficiale COMPLETED SUCCESS: https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38573545929 .
Pagina release: https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/tag/overmind-zero-v0.11.6-ota.127 .
Il manifest ufficiale su main indica `apkUrl=https://overmind-zero-updates.vercel.app/apk/overmind-zero-1127-d879edcd15e7.apk`. La raggiungibilità del proxy dal telefono non è stata provata; usare la release GitHub se il tasto Aggiorna non funziona.

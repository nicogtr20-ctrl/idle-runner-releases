# OVERMIND ZERO — 0116.9 Osservatore passivo della presa

## Scopo
Capire perche' ZERO puo' circondare una cassa e restare in **PRESA ON** per
secondi senza punti arancioni, mentre il cancello li mostra anche nelle vite
vergini. Nessuna conclusione sul "volere" di ZERO e nessuna modifica a
decisioni, pesi, soglie, attrito, collisioni, energia, disegno o ricompense.

## Campionamento
`GripContactObserver` legge **dopo** il solver del cancello e **prima/dopo**
quello delle casse. Conta per ciascun tipo di oggetto:
- scelta globale ON/OFF e numero di punti in contatto geometrico;
- punti che superano il comando neurale locale `GRIP_THRESHOLD`;
- punti che raggiungono `GRIP_HOLD` e soddisfano il rendering arancione;
- contatti con ON ma comando locale sotto soglia (veto);
- comando locale attivo anche con OFF generale;
- interruzioni del timer confrontando gli stati prima/dopo fisica cassa;
- interruzioni a faccia invariata o con geometria mutata;
- durata massima di contatto dello **stesso punto**, non solo del corpo;
- avanzamento reale del cancello e spostamento delle casse.

La traccia e' limitata a 190 eventi a intervalli di 0,8 s simulati durante
i contatti, con un riepilogo cumulativo della sessione e l'ultima vita.
`TOTAL` usa conteggi di **frame fisici**, non decisioni; `S` e' una
fotografia campionata, non una descrizione delle intenzioni.

## Isolamento
La registrazione e' separata dalle preferenze del cervello e dai **12 slot**
(`zero_grip_observer_v1169`). Il pulsante preesistente "CONDIVIDI DATI
CON GPT" include `gripContactObserver` solo su invio esplicito. Non esiste
upload in background. Il report non contiene token o identificativi.

Il test `GripContactObserverRegression` verifica comando ON ma veto,
comando locale durante OFF, evento di tenuta arancione, timer interrotto,
continuita' della vita precedente e osservazione senza mutazione.
Un eventuale difetto della fisica resta ipotesi finche' la traccia
non distingue almeno comando, contatto, tenuta e azzeramento.

Il ramo e' soltanto preparatorio. Nessun merge nella stabile ne' OTA senza
un'ulteriore autorizzazione.

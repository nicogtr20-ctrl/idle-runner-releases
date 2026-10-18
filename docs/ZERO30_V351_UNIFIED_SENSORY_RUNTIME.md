# ZERO 3.0 — V351: contratto sensoriale unico nel runtime reale

18 ottobre 2026. Master §§3,5,9,13–14,20; G2/G4/G7. PR #87 sperimentale.

## Lacuna e scelta prima dell'implementazione

V315 conserva la somatica completa ma non energia/danno; V344 fornisce già
le sensazioni vitali autentiche; V347 consegna alla GRU26 soltanto un riassunto
meccanico, senza spettro ottico. Riusare questi componenti, senza ricrearli.

Un frame immutabile V351 unisce V315, confine vitale V344, 24 canali spettrali
fisici già prodotti da Senses.spectral8 e 16 raggi realmente occlusi anche dalla
stanza. La GRU esistente diventa configurabile nella larghezza d'ingresso:
26 rimane il formato storico, 194 è il nuovo adattatore. Nessun ID, posizione
di risorsa, classe fisica o valore futuro entra nell'osservazione neurale.

L'osservazione 194 contiene: 20 feature V317, 24 spettri, 16 raggi, 16 settori
ciascuno per contatto/impulso/presa/estensione, 64 efferenze eseguite e 6
misure fisiologiche/corporee. I dati mancanti di efferenza hanno una maschera
esplicita. I dati grezzi V315 rimangono nel replay, senza distruggere differenze
fra le 96 patch e le 74 particelle interne.

Nuovo formato esperienza opzionale 351 conserva PRE e POST neurali completi,
con identità V315 e metadati vitali; i record storici 3222 restano identici.
Il blob GRU versionato conserva dimensioni e stato; il manifest atomico 348
rimane il proprietario congiunto di cervello, corpo, mondo, energia e replay.
Nuove nascite Android LAB usano V351; nessuna conversione dei profili 2.2
o dei checkpoint GRU26. La fisica candidata V346 respinta resta esclusa.

## Prove richieste e limiti

Test di fonte spettrale/occlusione, PRE/POST e rifiuto dati futuri, isolamento
degli array, efferenza realmente applicata, integrità e roundtrip del replay.
Vita reale GRU194 con ripresa a metà confrontata bytewise con una vita
continua; regressioni GRU26, V348, V350 e Android Debug. Nessuna promozione
G3/G4/G5/G6 o dichiarazione di ricerca delle risorse dalla sola presenza dei
sensori. Clock ancora fisso, critic e abilità richiedono prove separate.

## Risultati locali verificati

Compilazione JVM di tutte le classi pure Zero3 e della fisica canonica PASS.
V345 e V350 PASS senza cambiare i byte legacy; V348 24+24 vs48 PASS.
V351 sensori fisici/occlusione/immutabilità/PRE→POST→PRE e replay neurale PASS.
V351 vita GRU194 a 2000 decisioni: 1000 + nuovo processo JVM + 1000 identici
bytewise al controllo continuo, 63 segmenti e cache bounded. Come V328,
la fixture facilita energia fisica: non prova ricerca delle risorse.
CI remota 39118399048 e build Android Debug PASS; la validazione scientifica degli altri gate rimane distinta.
Il nuovo `tools/zero3_core_qa.py` riproduce la batteria e i fault del journal.

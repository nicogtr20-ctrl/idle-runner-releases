# OVERMIND ZERO — Esperimento 0116.6: presa visibile e continuità degli oggetti

**11 ottobre 2026.** Base 0.11.6-ota.72, con bagliori reali e finestre, e con diagnostica del cancello. Nessuna alterazione delle reti, premi, percezione o leggi fisiche.

## Osservazioni dell'utente

1. ZERO appena nato può mostrare immediatamente punte **arancioni** quando tocca il cancello; lo stesso colore non appare quando tiene una cassa.
2. Alcuni oggetti, comprese le casse manipolate attivamente, scompaiono all'improvviso durante una vita.

## Risultati dell'audit del codice

**Arancione**: `renderBody` disegna uno spark se `chamber!=null && surface.gripHold[i]>=Morphology.GRIP_HOLD`. Il requisito `chamber!=null` era soltanto una restrizione grafica: `SquareGripPuzzle.constrain` incrementa la stessa `gripHold` sulle casse anche senza stanza. Arancione significa presa fisica sostenuta >0,075 secondi simulati, non conoscenza del cancello, non prova di impugnatura bilaterale e non prova di spostamento. Un cervello nato da zero può selezionare casualmente un'azione adesiva o il comando locale di presa può superare la soglia; la combinazione può attivare adesione prima di qualunque associazione appresa.

**Sparizioni**: `extendHabitat()` viene eseguito ogni 13 secondi simulati, anche durante il contatto fisico. In caso di `environment.size()>24`, rimuoveva indiscriminatamente l'elemento più vecchio; i blocchi spesso vengono aggiunti in coppia alla sorgente `concealed`, che poteva essere rimossa separatamente. `activateChamber` creava la stanza con origine sul nucleo di ZERO e `excludeOrdinaryRoomObjects` scartava oggetti che si trovavano dentro il footprint della stanza (o li aveva raggiunti con il proprio movimento). Era dunque possibile perdere una cassa durante un tentativo di presa.

## Modifica

- `renderBody`: mostra arancione per qualunque punto con `gripHold>=GRIP_HOLD` e `touchSense>0.5`, anche senza stanza. Non cambia presa, motori né memoria.
- Nuova classe `HabitatContinuity`, *solo fisica/gestione della mappa*: la pulizia dei vecchi oggetti protegge quelli prossimi al nucleo di ZERO o realmente raggiunti dalle 96 particelle della membrana. Il limite di 24 è applicato riciclando **prima gli oggetti più vecchi non attivamente manipolati**. Rimane soft se tutte le entità sono davvero impegnate, per non violare la continuità mentre ZERO sperimenta; al successivo allontanamento possono venire liberate.
- Una coppia ancora **cassa + energia nascosta** viene rimossa *insieme* se è veramente lontana e non protetta, preservando coerenza. Quando la risorsa è stata rivelata e raccolta, rimane indipendente e la cassa può continuare a esistere.
- Una nuova stanza cerca una posizione fisicamente libera prima di apparire, invece di sovrapporsi agli oggetti esistenti e cancellarli. Se nessun posto adatto è trovato tra i candidati, la generazione viene posticipata di otto secondi simulati. La pulizia per i confini della stanza evita la cancellazione di oggetti ancora fisicamente manipolati.
- Non viene dato a ZERO alcun indizio speciale sul motivo di una sparizione, sulla classe fisica dell'oggetto o su una ricompensa. Questi sono interventi sull'integrità del mondo, non scorciatoie nel suo apprendimento.

## Limitazioni dichiarate

- Le risorse già realmente `consumed` devono **continuare a scomparire**: sono consumi energetici reali, non bug.
- Gli oggetti sufficientemente lontani e non coinvolti vengono ancora riciclati per evitare una crescita senza limite.
- Qualora tutti gli oggetti dentro il limite siano effettivamente vicini/toccati, il numero totale può superare temporaneamente 24: compromesso esplicito a favore dell'integrità del contatto.
- Una stanza può nascere più lontana di prima se la zona originale è occupata. Non è un target impartito a ZERO.
- Non si cambia il comportamento dei vecchi personaggi, ma le loro **esperienze future** potranno differire perché un oggetto ora non sparisce prematuramente; l'osservazione va separata da un miglioramento del cervello.

## Test di non regressione

`HabitatContinuity.main`: il blocco tenuto fisicamente e la sua sorgente nascosta resistono al limite degli oggetti; l'origine della stanza non ricopre il blocco; gli oggetti non impegnati si riciclano; la sorgente non rimane orfana. Suite storica Java completa e `assembleDebug`.

CI compara **byte per byte** con 0.11.6-ota.72 i file dei modelli cognitivi, il motore fisico, la logica della presa sul cancello e sulle casse, la memoria, la scena, la raccolta e il sistema di aggiornamento. Test e report documentati nel workflow `overmind-v1166-retention-qa.yml`.

Non disinstallare e non azzerare il personaggio. Installare sempre in sostituzione dell'APK firmata precedente dopo il completamento del publisher.

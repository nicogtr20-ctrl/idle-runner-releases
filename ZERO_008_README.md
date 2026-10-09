# ZERO-008 — Esperimento di scarsità, porta e presa fisica

## Intenzione
L'utente richiede molti più punti fisici e un ambiente che **obblighi ZERO a risolvere un problema**, non soltanto raggiungere pallini: risorse limitate, camera con nutrimento dietro un blocco, e manipolazione verificabile con due regioni del corpo.

## Implementazione iniziale
- **96 masse fisiche** nella membrana, derivata dai 32 della v0.7 senza modificare la rete di 74 pesi. Quantità di materiale ricalibrata; nessun braccio prefabbricato.
- I segnali energetici vengono **consumati definitivamente** al contatto, anziché tornare dopo 6 secondi. In carenza gli spawn ordinari sono rarefatti a uno circa ogni cinque ampliamenti della mappa.
- Dopo 70 secondi (oppure manualmente toccando l'area del test nel HUD), la fame aumenta e viene creata una stanza con ingresso ostruito da una **porta scorrevole rigida**. Il nutrimento si trova fisicamente dietro la porta; il corpo, la membrana e il nucleo interagiscono con pareti solide.
- **Meccanica della presa:** `ScarcityLab.solveGate` misura punti reali della membrana su **entrambe le facce opposte** della porta, con indici fisicamente distanziati sul contorno. Una singola spinta non genera alcun spostamento. Quando le superfici stringono contemporaneamente, la porta può scorrere secondo le velocità verticali misurate dei punti a contatto, con attrito, inerzia e arresti. Non vengono nominati arti né animazioni di presa.
- Test scientifici distinti: una presa bilaterale non equivale ad aver aperto la porta; aprirla non equivale ad aver raggiunto energia. Contatori/grandezze separati: eventi di presa, distanza massima porta, camere risolte, energie effettivamente raccolte.
- Nessun trucco: l'energia nella camera viene assimilata solo se **il nucleo** raggiunge la posizione fisica della risorsa, evitando raccolte telecinetiche tramite una membrana che attraversi pareti.
- Salvataggi separati `scarcity_elapsed_v08`, `scarcity_active_v08`, `room_success_v08`, `finite_energy_eaten_v08`, `grip_events_v08`, `gate_travel_v08`. I precedenti pesi motori, di manipolazione, morfologia e decisione restano intatti.

## Limitazioni importanti
Questo è un primo laboratorio fisico, non prova che ZERO sappia **già** afferrare o capire una barriera: la navigazione riceve ancora la direzione del target. La regola meccanica dei due contatti è programmata come fisica della presa; non è la scelta di due braccia predefinite. Occorrono test del movimento del gate *durante apprendimento su Android*, non solo unit test sintetici che simulano due punti in contatto.

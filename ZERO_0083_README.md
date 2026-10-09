# OVERMIND ZERO v0.8.3 — controllo di presa appreso
### Differenza fondamentale
In 0.8.2 due contatti fisici su facce opposte, anche senza un comando, generavano attrito sufficiente a spostare il cancello. In 0.8.3 questo è vietato. Ogni massa della membrana ha un sensore tattile e un **comando neurale di adesione separato**. ZERO nasce con il nuovo comando spento, non conosce la sua funzione. Soltanto punti indipendenti su facce opposte che mantengono il comando attivo e il contatto per almeno 75 ms possono generare trazione attraverso le loro velocità fisiche. Senza comando, contatto o solo con presa monolaterale, il blocco resta fermo.

### Migrazione
- I 74 pesi della membrana v0.8.2 conservano esattamente i loro indici e valori.
- Otto pesi nuovi append-only (6 connessioni, bias e ingresso tattile) portano il modello a 82 pesi e controllano l'uscita di presa condivisa su tutti i 96 punti. Pesi nuovi zero all'avvio.
- `morph_brain_v07` resta compatibile con la vecchia stringa da 74 pesi; nel salvataggio successivo viene scritta la versione estesa a 82. La pagina CERVELLO mostra l'architettura estesa.
- Nessuna azione ad alto livello “usa due braccia”: sensore, comando, attrito e urto sono implementati; la rete deve scoprire come pilotarli.
- `attemptGate` dà priorità a esplorare i pesi dell'adesione e usa conseguenze misurate: presenza di adesione sui punti realmente a contatto, presa sostenuta, spostamento e nutrimento raggiunto. Il semplice urto non viene premiato come presa.
- La porta rimane davanti al muro come nella 0.8.2. La morte per fame e il respawn mantengono la memoria.

### Verifiche
I test JVM controllano: due contatti passivi NON aprono, una sola adesione NON apre, due adesioni indipendenti sostenute possono trascinare, rilasciare una fa cessare la trazione, e i vecchi 74 pesi vengono migrati senza alterazioni. APK Android da compilare e installare sopra il precedente package firmato. In gioco sono visibili tocchi, adesioni attive, prese vere e traslazione del cancello.

# OVERMIND: ZERO — Whitepaper di progetto

**Versione:** 0.1 (documento vivo) · **Data:** 9 ottobre 2026 · **Stato:** prototipo di ricerca Android

## Sintesi
OVERMIND: ZERO è un esperimento di *artificial life* trasformato gradualmente in gioco mobile 2D. Il protagonista è una piccola massa scura dall'aspetto di ferrofluido/organismo simbionte. Il giocatore **non** controlla ZERO né le parla; è un osservatore invisibile che può modificare la simulazione, finanziare interventi con esperienza guadagnata e osservare come l'entità si comporta ed evolve.

La vera promessa è l'apprendimento **verificabile**: ZERO non deve recitare un'intelligenza con animazioni prefabbricate. Deve provare contrazioni e interazioni, rilevare gli effetti e aggiornare una propria rappresentazione del mondo.

## Visione a lungo termine
1. ZERO nasce senza memoria episodica, conoscenza del mondo o sequenze motorie pre-addestrate.
2. Possiede sensori elementari, un corpo deformabile, esigenze minime di energia/integrità e capacità di contrarre/espandere materia.
3. Impara relazioni causa-effetto: deformazione → spostamento, contatto → energia/danno, contatto multiplo → movimento di oggetti.
4. Apprende modalità per afferrare/spostare oggetti, aggirare ostacoli, sfruttare risorse e distribuire il proprio corpo nella mappa.
5. Può sviluppare preferenze, strategie locali, esplorazione e persistenza della memoria senza ricevere una lista prestabilita di obiettivi narrativi.
6. Il giocatore guadagna XP da scoperte e sviluppo e li spende su elementi del mondo, non su comandi diretti a ZERO.
7. ZERO non sa dell'esistenza del giocatore; variazioni anomale del mondo potrebbero condurla a elaborare ipotesi causali *solo se supportate dall'architettura futura*.

## Principi di design
- **Autonomia simulata, non teatro:** dichiarare quali decisioni vengono apprese e quali vincoli sono fissi.
- **Corpo organico realmente deformabile:** massa nera e pseudopodi procedurali, no braccia predefinite o sprite animati.
- **Nessun obbligo di conversazione:** niente chat o notifiche antropomorfiche attribuite alla creatura come suoi pensieri.
- **Mondo parzialmente ignoto:** le proprietà degli oggetti sono nel motore della simulazione e si scoprono con esperienze.
- **Esperienza come due sistemi:** memoria/apprendimento di ZERO persistente, XP del giocatore spendibili e indipendenti.
- **Approccio offline-first:** no API a pagamento, piccolo cervello allenabile sul telefono, priorità a fluidità e stabilità.
- **Misure empiriche:** confronti virgin vs trained e su direzioni/mappe non usate in addestramento.
- **Nessuna falsa pretesa:** non è cosciente, non è AGI, non possiede ragionamento linguistico autonomo.

## Risultati effettivamente osservati
- ZERO-001 ha addestrato da zero un controllore neurale da 62 parametri su corpo simulato con 10 punti di superficie.
- Sul Galaxy S21 l'utente ha osservato spontanee contrazioni e spostamento verso uno stimolo, anziché una camminata animata.
- Dopo 2.467 tentativi, confronto su direzioni nuove: controllo vergine -3,6 vs addestrato +179,7 (screenshot utente).
- ZERO-002, 4.772 tentativi: baseline +2,9 vs addestrata +204,4, 49 contatti ed effetti incontrati 3/3 (screenshot utente).
- Ciò dimostra progresso motorio nel **simulatore specifico**, non causalità generale o intelligenza general-purpose.
- Il sistema di oggetti attuale usa appena tre firme e apprendimento associativo: il numero 3/3 indica tipi incontrati, **non** la comprensione del mondo.

## Limiti tecnici correnti
- Le azioni del controllore sono contrazione radiale e aderenza di punti del corpo, in un simulatore semplificato.
- Il controllore motorio riceve ancora una direzione di stimolo. Non ha scoperto spontaneamente l'obiettivo di cercarlo.
- L'ambiente ha leggi fisiche predisposte; il classificatore degli effetti è molto semplice.
- La grafica è stilizzata, non una simulazione visivamente fotorealistica di vero ferrofluido.
- Il processo di addestramento è neuroevoluzione di pesi con fitness misurato, non un LLM e non un modello del mondo completo.
- L'algoritmo va stressato su mappe fisicamente diverse: alcune strategie possono sfruttare particolarità del simulatore.

## Cosa rende il gioco interessante
Osservare ZERO imparare a usare il proprio corpo, scoprire elementi del mondo, sopravvivere ed espandersi. Il giocatore modifica il mondo spendendo XP ottenuti da esperienze della simulazione, senza impartire ordini alla creatura e senza sapere in anticipo come reagirà.

## Milestone
- M0: controller motorio vergine e apprendimento verificato (raggiunta a livello di prototipo).
- M1: conseguenze di oggetti ignoti e memoria associativa (primo prototipo disponibile).
- M2: oggetti spostati da forze fisiche del corpo, apprendimento di contatto/manipolazione (prossimo studio, *sospeso per aggiornamenti*).
- M3: modello predittivo dello spazio e corpo con pseudopodi dinamici.
- M4: vera simulazione persistente con ciclo energetico, espansione e nodi.
- M5: gioco completo con UX, grafica ferrofluid, interventi divini e XP.

## Come aggiornare questo documento
Quando cambiano la visione, gli assunti sull'IA, i rischi o i traguardi, modificare questa pagina; aggiungere **ogni release** a `docs/CHANGELOG.md`; aggiornare il passaggio di consegne `docs/HANDOFF.md`.

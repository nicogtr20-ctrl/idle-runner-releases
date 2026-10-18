# ZERO 3.0 — V354: candidata integrata per il telefono

18 ottobre 2026. Stato: implementazione integrata per la prova Android; efficacia comportamentale e validazione Galaxy S21 ancora da misurare. Questo rapporto aggiorna i precedenti stati V353 senza trasformare i relativi fallimenti scientifici in approvazioni. [ADR e alternative](ZERO30_V354_COMPLETION_ADR.md).

## Cosa viene eseguito davvero

| Area del progetto | Implementazione e caller effettivo | Verifica e limiti |
|---|---|---|
| Corpo e mondo | `Zero3RuntimeV325` mantiene96 siti/74 particelle,64 efferenze e integra habitat, lastre, stanza/cancello, tempeste e ledger reale; `Zero3WorldBirthV354` genera una normale vita | Correzione congiunta V354 su tutti i solidi e crossing; nessuna forza sul COM, andatura o coordinata bersaglio al cervello |
| Contatto | `Zero3JointConstraintV354`, osservazione V319 del proprietario fisico fra tutte le BOX/lastre/cancello |4/4 scene da400 decisioni; fixture reale del difetto1514 riparata senza alterare comandi/timer,21 prese mantenute;20 vite/8000 decisioni indipendenti,46548 frame con presa e1726 bilaterali. Non dimostra locomozione appresa |
| Cervello centrale | GRU64 esistente con194 ingressi, unico attore, critic/target critic, clock effettivo0,25–0,65s | Nessuna seconda autorità motoria. Opzioni richiamate all'interno dell'attore; nessun gradiente REINFORCE alla proposta non eseguita |
| Memoria procedurale e obiettivi | `Zero3ExecutiveV354`:16 opzioni, proposta dai parametri realmente eseguiti, valore contestuale appreso con energia reale, obiettivi temporali e interruzione per danno | Candidata→probation→champion, controllo A/B temporale, rollback e64 metadati ritirati. Il test controllato esercita promozione/ritiro; non certifica abilità emergenti sul telefono |
| Modello e planning |3 predittori fisici V353,12 teste; `Zero3PlannerV354` condiziona fino a3 passi sui comandi scelti |2/3 passi disabilitati fino a64 sequenze heldout e vantaggio preregistrato. Nessuna traiettoria immaginata entra nel replay reale. Progresso informativo distinto da energia |
| Train e validazione |12 decisioni TRAIN e3 consecutive heldout ogni15, provenienza nel journal | Intero cervello congelato durante heldout. Modalità sola osservazione avanza fisica e memoria temporale ma congela pesi, replay appreso e promozioni |
| Crescita | Neurogenesi shadow V353 con training/validazione e veto, stessa generazione di checkpoint | Nessuna crescita utile scientificamente dimostrata; shadow respinta non aumenta i neuroni mantenuti |
| Continuità | Schema354 atomico V328/V329 con corpo, mondo, GRU, cortex, adesione, modello, planner, opzioni, ecologia, modalità e RNG |10000 decisioni:5000+nuovo JVM+5000 identiche al riferimento continuo. Rollback9984, corruzione segmenti, doppia corruzione e orfani verificati. La fixture energetica lunga misura persistenza, non sopravvivenza autonoma |
| Nuova vita | Eredità esplicita da profilo genitore ad altro slot vuoto | Pesi centrali/cortex/presa/opzioni mantenuti; working state e replay valore ripartono. Genitore conservato immutato e genealogia persistita |
| UI | `Zero3DashboardActivityV354`: Gioco, Panoramica, Cervello, Corpo, Abilità, Evoluzione, Percezione, Memoria e obiettivi, Analisi, Impostazioni | Snapshot difensivo di un confine completato; ogni metrica proviene dal runtime, assenze dichiarate |
| Atlante | `Zero3NeuralGraphV354` e `Zero3AtlasViewV354`: nodi effettivi, pesi selezionabili,3D illustrativo, filtri/pan/zoom |420 unità di calcolo base; particelle e attuatori non contati come neuroni. Connessioni visualizzate limitate e omissioni esplicite; attività non acquisita grigia |
| Profili e backup |12 slot V3 separati; clone, ZIP verificato, SAF import/export, eredità, telemetria locale | No sovrascrittura di slot occupati; limiti file/dimensione, rifiuto traversal/duplicati; test integrale import/clone/integrità |
| Android | Singolo executor owner condiviso anche fra ricreazioni Activity, autosave32, pausa/save, x1/x5/x20, TEST MAX esplicito, updater esistente | Build e test nativo API35, tutte10 pagine, dati reali, osservazione congelata e riapertura. Prestazione/termica/batteria sul S21 restano da misurare |

## Cosa conserviamo della 2.2

Manteniamo corpo e leggi fisiche, generazione/habitat, segnali sensoriali autentici, cortex e adesione appresi, GRU64, archivio segmentato verificato e updater/firma. Non rigeneriamo reti già approvate. I12 slot storici restano nel percorso precedente e sono apribili dalle Impostazioni V3; i12 slot V3 usano cartelle separate. Nessuna conversione implicita, cancellazione o reset.

## Evidenze riproducibili

Comando completo: `python3 tools/zero3_core_qa.py --variable-clock --cognitive --completion --independent --long-life 10000`.

Suite locale e [CI core39131534928](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39131534928) PASS, inclusa compilazione Android. In quella esecuzione il test nativo passava, ma il job raccolta PNG falliva: non viene contato come CI interamente verde. La successiva verifica della UI e della firma viene registrata qui a conclusione.

## Installazione e prova sul Galaxy S21

La candidata privata usa `com.overmind.zero.ota`, certificato stabile originale e codice1172, nome `3.0-candidate.354`. Il workflow produce l'APK soltanto dopo core completo e UI nativa; verifica firma/package/versione e salva hash/provenienza. Non pubblica release né modifica manifest OTA172. La candidata apre V3 di default rispettando la scelta esplicita dell'interfaccia storica.

1. Installare la candidata sopra l'app esistente; conservare dati e non disinstallare. Un eventuale rifiuto dell'installer va letto prima di cambiare versione o dati.
2. Aprire Impostazioni, scegliere uno slot V3 vuoto e creare un nuovo cervello. Partire da x1, controllare corpo e riserva, poi x5/x20.
3. Pausare e riaprire l'app; controllare stesso profilo/decisione, ripresa e backup. Provare sola osservazione: fisica avanza, conteggi train/validazione restano fermi.
4. Controllare le10 pagine, selezione nodi/pesi, clone e import in uno slot vuoto. TEST MAX si avvia solo esplicitamente e si arresta con PAUSA/uscita.
5. Annotare durata reale, velocità simulata, heap, temperatura, batteria, eventuali arresti e risultato motorio; esportare telemetria/backup locale per rendere riproducibile il difetto.

## Gate ancora da dimostrare

La candidata rende provabili i componenti integrati. Non chiude automaticamente G3/G4/G5/G6/G7 scientifici: locomozione autonoma, vantaggio del valore/planning, generalizzazione delle opzioni e beneficio della crescita richiedono confronti indipendenti. G9 richiede il Galaxy S21; G10 stabile resta successivo alla prova e alla decisione di pubblicare. La vecchia V346 fallita2/4 resta respinta. Nessun risultato di test viene presentato come abilità del cervello appena nato.

## Copertura della specifica UI

UI-01/02/05/06/08/13/14/15 coperti dal provider reale e dalla strumentazione nativa; UI-04 dal protocollo reale e fixture di lifecycle, non da una competenza appresa certificata. UI-07 e UI-10 hanno controlli negativi JVM su snapshot/integrità e slot; interazioni SAF e cambio legacy richiedono ulteriore prova dispositivo. UI-03 resta parziale: topologia/contatori shadow autentici, nessuna promozione utile forzata. UI-09/16 richiedono S21; UI-11 usa updater esistente senza pubblicare OTA; UI-12 separa esplicitamente TEST MAX, che avanza questo organismo. La schermata Evoluzione espone conteggi reali, non ancora una cronologia dettagliata di tutti i delta dei pesi. Questi limiti non vanno cancellati dal tracker.

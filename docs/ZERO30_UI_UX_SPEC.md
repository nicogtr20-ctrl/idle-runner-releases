# ZERO 3.0 — NUOVA INTERFACCIA GIOCO, IMPOSTAZIONI, CERVELLO, NEURONI, ABILITÀ ED EVOLUZIONE

> **V3.18 DATI REALI NON COLLEGATI ALLA UI (17-10-2026):** [report](ZERO30_V318_LEARNED_ADHESION.md). Il prototipo ottiene veri frame `gripHold` BLIND 4.600 e bilaterali 2.021 con 16 motoneuroni trainabili, ma attiva adesione inutilmente il 45,58% del tempo fuori dalle scene BOX e consuma il 56,9% di lavoro in più sui BOX; **gate FAIL**. Futuro pannello Corpo/Presa deve separare intent `adhesionPreference`, output `command>GRIP_THRESHOLD`, contatto, `gripHold`, due facce legali, costo `gel.work` e metri movimento fisico; visualizzare stato dell'abilità come **NON VALIDATA**, non celebrare una competenza. **UI 3D ZERO3 ancora non implementata.**

> **V3.18 — NUOVA FONTE PROTOTIPO MOTOR HEAD ADESIVO (17/10/2026):** [test del motoneurone e specifiche metriche reali](ZERO30_V318_LEARNED_ADHESION.md): i futuri overlay Corpo/Presa dovranno separare `adhesionPreference` dell'intenzione, comando neuronale effettivamente eseguito oltre soglia, tocco e `gripHold` legale (proprietario scatola), prese bilaterali, lavoro e inutili attivazioni FREE/FAR. Mostrare *learning vs frozen* e un gate scientifico, senza fingere nuove abilità o neuroni cresciuti. **La UI V3 resta non implementata.**

> **DATI AUTENTICI PER FUTURA UI 3.0 / V3.17 (17/10/2026):** [prova e limiti](ZERO30_V317_AUTONOMOUS_MOTOR_LOOP.md). In lab un encoder ricorrente sceglie 2.048 intenzioni reali ed esegue 64D nel corpo; 2.048 updates, ma gate G3 FALLITO: manca vantaggio su policy congelata. **Metrica che la UI DEVE mostrare senza abbellimenti:** grip command sopra soglia fisica 0/operazioni; patch grip command sopra soglia durante contatto 0; gripHold reale 0. Separare intenzione adesione, output corticale adesivo, patch adesione, grip realmente mantenuto, e contact/pulse. Non mostrare skill, neuroni aggiunti o performance non validata. **Nuova UI V3 NON implementata.**

> **STATO DATI UI ZERO3.16 (17/10/2026):** [contratto di intenzione tipizzato e replay completo](ZERO30_V316_INTENT_PERSISTENCE.md) testati solo nel laboratorio E2; 2.048 esperienze fisiche con 64 efferenze, 96×12 percezioni locali, 16 raggi, 74×7 fluidi e 4 checkpoint a due generazioni, proprietario unico per processo. La futura UI potrà mostrare **intenzione prima dell'azione e movimento realmente osservato dopo**, e checkpoint VERIFIED/ROLLED_BACK/CORRUPT, ma solo collegata a provider read-only di codice live. **Questa UI non è implementata**: nessuna metrica neurale, locomozione o abilità deve essere inventata, e questi replay sono esperienza di LABORATORIO non abilità apprese.

> **STATO DATI UI V3.15 (17/10/2026):** esiste un primo schema Java **sperimentale**, testato in [V3.15](ZERO30_V315_SENSORY_CAUSAL_CONTRACT.md), per alimentare in futuro le pagine Corpo/Percezione/Esperienze con 96×12 siti cutanei, 16 raggi, 48 propriocezioni, 74×7 particelle interne e 64 efferenze, con `touchSense`, `touchPulse`, `gripHold` e `gripCommand` **separati**. 2.048 esperienze reali di laboratorio registrate in RAM, energia/danno **NON DISPONIBILI** in questi test. **NESSUNA schermata UI V3 è ancora stata implementata né è collegata al gameplay**: non creare numeri di neuroni, grafici o abilità falsi. La UI resta deliverable successivo obbligatorio con provider read-only e true status.

> **CONTRATTO SENSORIALE VERIFICATO V3.12 (17/10/2026):** la pagina Corpo/Percezione del futuro ZERO 3.0 dovrà visualizzare separatamente `touchSense` (contatto fisico con facce solide), `touchPulse` (pressione meccanica dei dischi) e `gripHold` (presa valida posseduta da un solido, distinta dal comando volontario `gripCommand`). Non sommare i valori fingendo siano eventi equivalenti, non dichiarare adesione a partire dal semplice contatto. [Prova su mondo vero](ZERO30_MOTOR_V312_REAL_CONTACT_FIXTURE.md). **La UI è ancora una specifica: non implementata.**

> **DOCUMENTO VINCOLANTE** per le modifiche della GUI di OVERMIND ZERO 3.0. Leggere insieme al [MASTER BLUEPRINT](ZERO30_MASTER_BLUEPRINT.md) e al [CONTRATTO TECNICO](ZERO30_IMPLEMENTATION_CONTRACT.md).
>
> **Richiesta esplicita dell'utente (16/10/2026):** le nuove architetture di apprendimento richiedono **una nuova interfaccia delle impostazioni, abilità, neuroni, evoluzione e tutte le relative schermate, realmente collegate al 3.0, che sostituirà quella corrente 2.2.**
>
> **Stato:** SPECIFICA v1.0, non implementata. Non sostenere che una schermata esista o sia collegata a dati 3.0 finché non c'è codice e verifica.

## 0. Obiettivo e principi

ZERO 3.0 è un progetto di organismo neurale autonomo. L'interfaccia deve renderlo osservabile e piacevole da esplorare, **stile videogioco/simulazione neurale sci-fi pulita** (pannelli curati, neon sottile, animazioni non invadenti, sfondo scuro e atlas 3D) senza rendere opachi i dati reali.

La schermata 3.0 deve sostituire la 2.2 per tutti i profili 3.0, non limitarsi a rinominare etichette o aggiungere sezioni secondarie. La UI del legacy può restare accessibile **soltanto quando si apre un profilo 2.2**, marcata "storica / 2.2" e **mai** presentata come stato del 3.0.

I dati devono rispettare il **read-only brain boundary**: leggere uno snapshot non deve allenare una rete, assegnare un'intenzione, cambiare una configurazione o registrare una esperienza.

## 1. Mappa navigazione principale

\`\`\`text
ZERO 3.0
|
├─ GIOCO
│  ├─ Simulazione organismo / HUD
│  ├─ Velocità x1 / x5 / x20
│  ├─ TEST MAX (pulsante separato, per non premerlo per errore)
│  └─ Stato sessione, riserva, vita, ultimo feedback neurale
|
├─ PANORAMICA
│  ├─ Stato corpo e cervello
│  ├─ Esperienze, progresso reale e limiti
│  ├─ Ultima competenza verificata
│  └─ Progresso di apprendimento rispetto ai baseline
|
├─ CERVELLO
│  ├─ Atlante neurale 3D
│  ├─ Architettura / unità e sinapsi
│  ├─ Attività ricorrente / intenzioni
│  ├─ Valore / actor / critic
│  ├─ Previsione / world model / fiducia
│  └─ Neurogenesi
|
├─ CORPO
│  ├─ Vista organismo / distribuzione materiale
│  ├─ 96 punti membrana / 74 punti interni
│  ├─ 16 regioni / 64 attuatori
│  ├─ Tensione / contatto / presa / inerzia
│  ├─ Corteccia motoria e apprendimento
│  └─ Intenzione vs movimento realmente ottenuto
|
├─ ABILITÀ
│  ├─ Panoramica competenze
│  ├─ Candidate in apprendimento
│  ├─ Verificate / richiamate / consolidate
│  ├─ Fallite / decadute
│  └─ Dettaglio e prove di ciascuna abilità
|
├─ EVOLUZIONE
│  ├─ Nascita e storia del cervello
│  ├─ Crescita neurale confermata
│  ├─ Modifiche dei pesi / plasticità
│  ├─ Capacità acquisite e conservate
│  └─ Confronti checkpoint
|
├─ PERCEZIONE
│  ├─ Vista / luce / raggi
│  ├─ Tatto / adesione
│  ├─ Propriocezione
│  ├─ Interocezione
│  └─ Comandi effettivi (efference)
|
├─ MEMORIA E OBIETTIVI
│  ├─ Memoria ricorrente / episodica / procedurale
│  ├─ Episodi significativi
│  ├─ Scopi autonomi / continuità
│  ├─ Modello del mondo e piano
│  └─ Competenza riutilizzata: prove reali
|
├─ ANALISI
│  ├─ Diagnostica apprendimento
│  ├─ Motricità e stabilità
│  ├─ Validazione / holdouts
│  ├─ Benchmark A/B vecchio vs nuovo
│  ├─ Flight recorder degli eventi
│  └─ Condivisione telemetria volontaria
|
└─ IMPOSTAZIONI
   ├─ Gestione simulazione
   ├─ Modalità di allenamento / osservazione
   ├─ Backup, profili e 12 slot
   ├─ Display / grafica / performance
   ├─ Diagnostica avanzata e test
   ├─ Informazioni su V3 e sorgente dati
   └─ Aggiornamenti OTA / AGGIORNA
\`\`\`

Nessun menu "2.2" deve diventare l'unica via per vedere un fenomeno 3.0. Le sezioni devono essere visibili e raggiungibili; eventuali schede non disponibili devono mostrare il motivo e non inventare contenuti.

## 2. Layout e UI / stile

- Design Android Java/Canvas (o architettura UI Android verificata), leggibile su **Galaxy S21** verticale.
- Mood: neural sci-fi biologico, black/deep navy, accenti cyan/violet ammorbiditi, micro-pulsazioni per vera attività; non una console piatta da sviluppatore.
- Gerarchia: massimo due tap per raggiungere Cervello/Corpo/Abilità/Impostazioni; menu a schede o tab con nome completo e indicatore percorso.
- Titolo di sezione, sottotitolo breve, stato V3, timestamp dello snapshot e "?" con definizione della metrica.
- Corpo e atlante neurale con gesture zoom/pan/rotazione; fallback 2D se GPU/performance non consente 3D. L'orientamento deve rispettare la geometria **reale**, non un cerchio prestabilito.
- Grafici coerenti, assi e unità (esperienze, secondi SIMULATI, joule/resource unit fisiche, U, percentuali); distinguere esperienze, transizioni train, heldout, frame.
- **Nessuna card con numeri finti**; usare "non disponibile", "non validato", "in attesa di dati" quando manca la misura.
- GPU/Canvas: snapshot throttled, allocazioni contenute, non richiedere la scansione completa dei pesi ogni frame; configurabile dettaglio 3D e animazioni.
- Accessibilità: dimensioni touch, contrasto, screen-reader labels ove disponibili, safe area, leggibilità con font maggiori.

## 3. PANORAMICA (Home ZERO 3.0)

L'home è un pannello di sintesi, non un dump di parametri.

Elementi:
1. Organismo vivo, stato corrente, energia, tempo di vita **simulato**, record.
2. Stato cervello: architettura \`ZUAB 3.0\`, fase apprendimento, numero neuroni reali e nuove unità confermate.
3. Indicatore mobilità misurato (netto, lordo, allineamento) e differenza dall'inizio vita / da vergine.
4. Competenze: candidate, verificate, riutilizzate con successo.
5. Modello mondo: errore heldout per testa e confronto baseline, no fiducia numerica falsa.
6. Stato ultimo checkpoint, esito save, eventuale regressione.
7. Pulsanti navigazione reali: Cervello, Corpo, Abilità, Evoluzione, Analisi, Impostazioni.

Stato metriche con provenienza e data; la pagina non deve attivare alcun training.

## 4. CERVELLO, NEURONI E ATLANTE 3D

La schermata più riconoscibile e scenografica dell'app, ma fedele alla rete.

### 4.1 Scena neurale 3D

Ogni nodo 3D = **un'unità neurale realmente presente** (encoder, ricorrente centrale, motorio, crescita). Le sinapsi visualizzate corrispondono ai pesi veri, con filtro soglia visuale **dichiarato** e contatore dei pesi/nodi nascosti dal filtro. Non confondere particelle materiali con neuroni.

Interazioni:
- rotazione / zoom / pan;
- filtri per famiglia neurale, modulo, regione o livello;
- tap nodo -> ID, tipo, dimensione input, segnali, parametri, attività effettiva, data creazione, cambiamenti dall'ultimo checkpoint;
- tap collegamento -> origine/destinazione, peso reale, segno, importanza/gate se disponibile;
- contatori: neuroni originali, aggiunti, candidati, respinti, sinapsi effettive, parametri, ampiezza dell'attività;
- crescita visibile quando la nuova unità è **davvero accettata e collegata**.
- grafica 3D non deve introdurre una "mente" diversa: read-only del modello reale.

### 4.2 Sottosezioni cervello

**Ricorrenza:** stato di memoria, stabilità, gradient norm, evoluzione della rete. Non esporre un valore fittizio "intelligenza 96%" senza definizione.

**Actor:** ultima intenzione volontaria e distribuzione/confidenza stimata; mostra come è stata scelta, senza spacciarla per prova di successo.

**Critic:** valore stimato, incertezza, cronologia confrontata con outcome reali.

**World model:** previsione energia/movimento/tatto/danno, baseline, MSE, fiducia per testa, campioni indipendenti.

**Planner:** uso abilitato da fiducia, rollout solo ipotetico, contributo verificato alla decisione. Mai scena mentale mostrata come vera esperienza.

**Neurogenesi:** neuroni in addestramento shadow e unità definitivamente approvate; test di performance e rollback.

## 5. CORPO SIMBIONTE E CORTECCIA

**Requisito aggiuntivo per V3.1:** quando esisterà un vero provider dati di sviluppo motorio, visualizzare in Corpo/Analisi il *canale attuatore in esplorazione*, l'ampiezza del probe, il costo, l'aggiornamento effettivo dei pesi e lo **stato del gate** (candidato, rifiutato o validato), con distinzione tra esperimenti ripetuti in laboratorio e competenza imparata nell'app. Vietato presentare lo screening come abilità consolidata. Documento tecnico: [V3.1 motricità](ZERO30_MOTOR_V31_ENGINEERING.md).



### 5.1 Vista anatomica interattiva

- 96 punti della membrana, 74 masse interne (10 body + 64 liquid), registrati come materia, NON neuroni;
- 16 settori di comando, differenze locali, pseudopodi, area, volume, deformazione, orientamento reale;
- overlays switchable: normal del tessuto, velocità, strain, grip command vs actual hold, contatto, energia muscolare, reazioni esterne, resistenza;
- frame/post-collisione; colore e grafica leggibile anche con organismo piegato/ruotato.

### 5.2 Corteccia neurale

Toccare una regione mostra:
- ingresso sensoriale locale e vicini;
- intenzione globale ricevuta;
- quattro canali di output **effettivamente eseguiti**;
- stato recurrent e adattamenti locali;
- variazione dei pesi da nascita/checkpoint (unità e ordine reali);
- frequenza reale delle update, credito motorio, blocchi con motivo.

Doppia freccia opzionale \`INTENZIONE\` vs \`RISULTATO\` nel mondo, con le stesse coordinate e range, senza fare la "correzione direzionale" come azione nascosta.

### 5.3 Misure motorie

- distanza netto/lordo, coerenza con intenzione, tempo di immobilità;
- energia spesa per progresso, controllo muscolare attivo, inerzia accumulata;
- prove contaminate vs pulite, soglie, validazioni, di cosa ha imparato;
- benchmark virgin/current e storici allineati al tempo simulato.

## 6. ABILITÀ

Organizzate per lifecycle, non per vecchi moduli tecnici.

Schede richieste:
- **IN APPRENDIMENTO:** candidata, contesto, numero train, miglioramento, errori.
- **VERIFICATE:** campioni indipendenti e risultato, affidabilità, variabilità.
- **CONSOLIDATE:** salvate e disponibili al richiamo, contesto match, miglioramento costo/tempo.
- **UTILIZZATE:** ultimi utilizzi e outcome, successo/revert, confronto A/B.
- **DECADUTE/SCARTATE:** motivo esclusione, storia conservata.

Dettaglio abilità: \`skillId\`, che cosa misura, prova reale, data prima/ultima, intervalli/episodi distinti, stato di validazione, effetto motorio, test su contesti diversi, peso memoria, campioni. Non dichiarare "camminata appresa" solo perché esistono parametri motori.

Un'abilità nuova appare automaticamente dai dati 3.0, non da una stringa precompilata. Disabilitare (o non implementare) pulsanti di "forza abilità" che rompano autonomia; eventuale laboratorio è separato dalla vita reale.

## 7. EVOLUZIONE

Pannello di storia reale dell'apprendimento:
- timeline neuroni aggiunti/respinti, crescita reale dei collegamenti;
- cambiamento effettivo dei pesi; numero update e **mean absolute delta / norm**;
- modelli che superano baseline solo quando evidenza indipendente;
- skill prima/dopo, difficoltà superate e regressioni;
- capacità mantenute dopo morte, resume e aggiornamento;
- confronti tra checkpoint, versioni e seed, con tag legacy v2.2 e v3.0.

Non ridurre evoluzione a "neuroni totali" o "età". Distinguere unità, pesi, sinapsi, competenze, esperienze.

## 8. PERCEZIONE E MEMORIA

**Percezione:** 5/6 famiglie sensoriali, visione direzionale, contatti, interocezione, propriocezione, comandi realmente applicati e maschere invalidi, con sezione "cosa riceve il cervello" vs "dato world debug non visibile al cervello".

**Memoria:** memoria di lavoro recente, episodi distinti, replay per train, profilo del consolidamento, impronte di competenze, stato salvataggio/restore. Filtri tempo simulato e vita.

**Obiettivi e pianificazione:** intenzione corrente e persistenza con motivo interruzione; valori stimati, fonti sensoriali **anonime** utili in seguito a esperienza reale; previsioni del mondo separate dagli eventi osservati. Non mostrare una gemma in modo privilegiato al controller.

## 9. ANALISI E BENCHMARK

Diagnostica non cosmetica:
- KPI train / validation / stopped with reason;
- predizione vs baseline nel tempo (per componente);
- peso update effettivo e gradient norms (no silent learning);
- stabilità motor, proprio, nocontact/hold, scene/seed;
- timeline decision ID, intent, actuator revision, physics effect, consumed energy, learned target;
- A/B matched comparison with seed, checkpoint, version, confidence; no cherry-pick;
- esportazione consentita solo tramite consenso dell'utente, non caricamento automatico segreto;
- modalità «Test completo» chiaramente separata da gameplay, con test read-only sul cervello reale o clone isolato per prove attive; mai allenare profilo utente mentre si promette analisi passiva.

Il pulsante **TEST MAX** rimane fisicamente separato da x1/x5/x20; il sistema test non può cancellare un checkpoint. Etichette chiare per secondi reali/simulati.

## 10. IMPOSTAZIONI ZERO 3.0 — nuova struttura

Questa schermata va **riprogettata interamente**, non semplicemente ricolorata. Le opzioni sono raggruppate e collegate ai veri sottosistemi 3.0.

### 10.1 Generali / simulazione
- Nuova vita / nuovi cervelli: azione distinta, con conferma e backup, nessun reset involontario.
- Velocità, grafica, consumo CPU, livello dettaglio della visualizzazione neurale.
- Stato attivo/pausa.
- Preferenze di prestazioni per S21 e accessibilità.

### 10.2 Apprendimento
- Stato \`apprendimento attivo\` e \`solo osservazione\`, con effetto reale e visibile su trainer; niente switch fittizio.
- \`VALIDATION MODE\`/benchmark solo quando implementati davvero, mostrando se il test opera su clone e non modifica il profilo.
- Configurazioni avanzate e iperparametri solo in schermata sviluppatore, con ripristino e controlli sulle incompatibilità checkpoint. Di default nessun "massimizza intelligenza" arbitrario.
- Stato coda esperienza, note sui problemi/blocchi.

### 10.3 Memoria e dati
- Slot ZERO (12 esistenti), salvataggio, clone, backup con integrità.
- Versione cervello, compatibilità e conversione (v2.2 read-only o v3.0).
- Esportazione/importazione e condivisione telemetria solo con conferma e tracciabilità.
- Nessuna funzione cancella automaticamente l'originale legacy.

### 10.4 Analisi e test
- Esegui test diagnostici JVM/in-app disponibili senza inventare risultato.
- Browser risultati, errori, qualità, limiti e seed.
- Test motore/controllabilità chiaramente distinti dal cervello reale.

### 10.5 Aggiornamenti e informazioni
- **AGGIORNA** in-app: deve rimanere perfettamente funzionante, stessa firma/applicationId.
- Stato versione APK, versione brain schema e branch/release provenance quando disponibile.
- Descrizione funzioni realmente presenti, link ai paper e changelog.
- Rollback dei dati V3 basato su backup con integrità, non "downgrade app" implicito.

## 11. Source of truth: provider dati e aggiornamento schermo

Un solo strato diagnostico V3 produce snapshot tipizzati. Schematicamente:

\`\`\`text
ZeroBrainV3 / AdaptiveMotorCortex / ExperienceLedger
                  |
          LearningHealthSnapshotV3
          (schema, timestamp, sampleCount, provenance)
                  |
        V3DashboardDataProvider READ-ONLY
                  |
 Home, Brain/3D, Body, Skills, Evolution, Analysis, Settings
\`\`\`

Il provider non calcola "fatti" dal testo UI; ogni scheda mappa a metrica controllabile e a un test. Il polling non è training, non carica pesi completi a ogni frame e non ricrea UI a ogni update.

**Dati non disponibili:** modello non implementato, 0 campioni, confidence insufficiente, profilo legacy, corrotto -> stato esplicito differenziato. \`0\` e \`missing\` NON equivalenti.

**Neuroni vs neuroni visivi:** il modello espone \`neuronId\`, moduleId, weights & edges; il renderer può semplificare edges per performance ma non cambiare contatore degli effettivi.

## 12. Migrazione vecchia UI e compatibilità

Per V3:
- nuovo menu attivo come default; vecchie schermate 2.2 rimosse dal menu operativo di un cervello 3.0, oppure rese legacy read-only con titolo e messaggio dedicato;
- mantenere 12 slot e \`AGGIORNA\` nello stesso workflow familiare;
- valorizzare le schermate legacy da dati legacy solo se il profilo selezionato è 2.2;
- checkpoint da v3 non deve produrre grafici vuoti o crash perché le vecchie pref-key mancano;
- cambio profilo 2.2 ↔ 3.0 deve aggiornare TUTTI i menu e i dati e non modificare alcuna rete;
- back navigation, pause/resume, riapertura app e cambio orientamento non alterano la policy.

## 13. Test di approvazione obbligatori

| ID | Test | Risultato richiesto |
|---|---|---|
| UI-01 | Apri profilo 3.0 da menu | Tutte le schermate usano provider V3, non dati 2.2 |
| UI-02 | Visualizzazione neuronale | Conteggio unità 3D = network reale, filtri espliciti |
| UI-03 | Neurogenesi | Accettare 1 unità nel test crea nodo+edges visibili veri; rifiuto non crea neurone |
| UI-04 | Abilità | Candidato non appare come champion; recall e revert riflessi |
| UI-05 | Corpo | 96 shell + 74 inner, 16 settori e 64 comandi, corretto world heading |
| UI-06 | Learn vs observe | Sospende realmente update del trainer; letture continuano |
| UI-07 | Dati indisponibili | No N/A -> fake zero / no stale previous profile |
| UI-08 | Pause/resume | Nessun training su render, no decisione duplicata |
| UI-09 | Perf | App x1/x5/x20/testMax e atlas navigabili su S21 |
| UI-10 | Slots/backup | Tutti i 12 slot, same brain after save/load; Legacy 2.2 intact |
| UI-11 | Settings OTA | Pulsante AGGIORNA continua flusso Android |
| UI-12 | Modalità TEST | TEST MAX separato x1/x5/x20, clone isolato quando necessario |
| UI-13 | Metrics | Snapshot identical values in Home/Body/Skills/Analysis per same frame |
| UI-14 | Negative flags | Neuron count non confuso con 64 attuatori, 96 particelle o 320 weights |
| UI-15 | Navigazione | Nessuna sezione fondamentale nascosta o disabilitata senza motivo |
| UI-16 | Accessibilità | Tap targets, ritorno, focus e testi verificati su S21 |

Un test di rendering con JSON mock dimostra solo UI-structure; è necessario anche un test integrato collegato al **cervello V3 realmente allenato**. La UI non è completa se le schermate non mostrano i dati aggiornati dopo esperienze vere.

## 14. Checklist realizzazione UI (ancora non implementata)

- [ ] UX schema e data provider definito nel codice.
- [ ] Implementazione nuova shell di navigazione 3.0.
- [ ] Home/panoramica da snapshot reale.
- [ ] Cervello/neuroni 3D con unità e connessioni vere.
- [ ] Cortex/corpo 16x4/96+74 con fisica.
- [ ] Abilità candidate/champion/recall/failure.
- [ ] Evoluzione weight deltas/neurogenesis/retention.
- [ ] Percezione/memoria/obiettivi/world model.
- [ ] Analisi/baseline/benchmark/report.
- [ ] Impostazioni V3 e preservazione updater/slot/backup.
- [ ] Risoluzione UX di tutti i menu 2.2 rimasti raggiungibili da V3.
- [ ] QA UI-01..UI-16 e verifica Galaxy S21.
- [ ] Handoff della UI e mapping metriche aggiornato nel contratto.

**Successo significa:** l'utente può aprire Cervello, Neuroni, Abilità, Corpo, Evoluzione, Memoria, Analisi e Impostazioni e osservare fedelmente l'apprendimento di ZERO 3.0. Tutto coerente fra pannelli, con visualizzazione attraente e reale, nessun menù fantasma 2.2.

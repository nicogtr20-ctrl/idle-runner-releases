# ZERO3 V353 — modello fisico e validazione integrati

18 ottobre 2026. Stato: implementazione sperimentale, **non ZERO 3.0 completo**.
Base: V351/V352, GRU64 esistente, motore fisico e presa V319 riutilizzati.

## Codice effettivo

`Zero3CognitionV353` è posseduto dall'attore GRU esistente. Tre reti tanh,
194 sensi PRE + 5 parametri dell'intenzione realmente emessa + bias, 12 uscite:
netto riserva, danno, costo motorio, velocità x/y, contatto solido, presa,
errore area, cambiamento ottico, terminale, energia raccolta, tempo eseguito.
Le normalizzazioni energetiche usano la capacità reale di riserva (100).
I target provengono esclusivamente da V344 POST e dai sensori fisici V351.
Non vengono fornite coordinate/identità di cibo o obiettivi.

Un quinto delle decisioni viene assegnato alla validazione prima dell'azione,
tramite ID; V325 propaga la scelta a GRU, corteccia e adesione V319.
La validazione non aggiorna pesi, critic target, replay del valore, cache
individuale degli episodi o baseline. Aggiorna solo statistiche misurate.
Lo stato ricorrente e la fisica avanzano realmente anche su questi campioni.
Questa è **validazione online**, non un benchmark indipendente per la
certificazione finale: la selezione delle architetture usa queste statistiche.

Ogni testa ha errore quadratico, baseline ultimo esito TRAIN e dispersione
tra tre predittori, misurati sullo stesso campione di validazione. Uso ammesso
solo dopo almeno64 campioni della topologia corrente, errore <90% baseline
non degenere e dispersione limitata. Il planner confronta tre proposte
campionate dall'unico attore soltanto se energia/danno/costo superano il
controllo, penalizza la dispersione e conserva il rumore della proposta scelta.
È un confronto **a un passo**, non un planner multistep. Il gradiente attore
con selezione delle proposte rimane sperimentale: efficacia e bias richiedono
il confronto indipendente. Nessuna previsione entra nel journal reale.

Memoria:64 ultimi episodi TRAIN con PRE194, intenzione5 e outcome12; il journal
completo PRE/comandi effettivi/POST rimane V329. **Non è memoria procedurale,
non certifica skill, non implementa ancora recall/probation di abilità.**

## Crescita reale, con veto

Champion iniziale:12 unità nascoste per rete,36 unità predittive effettive
(distinte dalle64 GRU). Se rimangono errori misurati, ogni512 TRAIN è possibile
una copia candidata con2 unità in più per rete e nuove vere connessioni.
Dopo256 TRAIN e64 validazioni appaiate: accettazione solo se errore aggregato
scende di almeno5% e nessuna testa peggiora oltre10%. La promozione avviene
solo al confine di una decisione TRAIN; su validazione non si sostituiscono
matrici. In caso positivo, affidabilità delle teste azzerata e da rimisurare.
Limite20 unità per rete: massimo24 unità aggiuntive mantenute. UI distingue
unità champion, copia shadow, tentativi accettati/respinti. Il GRU resta64.

La prova sintetica di isolamento/codec ha eseguito2 tentativi: **0 accettati,
2 respinti**. Dimostra il veto e il conteggio onesto, NON un vantaggio della
neurogenesi né la validazione scientifica G7. Le prove fisiche non usano
questi target sintetici.

## Persistenza e Android

Schema353: blob cognitivo dentro il CRC del checkpoint GRU; intero organismo,
manifest, replay con provenienza TRAIN/validazione della medesima generazione.
Decoder verifica dimensioni, finitezza, contatori e topologia prima di mutare
la rete. Manifest rifiuta journal con provenienza cognitiva incompatibile.
Schemi345/348/351/352 restano leggibili e non vengono convertiti automaticamente.
Nuove vite LAB usano353; ripresa conserva esattamente il tipo storico.
Sessione Android privata V3, nuova diagnostica read-only con valori autentici;
misure mancanti indicate come non disponibili. Non è l'interfaccia finale G8.

## Verifica e limiti

Comando: `python3 tools/zero3_core_qa.py --variable-clock --cognitive --long-life 2000`.
Include regressioni storiche, isolamento pesi GRU/target/replay valore,
freeze di corteccia/presa su decisione fisica di validazione, veto planner
senza evidenza, memoria limitata, confronto bitwise della continuazione e
intero organismo353 a96 decisioni con riavvio a48.
Prova lunga in JVM separate:1000+restart+1000 confrontate bytewise con2000
continue;63 segmenti, rollback e doppia corruzione fail closed. Il fixture
alimenta l'organismo con risorse fisiche: non dimostra sopravvivenza appresa.

Risultati locali FINALI (inclusi trigger misurato e guardia di partizione): PASS.
Il core rifiuta outcome mancante/doppio e un cambio di partizione prima del
credito neurale. Prima build CI completa: [39122532281 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39122532281),
suite2000 + Android Debug. La guardia aggiuntiva è nel commit
`fdbbb93b5dbae14aa06c6f1f439c321d0be8949d`,
[CI finale39122690894 SUCCESS](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39122690894): suite2000 + Android Debug PASS.

Tre CI storici iniziali (V345/V347/V348) fallivano in compilazione perché
le liste esplicite non caricavano `Zero3CognitionV353`. Corretta la risoluzione
delle dipendenze JVM tramite `-sourcepath app/src/main/java` nelle suite ZERO30;
nessun test o soglia scientifica rimosso. Le relative compilazioni V345/V316/V348
sono rieseguite localmente, oltre alla suite core completa.

**Aperti:** G1 fisica congiunta (V346 respinta), G3 locomozione controllabile,
G4 efficacia attore, G5 affidabilità indipendente e pianificazione multistep,
G6 goals/skills/recall/probation, G7 vantaggio crescita, G8 UI/atlante3D completo,
G9 Galaxy S21 offline/heap/termica/process-kill e G10 pubblicazione.
Nessun merge nel publisher, nessuna OTA, nessun profilo2.2 modificato.
